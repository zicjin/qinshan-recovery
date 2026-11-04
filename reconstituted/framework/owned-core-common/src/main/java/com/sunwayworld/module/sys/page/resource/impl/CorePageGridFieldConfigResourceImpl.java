/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import com.sunwayworld.module.sys.page.resource.CorePageGridFieldConfigResource;
import com.sunwayworld.module.sys.page.service.CorePageGridFieldConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="Grid\u5217\u914d\u7f6e")
public class CorePageGridFieldConfigResourceImpl
implements CorePageGridFieldConfigResource {
    @Autowired
    private CorePageGridFieldConfigService configService;

    public CorePageGridFieldConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u6307\u5b9aGrid\u7684\u5217\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(method={RequestMethod.GET})
    public List<CorePageGridFieldConfigBean> selectCurrentGridConfigList(@RequestParam String pageId, @RequestParam String gridId) {
        return this.getService().selectCurrentGridConfigList(pageId, gridId);
    }

    @Override
    @Log(value="\u66f4\u65b0Grid\u7684\u5217\u914d\u7f6e\u4fe1\u606f", type=LogType.UPDATE)
    @RequestMapping(method={RequestMethod.POST})
    public void insertOrUpdateCurrentGridConfig(RestJsonWrapperBean wrapper) {
        this.getService().insertOrUpdateCurrentGridConfig(wrapper);
    }
}
