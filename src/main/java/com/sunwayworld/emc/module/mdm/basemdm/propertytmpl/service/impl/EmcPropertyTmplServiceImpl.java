package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.dao.EmcPropertyTmplDao;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@GikamBean
@Repository
public class EmcPropertyTmplServiceImpl implements EmcPropertyTmplService {

    @Autowired
    private EmcPropertyTmplDao emcPropertyTmplDao;

    @Autowired
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcPropertyTmplDao getDao() {
        return emcPropertyTmplDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcPropertyTmplBean emcPropertyTmplBean = wrapper.parseUnique(EmcPropertyTmplBean.class);
        emcPropertyTmplBean.setId(Long.valueOf(ApplicationContextHelper.getNextSequence(getDao().getTable())));
        getDao().insert(emcPropertyTmplBean);
        return emcPropertyTmplBean.getId();
    }

    @Override
    public Long insertField(Long id, RestJsonWrapperBean wrapper) {
        return propertyFieldService.insert(id, wrapper);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectFieldPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return propertyFieldService.selectPaginationByFilter(SearchFilter.instance().match("TMPLID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public EmcPropertyFieldBean selectFieldById(Long itemId) {
        return propertyFieldService.selectDetail(itemId);
    }

    @Override
    public void deleteField(RestJsonWrapperBean wrapper) {
        propertyFieldService.delete(wrapper);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectTmpls(Long id, String monitorTmplId) {
        return propertyFieldService.selectTmpls(id, monitorTmplId);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectTmpls(Long id, Long monitorPointId, String monitorPointCategoryId) {
        return propertyFieldService.selectTmpls(id, monitorPointId, monitorPointCategoryId);
    }

    @Override
    public EmcPropertyDataBean selectPropertyData(Long id, Long tmplId) {
        return propertyDataService.selectPropertyData(id, tmplId);
    }

    @Override
    public EmcPropertyDataBean selectPropertyData(Long id, Long monitorPointId, String monitorPointCategoryId) {
        return propertyDataService.selectPropertyData(id, monitorPointId, monitorPointCategoryId);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByMonitorPointId(Long id) {
        return propertyDataService.selectPropertyDataByMonitorPointId(id);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByMonitorPointId(Long id) {
        return propertyFieldService.selectPropertyFieldByMonitorPointId(id);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByTmplId(Long id) {
        return propertyFieldService.selectPropertyFieldByTmplId(id);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByTestMethodId(Long tmplId, Long testMethodId) {
        return propertyDataService.selectPropertyDataByTestMethodId(tmplId, testMethodId);
    }

    @Override
    public Long savePropertyDataByTestMethodId(Long tmplId, Long testMethodId, RestJsonWrapperBean wrapper) {
        return propertyDataService.savePropertyDataByTestMethodId(tmplId, testMethodId, wrapper);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByRunId(Long tmplId, String runId) {
        return propertyDataService.selectPropertyDataByRunId(tmplId, runId);
    }

    @Override
    public String saveResultRunPropertyDataByRunId(Long tmplId, String runId, RestJsonWrapperBean wrapper) {
        return propertyDataService.saveResultRunPropertyDataByRunId(tmplId, runId, wrapper);
    }

    @Override
    public void save(Long id, RestJsonWrapperBean wrapper) {
        propertyDataService.insertIndexSetsPropertyData(wrapper);
    }

    @Override
    public void batchUpdatePropertyData(RestJsonWrapperBean wrapper) {
        propertyDataService.batchUpdatePropertyData(wrapper);
    }

    @Override
    public List<EmcPropertyFieldBean> selectFieldsByMonitorPointCategory(String tmplName) {
        return propertyFieldService.selectListByFilter(SearchFilter.instance().match("TEMPLATENAME", tmplName).filter(MatchPattern.SEQ));
    }

    @Override
    public void updatePropertyFieldByMonitorPointCategory(RestJsonWrapperBean wrapper) {
        propertyFieldService.updatePropertyFieldByMonitorPointCategory(wrapper);
    }

    @Override
    public void updatePropertyDataByMonitorPoint(RestJsonWrapperBean wrapper) {
        propertyDataService.updatePropertyDataByMonitorPoint(wrapper);
    }

    @Override
    @Transactional
    public void copy(RestJsonWrapperBean wrapper) {
        List<EmcPropertyTmplBean> propertyTmplList = wrapper.parse(EmcPropertyTmplBean.class);

        List<EmcPropertyTmplBean> newPropertyTmplList = new ArrayList<>(propertyTmplList.size());
        List<EmcPropertyFieldBean> newPropertyFieldList = new ArrayList<>();
        propertyTmplList.forEach(propTmpl -> {
            // 复制模板
            EmcPropertyTmplBean newPropTmpl = new EmcPropertyTmplBean();
            newPropTmpl.setTemplateName(propTmpl.getTemplateName());
            newPropTmpl.setTemplateUse(propTmpl.getTemplateUse());
            newPropTmpl.setId(Long.valueOf(ApplicationContextHelper.getNextSequence(getDao().getTable())));
            newPropertyTmplList.add(newPropTmpl);

            // 复制字段
            List<EmcPropertyFieldBean> propertyFieldBeanList = propertyFieldService.selectListByFilter(SearchFilter.instance().match("tmplId", propTmpl.getId()).filter(MatchPattern.OR));
            propertyFieldBeanList.forEach(propField -> {
                EmcPropertyFieldBean newPropField = new EmcPropertyFieldBean();
                BeanUtils.copyProperties(propField, newPropField);
                newPropField.setTmplId(newPropTmpl.getId());
                newPropField.setId(ApplicationContextHelper.getNextIdentity());
                newPropertyFieldList.add(newPropField);
            });
        });

        if (!newPropertyTmplList.isEmpty()) {
            getDao().insert(newPropertyTmplList);
        }

        if (!newPropertyFieldList.isEmpty()) {
            propertyFieldService.getDao().insert(newPropertyFieldList);
        }
    }
}
