package com.sunwayworld.cloud.module.equipt.usagerecord.service.impl;

import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptMonthUsageRateDTO;
import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.cloud.module.equipt.usagerecord.persistent.dao.CoreEquiptUsageRecordDao;
import com.sunwayworld.cloud.module.equipt.usagerecord.service.CoreEquiptUsageRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptUsageRecordServiceImpl implements CoreEquiptUsageRecordService {
   @Autowired
   private CoreEquiptUsageRecordDao equiptUsageRecordDao;

   public CoreEquiptUsageRecordDao getDao() {
      return this.equiptUsageRecordDao;
   }

   @Override
   public Map<String, Object> selectEquiptCategoryUsageRateData(RestJsonWrapperBean jsonWrapper) {
      MapperParameter paramMap = this.dealParam(jsonWrapper);
      int times = Integer.parseInt(paramMap.get("analysisDimension").toString());
      LocalDate now = LocalDate.now();
      int year = now.getYear() - 1;
      Map<String, Map<String, Object>> yearDataMap = new HashMap<>();
      Set<String> equiptCategorySet = new LinkedHashSet<>();
      List<String> xAxisList = new ArrayList<>();

      for (int i = 0; i < times; i++) {
         paramMap.put("startTime", year + "-01-01 00:00:00");
         paramMap.put("endTime", year + "-12-31 23:59:59");
         List<Map<String, Object>> usageTimeList = this.getDao().selectUsageTimeByYear(paramMap);
         Map<String, Object> tempMap = new HashMap<>();
         usageTimeList.forEach(f -> {
            String category = (String)f.get("EQUIPTCATEGORY");
            BigDecimal usageTime = (BigDecimal)f.get("USAGETIME");
            BigDecimal planUseTime = (BigDecimal)f.get("PLANUSETIME");
            double planTime = planUseTime.doubleValue();
            Long useTime = usageTime.longValue();
            equiptCategorySet.add(category);
            tempMap.put(category, useTime);
            tempMap.put("planUseTime", planTime);
         });
         xAxisList.add(String.valueOf(year));
         yearDataMap.put(String.valueOf(year), tempMap);
         year--;
      }

      Collections.reverse(xAxisList);
      return this.dealResultData(yearDataMap, xAxisList, equiptCategorySet);
   }

   @Override
   public Map<String, Object> selectUsageRateData(RestJsonWrapperBean jsonWrapper) {
      Map<String, String> equiptParamMap = jsonWrapper.getParamMap();
      String analysisDimension = equiptParamMap.get("analysisDimension");
      int times = Integer.parseInt(analysisDimension);
      List<Map<String, Object>> usageTimeList = this.getDao().selectUsageTimeByMonth();
      Map<String, Object> selectResultMap = this.dealTotalUsageRateData(usageTimeList);
      return this.calculateTotalUsageRate(times, selectResultMap);
   }

   @Override
   public Page<CoreEquiptMonthUsageRateDTO> selectMonthUsageRatePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      this.dealFilterParams(parameter, wrapper.getParamMap());
      String month = (String)parameter.get("month");
      String year = (String)parameter.get("year");
      String date = year + this.getMonth(month);
      parameter.put("date", date);
      List<Map<String, Object>> recordList = this.getDao().selectRecordByDate(parameter);
      List<CoreEquiptMonthUsageRateDTO> usageRateList = recordList.stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, CoreEquiptMonthUsageRateDTO.class))
         .collect(Collectors.toList());
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(0);
      usageRateList.forEach(f -> {
         double planWorkTime = f.getPlanUseTime() * 21.75 * 60.0;
         String rate = numberFormat.format(f.getUsageTime().longValue() / planWorkTime * 100.0) + "%";
         f.setRate(rate);
      });
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> recordList);
      Map<String, String> columnTypeMap = page.getColumnTypeMap();
      columnTypeMap.put("equiptname", "string");
      columnTypeMap.put("equiptcategory", "string");
      return new Pagination<>(page, usageRateList);
   }

   @Override
   public Map<String, Object> selectContributionData(RestJsonWrapperBean jsonWrapper) {
      Map<String, String> equiptParamMap = jsonWrapper.getParamMap();
      String analysisDimension = equiptParamMap.get("analysisDimension");
      int times = Integer.parseInt(analysisDimension);
      List<Map<String, Object>> amountList = this.getDao().selectAmountGroupByMonth();
      Map<String, Object> amountMap = new HashMap<>();
      amountList.forEach(f -> amountMap.put((String)f.get("UPTIME"), f.get("AMOUNT")));
      return this.formatterResult(times, amountMap);
   }

   @Override
   public Map<String, Object> selectEquiptCategoryContributionData(RestJsonWrapperBean jsonWrapper) {
      MapperParameter paramMap = this.dealParam(jsonWrapper);
      int times = Integer.parseInt(paramMap.get("analysisDimension").toString());
      LocalDate date = LocalDate.now();
      int year = date.getYear();
      paramMap.put("startYear", String.valueOf(year - times));
      paramMap.put("endYear", String.valueOf(year));
      List<Map<String, Object>> amountList = this.getDao().selectAmountGroupByEquiptCategory(paramMap);
      return this.getAmountResult(times, year, amountList);
   }

   @Override
   public Page<CoreEquiptUsageRecordBean> selectMonthContributionPagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      String month = (String)parameter.get("month");
      String year = (String)parameter.get("year");
      String date = year + this.getMonth(month);
      parameter.put("date", date);
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(() -> this.getDao().selectContributionByDate(parameter), rowBounds);
   }

   private Map<String, Object> getAmountResult(int times, int year, List<Map<String, Object>> amountList) {
      Set<String> equiptCategorySet = new HashSet<>();
      Map<String, Map<String, Object>> amountMap = new HashMap<>();
      amountList.forEach(f -> {
         String categoryx = (String)f.get("EQUIPTCATEGORY");
         equiptCategorySet.add(categoryx);
         String yearValue = (String)f.get("YEAR");
         Object amount = f.get("AMOUNT");
         if (amountMap.keySet().contains(yearValue)) {
            Map<String, Object> tempMap = amountMap.get(yearValue);
            tempMap.put(categoryx, amount);
         } else {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put(categoryx, amount);
            amountMap.put(yearValue, tempMap);
         }
      });
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("category", equiptCategorySet);
      List<String> xAxisList = new ArrayList<>();

      for (int i = 0; i < times; i++) {
         xAxisList.add(String.valueOf(--year));
      }

      Collections.reverse(xAxisList);

      for (String category : equiptCategorySet) {
         List<Object> categoryAmountList = new ArrayList<>();

         for (String xAxis : xAxisList) {
            Map<String, Object> categoryAmountMap = amountMap.get(xAxis);
            if (CollectionUtils.isEmpty(categoryAmountMap)) {
               categoryAmountList.add(0);
            } else {
               categoryAmountList.add(categoryAmountMap.getOrDefault(category, 0));
            }
         }

         resultMap.put(category, categoryAmountList);
      }

      resultMap.put("xAxis", xAxisList);
      return resultMap;
   }

   private MapperParameter dealParam(RestJsonWrapperBean jsonWrapper) {
      MapperParameter parameter = new MapperParameter();
      String equiptCategory = jsonWrapper.getParamValue("equiptCategory");
      if (!StringUtils.isEmpty(equiptCategory) && !"所有".equals(equiptCategory)) {
         parameter.put("equiptCategory", equiptCategory);
      }

      String analysisDimension = StringUtils.isEmpty(jsonWrapper.getParamValue("analysisDimension")) ? "3" : jsonWrapper.getParamValue("analysisDimension");
      parameter.put("analysisDimension", analysisDimension);
      return parameter;
   }

   private Map<String, Object> formatterResult(int times, Map<String, Object> amountMap) {
      LocalDate date = LocalDate.now();
      int year = date.getYear() - 1;
      List<String> yearList = new ArrayList<>();
      Map<String, Object> resultMap = new HashMap<>();

      for (int i = 0; i < times; i++) {
         yearList.add(String.valueOf(year));
         List<Object> tempList = new ArrayList<>();

         for (int j = 1; j < 13; j++) {
            String key;
            if (j < 10) {
               key = year + "-0" + j;
            } else {
               key = year + "-" + j;
            }

            BigDecimal amount = (BigDecimal)amountMap.get(key);
            if (amount == null) {
               tempList.add(0);
            } else {
               tempList.add(amount);
            }
         }

         resultMap.put(String.valueOf(year), tempList);
         year--;
      }

      Collections.reverse(yearList);
      resultMap.put("category", yearList);
      return resultMap;
   }

   private void dealFilterParams(MapperParameter parameter, Map<String, String> paramMap) {
      String filter = paramMap.get("qf");
      if (!StringUtils.isEmpty(filter) && "{}".equals(filter)) {
         Map<String, String> map = JsonUtils.parse(filter, Map.class);

         for (Entry<String, String> temp : map.entrySet()) {
            String key = temp.getKey();
            if (key.contains("_CISC")) {
               parameter.put(key.replaceAll("_CISC", ""), temp.getValue());
            }
         }
      }
   }

   private String getMonth(String month) {
      String[] monthSplits = month.split("月");
      String monthNum = monthSplits[0];
      return Integer.parseInt(monthNum) > 9 ? "-" + monthNum : "-0" + monthNum;
   }

   private Map<String, Object> calculateTotalUsageRate(int times, Map<String, Object> selectResultMap) {
      LocalDate date = LocalDate.now();
      int year = date.getYear() - 1;
      List<String> timeList = new ArrayList<>();
      Map<String, Object> resultMap = new HashMap<>();
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(0);

      for (int i = 0; i < times; i++) {
         timeList.add(String.valueOf(year));
         List<Object> tempList = new ArrayList<>();

         for (int j = 1; j < 13; j++) {
            String key;
            if (j < 10) {
               key = year + "-0" + j;
            } else {
               key = year + "-" + j;
            }

            BigDecimal usageTimeDecimal = (BigDecimal)selectResultMap.get(key);
            if (usageTimeDecimal == null) {
               tempList.add("0");
            } else {
               long usageTime = usageTimeDecimal.longValue();
               BigDecimal planTimeDecimal = (BigDecimal)selectResultMap.get(key + "_PLANUSETIME");
               double planUseTime = planTimeDecimal.doubleValue();
               planUseTime = planUseTime * 60.0 * 21.75;
               String monthRate = numberFormat.format(usageTime / planUseTime * 100.0);
               tempList.add(monthRate);
            }
         }

         resultMap.put(String.valueOf(year), tempList);
         year--;
      }

      Collections.reverse(timeList);
      resultMap.put("category", timeList);
      return resultMap;
   }

   private Map<String, Object> dealTotalUsageRateData(List<Map<String, Object>> usageTimeList) {
      Map<String, Object> resultMap = new HashMap<>();
      if (CollectionUtils.isEmpty(usageTimeList)) {
         return resultMap;
      } else {
         usageTimeList.forEach(f -> {
            String uptime = String.valueOf(f.get("UPTIME"));
            resultMap.put(uptime, f.get("USAGETIME"));
            resultMap.put(uptime + "_PLANUSETIME", f.get("PLANUSETIME"));
         });
         return resultMap;
      }
   }

   private Map<String, Object> dealResultData(Map<String, Map<String, Object>> yearDataMap, List<String> xAxisList, Set<String> equiptCategorySet) {
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("xAxis", xAxisList);
      resultMap.put("category", equiptCategorySet);
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(0);

      for (String category : equiptCategorySet) {
         List<String> usageRate = new ArrayList<>();

         for (String year : xAxisList) {
            Map<String, Object> usageTimeMap = yearDataMap.get(year);
            if (CollectionUtils.isEmpty(usageTimeMap)) {
               usageRate.add("0");
            } else {
               Long usageTime = (Long)usageTimeMap.get(category);
               double planUseTime = (Double)usageTimeMap.get("planUseTime");
               double workTime = planUseTime * 60.0 * 250.0;
               String rate = usageTime == null ? "0" : numberFormat.format(usageTime.longValue() / workTime * 100.0);
               usageRate.add(rate);
            }
         }

         resultMap.put(category, usageRate);
      }

      return resultMap;
   }
}
