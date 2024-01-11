package com.sunwayworld.emc.module.bp.sol.service.impl;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolLineService;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sol.dao.EmcSolRecordDao;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@GikamBean
public class EmcSolRecordServiceImpl implements EmcSolRecordService {
    @Autowired
    private EmcSolRecordDao solRecordDao;

    @Autowired
    private EmcSolLineService solLineService;

    @Autowired
    private EmcSolService solService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSolRecordDao getDao() {
        return solRecordDao;
    }

    @Override
    public void calcResult(RestJsonWrapperBean wrapper) {
        String solIdStr = wrapper.getFilterValue("solId");
        if(StringUtils.isEmpty(solIdStr)) return;

        Long solId = NumberUtils.parseLong(solIdStr);

        if (!ObjectUtils.isEmpty(solId)) {
            EmcSolBean sol = solService.getDao().selectByIdIfPresent(solId);

            if (!ObjectUtils.isEmpty(sol)) {
                String revisionRule = Optional.ofNullable(sol.getRevisionRule()).orElse("EMC,5");//修约规则
                if (StringUtils.isEmpty(sol.getScript())) {
                    return;
//                    throw new AuthenticationServiceException("请输入：标定溶液计算公式！");
                }
                if (StringUtils.isEmpty(sol.getTakeWeightOrVolume())) {
                    return;
//                    throw new AuthenticationServiceException("请输入：标准液称重量(体积)！");
                }
                if (StringUtils.isEmpty(sol.getExt$Item("calibsolweightunit"))) {
                    return;
//                    throw new AuthenticationServiceException("请输入：待标液称重量(体积)单位！");
                }
                if (StringUtils.isEmpty(sol.getCalibSolConstantVolume())) {
                    return;
//                    throw new AuthenticationServiceException("请输入：待标液定容至！");
                }
                //查询标准液浓度
                EmcSolLineBean solLine = solLineService.selectFirstByFilter(SearchFilter.instance().match("solId", solId).filter(MatchPattern.SEQ));
                if (ObjectUtils.isEmpty(solLine) || StringUtils.isEmpty(solLine.getPrepareConcent())) {
                    return;
//                    throw new AuthenticationServiceException("请输入：标准液浓度！");
                }

                List<EmcSolRecordBean> solRecordList = selectListByFilter(SearchFilter.instance().match("solId", solId).filter(MatchPattern.EQ));
                //排序
                CollectionUtils.sort(solRecordList, Comparator.comparing(EmcSolRecordBean::getTitrationNum));

                solRecordList.forEach(solRecord -> {
                    if (isCanCalc(solRecord.getSamplingVolume()) && isCanCalc(solRecord.getBlankFinalValue())
                            && isCanCalc(solRecord.getBlankStartValue()) && isCanCalc(solRecord.getStandardStartValue())
                            && isCanCalc(solRecord.getStandardFinalValue())) {
                        String samplingVolume = solRecord.getSamplingVolume();
                        String blankFinalValue = solRecord.getBlankFinalValue();
                        String blankStartValue = solRecord.getBlankStartValue();
                        String standardStartValue = solRecord.getStandardStartValue();
                        String standardFinalValue = solRecord.getStandardFinalValue();
                        // V1 标准溶液消耗体积 = 标准溶液终读 - 标准溶液初读
                        double v1 = ArithUtils.sub(standardFinalValue, standardStartValue);
                        // V2 空白中消耗的标准溶液消耗体积 = 空白溶液终读 - 空白溶液初读
                        double v2 = ArithUtils.sub(blankFinalValue, blankStartValue);
                        // m 取样重量或质量
                        double m = NumberUtils.parseDouble(sol.getTakeWeightOrVolume());
                        // CV Constant volume 定容体积
                        double CV = NumberUtils.parseDouble(sol.getCalibSolConstantVolume());
                        // SV samplingvolume 取样体积
                        double SV = NumberUtils.parseDouble(samplingVolume);
                        // C2 标准液浓度
                        double C2 = NumberUtils.parseDouble(solLine.getPrepareConcent());
                        //标准溶液终读
                        double FV = NumberUtils.parseDouble(standardFinalValue);
                        //系数
                        double CF = NumberUtils.parseDouble(sol.getExt$Item("coefficient"));
                        //标准溶液终读 - 空白溶液终读
                        double V3 = ArithUtils.sub(standardFinalValue, blankFinalValue);
                        // c 计算的标准液浓度
                        double c = 0;
                        double molarMass;
                        String script = sol.getScript();//公式
                        // 根据不同的公式，设置不同的计算规则
                        switch (script) {
                            case "C1"://c = (6 * SV * 1.66) / (V1 - V2)
                                c = ArithUtils.div(ArithUtils.mul(ArithUtils.mul(6d, SV), 1.66d), ArithUtils.sub(v1, v2));
                                break;
                            case "C3"://c = SV / ((V1 - V2) * 0.053)
                                c = ArithUtils.div(SV, ArithUtils.mul(ArithUtils.sub(v1, v2), 0.053d));
                                break;
                            case "C5":
                                break;
                            case "C6":
                                break;
                            case "C7":
                                //molarMass = 0.08138
                                //c = (SV * m) / (CV * (V1 - V2) * molarMass);
                                molarMass = 0.08138d;
                                c = ArithUtils.div(ArithUtils.mul(SV, m), ArithUtils.mul(ArithUtils.mul(CV, ArithUtils.sub(v1, v2)), molarMass));
                                break;
                            case "C8":
                                //molarMass = 204.22
                                //c = (m * 1000) / ((V1 - V2) * molarMass)
                                molarMass = 204.22d;
                                c = ArithUtils.div(ArithUtils.mul(m, 1000d), ArithUtils.mul(ArithUtils.sub(v1, v2), molarMass));
                                break;
                            case "C2":
                            case "C4":
                            case "C9":
                            case "C10":
                            case "C11":
                            case "C12":
                            case "C13":
                                //c = (SV * C2) / (V1 - V2)
                                c = ArithUtils.div(ArithUtils.mul(C2, SV), ArithUtils.sub(v1, v2));
                                break;
                            case "C14":
                                //c = (C2 * FV) / (SV)
                                c = ArithUtils.div(ArithUtils.mul(C2, FV), SV);
                                break;
                            case "C15":
                                //系数/(标液用量终读-空白用量终读)
                                //c = CF / V3
                                c = ArithUtils.div(CF, V3);
                                break;
                        }

                        //修约
                        solRecord.setCalibrationConcentration(NumberUtils.rounding(ObjectUtils.toString(c),revisionRule));
                    }
                });
                getDao().fastUpdate(solRecordList,"calibrationConcentration");

                //计算均值、相对偏差
                //记录按照四次一组分组
                List<List<EmcSolRecordBean>> solRecordGroupList = splitList(solRecordList, 4);

                List<EmcSolRecordBean> updateFourRelativeDeviationList = new ArrayList<>();
                solRecordGroupList.forEach(currSolRecordList -> {
                    //每个组分筛选出非无效的记录，且标定浓度都不为空，则可以计算每个组分的相对偏差
                    List<EmcSolRecordBean> notInvalidList = currSolRecordList.stream().filter(e -> !ObjectUtils.equals("/", e.getCalibrationConcentration()))
                            .collect(Collectors.toList());
                    if (notInvalidList.stream().noneMatch(e -> ObjectUtils.isEmpty(e.getCalibrationConcentration()))) {

                        calcFourRelativeDeviation(notInvalidList, revisionRule);
                        updateFourRelativeDeviationList.addAll(notInvalidList);
                    }
                });

                if (!CollectionUtils.isEmpty(updateFourRelativeDeviationList)) {
                    String relativeDeviation = calcRelativeDeviation(updateFourRelativeDeviationList, revisionRule);
                    updateFourRelativeDeviationList.forEach(e -> e.setEightRelativeDeviation(relativeDeviation));
                }

                getDao().fastUpdate(updateFourRelativeDeviationList,"eightRelativeDeviation","fourRelativeDeviation","averageConcentration");
            }
        }
    }

    private void calcFourRelativeDeviation(List<EmcSolRecordBean> notInvalidList, String revisionRule) {
        double maxValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).max().orElse(0d);
        double minValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).min().orElse(0d);
        double avgValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).average().orElse(0d);

        String avg = NumberUtils.rounding(avgValue + "", "EMC,4");

        String relativeDeviation;
        try {
            relativeDeviation = NumberUtils.rounding(ArithUtils.mul(ArithUtils.div(ArithUtils.sub(maxValue, minValue) + "", avg), 100D) + "", "FDA,2");
        }catch (ArithmeticException e){
            relativeDeviation = "0";
        }

        String finalRelativeDeviation = relativeDeviation;
        notInvalidList.forEach(e -> {
            e.setFourRelativeDeviation(finalRelativeDeviation);
            e.setAverageConcentration(avg);
        });
    }

    private String calcRelativeDeviation(List<EmcSolRecordBean> notInvalidList, String revisionRule) {
        double maxValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).max().orElse(0d);
        double minValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).min().orElse(0d);
        double avgValue = notInvalidList.stream().mapToDouble(e -> Double.parseDouble(e.getCalibrationConcentration())).average().getAsDouble();

        String avg = NumberUtils.rounding(avgValue + "", revisionRule);
        return NumberUtils.rounding(ArithUtils.mul(ArithUtils.div(ArithUtils.sub(maxValue, minValue) + "", avg), 100D) + "", "FDA,2");
    }

    private boolean isCanCalc(String value) {
        return !StringUtils.isEmpty(value) && !ObjectUtils.equals(value, "/");
    }

    private <T> List<List<T>> splitList(List<T> list, int splitSize) {
        //判断集合是否为空
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        //计算分割后的大小
        int maxSize = (list.size() + splitSize - 1) / splitSize;
        //开始分割
        return Stream.iterate(0, n -> n + 1)
                .limit(maxSize)
                .parallel()
                .map(a -> list.parallelStream().skip(a * splitSize).limit(splitSize).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());
    }

}
