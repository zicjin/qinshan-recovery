/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import org.springframework.stereotype.Component;

@WebListener
@Component
public class ApplicationServletRequestListener
implements ServletRequestListener {
    @Override
    public native void requestDestroyed(ServletRequestEvent var1);

    @Override
    public native void requestInitialized(ServletRequestEvent var1);
}
