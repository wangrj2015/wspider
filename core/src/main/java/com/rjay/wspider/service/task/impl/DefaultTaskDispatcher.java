package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.service.task.TaskFactory;
import org.springframework.stereotype.Service;

@Service("defaultTaskDispatcher")
public class DefaultTaskDispatcher extends AbstractTaskDispatcher {

    @Override
    public void dispatch() {
        //do nothing
    }

    @Override
    public String getType() {
        return TaskFactory.TASK_DEFAULT;
    }

}
