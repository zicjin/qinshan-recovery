package com.sunwayworld.emc.module.bp.equiptcrves.service.impl;




import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptDetailBean;
import com.sunwayworld.emc.module.bp.equiptcrves.dao.PfAnalyticalEquiptDetailDao;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptDetailService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * @author licm@sunwayworld.com 2021年10月24日
 */
@Repository
@GikamBean
public class PfAnalyticalEquiptDetailServiceImpl implements PfAnalyticalEquiptDetailService {
    @Autowired
    private PfAnalyticalEquiptDetailDao pfAnalyticalEquiptDetailDao;

 
    @Override
    @SuppressWarnings("unchecked")
    public PfAnalyticalEquiptDetailDao getDao() {
        return pfAnalyticalEquiptDetailDao;
    }
    

    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
    	PfAnalyticalEquiptDetailBean equiptDetailBean = jsonWrapper.parseUnique(PfAnalyticalEquiptDetailBean.class);
    	String solId = equiptDetailBean.getSolId();
    	List<PfAnalyticalEquiptDetailBean> detailList = getDao().selectListByOneColumnValue(solId, "SOLID");
		String analysisNumber = null;
    	if(detailList.isEmpty()){
    		analysisNumber = "001";
    	}else{
    		detailList.sort(Comparator.comparing(details -> NumberUtils.parseLong(((PfAnalyticalEquiptDetailBean) details).getAnalysisNumber())).reversed());
    		Long maxNumber = Long.parseLong(detailList.get(0).getAnalysisNumber())+1;
            analysisNumber = String.format("%03d", maxNumber);      
    	} 
/*        String analysisNumber = ApplicationContextHelper.getNextSequence(getDao().getTable());
*/    	equiptDetailBean.setId(ApplicationContextHelper.getNextIdentity());
    	equiptDetailBean.setAnalysisNumber(analysisNumber);
        getDao().insert(equiptDetailBean);
        return equiptDetailBean.getId();
    }
   
    @Override
    @Log(value = "初始化插入曲线", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long addEquiptInfo(RestJsonWrapperBean jsonWrapper) {
    	//插入数据次数
    	int addnum = 7;
    	PfAnalyticalEquiptDetailBean pfAnalyticalEquiptDetailBean = new PfAnalyticalEquiptDetailBean();
    	PfAnalyticalEquiptDetailBean equiptDetailsBean = jsonWrapper.parseUnique(PfAnalyticalEquiptDetailBean.class);	  
        	String runId = equiptDetailsBean.getRunId();
        	String solId = equiptDetailsBean.getSolId();
        	List<PfAnalyticalEquiptDetailBean> detailList = getDao().selectListByOneColumnValue(solId, "SOLID");
    		String analysisNumber = null;
            for (int i =1;i<=addnum;i++) {
            	pfAnalyticalEquiptDetailBean.setId(ApplicationContextHelper.getNextIdentity());
            	pfAnalyticalEquiptDetailBean.setSolId(solId);
            	pfAnalyticalEquiptDetailBean.setRunId(runId);
            	//分析编号要求每条溶液的从001开始,不要向id一样一直递增,
            	if(detailList.isEmpty()){
            		analysisNumber = "00"+i;
            	}else{
            		detailList.sort(Comparator.comparing(details -> NumberUtils.parseLong(((PfAnalyticalEquiptDetailBean) details).getAnalysisNumber())).reversed());
            		Long maxNumber = Long.parseLong(detailList.get(0).getAnalysisNumber())+i;
                    analysisNumber = String.format("%03d", maxNumber);      
            	}  	
/*                String analysisNumber = ApplicationContextHelper.getNextSequence(getDao().getTable());
*/            	pfAnalyticalEquiptDetailBean.setAnalysisNumber(analysisNumber);
                getDao().insert(pfAnalyticalEquiptDetailBean);      
            }     		
    	
        return equiptDetailsBean.getId();

    }

}
    
    
