/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.List;
import java.util.Map;

public interface CoreLesInstanceService
extends GenericService<CoreLesInstanceBean, Long>,
GenericAuditableService<CoreLesInstanceBean, Long> {
    public Long insertByTemplateId(Long var1, RestJsonWrapperBean var2);

    public Long insertByTemplateId(Long var1, String var2, Map<String, String> var3);

    public CoreLesInstanceUnitBean selectUnit(Long var1, Long var2);

    public void saveUnit(Long var1, Long var2, RestJsonWrapperBean var3);

    public Page<CoreLesInstanceUnitDTO> selectUnitTreePagination(Long var1, RestJsonWrapperBean var2);

    public Long insertUnit(Long var1, RestJsonWrapperBean var2);

    public Page<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean var1);

    public void deleteUnit(Long var1, Long var2);

    public void swapUnit(Long var1, Long var2);

    public void dedentSection(Long var1, RestJsonWrapperBean var2);

    public void indentSection(Long var1, Long var2, RestJsonWrapperBean var3);

    public List<Map<String, Object>> selectLesUnitDataByUnitId(Long var1, RestJsonWrapperBean var2);

    public List<Map<String, Object>> getDataByDs(Long var1, Map<String, String> var2);

    public void updatePostUnitOrderByUnitId(Long var1, Long var2);

    public Long pasteUnit(Long var1, RestJsonWrapperBean var2);
}
