/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import java.util.HashMap;
import java.util.Map;

public class Kv
extends HashMap<Object, Object> {
    private static final long serialVersionUID = 1L;
    private static final String STATUS_OK = "isOk";
    private static final String STATUS_FAIL = "isFail";

    public static Kv by(Object key, Object value) {
        return new Kv().set(key, value);
    }

    public static Kv create() {
        return new Kv();
    }

    public static Kv ok() {
        return new Kv().setOk();
    }

    public static Kv ok(Object key, Object value) {
        return Kv.ok().set(key, value);
    }

    public static Kv fail() {
        return new Kv().setFail();
    }

    public static Kv fail(Object key, Object value) {
        return Kv.fail().set(key, value);
    }

    public Kv setOk() {
        super.put(STATUS_OK, Boolean.TRUE);
        super.put(STATUS_FAIL, Boolean.FALSE);
        return this;
    }

    public Kv setFail() {
        super.put(STATUS_OK, Boolean.FALSE);
        super.put(STATUS_FAIL, Boolean.TRUE);
        return this;
    }

    public boolean isOk() {
        Boolean isOk = (Boolean)this.get(STATUS_OK);
        return isOk != null && isOk != false;
    }

    public boolean isFail() {
        Boolean isFail = (Boolean)this.get(STATUS_FAIL);
        return isFail != null && isFail != false;
    }

    public Kv set(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public Kv set(Map<Object, Object> map) {
        super.putAll(map);
        return this;
    }

    public Kv set(Kv kv) {
        super.putAll(kv);
        return this;
    }

    public Kv delete(Object key) {
        super.remove(key);
        return this;
    }

    public Object getAs(Object key) {
        return this.get(key);
    }

    public String getStr(Object key) {
        return (String)this.get(key);
    }

    public Integer getInt(Object key) {
        return (Integer)this.get(key);
    }

    public Long getLong(Object key) {
        return (Long)this.get(key);
    }

    public Boolean getBoolean(Object key) {
        return (Boolean)this.get(key);
    }

    public boolean notNull(Object key) {
        return this.get(key) != null;
    }

    public boolean isNull(Object key) {
        return this.get(key) == null;
    }

    public boolean isTrue(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && (Boolean)value == true;
    }

    public boolean isFalse(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && (Boolean)value == false;
    }

    @Override
    public boolean equals(Object kv) {
        return kv instanceof Kv && super.equals(kv);
    }
}
