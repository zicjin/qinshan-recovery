package com.sunwayworld.emc.framework.constant;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
public class PfConstant extends EmcConstant {

    public static final String CURVE_NO_PREFIX_A = "A";

    public static final String CURVE_NO_PREFIX_B = "B";

    /****************************************************
     * QC样品
     ****************************************************/
    public static final String QC_Q_STD = "Q-STD";// 盲码标样
    public static final String QC_Q_DUP = "Q-DUP";// 密码平行
    public static final String QC_QXJH = "L-QXCH"; // 曲线校核
    public static final String QC_P_YSKB = "P-YSKB"; // 现场运输空白
    public static final String QC_P_ZCSBKB = "zcsbkb"; // 现场设备空白
    public static final String QC_L_ALAB = "L-Alab"; // 前管
    public static final String QC_L_BLAB = "L-Blab"; // 后管
    public static final String QC_P_CF = "P-CF"; // 尘氟

    public static final String QC_L_SPKC_P = "L-SPKC-P"; // 样品加标平行
    public static final String QC_L_SPKCK_P = "L-SPKCK-P"; // 实验室空白加标平行
    public static final String QC_L_QXCHD = "L-QXCHD"; // 最低点曲线校核/零点校核
    public static final String QC_L_SPKCK = "L-SPKCK"; // 空白加标

    /** 前管、后管样品编号后缀 */
    public static final String QC_L_ALAB_ORDER_NO_SUFFIX = "Q";
    public static final String QC_L_BLAB_ORDER_NO_SUFFIX = "H";
    public static final String QC_P_CF_ORDER_NO_SUFFIX = "C";

    /** 实验室平行样品编号后缀 */
    public static final String QC_LDUP_ORDER_NO_SUFFIX = "-DUP";
    /** 实验室样品加标样品编号后缀 */
    public static final String QC_SPKC_ORDER_NO_SUFFIX = "-MS";
    /** 实验室样品加标平行样品编号后缀 */
    public static final String QC_SPKC_P_ORDER_NO_SUFFIX = "-DUP";

    /** 实验室空白样品编号前缀 */
    public static final String QC_LBLANK_ORDER_NO_PREFIX = "MB";
    /** 实验室空白加标样品编号前缀 */
    public static final String QC_SPKCK_ORDER_NO_PREFIX = "LCS";
    /** 实验室标准样品编号前缀 */
    public static final String QC_STD_ORDER_NO_PREFIX = "CRM";
    /** 实验室空白加标平行样品编号前缀 */
    public static final String QC_SPKCK_P_ORDER_NO_PREFIX = "DCS";
    /** 最低点曲线校核样品编号前缀 */
    public static final String QC_QXCHD_ORDER_NO_PREFIX = "STD";
    /** 曲线校核样品编号前缀 */
    public static final String QC_QXCH_ORDER_NO_PREFIX = "STD";

    /****************************************************
     * Word报告
     ****************************************************/

    /** 水印报告 */
    public static final String WATERMARK_WORD_REPORT_ATTACHMENT_TYPE = "watermarkReport";
    /** 盖章报告 */
    public static final String STAMPED_WORD_REPORT_ATTACHMENT_TYPE = "stampedReport";
    /** 质控报告 */
    public static final String QC_WORD_REPORT_ATTACHMENT_TYPE = "qcReport";

    /** 综合Word报告模板 */
    public static final String COMPLEX_WORD_REPORT_TEMPLATE = "综合报告.docx";

    /** 质控Word报告模板 */
    public static final String QC_WORD_REPORT_TEMPLATE = "质控报告.docx";

    /** 二噁英Word报告模板 */
    public static final String DIOXIN_WORD_REPORT_TEMPLATE = "二噁英报告.docx";

    /** 二噁英质控Word报告模板 */
    public static final String DIOXIN_QC_WORD_REPORT_TEMPLATE = "二噁英质控报告.docx";

    /** 水对应的监测模板组ID */
    public static final long WATER_MONITOR_TMPL_GROUP_ID = 1L;
    /** 气对应的监测模板组ID */
    public static final long GAS_MONITOR_TMPL_GROUP_ID = 4L;
    /** 土壤对应的监测模板组ID */
    public static final long SOIL_MONITOR_TMPL_GROUP_ID = 3L;
    /** 噪声对应的监测模板组ID */
    public static final long NOISE_MONITOR_TMPL_GROUP_ID = 2L;
    /** 生物对应的监测模板组ID */
    public static final long BIOLOGICAL_MONITOR_TMPL_GROUP_ID = 5L;
    /** 固体废物对应的监测模板组ID */
    public static final long SOLID_WASTE_MONITOR_TMPL_GROUP_ID = 6L;

    /** 二噁英-水对应的监测模板组ID */
    public static final long DIOXIN_WATER_MONITOR_TMPL_GROUP_ID = 11L;
    /** 二噁英-有组织废气对应的监测模板组ID */
    public static final long DIOXIN_ORGANIZED_GAS_MONITOR_TMPL_GROUP_ID = 12L;
    /** 二噁英-土壤对应的监测模板组ID */
    public static final long DIOXIN_SOIL_MONITOR_TMPL_GROUP_ID = 13L;
    /** 二噁英-固体废物对应的监测模板组ID */
    public static final long DIOXIN_SOLID_WASTE_MONITOR_TMPL_GROUP_ID = 14L;
    /** 二噁英-空气对应的监测模板组ID */
    public static final long DIOXIN_AIR_MONITOR_TMPL_GROUP_ID = 15L;
    /** 二噁英-无组织废气对应的监测模板组ID */
    public static final long DIOXIN_UNORGANIZED_GAS_MONITOR_TMPL_GROUP_ID = 16L;
    /** 二噁英-饲料对应的监测模板组ID */
    public static final long DIOXIN_FODDER_MONITOR_TMPL_GROUP_ID = 17L;
    /** 二噁英-沉积物对应的监测模板组ID */
    public static final long DIOXIN_SEDIMENT_MONITOR_TMPL_GROUP_ID = 24L;


    public static final String DEFAULT_ENTRUST_ORG_DESC = "受%s委托";

    public static final String REPORT_ISSUE_DATE = "签发日期  %d年 %d 月 %d 日";

    public static final String EXECUTIVE_STANDARD = "执行标准";
    /** 检测结果表格结束提示信息 */
    public static final String TABLE_END_TIP_MESSAGE = "***本页完***";

    public static final String DETECT_CATEGORY = "委托检测";

    public static final String DEFAULT_RESULT = "检测结果";
    public static final String DEFAULT_TABLE_MESSAGE = "见附表%d";

    public static final String SAMPLING_ENTRUST_TYPE = "采样检测";
    public static final String NO_SAMPLING_ENTRUST_TYPE = "来样送检";

    public static final String SAMPLING_USER_TITLE = "采样人";
    public static final String NO_SAMPLING_USER_TITLE = "接样人";

    /** null替换值 */
    public static final String NULL_REPLACED_VALUE = "/";

    /** 分隔符 */
    public static final String COMMA_DELIMITER = ",";
    public static final String COMMA1_DELIMITER = "、";

    public static final String WATER_KEYWORD = "water";
    public static final String SOIL_KEYWORD = "soil";
    public static final String GAS_KEYWORD = "gas";
    public static final String NOISE_KEYWORD = "noise";

    /** 检测目的 */
    public static final String DETECT_PURPOSE_FORMATE_1 = "对%s的%s进行检测";
    public static final String DETECT_PURPOSE_FORMATE_2 = "受%s委托对%s的%s进行检测";
    public static final String DETECT_PURPOSE_FORMATE_3 = "%s检测";

    /** 默认分析科室排序 */
    public static final String[] DEFAULT_SORTED_ORG_NAMES = {
            "现场", "理化", "金属", "有机"
    };

    /** 土壤中的含水率测试 */
    public static final String WATER_CONTENT_TEST_MATCH_KEYWORD = "含水率";
    public static final String WATER_CONTENT_TEST_MATCH_KEYWORD_2 = "水分含量";

    /** 质控报告，符号附注 */
    public static final String QC_SYMBOL_REMARK = "“ND”和“检出限+L”表示检测项目浓度低于方法检出限。";
    public static final String QC_SYMBOL_REMARK_2 = "“ND”表示检测项目浓度低于方法检出限。";

    public static final String QC_ENVIRONMENTAL_CONDITIONS_FOR_SAMPLING = "为保证数据的可追溯性，公司对采样、运输、样品前处理、分析各过程按照标准规范要求进行环境条件控制，严格按照标准规范的要求执行，保存过程控制记录，以便追溯。";
    public static final String QC_ENVIRONMENTAL_CONDITIONS_FOR_NO_SAMPLING = "为保证数据的可追溯性，公司对样品前处理、分析各过程按照标准规范要求进行环境条件控制，严格按照标准规范的要求执行，保存过程控制记录，以便追溯。";

    /** 检测项目一览表标题，如：附表12 检测依据、仪器一览表 */
    public static final String SIMPLE_MONITOR_ITEM_TABLE_TITLE = "附表%d 检测项目一览表";
    /** 检测项目一览表格标题, "" 代表会被合并单元格的 */
    public static final String[] SIMPLE_MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "检测类别", "检测项目"
    };
    /** 检测项目一览结果表头列数 */
    public static final int SIMPLE_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 2;
    /** 不显示的仪器 */
    public static final String[] EXCLUDE_DEVICE_KEYWORDS = {"天平"};

    /** 检测项目表标题，如：附表12 检测依据、仪器一览表 */
    public static final String MONITOR_ITEM_TABLE_TITLE = "附表%d 检测依据、仪器一览表";
    /** 检测项目表格标题, "" 代表会被合并单元格的 */
    public static final String[] MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "检测类别", "分析项目", "检测依据", "检测仪器"
    };
    /** 检测项目结果表头列数 */
    public static final int MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 4;

    public static final String SAMPLING_DATE = "采样日期";
    public static final String ACCEPTED_DATE = "接样日期";

    /** 表格最大动态列数 */
    public static final int MAX_COLUMN_QTY = 4;

    public static final String TABLE_REMARK_TITLE = "注：";
    public static final String DELIVERY_TABLE_REMARK_2 = "气样体积数值由客户提供，我司仅对来样检测结果中的样品含量负责";
    public static final String DELIVERY_TABLE_REMARK_3 = "此项目为客户送样，仅对来样检测结果负责(气样体积数值由客户提供，我司仅对来样检测结果中的样品含量负责)";
    public static final String DELIVERY_TABLE_REMARK_4 = "检测数据仅供参考，不具有对社会的证明作用";
    public static final String ND_TABLE_REMARK = "“ND”表示未检出（低于检出限）";
    public static final String SLASH_TABLE_REMARK = "“/”表示不需要检测";
    public static final String LOW_LIMIT_TABLE_REAMRK = "“检出限+L”表示未检出";

    /** 水检测结果表标题，如：水质检测结果(1) */
    public static final String WATER_RESULT_TABLE_TITLE = "附表%d %s检测结果";
    /** 水检测结果表头标题, 固定为3行5列表，其中 %s 代表需要动态赋值的, "" 代表会被合并单元格的 */

    public static final String[] WATER_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限", "执行标准限值",
            "点位名称", "%s5", "%s6", "%s7", "%s8", "%s9", "", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "", "",
            "采样深度", "%s15", "%s16", "%s17", "%s18", "%s19", "", "",
            "样品状态描述", "%s20", "%s21", "%s22", "%s23", "%s24", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", "", ""
    };
    public static final String[] WATER_RESULT_TABLE_HEADER_TITLES_2 = {
            "采样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限",
            "点位名称", "%s5", "%s6", "%s7", "%s8", "%s9", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "",
            "采样深度", "%s15", "%s16", "%s17", "%s18", "%s19", "",
            "样品状态描述", "%s20", "%s21", "%s22", "%s23", "%s24", "",
            "检测项目", "检测结果", "", "", "", "", ""
    };
    public static final String[] WATER_RESULT_TABLE_HEADER_TITLES_3 = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限", "执行标准限值",
            "样品名称", "%s5", "%s6", "%s7", "%s8", "%s9", "", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "", "",
            "样品状态描述", "%s15", "%s16", "%s17", "%s18", "%s19", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", "", ""
    };
    public static final String[] WATER_RESULT_TABLE_HEADER_TITLES_4 = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限",
            "样品名称", "%s5", "%s6", "%s7", "%s8", "%s9", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "",
            "样品状态描述", "%s15", "%s16", "%s17", "%s18", "%s19", "",
            "检测项目", "检测结果", "", "", "", "", ""
    };

    /** 水检测结果表头列数 */
    public static final int WATER_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final int WATER_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 5;
    public static final String WATER_EXCLUDE_ORDER_DESC_KEYWORD = "浊度";

    /** 土检测结果表标题，如：土壤检测结果(1) */
    public static final String SOIL_RESULT_TABLE_TITLE = "附表%d %s检测结果";
    /** 土检测结果表头标题, 固定为3行5列表，其中 %s 代表需要动态赋值的, "" 代表会被合并单元格的 */
    public static final String[] SOIL_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限", "执行标准限值",
            "点位名称", "%s5", "%s6", "%s7", "%s8", "%s9", "", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "", "",
            "采样深度", "%s15", "%s16", "%s17", "%s18", "%s19", "", "",
            "样品状态描述", "%s20", "%s21", "%s22", "%s23", "%s24", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", "", ""
    };
    public static final String[] SOIL_RESULT_TABLE_HEADER_TITLES_2 = {
            "采样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限",
            "点位名称", "%s5", "%s6", "%s7", "%s8", "%s9", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "",
            "采样深度", "%s15", "%s16", "%s17", "%s18", "%s19", "",
            "样品状态描述", "%s20", "%s21", "%s22", "%s23", "%s24", "",
            "检测项目", "检测结果", "", "", "", "", ""
    };
    public static final String[] SOIL_RESULT_TABLE_HEADER_TITLES_3 = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限", "执行标准限值",
            "样品名称", "%s5", "%s6", "%s7", "%s8", "%s9", "", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "", "",
            "样品状态描述", "%s15", "%s16", "%s17", "%s18", "%s19", "", "",
            "检测项目", "检测结果", "", "", "", "", "", "", "", ""
    };
    public static final String[] SOIL_RESULT_TABLE_HEADER_TITLES_4 = {
            "接样日期", "%s0", "%s1", "%s2", "%s3", "%s4", "方法检出限",
            "样品名称", "%s5", "%s6", "%s7", "%s8", "%s9", "",
            "样品编号", "%s10", "%s11", "%s12", "%s13", "%s14", "",
            "样品状态描述", "%s15", "%s16", "%s17", "%s18", "%s19", "",
            "检测项目", "检测结果", "", "", "", "", ""
    };
    /** 土检测结果表头列数 */
    public static final int SOIL_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final int SOIL_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 7;
    public static final String SPECIAL_TEST_NAME = "屈";
    public static final String SPECIAL_REPLACED_TEST_NAME = "䓛";

    /** 噪声检测结果表头标题 */
    public static final String[] NOISE_RESULT_TABLE_HEADER_TITLES = {
            "监测日期", "测点编号", "点位名称", "测量时间", "检测值(单位:dB(A))", "标准限值(单位:dB(A))",
    };
    public static final String[] NOISE_RESULT_TABLE_HEADER_TITLES_2 = {
            "监测日期", "测点编号", "点位名称", "测量时间", "检测值(单位:dB(A))"
    };
    /** 噪声检测结果表头列数 */
    public static final int NOISE_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    public static final int NOISE_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 5;
    public static final String[] NOISE_TIME_ITEMS = { "昼间监测时间", "夜间监测时间" };
    public static final String[] NOISE_RESULT_KEYWORDS = { "昼间", "夜间" };

    /** 有组织废气检测结果表头标题 */
    public static final String[] GAS_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "", "%s0", "", "", "", "", "",
            "采样点位", "样品编号", "检测项目及检测结果", "", "", "", "", "",
            "", "", "%s1", "", "", "%s2", "", "",
            "", "", "%s3", "%s4", "%s5", "%s6", "%s7", "%s8"
    };
    /** 有组织废气送样 */
    public static final String[] GAS_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "", "%s0", "", "", "", "", "",
            "样品名称", "样品编号", "检测项目及检测结果", "", "", "", "", "",
            "", "", "%s1", "", "", "%s2", "", "",
            "", "", "%s3", "", "", "%s6", "", ""
    };
    /** 有组织废气检测结果表头列数 */
    public static final int GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final String[] GAS_EXCLUDED_RESULT_KEYWORDS = { "折算浓度", "排放速率" };
    public static final String[] GAS_RESULT_KEYWORDS = { "实测浓度,样品浓度", "折算浓度", "排放速率" };
    public static final String METHOD_LOW_LIMIT = "方法检出限";
    public static final String METHOD_LOW_VALUE = "最低检出量";
    public static final String GAS_SAMPLING_VOLUME_KEYWORD = "采样体积";
    public static final String GAS_ORDER_CONCENT_KEYWORD = "样品浓度";


    /** 有组织废气参数检测结果表头标题 */
    public static final String GAS_PARAMETER_RESULT_TABLE_TITLE = "管道参数、废气参数";
    public static final String[] GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES = {
            "采样位置", "样品编号", "排气筒高度(m)", "截面积(m<sup>2</sup>)", "含氧量(%)", "烟温(°C)", "含湿量(%)", "平均流速(m/s)", "标干风量(m<sup>3</sup>/h)"
    };
    /** 有组织废气检测结果表头列数 */
    public static final int GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY = 9;
    public static final String[] GAS_PARAMETER_RESULT_KEYWORDS = { "高度", "截面积", "含氧量", "烟温", "含湿量", "流速", "风量" };

    /** 无组织废气检测结果表头标题 */
    public static final String[] UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "", "%s0", "", "", "",
            "采样点位", "样品编号", "检测项目及检测结果", "", "", "",
            "", "", "%s1", "%s2", "%s3", "%s4"
    };
    public static final String[] UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "", "%s0", "", "", "",
            "样品名称", "样品编号", "检测项目及检测结果", "", "", "",
            "", "", "%s1", "%s2", "%s3", "%s4"
    };
    /** 无组织废气检测结果表头列数 */
    public static final int UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 6;

    /** 质控报告-实验室平行 */
    public static final String[] LDUP_RESULT_TABLE_HEADER_TITLES = {
            "无机类分析\n质量控制结果\n实验室平行样", "", "样品编号", "", "%s0", "", "", "",
            "", "", "样品类型", "", "%s1", "", "", "",
            "分析指标", "方法", "检出限", "单位", "平行样质控", "", "", "",
            "", "", "", "", "样品结果", "平行样结果", "相对偏差%", "相对偏差控制范围%"
    };
    public static final int LDUP_RESULT_TABLE_HEADER_COLUMN_QTY = 8;

    /** 质控报告-实验室标准样品 */
    public static final String[] STD_RESULT_TABLE_HEADER_TITLES = {
            "无机类分析\n质量控制结果\n有证标准物质", "", "实验室有证标准物质批号", "", "%s0", "", "", "",
            "", "", "土壤有证标准物质批号", "", "%s1", "", "", "",
            "", "", "样品类型", "", "%s2", "", "", "",
            "分析指标", "方法", "检出限", "单位", "有证标准物质质控", "", "", "",
            "", "", "", "", "标称浓度", "测定结果", "控制范围", "",
            "", "", "", "", "", "", "上限", "下限"
    };
    public static final int STD_RESULT_TABLE_HEADER_COLUMN_QTY = 8;

    /** 质控报告-实验室空白 */
    public static final String[] LBLANK_RESULT_TABLE_HEADER_TITLES = {
            "无机类分析\n质量控制结果\n实验室空白", "", "样品编号", "", "%s0", "%s1", "%s2",
            "", "", "样品类型", "", "%s3", "%s4", "%s5",
            "分析指标", "方法", "检出限", "单位", "空白质控结果", "空白质控结果", "空白质控结果"
    };
    public static final int LBLANK_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    /** 质控报告-全程序空白 */
    public static final String[] PBLANK_RESULT_TABLE_HEADER_TITLES = {
            "无机类分析\n质量控制结果\n全程序空白", "", "样品编号", "", "%s0",
            "", "", "样品类型", "", "%s1",
            "分析指标", "方法", "检出限", "单位", "空白质控结果"
    };
    public static final int PBLANK_RESULT_TABLE_HEADER_COLUMN_QTY = 5;

    /** 质控报告-样品加标 */
    public static final String[] LSPKC_RESULT_TABLE_HEADER_TITLES = {
            "无机类分析\n质量控制结果\n实验室样品加标", "", "样品批号", "", "/", "", "", "", "",
            "", "", "样品编号", "", "%s0", "", "", "", "",
            "", "", "样品类型", "", "%s1", "", "", "", "",
            "分析指标", "方法", "检出限", "单位", "样品加标质控", "", "", "", "",
            "", "", "", "", "样品结果", "加标量()", "回收率%", "加标回收率控制范围%", "",
            "", "", "", "", "", "", "", "下限", "上限",
    };
    public static final int LSPKC_RESULT_TABLE_HEADER_COLUMN_QTY = 9;

    /** 二噁英报告-有组织废气 */
    public static final String DIOXIN_KEYWORD = "二噁英";
    public static final String DIOXIN_KEYWORD_2 = "二噁英类";
    public static final String DIOXIN_RESULT_ITEM_KEYWORD = "二噁英测定浓度";
    public static final String DIOXIN_CONVERTED_RESULT_ITEM_KEYWORD = "换算后二噁英浓度";
    public static final String DIOXIN_ORGANIZED_GAS_KEYWORD = "有组织";
    public static final String DIOXIN_RESULT_TABLE_TITLE = "附表%d %s检测结果表";
    public static final String[] DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "点位名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/Nm<sup>3</sup>)", "平均值(ngTEQ/Nm<sup>3</sup>)"
    };
    public static final String[] DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "样品名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/Nm<sup>3</sup>)", "平均值(ngTEQ/Nm<sup>3</sup>)"
    };
    public static final String[] DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_3 = {
            "采样日期", "点位名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/Nm<sup>3</sup>)"
    };
    public static final String[] DIOXIN_GAS_RESULT_TABLE_HEADER_TITLES_4 = {
            "接样日期", "样品名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/Nm<sup>3</sup>)"
    };
    public static final int DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 7;
    public static final int DIOXIN_GAS_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 6;
    public static final String DIOXIN_GAS_ORDER_DESC = "（气）石英纤维滤筒、树脂、冷凝水";
    public static final String DIOXIN_GAS_ORDER_DESC_2 = "（气）石英纤维滤筒、树脂";

    /** 管道及废气参数 */
    public static final String DIOXIN_GAS_PARAMETER_RESULT_TABLE_TITLE = "管道及废气参数";
    public static final String[] DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES = {
            "点位名称", "样品编号", "排气筒高度(m)", "截面积(m<sup>2</sup>)", "含氧量(%)", "烟温(°C)", "含湿量(%)", "平均流速(m/s)", "标干风量(m<sup>3</sup>/h)"
    };
    public static final String[] DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES_2 = {
            "样品名称", "样品编号", "排气筒高度(m)", "截面积(m<sup>2</sup>)", "含氧量(%)", "烟温(°C)", "含湿量(%)", "平均流速(m/s)", "标干风量(m<sup>3</sup>/h)"
    };
    public static final String[] DIOXIN_GAS_PARAMETER_RESULT_KEYWORDS = {
            "高度", "截面积", "含氧量", "温度", "含湿量", "流速", "风量"
    };
    public static final int DIOXIN_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY = 9;

    /** 二噁英报告-环境空气 */
    public static final String DIOXIN_ENV_GAS_KEYWORD = "环境空气";
    public static final String DIOXIN_UNORGANIZED_GAS_KEYWORD = "无组织";
    public static final String[] DIOXIN_ENV_GAS_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "点位名称", "样品编号", "样品状态", "检测项目", "检测结果(pgTEQ/Nm<sup>3</sup>)"
    };
    public static final String[] DIOXIN_ENV_GAS_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "样品名称", "样品编号", "样品状态", "检测项目", "检测结果(pgTEQ/Nm<sup>3</sup>)"
    };
    public static final int DIOXIN_ENV_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    public static final String DIOXIN_ENV_GAS_ORDER_DESC = "（气）石英纤维滤膜、PUF";

    /** 气象参数 */
    public static final String DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_TITLE = "气象参数";
    public static final String[] DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES = {
            "检测点位", "采样日期", "风向", "风速(m/s)", "气压(kPa)", "气温(°C)", "湿度(%)"
    };
    public static final String[] DIOXIN_ENV_GAS_PARAMETER_RESULT_KEYWORDS = {
            "风向", "风速", "气压", "气温", "湿度"
    };
    public static final int DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    /** 二噁英报告-水 */
    public static final String[] DIOXIN_WATER_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "点位名称", "样品编号", "样品状态", "检测项目", "检测结果(pgTEQ/L)"
    };
    public static final String[] DIOXIN_WATER_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "样品名称", "样品编号", "样品状态", "检测项目", "检测结果(pgTEQ/L)"
    };
    public static final int DIOXIN_WATER_RESULT_TABLE_HEADER_COLUMN_QTY = 6;

    /** 二噁英报告-土壤 */
    public static final String[] DIOXIN_SOIL_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "点位名称", "样品编号", "采样深度(m)", "样品状态", "检测项目", "检测结果(ngTEQ/kg)"
    };
    public static final String[] DIOXIN_SOLID_WASTE_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "点位名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/kg)"
    };
    public static final String[] DIOXIN_SOIL_RESULT_TABLE_HEADER_TITLES_2 = {
            "接样日期", "样品名称", "样品编号", "样品状态", "检测项目", "检测结果(ngTEQ/kg)"
    };
    public static final String DIOXIN_RESULT_KEYWORD = "检测结果";
    public static final int DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY = 7;
    public static final int DIOXIN_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 6;

    /** 二噁英报告 - 固体废物 */
    public static final String DIOXIN_SOLID_WASTE_ORDER_DESC = "固体";
    public static final String[] DIOXIN_SOLID_WASTE_ORDER_CATEGORIES = {"固废", "固体废物", "飞灰", "城市污泥"};

    /** 二噁英报告-分析原始记录 */
    public static final String[] DIOXIN_SOIL_KEYWORDS = {"土"};
    public static final String[] DIOXIN_GAS_KEYWORDS = {"气"};
    public static final String[] DIOXIN_ORGANIZED_GAS_KEYWORDS = {"有组织废气"};
    public static final String[] DIOXIN_GAS_WASTE_KEYWORDS = {"废气"};
    public static final String[] DIOXIN_UNORGANIZED_GAS_KEYWORDS = {"无组织废气"};
    public static final String[] DIOXIN_ENV_GAS_KEYWORDS = {"环境空气"};
    public static final String[] DIOXIN_FODDER_KEYWORDS = {"饲料"};
    public static final String[] DIOXIN_WATER_KEYWORDS = {"水"};
    public static final String[]  DIOXIN_ZONEOFAERATION_KEYWORDS = {"包气带"};
    public static final String  DIOXIN_ZONEOFAERATION = "包气带";
    public static final String DIOXIN_ORIGINAL_RECORD_TITLE = "附件 高分辨气相色谱-质谱仪分析原始记录";
    public static final String[] SOIL_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES = {
            "样品类型", "", "%s0", "", "", "",
            "样品编号", "", "%s1", "取样量(g)", "%s2", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s3", "%s4", "%s5", "%s6",
    };
    public static final String[] GAS_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES = {
            "样品类型", "", "%s0", "", "", "",
            "样品编号", "", "%s1", "取样量(Nm<sup>3</sup>)", "%s2", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s3", "%s4", "%s5", "%s6",
    };
    public static final String[] WATER_DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_TITLES = {
            "样品类型", "", "%s0", "", "", "",
            "样品编号", "", "%s1", "取样量(L)", "%s2", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s3", "%s4", "%s5", "%s6",
    };
    public static final int DIOXIN_ORIGINAL_RECORD_TABLE_HEADER_COLUMN_QTY = 6;
    public static final String DIOXIN_SAMPLING_QUANTITY_ITEM_KEYWORD = "取样量";
    public static final String DIOXIN_REPORT_RESULT_ITEM_KEYWORD = "测定浓度";
    public static final String DIOXIN_UNIT_TITLE = "单位:%s";
    public static final String DIOXIN_UNIT_NEW_TITLE = "单位: %s";
    public static final String DIOXIN_ORIGINAL_RECORD_REPORT_RESULT = "二噁英类测定浓度 单位：%s";
    public static final String DIOXIN_ND_REPLACED_VALUE = "N.D.(<%s)";
    public static final String DIOXIN_SOIL_UNIT = "pg";
    /** 下面这个数组不要随便修改和删除，否则很多分析项结果可能无法获取 */
    public static final String[] DIOXIN_ORIGINAL_RECORD_KEYWORDS = {"组份浓度", "换算浓度", "检出限", "平均含氧量", "基准含氧量", "换算后二噁英浓度", "回收率", "实际测定含量", "组份浓度直读"};
    public static final String[] DIOXIN_ITEM_GROUPS = {"多氯代二苯并-对-二噁英", "多氯代二苯并呋喃"};
    public static final String[] DIOXIN_ITEM_GROUP_KEYWORDS = {"CDD", "CDF"};
    public static final String DIOXIN_ORIGINAL_RECORD_REMARK = "[注]: N.D.指低于检出限，计算毒性当量浓度时以 1/2 检出限计。";
    public static final String DIOXIN_ORIGINAL_RECORD_AVG_OXYGEN_RESULT = "平均含氧量（%）";
    public static final String DIOXIN_ORIGINAL_RECORD_CONVERTED_RESULT = "%s含氧量换算后二噁英浓度";
    public static final String MONITOR_REPORT_RECORD_REMARK = "[注]：客户送样，仅对来样检测结果负责。";

    /** 二噁英报告-回收率统计 */
    public static final String DIOXIN_RECOVERY_RATE_TITLE = "附件 %s回收率统计";
    public static final String DIOXIN_RECOVERY_RATE_KEYWORD = "回收率";
    public static final String DIOXIN_PURIFY_STANDARD = "净化内标";
    public static final String DIOXIN_SAMPLING_STANDARD = "采样内标";
    public static final String DIOXIN_SAMPLING_STANDARD_ITEM = "37Cl4-2378-TCDD";
    public static final String[] DIOXIN_RECOVERY_RATE_TABLE_HEADER_TITLES = {
            "样品编号", "%s0", "",
            "项目", "", "回收率(%)"
    };
    public static final int DIOXIN_RECOVERY_RATE_TABLE_HEADER_COLUMN_QTY = 3;

    public static final String DIOXIN_GAS_STORE_CONDITION = "环境空气和废气样品的保存条件：采集到的样品应被贮存在密闭容器内以避免损失或被周围环境所污染,贮存时应避光，应冷藏贮存。";
    public static final String DIOXIN_WATER_STORE_CONDITION = "水质样品的保存条件：应使水质样品保存在4～10℃的暗冷处，水质样品的保存与管理应符合 GB/T 12999 中的一般性规定。";
    public static final String DIOXIN_SOIL_STORE_CONDITION = "土壤样品的保存条件：配有温度记录设备的冰箱专门用于接样后制样前样品的存放，保证样品在＜4℃的环境中存放。";
    public static final String DIOXIN_SOLID_WASTE_STORE_CONDITION = "固体样品的保存条件：采集到的样品应被贮存在密闭容器内以避免损失或被周围环境所污染，固体废物样品的保存与管理应符合HJ/T 20中的一般性规定。";

    /** 二噁英质控报告 - 实验室空白分析结果*/
    public static final String DIOXIN_QC_BLANK_RESULT_TITLE = "空白实验结果记录表";
    public static final String DIOXIN_QC_ND_REPLACED_VALUE = "N.D.";
    public static final String[] DIOXIN_SOIL_QC_BLANK_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s1", "%s2", "%s3", "%s4",
    };
    public static final String[] DIOXIN_GAS_QC_BLANK_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s1", "%s2", "%s3", "%s4",
    };
    public static final String[] DIOXIN_WATER_QC_BLANK_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "",
            "二噁英类", "", "检出限", "组份浓度", "毒性当量浓度", "",
            "", "", "%s1", "%s2", "%s3", "%s4",
    };


    /** 检测项目表标题，如：附表12 检测依据、仪器一览表 */
    public static final String DIOXIN_QC_MONITOR_ITEM_TABLE_TITLE = "检测依据、仪器一览表";

    /** 二噁英质控报告 - 平行双样分析结果*/
    public static final String DIOXIN_QC_DUP_RESULT_TITLE = "%s平行双样分析结果记录表";
    public static final String DIOXIN_QC_DUP_RESULT_QUALIFIED = "合格";
    public static final String[] DIOXIN_QC_DUP_RESULT_TABLE_HEADER_TITLES = {
            "检测日期", "样品类型", "实验室样品编号", "检测项目", "检测值A(ng/kg)", "检测值B(ng/kg)", "相对偏差RD%", "结果评价"
    };
    public static final int DIOXIN_QC_DUP_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final String DIOXIN_QC_DUP_DEFAULT_REMARK = "单次平行的实验结果应为平均值的±30%以内";

    /** 二噁英质控报告 - 平行双样分析合格率*/
    public static final String DIOXIN_QC_DUP_PASS_RATE_TITLE = "%s平行双样分析合格率记录表";
    public static final String[] DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_TITLES = {
            "报告日期", "样品类型", "检测项目", "批样品数", "合格样品数", "合格率(%)"
    };
    public static final int DIOXIN_QC_DUP_PASS_RATE_TABLE_HEADER_COLUMN_QTY = 6;

    /** 二噁英质控报告 - 有证标准物质检测结果*/
    public static final String DIOXIN_QC_STD_RESULT_TITLE = "有证标准物质检测结果记录表";
    public static final String DIOXIN_QC_STD_ORDER_CATEGORY = "标准品";
    public static final String[] DIOXIN_QC_STD_RESULT_TABLE_HEADER_TITLES = {
            "样品类型", "检测项目", "标准物质编号", "标准值及其不确定度(ng/ml)", "保证值范围(ng/ml)", "检测结果(ng/ml)", "结果评价"
    };
    public static final int DIOXIN_QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    /** 二噁英质控报告 - 检出限结果表*/
    public static final String DIOXIN_QC_LIMIT_RESULT_TITLE = "%s检出限结果表";
    //土壤
    public static final String[] DIOXIN_SOIL_QC_LIMIT_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "", "",
            "二噁英类", "", "样品检出限(ng/kg)", "毒性当量因子", "换算结果(ngTEQ/kg)", "判定依据(ngTEQ/kg)", "判定结果"
    };
    //固废
    public static final String[] DIOXIN_SOIL_WASTE_QC_LIMIT_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "", "",
            "二噁英类", "", "样品检出限(ug/kg)", "毒性当量因子", "换算结果(ugTEQ/kg)", "判定依据(ugTEQ/kg)", "判定结果"
    };
    //废气
    public static final String[] DIOXIN_GAS_WASTE_QC_LIMIT_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "", "",
            "二噁英类", "", "样品检出限(ng/Nm<sup>3</sup>)", "毒性当量因子", "换算结果(ngTEQ/Nm<sup>3</sup>)", "判定依据(ngTEQ/Nm<sup>3</sup>)", "判定结果"
    };
    //空气
    public static final String[] DIOXIN_GAS_QC_LIMIT_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "", "",
            "二噁英类", "", "样品检出限(pg/Nm<sup>3</sup>)", "毒性当量因子", "换算结果(pgTEQ/Nm<sup>3</sup>)", "判定依据(pgTEQ/Nm<sup>3</sup>)", "判定结果"
    };
    //水
    public static final String[] DIOXIN_WATER_QC_LIMIT_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "", "", "", "",
            "二噁英类", "", "样品检出限(pg/L)", "毒性当量因子", "换算结果(pgTEQ/L)", "判定依据(pgTEQ/L)", "判定结果"
    };
    public static final int DIOXIN_QC_LIMIT_RESULT_TABLE_HEADER_COLUMN_QTY = 7;
    public static final String DIOXIN_QC_SOIL_JUDGE_BASIC = "1.0";
    public static final String DIOXIN_QC_ORGANIZED_GAS_JUDGE_BASIC = "0.050";
    public static final String DIOXIN_QC_UNORGANIZED_GAS_JUDGE_BASIC = "0.060";
    public static final String DIOXIN_QC_ENV_GAS_JUDGE_BASIC = "0.060";
    public static final String DIOXIN_QC_SOLID_WASTE_JUDGE_BASIC = "0.030";
    public static final String DIOXIN_QC_WATER_JUDGE_BASIC = "3.0";
    public static final String DIOXIN_QC_FODDER_JUDGE_BASIC = "0.075";
    public static final String DIOXIN_QC_QUALIFIED = "合格";
    //public static final String DIOXIN_QC_SOIL_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×(1×10<sup>-5</sup>)mg/kg=1×10<sup>-6</sup>mg/kg";
    public static final String DIOXIN_QC_SOIL_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×10ng/kg=1.0ng/kg";
    public static final String DIOXIN_QC_ORGANIZED_GAS_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×0.50ng/Nm<sup>3</sup>=0.050ng/Nm<sup>3</sup>";
    public static final String DIOXIN_QC_UNORGANIZED_GAS_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×0.60pg/m<sup>3</sup>=0.060pg/m<sup>3</sup>";
    public static final String DIOXIN_QC_ENV_GAS_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×0.60pg/m<sup>3</sup>=0.060pg/m<sup>3</sup>";
    public static final String DIOXIN_QC_SOLID_WASTE_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×0.30μg/kg=0.030μg/kg";
    public static final String DIOXIN_QC_WATER_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×30pg/L=3.0pg/L";
    public static final String DIOXIN_QC_FODDER_REMARK = "判定样品检出限的依据为评价质量浓度的1/10以下，即1/10×0.75ng/kg=0.075ng/kg";

    /** 二噁英质控报告 - 加标回收实验结果表*/
    public static final String DIOXIN_QC_SPKC_RESULT_TITLE = "%s加标回收实验结果表";
    public static final String[] DIOXIN_QC_SPKC_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "%s0", "", "",
            "内标物名称", "回收率(%)", "判定依据(%)", "判定结果"
    };
    public static final int DIOXIN_QC_SPKC_RESULT_TABLE_HEADER_COLUMN_QTY = 4;

    /** 质控报告-检测项目 */
    public static final String QC_MONITOR_ITEM_TABLE_REPLACE_KEYWORD = "monitorItemTable";
    public static final String[] QC_MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "序号", "监测类别", "检测项目"
    };
    public static final int QC_MONITOR_ITEM__TABLE_HEADER_COLUMN_QTY = 3;

    /** 质控报告-质控数量 */
    public static final String QC_QUANTITY_TABLE_REPLACE_KEYWORD = "qcQuantityTable";
    public static final String[] QC_QUANTITY_TABLE_HEADER_TITLES = {
            "类别", "分析项目", "全程序空白", "", "运输空白", "", "实验室空白", "", "现场平行样", "", "实验室平行样", "", "质控样", "", "加标样", "",
            "", "", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)",
    };
    public static final int QC_QUANTITY__TABLE_HEADER_COLUMN_QTY = 16;
    /*public static final String[] QC_QUANTITY_TABLE_HEADER_TITLES = {
            "类别", "分析项目", "全程序空白", "", "运输空白", "", "现场平行样", "", "实验室平行样", "", "质控样", "", "加标样", "",
            "", "", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)", "数量", "占比(%)",
    };
    public static final int QC_QUANTITY__TABLE_HEADER_COLUMN_QTY = 14;*/


    /** 质控报告-检测方法设备 */
    public static final String QC_METHOD_DEVICE_TABLE_REPLACE_KEYWORD = "methodDeviceTable";
    public static final String QC_METHOD_DEVICE_TABLE_TITLE = "表3-%d %s检测方法及仪器一览表";
    public static final String[] QC_METHOD_DEVICE_TABLE_HEADER_TITLES = {
            "检测项目", "检测依据", "检出限", "单位", "检测仪器名称及型号", "仪器设备编号"
    };
    public static final int QC_METHOD_DEVICE_TABLE_HEADER_COLUMN_QTY = 6;

    /** 质控报告-空白样质控结果 */
    public static final String QC_RESULT_TABLE_REPLACE_KEYWORD = "qcResultTable";
    public static final String QC_RESULT_EFFECTIVE = "符合";
    public static final String QC_RESULT_INEFFECTIVE = "不符合";
    public static final String QC_BLANK_KEYWORD = "空白";
    public static final String QC_BLANK_RESULT_TABLE_TITLE = "表3-4-%d 空白样质控结果";
    public static final String[] QC_BLANK_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "空白类型", "检测项目", "单位", "检测结果", "符合性"
    };
    public static final String  QC_BLANK_RESULT_REPORT_DISPLAY = "样品浓度";
    public static final int QC_BLANK_RESULT_TABLE_HEADER_COLUMN_QTY = 6;

    /** 质控报告-密码（现场）平行样质控结果 */
    public static final String QC_PDUP_RESULT_TABLE_TITLE = "表3-4-%d 密码（现场）平行样质控结果";
    public static final String QC_PDUP_PH_KEYWORD = "PH";
    public static final String[] QC_PDUP_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "", "%s0", "%s1", "相对偏差%", "控制范围%", "符合性",
            "检测项目", "单位", "检测结果", "检测结果", "", "", ""
    };
    public static final String[] QC_PDUP_RESULT_TABLE_HEADER_TITLES_1 = {
            "样品编号", "", "%s0", "%s1", "差值", "允许差值", "符合性",
            "检测项目", "单位", "检测结果", "检测结果", "", "", ""
    };
    public static final int QC_PDUP_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    /** 质控报告-实验室平行样质控结果 */
    public static final String QC_LDUP_RESULT_TABLE_TITLE = "表3-4-%d 实验室平行样质控结果";
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "相对偏差%", "控制范围%", "符合性"
    };
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES_1 = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "差值", "允许差值", "符合性"
    };
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES_2 = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "相对偏差%", "绝对偏差", "标准差", "控制范围%", "符合性"
    };
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES_3 = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "相对偏差%", "控制范围%", "符合性"
    };
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES_4 = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "绝对偏差", "控制范围%", "符合性"
    };
    public static final String[] QC_LDUP_RESULT_TABLE_HEADER_TITLES_5 = {
            "样品编号", "检测项目", "单位", "原样结果", "平行样结果", "相对标准偏差", "控制范围%", "符合性"
    };
    public static final int QC_LDUP_RESULT_TABLE_HEADER_COLUMN_QTY = 8;
    public static final int QC_LDUP_RESULT_TABLE_HEADER_COLUMN_QTY_2 = 10;

    /** 质控报告-加标样质控结果 */
    public static final String QC_LSPKC_RESULT_TABLE_TITLE = "表3-4-%d 加标样质控结果";
    public static final String QC_LSPKC_KEYWORD = "加标";
    public static final String[] QC_LSPKC_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "检测项目", "单位", "检测结果", "加标量", "加标回收率%", "控制范围%", "符合性"
    };
    public static final int QC_LSPKC_RESULT_TABLE_HEADER_COLUMN_QTY = 8;

    /** 质控报告-替代物回收率质控结果 */
    public static final String QC_RECOVERY_RATE_RESULT_TABLE_TITLE = "表3.4 替代物回收率控制结果";
    public static final String[] QC_RECOVERY_RATE_RESULT_TABLE_HEADER_TITLES = {
            "分析项目", "替代物名称", "回收率%", "", "控制范围%", "符合性",
            "", "", "最低", "最高", "", ""
    };
    public static final int QC_RECOVERY_RATE_RESULT_TABLE_HEADER_COLUMN_QTY = 6;

    /** 质控报告-实验室标准样品 */
    public static final String QC_STD_RESULT_TABLE_TITLE = "表3-4-%d 质控样控制结果";
    public static final String[] QC_STD_RESULT_TABLE_HEADER_TITLES = {
            "样品编号", "标液编号", "检测项目", "单位", "检测结果", "控制范围", "符合性"
    };
    public static final int QC_STD_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    public static final String QC_SAMPLING_DESC = "现场采样、";
    public static final String QC_SAMPLING_ENV = "采样、运输、";

    public static final String SAMPLECONCENTRATION = "样品浓度";// 样品浓度

    public static final String SOLIDWASTE = "固体废物";// 固体废物

    public static final String GAS_PARAMETER_0 = "排气筒高度";

    public static final String GAS_PARAMETER_1 = "截面积";

    public static final String GAS_PARAMETER_2 = "含氧量";

    public static final String GAS_PARAMETER_3 = "烟温";

    public static final String GAS_PARAMETER_4 = "含湿量";

    public static final String GAS_PARAMETER_5 = "平均流速";

    public static final String GAS_PARAMETER_6 = "标干风量";

    public static final String[] GAS_PARAMETER_lIST = {
            "排气筒高度", "截面积", "含氧量", "烟温", "含湿量", "平均流速", "标干风量"
    };

    public static final String DRY_MATTER_CONCENT_I = "干物质含量-I";

    public static final String SOLID_WASTE_WORD = "固体废物";

    public static final String EDIT_PROJECT_AMOUNT_LOG = "##########项目修改金额发送CRM接口-PROJECTEANOTE #####BEGIN";

    public static final String EDIT_PROJECT_AMOUNT_END_LOG = "##########项目修改金额发送CRM接口-PROJECTEANOTE #####END";

    public static final String EDIT_PROJECT_AMOUNT_SUCCESS = "**发送成功SUCCESS**";

    public static final String EDIT_PROJECT_AMOUNT_FAIL = "**发送失败FAIL**";

    public static final String ORDERCATEGORY_GAS = "气";

    public static final String PARTICULATE_MATTER = "颗粒物";

    public static final String SAMPLE_BEGIN_DATE = "采样开始时间";

    public static final String SOIL_SITE_SURVEY = "《土壤环境质量建设用地土壤污染风险管控标准（试行)》（GB36600-2018）";

    public static final String WATER_SITE_SURVEY = "地下水质量标准（GB/T 14848-2017）";

    public static final String SITE_SURVEY = "场地调查监测";

    public static final String SOIL_FILTER_VALUE_INDEX1 = "表1 建设用地土壤污染风险筛选值和管制值(基本项目)";

    public static final String SOIL_FILTER_VALUE_INDEX2 = "筛选值";

    public static final String SOIL_FILTER_VALUE_INDEX3 = "第二类用地";

    public static final String SOIL_CONTROL_VALUE_INDEX1 = "表1 建设用地土壤污染风险筛选值和管制值(基本项目)";

    public static final String SOIL_CONTROL_VALUE_INDEX2 = "管制值";

    public static final String SOIL_CONTROL_VALUE_INDEX3 = "第二类用地";

    public static final String WATER_LIMIT_VALUE_INDEX1 = "地下水质量常规指标及限值";

    public static final String WATER_LIMIT_VALUE_INDEX2 = "Ⅰ类";

    public static final String UNDO_AUDITED = "回退已提交";






}
