package com.sunwayworld.emc.module.bp.report.report.bean;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityMainBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * @author zhengxy@sunwayworld.com 2020年09月07日
 */
public class WordTemplateSourceDTO {

    private EmcProjectBean project;
    private List<EmcFolderBean> folderList;
    private List<EmcSampleBean> sampleList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderBean> qcOrderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskBean> qcOrderTaskList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private List<EmcOrderTaskResultBean> qcTaskResultList;
    private List<EmcItemGroupBean> itemGroupList;
    private List<EmcGroupItemBean> groupItemList;
    private List<EmcRunBean> runList;
    private EmcReportBean report;
    private List<EmcReportBean> reportList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcMonitorTmplBean> monitorTmplList;
    private List<EmcPropertyDataBean> propertyDataList;
    private List<EmcEquiptBean> equiptList;
    private List<CoreFileBean> coreFileList;
    private List<CoreCodeBean> coreCodeList;
    private CoreOrgBean coreOrg;
    private CrmOpportunityMainBean opportunityMain;
    private List<PfRunTempHumidityBean> tempHumidityList;
    private List<EmcIndexCategoryBean> indexCategoryList;
    private List<EmcQcRelatedOrderBean> qcRelatedOrderList;
    private List<EmcItemQcIndexBean> itemQcIndexList;

    public EmcProjectBean getProject() {
        return project;
    }

    public void setProject(EmcProjectBean project) {
        this.project = project;
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

    public List<EmcOrderBean> getQcOrderList() {
        return qcOrderList;
    }

    public void setQcOrderList(List<EmcOrderBean> qcOrderList) {
        this.qcOrderList = qcOrderList;
    }

    public List<EmcOrderTaskBean> getOrderTaskList() {
        return orderTaskList;
    }

    public void setOrderTaskList(List<EmcOrderTaskBean> orderTaskList) {
        this.orderTaskList = orderTaskList;
    }

    public List<EmcOrderTaskBean> getQcOrderTaskList() {
        return qcOrderTaskList;
    }

    public void setQcOrderTaskList(List<EmcOrderTaskBean> qcOrderTaskList) {
        this.qcOrderTaskList = qcOrderTaskList;
    }

    public List<EmcOrderTaskResultBean> getOrderTaskResultList() {
        return orderTaskResultList;
    }

    public void setOrderTaskResultList(List<EmcOrderTaskResultBean> orderTaskResultList) {
        this.orderTaskResultList = orderTaskResultList;
    }

    public List<EmcOrderTaskResultBean> getQcTaskResultList() {
        return qcTaskResultList;
    }

    public void setQcTaskResultList(List<EmcOrderTaskResultBean> qcTaskResultList) {
        this.qcTaskResultList = qcTaskResultList;
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

    public List<EmcRunBean> getRunList() {
        return runList;
    }

    public void setRunList(List<EmcRunBean> runList) {
        this.runList = runList;
    }

    public EmcReportBean getReport() {
        return report;
    }

    public void setReport(EmcReportBean report) {
        this.report = report;
    }

    public List<EmcReportBean> getReportList() {
        return reportList;
    }

    public void setReportList(List<EmcReportBean> reportList) {
        this.reportList = reportList;
    }

    public List<EmcReportResultBean> getReportResultList() {
        return reportResultList;
    }

    public void setReportResultList(List<EmcReportResultBean> reportResultList) {
        this.reportResultList = reportResultList;
    }

    public List<EmcMonitorTmplBean> getMonitorTmplList() {
        return monitorTmplList;
    }

    public void setMonitorTmplList(List<EmcMonitorTmplBean> monitorTmplList) {
        this.monitorTmplList = monitorTmplList;
    }

    public List<EmcPropertyDataBean> getPropertyDataList() {
        return propertyDataList;
    }

    public void setPropertyDataList(List<EmcPropertyDataBean> propertyDataList) {
        this.propertyDataList = propertyDataList;
    }

    public List<EmcEquiptBean> getEquiptList() {
        return equiptList;
    }

    public void setEquiptList(List<EmcEquiptBean> equiptList) {
        this.equiptList = equiptList;
    }

    public List<CoreFileBean> getCoreFileList() {
        return coreFileList;
    }

    public void setCoreFileList(List<CoreFileBean> coreFileList) {
        this.coreFileList = coreFileList;
    }

    public CoreOrgBean getCoreOrg() {
        return coreOrg;
    }

    public void setCoreOrg(CoreOrgBean coreOrg) {
        this.coreOrg = coreOrg;
    }

    public List<CoreCodeBean> getCoreCodeList() {
        return coreCodeList;
    }

    public void setCoreCodeList(List<CoreCodeBean> coreCodeList) {
        this.coreCodeList = coreCodeList;
    }

    public CrmOpportunityMainBean getOpportunityMain() {
        return opportunityMain;
    }

    public void setOpportunityMain(CrmOpportunityMainBean opportunityMain) {
        this.opportunityMain = opportunityMain;
    }

    public List<PfRunTempHumidityBean> getTempHumidityList() {
        return tempHumidityList;
    }

    public void setTempHumidityList(List<PfRunTempHumidityBean> tempHumidityList) {
        this.tempHumidityList = tempHumidityList;
    }

    public List<EmcIndexCategoryBean> getIndexCategoryList() {
        return indexCategoryList;
    }

    public void setIndexCategoryList(List<EmcIndexCategoryBean> indexCategoryList) {
        this.indexCategoryList = indexCategoryList;
    }

    public List<EmcQcRelatedOrderBean> getQcRelatedOrderList() {
        return qcRelatedOrderList;
    }

    public void setQcRelatedOrderList(List<EmcQcRelatedOrderBean> qcRelatedOrderList) {
        this.qcRelatedOrderList = qcRelatedOrderList;
    }

    public List<EmcItemQcIndexBean> getItemQcIndexList() {
        return itemQcIndexList;
    }

    public void setItemQcIndexList(List<EmcItemQcIndexBean> itemQcIndexList) {
        this.itemQcIndexList = itemQcIndexList;
    }
}
