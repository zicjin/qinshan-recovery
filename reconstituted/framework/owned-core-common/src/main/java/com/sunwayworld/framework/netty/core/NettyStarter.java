/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core;

import java.net.InetSocketAddress;

public interface NettyStarter {
    public void start();

    public void destroy();

    public boolean match(InetSocketAddress var1);
}
