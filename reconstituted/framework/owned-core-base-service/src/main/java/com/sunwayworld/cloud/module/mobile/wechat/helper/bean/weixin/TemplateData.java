/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.bean.weixin;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.HashMap;

public class TemplateData
implements Serializable {
    private static final long serialVersionUID = 8038149984818112449L;
    private String touser;
    private String template_id;
    private String url;
    private TemplateItem data = new TemplateItem();
    private MiniProgramData miniprogram;

    public static TemplateData New() {
        return new TemplateData();
    }

    private TemplateData() {
    }

    public String getTouser() {
        return this.touser;
    }

    public TemplateData setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public TemplateData setTemplate_id(String template_id) {
        this.template_id = template_id;
        return this;
    }

    public String getUrl() {
        return this.url;
    }

    public TemplateData setUrl(String url) {
        this.url = url;
        return this;
    }

    public TemplateItem getData() {
        return this.data;
    }

    public TemplateData add(String key, String value, String color) {
        this.data.put(key, new Item(value, color));
        return this;
    }

    public TemplateData add(String key, String value) {
        this.data.put(key, new Item(value));
        return this;
    }

    public MiniProgramData getMiniprogram() {
        return this.miniprogram;
    }

    public TemplateData setMiniprogram(String appid, String pagepath) {
        this.miniprogram = new MiniProgramData(appid, pagepath);
        return this;
    }

    public String build() {
        return JSON.toJSONString(this);
    }

    public class MiniProgramData {
        private String appid;
        private String pagepath;

        public MiniProgramData(String appid, String pagepath) {
            this.appid = appid;
            this.pagepath = pagepath;
        }

        public String getAppid() {
            return this.appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPagepath() {
            return this.pagepath;
        }

        public void setPagepath(String pagepath) {
            this.pagepath = pagepath;
        }
    }

    public class Item {
        private Object value;
        private String color;

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getColor() {
            return this.color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Item(Object value) {
            this(value, "#999");
        }

        public Item(Object value, String color) {
            this.value = value;
            this.color = color;
        }
    }

    public class TemplateItem
    extends HashMap<String, Item> {
        private static final long serialVersionUID = -3728490424738325020L;

        public TemplateItem() {
        }

        public TemplateItem(String key, Item item) {
            this.put(key, item);
        }
    }
}
