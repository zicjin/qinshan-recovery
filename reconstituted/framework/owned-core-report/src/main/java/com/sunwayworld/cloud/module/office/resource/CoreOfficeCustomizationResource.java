/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeCustomizationBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;

public interface CoreOfficeCustomizationResource
extends GenericCloudResource<CoreOfficeCustomizationBean, String> {
    public CoreOfficeCustomizationBean selectOfficeCustomization();
}
