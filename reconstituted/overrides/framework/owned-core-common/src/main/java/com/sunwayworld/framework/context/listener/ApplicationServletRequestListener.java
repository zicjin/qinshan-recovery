/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.listener;

import com.sunwayworld.framework.context.LocalContextHelper;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import org.springframework.stereotype.Component;

@WebListener
@Component
public class ApplicationServletRequestListener
implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        LocalContextHelper.removeServletRequest();
        LocalContextHelper.removeServletResponse();
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        LocalContextHelper.setServletRequest(event.getServletRequest());
    }
}
