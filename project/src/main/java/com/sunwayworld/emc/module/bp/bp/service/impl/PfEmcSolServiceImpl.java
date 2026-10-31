package com.sunwayworld.emc.module.bp.bp.service.impl;

import com.sunwayworld.emc.module.bp.bp.service.PfEmcSolService;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import com.sunwayworld.emc.module.bp.sol.service.impl.EmcSolServiceImpl;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjian0705@sunwayworld.com
 * @date 2021/7/22 16:18
 */
@Repository
@GikamBean
public class PfEmcSolServiceImpl extends EmcSolServiceImpl implements PfEmcSolService {
    @Autowired
    private EmcSolRecordService solRecordService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertRecord(Long id, RestJsonWrapperBean wrapper) {
        EmcSolRecordBean solRecord = wrapper.parseUnique(EmcSolRecordBean.class);
        solRecord.setId(ApplicationContextHelper.getNextIdentity());
        solRecord.setSolId(id);
        solRecord.setUnitName("mL");
        Long titrationNum = wrapper.parseUnique(EmcSolRecordBean.class).getTitrationNum();
        if (titrationNum != null) {
            solRecord.setTitrationNum(titrationNum);
        }
        solRecordService.getDao().insert(solRecord);
        return solRecord.getId();
    }

    @Override
    public Map<String, String> calaConcentration(String calibrationconcentration, String revisionRule) {
        calibrationconcentration = NumberUtils.rounding(calibrationconcentration, revisionRule);
        Map<String, String> result = new HashMap<>();
        result.put("concentration", calibrationconcentration);
        return result;
    }
    @Override
    public void setInvalidRecord(RestJsonWrapperBean wrapper) {
        List<EmcSolRecordBean> solRecordBeanList = wrapper.parse(EmcSolRecordBean.class);
        solRecordBeanList.forEach(e -> {
            e.setSamplingVolume("/");
            e.setBlankStartValue("/");
            e.setBlankFinalValue("/");
            e.setStandardStartValue("/");
            e.setStandardFinalValue("/");
            e.setCalibrationConcentration("/");
            e.setAverageConcentration("/");
            e.setEightRelativeDeviation("/");
            e.setFourRelativeDeviation("/");
        });
        solRecordService.getDao().update(solRecordBeanList, "samplingvolume", "blankstartvalue",
                "blankfinalvalue", "standardstartvalue","standardfinalvalue",
                "calibrationconcentration","averageconcentration","fourRelativeDeviation","eightRelativeDeviation","remark");
        wrapper.setFilterValue("solId",solRecordBeanList.get(0).getSolId());
        solRecordService.calcResult(wrapper);
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void abandoned(RestJsonWrapperBean wrapper) {
        List<EmcSolBean> solList = wrapper.parse(EmcSolBean.class);
        if (!solList.isEmpty()) {
            solList.forEach(e-> e.setStatus(Constant.NO));
            getDao().update(solList, "STATUS");
        }
    }
}
