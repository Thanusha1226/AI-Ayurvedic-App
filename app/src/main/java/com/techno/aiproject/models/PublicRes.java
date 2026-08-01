package com.techno.aiproject.models;

public class PublicRes {
    private boolean result;
    private String code;
    private String type;
    private String message;
    private String user_id;

    public PublicRes() {}

    public PublicRes(boolean result, String code, String type, String message, String user_id) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.result = result;
        this.user_id = user_id;
    }

    public boolean isResult() {
        return result;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getUser_id() {
        return user_id;
    }
}
