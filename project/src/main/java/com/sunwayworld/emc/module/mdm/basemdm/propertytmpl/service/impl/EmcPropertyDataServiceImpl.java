package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.sqlserver.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyDataDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

@Repository
@GikamBean
public class EmcPropertyDataServiceImpl implements EmcPropertyDataService {

    @Autowired
    private EmcPropertyDataDao emcPropertyDatayDao;

    @Autowired
    @Lazy
    private EmcMonitorPointCategoryService monitorPointCategorySerivce;

    @Autowired
    @Lazy
    private EmcMonitorPointService monitorPointSerivce;

    @Autowired
    @Lazy
    private EmcRunService runSerivce;

    @Autowired
    @Lazy
    private EmcSampleService emcSampleService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcPropertyDataDao getDao() {
        return emcPropertyDatayDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean emcPropertyDataBean = wrapper.parseUnique(EmcPropertyDataBean.class);
        // 查数据库是否已经存在记录，若已存在，改为update
        EmcPropertyDataBean bean = this.selectFirstByFilter(SearchFilter.instance().match("bizId", emcPropertyDataBean.getBizId()).filter(MatchPattern.EQ)
                .match("tmplId", emcPropertyDataBean.getTmplId()).filter(MatchPattern.EQ));
        if (bean != null) {
            emcPropertyDataBean.setId(bean.getId());
            getDao().update(emcPropertyDataBean);
            return emcPropertyDataBean.getId();
        }
        emcPropertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcPropertyDataBean);
        return emcPropertyDataBean.getId();
    }

    @Override
    public EmcPropertyDataBean selectPropertyData(Long id, Long tmplId) {
        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        emcPropertyDataBean.setBizId(id);
        emcPropertyDataBean.setTmplId(tmplId);
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            EmcSampleBean sampleBean = emcSampleService.selectById(id);
            if (sampleBean != null) {
                dataList.get(0).setExt$Item("samplingequiptname", sampleBean.getExt$Item("samplingequiptname"));
                dataList.get(0).setExt$Item("samplingequiptid", sampleBean.getExt$Item("samplingequiptid"));
            }
            return dataList.get(0);
        }

        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(tmplId)) {
            return new EmcPropertyDataBean();
        }

        emcPropertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcPropertyDataBean);

        return emcPropertyDataBean;
    }

    @Override
    public EmcPropertyDataBean selectPropertyData(Long id, Long monitorPointId, String monitorPointCategoryId) {
        EmcMonitorPointCategoryBean pointCategory=monitorPointCategorySerivce.getDao().selectByIdIfPresent(monitorPointCategoryId);

        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        emcPropertyDataBean.setBizId(id);
        emcPropertyDataBean.setTmplId(pointCategory.getMonitorPointType());
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            return dataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByMonitorPointId(Long id) {
        // 根据监测点 id 查询 模板 id
        EmcMonitorPointBean monitor = monitorPointSerivce.selectById(id);
        if (StringUtils.isEmpty(monitor.getMonitorPointCategoryId())) {
            return null;
        }
        EmcMonitorPointCategoryBean monitorCategory = monitorPointCategorySerivce.selectById(monitor.getMonitorPointCategoryId());
        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        emcPropertyDataBean.setBizId(id);
        emcPropertyDataBean.setTmplId(monitorCategory.getMonitorPointType());
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            return dataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId) {
        EmcPropertyDataBean emcPropertyDataBean = new EmcPropertyDataBean();
        if (tmplId != 0) {
            emcPropertyDataBean.setTmplId(tmplId);
        }
        emcPropertyDataBean.setBizId(testMethodId);
        List<EmcPropertyDataBean> dataList = getDao().selectList(emcPropertyDataBean);
        if (!dataList.isEmpty()) {
            return dataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByRunId(Long tmplId, String runId) {

        String testMethodId = runSerivce.selectDetail(runId).getTestMethodId();
        List<String> testMethodIdStrList = Arrays.asList(testMethodId.split(","));
        List<Long> testMethodIdList = testMethodIdStrList.stream().map(NumberUtils::parseLong).collect(Collectors.toList());

        List<EmcPropertyDataBean> testMethodDataList = selectListByFilter(SearchFilter.instance().match("BIZID", testMethodIdList).filter(MatchPattern.OR));
        List<EmcPropertyDataBean> runDataList = selectListByFilter(SearchFilter.instance().match("BIZID2", runId).filter(MatchPattern.EQ));

        if (tmplId != 0 && !runDataList.isEmpty()) {
            runDataList = runDataList.stream().filter(e -> ObjectUtils.equals(tmplId, e.getTmplId())).collect(Collectors.toList());
        }

        if (tmplId != 0 && !testMethodDataList.isEmpty()) {
            testMethodDataList = testMethodDataList.stream().filter(e -> ObjectUtils.equals(tmplId, e.getTmplId())).collect(Collectors.toList());
        }

        if (!runDataList.isEmpty()) {
            return runDataList.get(0);
        } else if (!testMethodDataList.isEmpty()) {
            return testMethodDataList.get(0);
        } else {
            return new EmcPropertyDataBean();
        }
    }

    @Override
    @Transactional
    public Long savePropertyDataByTestMethodId(Long tmplId, Long testMethodId, RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
        filterPropertyData.setBizId(testMethodId);
        filterPropertyData.setTableName("T_EMC_TEST_METHOD");
        List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

        EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
        BeanUtils.copyProperties(propertyData, newPropertyData);
        newPropertyData.setBizId(testMethodId);
        newPropertyData.setTableName("T_EMC_TEST_METHOD");
        newPropertyData.setTmplId(tmplId);

        if (!dataList.isEmpty()) {

            EmcPropertyDataBean oldPropertyData = dataList.get(0);
            newPropertyData.setId(oldPropertyData.getId());

            getDao().update(newPropertyData);

        } else {
            newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
            getDao().insert(newPropertyData);
        }

        return testMethodId;
    }

    @Override
    @Transactional
    public String saveResultRunPropertyDataByRunId(Long tmplId, String runId, RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        EmcPropertyDataBean filterPropertyData = new EmcPropertyDataBean();
        filterPropertyData.setBizId2(runId);
        filterPropertyData.setTableName("T_EMC_RUN");
        List<EmcPropertyDataBean> dataList = getDao().selectList(filterPropertyData);

        EmcPropertyDataBean newPropertyData = new EmcPropertyDataBean();
        BeanUtils.copyProperties(propertyData, newPropertyData);
        newPropertyData.setBizId2(runId);
        newPropertyData.setTableName("T_EMC_RUN");
        newPropertyData.setTmplId(tmplId);

        if (!dataList.isEmpty()) {

            EmcPropertyDataBean oldPropertyData = dataList.get(0);
            newPropertyData.setId(oldPropertyData.getId());

            getDao().update(newPropertyData);

        } else {
            newPropertyData.setId(ApplicationContextHelper.getNextIdentity());
            getDao().insert(newPropertyData);
        }

        return runId;
    }

    @Override
    public void insertIndexSetsPropertyData(RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);
        // 根据 bizId 查询 当前数据
        EmcPropertyDataBean oldPropertyData = getDao().selectListByOneColumnValue(propertyData.getBizId(), "BIZID").get(0);
        propertyData.setId(oldPropertyData.getId());
        getDao().update(propertyData);
    }

    @Override
    public void batchUpdatePropertyData(RestJsonWrapperBean wrapper) {
        List<EmcPropertyDataBean> propertyDataList = wrapper.parse(EmcPropertyDataBean.class);

        List<BeanPropertyDescriptor> descriptorList = BeanPropertyHelper.getBeanPropertyDescriptorList(propertyDataList.get(0).getClass());
        EmcPropertyDataBean propertyData = propertyDataList.get(0);
        List<String> notEmptyFieldNames = descriptorList.stream().filter(descriptor -> !ObjectUtils.isEmpty(descriptor.getPropertyValue(propertyData))).map(BeanPropertyDescriptor::getName).collect(Collectors.toList());

        getDao().update(propertyDataList, notEmptyFieldNames, "bizId");
    }

    @Override
    public void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean tmpl = wrapper.parseUnique(EmcPropertyDataBean.class);
        List<EmcMonitorPointBean> monitorPointList = wrapper.parse(EmcMonitorPointBean.class);
        List<Long> bizIdList = monitorPointList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcPropertyDataBean> list = getDao().selectListByOneColumnValues(bizIdList, "BIZID");
        for(EmcPropertyDataBean i : list) {
            BeanUtils.copyProperties(tmpl, i, "ID","BIZID","TMPLID","TABLENAME");
        }
        updateIfChanged(list);
    }

}
