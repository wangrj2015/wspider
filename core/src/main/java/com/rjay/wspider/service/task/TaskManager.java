package com.rjay.wspider.service.task;

import com.rjay.wspider.service.leader.LeaderSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * 爬虫任务调度管理器
 */
@Service
@DependsOn("springContextUtil")
public class TaskManager {


    private ExecutorService pool = new ThreadPoolExecutor(10, 10,
                                                          0L, TimeUnit.MILLISECONDS,
                                                          new LinkedBlockingQueue<Runnable>(),
                                                          new CustomizableThreadFactory("Spider_Task_Dispatcher_Thread_"));

    @Autowired
    private LeaderSelector leaderSelector;

    /**
     * true=本机模式  false=集群模式
     */
    @Value("${spider.standalone}")
    private String standalone;

    @PostConstruct
    public void init(){
        if(Boolean.parseBoolean(standalone)){
            work();
        }else{
            //注册监听器
            leaderSelector.registerListener(() -> work());
            //选举leader
            leaderSelector.select();
        }
    }

    private void work(){
        //启动豆瓣top250爬虫调度器
        startTaskDispatcher(TaskFactory.TASK_DOUBAN_TOP_250);
    }

    /**
     * 启动爬虫任务调度器
     * @param type 任务类型
     */
    public void startTaskDispatcher(String type){
        TaskDispatcher taskDispatcher = TaskFactory.createTaskDispatcher(type);
        taskDispatcher.setStatus(true);
        pool.submit(() -> taskDispatcher.dispatch());
    }

    /**
     * 停止爬虫任务调度器
     * @param type 任务类型
     */
    public void stopTaskDispatcher(String type){
        TaskDispatcher taskDispatcher = TaskFactory.createTaskDispatcher(type);
        taskDispatcher.setStatus(false);
    }

}
