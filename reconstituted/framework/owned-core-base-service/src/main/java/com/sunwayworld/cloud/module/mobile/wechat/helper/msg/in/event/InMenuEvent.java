/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event;

import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.EventInMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.in.event.ScanCodeInfo;

public class InMenuEvent
extends EventInMsg {
    public static final String EVENT_INMENU_CLICK = "CLICK";
    public static final String EVENT_INMENU_VIEW = "VIEW";
    public static final String EVENT_INMENU_SCANCODE_PUSH = "scancode_push";
    public static final String EVENT_INMENU_scancode_waitmsg = "scancode_waitmsg";
    public static final String EVENT_INMENU_PIC_SYSPHOTO = "pic_sysphoto";
    public static final String EVENT_INMENU_PIC_PHOTO_OR_ALBUM = "pic_photo_or_album";
    public static final String EVENT_INMENU_PIC_WEIXIN = "pic_weixin";
    public static final String EVENT_INMENU_LOCATION_SELECT = "location_select";
    public static final String EVENT_INMENU_MEDIA_ID = "media_id";
    public static final String EVENT_INMENU_VIEW_LIMITED = "view_limited";
    private String eventKey;
    private ScanCodeInfo scanCodeInfo;

    public InMenuEvent(String toUserName, String fromUserName, Integer createTime, String event) {
        super(toUserName, fromUserName, createTime, event);
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public ScanCodeInfo getScanCodeInfo() {
        return this.scanCodeInfo;
    }

    public void setScanCodeInfo(ScanCodeInfo scanCodeInfo) {
        this.scanCodeInfo = scanCodeInfo;
    }
}
