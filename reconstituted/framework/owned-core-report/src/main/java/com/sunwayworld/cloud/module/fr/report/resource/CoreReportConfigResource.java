/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.resource;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineTreeDTO;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreReportConfigResource
extends GenericCloudResource<CoreReportConfigBean, String>,
GenericChoosableCloudResource<CoreReportConfigBean, String> {
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.POST})
    public Long insertLine(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/lines"}, method={RequestMethod.DELETE})
    public void deleteLines(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/queries/selectable"}, method={RequestMethod.GET})
    public ArrayList<CoreReportConfigLineBean> selectSelectableList();

    @RequestMapping(value={"/{id}/lines/queries"}, method={RequestMethod.POST})
    public Pagination<CoreReportConfigLineBean> selectLinePaginationByMasterId(@PathVariable String var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/lines/action/activate"}, method={RequestMethod.PUT})
    public void activate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/action/deactivate"}, method={RequestMethod.PUT})
    public void deactivate(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/queries/choosable"}, method={RequestMethod.POST})
    public Pagination<CoreReportConfigLineBean> selectLineChoosablePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/tree"}, method={RequestMethod.POST})
    public ArrayList<CoreReportConfigLineTreeDTO> selectLineTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/queries"}, method={RequestMethod.POST})
    public Pagination<CoreReportConfigLineBean> selectLinePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/lines/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapLine(@PathVariable Long var1, @PathVariable Long var2);

    @PostMapping(value={"/lines/{id}/action/validate-unique"})
    public RestValidationResultBean validateLineUnique(@PathVariable Long var1);

    @PostMapping(value={"/lines/{id}/action/validate-unique/{columnName}/{columnValue}"})
    public RestValidationResultBean validateLineUnique(@PathVariable Long var1, @PathVariable String var2, @PathVariable String var3);

    @PostMapping(value={"/lines/action/validate-unique/{columnName}/{columnValue}"})
    public RestValidationResultBean validateLineUnique(@PathVariable String var1, @PathVariable String var2);

    @PostMapping(value={"/lines/select-list-by-ids"})
    public ArrayList<CoreReportConfigLineBean> selectListByIds(@RequestBody List<Long> var1);

    @PostMapping(value={"/lines/select-table"})
    public String getLineTable();
}
