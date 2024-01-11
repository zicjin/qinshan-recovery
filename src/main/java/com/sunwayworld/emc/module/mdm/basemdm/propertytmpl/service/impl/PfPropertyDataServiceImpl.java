package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.PfTestMethodService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.PfOrderTaskService;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.PfTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年06月16日
 */
@Repository
@GikamBean
public class PfPropertyDataServiceImpl extends EmcPropertyDataServiceImpl implements PfPropertyDataService {

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    @Lazy
    private PfOrderTaskService pfOrderTaskService;

    @Autowired
    @Lazy
    private PfTestMethodService testMethodService;
    
    @Autowired
    @Lazy
    private EmcRunService runSerivce;
    
    @Autowired
    @Lazy
    private PfTestCapacityService testCapacityService;

    @Override
    public EmcPropertyDataBean selectPropertyDataByFolderId(Long folderId) {
        List<EmcPropertyDataBean> propertyDataList = getDao().selectListByOneColumnValue(folderId, "bizId");
        if (propertyDataList.isEmpty()) {
            EmcFolderBean folder = folderService.selectById(folderId);
            EmcMonitorTmplBean monitorTmpl = monitorTmplService.getDao().selectById(folder.getMonitorTmplId());
            String pointPropertyTmplId = monitorTmpl.getExt$Item("pointPropertyTmplId");
            if (!NumberUtils.isNumber(pointPropertyTmplId)) {
                return new EmcPropertyDataBean();
            }

            EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
            propertyData.setId(ApplicationContextHelper.getNextIdentity());
            propertyData.setTmplId(NumberUtils.parseLong(pointPropertyTmplId));
            propertyData.setBizId(folderId);
            propertyData.setTableName(folderService.getDao().getTable());
            getDao().insert(propertyData);

            return propertyData;
        }

        return propertyDataList.get(0);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByOrderTaskId(Long tmplId, Long orderTaskId) {
        List<EmcOrderTaskBean> orderTaskList = pfOrderTaskService.selectListByFilter(SearchFilter.instance().match("ID", orderTaskId).filter(MatchPattern.OR));
    	String runId = orderTaskList.get(0).getRunId();
        String testMethodId = runSerivce.selectDetail(runId).getTestMethodId();
        List<String> testMethodIdStrList = Arrays.asList(testMethodId.split(","));
        List<Long> testMethodIdList = testMethodIdStrList.stream().map(NumberUtils::parseLong).collect(Collectors.toList());
        List<EmcTestCapacityBean> tcBeforeList = testCapacityService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId");
        List<EmcTestCapacityBean> tcList = tcBeforeList.stream().filter(tc ->StringUtils.equals(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"), tc.getOrgId())).collect(Collectors.toList());
        List<Long> tcIdList = tcList.stream().map(EmcTestCapacityBean :: getId).collect(Collectors.toList());
        //改用监测能力库的方法参数
        List<EmcPropertyDataBean> testCapacityDataList = selectListByFilter(SearchFilter.instance().match("BIZID", tcIdList).filter(MatchPattern.OR));

        //List<EmcPropertyDataBean> testMethodDataList = selectListByFilter(SearchFilter.instance().match("BIZID", testMethodIdList).filter(MatchPattern.OR));
        List<EmcPropertyDataBean> orderTaskDataList = selectListByFilter(SearchFilter.instance().match("BIZID", orderTaskId).filter(MatchPattern.EQ));
        if (tmplId != 0 && !orderTaskDataList.isEmpty()) {
        	orderTaskDataList = orderTaskDataList.stream().filter(e -> ObjectUtils.equals(tmplId, e.getTmplId())).collect(Collectors.toList());
        }

        if (!orderTaskDataList.isEmpty()) {
            return orderTaskDataList.get(0);
        }

        if (tmplId != 0 && !testCapacityDataList.isEmpty()) {
        	testCapacityDataList = testCapacityDataList.stream().filter(e -> ObjectUtils.equals(tmplId, e.getTmplId())).collect(Collectors.toList());
        }

        if (!testCapacityDataList.isEmpty()) {
            return testCapacityDataList.get(0);
        }

        return new EmcPropertyDataBean();
    }

    @Override
    public void savePropertyDataByOrderTaskId(Long tmplId, Long orderTaskId, RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
        filterPropertyData.setBizId(orderTaskId);
        filterPropertyData.setTableName("T_EMC_ORDER_TASK");
        List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

        EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
        BeanUtils.copyProperties(propertyData, newPropertyData);
        newPropertyData.setBizId(orderTaskId);
        newPropertyData.setTableName("T_EMC_ORDER_TASK");
        //新增的按照有体积
        newPropertyData.setExt$Item("flag1", "1");
        newPropertyData.setTmplId(tmplId);

        if (!dataList.isEmpty()) {

            EmcPropertyDataBean oldPropertyData = dataList.get(0);
            newPropertyData.setId(oldPropertyData.getId());

            getDao().update(newPropertyData);

        } else {
            newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
            getDao().insert(newPropertyData);
        }

    }

    @Override
    public void batchUpdatePropertyDataByOrderTaskId(Long tmplId,Long flag1, RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskBean>  orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
    	List<Long> idList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
    	 //去重
    	List<EmcOrderTaskBean>  orderTaskBatchList = pfOrderTaskService.getDao().selectListByOneColumnValues(idList, "ID");
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        orderTaskBatchList.forEach(e -> {
            EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
            filterPropertyData.setBizId(e.getId());
            filterPropertyData.setTableName("T_EMC_ORDER_TASK");
            List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

            EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
            BeanUtils.copyProperties(propertyData, newPropertyData);
            newPropertyData.setBizId(e.getId());
            newPropertyData.setTableName("T_EMC_ORDER_TASK");
            newPropertyData.setExt$Item("flag1", flag1.toString());
            newPropertyData.setTmplId(tmplId);

            if (!dataList.isEmpty()) {
                EmcPropertyDataBean oldPropertyData = dataList.get(0);
                newPropertyData.setId(oldPropertyData.getId());
                getDao().update(newPropertyData);
            } else {
                newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                getDao().insert(newPropertyData);
            }
        });
        orderTaskBatchList.clear();
    }

    @Override
    public Long batchUpdatePropertyDataByTestMethodId(Long tmplId, RestJsonWrapperBean wrapper) {
    	List<EmcTestMethodBean>  testMethodList = wrapper.parse(EmcTestMethodBean.class);
    	List<Long> idList = testMethodList.stream().map(EmcTestMethodBean::getId).distinct().collect(Collectors.toList());
    	 //去重
    	List<EmcTestMethodBean>  testMethodBatchList = testMethodService.getDao().selectListByOneColumnValues(idList, "ID");
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);
        List<EmcPropertyDataBean> insertPropertyDataList = new ArrayList<>();
        List<EmcPropertyDataBean> updatePropertyDataList = new ArrayList<>();

        testMethodBatchList.forEach(e -> {
            EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
            filterPropertyData.setBizId(e.getId());
            filterPropertyData.setTableName("T_EMC_TEST_METHOD");
            List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

            EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
            BeanUtils.copyProperties(propertyData, newPropertyData);
            newPropertyData.setBizId(e.getId());
            newPropertyData.setTableName("T_EMC_TEST_METHOD");
            newPropertyData.setTmplId(tmplId);

            if (!dataList.isEmpty()) {
                EmcPropertyDataBean oldPropertyData = dataList.get(0);
                newPropertyData.setId(oldPropertyData.getId());
                updatePropertyDataList.add(newPropertyData);

            } else {
                newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                insertPropertyDataList.add(newPropertyData);
            }
        });
        getDao().update(updatePropertyDataList);
        getDao().insert(insertPropertyDataList);
        return 1L;
    }
    
    @Override
    public EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId) {
        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        if (tmplId != 0) {
            emcPropertyDataBean.setTmplId(tmplId);
        }
        emcPropertyDataBean.setBizId(testMethodId);
        emcPropertyDataBean.setTableName("T_EMC_TEST_METHOD");
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            return dataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }
    
    @Override
    public EmcPropertyDataBean selectPropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1) {
        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        if (tmplId != 0) {
            emcPropertyDataBean.setTmplId(tmplId);
        }
        emcPropertyDataBean.setBizId(testCapacityId);
        emcPropertyDataBean.setTableName("T_EMC_TEST_CAPACITY");
        emcPropertyDataBean.setExt$Item("flag1", flag1.toString());
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            return dataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }
    
    @Override
    @Transactional
    public Long savePropertyDataByTestCapacityIdtmplId(Long tmplId, Long testCapacityId,Long flag1, RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
        filterPropertyData.setBizId(testCapacityId);
        filterPropertyData.setTableName("T_EMC_TEST_CAPACITY");
        filterPropertyData.setExt$Item("flag1", flag1.toString());
        List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

        EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
        BeanUtils.copyProperties(propertyData, newPropertyData);
        newPropertyData.setBizId(testCapacityId);
        newPropertyData.setTableName("T_EMC_TEST_CAPACITY");
        newPropertyData.setTmplId(tmplId);
        newPropertyData.setExt$Item("flag1", flag1.toString());
        

        if (!dataList.isEmpty()) {

            EmcPropertyDataBean oldPropertyData = dataList.get(0);
            newPropertyData.setId(oldPropertyData.getId());

            getDao().update(newPropertyData);

        } else {
            newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
            getDao().insert(newPropertyData);
        }

        return testCapacityId;
    }
    
    @Override
    public void copyTestMethodParam(RestJsonWrapperBean wrapper) {
    	//找到测试管理已经配置好的properdata
    	List<EmcPropertyDataBean> propertyDataList = getDao().selectListByOneColumnValue("T_EMC_TEST_METHOD", "tableName");
    	List<Long> bizIdList = propertyDataList.stream().map(EmcPropertyDataBean :: getBizId).distinct().collect(Collectors.toList());
    	//propertyDataList id递增，bizid变成监测能力库id
    	List<EmcTestCapacityBean> testCapacityBeforeList = testCapacityService.getDao().selectListByOneColumnValues(bizIdList, "testMethodId");
    	List<EmcTestCapacityBean> testCapacityList = testCapacityBeforeList.stream().filter(tc ->StringUtils.equals(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"), tc.getOrgId())).collect(Collectors.toList());
    	List<EmcPropertyDataBean> insertPropertyDataList = new ArrayList<>();
    	propertyDataList.forEach(pro ->{
    		//bizid替换为监测能力库的
    		List<EmcTestCapacityBean> testCapacityMatchList = testCapacityList.stream().filter(tc ->ObjectUtils.equals(pro.getBizId(), tc.getTestMethodId())).collect(Collectors.toList());
    		if(!testCapacityMatchList.isEmpty()){
    			pro.setId(ApplicationContextHelper.getNextIdentity());
        		pro.setTableName("T_EMC_TEST_CAPACITY");
    			Long testCapacityId = testCapacityMatchList.get(0).getId();
        		pro.setBizId(testCapacityId);
        		insertPropertyDataList.add(pro);
    		}

    	});
        getDao().insert(insertPropertyDataList);
    }
    
    @Override
    public Long batchUpdatePropertyDataByTestCapacityId(Long tmplId,Long flag1, RestJsonWrapperBean wrapper) {
    	List<EmcTestCapacityBean>  testCapacityList = wrapper.parse(EmcTestCapacityBean.class);
    	List<Long> idList = testCapacityList.stream().map(EmcTestCapacityBean::getId).distinct().collect(Collectors.toList());
    	 //去重
    	List<EmcTestCapacityBean>  testCapacityBatchList = testCapacityService.getDao().selectListByOneColumnValues(idList, "ID");
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);
        List<EmcPropertyDataBean> insertPropertyDataList = new ArrayList<>();
        List<EmcPropertyDataBean> updatePropertyDataList = new ArrayList<>();

        testCapacityBatchList.forEach(e -> {
            EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
            filterPropertyData.setBizId(e.getId());
            filterPropertyData.setTableName("T_EMC_TEST_CAPACITY");
            filterPropertyData.setExt$Item("flag1", flag1.toString());

            List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

            EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
            BeanUtils.copyProperties(propertyData, newPropertyData);
            newPropertyData.setBizId(e.getId());
            newPropertyData.setExt$Item("flag1", flag1.toString());
            newPropertyData.setTableName("T_EMC_TEST_CAPACITY");
            newPropertyData.setTmplId(tmplId);

            if (!dataList.isEmpty()) {
                EmcPropertyDataBean oldPropertyData = dataList.get(0);
                newPropertyData.setId(oldPropertyData.getId());
                updatePropertyDataList.add(newPropertyData);

            } else {
                newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                insertPropertyDataList.add(newPropertyData);
            }
        });
        getDao().update(updatePropertyDataList);
        getDao().insert(insertPropertyDataList);
        return 1L;
    }
    
    @Override
    public void batchUpdatePropertyDataByOrder(Long tmplId,Long flag1, RestJsonWrapperBean wrapper) {
    	List<EmcOrderTaskBean>  orderTaskOrgList = wrapper.parse(EmcOrderTaskBean.class);
    	List<String> runIdList = orderTaskOrgList.stream().filter(ok -> !StringUtils.isEmpty(ok.getRunId())).map(EmcOrderTaskBean :: getRunId).collect(Collectors.toList());
    	List<String> runAfterIdList = runIdList.stream().filter(item ->!StringUtils.isEmpty(item)).collect(Collectors.toList());
    	List<Long> orderIdList = orderTaskOrgList.stream().map(EmcOrderTaskBean :: getOrderId).distinct().collect(Collectors.toList());

    	List<EmcOrderTaskBean>  orderBeforeTaskList = pfOrderTaskService.getDao().selectListByOneColumnValues(orderIdList, "orderId");
    	List<EmcOrderTaskBean>  orderTaskList = orderBeforeTaskList.stream().filter(o ->runAfterIdList.contains(o.getRunId())).collect(Collectors.toList());

    	List<Long> idList = orderTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
    	 //去重
    	List<EmcOrderTaskBean>  orderTaskBatchList = pfOrderTaskService.getDao().selectListByOneColumnValues(idList, "ID");
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        orderTaskBatchList.forEach(e -> {
            EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
            filterPropertyData.setBizId(e.getId());
            filterPropertyData.setTableName("T_EMC_ORDER_TASK");
            List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

            EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
            BeanUtils.copyProperties(propertyData, newPropertyData);
            newPropertyData.setBizId(e.getId());
            newPropertyData.setTableName("T_EMC_ORDER_TASK");
            newPropertyData.setExt$Item("flag1", flag1.toString());
            newPropertyData.setTmplId(tmplId);

            if (!dataList.isEmpty()) {
                EmcPropertyDataBean oldPropertyData = dataList.get(0);
                newPropertyData.setId(oldPropertyData.getId());
                getDao().update(newPropertyData);
            } else {
                newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                getDao().insert(newPropertyData);
            }
        });
    }
    
    
}
