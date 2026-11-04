/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.resource.impl;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.resource.CorePdfAcqConfigResource;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqConfigService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="PDF\u91c7\u96c6\u914d\u7f6e")
@RestController
@GikamBean
public class CorePdfAcqConfigResourceImpl
implements CorePdfAcqConfigResource,
AbstractGenericResource<CorePdfAcqConfigService, CorePdfAcqConfigBean, Long>,
AbstractGenericChoosableResource<CorePdfAcqConfigService, CorePdfAcqConfigBean, Long>,
AbstractGenericSelectableResource<CorePdfAcqConfigService, CorePdfAcqConfigBean, Long> {
    @Autowired
    private CorePdfAcqConfigService pdfAcqConfigService;

    @Override
    public CorePdfAcqConfigService getService() {
        return this.pdfAcqConfigService;
    }

    @Override
    @Log(value="\u65b0\u589ePDF\u91c7\u96c6\u914d\u7f6e", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589ePDF\u91c7\u96c6\u914d\u7f6e\u660e\u7ec6", type=LogType.INSERT)
    public Long insertLine(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertLine(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664PDF\u91c7\u96c6\u914d\u7f6e\u660e\u7ec6", type=LogType.DELETE)
    public void deleteLine(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteLine(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636ePDF\u91c7\u96c6\u914d\u7f6eID\u67e5\u8be2\u660e\u7ec6", type=LogType.SELECT)
    public Page<CorePdfPageDTO> selectLinePaginationById(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectLinePaginationById(id, wrapper);
    }

    @Override
    public Page<CorePdfAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectAcqPreprocessPaginationByFileAcqConfigId(id, wrapper);
    }

    @Override
    @Log(value="PDF\u91c7\u96c6\u914d\u7f6e\u6d4b\u8bd5", type=LogType.SELECT)
    public List<CorePdfAcqResultDTO> test(Long id, Long fileId) {
        return this.getService().parse(id, fileId);
    }

    @Override
    public Long insertAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertAcqPreprocess(id, wrapper);
    }

    @Override
    public void deleteAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteAcqPreprocess(id, wrapper);
    }
}
