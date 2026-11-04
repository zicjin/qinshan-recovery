/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.resource;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/process-view-confs"})
public interface CoreProcessViewConfResource
extends GenericCloudResource<CoreProcessViewConfBean, Long>,
GenericActivatableCloudResource<CoreProcessViewConfBean, Long> {
    @RequestMapping(value={"/{id}/master-confs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreProcessViewMasterConfBean> selectMasterConfPagationByPvConf(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/master-confs/{masterConfId}/sub-confs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreProcessViewSubConfBean> selectSubConfPagationByMasterConf(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/master-confs"}, method={RequestMethod.POST})
    public Long insertMasterConf(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/master-confs"}, method={RequestMethod.DELETE})
    public void deleteMasterConf(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/sub-confs"}, method={RequestMethod.POST})
    public Long insertSubConf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/sub-confs"}, method={RequestMethod.DELETE})
    public void deleteSubConf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/confs/{sourceId}/source-types/{sourceType}/grid-sql"}, method={RequestMethod.GET})
    public String selectGridSql(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/grid-confs"}, method={RequestMethod.POST})
    public void insertGridConf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/grid-confs/queries"}, method={RequestMethod.POST})
    public Pagination<CoreProcessViewGridConfBean> selectGridConfPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/grid-confs/action/activate"}, method={RequestMethod.PUT})
    public void activateGridConf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/grid-confs/action/deactivate"}, method={RequestMethod.PUT})
    public void deactivateGridConf(RestJsonWrapperBean var1);

    @RequestMapping(value={"/confs/{sourceId}/grid-confs"}, method={RequestMethod.GET})
    public ArrayList<CoreProcessViewGridConfBean> selectGridConfColumnListByConf(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/master-confs/{masterConfId}"}, method={RequestMethod.GET})
    public CoreProcessViewMasterConfBean selectMasterConfDetail(@PathVariable Long var1);

    @RequestMapping(value={"/sub-confs/{subConfId}"}, method={RequestMethod.GET})
    public CoreProcessViewSubConfBean selectSubConfDetail(@PathVariable Long var1);

    @RequestMapping(value={"/process-view"}, method={RequestMethod.POST})
    public CoreProcessViewConfDTO selectProcessView(RestJsonWrapperBean var1);

    @RequestMapping(value={"/confs/{sourceId}/source-types/{sourceType}/grid-datas/queries"}, method={RequestMethod.POST})
    public Pagination<Map<String, Object>> selectGridConfDataPagination(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/master-confs/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapMasterConf(@PathVariable Long var1, @PathVariable Long var2);

    @RequestMapping(value={"/sub-confs/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapSubConf(@PathVariable Long var1, @PathVariable Long var2);
}
