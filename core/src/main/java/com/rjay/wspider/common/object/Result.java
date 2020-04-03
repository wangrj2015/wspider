package com.rjay.wspider.common.object;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private boolean success;

    private String reasonCode;

    private String reasonDesc;

    private T data;

    public static Result success(){
        return new Result().setSuccess(true);
    }

    public static <T> Result<T> success(T data){
        return new Result().setSuccess(true).setData(data);
    }

    public static Result fail(){
        return new Result().setSuccess(false);
    }

    public static Result fail(String reasonCode, String reasonDesc){
        return new Result().setSuccess(false).setReasonCode(reasonCode).setReasonDesc(reasonDesc);
    }

    public static Result fail(ErrorCode errorCode){
        return new Result().setSuccess(false).setReasonCode(errorCode.getCode()).setReasonDesc(errorCode.getDesc());
    }

    public boolean isSuccess() {
        return success;
    }

    public Result setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public Result setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String getReasonDesc() {
        return reasonDesc;
    }

    public Result setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" + "success=" + success + ", reasonCode='" + reasonCode + '\'' + ", reasonDesc='" + reasonDesc
               + '\'' + ", data=" + data + '}';
    }
}
