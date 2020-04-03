package com.rjay.wspider.service.balance;

import com.rjay.wspider.service.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Random;

/**
 * 最小活跃任务数策略
 * 选择所有worker中活跃任务数最小的，如果最小值仍大于设置的最大活跃数限制worker.max.active.task.count，则睡眠指定时间select.worker.sleep.time后重新选择
 */
public class MinActiveTaskCountStrategy implements WorkerSelectStrategy {

    private Logger logger = LoggerFactory.getLogger(MinActiveTaskCountStrategy.class);

    @Value("${select.worker.sleep.time}")
    private String selectWorkerSleepTime;

    @Value("${worker.max.active.task.count}")
    private String maxActiveTaskCount;

    private Random random = new Random();

    @Override
    public Worker selectWorker(Map<String,Worker> workers) {
        try{
            while(true){
                Worker best = selectMinActiveTaskCount(workers);
                if(null != best){
                    //最佳worker的活跃任务数 < worker最大活跃任务数限制
                    if(best.getActiveTaskCount() < Integer.parseInt(maxActiveTaskCount)){
                        return best;
                    }
                }
                //找不到合适的worker，等待
                long sleepTime = Long.parseLong(selectWorkerSleepTime);
                logger.info("wait:{} ms for select best worker",sleepTime);
                try{
                    Thread.sleep(sleepTime);
                }catch (Exception e){
                    //do nothing
                }
            }
        }catch (Exception e){
            logger.error("select best worker error", e);
        }
        return null;
    }

    private Worker selectMinActiveTaskCount(Map<String,Worker> workers){
        //选择活跃任务数最小的worker
        Worker best = null;
        for(Worker worker : workers.values()){
            if(null == best){
                best = worker;
            }
            if(worker.getActiveTaskCount() < best.getActiveTaskCount()){
                best = worker;
            }
            //活跃任务数相同的情况下，随机选择一个做为最佳
            if(worker.getActiveTaskCount() == best.getActiveTaskCount() && random.nextInt(2) == 1){
                best = worker;
            }
        }
        return best;
    }
}
