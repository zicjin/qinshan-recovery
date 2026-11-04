/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.property;

public class RestfulResolverDefinition {
    private static boolean restJsonWrapperEnable = true;

    public RestfulResolverDefinition() {
    }

    public RestfulResolverDefinition(boolean enable) {
        restJsonWrapperEnable = enable;
    }

    public static boolean isRestJsonWrapperEnable() {
        return restJsonWrapperEnable;
    }
}
