package com.sunwayworld.emc.module.bp.equiptcrves.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptCrvesBean;
import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptDetailBean;
import com.sunwayworld.emc.module.bp.equiptcrves.dao.PfAnalyticalEquiptCrvesDao;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptCrvesService;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptDetailService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.PfOrderItemGroupService;
import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author licm@sunwayworld.com 2021年10月24日
 */
@Repository
@GikamBean
public class PfAnalyticalEquiptCrvesServiceImpl implements PfAnalyticalEquiptCrvesService {

    @Autowired
    private PfAnalyticalEquiptCrvesDao pfAnalyticalEquiptCrvesDao;

    @Autowired
    @Lazy
    private PfAnalyticalEquiptDetailService pfAnalyticalEquiptDetailService;

    @Autowired
    @Lazy
    private PfOrderItemGroupService pfOrderItemGroupService;

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Override
    @SuppressWarnings("unchecked")
    public PfAnalyticalEquiptCrvesDao getDao() {
        return pfAnalyticalEquiptCrvesDao;
    }

    @Autowired
    @Lazy
    private PfOrderTaskService orderTaskService;

    @Autowired
    @Lazy
    private PfRunTempHumidityService pfRunTempHumidityService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;
    
    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
    	PfAnalyticalEquiptCrvesBean equiptCrvesBean = jsonWrapper.parseUnique(PfAnalyticalEquiptCrvesBean.class);
        String solId = ApplicationContextHelper.getNextSequence(getDao().getTable());
    	equiptCrvesBean.setId(ApplicationContextHelper.getNextIdentity());
    	equiptCrvesBean.setSolId(solId);
    	equiptCrvesBean.setAddDate(LocalDateTime.now());
        getDao().insert(equiptCrvesBean);
        return equiptCrvesBean.getId();
    }

    //历史曲线点击确定
    @Override
    public void copyCrves(RestJsonWrapperBean wrapper) {
    	List<PfAnalyticalEquiptCrvesBean> copyList = wrapper.parse(PfAnalyticalEquiptCrvesBean.class);
    	String runId = wrapper.getFilterValue("runId");
    	copyList.forEach(PfAnalyticalEquiptCrvesBean -> {
    		//复制曲线单挑的溶液编号
	        String solId = ApplicationContextHelper.getNextSequence(getDao().getTable());
        	PfAnalyticalEquiptCrvesBean newCrves = new PfAnalyticalEquiptCrvesBean();
        	//被复制曲线明细数据的runId和solId，必不为空
/*    		String oldRunId = PfAnalyticalEquiptCrvesBean.getRunId();
*/    		String oldSolId = PfAnalyticalEquiptCrvesBean.getSolId();
        	PfAnalyticalEquiptDetailBean newDetailCrves = new PfAnalyticalEquiptDetailBean();
        	//此步复制曲线，新增曲线表数据修改RunId和solId
        	newCrves.setId(ApplicationContextHelper.getNextIdentity());
        	newCrves.setRunId(runId);
        	newCrves.setSdReserveConcentration(PfAnalyticalEquiptCrvesBean.getSdReserveConcentration());
        	newCrves.setSdUseFluidConcentration(PfAnalyticalEquiptCrvesBean.getSdUseFluidConcentration());
        	newCrves.setSolId(solId);
        	newCrves.setCurveFactorFixedTo(PfAnalyticalEquiptCrvesBean.getCurveFactorFixedTo());
        	newCrves.setAdder(PfAnalyticalEquiptCrvesBean.getAdder());
        	newCrves.setAddDate(LocalDateTime.now());
        	newCrves.setExpirationTime(PfAnalyticalEquiptCrvesBean.getExpirationTime());
        	//newCrves.setExt$Item("testname",PfAnalyticalEquiptCrvesBean.getExt$Item("testname"));
        	//newCrves.setExt$Item("testmethodid",PfAnalyticalEquiptCrvesBean.getExt$Item("testmethodid"));
        	newCrves.setTestName(PfAnalyticalEquiptCrvesBean.getTestName());
        	newCrves.setTestMethodId(PfAnalyticalEquiptCrvesBean.getTestMethodId());
        	newCrves.setExt$Item("sdsolutionvolumeunit",PfAnalyticalEquiptCrvesBean.getExt$Item("sdsolutionvolumeunit"));
        	newCrves.setExt$Item("sdconcentrationunit",PfAnalyticalEquiptCrvesBean.getExt$Item("sdconcentrationunit"));
        	newCrves.setExt$Item("internalnumber",PfAnalyticalEquiptCrvesBean.getExt$Item("internalnumber"));
        	newCrves.setSetDate(PfAnalyticalEquiptCrvesBean.getSetDate());
        	newCrves.setExt$Item("aslope",PfAnalyticalEquiptCrvesBean.getExt$Item("aslope"));
        	newCrves.setExt$Item("bintercept",PfAnalyticalEquiptCrvesBean.getExt$Item("bintercept"));
        	newCrves.setExt$Item("rrelatedfactor",PfAnalyticalEquiptCrvesBean.getExt$Item("rrelatedfactor"));
        	//newCrves.setExt$Item("methodname",PfAnalyticalEquiptCrvesBean.getExt$Item("methodname"));
        	//newCrves.setExt$Item("methodid",PfAnalyticalEquiptCrvesBean.getExt$Item("methodid"));
        	newCrves.setMethodName(PfAnalyticalEquiptCrvesBean.getMethodName());
        	newCrves.setMethodId(PfAnalyticalEquiptCrvesBean.getMethodId());
        	newCrves.setItemGroup(PfAnalyticalEquiptCrvesBean.getItemGroup());
        	newCrves.setItemGroupId(PfAnalyticalEquiptCrvesBean.getItemGroupId());
        	newCrves.setStdSubstanceContentUnit(PfAnalyticalEquiptCrvesBean.getStdSubstanceContentUnit());
        	newCrves.setStdSubstanceContentVolUnit(PfAnalyticalEquiptCrvesBean.getStdSubstanceContentVolUnit());
        	newCrves.setCreatedById(LocalContextHelper.getLoginUserId());
        	newCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
        	newCrves.setCreatedTime(LocalDateTime.now());
        	newCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        	newCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        	getDao().insert(newCrves);
        	//被复制曲线的明细detailsList
            List<PfAnalyticalEquiptDetailBean> detailsList = pfAnalyticalEquiptDetailService.selectListByFilter(SearchFilter.instance()
            		.match("solId", oldSolId).filter(MatchPattern.OR));
            //此步复制曲线明细，新增曲线明细表数据修改RunId和solId
            if(!detailsList.isEmpty()){
                detailsList.forEach(PfAnalyticalEquiptDetailBean -> {
    	        	newDetailCrves.setId(ApplicationContextHelper.getNextIdentity());
    	        	newDetailCrves.setRunId(runId);//固定选中批批号
    	        	newDetailCrves.setSolId(solId);//被复制曲线编号，同一曲线下的不同明细批的曲线编号相同
    	        	newDetailCrves.setSdSubstances(PfAnalyticalEquiptDetailBean.getSdSubstances());
    	        	newDetailCrves.setAnalysisNumber(PfAnalyticalEquiptDetailBean.getAnalysisNumber());
    	        	newDetailCrves.setSdConcentration(PfAnalyticalEquiptDetailBean.getSdConcentration());
    	        	newDetailCrves.setSdSolutionVolume(PfAnalyticalEquiptDetailBean.getSdSolutionVolume());
    	        	newDetailCrves.setQcType(PfAnalyticalEquiptDetailBean.getQcType());
    	        	newDetailCrves.setStdSubstanceContent(PfAnalyticalEquiptDetailBean.getStdSubstanceContent());
    	        	newDetailCrves.setStdSubstanceContentVol(PfAnalyticalEquiptDetailBean.getStdSubstanceContentVol());
    	        	newDetailCrves.setRemark(PfAnalyticalEquiptDetailBean.getRemark());
    	        	newDetailCrves.setExt$Item("peakareaassayvalue",PfAnalyticalEquiptDetailBean.getExt$Item("peakareaassayvalue"));
    	        	newDetailCrves.setCreatedById(LocalContextHelper.getLoginUserId());
    	        	newDetailCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
    	        	newDetailCrves.setCreatedTime(LocalDateTime.now());
    	        	newDetailCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
    	        	newDetailCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
    	        	pfAnalyticalEquiptDetailService.getDao().insert(newDetailCrves);
            	});
            }
        });
    }

   //根据前台输入的曲线条数复制同一条曲线
    @Override
    public void copyNowCrves(RestJsonWrapperBean wrapper) {
    	PfAnalyticalEquiptCrvesBean copyCrvesBean = wrapper.parseUnique(PfAnalyticalEquiptCrvesBean.class);
    	String runId = wrapper.getFilterValue("runId");
    	String crvesNums = wrapper.getFilterValue("crvesNums");
    	Integer addNums = null;

    	if(crvesNums!=null){
    		addNums = Integer.valueOf(crvesNums);
    		//按照次数复制新的曲线
    		for (int i =1;i<=addNums;i++) {
    	        String solId = ApplicationContextHelper.getNextSequence(getDao().getTable());
            	PfAnalyticalEquiptCrvesBean newCrves = new PfAnalyticalEquiptCrvesBean();
            	newCrves.setId(ApplicationContextHelper.getNextIdentity());
            	newCrves.setRunId(runId);
            	newCrves.setSdReserveConcentration(copyCrvesBean.getSdReserveConcentration());
            	newCrves.setSdUseFluidConcentration(copyCrvesBean.getSdUseFluidConcentration());
            	newCrves.setSolId(solId);
            	newCrves.setCurveFactorFixedTo(copyCrvesBean.getCurveFactorFixedTo());
            	newCrves.setAdder(copyCrvesBean.getAdder());
            	newCrves.setAddDate(LocalDateTime.now());
            	newCrves.setExpirationTime(copyCrvesBean.getExpirationTime());
            	//newCrves.setExt$Item("testname",copyCrvesBean.getExt$Item("testname"));
            	newCrves.setTestName(copyCrvesBean.getTestName());
            	newCrves.setTestMethodId(copyCrvesBean.getTestMethodId());
            	//newCrves.setExt$Item("testmethodid",copyCrvesBean.getExt$Item("testmethodid"));
            	newCrves.setExt$Item("sdsolutionvolumeunit",copyCrvesBean.getExt$Item("sdsolutionvolumeunit"));
            	newCrves.setExt$Item("sdconcentrationunit",copyCrvesBean.getExt$Item("sdconcentrationunit"));
            	newCrves.setExt$Item("internalnumber",copyCrvesBean.getExt$Item("internalnumber"));
            	newCrves.setExt$Item("aslope",copyCrvesBean.getExt$Item("aslope"));
            	newCrves.setExt$Item("bintercept",copyCrvesBean.getExt$Item("bintercept"));
            	newCrves.setExt$Item("rrelatedfactor",copyCrvesBean.getExt$Item("rrelatedfactor"));
            	newCrves.setSetDate(copyCrvesBean.getSetDate());
/*            	newCrves.setExt$Item("methodname",copyCrvesBean.getExt$Item("methodname"));
            	newCrves.setExt$Item("methodid",copyCrvesBean.getExt$Item("methodid"));*/
            	newCrves.setMethodId(copyCrvesBean.getMethodId());
            	newCrves.setMethodName(copyCrvesBean.getMethodName());
            	newCrves.setCreatedById(LocalContextHelper.getLoginUserId());
            	newCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
            	newCrves.setCreatedTime(LocalDateTime.now());
            	//newCrves.setItemGroup(copyCrvesBean.getItemGroup());
            	//newCrves.setItemGroupId(copyCrvesBean.getItemGroupId());
            	newCrves.setStdSubstanceContentUnit(copyCrvesBean.getStdSubstanceContentUnit());
            	newCrves.setStdSubstanceContentVolUnit(copyCrvesBean.getStdSubstanceContentVolUnit());
            	newCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
            	newCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
            	getDao().insert(newCrves);
            	//被复制曲线明细数据的runId和solId，必不为空
        		String oldRunId = copyCrvesBean.getRunId();
        		String oldSolId = copyCrvesBean.getSolId();
            	PfAnalyticalEquiptDetailBean newDetailCrves = new PfAnalyticalEquiptDetailBean();
            	//被复制曲线的明细list
                List<PfAnalyticalEquiptDetailBean> detailsList = pfAnalyticalEquiptDetailService.selectListByFilter(SearchFilter.instance()
                		.match("RUNID", oldRunId).filter(MatchPattern.OR).match("solId", oldSolId).filter(MatchPattern.OR));
                //有几条这几条的runId改成当前曲线runId,solId改为
                detailsList.forEach(PfAnalyticalEquiptDetailBean -> {
    	        	newDetailCrves.setId(ApplicationContextHelper.getNextIdentity());
    	        	newDetailCrves.setRunId(runId);
    	        	newDetailCrves.setSolId(solId);
    	        	newDetailCrves.setSdSubstances(PfAnalyticalEquiptDetailBean.getSdSubstances());
    	        	newDetailCrves.setAnalysisNumber(PfAnalyticalEquiptDetailBean.getAnalysisNumber());
    	        	newDetailCrves.setSdConcentration(PfAnalyticalEquiptDetailBean.getSdConcentration());
    	        	newDetailCrves.setSdSolutionVolume(PfAnalyticalEquiptDetailBean.getSdSolutionVolume());
    	        	newDetailCrves.setQcType(PfAnalyticalEquiptDetailBean.getQcType());
    	        	newDetailCrves.setStdSubstanceContent(PfAnalyticalEquiptDetailBean.getStdSubstanceContent());
    	        	newDetailCrves.setStdSubstanceContentVol(PfAnalyticalEquiptDetailBean.getStdSubstanceContentVol());
    	        	newDetailCrves.setRemark(PfAnalyticalEquiptDetailBean.getRemark());
    	        	newDetailCrves.setExt$Item("peakareaassayvalue",PfAnalyticalEquiptDetailBean.getExt$Item("peakareaassayvalue"));
    	        	newDetailCrves.setCreatedById(LocalContextHelper.getLoginUserId());
    	        	newDetailCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
    	        	newDetailCrves.setCreatedTime(LocalDateTime.now());
    	        	newDetailCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
    	        	newDetailCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
    	        	pfAnalyticalEquiptDetailService.getDao().insert(newDetailCrves);
            	});
            }
    	}

    }

    //根据前台输入的曲线条数复制同一条曲线(曲线配置无RUNID)
    @Override
    public void copyConfigCrves(RestJsonWrapperBean wrapper) {
    	PfAnalyticalEquiptCrvesBean copyCrvesBean = wrapper.parseUnique(PfAnalyticalEquiptCrvesBean.class);
    	String crvesNums = wrapper.getFilterValue("crvesNums");
    	Integer addNums = null;

    	if(crvesNums!=null){
    		addNums = Integer.valueOf(crvesNums);
    		//按照次数复制新的曲线
    		for (int i =1;i<=addNums;i++) {
    	        String solId = ApplicationContextHelper.getNextSequence(getDao().getTable());
            	PfAnalyticalEquiptCrvesBean newCrves = new PfAnalyticalEquiptCrvesBean();
            	newCrves.setId(ApplicationContextHelper.getNextIdentity());
            	newCrves.setSdReserveConcentration(copyCrvesBean.getSdReserveConcentration());
            	newCrves.setSdUseFluidConcentration(copyCrvesBean.getSdUseFluidConcentration());
            	newCrves.setSolId(solId);
            	newCrves.setCurveFactorFixedTo(copyCrvesBean.getCurveFactorFixedTo());
            	newCrves.setAdder(copyCrvesBean.getAdder());
            	newCrves.setAddDate(LocalDateTime.now());
            	newCrves.setExpirationTime(copyCrvesBean.getExpirationTime());
            	//newCrves.setExt$Item("testname",copyCrvesBean.getExt$Item("testname"));
            	newCrves.setTestName(copyCrvesBean.getTestName());
            	//newCrves.setExt$Item("testmethodid",copyCrvesBean.getExt$Item("testmethodid"));
            	newCrves.setTestMethodId(copyCrvesBean.getTestMethodId());
/*            	newCrves.setExt$Item("methodname",copyCrvesBean.getExt$Item("methodname"));
            	newCrves.setExt$Item("methodid",copyCrvesBean.getExt$Item("methodid"));*/
            	newCrves.setMethodName(copyCrvesBean.getMethodName());
            	newCrves.setMethodId(copyCrvesBean.getMethodId());

            	newCrves.setExt$Item("sdsolutionvolumeunit",copyCrvesBean.getExt$Item("sdsolutionvolumeunit"));
            	newCrves.setExt$Item("sdconcentrationunit",copyCrvesBean.getExt$Item("sdconcentrationunit"));
            	newCrves.setExt$Item("internalnumber",copyCrvesBean.getExt$Item("internalnumber"));
            	newCrves.setSetDate(copyCrvesBean.getSetDate());
            	newCrves.setExt$Item("aslope",copyCrvesBean.getExt$Item("aslope"));
            	newCrves.setExt$Item("bintercept",copyCrvesBean.getExt$Item("bintercept"));
            	newCrves.setExt$Item("rrelatedfactor",copyCrvesBean.getExt$Item("rrelatedfactor"));
            	//newCrves.setItemGroup(copyCrvesBean.getItemGroup());
            	//newCrves.setItemGroupId(copyCrvesBean.getItemGroupId());
            	newCrves.setStdSubstanceContentUnit(copyCrvesBean.getStdSubstanceContentUnit());
            	newCrves.setStdSubstanceContentVolUnit(copyCrvesBean.getStdSubstanceContentVolUnit());
            	newCrves.setCreatedById(LocalContextHelper.getLoginUserId());
            	newCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
            	newCrves.setCreatedTime(LocalDateTime.now());
            	newCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
            	newCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
            	getDao().insert(newCrves);
        		String oldSolId = copyCrvesBean.getSolId();
            	PfAnalyticalEquiptDetailBean newDetailCrves = new PfAnalyticalEquiptDetailBean();
            	//被复制曲线的明细list
                List<PfAnalyticalEquiptDetailBean> detailsList = pfAnalyticalEquiptDetailService.selectListByFilter(SearchFilter.instance()
                		.match("solId", oldSolId).filter(MatchPattern.OR));
                detailsList.forEach(PfAnalyticalEquiptDetailBean -> {
    	        	newDetailCrves.setId(ApplicationContextHelper.getNextIdentity());
    	        	newDetailCrves.setSolId(solId);
    	        	newDetailCrves.setSdSubstances(PfAnalyticalEquiptDetailBean.getSdSubstances());
    	        	newDetailCrves.setAnalysisNumber(PfAnalyticalEquiptDetailBean.getAnalysisNumber());
    	        	newDetailCrves.setSdConcentration(PfAnalyticalEquiptDetailBean.getSdConcentration());
    	        	newDetailCrves.setSdSolutionVolume(PfAnalyticalEquiptDetailBean.getSdSolutionVolume());
    	        	newDetailCrves.setQcType(PfAnalyticalEquiptDetailBean.getQcType());
    	        	newDetailCrves.setStdSubstanceContent(PfAnalyticalEquiptDetailBean.getStdSubstanceContent());
    	        	newDetailCrves.setStdSubstanceContentVol(PfAnalyticalEquiptDetailBean.getStdSubstanceContentVol());
    	        	newDetailCrves.setRemark(PfAnalyticalEquiptDetailBean.getRemark());
    	        	newDetailCrves.setExt$Item("peakareaassayvalue",PfAnalyticalEquiptDetailBean.getExt$Item("peakareaassayvalue"));
    	        	newDetailCrves.setCreatedById(LocalContextHelper.getLoginUserId());
    	        	newDetailCrves.setCreatedByName(LocalContextHelper.getLoginUserName());
    	        	newDetailCrves.setCreatedTime(LocalDateTime.now());
    	        	newDetailCrves.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
    	        	newDetailCrves.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
    	        	newDetailCrves.setQcType(PfAnalyticalEquiptDetailBean.getQcType());
    	        	pfAnalyticalEquiptDetailService.getDao().insert(newDetailCrves);
            	});
            }
    	}

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertConfigCurve(RestJsonWrapperBean jsonWrapper) {
    	PfAnalyticalEquiptCrvesBean equiptCrvesBean = new PfAnalyticalEquiptCrvesBean();
        String solId = ApplicationContextHelper.getNextSequence(getDao().getTable());
    	equiptCrvesBean.setId(ApplicationContextHelper.getNextIdentity());
    	equiptCrvesBean.setSolId(solId);
    	equiptCrvesBean.setAddDate(LocalDateTime.now());
        getDao().insert(equiptCrvesBean);
    }

    @Override
    public String selectRunTestId(RestJsonWrapperBean jsonWrapper) {
    	String runId = jsonWrapper.getFilterValue("runId");
    	//根据runid找ordertask表的testId
    	String testIds = "";
    	List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId,"RUNID");
        List<Long> testIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        testIds = StringUtils.join(testIdList.toArray(), ",");
    	return testIds;

    }

    @Override
    public String selectItemGroup(RestJsonWrapperBean jsonWrapper) {
    	String runId = jsonWrapper.getFilterValue("runId");
    	//根据runid找ordertask表的itemGroup
    	String itemGroupIds = "";
		List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "testMethodId","methodId","orderId","testId"));
    	List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
		List<Long> testIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
		List<EmcOrderItemGroupBean> orderItemGroupList= pfOrderItemGroupService.selectListByFilter(SearchFilter.instance().match("testId", testIdList).filter(MatchPattern.OR).match("orderId", orderIdList).filter(MatchPattern.OR));
		List<Long> filterItemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
		List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(filterItemGroupIdList);
		List<Long> trueItemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

		itemGroupIds = StringUtils.join(trueItemGroupIdList.toArray(), ",");
    	return itemGroupIds;

    }


    //比较分析结束时间 ,分析开始时间结束时间目前绑定温湿度,如果小于最大日期不让他添加到现有曲线；相当于复制历史曲线数据并赋值RUNID
    @Override
    public String CompareEndDate(RestJsonWrapperBean wrapper) {
    	String runId = wrapper.getFilterValue("runId");
    	List<PfAnalyticalEquiptCrvesBean> curveList= wrapper.parse(PfAnalyticalEquiptCrvesBean.class);
    	//对比批下的最大日期
    	//根据runid找到温湿度表
        List<PfRunTempHumidityBean> tempHumList = pfRunTempHumidityService.selectListByFilter(SearchFilter.instance().match("runId", runId).filter(MatchPattern.OR));
        //找到这个list里面最新日期
        List<PfRunTempHumidityBean> notEmptyDateOrgList = tempHumList.stream().filter(t -> !ObjectUtils.isEmpty(t.getExt$Item("endDate"))).collect(Collectors.toList());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

      List<Date> notEmptyDateList = new ArrayList<Date>();

        notEmptyDateOrgList.forEach(e -> {
        	String endOrgDate = e.getExt$Item("endDate");
            Date endDate = new Date();
			try {
				endDate = df.parse(endOrgDate);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	notEmptyDateList.add(endDate);
        });
        if(notEmptyDateList.isEmpty()){
      	  return null;
        }
         //温湿度下的最新时间
        Date maxDate = notEmptyDateList.stream().max(Comparator.comparing(Function.identity())).get();

        List<PfAnalyticalEquiptCrvesBean> notEmptyDateCurveOrgList = curveList.stream().filter(t -> !ObjectUtils.isEmpty(t.getExpirationTime())).collect(Collectors.toList());

      List<Date> notEmptyDateCurveList = new ArrayList<Date>();

      notEmptyDateCurveOrgList.forEach(e -> {
    	  	Date date1 = new Date();
        	date1 = Date.from(e.getExpirationTime().atZone(ZoneId.systemDefault()).toInstant());
			notEmptyDateCurveList.add(date1);
        });
      if(notEmptyDateCurveList.isEmpty()){
    	  return null;
      }
         //温湿度下的最新时间
        Date maxDate1 = notEmptyDateCurveList.stream().max(Comparator.comparing(Function.identity())).get();
    	String isEarly = null;
    	if(maxDate1.before(maxDate)){
    		isEarly = "false";
    	}
    	return isEarly;

    }

	@Override
	@Transactional
	public String setDefaultCurve(RestJsonWrapperBean wrapper) {
		String runId = wrapper.getParamValue("runId");
		String note ="";
		if (StringUtils.isEmpty(runId)) {
			return note;
		}
		List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "testMethodId","methodId","orderId","testId"));
		List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
		Long methodId = orderTaskList.stream().map(EmcOrderTaskBean::getMethodId).distinct().collect(Collectors.toList()).get(0);
		List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
		List<Long> testIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestId).distinct().collect(Collectors.toList());
		//根据orderTask表的这些条件找到该批下的分析项组
		List<EmcOrderItemGroupBean> orderItemGroupList= pfOrderItemGroupService.selectListByFilter(SearchFilter.instance().match("testId", testIdList).filter(MatchPattern.OR).match("orderId", orderIdList).filter(MatchPattern.OR));
		//假分析项组ID
		List<Long> itemGroupIdList = orderItemGroupList.stream().map(EmcOrderItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
		List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(itemGroupIdList);
		//真分析项组ID
		List<Long> trueItemGroupList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());
		//通过真分析项组ID找的的真分析项组
		List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(trueItemGroupList,"ITEMGROUPID");

		List<PfAnalyticalEquiptCrvesBean> curveList = selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("ITEMGROUPID", trueItemGroupList).filter(MatchPattern.OR).match("createdByOrgId", LocalContextHelper.getLoginOrgId()).filter(MatchPattern.EQ)
				.match("runId", null).filter(MatchPattern.EQ).match("methodId", methodId).filter(MatchPattern.OR).match("isAbandon", "1").filter(MatchPattern.DIFFER));
		if(curveList.isEmpty()){
			return note;
		};
		List<PfRunTempHumidityBean> tempHumidityList = pfRunTempHumidityService.getDao().selectListByOneColumnValue(runId, "runId", Arrays.asList("id", "endDate"));

		LocalDate maxRunEndDate = tempHumidityList.stream().filter(tempHumidity -> !ObjectUtils.isEmpty(tempHumidity.getEndDate())).map(PfRunTempHumidityBean::getEndDate).max(Comparator.comparing(Function.identity())).orElse(LocalDate.now());
		LocalDate maxExpireDate = curveList.stream().filter(curve -> !ObjectUtils.isEmpty(curve.getExpirationTime())).map(curve -> curve.getExpirationTime().toLocalDate()).max(Comparator.comparing(Function.identity())).orElse(LocalDate.now());

		if (maxExpireDate.isBefore(maxRunEndDate)) {
			orderTaskList.clear();
			orderItemGroupList.clear();
			tempHumidityList.clear();
			return note;
		}

		// 查询曲线校核分析项结果
		EmcOrderTaskResultBean filterResult = new EmcOrderTaskResultBean();
		filterResult.setRunId(runId);
		filterResult.setQcCategoryCode(PfConstant.QC_QXJH);
		//查询样品加标分析项结果
		EmcOrderTaskResultBean filterSpkcResult = new EmcOrderTaskResultBean();
		filterSpkcResult.setRunId(runId);
		filterSpkcResult.setQcCategoryCode(PfConstant.QCCATEGORY_QCCATGORYID_L_SPKC);
		//查询空白加标分析项结果
		EmcOrderTaskResultBean filterSpkckResult = new EmcOrderTaskResultBean();
		filterSpkckResult.setRunId(runId);
		filterSpkckResult.setQcCategoryCode(PfConstant.QCCATEGORY_QCCATEGORYID_L_SPKCK);

		//曲线校核结果
		List<EmcOrderTaskResultBean> taskResultList = orderTaskResultService.getDao().selectList(filterResult, Arrays.asList("runId", "qcCategoryCode"), Arrays.asList("id", "runId", "qcCategoryId", "itemId", "testMethodId", "orderNo","qcCategoryCode","itemId"));
		List<EmcOrderTaskResultBean> targetResultList = new ArrayList<EmcOrderTaskResultBean>();
		if(!taskResultList.isEmpty()){
			List<Long> resultItemIdList = taskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
			Long qcCategoryId = taskResultList.get(0).getQcCategoryId();
			List<EmcItemQcIndexBean> itemQcIndexList = itemQcIndexService.selectListByFilter(SearchFilter.instance().match("itemId", resultItemIdList).filter(MatchPattern.OR).match("qcCategoryId", qcCategoryId).filter(MatchPattern.OR));
			List<Long> targetItemIdList = itemQcIndexList.stream().map(EmcItemQcIndexBean::getItemId).distinct().collect(Collectors.toList());
			targetResultList.addAll(taskResultList.stream().filter(result -> targetItemIdList.contains(result.getItemId())).collect(Collectors.toList()));
		}

		//样品加标结果
		List<EmcOrderTaskResultBean> taskSpkcResultList = orderTaskResultService.getDao().selectList(filterSpkcResult, Arrays.asList("runId", "qcCategoryCode"), Arrays.asList("id", "runId", "qcCategoryId", "itemId", "testMethodId", "orderNo","qcCategoryCode","itemId"));
		List<EmcOrderTaskResultBean> targetSpkcResultList = new ArrayList<EmcOrderTaskResultBean>();
		if(!taskSpkcResultList.isEmpty()){
			List<Long> resultSpkcItemIdList = taskSpkcResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
			Long qcSpkcCategoryId = taskSpkcResultList.get(0).getQcCategoryId();
			List<EmcItemQcIndexBean> itemSpkcQcIndexList = itemQcIndexService.selectListByFilter(SearchFilter.instance().match("itemId", resultSpkcItemIdList).filter(MatchPattern.OR).match("qcCategoryId", qcSpkcCategoryId).filter(MatchPattern.OR));
			List<Long> targetSpkcItemIdList = itemSpkcQcIndexList.stream().map(EmcItemQcIndexBean::getItemId).distinct().collect(Collectors.toList());
			targetSpkcResultList.addAll(taskSpkcResultList.stream().filter(result -> targetSpkcItemIdList.contains(result.getItemId())).collect(Collectors.toList()));

		}

		//空白加标结果
		List<EmcOrderTaskResultBean> taskSpkckResultList = orderTaskResultService.getDao().selectList(filterSpkckResult, Arrays.asList("runId", "qcCategoryCode"), Arrays.asList("id", "runId", "qcCategoryId", "itemId", "testMethodId", "orderNo","qcCategoryCode","itemId"));
		List<EmcOrderTaskResultBean> targetSpkckResultList = new ArrayList<EmcOrderTaskResultBean>();
		if(!taskSpkckResultList.isEmpty()){
			List<Long> resultSpkckItemIdList = taskSpkckResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
			Long qcSpkckCategoryId = taskSpkckResultList.get(0).getQcCategoryId();
			List<EmcItemQcIndexBean> itemSpkckQcIndexList = itemQcIndexService.selectListByFilter(SearchFilter.instance().match("itemId", resultSpkckItemIdList).filter(MatchPattern.OR).match("qcCategoryId", qcSpkckCategoryId).filter(MatchPattern.OR));
			List<Long> targetSpkckItemIdList = itemSpkckQcIndexList.stream().map(EmcItemQcIndexBean::getItemId).distinct().collect(Collectors.toList());
			targetSpkckResultList.addAll(taskSpkckResultList.stream().filter(result -> targetSpkckItemIdList.contains(result.getItemId())).collect(Collectors.toList()));

		}

		//目标的原始结果(这一步公共的)
		List<String> solIdList = curveList.stream().map(PfAnalyticalEquiptCrvesBean::getSolId).distinct().collect(Collectors.toList());
		List<PfAnalyticalEquiptDetailBean> detailList = pfAnalyticalEquiptDetailService.getDao().selectListByOneColumnValues(solIdList, "solId");
		//
		List<String> newSolIdList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), curveList.size());
		List<PfAnalyticalEquiptCrvesBean> newCurveList = new ArrayList<>(curveList.size());
		List<PfAnalyticalEquiptDetailBean> newDetailList = new ArrayList<>(detailList.size());
		List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();

		IntStream.range(0, curveList.size()).forEach(index -> {
			PfAnalyticalEquiptCrvesBean copiedCurve = curveList.get(index);
			PfAnalyticalEquiptCrvesBean newCurve = new PfAnalyticalEquiptCrvesBean();
			newCurve.setId(ApplicationContextHelper.getNextIdentity());
			newCurve.setRunId(runId);
			newCurve.setSdReserveConcentration(copiedCurve.getSdReserveConcentration());
			newCurve.setSdUseFluidConcentration(copiedCurve.getSdUseFluidConcentration());
			newCurve.setSolId(newSolIdList.get(index));
			newCurve.setCurveFactorFixedTo(copiedCurve.getCurveFactorFixedTo());
			newCurve.setAdder(copiedCurve.getAdder());
			newCurve.setAddDate(LocalDateTime.now());
			newCurve.setItemGroup(copiedCurve.getItemGroup());
			newCurve.setItemGroupId(copiedCurve.getItemGroupId());
			newCurve.setStdSubstanceContentUnit(copiedCurve.getStdSubstanceContentUnit());
			newCurve.setStdSubstanceContentVolUnit(copiedCurve.getStdSubstanceContentVolUnit());
			newCurve.setExpirationTime(copiedCurve.getExpirationTime());
			newCurve.setSetDate(copiedCurve.getSetDate());
			//newCurve.setExt$Item("testname",copiedCurve.getExt$Item("testname"));
			//newCurve.setExt$Item("testmethodid",copiedCurve.getExt$Item("testmethodid"));
			newCurve.setTestMethodId(copiedCurve.getTestMethodId());
			newCurve.setTestName(copiedCurve.getTestName());
			/*newCurve.setExt$Item("methodname",copiedCurve.getExt$Item("methodname"));
			newCurve.setExt$Item("methodid",copiedCurve.getExt$Item("methodid"));*/
			newCurve.setMethodId(copiedCurve.getMethodId());
			newCurve.setMethodName(copiedCurve.getMethodName());
			newCurve.setExt$Item("sdsolutionvolumeunit",copiedCurve.getExt$Item("sdsolutionvolumeunit"));
			newCurve.setExt$Item("sdconcentrationunit",copiedCurve.getExt$Item("sdconcentrationunit"));
			newCurve.setExt$Item("internalnumber",copiedCurve.getExt$Item("internalnumber"));
			newCurve.setExt$Item("aslope",copiedCurve.getExt$Item("aslope"));
			newCurve.setExt$Item("bintercept",copiedCurve.getExt$Item("bintercept"));
			newCurve.setExt$Item("rrelatedfactor",copiedCurve.getExt$Item("rrelatedfactor"));
			newCurveList.add(newCurve);

			Long testMethodId = Long.valueOf(copiedCurve.getTestMethodId());
			//这里要查曲线分析项组下的分析项id(这样相同测试不同分析项组的曲线同步可更新结果表中的结果集)
			List<EmcGroupItemBean> needGroupItemList = groupItemList.stream().filter(g -> ObjectUtils.equals(newCurve.getItemGroupId(), g.getItemGroupId())).collect(Collectors.toList());
			List<Long> itemIdMatchList =  needGroupItemList.stream().map(EmcGroupItemBean :: getItemId).distinct().collect(Collectors.toList());

			//获取曲线校核的对应结果表数据--对应曲线的分析项
			List<EmcOrderTaskResultBean> curOrgResultList = targetResultList.stream().filter(result -> ObjectUtils.equals(result.getTestMethodId(), testMethodId)).collect(Collectors.toList());
			List<EmcOrderTaskResultBean> curResultList = curOrgResultList.stream().filter(result -> itemIdMatchList.contains(result.getItemId())).collect(Collectors.toList());

			//获取样品加标的对应结果表数据 -- 对应曲线的分析项
			List<EmcOrderTaskResultBean> curOrgSpkcResultList = targetSpkcResultList.stream().filter(result -> ObjectUtils.equals(result.getTestMethodId(), testMethodId)).collect(Collectors.toList());
			List<EmcOrderTaskResultBean> curSpkcResultList = curOrgSpkcResultList.stream().filter(result -> itemIdMatchList.contains(result.getItemId())).collect(Collectors.toList());
			//获取空白加标的对应结果表数据 -- 对应曲线的分析项
			List<EmcOrderTaskResultBean> curOrgSpkckResultList = targetSpkckResultList.stream().filter(result -> ObjectUtils.equals(result.getTestMethodId(), testMethodId)).collect(Collectors.toList());
			List<EmcOrderTaskResultBean> curSpkckResultList = curOrgSpkckResultList.stream().filter(result -> itemIdMatchList.contains(result.getItemId())).collect(Collectors.toList());
			detailList.stream().filter(detail -> StringUtils.equals(detail.getSolId(), copiedCurve.getSolId())).forEach(detail -> {
				PfAnalyticalEquiptDetailBean newDetail = new PfAnalyticalEquiptDetailBean();
				newDetail.setId(ApplicationContextHelper.getNextIdentity());
				newDetail.setRunId(runId);//固定选中批批号
				newDetail.setSolId(newCurve.getSolId());//被复制曲线编号，同一曲线下的不同明细批的曲线编号相同
				newDetail.setSdSubstances(detail.getSdSubstances());
				newDetail.setAnalysisNumber(detail.getAnalysisNumber());
				newDetail.setSdConcentration(detail.getSdConcentration());
				newDetail.setSdSolutionVolume(detail.getSdSolutionVolume());
				newDetail.setQcType(detail.getQcType());
				newDetail.setStdSubstanceContent(detail.getStdSubstanceContent());
				newDetail.setStdSubstanceContentVol(detail.getStdSubstanceContentVol());
				newDetail.setRemark(detail.getRemark());
				newDetail.setExt$Item("peakareaassayvalue",detail.getExt$Item("peakareaassayvalue"));
				newDetail.setStdSubstanceContent(detail.getStdSubstanceContent());
				newDetail.setCreatedById(LocalContextHelper.getLoginUserId());
				newDetail.setCreatedByName(LocalContextHelper.getLoginUserName());
				newDetail.setCreatedTime(LocalDateTime.now());
				newDetail.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
				newDetail.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());

				// 根据分析编号匹配曲线校核的样品编号后缀，比如分析编号为001，对应的曲线校核就是STD1
				//先根据我加的字段去分析，这个字段可能包含多个分析项都要赋值
				//单挑曲线的detailsList

				List<PfAnalyticalEquiptDetailBean> detailSingleTestList = detailList.stream().filter(d -> StringUtils.equals(d.getSolId(),detail.getSolId())).collect(Collectors.toList());
				//单条曲线，QC类型填写的数据，若为空则此曲线按照金属组规则std数匹配
				List<String> qcTypeList = detailSingleTestList.stream().filter(p -> !StringUtils.isEmpty(p.getQcType())).map(PfAnalyticalEquiptDetailBean::getQcType).distinct().collect(Collectors.toList());

				if(!qcTypeList.isEmpty()){
					if(detail.getQcType()!= null && detail.getQcType() !=""){
						String qctype = detail.getQcType();
						String[] arr= qctype.split("[\\,]");
						List<String> qcTypelist=Arrays.asList(arr);
							if(qcTypelist.contains("L-QXCH")){//曲线校核
								List<EmcOrderTaskResultBean> matchedResultList = curResultList.stream()
										.filter(result -> StringUtils.equals(result.getQcCategoryCode(),"L-QXCH"))
										.collect(Collectors.toList());
								matchedResultList.forEach(result -> {
									if(detail.getSdConcentration()!=null && detail.getSdConcentration()!=""){
										result.setStandQty(detail.getSdConcentration());
									}
									updateResultList.add(result);
								});
							}
							if(qcTypelist.contains("L-SPKC")){//样品加标
								List<EmcOrderTaskResultBean> matchedSpkcResultList = curSpkcResultList.stream().filter(result -> StringUtils.equals(result.getQcCategoryCode(),"L-SPKC" )).collect(Collectors.toList());
								matchedSpkcResultList.forEach(result -> {
									if(detail.getStdSubstanceContent()!=null && detail.getStdSubstanceContent()!=""){
										result.setStandQty(detail.getStdSubstanceContent());
									}
									if(newCurve.getStdSubstanceContentUnit()!=null && newCurve.getStdSubstanceContentUnit()!=""){
										result.setRn12(newCurve.getStdSubstanceContentUnit());
									}
									result.setRn15(detail.getStdSubstanceContentVol());
									updateResultList.add(result);
								});
							}
							if(qcTypelist.contains("L-SPKCK")){//空白加标
								List<EmcOrderTaskResultBean> matchedSpkckResultList = curSpkckResultList.stream().filter(result -> StringUtils.equals(result.getQcCategoryCode(),"L-SPKCK" )).collect(Collectors.toList());
								matchedSpkckResultList.forEach(result -> {
									if(detail.getStdSubstanceContent()!=null && detail.getStdSubstanceContent()!=""){
										result.setStandQty(detail.getStdSubstanceContent());
									}
									if(newCurve.getStdSubstanceContentUnit()!=null && newCurve.getStdSubstanceContentUnit()!=""){
										result.setRn12(newCurve.getStdSubstanceContentUnit());
									}
									result.setRn15(detail.getStdSubstanceContentVol());
									updateResultList.add(result);
								});
							}
						}
					}else{
						int analysisNumber = NumberUtils.isNumber(detail.getAnalysisNumber()) ? NumberUtils.parseInt(detail.getAnalysisNumber()) : -1;
						List<EmcOrderTaskResultBean> matchedResultList = curResultList.stream().filter(result -> StringUtils.contains(result.getOrderNo(), PfConstant.QC_QXCH_ORDER_NO_PREFIX + analysisNumber)).collect(Collectors.toList());
						matchedResultList.forEach(result -> {
							result.setStandQty(detail.getSdConcentration());
							updateResultList.add(result);
						});
						}

				newDetailList.add(newDetail);
			});
		});

		getDao().insert(newCurveList);
		//最后更新的测试
		   List<Long> updateTestMethodIdList = newCurveList.stream().map(PfAnalyticalEquiptCrvesBean::getTestMethodId).collect(Collectors.toList());
		   List<Long> noUpdateTestMethodIdList = testMethodIdList.stream().filter(t ->!updateTestMethodIdList.contains(t)).collect(Collectors.toList());
		   List<EmcTestMethodBean> noTestMethodList = testMethodService.getDao().selectListByIds(noUpdateTestMethodIdList);
		   List<Long> notInTestIdList =  noTestMethodList.stream().map(t ->t.getTestId()).collect(Collectors.toList());
		   List<EmcTestBean> notInTestList = testService.getDao().selectListByIds(notInTestIdList);
		   List<String> notInTestNameList = notInTestList.stream().map(EmcTestBean ::getTestName).collect(Collectors.toList());
		   note = StringUtils.join(notInTestNameList, ",");
		
		pfAnalyticalEquiptDetailService.getDao().insert(newDetailList);

		if (!updateResultList.isEmpty()) {
			orderTaskResultService.getDao().update(updateResultList,"standQty","rn15","rn12");
			List<Long> resultIdList = updateResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList());
			EmcOrderTaskResultComputingEngine.getEngine(resultIdList).compute();
		}
		orderTaskList.clear();
		orderItemGroupList.clear();
		tempHumidityList.clear();
		return note;
	}
	
	@Override
	@Transactional
	public void changSolId(RestJsonWrapperBean wrapper) {
		//update的id
		String solId = wrapper.getFilterValue("solId");
		PfAnalyticalEquiptCrvesBean crves= wrapper.parseUnique(PfAnalyticalEquiptCrvesBean.class);
		//如果曲线id修改，原始曲线id对应得details表的solid也需要修改
		String beforeSolId = crves.getSolId();
		List<PfAnalyticalEquiptDetailBean> existDetailList = pfAnalyticalEquiptDetailService.getDao().selectListByOneColumnValue(beforeSolId,"solId");
		if(existDetailList.isEmpty()){
			return;
		}
		//将老的solId的详细信息改成新的
		existDetailList.forEach(d ->{
			d.setSolId(solId);
		});
		pfAnalyticalEquiptDetailService.getDao().update(existDetailList, "solId");
	}
	
}


