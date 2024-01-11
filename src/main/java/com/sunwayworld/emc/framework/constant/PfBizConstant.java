package com.sunwayworld.emc.framework.constant;

/**
 * @author zhengxy@sunwayworld.com 2021年05月28日
 */
public class PfBizConstant extends EmcBizConstant {

    /****************************************************
     * 样品测试STATUS状态
     ****************************************************/
    /** 质控审核 */
    public static final String ORDERTASK_STATUS_QC_AUDIT = "qcAudit";

    public static final String ADVISORY_MONITOR_TMPL_GROUP = "项目咨询";

    /** 质量控制业务类型项目编号后缀 */
    public static final String QC_PROJECT_NO_SUFFIX = "Z";
    public static final String QC_PROJECT_ORDER_NO_PREFIX = "M";

    public static final String ADVISORY_REPORT_STATUS_DRAFT = "draft";// 草稿
    public static final String ADVISORY_REPORT_STATUS_REPORTEDIT = "reportEdit";// 报告编制
    public static final String ADVISORY_REPORT_STATUS_REPORTCHECK = "reportCheck";// 报告复核
    public static final String ADVISORY_REPORT_STATUS_REPORTAUDIT = "reportAudit";// 报告审核
    public static final String ADVISORY_REPORT_STATUS_REPORTISSUE = "reportIssue";// 报告签发
    public static final String ADVISORY_REPORT_STATUS_REPORTDISCUSS = "reportDiscuss";// 专家上会
    public static final String ADVISORY_REPORT_STATUS_REPORTREVISE = "reportRevise";// 报告修订
    public static final String ADVISORY_REPORT_STATUS_REPORTARCHIVING = "reportArchiving";// 报告归档
    public static final String ADVISORY_REPORT_STATUS_DONE = "done";

}
