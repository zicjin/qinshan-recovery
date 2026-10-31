package com.sunwayworld.emc.module.mdm.bizmdm.client.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcClientDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientContactService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Repository
@GikamBean
public class EmcClientServiceImpl implements EmcClientService {
    @Autowired
    private EmcClientDao clientDao;

    @Autowired
    public EmcClientContactService clientContactService;

    @Autowired
    @Lazy
    public EmcMonitorPointService monitorPointService;

    @Autowired
    @Lazy
    public EmcMonitorPointSchemeService monitorPointSchemeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcClientDao getDao() {
        return clientDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcClientBean emcClient = jsonWrapper.parseUnique(EmcClientBean.class);
        emcClient.setId(ApplicationContextHelper.getNextIdentity());
        emcClient.setClientNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));

        String lxrName = emcClient.getExt$Item("lxrname");
        String lxrPhone = emcClient.getExt$Item("lxrphone");
        if (!StringUtils.isEmpty(lxrName) && !StringUtils.isEmpty(lxrPhone)) {
            EmcClientContactBean clientContact = new EmcClientContactBean();
            clientContact.setClientId(emcClient.getId());
            clientContact.setId(ApplicationContextHelper.getNextSequenceNumber(clientContactService.getDao().getTable()));
            clientContact.setLxrName(lxrName);
            clientContact.setLxrPhone(lxrPhone);
            clientContactService.getDao().insert(clientContact);
        }

        getDao().insert(emcClient);
        return emcClient.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertContact(Long id, RestJsonWrapperBean wrapper) {
        clientContactService.insert(id, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertMonitorPoint(Long id, RestJsonWrapperBean wrapper) {
        monitorPointService.insert(id, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper) {
        monitorPointSchemeService.insert(monitorPointId, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteContact(RestJsonWrapperBean wrapper) {
        clientContactService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteMonitorPoint(RestJsonWrapperBean wrapper) {
        monitorPointService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteMonitorPointScheme(RestJsonWrapperBean wrapper) {
        monitorPointSchemeService.delete(wrapper);
    }

    @Override
    public Page<EmcClientContactBean> selectContactPaginationByClientId(Long clientId, RestJsonWrapperBean wrapper) {
        return clientContactService.selectPaginationByFilter(SearchFilter.instance().match("CLIENTID", clientId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcMonitorPointBean> selectMonitorPointPaginationByClientId(Long clientId, RestJsonWrapperBean wrapper) {
        return monitorPointService.selectPaginationByFilter(SearchFilter.instance().match("CLIENTID", clientId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcMonitorPointSchemeBean> selectSchemePaginationByMonitorPointId(Long monitorPointId, RestJsonWrapperBean wrapper) {
        return monitorPointSchemeService.selectPaginationByFilter(SearchFilter.instance().match("MONITORPOINTID", monitorPointId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void insertMonitorPointIndex(RestJsonWrapperBean wrapper) {
        monitorPointService.getDao().update(wrapper.parseUnique(EmcMonitorPointBean.class), "indexId");

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper) {
        monitorPointSchemeService.copyMonitorPointScheme(monitorPointId, wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void copyMonitorPointIndex(RestJsonWrapperBean wrapper) {
        monitorPointService.getDao().update(wrapper.parse(EmcMonitorPointBean.class), "indexId");
    }

    @Override
    public Page<EmcMonitorPointBean> selectMonitorPointChoosablePagination(RestJsonWrapperBean wrapper) {
        return monitorPointService.selectChoosablePagination(wrapper);
    }

    @Override
    public Page<EmcMonitorPointSchemeBean> selectMonitorPointSchemeChoosablePagination(RestJsonWrapperBean wrapper) {
        return monitorPointSchemeService.selectChoosablePagination(wrapper);
    }

    @Override
    public List<EmcMonitorPointBean> selectFilterPointCategorySelectableList() {
        List<EmcMonitorPointBean> pointList = monitorPointService.selectSelectableList();
        List<EmcMonitorPointBean> filterPointList = pointList.stream().filter(f -> !ObjectUtils.isEmpty(f.getMonitorPointCategoryId())).collect(Collectors.toList());
        return new ArrayList<>(filterPointList.stream().collect(Collectors.groupingBy(EmcMonitorPointBean::getMonitorPointCategoryId, Collectors.collectingAndThen(Collectors.toList(), v -> v.get(0)))).values());
    }

    @Override
    public Page<EmcMonitorPointBean> selectMonitorPointByTestOrgId(RestJsonWrapperBean wrapper) {
        return monitorPointService.selectPagination(wrapper);
    }

    @Override
    public Page<EmcClientContactBean> selectContactChoosablePagination(RestJsonWrapperBean wrapper) {
        return clientContactService.selectChoosablePagination(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyMonitorPoint(RestJsonWrapperBean wrapper) {
        monitorPointService.copy(wrapper);
    }

    @Override
    public Page<EmcClientBean> selectSubClientChoosablePagination(RestJsonWrapperBean wrapper) {
        List<EmcClientBean> clientList = getDao().selectClientByCondition(wrapper.extractMapFilter()).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcClientBean.class)).collect(Collectors.toList());
        return new Pagination<>(clientList);
    }
}
