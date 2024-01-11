package com.sunwayworld.emc.module.bp.report.report.constant;

/**
 * @author zhengxy@sunwayworld.com 2023年09月14日
 */
public class EmcReportConstant {

    /** 默认英文数字使用新罗马 */
    public static final String DEFAULT_FONT_FAMILY = "Times New Roman";

    /** 报告结束 */
    public static final String END_OF_REPORT = "***报告结束***";

    /** 报告结果未检出表示方式 */
    public static final String LOW_LIMIT_DISPLAY_MODE = "lowLimit";

    public static final String NO_REMARK = "无";

    /** 固体废物前处理备注 */
    public static final String SOLID_WASTE_PRETREATMENT_REMARK_PREFIX = "固体废物前处理方法为《%s》";

    public static final String NORMAL_SCENE_PHOTO_TABLE_TITLE = "附录%d 现场照片";
    public static final String NORMAL_SCENE_PHOTO_TABLE_TITLE_2 = "附录%d 送样照片";

    /** 结果是否符合限值 */
    public static final String RESULT_EFFECTIVE = "符合";
    public static final String RESULT_INEFFECTIVE = "不符合";

    /** 送样土壤表头 */
    public static final String[] NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限",
            "样品名称", "%s5", "%s6", "%s7", "%s8", "", "",
            "样品编号", "%s9", "%s10", "%s11", "%s12", "", "",
            "样品状态描述", "%s13", "%s14", "%s15", "%s16", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", ""
    };
    public static final String[] NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "方法检出限", "是否符合限值要求",
            "样品名称", "%s4", "%s5", "%s6", "", "", "",
            "样品编号", "%s7", "%s8", "%s9", "", "", "",
            "样品状态描述", "%s10", "%s11", "%s12", "", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", ""
    };
    public static final int NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    public static final String DIOXIN_GAS_ORDER_CATEGORY = "有组织废气";

    /** 检测内容表标题，如：附表12 检测依据、仪器一览表 */
    public static final String MONITOR_CONTENT_TABLE_TITLE = "1. 检测内容";
    /** 检测内容表格标题, "" 代表会被合并单元格的 */
    public static final String[] MONITOR_CONTENT_TABLE_HEADER_TITLES = {
            "样品类型", "采样位置", "检测项目", "样品数量"
    };
    /** 送样 */
    public static final String[] MONITOR_CONTENT_TABLE_HEADER_TITLES_2 = {
            "样品类型", "样品名称", "样品状态", "样品介质/包装"
    };
    /** 检测内容结果表头列数 */
    public static final int MONITOR_CONTENT_TABLE_HEADER_COLUMN_QTY = 4;

    /** 检测项目表标题，如：附表12 检测依据、仪器一览表 */
    public static final String MONITOR_ITEM_TABLE_TITLE = "2. 检测分析方法";
    /** 检测项目表格标题, "" 代表会被合并单元格的 */
    public static final String[] MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "样品类型", "检测项目", "检测分析方法", "检测仪器"
    };
    /** 检测项目结果表头列数 */
    public static final int MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 4;

    /** 表格标题 */
    public static final String DIOXIN_RESULT_TABLE_TITLE = "3. 检测结果";
    /** 结果表格副标题，比如 3.1 土壤 */
    public static final String DIOXIN_RESULT_TABLE_SUB_TITLE = "3.%d %s";
    public static final String[] DIOXIN_RESULT_TABLE_HEADER_TITLES = {
            "采样时间", "采样位置", "检测项目", "毒性当量浓度", "%s0", "单位",
            "", "", "", "检测结果", "", ""
    };
    public static final String[] DIOXIN_RESULT_TABLE_HEADER_TITLES_2 = {
            "样品名称", "", "检测项目", "毒性当量浓度", "%s0", "单位",
            "", "", "", "检测结果", "", ""
    };
    public static final int DIOXIN_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    public static final String DIOXIN_RESULT_TABLE_REMARK = "详细检测结果见附表 %d";

    public static final String DIOXIN_ATTACHED_TABLE_TITLE = "附表%d 检测结果";
    public static final String DIOXIN_ATTACHED_TABLE_TITLE_2 = "续上表";

    public static final String DIOXIN_ND_RESULT = "N.D.";

    /** 附表 - 有组织废气检测结果 */
    public static final String DIOXIN_GAS_RESULT_TABLE_TITLE = "附表1 检测结果";
    public static final String[] DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES = {
            "采样时间", "", "%s0", "", "采样位置", "%s1", "", "",
            "检测项目", "", "", "实测浓度", "检出限", "换算浓度", "毒性当量浓度（TEQ）", "",
            "", "", "", "%s2", "%s3", "%s4", "I-TEF", "%s5"
    };
    public static final int DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final String[] DIOXIN_GAS_RESULT_TITLE_KEYWORDS = { "组分浓度", "检出限", "换算浓度", "二噁英" };
    public static final String[] DIOXIN_GAS_RESULT_ITEM_KEYWORDS = { "组分浓度", "检出限", "换算浓度", "毒性当量因子", "二噁英" };

    /** 附表 - 其他样品类型检测结果 */
    public static final String DIOXIN_OTHER_RESULT_TABLE_TITLE = "附表1 检测结果";
    public static final String[] DIOXIN_OTHER_RESULT_TABLE_HEADER_TITLES = {
            "采样时间", "", "%s0", "", "采样位置", "%s1", "",
            "检测项目", "", "", "实测浓度", "检出限", "毒性当量浓度（TEQ）", "",
            "", "", "", "%s2", "%s3", "I-TEF", "%s4"
    };
    public static final String[] DIOXIN_OTHER_RESULT_TABLE_HEADER_TITLES_2 = {
            "送样时间", "", "%s0", "", "样品名称", "%s1", "",
            "检测项目", "", "", "实测浓度", "检出限", "毒性当量浓度（TEQ）", "",
            "", "", "", "%s2", "%s3", "I-TEF", "%s4"
    };
    public static final int DIOXIN_OTHER_RESULT_TABLE_HEADER_COLUMN_QTY = 7;
    public static final String[] DIOXIN_OTHER_RESULT_TITLE_KEYWORDS = { "组分浓度", "检出限", "换算浓度"};
    public static final String[] DIOXIN_OTHER_RESULT_ITEM_KEYWORDS = { "组分浓度", "检出限", "毒性当量因子", "换算浓度" };

    public static final String[] DIOXIN_ITEM_GROUPS = {"多氯代二苯并对二噁英", "多氯代二苯并呋喃"};
    public static final String[] DIOXIN_ITEM_GROUP_KEYWORDS = {"CDD", "CDF"};
    public static final String[] DIOXIN_ITEM_KEYWORDS = {"组分浓度", "换算浓度", "毒性当量因子"};
    public static final String DIOXIN_REPORT_RESULT_ITEM_KEYWORD = "测定浓度";
    public static final String DIOXIN_ORIGINAL_RECORD_REPORT_RESULT = "二噁英类总量∑（PCDDs+PCDFs）";
    public static final String DIOXIN_KEYWORD = "二噁英";

    public static final String[] DIOXIN_OTHER_RESULT_REMARKS = {
        "实测浓度：二噁英类浓度测定值",
        "毒性当量浓度（TEQ）：实测浓度与该同类物的毒性当量因子（TEF）的乘积；二噁英毒性当量浓度为所有检测同类物毒性当量浓度之和；毒性当量因子（TEF）采用 I-TEF",
        "毒性当量（TEQ）质量分数：折算为相当于 2,3,7,8-T<sub>4</sub>CDD 的质量分数，ng/kg",
        "当样品的实测浓度低于检出限时用“N.D.”表示，计算毒性当量（TEQ）浓度以 1/2 检出限计"
    };

    /** 现场照片 */
    public static final String DIOXIN_SCENE_PHOTO_TABLE_TITLE = "4. 现场照片";
    public static final String DIOXIN_SCENE_PHOTO_TABLE_TITLE_2 = "4. 送样照片";
    public static final String[] DIOXIN_SCENE_PHOTO_TABLE_HEADER_TITLES = {
            "", ""
    };
    public static final int DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY = 2;

    /** 有组织废气参数 */
    public static final String DIOXIN_GAS_PARAMETER_TABLE_TITLE = "5. 烟气参数";
    public static final String[] DIOXIN_GAS_PARAMETER_TABLE_HEADER_TITLES = {
            "%s0", "", "", "", "",
            "%s1", "", "", "", "",
            "%s2", "", "", "", "",
            "参数", "时间段", "", "", "单位",
            "", "第一次", "第二次", "第三次", "",
    };
    public static final int DIOXIN_GAS_PARAMETER_TABLE_HEADER_COLUMN_QTY = 5;
    public static final String[] DIOXIN_GAS_PARAMETER_KEYWORDS = {"检测点位：%s", "检测项目：%s", "采样时间：%s"};
    public static final String[] DIOXIN_GAS_PARAMETER_RESULT_KEYWORDS = {"排气筒高度", "大气压", "截面积", "流速", "动压", "静压", "含氧量", "烟温", "含湿量", "烟气流量", "标干流量"};
    public static final String[] DIOXIN_GAS_PARAMETER_ITEM_KEYWORDS = {"高度", "气压", "面积", "流速", "动压", "静压", "含氧量", "烟温", "含湿量", "烟气", "标干"};

    /********************** 质控报告 ********************/

    /** QC判断类别 */
    public static final String RN7_DECISION_CATEGORY = "RN7";
    public static final String RN2_DECISION_CATEGORY = "RN2";
    public static final String RN14_DECISION_CATEGORY = "RN14";

    /** 浓度分析项 */
    public static final String RESULT_ITEM_KEYWORD = "浓度";
}
