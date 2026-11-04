/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.helper;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceDTO;
import com.sunwayworld.cloud.module.office.service.CoreOfficeTmplService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import java.util.Map;

public class OfficeHelper {
    public static CoreOfficeInstanceDTO buildReport(Long tmplId, Map<String, String> officeParamMap) {
        CoreOfficeTmplService officeTmplService = ApplicationContextHelper.getBean(CoreOfficeTmplService.class);
        return officeTmplService.buildReport(tmplId, officeParamMap, true);
    }
}
