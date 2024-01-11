package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataLineBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.EmcFetchDataDao;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataLineService;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Repository
@GikamBean
public class EmcFetchDataServiceImpl implements EmcFetchDataService {

    @Autowired
    private EmcFetchDataLineService fetchDataLineService;

    @Autowired
    private EmcFetchDataDao emcFetchDataDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFetchDataDao getDao() {
        return emcFetchDataDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcFetchDataBean emcFetchDataBean = wrapperBean.parseUnique(EmcFetchDataBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFetchDataBean.setId(id);
        getDao().insert(emcFetchDataBean);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void generateLine(RestJsonWrapperBean wrapper) {
        List<EmcFetchDataBean> fetchDataList = wrapper.parse(EmcFetchDataBean.class);
        if (fetchDataList.isEmpty()) {
            return;
        }

        List<EmcFetchDataLineBean> fetchDataLineList = new ArrayList<>();
        fetchDataList.forEach(f -> {
            Long fieldOne = Optional.ofNullable(f.getFieldOne()).orElse(0L);
            Long fieldTwo = Optional.ofNullable(f.getFieldTwo()).orElse(0L);
            Long fieldThree = Optional.ofNullable(f.getFieldThree()).orElse(0L);

            if (fieldOne < 1 || fieldTwo < 1) {
                return;
            }

            if (fieldThree > 0) {
                for (long i = 0; i <= fieldOne; i++) {
                    for (long j = 0; j <= fieldTwo; j++) {
                        for (long m = 0; m <= fieldThree; m++) {
                            fetchDataLineList.add(createDataLine(f.getId(), i, j, m));
                        }
                    }
                }
            } else {
                for (long i = 0; i <= fieldOne; i++) {
                    for (long j = 0; j <= fieldTwo; j++) {
                        fetchDataLineList.add(createDataLine(f.getId(), i, j, null));
                    }
                }
            }
        });

        fetchDataLineService.getDao().deleteBy(fetchDataLineList, "FETCHDATAID");
        fetchDataLineService.getDao().insert(fetchDataLineList);
    }
    
    private EmcFetchDataLineBean createDataLine(Long fetchDataId, Long one, Long two, Long three) {
        EmcFetchDataLineBean newLine = new EmcFetchDataLineBean();
        newLine.setId(ApplicationContextHelper.getNextIdentity());
        newLine.setFetchDataId(fetchDataId);
        newLine.setFieldOne(ObjectUtils.isEmpty(one) ? null : one + "");
        newLine.setFieldTwo(ObjectUtils.isEmpty(two) ? null : two + "");
        newLine.setFieldThree(ObjectUtils.isEmpty(three) ? null : three + "");

        return newLine;
    }
}
