package com.sunwayworld.emc.module.mdm.user.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;

@Component(value = "emcUserExportTemplate")
public class EmcUserExportTemplate implements GenericTemplateService<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcUserExportTemplate", EmcConstant.EMC_EXPORT_USER);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
