package com.rjay.wspider.service.task;

/**
 * 爬虫任务调度器
 */
public interface TaskDispatcher {

    /**
     * 调度
     */
    void dispatch();

    /**
     * 调度器类型
     * @return
     */
    String getType();


    /**
     * 设置状态
     * @param status
     * @return
     */
    void setStatus(boolean status);

}
