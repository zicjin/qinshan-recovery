/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core.server;

import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import org.springframework.lang.Nullable;

public class NettyServerConfig {
    private InetSocketAddress localAddress;
    private int bossConnectTimeoutMillis = 5000;
    private boolean workerKeepAlive = true;
    @Nullable
    private ChannelHandler bossGroupChannelHandler;
    private ChannelHandler workerGroupChannelHandler;

    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public int getBossConnectTimeoutMillis() {
        return this.bossConnectTimeoutMillis;
    }

    public void setBossConnectTimeoutMillis(int bossConnectTimeoutMillis) {
        this.bossConnectTimeoutMillis = bossConnectTimeoutMillis;
    }

    public boolean isWorkerKeepAlive() {
        return this.workerKeepAlive;
    }

    public void setWorkerKeepAlive(boolean workerKeepAlive) {
        this.workerKeepAlive = workerKeepAlive;
    }

    public ChannelHandler getBossGroupChannelHandler() {
        return this.bossGroupChannelHandler;
    }

    public void setBossGroupChannelHandler(ChannelHandler bossGroupChannelHandler) {
        this.bossGroupChannelHandler = bossGroupChannelHandler;
    }

    public ChannelHandler getWorkerGroupChannelHandler() {
        return this.workerGroupChannelHandler;
    }

    public void setWorkerGroupChannelHandler(ChannelHandler workerGroupChannelHandler) {
        this.workerGroupChannelHandler = workerGroupChannelHandler;
    }
}
