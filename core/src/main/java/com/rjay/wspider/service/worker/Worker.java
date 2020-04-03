package com.rjay.wspider.service.worker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * 任务执行者
 */
public class Worker implements Serializable {

    private static final String CHARSET = "utf-8";

    public static final String KEY_EXECUTED_TASK_COUNT = "executed_task_count";

    public static final String KEY_ACTIVE_TASK_COUNT = "active_task_count";

    private String host;

    private boolean active = false;

    private long executedTaskCount;

    private long activeTaskCount;

    private byte[] data;

    public Worker(){}

    public Worker(String host, byte[] data){
        this.host = host;
        this.data = data;
        try{
            String dataString = new String(data, CHARSET);
            JSONObject jsonObject = JSONObject.parseObject(dataString);
            this.executedTaskCount = jsonObject.getLong(KEY_EXECUTED_TASK_COUNT);
            this.activeTaskCount = jsonObject.getLong(KEY_ACTIVE_TASK_COUNT);
        }catch (Exception e){
            //do nothing
        }
    }

    public void setTaskCount(Long executedTaskCount, Long activeTaskCount){
        this.executedTaskCount = executedTaskCount;
        this.activeTaskCount = activeTaskCount;
        try{
            Map<String,Long> map = Maps.newHashMap();
            map.put(KEY_EXECUTED_TASK_COUNT, executedTaskCount);
            map.put(KEY_ACTIVE_TASK_COUNT, activeTaskCount);
            this.data = JSON.toJSONString(map).getBytes(CHARSET);
        }catch (Exception e){
            //do nothing
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public byte[] getData() {
        return data;
    }

    public long getExecutedTaskCount() {
        return executedTaskCount;
    }

    public long getActiveTaskCount() {
        return activeTaskCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        Worker worker = (Worker)obj;
        return this.host.equals(worker.getHost()) && Arrays.equals(this.data, worker.getData());
    }

    @Override
    public int hashCode() {
        return host.hashCode() + data.hashCode();
    }

    @Override
    public String toString() {
        return "Worker{" + "host='" + host + '\'' + ", executedTaskCount=" + executedTaskCount + ", activeTaskCount="
               + activeTaskCount + '}';
    }
}
