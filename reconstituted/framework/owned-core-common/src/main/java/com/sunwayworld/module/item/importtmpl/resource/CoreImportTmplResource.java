/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.resource;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplBean;
import com.sunwayworld.module.item.importtmpl.bean.CoreImportTmplConfigBean;
import com.sunwayworld.module.item.importtmpl.service.CoreImportTmplService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/import-tmpls"})
public interface CoreImportTmplResource
extends GenericResource<CoreImportTmplService, CoreImportTmplBean, Long> {
    public Page<CoreImportTmplConfigBean> selectTmplConfigPaginationByImportTmplId(Long var1, RestJsonWrapperBean var2);

    public void initTmplConfig(Long var1, RestJsonWrapperBean var2);

    public Long insertTmplConfig(Long var1, RestJsonWrapperBean var2);

    public void deleteTmplConfig(Long var1, RestJsonWrapperBean var2);

    public JSONObject importData(Long var1, Long var2);
}
