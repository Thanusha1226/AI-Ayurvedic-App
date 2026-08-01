package com.techno.aiproject.models;

public class FrogRequest {
    private String PhoneNumber;
    private String phone_country_code;
    private String device_type;
    private String customer_id;
    private String new_password;
    private int step;
    private String verify_code;
    private String password;

    public FrogRequest() {}

    public FrogRequest(String PhoneNumber, String phone_country_code, String verify_code, String password, String device_type, String customer_id,
                       String new_password, int step) {
        this.PhoneNumber = PhoneNumber;
        this.phone_country_code = phone_country_code;
        this.verify_code = verify_code;
        this.password = password;
        this.device_type = device_type;
        this.customer_id = customer_id;
        this.new_password = new_password;
        this.step = step;
    }

    public void setPhone(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public void setPhone_country_code(String phone_country_code) {
        this.phone_country_code = phone_country_code;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public String getPhone() { return PhoneNumber; }
    public String getPhone_country_code() { return phone_country_code; }
    public String getDevice_type() { return device_type; }
    public String getCustomer_id() { return customer_id; }
    public String getNew_password() { return new_password; }
    public int getStep() { return step; }
    public String getVerify_code() { return verify_code; }
    public String getPassword() { return password; }
}
