/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core.handler;

import com.sunwayworld.framework.netty.core.channel.NettyChannelHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyChannelActiveHandler
extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyChannelHolder.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelInactive(ctx);
        }
        finally {
            NettyChannelHolder.remove(ctx.channel());
        }
    }
}
