package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.service.task.Task;
import com.rjay.wspider.service.task.TaskListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 爬虫任务
 */
public abstract class AbstractTask implements Task {

    private Logger logger = LoggerFactory.getLogger(AbstractTask.class);

    /**
     * 任务参数
     */
    protected Map<String,String> params = Maps.newHashMap();

    /**
     * 监听器
     */
    protected List<TaskListener> listeners = Lists.newArrayList();

    /**
     * 任务类型
     * @return
     */
    public abstract String getType();

    /**
     * 启动任务
     */
    public void start(){
        try{
            execute();
        }catch (Exception e){
            logger.error("task execute error",e);
        }finally {
            listeners.stream().forEach((listener) -> listener.onFinish(this));
        }
    }

    public abstract void execute();


    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public void registerListener(TaskListener listener) {
        this.listeners.add(listener);
    }
}
