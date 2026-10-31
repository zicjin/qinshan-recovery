package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;

@GikamBean
@Component(value = "emcMtlExportTemplate")
public class EmcMtlExportTemplate implements GenericTemplateService<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcMtlExportTemplate", EmcConstant.EMC_EXPORT_MTL);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
