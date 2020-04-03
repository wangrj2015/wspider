package com.rjay.wspider.service.leader;

import com.rjay.wspider.service.balance.MinActiveTaskCountStrategy;
import com.rjay.wspider.service.balance.WorkerSelectStrategy;
import com.rjay.wspider.service.worker.Worker;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LeaderHolder {

    private Logger logger = LoggerFactory.getLogger(LeaderHolder.class);

    private Leader leader = new Leader();

    private Map<String,Worker> workers = Maps.newHashMap();

    private WorkerSelectStrategy strategy = new MinActiveTaskCountStrategy();

    public void reset(){
        leader = new Leader();
        workers.clear();
    }

    public void setHost(String host){
        this.leader.setHost(host);
    }

    public void setActive(boolean isActive){
        this.leader.setActive(isActive);
    }

    public boolean isActive(){
        return this.leader.isActive();
    }

    public void addWorker(Worker worker){
        if(!workers.containsKey(worker.getHost())){
            workers.put(worker.getHost(),worker);
        }else{
            Worker oldWorker = workers.get(worker.getHost());
            if(!oldWorker.equals(worker)){
                workers.put(worker.getHost(),worker);
            }
        }
        logger.info("addWorker:{} currentWorkers:{}",worker,workers);
    }

    public void rmWorker(Worker worker){
        workers.remove(worker.getHost());
        logger.info("rmWorker:{} currentWorkers:{}", worker, workers);
    }

    /**
     * 选择最佳的worker
     * @return
     */
    public Worker selectBestWorker(){
        return strategy.selectWorker(workers);
    }
}
