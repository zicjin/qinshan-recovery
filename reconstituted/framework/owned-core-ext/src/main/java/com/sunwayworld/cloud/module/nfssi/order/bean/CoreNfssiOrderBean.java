/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_NFSSI_ORDER")
public class CoreNfssiOrderBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private String id;
    private String back;
    private String samplingPlaceOrgName;
    private String samplingPlace;
    private String province;
    private String city;
    private String county;
    private String samplingUnit;
    private String address;
    private String legalPerson;
    private String postcode;
    private String contact;
    private String contactMobile;
    private String productionLicenseNo;
    private String sampleName;
    private Double samplingQty;
    private String firstCategory;
    private String secondCategory;
    private String thirdCategory;
    private String fourthCategory;
    private String sampleSource;
    private String annualSales;
    private String samplingMethod;
    private String samplingInstrument;
    private String specification;
    private String sampleBatchNo;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate productDate;
    private String qualityGuaranteePeriod;
    private String samplingKeepCondition;
    private String samplePack;
    private String remark;
    private String samplingOrgName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate samplingDate;
    private String samplingMobile;
    private String samplingOrgContact;
    private String testPurpose;
    private String sampleShap;
    private String sampleCategory;
    private String packageCategory;
    private String manufacturer;
    private String manufacturerAdress;
    private String samplingTache;
    private String executiveStandard;
    private String qualityLevel;
    private String sampleLogo;
    private String manufacturerContact;
    private String manufacturerMobile;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate receivedDate;
    private String areaCategory;
    private String manufacturerProvince;
    private String sampleProperty;
    private String price;
    private String samplingBaseNum;
    private String spareNum;
    private String samplingOrgAdress;
    private String samplingOrgPostCode;
    private String samplingOrgFax;
    private String testPlaceCreditCode;
    private String manufacturerCity;
    private String manufacturerCounty;
    private String sampleBarCode;
    private String taskSource;
    private String testPlaceFax;
    private String entrustContact;
    private String entrustProductionLicenseNo;
    private String entrustEnterpriseAdress;
    private String entrustEnterprise;
    private String importation;
    private String hasEntrustEnterpriseInfo;
    private String entrustPhone;
    private String entrustProvince;
    private String entrustCity;
    private String entrustCountry;
    private String originPlace;
    private String licenseType;
    private String licenseNo;
    private String barCode;
    private String rainbowCode;
    private String rainbowCodePath;
    private String approvalNo;
    @Clob
    private String funcCategoryCollection;
    private String inspectOrgName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate inspectionDate;
    private String inspectionOrgContact;
    private String inspectOrgContactMobile;
    private String inspectOrgContactEmail;
    private String inspectSealPerson;
    private String inspectSealPersonMobile;
    private String inspectSealPersonEmail;
    private String sampler;
    private String samplerMobile;
    private String samplerEmail;
    private String samplingOrgLevel;
    @Clob
    private String samplingOrgProvince;
    private String sampleUrl;
    private String samplingPicPath;
    private String networkLicenseNo;
    private String icpNo;
    private String networkPlatformProvince;
    private String networkPlatformCity;
    private String networkPlatformCounty;
    private String networkPlatformAdress;
    private String notificationPicPath;
    private String networkPlatformContact;
    private String networkPlatformContactMobile;
    private String networkOrderNo;
    private String submit;
    private String onlineShopName;
    private String dateCategory;
    private String samplingCategory;
    private String entrustProperty;
    private String reportTaskB;
    private String reportTaskA;
    private String testState;
    private String sampleType;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nffsiUpdatedTime;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedTime;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getSamplingPlaceOrgName() {
        return this.samplingPlaceOrgName;
    }

    public void setSamplingPlaceOrgName(String samplingPlaceOrgName) {
        this.samplingPlaceOrgName = samplingPlaceOrgName;
    }

    public String getSamplingPlace() {
        return this.samplingPlace;
    }

    public void setSamplingPlace(String samplingPlace) {
        this.samplingPlace = samplingPlace;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return this.county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSamplingUnit() {
        return this.samplingUnit;
    }

    public void setSamplingUnit(String samplingUnit) {
        this.samplingUnit = samplingUnit;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLegalPerson() {
        return this.legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactMobile() {
        return this.contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getProductionLicenseNo() {
        return this.productionLicenseNo;
    }

    public void setProductionLicenseNo(String productionLicenseNo) {
        this.productionLicenseNo = productionLicenseNo;
    }

    public String getSampleName() {
        return this.sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public Double getSamplingQty() {
        return this.samplingQty;
    }

    public void setSamplingQty(Double samplingQty) {
        this.samplingQty = samplingQty;
    }

    public String getFirstCategory() {
        return this.firstCategory;
    }

    public void setFirstCategory(String firstCategory) {
        this.firstCategory = firstCategory;
    }

    public String getSecondCategory() {
        return this.secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String getThirdCategory() {
        return this.thirdCategory;
    }

    public void setThirdCategory(String thirdCategory) {
        this.thirdCategory = thirdCategory;
    }

    public String getFourthCategory() {
        return this.fourthCategory;
    }

    public void setFourthCategory(String fourthCategory) {
        this.fourthCategory = fourthCategory;
    }

    public String getSampleSource() {
        return this.sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    public String getAnnualSales() {
        return this.annualSales;
    }

    public void setAnnualSales(String annualSales) {
        this.annualSales = annualSales;
    }

    public String getSamplingMethod() {
        return this.samplingMethod;
    }

    public void setSamplingMethod(String samplingMethod) {
        this.samplingMethod = samplingMethod;
    }

    public String getSamplingInstrument() {
        return this.samplingInstrument;
    }

    public void setSamplingInstrument(String samplingInstrument) {
        this.samplingInstrument = samplingInstrument;
    }

    public String getSpecification() {
        return this.specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSampleBatchNo() {
        return this.sampleBatchNo;
    }

    public void setSampleBatchNo(String sampleBatchNo) {
        this.sampleBatchNo = sampleBatchNo;
    }

    public LocalDate getProductDate() {
        return this.productDate;
    }

    public void setProductDate(LocalDate productDate) {
        this.productDate = productDate;
    }

    public String getQualityGuaranteePeriod() {
        return this.qualityGuaranteePeriod;
    }

    public void setQualityGuaranteePeriod(String qualityGuaranteePeriod) {
        this.qualityGuaranteePeriod = qualityGuaranteePeriod;
    }

    public String getSamplingKeepCondition() {
        return this.samplingKeepCondition;
    }

    public void setSamplingKeepCondition(String samplingKeepCondition) {
        this.samplingKeepCondition = samplingKeepCondition;
    }

    public String getSamplePack() {
        return this.samplePack;
    }

    public void setSamplePack(String samplePack) {
        this.samplePack = samplePack;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSamplingOrgName() {
        return this.samplingOrgName;
    }

    public void setSamplingOrgName(String samplingOrgName) {
        this.samplingOrgName = samplingOrgName;
    }

    public LocalDate getSamplingDate() {
        return this.samplingDate;
    }

    public void setSamplingDate(LocalDate samplingDate) {
        this.samplingDate = samplingDate;
    }

    public String getSamplingMobile() {
        return this.samplingMobile;
    }

    public void setSamplingMobile(String samplingMobile) {
        this.samplingMobile = samplingMobile;
    }

    public String getSamplingOrgContact() {
        return this.samplingOrgContact;
    }

    public void setSamplingOrgContact(String samplingOrgContact) {
        this.samplingOrgContact = samplingOrgContact;
    }

    public String getTestPurpose() {
        return this.testPurpose;
    }

    public void setTestPurpose(String testPurpose) {
        this.testPurpose = testPurpose;
    }

    public String getSampleShap() {
        return this.sampleShap;
    }

    public void setSampleShap(String sampleShap) {
        this.sampleShap = sampleShap;
    }

    public String getSampleCategory() {
        return this.sampleCategory;
    }

    public void setSampleCategory(String sampleCategory) {
        this.sampleCategory = sampleCategory;
    }

    public String getPackageCategory() {
        return this.packageCategory;
    }

    public void setPackageCategory(String packageCategory) {
        this.packageCategory = packageCategory;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerAdress() {
        return this.manufacturerAdress;
    }

    public void setManufacturerAdress(String manufacturerAdress) {
        this.manufacturerAdress = manufacturerAdress;
    }

    public String getSamplingTache() {
        return this.samplingTache;
    }

    public void setSamplingTache(String samplingTache) {
        this.samplingTache = samplingTache;
    }

    public String getExecutiveStandard() {
        return this.executiveStandard;
    }

    public void setExecutiveStandard(String executiveStandard) {
        this.executiveStandard = executiveStandard;
    }

    public String getQualityLevel() {
        return this.qualityLevel;
    }

    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }

    public String getSampleLogo() {
        return this.sampleLogo;
    }

    public void setSampleLogo(String sampleLogo) {
        this.sampleLogo = sampleLogo;
    }

    public String getManufacturerContact() {
        return this.manufacturerContact;
    }

    public void setManufacturerContact(String manufacturerContact) {
        this.manufacturerContact = manufacturerContact;
    }

    public String getManufacturerMobile() {
        return this.manufacturerMobile;
    }

    public void setManufacturerMobile(String manufacturerMobile) {
        this.manufacturerMobile = manufacturerMobile;
    }

    public LocalDate getReceivedDate() {
        return this.receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getAreaCategory() {
        return this.areaCategory;
    }

    public void setAreaCategory(String areaCategory) {
        this.areaCategory = areaCategory;
    }

    public String getManufacturerProvince() {
        return this.manufacturerProvince;
    }

    public void setManufacturerProvince(String manufacturerProvince) {
        this.manufacturerProvince = manufacturerProvince;
    }

    public String getSampleProperty() {
        return this.sampleProperty;
    }

    public void setSampleProperty(String sampleProperty) {
        this.sampleProperty = sampleProperty;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSamplingBaseNum() {
        return this.samplingBaseNum;
    }

    public void setSamplingBaseNum(String samplingBaseNum) {
        this.samplingBaseNum = samplingBaseNum;
    }

    public String getSpareNum() {
        return this.spareNum;
    }

    public void setSpareNum(String spareNum) {
        this.spareNum = spareNum;
    }

    public String getSamplingOrgAdress() {
        return this.samplingOrgAdress;
    }

    public void setSamplingOrgAdress(String samplingOrgAdress) {
        this.samplingOrgAdress = samplingOrgAdress;
    }

    public String getSamplingOrgPostCode() {
        return this.samplingOrgPostCode;
    }

    public void setSamplingOrgPostCode(String samplingOrgPostCode) {
        this.samplingOrgPostCode = samplingOrgPostCode;
    }

    public String getSamplingOrgFax() {
        return this.samplingOrgFax;
    }

    public void setSamplingOrgFax(String samplingOrgFax) {
        this.samplingOrgFax = samplingOrgFax;
    }

    public String getTestPlaceCreditCode() {
        return this.testPlaceCreditCode;
    }

    public void setTestPlaceCreditCode(String testPlaceCreditCode) {
        this.testPlaceCreditCode = testPlaceCreditCode;
    }

    public String getManufacturerCity() {
        return this.manufacturerCity;
    }

    public void setManufacturerCity(String manufacturerCity) {
        this.manufacturerCity = manufacturerCity;
    }

    public String getManufacturerCounty() {
        return this.manufacturerCounty;
    }

    public void setManufacturerCounty(String manufacturerCounty) {
        this.manufacturerCounty = manufacturerCounty;
    }

    public String getSampleBarCode() {
        return this.sampleBarCode;
    }

    public void setSampleBarCode(String sampleBarCode) {
        this.sampleBarCode = sampleBarCode;
    }

    public String getTaskSource() {
        return this.taskSource;
    }

    public void setTaskSource(String taskSource) {
        this.taskSource = taskSource;
    }

    public String getTestPlaceFax() {
        return this.testPlaceFax;
    }

    public void setTestPlaceFax(String testPlaceFax) {
        this.testPlaceFax = testPlaceFax;
    }

    public String getEntrustContact() {
        return this.entrustContact;
    }

    public void setEntrustContact(String entrustContact) {
        this.entrustContact = entrustContact;
    }

    public String getEntrustProductionLicenseNo() {
        return this.entrustProductionLicenseNo;
    }

    public void setEntrustProductionLicenseNo(String entrustProductionLicenseNo) {
        this.entrustProductionLicenseNo = entrustProductionLicenseNo;
    }

    public String getEntrustEnterpriseAdress() {
        return this.entrustEnterpriseAdress;
    }

    public void setEntrustEnterpriseAdress(String entrustEnterpriseAdress) {
        this.entrustEnterpriseAdress = entrustEnterpriseAdress;
    }

    public String getEntrustEnterprise() {
        return this.entrustEnterprise;
    }

    public void setEntrustEnterprise(String entrustEnterprise) {
        this.entrustEnterprise = entrustEnterprise;
    }

    public String getImportation() {
        return this.importation;
    }

    public void setImportation(String importation) {
        this.importation = importation;
    }

    public String getHasEntrustEnterpriseInfo() {
        return this.hasEntrustEnterpriseInfo;
    }

    public void setHasEntrustEnterpriseInfo(String hasEntrustEnterpriseInfo) {
        this.hasEntrustEnterpriseInfo = hasEntrustEnterpriseInfo;
    }

    public String getEntrustPhone() {
        return this.entrustPhone;
    }

    public void setEntrustPhone(String entrustPhone) {
        this.entrustPhone = entrustPhone;
    }

    public String getEntrustProvince() {
        return this.entrustProvince;
    }

    public void setEntrustProvince(String entrustProvince) {
        this.entrustProvince = entrustProvince;
    }

    public String getEntrustCity() {
        return this.entrustCity;
    }

    public void setEntrustCity(String entrustCity) {
        this.entrustCity = entrustCity;
    }

    public String getEntrustCountry() {
        return this.entrustCountry;
    }

    public void setEntrustCountry(String entrustCountry) {
        this.entrustCountry = entrustCountry;
    }

    public String getOriginPlace() {
        return this.originPlace;
    }

    public void setOriginPlace(String originPlace) {
        this.originPlace = originPlace;
    }

    public String getLicenseType() {
        return this.licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseNo() {
        return this.licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getRainbowCode() {
        return this.rainbowCode;
    }

    public void setRainbowCode(String rainbowCode) {
        this.rainbowCode = rainbowCode;
    }

    public String getRainbowCodePath() {
        return this.rainbowCodePath;
    }

    public void setRainbowCodePath(String rainbowCodePath) {
        this.rainbowCodePath = rainbowCodePath;
    }

    public String getApprovalNo() {
        return this.approvalNo;
    }

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    public String getFuncCategoryCollection() {
        return this.funcCategoryCollection;
    }

    public void setFuncCategoryCollection(String funcCategoryCollection) {
        this.funcCategoryCollection = funcCategoryCollection;
    }

    public String getInspectOrgName() {
        return this.inspectOrgName;
    }

    public void setInspectOrgName(String inspectOrgName) {
        this.inspectOrgName = inspectOrgName;
    }

    public LocalDate getInspectionDate() {
        return this.inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionOrgContact() {
        return this.inspectionOrgContact;
    }

    public void setInspectionOrgContact(String inspectionOrgContact) {
        this.inspectionOrgContact = inspectionOrgContact;
    }

    public String getInspectOrgContactMobile() {
        return this.inspectOrgContactMobile;
    }

    public void setInspectOrgContactMobile(String inspectOrgContactMobile) {
        this.inspectOrgContactMobile = inspectOrgContactMobile;
    }

    public String getInspectOrgContactEmail() {
        return this.inspectOrgContactEmail;
    }

    public void setInspectOrgContactEmail(String inspectOrgContactEmail) {
        this.inspectOrgContactEmail = inspectOrgContactEmail;
    }

    public String getInspectSealPerson() {
        return this.inspectSealPerson;
    }

    public void setInspectSealPerson(String inspectSealPerson) {
        this.inspectSealPerson = inspectSealPerson;
    }

    public String getInspectSealPersonMobile() {
        return this.inspectSealPersonMobile;
    }

    public void setInspectSealPersonMobile(String inspectSealPersonMobile) {
        this.inspectSealPersonMobile = inspectSealPersonMobile;
    }

    public String getInspectSealPersonEmail() {
        return this.inspectSealPersonEmail;
    }

    public void setInspectSealPersonEmail(String inspectSealPersonEmail) {
        this.inspectSealPersonEmail = inspectSealPersonEmail;
    }

    public String getSampler() {
        return this.sampler;
    }

    public void setSampler(String sampler) {
        this.sampler = sampler;
    }

    public String getSamplerMobile() {
        return this.samplerMobile;
    }

    public void setSamplerMobile(String samplerMobile) {
        this.samplerMobile = samplerMobile;
    }

    public String getSamplerEmail() {
        return this.samplerEmail;
    }

    public void setSamplerEmail(String samplerEmail) {
        this.samplerEmail = samplerEmail;
    }

    public String getSamplingOrgLevel() {
        return this.samplingOrgLevel;
    }

    public void setSamplingOrgLevel(String samplingOrgLevel) {
        this.samplingOrgLevel = samplingOrgLevel;
    }

    public String getSamplingOrgProvince() {
        return this.samplingOrgProvince;
    }

    public void setSamplingOrgProvince(String samplingOrgProvince) {
        this.samplingOrgProvince = samplingOrgProvince;
    }

    public String getSampleUrl() {
        return this.sampleUrl;
    }

    public void setSampleUrl(String sampleUrl) {
        this.sampleUrl = sampleUrl;
    }

    public String getSamplingPicPath() {
        return this.samplingPicPath;
    }

    public void setSamplingPicPath(String samplingPicPath) {
        this.samplingPicPath = samplingPicPath;
    }

    public String getNetworkLicenseNo() {
        return this.networkLicenseNo;
    }

    public void setNetworkLicenseNo(String networkLicenseNo) {
        this.networkLicenseNo = networkLicenseNo;
    }

    public String getIcpNo() {
        return this.icpNo;
    }

    public void setIcpNo(String icpNo) {
        this.icpNo = icpNo;
    }

    public String getNetworkPlatformProvince() {
        return this.networkPlatformProvince;
    }

    public void setNetworkPlatformProvince(String networkPlatformProvince) {
        this.networkPlatformProvince = networkPlatformProvince;
    }

    public String getNetworkPlatformCity() {
        return this.networkPlatformCity;
    }

    public void setNetworkPlatformCity(String networkPlatformCity) {
        this.networkPlatformCity = networkPlatformCity;
    }

    public String getNetworkPlatformCounty() {
        return this.networkPlatformCounty;
    }

    public void setNetworkPlatformCounty(String networkPlatformCounty) {
        this.networkPlatformCounty = networkPlatformCounty;
    }

    public String getNetworkPlatformAdress() {
        return this.networkPlatformAdress;
    }

    public void setNetworkPlatformAdress(String networkPlatformAdress) {
        this.networkPlatformAdress = networkPlatformAdress;
    }

    public String getNotificationPicPath() {
        return this.notificationPicPath;
    }

    public void setNotificationPicPath(String notificationPicPath) {
        this.notificationPicPath = notificationPicPath;
    }

    public String getNetworkPlatformContact() {
        return this.networkPlatformContact;
    }

    public void setNetworkPlatformContact(String networkPlatformContact) {
        this.networkPlatformContact = networkPlatformContact;
    }

    public String getNetworkPlatformContactMobile() {
        return this.networkPlatformContactMobile;
    }

    public void setNetworkPlatformContactMobile(String networkPlatformContactMobile) {
        this.networkPlatformContactMobile = networkPlatformContactMobile;
    }

    public String getNetworkOrderNo() {
        return this.networkOrderNo;
    }

    public void setNetworkOrderNo(String networkOrderNo) {
        this.networkOrderNo = networkOrderNo;
    }

    public String getSubmit() {
        return this.submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getOnlineShopName() {
        return this.onlineShopName;
    }

    public void setOnlineShopName(String onlineShopName) {
        this.onlineShopName = onlineShopName;
    }

    public String getDateCategory() {
        return this.dateCategory;
    }

    public void setDateCategory(String dateCategory) {
        this.dateCategory = dateCategory;
    }

    public String getSamplingCategory() {
        return this.samplingCategory;
    }

    public void setSamplingCategory(String samplingCategory) {
        this.samplingCategory = samplingCategory;
    }

    public String getEntrustProperty() {
        return this.entrustProperty;
    }

    public void setEntrustProperty(String entrustProperty) {
        this.entrustProperty = entrustProperty;
    }

    public String getReportTaskB() {
        return this.reportTaskB;
    }

    public void setReportTaskB(String reportTaskB) {
        this.reportTaskB = reportTaskB;
    }

    public String getReportTaskA() {
        return this.reportTaskA;
    }

    public void setReportTaskA(String reportTaskA) {
        this.reportTaskA = reportTaskA;
    }

    public String getTestState() {
        return this.testState;
    }

    public void setTestState(String testState) {
        this.testState = testState;
    }

    public String getSampleType() {
        return this.sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public LocalDateTime getNffsiUpdatedTime() {
        return this.nffsiUpdatedTime;
    }

    public void setNffsiUpdatedTime(LocalDateTime nffsiUpdatedTime) {
        this.nffsiUpdatedTime = nffsiUpdatedTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getBack() {
        return this.back;
    }

    public void setBack(String back) {
        this.back = back;
    }
}
