package com.sunwayworld.emc.module.bp.order.orderretain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.dao.EmcOrderRetainDao;
import com.sunwayworld.emc.module.bp.order.orderretain.service.PfOrderRetainService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;

/**
 * @author licm@sunwayworld.com 2021/10/15
 */
@Repository
@GikamBean
public class PfOrderRetainServiceImpl extends EmcOrderRetainServiceImpl implements PfOrderRetainService  {
    

    @Autowired
    private EmcOrderRetainDao orderRetainDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcOrderRetainDao getDao() {
        return orderRetainDao;
    }
    

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void confirmRetainOrder(RestJsonWrapperBean wrapperBean) {
    	List<EmcOrderRetainBean> emcOrderRetainBeanList = wrapperBean.parse(EmcOrderRetainBean.class);
/*        1、判断项目有没有留样过，有的话直接使用原序列号；
        2、针对新加的留样项目，只能取当前最大的序列号，然后递增1，递增的时候，最大值不超过999，超过后字母进一位*/
        emcOrderRetainBeanList.forEach(emcOrderRetainBean -> {
            Long projId =emcOrderRetainBean.getProjId();
            List<EmcOrderRetainBean> emcOrderRetainOrgList = selectAll();
            List<Long> projIdList = emcOrderRetainOrgList.stream().map(a -> a.getProjId()).distinct().collect(Collectors.toList());
            List<EmcOrderRetainBean> emcOrderRetainProjIDList = selectListByFilter(SearchFilter.instance().match("projId", projId).filter(MatchPattern.OR)
            		.match("isretain", "true").filter(MatchPattern.OR));
            if(emcOrderRetainProjIDList.size() !=0 ){
            	String  retainIndex = emcOrderRetainProjIDList.get(0).getExt$Item("retainindex");
            	if(retainIndex!=null && retainIndex!=""){
            		emcOrderRetainBean.setExt$Item("retainindex",retainIndex);
            	}
            }else{
        		String demo = "ABCDEFGHIJKLMNOPQRSTYVWSYZ";
        		String demo1 = "A";
        		int count =0;
        		int num = projIdList.size();
        		if(num>999){
        			num=num-999;
        			while(num!=999&&num/999>0){
        				num=num-999;
        				count++;
        			}
        			 demo1=demo.substring(count,count+1);
        		}
                String retainIndex = ApplicationContextHelper.getNextSequence(getDao().getTable(), demo1);
                emcOrderRetainBean.setExt$Item("retainindex",retainIndex);
            }
            emcOrderRetainBean.setExt$Item("isretain","true");
            emcOrderRetainBean.setOrderId(emcOrderRetainBean.getId());
            emcOrderRetainBean.setId(ApplicationContextHelper.getNextIdentity());
            emcOrderRetainBean.setRetainTimeLimit(DateTimeUtils.plusDays(LocalDateTime.now(), emcOrderRetainBean.getRetainDays()));    
            getDao().insert(emcOrderRetainBean);
        });
    }
    
}