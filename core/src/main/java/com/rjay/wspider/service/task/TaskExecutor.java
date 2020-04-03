package com.rjay.wspider.service.task;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 爬虫任务执行器
 */
public class TaskExecutor {

    private static ExecutorService pool = new ThreadPoolExecutor(100, 200,
                                                          0L, TimeUnit.MILLISECONDS,
                                                          new LinkedBlockingQueue<Runnable>(),
                                                          new CustomizableThreadFactory("Spider_Task_Executor_Thread_"));

    /**
     * 执行过的任务数量
     */
    private static AtomicLong executedTaskCount = new AtomicLong(0);

    /**
     * 活跃的任务数量
     */
    private static AtomicLong activeTaskCount = new AtomicLong(0);

    /**
     * 执行任务
     * @param task
     */
    public static void execute(Task task){
        //活跃的任务数量+1
        activeTaskCount.incrementAndGet();
        //注册任务监听器
        task.registerListener((t) -> {
            //活跃的任务数量-1
            activeTaskCount.decrementAndGet();
            //执行过的任务数量+1
            executedTaskCount.incrementAndGet();
        });
        //提交任务
        pool.submit(()->{
            task.start();
        });
    }

    public static long getExecutedTaskCount(){
        return executedTaskCount.get();
    }

    public static long getActiveTaskCount(){
        return activeTaskCount.get();
    }
}
