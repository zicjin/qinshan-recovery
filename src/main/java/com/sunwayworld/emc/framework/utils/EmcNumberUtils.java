package com.sunwayworld.emc.framework.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年12月21日
 */
public class EmcNumberUtils {

    /**
     * 数值区间正则表达式
     */
    private static final Pattern NUMERICAL_RANGE_PATTERN = Pattern.compile("[\\[|\\(]\\S+,\\S+[\\)|\\]]");

    private static final int MAX_SEQ_NO = 9999;
    private static final int DEFAULT_SEQ_NO_LENGTH = 4;
    private static final char[] FIRST_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 计算当前数值小数点后的位数
     */
    public static int getRadix(String number) {
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
     * 判断数值是否在区间范围内
     * @param number
     * @param numRange
     * @return
     */
    public static boolean inNumRange(String number, String numRange) {
        if (!NumberUtils.isNumber(number)) {
            return false;
        }

        if (!isValidNumRange(numRange)) {
            return false;
        }

        String[] pairs = StringUtils.split(numRange, ",");
        double lowValue = NumberUtils.parseDouble(pairs[0].substring(1));
        double highValue = NumberUtils.parseDouble(pairs[1].substring(0, pairs[1].length() - 1));

        double resultValue = NumberUtils.parseDouble(number);
        boolean lowMatch = pairs[0].startsWith("[") ? resultValue >= lowValue : resultValue > lowValue;
        boolean highMatch = pairs[1].endsWith("]") ? resultValue <= highValue : resultValue < highValue;

        return lowMatch && highMatch;
    }

    /**
     * 判断是否为有效的数字区间范围
     * @param numRange 数字区间
     * @return boolean
     */
    public static boolean isValidNumRange(String numRange) {
        Matcher matcher = NUMERICAL_RANGE_PATTERN.matcher(numRange);
        if (!matcher.matches()) {
            return false;
        }

        return Arrays.stream(StringUtils.split(numRange.replaceAll("[\\[|\\(|\\)|\\]]", ""), ",")).allMatch(NumberUtils::isNumber);
    }

    /**
     * 生成连续序列号，超过 9999 后，自动从 A001 开始，超过 A999后，自动从 B001开始
     * @param startSeqNo 起始序列号
     * @param qty 数量
     * @return 新序列号集合
     */
    public static List<String> nextSeqNoList(String startSeqNo, int qty) {
        if (StringUtils.isEmpty(startSeqNo) || startSeqNo.length() != DEFAULT_SEQ_NO_LENGTH || qty < 1) {
            return Collections.emptyList();
        }

        String firstChar = startSeqNo.substring(0, 1);
        int firstCharIndex = String.valueOf(FIRST_CHARS).indexOf(firstChar);
        int firstNumber = firstCharIndex >= 9 ? firstCharIndex : Integer.valueOf(firstChar);

        String replacedNum = firstNumber + startSeqNo.substring(1);

        int startNum = Integer.valueOf(replacedNum);
        List<Integer> newSeqNumList = new ArrayList<>(qty);
        List<String> nextSeqNoList = new ArrayList<>(qty);
        IntStream.range(0, qty).forEach(index -> {
            int nextNum = startNum + 1 + index;
            while ((nextNum > MAX_SEQ_NO && nextNum % 1000 == 0) || newSeqNumList.contains(nextNum)) {
                nextNum += 1;
            }
            newSeqNumList.add(nextNum);

            if (nextNum <= MAX_SEQ_NO) {
                nextSeqNoList.add(StringUtils.leftPad(String.valueOf(nextNum), DEFAULT_SEQ_NO_LENGTH));
                return;
            }

            int charIndex = Integer.valueOf(String.valueOf(nextNum).substring(0, 2));
            nextSeqNoList.add(FIRST_CHARS[charIndex] + String.valueOf(nextNum).substring(2));
        });

        return nextSeqNoList;
    }
}
