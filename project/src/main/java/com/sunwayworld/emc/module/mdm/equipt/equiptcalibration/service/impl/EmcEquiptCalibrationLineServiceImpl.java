package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.EmcEquiptCalibrationLineDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@GikamBean
@Repository
public class EmcEquiptCalibrationLineServiceImpl implements EmcEquiptCalibrationLineService {

    private static final String[] DEFAULT_GAS_NAMES = { "CO", "O2", "NO", "SO2" };
    /** 大气采样类 */
    private static final String AEROSPHERE_SAMPLING = "aerosphereSampling";
    /** 仪器性能类 */
    private static final String EQUIPT_CAPABILITY = "equiptCapability";
    /** 一般校准类 */
    private static final String GENERAL_CALIBRATION = "generalCalibration";

    private static final String[] DEFAULT_PERIODS = {"afterCalibrate", "beforeCalibrate"};

    private static final String DEFAULT_FLOW_UNIT = "L/min";

    @Autowired
    private EmcEquiptCalibrationLineDao emcEquiptCalibrationLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCalibrationLineDao getDao() {
        return emcEquiptCalibrationLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptCalibrationBean calibration = wrapper.parseUnique(EmcEquiptCalibrationBean.class);
        String calibrationType = calibration.getCalibrationType();

        List<EmcEquiptCalibrationLineBean> calibrationLineList = new ArrayList<>();
        String batchNo = ApplicationContextHelper.getNextSequence(getDao().getTable());
        if (AEROSPHERE_SAMPLING.equals(calibrationType) || GENERAL_CALIBRATION.equals(calibrationType)) {
            Arrays.asList(DEFAULT_PERIODS).forEach(period -> {
                EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                calibrationLine.setCalibrationId(calibration.getId());
                calibrationLine.setBatchNo(batchNo);
                calibrationLine.setCalibrationPeriod(period);
                calibrationLine.setFlowUnit(DEFAULT_FLOW_UNIT);
                calibrationLineList.add(calibrationLine);
            });
        } else if (EQUIPT_CAPABILITY.equals(calibrationType)) {
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
        }

        getDao().insert(calibrationLineList);

        return 0L;
    }

    @Override
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcEquiptCalibrationLineBean> calibrationLineList = wrapper.parse(EmcEquiptCalibrationLineBean.class);
        List<Long> lineIdList = calibrationLineList.stream().map(EmcEquiptCalibrationLineBean::getId).collect(Collectors.toList());
        List<EmcEquiptCalibrationLineBean> deleteFilterList = getDao().selectListByIds(lineIdList, Collections.singletonList("BATCHNO"));
        getDao().deleteBy(deleteFilterList, "BATCHNO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(List<EmcEquiptCalibrationBean> calibrationList) {
        if (calibrationList.isEmpty()) {
            return;
        }

        List<EmcEquiptCalibrationLineBean> calibrationLineList = new ArrayList<>();
        List<String> batchNoList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), calibrationList.size());
        IntStream.range(0, calibrationList.size()).forEach(index -> {
            EmcEquiptCalibrationBean calibration = calibrationList.get(index);
            String calibrationType = calibration.getCalibrationType();
            if (AEROSPHERE_SAMPLING.equals(calibrationType) || GENERAL_CALIBRATION.equals(calibrationType)) {
                Arrays.asList(DEFAULT_PERIODS).forEach(period -> {
                    EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                    calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                    calibrationLine.setCalibrationId(calibration.getId());
                    calibrationLine.setCalibrationPeriod(period);
                    calibrationLine.setFlowUnit(DEFAULT_FLOW_UNIT);
                    calibrationLine.setBatchNo(batchNoList.get(index));
                    calibrationLineList.add(calibrationLine);
                });
            } else if (EQUIPT_CAPABILITY.equals(calibrationType)) {
                Arrays.asList(DEFAULT_GAS_NAMES).forEach(gasName -> {
                    Arrays.asList(DEFAULT_PERIODS).forEach(period -> {
                        EmcEquiptCalibrationLineBean calibrationLine = new EmcEquiptCalibrationLineBean();
                        calibrationLine.setId(ApplicationContextHelper.getNextIdentity());
                        calibrationLine.setCalibrationId(calibration.getId());
                        calibrationLine.setCalibrationPeriod(period);
                        calibrationLine.setGasName(gasName);
                        calibrationLine.setFlowUnit(DEFAULT_FLOW_UNIT);
                        calibrationLine.setBatchNo(batchNoList.get(index));
                        calibrationLineList.add(calibrationLine);
                    });
                });
            }
        });

        getDao().insert(calibrationLineList);
    }
}
