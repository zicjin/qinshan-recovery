package com.sunwayworld.framework.jdk.tools;

import com.sun.tools.javac.file.BaseFileObject;
import com.sun.tools.javac.file.JavacFileManager;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;
import org.springframework.boot.loader.jar.JarFile;

class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
   private final Map<String, byte[]> classBytes = new ConcurrentHashMap<>();
   private final Map<String, List<JavaFileObject>> springBootInfoClassPackageMap = new HashMap<>();
   private final Map<String, List<JavaFileObject>> classPackageMap = new HashMap<>();
   private static MemoryJavaFileManager instance;

   public static MemoryJavaFileManager getInstance(JavaFileManager fileManager) {
      if (instance == null) {
         synchronized (MemoryJavaFileManager.class) {
            if (instance == null) {
               instance = new MemoryJavaFileManager(fileManager);
            }
         }
      }

      return instance;
   }

   private MemoryJavaFileManager(JavaFileManager fileManager) {
      super(fileManager);
      if (DynamicClassLoader.isJar()) {
         if (DynamicClassLoader.isServerStartUp()) {
            this.loadBootInfClass();
         } else {
            try {
               JarFile sourceJar = new JarFile(new File(DynamicClassLoader.getSourcePath()));

               for (JarEntry classEntry : sourceJar.stream()
                  .filter(e -> e.getName().endsWith(".class") && e.getName().startsWith("BOOT-INF/classes"))
                  .collect(Collectors.toList())) {
                  String classEntryName = classEntry.getName().substring(17);
                  String packgeName = classEntryName.substring(0, classEntryName.lastIndexOf("/")).replace("/", ".");
                  List<JavaFileObject> packageClassFileList = (List<JavaFileObject>)(this.springBootInfoClassPackageMap.containsKey(packgeName)
                     ? this.springBootInfoClassPackageMap.get(packgeName)
                     : new ArrayList<>());
                  packageClassFileList.add(
                     new MemoryJavaFileManager.MemorySpringBootInfoJavaClassObject(
                        classEntryName.replace("/", ".").replace(".class", ""),
                        new URL(sourceJar.getUrl(), classEntry.getName()),
                        (JavacFileManager)fileManager
                     )
                  );
                  this.springBootInfoClassPackageMap.put(packgeName, packageClassFileList);
               }

               for (JarEntry entry : sourceJar.stream().filter(e -> e.getName().endsWith(".jar")).collect(Collectors.toList())) {
                  JarFile libJar = sourceJar.getNestedJarFile(sourceJar.getEntry(entry.getName()));
                  if (!libJar.getName().contains("tools.jar")) {
                     Enumeration<JarEntry> libJarEntries = libJar.entries();

                     while (libJarEntries.hasMoreElements()) {
                        JarEntry libJarEntry = libJarEntries.nextElement();
                        String classPath = libJarEntry.getName().replace("/", ".");
                        if (classPath.endsWith(".class") && libJarEntry.getName().lastIndexOf("/") != -1) {
                           String packgeName = classPath.substring(0, libJarEntry.getName().lastIndexOf("/"));
                           List<JavaFileObject> packageClassFileList = (List<JavaFileObject>)(this.springBootInfoClassPackageMap.containsKey(packgeName)
                              ? this.springBootInfoClassPackageMap.get(packgeName)
                              : new ArrayList<>());
                           packageClassFileList.add(
                              new MemoryJavaFileManager.MemorySpringBootInfoJavaClassObject(
                                 libJarEntry.getName().replace("/", ".").replace(".class", ""),
                                 new URL(libJar.getUrl(), libJarEntry.getName()),
                                 (JavacFileManager)fileManager
                              )
                           );
                           this.springBootInfoClassPackageMap.put(packgeName, packageClassFileList);
                        }
                     }
                  }
               }
            } catch (IOException var13) {
               throw new ApplicationRuntimeException(var13);
            }
         }
      }
   }

   private void loadBootInfClass() {
      try {
         String jarPath = DynamicClassLoader.getSourcePath().replaceAll("\\\\", "/");
         String jarFolder = jarPath.substring(0, jarPath.lastIndexOf("/"));
         String classFolder = jarFolder.substring(0, jarFolder.lastIndexOf("/")) + "/classes";

         for (File file : FileUtils.getFileList(Paths.get(classFolder))) {
            String filePath = file.getPath().replaceAll("\\\\", "/");
            if (filePath.endsWith(".class")) {
               String className = filePath.split("/BOOT-INF/classes/")[1];
               String packgeName = className.substring(0, className.lastIndexOf("/")).replace("/", ".");
               List<JavaFileObject> packageClassFileList = (List<JavaFileObject>)(this.springBootInfoClassPackageMap.containsKey(packgeName)
                  ? this.springBootInfoClassPackageMap.get(packgeName)
                  : new ArrayList<>());
               packageClassFileList.add(
                  new MemoryJavaFileManager.MemorySpringBootInfoJavaClassObject(
                     className.replace("/", ".").replace(".class", ""), file.toURI().toURL(), (JavacFileManager)this.fileManager
                  )
               );
               this.springBootInfoClassPackageMap.put(packgeName, packageClassFileList);
            }
         }

         File[] jarFiles = new File(jarFolder).listFiles();

         for (File filex : jarFiles) {
            JarFile sourceJar = new JarFile(filex);

            for (JarEntry classEntry : sourceJar.stream().filter(e -> e.getName().endsWith(".class")).collect(Collectors.toList())) {
               String classEntryName = classEntry.getName().replaceAll("\\\\", "/");
               if (classEntryName.indexOf("/") >= 0) {
                  String packgeName = classEntryName.substring(0, classEntryName.lastIndexOf("/")).replace("/", ".");
                  List<JavaFileObject> packageClassFileList = (List<JavaFileObject>)(this.springBootInfoClassPackageMap.containsKey(packgeName)
                     ? this.springBootInfoClassPackageMap.get(packgeName)
                     : new ArrayList<>());
                  packageClassFileList.add(
                     new MemoryJavaFileManager.MemorySpringBootInfoJavaClassObject(
                        classEntryName.replace("/", ".").replace(".class", ""),
                        new URL(sourceJar.getUrl(), classEntry.getName()),
                        (JavacFileManager)this.fileManager
                     )
                  );
                  this.springBootInfoClassPackageMap.put(packgeName, packageClassFileList);
               }
            }
         }
      } catch (IOException var17) {
         throw new ApplicationRuntimeException(var17);
      }
   }

   public byte[] getClassBytes(String name) {
      return this.classBytes.get(name);
   }

   public Map<String, byte[]> getMultipleClassBytes(String name) {
      Map<String, byte[]> byteMap = new HashMap<>();
      byteMap.put(name, this.classBytes.get(name));
      if (this.classBytes.keySet().stream().anyMatch(key -> key.startsWith(name + "$"))) {
         this.classBytes.entrySet().stream().filter(e -> e.getKey().startsWith(name + "$")).forEach(e -> byteMap.put(e.getKey(), e.getValue()));
      }

      return byteMap;
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public void close() throws IOException {
      this.classBytes.clear();
   }

   @Override
   public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
      if ("CLASS_PATH".equals(location.getName()) && DynamicClassLoader.isJar()) {
         List<JavaFileObject> result = this.springBootInfoClassPackageMap.get(packageName);
         if (result != null) {
            return result;
         }
      }

      Iterable<JavaFileObject> it = super.list(location, packageName, kinds, recurse);
      if (kinds.contains(Kind.CLASS)) {
         List<JavaFileObject> javaFileObjectList = this.classPackageMap.get(packageName);
         if (javaFileObjectList != null) {
            if (it != null) {
               for (JavaFileObject javaFileObject : it) {
                  javaFileObjectList.add(javaFileObject);
               }
            }

            return javaFileObjectList;
         }
      }

      return it;
   }

   @Override
   public String inferBinaryName(Location location, JavaFileObject file) {
      return file instanceof MemoryJavaFileManager.MemoryInputJavaFileObject
         ? ((MemoryJavaFileManager.MemoryInputJavaFileObject)file).inferBinaryName()
         : super.inferBinaryName(location, file);
   }

   @Override
   public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
      return (JavaFileObject)(kind == Kind.CLASS
         ? new MemoryJavaFileManager.MemoryOutputJavaFileObject(className)
         : super.getJavaFileForOutput(location, className, kind, sibling));
   }

   JavaFileObject makeStringSource(String className, String sourceCode) {
      String classPath = className.replace('.', '/') + Kind.SOURCE.extension;
      return new SimpleJavaFileObject(URI.create("string:///" + classPath), Kind.SOURCE) {
         public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(sourceCode);
         }
      };
   }

   void makeBinaryClass(String className, final byte[] bs) {
      JavaFileObject javaFileObject = new MemoryJavaFileManager.MemoryInputJavaFileObject(className, bs);
      int pos = className.lastIndexOf(46);
      String packageName = pos > 0 ? className.substring(0, pos) : "";
      List<JavaFileObject> javaFileObjectList = this.classPackageMap.get(packageName);
      if (javaFileObjectList == null) {
         List<JavaFileObject> var7 = new LinkedList();
         var7.add(javaFileObject);
         this.classPackageMap.put(packageName, var7);
      } else {
         javaFileObjectList.add(javaFileObject);
      }
   }

   private class MemoryInputJavaFileObject extends SimpleJavaFileObject {
      private String className;
      private byte[] bytes;

      MemoryInputJavaFileObject(String className, byte[] bytes) {
         super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
         this.className = className;
         this.bytes = bytes;
      }

      @Override
      public InputStream openInputStream() {
         return new ByteArrayInputStream(this.bytes);
      }

      public String inferBinaryName() {
         return this.className;
      }
   }

   private class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
      final String name;

      MemoryOutputJavaFileObject(String name) {
         super(URI.create("string:///" + name), Kind.CLASS);
         this.name = name;
      }

      @Override
      public OutputStream openOutputStream() {
         return new FilterOutputStream(new ByteArrayOutputStream()) {
            @Override
            public void close() throws IOException {
               this.out.close();
               ByteArrayOutputStream bos = (ByteArrayOutputStream)this.out;
               byte[] bytes = bos.toByteArray();
               MemoryJavaFileManager.this.classBytes.put(MemoryOutputJavaFileObject.this.name, bytes);
               MemoryJavaFileManager.this.makeBinaryClass(MemoryOutputJavaFileObject.this.name, bytes);
            }
         };
      }
   }

   private class MemorySpringBootInfoJavaClassObject extends BaseFileObject {
      private String className;
      private URL url;

      protected MemorySpringBootInfoJavaClassObject(String className, URL url, JavacFileManager javacFileManager) {
         super(javacFileManager);
         this.className = className;
         this.url = url;
      }

      @Override
      public Kind getKind() {
         return Kind.valueOf("CLASS");
      }

      @Override
      public boolean isNameCompatible(String simpleName, Kind kind) {
         return false;
      }

      @Override
      public URI toUri() {
         try {
            return this.url.toURI();
         } catch (URISyntaxException var2) {
            throw new ApplicationRuntimeException(var2);
         }
      }

      @Override
      public String getName() {
         return this.className;
      }

      @Override
      public InputStream openInputStream() throws IOException {
         return this.url.openStream();
      }

      @Override
      public OutputStream openOutputStream() throws IOException {
         return null;
      }

      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
         return null;
      }

      @Override
      public Writer openWriter() throws IOException {
         return null;
      }

      @Override
      public long getLastModified() {
         return 0L;
      }

      @Override
      public boolean delete() {
         return false;
      }

      @Override
      public boolean equals(Object arg0) {
         return false;
      }

      @Override
      public String getShortName() {
         return this.className.substring(this.className.lastIndexOf("."));
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      protected String inferBinaryName(Iterable<? extends File> arg0) {
         return this.className;
      }
   }
}
