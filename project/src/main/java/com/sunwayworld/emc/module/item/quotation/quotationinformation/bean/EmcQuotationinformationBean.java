package com.sunwayworld.emc.module.item.quotation.quotationinformation.bean;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

/**
 * 报价单信息表 QUOTATIONREGISTERID
 * 
 * @author xiongjie@sunwayworld.com@sunwayworld.com
 * @date 2021-03-25
 */
@Table("T_EMC_QUOTATION_INFORMATION")
public class EmcQuotationinformationBean extends AbstractPersistable<Long> implements Persistable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private Long quotationRegisterId;// 报价单ID
    private String type;// 类型
    private String monitorPointName;// 点位名称
    private String testName;// 检测项目
    private String testMethodName;// 检测方法标准
    private String testId;// 测试id
    private String methodId;// 方法id
    private String monitorTmplName;// 样品类型
    private String testSamplePrice;// 检测采样单价
    private String testAnalysisPrice;// 检测分析单价
    private String testSamplePriceAfterCount;// 折扣后采样单价
    private String testSampleDiscount;// 采样折扣
    private String testAnalysisPriceAfterCount;// 折扣后分析单价
    private String testAnalysisDiscount;// 分析折扣
    private String testDiscount;// 检测折扣
    private String testMonitorNum;// 检测点位个数
    private String testDayNum;// 检测天数
    private String testFrequency;// 检测频次
    private String addPriceName;// 附加费用名称
    private String addUnitPrice;// 附加单价
    private String addNumber;// 附加数量
    private String addPrice;// 附加费用
    private String totalAnalysisPrice;// 分析总价
    private String totalSamplePrice;// 采样总价
    private String totalTestPrice;// 检测费用原价
    private String totalTestPriceAfterCount;// 检测费用优惠价
    private String totalPriceAfterTotalCount;// 总价打折后价格
    private String totalAddPrice;// 附加费用总价
    private String totalPrice;// 报价总计
    private String totalPriceAfterTax;// 税后总价
    private String totalDiscount;// 总价折扣
    private String totalTaxAmount;// 税额
    private String totalCost;// 成本
    private String totalProfit;// 利润
    private String remark;//备注(新锐)
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    public String getTestSamplePrice() {
        return testSamplePrice;
    }

    public void setTestSamplePrice(String testSamplePrice) {
        this.testSamplePrice = testSamplePrice;
    }

    public String getTestAnalysisPrice() {
        return testAnalysisPrice;
    }

    public void setTestAnalysisPrice(String testAnalysisPrice) {
        this.testAnalysisPrice = testAnalysisPrice;
    }

    public String getTestDiscount() {
        return testDiscount;
    }

    public void setTestDiscount(String testDiscount) {
        this.testDiscount = testDiscount;
    }

    public String getTestMonitorNum() {
        return testMonitorNum;
    }

    public void setTestMonitorNum(String testMonitorNum) {
        this.testMonitorNum = testMonitorNum;
    }

    public String getTestDayNum() {
        return testDayNum;
    }

    public void setTestDayNum(String testDayNum) {
        this.testDayNum = testDayNum;
    }

    public String getTestFrequency() {
        return testFrequency;
    }

    public void setTestFrequency(String testFrequency) {
        this.testFrequency = testFrequency;
    }

    public String getAddPriceName() {
        return addPriceName;
    }

    public void setAddPriceName(String addPriceName) {
        this.addPriceName = addPriceName;
    }

    public String getAddUnitPrice() {
        return addUnitPrice;
    }

    public void setAddUnitPrice(String addUnitPrice) {
        this.addUnitPrice = addUnitPrice;
    }

    public String getAddNumber() {
        return addNumber;
    }

    public void setAddNumber(String addNumber) {
        this.addNumber = addNumber;
    }

    public String getAddPrice() {
        return addPrice;
    }

    public void setAddPrice(String addPrice) {
        this.addPrice = addPrice;
    }

    public String getTotalAnalysisPrice() {
        return totalAnalysisPrice;
    }

    public void setTotalAnalysisPrice(String totalAnalysisPrice) {
        this.totalAnalysisPrice = totalAnalysisPrice;
    }

    public String getTotalSamplePrice() {
        return totalSamplePrice;
    }

    public void setTotalSamplePrice(String totalSamplePrice) {
        this.totalSamplePrice = totalSamplePrice;
    }

    public String getTotalAddPrice() {
        return totalAddPrice;
    }

    public void setTotalAddPrice(String totalAddPrice) {
        this.totalAddPrice = totalAddPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(String totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getQuotationRegisterId() {
        return quotationRegisterId;
    }

    public void setQuotationRegisterId(Long quotationRegisterId) {
        this.quotationRegisterId = quotationRegisterId;
    }

    public String getMonitorTmplName() {
        return monitorTmplName;
    }

    public void setMonitorTmplName(String monitorTmplName) {
        this.monitorTmplName = monitorTmplName;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getTestSamplePriceAfterCount() {
        return testSamplePriceAfterCount;
    }

    public void setTestSamplePriceAfterCount(String testSamplePriceAfterCount) {
        this.testSamplePriceAfterCount = testSamplePriceAfterCount;
    }

    public String getTestSampleDiscount() {
        return testSampleDiscount;
    }

    public void setTestSampleDiscount(String testSampleDiscount) {
        this.testSampleDiscount = testSampleDiscount;
    }

    public String getTestAnalysisPriceAfterCount() {
        return testAnalysisPriceAfterCount;
    }

    public void setTestAnalysisPriceAfterCount(String testAnalysisPriceAfterCount) {
        this.testAnalysisPriceAfterCount = testAnalysisPriceAfterCount;
    }

    public String getTestAnalysisDiscount() {
        return testAnalysisDiscount;
    }

    public void setTestAnalysisDiscount(String testAnalysisDiscount) {
        this.testAnalysisDiscount = testAnalysisDiscount;
    }

    public String getTotalTestPrice() {
        return totalTestPrice;
    }

    public void setTotalTestPrice(String totalTestPrice) {
        this.totalTestPrice = totalTestPrice;
    }

    public String getTotalTestPriceAfterCount() {
        return totalTestPriceAfterCount;
    }

    public void setTotalTestPriceAfterCount(String totalTestPriceAfterCount) {
        this.totalTestPriceAfterCount = totalTestPriceAfterCount;
    }

    public String getTotalPriceAfterTax() {
        return totalPriceAfterTax;
    }

    public void setTotalPriceAfterTax(String totalPriceAfterTax) {
        this.totalPriceAfterTax = totalPriceAfterTax;
    }

    public String getTotalPriceAfterTotalCount() {
        return totalPriceAfterTotalCount;
    }

    public void setTotalPriceAfterTotalCount(String totalPriceAfterTotalCount) {
        this.totalPriceAfterTotalCount = totalPriceAfterTotalCount;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(String totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
