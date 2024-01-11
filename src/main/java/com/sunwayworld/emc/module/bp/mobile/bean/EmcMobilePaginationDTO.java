package com.sunwayworld.emc.module.bp.mobile.bean;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodEquiptBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasPreAnalyBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEquiptAcqScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月09日
 */
public class EmcMobilePaginationDTO {

    private String message;
    /** 业务数据 */
    private Page<EmcProjectBean> projectPage; // 项目列表
    private Page<EmcFolderBean> folderPage; // 点位列表
    private Page<EmcSampleBean> samplePage; // 采样单列表
    private Page<EmcOrderBean> orderPage; // 样品列表
    private Page<EmcOrderContainerBean> orderContainerPage; // 样品容器列表
    private Page<EmcOrderTaskBean> orderTaskPage; // 样品测试列表
    private Page<EmcOrderTaskResultBean> orderTaskResultPage; // 样品测试结果列表
    private Page<EmcPropertyDataBean> propertyDataPage; // 属性值列表
    private Page<CoreFileBean> projectFilePage; // 项目附件
    private Page<EmcEquiptCalibrationBean> calibrationPage; // 仪器校准
    private Page<EmcEquiptCalibrationLineBean> calibrationLinePage; // 仪器校准记录
    private Page<EmcOrderItemGroupBean> orderItemGroupPage; // 样品分析项组
    /** 静态数据 */
    private Page<String> operateTypePage; // 操作类型
    private Page<EmcEquiptBean> equiptPage; // 仪器列表
    private Page<EmcEmpBean> empPage; // 人员列表
    private Page<EmcTestMethodEquiptBean> testMethodEquiptPage; // 测试关联仪器列表
    private Page<EmcEquiptTestMethodBean> equiptTestMethodPage;
    private Page<EmcPropertyFieldBean> propertyFieldPage; // 属性字段列表
    private Page<EmcMonitorTmplBean> monitorTmplPage; // 监测模板列表
    private Page<EmcItemResultBean> itemResultPage; // 测试分析项列表
    private Page<CoreCodeBean> coreCodePage; // 系统编码列表
    private Page<EmcMonitorPointBean> monitorPointPage; // 监测点列表
    private Page<EmcClientBean> clientPage; // 客户列表
    private Page<EmcMonitorTmplGroupBean> monitorTmplGroupPage; // 监测模板分组
    private Page<EmcMonitorSchemeBean> monitorSchemePage; // 监测方案
    private Page<EmcTestCategoryBean> testCategoryPage; // 测试分类
    private Page<EmcTestBean> testPage; // 测试库
    private Page<EmcTestMethodBean> testMethodPage; // 测试方法
    private Page<EmcMethodBean> methodPage; // 方法库
    private Page<EmcItemBean> itemPage; // 分析项
    private Page<EmcItemGroupBean> itemGroupPage; // 分析项组
    private Page<EmcGroupItemBean> groupItemPage; // 分析项组分析项
    private Page<EmcContainerCategoryBean> containerPage; // 容器组
    private Page<EmcEquiptCategoryBean> equiptCategoryPage; // 仪器类型
    private Page<EmcUnitBean> unitPage; // 单位
    private Page<EmcQcCategoryBean> qcCategoryPage; // QC类型
    private Page<EmcMonitorPointCategoryBean> monitorPointCategoryPage; // 监测点类型
    private Page<EmcTestMethodOrderCategoryBean> testOrderCategoryPage; // 测试样品类型
    private Page<EmcTestCapacityBean> testCapacityPage; // 监测能力库
    /** 仪器采集 */
    private Page<EmcEquiptAcqScriptBean> acqScriptPage; // 仪器采集脚本
    private Page<EmcEasParamBean> easParamPage; // 采集参数
    private Page<EmcEasPreAnalyBean> easPreAnalyPage; // 预处理
    private Page<EmcEasResultBean> easResultPage; // 结果参数
    private Page<EmcEquiptTestItemBean> equiptTestItemPage; // 采集分析项对照表

    private Page<EmcFolderBean> sampleParamFolderPage; // 气的采样参数点位列表

    private Page<EmcOrderBean> sampleParamOrderPage; // 气的采样参数样品列表

    private Page<EmcOrderTaskBean> sampleParamOrderTaskPage; // 气的采样参数样品测试列表

    private Page<EmcOrderTaskResultBean> sampleParamOrderTaskResultPage; // 气的采样参数样品测试结果列表

    private Page<EmcOrderItemGroupBean> sampleParamOrderItemGroupPage; // 气的采样参数样品分析项组

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page<EmcProjectBean> getProjectPage() {
        return projectPage;
    }

    public void setProjectPage(Page<EmcProjectBean> projectPage) {
        this.projectPage = projectPage;
    }

    public Page<EmcFolderBean> getFolderPage() {
        return folderPage;
    }

    public void setFolderPage(Page<EmcFolderBean> folderPage) {
        this.folderPage = folderPage;
    }

    public Page<EmcSampleBean> getSamplePage() {
        return samplePage;
    }

    public void setSamplePage(Page<EmcSampleBean> samplePage) {
        this.samplePage = samplePage;
    }

    public Page<EmcOrderBean> getOrderPage() {
        return orderPage;
    }

    public void setOrderPage(Page<EmcOrderBean> orderPage) {
        this.orderPage = orderPage;
    }

    public Page<EmcOrderContainerBean> getOrderContainerPage() {
        return orderContainerPage;
    }

    public void setOrderContainerPage(Page<EmcOrderContainerBean> orderContainerPage) {
        this.orderContainerPage = orderContainerPage;
    }

    public Page<EmcOrderTaskBean> getOrderTaskPage() {
        return orderTaskPage;
    }

    public void setOrderTaskPage(Page<EmcOrderTaskBean> orderTaskPage) {
        this.orderTaskPage = orderTaskPage;
    }

    public Page<EmcOrderTaskResultBean> getOrderTaskResultPage() {
        return orderTaskResultPage;
    }

    public void setOrderTaskResultPage(Page<EmcOrderTaskResultBean> orderTaskResultPage) {
        this.orderTaskResultPage = orderTaskResultPage;
    }

    public Page<EmcPropertyDataBean> getPropertyDataPage() {
        return propertyDataPage;
    }

    public void setPropertyDataPage(Page<EmcPropertyDataBean> propertyDataPage) {
        this.propertyDataPage = propertyDataPage;
    }

    public Page<CoreFileBean> getProjectFilePage() {
        return projectFilePage;
    }

    public void setProjectFilePage(Page<CoreFileBean> projectFilePage) {
        this.projectFilePage = projectFilePage;
    }

    public Page<EmcEquiptCalibrationBean> getCalibrationPage() {
        return calibrationPage;
    }

    public void setCalibrationPage(Page<EmcEquiptCalibrationBean> calibrationPage) {
        this.calibrationPage = calibrationPage;
    }

    public Page<EmcEquiptCalibrationLineBean> getCalibrationLinePage() {
        return calibrationLinePage;
    }

    public void setCalibrationLinePage(Page<EmcEquiptCalibrationLineBean> calibrationLinePage) {
        this.calibrationLinePage = calibrationLinePage;
    }

    public Page<EmcOrderItemGroupBean> getOrderItemGroupPage() {
        return orderItemGroupPage;
    }

    public void setOrderItemGroupPage(Page<EmcOrderItemGroupBean> orderItemGroupPage) {
        this.orderItemGroupPage = orderItemGroupPage;
    }

    public Page<String> getOperateTypePage() {
        return operateTypePage;
    }

    public void setOperateTypePage(Page<String> operateTypePage) {
        this.operateTypePage = operateTypePage;
    }

    public Page<EmcEquiptBean> getEquiptPage() {
        return equiptPage;
    }

    public void setEquiptPage(Page<EmcEquiptBean> equiptPage) {
        this.equiptPage = equiptPage;
    }

    public Page<EmcEmpBean> getEmpPage() {
        return empPage;
    }

    public void setEmpPage(Page<EmcEmpBean> empPage) {
        this.empPage = empPage;
    }

    public Page<EmcTestMethodEquiptBean> getTestMethodEquiptPage() {
        return testMethodEquiptPage;
    }

    public void setTestMethodEquiptPage(Page<EmcTestMethodEquiptBean> testMethodEquiptPage) {
        this.testMethodEquiptPage = testMethodEquiptPage;
    }

    public Page<EmcEquiptTestMethodBean> getEquiptTestMethodPage() {
        return equiptTestMethodPage;
    }

    public void setEquiptTestMethodPage(Page<EmcEquiptTestMethodBean> equiptTestMethodPage) {
        this.equiptTestMethodPage = equiptTestMethodPage;
    }

    public Page<EmcPropertyFieldBean> getPropertyFieldPage() {
        return propertyFieldPage;
    }

    public void setPropertyFieldPage(Page<EmcPropertyFieldBean> propertyFieldPage) {
        this.propertyFieldPage = propertyFieldPage;
    }

    public Page<EmcMonitorTmplBean> getMonitorTmplPage() {
        return monitorTmplPage;
    }

    public void setMonitorTmplPage(Page<EmcMonitorTmplBean> monitorTmplPage) {
        this.monitorTmplPage = monitorTmplPage;
    }

    public Page<EmcItemResultBean> getItemResultPage() {
        return itemResultPage;
    }

    public void setItemResultPage(Page<EmcItemResultBean> itemResultPage) {
        this.itemResultPage = itemResultPage;
    }

    public Page<CoreCodeBean> getCoreCodePage() {
        return coreCodePage;
    }

    public void setCoreCodePage(Page<CoreCodeBean> coreCodePage) {
        this.coreCodePage = coreCodePage;
    }

    public Page<EmcMonitorPointBean> getMonitorPointPage() {
        return monitorPointPage;
    }

    public void setMonitorPointPage(Page<EmcMonitorPointBean> monitorPointPage) {
        this.monitorPointPage = monitorPointPage;
    }

    public Page<EmcClientBean> getClientPage() {
        return clientPage;
    }

    public void setClientPage(Page<EmcClientBean> clientPage) {
        this.clientPage = clientPage;
    }

    public Page<EmcMonitorTmplGroupBean> getMonitorTmplGroupPage() {
        return monitorTmplGroupPage;
    }

    public void setMonitorTmplGroupPage(Page<EmcMonitorTmplGroupBean> monitorTmplGroupPage) {
        this.monitorTmplGroupPage = monitorTmplGroupPage;
    }

    public Page<EmcMonitorSchemeBean> getMonitorSchemePage() {
        return monitorSchemePage;
    }

    public void setMonitorSchemePage(Page<EmcMonitorSchemeBean> monitorSchemePage) {
        this.monitorSchemePage = monitorSchemePage;
    }

    public Page<EmcTestCategoryBean> getTestCategoryPage() {
        return testCategoryPage;
    }

    public void setTestCategoryPage(Page<EmcTestCategoryBean> testCategoryPage) {
        this.testCategoryPage = testCategoryPage;
    }

    public Page<EmcTestBean> getTestPage() {
        return testPage;
    }

    public void setTestPage(Page<EmcTestBean> testPage) {
        this.testPage = testPage;
    }

    public Page<EmcTestMethodBean> getTestMethodPage() {
        return testMethodPage;
    }

    public void setTestMethodPage(Page<EmcTestMethodBean> testMethodPage) {
        this.testMethodPage = testMethodPage;
    }

    public Page<EmcMethodBean> getMethodPage() {
        return methodPage;
    }

    public void setMethodPage(Page<EmcMethodBean> methodPage) {
        this.methodPage = methodPage;
    }

    public Page<EmcItemBean> getItemPage() {
        return itemPage;
    }

    public void setItemPage(Page<EmcItemBean> itemPage) {
        this.itemPage = itemPage;
    }

    public Page<EmcItemGroupBean> getItemGroupPage() {
        return itemGroupPage;
    }

    public void setItemGroupPage(Page<EmcItemGroupBean> itemGroupPage) {
        this.itemGroupPage = itemGroupPage;
    }

    public Page<EmcGroupItemBean> getGroupItemPage() {
        return groupItemPage;
    }

    public void setGroupItemPage(Page<EmcGroupItemBean> groupItemPage) {
        this.groupItemPage = groupItemPage;
    }

    public Page<EmcContainerCategoryBean> getContainerPage() {
        return containerPage;
    }

    public void setContainerPage(Page<EmcContainerCategoryBean> containerPage) {
        this.containerPage = containerPage;
    }

    public Page<EmcEquiptCategoryBean> getEquiptCategoryPage() {
        return equiptCategoryPage;
    }

    public void setEquiptCategoryPage(Page<EmcEquiptCategoryBean> equiptCategoryPage) {
        this.equiptCategoryPage = equiptCategoryPage;
    }

    public Page<EmcUnitBean> getUnitPage() {
        return unitPage;
    }

    public void setUnitPage(Page<EmcUnitBean> unitPage) {
        this.unitPage = unitPage;
    }

    public Page<EmcQcCategoryBean> getQcCategoryPage() {
        return qcCategoryPage;
    }

    public void setQcCategoryPage(Page<EmcQcCategoryBean> qcCategoryPage) {
        this.qcCategoryPage = qcCategoryPage;
    }

    public Page<EmcMonitorPointCategoryBean> getMonitorPointCategoryPage() {
        return monitorPointCategoryPage;
    }

    public void setMonitorPointCategoryPage(Page<EmcMonitorPointCategoryBean> monitorPointCategoryPage) {
        this.monitorPointCategoryPage = monitorPointCategoryPage;
    }

    public Page<EmcTestMethodOrderCategoryBean> getTestOrderCategoryPage() {
        return testOrderCategoryPage;
    }

    public void setTestOrderCategoryPage(Page<EmcTestMethodOrderCategoryBean> testOrderCategoryPage) {
        this.testOrderCategoryPage = testOrderCategoryPage;
    }

    public Page<EmcTestCapacityBean> getTestCapacityPage() {
        return testCapacityPage;
    }

    public void setTestCapacityPage(Page<EmcTestCapacityBean> testCapacityPage) {
        this.testCapacityPage = testCapacityPage;
    }

    public Page<EmcEquiptAcqScriptBean> getAcqScriptPage() {
        return acqScriptPage;
    }

    public void setAcqScriptPage(Page<EmcEquiptAcqScriptBean> acqScriptPage) {
        this.acqScriptPage = acqScriptPage;
    }

    public Page<EmcEasParamBean> getEasParamPage() {
        return easParamPage;
    }

    public void setEasParamPage(Page<EmcEasParamBean> easParamPage) {
        this.easParamPage = easParamPage;
    }

    public Page<EmcEasPreAnalyBean> getEasPreAnalyPage() {
        return easPreAnalyPage;
    }

    public void setEasPreAnalyPage(Page<EmcEasPreAnalyBean> easPreAnalyPage) {
        this.easPreAnalyPage = easPreAnalyPage;
    }

    public Page<EmcEasResultBean> getEasResultPage() {
        return easResultPage;
    }

    public void setEasResultPage(Page<EmcEasResultBean> easResultPage) {
        this.easResultPage = easResultPage;
    }

    public Page<EmcEquiptTestItemBean> getEquiptTestItemPage() {
        return equiptTestItemPage;
    }

    public void setEquiptTestItemPage(Page<EmcEquiptTestItemBean> equiptTestItemPage) {
        this.equiptTestItemPage = equiptTestItemPage;
    }

    public Page<EmcFolderBean> getSampleParamFolderPage() {
        return sampleParamFolderPage;
    }

    public void setSampleParamFolderPage(Page<EmcFolderBean> sampleParamFolderPage) {
        this.sampleParamFolderPage = sampleParamFolderPage;
    }

    public Page<EmcOrderBean> getSampleParamOrderPage() {
        return sampleParamOrderPage;
    }

    public void setSampleParamOrderPage(Page<EmcOrderBean> sampleParamOrderPage) {
        this.sampleParamOrderPage = sampleParamOrderPage;
    }

    public Page<EmcOrderTaskBean> getSampleParamOrderTaskPage() {
        return sampleParamOrderTaskPage;
    }

    public void setSampleParamOrderTaskPage(Page<EmcOrderTaskBean> sampleParamOrderTaskPage) {
        this.sampleParamOrderTaskPage = sampleParamOrderTaskPage;
    }

    public Page<EmcOrderTaskResultBean> getSampleParamOrderTaskResultPage() {
        return sampleParamOrderTaskResultPage;
    }

    public void setSampleParamOrderTaskResultPage(Page<EmcOrderTaskResultBean> sampleParamOrderTaskResultPage) {
        this.sampleParamOrderTaskResultPage = sampleParamOrderTaskResultPage;
    }

    public Page<EmcOrderItemGroupBean> getSampleParamOrderItemGroupPage() {
        return sampleParamOrderItemGroupPage;
    }

    public void setSampleParamOrderItemGroupPage(Page<EmcOrderItemGroupBean> sampleParamOrderItemGroupPage) {
        this.sampleParamOrderItemGroupPage = sampleParamOrderItemGroupPage;
    }
}
