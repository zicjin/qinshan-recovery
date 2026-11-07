package com.sunwayworld.emc.module.mdm.invoices.service.impl;


import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcReturnedService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@GikamBean
public class EmcReturnedServiceImpl implements EmcReturnedService {

    @Autowired
    private EmcReturnedDao emcReturnedDao;

    @Autowired
    private EmcInvoiceService invoiceService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReturnedDao getDao() {
        return emcReturnedDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long id ,RestJsonWrapperBean jsonWrapper) {
        EmcReturnedBean emcReturned = jsonWrapper.parseUnique(EmcReturnedBean.class);
        EmcInvoiceBean invoiceBean = invoiceService.selectById(id);
        if (invoiceBean.getInvoiceAmount() == null || invoiceBean.getInvoiceAmount().trim().isEmpty()) {
            throw new AuthenticationServiceException("请输入开票金额！");
        }
        List<EmcReturnedBean> returnedList = getDao().selectListByOneColumnValue(id, "invoiceId");
        List<BigDecimal> returnedAmountList = returnedList.stream().map(e -> new BigDecimal(e.getReturnedAmount())).collect(Collectors.toList());
        BigDecimal returnedAmount = new BigDecimal(emcReturned.getReturnedAmount());
        for (int i = 0; i < returnedAmountList.size(); i++) {
            returnedAmount = returnedAmount.add(returnedAmountList.get(i));
        }
            BigDecimal uncollected = new BigDecimal(invoiceBean.getInvoiceAmount()).subtract(returnedAmount);
            emcReturned.setId(ApplicationContextHelper.getNextIdentity());
            emcReturned.setInvoiceId(id);
            emcReturned.setInvoiceAmount(invoiceBean.getInvoiceAmount());
            emcReturned.setUncollectedAmount(uncollected.toString());

            if (uncollected.compareTo(BigDecimal.ZERO) < 0) {
                throw new AuthenticationServiceException("输入的回款金额大于未回款金额！");
            }

            if (uncollected.compareTo(BigDecimal.ZERO) == 0) {
                invoiceBean.setDoneFlag("1");
            }

            invoiceService.updateIfChanged(invoiceBean);
            getDao().insert(emcReturned);
        return emcReturned.getId();
    }
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @Audit("AUDIT.DELETE")
    public void delete(RestJsonWrapperBean jsonWrapper) {
        List<EmcReturnedBean> selected = jsonWrapper.parse(EmcReturnedBean.class);
        Set<Long> invoiceIds = new LinkedHashSet<>();
        for (EmcReturnedBean item : selected) {
            EmcReturnedBean stored = getDao().selectByIdIfPresent(Objects.requireNonNull(item.getId(), "Return id is required"));
            if (stored != null) {
                invoiceIds.add(stored.getInvoiceId());
            }
        }
        // Keep framework cascade, attachment/workflow cleanup and DELETE audit behavior.
        EmcReturnedService.super.delete(jsonWrapper);
        invoiceIds.forEach(this::refreshDoneFlag);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    @Audit("AUDIT.DELETE")
    public void delete(Long id) {
        EmcReturnedBean stored = getDao().selectByIdIfPresent(Objects.requireNonNull(id, "Return id is required"));
        EmcReturnedService.super.delete(id);
        if (stored != null) {
            refreshDoneFlag(stored.getInvoiceId());
        }
    }

    private void refreshDoneFlag(Long invoiceId) {
        EmcInvoiceBean invoice = invoiceService.selectById(invoiceId);
        if (invoice.getInvoiceAmount() == null || invoice.getInvoiceAmount().trim().isEmpty()) {
            // Historical missing amounts cannot establish completion; rollback the deletion.
            throw new AuthenticationServiceException("请输入开票金额！");
        }
        BigDecimal returnedAmount = BigDecimal.ZERO;
        for (EmcReturnedBean remaining : getDao().selectListByOneColumnValue(invoiceId, "invoiceId")) {
            returnedAmount = returnedAmount.add(new BigDecimal(remaining.getReturnedAmount()));
        }
        String doneFlag = new BigDecimal(invoice.getInvoiceAmount()).subtract(returnedAmount)
                .compareTo(BigDecimal.ZERO) == 0 ? "1" : "0";
        if (!doneFlag.equals(invoice.getDoneFlag())) {
            invoice.setDoneFlag(doneFlag);
            invoiceService.updateIfChanged(invoice);
        }
    }
}
