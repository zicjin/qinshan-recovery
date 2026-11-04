/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.function.FunctionAble;
import java.util.ArrayList;
import java.util.Collection;

public class LimsTemplateDataList<T>
extends ArrayList<T>
implements FunctionAble {
    private static final long serialVersionUID = 6180654841086700723L;

    public LimsTemplateDataList(int initialCapacity) {
        super(initialCapacity);
    }

    public LimsTemplateDataList() {
    }

    public LimsTemplateDataList(Collection<? extends T> c) {
        super(c);
    }
}
