/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.resource;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/module/item/page-fields"})
public interface CorePageFieldResource
extends GenericCloudResource<CorePageFieldBean, Long>,
GenericChoosableCloudResource<CorePageFieldBean, Long> {
    @RequestMapping(value={"/{pfId}/comps"}, method={RequestMethod.POST})
    public Long insertComp(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{pfId}/comps"}, method={RequestMethod.DELETE})
    public void deleteComp(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{pfId}/comps/queries"}, method={RequestMethod.POST})
    public Pagination<CorePageFieldCompBean> selectCompPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/comps/{pfcId}/fields"}, method={RequestMethod.POST})
    public Long insertCompField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/comps/{pfcId}/fields"}, method={RequestMethod.DELETE})
    public void deleteCompField(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/comps/{pfcId}/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CorePageFieldCompFieldBean> selectCompFieldPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/comps/{pfcId}/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapCompField(@PathVariable Long var1, @PathVariable Long var2, @PathVariable Long var3);

    @RequestMapping(value={"/{pfId}/comp-fields/queries"}, method={RequestMethod.GET})
    public ArrayList<CorePageFieldDTO> selectCompFieldList(@PathVariable Long var1);
}
