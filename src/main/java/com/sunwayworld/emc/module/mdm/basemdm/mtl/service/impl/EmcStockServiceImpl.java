package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:33:21
 */
@Repository
@GikamBean
public class EmcStockServiceImpl implements EmcStockService {

    @Autowired
    private EmcStockDao stockDao;

    @Autowired
    private EmcStockLogService stockLogService;

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private EmcStockConcentService stockConcentService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcStockDao getDao() {
        return stockDao;
    }

    public static final String LOG_TYPE_ADD = "添加";
    public static final String LOG_TYPE_STORAGE = "入库";
    public static final String LOG_TYPE_INVENTORY = "盘存";
    public static final String LOG_TYPE_ABANDON = "废弃";
    public static final String LOG_TYPE_RECIEVE = "领取";
    public static final String STOCK_QTY = "STOCKQTY";
    public static final String STOCK_STATUS = "STATUS";

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);

        //库存
        EmcStockBean emcStock = wrapper.parseUnique(EmcStockBean.class);

        List<EmcMtlMaintainBean> maintainList = mtlMaintainService.getDao().selectListByOneColumnValue(mtlId,"MTLID");

        int qty = Optional.ofNullable(emcStockLog.getQty()).orElse(0D).intValue();

        List<EmcStockBean> newStockList = new ArrayList<>(qty);
        List<EmcStockLogBean> newStockLogList = new ArrayList<>(qty);
        List<EmcStockConcentBean> newStockConcentList = new ArrayList<>();

        IntStream.range(0, qty).forEach(i -> {
            EmcStockBean newStock = new EmcStockBean();
            BeanUtils.copyProperties(emcStock, newStock);
            newStock.setStockQty(1D);
            newStock.setId(ApplicationContextHelper.getNextIdentity());
            newStock.setMtlId(mtlId);
            newStock.setStatus(Constant.NO);
            newStockList.add(newStock);

            EmcStockLogBean newStockLog = new EmcStockLogBean();
            BeanUtils.copyProperties(emcStockLog, newStockLog);
            newStockLog.setQty(1D);
            newStockLog.setId(ApplicationContextHelper.getNextIdentity());
            newStockLog.setStockId(newStock.getId());
            if (emcStockLog.getLogType() == null) {
                newStockLog.setLogType(LOG_TYPE_ADD);
            }
            newStockLogList.add(newStockLog);

            EmcStockConcentBean newStockConcent = new EmcStockConcentBean();
            newStockConcent.setId(ApplicationContextHelper.getNextIdentity());
            newStockConcent.setStockId(newStock.getId());
            newStockConcent.setCategory(maintainList.get(0).getCategory());
            newStockConcent.setMtlMaintainId(maintainList.get(0).getId());
            newStockConcentList.add(newStockConcent);
        });

        getDao().insert(newStockList);

        //历史记录
        stockLogService.getDao().insert(newStockLogList);

        //更新材料管理库存
        mtlService.updateStockQty(mtlId);

        //添加浓度
        stockConcentService.getDao().insert(newStockConcentList);

        return emcStock.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void storage(Long id, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);

        //历史记录
        emcStockLog.setId(ApplicationContextHelper.getNextIdentity());
        emcStockLog.setStockId(id);
        emcStockLog.setLogType(LOG_TYPE_STORAGE);
        EmcStockBean emcStock = getDao().selectById(id);
        emcStockLog.setPreviousQty(emcStockLog.getQty());
        emcStock.setStockQty(emcStock.getStockQty() + emcStockLog.getQty());
        emcStockLog.setExt$Item("operateqty", emcStockLog.getQty() + "");
        emcStockLog.setQty(emcStock.getStockQty());
        stockLogService.getDao().insert(emcStockLog);

        //库存
        getDao().update(emcStock, STOCK_QTY);

        //更新材料管理库存
        mtlService.updateStockQty(emcStock.getMtlId());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void inventory(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);
        List<Long> stockIdList = Arrays.stream( emcStockLog.getExt$().get("stockId").split(","))
                                 .map(stockId -> Long.parseLong(stockId)).collect(Collectors.toList());
        List<EmcStockBean> stockList = getDao().selectListByIds(stockIdList);
        List<EmcStockLogBean> stockLogList = stockList.stream().map(stock -> {
            EmcStockLogBean emcStockLogBean = new EmcStockLogBean();
            emcStockLogBean.setPreviousQty(stock.getStockQty());
            emcStockLogBean.setId(ApplicationContextHelper.getNextIdentity());
            emcStockLogBean.setStockId(stock.getId());
            emcStockLogBean.setLogType(LOG_TYPE_INVENTORY);
            emcStockLogBean.setQty(emcStockLog.getQty());
            emcStockLogBean.setLogContent(emcStockLog.getLogContent());
            emcStockLog.setExt$Item("operateqty", emcStockLog.getQty() + "");
            return emcStockLogBean;
        }).collect(Collectors.toList());
        //历史记录
        stockLogService.getDao().insert(stockLogList);

        EmcStockBean emcStock = new EmcStockBean();
        emcStock.setStockQty(emcStockLog.getQty());
        //库存
        getDao().updateByIds(emcStock, stockIdList, STOCK_QTY);

        //更新材料管理库存
        mtlService.updateStockQty(mtlId);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void abandon(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);
        List<Long> stockIdList = Arrays.stream( emcStockLog.getExt$().get("stockId").split(","))
                                 .map(stockId -> Long.parseLong(stockId)).collect(Collectors.toList());
        List<EmcStockBean> stockList = getDao().selectListByIds(stockIdList);
        List<EmcStockLogBean> stockLogList = stockList.stream().map(stock -> {
            EmcStockLogBean emcStockLogBean = new EmcStockLogBean();
            emcStockLogBean.setPreviousQty(stock.getStockQty());
            emcStockLogBean.setId(ApplicationContextHelper.getNextIdentity());
            emcStockLogBean.setStockId(stock.getId());
            emcStockLogBean.setLogType(LOG_TYPE_ABANDON);
            emcStockLogBean.setQty(stock.getStockQty());
            emcStockLogBean.setExt$Item("operateqty", "0");
            return emcStockLogBean;
        }).collect(Collectors.toList());
        //历史记录
        stockLogService.getDao().insert(stockLogList);

        EmcStockBean emcStockBean = new EmcStockBean();
        emcStockBean.setStatus(Constant.YES);
        //库存
        getDao().updateByIds(emcStockBean, stockIdList, STOCK_STATUS);

        //更新材料管理库存
        mtlService.updateStockQty(mtlId);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void recieve(Long id, RestJsonWrapperBean wrapper) {
        List<EmcStockLogBean> emcStockLogList = wrapper.parse(EmcStockLogBean.class);
        emcStockLogList.forEach(emcStockLog -> {
            emcStockLog.setId(ApplicationContextHelper.getNextIdentity());
            emcStockLog.setStockId(id);
            emcStockLog.setLogType(LOG_TYPE_RECIEVE);
            EmcStockBean emcStock = getDao().selectById(id);
            emcStockLog.setExt$Item("operateqty", emcStockLog.getQty() + "");
            emcStockLog.setPreviousQty(emcStock.getStockQty());
            emcStock.setStockQty(emcStock.getStockQty() - emcStockLog.getQty());
            emcStockLog.setQty(emcStock.getStockQty());

            //库存
            getDao().update(emcStock, STOCK_QTY);

            //历史记录
            stockLogService.getDao().insert(emcStockLog);

            //更新材料管理库存
            mtlService.updateStockQty(emcStock.getMtlId());
        });
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockService.super.delete(wrapper);
      //更新材料管理库存
        mtlService.updateStockQty(mtlId);
    }


    @Override
    public Page<EmcStockBean> selectStockLowLine(RestJsonWrapperBean wrapper) {

        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectStockLowLine(parameter), rowBounds);

    }

    @Override
    public Page<EmcStockBean> selectStockOverdue(RestJsonWrapperBean wrapper) {

        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectStockOverdue(parameter), rowBounds);

    }

    @Override
    public Page<EmcStockBean> selectDetailChoosablePagination(RestJsonWrapperBean wrapper) {
        return selectPagination(wrapper);
    }

    @Override
    public RestValidationResultBean validateStockUnique(Long id, String columnName, String columnValue) {
        return validateUnique(id, columnName, columnValue);
    }

    @Override
    public Long insertGeneralStock(String mtlId, RestJsonWrapperBean wrapper) {
        EmcStockLogBean emcStockLog = wrapper.parseUnique(EmcStockLogBean.class);

        //库存
        EmcStockBean emcStock = wrapper.parseUnique(EmcStockBean.class);
        emcStock.setId(ApplicationContextHelper.getNextIdentity());
        emcStock.setStockQty(emcStockLog.getQty());
        emcStock.setMtlId(mtlId);
        emcStock.setStatus(Constant.NO);
        getDao().insert(emcStock);

        //历史记录
        emcStockLog.setId(ApplicationContextHelper.getNextIdentity());
        emcStockLog.setStockId(emcStock.getId());
        if (emcStockLog.getLogType() == null) {
            emcStockLog.setLogType(LOG_TYPE_ADD);
        }
        stockLogService.getDao().insert(emcStockLog);

        //更新材料管理库存
        mtlService.updateStockQty(mtlId);

        return emcStock.getId();
    }

    @Override
    public Page<EmcStockBean> selectQcStdStockPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectQcStdStockPagination(parameter), rowBounds);
    }
}
