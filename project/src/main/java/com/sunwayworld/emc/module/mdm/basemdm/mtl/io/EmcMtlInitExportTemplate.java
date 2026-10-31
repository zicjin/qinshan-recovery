package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@GikamBean
@Component(value="emcMtlInitExportTemplate")
public class EmcMtlInitExportTemplate
implements GenericTemplateService<String> {
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<Pair<String, String>>();
        Pair templatePair = Pair.of((Object)"emcMtlInitExportTemplate", (Object)"\u6750\u6599\u7ba1\u7406\u521d\u59cb\u5316\u5bfc\u5165\u6a21\u677f");
        templatePairList.add(templatePair);
        return templatePairList;
    }
}
