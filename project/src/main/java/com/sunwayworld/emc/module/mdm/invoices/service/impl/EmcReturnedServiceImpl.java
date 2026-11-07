package com.sunwayworld.emc.module.mdm.invoices.service.impl;


import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.bean.EmcReturnedBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcReturnedDao;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcInvoiceService;
import com.sunwayworld.emc.module.mdm.invoices.service.EmcReturnedService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
    public void delete(Long aLong) {
        getDao().delete(aLong);
    }
}
