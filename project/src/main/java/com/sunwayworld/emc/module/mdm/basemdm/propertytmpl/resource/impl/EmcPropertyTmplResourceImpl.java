package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource.impl;

import java.util.List;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource.EmcPropertyTmplResource;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "属性设置")
@RestController
@GikamBean
public class EmcPropertyTmplResourceImpl implements EmcPropertyTmplResource {

    @Autowired
    private EmcPropertyTmplService propertyTmplService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Override
    public EmcPropertyTmplService getService() {
        return propertyTmplService;
    }

    @Log(value = "新增属性设置", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "新增属性数据", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/property-datas", method = RequestMethod.POST)
    public Long insertPropertyData(RestJsonWrapperBean wrapper) {
        return propertyDataService.insert(wrapper);
    }

    @Override
    @Log(value = "新增属性字段", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/fields", method = RequestMethod.POST)
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertField(id, wrapper);
    }

    @Override
    @Log(value = "属性字段查询", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/fields/queries", method = RequestMethod.POST)
    public Page<EmcPropertyFieldBean> selectFieldPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectFieldPaginationById(id, wrapper);
    }

    @Override
    @Log(value = "查询属性字段详情", type = LogType.SELECT)
    @RequestMapping(value = "/fields/{itemId}", method = RequestMethod.GET)
    public EmcPropertyFieldBean selectFieldById(@PathVariable Long itemId) {
        return getService().selectFieldById(itemId);
    }

    @Override
    @Log(value = "删除属性字段", type = LogType.DELETE)
    @RequestMapping(value = "/fields", method = RequestMethod.DELETE)
    public void deleteField(RestJsonWrapperBean wrapper) {
        getService().deleteField(wrapper);
    }

    @Log(value = "查询属性设置信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/tmpls/{id}/judge-samples/{monitorTmplId}", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectTmpls(@PathVariable Long id, @PathVariable String monitorTmplId) {
        return getService().selectTmpls(id, monitorTmplId);
    }

    @Log(value = "查询属性设置信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/tmpls/{id}/monitorPointId/{monitorPointId}/monitorPointCategoryId/{monitorPointCategoryId}", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectTmpls(@PathVariable Long id, @PathVariable Long monitorPointId, @PathVariable String monitorPointCategoryId) {
        return getService().selectTmpls(id, monitorPointId,monitorPointCategoryId);
    }

    @Log(value = "查询评价标准信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/index-sets/{id}", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectIndexSetByMonitorTmplId(@PathVariable Long id) {
        return getService().selectPropertyFieldByTmplId(id);
    }

    @Log(value = "现场采样-采样单-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/property-fields/{id}/queries/property-datas/{tmplId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyData(@PathVariable Long id, @PathVariable Long tmplId) {
        return getService().selectPropertyData(id, tmplId);
    }

    @Log(value = "查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/property-fields/{id}/queries/property-datas/{monitorPointId}/{monitorPointCategoryId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyData(@PathVariable Long id, @PathVariable Long monitorPointId, @PathVariable String monitorPointCategoryId) {
        return getService().selectPropertyData(id, monitorPointId,monitorPointCategoryId);
    }

    @Log(value = "监测点管理-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/monitor-points/queries/property-datas/{id}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByMonitorPointId(@PathVariable Long id) {
        return getService().selectPropertyDataByMonitorPointId(id);
    }

    @Override
    @Log(value = "根据监测点id查询属性设置模板", type = LogType.SELECT)
    @RequestMapping(value = "/property-fields/queries/{id}", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorPointId(@PathVariable Long id) {
        return getService().selectPropertyFieldByMonitorPointId(id);
    }

    @Log(value = "查询测试方法管理-方法参数-属性设置信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/test-methods/{id}/property-fields", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectTestMethodByTmplId(@PathVariable Long id) {
        return getService().selectPropertyFieldByTmplId(id);
    }

    @Log(value = "测试方法-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/test-methods/{tmplId}/property-datas/{testMethodId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByTestMethodId(@PathVariable Long tmplId, @PathVariable Long testMethodId) {
        return getService().selectPropertyDataByTestMethodId(tmplId, testMethodId);
    }

    @Log(value = "测试方法-保存属性数据信息", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/property-datas/{tmplId}/action/{testMethodId}", method = RequestMethod.PUT)
    public void savePropertyDataByTestMethodId(@PathVariable Long tmplId, @PathVariable Long testMethodId, RestJsonWrapperBean wrapper) {
        getService().savePropertyDataByTestMethodId(tmplId, testMethodId, wrapper);
    }

    @Log(value = "分析录入-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/result-runs/{tmplId}/property-datas/{runId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByRunId(@PathVariable Long tmplId, @PathVariable String runId) {
        return getService().selectPropertyDataByRunId(tmplId, runId);
    }

    @Log(value = "分析录入-保存属性数据信息", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/result-runs/property-datas/{tmplId}/action/{runId}", method = RequestMethod.PUT)
    public void saveResultRunPropertyDataByRunId(@PathVariable Long tmplId, @PathVariable String runId, RestJsonWrapperBean wrapper) {
        getService().saveResultRunPropertyDataByRunId(tmplId, runId, wrapper);
    }

    @Log(value = "指标设置-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/index-sets/{id}/{tmplId}/queries/property-datas", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByIndexId(@PathVariable Long id, @PathVariable Long tmplId) {
        return getService().selectPropertyData(id, tmplId);
    }

    @Override
    @Log(value = "批录更新属性数据", type = LogType.UPDATE)
    @RequestMapping(value = "/property-datas/action/batch-update", method = RequestMethod.POST)
    public void batchUpdatePropertyData(RestJsonWrapperBean wrapper) {
        getService().batchUpdatePropertyData(wrapper);
    }

    @Override
    @Log(value = "监测点类型查询字段", type = LogType.SELECT)
    @RequestMapping(value = "/property-fields/monitor-point-category/query/{tmplName}", method = RequestMethod.GET)
    public List<EmcPropertyFieldBean> selectFieldsByMonitorPointCategory(@PathVariable String tmplName) {
        return getService().selectFieldsByMonitorPointCategory(tmplName);
    }

    @Override
    @Log(value = "监测点类型修改默认值", type = LogType.UPDATE)
    @RequestMapping(value = "/property-fields/action/update-defaultvalue", method = RequestMethod.POST)
    public void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper) {
        getService().updatePropertyFieldByMonitorPointCategory(wrapper);
    }

    @Override
    @Log(value = "监测点管理修改属性值", type = LogType.UPDATE)
    @RequestMapping(value = "/property-datas/action/update-value", method = RequestMethod.POST)
    public void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper) {
        getService().updatePropertyDataByMonitorPoint(wrapper);
    }

    @Override
    @Log(value = "属性复制", type = LogType.INSERT)
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }
}
