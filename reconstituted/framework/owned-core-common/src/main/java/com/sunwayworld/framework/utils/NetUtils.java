/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class NetUtils {
    private static final String IPV6_DEFAULT_LOCALIP_MARK = "0:0:0:0:0:0:0:";
    private static final String IPV4_DEFAULT_LOCALIP_MARK = "0.0.0.0";

    public static final boolean equals(SocketAddress lsa, SocketAddress rsa) {
        if (lsa == null) {
            return rsa == null;
        }
        if (rsa == null) {
            return false;
        }
        if (!lsa.getClass().equals(rsa.getClass())) {
            return false;
        }
        if (lsa.equals(rsa)) {
            return true;
        }
        if (StringUtils.removeStart(lsa.toString(), "/").equals(StringUtils.removeStart(rsa.toString(), "/"))) {
            return true;
        }
        lsa = NetUtils.resolve(lsa);
        rsa = NetUtils.resolve(rsa);
        return lsa.equals(rsa);
    }

    public static final boolean equals(String leftAddress, String rightAddress) {
        if (StringUtils.contains(leftAddress, IPV6_DEFAULT_LOCALIP_MARK) || StringUtils.contains(leftAddress, IPV4_DEFAULT_LOCALIP_MARK)) {
            leftAddress = new InetSocketAddress("localhost", NumberUtils.parseInt(leftAddress.substring(leftAddress.lastIndexOf(":") + 1))).toString();
        }
        if (StringUtils.contains(rightAddress, IPV6_DEFAULT_LOCALIP_MARK) || StringUtils.contains(rightAddress, IPV4_DEFAULT_LOCALIP_MARK)) {
            rightAddress = new InetSocketAddress("localhost", NumberUtils.parseInt(rightAddress.substring(rightAddress.lastIndexOf(":") + 1))).toString();
        }
        return StringUtils.removeStart(leftAddress, "/").equals(StringUtils.removeStart(rightAddress.toString(), "/"));
    }

    private static final SocketAddress resolve(SocketAddress socketAddress) {
        String plainSocketAddress = socketAddress.toString();
        if (StringUtils.contains(plainSocketAddress, IPV6_DEFAULT_LOCALIP_MARK) || StringUtils.contains(plainSocketAddress, IPV4_DEFAULT_LOCALIP_MARK)) {
            return new InetSocketAddress("localhost", NumberUtils.parseInt(plainSocketAddress.substring(plainSocketAddress.lastIndexOf(":") + 1)));
        }
        return socketAddress;
    }
}
