package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.EmcEquiptCalibrationDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationLineService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.service.EmcEquiptCalibrationService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@GikamBean
@Repository
public class EmcEquiptCalibrationServiceImpl implements EmcEquiptCalibrationService {

    /** 大气采样类 */
    private static final String AEROSPHERE_SAMPLING = "aerosphereSampling";
    /** 一般校准类 */
    private static final String GENERAL_CALIBRATION = "generalCalibration";
    /** 误差范围默认值 */
    private static final String ERROR_SCOPE_DEFAULT_VALUE = "≤±5%";

    @Autowired
    private EmcEquiptCalibrationLineService calibrationLineService;

    @Autowired
    private EmcEquiptCalibrationDao emcEquiptCalibrationDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCalibrationDao getDao() {
        return emcEquiptCalibrationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
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
                calibrationList.add(insertCalibration);
            });

            getDao().insert(calibrationList);
            calibrationLineService.insert(calibrationList);

            return calibrationList.get(0).getId();
        }

        Long id = ApplicationContextHelper.getNextIdentity();
        equiptCalibration.setId(id);
        equiptCalibration.setCalibrationDate(LocalDateTime.now());
        if (StringUtils.equals(AEROSPHERE_SAMPLING, equiptCalibration.getCalibrationType()) || StringUtils.equals(GENERAL_CALIBRATION, equiptCalibration.getCalibrationType())) {
            equiptCalibration.setErrorScope(ERROR_SCOPE_DEFAULT_VALUE);
        }
        getDao().insert(equiptCalibration);
        calibrationLineService.insert(Collections.singletonList(equiptCalibration));

        return id;
    }

    @Override
    public Page<EmcEquiptCalibrationLineBean> selectCalibrationLinePagination(Long id, RestJsonWrapperBean wrapper) {
        return calibrationLineService.selectPaginationByFilter(SearchFilter.instance().match("CALIBRATIONID", id + "").filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void insertCalibrationLine(RestJsonWrapperBean wrapper) {
        calibrationLineService.insert(wrapper);
    }

    @Override
    public void deleteCalibrationLine(RestJsonWrapperBean wrapper) {
        calibrationLineService.delete(wrapper);
    }

    @Override
    public void samplingBatchInput(RestJsonWrapperBean wrapper) {
        List<EmcEquiptCalibrationBean> calibrationList = wrapper.parse(EmcEquiptCalibrationBean.class);
        List<Long> calibrationIdList = calibrationList.stream().map(EmcEquiptCalibrationBean::getId).collect(Collectors.toList());
        List<EmcEquiptCalibrationLineBean> calibrationLineList = calibrationLineService.getDao().selectListByOneColumnValues(calibrationIdList, "calibrationId");
        if (calibrationLineList.isEmpty()) {
            return;
        }

        calibrationLineList.forEach(line -> {
            line.setHumidity(calibrationList.get(0).getExt$Item("humidity"));
            line.setTemperature(calibrationList.get(0).getExt$Item("temperature"));
            line.setPressure(calibrationList.get(0).getExt$Item("pressure"));
            line.setCalibrationResult(calibrationList.get(0).getExt$Item("calibrationresult"));
        });

        calibrationLineService.getDao().update(calibrationLineList, "HUMIDITY", "TEMPERATURE", "PRESSURE", "CALIBRATIONRESULT");
    }
}
