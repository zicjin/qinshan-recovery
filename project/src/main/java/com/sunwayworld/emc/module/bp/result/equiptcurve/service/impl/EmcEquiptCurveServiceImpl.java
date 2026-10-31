package com.sunwayworld.emc.module.bp.result.equiptcurve.service.impl;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcEquiptCurveConfigService;
import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveStandardBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveStandardService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.bp.result.equiptcurve.dao.EmcEquiptCurveDao;
import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@GikamBean
public class EmcEquiptCurveServiceImpl implements EmcEquiptCurveService {

    @Autowired
    private EmcEquiptCurveDao emcEquiptCurveDao;

    @Autowired
    private EmcEquiptCurveConfigService emcEquiptCurveConfigService;

    @Autowired
    private EmcEquiptCurveStandardService emcEquiptCurveStandardService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCurveDao getDao() {
        return emcEquiptCurveDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptCurveBean emcEquiptCurve = new EmcEquiptCurveBean();
        Long equiptId = NumberUtils.parseLong(jsonWrapper.getParamValue("equiptId"));
        emcEquiptCurve.setId(ApplicationContextHelper.getNextIdentity());
        emcEquiptCurve.setEquiptCurveNo(ApplicationContextHelper.getNextSequence(getDao().getTable(), PfConstant.CURVE_NO_PREFIX_B));
        emcEquiptCurve.setEquiptId(equiptId);
        getDao().insert(emcEquiptCurve);
        return emcEquiptCurve.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcEquiptCurveBean> equiptCurveList = wrapper.parse(EmcEquiptCurveBean.class);
        List<EmcEquiptCurveBean> updateList = new ArrayList<>();
        for (EmcEquiptCurveBean e : equiptCurveList){
            Long oldId = e.getId();
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEquiptCurveNo(ApplicationContextHelper.getNextSequence(getDao().getTable(), PfConstant.CURVE_NO_PREFIX_B));
            e.setExpireDate(null);
            e.setCreatedById(LocalContextHelper.getLoginUserId());
            e.setCreatedByName(LocalContextHelper.getLoginUserName());
            e.setCreatedTime(LocalDateTime.now());
            e.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
            e.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
            List<EmcEquiptCurveConfigBean> curveConfigBeans = emcEquiptCurveConfigService.selectListByFilter(SearchFilter.instance().match("EQUIPTCURVEID", oldId).filter(MatchPattern.SEQ));
            List<EmcEquiptCurveStandardBean> curveStandardBeans = emcEquiptCurveStandardService.selectListByFilter(SearchFilter.instance().match("EQUIPTCURVEID", oldId).filter(MatchPattern.SEQ));
            curveConfigBeans.stream().forEach(c -> {
                c.setId(ApplicationContextHelper.getNextIdentity());
                c.setEquiptCurveId(e.getId());
            });
            curveStandardBeans.stream().forEach(s -> {
                s.setId(ApplicationContextHelper.getNextIdentity());
                s.setEquiptCurveId(e.getId());
            });
            emcEquiptCurveConfigService.getDao().insert(curveConfigBeans);
            emcEquiptCurveStandardService.getDao().insert(curveStandardBeans);
            updateList.add(e);
        }
        getDao().insert(updateList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchUnit(String ids, RestJsonWrapperBean wrapper) {
        EmcEquiptCurveConfigBean emcEquiptCurveConfigBean = wrapper.parseUnique(EmcEquiptCurveConfigBean.class);
        List<Long> equiptCurveConfigIds = new ArrayList<>();
        List<EmcEquiptCurveConfigBean> updateList = new ArrayList<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] strs = ids.split(",");
            for (String str : strs ){
                equiptCurveConfigIds.add(Long.parseLong(str));
            }
        }
        List<EmcEquiptCurveConfigBean> emcEquiptCurveConfigBeanList = emcEquiptCurveConfigService.selectListByIds(equiptCurveConfigIds);
        for (EmcEquiptCurveConfigBean configBean : emcEquiptCurveConfigBeanList){
            configBean.setConcentrationUnit(emcEquiptCurveConfigBean.getConcentrationUnit());
            configBean.setVolumeUnit(emcEquiptCurveConfigBean.getVolumeUnit());
            updateList.add(configBean);
        }
        emcEquiptCurveConfigService.getDao().update(updateList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertStandard(Long id, RestJsonWrapperBean wrapper) {
        EmcEquiptCurveStandardBean emcEquiptCurveStandardBean = wrapper.parseUnique(EmcEquiptCurveStandardBean.class);
        emcEquiptCurveStandardBean.setId(ApplicationContextHelper.getNextIdentity());
        emcEquiptCurveStandardBean.setEquiptCurveId(id);
        emcEquiptCurveStandardService.getDao().insert(emcEquiptCurveStandardBean);
        return emcEquiptCurveStandardBean.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertByEquiptId(Long equiptId, RestJsonWrapperBean wrapper) {
        EmcEquiptCurveBean emcEquiptCurveBean = wrapper.parseUnique(EmcEquiptCurveBean.class);
        emcEquiptCurveBean.setId(ApplicationContextHelper.getNextIdentity());
        emcEquiptCurveBean.setEquiptCurveNo(ApplicationContextHelper.getNextSequence(getDao().getTable(), PfConstant.CURVE_NO_PREFIX_B));
        emcEquiptCurveBean.setEquiptId(equiptId);
        this.getDao().insert(emcEquiptCurveBean);
        return emcEquiptCurveBean.getId();
    }
}
