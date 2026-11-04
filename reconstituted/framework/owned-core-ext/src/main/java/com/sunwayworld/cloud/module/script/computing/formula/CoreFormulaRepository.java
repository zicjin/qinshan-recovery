package com.sunwayworld.cloud.module.script.computing.formula;

import com.sunwayworld.cloud.module.script.computing.CoreResultComputingEngine;
import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamFormulaHelper;
import com.sunwayworld.framework.script.exeception.BshIgnoreException;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoreFormulaRepository {
   public static CoreResultBean getCurrent() {
      return GikamComputingDataHolder.getCurrentData();
   }

   public static Double get(String arg) {
      List<CoreResultBean> filterResultList = getResultList(arg);
      GikamFormulaHelper.setupRelevantData(filterResultList);
      if (filterResultList.isEmpty()) {
         throw new BshIgnoreException();
      } else {
         CoreResultBean result = filterResultList.get(0);
         if (result.getOriginalResult() != null && NumberUtils.isNumber(result.getOriginalResult())) {
            return NumberUtils.parseDouble(result.getOriginalResult());
         } else {
            throw new BshIgnoreException();
         }
      }
   }

   public static Double min(String... args) {
      List<String> argList = Arrays.asList(args);
      List<CoreResultBean> resultList = new ArrayList<>();

      for (String arg : argList) {
         resultList.addAll(getResultList(arg));
      }

      GikamFormulaHelper.setupRelevantData(resultList);
      return resultList.stream().mapToDouble(r -> NumberUtils.parseDouble(r.getOriginalResult())).min().orElse(0.0);
   }

   public static Double max(String... args) {
      List<String> argList = Arrays.asList(args);
      List<CoreResultBean> resultList = new ArrayList<>();

      for (String arg : argList) {
         resultList.addAll(getResultList(arg));
      }

      GikamFormulaHelper.setupRelevantData(resultList);
      return resultList.stream().mapToDouble(r -> NumberUtils.parseDouble(r.getOriginalResult())).max().orElse(0.0);
   }

   public static Double avg(String... args) {
      List<String> argList = Arrays.asList(args);
      List<CoreResultBean> filterResultList = new ArrayList<>();

      for (String arg : argList) {
         filterResultList.addAll(getResultList(arg));
      }

      List<CoreResultBean> resultList = filterResultList.stream()
         .filter(e -> !ObjectUtils.isEmpty(e.getOriginalResult()) && NumberUtils.isNumber(e.getOriginalResult()))
         .collect(Collectors.toList());
      GikamFormulaHelper.setupRelevantData(resultList);
      if (resultList.isEmpty()) {
         throw new BshIgnoreException();
      } else {
         Double sum = 0.0;

         for (CoreResultBean result : resultList) {
            sum = ArithUtils.add(sum.toString(), result.getOriginalResult());
         }

         return ArithUtils.div(sum, resultList.size());
      }
   }

   public static Double stdev(String... args) {
      List<String> argList = Arrays.asList(args);
      List<CoreResultBean> resultList = new ArrayList<>();

      for (String arg : argList) {
         resultList.addAll(getResultList(arg));
      }

      GikamFormulaHelper.setupRelevantData(resultList);
      if (resultList.isEmpty()) {
         return 0.0;
      } else {
         double avg = 0.0;

         for (CoreResultBean result : resultList) {
            avg = ArithUtils.add(avg, NumberUtils.parseDouble(result.getOriginalResult()));
         }

         avg = ArithUtils.div(avg, resultList.size());
         double sum = 0.0;

         for (CoreResultBean result : resultList) {
            double propertyValue = NumberUtils.parseDouble(result.getOriginalResult()) - avg;
            sum = ArithUtils.add(sum, ArithUtils.mul(propertyValue, propertyValue));
         }

         double value = ArithUtils.div(sum, resultList.size() - 1);
         return ArithUtils.sqrt(value, 2.0);
      }
   }

   public static List<CoreResultBean> getResultList(String arg) {
      if (StringUtils.isEmpty(arg)) {
         throw new FormulaException("CORE.MODULE.SCRIPT.RESULTS.EXCEPTION.REQUIRE_ARG");
      } else {
         CoreResultBean result = GikamComputingDataHolder.getCurrentData();
         CoreResultBean filterResult = new CoreResultBean();
         String[] args = arg.split("\\|");
         if (args.length == 1) {
            filterResult.setTestName(result.getTestName());
            setItemCondition(args[0], filterResult, result);
         } else {
            setTestCondition(args[0], filterResult, result);
            setItemCondition(args[1], filterResult, result);
         }

         List<CoreResultBean> dependencyResultList = CoreResultComputingEngine.getDependencyResultList();
         return dependencyResultList.stream().filter(r -> {
            Boolean validateFlag = true;
            validateFlag = r.getTestName().equals(filterResult.getTestName());
            if (!validateFlag) {
               return validateFlag;
            } else {
               validateFlag = r.getItemName().equals(filterResult.getItemName());
               if (!validateFlag) {
                  return validateFlag;
               } else {
                  if (filterResult.getRepeatNo() != null) {
                     validateFlag = r.getRepeatNo() == filterResult.getRepeatNo();
                  }

                  return validateFlag;
               }
            }
         }).collect(Collectors.toList());
      }
   }

   private static void setTestCondition(String testCondition, CoreResultBean filterResult, CoreResultBean result) {
      String[] testCondArray = testCondition.split(";");
      filterResult.setTestName(testCondArray[0]);
   }

   private static void setItemCondition(String itemCondition, CoreResultBean filterResult, CoreResultBean result) {
      String[] analysisCondArray = itemCondition.split(";");
      filterResult.setItemName(analysisCondArray[0]);
      String repeatNo = analysisCondArray[1];
      if ("#".equals(repeatNo)) {
         filterResult.setRepeatNo(result.getRepeatNo());
      } else if (!"*".equals(repeatNo)) {
         filterResult.setRepeatNo(NumberUtils.parseLong(repeatNo));
      }
   }
}
