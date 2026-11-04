/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.render;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.template.ElementTemplate;

public interface LimsCustomerRender<T> {
    public void render(ElementTemplate var1, T var2, XWPFTemplate var3);
}
