package com.sunwayworld.emc.module.item.quotation.addprice.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.quotation.addprice.bean.EmcAddpriceBean;
import com.sunwayworld.emc.module.item.quotation.addprice.dao.EmcAddpriceDao;
import com.sunwayworld.emc.module.item.quotation.addprice.mapper.EmcAddpriceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcAddpriceDaoImpl extends MybatisDaoSupport<EmcAddpriceBean, Long> implements EmcAddpriceDao {

    @Autowired
    private EmcAddpriceMapper emcAddpriceMapper;

    @Override
    public EmcAddpriceMapper getMapper() {
        return emcAddpriceMapper;
    }

}
