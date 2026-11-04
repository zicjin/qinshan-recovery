/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core.channel;

import com.sunwayworld.framework.utils.NetUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.SocketAddress;

public class NettyChannelHolder {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static final void add(Channel channel) {
        channelGroup.add(channel);
    }

    public static final void remove(Channel channel) {
        channelGroup.remove(channel);
    }

    public static final boolean isRemotePresent(SocketAddress remtoteSocketAddress) {
        for (Channel channel : channelGroup) {
            if (!NetUtils.equals(remtoteSocketAddress, channel.remoteAddress())) continue;
            return true;
        }
        return false;
    }

    public static final boolean isLocalPresent(SocketAddress localSocketAddress) {
        for (Channel channel : channelGroup) {
            if (!NetUtils.equals(localSocketAddress, channel.localAddress())) continue;
            return true;
        }
        return false;
    }

    public static final Channel getByRemote(SocketAddress remtoteSocketAddress) {
        for (Channel channel : channelGroup) {
            if (!NetUtils.equals(remtoteSocketAddress, channel.remoteAddress())) continue;
            return channel;
        }
        return null;
    }

    public static final Channel getByLocal(SocketAddress localSocketAddress) {
        for (Channel channel : channelGroup) {
            if (!NetUtils.equals(localSocketAddress, channel.localAddress())) continue;
            return channel;
        }
        return null;
    }

    public static final ChannelGroup getGroup() {
        return channelGroup;
    }
}
