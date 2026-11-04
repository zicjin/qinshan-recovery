/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericImportService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.List;

public interface CoreLesTemplateService
extends GenericService<CoreLesTemplateBean, Long>,
GenericAuditableService<CoreLesTemplateBean, Long>,
GenericChoosableService<CoreLesTemplateBean, Long>,
GenericImportService<CoreLesTemplateBean, Long> {
    public Long newVersion(Long var1, RestJsonWrapperBean var2);

    public Long copy(Long var1, RestJsonWrapperBean var2);

    public void expireTemplate(RestJsonWrapperBean var1);

    public Long insertUnit(Long var1, RestJsonWrapperBean var2);

    public CoreLesTemplateUnitBean selectUnit(String var1, Long var2);

    public void saveUnit(String var1, Long var2, RestJsonWrapperBean var3);

    public void deleteUnit(String var1, Long var2);

    public Page<CoreLesTemplateUnitDTO> selectUnitTreePagination(String var1, RestJsonWrapperBean var2);

    public void swapUnit(Long var1, Long var2);

    public void dedentSection(Long var1, RestJsonWrapperBean var2);

    public void indentSection(Long var1, Long var2, RestJsonWrapperBean var3);

    public Page<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean var1);

    public List<CoreLesTemplateUnitBean> selectUnitList(Long var1);

    public RestValidationResultBean submitValidate(RestJsonWrapperBean var1);
}
