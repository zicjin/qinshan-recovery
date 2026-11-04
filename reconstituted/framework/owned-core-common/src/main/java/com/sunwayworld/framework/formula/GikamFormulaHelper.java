/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.formula;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GikamFormulaHelper {
    public static final Pattern ARITH_PATTERN = Pattern.compile("@\\{[^{};]++\\}");
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("[A-Za-z]++\\w*");

    public static String getRevisedScript(String script) {
        if (StringUtils.isEmpty(script)) {
            return script;
        }
        script = script.replaceAll("//.*?(\n|\r)", "\n");
        String copy = script = script.replaceAll("\\/\\*.*?\\*\\/", "");
        Long suffix = ApplicationContextHelper.getNextIdentity();
        Matcher m = ARITH_PATTERN.matcher(copy);
        while (m.find()) {
            String ov;
            String v = ov = copy.substring(m.start(), m.end());
            v = StringUtils.replaceIgnoreCase(v, "pow", " pow ");
            v = StringUtils.replaceIgnoreCase(v, "sqrt", " sqrt ");
            Matcher vm = VARIABLE_PATTERN.matcher(v);
            ArrayList<Pair<Integer, Integer>> pairList = new ArrayList<Pair<Integer, Integer>>();
            while (vm.find()) {
                pairList.add(Pair.of(vm.start(), vm.end()));
            }
            Collections.reverse(pairList);
            ArrayList<String> keyList = new ArrayList<String>();
            String copyV = v;
            for (Pair pair : pairList) {
                String key = v.substring((Integer)pair.getFirst(), (Integer)pair.getSecond());
                if ("pow".equalsIgnoreCase(key) || "sqrt".equalsIgnoreCase(key)) continue;
                copyV = copyV.substring(0, (Integer)pair.getFirst()) + key + suffix + copyV.substring((Integer)pair.getSecond());
                keyList.add(key);
            }
            String r = "\"" + copyV.substring(2, copyV.length() - 1) + "\"";
            StringBuilder stringBuilder = new StringBuilder();
            String nextValue = r.replaceAll("\\s*pow\\s*", "pow").replaceAll("\\s*sqrt\\s*", "sqrt");
            for (String key : keyList) {
                String tempKey = "temp" + ApplicationContextHelper.getNextIdentity();
                stringBuilder.append(tempKey).append("=").append("StringUtils.replace(" + nextValue + ", \"" + key + suffix + "\", " + key + ");\n");
                nextValue = tempKey;
            }
            int pos = script.indexOf(ov);
            if (pos == -1) {
                script = stringBuilder.toString() + StringUtils.replaceFirst(script, ov, "ArithUtils.eval(" + nextValue + ")");
                continue;
            }
            String subscript = script.substring(0, pos);
            int lastPos = Math.max(subscript.lastIndexOf("\n"), subscript.lastIndexOf(";"));
            script = script.substring(0, lastPos + 1) + "\n" + stringBuilder.toString() + StringUtils.replaceFirst(script.substring(lastPos + 1), ov, "ArithUtils.eval(" + nextValue + ")");
        }
        for (Class<?> clazz : GikamFormulaRegistry.INSTANCE.getRepository()) {
            List<Method> methodList = ReflectionUtils.getMethodList(clazz, 1, 8);
            script = GikamFormulaHelper.replaceSetProperty(methodList, script);
            script = GikamFormulaHelper.replaceMethodArgument(methodList, script);
        }
        return script;
    }

    public static <T extends Insertable<ID>, ID extends Serializable> void setupRelevantData(List<T> relevantItemList) {
        Insertable currentData = (Insertable)GikamComputingDataHolder.getCurrentData();
        Serializable id = (Serializable)currentData.getId();
        List relevantIdList = relevantItemList.stream().filter(i -> !((Serializable)i.getId()).equals(id)).map(i -> (Serializable)i.getId()).collect(Collectors.toList());
        GikamComputingDataHolder.setRelevantData(id, relevantIdList);
    }

    private static String replaceSetProperty(List<Method> methodList, String script) {
        for (Method method : methodList) {
            Class<?> type = method.getReturnType();
            if (!ClassUtils.isGikamType(type)) continue;
            Pattern pattern = Pattern.compile("(\\s+|;\\s*|^\\s*)" + method.getName() + "\\([^()]*\\)\\s*.\\s*[A-za-z]++\\w*\\s*=\\s*[^\"\\s]");
            String oldScript = script;
            Matcher matcher = pattern.matcher(oldScript);
            while (matcher.find()) {
                int end = matcher.end();
                String matchValue = oldScript.substring(matcher.start(), end);
                String fieldName = matchValue.substring(matchValue.indexOf(".") + 1, matchValue.indexOf("="));
                Field field = ReflectionUtils.findField(type, fieldName);
                if (!String.class.equals(field.getType())) continue;
                int pos = script.indexOf(";", end);
                script = pos > 0 ? script.substring(0, pos) + ")" + script.substring(pos) : script + ")";
                script = StringUtils.replace(script, matchValue, matchValue.replaceFirst("=", "=\"\"+("));
            }
        }
        return script;
    }

    private static String replaceMethodArgument(List<Method> methodList, String script) {
        for (Method method : methodList) {
            Pattern pattern = Pattern.compile("(=\\s*|\\s+|;\\s*|^\\s*|=\"\"\\+\\(\\s*)" + method.getName() + "\\s*\\(([^()]*(\\([^()]*\\))?[^()]*)++\\)");
            String oldScript = script;
            Class<?>[] parameterTypes = method.getParameterTypes();
            Matcher matcher = pattern.matcher(oldScript);
            while (matcher.find()) {
                String replacedValue;
                int endPos;
                int startPos;
                int start = matcher.start();
                String matchValue = oldScript.substring(start, matcher.end());
                String paramValue = matchValue.substring((startPos = matchValue.indexOf("(")) + 1, endPos = matchValue.lastIndexOf(")"));
                if (StringUtils.isEmpty(paramValue)) continue;
                ArrayList<String> paramList = new ArrayList<String>();
                int lastComaPos = 0;
                int pos = -1;
                while ((pos = paramValue.indexOf(",", pos + 1)) > 0) {
                    String temp = paramValue.substring(0, pos);
                    int lastStartBracketsPos = temp.lastIndexOf("(");
                    if (lastStartBracketsPos < 0) {
                        paramList.add(paramValue.substring(lastComaPos, pos));
                        lastComaPos = pos + 1;
                        continue;
                    }
                    int lastEndBracketsPos = temp.lastIndexOf(")");
                    if (lastEndBracketsPos <= lastStartBracketsPos) continue;
                    paramList.add(paramValue.substring(lastComaPos, pos));
                    lastComaPos = pos + 1;
                }
                paramList.add(paramValue.substring(lastComaPos));
                StringBuilder sb = new StringBuilder();
                String subScript = script.substring(0, start);
                int j = paramList.size();
                for (int i = 0; i < j; ++i) {
                    String param = (String)paramList.get(i);
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    if (!VARIABLE_PATTERN.matcher(param).matches()) {
                        sb.append("\"").append(param).append("\"");
                        continue;
                    }
                    Pattern paramPattern = Pattern.compile("(;\\s*|^\\s*|,\\s*|\\w+\\s+|\\{\\s*|\\(\\s*)" + param + "\\s*=");
                    if (paramPattern.matcher(subScript).find()) {
                        if (method.isVarArgs()) {
                            sb.append("ObjectUtils.toString(").append(param).append(")");
                            continue;
                        }
                        sb.append("ConvertUtils.convert(").append(param).append(",").append(parameterTypes[i].getName()).append(".class)");
                        continue;
                    }
                    sb.append("\"").append(param).append("\"");
                }
                if (method.isVarArgs()) {
                    replacedValue = matchValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(new String[]{\"");
                    replacedValue = StringUtils.replaceLast(replacedValue, ")", "\"})");
                    replacedValue = replacedValue.replaceAll("\\{.*\\}", "{" + sb.toString() + "}");
                    script = StringUtils.replace(script, matchValue, replacedValue);
                    continue;
                }
                replacedValue = matchValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(\"").replaceFirst("\\)", "\")").replaceAll("\\(.*\\)", "(" + sb.toString() + ")");
                script = StringUtils.replace(script, matchValue, replacedValue);
            }
        }
        return script;
    }
}
