package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlMaintainDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:40:54
 */
@Repository
@GikamBean
public class EmcMtlMaintainServiceImpl implements EmcMtlMaintainService {

    @Autowired
    private EmcMtlMaintainDao mtlMaintainDao;

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    private EmcStockConcentService stockConcentService;

    @Autowired
    private EmcStockService stockService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcMtlMaintainDao getDao() {
        return mtlMaintainDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertMaintain(String id, RestJsonWrapperBean wrapper) {
        EmcMtlBean emcMtlBean = mtlService.selectById(id);
        EmcMtlMaintainBean emcMtlMaintainBean = wrapper.parseUnique(EmcMtlMaintainBean.class);
        if (emcMtlBean != null) {
            emcMtlMaintainBean.setMtlName(emcMtlBean.getMtlName());
        }
        emcMtlMaintainBean.setMtlId(id);
        emcMtlMaintainBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcMtlMaintainBean);

        //添加浓度
        List<EmcStockBean> stockList = stockService.getDao().selectListByOneColumnValue(id, "MTLID");
        List<EmcStockConcentBean> insertConcentList = stockList.stream().map(stock -> {
            EmcStockConcentBean emcStockConcent = new EmcStockConcentBean();
            emcStockConcent.setId(ApplicationContextHelper.getNextIdentity());
            emcStockConcent.setStockId(stock.getId());
            emcStockConcent.setCategory(emcMtlMaintainBean.getCategory());
            emcStockConcent.setMtlMaintainId(emcMtlMaintainBean.getId());
            return emcStockConcent;
        }).collect(Collectors.toList());
        stockConcentService.getDao().insert(insertConcentList);

        return emcMtlMaintainBean.getId();
    }

}
