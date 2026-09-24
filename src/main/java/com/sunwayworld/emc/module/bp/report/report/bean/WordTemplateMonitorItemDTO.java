package com.sunwayworld.emc.module.bp.report.report.bean;

/**
 * Word模板中检测项目表格数据（固定表头）
 * @author zhengxy@sunwayworld.com 2020年06月10日
 */
public class WordTemplateMonitorItemDTO {

    /** 样品类型（兼容历史报告表格调用） */
    private String orderCategory;
    /** 检测项目 */
    private String testName;
    /** 检测方法及依据 */
    private String testMethodName;
    /** 检测设备 */
    private String detectDevice;
    private String lowLimit;

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
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

    public String getDetectDevice() {
        return detectDevice;
    }

    public void setDetectDevice(String detectDevice) {
        this.detectDevice = detectDevice;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    @Override
    public String toString() {
        return "WordTemplateMonitorItemDTO{" +
                ", testName='" + testName + '\'' +
                ", testMethodName='" + testMethodName + '\'' +
                ", detectDevice='" + detectDevice + '\'' +
                '}';
    }
}
