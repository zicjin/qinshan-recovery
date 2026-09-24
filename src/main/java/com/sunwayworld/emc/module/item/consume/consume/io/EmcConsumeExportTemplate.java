/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.item.consume.consume.io;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@GikamBean
@Component(value="emcConsumeExportTemplate")
public class EmcConsumeExportTemplate
implements GenericTemplateService<String> {
    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        ArrayList<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair<String, String> templatePair = Pair.of("emcConsumeExportTemplate", "\u8017\u6750\u5bfc\u5165\u6a21\u677f");
        templatePairList.add(templatePair);
        return templatePairList;
    }
}
