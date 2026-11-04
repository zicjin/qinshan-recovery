package com.sunwayworld.cloud.module.lcdp.resource;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageI18nService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LcdpResourceCache {
   public static final String REDIS_RESOURCE_MD5_NAME = "PERM_$RESOURCE";
   public static final String REDIS_RESOURCE_UPDATE_MD5_NAME = "PERM_$RESOURCE_UPDATE";
   public static final String REDIS_PAGE_COMP_MD5_NAME = "PERM_$PAGE_COMP";
   public static final String REDIS_PAGE_COMP_UPDATE_MD5_NAME = "PERM_$PAGE_COMP_UPDATE";
   public static final String REDIS_PAGE_I18N_MD5_NAME = "PERM_$PAGE_I18N";
   public static final String REDIS_PAGE_I18N_UPDATE_MD5_NAME = "PERM_$PAGE_I18N_UPDATE";
   private static final Map<String, LcdpResourceBean> memoryResourceContentMap = new HashMap<>();
   private static final Map<Long, List<LcdpModulePageCompBean>> memoryPageCompMap = new HashMap<>();
   private static final Map<Long, Map<String, Map<String, String>>> memoryPageI18nMap = new HashMap<>();
   private static volatile LcdpResourceCache instance;

   private LcdpResourceCache() {
      this.init();
   }

   public static LcdpResourceCache instance() {
      if (instance == null) {
         synchronized (LcdpResourceCache.class) {
            if (instance == null) {
               instance = new LcdpResourceCache();
            }
         }
      }

      return instance;
   }

   private void init() {
      LcdpResourceService resourceService = ApplicationContextHelper.getBean(LcdpResourceService.class);
      SearchFilter filter = SearchFilter.instance().match("RESOURCECATEGORY", "view").filter(MatchPattern.EQ);
      filter.match("EFFECTVERSION", null).filter(MatchPattern.DIFFER).match("DELETEFLAG", "0").filter(MatchPattern.EQ);
      List<LcdpResourceBean> resourceList = resourceService.selectListByFilter(filter, new Order[0]);
      List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
      LcdpResourceHistoryService historyService = ApplicationContextHelper.getBean(LcdpResourceHistoryService.class);
      List<LcdpResourceHistoryBean> resourceHistoryList = historyService.selectListByFilter(
         SearchFilter.instance().match("resourceId", resourceIdList).filter(MatchPattern.OR).match("effectFlag", "yes").filter(MatchPattern.EQ), new Order[0]
      );
      List<Long> histroyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
      LcdpModulePageCompService modulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageCompService.class);
      List<LcdpModulePageCompBean> modulePageCompList = modulePageCompService.selectListByFilter(
         SearchFilter.instance().match("modulePageHistoryId", histroyIdList).filter(MatchPattern.OR), new Order[0]
      );
      Map<Long, List<LcdpModulePageCompBean>> map = modulePageCompList.stream().collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageId));
      map.forEach(
         (resourceId, pageCompList) -> {
            LcdpModulePageCompBean pageComp = pageCompList.stream().findFirst().get();
            RedisHelper.put(
               "PERM_$PAGE_COMP",
               resourceId.toString(),
               EncryptUtils.MD5Encrypt(pageComp.getModulePageVersion() == null ? "1" : pageComp.getModulePageVersion().toString())
            );
         }
      );
      memoryPageCompMap.putAll(map);
   }

   public static synchronized void put(List<LcdpResourceBean> resourceList) {
      if (!resourceList.isEmpty()) {
         resourceList.forEach(resource -> {
            String path = resource.getPath();
            put(path, resource);
         });
      }
   }

   public static synchronized void put(String path, LcdpResourceBean resource) {
      memoryResourceContentMap.put(path, resource);
      String classNameMd5 = RedisHelper.get("PERM_$RESOURCE", path);
      String newClassNameMd5 = EncryptUtils.MD5Encrypt(resource.getClassName());
      if (classNameMd5 == null || !StringUtils.equals(classNameMd5, newClassNameMd5)) {
         RedisHelper.put("PERM_$RESOURCE", path, newClassNameMd5);
         if (!StringUtils.isEmpty(classNameMd5)) {
            RedisHelper.put("PERM_$RESOURCE_UPDATE", path, newClassNameMd5);
         }
      }
   }

   public static void evict(List<LcdpResourceBean> resourceList) {
      if (!resourceList.isEmpty()) {
         resourceList.forEach(resource -> evict(resource.getPath()));
      }
   }

   public static void evict(String path) {
      memoryResourceContentMap.remove(path);
      String classNameMd5 = RedisHelper.get("PERM_$RESOURCE", path);
      if (!StringUtils.isEmpty(classNameMd5)) {
         RedisHelper.evict("PERM_$RESOURCE", path);
      }
   }

   public static synchronized void putPageCompList(List<LcdpModulePageCompBean> modulePageCompList) {
      if (!modulePageCompList.isEmpty()) {
         Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompsMap = modulePageCompList.stream()
            .collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageId));
         resourceId2PageCompsMap.forEach((resourceId, pageCompList) -> putPageCompList(resourceId, (List<LcdpModulePageCompBean>)pageCompList));
      }
   }

   public static synchronized void putPageCompList(Long resourceId, List<LcdpModulePageCompBean> pageCompList) {
      memoryPageCompMap.put(resourceId, pageCompList);
      String pageCompVersion = RedisHelper.get("PERM_$PAGE_COMP", resourceId.toString());
      if (!pageCompList.isEmpty()) {
         LcdpModulePageCompBean pageComp = pageCompList.stream().findFirst().get();
         String newPageCompVersion = EncryptUtils.MD5Encrypt(pageComp.getModulePageVersion() == null ? "1" : pageComp.getModulePageVersion().toString());
         if (pageCompVersion == null || !StringUtils.equals(pageCompVersion, newPageCompVersion)) {
            RedisHelper.put("PERM_$PAGE_COMP", resourceId.toString(), newPageCompVersion);
            if (!StringUtils.isEmpty(pageCompVersion)) {
               RedisHelper.put("PERM_$PAGE_COMP_UPDATE", resourceId.toString(), newPageCompVersion);
            }
         }
      }
   }

   public static void evictPageCompList(List<LcdpModulePageCompBean> modulePageCompList) {
      if (!modulePageCompList.isEmpty()) {
         Map<Long, List<LcdpModulePageCompBean>> resourceId2PageCompList = modulePageCompList.stream()
            .collect(Collectors.groupingBy(LcdpModulePageCompBean::getModulePageId));
         resourceId2PageCompList.forEach((resourceId, pageCompList) -> evictPageComp(resourceId));
      }
   }

   public static void evictPageComp(Long resourceId) {
      memoryResourceContentMap.remove(resourceId);
      String pageCompVersion = RedisHelper.get("PERM_$PAGE_COMP", resourceId.toString());
      if (!StringUtils.isEmpty(pageCompVersion)) {
         RedisHelper.evict("PERM_$PAGE_COMP", resourceId.toString());
      }
   }

   public static LcdpResourceBean getMemoryContent(String path) {
      String classNameMd5 = RedisHelper.get("PERM_$RESOURCE", path);
      if (classNameMd5 == null) {
         LcdpResourceService resourceService = ApplicationContextHelper.getBean(LcdpResourceService.class);
         LcdpResourceBean resource = resourceService.selectFirstByFilter(
            SearchFilter.instance()
               .match("PATH", path)
               .filter(MatchPattern.EQ)
               .match("DELETEFLAG", "0")
               .filter(MatchPattern.EQ)
               .match("EFFECTVERSION", null)
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
         if (resource == null) {
            return null;
         } else {
            put(path, resource);
            return resource;
         }
      } else {
         String updateClassNameMD5 = RedisHelper.get("PERM_$RESOURCE_UPDATE", path);
         if (!StringUtils.isEmpty(updateClassNameMD5) && !StringUtils.equals(classNameMd5, updateClassNameMD5)) {
            return null;
         } else {
            LcdpResourceBean currentResource = memoryResourceContentMap.get(path);
            if (!ObjectUtils.isEmpty(currentResource)) {
               String className = currentResource.getClassName();
               String currentClassNameMd5 = EncryptUtils.MD5Encrypt(className);
               if (StringUtils.equals(classNameMd5, currentClassNameMd5)) {
                  return memoryResourceContentMap.get(path);
               }
            }

            LcdpResourceService resourceService = ApplicationContextHelper.getBean(LcdpResourceService.class);
            LcdpResourceBean resource = resourceService.selectFirstByFilter(
               SearchFilter.instance().match("PATH", path).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
            if (resource == null) {
               return null;
            } else {
               put(path, resource);
               return resource;
            }
         }
      }
   }

   public static List<LcdpModulePageCompBean> getMemoryPageComp(Long resourceId, Long resourceHistoryId) {
      String pageCompMD5 = RedisHelper.get("PERM_$PAGE_COMP", resourceId.toString());
      if (pageCompMD5 == null) {
         LcdpModulePageCompService modulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageCompService.class);
         List<LcdpModulePageCompBean> pageCompList = modulePageCompService.selectListByFilter(
            SearchFilter.instance().match("modulePageHistoryId", resourceHistoryId).filter(MatchPattern.EQ), new Order[0]
         );
         if (pageCompList.isEmpty()) {
            return pageCompList;
         } else {
            putPageCompList(resourceId, pageCompList);
            return pageCompList;
         }
      } else {
         String updatePageComp = RedisHelper.get("PERM_$PAGE_COMP_UPDATE", resourceId.toString());
         if (!StringUtils.isEmpty(updatePageComp) && !StringUtils.equals(pageCompMD5, updatePageComp)) {
            LcdpModulePageCompService modulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageCompService.class);
            List<LcdpModulePageCompBean> pageCompList = modulePageCompService.selectListByFilter(
               SearchFilter.instance().match("modulePageHistoryId", resourceHistoryId).filter(MatchPattern.EQ), new Order[0]
            );
            if (pageCompList.isEmpty()) {
               return pageCompList;
            } else {
               putPageCompList(resourceId, pageCompList);
               return pageCompList;
            }
         } else {
            return memoryPageCompMap.get(resourceId);
         }
      }
   }

   public static Map<String, Map<String, String>> getMemoryPageI18n(Long resourceId, Long resourceHistoryId) {
      String pageI18nMD5 = RedisHelper.get("PERM_$PAGE_I18N", resourceId.toString());
      if (pageI18nMD5 != null && !ObjectUtils.isEmpty(memoryPageI18nMap.get(resourceId))) {
         String updatePageI18nMD5 = RedisHelper.get("PERM_$PAGE_I18N_UPDATE", resourceId.toString());
         if (!StringUtils.isEmpty(updatePageI18nMD5) && !StringUtils.equals(pageI18nMD5, updatePageI18nMD5)) {
            LcdpModulePageI18nService modulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageI18nService.class);
            Map<String, Map<String, String>> pageCompList = modulePageCompService.selectPageI18nMessage(resourceHistoryId);
            putPageI18n(resourceId, resourceHistoryId, pageCompList);
            return pageCompList;
         } else {
            return memoryPageI18nMap.get(resourceId);
         }
      } else {
         LcdpModulePageI18nService modulePageCompService = ApplicationContextHelper.getBean(LcdpModulePageI18nService.class);
         Map<String, Map<String, String>> pageCompList = modulePageCompService.selectPageI18nMessage(resourceHistoryId);
         putPageI18n(resourceId, resourceHistoryId, pageCompList);
         return pageCompList;
      }
   }

   public static synchronized void putPageI18n(Long resourceId, Long resourceHistoryId, Map<String, Map<String, String>> pageI18n) {
      memoryPageI18nMap.put(resourceId, pageI18n);
      String pageI18nVersionId = RedisHelper.get("PERM_$PAGE_I18N", resourceId.toString());
      String newPageI18nVersionId = EncryptUtils.MD5Encrypt(resourceHistoryId.toString());
      if (pageI18nVersionId == null || !StringUtils.equals(pageI18nVersionId, newPageI18nVersionId)) {
         RedisHelper.put("PERM_$PAGE_I18N", resourceId.toString(), newPageI18nVersionId);
         if (!StringUtils.isEmpty(pageI18nVersionId)) {
            RedisHelper.put("PERM_$PAGE_I18N_UPDATE", resourceId.toString(), newPageI18nVersionId);
         }
      }
   }

   public static void evictPageI18nList(List<Long> resourceIdList) {
      if (!resourceIdList.isEmpty()) {
         resourceIdList.forEach(id -> evictPageI18n(id));
      }
   }

   public static void evictPageI18n(Long resourceId) {
      memoryPageI18nMap.remove(resourceId);
      String pageCompVersion = RedisHelper.get("PERM_$PAGE_I18N", resourceId.toString());
      if (!StringUtils.isEmpty(pageCompVersion)) {
         RedisHelper.evict("PERM_$PAGE_I18N", resourceId.toString());
      }
   }
}
