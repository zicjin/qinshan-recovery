/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.mobile.bean;

import java.util.Arrays;

public enum EmcOperationType {
    EQUIPT("equipt", "\u4eea\u5668\u8bbe\u5907"),
    EQUIPT_TEST_METHOD("equiptTestMethod", "\u6d4b\u8bd5\u5173\u8054\u4eea\u5668"),
    EMP("emp", "\u4eba\u5458\u4fe1\u606f"),
    PROPERTY_FIELD("propertyField", "\u5c5e\u6027\u5b57\u6bb5"),
    MONITOR_TMPL("monitorTmpl", "\u76d1\u6d4b\u6a21\u677f"),
    CORE_CODE("coreCode", "\u7cfb\u7edf\u7f16\u7801"),
    ITEM_RESULT("itemResult", "\u6d4b\u8bd5\u5206\u6790\u9879\u7ed3\u679c"),
    MONITOR_POINT("monitorPoint", "\u76d1\u6d4b\u70b9"),
    CLIENT("client", "\u5ba2\u6237"),
    MONITOR_TMPL_GROUP("monitorTmplGroup", "\u76d1\u6d4b\u6a21\u677f\u5206\u7ec4"),
    MONITOR_SCHEME("monitorScheme", "\u76d1\u6d4b\u65b9\u6848"),
    TEST("test", "\u6d4b\u8bd5\u5e93"),
    TEST_CATEGORY("testCategory", "\u6d4b\u8bd5\u5206\u7c7b"),
    TEST_METHOD("testMethod", "\u6d4b\u8bd5\u65b9\u6cd5"),
    METHOD("method", "\u65b9\u6cd5\u5e93"),
    ITEM("item", "\u5206\u6790\u9879"),
    ITEMGROUP("itemGroup", "\u5206\u6790\u9879\u7ec4"),
    GROUPITEM("groupItem", "\u5206\u6790\u9879\u7ec4\u5206\u6790\u9879"),
    CONTAINER("container", "\u5bb9\u5668\u7ec4"),
    CALIBRATION("calibration", "\u4eea\u5668\u6821\u51c6"),
    EQUIPTCATEGORY("equiptCategory", "\u4eea\u5668\u7c7b\u578b"),
    UNIT("unit", "\u5355\u4f4d"),
    QC_CATEGORY("qcCategory", "QC\u7c7b\u578b"),
    MONITOR_POINT_CATEGORY("monitorPointCategory", "\u76d1\u6d4b\u70b9\u7c7b\u578b"),
    TEST_ORDER_CATEGORY("testOrderCategory", "\u6d4b\u8bd5\u6837\u54c1\u7c7b\u578b"),
    TEST_CAPACITY("testCapacity", "\u76d1\u6d4b\u80fd\u529b\u5e93"),
    ACQ_SCRIPT("acqScript", "\u4eea\u5668\u91c7\u96c6\u811a\u672c"),
    EAS_PARAM("easParam", "\u4eea\u5668\u91c7\u96c6\u53c2\u6570"),
    EAS_PRE_ANALY("easPreAnaly", "\u4eea\u5668\u91c7\u96c6\u9884\u5904\u7406"),
    EAS_RESULT("easResult", "\u4eea\u5668\u91c7\u96c6\u7ed3\u679c\u53c2\u6570"),
    EQUIPT_TEST_ITEM("equiptTestItem", "\u4eea\u5668\u91c7\u96c6\u5206\u6790\u9879\u5bf9\u7167\u8868"),
    CAPACITY_ITEM_GROUP("capacityItemGroup", "\u5355\u4f4d\u80fd\u529b\u5e93\u5206\u6790\u9879\u7ec4"),
    CAPACITY_ITEM("capacityItem", "\u5355\u4f4d\u80fd\u529b\u5e93\u5206\u6790\u9879"),
    RELATED_TEST_METHOD("relatedTestMethod", "\u5173\u8054\u6d4b\u8bd5");

    private String type;
    private String desc;

    private EmcOperationType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }

    public static EmcOperationType getOperationType(String type) {
        return Arrays.stream(EmcOperationType.values()).filter(p -> p.getType().equals(type)).findAny().orElse(null);
    }
}

