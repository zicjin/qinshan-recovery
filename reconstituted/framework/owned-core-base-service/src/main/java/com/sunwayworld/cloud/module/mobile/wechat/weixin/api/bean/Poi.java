/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.bean;

import java.io.Serializable;
import java.util.List;

public class Poi
implements Serializable {
    private static final long serialVersionUID = 4899154126408515026L;
    private String sid;
    private String business_name;
    private String branch_name;
    private String province;
    private String city;
    private String district;
    private String address;
    private String telephone;
    private List<String> categories;
    private Integer offset_type;
    private String longitude;
    private String latitude;
    private List<PhotoUrl> photo_list;
    private String recommend;
    private String special;
    private String introduction;
    private String open_time;
    private Integer avg_price;
    private String poi_id;
    private Integer available_state;
    private Integer update_status;

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getBusiness_name() {
        return this.business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBranch_name() {
        return this.branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Integer getOffset_type() {
        return this.offset_type;
    }

    public void setOffset_type(Integer offset_type) {
        this.offset_type = offset_type;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<PhotoUrl> getPhoto_list() {
        return this.photo_list;
    }

    public void setPhoto_list(List<PhotoUrl> photo_list) {
        this.photo_list = photo_list;
    }

    public String getRecommend() {
        return this.recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getOpen_time() {
        return this.open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public Integer getAvg_price() {
        return this.avg_price;
    }

    public void setAvg_price(Integer avg_price) {
        this.avg_price = avg_price;
    }

    public String getPoi_id() {
        return this.poi_id;
    }

    public void setPoi_id(String poi_id) {
        this.poi_id = poi_id;
    }

    public Integer getAvailable_state() {
        return this.available_state;
    }

    public void setAvailable_state(Integer available_state) {
        this.available_state = available_state;
    }

    public Integer getUpdate_status() {
        return this.update_status;
    }

    public void setUpdate_status(Integer update_status) {
        this.update_status = update_status;
    }

    public static class PhotoUrl {
        private String photo_url;

        public String getPhoto_url() {
            return this.photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }
    }
}
