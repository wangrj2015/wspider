package com.rjay.wspider.web.controller;

import com.rjay.wspider.common.object.ErrorCode;
import com.rjay.wspider.common.object.Result;
import com.rjay.wspider.service.leader.LeaderHolder;
import com.rjay.wspider.service.task.TaskManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 爬虫调度器controller
 */
@RestController
@RequestMapping("/task/dispatcher")
public class TaskDispatcherController {

    private Logger logger = LoggerFactory.getLogger(TaskDispatcherController.class);

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private LeaderHolder leaderHolder;


    @RequestMapping("/start")
    public Result startDispatcher(String type){
        if(StringUtils.isEmpty(type)){
            return Result.fail(ErrorCode.TASK_TYPE_IS_EMPTY);
        }
        if(!leaderHolder.isActive()){
            return Result.fail(ErrorCode.NOT_LEADER);
        }
        logger.info("start dispatcher:{}",type);
        taskManager.startTaskDispatcher(type);
        logger.info("start dispatcher success:{}",type);
        return Result.success();
    }

    @RequestMapping("/stop")
    public Result stopDispatcher(String type){
        if(StringUtils.isEmpty(type)){
            return Result.fail(ErrorCode.TASK_TYPE_IS_EMPTY);
        }
        if(!leaderHolder.isActive()){
            return Result.fail(ErrorCode.NOT_LEADER);
        }
        logger.info("stop dispatcher:{}",type);
        taskManager.stopTaskDispatcher(type);
        logger.info("stop dispatcher success:{}",type);
        return Result.success();
    }

}
