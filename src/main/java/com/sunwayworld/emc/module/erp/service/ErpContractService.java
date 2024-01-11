package com.sunwayworld.emc.module.erp.service;

import java.util.List;

import com.sunwayworld.emc.module.erp.bean.RestResult;
import com.sunwayworld.emc.module.erp.bean.contract.AttachVO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractChangeApplyDTO;
import com.sunwayworld.emc.module.erp.bean.contract.MidPlanContractVO;
import com.sunwayworld.emc.module.erp.bean.contract.PlanContractCancelReq;
import com.sunwayworld.emc.module.erp.bean.contract.PushAuditUserContractReq;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;

/**
 * @author zhengxy@sunwayworld.com 2023年11月06日
 */
public interface ErpContractService {

    /**
     * ERP推送计划合同到LIMS
     * @param planContract
     * @return
     */
    RestResult<Boolean> pushPlanContract(MidPlanContractVO planContract);

    /**
     * ERP推送计划合同变更到LIMS
     * @param contractChange
     * @return
     */
    RestResult<Boolean> pushPlanContractChange(MidPlanContractChangeApplyDTO contractChange);

    /**
     *
     * @param auditUserContractReq
     * @return
     */
    RestResult<Boolean> pushAuditUserContract(PushAuditUserContractReq auditUserContractReq);

    /**
     * ERP推送计划合同作废到LIMS
     * @param planContractCancelReq
     * @return
     */
    RestResult<Boolean> pushPlanContractCancel(PlanContractCancelReq planContractCancelReq);

    /**
     * 下载合同附件
     * @param attachList 附件列表
     * @param contractId 合同ID
     */
    void downloadAttach(List<AttachVO> attachList, Long contractId);

    /**
     * 获取商户机构
     * @return CoreOrgBean
     */
    CoreOrgBean getMerchantOrg();
}
