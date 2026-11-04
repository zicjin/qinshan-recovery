/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.module.sys.metadata.bean.CoreParentTableInstanceDTO;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTableFieldDTO;
import com.sunwayworld.module.sys.metadata.bean.CoreTableHierarchyBean;
import com.sunwayworld.module.sys.metadata.bean.CoreTablePermissionBean;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface CoreTableService
extends GenericService<CoreTableBean, String>,
GenericChoosableService<CoreTableBean, String> {
    public List<CoreParentTableInstanceDTO> selectAncientTableInstanceList(String var1, List<? extends Serializable> var2);

    public <ID extends Serializable> void deleteCascade(String var1, ID var2);

    public <ID extends Serializable> void batchCascadeDelete(String var1, List<ID> var2);

    default public String selectMasterId(CoreTableHierarchyBean tableHierarchy, Serializable id) {
        Map<? extends Serializable, String> map = this.selectMasterMap(tableHierarchy, Arrays.asList(id));
        if (map.isEmpty()) {
            return null;
        }
        return map.values().iterator().next();
    }

    public Map<? extends Serializable, String> selectMasterMap(CoreTableHierarchyBean var1, List<? extends Serializable> var2);

    public List<CoreTableHierarchyBean> selectTableHierarchyByMaster(String var1);

    public CoreTableBean selectTable(String var1);

    public List<CoreTableHierarchyBean> selectTableHierarchy(String var1);

    public <ID> CoreTableHierarchyBean selectTableHierarchy(String var1, ID var2);

    public CoreTablePermissionBean selectTablePermission(String var1, String var2);

    public Page<ColumnContext> selectTableColumnList(String var1, RestJsonWrapperBean var2);

    public List<CoreTableFieldDTO> selectFieldListByTableName(String var1);
}
