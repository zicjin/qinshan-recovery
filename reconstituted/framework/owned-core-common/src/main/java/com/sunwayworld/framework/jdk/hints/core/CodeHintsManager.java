package com.sunwayworld.framework.jdk.hints.core;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.google.googlejavaformat.java.JavaFormatterOptions.Style;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.hints.CodeHintsClass;
import com.sunwayworld.framework.jdk.hints.CodeHintsField;
import com.sunwayworld.framework.jdk.hints.CodeHintsMethod;
import com.sunwayworld.framework.jdk.hints.CodeHintsMethodParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CodeHintsManager {
   private static final Map<String, Class<?>> classCache = new HashMap<>();
   private static final Set<String> codeHintsKeyWords = new HashSet<>();

   public static void init() {
      CodeHintsProperties codeHintsProperties = ApplicationContextHelper.getBean(CodeHintsProperties.class);
      Set<Class<?>> classSet = new HashSet<>();
      String[] initPackageNames = new String[]{"com.sunwayworld"};
      List<String> packageNameList = new ArrayList<>(Arrays.asList(initPackageNames));
      String[] packageNames = codeHintsProperties.getPackageNames();
      if (packageNames != null) {
         packageNameList.addAll(Arrays.asList(packageNames));
      }

      packageNameList.stream().distinct().forEach(e -> {
         List<Class<?>> classList = ClassUtils.getAllClasses(e);
         classSet.addAll(classList);
      });
      String[] initClassNames = new String[]{
         "java.lang.String",
         "java.lang.Long",
         "java.lang.Integer",
         "java.lang.Double",
         "java.util.List",
         "java.util.ArrayList",
         "java.util.Map",
         "java.util.HashMap",
         "java.util.Date",
         "java.util.stream.Collectors",
         "java.util.stream.Stream",
         "java.time.LocalDate",
         "java.time.LocalDateTime",
         "com.alibaba.fastjson.JSON",
         "org.springframework.stereotype.Repository",
         "org.springframework.transaction.annotation.Transactional",
         "org.springframework.beans.factory.annotation.Autowired",
         "com.sunwayworld.framework.audit.constant.AuditConstant"
      };
      List<String> classNameList = new ArrayList<>(Arrays.asList(initClassNames));
      String[] classNames = codeHintsProperties.getClassNames();
      if (classNames != null) {
         classNameList.addAll(Arrays.asList(classNames));
      }

      classNameList.stream().distinct().forEach(e -> classSet.add(ClassUtils.getClass(e)));

      for (Class<?> clazz : classSet) {
         CodeHintsClass codeHintsClass = getCodeHintsClass(clazz);
         String classSimpleName = codeHintsClass.getSimpleName();
         register(classSimpleName, clazz);
      }
   }

   public static CodeHintsClass getCodeHintsClass(Class<?> clazz) {
      if (clazz != null && clazz.getName() != null) {
         CodeHintsClass codeHintsClass = new CodeHintsClass();
         codeHintsClass.setName(clazz.getName());
         codeHintsClass.setSimpleName(clazz.getSimpleName());
         List<CodeHintsField> codeHintsFieldList = new ArrayList<>();
         CodeHintsField superField = new CodeHintsField();
         superField.setName("super");
         superField.setType("class");
         codeHintsFieldList.add(superField);

         for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && field.getAnnotation(Transient.class) == null) {
               CodeHintsField codeHintsField = new CodeHintsField();
               codeHintsField.setName(field.getName());
               codeHintsField.setType(field.getType().getName());
               codeHintsFieldList.add(codeHintsField);
            }
         }

         codeHintsFieldList.sort(Comparator.comparing(CodeHintsField::getName));
         codeHintsClass.setFields(codeHintsFieldList.toArray(new CodeHintsField[0]));
         List<CodeHintsMethod> codeHintsMethodList = new ArrayList<>();
         List<Method> methodList;
         if (clazz.isEnum()) {
            methodList = Arrays.asList(clazz.getDeclaredMethods());
         } else {
            methodList = ReflectionUtils.getMethodList(clazz, 1);
         }

         for (Method method : methodList) {
            CodeHintsMethod codeHintsMethod = new CodeHintsMethod();
            codeHintsMethod.setName(method.getName());
            Class<?> returnType = method.getReturnType();
            codeHintsMethod.setReturnType(returnType.isArray() ? returnType.getSimpleName() : returnType.getName());
            Parameter[] parameters = method.getParameters();
            if (parameters != null && parameters.length > 0) {
               CodeHintsMethodParameter[] methodParameters = new CodeHintsMethodParameter[parameters.length];
               StringBuilder sb = new StringBuilder("(");

               for (int i = 0; i < parameters.length; i++) {
                  Parameter parameter = parameters[i];
                  CodeHintsMethodParameter methodParameter = new CodeHintsMethodParameter();
                  Class<?> type = parameter.getType();
                  methodParameter.setName(parameter.getName());
                  methodParameter.setType(type.isArray() ? type.getSimpleName() : type.getName());
                  methodParameters[i] = methodParameter;
                  if (i > 0) {
                     sb.append(", ");
                  }

                  sb.append(parameter.getType().getSimpleName()).append(" ").append(parameter.getName());
               }

               sb.append(")");
               codeHintsMethod.setParameters(methodParameters);
               codeHintsMethod.setInsertText(method.getName() + sb);
               sb.append(" ").append(returnType.getSimpleName());
               codeHintsMethod.setFullName(sb.toString());
            } else {
               codeHintsMethod.setParameters(new CodeHintsMethodParameter[0]);
               codeHintsMethod.setInsertText(method.getName() + "()");
               codeHintsMethod.setFullName("() " + returnType.getSimpleName());
            }

            codeHintsMethodList.add(codeHintsMethod);
         }

         codeHintsMethodList.sort(Comparator.comparing(CodeHintsMethod::getName));
         codeHintsClass.setMethods(codeHintsMethodList.toArray(new CodeHintsMethod[0]));
         return codeHintsClass;
      } else {
         return null;
      }
   }

   public static void register(String name, Class<?> clazz) {
      classCache.put(name.toLowerCase(), clazz);
      codeHintsKeyWords.add(name);
   }

   public static CodeHintsClass getCodeHints(String name) {
      CodeHintsClass codeHint = getCodeHintsClass(classCache.get(name.toLowerCase()));
      return codeHint == null ? new CodeHintsClass() : codeHint;
   }

   public static CodeHintsClass selectByPath(String path) {
      path = path.replaceAll("\\.super", "");
      if (!path.contains(".")) {
         return Optional.ofNullable(getCodeHints(path.toLowerCase())).orElse(new CodeHintsClass());
      } else {
         String[] values = StringUtils.split(path, ".");
         CodeHintsClass parent = null;

         for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            if (i == 0) {
               parent = getCodeHints(value.toLowerCase());
               if (parent == null) {
                  return new CodeHintsClass();
               }
            } else if (StringUtils.contains(value, "(")) {
               int left = value.indexOf("(");
               int right = value.indexOf(")");
               if (right < left) {
                  return new CodeHintsClass();
               }

               String simpleName = value.substring(0, left);
               String paramString = value.substring(left + 1, right);
               String[] params = StringUtils.isBlank(paramString) ? new String[0] : StringUtils.split(paramString, ",");
               boolean found = false;
               if (parent == null || parent.getMethods() == null) {
                  return new CodeHintsClass();
               }

               for (CodeHintsMethod method : parent.getMethods()) {
                  if (match(method, simpleName, params)) {
                     String key = method.getReturnType();
                     if (StringUtils.contains(key, ".")) {
                        key = key.substring(key.lastIndexOf(".") + 1);
                     }

                     parent = getCodeHints(key.toLowerCase());
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  return new CodeHintsClass();
               }
            } else {
               boolean foundx = false;

               for (CodeHintsField field : parent.getFields()) {
                  if (field.getName().equals(value)) {
                     parent = getCodeHints(value.toLowerCase());
                     foundx = true;
                     break;
                  }
               }

               if (!foundx) {
                  return new CodeHintsClass();
               }
            }
         }

         return Optional.ofNullable(parent).orElse(new CodeHintsClass());
      }
   }

   public static void hintsConfig(List<String> classNameList) {
      if (classNameList != null && classNameList.size() > 0) {
         for (String className : classNameList) {
            Class<?> clazz = ClassUtils.getClass(className);
            CodeHintsClass codeHintsClass = getCodeHintsClass(clazz);
            String classSimpleName = codeHintsClass.getSimpleName();
            register(classSimpleName, clazz);
         }
      }
   }

   public static String formatSourceCodes(RestJsonWrapperBean wrapper) {
      String sourceCodes = wrapper.getParamValue("script");
      if (StringUtils.isEmpty(sourceCodes)) {
         return null;
      } else {
         try {
            String code = new Formatter(JavaFormatterOptions.builder().style(Style.AOSP).build()).formatSource(sourceCodes);
            return code.replaceAll("@Autowired", "@Autowired\n   ");
         } catch (FormatterException var3) {
            throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.REMIND.EXCEPTION.SCRIPT_FORMAT_ERROR"), var3);
         }
      }
   }

   public static Class<?> getClassByName(String className) {
      return classCache.get(className);
   }

   public static Set<String> getCodeHintsKeyWords() {
      return codeHintsKeyWords;
   }

   private static boolean match(CodeHintsMethod method, String simpleName, String[] params) {
      if (!method.getName().equals(simpleName)) {
         return false;
      } else if (method.getParameters().length != params.length) {
         return false;
      } else {
         for (int i = 0; i < method.getParameters().length; i++) {
            CodeHintsMethodParameter parameter = method.getParameters()[i];
            String param = params[i].trim();
            if (!StringUtils.startsWith(param, parameter.getType() + " ")) {
               return false;
            }
         }

         return true;
      }
   }
}
