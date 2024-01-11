package com.sunwayworld.emc.module.bp.info.infotmpl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplPointBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcTmplFieldItemBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.dao.EmcInfoTmplDao;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplPointService;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplService;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcTmplFieldItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-19
 */

@Repository
@GikamBean
public class EmcInfoTmplServiceImpl implements EmcInfoTmplService {

    @Autowired
    private EmcInfoTmplDao infoTmplDao;

    @Autowired
    private EmcInfoTmplFieldService infoTmplFieldService;

    @Autowired
    private EmcTmplFieldItemService tmplFieldItemService;

    @Autowired
    private EmcInfoTmplPointService infoTmplPointService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoTmplDao getDao() {
        return infoTmplDao;
    }

    @Override
    @Log(value = "新增上报模板", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        // 插入上传模板数据
        EmcInfoTmplBean infoTmpl = jsonWrapper.parseUnique(EmcInfoTmplBean.class);
        infoTmpl.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(infoTmpl);

        // 初始字段项数据
        infoTmplFieldService.initData(infoTmpl.getId());

        return infoTmpl.getId();
    }

    @Override
    @Log(value = "复制上报模板", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcInfoTmplBean copy(Long tmplId) {
        // 复制上报模板
        EmcInfoTmplBean infoTmpl = getDao().selectById(tmplId);
        infoTmpl.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(infoTmpl);

        // 复制模板
        // 模板来源编码和复制后新编码的映射关系
        Pair<Long, Long> infoTmplIdPair = Pair.of(tmplId, infoTmpl.getId());
        infoTmplPointService.copy(infoTmplIdPair);

        // 复制模板字段
        List<Pair<Long, Long>> infoTmplFieldIdPairList = infoTmplFieldService.copy(infoTmplIdPair);

        // 复制模板字段分析项
        if (!CollectionUtils.isEmpty(infoTmplFieldIdPairList)) {
            tmplFieldItemService.copy(infoTmplFieldIdPairList);
        }

        return infoTmpl;
    }

    @Override
    @Log(value = "查询上报模板字段项", type = LogType.SELECT)
    public Page<EmcInfoTmplFieldBean> selectTmplFieldPaginationByTmplId(Long tmplId, RestJsonWrapperBean wrapper) {
        return infoTmplFieldService.selectPaginationByFilter(SearchFilter.instance().match("INFOTMPLID", tmplId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询上报模板字段项", type = LogType.SELECT)
    public Page<EmcInfoTmplFieldBean> selectTmplFieldPagination(RestJsonWrapperBean wrapper) {
        return infoTmplFieldService.selectPagination(wrapper);
    }
    
    @Override
    @Log(value = "查询上报模板字段项", type = LogType.SELECT)
    public List<EmcInfoTmplFieldBean> selectTmplFieldSelectableList() {
        return infoTmplFieldService.selectSelectableList();
    }

    @Override
    @Log(value = "查询上报模板字段分析项", type = LogType.SELECT)
    public Page<EmcTmplFieldItemBean> selectTmplFieldItemPaginationByTmplId(RestJsonWrapperBean wrapper) {
        return tmplFieldItemService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "新增上报模板字段分析项", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertTmplFieldItem(Long tmplId, RestJsonWrapperBean wrapper) {
        return tmplFieldItemService.insert(tmplId, wrapper);
    }

    @Override
    @Log(value = "删除上报模板字段分析项", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteTmplFieldItem(RestJsonWrapperBean wrapper) {
        tmplFieldItemService.delete(wrapper);
    }

    @Override
    @Log(value = "查询上报模板监测点", type = LogType.SELECT)
    public Page<EmcInfoTmplPointBean> selectTmplPointPaginationByTmplId(Long tmplId, RestJsonWrapperBean wrapper) {
        return infoTmplPointService.selectPaginationByFilter(SearchFilter.instance().match("INFOTMPLID", tmplId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "查询上报模板监测点", type = LogType.SELECT)
    public Page<EmcInfoTmplPointBean> selectTmplPointPagination(RestJsonWrapperBean wrapper) {
        return infoTmplPointService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "新增上报模板监测点", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertTmplPoint(RestJsonWrapperBean wrapper) {
        infoTmplPointService.insertMul(wrapper);
    }

    @Override
    @Log(value = "删除上报模板监测点", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteTmplPoint(RestJsonWrapperBean wrapper) {
        infoTmplPointService.deleteMul(wrapper);
    }

    @Override
    @Log(value = "同时更新多个上报模板的监测点", type = LogType.UPDATE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateTmplPoint(RestJsonWrapperBean wrapper) {
        infoTmplPointService.updateMul(wrapper);
    }
}
