/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.service;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplConfigBean;

public interface CoreImportTmplService
extends GenericService<CoreImportTmplBean, Long> {
    public Page<CoreImportTmplConfigBean> selectTmplConfigPaginationByImportTmplId(Long var1, RestJsonWrapperBean var2);

    public Long insertTmplConfig(Long var1, RestJsonWrapperBean var2);

    public void deleteTmplConfig(Long var1, RestJsonWrapperBean var2);

    public JSONObject importData(Long var1, Long var2);

    public void initTmplConfig(Long var1, RestJsonWrapperBean var2);
}
