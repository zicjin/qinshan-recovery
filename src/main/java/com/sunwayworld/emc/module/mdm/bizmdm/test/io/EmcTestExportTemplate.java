package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "emcTestExportTemplate")
public class EmcTestExportTemplate implements GenericExportTemplate<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcTestExportTemplate", EmcConstant.EMC_EXPORT_TEST);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
