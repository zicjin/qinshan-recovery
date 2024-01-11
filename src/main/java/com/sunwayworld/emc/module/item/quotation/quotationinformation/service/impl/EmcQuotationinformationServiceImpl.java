package com.sunwayworld.emc.module.item.quotation.quotationinformation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.quotation.addprice.bean.EmcAddpriceBean;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.bean.EmcDetectionfactorBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.bean.EmcQuotationinformationBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.dao.EmcQuotationinformationDao;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.service.EmcQuotationinformationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;

@Repository
@GikamBean
public class EmcQuotationinformationServiceImpl implements EmcQuotationinformationService {

    @Autowired
    private EmcQuotationinformationDao emcQuotationinformationDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQuotationinformationDao getDao() {
        return emcQuotationinformationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        String type = jsonWrapper.getFilterValue("type");
        Long quotationRegisterId = Long.parseLong(jsonWrapper.getFilterValue("quotationRegisterId"));
        List<EmcQuotationinformationBean> insertInformationList = new ArrayList<>();
        if("test".equals(type)){//添加检测项
            List<EmcDetectionfactorBean> detectionList = jsonWrapper.parse(EmcDetectionfactorBean.class);
            detectionList.forEach(e -> {
                EmcQuotationinformationBean bean = new EmcQuotationinformationBean();
                bean.setId(ApplicationContextHelper.getNextIdentity());
                bean.setQuotationRegisterId(quotationRegisterId);
                bean.setType("test");
                bean.setTestName(e.getItemGroupName());
                bean.setTestMethodName(e.getExt$Item("testmethodbasis"));
                bean.setMonitorTmplName(e.getMonitorTmplGroupName());
                bean.setTestSamplePrice(e.getSamplePrice());
                bean.setTestAnalysisPrice(e.getAnalysisPrice());
                bean.setTestId(ObjectUtils.toString(e.getTestId()));
                bean.setMethodId(ObjectUtils.toString(e.getMethodId()));
                bean.setTestAnalysisDiscount("100%");
                bean.setTestSampleDiscount("100%");
                bean.setTestDayNum("1");
                bean.setTestFrequency("1");
                bean.setTestMonitorNum("1");
                insertInformationList.add(bean);
            });
        }else{//添加附加费用
            List<EmcAddpriceBean> addPriceList = jsonWrapper.parse(EmcAddpriceBean.class);
            addPriceList.forEach(e -> {
                EmcQuotationinformationBean bean = new EmcQuotationinformationBean();
                bean.setId(ApplicationContextHelper.getNextIdentity());
                bean.setQuotationRegisterId(quotationRegisterId);
                bean.setAddPriceName(e.getPriceName());
                if(e.getPriceName().contains("报告编制费")){
                    bean.setType("test");
                    bean.setTestName(e.getPriceName());
                }else{
                    bean.setType("add");
                    bean.setAddUnitPrice(e.getPrice());
                }
                insertInformationList.add(bean);
            });
        }
        getDao().insert(insertInformationList);
        return insertInformationList.get(0).getId();
    }

}
