/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreLesInstanceResource
extends GenericCloudResource<CoreLesInstanceBean, Long>,
GenericAuditableCloudResource<CoreLesInstanceBean, Long> {
    @RequestMapping(value={"/source-templates/{templateId}"}, method={RequestMethod.POST})
    public Long insertByTemplateId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/instance-units"}, method={RequestMethod.POST})
    public Long insertUnit(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/instance-units/{unitId}"}, method={RequestMethod.GET})
    public CoreLesInstanceUnitBean selectUnit(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/instance-units/{unitId}"}, method={RequestMethod.PUT})
    public void saveUnit(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/instance-units/{unitId}/action/refresh"}, method={RequestMethod.PUT})
    public void refreshUnit(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/{id}/instance-units/{unitId}"}, method={RequestMethod.DELETE})
    public void deleteUnit(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/{id}/instance-units/tree-grid"}, method={RequestMethod.POST})
    public Pagination<CoreLesInstanceUnitDTO> selectUnitTreePagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/instance-units/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapUnit(@PathVariable Long var1, @PathVariable Long var2, @PathVariable Long var3);

    @RequestMapping(value={"/{unitId}/action/dedent"}, method={RequestMethod.PUT})
    public void dedentSection(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{unitId}/action/indent/{parentId}"}, method={RequestMethod.PUT})
    public void indentSection(@PathVariable Long var1, @PathVariable Long var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/queries/instance-units"}, method={RequestMethod.POST})
    public Pagination<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{unitId}/instance-unit-datas"}, method={RequestMethod.POST})
    public List<Map<String, Object>> selectLesUnitDataByUnitId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/action/paste-instance-units"}, method={RequestMethod.POST})
    public Long pasteUnit(@PathVariable Long var1, RestJsonWrapperBean var2);
}
