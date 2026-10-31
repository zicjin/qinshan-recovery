package com.sunwayworld.emc.module.bp.info.infodata.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.emc.module.bp.info.infoconfig.service.EmcInfoConfigService;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcWaterResultBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcWaterResultDao;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataService;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcWaterResultService;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategoryIndexService;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategorySectionService;
import com.sunwayworld.emc.module.bp.info.infotmpl.bean.EmcInfoTmplFieldBean;
import com.sunwayworld.emc.module.bp.info.infotmpl.service.EmcInfoTmplFieldService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcWaterResultServiceImpl implements EmcWaterResultService {

    @Autowired
    private EmcWaterResultDao emcWaterResultDao;

    @Autowired
    private EmcInfoConfigService emcInfoConfigService;

    @Autowired
    private EmcCategoryIndexService emcCategoryIndexService;

    @Autowired
    private EmcCategorySectionService emcCategorySectionService;

    @Autowired
    private EmcInfoDataService emcInfoDataService;

    @Autowired
    private EmcInfoTmplFieldService infoTmplFieldService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcWaterResultDao getDao() {
        return emcWaterResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcWaterResultBean emcWaterResult = jsonWrapper.parseUnique(EmcWaterResultBean.class);
        emcWaterResult.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcWaterResult);
        return emcWaterResult.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void calculate(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcInfoDataBean> infoDataList = emcInfoDataService.selectList(parameter);
        Long infoConfigId = NumberUtils.parseLong(parameter.get("infoConfigId"));
        EmcInfoConfigBean infoConfig = emcInfoConfigService.selectById(infoConfigId);
        List<EmcInfoTmplFieldBean> fieldList = infoTmplFieldService.selectListByFilter(SearchFilter.instance().match("INFOTMPLID", infoConfig.getInfoTmplId()).filter(MatchPattern.EQ)).stream()
                .filter(e -> !ObjectUtils.isEmpty(e.getFieldTitle())).collect(Collectors.toList());
        String year = parameter.get(EmcConstant.INFO_DATA_CALCULATE_TYPE_YEAR).toString();

        List<EmcWaterResultBean> allWaterList = new ArrayList<>();
        List<EmcWaterResultBean> deleteWaterList = new ArrayList<>();

        // 按月计算
        if (EmcConstant.INFO_DATA_CALCULATE_TYPE_MONTH.equals(parameter.get("calType"))) {
            String month = parameter.get(EmcConstant.INFO_DATA_CALCULATE_TYPE_MONTH).toString();
            List<EmcWaterResultBean> deleteDayWaterList = selectListByFilter(SearchFilter.instance().match("YEAR", year).filter(MatchPattern.EQ).match("MONTH", month).filter(MatchPattern.EQ)
                    .match("TYPE", EmcConstant.INFO_DATA_CALCULATE_TYPE_DAY).filter(MatchPattern.EQ));
            List<EmcWaterResultBean> deleteMonthWaterList = selectListByFilter(SearchFilter.instance().match("YEAR", year).filter(MatchPattern.EQ).match("MONTH", month).filter(MatchPattern.EQ)
                    .match("TYPE", EmcConstant.INFO_DATA_CALCULATE_TYPE_MONTH).filter(MatchPattern.EQ));

            deleteWaterList.addAll(deleteDayWaterList);
            deleteWaterList.addAll(deleteMonthWaterList);

            // 日均值
            Collection<List<EmcInfoDataBean>> dayGroupList = infoDataList.stream().collect(Collectors.groupingBy(e -> e.getMonitorDate() + e.getExt$Item("monitorpointname"))).values();
            List<EmcWaterResultBean> dayWaterList = getWaterResultList(fieldList, dayGroupList, year, month, EmcConstant.INFO_DATA_CALCULATE_TYPE_DAY);
            // 月均值
            Collection<List<EmcInfoDataBean>> monthGroupList = infoDataList.stream().collect(Collectors.groupingBy(e -> e.getExt$Item("monitorpointname"))).values();
            List<EmcWaterResultBean> monthWaterList = getWaterResultList(fieldList, monthGroupList, year, month, EmcConstant.INFO_DATA_CALCULATE_TYPE_MONTH);
            allWaterList.addAll(dayWaterList);
            allWaterList.addAll(monthWaterList);
        }

        // 按年计算
        if (EmcConstant.INFO_DATA_CALCULATE_TYPE_YEAR.equals(parameter.get("calType"))) {

            List<EmcWaterResultBean> deleteHalfYearWaterList = selectListByFilter(
                    SearchFilter.instance().match("YEAR", year).filter(MatchPattern.EQ).match("TYPE", EmcConstant.INFO_DATA_CALCULATE_TYPE_HALF_YEAR).filter(MatchPattern.EQ));
            List<EmcWaterResultBean> deleteYearWaterList = selectListByFilter(
                    SearchFilter.instance().match("YEAR", year).filter(MatchPattern.EQ).match("TYPE", EmcConstant.INFO_DATA_CALCULATE_TYPE_YEAR).filter(MatchPattern.EQ));

            deleteWaterList.addAll(deleteHalfYearWaterList);
            deleteWaterList.addAll(deleteYearWaterList);

            // 半年均值
            LocalDate beginDate = DateTimeUtils.parseLocalDate(year + EmcConstant.INFO_DATA_BEGIN_DATE);
            LocalDate endDate = DateTimeUtils.parseLocalDate(year + EmcConstant.INFO_DATA_END_DATE);
            List<EmcInfoDataBean> halfYearInfoDataList = emcInfoDataService.selectListByFilter(SearchFilter.instance().match("INFOCONFIGID", infoConfigId).filter(MatchPattern.EQ)
                    .match("MONITORDATE", beginDate).filter(MatchPattern.DGOE).match("MONITORDATE", endDate).filter(MatchPattern.DLOE));
            Collection<List<EmcInfoDataBean>> halfYearGroupList = halfYearInfoDataList.stream().collect(Collectors.groupingBy(e -> e.getExt$Item("monitorpointname"))).values();
            List<EmcWaterResultBean> halfYearWaterList = getWaterResultList(fieldList, halfYearGroupList, year, null, EmcConstant.INFO_DATA_CALCULATE_TYPE_HALF_YEAR);
            // 年均值
            Collection<List<EmcInfoDataBean>> yearGroupList = infoDataList.stream().collect(Collectors.groupingBy(e -> e.getExt$Item("monitorpointname"))).values();
            List<EmcWaterResultBean> yearWaterList = getWaterResultList(fieldList, yearGroupList, year, null, EmcConstant.INFO_DATA_CALCULATE_TYPE_YEAR);
            allWaterList.addAll(halfYearWaterList);
            allWaterList.addAll(yearWaterList);
        }

        if (!allWaterList.isEmpty()) {
            getDao().deleteBy(deleteWaterList);

            // 插入数据
            getDao().insert(allWaterList);
        }
    }

    private List<EmcWaterResultBean> getWaterResultList(List<EmcInfoTmplFieldBean> fieldList, Collection<List<EmcInfoDataBean>> groupList, String year, String month, String type) {

        List<EmcWaterResultBean> waterList = new ArrayList<>();

        // 查询评价标准指标值
        List<EmcCategoryIndexBean> categoryIndexList = emcCategoryIndexService
                .selectListByFilter(SearchFilter.instance().match("INDEXVALUE", EmcConstant.INFO_DATA_CATEGORY_INDEX_VALUE).filter(MatchPattern.EQ), Order.asc("CATEGORY"));

        // 查询评价标准断面属性
        List<EmcCategorySectionBean> categorySectionList = emcCategorySectionService
                .selectListByFilter(SearchFilter.instance().match("INDEXVALUE", EmcConstant.INFO_DATA_CATEGORY_INDEX_VALUE).filter(MatchPattern.EQ), Order.asc("CATEGORY"));

        fieldList.stream().forEach(e -> {
            if (Constant.YES.equals(e.getTransform())) {
                groupList.stream().forEach(o -> {
                    String sumResult = "0";
                    for (EmcInfoDataBean m : o) {
                        Map<String, Object> infoDataMap = BeanUtils.toMap(m);
                        if (NumberUtils.isNumber(infoDataMap.get(StringUtils.lowerCase(e.getField())).toString())) {
                            String originResult = new BigDecimal(infoDataMap.get(StringUtils.lowerCase(e.getField())).toString()).stripTrailingZeros().toPlainString();
                            sumResult = new BigDecimal(sumResult).add(new BigDecimal(originResult)).toString();
                        }
                    }
                    String avgResult = new BigDecimal(sumResult).divide(new BigDecimal(o.size()), 2, BigDecimal.ROUND_HALF_UP).toString();
                    EmcWaterResultBean water = new EmcWaterResultBean();

                    water.setId(ApplicationContextHelper.getNextIdentity());
                    water.setInfoConfigId(o.get(0).getInfoConfigId());
                    water.setMonitorPointName(o.get(0).getExt$Item("monitorpointname"));
                    water.setYear(year);
                    if (EmcConstant.INFO_DATA_CALCULATE_TYPE_DAY.equals(type) || EmcConstant.INFO_DATA_CALCULATE_TYPE_MONTH.equals(type)) {
                        water.setMonth(month);
                    }
                    if (EmcConstant.INFO_DATA_CALCULATE_TYPE_DAY.equals(type)) {
                        water.setDay(String.valueOf(o.get(0).getMonitorDate().getDayOfMonth()));
                    }
                    water.setAverageResult(avgResult);

                    String waterQualityCategory = EmcConstant.INFO_DATA_CALCULATE_WATER_QUALITY_CATEGORY;
                    String pullyteIndex = null;

                    for (EmcCategoryIndexBean categoryIndex : categoryIndexList) {
                        if (!categoryIndex.getItemName().equals(e.getFieldTitle())) {
                            break;
                        }
                        if (StringUtils.lowerCase(e.getFieldTitle()).contains(EmcConstant.INFO_DATA_CALCULATE_PH)) {
                            if (NumberUtils.isNumber(categoryIndex.getLowLimit()) && NumberUtils.isNumber(categoryIndex.getHighLimit())
                                    && NumberUtils.toDouble(categoryIndex.getLowLimit()) <= NumberUtils.toDouble(avgResult)
                                    && NumberUtils.toDouble(categoryIndex.getHighLimit()) >= NumberUtils.toDouble(avgResult)) {
                                waterQualityCategory = categoryIndex.getExt$Item("codename");
                                if (!ObjectUtils.isEmpty(categoryIndex.getDetectLimit()) && NumberUtils.toDouble(avgResult) < categoryIndex.getDetectLimit()) {
                                    pullyteIndex = new BigDecimal(categoryIndex.getDetectLimit()).divide(new BigDecimal(categoryIndex.getHighLimit()), 2, BigDecimal.ROUND_HALF_UP).toString();
                                } else {
                                    pullyteIndex = new BigDecimal(avgResult).divide(new BigDecimal(categoryIndex.getHighLimit()), 2, BigDecimal.ROUND_HALF_UP).toString();
                                }
                                break;
                            }
                        } else if (EmcConstant.INFO_DATA_CALCULATE_O.equals(e.getFieldTitle())) {
                            if (NumberUtils.isNumber(categoryIndex.getLowLimit()) && NumberUtils.toDouble(categoryIndex.getLowLimit()) <= NumberUtils.toDouble(avgResult)) {
                                waterQualityCategory = categoryIndex.getExt$Item("codename");
                            }
                        } else {
                            if (NumberUtils.isNumber(categoryIndex.getHighLimit()) && NumberUtils.toDouble(categoryIndex.getHighLimit()) >= NumberUtils.toDouble(avgResult)) {
                                waterQualityCategory = categoryIndex.getExt$Item("codename");
                                if (!ObjectUtils.isEmpty(categoryIndex.getDetectLimit()) && NumberUtils.toDouble(avgResult) < categoryIndex.getDetectLimit()) {
                                    pullyteIndex = new BigDecimal(categoryIndex.getDetectLimit()).divide(new BigDecimal(categoryIndex.getHighLimit()), 2, BigDecimal.ROUND_HALF_UP).toString();
                                } else {
                                    pullyteIndex = new BigDecimal(avgResult).divide(new BigDecimal(categoryIndex.getHighLimit()), 2, BigDecimal.ROUND_HALF_UP).toString();
                                }
                            }
                        }
                    }

                    String exceedStandard = "0";
                    for (EmcCategorySectionBean categorySection : categorySectionList) {
                        if (categorySection.getMonitorPointName().equals(water.getMonitorPointName()) && categorySection.getExt$Item("itemname").equals(e.getFieldTitle())) {
                            if (StringUtils.lowerCase(e.getFieldTitle()).contains(EmcConstant.INFO_DATA_CALCULATE_PH)) {
                                if (NumberUtils.isNumber(categorySection.getExt$Item("lowlimit")) && NumberUtils.isNumber(categorySection.getExt$Item("hightlimit"))
                                        && NumberUtils.toDouble(categorySection.getExt$Item("lowlimit")) <= NumberUtils.toDouble(avgResult)
                                        && NumberUtils.toDouble(categorySection.getExt$Item("hightlimit")) >= NumberUtils.toDouble(avgResult)) {
                                    exceedStandard = "1";
                                }
                            } else if (EmcConstant.INFO_DATA_CALCULATE_O.equals(e.getFieldTitle())) {
                                if (NumberUtils.isNumber(categorySection.getExt$Item("lowlimit")) && NumberUtils.toDouble(categorySection.getExt$Item("lowlimit")) <= NumberUtils.toDouble(avgResult)) {
                                    exceedStandard = "1";
                                }
                            } else {
                                if (NumberUtils.isNumber(categorySection.getExt$Item("hightlimit"))
                                        && NumberUtils.toDouble(categorySection.getExt$Item("hightlimit")) >= NumberUtils.toDouble(avgResult)) {
                                    exceedStandard = "1";
                                }
                            }

                        }
                    }

                    water.setWaterQualityCategory(waterQualityCategory); // 水质类别
                    water.setPullyteIndex(pullyteIndex); // 污染指数
                    water.setExceedStandard(exceedStandard); // 是否超标
                    String overMulTime = "0";
                    if (!StringUtils.isBlank(pullyteIndex)) {
                        BigDecimal result = new BigDecimal(pullyteIndex).subtract(new BigDecimal(1));
                        overMulTime = result.compareTo(new BigDecimal(0)) >= 0 ? result.toString() : "0";
                    }
                    water.setOverMulTime(overMulTime); // 超标倍数

                    String eutrophication = null;
                    if(NumberUtils.toDouble(avgResult) > 0){
                        if (EmcConstant.INFO_DATA_CALCULATE_CHLA.equals(e.getFieldTitle())) {
                            eutrophication = new BigDecimal(10 * (2.5 + 1.086 * Math.log(NumberUtils.toDouble(avgResult)))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        if (EmcConstant.INFO_DATA_CALCULATE_TP.equals(e.getFieldTitle())) {
                            eutrophication = new BigDecimal(10 * (9.436 + 1.624 * Math.log(NumberUtils.toDouble(avgResult)))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        if (EmcConstant.INFO_DATA_CALCULATE_TN.equals(e.getFieldTitle())) {
                            eutrophication = new BigDecimal(10 * (5.453 + 1.694 * Math.log(NumberUtils.toDouble(avgResult)))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        if (EmcConstant.INFO_DATA_CALCULATE_SD.equals(e.getFieldTitle())) {
                            eutrophication = new BigDecimal(10 * (5.118 - 1.94 * Math.log(NumberUtils.toDouble(avgResult)))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                        if (EmcConstant.INFO_DATA_CALCULATE_CODMN.equals(e.getFieldTitle())) {
                            eutrophication = new BigDecimal(10 * (0.109 + 2.661 * Math.log(NumberUtils.toDouble(avgResult)))).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                        }
                    }
                    water.setEutrophication(eutrophication); // 富营养指数
                    water.setItemAliasName(e.getFieldTitle());
                    water.setType(type);
                    water.setMonitorDate(o.get(0).getMonitorDate());
                    ;
                    waterList.add(water);
                });
            }
        });
        return waterList;
    }
}
