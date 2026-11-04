/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.compute;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.deepoove.poi.render.compute.RenderDataComputeFactory;

@Deprecated
public class LimsRenderDataComputeFactory
implements RenderDataComputeFactory {
    private final Configure config;

    public LimsRenderDataComputeFactory(Configure config) {
        this.config = config;
    }

    @Override
    public RenderDataCompute newCompute(EnvModel model) {
        return null;
    }

    @Override
    public RenderDataCompute newCompute(Object model) {
        RenderDataCompute render = null;
        return render;
    }
}
