package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationLineService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.PfEquiptCalibrationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

@GikamBean
@Repository
public class PfEquiptCalibrationServiceImpl extends EmcEquiptCalibrationServiceImpl implements PfEquiptCalibrationService {
    /** 大气采样类 */
    private static final String AEROSPHERE_SAMPLING = "aerosphereSampling";
    /** 一般校准类 */
    private static final String GENERAL_CALIBRATION = "generalCalibration";
    /** 误差范围默认值 */
    private static final String ERROR_SCOPE_DEFAULT_VALUE = "≤±5%";
    
    @Autowired
    private EmcEquiptCalibrationLineService calibrationLineService;
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long sampleId,RestJsonWrapperBean wrapperBean) {
        EmcEquiptCalibrationBean equiptCalibration = wrapperBean.parseUnique(EmcEquiptCalibrationBean.class);
        String multiEquiptId = equiptCalibration.getExt$Item("multiequiptid");
        if (!StringUtils.isEmpty(multiEquiptId)) {
            String[] equiptIds = StringUtils.split(multiEquiptId, ",");
            List<EmcEquiptCalibrationBean> calibrationList = new ArrayList<>();
            Arrays.stream(equiptIds).forEach(equiptId -> {
                EmcEquiptCalibrationBean insertCalibration = new EmcEquiptCalibrationBean();
                BeanUtils.copyProperties(equiptCalibration, insertCalibration);
                if (StringUtils.equals(AEROSPHERE_SAMPLING, insertCalibration.getCalibrationType()) || StringUtils.equals(GENERAL_CALIBRATION, insertCalibration.getCalibrationType())) {
                    insertCalibration.setErrorScope(ERROR_SCOPE_DEFAULT_VALUE);
                }
                insertCalibration.setEquiptId(NumberUtils.parseLong(equiptId));
                insertCalibration.setId(ApplicationContextHelper.getNextIdentity());
                insertCalibration.setCalibrationDate(LocalDateTime.now());
                insertCalibration.setExt$Item("sampleId",sampleId.toString());
                calibrationList.add(insertCalibration);
            });

            getDao().insert(calibrationList);
            calibrationLineService.insert(calibrationList);

            return calibrationList.get(0).getId();
        }

        Long id = ApplicationContextHelper.getNextIdentity();
        equiptCalibration.setId(id);
        equiptCalibration.setCalibrationDate(LocalDateTime.now());
        equiptCalibration.setExt$Item("sampleId",sampleId.toString());
        if (StringUtils.equals(AEROSPHERE_SAMPLING, equiptCalibration.getCalibrationType()) || StringUtils.equals(GENERAL_CALIBRATION, equiptCalibration.getCalibrationType())) {
            equiptCalibration.setErrorScope(ERROR_SCOPE_DEFAULT_VALUE);
        }
        getDao().insert(equiptCalibration);
        calibrationLineService.insert(Collections.singletonList(equiptCalibration));

        return id;
    }
}
