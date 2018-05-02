package com.shaw.dolores.utils;

public enum ResponseCode {
    SUCCESS(200, "操作成功"),
    LOGIN_WRONG(999, "账号或密码错误"),
    NOT_LOGIN(1000, "未登录,请先登录"),
    PARAM_NULL(1001, "参数为空"),
    FIND_NULL(1002, "查询为空"),
    FAIL(1003, "操作失败"),
    CODES_WRONG(1004, "验证码错误"),
    IP_WRONG(1005, "IP验证无效"),
    ID_WRONG(1006, "ID无效"),
    MSG_OVER(1007, "消息已结束"),
    LOGIN_TIMEOUT(1008, "登录超时"),
    PERMISSION_WRONG(1009, "权限不足"),
    SOCKET_NOT_CONNECT(1010, "任务执行客户端未连接"),
    USER_REPEAT(1011, "用户重复"),
    PARAM_NOT_FORMAT(1012, "参数格式错误"),
    TRY_LATER(1013, "请稍后重试");

    /**
     * 消息状态
     */
    private int code;
    /**
     * 信息
     */
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}