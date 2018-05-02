package com.shaw.dolores.utils;

public class ResponseDataholder<T> {
    private T payload;
    private boolean success;
    private String msg;
    private int code;
    private long timestamp;

    public ResponseDataholder() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResponseDataholder success(T payload) {
        return new ResponseDataholder<>(true, ResponseCode.SUCCESS.getCode(), payload, ResponseCode.SUCCESS.getMsg());
    }

    public static <T> ResponseDataholder success() {
        return new ResponseDataholder<>(true, ResponseCode.SUCCESS.getCode(), null, ResponseCode.SUCCESS.getMsg());
    }

    public static ResponseDataholder fail(ResponseCode responseCode) {
        return new ResponseDataholder<>(false, responseCode.getCode(), null, responseCode.getMsg());
    }

    public static ResponseDataholder fail(String msg) {
        return new ResponseDataholder<>(false, 500, null, msg);
    }

    public ResponseDataholder(boolean success, int code, T payload, String msg) {
        this.timestamp = System.currentTimeMillis();
        this.success = success;
        this.payload = payload;
        this.msg = msg;
        this.code = code;
    }

    public T getPayload() {
        return this.payload;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
