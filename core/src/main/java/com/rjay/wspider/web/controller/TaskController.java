package com.rjay.wspider.web.controller;

import com.rjay.wspider.common.object.ErrorCode;
import com.rjay.wspider.common.object.Result;
import com.rjay.wspider.service.task.Task;
import com.rjay.wspider.service.task.TaskExecutor;
import com.rjay.wspider.service.task.TaskFactory;
import com.rjay.wspider.service.worker.Worker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 爬虫任务controller
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    private Logger logger = LoggerFactory.getLogger(TaskController.class);

    @RequestMapping("/add")
    public Result addTask(@RequestBody String data){
        logger.info("add task,param:{}",data);
        Task task = buildTask(data);
        if(null == task){
            return Result.fail(ErrorCode.TASK_IS_EMPTY);
        }
        TaskExecutor.execute(task);
        Map<String,Long> map = Maps.newHashMap();
        map.put(Worker.KEY_EXECUTED_TASK_COUNT, TaskExecutor.getExecutedTaskCount());
        map.put(Worker.KEY_ACTIVE_TASK_COUNT, TaskExecutor.getActiveTaskCount());
        logger.info("add task success,param:{} result:{}", data, map);
        return Result.success().setData(map);
    }

    private Task buildTask(String data){
        if(StringUtils.isEmpty(data)){
            return null;
        }
        Map<String,String> params = JSON.parseObject(data, new TypeReference<Map<String,String>>(){});
        return TaskFactory.createTask(params);
    }
}
