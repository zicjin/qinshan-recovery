package com.sunwayworld.emc.module.bp.result.olifilm.service.impl;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.emc.module.bp.result.olifilm.dao.EmcOilFilmDao;
import com.sunwayworld.emc.module.bp.result.olifilm.bean.EmcOilFilmBean;
import com.sunwayworld.emc.module.bp.result.olifilm.service.EmcOilFilmService;
import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean

public class EmcOilFilmServiceImpl implements EmcOilFilmService {

    @Autowired
    private EmcOilFilmDao emcOilFilmDao;

    @Autowired
    private CoreOrgService orgService;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcOilFilmDao getDao() {
        return emcOilFilmDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
    	String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");    	
        List<CoreOrgBean> orgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
        CoreOrgBean coreOrg= orgList.get(0);
        String orgShortName = "H"+coreOrg.getExt$Item("orgshortname");
        String oilFilmNo = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
        EmcOilFilmBean emcOilFilm = new EmcOilFilmBean();
        emcOilFilm.setId(ApplicationContextHelper.getNextIdentity());
        emcOilFilm.setOilFilmNo(oilFilmNo);
        emcOilFilm.setOrgId(coreOrg.getId());
        emcOilFilm.setCreatedById(LocalContextHelper.getLoginUserId());
        emcOilFilm.setCreatedByName(LocalContextHelper.getLoginUserName());
        emcOilFilm.setCreatedTime(LocalDateTime.now());
        emcOilFilm.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        emcOilFilm.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        getDao().insert(emcOilFilm);
        return emcOilFilm.getId();
    }
    
    @Override
    @Log(value = "批量新增", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String batchAdd(RestJsonWrapperBean jsonWrapper) {
    	//插入数据次数
    	String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");    	
        List<CoreOrgBean> orgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
        CoreOrgBean coreOrg= orgList.get(0);
        String orgShortName = "H"+coreOrg.getExt$Item("orgshortname");
        
    	String addnum= jsonWrapper.getFilterValue("addnum");
    	if (addnum != null && addnum.length() != 0){
        	int addnum1 = Integer.parseInt(addnum);
        	  for (int i =1;i<=addnum1;i++) {
        	      String oilFilmNo = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
        		  EmcOilFilmBean emcOilFilm = new EmcOilFilmBean();
                  emcOilFilm.setId(ApplicationContextHelper.getNextIdentity());
                  emcOilFilm.setOilFilmNo(oilFilmNo);
                  emcOilFilm.setOrgId(coreOrg.getId());
                  emcOilFilm.setCreatedById(LocalContextHelper.getLoginUserId());
                  emcOilFilm.setCreatedByName(LocalContextHelper.getLoginUserName());
                  emcOilFilm.setCreatedTime(LocalDateTime.now());
                  emcOilFilm.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                  emcOilFilm.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                  getDao().insert(emcOilFilm);  
              }     		
        	
    	}
       
        return addnum;

    }
    

}
