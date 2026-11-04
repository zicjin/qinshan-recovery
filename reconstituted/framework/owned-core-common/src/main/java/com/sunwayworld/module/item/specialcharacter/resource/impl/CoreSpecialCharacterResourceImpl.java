/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.specialcharacter.bean.CoreSpecialCharacterBean;
import com.sunwayworld.module.item.specialcharacter.resource.CoreSpecialCharacterResource;
import com.sunwayworld.module.item.specialcharacter.service.CoreSpecialCharacterService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7279\u6b8a\u5b57\u7b26")
@RestController
@GikamBean
public class CoreSpecialCharacterResourceImpl
implements CoreSpecialCharacterResource {
    @Autowired
    private CoreSpecialCharacterService specialCharacterService;

    @Override
    public CoreSpecialCharacterService getService() {
        return this.specialCharacterService;
    }

    @Override
    @Log(value="\u6839\u636e\u7279\u6b8a\u5b57\u7b26ID\u66f4\u65b0\u5e38\u7528\u5b57\u7b26\u9009\u62e9\u6b21\u6570\u6216\u65b0\u589e\u5e38\u7528\u5b57\u7b26", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/common-characters"}, method={RequestMethod.PUT})
    public void updateOrInsertCommonCharacterById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().updateOrInsertCommonCharacterById(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u9009\u62e9\u6b21\u6570\u964d\u5e8f\u6392\u5e8f\u67e5\u8be2\u7279\u6b8a\u5b57\u7b26", type=LogType.SELECT)
    @RequestMapping(value={"/queries/choose-qty-desc"}, method={RequestMethod.POST})
    public Page<CoreSpecialCharacterBean> selectPagationByChooseQtyDesc(RestJsonWrapperBean wrapper) {
        return this.getService().selectPagationByChooseQtyDesc(wrapper);
    }

    @Override
    @Log(value="\u7279\u6b8a\u5b57\u7b26\u5206\u7c7b\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/categories/queries"}, method={RequestMethod.GET})
    public List<CoreCodeBean> selectCategoryList() {
        return this.getService().selectCategoryList();
    }
}
