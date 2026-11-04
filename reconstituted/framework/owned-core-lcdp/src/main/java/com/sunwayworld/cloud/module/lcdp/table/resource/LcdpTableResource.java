/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.resource;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpDynamicPagination;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableViewDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/tables"})
public interface LcdpTableResource
extends GenericCloudResource<LcdpTableBean, Long>,
GenericChoosableCloudResource<LcdpTableBean, Long> {
    @RequestMapping(value={"/tree"}, method={RequestMethod.POST})
    public List<LcdpTableTreeNodeDTO> selectTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/physical"}, method={RequestMethod.POST})
    public List<LcdpTableBean> selectPhysicalTableNameList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/virtual"}, method={RequestMethod.POST})
    public List<LcdpTableBean> selectVirtualTableNameList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{tableName}/info/physical"}, method={RequestMethod.GET})
    public LcdpTableDTO selectPhysicalTableInfo(String var1);

    @RequestMapping(value={"/{id}/info"}, method={RequestMethod.GET})
    public LcdpTableDTO selectTableInfo(Long var1);

    @RequestMapping(value={"/fields/queries/physical"}, method={RequestMethod.POST})
    public List<LcdpTableFieldBean> selectPhysicalFieldList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/fields/queries/selectable"}, method={RequestMethod.GET})
    public List<LcdpTableFieldBean> selectPhysicalFieldSelectableList();

    @RequestMapping(value={"/{tableName}/action/design"}, method={RequestMethod.POST})
    public Long design(String var1);

    @RequestMapping(value={"/{tableName}/action/checkout"}, method={RequestMethod.POST})
    public void checkout(String var1);

    @RequestMapping(value={"/action/compare"}, method={RequestMethod.POST})
    public LcdpTableCompareDTO<LcdpTableBean> compare(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/validate-table-unique/{tableName}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateTableNameUnique(String var1);

    @RequestMapping(value={"/action/validate-index-unique/{indexName}"}, method={RequestMethod.POST})
    public RestValidationResultBean validateIndexNameUnique(String var1);

    @RequestMapping(value={"/queries/table-view/physical"}, method={RequestMethod.POST})
    public List<LcdpTableViewDTO> selectTableViewList(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/execute-sql"}, method={RequestMethod.POST})
    public LcdpTableDTO executeSql(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/dynamic"}, method={RequestMethod.POST})
    public LcdpDynamicPagination<Map<String, Object>> dynamicQuery(RestJsonWrapperBean var1);
}
