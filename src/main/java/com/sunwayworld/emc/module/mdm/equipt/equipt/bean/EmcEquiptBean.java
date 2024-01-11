package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 容器类型
 *
 * @author magt@sunwayworld.com 2019/9/9
 *
 */
@Table("T_EMC_EQUIPT")
public class EmcEquiptBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = 1036700237775060656L;

    @Id
    private Long id; // 主键
    private String equiptId; // 站内编号
    private String equiptName; // 仪器名称
    private String equiptEnglishName; // 仪器英文名称
    private String specification; // 规格型号
    private String manufacturer; // 生产厂家
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate factoryDate; // 出厂日期
    private String factoryNo; // 出厂编号
    private String price; // 购置价格
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate; // 购买日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate; // 到货日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate openDate; // 开箱日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate; // 安装调试日期
    private String installUser; // 安装人员
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate acceptDate; // 验收日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate usedDate; // 投入使用日期
    private String usedDepartmentId; // 使用科室
    private String usedDepostId; // 使用岗位
    private String usedUserId; // 仪器使用人员ID
    private String usedUserName; // 仪器使用人员
    private String maintainUserId; // 维护人
    private String address; // 地点
    private String keepUserId; // 保管人
    private String state; // 仪器现状
    private String func; // 功能及应用范围
    private String tecIndex; // 主要技术指标
    private String testRange; // 测量范围
    private String accuracy; // 精确度
    private String status; // 仪器状态
    private Long equiptCategoryId; // 仪器类型
    private String installOrg; // 安装单位
    private String installPhone; // 安装联系电话
    private String serviceContactName; // 售后服务联系人
    private String servicePhone; // 售后服务联系电话
    private String sharing; // 是否共享
    private String maintain; // 需要维护
    private String sourceMethod; // 仪器溯源方式
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sourceDate; // 溯源有效期
    private String remark; // 备注
    private Long acqScriptId; // 采集脚本ID
    private String acqScriptIds; // 多采集脚本ID
    private String scriptDesc;// 脚本描述
    private String scriptDescs;// 多采集脚本描述
    private String interfaceCategory; // 接口类型 （下拉列表 ：文件采集）
    private String catalog; // 扫描文件夹 private String scriptCategory; // 脚本类型 下拉列表
                            // 通用配置、定制配置
    private String scriptCategory; // 脚本类型 下拉列表 通用配置、定制配置
    private String maintenanceUserId;// 维护人编码
    private String maintenanceUserName;// 维护人名称
    private String isOnStock;//是否在库

    private String orgId;//所属单位编码

    private String calibrationType; // 校准类型

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(String equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getEquiptEnglishName() {
        return equiptEnglishName;
    }

    public void setEquiptEnglishName(String equiptEnglishName) {
        this.equiptEnglishName = equiptEnglishName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getFactoryDate() {
        return factoryDate;
    }

    public void setFactoryDate(LocalDate factoryDate) {
        this.factoryDate = factoryDate;
    }

    public String getFactoryNo() {
        return factoryNo;
    }

    public void setFactoryNo(String factoryNo) {
        this.factoryNo = factoryNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getInstallDate() {
        return installDate;
    }

    public void setInstallDate(LocalDate installDate) {
        this.installDate = installDate;
    }

    public String getInstallUser() {
        return installUser;
    }

    public void setInstallUser(String installUser) {
        this.installUser = installUser;
    }

    public LocalDate getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(LocalDate acceptDate) {
        this.acceptDate = acceptDate;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }

    public String getUsedDepartmentId() {
        return usedDepartmentId;
    }

    public void setUsedDepartmentId(String usedDepartmentId) {
        this.usedDepartmentId = usedDepartmentId;
    }

    public String getUsedDepostId() {
        return usedDepostId;
    }

    public void setUsedDepostId(String usedDepostId) {
        this.usedDepostId = usedDepostId;
    }

    public String getUsedUserId() {
        return usedUserId;
    }

    public void setUsedUserId(String usedUserId) {
        this.usedUserId = usedUserId;
    }

    public String getUsedUserName() {
        return usedUserName;
    }

    public void setUsedUserName(String usedUserName) {
        this.usedUserName = usedUserName;
    }

    public String getMaintainUserId() {
        return maintainUserId;
    }

    public void setMaintainUserId(String maintainUserId) {
        this.maintainUserId = maintainUserId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeepUserId() {
        return keepUserId;
    }

    public void setKeepUserId(String keepUserId) {
        this.keepUserId = keepUserId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getTecIndex() {
        return tecIndex;
    }

    public void setTecIndex(String tecIndex) {
        this.tecIndex = tecIndex;
    }

    public String getTestRange() {
        return testRange;
    }

    public void setTestRange(String testRange) {
        this.testRange = testRange;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEquiptCategoryId() {
        return equiptCategoryId;
    }

    public void setEquiptCategoryId(Long equiptCategoryId) {
        this.equiptCategoryId = equiptCategoryId;
    }

    public String getInstallOrg() {
        return installOrg;
    }

    public void setInstallOrg(String installOrg) {
        this.installOrg = installOrg;
    }

    public String getInstallPhone() {
        return installPhone;
    }

    public void setInstallPhone(String installPhone) {
        this.installPhone = installPhone;
    }

    public String getServiceContactName() {
        return serviceContactName;
    }

    public void setServiceContactName(String serviceContactName) {
        this.serviceContactName = serviceContactName;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getSharing() {
        return sharing;
    }

    public void setSharing(String sharing) {
        this.sharing = sharing;
    }

    public String getMaintain() {
        return maintain;
    }

    public void setMaintain(String maintain) {
        this.maintain = maintain;
    }

    public String getSourceMethod() {
        return sourceMethod;
    }

    public void setSourceMethod(String sourceMethod) {
        this.sourceMethod = sourceMethod;
    }

    public LocalDate getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(LocalDate sourceDate) {
        this.sourceDate = sourceDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getAcqScriptId() {
        return acqScriptId;
    }

    public void setAcqScriptId(Long acqScriptId) {
        this.acqScriptId = acqScriptId;
    }

    public String getAcqScriptIds() {
        return acqScriptIds;
    }

    public void setAcqScriptIds(String acqScriptIds) {
        this.acqScriptIds = acqScriptIds;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getScriptDescs() {
        return scriptDescs;
    }

    public void setScriptDescs(String scriptDescs) {
        this.scriptDescs = scriptDescs;
    }

    public String getInterfaceCategory() {
        return interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        this.interfaceCategory = interfaceCategory;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getScriptCategory() {
        return scriptCategory;
    }

    public void setScriptCategory(String scriptCategory) {
        this.scriptCategory = scriptCategory;
    }

    public String getMaintenanceUserId() {
        return maintenanceUserId;
    }

    public void setMaintenanceUserId(String maintenanceUserId) {
        this.maintenanceUserId = maintenanceUserId;
    }

    public String getMaintenanceUserName() {
        return maintenanceUserName;
    }

    public void setMaintenanceUserName(String maintenanceUserName) {
        this.maintenanceUserName = maintenanceUserName;
    }

    public String getIsOnStock() {
        return isOnStock;
    }

    public void setIsOnStock(String isOnStock) {
        this.isOnStock = isOnStock;
    }

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

    public String getCalibrationType() {
        return calibrationType;
    }

    public void setCalibrationType(String calibrationType) {
        this.calibrationType = calibrationType;
    }
}
