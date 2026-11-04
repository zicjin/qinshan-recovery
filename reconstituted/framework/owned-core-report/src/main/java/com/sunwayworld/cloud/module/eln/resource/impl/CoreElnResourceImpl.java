/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.resource.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnAtRecordBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.resource.CoreElnResource;
import com.sunwayworld.cloud.module.eln.service.CoreElnAtRecordService;
import com.sunwayworld.cloud.module.eln.service.CoreElnService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="eln\u5b9e\u4f8b")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/eln/elns"})
public class CoreElnResourceImpl
implements CoreElnResource,
AbstractGenericResource<CoreElnService, CoreElnBean, Long> {
    @Autowired
    private CoreElnService coreElnService;
    @Autowired
    private CoreElnAtRecordService coreElnAtRecordService;

    @Override
    public CoreElnService getService() {
        return this.coreElnService;
    }

    @Override
    @Log(value="\u66f4\u65b0eln\u5b9e\u4f8b", type=LogType.UPDATE)
    public void updateEln(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().updateEln(id, wrapper);
    }

    @Override
    @Log(value="\u6253\u5f00eln\u5b9e\u4f8b", type=LogType.SELECT)
    public CoreElnDTO selectElnDTO(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return this.coreElnService.selectElnDTO(tmplId, null, wrapper);
    }

    @Override
    @Log(value="\u6253\u5f00eln\u5b9e\u4f8b", type=LogType.SELECT)
    public CoreElnDTO selectElnDTO(@PathVariable(required=false) Long id, @PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
        return this.coreElnService.selectElnDTO(tmplId, id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u5355\u5143\u683c\u67e5\u8be2\u4fee\u6539\u8bb0\u5f55", type=LogType.SELECT)
    public Pagination<CoreElnAtRecordBean> selectElnAtRecordByCol(@PathVariable Long id, @PathVariable String sheetNo, @PathVariable String colUuidCol, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreElnAtRecordService.selectPaginationByFilter(SearchFilter.instance().match("ELNID", id).filter(MatchPattern.SEQ).match("SHEETNO", sheetNo).filter(MatchPattern.SEQ).match("ROWUUIDCOL", colUuidCol).filter(MatchPattern.SEQ), wrapper);
    }
}
