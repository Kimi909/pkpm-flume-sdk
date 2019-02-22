package com.pkpm.util;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Flume客户端工具类
 * 工具类初始化默认读取配置文件flume-client.properties
 * 配置文件放在classpath下
 * 
 * @author accountwcx@qq.com
 *
 */
public class FlumeRpcClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(FlumeRpcClientUtils.class);

    private static Properties props = new Properties();

    // 是否已初始化
    private static volatile boolean isInit = false;

    // 发送消息的默认编码，如果没有设置编码，则使用该默认编码
    private static final String defaultCharsetName = "utf-8";

    // 发送消息的编码
    private static Charset charset;

    // 如果消息发送失败，尝试发送的消息次数，默认为3次
    private static int attemptTimes = 3;

    private static RpcClient client;

    static {
        init();
    }
    /**
     * 初始化客户端配置 客户端配置必须放在classpath根目录下的flume-client.properties文件中
     */
    public synchronized static void init() {
        if(isInit){
            return;
        }

        logger.info("初始化配置flume-client.properties");

        // 读取配置文件
        InputStream is = FlumeRpcClientUtils.class.getClassLoader().getResourceAsStream("flume-client.properties");

        if (is == null) {
            logger.error("找不到配置文件flume-client.properties");
            return;
        }

        try {
            props.load(is);

            // 从配置文件中读取消息编码
            String charsetName = props.getProperty("charset");
            if (charsetName != null && !charsetName.equals("")) {
                try {
                    charset = Charset.forName(charsetName);
                } catch (Exception e) {
                    logger.error("编码charset=" + charsetName + "初始化失败，使用默认编码charset=" + defaultCharsetName, e);
                }
            }
            props.remove("charset");

            // 如果编码为空，则使用默认编码utf-8
            if (charset == null) {
                try {
                    charset = Charset.forName(defaultCharsetName);
                } catch (Exception e) {
                    logger.error("默认编码charset=" + defaultCharsetName + "初始化失败", e);
                }
            }

            // 读取消息发送次数配置
            String strAttemptTimes = props.getProperty("attemptTimes");
            if (strAttemptTimes != null && !strAttemptTimes.equals("")) {
                int tmpAttemptTimes = 0;
                try {
                    tmpAttemptTimes = Integer.parseInt(strAttemptTimes);
                } catch (NumberFormatException e) {
                    logger.error("消息发送次数attemptTimes=" + strAttemptTimes + "初始化失败，使用默认发送次数attemptTimes=" + attemptTimes, e);
                }

                if (tmpAttemptTimes > 0) {
                    attemptTimes = tmpAttemptTimes;
                }
            }
            props.remove("attemptTimes");

            // 初始化Flume Client
            // 根据不同的client.type，实例也不一样
            client = RpcClientFactory.getInstance(props);

            isInit = true;
        } catch (IOException e) {
            logger.error("配置文件flume-client.properties读取失败", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 发送一条记录，如果发送失败，该方法会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 建议使用appendBatch以获得更好的性能。
     * 
     * @param data 发送内容
     * @return 发送成功返回true，失败返回false
     */
    public static boolean append(String data) {
        boolean flag = false;
        Event event = EventBuilder.withBody(data, charset);
        int current = 0;

        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.append(event);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.error("发送失败，当前已尝试" + current + "次", e);
                logger.error("失败消息" + data);
            }
        }

        return flag;
    }

    /**
     * 发送一条记录，如果发送失败，该方法会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 建议使用appendBatch以获得更好的性能。
     * 
     * @param event 发送内容
     * @return 发送成功返回true，失败返回false
     */
    public static boolean append(Event event){
        boolean flag = false;

        int current = 0;

        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.append(event);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.error("发送失败，当前已尝试" + current + "次", e);
                logger.error("失败消息" + new String(event.getBody(), charset));
            }
        }

        return flag;
    }

    /**
     * 发送一条记录，如果发送失败，该方法会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 建议使用appendBatch以获得更好的性能。
     * 
     * @param data 发送内容
     * @param headers 发送的头文件
     * @return 发送成功返回true，失败返回false
     */
    public static boolean append(String data, Map<String, String> headers){
        Event event = EventBuilder.withBody(data, charset);

        if(headers != null){
            event.setHeaders(headers);
        }

        return append(event);
    }

    /**
     * 以批量的方式发送一条记录，该记录不会立即发送，而是会放到内存队列中，直到队列中的记录数达到batchSize才会发送。
     * 如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 
     * appendBatch性能远高于append，建议使用。
     * 
     * @param data 单个记录
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatch(String data){
        List<String> items = new ArrayList<String>();
        items.add(data);
        return appendBatch(items);
    }

    /**
     * 以批量的方式发送一条记录，该记录不会立即发送，而是会放到内存队列中，直到队列中的记录数达到batchSize才会发送。
     * 如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 
     * appendBatch性能远高于append，建议使用。
     * 
     * @param data 单个记录
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatch(String data, Map<String, String> headers){
        List<Event> events = new ArrayList<Event>();
        Event event = EventBuilder.withBody(data, charset);
        event.setHeaders(headers);
        events.add(event);
        return appendBatchEvent(events);
    }

    /**
     * 以批量的方式发送一条记录，该记录不会立即发送，而是会放到内存队列中，直到队列中的记录数达到batchSize才会发送。
     * 如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * 
     * appendBatch性能远高于append，建议使用。
     * 
     * @param data 单个记录
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatch(Event event){
        List<Event> events = new ArrayList<Event>();
        events.add(event);
        return appendBatchEvent(events);
    }

    /**
     * 批量发送多条记录，如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * appendBatch性能远高于append，建议使用。
     * 
     * @param items 内容列表
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatch(List<String> items){
        return appendBatch(items, null);
    }

    /**
     * 批量发送多条记录，如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * appendBatch性能远高于append，建议使用。
     * 
     * @param items 内容列表
     * @param headers 头部内容
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatch(List<String> items, Map<String, String> headers){
        boolean flag = false;

        // 如果参数不符合要求，则退出
        if(items == null || items.size() < 1){
            return flag;
        }

        List<Event> events = new LinkedList<Event>();

        if(headers != null){
            for(String item : items){
                Event event = EventBuilder.withBody(item, charset);
                event.setHeaders(headers);
                events.add(event);
            }
        }else{
            for(String item : items){
                events.add(EventBuilder.withBody(item, charset));
            }
        }

        // 当前尝试发送的次数
        int current = 0;

        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.appendBatch(events);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.error("批量发送失败，当前已尝试" + current + "次", e);
            }
        }

        return flag;
    }

    /**
     * 批量发送多条记录，如果发送失败，会尝试多次发送，尝试次数在attemptTimes中设置，默认3次。
     * appendBatch性能远高于append，建议使用。
     * 
     * @param events 内容列表
     * @return 发送成功返回true，失败返回false
     */
    public static boolean appendBatchEvent(List<Event> events){
        boolean flag = false;

        // 如果参数不符合要求，则退出
        if(events == null || events.size() < 1){
            return flag;
        }

        // 当前尝试发送的次数
        int current = 0;

        while (!flag && current < attemptTimes) {
            current++;
            try {
                client.appendBatch(events);
                flag = true;
            } catch (EventDeliveryException e) {
                logger.error("批量发送失败，当前已尝试" + current + "次", e);
            }
        }

        return flag;
    }

    public static int getBatchSize(){
        return client.getBatchSize();
    }
}