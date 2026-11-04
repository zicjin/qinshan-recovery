/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreCodeGenerateConfigService
extends GenericService<CoreCodeGenerateConfigBean, String> {
    public String codeGenerate(String var1, RestJsonWrapperBean var2);

    public Long insertTable(String var1, RestJsonWrapperBean var2);

    public void deleteTable(RestJsonWrapperBean var1);

    public Page<CoreCodeGenerateConfigTableBean> selectTablePaginationByConfigId(String var1, RestJsonWrapperBean var2);

    public Long insertField(RestJsonWrapperBean var1);

    public void deleteField(RestJsonWrapperBean var1);

    public Page<CoreCodeGenerateConfigFieldBean> selectFieldPagination(RestJsonWrapperBean var1);

    public void swapField(Long var1, Long var2);

    public String copy(String var1);
}
