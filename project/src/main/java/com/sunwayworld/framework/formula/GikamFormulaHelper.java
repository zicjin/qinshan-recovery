package com.sunwayworld.framework.formula;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;

public class GikamFormulaHelper {

    private static final Pattern ARITH_PATTERN = Pattern.compile("@\\{[^{};]+\\}");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("[A-Za-z]+\\w*");

    public static String getRevisedScript(String script) {
        if (StringUtils.isEmpty(script)) {
            return script;
        }

        // 去掉注释代码
        script = script.replaceAll("//.*?(\n|\r)", "\n");
        script = script.replaceAll("\\/\\*.*?\\*\\/", "");

        String copy = script;

        Long suffix = ApplicationContextHelper.getNextIdentity();

        Matcher m = ARITH_PATTERN.matcher(copy);
        while (m.find()) {
            String ov = copy.substring(m.start(), m.end());
            String v = ov;

            v = StringUtils.replaceIgnoreCase(v, "pow", " pow ");
            v = StringUtils.replaceIgnoreCase(v, "sqrt", " sqrt ");

            Matcher vm = VARIABLE_PATTERN.matcher(v);

            List<Pair<Integer, Integer>> pairList = new ArrayList<>();

            while (vm.find()) {
                pairList.add(Pair.of(vm.start(), vm.end()));
            }

            Collections.reverse(pairList);

            List<String> keyList = new ArrayList<>();

            String copyV = v;
            for (Pair<Integer, Integer> pair : pairList) {
                String key = v.substring(pair.getFirst(), pair.getSecond());

                if (!"pow".equalsIgnoreCase(key) && !"sqrt".equalsIgnoreCase(key)) {
                    copyV = new StringBuilder(copyV.substring(0, pair.getFirst())).append(key).append(suffix).append(copyV.substring(pair.getSecond())).toString();

                    keyList.add(key);
                }
            }

            String r = "\"" + copyV.substring(2, copyV.length() - 1) + "\"";

            StringBuilder sb = new StringBuilder();
            String nextValue = r.replaceAll("\\s*pow\\s*", "pow").replaceAll("\\s*sqrt\\s*", "sqrt");
            for (String key : keyList) {
                String tempKey = "temp" + ApplicationContextHelper.getNextIdentity();

                sb.append(tempKey).append("=")
                        .append(new StringBuilder().append("StringUtils.replace(").append(nextValue).append(", \"").append(key).append(suffix).append("\", ").append(key).append(");\n").toString());

                nextValue = tempKey;
            }

            int pos = script.indexOf(ov);

            if (pos == -1) {
                script = sb.toString() + StringUtils.replaceFirst(script, ov, "ArithUtils.eval(" + nextValue + ")");
            } else {
                String subscript = script.substring(0, pos);

                int lastPos = Math.max(subscript.lastIndexOf("\n"), subscript.lastIndexOf(";"));

                script = script.substring(0, lastPos + 1) + "\n" + sb.toString() + StringUtils.replaceFirst(script.substring(lastPos + 1), ov, "ArithUtils.eval(" + nextValue + ")");
            }
        }

        for (Class<?> clazz : GikamFormulaRegistry.INSTANCE.getRepository()) {
            List<Method> methodList = ReflectionUtils.getMethodList(clazz, Modifier.PUBLIC, Modifier.STATIC);

            script = replaceSetProperty(methodList, script);

            script = replaceMethodArgument(methodList, script);
        }
        ;

        return script;
    }

    public static <T extends Insertable<ID>, ID extends Serializable> void setupRelevantData(List<T> relevantItemList) {
        T currentData = GikamComputingDataHolder.getCurrentData();

        ID id = currentData.getId();

        List<ID> relevantIdList = relevantItemList.stream().filter(i -> !i.getId().equals(id)).map(i -> i.getId()).collect(Collectors.toList());

        GikamComputingDataHolder.setRelevantData(id, relevantIdList);
    }

    // --------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------
    private static String replaceSetProperty(List<Method> methodList, String script) {
        for (Method method : methodList) {
            Class<?> type = method.getReturnType();

            if (!ClassUtils.isGikamType(type)) {
                continue;
            }

            Pattern pattern = Pattern.compile("(\\s+|;\\s*|^\\s*)" + method.getName() + "\\([^()]*\\)\\s*.\\s*[A-za-z]++\\w*\\s*=\\s*[^\"\\s]");

            String oldScript = script;

            Matcher matcher = pattern.matcher(oldScript);
            while (matcher.find()) {
                int end = matcher.end();

                String matchValue = oldScript.substring(matcher.start(), end);

                String fieldName = matchValue.substring(matchValue.indexOf(".") + 1, matchValue.indexOf("="));

                Field field = ReflectionUtils.findField(type, fieldName);

                if (String.class.equals(field.getType())) {
                    int pos = script.indexOf(";", end);

                    if (pos > 0) {
                        script = script.substring(0, pos) + ")" + script.substring(pos);
                    } else {
                        script = script + ")";
                    }

                    script = StringUtils.replace(script, matchValue, matchValue.replaceFirst("=", "=\"\"+("));
                }
            }
        }

        return script;
    }

    private static String replaceMethodArgument(List<Method> methodList, String script) {
        for (Method method : methodList) {
            // 判断脚本中是否调用计算公式
            Pattern pattern = Pattern.compile("(=\\s*|\\s+|;\\s*|^\\s*|=\"\"\\+\\(\\s*)" + method.getName() + "\\s*\\(([^()]*(\\([^()]*\\))?[^()]*)++\\)");

            String oldScript = script;

            Class<?>[] parameterTypes = method.getParameterTypes();

            // 这里start下标是通过oldScript获取的
            Matcher matcher = pattern.matcher(oldScript);
            while(matcher.find()) {
                int start = matcher.start();

                String matchValue = oldScript.substring(start, matcher.end());

                int startPos = matchValue.indexOf("(");
                int endPos = matchValue.lastIndexOf(")");

                String paramValue = matchValue.substring(startPos + 1, endPos);

                if (StringUtils.isEmpty(paramValue)) {
                    continue;
                }

                List<String> paramList = new ArrayList<>();

                int lastComaPos = 0;
                int pos = -1;
                while((pos = paramValue.indexOf(",", pos + 1)) > 0) {
                    String temp = paramValue.substring(0, pos);

                    int lastStartBracketsPos = temp.lastIndexOf("(");

                    if (lastStartBracketsPos < 0) {
                        paramList.add(paramValue.substring(lastComaPos, pos));
                        lastComaPos = pos + 1;
                    } else {
                        int lastEndBracketsPos = temp.lastIndexOf(")");

                        if (lastEndBracketsPos > lastStartBracketsPos) {
                            paramList.add(paramValue.substring(lastComaPos, pos));
                            lastComaPos = pos + 1;
                        }
                    }
                }

                paramList.add(paramValue.substring(lastComaPos));

                StringBuilder sb = new StringBuilder();

                // 原工具类中下面这句存在问题，start下标在最开始是通过oldScript获取的，并且
                // script会发生改变，对改变后的字符串进行截取是不正确的
                // String subScript = script.substring(0, start);

                String subScript = oldScript.substring(0, start);
                for (int i = 0, j = paramList.size(); i < j; i++) {
                    String param = paramList.get(i);

                    if (sb.length() > 0) {
                        sb.append(",");
                    }

                    // 不是参数
                    if (!VARIABLE_PATTERN.matcher(param).matches()) {
                        sb.append("\"").append(param).append("\"");
                        continue;
                    }

                    Pattern paramPattern = Pattern.compile("(;\\s*|^\\s*|,\\s*|\\w+\\s+|\\{\\s*|\\(\\s*)" + param + "\\s*=");

                    if (paramPattern.matcher(subScript).find()) {
                        if (method.isVarArgs()) {
                            sb.append("ObjectUtils.toString(").append(param).append(")");
                        } else {
                            sb.append("ConvertUtils.convert(").append(param).append(",").append(parameterTypes[i].getName()).append(".class)");
                        }
                    } else {
                        sb.append("\"").append(param).append("\"");
                    }
                }

                if (method.isVarArgs()) {
                    String replacedValue = matchValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(new String[]{\"");
                    replacedValue = StringUtils.replaceLast(replacedValue, ")", "\"})");
                    replacedValue = replacedValue.replaceAll("\\{.*\\}", "{" + sb.toString() + "}");
                    script = StringUtils.replace(script, matchValue, replacedValue);
                } else {
                    String replacedValue = matchValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(\"").replaceFirst("\\)", "\")").replaceAll("\\(.*\\)", "(" + sb.toString() + ")");
                    script = StringUtils.replace(script, matchValue, replacedValue);
                }
            }
        }

        return script;
    }
}

