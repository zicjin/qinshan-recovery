package com.sunwayworld.emc.module.item.consume.consume.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.dao.EmcCsmStockDao;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockLogService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcCsmStockServiceImpl implements EmcCsmStockService {

    public static final String LOG_TYPE_ADD = "添加";
    public static final String LOG_TYPE_STORAGE = "入库";
    public static final String LOG_TYPE_INVENTORY = "盘存";
    public static final String LOG_TYPE_ABANDON = "废弃";
    public static final String LOG_TYPE_RECIEVE = "领取";
    public static final String STOCK_QTY = "STOCKQTY";
    public static final String STOCK_STATUS = "STATUS";

    @Autowired
    private EmcCsmStockDao emcCsmStockDao;

    @Autowired
    private EmcCsmStockLogService csmStockLogService;

    @Autowired
    private EmcConsumeService consumeService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcCsmStockDao getDao() {
        return emcCsmStockDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String mtlId, RestJsonWrapperBean wrapper) {
        EmcCsmStockLogBean emcCsmStockLog = wrapper.parseUnique(EmcCsmStockLogBean.class);

        // 库存
        EmcCsmStockBean emcCsmStock = new EmcCsmStockBean();
        emcCsmStock.setId(ApplicationContextHelper.getNextIdentity());
        emcCsmStock.setStockQty(emcCsmStockLog.getQty());
        emcCsmStock.setMtlId(mtlId);
        emcCsmStock.setStatus(Constant.NO);
        emcCsmStock.setBatchNo(emcCsmStockLog.getExt$Item("batchno"));
        getDao().insert(emcCsmStock);

        // 历史记录
        emcCsmStockLog.setId(ApplicationContextHelper.getNextIdentity());
        emcCsmStockLog.setStockId(emcCsmStock.getId());
        emcCsmStockLog.setLogType(LOG_TYPE_ADD);
        csmStockLogService.getDao().insert(emcCsmStockLog);

        // 更新材料管理库存
        consumeService.updateStockQty(mtlId);

        return emcCsmStock.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void storage(Long id, RestJsonWrapperBean wrapper) {
        EmcCsmStockLogBean emcCsmStockLog = wrapper.parseUnique(EmcCsmStockLogBean.class);

        // 历史记录
        emcCsmStockLog.setId(ApplicationContextHelper.getNextIdentity());
        emcCsmStockLog.setStockId(id);
        emcCsmStockLog.setLogType(LOG_TYPE_STORAGE);
        EmcCsmStockBean emcCsmStock = getDao().selectById(id);
        emcCsmStockLog.setExt$Item("operateqty", emcCsmStockLog.getQty() + "");
        emcCsmStockLog.setPreviousQty(emcCsmStock.getStockQty());
        emcCsmStock.setStockQty(emcCsmStock.getStockQty() + emcCsmStockLog.getQty());
        emcCsmStockLog.setQty(emcCsmStock.getStockQty());
        csmStockLogService.getDao().insert(emcCsmStockLog);

        // 库存
        getDao().update(emcCsmStock, STOCK_QTY);

        // 更新材料管理库存
        consumeService.updateStockQty(emcCsmStock.getMtlId());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void inventory(String mtlId, RestJsonWrapperBean wrapper) {
        EmcCsmStockLogBean emcCsmStockLog = wrapper.parseUnique(EmcCsmStockLogBean.class);
        List<Long> stockIdList = Arrays.stream(emcCsmStockLog.getExt$().get("stockId").split(",")).map(stockId -> Long.parseLong(stockId)).collect(Collectors.toList());
        List<EmcCsmStockBean> stockList = getDao().selectListByIds(stockIdList);
        List<EmcCsmStockLogBean> stockLogList = stockList.stream().map(stock -> {
            EmcCsmStockLogBean EmcCsmStockLogBean = new EmcCsmStockLogBean();
            EmcCsmStockLogBean.setExt$Item("operateqty", stock.getStockQty() - emcCsmStockLog.getQty() + "");
            EmcCsmStockLogBean.setPreviousQty(stock.getStockQty());
            EmcCsmStockLogBean.setId(ApplicationContextHelper.getNextIdentity());
            EmcCsmStockLogBean.setStockId(stock.getId());
            EmcCsmStockLogBean.setLogType(LOG_TYPE_INVENTORY);
            EmcCsmStockLogBean.setQty(emcCsmStockLog.getQty());
            EmcCsmStockLogBean.setLogContent(emcCsmStockLog.getLogContent());
            return EmcCsmStockLogBean;
        }).collect(Collectors.toList());
        // 历史记录
        csmStockLogService.getDao().insert(stockLogList);

        EmcCsmStockBean emcCsmStock = new EmcCsmStockBean();
        emcCsmStock.setStockQty(emcCsmStockLog.getQty());
        // 库存
        getDao().updateByIds(emcCsmStock, stockIdList, STOCK_QTY);

        // 更新材料管理库存
        consumeService.updateStockQty(mtlId);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void abandon(String mtlId, RestJsonWrapperBean wrapper) {
        EmcCsmStockLogBean emcCsmStockLog = wrapper.parseUnique(EmcCsmStockLogBean.class);
        List<Long> stockIdList = Arrays.stream(emcCsmStockLog.getExt$().get("stockId").split(",")).map(stockId -> Long.parseLong(stockId)).collect(Collectors.toList());
        List<EmcCsmStockBean> stockList = getDao().selectListByIds(stockIdList);
        List<EmcCsmStockLogBean> stockLogList = stockList.stream().map(stock -> {
            EmcCsmStockLogBean EmcCsmStockLogBean = new EmcCsmStockLogBean();
            EmcCsmStockLogBean.setExt$Item("operateqty", "0");
            EmcCsmStockLogBean.setPreviousQty(stock.getStockQty());
            EmcCsmStockLogBean.setId(ApplicationContextHelper.getNextIdentity());
            EmcCsmStockLogBean.setStockId(stock.getId());
            EmcCsmStockLogBean.setLogType(LOG_TYPE_ABANDON);
            EmcCsmStockLogBean.setQty(stock.getStockQty());
            EmcCsmStockLogBean.setLogContent(emcCsmStockLog.getLogContent());
            return EmcCsmStockLogBean;
        }).collect(Collectors.toList());
        // 历史记录
        csmStockLogService.getDao().insert(stockLogList);

        EmcCsmStockBean EmcCsmStockBean = new EmcCsmStockBean();
        EmcCsmStockBean.setStatus(Constant.YES);
        // 库存
        getDao().updateByIds(EmcCsmStockBean, stockIdList, STOCK_STATUS);

        // 更新材料管理库存
        consumeService.updateStockQty(mtlId);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void recieve(Long id, RestJsonWrapperBean wrapper) {
        EmcCsmStockLogBean emcCsmStockLog = wrapper.parseUnique(EmcCsmStockLogBean.class);
        emcCsmStockLog.setId(ApplicationContextHelper.getNextIdentity());
        emcCsmStockLog.setStockId(id);
        emcCsmStockLog.setLogType(LOG_TYPE_RECIEVE);
        EmcCsmStockBean emcCsmStock = getDao().selectById(id);
        emcCsmStockLog.setExt$Item("operateqty", emcCsmStockLog.getQty() + "");
        emcCsmStockLog.setPreviousQty(emcCsmStock.getStockQty());
        if (emcCsmStock.getStockQty() - emcCsmStockLog.getQty() < 0) {
            throw new CheckedException("T_EMC_CONSUME.CHECK.STOCKQTY_MAX");
        }
        emcCsmStock.setStockQty(emcCsmStock.getStockQty() - emcCsmStockLog.getQty());
        emcCsmStockLog.setQty(emcCsmStock.getStockQty());

        // 库存
        getDao().update(emcCsmStock, "STOCKQTY");

        // 历史记录
        csmStockLogService.getDao().insert(emcCsmStockLog);

        // 更新材料管理库存
        consumeService.updateStockQty(emcCsmStock.getMtlId());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(String mtlId, RestJsonWrapperBean wrapper) {
        EmcCsmStockService.super.delete(wrapper);
        // 更新材料管理库存
        consumeService.updateStockQty(mtlId);
    }
}
