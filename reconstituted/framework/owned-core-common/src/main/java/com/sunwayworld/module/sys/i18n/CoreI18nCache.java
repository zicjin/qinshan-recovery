package com.sunwayworld.module.sys.i18n;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.i18n.DatabaseDrivenMessageSource;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

public class CoreI18nCache {
   public static final String REDIS_I18N_MD5_NAME = "PERM_$I18N";
   public static final String REDIS_I18N_MD5_KEY = "MD5";
   private static final Logger logger = LogManager.getLogger(CoreI18nCache.class);
   private Map<String, Map<String, String>> fileI18nMap;
   private Map<String, CoreI18nCache.I18nStore> cache = new ConcurrentHashMap<>();
   private static volatile CoreI18nCache instance;

   private CoreI18nCache() {
      this.loadFileI18n();
      SpringUtils.getBean(TenantManager.class).call("LOAD_I18N", () -> this.load());
   }

   public static CoreI18nCache instance() {
      if (instance == null) {
         synchronized (CoreI18nCache.class) {
            if (instance == null) {
               instance = new CoreI18nCache();
            }
         }
      }

      return instance;
   }

   public Map<String, String> getI18nLocaleMap(String localeCode) {
      return Optional.ofNullable(this.getI18nMap()).orElseGet(() -> CollectionUtils.emptyMap()).get(StringUtils.lowerCase(localeCode));
   }

   public synchronized void reload() {
      ExecutorHelper.submit(this::load);
   }

   private Map<String, Map<String, String>> getI18nMap() {
      Object value = RedisHelper.get("PERM_$I18N", "MD5");
      CoreI18nCache.I18nStore store = this.cache.get(TenantContext.getTenant());
      if ((store == null || store.getMd5() == null || value == null || !store.getMd5().equals(value)) && (store == null || store.getCounter().get() == 0)) {
         this.reload();
      }

      return store.getI18nMap();
   }

   private void loadFileI18n() {
      Environment environment = ApplicationContextHelper.getBean(Environment.class);
      String baseNames = environment.getProperty("spring.messages.basename");
      Map<String, Map<String, String>> fileMap = new TreeMap<>();
      CoreI18nConfigService configService = ApplicationContextHelper.getBean(CoreI18nConfigService.class);
      List<CoreI18nConfigBean> configList = new ArrayList<>();
      SpringUtils.getBean(TenantManager.class).masterCall("I18N_CONFIG", () -> configList.addAll(configService.selectAll()));

      for (CoreI18nConfigBean config : configList) {
         String locale = config.getId().toLowerCase();
         Map<String, String> messageMap = new TreeMap<>();

         for (String baseName : StringUtils.split(baseNames, ",")) {
            String name = "/" + baseName + "_" + locale + ".properties";

            try (InputStream is = DatabaseDrivenMessageSource.class.getResourceAsStream(name)) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
               String line = null;

               while ((line = reader.readLine()) != null) {
                  if (!StringUtils.startsWith(line.trim(), "#") && !StringUtils.isBlank(line)) {
                     int index = line.indexOf("=");
                     if (index > 0) {
                        String code = line.substring(0, index).trim();
                        if (!messageMap.containsKey(code)) {
                           String value = line.substring(index + 1).trim();
                           messageMap.put(code, value);
                        }
                     }
                  }
               }
            } catch (Exception var32) {
               logger.warn("Unable load message properties from [" + name + "]");
            }
         }

         fileMap.put(locale, messageMap);
      }

      this.fileI18nMap = fileMap;
   }

   private void load() {
      String tenant = TenantContext.getTenant();
      CoreI18nCache.I18nStore store = this.cache.get(tenant);
      if (store == null) {
         synchronized (this.cache) {
            if (store == null) {
               store = new CoreI18nCache.I18nStore();
               this.cache.put(tenant, store);
            }
         }
      }

      int oldValue = store.getCounter().getAndIncrement();
      if (oldValue == 0) {
         store.setMd5(null);
         RedisHelper.evict("PERM_$I18N", "MD5");
         List<CoreI18nMessageBean> messageList = new ArrayList<>();
         CoreI18nMessageService messageService = ApplicationContextHelper.getBean(CoreI18nMessageService.class);
         int qty = messageService.getDao().count();
         int loop = (int)Math.ceil(Double.valueOf("" + qty) / 2000.0);
         AtomicInteger atomic = new AtomicInteger();

         for (int i = 0; i < loop; i++) {
            atomic.incrementAndGet();
            int j = i + 1;
            ExecutorHelper.submit(() -> {
               try {
                  MybatisPageHelper.setDataOnly();
                  PageRequest page = new PageRequest();
                  page.setPageNumber(j);
                  page.setPageSize(2000);
                  PageRowBounds rowBounds = new PageRowBounds(page);
                  List<CoreI18nMessageBean> selectedMessageList = messageService.selectPagination(new MapperParameter(), rowBounds).getRows();
                  synchronized (messageList) {
                     if (!CollectionUtils.isEmpty(selectedMessageList)) {
                        messageList.addAll(selectedMessageList);
                     }
                  }
               } finally {
                  atomic.decrementAndGet();
                  MybatisPageHelper.removeDataOnly();
               }
            });
         }

         while (atomic.get() > 0) {
            try {
               Thread.sleep(1L);
            } catch (InterruptedException var14) {
            }
         }

         Map<String, Map<String, String>> map = new TreeMap<>();

         for (CoreI18nMessageBean message : messageList) {
            String localeId = message.getI18nConfigId().toLowerCase();
            Map<String, String> i18nMap = map.get(localeId);
            if (i18nMap == null) {
               i18nMap = new TreeMap<>();
               map.put(localeId, i18nMap);
            }

            i18nMap.put(message.getExt$Item("code"), message.getMessage());
         }

         for (Entry<String, Map<String, String>> entry : this.fileI18nMap.entrySet()) {
            if (map.containsKey(entry.getKey())) {
               map.get(entry.getKey()).putAll(entry.getValue());
            } else {
               map.put(entry.getKey(), entry.getValue());
            }
         }

         store.setI18nMap(map);
         if (store.getCounter().getAndSet(0) > 1) {
            this.load();
         } else {
            String temp = EncryptUtils.MD5Encrypt(map.toString());
            RedisHelper.put("PERM_$I18N", "MD5", temp);
            store.setMd5(temp);
         }
      }
   }

   private static class I18nStore {
      private Map<String, Map<String, String>> i18nMap;
      private AtomicInteger counter = new AtomicInteger(0);
      private String md5;

      private I18nStore() {
      }

      public Map<String, Map<String, String>> getI18nMap() {
         return this.i18nMap;
      }

      public void setI18nMap(Map<String, Map<String, String>> i18nMap) {
         this.i18nMap = i18nMap;
      }

      public AtomicInteger getCounter() {
         return this.counter;
      }

      public String getMd5() {
         return this.md5;
      }

      public void setMd5(String md5) {
         this.md5 = md5;
      }
   }
}
