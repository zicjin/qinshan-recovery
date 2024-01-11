package com.sunwayworld.emc.framework.constant;

/**
 * EMC 业务流程状态常量类
 */
public class EmcBizConstant {

    /****************************************************
     * 项目中文节点状态
     ****************************************************/
    public static final String PROJECT_CN_NODE_XMDJ = "登记";
    public static final String PROJECT_CN_NODE_FABZ = "方案编制";
    public static final String PROJECT_CN_NODE_FASH = "方案审核";
    public static final String PROJECT_CN_NODE_CYZ = "采样中";
    public static final String PROJECT_CN_NODE_YPJS = "样品接收";
    public static final String PROJECT_CN_NODE_FXZ = "分析中";
    public static final String PROJECT_CN_NODE_BGBZ = "报告编制";

    /****************************************************
     * 项目STATUS状态
     ****************************************************/
    public static final String PROJECT_STATUS_CREATE = "create"; // 登记
    public static final String PROJECT_STATUS_SCHEMEDRAFT = "schemeDraft"; // 方案编制
    public static final String PROJECT_STATUS_SCHEMEAUDIT = "schemeAudit"; // 方案审核
    public static final String PROJECT_STATUS_AUDITFINISHED = "auditFinished"; // 方案审核之后报告编制之前
    public static final String PROJECT_STATUS_REPORTEDIT = "reportEdit"; // 报告编制
    public static final String PROJECT_STATUS_CANCEL = "cancel";// 取消

    /****************************************************
     * 项目PROCESSSTATUS状态
     ****************************************************/
    public static final String PROJECT_PROCESSSTATUS_DRAFT = "draft"; // 草稿
    public static final String PROJECT_PROCESSSTATUS_APPROVE = "approve"; // 审核
    public static final String PROJECT_PROCESSSTATUS_DONE = "done"; // 完成

    /****************************************************
     * 点位STATUS状态
     ****************************************************/
    public static final String FOLDER_STATUS_DRAFT = "draft"; // 采样安排
    public static final String FOLDER_STATUS_DRAW = "draw"; // 采样安排之后报告编制之前
    public static final String FOLDER_STATUS_DONE = "done"; // 报告编制

    /****************************************************
     * 点位PROCESSSTATUS状态
     ****************************************************/
    public static final String FOLDER_PROCESSSTATUS_DRAFT = "draft"; // 采样安排
    public static final String FOLDER_PROCESSSTATUS_DONE = "done"; // 采样安排之后

    public static final String SAMPLE_STATUS_REPORTUNDO = "reportundo";// 报告退回
    public static final String SAMPLE_STATUS_PRELOGGED = "preLogged"; // 样品接收
    public static final String SAMPLE_STATUS_MUTUAL = "mutual";// 采样审核节点 -采样互审

    /****************************************************
     * 采样单PROCESSSTATUS状态
     ****************************************************/
    public static final String SAMPLE_PROCESSSTATUS_DRAFT = "draft"; // 现场采样
    public static final String SAMPLE_PROCESSSTATUS_APPROVE = "approve"; // 采样互审、采样审核
    public static final String SAMPLE_PROCESSSTATUS_DONE = "done"; // 采样审核之后

    /****************************************************
     * 样品测试STATUS状态
     ****************************************************/
    public static final String ORDERTASK_STATUS_DRAFT = "draft"; // 采样安排、现场采样、采样互审、采样审核
    public static final String ORDERTASK_STATUS_PRELOGGED = "preLogged"; // 样品接收
    public static final String ORDERTASK_STATUS_LOGGED = "logged"; // 分析录入
    public static final String ORDERTASK_STATUS_APPROVE = "approve"; // 分析校核
    public static final String ORDERTASK_STATUS_AUDIT = "audit"; // 分析审核
    public static final String ORDERTASK_STATUS_DONE = "done"; // 报告编制

    /****************************************************
     * 样品测试结果STATUS状态
     ****************************************************/
    public static final String ORDERTASKRESULT_STATUS_LOGGED = "Logged";
    public static final String ORDERTASKRESULT_STATUS_DONE = "Done";

    /****************************************************
     * 批STATUS状态
     ****************************************************/
    public static final String RUN_STATUS_DRAFT = "draft"; // 分析录入
    public static final String RUN_STATUS_APPROVE = "approve"; // 分析校核
    public static final String RUN_STATUS_AUDIT = "audit"; // 分析审核
    public static final String RUN_STATUS_DONE = "done"; // 报告编制

    /****************************************************
     * 批PROCESSSTATUS状态
     ****************************************************/
    public static final String RUN_PROCESSSTATUS_DRAFT = "draft"; // 分析录入
    public static final String RUN_PROCESSSTATUS_APPROVE = "approve"; // 分析校核、分析审核
    public static final String RUN_PROCESSSTATUS_DONE = "done"; // 报告编制

    /****************************************************
     * 业务流程表来源
     ****************************************************/
    public static final String BP_SOURCE_MONITORSCHEME = "monitorscheme"; // 监测方案
    public static final String BP_SOURCE_ORDERREGISTRATION = "orderregistration"; // 样品登记
    public static final String BP_SOURCE_MONITORSCHEMESAMPLEPARAM = "monitorschemesampleparam"; // 监测方案气采样参数

    /****************************************************
     * 分析类型
     ****************************************************/
    public static final String ANALYSISCATEGORY_SAMPLING = "sampling";// 采样
    public static final String ANALYSISCATEGORY_ANALYSIS = "analysis";// 分析

    /****************************************************
     * 样品测试流程记录表-操作类型
     ****************************************************/
    /** 样品接收 **/
    public static final String ORDER_TASK_LOG_SAMPLE_ACCEPT = "样品接收";
    /** 结果录入 **/
    public static final String ORDER_TASK_LOG_RESULT_INPUT = "结果录入";
    /** 同组互审 **/
    public static final String ORDER_TASK_LOG_GROUP_AUDIT = "同组互审";
    /** 主任审核 **/
    public static final String ORDER_TASK_LOG_LEADER_AUDIT = "主任审核";
    /** 同组互审退回 **/
    public static final String ORDER_TASK_LOG_GROUP_AUDIT_UNDO = "同组互审退回";
    /** 主任审核退回 **/
    public static final String ORDER_TASK_LOG_LEADER_AUDIT_UNDO = "主任审核退回";
    /** 添加测试 **/
    public static final String ORDER_TASK_LOG_ADD_TEST = "添加测试";
    /** 删除测试 **/
    public static final String ORDER_TASK_LOG_DELETE_TEST = "删除测试";
    /** 修改方法 **/
    public static final String ORDER_TASK_LOG_EDIT_METHOD = "修改方法";

    /****************************************************
     * 样品测试流程记录表-模块名称
     ****************************************************/
    /** 采样安排 **/
    public static final String ORDER_TASK_LOG_SAMPLE_PLAN = "采样安排";
    /** 现场采样 **/
    public static final String ORDER_TASK_LOG_SAMPLING = "现场采样";
    /** 采样互审 **/
    public static final String ORDER_TASK_LOG_SAMPLIE_MUTUAL_AUDIT = "采样互审";
    /** 采样审核 **/
    public static final String ORDER_TASK_LOG_SAMPLIE_AUDIT = "采样审核";
    /** 分析录入 **/
    public static final String ORDER_TASK_LOG_ANALYSE = "分析录入";
    /** 分析校核 **/
    public static final String ORDER_TASK_LOG_ANALYSE_MUTUAL_AUDIT = "分析校核";
    /** 分析审核 **/
    public static final String ORDER_TASK_LOG_ANALYSE_AUDIT = "分析审核";
    /** 检测项目修改 **/
    public static final String ORDER_TASK_LOG_CHECK_PROJECT_EDIT = "检测项目修改";
    /** 检测方法修改 **/
    public static final String ORDER_TASK_LOG_CHECK_METHOD_EDIT = "检测方法修改";

    /****************************************************
     * 报告--状态
     ****************************************************/
    public static final String REPORT_STATUS_DRAFT = "draft";// 草稿
    public static final String REPORT_STATUS_REPORTEDIT = "reportEdit";// 报告编制
    public static final String REPORT_STATUS_REPORTCHECK = "reportCheck";// 报告复核
    public static final String REPORT_STATUS_REPORTAUDIT = "reportAudit";// 报告审核
    public static final String REPORT_STATUS_REPORTISSUE = "reportIssue";// 报告签发
    public static final String REPORT_STATUS_REPORTPRINT = "reportPrint";// 报告打印
    public static final String REPORT_STATUS_REPORTSEND = "reportSend";// 报告发送
    public static final String REPORT_STATUS_REPORTARCHIVING = "reportArchiving";// 报告归档
    public static final String REPORT_STATUS_DONE = "done";// 结束

    /****************************************************
     * 项目中文节点状态
     ****************************************************/
    public static final String PROJECT_CN_NODE_QUALITY_CONTROL = "质量控制";

    /****************************************************
     * 项目STATUS状态
     ****************************************************/
    /** 质量控制 */
    public static final String PROJECT_STATUS_QUALITY_CONTROL = "qualityControl";

    /** 任务分配 */
    public static final String PROJECT_STATUS_DISTRIBUTE_PROJECT = "distributeProject";

    /****************************************************
     * 采样单STATUS状态
     ****************************************************/
    /** 现场采样 */
    public static final String SAMPLE_STATUS_DRAFT = "draft";
    /** 结果录入 */
    public static final String SAMPLE_STATUS_LOGGED = "logged";
    /** 采样互审 */
    public static final String SAMPLE_STATUS_APPROVE = "approve";
    /** 采样审核 */
    public static final String SAMPLE_STATUS_AUDIT = "audit";
    /** 采样结束 */
    public static final String SAMPLE_STATUS_DONE = "done";

    //数字和英文字母对照
    public static final String[] NUMFORENG = new String[]{"A","B","C","D","E","F","G", "H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
}
