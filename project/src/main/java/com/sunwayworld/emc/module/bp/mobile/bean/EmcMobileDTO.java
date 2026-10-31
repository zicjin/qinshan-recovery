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
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
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
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
    public class EmcMobileDTO {

    private String message;
    /** 业务数据 */
    private List<EmcProjectBean> projectList; // 项目列表
    private List<EmcFolderBean> folderList; // 点位列表
    private List<EmcSampleBean> sampleList; // 采样单列表
    private List<EmcOrderBean> orderList; // 样品列表
    private List<EmcOrderContainerBean> orderContainerList;
    private List<EmcOrderTaskBean> orderTaskList; // 样品测试列表
    private List<EmcOrderTaskResultBean> orderTaskResultList; // 样品测试结果列表
    private List<EmcPropertyDataBean> propertyDataList; // 属性值列表
    private List<CoreFileBean> projectFileList; // 项目附件
    private List<EmcEquiptCalibrationBean> calibrationList; // 仪器校准
    private List<EmcEquiptCalibrationLineBean> calibrationLineList; // 仪器校准记录
    private List<EmcOrderItemGroupBean> orderItemGroupList; // 样品分析项组
    private List<EmcEquiptRequisitionBean> requisitionList; // 仪器领用出入库记录
    /** 静态数据 */
    private List<String> operateTypeList; // 操作类型
    private List<EmcEquiptBean> equiptList; // 仪器列表
    private List<EmcEmpBean> empList; // 人员列表
    private List<EmcTestMethodEquiptBean> testMethodEquiptList; // 测试关联仪器列表
    private List<EmcEquiptTestMethodBean> equiptTestMethodList;
    private List<EmcPropertyFieldBean> propertyFieldList; // 属性字段列表
    private List<EmcMonitorTmplBean> monitorTmplList; // 监测模板列表
    private List<EmcItemResultBean> itemResultList; // 测试分析项列表
    private List<CoreCodeBean> codeList; // 系统编码列表
    private List<EmcMonitorPointBean> monitorPointList; // 监测点列表
    private List<EmcClientBean> clientList; // 客户列表
    private List<EmcMonitorTmplGroupBean> monitorTmplGroupList; // 监测模板分组
    private List<EmcMonitorSchemeBean> monitorSchemeList; // 监测方案
    private List<EmcTestCategoryBean> testCategoryList; // 测试分类
    private List<EmcTestBean> testList; // 测试库
    private List<EmcTestMethodBean> testMethodList; // 测试方法
    private List<EmcMethodBean> methodList; // 方法库
    private List<EmcItemBean> itemList; // 分析项
    private List<EmcItemGroupBean> itemGroupList; // 分析项组
    private List<EmcGroupItemBean> groupItemList; // 分析项组分析项
    private List<EmcContainerCategoryBean> containerCategoryBeanList; // 容器组
    private List<EmcEquiptCategoryBean> equiptCategoryList; // 仪器类型
    private List<EmcUnitBean> unitList; // 单位
    private List<EmcQcCategoryBean> qcCategoryList; // QC类型
    private List<EmcMonitorPointCategoryBean> monitorPointCategoryList; // 监测点类型
    private List<EmcTestMethodOrderCategoryBean> testOrderCategoryList; // 测试样品类型

    private List<EmcFolderBean> sampleParamFolderList; // 气的采样参数点位列表

    private List<EmcOrderBean> sampleParamOrderList; // 气的采样参数样品列表

    private List<EmcOrderTaskBean> sampleParamOrderTaskList; // 气的采样参数样品测试列表

    private List<EmcOrderTaskResultBean> sampleParamOrderTaskResultList; // 气的采样参数样品测试结果列表

    private List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList; // 气的采样参数样品分析项组

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<EmcProjectBean> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<EmcProjectBean> projectList) {
        this.projectList = projectList;
    }

    public List<EmcFolderBean> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<EmcFolderBean> folderList) {
        this.folderList = folderList;
    }

    public List<EmcSampleBean> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<EmcSampleBean> sampleList) {
        this.sampleList = sampleList;
    }

    public List<EmcOrderBean> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<EmcOrderBean> orderList) {
        this.orderList = orderList;
    }

    public List<EmcOrderContainerBean> getOrderContainerList() {
        return orderContainerList;
    }

    public void setOrderContainerList(List<EmcOrderContainerBean> orderContainerList) {
        this.orderContainerList = orderContainerList;
    }

    public List<EmcOrderTaskBean> getOrderTaskList() {
        return orderTaskList;
    }

    public void setOrderTaskList(List<EmcOrderTaskBean> orderTaskList) {
        this.orderTaskList = orderTaskList;
    }

    public List<EmcOrderTaskResultBean> getOrderTaskResultList() {
        return orderTaskResultList;
    }

    public void setOrderTaskResultList(List<EmcOrderTaskResultBean> orderTaskResultList) {
        this.orderTaskResultList = orderTaskResultList;
    }

    public List<EmcPropertyDataBean> getPropertyDataList() {
        return propertyDataList;
    }

    public void setPropertyDataList(List<EmcPropertyDataBean> propertyDataList) {
        this.propertyDataList = propertyDataList;
    }

    public List<String> getOperateTypeList() {
        return operateTypeList;
    }

    public List<CoreFileBean> getProjectFileList() {
        return projectFileList;
    }

    public void setProjectFileList(List<CoreFileBean> projectFileList) {
        this.projectFileList = projectFileList;
    }

    public List<EmcEquiptCalibrationBean> getCalibrationList() {
        return calibrationList;
    }

    public void setCalibrationList(List<EmcEquiptCalibrationBean> calibrationList) {
        this.calibrationList = calibrationList;
    }

    public List<EmcEquiptCalibrationLineBean> getCalibrationLineList() {
        return calibrationLineList;
    }

    public void setCalibrationLineList(List<EmcEquiptCalibrationLineBean> calibrationLineList) {
        this.calibrationLineList = calibrationLineList;
    }

    public List<EmcEquiptRequisitionBean> getRequisitionList() {
        return requisitionList;
    }

    public void setRequisitionList(List<EmcEquiptRequisitionBean> requisitionList) {
        this.requisitionList = requisitionList;
    }

    public List<EmcOrderItemGroupBean> getOrderItemGroupList() {
        return orderItemGroupList;
    }

    public void setOrderItemGroupList(List<EmcOrderItemGroupBean> orderItemGroupList) {
        this.orderItemGroupList = orderItemGroupList;
    }

    public void setOperateTypeList(List<String> operateTypeList) {
        this.operateTypeList = operateTypeList;
    }

    public List<EmcEquiptBean> getEquiptList() {
        return equiptList;
    }

    public void setEquiptList(List<EmcEquiptBean> equiptList) {
        this.equiptList = equiptList;
    }

    public List<EmcEmpBean> getEmpList() {
        return empList;
    }

    public void setEmpList(List<EmcEmpBean> empList) {
        this.empList = empList;
    }

    public List<EmcTestMethodEquiptBean> getTestMethodEquiptList() {
        return testMethodEquiptList;
    }

    public void setTestMethodEquiptList(List<EmcTestMethodEquiptBean> testMethodEquiptList) {
        this.testMethodEquiptList = testMethodEquiptList;
    }

    public List<EmcEquiptTestMethodBean> getEquiptTestMethodList() {
        return equiptTestMethodList;
    }

    public void setEquiptTestMethodList(List<EmcEquiptTestMethodBean> equiptTestMethodList) {
        this.equiptTestMethodList = equiptTestMethodList;
    }

    public List<EmcPropertyFieldBean> getPropertyFieldList() {
        return propertyFieldList;
    }

    public void setPropertyFieldList(List<EmcPropertyFieldBean> propertyFieldList) {
        this.propertyFieldList = propertyFieldList;
    }

    public List<EmcMonitorTmplBean> getMonitorTmplList() {
        return monitorTmplList;
    }

    public void setMonitorTmplList(List<EmcMonitorTmplBean> monitorTmplList) {
        this.monitorTmplList = monitorTmplList;
    }

    public List<EmcItemResultBean> getItemResultList() {
        return itemResultList;
    }

    public void setItemResultList(List<EmcItemResultBean> itemResultList) {
        this.itemResultList = itemResultList;
    }

    public List<CoreCodeBean> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<CoreCodeBean> codeList) {
        this.codeList = codeList;
    }

    public List<EmcMonitorPointBean> getMonitorPointList() {
        return monitorPointList;
    }

    public void setMonitorPointList(List<EmcMonitorPointBean> monitorPointList) {
        this.monitorPointList = monitorPointList;
    }

    public List<EmcClientBean> getClientList() {
        return clientList;
    }

    public void setClientList(List<EmcClientBean> clientList) {
        this.clientList = clientList;
    }

    public List<EmcMonitorTmplGroupBean> getMonitorTmplGroupList() {
        return monitorTmplGroupList;
    }

    public void setMonitorTmplGroupList(List<EmcMonitorTmplGroupBean> monitorTmplGroupList) {
        this.monitorTmplGroupList = monitorTmplGroupList;
    }

    public List<EmcMonitorSchemeBean> getMonitorSchemeList() {
        return monitorSchemeList;
    }

    public void setMonitorSchemeList(List<EmcMonitorSchemeBean> monitorSchemeList) {
        this.monitorSchemeList = monitorSchemeList;
    }

    public List<EmcTestCategoryBean> getTestCategoryList() {
        return testCategoryList;
    }

    public void setTestCategoryList(List<EmcTestCategoryBean> testCategoryList) {
        this.testCategoryList = testCategoryList;
    }

    public List<EmcTestBean> getTestList() {
        return testList;
    }

    public void setTestList(List<EmcTestBean> testList) {
        this.testList = testList;
    }

    public List<EmcTestMethodBean> getTestMethodList() {
        return testMethodList;
    }

    public void setTestMethodList(List<EmcTestMethodBean> testMethodList) {
        this.testMethodList = testMethodList;
    }

    public List<EmcMethodBean> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<EmcMethodBean> methodList) {
        this.methodList = methodList;
    }

    public List<EmcItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<EmcItemBean> itemList) {
        this.itemList = itemList;
    }

    public List<EmcItemGroupBean> getItemGroupList() {
        return itemGroupList;
    }

    public void setItemGroupList(List<EmcItemGroupBean> itemGroupList) {
        this.itemGroupList = itemGroupList;
    }

    public List<EmcGroupItemBean> getGroupItemList() {
        return groupItemList;
    }

    public void setGroupItemList(List<EmcGroupItemBean> groupItemList) {
        this.groupItemList = groupItemList;
    }

    public List<EmcContainerCategoryBean> getContainerCategoryBeanList() {
        return containerCategoryBeanList;
    }

    public void setContainerCategoryBeanList(List<EmcContainerCategoryBean> containerCategoryBeanList) {
        this.containerCategoryBeanList = containerCategoryBeanList;
    }

    public List<EmcEquiptCategoryBean> getEquiptCategoryList() {
        return equiptCategoryList;
    }

    public void setEquiptCategoryList(List<EmcEquiptCategoryBean> equiptCategoryList) {
        this.equiptCategoryList = equiptCategoryList;
    }

    public List<EmcUnitBean> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<EmcUnitBean> unitList) {
        this.unitList = unitList;
    }

    public List<EmcQcCategoryBean> getQcCategoryList() {
        return qcCategoryList;
    }

    public void setQcCategoryList(List<EmcQcCategoryBean> qcCategoryList) {
        this.qcCategoryList = qcCategoryList;
    }

    public List<EmcMonitorPointCategoryBean> getMonitorPointCategoryList() {
        return monitorPointCategoryList;
    }

    public void setMonitorPointCategoryList(List<EmcMonitorPointCategoryBean> monitorPointCategoryList) {
        this.monitorPointCategoryList = monitorPointCategoryList;
    }

    public List<EmcTestMethodOrderCategoryBean> getTestOrderCategoryList() {
        return testOrderCategoryList;
    }

    public void setTestOrderCategoryList(List<EmcTestMethodOrderCategoryBean> testOrderCategoryList) {
        this.testOrderCategoryList = testOrderCategoryList;
    }

    public List<EmcFolderBean> getSampleParamFolderList() {
        return sampleParamFolderList;
    }

    public void setSampleParamFolderList(List<EmcFolderBean> sampleParamFolderList) {
        this.sampleParamFolderList = sampleParamFolderList;
    }

    public List<EmcOrderBean> getSampleParamOrderList() {
        return sampleParamOrderList;
    }

    public void setSampleParamOrderList(List<EmcOrderBean> sampleParamOrderList) {
        this.sampleParamOrderList = sampleParamOrderList;
    }

    public List<EmcOrderTaskBean> getSampleParamOrderTaskList() {
        return sampleParamOrderTaskList;
    }

    public void setSampleParamOrderTaskList(List<EmcOrderTaskBean> sampleParamOrderTaskList) {
        this.sampleParamOrderTaskList = sampleParamOrderTaskList;
    }

    public List<EmcOrderTaskResultBean> getSampleParamOrderTaskResultList() {
        return sampleParamOrderTaskResultList;
    }

    public void setSampleParamOrderTaskResultList(List<EmcOrderTaskResultBean> sampleParamOrderTaskResultList) {
        this.sampleParamOrderTaskResultList = sampleParamOrderTaskResultList;
    }

    public List<EmcOrderItemGroupBean> getSampleParamOrderItemGroupList() {
        return sampleParamOrderItemGroupList;
    }

    public void setSampleParamOrderItemGroupList(List<EmcOrderItemGroupBean> sampleParamOrderItemGroupList) {
        this.sampleParamOrderItemGroupList = sampleParamOrderItemGroupList;
    }

    @Override
    public String toString() {
        return "EmcMobileDTO{" +
                "message='" + message + '\'' +
                ", projectList=" + projectList +
                ", folderList=" + folderList +
                ", sampleList=" + sampleList +
                ", orderList=" + orderList +
                ", orderContainerList=" + orderContainerList +
                ", orderTaskList=" + orderTaskList +
                ", orderTaskResultList=" + orderTaskResultList +
                ", propertyDataList=" + propertyDataList +
                ", projectFileList=" + projectFileList +
                ", calibrationList=" + calibrationList +
                ", calibrationLineList=" + calibrationLineList +
                ", orderItemGroupList=" + orderItemGroupList +
                ", requisitionList=" + requisitionList +
                ", operateTypeList=" + operateTypeList +
                ", equiptList=" + equiptList +
                ", empList=" + empList +
                ", testMethodEquiptList=" + testMethodEquiptList +
                ", equiptTestMethodList=" + equiptTestMethodList +
                ", propertyFieldList=" + propertyFieldList +
                ", monitorTmplList=" + monitorTmplList +
                ", itemResultList=" + itemResultList +
                ", codeList=" + codeList +
                ", monitorPointList=" + monitorPointList +
                ", clientList=" + clientList +
                ", monitorTmplGroupList=" + monitorTmplGroupList +
                ", monitorSchemeList=" + monitorSchemeList +
                ", testCategoryList=" + testCategoryList +
                ", testList=" + testList +
                ", testMethodList=" + testMethodList +
                ", methodList=" + methodList +
                ", itemList=" + itemList +
                ", itemGroupList=" + itemGroupList +
                ", groupItemList=" + groupItemList +
                ", containerCategoryBeanList=" + containerCategoryBeanList +
                ", equiptCategoryList=" + equiptCategoryList +
                ", unitList=" + unitList +
                ", qcCategoryList=" + qcCategoryList +
                ", monitorPointCategoryList=" + monitorPointCategoryList +
                ", testOrderCategoryList=" + testOrderCategoryList +
                ", sampleParamFolderList=" + sampleParamFolderList +
                ", sampleParamOrderList=" + sampleParamOrderList +
                ", sampleParamOrderTaskList=" + sampleParamOrderTaskList +
                ", sampleParamOrderTaskResultList=" + sampleParamOrderTaskResultList +
                ", sampleParamOrderItemGroupList=" + sampleParamOrderItemGroupList +
                '}';
    }

    private List<EmcCapacityItemGroupBean> capacityItemGroupList;
    private List<EmcCapacityItemBean> capacityItemList;
    private List<EmcRelatedTestMethodBean> relatedTestMethodList;

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
