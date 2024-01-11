package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月13日
 */
@GikamBean
@Repository
public class PfEquiptCalibrationLineServiceImpl extends EmcEquiptCalibrationLineServiceImpl {

    private static final String[] DEFAULT_GAS_NAMES = { "CO", "O2", "NO",  "NO2","SO2" };

    private static final String[] DEFAULT_PERIODS = {"afterCalibrate", "beforeCalibrate"};

    private static final String DEFAULT_FLOW_UNIT = "L/min";

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptCalibrationBean calibration = wrapper.parseUnique(EmcEquiptCalibrationBean.class);
        String calibrationType = calibration.getCalibrationType();

        List<EmcEquiptCalibrationLineBean> calibrationLineList = new ArrayList<>();
        String batchNo = ApplicationContextHelper.getNextSequence(getDao().getTable());
        if ("dioxinCalibration".equalsIgnoreCase(calibrationType)) {
            String calibrationCategory = calibration.getCalibrationCategory();
            int size = "smoke".equalsIgnoreCase(calibrationCategory) ? 3 : 1;
            IntStream.range(0, size).forEach(index -> {
                EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                calibrationLine.setCalibrationId(calibration.getId());
                calibrationLine.setBatchNo(batchNo);
                calibrationLineList.add(calibrationLine);
            });
        } else if ("flowCalibration".equals(calibrationType)) {
            Arrays.asList(DEFAULT_PERIODS).forEach(period -> {
                EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                calibrationLine.setCalibrationId(calibration.getId());
                calibrationLine.setBatchNo(batchNo);
                calibrationLine.setCalibrationPeriod(period);
                calibrationLine.setFlowUnit(DEFAULT_FLOW_UNIT);
                calibrationLineList.add(calibrationLine);
            });
        } else if ("stdGasCalibration".equals(calibrationType)) {
            Arrays.asList(DEFAULT_GAS_NAMES).forEach(gasName -> {
                Arrays.asList(DEFAULT_PERIODS).forEach(period -> {
                    EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                    calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                    calibrationLine.setCalibrationId(calibration.getId());
                    calibrationLine.setBatchNo(batchNo);
                    calibrationLine.setCalibrationPeriod(period);
                    calibrationLine.setGasName(gasName);
                    calibrationLine.setFlowUnit(DEFAULT_FLOW_UNIT);
                    calibrationLineList.add(calibrationLine);
                });
            });
        } else {
            EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
            calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
            calibrationLine.setCalibrationId(calibration.getId());
            calibrationLine.setBatchNo(batchNo);
            calibrationLineList.add(calibrationLine);
        }

        getDao().insert(calibrationLineList);

        return 0L;
    }
}
