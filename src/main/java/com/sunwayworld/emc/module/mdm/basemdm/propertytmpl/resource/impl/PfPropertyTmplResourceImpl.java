package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.resource.PfPropertyTmplResource;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyTmplService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月15日
 */
@LogModule(value = "属性设置")
@RestController
@GikamBean
public class PfPropertyTmplResourceImpl extends EmcPropertyTmplResourceImpl implements PfPropertyTmplResource {

    @Autowired
    private PfPropertyFieldService propertyFieldService;

    @Autowired
    private PfPropertyDataService propertyDataService;

    @Autowired
    private PfPropertyTmplService propertyTmplService;


    @Override
    public PfPropertyTmplService getService() {
        return propertyTmplService;
    }

    @Override
    @Log(value = "根据样品类型查询监测点属性字段", type = LogType.SELECT)
    @RequestMapping(value = "/monitor-tmpls/{monitorTmplId}/property-fields/queries", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorTmplId(@PathVariable String monitorTmplId) {
        return propertyFieldService.selectPropertyFieldByMonitorTmplId(monitorTmplId);
    }

    @Override
    @Log(value = "根据点位查询属性数据信息", type = LogType.SELECT)
    @RequestMapping(value = "/folders/{folderId}/property-data/queries", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByFolderId(@PathVariable Long folderId) {
        return propertyDataService.selectPropertyDataByFolderId(folderId);
    }

    @Log(value = "分析录入-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/result-order-task/{tmplId}/property-datas/{orderTaskId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByOrderTaskId(@PathVariable Long tmplId, @PathVariable Long orderTaskId) {
        return getService().selectPropertyDataByOrderTaskId(tmplId, orderTaskId);
    }

    @Log(value = "分析录入-保存属性数据信息", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/result-order-task/property-datas/{tmplId}/action/{orderTaskId}", method = RequestMethod.POST)
    public void savePropertyDataByOrderTaskId(@PathVariable Long tmplId, @PathVariable Long orderTaskId, RestJsonWrapperBean wrapper) {
         getService().savePropertyDataByOrderTaskId(tmplId, orderTaskId, wrapper);
    }

    @Log(value = "分析录入-保存属性数据信息", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/result-order-task/property-datas/action/batch-update/{tmplId}/flag/{flag1}", method = RequestMethod.POST)
    public void batchUpdatePropertyDataByOrderTaskId(@PathVariable Long tmplId,@PathVariable Long flag1, RestJsonWrapperBean wrapper) {
         getService().batchUpdatePropertyDataByOrderTaskId(tmplId,flag1, wrapper);
    }


    @Log(value = "分析录入-批量设置测试管理方法参数", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/test-method/property-datas/action/batch-update/{tmplId}", method = RequestMethod.POST)
    public Long batchUpdatePropertyDataByTestMethodId(@PathVariable Long tmplId, RestJsonWrapperBean wrapper) {
         return getService().batchUpdatePropertyDataByTestMethodId(tmplId, wrapper);
    }

    @Log(value = "监测能力库-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/test-capacity/{tmplId}/property-datas/{testCapacityId}/flag/{flag1}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByTestCapacityId(@PathVariable Long tmplId, @PathVariable Long testCapacityId,@PathVariable Long flag1) {
        return getService().selectPropertyDataByTestCapacityId(tmplId, testCapacityId,flag1);
    }

    @Log(value = "监测能力库-保存属性数据信息", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/property-datas/test-capacity/{tmplId}/action/{testCapacityId}/flag/{flag1}", method = RequestMethod.PUT)
    public void savePropertyDataByTestCapacityId(@PathVariable Long tmplId, @PathVariable Long testCapacityId,@PathVariable Long flag1,RestJsonWrapperBean wrapper) {
        getService().savePropertyDataByTestCapacityId(tmplId, testCapacityId,flag1, wrapper);
    }

    @Log(value = "查询监测能力库-方法参数-属性设置信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/test-capacity/{id}/property-fields", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectTestCapacityByTmplId(@PathVariable Long id) {
        return getService().selectPropertyFieldByTmplId(id);
    }

    @Override
    @Log(value = "监测能力库复制测试管理方法参数", type = LogType.INSERT)
    @RequestMapping(value = "/copy-test-method-param", method = RequestMethod.POST)
    public void copyTestMethodParam(RestJsonWrapperBean wrapper) {
    	 getService().copyTestMethodParam(wrapper);
    }

    @Log(value = "监测能力库-批量设置监测能力库方法参数", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/test-capacity/property-datas/action/batch-update/{tmplId}/flag/{flag1}", method = RequestMethod.POST)
    public Long batchUpdatePropertyDataByTestCapacityId(@PathVariable Long tmplId,@PathVariable Long flag1, RestJsonWrapperBean wrapper) {
         return getService().batchUpdatePropertyDataByTestCapacityId(tmplId,flag1,wrapper);
    }

    @Log(value = "分析录入-按样品批录入", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/result-order-task/property-datas/action/batch-update-order/{tmplId}/flag/{flag1}", method = RequestMethod.POST)
    public void batchUpdatePropertyDataByOrder(@PathVariable Long tmplId,@PathVariable Long flag1, RestJsonWrapperBean wrapper) {
         getService().batchUpdatePropertyDataByOrder(tmplId,flag1, wrapper);
    }

    @Override
    @Log(value = "分析录入-根据批号查询属性设置模板", type = LogType.SELECT)
    @RequestMapping(value = "/result-runs/property-fields/queries/{runId}", method = RequestMethod.GET)
    public Page<EmcPropertyFieldBean> selectPropertyFieldByRunId(@PathVariable String runId) {
        return getService().selectPropertyFieldByRunId(runId);
    }

    @Log(value = "分析录入-查询属性数据信息", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/result-runs/property-datas/{runId}", method = RequestMethod.GET)
    public EmcPropertyDataBean selectPropertyDataByRunId(@PathVariable String runId) {
        return getService().selectPropertyDataByRunId(runId);
    }

    @Log(value = "分析录入-保存属性数据信息", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/result-runs/property-datas/action/save/{runId}", method = RequestMethod.PUT)
    public void saveResultRunPropertyDataByRunId(@PathVariable String runId, RestJsonWrapperBean wrapper) {
        getService().saveResultRunPropertyDataByRunId(runId, wrapper);
    }

}
