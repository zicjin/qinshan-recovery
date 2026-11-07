package com.sunwayworld.emc.module.mdm.invoices.service.impl;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcInvoiceDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@GikamBean
public class EmcInvoiceServiceImpl implements EmcInvoiceService {

    @Autowired
    private EmcInvoiceDao emcInvoiceDao;

    @Autowired
    private EmcContractService contractService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInvoiceDao getDao() {
        return emcInvoiceDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInvoiceBean emcInvoice =jsonWrapper.getBodyList().size() > 0 ? jsonWrapper.parseUnique(EmcInvoiceBean.class) : new EmcInvoiceBean();
        EmcInvoiceBean insertBean = new EmcInvoiceBean();
        String copyFlag = "1";
        BeanUtils.copyProperties(emcInvoice,insertBean);

        String key = LocalDateTime.now().getYear() + "" +  StringUtils.leftPad(LocalDateTime.now().getMonthValue() + "", 2);
        String sysNo = ApplicationContextHelper.getNextSequence(getDao().getTable(), key);
        insertBean.setExt$Item("COPYFLAG",copyFlag);
        insertBean.setSysNo(sysNo);
        if (!ObjectUtils.isEmpty(emcInvoice.getId())) {
            insertBean.setInvoiceAmount(null);
            insertBean.setContractId(null);
            insertBean.setContractName(null);
            insertBean.setProjectId(null);
            insertBean.setProjectName(null);
        }

        insertBean.setId(ApplicationContextHelper.getNextIdentity());


        getDao().insert(insertBean);
        return insertBean.getId();
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcInvoiceService.super.startProcess(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcInvoiceBean> emcInvoiceList = emcInvoiceDao.selectListByIds(idList);
        List<Long> contractIdList = emcInvoiceList.stream().map(e -> NumberUtils.parseLong(e.getContractId())).collect(Collectors.toList());
        List<EmcContractBean> contractList = contractService.selectListByFilter(SearchFilter.instance().match("id",contractIdList).filter(MatchPattern.OR));
        contractList.forEach(e -> {
            for (int i = 0; i < emcInvoiceList.size(); i++) {
                if (ObjectUtils.isEmpty(e.getExt$Item("uninvoicedAmount")) || ObjectUtils.isEmpty(emcInvoiceList.get(i).getInvoiceAmount())) {
                    throw new AuthenticationServiceException("有未开票金额或开票收入金额为空！");
                }
                BigDecimal uninvoicedAmount = new BigDecimal(e.getExt$Item("uninvoicedAmount"));
                BigDecimal invoiceAmount = new BigDecimal(emcInvoiceList.get(i).getInvoiceAmount());
                BigDecimal last = uninvoicedAmount.subtract(invoiceAmount);
                if (last.compareTo(BigDecimal.ZERO) < 0) {
                    throw new AuthenticationServiceException("开票金额大于当前未开票金额！");
                }
                e.setExt$Item("uninvoicedAmount",last.toString());
            }
        });
        contractService.getDao().update(contractList,"uninvoicedAmount");
        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcInvoiceService.super.rejectTask(wrapper);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcInvoiceBean> emcInvoiceList = emcInvoiceDao.selectListByIds(idList);
        List<Long> contractIdList = emcInvoiceList.stream().map(e -> NumberUtils.parseLong(e.getContractId())).collect(Collectors.toList());
        List<EmcContractBean> contractList = contractService.selectListByFilter(SearchFilter.instance().match("id",contractIdList).filter(MatchPattern.OR));
        contractList.forEach(e -> {
            for (int i = 0; i < emcInvoiceList.size(); i++) {
                BigDecimal uninvoicedAmount = new BigDecimal(e.getExt$Item("uninvoicedAmount"));
                BigDecimal invoiceAmount = new BigDecimal(emcInvoiceList.get(i).getInvoiceAmount());
                BigDecimal last = uninvoicedAmount.add(invoiceAmount);
                e.setExt$Item("uninvoicedAmount",last.toString());
            }
        });
        contractService.getDao().update(contractList,"uninvoicedAmount");
        return coreBpmnInstanceStatusDTOList;
    }
}
