/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.template.ElementTemplate;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsCustomizeRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.render.LimsCustomerRender;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimsCustomizeRenderPolicy<T>
extends AbstractRenderPolicy<LimsCustomizeRenderData<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimsCustomizeRenderPolicy.class);

    @Override
    protected void afterRender(RenderContext<LimsCustomizeRenderData<T>> context) {
        this.clearPlaceholder(context, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doRender(RenderContext<LimsCustomizeRenderData<T>> context) throws Exception {
        ElementTemplate eleTemplate = context.getEleTemplate();
        XWPFTemplate template = context.getTemplate();
        LimsCustomizeRenderData<T> data = context.getData();
        try {
            LimsCustomerRender customerRender = (LimsCustomerRender)ApplicationContextHelper.getBean(data.getCustomerRender());
            LOGGER.info("##rend data [{}] with customer render [{}] ##", (Object)eleTemplate.getTagName(), (Object)data.getCustomerRender());
            customerRender.render(eleTemplate, data.getData(), template);
        }
        catch (Exception e) {
            LOGGER.info("--rend data [{}] with customer render [{}] error --", eleTemplate.getTagName(), data.getCustomerRender(), e);
        }
        finally {
            this.clearPlaceholder(context, false);
        }
    }
}
