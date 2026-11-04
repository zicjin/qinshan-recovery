/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.io.filemanage;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component(value="coreFileManageExportTemplate")
public class CoreFileManageExportTemplate
implements GenericTemplateService<String> {
    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        ArrayList<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair<String, String> templatePair = Pair.of("coreFileManageExportTemplate", "\u6587\u4ef6\u7ba1\u7406\u5bfc\u5165\u6a21\u677f");
        templatePairList.add(templatePair);
        return templatePairList;
    }
}
