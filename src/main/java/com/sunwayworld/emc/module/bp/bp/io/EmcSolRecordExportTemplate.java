package com.sunwayworld.emc.module.bp.bp.io;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.support.base.service.GenericTemplateService;
import org.springframework.stereotype.Component;

import com.sunwayworld.framework.data.Pair;

@Component(value = "emcSolRecordExportTemplate")
public class EmcSolRecordExportTemplate implements GenericTemplateService<String> {

    public static final String EMC_EXPORT_TEST = "标定原始记录导入模版";
    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcSolRecordExportTemplate", EMC_EXPORT_TEST);
        templatePairList.add(templatePair);
        return templatePairList;
    }

}
