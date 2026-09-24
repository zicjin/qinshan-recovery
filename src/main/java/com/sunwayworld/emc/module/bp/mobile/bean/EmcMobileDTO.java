/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.mobile.bean;

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
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
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
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import java.util.List;

public class EmcMobileDTO {
    private String message;
    private List<EmcProjectBean> projectList;
    private List<EmcFolderBean> folderList;
    private List<EmcSampleBean> sampleList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderContainerBean> orderContainerList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private List<EmcPropertyDataBean> propertyDataList;
    private List<CoreFileBean> projectFileList;
    private List<EmcEquiptCalibrationBean> calibrationList;
    private List<EmcEquiptCalibrationLineBean> calibrationLineList;
    private List<EmcOrderItemGroupBean> orderItemGroupList;
    private List<EmcEquiptRequisitionBean> requisitionList;
    private List<String> operateTypeList;
    private List<EmcEquiptBean> equiptList;
    private List<EmcEmpBean> empList;
    private List<EmcTestMethodEquiptBean> testMethodEquiptList;
    private List<EmcEquiptTestMethodBean> equiptTestMethodList;
    private List<EmcPropertyFieldBean> propertyFieldList;
    private List<EmcMonitorTmplBean> monitorTmplList;
    private List<EmcItemResultBean> itemResultList;
    private List<CoreCodeBean> codeList;
    private List<EmcMonitorPointBean> monitorPointList;
    private List<EmcClientBean> clientList;
    private List<EmcMonitorTmplGroupBean> monitorTmplGroupList;
    private List<EmcMonitorSchemeBean> monitorSchemeList;
    private List<EmcTestCategoryBean> testCategoryList;
    private List<EmcTestBean> testList;
    private List<EmcTestMethodBean> testMethodList;
    private List<EmcMethodBean> methodList;
    private List<EmcItemBean> itemList;
    private List<EmcItemGroupBean> itemGroupList;
    private List<EmcGroupItemBean> groupItemList;
    private List<EmcContainerCategoryBean> containerCategoryBeanList;
    private List<EmcEquiptCategoryBean> equiptCategoryList;
    private List<EmcUnitBean> unitList;
    private List<EmcQcCategoryBean> qcCategoryList;
    private List<EmcMonitorPointCategoryBean> monitorPointCategoryList;
    private List<EmcTestMethodOrderCategoryBean> testOrderCategoryList;
    private List<EmcFolderBean> sampleParamFolderList;
    private List<EmcOrderBean> sampleParamOrderList;
    private List<EmcOrderTaskBean> sampleParamOrderTaskList;
    private List<EmcOrderTaskResultBean> sampleParamOrderTaskResultList;
    private List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList;
    private List<EmcCapacityItemGroupBean> capacityItemGroupList;
    private List<EmcCapacityItemBean> capacityItemList;
    private List<EmcRelatedTestMethodBean> relatedTestMethodList;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EmcProjectBean> getProjectList() {
        return this.projectList;
    }

    public void setProjectList(List<EmcProjectBean> projectList) {
        this.projectList = projectList;
    }

    public List<EmcFolderBean> getFolderList() {
        return this.folderList;
    }

    public void setFolderList(List<EmcFolderBean> folderList) {
        this.folderList = folderList;
    }

    public List<EmcSampleBean> getSampleList() {
        return this.sampleList;
    }

    public void setSampleList(List<EmcSampleBean> sampleList) {
        this.sampleList = sampleList;
    }

    public List<EmcOrderBean> getOrderList() {
        return this.orderList;
    }

    public void setOrderList(List<EmcOrderBean> orderList) {
        this.orderList = orderList;
    }

    public List<EmcOrderContainerBean> getOrderContainerList() {
        return this.orderContainerList;
    }

    public void setOrderContainerList(List<EmcOrderContainerBean> orderContainerList) {
        this.orderContainerList = orderContainerList;
    }

    public List<EmcOrderTaskBean> getOrderTaskList() {
        return this.orderTaskList;
    }

    public void setOrderTaskList(List<EmcOrderTaskBean> orderTaskList) {
        this.orderTaskList = orderTaskList;
    }

    public List<EmcOrderTaskResultBean> getOrderTaskResultList() {
        return this.orderTaskResultList;
    }

    public void setOrderTaskResultList(List<EmcOrderTaskResultBean> orderTaskResultList) {
        this.orderTaskResultList = orderTaskResultList;
    }

    public List<EmcPropertyDataBean> getPropertyDataList() {
        return this.propertyDataList;
    }

    public void setPropertyDataList(List<EmcPropertyDataBean> propertyDataList) {
        this.propertyDataList = propertyDataList;
    }

    public List<String> getOperateTypeList() {
        return this.operateTypeList;
    }

    public List<CoreFileBean> getProjectFileList() {
        return this.projectFileList;
    }

    public void setProjectFileList(List<CoreFileBean> projectFileList) {
        this.projectFileList = projectFileList;
    }

    public List<EmcEquiptCalibrationBean> getCalibrationList() {
        return this.calibrationList;
    }

    public void setCalibrationList(List<EmcEquiptCalibrationBean> calibrationList) {
        this.calibrationList = calibrationList;
    }

    public List<EmcEquiptCalibrationLineBean> getCalibrationLineList() {
        return this.calibrationLineList;
    }

    public void setCalibrationLineList(List<EmcEquiptCalibrationLineBean> calibrationLineList) {
        this.calibrationLineList = calibrationLineList;
    }

    public List<EmcEquiptRequisitionBean> getRequisitionList() {
        return this.requisitionList;
    }

    public void setRequisitionList(List<EmcEquiptRequisitionBean> requisitionList) {
        this.requisitionList = requisitionList;
    }

    public List<EmcOrderItemGroupBean> getOrderItemGroupList() {
        return this.orderItemGroupList;
    }

    public void setOrderItemGroupList(List<EmcOrderItemGroupBean> orderItemGroupList) {
        this.orderItemGroupList = orderItemGroupList;
    }

    public void setOperateTypeList(List<String> operateTypeList) {
        this.operateTypeList = operateTypeList;
    }

    public List<EmcEquiptBean> getEquiptList() {
        return this.equiptList;
    }

    public void setEquiptList(List<EmcEquiptBean> equiptList) {
        this.equiptList = equiptList;
    }

    public List<EmcEmpBean> getEmpList() {
        return this.empList;
    }

    public void setEmpList(List<EmcEmpBean> empList) {
        this.empList = empList;
    }

    public List<EmcTestMethodEquiptBean> getTestMethodEquiptList() {
        return this.testMethodEquiptList;
    }

    public void setTestMethodEquiptList(List<EmcTestMethodEquiptBean> testMethodEquiptList) {
        this.testMethodEquiptList = testMethodEquiptList;
    }

    public List<EmcEquiptTestMethodBean> getEquiptTestMethodList() {
        return this.equiptTestMethodList;
    }

    public void setEquiptTestMethodList(List<EmcEquiptTestMethodBean> equiptTestMethodList) {
        this.equiptTestMethodList = equiptTestMethodList;
    }

    public List<EmcPropertyFieldBean> getPropertyFieldList() {
        return this.propertyFieldList;
    }

    public void setPropertyFieldList(List<EmcPropertyFieldBean> propertyFieldList) {
        this.propertyFieldList = propertyFieldList;
    }

    public List<EmcMonitorTmplBean> getMonitorTmplList() {
        return this.monitorTmplList;
    }

    public void setMonitorTmplList(List<EmcMonitorTmplBean> monitorTmplList) {
        this.monitorTmplList = monitorTmplList;
    }

    public List<EmcItemResultBean> getItemResultList() {
        return this.itemResultList;
    }

    public void setItemResultList(List<EmcItemResultBean> itemResultList) {
        this.itemResultList = itemResultList;
    }

    public List<CoreCodeBean> getCodeList() {
        return this.codeList;
    }

    public void setCodeList(List<CoreCodeBean> codeList) {
        this.codeList = codeList;
    }

    public List<EmcMonitorPointBean> getMonitorPointList() {
        return this.monitorPointList;
    }

    public void setMonitorPointList(List<EmcMonitorPointBean> monitorPointList) {
        this.monitorPointList = monitorPointList;
    }

    public List<EmcClientBean> getClientList() {
        return this.clientList;
    }

    public void setClientList(List<EmcClientBean> clientList) {
        this.clientList = clientList;
    }

    public List<EmcMonitorTmplGroupBean> getMonitorTmplGroupList() {
        return this.monitorTmplGroupList;
    }

    public void setMonitorTmplGroupList(List<EmcMonitorTmplGroupBean> monitorTmplGroupList) {
        this.monitorTmplGroupList = monitorTmplGroupList;
    }

    public List<EmcMonitorSchemeBean> getMonitorSchemeList() {
        return this.monitorSchemeList;
    }

    public void setMonitorSchemeList(List<EmcMonitorSchemeBean> monitorSchemeList) {
        this.monitorSchemeList = monitorSchemeList;
    }

    public List<EmcTestCategoryBean> getTestCategoryList() {
        return this.testCategoryList;
    }

    public void setTestCategoryList(List<EmcTestCategoryBean> testCategoryList) {
        this.testCategoryList = testCategoryList;
    }

    public List<EmcTestBean> getTestList() {
        return this.testList;
    }

    public void setTestList(List<EmcTestBean> testList) {
        this.testList = testList;
    }

    public List<EmcTestMethodBean> getTestMethodList() {
        return this.testMethodList;
    }

    public void setTestMethodList(List<EmcTestMethodBean> testMethodList) {
        this.testMethodList = testMethodList;
    }

    public List<EmcMethodBean> getMethodList() {
        return this.methodList;
    }

    public void setMethodList(List<EmcMethodBean> methodList) {
        this.methodList = methodList;
    }

    public List<EmcItemBean> getItemList() {
        return this.itemList;
    }

    public void setItemList(List<EmcItemBean> itemList) {
        this.itemList = itemList;
    }

    public List<EmcItemGroupBean> getItemGroupList() {
        return this.itemGroupList;
    }

    public void setItemGroupList(List<EmcItemGroupBean> itemGroupList) {
        this.itemGroupList = itemGroupList;
    }

    public List<EmcGroupItemBean> getGroupItemList() {
        return this.groupItemList;
    }

    public void setGroupItemList(List<EmcGroupItemBean> groupItemList) {
        this.groupItemList = groupItemList;
    }

    public List<EmcContainerCategoryBean> getContainerCategoryBeanList() {
        return this.containerCategoryBeanList;
    }

    public void setContainerCategoryBeanList(List<EmcContainerCategoryBean> containerCategoryBeanList) {
        this.containerCategoryBeanList = containerCategoryBeanList;
    }

    public List<EmcEquiptCategoryBean> getEquiptCategoryList() {
        return this.equiptCategoryList;
    }

    public void setEquiptCategoryList(List<EmcEquiptCategoryBean> equiptCategoryList) {
        this.equiptCategoryList = equiptCategoryList;
    }

    public List<EmcUnitBean> getUnitList() {
        return this.unitList;
    }

    public void setUnitList(List<EmcUnitBean> unitList) {
        this.unitList = unitList;
    }

    public List<EmcQcCategoryBean> getQcCategoryList() {
        return this.qcCategoryList;
    }

    public void setQcCategoryList(List<EmcQcCategoryBean> qcCategoryList) {
        this.qcCategoryList = qcCategoryList;
    }

    public List<EmcMonitorPointCategoryBean> getMonitorPointCategoryList() {
        return this.monitorPointCategoryList;
    }

    public void setMonitorPointCategoryList(List<EmcMonitorPointCategoryBean> monitorPointCategoryList) {
        this.monitorPointCategoryList = monitorPointCategoryList;
    }

    public List<EmcTestMethodOrderCategoryBean> getTestOrderCategoryList() {
        return this.testOrderCategoryList;
    }

    public void setTestOrderCategoryList(List<EmcTestMethodOrderCategoryBean> testOrderCategoryList) {
        this.testOrderCategoryList = testOrderCategoryList;
    }

    public List<EmcFolderBean> getSampleParamFolderList() {
        return this.sampleParamFolderList;
    }

    public void setSampleParamFolderList(List<EmcFolderBean> sampleParamFolderList) {
        this.sampleParamFolderList = sampleParamFolderList;
    }

    public List<EmcOrderBean> getSampleParamOrderList() {
        return this.sampleParamOrderList;
    }

    public void setSampleParamOrderList(List<EmcOrderBean> sampleParamOrderList) {
        this.sampleParamOrderList = sampleParamOrderList;
    }

    public List<EmcOrderTaskBean> getSampleParamOrderTaskList() {
        return this.sampleParamOrderTaskList;
    }

    public void setSampleParamOrderTaskList(List<EmcOrderTaskBean> sampleParamOrderTaskList) {
        this.sampleParamOrderTaskList = sampleParamOrderTaskList;
    }

    public List<EmcOrderTaskResultBean> getSampleParamOrderTaskResultList() {
        return this.sampleParamOrderTaskResultList;
    }

    public void setSampleParamOrderTaskResultList(List<EmcOrderTaskResultBean> sampleParamOrderTaskResultList) {
        this.sampleParamOrderTaskResultList = sampleParamOrderTaskResultList;
    }

    public List<EmcOrderItemGroupBean> getSampleParamOrderItemGroupList() {
        return this.sampleParamOrderItemGroupList;
    }

    public void setSampleParamOrderItemGroupList(List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList) {
        this.sampleParamOrderItemGroupList = sampleParamOrderItemGroupList;
    }

    public String toString() {
        return "EmcMobileDTO{message='" + this.message + '\'' + ", projectList=" + this.projectList + ", folderList=" + this.folderList + ", sampleList=" + this.sampleList + ", orderList=" + this.orderList + ", orderContainerList=" + this.orderContainerList + ", orderTaskList=" + this.orderTaskList + ", orderTaskResultList=" + this.orderTaskResultList + ", propertyDataList=" + this.propertyDataList + ", projectFileList=" + this.projectFileList + ", calibrationList=" + this.calibrationList + ", calibrationLineList=" + this.calibrationLineList + ", orderItemGroupList=" + this.orderItemGroupList + ", requisitionList=" + this.requisitionList + ", operateTypeList=" + this.operateTypeList + ", equiptList=" + this.equiptList + ", empList=" + this.empList + ", testMethodEquiptList=" + this.testMethodEquiptList + ", equiptTestMethodList=" + this.equiptTestMethodList + ", propertyFieldList=" + this.propertyFieldList + ", monitorTmplList=" + this.monitorTmplList + ", itemResultList=" + this.itemResultList + ", codeList=" + this.codeList + ", monitorPointList=" + this.monitorPointList + ", clientList=" + this.clientList + ", monitorTmplGroupList=" + this.monitorTmplGroupList + ", monitorSchemeList=" + this.monitorSchemeList + ", testCategoryList=" + this.testCategoryList + ", testList=" + this.testList + ", testMethodList=" + this.testMethodList + ", methodList=" + this.methodList + ", itemList=" + this.itemList + ", itemGroupList=" + this.itemGroupList + ", groupItemList=" + this.groupItemList + ", containerCategoryBeanList=" + this.containerCategoryBeanList + ", equiptCategoryList=" + this.equiptCategoryList + ", unitList=" + this.unitList + ", qcCategoryList=" + this.qcCategoryList + ", monitorPointCategoryList=" + this.monitorPointCategoryList + ", testOrderCategoryList=" + this.testOrderCategoryList + ", sampleParamFolderList=" + this.sampleParamFolderList + ", sampleParamOrderList=" + this.sampleParamOrderList + ", sampleParamOrderTaskList=" + this.sampleParamOrderTaskList + ", sampleParamOrderTaskResultList=" + this.sampleParamOrderTaskResultList + ", sampleParamOrderItemGroupList=" + this.sampleParamOrderItemGroupList + '}';
    }

    public List<EmcCapacityItemGroupBean> getCapacityItemGroupList() {
        return this.capacityItemGroupList;
    }

    public void setCapacityItemGroupList(List<EmcCapacityItemGroupBean> capacityItemGroupList) {
        this.capacityItemGroupList = capacityItemGroupList;
    }

    public List<EmcCapacityItemBean> getCapacityItemList() {
        return this.capacityItemList;
    }

    public void setCapacityItemList(List<EmcCapacityItemBean> capacityItemList) {
        this.capacityItemList = capacityItemList;
    }

    public List<EmcRelatedTestMethodBean> getRelatedTestMethodList() {
        return this.relatedTestMethodList;
    }

    public void setRelatedTestMethodList(List<EmcRelatedTestMethodBean> relatedTestMethodList) {
        this.relatedTestMethodList = relatedTestMethodList;
    }
}

