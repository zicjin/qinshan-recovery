package com.sunwayworld.emc.module.mdm.invoices.service.impl;


import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcReturnedService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(EmcReturnedBean item) {
        updateIfChanged(Collections.singletonList(item));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(List<EmcReturnedBean> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        Map<Long, EmcReturnedBean> stored = new LinkedHashMap<>();
        Map<Long, BigDecimal> amounts = new LinkedHashMap<>();
        Set<Long> invoiceIds = new LinkedHashSet<>();
        for (EmcReturnedBean item : items) {
            Long id = Objects.requireNonNull(item.getId(), "Return id is required");
            if (stored.containsKey(id)) {
                throw new IllegalArgumentException("Duplicate return id in update batch");
            }
            EmcReturnedBean previous = getDao().selectById(id);
            stored.put(id, previous);
            // The stored relationship is authoritative, including for partial JSON patches.
            item.setInvoiceId(previous.getInvoiceId());
            item.setInvoiceAmount(previous.getInvoiceAmount());
            item.setUncollectedAmount(previous.getUncollectedAmount());
            if (hasReturnedAmount(item)) {
                BigDecimal amount = new BigDecimal(Objects.requireNonNull(item.getReturnedAmount(), "Return amount is required"));
                amounts.put(id, amount);
                if (amount.compareTo(new BigDecimal(previous.getReturnedAmount())) != 0) {
                    invoiceIds.add(previous.getInvoiceId());
                }
            }
        }
        Map<Long, EmcInvoiceBean> invoices = new LinkedHashMap<>();
        Map<Long, BigDecimal> balances = new LinkedHashMap<>();
        for (Long invoiceId : invoiceIds) {
            EmcInvoiceBean invoice = invoiceService.selectById(invoiceId);
            if (invoice.getInvoiceAmount() == null || invoice.getInvoiceAmount().trim().isEmpty()) {
                throw new AuthenticationServiceException("请输入开票金额！");
            }
            BigDecimal total = BigDecimal.ZERO;
            for (EmcReturnedBean sibling : getDao().selectListByOneColumnValue(invoiceId, "invoiceId")) {
                total = total.add(amounts.containsKey(sibling.getId())
                        ? amounts.get(sibling.getId()) : new BigDecimal(sibling.getReturnedAmount()));
            }
            BigDecimal balance = new BigDecimal(invoice.getInvoiceAmount()).subtract(total);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                throw new AuthenticationServiceException("输入的回款金额大于未回款金额！");
            }
            invoices.put(invoiceId, invoice);
            balances.put(invoiceId, balance);
        }
        for (EmcReturnedBean item : items) {
            EmcReturnedBean previous = stored.get(item.getId());
            if (hasReturnedAmount(item) && balances.containsKey(previous.getInvoiceId())
                    && new BigDecimal(item.getReturnedAmount()).compareTo(new BigDecimal(previous.getReturnedAmount())) != 0) {
                item.setInvoiceAmount(invoices.get(previous.getInvoiceId()).getInvoiceAmount());
                item.setUncollectedAmount(balances.get(previous.getInvoiceId()).toString());
            }
        }
        // Preserve framework partial-property merging, created fields, update hooks and audit.
        EmcReturnedService.super.updateIfChanged(items);
        invoiceIds.forEach(this::refreshDoneFlag);
    }

    private boolean hasReturnedAmount(EmcReturnedBean item) {
        if (item instanceof BeanPropertyListener) {
            return ((BeanPropertyListener) item).getChangedPropertyEventList().stream()
                    .anyMatch(event -> "returnedAmount".equalsIgnoreCase(event.getPropertyDescriptor().getName()));
        }
        return item.getReturnedAmount() != null;
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
