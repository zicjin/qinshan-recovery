package com.sunwayworld.emc.module.bp.io;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;
@Component(value = "emcOrderTaskResultExportTemplate")
public class EmcOrderTaskResultExportTemplate implements GenericExportTemplate<String> {
    
    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcOrderTaskResultExportTemplate", "结果导出模板");
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
