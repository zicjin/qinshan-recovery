/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util;

import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.Map;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.Loader;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class DynamicCreateObjectUtil {
    public static final String DYNAMIC_SOAP_ENTITY_PREFIX = DynamicCreateObjectUtil.class.getPackage().getName();
    public static ClassLoader classLoader;
    public static Pair<ClassPool, Loader> pair;

    public static Annotation addSingleAnnotation(ConstPool constpool, String annotationClassName, Map<String, Object> annotationMap) {
        Annotation annotation = new Annotation(annotationClassName, constpool);
        for (Map.Entry<String, Object> fieldValue : annotationMap.entrySet()) {
            if (ObjectUtils.isEmpty(fieldValue.getValue())) continue;
            if (fieldValue.getValue() instanceof Enum) {
                annotation.addMemberValue(fieldValue.getKey(), (MemberValue)new EnumMemberValue(constpool));
                continue;
            }
            if (fieldValue.getValue() instanceof Boolean) {
                annotation.addMemberValue(fieldValue.getKey(), (MemberValue)new BooleanMemberValue((Boolean)fieldValue.getValue(), constpool));
                continue;
            }
            if (fieldValue.getValue() instanceof Integer) {
                annotation.addMemberValue(fieldValue.getKey(), (MemberValue)new StringMemberValue((Integer)fieldValue.getValue(), constpool));
                continue;
            }
            annotation.addMemberValue(fieldValue.getKey(), (MemberValue)new StringMemberValue((String)fieldValue.getValue(), constpool));
        }
        return annotation;
    }

    static {
        pair = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ClassClassPath(DynamicCreateObjectUtil.class));
            pair = Pair.of(classPool, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
