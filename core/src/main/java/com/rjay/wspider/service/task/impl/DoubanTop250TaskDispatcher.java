package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.service.task.TaskFactory;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("doubanTop250TaskDispatcher")
public class DoubanTop250TaskDispatcher extends AbstractTaskDispatcher {

    @Override
    public void dispatch() {
        for(int i = 0; i<10; i++){
            Map<String,String> params = Maps.newHashMap();
            params.put("start", String.valueOf(i * 25));
            dispatchTask(params);
        }
    }

    @Override
    public String getType() {
        return TaskFactory.TASK_DOUBAN_TOP_250;
    }

}
