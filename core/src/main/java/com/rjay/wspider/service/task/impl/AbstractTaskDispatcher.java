package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.common.object.Result;
import com.rjay.wspider.service.leader.LeaderHolder;
import com.rjay.wspider.service.worker.Worker;
import com.rjay.wspider.service.task.TaskDispatcher;
import com.rjay.wspider.service.task.TaskExecutor;
import com.rjay.wspider.service.task.TaskFactory;
import com.rjay.wspider.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 爬虫任务调度器
 */
public abstract class AbstractTaskDispatcher implements TaskDispatcher {

    private Logger logger = LoggerFactory.getLogger(AbstractTaskDispatcher.class);

    private static final String WORKER_ADD_TASK_URL = "http://%s:8088/task/add";

    @Autowired
    private LeaderHolder leaderHolder;

    /**
     * true=本机模式  false=集群模式
     */
    @Value("${spider.standalone}")
    private String standalone;

    /**
     * 调度器活跃状态
     */
    public boolean status;

    /**
     * 调度
     */
    public abstract void dispatch();

    public abstract String getType();

    protected boolean dispatchTask(Map<String,String> params){
        try{
            params.put(TaskFactory.TASK_TYPE, getType());
            //本机模式
            if(Boolean.parseBoolean(standalone)){
                TaskExecutor.execute(TaskFactory.createTask(params));
                return true;
            }
            //集群模式
            Worker worker = leaderHolder.selectBestWorker();
            if(null == worker){
                logger.warn("Oh! No worker!");
                return false;
            }
            String url = String.format(WORKER_ADD_TASK_URL,worker.getHost());
            String result = HttpClientUtils.sendPostWithJSON(url, JSON.toJSONString(params));
            logger.info("dispatch to:{} Task:{} result:{}", worker, params, result);
            if(StringUtils.isEmpty(result)){
                return false;
            }
            Result resultObj = JSON.parseObject(result, new TypeReference<Result<Map<String,Long>>>(){});
            Map<String,Long> map = (Map<String,Long>)resultObj.getData();
            worker.setTaskCount(map.get(Worker.KEY_EXECUTED_TASK_COUNT), map.get(Worker.KEY_ACTIVE_TASK_COUNT));
            return true;
        }catch (Exception e){
            logger.error("dispatchTask:{} error", params, e);
            return false;
        }
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }
}
