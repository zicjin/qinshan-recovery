/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SerializationUtils {
    private static final ThreadLocal<Kryo> KRYO_CONTAINER = new ThreadLocal<Kryo>(){

        @Override
        protected Kryo initialValue() {
            return new Kryo();
        }
    };

    public static final <T> void serialize(OutputStream output, T item) {
        if (item == null) {
            return;
        }
        Class<?> clazz = item.getClass();
        Kryo kryo = KRYO_CONTAINER.get();
        kryo.register(clazz);
        try (Output os = new Output(output, 10240);){
            kryo.writeObject(os, item);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final <T> byte[] serialize(T item) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            SerializationUtils.serialize(baos, item);
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
        catch (IOException ioe) {
            throw new ApplicationRuntimeException(ioe);
        }
    }

    public static final <T> T deserialize(InputStream input, Class<T> clazz) {
        if (input == null) {
            return null;
        }
        Kryo kryo = KRYO_CONTAINER.get();
        kryo.register(clazz);
        try (Input in = new Input(input, 10240);){
            T t = kryo.readObject(in, clazz);
            return t;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static final <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null) return null;
        if (bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);){
            T t = SerializationUtils.deserialize(bais, clazz);
            return t;
        }
        catch (IOException ioe) {
            throw new ApplicationRuntimeException(ioe);
        }
    }
}
