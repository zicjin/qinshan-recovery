package com.sunwayworld.emc.module.mdm.emp.emp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 人员
 *
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Table("T_EMC_EMP")
public class EmcEmpBean extends AbstractInsertable<String> implements Insertable<String> {

    @Transient
    private static final long serialVersionUID = 2589237232985971477L;

    @Id
    private String id; // 主键
    private String userName; // 姓名
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 出生日期
    private Integer age; // 年龄
    private String mobile; // 手机
    private String sex; // 性别
    private String filterTest; // 是否过滤测试
    private String phone; // 联系电话
    private String nation; // 民族
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startWorkDate; // 参加工作时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate titleDate; // 职称聘用时间
    private String professionalTitle; // 专业技术职称
    private String position; // 职务
    private String remark; // 备注
    private String electronicSignature; // 电子签名
    private String pic; // 照片
    private String onDuty; // 在岗状态

    private String editReport; // 是否为报告编制人
    private String orderQcUser; // 是否为样品接收质控人
    private String projectQcUser; // 是否为项目质控人
    private String isEquiptManager;//是否为仪器管理员

    private String quoter; // 是否为报价员

    @JSONField(serializeUsing = ToStringSerializer.class)
    private Long erpGbId; // ERP GBID
    private String erpJobNumber; // ERP 工号

    private String createdById; // 制单人编码
    private String createdByName; // 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间
    private String createdByOrgId; // 制单人单位编码
    private String createdByOrgName; // 制单人单位名称

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getFilterTest() {
        return filterTest;
    }
    public void setFilterTest(String filterTest) {
        this.filterTest = filterTest;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getNation() {
        return nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public LocalDate getStartWorkDate() {
        return startWorkDate;
    }
    public void setStartWorkDate(LocalDate startWorkDate) {
        this.startWorkDate = startWorkDate;
    }
    public LocalDate getTitleDate() {
        return titleDate;
    }
    public void setTitleDate(LocalDate titleDate) {
        this.titleDate = titleDate;
    }
    public String getProfessionalTitle() {
        return professionalTitle;
    }
    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getElectronicSignature() {
        return electronicSignature;
    }
    public void setElectronicSignature(String electronicSignature) {
        this.electronicSignature = electronicSignature;
    }
    public String getPic() {
        return pic;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getOnDuty() {
        return onDuty;
    }
    public void setOnDuty(String onDuty) {
        this.onDuty = onDuty;
    }

    public String getEditReport() {
        return editReport;
    }

    public void setEditReport(String editReport) {
        this.editReport = editReport;
    }

    public String getOrderQcUser() {
        return orderQcUser;
    }

    public void setOrderQcUser(String orderQcUser) {
        this.orderQcUser = orderQcUser;
    }

    public String getProjectQcUser() {
        return projectQcUser;
    }

    public void setProjectQcUser(String projectQcUser) {
        this.projectQcUser = projectQcUser;
    }

    public String getIsEquiptManager() {
        return isEquiptManager;
    }

    public void setIsEquiptManager(String isEquiptManager) {
        this.isEquiptManager = isEquiptManager;
    }

    public String getQuoter() {
        return quoter;
    }

    public void setQuoter(String quoter) {
        this.quoter = quoter;
    }

    public Long getErpGbId() {
        return erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
    }

    public String getErpJobNumber() {
        return erpJobNumber;
    }

    public void setErpJobNumber(String erpJobNumber) {
        this.erpJobNumber = erpJobNumber;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
