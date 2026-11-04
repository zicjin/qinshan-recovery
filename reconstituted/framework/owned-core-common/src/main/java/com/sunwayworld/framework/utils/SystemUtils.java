package com.sunwayworld.framework.utils;

import com.sun.management.OperatingSystemMXBean;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.ProcessException;
import com.sunwayworld.framework.exception.UnexpectedException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SystemUtils {
   private static final Logger logger = LogManager.getLogger(SystemUtils.class);
   private static final boolean IS_OS_WINDOWS = StringUtils.startsWith(getSystemProperty("os.name"), "Windows");
   private static final boolean IS_OS_MAC = StringUtils.startsWith(getSystemProperty("os.name"), "Mac");
   private static final boolean IS_OS_ARM = "aarch64".equals(getSystemProperty("os.arch"));
   private static final boolean IS_OS_LOONGARCH = "loongarch64".equals(getSystemProperty("os.arch"));

   public static final boolean isWindows() {
      return IS_OS_WINDOWS;
   }

   public static final boolean isLinux() {
      return !IS_OS_WINDOWS && !IS_OS_MAC;
   }

   public static final boolean isArm() {
      return IS_OS_ARM;
   }

   public static final boolean isMac() {
      return IS_OS_MAC;
   }

   public static final boolean isLoongArch() {
      return IS_OS_LOONGARCH;
   }

   public static final Path getWorkingPath() {
      return Paths.get(System.getProperty("user.dir"));
   }

   public static final String execAndReturn(String cmd) {
      try {
         Process process = Runtime.getRuntime().exec(cmd);
         SystemUtils.OutputInterceptor error = new SystemUtils.OutputInterceptor(process.getErrorStream());
         error.start();
         String output = getInputStreamContent(process.getInputStream());
         process.waitFor();
         return output;
      } catch (IOException var4) {
         throw new FileException(var4);
      } catch (InterruptedException var5) {
         throw new ProcessException(var5);
      }
   }

   public static final void exec(String cmd) {
      try {
         Process process = Runtime.getRuntime().exec(cmd);
         SystemUtils.OutputInterceptor error = new SystemUtils.OutputInterceptor(process.getErrorStream());
         SystemUtils.OutputInterceptor output = new SystemUtils.OutputInterceptor(process.getInputStream());
         error.start();
         output.start();
         process.waitFor();
      } catch (IOException var4) {
         throw new FileException(var4);
      } catch (InterruptedException var5) {
         throw new ProcessException(var5);
      }
   }

   public static final List<String> getMacAddresses() {
      throw new UnsupportedOperationException("Sunway MAC binding capability removed");
   }

   public static final String getOSName() {
      return getSystemProperty("os.name");
   }

   public static int getCpuRatio() {
      OperatingSystemMXBean osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
      return (int)(osmxb.getSystemCpuLoad() * 100.0);
   }

   public static int getMemoryUsageRatio() {
      Runtime run = Runtime.getRuntime();
      long max = run.maxMemory();
      long total = run.totalMemory();
      long free = run.freeMemory();
      return (int)((total - free) * 100L / max);
   }

   private static String getInputStreamContent(InputStream is) {
      String var2;
      try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
         StringBuilder info = new StringBuilder();
         String line = null;

         while ((line = br.readLine()) != null) {
            if (info.length() > 0) {
               info.append("\n");
            }

            info.append(line);
         }

         return info.toString();
      } catch (IOException var31) {
         var2 = "";
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (IOException var27) {
            }
         }
      }

      return var2;
   }

   private static final String getSystemProperty(String property) {
      try {
         return System.getProperty(property);
      } catch (SecurityException var2) {
         throw new UnexpectedException(var2);
      }
   }

   private static class OutputInterceptor extends Thread {
      private InputStream is;

      public OutputInterceptor(InputStream is) {
         this.is = is;
      }

      @Override
      public void run() {
         String result = SystemUtils.getInputStreamContent(this.is);
         if (!StringUtils.isEmpty(result)) {
            SystemUtils.logger.info(result);
         }
      }
   }
}
