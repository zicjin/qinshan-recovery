package com.sunwayworld.emc.module.bp.voc.service.impl;



import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.emc.module.bp.voc.bean.PfVocWeightingBean;
import com.sunwayworld.emc.module.bp.voc.dao.PfVocWeightingDao;
import com.sunwayworld.emc.module.bp.voc.service.PfVocWeightingService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
/**
 * @author licm@sunwayworld.com 2021年09月24日
 */
@Repository
@GikamBean
public class PfVocWeightingServiceImpl implements PfVocWeightingService {

    @Autowired
    private PfVocWeightingDao pfVocWeightingDao;

    @Autowired
    private CoreOrgService orgService;
    
    @Autowired
    @Lazy
    private PfOrderService orderService;
    
    @Autowired
    @Lazy
    private PfOrderTaskService orderTaskService;
    
    @Autowired
    @Lazy
    private PfProjectService projectService; 
    
    @Autowired
    @Lazy
    private PfOrderTaskResultService orderTaskResultService;
    
    
    @Override
    @SuppressWarnings("unchecked")
    public PfVocWeightingDao getDao() {
        return pfVocWeightingDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
    	PfVocWeightingBean pfVocWeighting = jsonWrapper.parseUnique(PfVocWeightingBean.class);
    	String projNo= pfVocWeighting.getProjNo();
    	String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");    	
        List<CoreOrgBean> sampleOrgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
        CoreOrgBean coreOrg= sampleOrgList.get(0);
        String orgShortName = coreOrg.getExt$Item("orgshortname");
        String vocNumber = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
        pfVocWeighting.setProjNo(projNo);
        pfVocWeighting.setVocNumber(vocNumber);
    	pfVocWeighting.setId(ApplicationContextHelper.getNextIdentity());
    	pfVocWeighting.setOrgId(coreOrg.getId());
        getDao().insert(pfVocWeighting);
        return pfVocWeighting.getId();
    }
    
    @Override
    @Log(value = "根据VOC瓶数量插入VOC瓶", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String addVoc(RestJsonWrapperBean jsonWrapper) {
    	//插入数据次数
    	String addnum= jsonWrapper.getFilterValue("addnum");
    	if (addnum != null && addnum.length() != 0){
        	int addnum1 = Integer.parseInt(addnum);
        	PfVocWeightingBean pfVocWeighting = new PfVocWeightingBean();
        	String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");    	
            List<CoreOrgBean> sampleOrgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
	        CoreOrgBean coreOrg= sampleOrgList.get(0);
            String orgShortName = coreOrg.getExt$Item("orgshortname");
            for (int i =1;i<=addnum1;i++) {
                String vocNumber = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
            	pfVocWeighting.setId(ApplicationContextHelper.getNextIdentity());
            	pfVocWeighting.setVocNumber(vocNumber);
            	pfVocWeighting.setOrgId(coreOrg.getId());
                getDao().insert(pfVocWeighting);      
            }     		
    	}
        return addnum;

    }
   
/*    @Override
    @Transactional
    public Page<PfVocWeightingBean> vocPage(RestJsonWrapperBean wrapper) {
    	String projNo= wrapper.getFilterValue("projNo");
        List<PfVocWeightingBean> vocList = getDao().selectListByOneColumnValue(projNo, "projNo");
        return new Pagination<>(vocList);
    }*/
    


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertDeliverBottle(RestJsonWrapperBean wrapper) {
    	//前台传样品编号
    	List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
    	//测试对应的项目、样品表数据
    	List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean ::getId).distinct().collect(Collectors.toList());
    	List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean ::getOrderId).distinct().collect(Collectors.toList());
    	//所选的样品
    	List<EmcOrderBean> orderAllList = orderService.getDao().selectListByIds(orderIdList);
    	//防止质控样没有projid找不到项目所做操作
    	orderAllList.forEach(o ->{
    		if(o.getOrderNo().contains("-")){
    			//List<EmcOrderBean> uniqueOrderList = orderAllList.stream().filter(order ->StringUtils.equals(orderNoNa, order.getOrderNo())).collect(Collectors.toList());
        		o.setOrderNo(o.getOrderNo().substring(0, o.getOrderNo().indexOf("-")));
        		//o.setProjId(uniqueOrderList.get(0).getProjId());
    		}
    	});
    	//获取有proid的order
    	List<String> orderNoList = orderAllList.stream().map(EmcOrderBean :: getOrderNo).distinct().collect(Collectors.toList());
    	List<EmcOrderBean> haveProjIdOrderList = orderService.getDao().selectListByOneColumnValues(orderNoList,"orderNo");
    	if(haveProjIdOrderList.isEmpty()){
    		return;
    	}
    	orderAllList.forEach(o ->{
    			List<EmcOrderBean> uniqueOrderList = haveProjIdOrderList.stream().filter(order ->StringUtils.equals(o.getOrderNo(), order.getOrderNo())).collect(Collectors.toList());
        		if(!uniqueOrderList.isEmpty()){
        			//带批号的样品没有对应的projid
        			o.setProjId(uniqueOrderList.get(0).getProjId());	
        		}
    	});
    	List<Long> projIdList = orderAllList.stream().map(EmcOrderBean ::getProjId).distinct().collect(Collectors.toList());
    	List<EmcProjectBean> projectAllList = projectService.getDao().selectListByIds(projIdList);
    	
    	List<EmcOrderTaskBean> orderTaskAllList = orderTaskService.getDao().selectListByIds(orderTaskIdList);
    	List<EmcOrderTaskResultBean> orderTaskResultAllList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "orderTaskId");
    	//获取编号前缀
    	String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");    	
        List<CoreOrgBean> sampleOrgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
        CoreOrgBean coreOrg= sampleOrgList.get(0);
        String orgShortName = coreOrg.getExt$Item("orgshortname")+"S";
    	orderTaskList.forEach(e ->{
    		String orderNo = e.getExt$Item("orderNo");
    		if(orderNo.contains("-")){
        		orderNo = orderNo.substring(0, orderNo.indexOf("-"));
        	}
        	List<EmcOrderBean> orderList = orderAllList.stream().filter(order ->ObjectUtils.equals(e.getOrderId(), order.getId())).collect(Collectors.toList());
        	
        	//voc表新增一个瓶子，ordertask表更新数据
        	Long projId= orderList.get(0).getProjId();
        	List<EmcProjectBean> projList = projectAllList.stream().filter(p ->ObjectUtils.equals(projId, p.getId())).collect(Collectors.toList());
        	//采样的不需要新增送样的瓶子
        	List<EmcOrderTaskBean> orderTaskForUpdateList = orderTaskAllList.stream().filter(task ->ObjectUtils.equals(e.getId(),task.getId())).collect(Collectors.toList());
        	EmcOrderTaskBean orderTask = orderTaskForUpdateList.get(0);
        	List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultAllList.stream().filter(result ->ObjectUtils.equals(e.getId(),result.getOrderTaskId())).collect(Collectors.toList());
        	List<String> itemNameList = orderTaskResultList.stream().map(EmcOrderTaskResultBean :: getItemName).distinct().collect(Collectors.toList());
        	String itemNameString = String.join(",", itemNameList);
    		if(itemNameString.contains("VOC")&&!projList.isEmpty()){
            	EmcProjectBean proj = projList.get(0);
    			if(!proj.getSample().equals("1")){
            		if(orderTask.getExt$Item("VOCBOTTLE")== null || orderTask.getExt$Item("VOCBOTTLE").equals("")){
            			PfVocWeightingBean pfVocWeighting = new PfVocWeightingBean();
                        String vocNumber = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
                        pfVocWeighting.setProjNo(proj.getProjNo());
                        pfVocWeighting.setVocNumber(vocNumber);
                    	pfVocWeighting.setId(ApplicationContextHelper.getNextIdentity());
                        getDao().insert(pfVocWeighting);
                    	orderTask.setExt$Item("VOCBOTTLE", vocNumber);
                    	orderTaskService.getDao().update(orderTask, "VOCBOTTLE");
            		}    				
    			}

    		}
    	}
    	);
        return;
    }
    
    
}