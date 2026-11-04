/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.service;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.io.File;
import java.util.List;
import java.util.Map;

public interface CoreOfficeTmplService
extends GenericService<CoreOfficeTmplBean, Long>,
GenericActivatableService<CoreOfficeTmplBean, Long>,
GenericChoosableService<CoreOfficeTmplBean, Long> {
    public String buildReport(Long var1, RestJsonWrapperBean var2, boolean var3);

    public CoreOfficeInstanceDTO buildReport(Long var1, Map<String, String> var2, boolean var3);

    public Long insertBlankDocument(Long var1);

    public void clearBindDs(Long var1);

    public List<String> selectBindDsNameList(Long var1, File var2);

    public String getTestFormFieldByTmplId(Long var1, RestJsonWrapperBean var2);
}
