package com.sunwayworld.emc.module.mdm.contract.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.PfContractService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author zhengxy@sunwayworld.com 2021年06月30日
 */
@Repository
@GikamBean
public class PfContractServiceImpl extends EmcContractServiceImpl implements PfContractService {

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcClientService clientService;

    @Autowired
    private CoreReportConfigLineService reportConfigLineService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcContractBean emcContract = jsonWrapper.parseUnique(EmcContractBean.class);
        emcContract.setId(ApplicationContextHelper.getNextIdentity());

        // 合同编号规则为：公司简称+年月日+流水号2位，此处公司简称若未获取则默认为ZK(测试用，后期对接CRM后不需要生成)
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        String parentOrgId = loginUser.getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String orgCode = Optional.ofNullable(parentOrg.getExt$Item("orgShortName")).orElse("ZK");

        String registerDateStr = emcContract.getExt$Item("registerdate");
        LocalDate registerDate = DateTimeUtils.parseLocalDate(registerDateStr);
        String formatDateStr = DateTimeUtils.formatLocalDate(registerDate, "yyMMdd");

        String contractNoPrefix = orgCode + formatDateStr;
        String nextSeqNo = "01";
        EmcContractBean maxNoContract = this.selectFirstByFilter(SearchFilter.instance().match("contractNo", contractNoPrefix).filter(MatchPattern.SB), Order.desc("contractNo"));
        if (maxNoContract != null && !StringUtils.isEmpty(maxNoContract.getContractNo())) {
            String suffix = maxNoContract.getContractNo().substring(contractNoPrefix.length());
            int nextSeqNoValue = NumberUtils.isNumber(suffix) ? NumberUtils.parseInt(suffix) + 1 : 1;
            nextSeqNo = StringUtils.leftPad(nextSeqNoValue + "", 2);
        }
        String nextContractNo = contractNoPrefix + nextSeqNo;
        emcContract.setContractNo(nextContractNo);

        // 根据客户标记查询乙方单位，默认取第一个
        EmcClientBean filterClient = new EmcClientBean();
        filterClient.setExt$Item("clientTag", "contractor");
        EmcClientBean contractor = clientService.getDao().selectFirstIfPresent(filterClient, Arrays.asList("CLIENTTAG"),
                Arrays.asList("ID", "CLIENTNAME", "ADDRESS", "CREDITCODE", "OPENINGBANK", "BANKACCOUNT", "PHONE"));
        if (contractor != null) {
            emcContract.setExt$Item("contractororgid", contractor.getId() + "");
            emcContract.setExt$Item("contractorname", contractor.getClientName());
            emcContract.setExt$Item("contractoraddress", contractor.getAddress());
            emcContract.setExt$Item("contractorlicense", contractor.getCreditCode());
            emcContract.setExt$Item("contractorbank", contractor.getExt$Item("openingbank"));
            emcContract.setExt$Item("contractoraccount", contractor.getExt$Item("bankaccount"));
            emcContract.setExt$Item("contractorphone", contractor.getPhone());
        }
        emcContract.setExt$Item("audit", Constant.NO);
        emcContract.setExt$Item("subpackage", Constant.NO);
        emcContract.setExt$Item("nonstandardmethod", Constant.NO);
        emcContract.setExt$Item("resultevaluation", Constant.NO);
        emcContract.setExt$Item("orgId", parentOrgId);
        emcContract.setReviewer(LocalContextHelper.getLoginUserId());

        // 查询合同报表，默认取第一个
        List<CoreReportConfigLineBean> reportConfigLineList = reportConfigLineService.getDao().selectListByOneColumnValue("contract", "REPORTCONFIGID", Collections.singletonList("ID"));
        if (!reportConfigLineList.isEmpty()) {
            emcContract.setExt$Item("REPORTCONFIGLINEID", reportConfigLineList.get(0).getId() + "");
        }

        getDao().insert(emcContract);

        return emcContract.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        return super.startProcess(wrapper);
    }
}
