/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.specialcharacter.bean.CoreSpecialCharacterBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import java.util.List;

public interface CoreSpecialCharacterService
extends GenericService<CoreSpecialCharacterBean, Long> {
    public void updateOrInsertCommonCharacterById(Long var1, RestJsonWrapperBean var2);

    public Page<CoreSpecialCharacterBean> selectPagationByChooseQtyDesc(RestJsonWrapperBean var1);

    public List<CoreCodeBean> selectCategoryList();
}
