# flume sdk封装

## 介绍
- 1、基于flume的高可用的，高可靠的，分布式的海量日志采集、聚合和传输的系统
 
## 产生背景
apache-flume-1.8.0-bin也可以在windows环境下,通过cmd命令收集指定目录或者动态指定文件（tail -f xx.log）的形式收集日志；但是如果在服务器主机应用比较多，内存比较少的情况下，flume项目需要进一步精简；以及提供二次开发。


## 已完成的功能
 - 1、java项目直接调用工具类上传日志
 - 2、其他语言通过RESTFUL接口形式,调用接口上传到HDFS日志
 - 3、主动采集日志,通过配置flume-client.properties指定的目录收集日志
 - 4、 引入WinFileUtil，支持windows使用tail -f xx.log收集日志。

部署教程参考：
- 7节点部署Hadoop HA高可用集群教程参考： https://www.imooc.com/article/37771
 
  
- 5节点：flume组合模式之高可用配置 https://blog.csdn.net/lzxlfly/article/details/80672267

## 核心依赖
- 1、springboot
- 2、flume-ng-core flume-ng-sinks  flume-ng-sdk
- 3、taildirSource 支持 windows系统（引用jna jna-platform jar包）

## hadoop部署异常总结见hadoop-exception.md： 

## 提交反馈
- 1、欢迎提交 issue，请写清楚遇到问题的原因，开发环境，复显步骤。

- 2、有问题联系kbping@qq.com

### 注意：
-f src\main\resources\flume-client.properties -n agent1

因windows系统不支持tail -f xx.log命令，需要下载tail.exe,安装在C:\Windows\System32目录下。