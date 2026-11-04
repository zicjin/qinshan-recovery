package com.sunwayworld.framework.jdk.tools;

import com.alibaba.fastjson.JSONObject;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sunwayworld.framework.exception.CompileException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.jdk.hints.CodeHintsClass;
import com.sunwayworld.framework.jdk.hints.core.CodeHintsManager;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager.Location;

public abstract class JavaCompilerManager {
   private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
   private static Context context;
   private static StandardJavaFileManager stdManager;
   private static MemoryJavaFileManager manager;
   private static final List<String> importKeyWordList;
   private static final List<String> ignoreWordList;

   public static String getClassName(String sourceCode) {
      Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
      Matcher matcher = pattern.matcher(sourceCode);
      if (matcher.find()) {
         return matcher.group(1);
      } else {
         throw new ApplicationRuntimeException("JDK.TOOLS.SOURCE_CODE.NO_CLASSNAME");
      }
   }

   public static CompileMultipleResult compile(Map<String, String> sourceMap) {
      CompileMultipleResult result = new CompileMultipleResult();
      List<JavaFileObject> jfoList = new ArrayList<>();
      sourceMap.forEach((k, v) -> jfoList.add(manager.makeStringSource(k, v)));
      DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector<>();
      List<String> options = new ArrayList<>();
      options.add("-XDuseUnsharedTable");
      CompilationTask task = compiler.getTask(null, manager, dc, options, null, jfoList);
      if (task.call()) {
         Map<String, byte[]> bytesMap = new HashMap<>();
         sourceMap.keySet().forEach(k -> {
            if (manager.getClassBytes(k) != null) {
               bytesMap.putAll(manager.getMultipleClassBytes(k));
            }
         });
         result.setBytesMap(bytesMap);
      } else {
         Map<String, String> errorMessageMap = new HashMap<>();
         dc.getDiagnostics().forEach(e -> {
            String message = e.getMessage(Locale.ENGLISH);
            if (e.getSource() != null) {
               String name = StringUtils.removeStart(e.getSource().getName(), "/");
               name = name.substring(0, name.indexOf("."));
               errorMessageMap.put(name, message);
            }
         });
         result.setErrorMessageMap(errorMessageMap);
      }

      return result;
   }

   public static Map<String, byte[]> compile(String sourceCode) {
      return compile(getClassName(sourceCode), sourceCode);
   }

   private static Map<String, byte[]> compile(String className, String sourceCode) {
      JavaFileObject javaFileObject = manager.makeStringSource(className, sourceCode);
      DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector<>();
      List<String> options = new ArrayList<>();
      options.add("-XDuseUnsharedTable");
      CompilationTask task = compiler.getTask(null, manager, dc, options, null, Arrays.asList(javaFileObject));
      if (task.call()) {
         return manager.getMultipleClassBytes(className);
      } else {
         List<String> importList = new ArrayList<>();
         List<Map<String, Object>> list = dc.getDiagnostics().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            String message = e.getMessage(Locale.ENGLISH);
            if (message.contains("lombok.javac.apt.LombokProcessor")) {
               map.put("lineNumber", -1);
            } else {
               map.put("lineNumber", e.getLineNumber());
            }

            map.put("message", message);
            parseNoImportMessage(map, message, importList);
            return map;
         }).collect(Collectors.toList());
         throw new CompileException(JSONObject.toJSONString(list));
      }
   }

   private static void parseNoImportMessage(Map<String, Object> map, String message, List<String> importList) {
      String[] messages = message.split("\n");
      String importKeyWord = importKeyWordList.stream().filter(f -> message.replaceAll(" ", "").contains(f)).collect(Collectors.joining());
      if (!StringUtils.isEmpty(importKeyWord)) {
         String noImportClassName = Arrays.stream(messages)
            .filter(f -> f.replaceAll(" ", "").contains(importKeyWord))
            .map(a -> a.replaceAll(" ", "").split(importKeyWord)[1])
            .collect(Collectors.joining());
         if (!StringUtils.isEmpty(noImportClassName)) {
            char firstChar = noImportClassName.trim().charAt(0);
            if (firstChar >= 'a' && firstChar <= 'z') {
               return;
            }

            if (ignoreWordList.contains(noImportClassName.trim().toLowerCase())) {
               return;
            }

            CodeHintsClass hintsClass = CodeHintsManager.getCodeHints(noImportClassName.trim().toLowerCase());
            if (hintsClass != null && !StringUtils.isEmpty(hintsClass.getName()) && hintsClass.getName().indexOf(".") > -1) {
               String importContent = String.format("import %s;", hintsClass.getName());
               if (!importList.contains(importContent)) {
                  map.put("type", "import");
                  map.put("content", importContent);
                  importList.add(importContent);
               }
            }
         }
      }
   }

   static {
      if (compiler == null) {
         if (StringUtils.containsIgnoreCase(System.getProperty("java.runtime.name"), "OpenJDK")) {
            throw new ApplicationRuntimeException("Please install java-1.8.0-openjdk and java-1.8.0-openjdk-devel!");
         } else {
            throw new ApplicationRuntimeException("Please contact sunwayworld!");
         }
      } else {
         context = new Context();
         context.put(Log.outKey, new PrintWriter(System.err, true));
         stdManager = new JavaCompilerManager.SpringJavaFileManager(context, true, null);
         manager = MemoryJavaFileManager.getInstance(stdManager);
         importKeyWordList = Arrays.asList("符号:类", "symbol:class", "符号:变量", "symbol:variable");
         ignoreWordList = Arrays.asList("id", "string", "long", "byte", "short", "int", "integer", "float", "double", "char", "boolean");
      }
   }

   private static class SpringJavaFileManager extends JavacFileManager {
      public SpringJavaFileManager(Context context, boolean b, Charset charset) {
         super(context, b, charset);
      }

      @Override
      public ClassLoader getClassLoader(Location location) {
         nullCheck(location);
         Iterable<? extends File> fileIterable = this.getLocation(location);
         if (fileIterable == null) {
            return null;
         } else {
            ListBuffer<URL> listBuffer = new ListBuffer<>();

            for (File file : fileIterable) {
               try {
                  listBuffer.append(file.toURI().toURL());
               } catch (MalformedURLException var7) {
                  throw new ApplicationRuntimeException(var7);
               }
            }

            return this.getClassLoader(listBuffer.toArray(new URL[0]));
         }
      }

      @Override
      protected ClassLoader getClassLoader(URL[] urls) {
         ClassLoader classLoader = this.getClass().getClassLoader();

         try {
            Class<?> launchedClassLoaderClass = Class.forName("org.springframework.boot.loader.LaunchedURLClassLoader");
            Class<?>[] paramClasses = new Class[]{URL[].class, ClassLoader.class};
            Constructor<?> constructor = launchedClassLoaderClass.getConstructor(paramClasses);
            return (ClassLoader)constructor.newInstance(urls, classLoader);
         } catch (Throwable var6) {
            return new URLClassLoader(urls, classLoader);
         }
      }
   }
}
