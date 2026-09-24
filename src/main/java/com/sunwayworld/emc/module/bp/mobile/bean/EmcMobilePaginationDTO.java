/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.mobile.bean;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
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
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
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
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
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

public class EmcMobilePaginationDTO {
    private String message;
    private Page<EmcProjectBean> projectPage;
    private Page<EmcFolderBean> folderPage;
    private Page<EmcSampleBean> samplePage;
    private Page<EmcOrderBean> orderPage;
    private Page<EmcOrderContainerBean> orderContainerPage;
    private Page<EmcOrderTaskBean> orderTaskPage;
    private Page<EmcOrderTaskResultBean> orderTaskResultPage;
    private Page<EmcPropertyDataBean> propertyDataPage;
    private Page<CoreFileBean> projectFilePage;
    private Page<EmcEquiptCalibrationBean> calibrationPage;
    private Page<EmcEquiptCalibrationLineBean> calibrationLinePage;
    private Page<EmcOrderItemGroupBean> orderItemGroupPage;
    private Page<EmcOrderItemBean> orderItemPage;
    private Page<String> operateTypePage;
    private Page<EmcEquiptBean> equiptPage;
    private Page<EmcEmpBean> empPage;
    private Page<EmcTestMethodEquiptBean> testMethodEquiptPage;
    private Page<EmcEquiptTestMethodBean> equiptTestMethodPage;
    private Page<EmcPropertyFieldBean> propertyFieldPage;
    private Page<EmcMonitorTmplBean> monitorTmplPage;
    private Page<EmcItemResultBean> itemResultPage;
    private Page<CoreCodeBean> coreCodePage;
    private Page<EmcMonitorPointBean> monitorPointPage;
    private Page<EmcClientBean> clientPage;
    private Page<EmcMonitorTmplGroupBean> monitorTmplGroupPage;
    private Page<EmcMonitorSchemeBean> monitorSchemePage;
    private Page<EmcTestCategoryBean> testCategoryPage;
    private Page<EmcTestBean> testPage;
    private Page<EmcTestMethodBean> testMethodPage;
    private Page<EmcMethodBean> methodPage;
    private Page<EmcItemBean> itemPage;
    private Page<EmcItemGroupBean> itemGroupPage;
    private Page<EmcGroupItemBean> groupItemPage;
    private Page<EmcContainerCategoryBean> containerPage;
    private Page<EmcEquiptCategoryBean> equiptCategoryPage;
    private Page<EmcUnitBean> unitPage;
    private Page<EmcQcCategoryBean> qcCategoryPage;
    private Page<EmcMonitorPointCategoryBean> monitorPointCategoryPage;
    private Page<EmcTestMethodOrderCategoryBean> testOrderCategoryPage;
    private Page<EmcTestCapacityBean> testCapacityPage;
    private Page<EmcEquiptAcqScriptBean> acqScriptPage;
    private Page<EmcEasParamBean> easParamPage;
    private Page<EmcEasPreAnalyBean> easPreAnalyPage;
    private Page<EmcEasResultBean> easResultPage;
    private Page<EmcEquiptTestItemBean> equiptTestItemPage;
    private Page<EmcFolderBean> sampleParamFolderPage;
    private Page<EmcOrderBean> sampleParamOrderPage;
    private Page<EmcOrderTaskBean> sampleParamOrderTaskPage;
    private Page<EmcOrderTaskResultBean> sampleParamOrderTaskResultPage;
    private Page<EmcOrderItemGroupBean> sampleParamOrderItemGroupPage;
    private Page<EmcCapacityItemGroupBean> capacityItemGroupPage;
    private Page<EmcCapacityItemBean> capacityItemPage;
    private Page<EmcRelatedTestMethodBean> relatedTestMethodPage;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page<EmcProjectBean> getProjectPage() {
        return this.projectPage;
    }

    public void setProjectPage(Page<EmcProjectBean> projectPage) {
        this.projectPage = projectPage;
    }

    public Page<EmcFolderBean> getFolderPage() {
        return this.folderPage;
    }

    public void setFolderPage(Page<EmcFolderBean> folderPage) {
        this.folderPage = folderPage;
    }

    public Page<EmcSampleBean> getSamplePage() {
        return this.samplePage;
    }

    public void setSamplePage(Page<EmcSampleBean> samplePage) {
        this.samplePage = samplePage;
    }

    public Page<EmcOrderBean> getOrderPage() {
        return this.orderPage;
    }

    public void setOrderPage(Page<EmcOrderBean> orderPage) {
        this.orderPage = orderPage;
    }

    public Page<EmcOrderContainerBean> getOrderContainerPage() {
        return this.orderContainerPage;
    }

    public void setOrderContainerPage(Page<EmcOrderContainerBean> orderContainerPage) {
        this.orderContainerPage = orderContainerPage;
    }

    public Page<EmcOrderTaskBean> getOrderTaskPage() {
        return this.orderTaskPage;
    }

    public void setOrderTaskPage(Page<EmcOrderTaskBean> orderTaskPage) {
        this.orderTaskPage = orderTaskPage;
    }

    public Page<EmcOrderTaskResultBean> getOrderTaskResultPage() {
        return this.orderTaskResultPage;
    }

    public void setOrderTaskResultPage(Page<EmcOrderTaskResultBean> orderTaskResultPage) {
        this.orderTaskResultPage = orderTaskResultPage;
    }

    public Page<EmcPropertyDataBean> getPropertyDataPage() {
        return this.propertyDataPage;
    }

    public void setPropertyDataPage(Page<EmcPropertyDataBean> propertyDataPage) {
        this.propertyDataPage = propertyDataPage;
    }

    public Page<CoreFileBean> getProjectFilePage() {
        return this.projectFilePage;
    }

    public void setProjectFilePage(Page<CoreFileBean> projectFilePage) {
        this.projectFilePage = projectFilePage;
    }

    public Page<EmcEquiptCalibrationBean> getCalibrationPage() {
        return this.calibrationPage;
    }

    public void setCalibrationPage(Page<EmcEquiptCalibrationBean> calibrationPage) {
        this.calibrationPage = calibrationPage;
    }

    public Page<EmcEquiptCalibrationLineBean> getCalibrationLinePage() {
        return this.calibrationLinePage;
    }

    public void setCalibrationLinePage(Page<EmcEquiptCalibrationLineBean> calibrationLinePage) {
        this.calibrationLinePage = calibrationLinePage;
    }

    public Page<EmcOrderItemGroupBean> getOrderItemGroupPage() {
        return this.orderItemGroupPage;
    }

    public void setOrderItemGroupPage(Page<EmcOrderItemGroupBean> orderItemGroupPage) {
        this.orderItemGroupPage = orderItemGroupPage;
    }

    public Page<EmcOrderItemBean> getOrderItemPage() {
        return this.orderItemPage;
    }

    public void setOrderItemPage(Page<EmcOrderItemBean> orderItemPage) {
        this.orderItemPage = orderItemPage;
    }

    public Page<String> getOperateTypePage() {
        return this.operateTypePage;
    }

    public void setOperateTypePage(Page<String> operateTypePage) {
        this.operateTypePage = operateTypePage;
    }

    public Page<EmcEquiptBean> getEquiptPage() {
        return this.equiptPage;
    }

    public void setEquiptPage(Page<EmcEquiptBean> equiptPage) {
        this.equiptPage = equiptPage;
    }

    public Page<EmcEmpBean> getEmpPage() {
        return this.empPage;
    }

    public void setEmpPage(Page<EmcEmpBean> empPage) {
        this.empPage = empPage;
    }

    public Page<EmcTestMethodEquiptBean> getTestMethodEquiptPage() {
        return this.testMethodEquiptPage;
    }

    public void setTestMethodEquiptPage(Page<EmcTestMethodEquiptBean> testMethodEquiptPage) {
        this.testMethodEquiptPage = testMethodEquiptPage;
    }

    public Page<EmcEquiptTestMethodBean> getEquiptTestMethodPage() {
        return this.equiptTestMethodPage;
    }

    public void setEquiptTestMethodPage(Page<EmcEquiptTestMethodBean> equiptTestMethodPage) {
        this.equiptTestMethodPage = equiptTestMethodPage;
    }

    public Page<EmcPropertyFieldBean> getPropertyFieldPage() {
        return this.propertyFieldPage;
    }

    public void setPropertyFieldPage(Page<EmcPropertyFieldBean> propertyFieldPage) {
        this.propertyFieldPage = propertyFieldPage;
    }

    public Page<EmcMonitorTmplBean> getMonitorTmplPage() {
        return this.monitorTmplPage;
    }

    public void setMonitorTmplPage(Page<EmcMonitorTmplBean> monitorTmplPage) {
        this.monitorTmplPage = monitorTmplPage;
    }

    public Page<EmcItemResultBean> getItemResultPage() {
        return this.itemResultPage;
    }

    public void setItemResultPage(Page<EmcItemResultBean> itemResultPage) {
        this.itemResultPage = itemResultPage;
    }

    public Page<CoreCodeBean> getCoreCodePage() {
        return this.coreCodePage;
    }

    public void setCoreCodePage(Page<CoreCodeBean> coreCodePage) {
        this.coreCodePage = coreCodePage;
    }

    public Page<EmcMonitorPointBean> getMonitorPointPage() {
        return this.monitorPointPage;
    }

    public void setMonitorPointPage(Page<EmcMonitorPointBean> monitorPointPage) {
        this.monitorPointPage = monitorPointPage;
    }

    public Page<EmcClientBean> getClientPage() {
        return this.clientPage;
    }

    public void setClientPage(Page<EmcClientBean> clientPage) {
        this.clientPage = clientPage;
    }

    public Page<EmcMonitorTmplGroupBean> getMonitorTmplGroupPage() {
        return this.monitorTmplGroupPage;
    }

    public void setMonitorTmplGroupPage(Page<EmcMonitorTmplGroupBean> monitorTmplGroupPage) {
        this.monitorTmplGroupPage = monitorTmplGroupPage;
    }

    public Page<EmcMonitorSchemeBean> getMonitorSchemePage() {
        return this.monitorSchemePage;
    }

    public void setMonitorSchemePage(Page<EmcMonitorSchemeBean> monitorSchemePage) {
        this.monitorSchemePage = monitorSchemePage;
    }

    public Page<EmcTestCategoryBean> getTestCategoryPage() {
        return this.testCategoryPage;
    }

    public void setTestCategoryPage(Page<EmcTestCategoryBean> testCategoryPage) {
        this.testCategoryPage = testCategoryPage;
    }

    public Page<EmcTestBean> getTestPage() {
        return this.testPage;
    }

    public void setTestPage(Page<EmcTestBean> testPage) {
        this.testPage = testPage;
    }

    public Page<EmcTestMethodBean> getTestMethodPage() {
        return this.testMethodPage;
    }

    public void setTestMethodPage(Page<EmcTestMethodBean> testMethodPage) {
        this.testMethodPage = testMethodPage;
    }

    public Page<EmcMethodBean> getMethodPage() {
        return this.methodPage;
    }

    public void setMethodPage(Page<EmcMethodBean> methodPage) {
        this.methodPage = methodPage;
    }

    public Page<EmcItemBean> getItemPage() {
        return this.itemPage;
    }

    public void setItemPage(Page<EmcItemBean> itemPage) {
        this.itemPage = itemPage;
    }

    public Page<EmcItemGroupBean> getItemGroupPage() {
        return this.itemGroupPage;
    }

    public void setItemGroupPage(Page<EmcItemGroupBean> itemGroupPage) {
        this.itemGroupPage = itemGroupPage;
    }

    public Page<EmcGroupItemBean> getGroupItemPage() {
        return this.groupItemPage;
    }

    public void setGroupItemPage(Page<EmcGroupItemBean> groupItemPage) {
        this.groupItemPage = groupItemPage;
    }

    public Page<EmcContainerCategoryBean> getContainerPage() {
        return this.containerPage;
    }

    public void setContainerPage(Page<EmcContainerCategoryBean> containerPage) {
        this.containerPage = containerPage;
    }

    public Page<EmcEquiptCategoryBean> getEquiptCategoryPage() {
        return this.equiptCategoryPage;
    }

    public void setEquiptCategoryPage(Page<EmcEquiptCategoryBean> equiptCategoryPage) {
        this.equiptCategoryPage = equiptCategoryPage;
    }

    public Page<EmcUnitBean> getUnitPage() {
        return this.unitPage;
    }

    public void setUnitPage(Page<EmcUnitBean> unitPage) {
        this.unitPage = unitPage;
    }

    public Page<EmcQcCategoryBean> getQcCategoryPage() {
        return this.qcCategoryPage;
    }

    public void setQcCategoryPage(Page<EmcQcCategoryBean> qcCategoryPage) {
        this.qcCategoryPage = qcCategoryPage;
    }

    public Page<EmcMonitorPointCategoryBean> getMonitorPointCategoryPage() {
        return this.monitorPointCategoryPage;
    }

    public void setMonitorPointCategoryPage(Page<EmcMonitorPointCategoryBean> monitorPointCategoryPage) {
        this.monitorPointCategoryPage = monitorPointCategoryPage;
    }

    public Page<EmcTestMethodOrderCategoryBean> getTestOrderCategoryPage() {
        return this.testOrderCategoryPage;
    }

    public void setTestOrderCategoryPage(Page<EmcTestMethodOrderCategoryBean> testOrderCategoryPage) {
        this.testOrderCategoryPage = testOrderCategoryPage;
    }

    public Page<EmcTestCapacityBean> getTestCapacityPage() {
        return this.testCapacityPage;
    }

    public void setTestCapacityPage(Page<EmcTestCapacityBean> testCapacityPage) {
        this.testCapacityPage = testCapacityPage;
    }

    public Page<EmcEquiptAcqScriptBean> getAcqScriptPage() {
        return this.acqScriptPage;
    }

    public void setAcqScriptPage(Page<EmcEquiptAcqScriptBean> acqScriptPage) {
        this.acqScriptPage = acqScriptPage;
    }

    public Page<EmcEasParamBean> getEasParamPage() {
        return this.easParamPage;
    }

    public void setEasParamPage(Page<EmcEasParamBean> easParamPage) {
        this.easParamPage = easParamPage;
    }

    public Page<EmcEasPreAnalyBean> getEasPreAnalyPage() {
        return this.easPreAnalyPage;
    }

    public void setEasPreAnalyPage(Page<EmcEasPreAnalyBean> easPreAnalyPage) {
        this.easPreAnalyPage = easPreAnalyPage;
    }

    public Page<EmcEasResultBean> getEasResultPage() {
        return this.easResultPage;
    }

    public void setEasResultPage(Page<EmcEasResultBean> easResultPage) {
        this.easResultPage = easResultPage;
    }

    public Page<EmcEquiptTestItemBean> getEquiptTestItemPage() {
        return this.equiptTestItemPage;
    }

    public void setEquiptTestItemPage(Page<EmcEquiptTestItemBean> equiptTestItemPage) {
        this.equiptTestItemPage = equiptTestItemPage;
    }

    public Page<EmcFolderBean> getSampleParamFolderPage() {
        return this.sampleParamFolderPage;
    }

    public void setSampleParamFolderPage(Page<EmcFolderBean> sampleParamFolderPage) {
        this.sampleParamFolderPage = sampleParamFolderPage;
    }

    public Page<EmcOrderBean> getSampleParamOrderPage() {
        return this.sampleParamOrderPage;
    }

    public void setSampleParamOrderPage(Page<EmcOrderBean> sampleParamOrderPage) {
        this.sampleParamOrderPage = sampleParamOrderPage;
    }

    public Page<EmcOrderTaskBean> getSampleParamOrderTaskPage() {
        return this.sampleParamOrderTaskPage;
    }

    public void setSampleParamOrderTaskPage(Page<EmcOrderTaskBean> sampleParamOrderTaskPage) {
        this.sampleParamOrderTaskPage = sampleParamOrderTaskPage;
    }

    public Page<EmcOrderTaskResultBean> getSampleParamOrderTaskResultPage() {
        return this.sampleParamOrderTaskResultPage;
    }

    public void setSampleParamOrderTaskResultPage(Page<EmcOrderTaskResultBean> sampleParamOrderTaskResultPage) {
        this.sampleParamOrderTaskResultPage = sampleParamOrderTaskResultPage;
    }

    public Page<EmcOrderItemGroupBean> getSampleParamOrderItemGroupPage() {
        return this.sampleParamOrderItemGroupPage;
    }

    public void setSampleParamOrderItemGroupPage(Page<EmcOrderItemGroupBean> sampleParamOrderItemGroupPage) {
        this.sampleParamOrderItemGroupPage = sampleParamOrderItemGroupPage;
    }

    public Page<EmcCapacityItemGroupBean> getCapacityItemGroupPage() {
        return this.capacityItemGroupPage;
    }

    public void setCapacityItemGroupPage(Page<EmcCapacityItemGroupBean> capacityItemGroupPage) {
        this.capacityItemGroupPage = capacityItemGroupPage;
    }

    public Page<EmcCapacityItemBean> getCapacityItemPage() {
        return this.capacityItemPage;
    }

    public void setCapacityItemPage(Page<EmcCapacityItemBean> capacityItemPage) {
        this.capacityItemPage = capacityItemPage;
    }

    public Page<EmcRelatedTestMethodBean> getRelatedTestMethodPage() {
        return this.relatedTestMethodPage;
    }

    public void setRelatedTestMethodPage(Page<EmcRelatedTestMethodBean> relatedTestMethodPage) {
        this.relatedTestMethodPage = relatedTestMethodPage;
    }
}

