package com.sunwayworld.emc.framework.sso;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author zhengxy@sunwayworld.com 2023年11月27日
 */
public class IdaasUserInfoResponse implements Serializable {

    private String sub;
    private String name;
    private String email;
    @JSONField(name = "phone_number")
    private String phoneNumber;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
