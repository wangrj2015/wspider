package com.rjay.wspider.common.object;

public enum ErrorCode {

    TASK_IS_EMPTY("201","任务不能为空"),

    TASK_TYPE_IS_EMPTY("202","任务类型不能为空"),

    NOT_LEADER("203","not leader");

    String code;

    String desc;

    ErrorCode(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
