package com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.EmcEquiptResource;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptUseRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "仪器管理")
@RestController
@GikamBean
public class EmcEquiptResourceImpl implements EmcEquiptResource {

    @Autowired
    private EmcEquiptService equiptService;

    @Autowired
    private EmcEquiptPlanService equiptPlanService;

    @Autowired
    EmcEquiptMaintService emcEquiptMaintService;

    @Autowired
    private EmcEquiptRecordService equiptRecordService;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    private EmcEquiptUseRecordService equiptUseRecordService;

    @Autowired
    private EmcEquiptPerCheckPlanService emcEquiptPerCheckPlanService;

    @Autowired
    private EmcEquiptPerCheckRecordService emcEquiptPerCheckRecordService;

    @Override
    public EmcEquiptService getService() {
        return equiptService;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    /** Keep the original HTTP contract while delegating to the owned service seam. */
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void save(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().save(id, wrapper);
    }

    /** The generic resource default is a read-only placeholder; owned equipment handles the delete. */
    @Override
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        getService().delete(wrapper);
    }

    @Override
    @Log(value = "插入仪器测试方法", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/test-methods", method = RequestMethod.POST)
    public Long insertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "测试方法复制到其他设备中", type = LogType.INSERT)
    @RequestMapping(value = "/test-methods/action/copyto", method = RequestMethod.POST)
    public void testMethodCopyTo( RestJsonWrapperBean wrapper) {
        equiptTestMethodService.testMethodCopyTo(wrapper);
        //return getService().insertTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "查询仪器测试方法", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/test-methods/queries", method = RequestMethod.POST)
    public Page<EmcEquiptTestMethodBean> selectEquiptTestMethodRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectEquiptTestMethodRawPagination(id, wrapper);
    }

    @Override
    @Log(value = "删除仪器测试方法", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/test-methods", method = RequestMethod.DELETE)
    public void deleteTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "插入仪器测试项目", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/test-items", method = RequestMethod.POST)
    public Long insertTestItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertTestItem(id, wrapper);
    }

    @Override
    @Log(value = "查询仪器测试项目", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/test-items/queries", method = RequestMethod.POST)
    public Page<EmcEquiptTestItemBean> selectEquiptTestItemRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectEquiptTestItemRawPagination(id, wrapper);
    }

    @Override
    @Log(value = "删除仪器测试项目", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/test-items", method = RequestMethod.DELETE)
    public void deleteTestItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteTestItem(id, wrapper);
    }

    @Log(value = "详情页查询DCU配置信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/DCU-confs/queries", method = RequestMethod.GET)
    @Override
    public EmcEquiptDCUConfBean selectSearchDCUConfDetailByEquipt(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectDCUConfDetail(id);
    }

    @Override
    @Log(value = "插入维护计划", type = LogType.INSERT)
    @RequestMapping(value = "/plans", method = RequestMethod.POST)
    public Long insertPlan(RestJsonWrapperBean wrapper) {
        return equiptPlanService.insert(wrapper);

    }

    @Override
    @Log(value = "插入维护记录", type = LogType.INSERT)
    @RequestMapping(value = "/records", method = RequestMethod.POST)
    public Long insertRecord(RestJsonWrapperBean wrapper) {
        return equiptRecordService.insert(wrapper);

    }

    @Log(value = "查询可选维护事件", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/maints/queries/selectable", method = RequestMethod.POST)
    @Override
    public List<EmcEquiptMaintBean> selectMaintSelectableList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return equiptPlanService.selectMaintSelectableList(id, wrapper);

    }

    @Log(value = "查询记录可选维护事件", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/records/maints/queries/selectable", method = RequestMethod.GET)
    @Override
    public List<EmcEquiptMaintBean> selectRecordMaintSelectableList(@PathVariable Long id) {
        return equiptRecordService.selectRecordMaintSelectableList(id);

    }

    @Log(value = "查询期间审核记录可选维护事件", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/per-check-records/maints/queries/selectable", method = RequestMethod.GET)
    @Override
    public List<EmcEquiptMaintBean> selectPerCheckRecordMaintSelectableList(@PathVariable Long id) {
        return emcEquiptPerCheckRecordService.selectRecordMaintSelectableList(id);

    }

    @Log(value = "完成维护记录", type = LogType.UPDATE)
    @RequestMapping(value = "/records/action/complete", method = RequestMethod.PUT)
    @Override
    public void complete(RestJsonWrapperBean wrapper) {
        equiptRecordService.complete(wrapper);
    }

    @Log(value = "完成维护记录", type = LogType.UPDATE)
    @RequestMapping(value = "/per-check-records/action/complete", method = RequestMethod.PUT)
    @Override
    public void perCheckRecordcomplete(RestJsonWrapperBean wrapper) {
        emcEquiptPerCheckRecordService.complete(wrapper);
    }

    @Override
    @Log(value = "查询维护计划", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/plans/queries", method = RequestMethod.POST)
    public Page<EmcEquiptPlanBean> selectEquiptPlanRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return equiptPlanService.selectRawPaginationByEquiptId(id, wrapper);
    }

    @Override
    @Log(value = "删除维护计划", type = LogType.DELETE)
    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    public void deletePlan( RestJsonWrapperBean wrapper) {
         equiptPlanService.delete(wrapper);;
    }

    @Override
    @Log(value = "查询维护记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/records/queries", method = RequestMethod.POST)
    public Page<EmcEquiptRecordBean> selectEquiptRecordRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return equiptRecordService.selectRawPaginationByEquiptId(id, wrapper);
    }

    @Override
    @Log(value = "删除维护记录", type = LogType.DELETE)
    @RequestMapping(value = "/records", method = RequestMethod.DELETE)
    public void deleteRecord( RestJsonWrapperBean wrapper) {
         equiptRecordService.delete( wrapper);
    }

    @Override
    @Log(value = "更新仪器使用记录", type = LogType.UPDATE)
    @RequestMapping(value = "/use-records", method = RequestMethod.PUT)
    public void updateUseRecord(RestJsonWrapperBean wrapper) {
        equiptUseRecordService.updateUseRecord(wrapper);
    }

    @Override
    @Log(value = "删除仪器使用记录", type = LogType.DELETE)
    @RequestMapping(value = "/delete-records", method = RequestMethod.DELETE)
    public void deleteUseRecord(RestJsonWrapperBean wrapper) {
        equiptUseRecordService.delete(wrapper);
    }

    @Override
    @Log(value = "查询使用记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/use-records/queries", method = RequestMethod.POST)
    public Page<EmcEquiptUseRecordBean> selectEquiptUseRecordRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return equiptUseRecordService.selectRawPaginationByEquiptIdOrSampleId(id, wrapper);
    }

    @Override
    @Log(value = "新增仪器使用记录", type = LogType.UPDATE)
    @RequestMapping(value = "/use-records", method = RequestMethod.POST)
    public void insertUseRecord(RestJsonWrapperBean wrapper) {
        equiptUseRecordService.insert(wrapper);
    }

    @Override
    @Log(value = "查询使用记录", type = LogType.SELECT)
    @RequestMapping(value = "/use-records/queries", method = RequestMethod.POST)
    public Page<EmcEquiptUseRecordBean> selectEquiptUseRecordPagination(RestJsonWrapperBean wrapper) {
        return equiptUseRecordService.selectEquiptUseRecordPagination(wrapper);
    }

    @Override
    @Log(value = "验证仪器DCU配置", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/action/validate-dcu-config", method = RequestMethod.POST)
    public RestValidationResultBean validateDcuConfig(@PathVariable Long id) {
        return getService().validateDcuConfig(id);
    }

    @Override
    @Log(value = "查询提醒天数", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/maint-warningdays/queries", method = RequestMethod.POST)
    public Long selectMainWaringDaysById(@PathVariable Long id) {
        return emcEquiptMaintService.selectById(id).getWarningDays();
    }

    @Override
    @Log(value = "查询仪器维护选择信息", type = LogType.SELECT)
    @RequestMapping(value = "/cali-queries/choosable", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectCaliChooseRawPagination(RestJsonWrapperBean wrapper) {
        return getService().selectCaliChoose(wrapper);
    }
    @Override
    @Log(value = "查询仪器维护选择信息", type = LogType.SELECT)
    @RequestMapping(value = "/verify-queries/choosable", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectVerifyChooseRawPagination(RestJsonWrapperBean wrapper) {
        return getService().selectVerifyChoose(wrapper);
    }
    @Override
    @Log(value = "查询仪器维护选择信息", type = LogType.SELECT)
    @RequestMapping(value = "/per-check-queries/choosable", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectPerCheckChooseRawPagination(RestJsonWrapperBean wrapper) {
        return getService().selectPerCheckChoose(wrapper);
    }

    @Override
    @Log(value = "插入期间核查计划", type = LogType.INSERT)
    @RequestMapping(value = "/per-check-plan", method = RequestMethod.POST)
    public Long insertPerCheckPlan(RestJsonWrapperBean wrapperBean) {
        return emcEquiptPerCheckPlanService.insert(wrapperBean);
    }

    @Override
    @Log(value = "删除期间核查计划", type = LogType.DELETE)
    @RequestMapping(value = "/per-check-plan", method = RequestMethod.DELETE)
    public void deletePerCheckPlan(RestJsonWrapperBean wrapper) {
        emcEquiptPerCheckPlanService.delete(wrapper);
    }

    @Override
    @Log(value = "查询期间核查计划", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/per-check-plan/queries", method = RequestMethod.POST)
    public Page<EmcEquiptPerCheckPlanBean> selectPerCheckPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return emcEquiptPerCheckPlanService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",id).filter(MatchPattern.EQ),wrapper);
    }

    @Override
    @Log(value = "插入期间核查记录", type = LogType.INSERT)
    @RequestMapping(value = "/per-check-record", method = RequestMethod.POST)
    public Long insertPerCheckRecord(RestJsonWrapperBean wrapper) {
        return emcEquiptPerCheckRecordService.insert(wrapper);
    }

    @Override
    @Log(value = "删除期间核查记录", type = LogType.DELETE)
    @RequestMapping(value = "/per-check-record", method = RequestMethod.DELETE)
    public void deletePerCheckRecord(RestJsonWrapperBean wrapper) {
        emcEquiptPerCheckRecordService.delete(wrapper);
    }

    @Override
    @Log(value = "查询期间核查记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/per-check-record/queries", method = RequestMethod.POST)
    public Page<EmcEquiptPerCheckRecordBean> selectPerCheckRecordPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return emcEquiptPerCheckRecordService.selectPaginationByFilter(SearchFilter.instance().match("equiptId",id).filter(MatchPattern.EQ),wrapper);
    }
}
