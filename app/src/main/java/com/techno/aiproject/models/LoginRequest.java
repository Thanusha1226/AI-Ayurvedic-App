package com.techno.aiproject.models;

public class LoginRequest {
    private String email;
    private String step;
    private String cart_reference;
    private String phoneNumber;
    private String deviceId;
    private String password;
    private String device_type;
    private String push_token;
    private String device_token;
    private String customer_id;

    public LoginRequest() {}

    public LoginRequest(String email, String step, String password, String device_type, String push_token, String device_token, String customer_id,
                        String cart_reference, String phoneNumber, String deviceId) {
        this.email = email;
        this.password = password;
        this.device_type = device_type;
        this.push_token = push_token;
        this.device_token = device_token;
        this.step = step;
        this.cart_reference = cart_reference;
        this.customer_id = customer_id;
        this.phoneNumber = phoneNumber;
        this.deviceId = deviceId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCart_reference(String cart_reference) {
        this.cart_reference = cart_reference;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getEmail() { return email; }
    public String getStep() { return step; }
    public String getCart_reference() { return cart_reference; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDeviceId() { return deviceId; }
    public String getPassword() { return password; }
    public String getDevice_type() { return device_type; }
    public String getPush_token() { return push_token; }
    public String getDevice_token() { return device_token; }
    public String getCustomer_id() { return customer_id; }
}
