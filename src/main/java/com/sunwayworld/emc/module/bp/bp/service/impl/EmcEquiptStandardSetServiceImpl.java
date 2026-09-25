package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.bean.EmcEquiptStandardSetBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcEquiptStandardSetDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcEquiptStandardSetService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcEquiptStandardSetServiceImpl
implements EmcEquiptStandardSetService {
    @Autowired
    private EmcEquiptStandardSetDao emcEquiptStandardSetDao;

    public EmcEquiptStandardSetDao getDao() {
        return this.emcEquiptStandardSetDao;
    }

    @Transactional
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        String[] gasNames;
        EmcEquiptStandardSetBean emcEquiptStandardSet = (EmcEquiptStandardSetBean)jsonWrapper.parseUnique(EmcEquiptStandardSetBean.class);
        ArrayList<EmcEquiptStandardSetBean> insertList = new ArrayList<EmcEquiptStandardSetBean>();
        gasNames = emcEquiptStandardSet.getGasName().split(",");
        for (String gasName : gasNames) {
            EmcEquiptStandardSetBean standardSet = new EmcEquiptStandardSetBean();
            standardSet.setId(ApplicationContextHelper.getNextIdentity());
            standardSet.setGasName(gasName);
            standardSet.setSampleId(emcEquiptStandardSet.getSampleId());
            standardSet.setBizType("1");
            insertList.add(standardSet);
            EmcEquiptStandardSetBean standardSet1 = new EmcEquiptStandardSetBean();
            standardSet1.setId(ApplicationContextHelper.getNextIdentity());
            standardSet1.setGasName(gasName);
            standardSet1.setSampleId(emcEquiptStandardSet.getSampleId());
            standardSet1.setBizType("2");
            standardSet1.setGasType("\u6807\u6c14");
            insertList.add(standardSet1);
            EmcEquiptStandardSetBean standardSet2 = new EmcEquiptStandardSetBean();
            standardSet2.setId(ApplicationContextHelper.getNextIdentity());
            standardSet2.setGasName(gasName);
            standardSet2.setSampleId(emcEquiptStandardSet.getSampleId());
            standardSet2.setBizType("2");
            standardSet2.setGasType("\u96f6\u6c14");
            standardSet2.setBeforeTestA("0");
            standardSet2.setBeforeTestB("0");
            standardSet2.setBeforeError("0");
            standardSet2.setAfterTestA("0");
            standardSet2.setAfterTestB("0");
            standardSet2.setAfterError("0");
            standardSet2.setDensity("0");
            standardSet2.setConclusion("\u5408\u683c");
            insertList.add(standardSet2);
        }
        this.getDao().insert(insertList);
        return ((EmcEquiptStandardSetBean)insertList.get(0)).getId();
    }
}
