/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.service;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreProcessViewConfService
extends GenericService<CoreProcessViewConfBean, Long>,
GenericActivatableService<CoreProcessViewConfBean, Long> {
    public Long insertMasterConf(RestJsonWrapperBean var1);

    public void deleteMasterConf(RestJsonWrapperBean var1);

    public Long insertSubConf(RestJsonWrapperBean var1);

    public void deleteSubConf(RestJsonWrapperBean var1);

    public Page<CoreProcessViewMasterConfBean> selectMasterConfPagationByPvConf(Long var1, RestJsonWrapperBean var2);

    public Page<CoreProcessViewSubConfBean> selectSubConfPagationByMasterConf(Long var1, RestJsonWrapperBean var2);

    public String selectGridSql(Long var1, String var2);

    public void insertGridConf(RestJsonWrapperBean var1);

    public Page<CoreProcessViewGridConfBean> selectGridConfPagination(RestJsonWrapperBean var1);

    public void activateGridConf(RestJsonWrapperBean var1);

    public void deactivateGridConf(RestJsonWrapperBean var1);

    public List<CoreProcessViewGridConfBean> selectGridConfColumnListByConf(Long var1, RestJsonWrapperBean var2);

    public CoreProcessViewMasterConfBean selectMasterConfDetail(Long var1);

    public CoreProcessViewSubConfBean selectSubConfDetail(Long var1);

    public CoreProcessViewConfDTO selectProcessView(RestJsonWrapperBean var1);

    public Page<Map<String, Object>> selectGridDataPagination(Long var1, String var2, RestJsonWrapperBean var3);

    public void swapMasterConf(String var1, Long var2, Long var3);

    public void swapSubConf(String var1, Long var2, Long var3);
}
