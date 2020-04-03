package com.rjay.wspider.service.task;

import com.rjay.wspider.service.task.impl.DefaultTask;
import com.rjay.wspider.service.task.impl.DoubanTop250Task;
import com.rjay.wspider.util.SpringContextUtil;

import java.util.Map;

/**
 * 爬虫任务工厂
 */
public class TaskFactory {

    public static final String TASK_TYPE = "task_type";

    public static final String TASK_DEFAULT = "Default";

    public static final String TASK_DOUBAN_TOP_250 = "DoubanTop250";

    /**
     * 创建爬虫任务
     * @param params 任务参数
     * @return
     */
    public static Task createTask(Map<String,String> params){
        String type = params.get(TASK_TYPE);
        Task task = null;
        switch (type){
            case TASK_DOUBAN_TOP_250:
                task = new DoubanTop250Task();
                break;
            default:
                task = new DefaultTask();

        }
        task.setParams(params);
        return task;
    }

    /**
     * 创建爬虫任务调度器
     * @param type 任务类型
     * @return
     */
    public static TaskDispatcher createTaskDispatcher(String type){
        switch (type){
            case TASK_DOUBAN_TOP_250:
                return SpringContextUtil.getBean("doubanTop250TaskDispatcher", TaskDispatcher.class);
            default:
                return SpringContextUtil.getBean("defaultTaskDispatcher", TaskDispatcher.class);
        }
    }
}
