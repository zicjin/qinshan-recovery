package com.sunwayworld.cloud.module.item.spcs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreCoefficientBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreCompFlushRespBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreCompSettingDataExRuleBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcDataSummaryBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionDescBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionPileBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionPointBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcLineBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcResultBean;
import com.sunwayworld.cloud.module.item.spcs.bean.LimisBean;
import com.sunwayworld.cloud.module.item.spcs.bean.OprateButtonBean;
import com.sunwayworld.cloud.module.item.spcs.service.CoreCoefficientService;
import com.sunwayworld.cloud.module.item.spcs.service.CoreSpcService;
import com.sunwayworld.cloud.module.item.spcs.support.constant.ExceptionPointRecognizer;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.DefectNumberControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.MeanByRangeControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.MeanByStandardControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.MeanRangeControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.MovingRangeControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.SingleValueControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.StandardDeviationControlChart;
import com.sunwayworld.cloud.module.item.spcs.support.constant.SpcHelper.UnqualifiedProductControlChart;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CoreSpcServiceImpl implements CoreSpcService {
   public static final String SUMMARY = "summary";
   @Autowired
   private CoreCoefficientService coreCoefficientService;

   @Transactional
   @Override
   public CoreCompFlushRespBean handleSpc(RestJsonWrapperBean wrapper) {
      List<String> groupDatas = wrapper.getBodyList().get(0);
      List<CoreCompFlushRespBean> groupResults = new ArrayList<>();

      for (String groupData : groupDatas) {
         groupResults.add(this.handleGroupData(groupData));
      }

      return this.mergeResult(groupResults);
   }

   private CoreCompFlushRespBean mergeResult(List<CoreCompFlushRespBean> groupResults) {
      int offset = 0;
      List<Object> groupSummary = new ArrayList<>();
      List<Double> groupData = new ArrayList<>();
      List<Double> groupAverage = new ArrayList<>();
      List<Double> groupStandardDeviation = new ArrayList<>();
      List<CoreSpcLineBean> groupLine = new ArrayList<>();
      List<CoreSpcExceptionPointBean> groupException = new ArrayList<>();
      List<CoreSpcExceptionPileBean> groupExceptionPile = new ArrayList<>();
      List<CoreSpcExceptionDescBean> groupOutlier = new ArrayList<>();
      TreeMap<String, List<String>> outlierMap = new TreeMap<>();

      for (CoreCompFlushRespBean groupResult : groupResults) {
         Object summary = groupResult.getAddition().get("summary");
         if (summary != null) {
            groupSummary.add(summary);
         }

         CoreSpcResultBean resultBean = groupResult.getResultBean();
         if (resultBean != null) {
            List<Double> data = resultBean.getData();
            int dataSize = data.size();
            groupData.addAll(data);
            groupAverage.add(resultBean.getAverage());
            groupStandardDeviation.add(resultBean.getStandardDeviation());
            List<CoreSpcLineBean> lines = resultBean.getLines();
            if (lines != null) {
               for (CoreSpcLineBean line : lines) {
                  line.setStartIndex(offset);
                  line.setEndIndex(offset + dataSize - 1);
               }

               groupLine.addAll(lines);
            }

            List<CoreSpcExceptionPointBean> exceptions = resultBean.getException();
            if (exceptions != null) {
               for (CoreSpcExceptionPointBean exception : exceptions) {
                  String tooltip = exception.getTooltip();
                  Double fixedAxisAxis = exception.getxAxis() + offset;
                  exception.setxAxis(fixedAxisAxis);
                  String pointStr = "[" + fixedAxisAxis.intValue() + ", " + NumberUtils.toDouble(exception.getyAxis(), 2) + "]";
                  if (outlierMap.containsKey(tooltip)) {
                     outlierMap.get(tooltip).add(pointStr);
                  } else {
                     ArrayList<String> list = new ArrayList<>();
                     list.add(pointStr);
                     outlierMap.put(tooltip, list);
                  }
               }

               groupException.addAll(exceptions);
            }

            List<CoreSpcExceptionPileBean> exceptionPile = resultBean.getExceptionPile();
            if (exceptions != null) {
               for (CoreSpcExceptionPileBean coreSpcExceptionPileBean : exceptionPile) {
                  coreSpcExceptionPileBean.setxAxis(coreSpcExceptionPileBean.getxAxis() + offset);
               }

               groupExceptionPile.addAll(exceptionPile);
            }

            groupOutlier = outlierMap.entrySet().stream().map(entry -> {
               CoreSpcExceptionDescBean descBean = new CoreSpcExceptionDescBean();
               String valueJoin = CollectionUtils.isEmpty(entry.getValue()) ? "-" : StringUtils.join(entry.getValue(), ", ");
               descBean.setText(entry.getKey() + ": " + valueJoin);
               return descBean;
            }).collect(Collectors.toList());
            offset += dataSize;
         }
      }

      CoreCompFlushRespBean mergeResult = new CoreCompFlushRespBean();
      if (!CollectionUtils.isEmpty(groupSummary)) {
         mergeResult.putAddition("summary", groupSummary);
      }

      if (!CollectionUtils.isEmpty(groupData)) {
         mergeResult.putAddition("data", groupData);
         mergeResult.putAddition("average", groupAverage);
         mergeResult.putAddition("standardDeviation", groupStandardDeviation);
         mergeResult.putAddition("lines", groupLine);
         mergeResult.putAddition("exception", groupException);
         mergeResult.putAddition("exceptionPile", groupExceptionPile);
         mergeResult.putAddition("outlier", groupOutlier);
      }

      return mergeResult;
   }

   private CoreCompFlushRespBean handleGroupData(String groupData) {
      JSONObject dataParam = JSON.parseObject(groupData);
      String type = dataParam.getString("type");
      LimisBean limisBean = JSON.parseObject(dataParam.getString("limisBean"), LimisBean.class);
      OprateButtonBean oprateButtonBean = JSON.parseObject(dataParam.getString("oprateButtonBean"), OprateButtonBean.class);
      Double adjustment = dataParam.getDouble("adjustment");
      Double sampleNumber = dataParam.getDouble("sampleNumber");
      Long sampleSizeData = dataParam.getLong("sampleSize");
      String subGroupType = dataParam.getString("subGroupType");
      String lcl = dataParam.getString("LCL");
      String ucl = dataParam.getString("UCL");
      List data = JSON.parseObject(dataParam.getString("dataList"), ArrayList.class);
      List<Double> dataList = new ArrayList<>();

      for (int i = 0; i < data.size(); i++) {
         dataList.add(Double.parseDouble(data.get(i).toString()));
      }

      List<Integer> removeOutlierDatas = JSON.parseObject(dataParam.getString("removeOutliers"), ArrayList.class);
      List<Integer> removeOutliers = new ArrayList<>();

      for (int j = 0; j < removeOutlierDatas.size(); j++) {
         removeOutliers.add(Integer.parseInt(removeOutlierDatas.get(j).toString()));
      }

      List exceptionRuleList = JSON.parseObject(dataParam.getString("exceptionRule"), ArrayList.class);
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList = new ArrayList<>();

      for (int m = 0; m < exceptionRuleList.size(); m++) {
         CoreCompSettingDataExRuleBean dvReportPageCompSettingDataExRuleDTO = JSON.parseObject(
            JSON.toJSONString(exceptionRuleList.get(m)), CoreCompSettingDataExRuleBean.class
         );
         exceptionRuleDataList.add(dvReportPageCompSettingDataExRuleDTO);
      }

      CoreCompFlushRespBean flushResp = new CoreCompFlushRespBean();
      if (!dataList.isEmpty()) {
         if (oprateButtonBean.getSummary() != null) {
            this.calculateSummary(dataList, removeOutliers, limisBean, type, flushResp, sampleSizeData);
         } else if (oprateButtonBean.getDetails() != null) {
            this.setDataDetails(dataList, type, flushResp, sampleSizeData, subGroupType);
         } else {
            CoreSpcResultBean firstMeasureResult = this.flushSpcComponent(
               dataList, type, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleNumber, sampleSizeData, subGroupType, ucl, lcl
            );
            flushResp.setResultBean(firstMeasureResult);
         }
      }

      return flushResp;
   }

   private void setDataDetails(List<Double> dataList, String type, CoreCompFlushRespBean flushResp, Long sampleSizeData, String subGroupType) {
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      switch (type) {
         case "spcSingle":
         case "spcRange":
         case "spcDefect":
         case "spcUnqualified":
            flushResp.setDataList(this.getSingleDetails(dataList));
            break;
         case "spcMeanRange":
         case "spcMeanByRange":
         case "spcMeanByStandard":
         case "spcStandard":
            flushResp.setDataList(this.getMeanDetails(dataList, sampleSize, subGroupType));
      }
   }

   private List<Map<String, Object>> getMeanDetails(List<Double> dataList, int sampleSize, String subGroupType) {
      List<Map<String, Object>> detailsList = new ArrayList<>();
      List<List<Double>> splitDataList = SpcHelper.getSubGroup(dataList, sampleSize, subGroupType);
      IntStream.range(0, splitDataList.size())
         .forEach(
            i -> {
               Map<String, Object> dataMap = new LinkedHashMap<>();
               List<Double> observedValueList = splitDataList.get(i);
               dataMap.put("index", i);
               IntStream.range(0, observedValueList.size()).forEach(j -> dataMap.put("data" + j, observedValueList.get(j)));
               double average = SpcHelper.calculateAverage(observedValueList);
               dataMap.put("average", this.setPrecision(average, 2));
               dataMap.put("standardDeviation", this.setPrecision(SpcHelper.calculateVariance(observedValueList, average), 2));
               dataMap.put(
                  "rang", this.setPrecision(SpcHelper.calculateRange(SpcHelper.calculateMax(observedValueList), SpcHelper.calculateMin(observedValueList)), 2)
               );
               detailsList.add(dataMap);
            }
         );
      return detailsList;
   }

   private List<Map<String, Object>> getSingleDetails(List<Double> dataList) {
      return IntStream.range(0, dataList.size()).mapToObj(i -> {
         Map<String, Object> dataMap = new LinkedHashMap<>();
         dataMap.put("index", i);
         dataMap.put("data", dataList.get(i));
         return dataMap;
      }).collect(Collectors.toList());
   }

   private CoreSpcResultBean flushSpcComponent(
      List<Double> dataList,
      String type,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Double sampleNumber,
      Long sampleSizeData,
      String subGroupType,
      String ucl,
      String lcl
   ) {
      switch (type) {
         case "spcSingle":
            return this.handleSingleValueControlChart(dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, ucl, lcl);
         case "spcRange":
            return this.handleMovingRangeControlChart(dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, ucl, lcl);
         case "spcMeanRange":
            return this.handleMeanRangeControlChart(
               dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleSizeData, subGroupType, ucl, lcl
            );
         case "spcMeanByRange":
            return this.handleMeanByRangeControlChart(
               dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleSizeData, subGroupType, ucl, lcl
            );
         case "spcMeanByStandard":
            return this.handleMeanByStandardControlChart(
               dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleSizeData, subGroupType, ucl, lcl
            );
         case "spcStandard":
            return this.handleStandardDeviationControlChart(
               dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleSizeData, subGroupType, ucl, lcl
            );
         case "spcDefect":
            return this.handleDefectNumberControlChart(dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, ucl, lcl);
         case "spcUnqualified":
            return this.handleUnqualifiedProductControlChart(dataList, limisBean, removeOutliers, exceptionRuleDataList, adjustment, sampleNumber, ucl, lcl);
         default:
            return null;
      }
   }

   private CoreSpcResultBean handleSingleValueControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      String ucl,
      String lcl
   ) {
      this.removeOutliers(dataList, removeOutliers);
      double average = SpcHelper.calculateAverage(dataList);
      double movingRange = SpcHelper.calculateMovingRangeAverage(dataList);
      double uclValue = StringUtils.isEmpty(ucl) ? SingleValueControlChart.getUcl(average, movingRange) : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl) ? SingleValueControlChart.getLcl(average, movingRange) : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(average, uclValue, lclValue, standardLimit));
      spcResult.setData(dataList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(dataList, average);
      this.appendExceptionData(
         dataList,
         average,
         standardDeviation,
         false,
         exceptionRuleDataList,
         spcResult,
         adjustment,
         String.valueOf(uclValue),
         String.valueOf(lclValue),
         standardLimit
      );
      spcResult.setAverage(average);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleMovingRangeControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      String ucl,
      String lcl
   ) {
      this.removeOutliers(dataList, removeOutliers);
      List<Double> rangeList = SpcHelper.calculateMovingRange(dataList);
      double movingRangeAverage = SpcHelper.calculateAverage(rangeList);
      double uclValue = StringUtils.isEmpty(ucl) ? MovingRangeControlChart.getUcl(movingRangeAverage) : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl) ? 0.0 : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(movingRangeAverage, uclValue, lclValue, standardLimit));
      double standardDeviation = SpcHelper.calculateStandardDeviation(rangeList, movingRangeAverage);
      this.appendExceptionData(rangeList, movingRangeAverage, standardDeviation, true, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      rangeList.add(0, 0.0);
      spcResult.setData(rangeList);
      spcResult.setAverage(movingRangeAverage);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleMeanRangeControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Long sampleSizeData,
      String subGroupType,
      String ucl,
      String lcl
   ) {
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      List<List<Double>> subgroupList = SpcHelper.getSubGroup(dataList, sampleSize, subGroupType);
      this.removeOutliers(subgroupList, removeOutliers);
      List<Double> rangeList = SpcHelper.calculateRangeOfSubgroup(subgroupList);
      double rangeAverage = SpcHelper.calculateAverage(rangeList);
      CoreCoefficientBean coefficient = this.coreCoefficientService.selectById((long)sampleSize);
      double uclValue = StringUtils.isEmpty(ucl) ? MeanRangeControlChart.getUcl(coefficient.getCoefficientD4(), rangeAverage) : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl) ? MeanRangeControlChart.getLcl(coefficient.getCoefficientD3(), rangeAverage) : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(rangeAverage, uclValue, lclValue, standardLimit));
      spcResult.setData(rangeList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(rangeList, rangeAverage);
      this.appendExceptionData(rangeList, rangeAverage, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(rangeAverage);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleMeanByRangeControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Long sampleSizeData,
      String subGroupType,
      String ucl,
      String lcl
   ) {
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      List<List<Double>> subgroupList = SpcHelper.getSubGroup(dataList, sampleSize, subGroupType);
      this.removeOutliers(subgroupList, removeOutliers);
      List<Double> averageList = SpcHelper.calculateAverageOfSubgroup(subgroupList);
      double average = averageList.stream().mapToDouble(t -> t).average().getAsDouble();
      double rangeAverage = SpcHelper.calculateAverageOfRangeOfSubgroup(subgroupList);
      CoreCoefficientBean coefficient = this.coreCoefficientService.selectById((long)sampleSize);
      double uclValue = StringUtils.isEmpty(ucl)
         ? MeanByRangeControlChart.getUcl(coefficient.getCoefficientA2(), average, rangeAverage)
         : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl)
         ? MeanByRangeControlChart.getLcl(coefficient.getCoefficientA2(), average, rangeAverage)
         : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(average, uclValue, lclValue, standardLimit));
      spcResult.setData(averageList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(averageList, average);
      this.appendExceptionData(averageList, average, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(average);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleMeanByStandardControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Long sampleSizeData,
      String subGroupType,
      String ucl,
      String lcl
   ) {
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      List<List<Double>> subgroupList = SpcHelper.getSubGroup(dataList, sampleSize, subGroupType);
      this.removeOutliers(subgroupList, removeOutliers);
      List<Double> averageList = SpcHelper.calculateAverageOfSubgroup(subgroupList);
      List<Double> standardList = SpcHelper.calculateStandardOfSubgroup(subgroupList);
      double average = SpcHelper.calculateAverage(averageList);
      double standardAverage = SpcHelper.calculateAverage(standardList);
      CoreCoefficientBean coefficient = this.coreCoefficientService.selectById((long)sampleSize);
      double uclValue = StringUtils.isEmpty(ucl)
         ? MeanByStandardControlChart.getUcl(coefficient.getCoefficientA3(), average, standardAverage)
         : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl)
         ? MeanByStandardControlChart.getLcl(coefficient.getCoefficientA3(), average, standardAverage)
         : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(average, uclValue, lclValue, standardLimit));
      spcResult.setData(averageList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(averageList, average);
      this.appendExceptionData(averageList, average, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(average);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleStandardDeviationControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Long sampleSizeData,
      String subGroupType,
      String ucl,
      String lcl
   ) {
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      List<List<Double>> subgroupList = SpcHelper.getSubGroup(dataList, sampleSize, subGroupType);
      this.removeOutliers(subgroupList, removeOutliers);
      List<Double> standardList = SpcHelper.calculateStandardOfSubgroup(subgroupList);
      double standardAverage = SpcHelper.calculateAverage(standardList);
      CoreCoefficientBean coefficient = this.coreCoefficientService.selectById((long)sampleSize);
      double uclValue = StringUtils.isEmpty(ucl)
         ? StandardDeviationControlChart.getUcl(coefficient.getCoefficientB3(), standardAverage)
         : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl)
         ? StandardDeviationControlChart.getLcl(coefficient.getCoefficientB4(), standardAverage)
         : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(standardAverage, uclValue, lclValue, standardLimit));
      spcResult.setData(standardList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(standardList, standardAverage);
      this.appendExceptionData(standardList, standardAverage, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(standardAverage);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleDefectNumberControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      String ucl,
      String lcl
   ) {
      this.removeOutliers(dataList, removeOutliers);
      double average = SpcHelper.calculateAverage(dataList);
      double uclValue = StringUtils.isEmpty(ucl) ? DefectNumberControlChart.getUcl(average) : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl) ? DefectNumberControlChart.getLcl(average) : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(average, uclValue, lclValue, standardLimit));
      spcResult.setData(dataList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(dataList, average);
      this.appendExceptionData(dataList, average, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(average);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private CoreSpcResultBean handleUnqualifiedProductControlChart(
      List<Double> dataList,
      LimisBean limisBean,
      List<Integer> removeOutliers,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      Double adjustment,
      Double sampleNumber,
      String ucl,
      String lcl
   ) {
      this.removeOutliers(dataList, removeOutliers);
      int batchSize = dataList.size();
      int sampleSizeOfEachBatch = Optional.ofNullable(sampleNumber).orElse(new Double(100.0)).intValue();

      for (Double data : dataList) {
         if (data.compareTo((double)sampleSizeOfEachBatch) > 0) {
            throw new ApplicationWarningException(I18nHelper.getMessage("CORE.MODULE.ITEM.SPCS.TIP.UNQUALIFIED_NUMBER_GREATER_THAN_SAMPLE_NUMBER"));
         }
      }

      double averageOfUnqualifiedProportion = SpcHelper.calculateAverageOfUnqualifiedProportion(dataList, batchSize, sampleSizeOfEachBatch);
      double nProportion = SpcHelper.calculateNProportion(averageOfUnqualifiedProportion, sampleSizeOfEachBatch);
      double uclValue = StringUtils.isEmpty(ucl)
         ? UnqualifiedProductControlChart.getUcl(averageOfUnqualifiedProportion, sampleSizeOfEachBatch)
         : NumberUtils.parseDouble(ucl);
      double lclValue = StringUtils.isEmpty(lcl)
         ? UnqualifiedProductControlChart.getLcl(averageOfUnqualifiedProportion, sampleSizeOfEachBatch)
         : NumberUtils.parseDouble(lcl);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      CoreSpcResultBean spcResult = new CoreSpcResultBean();
      spcResult.setLines(this.createLinesOnSpc(nProportion, uclValue, lclValue, standardLimit));
      spcResult.setData(dataList);
      double standardDeviation = SpcHelper.calculateStandardDeviation(dataList, nProportion);
      this.appendExceptionData(dataList, nProportion, standardDeviation, false, exceptionRuleDataList, spcResult, adjustment, ucl, lcl, standardLimit);
      spcResult.setAverage(nProportion);
      spcResult.setStandardDeviation(standardDeviation);
      return spcResult;
   }

   private void appendExceptionData(
      List<Double> dataList,
      double average,
      double standardDeviation,
      boolean isMovingRange,
      List<CoreCompSettingDataExRuleBean> exceptionRuleDataList,
      CoreSpcResultBean spcResult,
      Double adjustment,
      String ucl,
      String lcl,
      LimisBean standardLimit
   ) {
      if (exceptionRuleDataList != null) {
         List<CoreSpcExceptionPileBean> exceptionPileList = new ArrayList<>();
         List<CoreSpcExceptionDescBean> outlierList = new ArrayList<>();
         List<CoreSpcExceptionPointBean> exceptionList = new ArrayList<>();
         Set<String> recognizedRuleCache = new HashSet<>();

         for (int i = 0; i < exceptionRuleDataList.size(); i++) {
            CoreCompSettingDataExRuleBean exRule = exceptionRuleDataList.get(i);
            if (!this.isRecognized(exRule, recognizedRuleCache)) {
               int ruleIndex = i;
               List<String> tipList = new ArrayList<>();
               List<CoreSpcExceptionPointBean> exceptionPointList = this.recognizeExceptionPoints(
                  exRule, dataList, average, standardDeviation, ucl, lcl, standardLimit
               );
               exceptionPointList.forEach(
                  exceptionPoint -> {
                     if (isMovingRange) {
                        exceptionPoint.setxAxis(ArithUtils.add(exceptionPoint.getxAxis().intValue(), 1.0));
                     }

                     CoreSpcExceptionPileBean exceptionPile = this.createExceptionPile(exceptionPoint, exRule);
                     CoreSpcExceptionPointBean exPoint = null;
                     if (exceptionList.contains(exceptionPoint)) {
                        exPoint = exceptionList.get(exceptionList.indexOf(exceptionPoint));
                        exPoint.setTooltip(exPoint.getTooltip() + "<br/>" + exRule.getText());
                        exPoint.setPileNum(exPoint.getPileNum() + 1);
                     } else {
                        exPoint = new CoreSpcExceptionPointBean();
                        exPoint.setxAxis(exceptionPoint.getxAxis());
                        exPoint.setyAxis(exceptionPoint.getyAxis());
                        exPoint.setTooltip(exRule.getText());
                        exPoint.setPileNum(1);
                        exceptionList.add(exPoint);
                     }

                     exceptionPile.setSymbolOffset(
                        new int[]{0, (int)ArithUtils.mul(exPoint.getPileNum() - 1, ArithUtils.sub(-10.0, this.getAdjustment(adjustment)))}
                     );
                     exceptionPile.setRulesIndex(ruleIndex);
                     exceptionPileList.add(exceptionPile);
                     tipList.add(exceptionPoint.toString());
                  }
               );
               CoreSpcExceptionDescBean coreSpcExceptionDescBean = new CoreSpcExceptionDescBean();
               coreSpcExceptionDescBean.setRuleId(exRule.getRuleId());
               coreSpcExceptionDescBean.setColor(exRule.getColor());
               coreSpcExceptionDescBean.setText(exRule.getText() + ": " + (tipList.isEmpty() ? "-" : StringUtils.join(tipList, ", ")));
               outlierList.add(coreSpcExceptionDescBean);
            }
         }

         spcResult.setOutlier(outlierList);
         spcResult.setExceptionPile(exceptionPileList);
         spcResult.setException(exceptionList);
      }
   }

   private CoreSpcExceptionPileBean createExceptionPile(CoreSpcExceptionPointBean exceptionPoint, CoreCompSettingDataExRuleBean exRule) {
      CoreSpcExceptionPileBean exceptionPile = new CoreSpcExceptionPileBean();
      exceptionPile.setxAxis(exceptionPoint.getxAxis());
      exceptionPile.setTooltip(exRule.getText());
      exceptionPile.setColor(exRule.getColor());
      exceptionPile.setRuleId(exRule.getRuleId());
      return exceptionPile;
   }

   private double getAdjustment(Double adjustment) {
      return Optional.ofNullable(adjustment).orElse(0.0);
   }

   private List<CoreSpcExceptionPointBean> recognizeExceptionPoints(
      CoreCompSettingDataExRuleBean exRule, List<Double> dataList, double average, double standardDeviation, String ucl, String lcl, LimisBean standardLimit
   ) {
      String ruleValue = exRule.getValue();
      String var11 = exRule.getType();
      switch (var11) {
         case "twoThirds":
            return ExceptionPointRecognizer.recognizeTwoThirds(dataList, average, standardDeviation, NumberUtils.parseInt(ruleValue));
         case "fourFifths":
            return ExceptionPointRecognizer.recognizeFourFifths(dataList, average, standardDeviation, NumberUtils.parseInt(ruleValue));
         case "continuous":
            return ExceptionPointRecognizer.recognizeContinuous(dataList, average, NumberUtils.parseInt(ruleValue));
         case "eightLackC":
            return ExceptionPointRecognizer.recognizeEightLackC(dataList, average, standardDeviation, NumberUtils.parseInt(ruleValue));
         case "unilateral":
            return ExceptionPointRecognizer.recognizeUnilateral(dataList, average, NumberUtils.parseInt(ruleValue));
         case "alternate":
            return ExceptionPointRecognizer.recognizeAlternate(dataList, average, NumberUtils.parseInt(ruleValue));
         case "insideOfC":
            return ExceptionPointRecognizer.recognizeInsideOfC(dataList, average, standardDeviation, NumberUtils.parseInt(ruleValue));
         case "outOfBounds":
            return ExceptionPointRecognizer.recognizeOutOfBounds(dataList, average, standardDeviation, ucl, lcl, ruleValue);
         case "outOfStandBounds":
            return ExceptionPointRecognizer.recognizeOutOfStandBounds(dataList, standardLimit);
         case "outOfPermissions":
            return ExceptionPointRecognizer.recognizeOutOfPermissions(dataList, ucl, lcl);
         case "outOfCustomBounds":
            return ExceptionPointRecognizer.recognizeOutOfCustomBounds(dataList, ruleValue);
         case "nPointOutOfMStand":
            return ExceptionPointRecognizer.recognizeNPointOutOfMStand(dataList, average, standardDeviation, ruleValue);
         case "nPointMCountOutOfXStand":
            return ExceptionPointRecognizer.recognizeNPointMCountOutOfXStand(dataList, average, standardDeviation, ruleValue);
         default:
            return CollectionUtils.emptyList();
      }
   }

   private boolean isRecognized(CoreCompSettingDataExRuleBean exRule, Set<String> recognizedRuleCache) {
      String ruleKey = StringUtils.isBlank(exRule.getValue()) ? exRule.getType() : exRule.getType() + exRule.getValue();
      boolean recognized = recognizedRuleCache.contains(ruleKey);
      if (!recognized) {
         recognizedRuleCache.add(ruleKey);
      }

      return recognized;
   }

   private List<CoreSpcLineBean> createLinesOnSpc(double average, double uclValue, double lclValue, LimisBean standardLimit) {
      List<CoreSpcLineBean> lineList = new ArrayList<>();
      CoreSpcLineBean coreSpcLineBeanAverage = new CoreSpcLineBean();
      coreSpcLineBeanAverage.setyAxis(average);
      coreSpcLineBeanAverage.setTooltip(I18nHelper.getMessage("CORE.MODULE.ITEM.SPCS.TIP.AVERAGE_LINE") + "：" + average);
      coreSpcLineBeanAverage.setType("average");
      lineList.add(coreSpcLineBeanAverage);
      CoreSpcLineBean coreSpcLineBeanUrl = new CoreSpcLineBean();
      coreSpcLineBeanUrl.setyAxis(uclValue);
      coreSpcLineBeanUrl.setTooltip("UCL：" + uclValue);
      coreSpcLineBeanUrl.setType("UCL");
      lineList.add(coreSpcLineBeanUrl);
      CoreSpcLineBean coreSpcLineBeanUwl = new CoreSpcLineBean();
      double uwl = SpcHelper.calculateUwl(uclValue, average);
      coreSpcLineBeanUwl.setyAxis(uwl);
      coreSpcLineBeanUwl.setTooltip("UWL：" + uwl);
      coreSpcLineBeanUwl.setType("UWL");
      lineList.add(coreSpcLineBeanUwl);
      if (lclValue > 0.0) {
         CoreSpcLineBean coreSpcLineBeanLcl = new CoreSpcLineBean();
         coreSpcLineBeanLcl.setyAxis(lclValue);
         coreSpcLineBeanLcl.setTooltip("LCL：" + lclValue);
         coreSpcLineBeanLcl.setType("LCL");
         lineList.add(coreSpcLineBeanLcl);
         CoreSpcLineBean coreSpcLineBeanLwl = new CoreSpcLineBean();
         double lwl = SpcHelper.calculateLwl(lclValue, average);
         coreSpcLineBeanLwl.setyAxis(lwl);
         coreSpcLineBeanLwl.setTooltip("LWL：" + lwl);
         coreSpcLineBeanLwl.setType("LWL");
         lineList.add(coreSpcLineBeanLwl);
      }

      Optional.ofNullable(standardLimit).ifPresent(s -> {
         if (s.getUpperLimit() != null) {
            CoreSpcLineBean coreSpcLineBeanUpperLimit = new CoreSpcLineBean();
            coreSpcLineBeanUpperLimit.setyAxis(s.getUpperLimit());
            coreSpcLineBeanUpperLimit.setTooltip("标准上限：" + s.getUpperLimit());
            coreSpcLineBeanUpperLimit.setType("upperLimit");
            lineList.add(coreSpcLineBeanUpperLimit);
         }

         if (s.getLowerLimit() != null) {
            CoreSpcLineBean coreSpcLineBeanLowerLimit = new CoreSpcLineBean();
            coreSpcLineBeanLowerLimit.setyAxis(s.getLowerLimit());
            coreSpcLineBeanLowerLimit.setTooltip("标准下限：" + s.getLowerLimit());
            coreSpcLineBeanLowerLimit.setType("lowerLimit");
            lineList.add(coreSpcLineBeanLowerLimit);
         }
      });
      return lineList;
   }

   private void calculateSummary(
      List<Double> dataList, List<Integer> removeOutliers, LimisBean limisBean, String type, CoreCompFlushRespBean flushResp, Long sampleSizeData
   ) {
      this.removeOutliers(dataList, removeOutliers);
      LimisBean standardLimit = Optional.ofNullable(limisBean).orElse(null);
      switch (type) {
         case "spcSingle":
         case "spcRange":
         case "spcMeanRange":
         case "spcMeanByRange":
         case "spcMeanByStandard":
         case "spcStandard":
         case "spcDefect":
         case "spcUnqualified":
            flushResp.putAddition("summary", this.calculateDataSummaryOfSinleControlChart(dataList, standardLimit, sampleSizeData));
      }
   }

   private CoreSpcDataSummaryBean calculateDataSummaryOfSinleControlChart(List<Double> dataList, LimisBean standardLimit, Long sampleSizeData) {
      CoreSpcDataSummaryBean summary = new CoreSpcDataSummaryBean();
      summary.setSampleNumber(dataList.size());
      double average = SpcHelper.calculateAverage(dataList);
      summary.setAverage(this.setPrecision(average, 4));
      double variance = this.setPrecision(SpcHelper.calculateVariance(dataList, average), 16);
      summary.setVariance(this.setPrecision(variance, 4));
      double standardDeviation = SpcHelper.calculateStandardDeviation(variance);
      summary.setStandardDeviation(this.setPrecision(standardDeviation, 4));
      summary.setCoefficient(this.setPrecision(SpcHelper.calculateCoefficientOfVariation(standardDeviation, average), 4));
      summary.setFiveQuartile(this.setPrecision(SpcHelper.calculateQuantile(dataList, 0.05), 4));
      summary.setOneQuartile(this.setPrecision(SpcHelper.calculateQuantile(dataList, 0.25), 4));
      summary.setThreeQuartile(this.setPrecision(SpcHelper.calculateQuantile(dataList, 0.75), 4));
      summary.setNinetyFive(this.setPrecision(SpcHelper.calculateQuantile(dataList, 0.95), 4));
      summary.setKurtosis(this.setPrecision(SpcHelper.calculateKurtosis(dataList, average), 4));
      summary.setMedian(SpcHelper.calculateMedian(dataList));
      summary.setDeviation(this.setPrecision(SpcHelper.calculateMeanAbsoluteDeviation(dataList, average), 4));
      summary.setSkewness(this.setPrecision(SpcHelper.calculateSkewness(dataList, average, standardDeviation), 4));
      summary.setSum(SpcHelper.calculateSum(dataList));
      summary.setMin(SpcHelper.calculateMin(dataList));
      summary.setMax(SpcHelper.calculateMax(dataList));
      summary.setRange(SpcHelper.calculateRange(summary.getMax(), summary.getMin()));
      Double upperLimit = Optional.ofNullable(standardLimit).map(LimisBean::getUpperLimit).orElse(null);
      summary.setUpperLimit(upperLimit);
      Double lowerLimit = Optional.ofNullable(standardLimit).map(LimisBean::getLowerLimit).orElse(null);
      summary.setLowerLimit(lowerLimit);
      int sampleSize = NumberUtils.parseInt(Optional.ofNullable(sampleSizeData).orElse(0L));
      sampleSize = Math.max(sampleSize, 2);
      CoreCoefficientBean coefficient = this.coreCoefficientService.selectById((long)sampleSize);
      double movingRangeAverage = SpcHelper.calculateMovingRangeAverage(dataList);
      summary.setRangeAverage(this.setPrecision(movingRangeAverage, 4));
      double sigma = ArithUtils.div(movingRangeAverage, coefficient.getCoefficientLowerD2());
      summary.setSigma(this.setPrecision(sigma, 4));
      Double lowerSpecification = 0.0;
      Double Cpl = 0.0;
      if (null != lowerLimit) {
         lowerSpecification = SpcHelper.calculateLowerSpecification(lowerLimit, average, standardDeviation);
         summary.setLowerSpecification(String.valueOf(lowerSpecification));
         Cpl = SpcHelper.calculateLowerSpecification(lowerLimit, average, sigma);
      }

      Double upperSpecification = 0.0;
      Double Cpu = 0.0;
      if (null != upperLimit) {
         upperSpecification = SpcHelper.calculateUpperSpecification(upperLimit, average, standardDeviation);
         summary.setUpperSpecification(String.valueOf(upperSpecification));
         Cpu = SpcHelper.calculateUpperSpecification(upperLimit, average, sigma);
      }

      if (null != upperLimit && null != lowerLimit) {
         summary.setProcessCapability(String.valueOf(SpcHelper.calculateProcessCapability(upperLimit, lowerLimit, standardDeviation)));
         summary.setActualProcessCapacity(String.valueOf(SpcHelper.calculateActualProcessCapacity(upperSpecification, lowerSpecification)));
         summary.setProcessCapabilityIndex(String.valueOf(SpcHelper.calculateProcessCapabilityIndex(Cpu, Cpl)));
      } else if (null != lowerLimit) {
         summary.setActualProcessCapacity(String.valueOf(lowerSpecification));
         summary.setProcessCapabilityIndex(String.valueOf(Cpl));
      } else if (null != upperLimit) {
         summary.setActualProcessCapacity(String.valueOf(upperSpecification));
         summary.setProcessCapabilityIndex(String.valueOf(Cpu));
      }

      return summary;
   }

   private void removeOutliers(List<?> sourceList, List<Integer> removedOutlierList) {
      if (!CollectionUtils.isEmpty(removedOutlierList)) {
         List<Integer> indexList = new ArrayList<>();
         removedOutlierList.forEach(item -> indexList.add(item));
         indexList.stream().sorted();

         for (int i = indexList.size() - 1; i >= 0; i--) {
            if (i <= sourceList.size()) {
               sourceList.remove(indexList.get(i).intValue());
            }
         }
      }
   }

   private double setPrecision(double value) {
      return this.setPrecision(value, 2);
   }

   private Double setPrecision(double value, int scale) {
      return SpcHelper.setPrecision(value, scale);
   }
}
