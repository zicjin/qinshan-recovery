/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.resource.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsFieldBean;
import com.sunwayworld.cloud.module.item.customds.resource.CoreCustomDsResource;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsFieldService;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u8868")
@RestController
@GikamBean
public class CoreCustomDsResourceImpl
implements CoreCustomDsResource,
AbstractGenericResource<CoreCustomDsService, CoreCustomDsBean, Long>,
AbstractGenericActivatableResource<CoreCustomDsService, CoreCustomDsBean, Long>,
AbstractGenericChoosableResource<CoreCustomDsService, CoreCustomDsBean, Long> {
    @Autowired
    private CoreCustomDsService coreCustomDsService;
    @Autowired
    private CoreCustomDsFieldService coreCustomDsFieldService;

    @Override
    public CoreCustomDsService getService() {
        return this.coreCustomDsService;
    }

    @Override
    @Log(value="\u6839\u636e\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u8868ID\u67e5\u8be2\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u5b57\u6bb5\u8868", type=LogType.SELECT)
    public Page<CoreCustomDsFieldBean> selectDsFieldPaginationByCustomDsId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.coreCustomDsFieldService.selectPaginationByFilter(SearchFilter.instance().match("DSID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u6570\u636e\u6e90\u5b57\u6bb5\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapField(Long firstId, Long secondId) {
        this.coreCustomDsFieldService.swap("ORDERNO", firstId, secondId);
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u5b57\u6bb5\u8868", type=LogType.INSERT)
    public Long insertDsField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Long)this.coreCustomDsFieldService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u5b57\u6bb5\u8868", type=LogType.DELETE)
    public void deleteDsField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6240\u6709\u5df2\u542f\u7528\u7684\u6570\u636e\u6e90", type=LogType.SELECT)
    public List<CoreCustomDsDTO> getActiveCustomDsList() {
        return this.getService().getActiveCustomDsList();
    }

    @Override
    @Log(value="\u6839\u636eid\u8fd4\u56de\u6570\u636e\u6e90sql\u6267\u884c\u7684\u7ed3\u679c", type=LogType.SELECT)
    public List<Map<String, Object>> selectDsResultData(Long dsId, RestJsonWrapperBean wrapper) {
        return this.getService().getCustomDsDataById(dsId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u6570\u636e\u6e90\u5217", type=LogType.INSERT)
    public void insertDsFields(Long dsId, RestJsonWrapperBean wrapper) {
        this.getService().insertDsFields(dsId, wrapper);
    }

    @Override
    @Log(value="\u9884\u89c8\u81ea\u5b9a\u4e49\u6570\u636e\u6e90SQL", type=LogType.SELECT)
    public List<Map<String, Object>> previewSql(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().previewSql(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u81ea\u5b9a\u4e49\u6570\u636e\u6e90SQL\u9884\u89c8\u5217\u96c6\u5408", type=LogType.SELECT)
    public List<String> getPreviewColumnList(Long id, RestJsonWrapperBean wrapper) {
        return this.getService().getPreviewColumnList(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6570\u636e\u6e90\u5b57\u6bb5\u503c\u96c6\u5408", type=LogType.SELECT)
    public List<Object> getColumnValues(Long dsId, RestJsonWrapperBean wrapper) {
        return this.getService().getColumnValues(dsId, wrapper);
    }
}
