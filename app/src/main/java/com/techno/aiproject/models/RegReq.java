package com.techno.aiproject.models;

public class RegReq {
    private String name;
    private String deviceId;
    private String customer_id;
    private int step;
    private String verify_code;
    private String first_name;
    private String last_name;
    private String email;
    private String phoneNumber;
    private String phone_country_code;
    private String password;

    public RegReq() {}

    public RegReq(String name, String deviceId, int step, String customer_id, String verify_code, String first_name, String last_name, String email,
                  String phoneNumber, String phone_country_code, String password) {
        this.name = name;
        this.customer_id = customer_id;
        this.deviceId = deviceId;
        this.step = step;
        this.verify_code = verify_code;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.phone_country_code = phone_country_code;
        this.password = password;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhone_country_code(String phone_country_code) {
        this.phone_country_code = phone_country_code;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() { return name; }
    public String getDeviceId() { return deviceId; }
    public String getCustomer_id() { return customer_id; }
    public int getStep() { return step; }
    public String getVerify_code() { return verify_code; }
    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPhone_country_code() { return phone_country_code; }
    public String getPassword() { return password; }
}
