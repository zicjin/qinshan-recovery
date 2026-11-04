/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArithUtils {
    private static final int DEFAULT_DIV_SCALE = 16;
    private static final BigDecimal MAX_DEVIATION = new BigDecimal("1E-16");
    private static final Pattern SCIENTIFIC_NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(E|e)\\+\\d+");

    public static double eval(String expression) {
        return Calculator.eval(expression);
    }

    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2.trim()));
        return b1.add(b2).doubleValue();
    }

    public static double add(List<String> strList) {
        BigDecimal result = new BigDecimal(0);
        for (String str : strList) {
            result = result.add(new BigDecimal(StringUtils.trim(str)));
        }
        return result.doubleValue();
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double sub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(List<String> strList) {
        BigDecimal result = new BigDecimal(1);
        for (String str : strList) {
            result = result.multiply(new BigDecimal(StringUtils.trim(str)));
        }
        return result.doubleValue();
    }

    public static double div(double v1, double v2) {
        return ArithUtils.div(v1, v2, 16);
    }

    public static double div(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(StringUtils.trim(v1));
        BigDecimal b2 = new BigDecimal(StringUtils.trim(v2));
        return b1.divide(b2, 16, 4).doubleValue();
    }

    public static double div(double v1, double v2, int scale) {
        if (0.0 == v2) {
            throw new InvalidDataException("GIKAM.ARITH.TIP.DIVIDE_ZERO");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, 4).doubleValue();
    }

    public static double sqrt(double v, double n) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal d = new BigDecimal(Double.toString(n));
        BigDecimal x = b.divide(d, MathContext.DECIMAL128);
        while (x.subtract(x = ArithUtils.sqrtIteration(x, b, d)).abs().compareTo(MAX_DEVIATION) > 0) {
        }
        return x.doubleValue();
    }

    public static double pow(double v, double p) {
        return Math.pow(v, p);
    }

    public static double round(double v, int scale) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 4).doubleValue();
    }

    public static double round(String v, int scale) {
        BigDecimal b = new BigDecimal(StringUtils.trim(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, 4).doubleValue();
    }

    private static BigDecimal sqrtIteration(BigDecimal x, BigDecimal n, BigDecimal d) {
        if (x.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return x.add(n.divide(x, MathContext.DECIMAL128)).divide(d, MathContext.DECIMAL128);
    }

    private static class Calculator {
        private Stack<String> postfixStack = new Stack();
        private Stack<Character> opStack = new Stack();
        private int[] operatPriority = new int[]{0, 3, 2, 1, -1, 1, 0, 2};

        private Calculator() {
        }

        public static double eval(String expression) {
            return new Calculator().calculate(Calculator.transform(expression));
        }

        private static String transform(String expression) {
            expression = expression.toLowerCase();
            expression = StringUtils.replace(expression, "sqrt", "s");
            expression = StringUtils.replace(expression, "pow", "p");
            expression = Calculator.transformScientificNumber(expression);
            char[] arr = expression.toCharArray();
            for (int i = 0; i < arr.length; ++i) {
                if (arr[i] != '-') continue;
                if (i == 0) {
                    arr[i] = 126;
                    continue;
                }
                char c = arr[i - 1];
                if (c != '+' && c != '-' && c != '*' && c != '/' && c != '(' && c != 'e' && c != 's' && c != 'p') continue;
                arr[i] = 126;
            }
            if (arr[0] == '~' && arr[1] == '(') {
                arr[0] = 45;
                return "0" + new String(arr);
            }
            return new String(arr);
        }

        public double calculate(String expression) {
            Stack<String> resultStack = new Stack<String>();
            this.prepare(expression);
            Collections.reverse(this.postfixStack);
            while (!this.postfixStack.isEmpty()) {
                String currentValue = this.postfixStack.pop();
                if (!this.isOperator(currentValue.charAt(0))) {
                    currentValue = currentValue.replace("~", "-");
                    resultStack.push(currentValue);
                    continue;
                }
                String secondValue = (String)resultStack.pop();
                String firstValue = (String)resultStack.pop();
                firstValue = firstValue.replace("~", "-");
                secondValue = secondValue.replace("~", "-");
                String tempResult = this.calculate(firstValue, secondValue, currentValue.charAt(0));
                resultStack.push(tempResult);
            }
            return Double.valueOf((String)resultStack.pop());
        }

        private void prepare(String expression) {
            this.opStack.push(Character.valueOf(','));
            char[] arr = expression.toCharArray();
            int currentIndex = 0;
            int count = 0;
            for (int i = 0; i < arr.length; ++i) {
                char currentOp = arr[i];
                if (this.isOperator(currentOp)) {
                    if (count > 0) {
                        this.postfixStack.push(new String(arr, currentIndex, count));
                    }
                    char peekOp = this.opStack.peek().charValue();
                    if (currentOp == ')') {
                        while (this.opStack.peek().charValue() != '(') {
                            this.postfixStack.push(String.valueOf(this.opStack.pop()));
                        }
                        this.opStack.pop();
                    } else {
                        while (currentOp != '(' && peekOp != ',' && this.compare(currentOp, peekOp)) {
                            this.postfixStack.push(String.valueOf(this.opStack.pop()));
                            peekOp = this.opStack.peek().charValue();
                        }
                        this.opStack.push(Character.valueOf(currentOp));
                    }
                    count = 0;
                    currentIndex = i + 1;
                    continue;
                }
                ++count;
            }
            if (count > 1 || count == 1 && !this.isOperator(arr[currentIndex])) {
                this.postfixStack.push(new String(arr, currentIndex, count));
            }
            while (this.opStack.peek().charValue() != ',') {
                this.postfixStack.push(String.valueOf(this.opStack.pop()));
            }
        }

        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == 'p' || c == 's';
        }

        public boolean compare(char cur, char peek) {
            if (cur == 'p' || cur == 's') {
                return peek == '(' || peek == ')';
            }
            if (peek == 'p' || peek == 's') {
                return cur != '(' && cur != ')';
            }
            boolean result = false;
            if (this.operatPriority[peek - 40] >= this.operatPriority[cur - 40]) {
                result = true;
            }
            return result;
        }

        private String calculate(String firstValue, String secondValue, char currentOp) {
            String result = "";
            switch (currentOp) {
                case '+': {
                    result = String.valueOf(ArithUtils.add(firstValue, secondValue));
                    break;
                }
                case '-': {
                    result = String.valueOf(ArithUtils.sub(firstValue, secondValue));
                    break;
                }
                case '*': {
                    result = String.valueOf(ArithUtils.mul(firstValue, secondValue));
                    break;
                }
                case '/': {
                    result = String.valueOf(ArithUtils.div(firstValue, secondValue));
                    break;
                }
                case 'p': {
                    result = String.valueOf(ArithUtils.pow(NumberUtils.parseDouble(firstValue), NumberUtils.parseDouble(secondValue)));
                    break;
                }
                case 's': {
                    result = String.valueOf(ArithUtils.sqrt(NumberUtils.parseDouble(firstValue), NumberUtils.parseDouble(secondValue)));
                }
            }
            return result;
        }

        private static String transformScientificNumber(String expression) {
            String oldExpression = expression;
            Matcher matcher = SCIENTIFIC_NUMBER_PATTERN.matcher(oldExpression);
            while (matcher.find()) {
                String scientificNumber = oldExpression.substring(matcher.start(), matcher.end());
                expression = StringUtils.replaceFirst(expression, scientificNumber, NumberUtils.parseDouble(scientificNumber));
            }
            return expression;
        }
    }
}
