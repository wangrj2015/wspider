package com.rjay.wspider.service.worker;

import com.rjay.wspider.service.leader.ZookeeperClient;
import com.rjay.wspider.service.task.TaskExecutor;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class WorkerHolder {

    private Logger logger = LoggerFactory.getLogger(WorkerHolder.class);

    private static final String WORKER_PATH = "/worker";

    @Autowired
    private ZookeeperClient zookeeperClient;

    private Worker worker = new Worker();

    public void reset(){
        this.worker = new Worker();
    }

    public void setHost(String host){
        this.worker.setHost(host);
    }

    public void setActive(boolean isActive){
        this.worker.setActive(isActive);
    }

    @Scheduled(cron = " 0/5 * * * * ? ")
    public void reportTaskCount(){
        if(!this.worker.isActive()){
            return;
        }
        if(TaskExecutor.getExecutedTaskCount() == worker.getExecutedTaskCount()
           && TaskExecutor.getActiveTaskCount() == worker.getActiveTaskCount()){
            return;
        }
        try{
            if(!zookeeperClient.isInit()){
                return;
            }
            this.worker.setTaskCount(TaskExecutor.getExecutedTaskCount(), TaskExecutor.getActiveTaskCount());
            if(null != zookeeperClient.checkExist(ZKPaths.makePath(WORKER_PATH, worker.getHost()))){
                zookeeperClient.setData(ZKPaths.makePath(WORKER_PATH, worker.getHost()), worker.getData());
            }
        }catch (Exception e){
            logger.error("report task count error", e);
        }
    }
}
