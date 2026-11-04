/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.support;

import com.sunwayworld.cloud.module.lcdp.support.AbstractCustomMap;
import java.util.Map;
import java.util.function.Consumer;

public class LcdpChainMap<K, V>
extends AbstractCustomMap<K, V> {
    public LcdpChainMap() {
    }

    public LcdpChainMap(Map<K, V> map) {
        super(map);
    }

    public LcdpChainMap(Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public LcdpChainMap(K key, V value) {
        this();
        this.put(key, value);
    }

    public LcdpChainMap(Map<K, V> map, K key, V value) {
        this(map);
        this.put(key, value);
    }

    public static <K, V> LcdpChainMap<K, V> of(K key, V value) {
        return new LcdpChainMap<K, V>().set(key, value);
    }

    public LcdpChainMap<K, V> set(K key, V value) {
        return this.set(true, key, value);
    }

    public LcdpChainMap<K, V> set(boolean condition, K key, V value) {
        if (condition) {
            this.put(key, value);
        }
        return this;
    }

    public LcdpChainMap<K, V> func(Consumer<LcdpChainMap<K, V>> function) {
        return this.func(true, function);
    }

    public LcdpChainMap<K, V> func(boolean condition, Consumer<LcdpChainMap<K, V>> function) {
        if (condition && function != null) {
            function.accept(this);
        }
        return this;
    }
}
