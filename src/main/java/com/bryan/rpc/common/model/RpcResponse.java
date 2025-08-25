package com.bryan.rpc.common.model;

import java.io.Serializable;

public class RpcResponse extends Message implements Serializable {
    private String requestId;
    private Object result;
    private String error;

    public RpcResponse() {
    }

    public RpcResponse(Object result, String error, String requestId) {
        this.result = result;
        this.error = error;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
