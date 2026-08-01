package com.techno.aiproject.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponce {
    private boolean result;
    private String message;
    private String code;

    @SerializedName("user_id")
    @Expose
    private int user_id;

    public LoginResponce(boolean result, String message, String code, int user_id) {
        this.result = result;
        this.message = message;
        this.code = code;
        this.user_id = user_id;
    }

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public int getUser_id() {
        return user_id;
    }
}
