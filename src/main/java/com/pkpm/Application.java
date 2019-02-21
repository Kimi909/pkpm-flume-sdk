package com.pkpm;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.apache.commons.cli.*;
import org.apache.flume.*;
import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.node.PollingPropertiesFileConfigurationProvider;
import org.apache.flume.node.PollingZooKeeperConfigurationProvider;
import org.apache.flume.node.PropertiesFileConfigurationProvider;
import org.apache.flume.node.StaticZooKeeperConfigurationProvider;
import org.apache.flume.util.SSLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);



	public static void main(String[] args) {
		//启动flume
		startFlume(args);
		SpringApplication.run(Application.class, args);

	}

	private static void startFlume(String[] args) {
		try {
			SSLUtil.initGlobalSSLParameters();

			Options options = new Options();

			Option option = new Option("n", "name", true, "the name of this agent");
			option.setRequired(true);
			options.addOption(option);

			option = new Option("f", "conf-file", true,
					"specify a config file (required if -z missing)");
			option.setRequired(false);
			options.addOption(option);

			option = new Option(null, "no-reload-conf", false,
					"do not reload config file if changed");
			options.addOption(option);

			// Options for Zookeeper
			option = new Option("z", "zkConnString", true,
					"specify the ZooKeeper connection to use (required if -f missing)");
			option.setRequired(false);
			options.addOption(option);

			option = new Option("p", "zkBasePath", true,
					"specify the base path in ZooKeeper for agent configs");
			option.setRequired(false);
			options.addOption(option);

			option = new Option("h", "help", false, "display help text");
			options.addOption(option);

			CommandLineParser parser = new GnuParser();
			CommandLine commandLine = parser.parse(options, args);

			if (commandLine.hasOption('h')) {
				new HelpFormatter().printHelp("flume-ng agent", options, true);
				return;
			}

			String agentName = commandLine.getOptionValue('n');
			boolean reload = !commandLine.hasOption("no-reload-conf");

			boolean isZkConfigured = false;
			if (commandLine.hasOption('z') || commandLine.hasOption("zkConnString")) {
				isZkConfigured = true;
			}

			org.apache.flume.node.Application application;
			if (isZkConfigured) {
				// get options
				String zkConnectionStr = commandLine.getOptionValue('z');
				String baseZkPath = commandLine.getOptionValue('p');

				if (reload) {
					EventBus eventBus = new EventBus(agentName + "-event-bus");
					List<LifecycleAware> components = Lists.newArrayList();
					PollingZooKeeperConfigurationProvider zookeeperConfigurationProvider =
							new PollingZooKeeperConfigurationProvider(
									agentName, zkConnectionStr, baseZkPath, eventBus);
					components.add(zookeeperConfigurationProvider);
					application = new org.apache.flume.node.Application(components);
					eventBus.register(application);
				} else {
					StaticZooKeeperConfigurationProvider zookeeperConfigurationProvider =
							new StaticZooKeeperConfigurationProvider(
									agentName, zkConnectionStr, baseZkPath);
					application = new org.apache.flume.node.Application();
					application.handleConfigurationEvent(zookeeperConfigurationProvider.getConfiguration());
				}
			} else {
				File configurationFile = new File(commandLine.getOptionValue('f'));

        /*
         * The following is to ensure that by default the agent will fail on
         * startup if the file does not exist.
         */
				if (!configurationFile.exists()) {
					// If command line invocation, then need to fail fast
					if (System.getProperty(Constants.SYSPROP_CALLED_FROM_SERVICE) ==
							null) {
						String path =    configurationFile.getPath() ;
						try {
							path =   configurationFile.getCanonicalPath()  ;
						} catch (IOException ex) {
							logger.error("Failed to read canonical path for file: " + path,
									ex);
						}
						throw new ParseException(
								"The specified configuration file does not exist: " + path);
					}
				}
				List<LifecycleAware> components = Lists.newArrayList();

				if (reload) {
					EventBus eventBus = new EventBus(agentName + "-event-bus");
					PollingPropertiesFileConfigurationProvider configurationProvider =
							new PollingPropertiesFileConfigurationProvider(
									agentName, configurationFile, eventBus, 30);
					components.add(configurationProvider);
					application = new org.apache.flume.node.Application(components);
					eventBus.register(application);
				} else {
					PropertiesFileConfigurationProvider configurationProvider =
							new PropertiesFileConfigurationProvider(agentName, configurationFile);
					application = new org.apache.flume.node.Application();
					application.handleConfigurationEvent(configurationProvider.getConfiguration());
				}
			}
			application.start();

			final org.apache.flume.node.Application appReference = application;
			Runtime.getRuntime().addShutdownHook(new Thread("agent-shutdown-hook") {
				@Override
				public void run() {
					appReference.stop();
				}
			});

		} catch (Exception e) {
			logger.error("A fatal error occurred while running. Exception follows.", e);
			return;
		}
	}

	class ConsumeXSink implements Sink {
		volatile int remaining;
		private LifecycleState state;
		private String name;
		private Channel channel;
		private Integer written;

		public ConsumeXSink(int consumeCount) {
			remaining = consumeCount;
			written = 0;
		}

		@Override
		public void start() {
			state = LifecycleState.START;
		}

		@Override
		public void stop() {
			state = LifecycleState.STOP;
		}

		@Override
		public LifecycleState getLifecycleState() {
			return state;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setChannel(Channel channel) {
			this.channel = channel;
		}

		@Override
		public Channel getChannel() {
			return channel;
		}

		public synchronized void setRemaining(int remaining) {
			this.remaining = remaining;
		}

		@Override
		public Status process() throws EventDeliveryException {
			synchronized (this) {
				if (remaining <= 0) {
					throw new EventDeliveryException("can't consume more");
				}
			}

			Transaction tx = channel.getTransaction();
			tx.begin();
			Event e = channel.take();
			tx.commit();
			tx.close();

			if (e != null) {
				synchronized (this) {
					remaining--;
				}
				written++;
			}

			return Status.READY;
		}

		public Integer getWritten() {
			return written;
		}
	}

}
