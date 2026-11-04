/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.support.constant;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionPointBean;
import com.sunwayworld.cloud.module.item.spcs.bean.LimisBean;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class ExceptionPointRecognizer {
    public static List<CoreSpcExceptionPointBean> recognizeTwoThirds(List<Double> dataList, double average, double standardDeviation, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> twoThirdsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue) || standardDeviation == 0.0) {
            return twoThirdsList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (2.0 < Math.abs(result) && Math.abs(result) < 3.0) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < ruleValue - 1 || !ExceptionPointRecognizer.isSameside(tempList)) continue;
            twoThirdsList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return twoThirdsList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeFourFifths(List<Double> dataList, double average, double standardDeviation, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> fourFifthsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue) || standardDeviation == 0.0) {
            return fourFifthsList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) > 1.0) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < ruleValue - 1 || !ExceptionPointRecognizer.isSameside(tempList)) continue;
            fourFifthsList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return fourFifthsList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeContinuous(List<Double> dataList, double average, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> continuousList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue)) {
            return continuousList;
        }
        int dataGrowthNum = 0;
        int dataReduceNum = 0;
        for (int i = 1; i < dataList.size(); ++i) {
            double data = dataList.get(i);
            if (dataList.get(i - 1).compareTo(data) < 0) {
                ++dataGrowthNum;
                dataReduceNum = 0;
            } else if (dataList.get(i - 1).compareTo(data) > 0) {
                dataGrowthNum = 0;
                ++dataReduceNum;
            }
            if (dataGrowthNum < ruleValue - 1 && dataReduceNum < ruleValue - 1) continue;
            continuousList.add(ExceptionPointRecognizer.createExceptionPoint(i, data));
        }
        return continuousList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeEightLackC(List<Double> dataList, double average, double standardDeviation, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> eightLackCList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue) || standardDeviation == 0.0) {
            return eightLackCList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) > 1.0) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < ruleValue || ExceptionPointRecognizer.isSameside(tempList)) continue;
            eightLackCList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return eightLackCList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeUnilateral(List<Double> dataList, double average, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> unilateralList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue)) {
            return unilateralList;
        }
        int dataUpNum = 0;
        int dataDownNum = 0;
        for (int i = 0; i < dataList.size(); ++i) {
            if (dataList.get(i).compareTo(average) > 0) {
                ++dataUpNum;
                dataDownNum = 0;
            } else if (dataList.get(i).compareTo(average) < 0) {
                dataUpNum = 0;
                ++dataDownNum;
            }
            if (dataUpNum < ruleValue && dataDownNum < ruleValue) continue;
            unilateralList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return unilateralList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeAlternate(List<Double> dataList, double average, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> fourteenAlternationMap = new ArrayList<CoreSpcExceptionPointBean>();
        if (ruleValue <= 3 && !ExceptionPointRecognizer.checkDataList(dataList, ruleValue)) {
            return fourteenAlternationMap;
        }
        int fourteenAlternationNum = 0;
        for (int i = 2; i < dataList.size(); ++i) {
            double data = dataList.get(i);
            fourteenAlternationNum = dataList.get(i - 1).compareTo(dataList.get(i - 2)) > 0 && dataList.get(i - 1).compareTo(data) > 0 || dataList.get(i - 1).compareTo(dataList.get(i - 2)) < 0 && dataList.get(i - 1).compareTo(data) < 0 ? ++fourteenAlternationNum : 0;
            if (fourteenAlternationNum < ruleValue - 2) continue;
            fourteenAlternationMap.add(ExceptionPointRecognizer.createExceptionPoint(i, data));
        }
        return fourteenAlternationMap;
    }

    public static List<CoreSpcExceptionPointBean> recognizeInsideOfC(List<Double> dataList, double average, double standardDeviation, int ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> insideOfCList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue) || standardDeviation == 0.0) {
            return insideOfCList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) < 1.0) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < ruleValue || ExceptionPointRecognizer.isSameside(tempList)) continue;
            insideOfCList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return insideOfCList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfBounds(List<Double> dataList, double average, double standardDeviation, String ucl, String lcl, String ruleValue) {
        float times = NumberUtils.parseFloat(ruleValue);
        ArrayList<CoreSpcExceptionPointBean> outOfBoundsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, 1)) {
            return outOfBoundsList;
        }
        double uclValue = ArithUtils.add(average, ArithUtils.mul(standardDeviation, times));
        if (!StringUtils.isEmpty(ucl)) {
            double customUclValue = NumberUtils.parseDouble(ucl);
            double customStand = ArithUtils.div(ArithUtils.sub(customUclValue, average), 3.0);
            uclValue = ArithUtils.add(average, ArithUtils.mul(customStand, times));
        }
        double lclValue = ArithUtils.sub(average, ArithUtils.mul(standardDeviation, times));
        if (!StringUtils.isEmpty(lcl)) {
            double customLclValue = NumberUtils.parseDouble(lcl);
            double customStand = ArithUtils.div(ArithUtils.sub(average, customLclValue), 3.0);
            lclValue = ArithUtils.sub(average, ArithUtils.mul(customStand, times));
        }
        for (int i = 0; i < dataList.size(); ++i) {
            double data = dataList.get(i);
            if (!(data > uclValue) && !(data < lclValue)) continue;
            outOfBoundsList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return outOfBoundsList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfBounds(List<Double> dataList, double average, double standardDeviation) {
        return ExceptionPointRecognizer.recognizeOutOfBounds(dataList, average, standardDeviation, 3);
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfBounds(List<Double> dataList, double average, double standardDeviation, int times) {
        ArrayList<CoreSpcExceptionPointBean> outOfBoundsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, 1) || standardDeviation == 0.0) {
            return outOfBoundsList;
        }
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (!(Math.abs(result) > Double.valueOf(times))) continue;
            outOfBoundsList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return outOfBoundsList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeOneTwiceSd4Lj(List<Double> dataList, double average, double standardDeviation) {
        return ExceptionPointRecognizer.recognizeOutOfBounds(dataList, average, standardDeviation, 2);
    }

    public static List<CoreSpcExceptionPointBean> recognizeOneTripleSd4Lj(List<Double> dataList, double average, double standardDeviation) {
        return ExceptionPointRecognizer.recognizeOutOfBounds(dataList, average, standardDeviation, 3);
    }

    public static List<CoreSpcExceptionPointBean> recognizeTwoTwiceSd4Lj(List<Double> dataList, double average, double standardDeviation) {
        return ExceptionPointRecognizer.recognizeTwoTwiceSd4Lj(dataList, average, standardDeviation, 2, 2);
    }

    public static List<CoreSpcExceptionPointBean> recognizeTwoTwiceSd4Lj(List<Double> dataList, double average, double standardDeviation, int ruleValue, int times) {
        ArrayList<CoreSpcExceptionPointBean> exPointList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, ruleValue) || standardDeviation == 0.0) {
            return exPointList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) > (double)times) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < ruleValue || !ExceptionPointRecognizer.isSameside(tempList)) continue;
            exPointList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return exPointList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeRQuadrupleSd4Lj(List<Double> dataList, double average, double standardDeviation) {
        ArrayList<CoreSpcExceptionPointBean> exPointList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, 2) || standardDeviation == 0.0) {
            return exPointList;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) > 2.0) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < 2 || ExceptionPointRecognizer.isSameside(tempList)) continue;
            exPointList.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return exPointList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeSameSideBeyondOneSd4Lj(List<Double> dataList, double average, double standardDeviation, int ruleValue) {
        return ExceptionPointRecognizer.recognizeTwoTwiceSd4Lj(dataList, average, standardDeviation, ruleValue, 1);
    }

    public static List<CoreSpcExceptionPointBean> recognizeSameSide4Lj(List<Double> dataList, double average, int ruleValue) {
        return ExceptionPointRecognizer.recognizeUnilateral(dataList, average, ruleValue);
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfStandBounds(List<Double> dataList, LimisBean standardLimit) {
        ArrayList<CoreSpcExceptionPointBean> outOfBoundsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, 1)) {
            return outOfBoundsList;
        }
        if (null == standardLimit) {
            return outOfBoundsList;
        }
        for (int i = 0; i < dataList.size(); ++i) {
            Double sample = dataList.get(i);
            Double upperLimit = standardLimit.getUpperLimit();
            Double lowerLimit = standardLimit.getLowerLimit();
            if (!(upperLimit != null && sample > upperLimit) && (lowerLimit == null || !(sample < lowerLimit))) continue;
            outOfBoundsList.add(ExceptionPointRecognizer.createExceptionPoint(i, sample));
        }
        return outOfBoundsList;
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfCustomBounds(List<Double> dataList, String ruleValue) {
        ArrayList<CoreSpcExceptionPointBean> outOfCustomBounds = new ArrayList<CoreSpcExceptionPointBean>();
        String[] paramArrs = ruleValue.split(",");
        if (0 == paramArrs.length) {
            return outOfCustomBounds;
        }
        Double upper = null;
        Double lower = null;
        if (1 == paramArrs.length) {
            upper = NumberUtils.parseDouble(paramArrs[0]);
        } else {
            lower = NumberUtils.parseDouble(paramArrs[1]);
            if (!StringUtils.isEmpty(paramArrs[0])) {
                upper = NumberUtils.parseDouble(paramArrs[0]);
            }
        }
        for (int i = 0; i < dataList.size(); ++i) {
            Double sample = dataList.get(i);
            if (!(upper != null && sample > upper) && (lower == null || !(sample < lower))) continue;
            outOfCustomBounds.add(ExceptionPointRecognizer.createExceptionPoint(i, sample));
        }
        return outOfCustomBounds;
    }

    public static List<CoreSpcExceptionPointBean> recognizeNPointOutOfMStand(List<Double> dataList, double average, double standardDeviation, String ruleValue) {
        String[] paramArrs = ruleValue.split(",");
        if (2 != paramArrs.length) {
            throw new CheckedException("\u5224\u5f02\u89c4\u5219nPointOutOfMStand\u53c2\u6570\u9519\u8bef, value: " + ruleValue);
        }
        int pointNum = NumberUtils.parseInt(paramArrs[0]);
        float times = NumberUtils.parseFloat(paramArrs[1]);
        ArrayList<CoreSpcExceptionPointBean> nPointOutOfMStand = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, pointNum)) {
            return nPointOutOfMStand;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        for (int i = 0; i < dataList.size(); ++i) {
            double result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation);
            if (Math.abs(result) > (double)times) {
                tempList.add(result);
            } else {
                tempList.clear();
            }
            if (tempList.size() < pointNum) continue;
            nPointOutOfMStand.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return nPointOutOfMStand;
    }

    public static List<CoreSpcExceptionPointBean> recognizeNPointMCountOutOfXStand(List<Double> dataList, double average, double standardDeviation, String ruleValue) {
        String[] paramArrs = ruleValue.split(",");
        if (3 != paramArrs.length) {
            throw new CheckedException("\u5224\u5f02\u89c4\u5219nPointMCountOutOfXStand\u53c2\u6570\u9519\u8bef, value: " + ruleValue);
        }
        int pointNum = NumberUtils.parseInt(paramArrs[0]);
        int count = NumberUtils.parseInt(paramArrs[1]);
        float times = NumberUtils.parseFloat(paramArrs[2]);
        ArrayList<CoreSpcExceptionPointBean> nPointMCountOutOfXStand = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, pointNum)) {
            return nPointMCountOutOfXStand;
        }
        ArrayList<Double> tempList = new ArrayList<Double>();
        int size = 1;
        for (int i = 0; i < dataList.size(); ++i) {
            double result;
            if (!CollectionUtils.isEmpty(tempList)) {
                ++size;
            }
            if (Math.abs(result = ArithUtils.div(ArithUtils.sub(dataList.get(i), average), standardDeviation)) > (double)times) {
                tempList.add(result);
            } else if (size >= pointNum) {
                tempList.clear();
                size = 1;
            }
            if (tempList.size() <= count) continue;
            nPointMCountOutOfXStand.add(ExceptionPointRecognizer.createExceptionPoint(i, dataList.get(i)));
        }
        return nPointMCountOutOfXStand;
    }

    private static CoreSpcExceptionPointBean createExceptionPoint(double pointIndex, double pointValue) {
        return ExceptionPointRecognizer.createExceptionPoint(pointIndex, pointValue, null, 0);
    }

    private static CoreSpcExceptionPointBean createExceptionPoint(double pointIndex, double pointValue, String tooltip, int pileNum) {
        CoreSpcExceptionPointBean coreSpcExceptionPointBean = new CoreSpcExceptionPointBean();
        coreSpcExceptionPointBean.setxAxis(pointIndex);
        coreSpcExceptionPointBean.setyAxis(pointValue);
        coreSpcExceptionPointBean.setTooltip(tooltip);
        coreSpcExceptionPointBean.setPileNum(pileNum);
        return coreSpcExceptionPointBean;
    }

    private static boolean checkDataList(List<Double> dataList, int minRequiredNum) {
        return !CollectionUtils.isEmpty(dataList) && dataList.size() >= minRequiredNum;
    }

    private static boolean isSameside(List<Double> offsetList) {
        for (int i = 0; i < offsetList.size() - 1; ++i) {
            if (!(offsetList.get(i) * offsetList.get(i + 1) <= 0.0)) continue;
            return false;
        }
        return true;
    }

    public static List<CoreSpcExceptionPointBean> recognizeOutOfPermissions(List<Double> dataList, String ucl, String lcl) {
        ArrayList<CoreSpcExceptionPointBean> outOfBoundsList = new ArrayList<CoreSpcExceptionPointBean>();
        if (!ExceptionPointRecognizer.checkDataList(dataList, 1)) {
            return outOfBoundsList;
        }
        if (StringUtils.isEmpty(ucl) || StringUtils.isEmpty(lcl)) {
            return outOfBoundsList;
        }
        for (int i = 0; i < dataList.size(); ++i) {
            Double sample = dataList.get(i);
            double customUclValue = NumberUtils.parseDouble(ucl);
            double customLclValue = NumberUtils.parseDouble(lcl);
            if (!(sample > customUclValue) && !(sample < customLclValue)) continue;
            outOfBoundsList.add(ExceptionPointRecognizer.createExceptionPoint(i, sample));
        }
        return outOfBoundsList;
    }
}
