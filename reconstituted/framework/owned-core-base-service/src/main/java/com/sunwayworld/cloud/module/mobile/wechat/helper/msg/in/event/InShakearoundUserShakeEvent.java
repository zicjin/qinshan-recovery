/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.InMsg;
import java.util.ArrayList;
import java.util.List;

public class InShakearoundUserShakeEvent
extends InMsg {
    private String event;
    private String uuid;
    private Integer major;
    private Integer minor;
    private Float distance;
    private List<AroundBeacon> aroundBeaconList = new ArrayList<AroundBeacon>();

    public InShakearoundUserShakeEvent(String toUserName, String fromUserName, Integer createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getMajor() {
        return this.major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return this.minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Float getDistance() {
        return this.distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public List<AroundBeacon> getAroundBeaconList() {
        return this.aroundBeaconList;
    }

    public void setAroundBeaconList(List<AroundBeacon> aroundBeaconList) {
        this.aroundBeaconList = aroundBeaconList;
    }

    public static class AroundBeacon {
        private String uuid;
        private Integer major;
        private Integer minor;
        private Float distance;

        public String getUuid() {
            return this.uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Integer getMajor() {
            return this.major;
        }

        public void setMajor(Integer major) {
            this.major = major;
        }

        public Integer getMinor() {
            return this.minor;
        }

        public void setMinor(Integer minor) {
            this.minor = minor;
        }

        public Float getDistance() {
            return this.distance;
        }

        public void setDistance(Float distance) {
            this.distance = distance;
        }
    }
}
