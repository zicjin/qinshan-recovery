/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlDTO;
import com.sunwayworld.cloud.module.item.customds.resource.CoreCustomDsSqlResource;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsSqlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49SQL")
@RestController
@GikamBean
public class CoreCustomDsSqlResourceImpl
implements CoreCustomDsSqlResource {
    @Autowired
    private CoreCustomDsSqlService coreCustomDsSqlService;

    @Override
    @Log(value="\u67e5\u8be2SQL\u62d6\u62fd\u7ec4\u4ef6\u6570\u636e\u5e93\u8868\u6570\u636e", type=LogType.SELECT)
    public List<CoreCustomDsSqlDTO> selectAllList(RestJsonWrapperBean wrapper) {
        return this.coreCustomDsSqlService.selectAllList(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49SQL", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.coreCustomDsSqlService.insert(wrapper);
    }

    @Override
    @Log(value="\u6821\u9a8cSQL\u662f\u5426\u5408\u6cd5", type=LogType.VALIDATE)
    public RestValidationResultBean validateSql(RestJsonWrapperBean wrapper) {
        return this.coreCustomDsSqlService.validateSql(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u81ea\u5b9a\u4e49SQL", type=LogType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        this.coreCustomDsSqlService.delete(wrapper);
    }

    @Override
    @Log(value="\u4fee\u6539\u81ea\u5b9a\u4e49SQL", type=LogType.UPDATE)
    public void save(Long id, RestJsonWrapperBean wrapper) {
        this.coreCustomDsSqlService.save(id, wrapper);
    }

    @Override
    @Log(value="\u9884\u89c8\u81ea\u5b9a\u4e49SQL", type=LogType.SELECT)
    public List<Map<String, Object>> previewSql(Long id, RestJsonWrapperBean wrapper) {
        return this.coreCustomDsSqlService.previewSql(id, wrapper);
    }
}
