package com.sunwayworld.emc.module.item.mtlapply.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.mtlapply.bean.EmcMtlApplyBean;
import com.sunwayworld.emc.module.item.mtlapply.dao.EmcMtlApplyDao;
import com.sunwayworld.emc.module.item.mtlapply.service.EmcMtlApplyService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@Repository
@GikamBean
public class EmcMtlApplyServiceImpl implements EmcMtlApplyService {

    private static final String LOG_TYPE_RECEIVE = "领取";

    @Autowired
    private EmcStockService stockService;

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    private EmcStockLogService stockLogService;

    @Autowired
    private EmcMtlApplyDao emcMtlApplyDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMtlApplyDao getDao() {
        return emcMtlApplyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        List<EmcMtlApplyBean> emcMtlApplyList = wrapperBean.parse(EmcMtlApplyBean.class);
        emcMtlApplyList.forEach(emcMtlApplyBean -> {
            Long id = ApplicationContextHelper.getNextIdentity();
            emcMtlApplyBean.setId(id);
        });
        getDao().insert(emcMtlApplyList);
        return 0L;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        // 正常处理工作流
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcMtlApplyService.super.completeTask(wrapper);
        List<Long> mtlApplyIdList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcMtlApplyBean> mtlApplyList = selectListByFilter(SearchFilter.instance().match("ID", mtlApplyIdList).filter(MatchPattern.OR));

        List<Long> stockIdList = mtlApplyList.stream().map(EmcMtlApplyBean::getStockId).collect(Collectors.toList());
        List<EmcStockBean> stockList = stockService.getDao().selectListByIds(stockIdList);

        List<String> mtlIdList = mtlApplyList.stream().map(EmcMtlApplyBean::getMtlId).collect(Collectors.toList());
        List<EmcMtlBean> mtlList = mtlService.getDao().selectListByIds(mtlIdList);

        List<EmcStockLogBean> insertStockLogList = new ArrayList<>();

        Map<String, EmcMtlBean> updateMtlMap = new HashMap<>();
        Map<Long, EmcStockBean> updateStockMap = new HashMap<>();

        mtlApplyList.forEach(m -> {
            EmcStockLogBean stockLog = new EmcStockLogBean();
            stockLog.setId(ApplicationContextHelper.getNextIdentity());
            stockLog.setStockId(m.getStockId());
            stockLog.setLogType(LOG_TYPE_RECEIVE);
            stockLog.setReceivedById(m.getCreatedById());
            stockLog.setReceivedByOrgId(m.getCreatedByOrgId());

            EmcStockBean updateStock = updateStockMap.get(m.getStockId());
            if (updateStock == null) {
                updateStock = new EmcStockBean();
                updateStock.setId(m.getStockId());
                EmcStockBean stockBean = stockList.stream().filter(s -> ObjectUtils.equals(s.getId(), m.getStockId())).findFirst().get();
                updateStock.setStockQty(stockBean.getStockQty() - m.getApplyQty());
                stockLog.setPreviousQty(stockBean.getStockQty());
            } else {
                stockLog.setPreviousQty(updateStock.getStockQty());
                updateStock.setStockQty(updateStock.getStockQty() - m.getApplyQty());
            }

            stockLog.setQty(updateStock.getStockQty());
            insertStockLogList.add(stockLog);
            updateStockMap.put(updateStock.getId(), updateStock);

            EmcMtlBean updateMtl = updateMtlMap.get(m.getMtlId());
            if (updateMtl == null) {
                updateMtl = new EmcMtlBean();
                EmcMtlBean mtlBean = mtlList.stream().filter(t -> ObjectUtils.equals(t.getId(), m.getMtlId())).findFirst().get();
                updateMtl.setId(m.getMtlId());
                updateMtl.setStockQty(mtlBean.getStockQty() - m.getApplyQty());
            } else {
                updateMtl.setStockQty(updateMtl.getStockQty() - m.getApplyQty());
            }

            updateMtlMap.put(m.getMtlId(), updateMtl);
        });

        List<EmcStockBean> updateStockList = new ArrayList<>(updateStockMap.values());
        List<EmcMtlBean> updateMtlList = new ArrayList<>(updateMtlMap.values());
        stockService.getDao().update(updateStockList);
        mtlService.getDao().update(updateMtlList);
        stockLogService.getDao().insert(insertStockLogList);

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public RestValidationResultBean validateStock(RestJsonWrapperBean wrapper) {
        List<EmcMtlApplyBean> applyList = wrapper.parse(EmcMtlApplyBean.class);
        List<Long> stockIdList = applyList.stream().map(EmcMtlApplyBean::getStockId).collect(Collectors.toList());

        // 将申请按照库存编号进行分组
        Map<Long, List<EmcMtlApplyBean>> applyStockMap = applyList.stream().collect(Collectors.groupingBy(EmcMtlApplyBean::getStockId));
        Map<Long, Double> applyQtyMap = new HashMap<>();
        applyStockMap.forEach((k, v) -> applyQtyMap.put(k, v.stream().mapToDouble(EmcMtlApplyBean::getApplyQty).sum()));

        List<EmcStockBean> stockList = stockService.getDao().selectListByIds(stockIdList);
        boolean validateResult = stockList.stream().anyMatch(s -> applyQtyMap.get(s.getId()) > s.getStockQty());
        if (validateResult) {
            return new RestValidationResultBean(false);
        }

        return new RestValidationResultBean(true);
    }
}
