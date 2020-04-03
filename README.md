# 分布式爬虫系统 wspider
使用zk作为注册中心，多实例无状态部署，应用启动时竞选leader，竞选成功的实例(leader)负责分发任务，其他实例(worker)负责执行任务，运行过程中leader挂掉的话自动重新竞选

## 配置介绍
spider.standalone  
执行模式:true=本机模式 false=集群模式

select.worker.sleep.time  
集群模式下，使用最小活跃任务数分发策略时，找不到可用worker时的等待时间ms

worker.max.active.task.count  
worker最大活跃任务数

## 定制爬虫
1. 参考示例  
DoubanTop250Task  
DoubanTop250TaskDispatcher
2. 实现  
com.rjay.wspider.service.task.Task  
com.rjay.wspider.service.task.TaskDispatcher

## 本机模式启动
spider.standalone=true  
原地起飞

## 集群模式启动
spider.standalone=false
1. [部署zookeeper](https://zookeeper.apache.org/doc/current/zookeeperStarted.html)
2. 修改application.properties中zookeeper.hostList
3. 起飞

## 感谢
[WebMagic](https://github.com/code4craft/webmagic)