/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

public abstract class SpringUtils {
    public static void addEnvironment(String key, Object value) {
        ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment)ApplicationContextHelper.getBean(Environment.class);
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        Properties p = new Properties();
        p.put(key, value);
        propertySources.addFirst(new PropertiesPropertySource(key, p));
    }

    public static synchronized void registerBean(String beanName, Class<?> clazz) {
        DefaultListableBeanFactory defaultListableBeanFactory;
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        if (beanName == null) {
            beanName = clazz.getName();
        }
        if ((defaultListableBeanFactory = (DefaultListableBeanFactory)((ConfigurableApplicationContext)applicationContext).getBeanFactory()).containsBeanDefinition(beanName)) {
            defaultListableBeanFactory.removeBeanDefinition(beanName);
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
        long count = 0L;
        while (!applicationContext.containsBean(beanName) && count++ < 1000L) {
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public static <T> T getBean(String beanName) {
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        return (T)((ConfigurableApplicationContext)applicationContext).getBeanFactory().getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        return ((ConfigurableApplicationContext)applicationContext).getBeanFactory().getBean(clazz);
    }

    public static void removeBeansIncludingAncestors(Class<?> beanClass) {
        ApplicationContext applicationContext = ApplicationContextHelper.getApplicationContext();
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)((ConfigurableApplicationContext)applicationContext).getBeanFactory();
        while (beanClass != null && !Object.class.equals(beanClass)) {
            BeanFactoryUtils.beansOfTypeIncludingAncestors(((ConfigurableApplicationContext)applicationContext).getBeanFactory(), beanClass).forEach((k, v) -> defaultListableBeanFactory.removeBeanDefinition((String)k));
            beanClass = beanClass.getSuperclass();
        }
    }

    public static Object invoke(String beanName, Method method, Object ... args) {
        Object springBean = SpringUtils.getBean(beanName);
        if (method.getParameterCount() > 0) {
            return ReflectionUtils.invokeMethod(method, springBean, args);
        }
        return ReflectionUtils.invokeMethod(method, springBean);
    }

    public static Object invoke(String beanName, String methodName, Object ... args) {
        Object springBean = SpringUtils.getBean(beanName);
        return SpringUtils.invoke(springBean, methodName, args);
    }

    public static Object invoke(Object springBean, String methodName, Object ... args) {
        if (args.length == 0) {
            return ReflectionUtils.invokeMethod(ReflectionUtils.getUniqueMethodByName(springBean.getClass(), methodName), springBean);
        }
        List<ReflectionUtils.MethodWrapper> methodWrapperList = ReflectionUtils.getMethodWrapperListByName(springBean.getClass(), methodName);
        if (methodWrapperList.isEmpty()) {
            throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.NO_METHOD_FOR_NAME_AND_PARAMS", ClassUtils.getRawType(springBean.getClass()).toString(), methodName, SpringUtils.getParamSignature(args));
        }
        if (methodWrapperList.size() == 1) {
            ReflectionUtils.MethodWrapper methodWrapper = methodWrapperList.get(0);
            if (ArrayUtils.isEmpty(methodWrapper.getParamTypes())) {
                return ReflectionUtils.invokeMethod(methodWrapper.getMethod(), springBean);
            }
            return ReflectionUtils.invokeMethod(methodWrapper.getMethod(), springBean, args);
        }
        Object[] cloneArgs = null;
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
                        cloneArgs = new Object[i + 1];
                        System.arraycopy(args, 0, cloneArgs, 0, i + 1);
                        args = cloneArgs;
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
            return ReflectionUtils.invokeMethod(methodWrapper.getMethod(), springBean, args);
        }
        throw new ApplicationRuntimeException("GIKAM.EXCEPTION.REFLECTION.DUPLICATED_METHOD_FOR_NAME_AND_PARAMS", ClassUtils.getRawType(springBean.getClass()).toString(), methodName, SpringUtils.getParamSignature(args));
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
