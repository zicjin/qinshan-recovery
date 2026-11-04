/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.fileacqconfig.resource.impl;

import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.bean.CoreFileAcqResultParamBean;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.resource.CoreFileAcqConfigResource;
import com.sunwayworld.cloud.module.equipt.fileacqconfig.service.CoreFileAcqConfigService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6587\u4ef6\u91c7\u96c6\u914d\u7f6e")
@RestController
@GikamBean
public class CoreFileAcqConfigResourceImpl
implements CoreFileAcqConfigResource,
AbstractGenericResource<CoreFileAcqConfigService, CoreFileAcqConfigBean, Long>,
AbstractGenericChoosableResource<CoreFileAcqConfigService, CoreFileAcqConfigBean, Long> {
    @Autowired
    private CoreFileAcqConfigService coreFileAcqConfigService;

    @Override
    public CoreFileAcqConfigService getService() {
        return this.coreFileAcqConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u91c7\u96c6\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u91c7\u96c6\u914d\u7f6eID\u67e5\u8be2\u6587\u4ef6\u91c7\u96c6\u53c2\u6570", type=LogType.SELECT)
    public Page<CoreFileAcqParamBean> selectAcqParamPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAcqParamPaginationByFileAcqConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u91c7\u96c6\u53c2\u6570", type=LogType.INSERT)
    public Long insertAcqParam(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAcqParam(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u91c7\u96c6\u53c2\u6570", type=LogType.DELETE)
    public void deleteAcqParam(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAcqParam(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u91c7\u96c6\u914d\u7f6eID\u67e5\u8be2\u6587\u4ef6\u91c7\u96c6\u9884\u5904\u7406", type=LogType.SELECT)
    public Page<CoreFileAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAcqPreprocessPaginationByFileAcqConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u91c7\u96c6\u9884\u5904\u7406", type=LogType.INSERT)
    public Long insertAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAcqPreprocess(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u91c7\u96c6\u9884\u5904\u7406", type=LogType.DELETE)
    public void deleteAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAcqPreprocess(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6\u91c7\u96c6\u914d\u7f6eID\u67e5\u8be2\u6587\u4ef6\u91c7\u96c6\u7ed3\u679c\u53c2\u6570", type=LogType.SELECT)
    public Page<CoreFileAcqResultParamBean> selectAcqResultParamPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAcqResultParamPaginationByFileAcqConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6587\u4ef6\u91c7\u96c6\u7ed3\u679c\u53c2\u6570", type=LogType.INSERT)
    public Long insertAcqResultParam(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAcqResultParam(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6587\u4ef6\u91c7\u96c6\u7ed3\u679c\u53c2\u6570", type=LogType.DELETE)
    public void deleteAcqResultParam(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAcqResultParam(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6587\u4ef6ID\u67e5\u8be2\u6587\u4ef6\u5185\u5bb9", type=LogType.SELECT)
    public String selectFileContentByFileId(Long id, Long fileId) {
        return this.getService().selectFileContentByFileId(id, fileId);
    }

    @Override
    @Log(value="\u9884\u5904\u7406", type=LogType.PROCESS)
    public String preprocess(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().preprocess(id);
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u89e3\u6790", type=LogType.PROCESS)
    public Page<CoreFileAcqResultBean> test(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().test(id, wrapper);
    }

    @Override
    @Log(value="\u590d\u5236", type=LogType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        this.getService().copy(wrapper);
    }
}
