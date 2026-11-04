/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.core;

import com.sunwayworld.framework.netty.core.NettyStarter;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum NettyStarterRegistry {
    INSTANCE;

    private static final Set<NettyStarter> starterSet;

    public NettyStarter getByAddress(InetSocketAddress socketAddress) {
        return starterSet.stream().filter(s -> s.match(socketAddress)).findAny().orElse(null);
    }

    public void removeUnavailable(List<InetSocketAddress> availableSocketAddressList) {
        starterSet.removeIf(s -> availableSocketAddressList.stream().noneMatch(a -> s.match((InetSocketAddress)a)));
    }

    public void add(NettyStarter nettyStarter) {
        starterSet.add(nettyStarter);
    }

    public void addAll(Collection<NettyStarter> starterList) {
        starterSet.addAll(starterList);
    }

    public boolean isPresent(NettyStarter starter) {
        return starterSet.contains(starter);
    }

    public Set<NettyStarter> getStarterSet() {
        return starterSet;
    }

    public void clear() {
        starterSet.clear();
    }

    static {
        starterSet = ConcurrentHashMap.newKeySet();
    }
}
