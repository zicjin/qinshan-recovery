package com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.impl;


import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.PfPropertyTmplService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年01月12日
 */
@Repository
@GikamBean
public class PfPropertyTmplServiceImpl extends EmcPropertyTmplServiceImpl implements PfPropertyTmplService {

    @Autowired
    private PfPropertyDataService propertyDataService;

    @Autowired
    private PfPropertyFieldService propertyFieldService;

    @Override
    public EmcPropertyDataBean selectPropertyDataByOrderTaskId(Long tmplId, Long orderTaskId) {
        return propertyDataService.selectPropertyDataByOrderTaskId(tmplId, orderTaskId);
    }

    @Override
    public void savePropertyDataByOrderTaskId(Long tmplId,Long orderTaskId, RestJsonWrapperBean wrapper) {
         propertyDataService.savePropertyDataByOrderTaskId(tmplId, orderTaskId,wrapper);
    }

    @Override
    public void batchUpdatePropertyDataByOrderTaskId(Long tmplId,Long flag1,RestJsonWrapperBean wrapper) {
         propertyDataService.batchUpdatePropertyDataByOrderTaskId(tmplId,flag1,wrapper);
    }

    @Override
    public Long batchUpdatePropertyDataByTestMethodId(Long tmplId, RestJsonWrapperBean wrapper) {
         return propertyDataService.batchUpdatePropertyDataByTestMethodId(tmplId,wrapper);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1) {
        return propertyDataService.selectPropertyDataByTestCapacityId(tmplId, testCapacityId,flag1);
    }

    @Override
    public Long savePropertyDataByTestCapacityId(Long tmplId, Long testCapacityId,Long flag1, RestJsonWrapperBean wrapper) {
        return propertyDataService.savePropertyDataByTestCapacityIdtmplId(tmplId, testCapacityId,flag1, wrapper);
    }

    @Override
    public void copyTestMethodParam(RestJsonWrapperBean wrapper) {
         propertyDataService.copyTestMethodParam(wrapper);
    }

    @Override
    public Long batchUpdatePropertyDataByTestCapacityId(Long tmplId,Long flag1, RestJsonWrapperBean wrapper) {
         return propertyDataService.batchUpdatePropertyDataByTestCapacityId(tmplId,flag1,wrapper);
    }

    @Override
    public void batchUpdatePropertyDataByOrder(Long tmplId,Long flag1,RestJsonWrapperBean wrapper) {
         propertyDataService.batchUpdatePropertyDataByOrder(tmplId,flag1,wrapper);
    }

    @Override
    public Page<EmcPropertyFieldBean> selectPropertyFieldByRunId(String runId) {
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValue(runId, "bizId2", Arrays.asList("id", "tmplId"));
        if (propertyDataList.isEmpty()) {
            return new Pagination<>();
        }

        Long tmplId = propertyDataList.get(0).getTmplId();
        return propertyFieldService.selectPropertyFieldByTmplId(tmplId);
    }

    @Override
    public EmcPropertyDataBean selectPropertyDataByRunId(String runId) {
        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValue(runId, "bizId2");
        if (propertyDataList.isEmpty()) {
            return new EmcPropertyDataBean();
        }

        return propertyDataList.get(0);
    }

    @Override
    public void saveResultRunPropertyDataByRunId(String runId, RestJsonWrapperBean wrapper) {
        EmcPropertyDataBean propertyData = wrapper.parseUnique(EmcPropertyDataBean.class);

        List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValue(runId, "bizId2");
        if (propertyDataList.isEmpty()) {
            propertyData.setBizId2(runId);
            propertyData.setId(ApplicationContextHelper.getNextIdentity());
            propertyData.setTableName("T_EMC_RUN");
            propertyDataService.getDao().insert(propertyData);
            return;
        }

        propertyData.setId(propertyDataList.get(0).getId());
        propertyDataService.getDao().update(propertyData);
    }

}
