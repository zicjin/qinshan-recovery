package com.sunwayworld.emc.module.mdm.bizmdm.method.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "emcMethodExportTemplate")
public class EmcMethodExportTemplate implements GenericExportTemplate<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcMethodExportTemplate", EmcConstant.EMC_EXPORT_METHOD);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
