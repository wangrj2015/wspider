package com.rjay.wspider.service.task.impl;

import com.rjay.wspider.service.task.TaskFactory;

public class DefaultTask extends AbstractTask {

    @Override
    public void execute() {
        //do nothing
    }

    @Override
    public String getType() {
        return TaskFactory.TASK_DEFAULT;
    }
}
