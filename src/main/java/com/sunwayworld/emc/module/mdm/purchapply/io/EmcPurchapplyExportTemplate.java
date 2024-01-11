package com.sunwayworld.emc.module.mdm.purchapply.io;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luoc@sunwayworld.com 2020/11/19
 */
@Component(value = "emcPurchapplyExportTemplate")
public class EmcPurchapplyExportTemplate implements GenericExportTemplate<String> {

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcPurchapplyExportTemplate", EmcConstant.EMC_EXPORT_PURCHAPPLY);
        templatePairList.add(templatePair);
        return templatePairList;
    }
}
