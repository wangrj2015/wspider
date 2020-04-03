package com.rjay.wspider.service.task;

/**
 * 任务监听器
 */
public interface TaskListener {

    /**
     * 任务结束
     * @param task
     */
    void onFinish(Task task);

}
