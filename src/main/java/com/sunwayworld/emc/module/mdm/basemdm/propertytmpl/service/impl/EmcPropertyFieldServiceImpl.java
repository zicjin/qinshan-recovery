package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;

import java.util.List;

import com.microsoft.sqlserver.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyFieldDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;

@Repository
@GikamBean
public class EmcPropertyFieldServiceImpl implements EmcPropertyFieldService {

    @Autowired
    private EmcPropertyFieldDao emcPropertyFieldDao;

    @Autowired
    @Lazy
    private EmcPropertyDataService propertyDataService;

    @Autowired
    @Lazy
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    @Lazy
    private EmcSampleService sampleService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    @Lazy
    private EmcMonitorPointService monitorPointSerivce;

    @Autowired
    @Lazy
    private EmcMonitorPointCategoryService monitorPointCategorySerivce;

    @SuppressWarnings("unchecked")
    @Override
    public EmcPropertyFieldDao getDao() {
        return emcPropertyFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long id, RestJsonWrapperBean wrapper) {
        EmcPropertyFieldBean emcPropertyFieldBean = wrapper.parseUnique(EmcPropertyFieldBean.class);
        emcPropertyFieldBean.setId(ApplicationContextHelper.getNextIdentity());
        emcPropertyFieldBean.setTmplId(id);
        getDao().insert(emcPropertyFieldBean);
        return emcPropertyFieldBean.getId();
    }

    @Override
    @Transactional
    public Page<EmcPropertyFieldBean> selectTmpls(Long id, String monitorTmplId) {
        Long tmplId;
        if ("undefined".equals(monitorTmplId)) { // 为空的话 查询 属性模板id
            // 查询 order 表的 monitorTmplId, 存在已有采样单的样品已添加，但采样单的监测模板id为空
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValue(id, "SAMPLEID");
            if (orderList.size() > 0) {
                monitorTmplId = orderList.get(0).getMonitorTmplId();
                // (id --> sample 表的主键)
                EmcSampleBean sampleBean = new EmcSampleBean();
                sampleBean.setId(id);
                sampleBean = sampleService.selectById(id);
                if (StringUtils.isEmpty(sampleBean.getMonitorTmplId())) {
                    sampleBean.setMonitorTmplId(monitorTmplId);
                    sampleService.update(sampleBean);
                }
            } else {
                return new Pagination<>();
            }
        }
        EmcMonitorTmplBean monitorTmplBean = monitorTmplService.selectById(monitorTmplId);
        EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
        propertyDataBean.setBizId(id);
        List<EmcPropertyDataBean> dataList = propertyDataService.getDao().selectList(propertyDataBean);

        tmplId = monitorTmplBean.getPropertyTmplId();
        List<EmcPropertyFieldBean> propertyFieldList = getDao().selectListByOneColumnValue(tmplId, "TMPLID", Order.asc("ORDERNO"), Order.desc("ID"));

        if (dataList.isEmpty()) {
            propertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
            propertyDataBean.setTmplId(monitorTmplBean.getPropertyTmplId());
            propertyDataBean.setTableName(sampleService.getDao().getTable());
            propertyFieldList.stream().filter(e -> !StringUtils.isEmpty(e.getDefaultValue())).forEach(e -> {
                String internalField = e.getInternalField(); // 内部字段
                String defaultValue = e.getDefaultValue(); // 默认值
                ReflectionUtils.setFieldValue(propertyDataBean, internalField, defaultValue);
            });
            propertyDataService.getDao().insert(propertyDataBean);
        } else {
            EmcPropertyDataBean propertyData = dataList.get(0);
            propertyData.setTmplId(tmplId);
            propertyData.setTableName(sampleService.getDao().getTable());
            propertyFieldList.stream().filter(e -> !StringUtils.isEmpty(e.getDefaultValue())).forEach(e -> {
                String internalField = e.getInternalField(); // 内部字段
                String defaultValue = e.getDefaultValue(); // 默认值
                if(ObjectUtils.isEmpty(ReflectionUtils.getFieldValue(propertyData, internalField)) && !ObjectUtils.isEmpty(defaultValue)){
                    ReflectionUtils.setFieldValue(propertyData, internalField, defaultValue);
                }
            });
            propertyDataService.getDao().update(propertyData);
        }
        return new Pagination<>(propertyFieldList);
    }

    @Override
    @Transactional
    public Page<EmcPropertyFieldBean> selectTmpls(Long id, Long monitorPointId, String monitorPointCategoryId) {
        EmcMonitorPointCategoryBean pointCategory=monitorPointCategorySerivce.getDao().selectByIdIfPresent(monitorPointCategoryId);

        Long tmplId = pointCategory.getMonitorPointType();
        List<EmcPropertyFieldBean> propertyFieldList = getDao().selectListByOneColumnValue(tmplId, "TMPLID", Order.asc("ORDERNO"), Order.desc("ID"));

        EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
        propertyDataBean.setBizId(id);
        List<EmcPropertyDataBean> dataList = propertyDataService.getDao().selectList(propertyDataBean);

        if (dataList.isEmpty()) {
            propertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
            propertyDataBean.setTmplId(tmplId);
            propertyDataBean.setTableName(folderService.getDao().getTable());
            propertyFieldList.stream().filter(e -> !StringUtils.isEmpty(e.getDefaultValue())).forEach(e -> {
                String internalField = e.getInternalField(); // 内部字段
                String defaultValue = e.getDefaultValue(); // 默认值
                ReflectionUtils.setFieldValue(propertyDataBean, internalField, defaultValue);
            });
            propertyDataService.getDao().insert(propertyDataBean);
        } else {
            EmcPropertyDataBean propertyData = dataList.get(0);
            propertyData.setTmplId(tmplId);
            propertyData.setTableName(folderService.getDao().getTable());
            propertyFieldList.stream().filter(e -> !StringUtils.isEmpty(e.getDefaultValue())).forEach(e -> {
                String internalField = e.getInternalField(); // 内部字段
                String defaultValue = e.getDefaultValue(); // 默认值
                if(ObjectUtils.isEmpty(ReflectionUtils.getFieldValue(propertyData, internalField)) && !ObjectUtils.isEmpty(defaultValue)){
                    ReflectionUtils.setFieldValue(propertyData, internalField, defaultValue);
                }
            });
            propertyDataService.getDao().update(propertyData);
        }
        return new Pagination<>(propertyFieldList);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorPointId(Long id) {
        EmcMonitorPointBean monitor = monitorPointSerivce.selectById(id);
        if (StringUtils.isEmpty(monitor.getMonitorPointCategoryId())) {
            return new Pagination<>();
        }
        EmcMonitorPointCategoryBean monitorCategory = monitorPointCategorySerivce.selectById(monitor.getMonitorPointCategoryId());
        List<EmcPropertyFieldBean> propertyFieldList = getDao().selectListByOneColumnValue(monitorCategory.getMonitorPointType(), "TMPLID", Order.asc("ORDERNO"), Order.desc("ID"));
        if (propertyFieldList.isEmpty()) {
            return new Pagination<>();
        }
        return new Pagination<>(propertyFieldList);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByTmplId(Long id) {
        List<EmcPropertyFieldBean> fieldList = getDao().selectListByOneColumnValue(id, "TMPLID", Order.asc("ORDERNO"), Order.desc("ID"));
        if (fieldList.isEmpty()) {
            return new Pagination<>();
        }
        return new Pagination<>(fieldList);
    }

    @Override
    public void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper) {
        EmcPropertyFieldBean field = wrapper.parseUnique(EmcPropertyFieldBean.class);
        getDao().update(field, "DEFAULTVALUE");
    }

}
