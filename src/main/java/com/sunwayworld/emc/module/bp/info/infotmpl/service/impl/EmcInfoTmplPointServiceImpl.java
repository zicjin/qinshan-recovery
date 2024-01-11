package com.sunwayworld.emc.module.bp.info.infotmpl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplPointDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Repository
@GikamBean
public class EmcInfoTmplPointServiceImpl implements EmcInfoTmplPointService {
    @Autowired
    private EmcInfoTmplPointDao infoTmplPointDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoTmplPointDao getDao() {
        return infoTmplPointDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInfoTmplPointBean infoTmplPoint = jsonWrapper.parseUnique(EmcInfoTmplPointBean.class);
        infoTmplPoint.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(infoTmplPoint);
        return infoTmplPoint.getId();
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public void insertMul(RestJsonWrapperBean jsonWrapper) {
        List<EmcInfoTmplPointBean> infoTmplPointList = jsonWrapper.parse(EmcInfoTmplPointBean.class);
        infoTmplPointList.forEach(infoTmplPoint -> {
            infoTmplPoint.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(infoTmplPointList);
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE) 
    public void deleteMul(RestJsonWrapperBean jsonWrapper) {
        List<EmcInfoTmplPointBean> infoTmplPointList = jsonWrapper.parse(EmcInfoTmplPointBean.class);
        getDao().deleteBy(infoTmplPointList);
    }
    
    @Override
    @Log(value = "复制模板监测点", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public List<EmcInfoTmplPointBean> copy(Pair<Long, Long> infoTmplIdPair) {
        //查询原模板对象
        List<EmcInfoTmplPointBean> infoTmplPointList = getDao().selectListByOneColumnValue(infoTmplIdPair.getFirst(), "INFOTMPLID", Order.asc("ID"));
        if(CollectionUtils.isEmpty(infoTmplPointList)) {
            return new ArrayList<>();
        }
        //新的模板字段项列表
        List<EmcInfoTmplPointBean> newInfoTmplPointList = infoTmplPointList.stream().map(infoTmplPoint -> {
            infoTmplPoint.setId(ApplicationContextHelper.getNextIdentity());
            infoTmplPoint.setInfoTmplId(infoTmplIdPair.getSecond());
            return infoTmplPoint;
        }).collect(Collectors.toList());
        //插入数据库
        getDao().insert(newInfoTmplPointList);
        return newInfoTmplPointList;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE) 
    public void updateMul(RestJsonWrapperBean jsonWrapper) {
        List<EmcInfoTmplBean> infoTmplList = jsonWrapper.parse(EmcInfoTmplBean.class);        
        List<EmcMonitorPointBean> monitorPointList = jsonWrapper.parse(EmcMonitorPointBean.class);        
        
        // 先将各模板的监测点数据删除
        List<EmcInfoTmplPointBean> infoTmplPointList = new ArrayList<>();
        infoTmplList.forEach(infoTmpl -> {
            EmcInfoTmplPointBean infoTmplPoint = new EmcInfoTmplPointBean();
            infoTmplPoint.setInfoTmplId(infoTmpl.getId());
            infoTmplPointList.add(infoTmplPoint);
        });
        getDao().deleteBy(infoTmplPointList, "INFOTMPLID");
        
        // 再插入选择的模板和监测点
        if(CollectionUtils.isEmpty(monitorPointList))
            return;
        infoTmplPointList.clear();
        infoTmplList.forEach(infoTmpl -> {
            monitorPointList.forEach(monitorPoint -> {
                EmcInfoTmplPointBean infoTmplPoint = new EmcInfoTmplPointBean();
                infoTmplPoint.setId(ApplicationContextHelper.getNextIdentity());
                infoTmplPoint.setInfoTmplId(infoTmpl.getId());
                infoTmplPoint.setMonitorPointId(monitorPoint.getId());
                infoTmplPointList.add(infoTmplPoint);
            });
        });
        getDao().insert(infoTmplPointList);
    }
}
