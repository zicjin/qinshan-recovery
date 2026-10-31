package com.sunwayworld.emc.module.erp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月26日
 */
public interface ErpReportService {

    /**
     * 分配跟单员，就是更新报告编制人到ERP
     * @param reportList
     */
    void updateMerchandiser(List<EmcReportBean> reportList);

    /**
     * 分配跟单员，就是更新报告编制人到ERP，此方法用于远程调用模块使用
     * @param jsonStr
     * @return
     */
    String updateMerchandiserToErp(String jsonStr);

    /**
     * 上传报告文件 - 上传草稿报告或上传正式报告之前都需要调用
     * 用于获取报告文件对应的 reportUrl
     * @param reportList
     */
    void uploadOrderReportFile(List<EmcReportBean> reportList);

    /**
     * 上传草稿版报告 - 报告编制提交时调用
     * @param reportList
     */
    void uploadOrderDraftReport(List<EmcReportBean> reportList);

    /**
     * 上传草稿版报告 - 报告编制提交时调用
     * @param jsonStr
     * @return
     */
    String uploadOrderDraftReportToErp(String jsonStr);

    /**
     * 提交报告审核申请 - 报告编制提交时调用
     * @param reportList
     */
    void submitOrderReportAuditApply(List<EmcReportBean> reportList);

    /**
     * 提交报告审核申请 - 报告编制提交时调用
     * @param jsonStr
     * @return
     */
    String submitOrderReportAuditApplyToErp(String jsonStr);

    /**
     * 订单报告审核 - 报告审核提交或退回时调用，用于通知报告审核的结果
     * @param reportList
     */
    void auditOrderReport(List<EmcReportBean> reportList, boolean reject, String rejectReason);

    /**
     * 订单报告审核 - 报告审核提交或退回时调用，用于通知报告审核的结果
     * @param jsonStr
     */
    String auditOrderReportToErp(String jsonStr);

    /**
     * 上传正式版报告 - 报告签发提交时调用
     * @param reportList
     */
    void uploadOrderFinalReport(List<EmcReportBean> reportList);

    /**
     * 上传正式版报告 - 报告签发提交时调用
     * @param jsonStr
     * @return
     */
    String uploadOrderFinalReportToErp(String jsonStr);

    /**
     * 发送报告 - 报告发送提交时调用
     * @param reportList
     */
    void sendOrderReport(List<EmcReportBean> reportList);

    /**
     * 发送报告 - 报告发送提交时调用
     * @param jsonStr
     */
    String sendOrderReportToErp(String jsonStr);

    /**
     * 修改报告审核人 -
     * @param wrapper
     */
    void updateReportAuditUser(RestJsonWrapperBean wrapper);

    /**
     * 获取商户ID
     * @return
     */
    String getMerchantId();

    /**
     * 获取默认报价员ID
     * @return ERP GBID
     */
    Long getDefaultSaleGbId();
}
