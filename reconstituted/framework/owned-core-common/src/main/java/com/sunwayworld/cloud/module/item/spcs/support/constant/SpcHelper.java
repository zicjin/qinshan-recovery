package com.sunwayworld.cloud.module.item.spcs.support.constant;

import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpcHelper {
   public static double calculateAverage(List<Double> observedValueList) {
      return observedValueList.stream().collect(Collectors.averagingDouble(n -> n));
   }

   public static double calculateSum(List<Double> observedValueList) {
      return observedValueList.stream().mapToDouble(t -> t).sum();
   }

   public static double calculateMin(List<Double> observedValueList) {
      return observedValueList.stream().min(Comparator.comparing(Function.identity())).get();
   }

   public static double calculateMax(List<Double> observedValueList) {
      return observedValueList.stream().max(Comparator.comparing(Function.identity())).get();
   }

   public static double calculateRange(double max, double min) {
      return ArithUtils.sub(max, min);
   }

   public static double calculateVariance(List<Double> observedValueList) {
      double aver = calculateAverage(observedValueList);
      return calculateVariance(observedValueList, aver);
   }

   public static double calculateVariance(List<Double> observedValueList, double average) {
      int divisor = observedValueList.size() == 1 ? 1 : observedValueList.size() - 1;
      return ArithUtils.div(observedValueList.stream().map(t -> Math.pow(ArithUtils.sub(t, average), 2.0)).collect(Collectors.summingDouble(n -> n)), divisor);
   }

   public static double calculateCoefficientOfVariation(double standardDeviation, double average) {
      return ArithUtils.div(standardDeviation, average);
   }

   public static double calculateStandardDeviation(double variance) {
      return Math.sqrt(variance);
   }

   public static double calculateStandardDeviation(List<Double> observedValueList, double average) {
      return Math.sqrt(calculateVariance(observedValueList, average));
   }

   public static double calculateMovingRangeAverage(List<Double> observedValueList) {
      return calculateAverage(calculateMovingRange(observedValueList));
   }

   public static List<Double> calculateMovingRange(List<Double> observedValueList) {
      List<Double> rangeList = new ArrayList<>();

      for (int i = 0; i < observedValueList.size() - 1; i++) {
         rangeList.add(Math.abs(calculateRange(observedValueList.get(i + 1), observedValueList.get(i))));
      }

      return rangeList;
   }

   public static List<Double> calculateStandardOfSubgroup(List<List<Double>> subgroupList) {
      return subgroupList.stream().map(item -> calculateStandardDeviation(calculateVariance((List<Double>)item))).collect(Collectors.toList());
   }

   public static double calculateAverageOfRangeOfSubgroup(List<List<Double>> subgroupList) {
      return calculateAverage(calculateRangeOfSubgroup(subgroupList));
   }

   public static List<Double> calculateRangeOfSubgroup(List<List<Double>> subgroupList) {
      return subgroupList.stream().map(item -> calculateRange(calculateMax((List<Double>)item), calculateMin((List<Double>)item))).collect(Collectors.toList());
   }

   public static List<Double> calculateAverageOfSubgroup(List<List<Double>> subgroupList) {
      return subgroupList.stream().map(item -> calculateAverage((List<Double>)item)).collect(Collectors.toList());
   }

   public static double calculateAverageOfUnqualifiedProportion(List<Double> observedValueList, int batchSize, int sampleSizeOfEachBatch) {
      double sampleSizeOfUnqualifiedProduct = calculateSum(observedValueList);
      return calculateAverageOfUnqualifiedProportion(sampleSizeOfUnqualifiedProduct, batchSize, sampleSizeOfEachBatch);
   }

   public static double calculateAverageOfUnqualifiedProportion(double sampleSizeOfUnqualifiedProduct, int batchSize, int sampleSizeOfEachBatch) {
      int totalSampleSize = (int)ArithUtils.mul(sampleSizeOfEachBatch, batchSize);
      return calculateAverageOfUnqualifiedProportion(sampleSizeOfUnqualifiedProduct, totalSampleSize);
   }

   public static double calculateAverageOfUnqualifiedProportion(double sampleSizeOfUnqualifiedProduct, int totalSampleSize) {
      return ArithUtils.div(sampleSizeOfUnqualifiedProduct, totalSampleSize);
   }

   public static double calculateNProportion(List<Double> observedValueList, int batchSize, int sampleSizeOfEachBatch) {
      return ArithUtils.mul(sampleSizeOfEachBatch, calculateAverageOfUnqualifiedProportion(observedValueList, batchSize, sampleSizeOfEachBatch));
   }

   public static double calculateNProportion(double averageOfUnqualifiedProportion, int sampleSizeOfEachBatch) {
      return ArithUtils.mul(sampleSizeOfEachBatch, averageOfUnqualifiedProportion);
   }

   public static double calculateMedian(List<Double> observedValueList) {
      List<Double> sortedList = observedValueList.stream().sorted().collect(Collectors.toList());
      int size = sortedList.size();
      return sortedList.size() % 2 == 0 ? (sortedList.get(size / 2 - 1) + sortedList.get(size / 2)) / 2.0 : sortedList.get((size - 1) / 2);
   }

   public static double calculateMeanAbsoluteDeviation(List<Double> observedValueList, double average) {
      double dividend = observedValueList.stream().map(item -> Math.abs(ArithUtils.sub(item, average))).collect(Collectors.summingDouble(n -> n));
      return ArithUtils.div(dividend, observedValueList.size());
   }

   public static double calculateSkewness(List<Double> observedValueList, double average, double standardDeviation) {
      double dividend = observedValueList.stream()
         .map(item -> Math.pow(ArithUtils.div(ArithUtils.sub(item, average), standardDeviation), 3.0))
         .collect(Collectors.summingDouble(n -> n));
      return ArithUtils.div(dividend, observedValueList.size());
   }

   public static double calculateKurtosis(List<Double> observedValueList, double average) {
      double item = observedValueList.stream().map(t -> Math.pow(ArithUtils.sub(t, average), 4.0)).collect(Collectors.summingDouble(n -> n));
      double kurtosisDividend = ArithUtils.div(item, observedValueList.size());
      double kurtosisDivisor = Math.pow(kurtosisDividend, 2.0);
      if (0.0 == kurtosisDivisor) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.SPCS.TIP.CAN_NOT_CALC_KURTOSIS"));
      } else {
         return ArithUtils.sub(ArithUtils.div(kurtosisDividend, kurtosisDivisor), 3.0);
      }
   }

   public static double calculateQuantile(List<Double> observedValueList, double percentile) {
      List<Double> sortedList = observedValueList.stream().sorted().collect(Collectors.toList());
      double multiplication = ArithUtils.mul(percentile, sortedList.size());
      String[] split = Double.toString(multiplication).split("\\.");
      if (split.length > 1) {
         return sortedList.get(NumberUtils.parseInt(split[0]));
      } else {
         Double later = sortedList.get(Integer.parseInt(split[0]) - 1);
         Double fater = sortedList.get(Integer.parseInt(split[0]));
         return ArithUtils.div(ArithUtils.add(later, fater), 2.0);
      }
   }

   public static double calculateProcessCapability(double upperLimit, double lowerLimit, double standardDeviation) {
      return ArithUtils.div(ArithUtils.sub(upperLimit, lowerLimit), ArithUtils.mul(6.0, standardDeviation), 16);
   }

   public static double calculateLowerSpecification(double lowerLimit, double average, double standardDeviation) {
      return ArithUtils.div(ArithUtils.sub(average, lowerLimit), ArithUtils.mul(3.0, standardDeviation), 16);
   }

   public static double calculateUpperSpecification(double upperLimit, double average, double standardDeviation) {
      return ArithUtils.div(ArithUtils.sub(upperLimit, average), ArithUtils.mul(3.0, standardDeviation), 16);
   }

   public static double calculateActualProcessCapacity(double upperSpecification, double lowerSpecification) {
      return upperSpecification > lowerSpecification ? lowerSpecification : upperSpecification;
   }

   public static double calculateProcessCapabilityIndex(double upperSpecification, double lowerSpecification) {
      return upperSpecification > lowerSpecification ? lowerSpecification : upperSpecification;
   }

   public static double setPrecision(double value) {
      return setPrecision(value, 2);
   }

   public static double setPrecision(double value, int scale) {
      return new BigDecimal(value).setScale(scale, 4).doubleValue();
   }

   public static <T> List<List<T>> splitObservedValueList(List<T> valueList, int sampleSizeOfSubgroup) {
      int sizeOfSubgroup = (valueList.size() + sampleSizeOfSubgroup - 1) / sampleSizeOfSubgroup;
      return Stream.<Integer>iterate(0, n -> n + 1)
         .limit(sizeOfSubgroup)
         .parallel()
         .<List<T>>map(a -> valueList.parallelStream().skip(a * sampleSizeOfSubgroup).limit(sampleSizeOfSubgroup).collect(Collectors.toList()))
         .filter(b -> !b.isEmpty())
         .collect(Collectors.toList());
   }

   public static <T> List<List<T>> splitMoveRangeValueList(List<T> valueList, int sampleSizeOfSubgroup) {
      int sizeOfSubgroup = valueList.size() + 1 - sampleSizeOfSubgroup;
      return Stream.<Integer>iterate(0, n -> n + 1)
         .limit(sizeOfSubgroup)
         .parallel()
         .<List<T>>map(a -> valueList.parallelStream().skip(a.intValue()).limit(sampleSizeOfSubgroup).collect(Collectors.toList()))
         .filter(b -> !b.isEmpty())
         .collect(Collectors.toList());
   }

   public static <T> List<List<T>> getSubGroup(List<T> valueList, int sampleSizeOfSubgroup, String groupType) {
      return "moveAverage".equalsIgnoreCase(groupType)
         ? splitMoveRangeValueList(valueList, sampleSizeOfSubgroup)
         : splitObservedValueList(valueList, sampleSizeOfSubgroup);
   }

   public static double calculateUwl(double ucl, double average) {
      return ArithUtils.add(average, ArithUtils.mul(0.667, ArithUtils.sub(ucl, average)));
   }

   public static double calculateLwl(double lcl, double average) {
      return ArithUtils.sub(average, ArithUtils.mul(0.667, ArithUtils.sub(average, lcl)));
   }

   public static final class DefectNumberControlChart {
      public static double getUcl(double average) {
         return average > 0.0 ? ArithUtils.add(average, ArithUtils.mul(3.0, Math.sqrt(average))) : 0.0;
      }

      public static double getLcl(double average) {
         double lcl = 0.0;
         if (average > 0.0) {
            lcl = ArithUtils.sub(average, ArithUtils.mul(3.0, Math.sqrt(average)));
         }

         return lcl > 0.0 ? lcl : 0.0;
      }
   }

   public static final class MeanByRangeControlChart {
      public static double getUcl(double coefficientA2, double average, double rangeAverage) {
         return ArithUtils.add(average, ArithUtils.mul(coefficientA2, rangeAverage));
      }

      public static double getLcl(double coefficientA2, double average, double rangeAverage) {
         return ArithUtils.sub(average, ArithUtils.mul(coefficientA2, rangeAverage));
      }
   }

   public static final class MeanByStandardControlChart {
      public static double getUcl(double coefficientA3, double average, double standardAverage) {
         return ArithUtils.add(average, ArithUtils.mul(coefficientA3, standardAverage));
      }

      public static double getLcl(double coefficientA3, double average, double standardAverage) {
         return ArithUtils.sub(average, ArithUtils.mul(coefficientA3, standardAverage));
      }
   }

   public static final class MeanRangeControlChart {
      public static double getUcl(double coefficientD4, double rangeAverage) {
         return ArithUtils.mul(coefficientD4, rangeAverage);
      }

      public static double getLcl(double coefficientD3, double rangeAverage) {
         return ArithUtils.mul(coefficientD3, rangeAverage);
      }
   }

   public static final class MovingRangeControlChart {
      public static double getUcl(double movingRangeAverage) {
         return ArithUtils.mul(3.267, movingRangeAverage);
      }
   }

   public static final class SingleValueControlChart {
      public static double getUcl(double average, double movingRange) {
         return ArithUtils.add(average, ArithUtils.mul(2.66, movingRange));
      }

      public static double getLcl(double average, double movingRange) {
         return ArithUtils.sub(average, ArithUtils.mul(2.66, movingRange));
      }
   }

   public static final class StandardDeviationControlChart {
      public static double getUcl(double coefficientB3, double standardAverage) {
         return ArithUtils.mul(coefficientB3, standardAverage);
      }

      public static double getLcl(double coefficientB4, double standardAverage) {
         return ArithUtils.mul(coefficientB4, standardAverage);
      }
   }

   public static final class UnqualifiedProductControlChart {
      public static double getUcl(double averageOfUnqualifiedProportion, int sampleSizeOfEachBatch) {
         double nProportion = SpcHelper.calculateNProportion(averageOfUnqualifiedProportion, sampleSizeOfEachBatch);
         double segmentVaue = ArithUtils.mul(nProportion, ArithUtils.sub(1.0, averageOfUnqualifiedProportion));
         return segmentVaue > 0.0 ? ArithUtils.add(nProportion, ArithUtils.mul(3.0, Math.sqrt(segmentVaue))) : 0.0;
      }

      public static double getLcl(double averageOfUnqualifiedProportion, int sampleSizeOfEachBatch) {
         double lcl = 0.0;
         double nProportion = SpcHelper.calculateNProportion(averageOfUnqualifiedProportion, sampleSizeOfEachBatch);
         double segmentVaue = ArithUtils.mul(nProportion, ArithUtils.sub(1.0, averageOfUnqualifiedProportion));
         if (segmentVaue > 0.0) {
            lcl = ArithUtils.sub(nProportion, ArithUtils.mul(3.0, segmentVaue));
         }

         return lcl > 0.0 ? lcl : 0.0;
      }
   }
}
