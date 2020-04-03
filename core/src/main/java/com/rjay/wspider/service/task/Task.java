package com.rjay.wspider.service.task;

import java.util.Map;

/**
 * 爬虫任务
 */
public interface Task{


    /**
     * 任务类型
     * @return
     */
    String getType();

    /**
     * 启动任务
     */
    void start();

    /**
     * 注册监听器
     * @param listener
     */
    void registerListener(TaskListener listener);

    /**
     * 获取任务参数
     * @return
     */
    Map<String, String> getParams();

    /**
     * 设置任务参数
     * @param params
     */
    void setParams(Map<String, String> params);
}
