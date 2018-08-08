package com.ditp.domain;

import java.io.Serializable;

public class JsonObject implements Serializable {
    private static final long serialVersionUID = -4383923595659430916L;

    private boolean success = false;
    private String msg = "";
    private transient Object data = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}