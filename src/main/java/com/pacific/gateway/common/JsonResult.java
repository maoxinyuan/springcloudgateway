package com.pacific.gateway.common;

public class JsonResult {

    private boolean success;
    private String errorCode;
    private String msg;

    public JsonResult(){
        this.success = true;
        this.errorCode = String.valueOf(200);
    }

    public JsonResult(boolean success, String errorCode, String msg){
        this.success = success;
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}