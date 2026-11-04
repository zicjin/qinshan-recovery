/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.resource.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineTreeDTO;
import com.sunwayworld.cloud.module.fr.report.resource.CoreReportConfigResource;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62a5\u8868\u914d\u7f6e")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/report-configs"})
public class CoreReportConfigResourceImpl
implements CoreReportConfigResource,
AbstractGenericResource<CoreReportConfigService, CoreReportConfigBean, String>,
AbstractGenericChoosableResource<CoreReportConfigService, CoreReportConfigBean, String> {
    @Autowired
    private CoreReportConfigService coreReportConfigService;
    @Autowired
    private CoreReportConfigLineService coreReportConfigLineService;

    @Override
    public CoreReportConfigService getService() {
        return this.coreReportConfigService;
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u8868\u914d\u7f6e", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u62a5\u8868\u914d\u7f6e", type=LogType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u8868\u914d\u7f6e\u660e\u7ec6", type=LogType.INSERT)
    public Long insertLine(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.coreReportConfigLineService.insert(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u62a5\u8868\u914d\u7f6e\u660e\u7ec6", type=LogType.DELETE)
    public void deleteLines(RestJsonWrapperBean wrapper) {
        this.coreReportConfigLineService.delete(wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    public void swapLine(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.coreReportConfigLineService.swap("ORDERNO", firstId, secondId);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    public RestValidationResultBean validateLineUnique(Long id) {
        return this.coreReportConfigLineService.validateUnique(id, null, null);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    public RestValidationResultBean validateLineUnique(Long id, String columnName, String columnValue) {
        return this.coreReportConfigLineService.validateUnique(id, columnName, columnValue);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u6570\u636e\u552f\u4e00\u6027", type=LogType.VALIDATE)
    public RestValidationResultBean validateLineUnique(String columnName, String columnValue) {
        return this.coreReportConfigLineService.validateUnique(null, columnName, columnValue);
    }

    @Log(value="\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/lines/{lineId}"}, method={RequestMethod.GET})
    public CoreReportConfigLineBean selectLineDetail(@PathVariable Long lineId) {
        return (CoreReportConfigLineBean)this.coreReportConfigLineService.selectDetail(lineId);
    }

    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u4e0b\u62c9\u5217\u8868\u67e5\u8be2", type=LogType.SELECT)
    public ArrayList<CoreReportConfigLineBean> selectSelectableList() {
        return (ArrayList)this.coreReportConfigLineService.selectSelectableList();
    }

    @Override
    @Log(value="\u542f\u7528\u62a5\u8868\u914d\u7f6e\u660e\u7ec6", type=LogType.ACTIVATE)
    public void activate(RestJsonWrapperBean wrapper) {
        this.coreReportConfigLineService.activate(wrapper);
    }

    @Override
    @Log(value="\u505c\u7528\u62a5\u8868\u914d\u7f6e\u660e\u7ec6", type=LogType.ACTIVATE)
    public void deactivate(RestJsonWrapperBean wrapper) {
        this.coreReportConfigLineService.deactivate(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u4e3b\u8868\u67e5\u8be2\u660e\u7ec6", type=LogType.SELECT)
    public Pagination<CoreReportConfigLineBean> selectLinePaginationByMasterId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreReportConfigLineService.selectPaginationByFilter(SearchFilter.instance().match("REPORTCONFIGID", id).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u9009\u62e9\u5217\u8868\u67e5\u8be2", type=LogType.SELECT)
    public Pagination<CoreReportConfigLineBean> selectLineChoosablePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreReportConfigLineService.selectChoosablePagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u62a5\u8868\u660e\u7ec6\u6811", type=LogType.SELECT)
    public ArrayList<CoreReportConfigLineTreeDTO> selectLineTree(RestJsonWrapperBean jsonWrapper) {
        return (ArrayList)this.coreReportConfigLineService.selectLineTree(jsonWrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u62a5\u8868\u660e\u7ec6", type=LogType.SELECT)
    public Pagination<CoreReportConfigLineBean> selectLinePagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreReportConfigLineService.selectPagination(wrapper);
    }

    @Override
    public ArrayList<CoreReportConfigLineBean> selectListByIds(List<Long> idList) {
        return (ArrayList)this.coreReportConfigLineService.selectListByIds(idList);
    }

    @Override
    public String getLineTable() {
        return this.coreReportConfigLineService.getDao().getTable();
    }
}
