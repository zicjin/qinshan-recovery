package com.sunwayworld.emc.framework.word;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2020年09月02日
 */
public class WordTemplateUtils {

    private static final String NULL_VALUE = "null";
    private static final String SUPERSCRIPT_REPLACED_VALUE = "%s×10<sup>%s</sup>";
    /** Word中 1cm 换算成 POI 中的 int 值 */
    private static final int CM_TO_INT_BASE_VALUE = 557;

    private static final String DEFAULT_DATE_RANGE_DELIMITER = " ~ ";

    /**
     * 获取日期，如果(采样/分析)日期不一致，则取最小日期和最大日期
     * @param dateList 日期列表
     * @return
     */
    public static String getDate(List<LocalDate> dateList) {
        List<LocalDate> notEmptyDateList = dateList.stream().filter(d -> !ObjectUtils.isEmpty(d)).collect(Collectors.toList());
        return notEmptyDateList.stream().min(Comparator.comparing(Function.identity())).map(date -> {
            String minSampleDateStr = DateTimeUtils.formatLocalDate(date, EmcConstant.TABLE_DATE_PATTERN);
            LocalDate maxSampleDate = notEmptyDateList.stream().max(Comparator.comparing(Function.identity())).get();
            String maxSampleDateStr = DateTimeUtils.formatLocalDate(maxSampleDate, EmcConstant.TABLE_DATE_PATTERN);
            if (StringUtils.equals(minSampleDateStr, maxSampleDateStr)) {
                return minSampleDateStr;
            }
            return minSampleDateStr + DEFAULT_DATE_RANGE_DELIMITER + maxSampleDateStr;
        }).orElse("");
    }

    public static String getDate(List<LocalDate> startDateList, List<LocalDate> endDateList) {
        List<LocalDate> dateList = new ArrayList<>(startDateList);
        dateList.addAll(endDateList);

        return getDate(dateList);
    }

    public static String getDate(List<LocalDate> dateList, boolean strict) {
        List<LocalDate> notEmptyDateList = dateList.stream().filter(d -> !ObjectUtils.isEmpty(d)).distinct().collect(Collectors.toList());

        LocalDate minDate = notEmptyDateList.stream().min(Comparator.comparing(Function.identity())).orElse(null);
        if (minDate == null) {
            return "";
        }

        LocalDate maxDate = notEmptyDateList.stream().max(Comparator.comparing(Function.identity())).orElse(minDate);
        int days = Period.between(minDate, maxDate).getDays();

        if (days == 0) {
            return DateTimeUtils.formatLocalDate(minDate, EmcConstant.TABLE_DATE_PATTERN);
        }

        // 如果是严格模式，并且日期不是连续的，则先将日期分割为区间段，再以顿号隔开
        if (strict) {
            return getDateRange(splitDateRange(notEmptyDateList));
        }

        return String.join(DEFAULT_DATE_RANGE_DELIMITER, DateTimeUtils.formatLocalDate(minDate, EmcConstant.TABLE_DATE_PATTERN), DateTimeUtils.formatLocalDate(maxDate, EmcConstant.TABLE_DATE_PATTERN));
    }

    /**
     * 分割日期区间
     * @param dateList 日期列表
     * @return
     */
    public static List<List<LocalDate>> splitDateRange(List<LocalDate> dateList) {
        List<LocalDate> notEmptyDateList = dateList.stream().filter(d -> !ObjectUtils.isEmpty(d)).distinct().collect(Collectors.toList());
        if (notEmptyDateList.isEmpty()) {
            return Collections.emptyList();
        }

        notEmptyDateList.sort(Comparator.comparing(Function.identity()));

        List<List<LocalDate>> dateRangeList = new ArrayList<>();
        int dateSize = notEmptyDateList.size();
        List<LocalDate> rangeList = new ArrayList<>();

        IntStream.range(0, dateSize).forEach(index -> {
            if ((index + 1) < dateSize && !notEmptyDateList.get(index).plusDays(1L).equals(notEmptyDateList.get(index + 1))) {
                rangeList.add(notEmptyDateList.get(index));
                dateRangeList.add(new ArrayList<>(rangeList));
                rangeList.clear();
                return;
            }

            rangeList.add(notEmptyDateList.get(index));
        });
        dateRangeList.add(rangeList);

        return dateRangeList;
    }

    public static String getDateRange(List<List<LocalDate>> dateRangeList) {
        List<String> dateStrList = new ArrayList<>(dateRangeList.size());
        dateRangeList.forEach(dateList -> dateStrList.add(getDate(dateList)));

        return String.join("、", dateStrList);
    }

    public static String getDateTime(List<LocalDateTime> dateTimeList) {
        List<LocalDateTime> notEmptyDateTimeList = dateTimeList.stream().filter(dateTime -> !ObjectUtils.isEmpty(dateTime)).collect(Collectors.toList());
        return notEmptyDateTimeList.stream().min(Comparator.comparing(Function.identity())).map(date -> {
            String minSampleDateStr = DateTimeUtils.formatLocalDateTime(date, EmcConstant.TABLE_DATE_PATTERN);
            LocalDateTime maxSampleDate = notEmptyDateTimeList.stream().max(Comparator.comparing(Function.identity())).get();
            String maxSampleDateStr = DateTimeUtils.formatLocalDateTime(maxSampleDate, EmcConstant.TABLE_DATE_PATTERN);
            if (StringUtils.equals(minSampleDateStr, maxSampleDateStr)) {
                return minSampleDateStr;
            }
            return minSampleDateStr + DEFAULT_DATE_RANGE_DELIMITER + maxSampleDateStr;
        }).orElse("");
    }

    /**
     * 获取最大最小值
     * @param resultList
     * @return
     */
    public static String getMinMaxValue(List<String> resultList) {
        List<String> notEmptyResultList = resultList.stream().filter(result -> !StringUtils.isEmpty(result)).collect(Collectors.toList());
        return notEmptyResultList.stream().min(Comparator.comparing(Function.identity())).map(minResult -> {
            String maxResult = notEmptyResultList.stream().max(Comparator.comparing(Function.identity())).get();
            if (StringUtils.equals(minResult, maxResult)) {
                return minResult;
            }
            return minResult + DEFAULT_DATE_RANGE_DELIMITER + maxResult;
        }).orElse("");
    }

    /**
     * 数据转换，将科学数转换为正常数值
     * @param number
     * @return
     */
    public static String convert(String number) {
        boolean scientificNumber = NumberUtils.isScientificNumber(number);
        if (scientificNumber) {
            return NumberUtils.formatNumber(new BigDecimal(number));
        }

        return number;
    }

    /**
     * 将科学计数法转换成Word中支持的上标形式，比如：
     *  3E-2 : 3x10<sup>-2</sup>
     *  3E2  : 3x10<sup>2</sup>
     *  3E+2 : 3x10<sup>2</sup>
     * @param number
     * @return
     */
    public static String convertScientificToWordFormat(String number) {
        if (!NumberUtils.isScientificNumber(number)) {
            return number;
        }

        String factorNumber = number.substring(0, number.toUpperCase().indexOf("E"));
        String superscript = number.substring(number.toUpperCase().indexOf("E") + 1).replace("+", "");

        return String.format(SUPERSCRIPT_REPLACED_VALUE, factorNumber, superscript);
    }

    public static void mapNullToString(Map<String, Object> map, String replaceText) {
        Set<String> set = map.keySet();
        if (!set.isEmpty()) {
            set.forEach(s -> map.putIfAbsent(s, replaceText));
        }
    }

    public static void listListNullToString(List<List<Object>> tableDataList, String replaceText) {
        tableDataList.forEach(list -> IntStream.range(0, list.size()).forEach(i -> {
                    if (list.get(i) == null || NULL_VALUE.equals(list.get(i))) {
                        list.set(i, replaceText);
                    }
                })
        );
    }

    public static <T> Map<String, Object> toMap(T bean) {
        Map<String, Object> result = new HashMap<>();

        if (bean == null) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) bean.getClass();

        BeanPropertyHelper.getBeanPropertyDescriptorList(type).forEach(p -> result.put(p.getName(), ReflectionUtils.invokeMethod(p.getReadMethod(), bean)));

        return result;
    }

    public static String getRevisedValue(String originalResult, String revisionRule, String resultLowLimit, EmcItemBean item, List<EmcItemRevisionBean> itemRevisionList) {
        if (item == null) {
            return originalResult;
        }

        String value = originalResult;
        if (!StringUtils.isEmpty(value) && NumberUtils.isNumber(value)) {
            double valueNum = NumberUtils.parseDouble(value);

            // 处理分段修约：
            // 1、根据值查询他所在区间范围内的分段修约
            for (EmcItemRevisionBean e : itemRevisionList) {
                // 下限符号
                String lowLimitSymbol = e.getLowLimitSymbol();
                // 下限
                String lowLimit = e.getLowLimit();
                // 上限符号
                String highLimitSymbol = e.getHighLimitSymbol();
                // 上限
                String highLimit = e.getHighLimit();
                if (!StringUtils.equals("/", lowLimitSymbol) && !StringUtils.isEmpty(lowLimit) && NumberUtils.isNumber(lowLimit)) {
                    double lowLimitNum = NumberUtils.parseDouble(lowLimit);
                    if (StringUtils.equals(">", lowLimitSymbol)) {
                        if (valueNum <= lowLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum < lowLimitNum) {
                            continue;
                        }
                    }
                }

                if (!StringUtils.equals("/", highLimitSymbol) && !StringUtils.isEmpty(highLimit) && NumberUtils.isNumber(highLimit)) {
                    double highLimitNum = NumberUtils.parseDouble(highLimit);
                    if (StringUtils.equals("<", lowLimitSymbol)) {
                        if (valueNum >= highLimitNum) {
                            continue;
                        }
                    } else {
                        if (valueNum > highLimitNum) {
                            continue;
                        }
                    }
                }
                revisionRule = e.getRevisionRule();
            }

            String revisionValue = value;
            if (!StringUtils.isEmpty(revisionRule) && !EmcConstant.REVISION_RULE_NONE_DESC.equals(revisionRule)) {
                revisionValue = NumberUtils.rounding(revisionValue, revisionRule);
            }

            String limitRevision = item.getLimitRevision();
            if (!StringUtils.isEmpty(limitRevision) && Constant.YES.equals(limitRevision) && !ObjectUtils.isEmpty(resultLowLimit)) {
                // 是否根据检出限修约为是, 判断检出低限是否为空
                String lowLimitStr = ObjectUtils.toString(resultLowLimit);
                int radix = getRadix(lowLimitStr);
                // 检出限修约值
                String lowLimitRevisionValue = NumberUtils.rounding(value, "ISO," + radix);
                // 比较检出限修约值和之前的计算修约值，取小数点位数小的值
                int isoRadix = getRadix(lowLimitRevisionValue);

                int revisionRadix = getRadix(revisionValue);
                if (isoRadix < revisionRadix) {
                    revisionValue = lowLimitRevisionValue;
                }
            }

            return revisionValue;
        }

        return value;
    }

    /**
     * 计算当前数值小数点后的位数
     */
    private static int getRadix(String number) {
        String num = number;

        // 如果数值采用科学计数法，则先转换成实际值，再取小数位
        boolean scientificNumber = NumberUtils.isScientificNumber(number);
        if (scientificNumber) {
            num = NumberUtils.formatNumber(new BigDecimal(number));
        }

        if (!num.contains(".")) {
            return 0;
        }

        return num.length() - num.indexOf(".") - 1;
    }

    /**
     * Word文档单位转换
     * @param cmValue 厘米
     * @return int值
     */
    public static int cm2Int(double cmValue) {
        return Double.valueOf(CM_TO_INT_BASE_VALUE * cmValue).intValue();
    }

    /**
     * Word文档厘米单位转换
     * @param cmValues
     * @return
     */
    public static List<Integer> cm2Ints(List<Double> cmValues) {
        return cmValues.stream().mapToInt(WordTemplateUtils::cm2Int).boxed().collect(Collectors.toList());
    }

    public static String handleOrderCategory(String orderCategory) {
        if (StringUtils.isEmpty(orderCategory)) {
            return orderCategory;
        }

        return orderCategory.replaceAll(PfConstant.DIOXIN_KEYWORD_2, "").replaceAll(PfConstant.DIOXIN_KEYWORD, "");
    }

    public static List<String> handleOrderCategory(List<String> orderCategories) {
        return orderCategories.stream().map(WordTemplateUtils::handleOrderCategory).collect(Collectors.toList());
    }

    /**
     * 按默认的科室顺序排序样品测试
     * @param orderTaskList
     */
    public static void sortedByAnalysisOrg(List<EmcOrderTaskBean> orderTaskList) {
        sortedByAnalysisOrg(orderTaskList, Arrays.asList(PfConstant.DEFAULT_SORTED_ORG_NAMES));
    }

    /**
     * 按指定的分析科室排序样品测试
     * @param orderTaskList
     * @param sortedOrgNameList
     */
    public static void sortedByAnalysisOrg(List<EmcOrderTaskBean> orderTaskList, List<String> sortedOrgNameList) {
        if (CollectionUtils.isEmpty(orderTaskList) || CollectionUtils.isEmpty(sortedOrgNameList) || orderTaskList.size() == 1) {
            return;
        }

        // 此处使用mobileId字段暂存科室排序码值，为了处理下面的双字段排序，不能使用ext$字段
        IntStream.range(0, sortedOrgNameList.size()).forEach(index -> orderTaskList.stream()
                .filter(task -> StringUtils.contains(task.getAnalysisOrgName(), sortedOrgNameList.get(index)))
                .forEach(task -> task.setMobileId(String.valueOf(index))));

        orderTaskList.stream().filter(task -> sortedOrgNameList.stream().noneMatch(orgName -> StringUtils.contains(task.getAnalysisOrgName(), orgName)))
                .forEach(task -> task.setMobileId(String.valueOf(sortedOrgNameList.size())));

        orderTaskList.stream().filter(task -> !NumberUtils.isNumber(task.getExt$Item("testItemSortNum"))).forEach(task -> task.setExt$Item("testItemSortNum", String.valueOf(Long.MAX_VALUE)));

        orderTaskList.stream().filter(task -> !NumberUtils.isNumber(task.getExt$Item("schemeSortNum"))).forEach(task -> task.setExt$Item("schemeSortNum", String.valueOf(Integer.MAX_VALUE)));

        orderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getMobileId)
                .thenComparingLong(task -> NumberUtils.parseLong(task.getExt$Item("testItemSortNum")))
                .thenComparingInt(task -> NumberUtils.parseInt(task.getExt$Item("schemeSortNum")))
                .thenComparingLong(EmcOrderTaskBean::getOrderId)
                .thenComparingLong(EmcOrderTaskBean::getTestMethodId));
    }

    /**
     * 按默认的科室顺序排序样品测试
     * @param reportResultList
     */
    public static void sortedResultByAnalysisOrg(List<EmcReportResultBean> reportResultList) {
        sortedResultByAnalysisOrg(reportResultList, Arrays.asList(PfConstant.DEFAULT_SORTED_ORG_NAMES));
    }

    /**
     * 按指定的分析科室排序样品测试
     * @param reportResultList
     * @param sortedOrgNameList
     */
    public static void sortedResultByAnalysisOrg(List<EmcReportResultBean> reportResultList, List<String> sortedOrgNameList) {
        if (CollectionUtils.isEmpty(reportResultList) || CollectionUtils.isEmpty(sortedOrgNameList) || reportResultList.size() == 1) {
            return;
        }

        // 此处使用repeatQty字段暂存科室排序码值，为了处理下面的双字段排序，不能使用ext$字段
        IntStream.range(0, sortedOrgNameList.size()).forEach(index -> reportResultList.stream()
                .filter(result -> StringUtils.contains(result.getExt$Item("analysisOrgName"), sortedOrgNameList.get(index)))
                .forEach(result -> result.setRepeatQty((long)index)));

        reportResultList.stream().filter(result -> sortedOrgNameList.stream().noneMatch(orgName -> StringUtils.contains(result.getExt$Item("analysisOrgName"), orgName)))
                .forEach(result -> result.setRepeatQty((long)sortedOrgNameList.size()));

        reportResultList.stream().filter(result -> !NumberUtils.isNumber(result.getExt$Item("testItemSortNum"))).forEach(result -> result.setExt$Item("testItemSortNum", String.valueOf(Long.MAX_VALUE)));

        reportResultList.stream().filter(result -> !NumberUtils.isNumber(result.getExt$Item("schemeSortNum"))).forEach(result -> result.setExt$Item("schemeSortNum", String.valueOf(Integer.MAX_VALUE)));

        reportResultList.sort(Comparator.comparing(EmcReportResultBean::getRepeatQty)
                .thenComparing(result -> NumberUtils.parseLong(result.getExt$Item("testItemSortNum")))
                .thenComparing(result -> NumberUtils.parseInt(result.getExt$Item("schemeSortNum")))
                .thenComparingLong(EmcReportResultBean::getOrderId)
                .thenComparingLong(EmcReportResultBean::getTestMethodId));
    }

    public static String convertUnit(Map<String, List<Pair<String, Double>>> unitConvertRegistry, String sourceUnit, String sourceResult, String targetUnit) {
        if (!NumberUtils.isNumber(sourceResult)) {
            return sourceResult;
        }

        List<Pair<String, Double>> convertList = unitConvertRegistry.get(sourceUnit);
        if (Objects.isNull(convertList) || convertList.isEmpty()) {
            return sourceResult;
        }

        Pair<String, Double> convertFactorPair = convertList.stream().filter(pair -> StringUtils.equals(pair.getFirst(), targetUnit) && Objects.nonNull(pair.getSecond())).findFirst().orElse(null);
        if (Objects.isNull(convertFactorPair)) {
            return sourceResult;
        }

        BigDecimal multiplyResult = new BigDecimal(sourceResult).multiply(new BigDecimal(String.valueOf(convertFactorPair.getSecond())));

        return multiplyResult.toPlainString();
    }

}
