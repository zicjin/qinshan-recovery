package com.sunwayworld.emc.module.bp.mobile.bean;

import java.util.Arrays;

/**
 * @author zhengxy@sunwayworld.com 2019年11月19日
 */
public enum EmcOperationType {
    /** 操作类型 */
    EQUIPT("equipt", "仪器设备"),
    EQUIPT_TEST_METHOD("equiptTestMethod", "测试关联仪器"),
    EMP("emp", "人员信息"),
    PROPERTY_FIELD("propertyField", "属性字段"),
    MONITOR_TMPL("monitorTmpl", "监测模板"),
    CORE_CODE("coreCode", "系统编码"),
    ITEM_RESULT("itemResult", "测试分析项结果"),
    MONITOR_POINT("monitorPoint", "监测点"),
    CLIENT("client", "客户"),
    MONITOR_TMPL_GROUP("monitorTmplGroup", "监测模板分组"),
    MONITOR_SCHEME("monitorScheme", "监测方案"),
    TEST("test", "测试库"),
    TEST_CATEGORY("testCategory", "测试分类"),
    TEST_METHOD("testMethod", "测试方法"),
    METHOD("method", "方法库"),
    ITEM("item", "分析项"),
    ITEMGROUP("itemGroup", "分析项组"),
    GROUPITEM("groupItem", "分析项组分析项"),
    CONTAINER("container", "容器组"),
    CALIBRATION("calibration", "仪器校准"),
    EQUIPTCATEGORY("equiptCategory", "仪器类型"),
    UNIT("unit", "单位"),
    QC_CATEGORY("qcCategory", "QC类型"),
    MONITOR_POINT_CATEGORY("monitorPointCategory", "监测点类型"),
    TEST_ORDER_CATEGORY("testOrderCategory", "测试样品类型"),
    TEST_CAPACITY("testCapacity", "监测能力库"),
    ACQ_SCRIPT("acqScript", "仪器采集脚本"),
    EAS_PARAM("easParam", "仪器采集参数"),
    EAS_PRE_ANALY("easPreAnaly", "仪器采集预处理"),
    EAS_RESULT("easResult", "仪器采集结果参数"),
    EQUIPT_TEST_ITEM("equiptTestItem", "仪器采集分析项对照表"),
    CAPACITY_ITEM_GROUP("capacityItemGroup", "单位能力库分析项组"),
    CAPACITY_ITEM("capacityItem", "单位能力库分析项"),
    RELATED_TEST_METHOD("relatedTestMethod", "关联测试");

    private String type;
    private String desc;

    EmcOperationType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static EmcOperationType getOperationType(String type) {
        return Arrays.stream(values()).filter(p -> p.getType().equals(type)).findAny().orElse(null);
    }
}
