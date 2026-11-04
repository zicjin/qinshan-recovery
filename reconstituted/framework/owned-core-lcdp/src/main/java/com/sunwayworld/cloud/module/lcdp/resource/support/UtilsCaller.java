/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptUtils;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsCaller {
    private final String utilName;
    private final String methodName;

    private UtilsCaller(String utilName, String methodName) {
        this.utilName = utilName;
        this.methodName = methodName;
    }

    public static UtilsCaller of(String utilName, String methodName) {
        return new UtilsCaller(utilName, methodName);
    }

    public Object call(Object ... args) {
        String utilsClassName = LcdpScriptUtils.getUtilsClassName(this.utilName);
        DynamicClassLoader.getInstance();
        Class<?> utilClass = DynamicClassLoader.getClassByClassName(utilsClassName);
        if (args.length == 0) {
            try {
                return ReflectionUtils.getUniqueMethodByName(utilClass, this.methodName).invoke(utilClass, new Object[0]);
            }
            catch (Exception e2) {
                throw new CheckedException(e2);
            }
        }
        List<ReflectionUtils.MethodWrapper> methodWrapperList = ReflectionUtils.getMethodWrapperListByName(utilClass, this.methodName);
        if (methodWrapperList.isEmpty()) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.NO_METHOD_FOR_NAME_AND_PARAMS", ClassUtils.getRawType(utilClass).toString(), this.methodName, UtilsCaller.getParamSignature(args));
        }
        if (methodWrapperList.size() == 1) {
            ReflectionUtils.MethodWrapper methodWrapper = methodWrapperList.get(0);
            try {
                Method method = methodWrapper.getMethod();
                if (ArrayUtils.isEmpty(methodWrapper.getParamTypes())) {
                    return method.invoke(null, new Object[0]);
                }
                return method.invoke(null, args);
            }
            catch (Exception e3) {
                throw new CheckedException(e3);
            }
        }
        for (ReflectionUtils.MethodWrapper methodWrapper : methodWrapperList) {
            Class<?>[] paramTypes = methodWrapper.getMethod().getParameterTypes();
            if (paramTypes.length != args.length && !paramTypes[paramTypes.length - 1].isArray()) continue;
            boolean flag = true;
            for (int i = 0; i < paramTypes.length; ++i) {
                Class<?> type = paramTypes[i];
                if (i >= args.length) {
                    List argList = Arrays.stream(args).collect(Collectors.toList());
                    argList.add(ArrayUtils.emptyArray(type.getComponentType()));
                    args = argList.toArray();
                    break;
                }
                Object param = args[i];
                if (i == paramTypes.length - 1 && type.isArray()) {
                    Class<?> genericClass = type.getComponentType();
                    List<?> variableArgList = Arrays.stream(args).skip(i).collect(Collectors.toList());
                    if (variableArgList.stream().allMatch(e -> genericClass.isAssignableFrom(e.getClass()))) {
                        args[i] = variableArgList.toArray(ArrayUtils.emptyArray(genericClass));
                        break;
                    }
                    flag = false;
                    break;
                }
                if (param == null || type.isAssignableFrom(param.getClass())) continue;
                flag = false;
                break;
            }
            if (!flag) continue;
            Method method = methodWrapper.getMethod();
            try {
                return method.invoke(null, args);
            }
            catch (Exception e4) {
                throw new CheckedException(e4);
            }
        }
        throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.DUPLICATED_METHOD_FOR_NAME_AND_PARAMS", ClassUtils.getRawType(utilClass).toString(), this.methodName, UtilsCaller.getParamSignature(args));
    }

    public Object getConstant() {
        Class<?> utilClass = null;
        try {
            String utilsClassName = LcdpScriptUtils.getUtilsClassName(this.utilName);
            DynamicClassLoader.getInstance();
            utilClass = DynamicClassLoader.getClassByClassName(utilsClassName);
            Field declaredField = utilClass.getDeclaredField(this.methodName);
            Object constantValue = declaredField.get(null);
            return constantValue;
        }
        catch (Exception e) {
            throw new CheckedException(e);
        }
    }

    private static String getParamSignature(Object ... params) {
        if (ArrayUtils.isEmpty(params)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; ++i) {
            Object param = params[i];
            if (i > 0) {
                sb.append(", ");
            }
            if (param == null) {
                sb.append("NIL");
                continue;
            }
            sb.append(ClassUtils.getRawType(param.getClass()).getSimpleName());
        }
        return sb.toString();
    }
}
