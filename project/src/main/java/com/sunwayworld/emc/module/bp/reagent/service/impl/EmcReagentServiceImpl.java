package com.sunwayworld.emc.module.bp.reagent.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentBean;
import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.emc.module.bp.reagent.dao.EmcReagentDao;
import com.sunwayworld.emc.module.bp.reagent.service.EmcReagentCompositionService;
import com.sunwayworld.emc.module.bp.reagent.service.EmcReagentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;

@Repository
@GikamBean
public class EmcReagentServiceImpl implements EmcReagentService {
    @Autowired
    private EmcReagentDao reagentDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReagentDao getDao() {
        return reagentDao;
    }

    @Autowired
    private EmcReagentCompositionService reagentCompositionService;

    @Override
    public Page<EmcReagentCompositionBean> selectReagentCompositionPagination(Long id, RestJsonWrapperBean wrapper) {
        return reagentCompositionService.selectPaginationByFilter(SearchFilter.instance().match("REAGENTID", id).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    public Long insertComposition(Long id, RestJsonWrapperBean wrapper) {
        return reagentCompositionService.insert(id, wrapper);
    }

    @Override
    public void deleteComposition(Long id, RestJsonWrapperBean wrapper) {
        reagentCompositionService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long copyReagent(Long id, RestJsonWrapperBean wrapper) {

        EmcReagentBean reagent = wrapper.parseUnique(EmcReagentBean.class);
        EmcReagentBean reagentCopy = new EmcReagentBean();
        BeanUtils.copyProperties(reagent, reagentCopy);
        reagentCopy.setId(ApplicationContextHelper.getNextIdentity());
        reagentCopy.setPrepareDate(null);
        reagentCopy.setTemperature(null);
        reagentCopy.setHumidity(null);
        getDao().insert(reagentCopy);

        Page<EmcReagentCompositionBean> compositionPage = selectReagentCompositionPagination(reagent.getId(),new RestJsonWrapperBean());
        List<EmcReagentCompositionBean> compositionCopyList = new ArrayList<>();
        compositionPage.getRows().stream().forEach(c -> {
            EmcReagentCompositionBean compositionCopy = new EmcReagentCompositionBean();
            BeanUtils.copyProperties(c, compositionCopy);
            compositionCopy.setId(ApplicationContextHelper.getNextIdentity());
            compositionCopy.setReagentId(reagentCopy.getId());
            compositionCopyList.add(compositionCopy);
        });
        reagentCompositionService.getDao().insert(compositionCopyList);

        return reagentCopy.getId();
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcReagentBean reagent = jsonWrapper.parseUnique(EmcReagentBean.class);
        reagent.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(reagent);
        return reagent.getId();
    }

}
