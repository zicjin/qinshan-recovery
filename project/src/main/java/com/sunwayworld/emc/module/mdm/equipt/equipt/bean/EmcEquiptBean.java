package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT")
public class EmcEquiptBean
extends AbstractInsertable<Long> {
    @Transient
    private static final long serialVersionUID = 1036700237775060656L;
    @Id
    private Long id;
    private String equiptId;
    private String equiptName;
    private String equiptEnglishName;
    private String specification;
    private String manufacturer;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate factoryDate;
    private String factoryNo;
    private String price;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate purchaseDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate arrivalDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate openDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate installDate;
    private String installUser;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate acceptDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate usedDate;
    private String usedDepartmentId;
    private String usedDepostId;
    private String usedUserId;
    private String usedUserName;
    private String maintainUserId;
    private String address;
    private String keepUserId;
    private String state;
    private String func;
    private String tecIndex;
    private String testRange;
    private String accuracy;
    private String status;
    private Long equiptCategoryId;
    private String installOrg;
    private String installPhone;
    private String serviceContactName;
    private String servicePhone;
    private String sharing;
    private String maintain;
    private String sourceMethod;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate sourceDate;
    @NotNull(defaultValue="\u81ea\u6709")
    private String equiptBelong;
    private String remark;
    private Long acqScriptId;
    private String acqScriptIds;
    private String scriptDesc;
    private String scriptDescs;
    private String interfaceCategory;
    private String catalog;
    private String scriptCategory;
    private String maintenanceUserId;
    private String maintenanceUserName;
    private String isOnStock;
    private String orgId;
    private String calibrationType;
    @NotNull(defaultValue="1")
    private String equiptRecord;
    private String sceneEquiptNo;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquiptId() {
        return this.equiptId;
    }

    public void setEquiptId(String equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptName() {
        return this.equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getEquiptEnglishName() {
        return this.equiptEnglishName;
    }

    public void setEquiptEnglishName(String equiptEnglishName) {
        this.equiptEnglishName = equiptEnglishName;
    }

    public String getSpecification() {
        return this.specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getFactoryDate() {
        return this.factoryDate;
    }

    public void setFactoryDate(LocalDate factoryDate) {
        this.factoryDate = factoryDate;
    }

    public String getFactoryNo() {
        return this.factoryNo;
    }

    public void setFactoryNo(String factoryNo) {
        this.factoryNo = factoryNo;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getPurchaseDate() {
        return this.purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getArrivalDate() {
        return this.arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getOpenDate() {
        return this.openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getInstallDate() {
        return this.installDate;
    }

    public void setInstallDate(LocalDate installDate) {
        this.installDate = installDate;
    }

    public String getInstallUser() {
        return this.installUser;
    }

    public void setInstallUser(String installUser) {
        this.installUser = installUser;
    }

    public LocalDate getAcceptDate() {
        return this.acceptDate;
    }

    public void setAcceptDate(LocalDate acceptDate) {
        this.acceptDate = acceptDate;
    }

    public LocalDate getUsedDate() {
        return this.usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }

    public String getUsedDepartmentId() {
        return this.usedDepartmentId;
    }

    public void setUsedDepartmentId(String usedDepartmentId) {
        this.usedDepartmentId = usedDepartmentId;
    }

    public String getUsedDepostId() {
        return this.usedDepostId;
    }

    public void setUsedDepostId(String usedDepostId) {
        this.usedDepostId = usedDepostId;
    }

    public String getUsedUserId() {
        return this.usedUserId;
    }

    public void setUsedUserId(String usedUserId) {
        this.usedUserId = usedUserId;
    }

    public String getUsedUserName() {
        return this.usedUserName;
    }

    public void setUsedUserName(String usedUserName) {
        this.usedUserName = usedUserName;
    }

    public String getMaintainUserId() {
        return this.maintainUserId;
    }

    public void setMaintainUserId(String maintainUserId) {
        this.maintainUserId = maintainUserId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeepUserId() {
        return this.keepUserId;
    }

    public void setKeepUserId(String keepUserId) {
        this.keepUserId = keepUserId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFunc() {
        return this.func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getTecIndex() {
        return this.tecIndex;
    }

    public void setTecIndex(String tecIndex) {
        this.tecIndex = tecIndex;
    }

    public String getTestRange() {
        return this.testRange;
    }

    public void setTestRange(String testRange) {
        this.testRange = testRange;
    }

    public String getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEquiptCategoryId() {
        return this.equiptCategoryId;
    }

    public void setEquiptCategoryId(Long equiptCategoryId) {
        this.equiptCategoryId = equiptCategoryId;
    }

    public String getInstallOrg() {
        return this.installOrg;
    }

    public void setInstallOrg(String installOrg) {
        this.installOrg = installOrg;
    }

    public String getInstallPhone() {
        return this.installPhone;
    }

    public void setInstallPhone(String installPhone) {
        this.installPhone = installPhone;
    }

    public String getServiceContactName() {
        return this.serviceContactName;
    }

    public void setServiceContactName(String serviceContactName) {
        this.serviceContactName = serviceContactName;
    }

    public String getServicePhone() {
        return this.servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getSharing() {
        return this.sharing;
    }

    public void setSharing(String sharing) {
        this.sharing = sharing;
    }

    public String getMaintain() {
        return this.maintain;
    }

    public void setMaintain(String maintain) {
        this.maintain = maintain;
    }

    public String getSourceMethod() {
        return this.sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public LocalDate getSourceDate() {
        return this.sourceDate;
    }

    public void setSourceDate(LocalDate sourceDate) {
        this.sourceDate = sourceDate;
    }

    public String getEquiptBelong() {
        return this.equiptBelong;
    }

    public void setEquiptBelong(String equiptBelong) {
        this.equiptBelong = equiptBelong;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getAcqScriptId() {
        return this.acqScriptId;
    }

    public void setAcqScriptId(Long acqScriptId) {
        this.acqScriptId = acqScriptId;
    }

    public String getAcqScriptIds() {
        return this.acqScriptIds;
    }

    public void setAcqScriptIds(String acqScriptIds) {
        this.acqScriptIds = acqScriptIds;
    }

    public String getScriptDesc() {
        return this.scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getScriptDescs() {
        return this.scriptDescs;
    }

    public void setScriptDescs(String scriptDescs) {
        this.scriptDescs = scriptDescs;
    }

    public String getInterfaceCategory() {
        return this.interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        this.interfaceCategory = interfaceCategory;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getScriptCategory() {
        return this.scriptCategory;
    }

    public void setScriptCategory(String scriptCategory) {
        this.scriptCategory = scriptCategory;
    }

    public String getMaintenanceUserId() {
        return this.maintenanceUserId;
    }

    public void setMaintenanceUserId(String maintenanceUserId) {
        this.maintenanceUserId = maintenanceUserId;
    }

    public String getMaintenanceUserName() {
        return this.maintenanceUserName;
    }

    public void setMaintenanceUserName(String maintenanceUserName) {
        this.maintenanceUserName = maintenanceUserName;
    }

    public String getIsOnStock() {
        return this.isOnStock;
    }

    public void setIsOnStock(String isOnStock) {
        this.isOnStock = isOnStock;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCalibrationType() {
        return this.calibrationType;
    }

    public void setCalibrationType(String calibrationType) {
        this.calibrationType = calibrationType;
    }

    public String getEquiptRecord() {
        return this.equiptRecord;
    }

    public void setEquiptRecord(String equiptRecord) {
        this.equiptRecord = equiptRecord;
    }

    public String getSceneEquiptNo() {
        return this.sceneEquiptNo;
    }

    public void setSceneEquiptNo(String sceneEquiptNo) {
        this.sceneEquiptNo = sceneEquiptNo;
    }
}
