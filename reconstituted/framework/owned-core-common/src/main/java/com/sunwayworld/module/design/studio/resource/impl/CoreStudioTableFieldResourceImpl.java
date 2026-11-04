/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import com.sunwayworld.module.design.studio.resource.CoreStudioTableFieldResource;
import com.sunwayworld.module.design.studio.service.CoreStudioTableFieldService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreStudioTableFieldResourceImpl
implements CoreStudioTableFieldResource {
    @Autowired
    private CoreStudioTableFieldService tableFieldService;

    @Override
    public CoreStudioTableFieldService getService() {
        return this.tableFieldService;
    }

    @Override
    @Log(value="\u8868\u4e2d\u5217\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    public List<CoreStudioTableFieldBean> selectListByTable(@RequestParam(value="tableName") String tableName) {
        return this.getService().selectListByTable(tableName);
    }
}
