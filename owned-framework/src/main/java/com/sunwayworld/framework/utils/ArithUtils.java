package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.InvalidDataException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Decimal arithmetic used by business calculations.
 *
 * The implementation deliberately keeps arithmetic in {@link BigDecimal}
 * until the supplier-facing double result is produced.  No framework context,
 * native library, or license service is involved.
 */
public class ArithUtils {
    private static final int DEFAULT_DIV_SCALE = 16;
    private static final BigDecimal MAX_DEVIATION = new BigDecimal("1E-16");
    private static final Pattern SCIENTIFIC_NUMBER_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(E|e)\\+\\d+");

    public ArithUtils() { }

    public static double eval(String expression) {
        if (expression == null) throw new InvalidDataException("expression must not be null");
        String value = expression.trim().toLowerCase();
        if (value.length() == 0) throw new InvalidDataException("expression must not be empty");
        return evaluate(value.replace("sqrt", "s").replace("pow", "p"));
    }

    public static double add(double left, double right) {
        return decimal(left).add(decimal(right)).doubleValue();
    }

    public static double add(String left, String right) {
        return decimal(left).add(decimal(right)).doubleValue();
    }

    public static double add(List<String> values) {
        BigDecimal result = BigDecimal.ZERO;
        for (String value : values) result = result.add(decimal(value));
        return result.doubleValue();
    }

    public static double sub(double left, double right) {
        return decimal(left).subtract(decimal(right)).doubleValue();
    }

    public static double sub(String left, String right) {
        return decimal(left).subtract(decimal(right)).doubleValue();
    }

    public static double mul(double left, double right) {
        return decimal(left).multiply(decimal(right)).doubleValue();
    }

    public static double mul(String left, String right) {
        return decimal(left).multiply(decimal(right)).doubleValue();
    }

    public static double mul(List<String> values) {
        BigDecimal result = BigDecimal.ONE;
        for (String value : values) result = result.multiply(decimal(value));
        return result.doubleValue();
    }

    public static double div(double left, double right) {
        return div(left, right, DEFAULT_DIV_SCALE);
    }

    public static double div(String left, String right) {
        return decimal(left).divide(decimal(right), DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(double left, double right, int scale) {
        if (right == 0.0D) throw new InvalidDataException("GIKAM.ARITH.TIP.DIVIDE_ZERO");
        return decimal(left).divide(decimal(right), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double sqrt(double left, double right) {
        BigDecimal numerator = decimal(left);
        BigDecimal denominator = decimal(right);
        BigDecimal current = numerator.divide(denominator, MathContext.DECIMAL128);
        BigDecimal next;
        do {
            next = sqrtIteration(current, numerator, denominator);
            if (next.subtract(current).abs().compareTo(MAX_DEVIATION) <= 0) break;
            current = next;
        } while (true);
        return next.doubleValue();
    }

    public static double pow(double left, double right) {
        return Math.pow(left, right);
    }

    public static double round(double value, int scale) {
        return decimal(value).divide(BigDecimal.ONE, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double round(String value, int scale) {
        return decimal(value).divide(BigDecimal.ONE, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private static BigDecimal sqrtIteration(BigDecimal current, BigDecimal numerator, BigDecimal denominator) {
        if (current.equals(BigDecimal.ZERO)) return BigDecimal.ZERO;
        return current.add(numerator.divide(current, MathContext.DECIMAL128))
                .divide(denominator, MathContext.DECIMAL128);
    }

    private static BigDecimal decimal(double value) {
        return new BigDecimal(Double.toString(value));
    }

    private static BigDecimal decimal(String value) {
        if (value == null) throw new NumberFormatException("null");
        return new BigDecimal(StringUtils.trim(value));
    }

    /** Evaluate +, -, *, /, p(pow), and s(sqrt) with the supplier precedence. */
    private static double evaluate(String expression) {
        Deque<Double> values = new ArrayDeque<Double>();
        Deque<Character> operators = new ArrayDeque<Character>();
        int index = 0;
        boolean expectValue = true;
        while (index < expression.length()) {
            char c = expression.charAt(index);
            if (Character.isWhitespace(c)) { index++; continue; }
            if (Character.isDigit(c) || c == '.' || ((c == '+' || c == '-') && expectValue)) {
                if ((c == '+' || c == '-') && expectValue) {
                    int start = index++;
                    while (index < expression.length() && Character.isWhitespace(expression.charAt(index))) index++;
                    if (index < expression.length() && expression.charAt(index) == '(') {
                        operators.push(c == '-' ? '~' : '+');
                        expectValue = true;
                        continue;
                    }
                    index = numberEnd(expression, index);
                    values.push(Double.valueOf(expression.substring(start, index).replace(" ", "")));
                    expectValue = false;
                    continue;
                }
                int start = index;
                index = numberEnd(expression, index);
                if (start == index) throw new InvalidDataException("invalid arithmetic expression: " + expression);
                values.push(Double.valueOf(expression.substring(start, index)));
                expectValue = false;
                continue;
            }
            if (c == '(') {
                operators.push(c); index++; expectValue = true; continue;
            }
            if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') apply(values, operators.pop());
                if (operators.isEmpty()) throw new InvalidDataException("invalid arithmetic expression: " + expression);
                operators.pop(); index++; expectValue = false; continue;
            }
            if (isOperator(c)) {
                if (expectValue && c == '-') { operators.push('~'); index++; continue; }
                while (!operators.isEmpty() && operators.peek() != '(' && precedence(operators.peek()) >= precedence(c)) apply(values, operators.pop());
                operators.push(c); index++; expectValue = true; continue;
            }
            if (c == 's' || c == 'p') {
                while (!operators.isEmpty() && operators.peek() != '(' && precedence(operators.peek()) >= precedence(c)) apply(values, operators.pop());
                operators.push(c); index++; expectValue = true; continue;
            }
            throw new InvalidDataException("invalid arithmetic expression: " + expression);
        }
        while (!operators.isEmpty()) {
            char op = operators.pop();
            if (op == '(') throw new InvalidDataException("invalid arithmetic expression: " + expression);
            apply(values, op);
        }
        if (values.size() != 1) throw new InvalidDataException("invalid arithmetic expression: " + expression);
        return values.pop();
    }

    private static int numberEnd(String expression, int index) {
        int end = index;
        boolean exponent = false;
        while (end < expression.length()) {
            char c = expression.charAt(end);
            if (Character.isDigit(c) || c == '.') { end++; continue; }
            if ((c == 'e' || c == 'E') && !exponent) { exponent = true; end++; if (end < expression.length() && (expression.charAt(end) == '+' || expression.charAt(end) == '-')) end++; continue; }
            break;
        }
        return end;
    }

    private static boolean isOperator(char c) { return c == '+' || c == '-' || c == '*' || c == '/'; }

    private static int precedence(char c) {
        if (c == '~') return 4;
        if (c == 's' || c == 'p') return 3;
        if (c == '*' || c == '/') return 2;
        return 1;
    }

    private static void apply(Deque<Double> values, char op) {
        if (op == '~') { values.push(-values.pop()); return; }
        if (values.size() < 2) throw new InvalidDataException("invalid arithmetic expression");
        double right = values.pop();
        double left = values.pop();
        switch (op) {
            case '+': values.push(add(left, right)); break;
            case '-': values.push(sub(left, right)); break;
            case '*': values.push(mul(left, right)); break;
            case '/': values.push(div(left, right)); break;
            case 'p': values.push(pow(left, right)); break;
            case 's': values.push(sqrt(left, right)); break;
            default: throw new InvalidDataException("invalid arithmetic operator: " + op);
        }
    }
}
