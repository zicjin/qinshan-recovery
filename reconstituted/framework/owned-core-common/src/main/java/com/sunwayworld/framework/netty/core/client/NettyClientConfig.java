/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core.client;

import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;

public class NettyClientConfig {
    private InetSocketAddress serverAddress;
    private int connectTimeoutMillis = 5000;
    private boolean keepAlive = true;
    private ChannelHandler channelHandler;
    private int maxAttempts = 10;

    public InetSocketAddress getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public ChannelHandler getChannelHandler() {
        return this.channelHandler;
    }

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
