package com.sunwayworld.emc.framework.constant;

import java.util.regex.Pattern;

/**
 * EMC 经常使用的常量类，特殊用途的常量类不在此处定义
 */
public class EmcConstant {
    /** 默认数字 **/
    public static final Long ONE = 1L;

    /** 空字符串显示 **/
    public static final String N_A = "N/A";

    public static final String CURVE_NO_PREFIX_A = "A";

    /** 小于检出低限 */
    public static final String ND = "ND";
    /** 大于检出高限 */
    public static final String HD = "HD";

    /** 默认下限符号 **/
    public static final String LOW_LIMIT_DEFAULT_SYMBOL = "3";
    /** 默认上限符号 **/
    public static final String HIGH_LIMIT_DEFAULT_SYMBOL = "2";

    /****************************************************
     * 修约规则名称、描述
     ****************************************************/
    /** 修约 - ISO **/
    public static final String REVISION_RULE_ISO = "ISO";
    /** 修约 - EPA **/
    public static final String REVISION_RULE_EPA = "EPA";
    /** 修约 - FDA **/
    public static final String REVISION_RULE_FDA = "FDA";
    /** 修约 - EMC **/
    public static final String REVISION_RULE_EMC = "EMC";
    /** 修约 - 无 **/
    public static final String REVISION_RULE_NONE = "NONE";
    /** 修约 - 无 - 描述 **/
    public static final String REVISION_RULE_NONE_DESC = "无修约规则";
    /** 修约 - ISO - 描述 **/
    public static final String REVISION_RULE_ISO_DESC = "如果大于5，则最后的有效数字+1；如果小于5，则舍去；例如：设 小数为3位，则150.2637=150.264和174.2834=174.283";
    /** 修约 - EPA - 描述 **/
    public static final String REVISION_RULE_EPA_DESC = "如果大于或等于5，则有效数字最后一位+1，其余非有效数字将被删除 （如果是在小数点右边）或是被换成0（如果在小数点左边）。例如： 如果设置有效数字为5位，那么175.6637=175.66；20673986=20674000";
    /** 修约 - FDA - 描述 **/
    public static final String REVISION_RULE_FDA_DESC = "当尾数小于或等于4时，直接将尾数舍去；当尾数大于或等于6时，将尾数 舍去并向前一位进位；当尾数为5，而尾数后面的数字均为0时，应看尾数 “5”的前一位：若前一位数字此时为奇数，就向前进一位；若前一位数字 此时为偶数，则将尾数舍去，数字“0”在此时被视为偶数；当尾数为5，而 尾数“5”的后面还有任何不是0的数字时，无论前一位在此时为奇数还是偶数， 也无论“5”后面不为0的数字在哪一位上，都向前进一位";
    /** 修约 - EMC - 描述 **/
    public static final String REVISION_RULE_EMC_DESC = "当尾数小于或等于4时，直接将尾数舍去；当尾数大于或等于6时，将尾数舍去 并向前一位进位；当尾数为5，而尾数后面的数字均为0时，应看尾数“5”的前 一位：若前一位数字此时为奇数，就向前进一位；若前一位数字此时为偶数， 则将尾数舍去，数字“0”在此时被视为偶数；当尾数为5，而尾数“5”的后面 还有任何不是0的数字时，无论前一位在此时为奇数还是偶数，也无论“5”后面 不为0的数字在哪一位上，都向前进一位";

    /****************************************************
     * QC样品
     ****************************************************/
    public static final String QC_L_DUP = "L-DUP";// 实验室平行
    public static final String QC_P_DUP = "P-DUP";// 现场平行

    /****************************************************
     * 锁定相关
     ****************************************************/
    public static final String LOCKED = "locked";// 锁定
    public static final String UNLOCK = "unlock";// 解锁

    /****************************************************
     * PN函数
     ****************************************************/
    public static final String PN_DAYAVERAGE = "dayaverage";// 日均值
    public static final String PN_SAMPLE = "sample";// 样品值
    public static final String PN_PERAVERAGE = "peraverage";// 次均值
    public static final String PN_TOTALAVERAGE = "totalaverage";// 总均值

    /****************************************************
     * 分析项目--状态
     ****************************************************/
    public static final String ANALYSIS_PROJECT_PRETRIAL = "pretrial";// 分析预审
    public static final String ANALYSIS_PROJECT_AUDIT = "audit";// 分析审核
    public static final String ANALYSIS_PROJECT_AUDITQUERY = "auditQuery";// 审核查询
    public static final String ANALYSIS_PROJECT_DRAFT = "draft";// 草稿

    /** 制单列数组，主要用于Bean复制时忽略制单 **/
    public static final String[] CREATED_COLUMNS = new String[] { "createdById", "createdByName", "createdTime", "createdByOrgId", "createdByOrgName" };

    /****************************************************
     * 仪器采集-预处理方式
     ****************************************************/
    /** 删除 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_DELETE = "delete";
    /** 删除行 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_DELETE_LINE = "deleteLine";
    /** 替换 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_REPLACE = "replace";
    /** 替换成空格 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_REPLACE_TO_SPACE = "replaceToSpace";
    /** 正则替换 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_REGULAR_REPLACE = "regularReplace";
    /** 正则替换成空格 **/
    public static final String EQUIPT_ACQ_PRE_ANALY_REGULAR_REPLACE_TO_SPACE = "regularReplaceToSpace";

    /****************************************************
     * 仪器采集-采集参数编码
     ****************************************************/
    /** 结果 **/
    public static final String EQUIPT_ACQ_PARAM_RESULT = "result";
    /** 样品编号 **/
    public static final String EQUIPT_ACQ_PARAM_ORDER_NUMBER = "orderNumber";
    /** 段落 **/
    public static final String EQUIPT_ACQ_PARAM_PARAGRAPH = "paragraph";
    /** 测试名称 **/
    public static final String EQUIPT_ACQ_PARAM_TEST_NAME = "testName";
    /** 仪器编号 **/
    public static final String EQUIPT_ACQ_PARAM_EQUIPT_NO = "equiptNo";
    /** 文件编号 **/
    public static final String EQUIPT_ACQ_PARAM_FILE_NO = "fileNo";

    /****************************************************
     * 仪器采集-数据格式
     ****************************************************/
    /** 单段 **/
    public static final String EQUIPT_ACQ_DATA_FORMAT_SINGLE = "single";
    /** 多段 **/
    public static final String EQUIPT_ACQ_DATA_FORMAT_MULTIPLE = "multiple";

    /****************************************************
     * 仪器采集-样品编号格式
     ****************************************************/
    /** 单个 **/
    public static final String EQUIPT_ACQ_ORDER_NO_FORMAT_SINGLE = "single";
    /** 列表 **/
    public static final String EQUIPT_ACQ_ORDER_NO_FORMAT_MULTIPLE = "multiple";

    /****************************************************
     * 仪器采集-结果格式
     ****************************************************/
    /** 列 **/
    public static final String EQUIPT_ACQ_RESULT_FORMAT_LINE = "line";
    /** 表格 **/
    public static final String EQUIPT_ACQ_RESULT_FORMAT_TABLE = "table";

    /****************************************************
     * 仪器采集-解析方式
     ****************************************************/
    /** 优先解析顺序 **/
    public static final String ANALYSIS_WAY_PRIOR_SEQUENCE = "sequence";
    /** 优先解析数据 **/
    public static final String ANALYSIS_WAY_PRIOR_DATA = "data";

    /****************************************************
     * 仪器采集-脚本类型
     ****************************************************/
    /** 通用脚本 **/
    public static final String COMMON_SCRIPT = "acq-script";
    /** 特殊脚本 **/
    public static final String SPECIAL_SCRIPT = "file-script";

    /****************************************************
     * 文件类型
     ****************************************************/
    /** PDF **/
    public static final String FILECATAEGORY_PDF = "pdf";
    /** XLSX **/
    public static final String FILECATAEGORY_XLSX = "xlsx";
    /** XLS **/
    public static final String FILECATAEGORY_XLS = "xls";

    /****************************************************
     * 仪器采集-文件是否解析出数据
     ****************************************************/
    /** 没有解析 **/
    public static final String EQUIPT_ACQ_FILE_STATUS_NONE = "0";
    /** 解析出数据了 **/
    public static final String EQUIPT_ACQ_FILE_STATUS_DONE = "1";
    /** 仪器采集脚本ID **/
    public static final Long EQUIPT_ACQ_SCRIPT_ID = 1531337609609632L;

    /****************************************************
     * 校准曲线-操作类型
     ****************************************************/
    /** 复制 **/
    public static final String CURVE_COPY = "copy";
    /** 编辑 **/
    public static final String CURVE_EDIT = "edit";

    /****************************************************
     * 数据汇总-类型
     ****************************************************/
    /** 项目 **/
    public static final String SUMMARY_TYPE_PROJECT = "project";
    /** 批 **/
    public static final String SUMMARY_TYPE_RUN = "run";
    /** 点位 **/
    public static final String SUMMARY_TYPE_FOLDER = "folder";

    /****************************************************
     * qc类型-质控使用类型
     ****************************************************/
    /** 现场质控 **/
    public static final String QCCATEGORY_USECATEGORY_ORDERACCEPT = "orderAccept";
    /** 实验室质控 **/
    public static final String QCCATEGORY_USECATEGORY_LAB = "lab";
    /** 样品接受质控 **/
    public static final String QCCATEGORY_USECATEGORY_SCENE = "scene";

    /****************************************************
     * qc类型-qc类型编码
     ****************************************************/
    /** 实验室空白 **/
    public static final String QCCATEGORY_QCCATEGORYID_L_BLANK = "L-BLANK";
    /** 实验室标准样品 **/
    public static final String QCCATEGORY_QCCATEGORYID_QC_STD = "QC-STD";
    /** 空白加标 **/
    public static final String QCCATEGORY_QCCATEGORYID_L_SPKCK = "L-SPKCK";
    /** 样品加标 **/
    public static final String QCCATEGORY_QCCATGORYID_L_SPKC = "L-SPKC";

    /** 现场空白 **/
    public static final String QCCATEGORY_QCCATEGORYID_P_BLANK = "P-BLANK";
    /** 现场标准样品 **/
    public static final String QCCATEGORY_QCCATEGORYID_PQC_STD = "PQC-STD";
    /** 现场平行 **/
    public static final String QCCATEGORY_QCCATEGORYID_P_DUP = "P-DUP";
    /** 密码平行 **/
    public static final String QCCATEGORY_QCCATEGORYID_Q_DUP = "Q-DUP";


    /****************************************************
     * word报告模板-文件名称
     ****************************************************/
    /** 委托报告模板（无评价）.docx **/
    public static final String WORD_REPORT_TEMPLATE_ENTRUST_REPORT_NO_EVALUATION = "委托报告模板（无评价）.docx";
    /** 委托报告模板（带评价）.docx **/
    public static final String WORD_REPORT_TEMPLATE_ENTRUST_REPORT_WITH_EVALUATION = "委托报告模板（带评价）.docx";
    /** 监督性监测模板.docx **/
    public static final String WORD_REPORT_TEMPLATE_SUPERVISAL_MONITORING = "监督性监测模板（废水、废气、噪声）.docx";
    /** 送样报告模板.docx **/
    public static final String WORD_REPORT_TEMPLATE_SAMPLING_REPORT = "送样报告模板.docx";
    /** 常规报告板式除去噪声模板.docx **/
    public static final String WORD_REPORT_TEMPLATE_NORMAL_REPORT_WITHOUT_NOISE = "常规报告板式除去噪声模板.docx";
    /** 常规报告板式噪声模板.docx **/
    public static final String WORD_REPORT_TEMPLATE_NORMAL_REPORT_NOISE = "常规报告板式噪声模板.docx";

    /** 数据表文件名，比如：DAJC206020955-数据表 */
    public static final String DEFAULT_DATA_SHEET_FILE_NAME = "DAJC-数据表-%s.xls";
    /** RS比对报告文件名，比如：DAJC206020955-比对报告 */
    public static final String DEFAULT_RS_REPORT_FILE_NAME = "DAJC-比对报告-%s.pdf";
    /** 数据表附件类型 */
    public static final String DATE_SHEET_FILE_BIZ_CATEGORY = "dataSheet";
    /** RS报告附件类型 */
    public static final String RS_REPORT_FILE_BIZ_CATEGORY = "rsReport";

    /****************************************************
     * 昼夜评价-属性模板固定ID，此处固定为 100
     ****************************************************/
    public static final String DAY_NIGHT_EVAL_PROPERTY_TMPL_ID = "100";

    /****************************************************
     * 昼夜评价-属性模板固定模糊名称
     ****************************************************/
    public static final String DAY_NIGHT_EVAL_PROPERTY_TMPL_NAME = "昼夜";

    /****************************************************
     * 内插评价-属性模板固定模糊名称
     ****************************************************/
    public static final String INTERPOLATE_EVAL_PROPERTY_TMPL_NAME = "内插";

    /****************************************************
     * 综合信息计算
     ****************************************************/
    public static final String INFO_DATA_CALCULATE_PH = "ph";
    public static final String INFO_DATA_CALCULATE_TYPE_DAY = "day";
    public static final String INFO_DATA_CALCULATE_TYPE_MONTH = "month";
    public static final String INFO_DATA_CALCULATE_TYPE_HALF_YEAR = "halfYear";
    public static final String INFO_DATA_CALCULATE_TYPE_YEAR = "year";
    public static final String INFO_DATA_CALCULATE_WATER_QUALITY_CATEGORY = "劣V5";
    public static final String INFO_DATA_CALCULATE_O = "溶解氧";
    public static final String INFO_DATA_CALCULATE_CHLA = "叶绿素";
    public static final String INFO_DATA_CALCULATE_TP = "总磷";
    public static final String INFO_DATA_CALCULATE_TN = "总氮";
    public static final String INFO_DATA_CALCULATE_SD = "硒";
    public static final String INFO_DATA_CALCULATE_CODMN = "高锰酸盐指数";
    public static final String INFO_DATA_CATEGORY_INDEX_VALUE = "地表水";
    public static final String INFO_DATA_BEGIN_DATE = "-01-01";
    public static final String INFO_DATA_END_DATE = "-06-30";

    /****************************************************
     * 工作流提交信息
     ****************************************************/
    public static final String PROCESS_START = "提交";

    /****************************************************
     * 附件管理
     ****************************************************/
    public static final String ATTACHMENT_TARGETID = "T_EMC_HOME_ATTACHMENT$";
    public static final String ATTACHMENT_TARGETID_DELETE = "X";

    /****************************************************
     * 测试管理分析项默认值
     ****************************************************/
    public static final String EMC_ITEM_RESULT_CATEGORY_NUMBER = "number"; //结果类型 数字型
    public static final String EMC_ITEM_LIMIT_FLAG_LESS_OR_EQUAL_LOW = "1"; //检出限标志<=low
    public static final String EMC_ITEM_PNFUNC_SAMPLE = "sample"; //PN函数类型 sample

    /****************************************************
     * 导入导出
     ****************************************************/
    public static final String EMC_EXPORT_CLIENT = "监测点管理导入模板";
    public static final String EMC_EXPORT_METHOD = "方法管理导入模板";
    public static final String EMC_EXPORT_METHOD_UPDATE = "方法管理数据更新导入模板";
    public static final String EMC_EXPORT_TEST_METHOD = "测试管理导入模板";
    public static final String EMC_EXPORT_TEST = "测试库导入模板";
    public static final String EMC_EXPORT_TEST_UPDATE = "测试库数据更新导入模板";
    public static final String EMC_EXPORT_USER = "用户管理导入模板";
    public static final String EMC_EXPORT_EMP = "人员管理导入模板";
    public static final String EMC_EXPORT_MTL = "材料管理导入模板";
    public static final String EMC_EXPORT_MTL_INIT = "材料管理初始化导入模板";
    public static final String EMC_EXPORT_MTL_STOCK = "库存导入模板";
    public static final String EMC_EXPORT_DCU_EAS_RESULT = "仪器采集结果模板";
    public static final String EMC_EXPORT_PURCHAPPLY = "采购明细导入模板";
    public static final String EMC_EXPORT_EMCCONSUME = "耗材导入模板";

    /****************************************************
     * 合同管理
     ****************************************************/
    public static final String EMC_REVIEW_CONTENT_NAME_TEST_REPORT = "检测报告";
    public static final String EMC_REVIEW_CONTENT_TYPE_TEST_REPORT = "任务期限";

    public static final String EMC_REVIEW_CONTENT_NAME_MAINTAIN_SECRECY = "保密要求";
    public static final String EMC_REVIEW_CONTENT_TYPE_MAINTAIN_SECRECY = "保密要求";

    public static final String EMC_REVIEW_CONTENT_NAME_SPECIAL_REQUIREMENT = "特殊要求";
    public static final String EMC_REVIEW_CONTENT_TYPE_SPECIAL_REQUIREMENT = "双方特殊要求";

    public static final String EMC_REVIEW_CONTENT_NAME_ALTERATION = "变更或偏离要求";
    public static final String EMC_REVIEW_CONTENT_TYPE_ALTERATION = "合同变更或偏离要求";

    public static final String EMC_REVIEW_CONTENT_NAME_DRUG_REAGENT = "药品试剂";
    public static final String EMC_REVIEW_CONTENT_TYPE_DRUG_REAGENT = "技术要求";

    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_QUALIFICATION = "检测资质";
    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_TESTER = "检测人员";
    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_CONSUMABLE = "耗材耗品";
    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_TEST_METHOD = "检测方法";
    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_TEST_EQUIPT = "仪器设备";
    public static final String EMC_REVIEW_CONTENT_NAME_TESTING_TEST_REFERENCE_MATERIAL = "标准物质";
    public static final String EMC_REVIEW_CONTENT_TYPE_TECHNICAL_REQUIREMENT = "技术要求";

    public static final String EMC_REVIEW_CONTENT_NAME_OUTLAY = "经费保障";
    public static final String EMC_REVIEW_CONTENT_TYPE_OUTLAY = "经费";

    /****************************************************
     * 易标准对接
     ****************************************************/
    public static final String ESTANDARD_INSERT = "新增";
    public static final String ESTANDARD_UPDATE = "更新";

    /** 监测/检测方案报表模板关键字 */
    public static final String SCHEME_REPORT_KEYWORD = "检测方案";

    /****************************************************
     * Word报告
     ****************************************************/
    /** Word报告附件类型 */
    public static final String WORD_REPORT_ATTACHMENT_TYPE = "wordReport";

    /** N/A仪器 */
    public static final String EQUIPMENT_NA = "N/A";

    /** 小时分钟正则表达式 */
    public static final Pattern TIME_PATTERN = Pattern.compile("([0-1][0-9]|2[0-3]):([0-5][0-9])");

    /** 土壤报告中的ND替代值 */
    public static final String NOT_CHECK_OUT = "未检出";
    /** PH */
    public static final String PH = "PH";

    /** 特殊测试处理 - 细菌总数（小于检出限时，报<检出限，不报检出限+L） */
    public static final String SPECIAL_TEST_OF_BACTERIUM = "细菌总数";

    /** 样品来源 */
    public static final String SAMPLING = "采样";
    public static final String DELIVERING = "送样";

    /** 受检单位描述 */
    public static final String TEST_ORG_NAME_DESC = "对%s";

    /** 综合Word报告模板 */
    public static final String COMPLEX_WORD_REPORT_TEMPLATE = "DAJC-综合报告.docx";

    /** 油烟Word报告模板 */
    public static final String LAMPBLACK_WORD_REPORT_TEMPLATE = "DAJC-油烟报告.docx";

    /** 场调土壤报告 */
    public static final String FACTORY_SOIL_WORD_REPORT_TEMPLATE = "DAJC-场调土壤报告.docx";

    /** 场调地下水报告 */
    public static final String FACTORY_GROUNDWATER_WORD_REPORT_TEMPLATE = "DAJC-场调地下水报告.docx";

    /** 油气回收报告 */
    public static final String OIL_GAS_RECOVERY_WORD_REPORT_TEMPLATE = "DAJC-油气回收报告.docx";

    /** 振动报告 */
    public static final String VIBRATION_WORD_REPORT_TEMPLATE = "DAJC-振动报告.docx";

    /** 送样报告 */
    public static final String COLLECTED_SAMPLE_WORD_REPORT_TEMPLATE = "DAJC-送样报告.docx";

    /** 综合Word报告模板(验收业务) */
    public static final String CHECK_COMPLEX_WORD_REPORT_TEMPLATE = "DAJC-综合报告(验收业务).docx";

    /** 默认统一的样品描述 */
    public static final String DEFAULT_ORDER_DESCRIPTION = "样品保存完好，符合检测要求，详见检测结果";

    /** 水对应的监测模板组ID */
    public static final long WATER_MONITOR_TMPL_GROUP_ID = 13L;
    /** 气对应的监测模板组ID */
    public static final long GAS_MONITOR_TMPL_GROUP_ID = 16L;
    /** 土壤对应的监测模板组ID */
    public static final long SOIL_MONITOR_TMPL_GROUP_ID = 15L;
    /** 噪声对应的监测模板组ID */
    public static final long NOISE_MONITOR_TMPL_GROUP_ID = 14L;

    /** 表格头动态标题占位符前缀为 %s */
    public static final String TABLE_HEADER_DYNAMIC_TITLE_PLACEHOLDER_PREFIX = "%s";

    /** 检测项目表格占位符 */
    public static final String MONITOR_TABLE_PLACEHOLDER = "monitorTable";

    /** 检测结果动态表格占位符 */
    public static final String DYNAMIC_TABLE_PLACEHOLDER = "resultTables";

    /** null替换值 */
    public static final String NULL_REPLACED_VALUE = "/";

    /** 结果表中的日期格式 */
    public static final String TABLE_DATE_PATTERN = "yyyy.MM.dd";

    /** 检测结果表格结束提示信息 */
    public static final String TABLE_END_TIP_MESSAGE = "（本页以下空白）";

    /** 换页起始标记 */
    public static final String NEW_PAGE_START_PLACEHOLDER = "newPageStart";

    /** 备注 */
    public static final String REMARK = "备注";

    public static final String PARAMETER_TEST_MATCH_KEYWORD = "参数";

    /** 土壤中的干物质测试 */
    public static final String DRY_MATTER_TEST_MATCH_KEYWORD = "干物质";

    public static final String NO_UNIT = "无量纲";

    /** 检测项目表格标题, "" 代表会被合并单元格的 */
    public static final String[] MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "检测项目", "检测方法及依据", "采样设备", "检测设备名称及编号", "检出限"
    };
    /** 检测项目结果表头列数 */
    public static final int MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 5;

    /** 送样检测项目表格标题, "" 代表会被合并单元格的 */
    public static final String[] COLLECTED_SAMPLE_MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "检测项目", "检测方法及依据", "检测设备名称及编号", "检出限"
    };
    /** 送样检测项目结果表头列数 */
    public static final int COLLECTED_SAMPLE_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 4;

    /** 水检测结果表标题，如：水质检测结果(1) */
    public static final String WATER_RESULT_TABLE_TITLE = "%s检测结果 (%d)";
    /** 水检测结果表头标题, 固定为3行5列表，其中 %s 代表需要动态赋值的, "" 代表会被合并单元格的 */
    public static final String[] WATER_RESULT_TABLE_HEADER_TITLES = {
            "检测点位", "%s0", "采样日期", "%s1", "",
            "样品类别", "%s2", "样品状态", "%s3", "",
            "检测项目", "样品编号", "单位", "测定值", "检测结果"
    };
    /** 水检测结果表头列数 */
    public static final int WATER_RESULT_TABLE_HEADER_COLUMN_QTY = 5;

    /** 气检测结果表标题，如：固定污染源检测结果报告单 */
    public static final String GAS_RESULT_TABLE_TITLE = "%s检测结果报告单";
    /** 气检测结果表头标题, 固定为2行10列，其中 "" 代表会被合并单元格的 */
    public static final String[] GAS_RESULT_TABLE_HEADER_TITLES = {
            "检测点位", "检测项目", "样品编号", "检测结果", "", "", "", "", "", "",
            "", "", "", "实测浓度(mg/m<sup>3</sup>)", "折算浓度(mg/m<sup>3</sup>)", "标干流量(m<sup>3</sup>/h)", "排放速率(kg/h)", "氧含量(%)", "实测/折算浓度平均值(mg/m<sup>3</sup>)", "排放速率平均值(kg/h)"
    };
    /** 匹配分析项名称关键字 */
    public static final String[] GAS_RESULT_MATCH_ITEM_KEYWORDS = {
            "实测浓度", "折算浓度", "标干流量", "排放速率"
    };
    /** 匹配其他测试中分析项名称关键字 */
    public static final String GAS_MATCH_OTHER_TEST_ITEM_KEYWORD = "氧含量";
    /** 气检测结果表头列数 */
    public static final int GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 10;

    /** 无组织废气匹配关键字 */
    public static final String UNORGANIZED_GAS_KEYWORD = "无组织";
    /** 无组织废气检测结果表标题，如：无组织废气检测结果 */
    public static final String UNORGANIZED_GAS_RESULT_TABLE_TITLE = "%s废气检测结果";
    /** 无组织废气检测结果表头标题, 固定为2行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES = {
            "检测项目", "检测点位", "样品编号及检测结果（mg/m<sup>3</sup>）", "",
            "", "", "样品编号", "检测结果"
    };
    /** 无组织废气检测结果表头列数 */
    public static final int UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 4;
    /** 无组织废气检测点位布置示意图 */
    public static final String UNORGANIZED_GAS_POINT_DIAGRAM_TITLE = "无组织废气检测点位布置示意图";

    /** 气象参数检测结果表标题，如：气象参数检测结果 */
    public static final String METEOROLOGY_PARAMETER_RESULT_TABLE_TITLE = "气象参数检测结果";
    /** 气象参数检测结果表头标题, 固定为1行7列，其中 "" 代表会被合并单元格的 */
    public static final String[] METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "天气情况", "风向", "风速(m/s)", "相对湿度(%)", "温度(°C)", "大气压力(kPa)"
    };
    /** 用于匹配分析项的名称 */
    public static final String[] METEOROLOGY_PARAMETER_SEARCH_TITLES = {
            "风向", "风速", "相对湿度", "温度", "大气压力"
    };
    /** 气象参数检测结果表头列数 */
    public static final int METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY = 7;

    /** 土检测结果表标题，如：土壤检测结果、固体废弃物检测结果 */
    public static final String SOIL_RESULT_TABLE_TITLE = "%s检测结果 (%d)";
    /** 砂砾含量，比如：砂砾含量5% */
    public static final String SOIL_GRAVEL_PERCENT = "砂砾含量%s";
    /** 土检测结果表头标题, 固定为3行5列，其中 "" 代表会被合并单元格的 */
    public static final String[] SOIL_RESULT_TABLE_HEADER_TITLES = {
            "检测点位", "%s0", "采样日期", "%s1", "",
            "样品类别", "%s2", "样品状态", "%s3", "",
            "采样点位", "检测项目", "样品编号", "单位", "检测结果"
    };
    /** 土检测结果表头列数 */
    public static final int SOIL_RESULT_TABLE_HEADER_COLUMN_QTY = 5;

    /** 噪声检测结果表标题，如：环境噪声检测结果 */
    public static final String NOISE_RESULT_TABLE_TITLE = "%s检测结果";
    /** 噪声检测结果表头标题, 固定为4行6列，其中 "" 代表会被合并单元格的 */
    public static final String[] NOISE_RESULT_TABLE_HEADER_TITLES = {
            "检测项目", "", "%s0", "", "检测日期", "%s1",
            "天气情况", "", "%s2", "", "风速（m/s）", "%s3",
            "检测点位", "主要噪声源", "检测结果(dB(A))", "", "", "",
            "", "", "检测时间", "昼间", "检测时间", "夜间"
    };
    /** 噪声检测结果表头列数 */
    public static final int NOISE_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    /** 噪声检测点位布置示意图 */
    public static final String NOISE_POINT_DIAGRAM_TITLE = "噪声检测点位布置示意图";
    /** 需要显示在列上的分析项 */
    public static final String MAIN_NOISE_SOURCE = "主要声源";

    /** 油烟检测结果表标题，如油烟检测结果 */
    public static final String LAMPBLACK_RESULT_TABLE_TITLE = "油烟检测结果报告单";
    /** 油烟检测结果表头标题, 固定为4行6列，其中 "" 代表会被合并单元格的 */
    public static final String[] LAMPBLACK_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "", "%s0", "分析日期", "", "%s1",
            "排气罩投影面积(m<sup>2</sup>)", "", "%s2", "规模", "", "%s3",
            "检测点位", "检测频次", "样品编号", "检测结果", "", "",
            "", "", "", "标干流量(m<sup>3</sup>/h)", "排放浓度(mg/m<sup>3</sup>)", "平均排放浓度(mg/m<sup>3</sup>)"
    };
    /** 油烟检测结果表头列数 */
    public static final int LAMPBLACK_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    /** 匹配油烟样品类型关键字 */
    public static final String LAMPBLACK_ORDER_CATEGORY_KEYWORD = "油烟";
    /** 检测频次 */
    public static final String LAMPBLACK_DETECTION_FREQUENCY = "第%s次";
    /** 匹配排气罩投影面积分析项 */
    public static final String LAMPBLACK_VENT_HOOD_MATCH_ITEM_KEYWORD = "排气罩投影面积";
    /** 匹配规模分析项 */
    public static final String LAMPBLACK_SCALE_MATCH_ITEM_KEYWORD = "规模";
    /** 用于匹配分析项的名称 */
    public static final String[] LAMPBLACK_SEARCH_TITLES = {
            "标干流量", "排放浓度"
    };
    /** 无效的油烟结果 */
    public static final String LAMPBLACK_INVALID_RESULT = "%s(无效)";

    /** 臭气浓度检测结果表标题，如臭气浓度检测结果 */
    public static final String STINK_RESULT_TABLE_TITLE = "臭气浓度检测结果报告单";
    /** 臭气浓度检测结果表头标题, 固定为4行6列，其中 "" 代表会被合并单元格的 */
    public static final String[] STINK_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "%s0", "", "分析日期", "%s1",
            "检测点位", "检测项目", "样品编号", "检测结果", "最大测定值"
    };
    /** 臭气浓度检测结果表头列数 */
    public static final int STINK_RESULT_TABLE_HEADER_COLUMN_QTY = 5;
    /** 匹配臭气浓度检测项目关键字 */
    public static final String STINK_TEST_KEYWORD = "臭气浓度";

    /** 委托的土壤检测结果表标题，如土壤检测结果 */
    public static final String ENTRUSTED_SOIL_RESULT_TABLE_TITLE = "土壤检测结果";
    /** 委托的土壤检测结果表头标题, 固定为3行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] ENTRUSTED_SOIL_RESULT_TABLE_HEADER_TITLES0 = {
            "检测项目", "检测结果", "", "",
            "", "%s0", "", "",
            "采样深度(cm)", "%s1", "%s2", "%s3"
    };
    public static final String[] ENTRUSTED_SOIL_RESULT_TABLE_HEADER_TITLES = {
            "采样点位", "%s0", "%s2", "%s4",
            "采样深度(cm)", "%s1", "%s3", "%s5",
            "检测项目", "检测结果", "检测结果", "检测结果"
    };
    /** 委托的土壤检测结果表头列数 */
    public static final int ENTRUSTED_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY = 4;
    /** 结果列固定为3 */
    public static final int ENTRUSTED_SOIL_RESULT_COLUMN_QTY = 3;
    public static final String ENTRUSTED_SOIL_FOLDER_NAME = "%s采样点";

    /** 委托的土壤样品状态附表标题 */
    public static final String ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_TITLE = "附表: 样品状态一览表";
    /** 委托的土壤样品状态附表表头标题, 固定为1行8列，其中 "" 代表会被合并单元格的 */
    public static final String[] ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_TITLES = {
            "采样点位", "采样深度(cm)", "颜色", "质地", "湿度", "植物根系", "砂砾含量", "其他异物"
    };
    /** 委托的土壤样品状态附表表头列数 */
    public static final int ENTRUSTED_SOIL_ORDER_STATUS_RESULT_TABLE_HEADER_COLUMN_QTY = 8;

    /** 场调地下水检测结果表标题 */
    public static final String FACTORY_GROUNDWATER_RESULT_TABLE_TITLE = "地下水检测结果";
    /** 场调地下水检测结果表表头标题, 固定为3行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_TITLES = {
            "样品类别", "%s0", "采样日期", "%s1",
            "采样点位", "%s2", "%s4", "%s5",
            "样品状态", "%s3", "%s5", "%6"
    };
    /** 场调地下水检测结果表表头列数 */
    public static final int FACTORY_GROUNDWATER_RESULT_TABLE_HEADER_COLUMN_QTY = 4;
    /** 结果列固定为3 */
    public static final int FACTORY_GROUNDWATER_RESULT_COLUMN_QTY = 3;

    /** 振动检测结果表标题 */
    public static final String VIBRATION_RESULT_TABLE_TITLE = "振动检测结果";
    /** 振动检测结果表表头标题, 固定为3行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] VIBRATION_RESULT_TABLE_HEADER_TITLES = {
            "检测项目", "%s0", "检测日期", "%s1",
            "检测点位", "检测结果", "", "",
            "", "检测时间", "单位", "测量值"
    };
    /** 振动检测结果表表头列数 */
    public static final int VIBRATION_RESULT_TABLE_HEADER_COLUMN_QTY = 4;
    /** 振动检测点位布置示意图标题 */
    public static final String VIBRATION_POINT_DIAGRAM_TITLE = "振动检测点位布置示意图";

    /** 送样报告检测结果表标题，比如废水检测结果、土壤检测结果 */
    public static final String COLLECTED_SAMPLE_RESULT_TABLE_TITLE = "%s检测结果";
    /** 送样报告检测结果表表头标题, 固定为3行5列，其中 "" 代表会被合并单元格的 */
    public static final String[] COLLECTED_SAMPLE_RESULT_TABLE_HEADER_TITLES = {
            "样品原名称", "%s0", "送样日期", "%s1", "",
            "样品类别", "%s2", "样品状态", "%s3", "",
            "检测项目", "原样品编号", "样品编号", "单位", "检测结果"
    };
    /** 送样报告检测结果表表头列数 */
    public static final int COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY = 5;

    /** 送样报告检测结果表标题，比如废水检测结果、土壤检测结果 */
    public static final String COLLECTED_SAMPLE_RESULT_TABLE_TITLE2 = "%s检测结果";
    /** 送样报告检测结果表表头标题, 固定为2行7列，其中 "" 代表会被合并单元格的 */
    public static final String[] COLLECTED_SAMPLE_RESULT_TABLE_HEADER_TITLES2 = {
            "样品类别", "%s0", "", "送样日期", "", "%s1", "",
            "样品原名称", "检测项目", "样品状态", "原样品编号", "样品编号", "单位", "检测结果"
    };
    /** 送样报告检测结果表表头列数 */
    public static final int COLLECTED_SAMPLE_RESULT_TABLE_HEADER_COLUMN_QTY2 = 7;

    /****************************************************
     * 油气回收Word报告
     ****************************************************/
    public static final String DEFAULT_CONCLUSION = "合格";

    /** 加油站检测方法、依据及使用仪器表格标题, 固定为5行3列，其中 "" 代表会被合并单元格的 */
    public static final String[] GAS_STATION_MONITOR_ITEM_TABLE_HEADER_TITLES = {
            "油气回收系统名称", "加油站油气回收系统", "",
            "检测方法依据", "%s0", "",
            "执行标准", "%s1", "",
            "检测主要设备", "", "",
            "名称", "型号", "编号",
    };
    /** 加油站相关情况表表头列数 */
    public static final int GAS_STATION_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY = 3;

    /** 加油站相关情况表格占位符 */
    public static final String GAS_STATION_TABLE_PLACEHOLDER = "gasStationTable";
    /** 加油站相关情况表格标题, 固定为5行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] GAS_STATION_RESULT_TABLE_HEADER_TITLES = {
            "加油站名称", "%s0", "", "",
            "汽油罐数（个）", "%s1", "油气管线是否连通", "%s2",
            "油罐体积（L）", "%s3", "油气空间（L）", "%s4",
            "加油机数（台）", "%s5", "加油枪数（个）", "%s6",
            "备注", "%s7", "", ""
    };
    /** 加油站相关情况表表头列数 */
    public static final int GAS_STATION_RESULT_TABLE_HEADER_COLUMN_QTY = 4;

    /** 检测设备相关情况表格占位符 */
    public static final String EQUIPMENT_TABLE_PLACEHOLDER = "equipmentTable";
    public static final String DEFAULT_DATE_FORMAT = "yyyy年MM月dd日";
    /** 检测设备相关情况表格标题, 固定为7行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] EQUIPMENT_RESULT_TABLE_HEADER_TITLES = {
            "检测设备型号及名称", "%s0", "仪器编号", "%s1",
            "检测设备检测前自身气密性检查", "", "", "",
            "检测时间", "%s2", "大气压（kPa）", "%s3",
            "环境温度(℃)", "%s4", "环境湿度（%RH）", "%s5",
            "起始压力（Pa）", "%s6", "结束压力（Pa）", "%s7",
            "压力差（Pa）", "%s8", "标准压差（Pa）", "≤15",
            "结论", "%s9", "", ""
    };
    /** 检测设备相关情况表表头列数 */
    public static final int EQUIPMENT_RESULT_TABLE_HEADER_COLUMN_QTY = 4;

    /** 密闭性检测结果表标题，比如密闭性检测结果 */
    public static final String HERMETIC_RESULT_TABLE_TITLE = "密闭性检测结果";
    /** 密闭性检测结果表表头标题, 固定为1行6列，其中 "" 代表会被合并单元格的 */
    public static final String[] HERMETIC_RESULT_TABLE_HEADER_TITLES = {
            "连通油罐", "油罐编号", "油罐容积(L)", "油气空间(L)", "起始压力(Pa)", "5min后剩余压力(Pa)"
    };
    /** 密闭性检测结果表表头列数 */
    public static final int HERMETIC_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    /** 最小剩余压力限值(Pa)标题 */
    public static final String HERMETIC_MIN_LEFT_KPA_TITLE = "最小剩余压力限值(Pa)";
    /** 最小剩余压力分析项匹配关键字 */
    public static final String HERMETIC_MIN_LEFT_KPA_MATCH_KEYWORD = "最小剩余压力限值";
    public static final String CONCLUSION_TITLE = "结论";
    public static final String[] HERMETIC_MATCH_ITEM_KEYWORDS = {
            "油气管是否连通", "油罐编号", "油罐容积", "油气空间", "初始压力", "5min"
    };
    public static final String HERMETIC_ITEM_KEYWORD = "密闭性";

    /** 液阻检测结果表标题，比如液阻检测结果 */
    public static final String LIQUID_RESISTANCE_RESULT_TABLE_TITLE = "液阻检测结果";
    /** 液阻检测结果表表头标题, 固定为2行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] LIQUID_RESISTANCE_RESULT_TABLE_HEADER_TITLES = {
            "加油机编号", "液阻压力(Pa)", "", "",
            "", "18.0 L/min", "28.0 L/min", "38.0 L/min"
    };
    /** 液阻检测结果表表头列数 */
    public static final int LIQUID_RESISTANCE_RESULT_TABLE_HEADER_COLUMN_QTY = 4;
    /** 液阻最大压力限值(Pa)标题 */
    public static final String LIQUID_RESISTANCE_MAX_KPA_TITLE = "液阻最大压力限值(Pa)";
    /** 液阻最大压力限值匹配分析项关键字*/
    public static final String[] LIQUID_RESISTANCE_MAX_KPA_MATCH_ITEM_KEYWORDS = {
            "液阻压力18", "液阻压力28", "液阻压力38"
    };
    public static final String[] LIQUID_RESISTANCE_MATCH_ITEM_KEYWORDS = {
            "加油机编号", "18.0", "28.0", "38.0"
    };
    public static final String LIQUID_RESISTANCE_ITEM_KEYWORD = "液阻";

    /** 气液比检测结果表标题，比如气液比检测结果 */
    public static final String GAS_LIQUID_RATIO_RESULT_TABLE_TITLE = "气液比检测结果";
    /** 气液比检测结果表表头标题, 固定为1行5列，其中 "" 代表会被合并单元格的 */
    public static final String[] GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_TITLES = {
            "加油枪编号", "档位", "加油体积(L)", "回收油气体积(L)", "气液比值"
    };
    /** 气液比检测结果表表头列数 */
    public static final int GAS_LIQUID_RATIO_RESULT_TABLE_HEADER_COLUMN_QTY = 5;
    /** 气液比标准范围 */
    public static final String GAS_LIQUID_RATIO_STD_RANGE_TITLE = "标准范围";
    public static final String GAS_LIQUID_RATIO_STD_RANGE_VALUE = "1.0≤气液比≤1.2";
    public static final String[] GAS_LIQUID_RATIO_MATCH_ITEM_KEYWORDS = {
            "加油枪编号", "档位", "加油体积", "回收油气体积", "气液比"
    };
    public static final String GAS_LIQUID_RATIO_ITEM_KEYWORD = "气液比";

    /** 油气排放浓度检测结果表标题，比如油气排放浓度检测结果 */
    public static final String OIL_GAS_DENSITY_RESULT_TABLE_TITLE = "油气排放浓度检测结果";
    /** 油气排放浓度检测结果表表头标题, 固定为2行5列，其中 "" 代表会被合并单元格的 */
    public static final String[] OIL_GAS_DENSITY_RESULT_TABLE_HEADER_TITLES = {
            "检测项目", "检测点位", "样品编号", "检测结果", "",
            "", "", "", "检测结果(mg/m<sup>3</sup>)", "平均检测结果(g/m<sup>3</sup>)"
    };
    /** 油气排放浓度检测结果表表头列数 */
    public static final int OIL_GAS_DENSITY_RESULT_TABLE_HEADER_COLUMN_QTY = 5;
    /** 油气排放浓度标准范围 */
    public static final String OIL_GAS_DENSITY_STD_RANGE_TITLE = "标准范围";
    /** 油气排放浓度标准范围值 */
    public static final String OIL_GAS_DENSITY_STD_RANGE_VALUE = "≤25 g/m<sup>3</sup>";
    public static final String OIL_GAS_DENSITY_ITEM_KEYWORD = "油气排放浓度";

    /****************************************************
     * 验收业务综合Word报告
     ****************************************************/
    /** 气检测结果表标题，如：固定污染源检测结果报告单 */
    public static final String CHECK_GAS_RESULT_TABLE_TITLE = "%s检测结果报告单";
    /** 气检测结果表头标题, 固定为2行10列，其中 "" 代表会被合并单元格的 */
    public static final String[] CHECK_GAS_RESULT_TABLE_HEADER_TITLES = {
            "检测点位", "检测项目", "采样日期", "采样频次", "样品编号", "检测结果", "", "", "", "", "", "",
            "", "", "", "", "", "实测浓度(mg/m<sup>3</sup>)", "折算浓度(mg/m<sup>3</sup>)", "标干流量(m<sup>3</sup>/h)", "排放速率(kg/h)", "氧含量(%)", "实测/折算浓度平均值(mg/m<sup>3</sup>)", "排放速率平均值(kg/h)"
    };
    /** 匹配分析项名称关键字 */
    public static final String[] CHECK_GAS_RESULT_MATCH_ITEM_KEYWORDS = {
            "实测浓度", "折算浓度", "标干流量", "排放速率"
    };
    /** 匹配其他测试中分析项名称关键字 */
    public static final String CHECK_GAS_MATCH_OTHER_TEST_ITEM_KEYWORD = "氧含量";
    /** 气检测结果采样频次 */
    public static final String CHECK_GAS_RESULT_FREQUENCY = "第%d次";
    /** 气检测结果表头列数 */
    public static final int CHECK_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 12;

    /** 无组织废气匹配关键字 */
    public static final String CHECK_UNORGANIZED_GAS_KEYWORD = "无组织";
    /** 无组织废气检测结果表标题，如：无组织废气检测结果 */
    public static final String CHECK_UNORGANIZED_GAS_RESULT_TABLE_TITLE = "%s废气检测结果";
    /** 无组织废气检测结果表头标题, 固定为2行4列，其中 "" 代表会被合并单元格的 */
    public static final String[] CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_TITLES = {
            "检测项目", "采样日期", "采样频次", "检测点位", "样品编号及检测结果（mg/m<sup>3</sup>）", "",
            "", "", "", "", "样品编号", "检测结果"
    };
    /** 无组织废气检测结果表头列数 */
    public static final int CHECK_UNORGANIZED_GAS_RESULT_TABLE_HEADER_COLUMN_QTY = 6;
    /** 无组织废气检测结果采样频次 */
    public static final String CHECK_UNORGANIZED_GAS_RESULT_FREQUENCY = "第%d次";
    /** 无组织废气检测点位布置示意图 */
    public static final String CHECK_UNORGANIZED_GAS_POINT_DIAGRAM_TITLE = "无组织废气检测点位布置示意图";

    /** 气象参数检测结果表标题，如：气象参数检测结果 */
    public static final String CHECK_METEOROLOGY_PARAMETER_RESULT_TABLE_TITLE = "气象参数检测结果";
    /** 气象参数检测结果表头标题, 固定为1行7列，其中 "" 代表会被合并单元格的 */
    public static final String[] CHECK_METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_TITLES = {
            "采样日期", "采样时间", "天气情况", "风向", "风速(m/s)", "相对湿度(%)", "温度(°C)", "大气压力(kPa)"
    };
    /** 用于匹配分析项的名称 */
    public static final String[] CHECK_METEOROLOGY_PARAMETER_SEARCH_TITLES = {
            "风向", "风速", "相对湿度", "温度", "大气压力"
    };
    /** 气象参数检测结果表头列数 */
    public static final int CHECK_METEOROLOGY_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY = 8;

    /****************************************************
     * 方法运行状态
     ****************************************************/
    public static final String METHOD_RUN_STATUS_WORKING = "working";// 现行有效
    public static final String METHOD_RUN_STATUS_OUTED = "outed";// 废止
    public static final String METHOD_RUN_STATUS_NOSTART = "nostart";// 待实施

    /****************************************************
     * 方法版本
     ****************************************************/
    public static final String METHOD_VERSION_FIRST = "first";// 首次
    public static final String METHOD_VERSION_REVISE = "revise";// 修订

    /****************************************************
     * 方法类型，监测类别，版本
     ****************************************************/
    public static final String MONITORCATEGORY = "monitorCategory";// 监测类别
    public static final String METHODCATEGORY = "methodCategory";// 方法类型
    public static final String METHODVERSION = "methodVersion";// 版本
    public static final String METHOD_CATEGORIES = "monitorCategory,methodCategory,methodVersion,runStatus";// 方法类别

    /****************************************************
     * 材料管理
     ****************************************************/
    public static final String MTL_DEFAULT_REMIND_DAY = "30";// 默认提醒天数
}
