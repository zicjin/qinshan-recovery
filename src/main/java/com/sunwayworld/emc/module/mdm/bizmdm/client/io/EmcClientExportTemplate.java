package com.sunwayworld.emc.module.mdm.bizmdm.client.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;

@Component(value = "emcClientExportTemplate")
public class EmcClientExportTemplate implements GenericTemplateService<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcClientExportTemplate", EmcConstant.EMC_EXPORT_CLIENT);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
