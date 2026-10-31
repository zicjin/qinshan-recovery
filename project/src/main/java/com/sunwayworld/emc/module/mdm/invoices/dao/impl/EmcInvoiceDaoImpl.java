package com.sunwayworld.emc.module.mdm.invoices.dao.impl;

import com.sunwayworld.emc.module.mdm.invoices.bean.EmcInvoiceBean;
import com.sunwayworld.emc.module.mdm.invoices.dao.EmcInvoiceDao;
import com.sunwayworld.emc.module.mdm.invoices.mapper.EmcInvoiceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcInvoiceDaoImpl extends MybatisDaoSupport<EmcInvoiceBean, Long> implements EmcInvoiceDao {

    @Autowired
    private EmcInvoiceMapper emcInvoiceMapper;

    @Override
    public EmcInvoiceMapper getMapper() {
        return emcInvoiceMapper;
    }

}
