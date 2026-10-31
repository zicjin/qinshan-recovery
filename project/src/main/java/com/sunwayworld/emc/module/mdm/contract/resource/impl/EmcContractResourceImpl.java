package com.sunwayworld.emc.module.mdm.contract.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.emc.module.mdm.contract.resource.EmcContractResource;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("合同表")
@RestController
@GikamBean
public class EmcContractResourceImpl implements EmcContractResource {

    @Autowired
    private EmcContractService emcContractService;

    @Override
    public EmcContractService getService() {
        return emcContractService;
    }

    @Log(value = "新增合同表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询合同提要", type = LogType.SELECT)
    @RequestMapping(value = "/{contractId}/review-contents", method = RequestMethod.POST)
    public Page<EmcReviewContentBean> selectReviewContentPaginationByContractId(@PathVariable Long contractId, RestJsonWrapperBean wrapper) {
        return getService().selectReviewContentPaginationByContractId(contractId, wrapper);
    }

    @Override
    @Log(value = "添加费用类别", type = LogType.INSERT)
    @RequestMapping(value = "/{contractId}/expense-categories", method = RequestMethod.POST)
    public void insertExpenseCategory(@PathVariable Long contractId, RestJsonWrapperBean wrapper) {
        getService().insertExpenseCategory(contractId, wrapper);
    }

    @Override
    @Log(value = "删除费用类别", type = LogType.DELETE)
    @RequestMapping(value = "/expense-categories", method = RequestMethod.DELETE)
    public void deleteExpenseCategory(RestJsonWrapperBean wrapper) {
        getService().deleteExpenseCategory(wrapper);
    }

    @Override
    @Log(value = "查询费用类型", type = LogType.SELECT)
    @RequestMapping(value = "/{contractId}/expense-categories/queries", method = RequestMethod.POST)
    public Page<EmcExpenseCategoryBean> selectExpenseCategoryPaginationByContractId(@PathVariable Long contractId, RestJsonWrapperBean wrapper) {
        return getService().selectExpenseCategoryPaginationByContractId(contractId, wrapper);
    }

    @Override
    @Log(value = "添加监测项目", type = LogType.INSERT)
    @RequestMapping(value = "/monitor-items", method = RequestMethod.POST)
    public void insertMonitorItem(RestJsonWrapperBean wrapper) {
        getService().insertMonitorItem(wrapper);
    }

    @Override
    @Log(value = "删除监测项目", type = LogType.DELETE)
    @RequestMapping(value = "/monitor-items", method = RequestMethod.DELETE)
    public void deleteMonitorItem(RestJsonWrapperBean wrapper) {
        getService().deleteMonitorItem(wrapper);
    }

    @Override
    @Log(value = "查询监测项目", type = LogType.SELECT)
    @RequestMapping(value = "/{contractId}/expense-categories/{expenseCategoryId}/monitor-items/queries", method = RequestMethod.POST)
    public Page<EmcMonitorItemBean> selectMonitorItemPagination(@PathVariable Long contractId, @PathVariable Long expenseCategoryId, RestJsonWrapperBean wrapper) {
        return getService().selectMonitorItemPagination(contractId, expenseCategoryId, wrapper);
    }

    @Override
    @Log(value = "复制费用预算", type = LogType.INSERT)
    @RequestMapping(value = "/expense-categories/action/copy", method = RequestMethod.POST)
    public void copyExpenseCategory(RestJsonWrapperBean wrapper) {
        getService().copyExpenseCategory(wrapper);
    }

    @Override
    @Log(value = "复制合同", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copyContract(RestJsonWrapperBean wrapper) {
        getService().copyContract(wrapper);
    }

    @Override
    @Log(value = "计算总价", type = LogType.UPDATE)
    @RequestMapping(value = "/{id}/action/calculate", method = RequestMethod.POST)
    public void calculateTotalPrice(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().calculateTotalPrice(id, wrapper);
    }

    @Override
    @Log(value = "查询合同评审基本信息", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/review-basic-info", method = RequestMethod.GET)
    public EmcReviewBasicInfoBean selectReviewBasicInfo(@PathVariable Long id) {
        return getService().selectReviewBasicInfo(id);
    }
    
    @Override
    @Log(value = "查询检测项目", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/monitor-items/queries", method = RequestMethod.POST)
    public Page<EmcMonitorItemBean> selectMonitorItemPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectMonitorItemPagination(id, wrapper);
    }
}
