package com.sunwayworld.emc.module.mdm.equipt.equipt.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.springframework.stereotype.Component;

import com.sunwayworld.framework.data.Pair;

@Component(value = "emcEquipExportTemplate")
public class EmcEquipExportTemplate implements GenericTemplateService<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcEquipExportTemplate", "仪器基本信息模版");
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
