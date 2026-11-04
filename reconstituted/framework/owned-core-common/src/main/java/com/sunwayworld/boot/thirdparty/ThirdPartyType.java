/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot.thirdparty;

import com.sunwayworld.boot.thirdparty.Extractor;
import com.sunwayworld.boot.thirdparty.sunwaylibrary.SunwayLibraryExtractor;
import com.sunwayworld.boot.thirdparty.watermarkfont.WaterMarkFontExtractor;
import com.sunwayworld.framework.utils.ClassUtils;

public enum ThirdPartyType {
    SUNWAY_LIBRARY(SunwayLibraryExtractor.class),
    WATER_MARK_FONT(WaterMarkFontExtractor.class);

    private Class<? extends Extractor> extractorClass;

    private ThirdPartyType(Class<? extends Extractor> extractorClass) {
        this.extractorClass = extractorClass;
    }

    public Extractor getInstance() {
        return ClassUtils.newInstance(this.extractorClass);
    }
}
