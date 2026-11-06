package com.sunwayworld.cloud.boot.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.LcdpWebServiceContext;
import com.sunwayworld.cloud.module.lcdp.base.service.LcdpBaseService;
import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.cloud.module.lcdp.configparam.service.LcdpConfigParamService;
import com.sunwayworld.cloud.module.lcdp.resource.LcdpResourceCache;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageI18nService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.bean.LcdpResourceDocumentBean;
import com.sunwayworld.cloud.module.lcdp.resourcedocument.support.LuceneIndexManager;
import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.jdk.hints.core.CodeHintsManager;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.jdk.tools.LoadMultipleResult;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
   prefix = "sunway.lcdp",
   name = {"enable"},
   havingValue = "true",
   matchIfMissing = true
)
/**
 * Loads database-backed LCDP Java resources after the application context is
 * fully created.  ApplicationPreparedEvent is published before regular
 * {@code @Service} beans are instantiated, so a listener registered only as a
 * component never receives that event.  That left valid resource rows in the
 * database and cache while their dynamic service beans were absent.
 */
public class LcdpReadyListener implements ApplicationListener<ApplicationReadyEvent> {
   private boolean initialized;
   @Autowired
   private LcdpResourceService lcdpResourceService;
   @Autowired
   private LcdpResourceHistoryService lcdpResourceHistoryService;
   @Autowired
   private LcdpModulePageCompService lcdpModulePageCompService;
   @Autowired
   private LcdpModulePageI18nService lcdpModulePageI18nService;
   @Autowired
   private CoreI18nMessageService coreI18nMessageService;
   @Autowired
   private CoreAdminSelectConfigService coreAdminSelectConfigService;
   @Autowired
   private CoreConstantService coreConstantService;
   @Autowired
   private LcdpModulePageCompService modulePageCompService;
   @Autowired
   private LcdpConfigParamService lcdpConfigParamService;
   @Autowired
   @Lazy
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   private static final Logger log = LoggerFactory.getLogger(LcdpReadyListener.class);

   public synchronized void onApplicationEvent(ApplicationReadyEvent event) {
      if (initialized) {
         log.info("============>LCDP scripts already loaded; skipping duplicate ApplicationReadyEvent");
         return;
      }
      initialized = true;
      long now = System.currentTimeMillis();
      this.initAppUUID();
      SearchFilter filter = SearchFilter.instance().match("RESOURCECATEGORY", "java").filter(MatchPattern.EQ);
      filter.match("EFFECTVERSION", null)
         .filter(MatchPattern.DIFFER)
         .match("CLASSCONTENT", null)
         .filter(MatchPattern.DIFFER)
         .match("DELETEFLAG", "0")
         .filter(MatchPattern.EQ);
      List<LcdpResourceBean> resourceList = this.lcdpResourceService.selectListByFilter(filter, new Order[0]);
      List<LcdpReadyListener.SourceConfig> sourceConfigList = new ArrayList<>();

      for (LcdpResourceBean resource : resourceList) {
         if (StringUtils.isBlank(resource.getPath())) {
            log.error("============>资源表内容异常：" + resource.getResourceName() + "（" + resource.getId() + "）的path为空");
         } else {
            String className = JavaCompilerManager.getClassName(resource.getClassContent());
            String hintName = resource.getResourceName();
            String sourceCode = resource.getClassContent();
            sourceConfigList.add(new LcdpReadyListener.SourceConfig(className, hintName, sourceCode, resource.getPath()));
            LcdpResourceCache.put(resource.getPath(), resource);
         }
      }

      SearchFilter historyFilter = SearchFilter.instance()
         .match("RESOURCECATEGORY", "java")
         .filter(MatchPattern.EQ)
         .match("CLASSCONTENT", null)
         .filter(MatchPattern.DIFFER)
         .match("DELETEFLAG", "0")
         .filter(MatchPattern.EQ)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ);

      for (LcdpResourceHistoryBean resourceHistory : this.lcdpResourceHistoryService.selectListByFilter(historyFilter, new Order[0])) {
         if (StringUtils.isBlank(resourceHistory.getPath())) {
            log.error("============>资源历史表内容异常：" + resourceHistory.getResourceName() + "（" + resourceHistory.getId() + "）的path为空");
         } else {
            String className = JavaCompilerManager.getClassName(resourceHistory.getClassContent());
            String hintName = resourceHistory.getResourceName();
            String sourceCode = resourceHistory.getClassContent();
            sourceConfigList.add(new LcdpReadyListener.SourceConfig(className, hintName, sourceCode, resourceHistory.getPath()));
         }
      }

      List<LcdpReadyListener.SourceConfig> xlintSourceConfigList = new ArrayList<>();
      this.registerScript(sourceConfigList, xlintSourceConfigList);
      if (!xlintSourceConfigList.isEmpty()) {
         log.info("============>)对报错Xlint警告的类开始二次注册");
         this.registerScript(xlintSourceConfigList);
      }

      log.info("============>脚本已加载完毕，耗时：" + (System.currentTimeMillis() - now) / 1000L + "秒");
      long mapperStart = System.currentTimeMillis();
      SearchFilter historyMapperFilter = SearchFilter.instance()
         .match("RESOURCECATEGORY", "mapper")
         .filter(MatchPattern.EQ)
         .match("CONTENT", null)
         .filter(MatchPattern.DIFFER)
         .match("DELETEFLAG", "0")
         .filter(MatchPattern.EQ)
         .match("SUBMITFLAG", "no")
         .filter(MatchPattern.EQ)
         .match("PATH", LcdpMapperUtils.DIALECT_MAPPER_SUFFIX)
         .filter(MatchPattern.SE);
      List<LcdpResourceHistoryBean> mapperHistoryList = this.lcdpResourceHistoryService.selectListByFilter(historyMapperFilter, new Order[0]);
      SearchFilter mapperFilter = SearchFilter.instance()
         .match("RESOURCECATEGORY", "mapper")
         .filter(MatchPattern.EQ)
         .match("EFFECTVERSION", null)
         .filter(MatchPattern.DIFFER)
         .match("CONTENT", null)
         .filter(MatchPattern.DIFFER)
         .match("DELETEFLAG", "0")
         .filter(MatchPattern.EQ)
         .match("PATH", LcdpMapperUtils.DIALECT_MAPPER_SUFFIX)
         .filter(MatchPattern.SE);

      for (LcdpResourceBean mapper : this.lcdpResourceService.selectListByFilter(mapperFilter, new Order[0])) {
         this.registerMapper(mapper.getPath(), true, mapper.getContent());
      }

      for (LcdpResourceHistoryBean mapper : mapperHistoryList) {
         this.registerMapper(mapper.getPath(), false, mapper.getContent());
      }

      log.info("============>mapper已加载完毕，耗时：" + (System.currentTimeMillis() - mapperStart) / 1000L + "秒");
      MemoryCacheManager.put("REGISTER_FINISH_KEY", "1");
      this.initResourceIndex();
      this.dealHistoryPageCompsForGlobalConfig();
      this.resourceCheckoutRecordService.dealResourceCheckoutRecord();
      this.coreAdminSelectConfigService.supplementSelectConfig();
      this.dealHistoryPageCompI18n();
      this.dealDuplicateIdCompsCompI18n();
      this.dealHistoryGridScroll();
      this.dealHistoryGridBadgeCount();
      this.dealHistoryGridShowCheckedNum();
      this.dealHistoryGridCheckContinuous();
      this.dealHistoryShowCheckedNum2Null();
      LcdpWebServiceContext.init();
   }

   private void initAppUUID() {
      List<LcdpConfigParamBean> appUUIDList = this.lcdpConfigParamService
         .selectListByFilter(SearchFilter.instance().match("paramCode", "appuuid").filter(MatchPattern.EQ), new Order[0]);
      if (appUUIDList.isEmpty()) {
         LcdpConfigParamBean AppUUID = new LcdpConfigParamBean();
         AppUUID.setId(ApplicationContextHelper.getNextIdentity());
         AppUUID.setParamCode("appuuid");
         AppUUID.setParamValue(StringUtils.randomUUID());
         this.lcdpConfigParamService.getDao().insert(AppUUID);
      }
   }

   private void dealHistoryPageCompsForGlobalConfig() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GLOBAL_CONFIG_DEAL");
      if (queryCoreConstant == null) {
         List<LcdpResourceBean> resourceList = this.lcdpResourceService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTVERSION", null)
                  .filter(MatchPattern.DIFFER)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR).match("EFFECTFLAG", "yes").filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resourceHistoryIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
         List<LcdpModulePageCompBean> pageCompList = this.lcdpModulePageCompService
            .selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistoryIdList).filter(MatchPattern.OR), new Order[0]);
         List<String> dealTypeList = Arrays.asList("Grid", "Uploader", "WindowToolbar", "GridToolbar");
         List<String> fieldTypeList = Arrays.asList("SELECT", "DATE", "DATETIME", "YEAR", "MONTH", "TIME");
         List<String> timeTypeList = Arrays.asList("DATE", "DATETIME", "YEAR", "MONTH", "TIME");
         List<LcdpModulePageCompBean> dealPageCompList = pageCompList.stream()
            .filter(comp -> dealTypeList.contains(comp.getType()))
            .collect(Collectors.toList());
         List<LcdpModulePageCompBean> dealFieldList = pageCompList.stream()
            .filter(
               comp -> {
                  String type = JSON.parseObject(StringUtils.isEmpty(comp.getConfigExtend()) ? comp.getConfig() : comp.getConfig() + comp.getConfigExtend())
                     .getString("type");
                  return StringUtils.isEmpty(type) ? false : fieldTypeList.contains(type.toUpperCase());
               }
            )
            .collect(Collectors.toList());
         dealPageCompList.forEach(comp -> {
            String config = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
            JSONObject configObject = JSON.parseObject(config);
            String var4x = comp.getType();
            switch (var4x) {
               case "Grid":
                  configObject.remove("filterOpen");
                  configObject.remove("toolbarAlign");
                  configObject.remove("editorInvisible");
                  configObject.remove("columnsFill");
                  configObject.remove("contentAlign");
                  configObject.remove("checkOnActive");
                  configObject.remove("checkOneOnActive");
               case "Uploader":
                  configObject.remove("toolbarAlign");
                  break;
               case "WindowToolbar":
                  configObject.remove("toolbarAlign");
                  break;
               case "GridToolbar":
                  configObject.remove("toolbarAlign");
            }

            this.cutConfig(configObject.toJSONString(), comp);
         });
         dealFieldList.forEach(comp -> {
            String config = comp.getConfig() + (StringUtils.isEmpty(comp.getConfigExtend()) ? "" : comp.getConfigExtend());
            JSONObject configObject = JSON.parseObject(config);
            String type = configObject.getString("type");
            if (StringUtils.equalsIgnoreCase(type, "select")) {
               configObject.remove("search");
               configObject.remove("firstBlank");
            }

            if (timeTypeList.contains(type.toUpperCase())) {
               configObject.remove("editable");
            }

            this.cutConfig(configObject.toJSONString(), comp);
         });
         dealPageCompList.addAll(dealFieldList);
         this.lcdpModulePageCompService.getDao().update(dealPageCompList, "CONFIG", "CONFIGEXTEND");
         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GLOBAL_CONFIG_DEAL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台历史数据处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>全部配置属性历史数据已处理完毕，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealHistoryGridScroll() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GRID_SCROLL_DEAL");
      if (queryCoreConstant == null) {
         List<LcdpResourceHistoryBean> effectResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTFLAG", "yes")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<LcdpResourceHistoryBean> unSubmitResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("submitFlag", "no")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resoureIdList = unSubmitResourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
         effectResourceHistoryList.removeIf(effect -> resoureIdList.contains(effect.getResourceId()));
         effectResourceHistoryList.addAll(unSubmitResourceHistoryList);
         effectResourceHistoryList = effectResourceHistoryList.stream()
            .collect(
               Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceHistoryBean::getId))), ArrayList::new)
            );
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(effectResourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.modulePageCompService.dealGridSroll(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GRID_SCROLL_DEAL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台Grid滚动条处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台Grid滚动条处理，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealHistoryGridBadgeCount() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GRID_BADGE_COUNT_DEAL");
      if (queryCoreConstant == null) {
         List<LcdpResourceHistoryBean> effectResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTFLAG", "yes")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<LcdpResourceHistoryBean> unSubmitResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("submitFlag", "no")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resoureIdList = unSubmitResourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
         effectResourceHistoryList.removeIf(effect -> resoureIdList.contains(effect.getResourceId()));
         effectResourceHistoryList.addAll(unSubmitResourceHistoryList);
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(effectResourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.modulePageCompService.dealGridBadgeCount(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GRID_BADGE_COUNT_DEAL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台Grid最大显示数量处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台Grid最大显示数量处理，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealHistoryGridShowCheckedNum() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GRID_SHOW_CHECKED_NUM");
      if (queryCoreConstant == null) {
         List<LcdpResourceHistoryBean> effectResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTFLAG", "yes")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<LcdpResourceHistoryBean> unSubmitResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("submitFlag", "no")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resoureIdList = unSubmitResourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
         effectResourceHistoryList.removeIf(effect -> resoureIdList.contains(effect.getResourceId()));
         effectResourceHistoryList.addAll(unSubmitResourceHistoryList);
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(effectResourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.modulePageCompService.dealGridShowCheckedNum(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GRID_SHOW_CHECKED_NUM");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台Grid显示选中数量");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台Grid显示选中数量，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealHistoryShowCheckedNum2Null() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GRID_CHECKED_NUM_TO_NULL");
      if (queryCoreConstant == null) {
         List<LcdpResourceHistoryBean> effectResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTFLAG", "yes")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<LcdpResourceHistoryBean> unSubmitResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("submitFlag", "no")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resoureIdList = unSubmitResourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
         effectResourceHistoryList.removeIf(effect -> resoureIdList.contains(effect.getResourceId()));
         effectResourceHistoryList.addAll(unSubmitResourceHistoryList);
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(effectResourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.modulePageCompService.dealHistoryShowCheckedNum2Null(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GRID_CHECKED_NUM_TO_NULL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台Grid显示选中数量");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台Grid显示选中数量，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealHistoryGridCheckContinuous() {
      long dealStart = System.currentTimeMillis();
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_GRID_CHECK_CONTINUOUS");
      if (queryCoreConstant == null) {
         List<LcdpResourceHistoryBean> effectResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTFLAG", "yes")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<LcdpResourceHistoryBean> unSubmitResourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("submitFlag", "no")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resoureIdList = unSubmitResourceHistoryList.stream().map(LcdpResourceHistoryBean::getResourceId).collect(Collectors.toList());
         effectResourceHistoryList.removeIf(effect -> resoureIdList.contains(effect.getResourceId()));
         effectResourceHistoryList.addAll(unSubmitResourceHistoryList);
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(effectResourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.modulePageCompService.dealHistoryGridCheckContinuous(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_GRID_CHECK_CONTINUOUS");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台Grid保留选中数量");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台Grid保留选中数量，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void dealDuplicateIdCompsCompI18n() {
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_PAGE_I18N_DUPLICATEID_DEAL");
      if (queryCoreConstant == null) {
         long dealStart = System.currentTimeMillis();
         List<LcdpResourceBean> resourceList = this.lcdpResourceService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTVERSION", null)
                  .filter(MatchPattern.DIFFER)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR).match("EFFECTFLAG", "yes").filter(MatchPattern.EQ),
               new Order[0]
            );
         resourceHistoryList = resourceHistoryList.stream()
            .collect(
               Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LcdpResourceHistoryBean::getId))), ArrayList::new)
            );
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(resourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.lcdpModulePageI18nService.batchDealHistoryPageCompI18nForDuplicateId(chunkItemList);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_PAGE_I18N_DUPLICATEID_DEAL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台页面国际化组件重复ID历史数据处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台页面国际化历史数据处理已处理完毕，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   public void dealHistoryPageCompI18n() {
      CoreConstantBean queryCoreConstant = this.coreConstantService.selectByIdIfPresent("LCDP_PAGE_I18N_DEAL");
      if (queryCoreConstant == null) {
         long dealStart = System.currentTimeMillis();
         List<LcdpResourceBean> resourceList = this.lcdpResourceService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("RESOURCECATEGORY", "view")
                  .filter(MatchPattern.EQ)
                  .match("EFFECTVERSION", null)
                  .filter(MatchPattern.DIFFER)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         List<Long> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService
            .selectListByFilter(
               SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR).match("EFFECTFLAG", "yes").filter(MatchPattern.EQ),
               new Order[0]
            );
         List<CoreI18nMessageBean> coreI18nMessageList = this.coreI18nMessageService
            .selectListByFilter(
               SearchFilter.instance().match("CODE", "GIKAM").filter(MatchPattern.SB).match("I18NCONFIGID", "zh-CN").filter(MatchPattern.SEQ), new Order[0]
            );
         Map<String, String> i18nMessageMap = new HashMap<>();
         coreI18nMessageList.forEach(coreI18nMessageBean -> i18nMessageMap.put(coreI18nMessageBean.getMessage(), coreI18nMessageBean.getExt$Item("CODE")));
         ListChunkIterator<LcdpResourceHistoryBean> chunkIterator = ListChunkIterator.of(resourceHistoryList, 100);

         while (chunkIterator.hasNext()) {
            List<LcdpResourceHistoryBean> chunkItemList = chunkIterator.nextChunk();
            this.lcdpModulePageI18nService.batchDealHistoryPageCompI18n(chunkItemList, i18nMessageMap);
         }

         CoreConstantBean coreConstant = new CoreConstantBean();
         coreConstant.setId("LCDP_PAGE_I18N_DEAL");
         coreConstant.setConstantValue("1");
         coreConstant.setConstantDesc("低代码平台页面国际化历史数据处理");
         coreConstant.setClassfication("系统");
         this.coreConstantService.getDao().insert(coreConstant);
         log.info("============>低代码平台页面国际化历史数据处理已处理完毕，耗时：" + (System.currentTimeMillis() - dealStart) / 1000L + "秒");
      }
   }

   private void initResourceIndex() {
      String indexPath = LuceneIndexManager.getIndexPath();
      File indexFile = new File(indexPath);
      if (!indexFile.exists()) {
         indexFile.mkdirs();
      }

      if (indexFile.listFiles().length == 0) {
         SearchFilter filter = SearchFilter.instance()
            .match("EFFECTVERSION", null)
            .filter(MatchPattern.DIFFER)
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ);
         List<LcdpResourceBean> resourceList = this.lcdpResourceService.selectListByFilter(filter, new Order[0]);
         SearchFilter historyFilter = SearchFilter.instance()
            .match("DELETEFLAG", "0")
            .filter(MatchPattern.EQ)
            .match("SUBMITFLAG", "no")
            .filter(MatchPattern.EQ);
         List<LcdpResourceHistoryBean> resourceHistoryList = this.lcdpResourceHistoryService.selectListByFilter(historyFilter, new Order[0]);
         List<LcdpResourceDocumentBean> documentList = new ArrayList<>();
         resourceList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
         resourceHistoryList.stream().forEach(e -> documentList.add(new LcdpResourceDocumentBean(e)));
         LuceneIndexManager.addIndex(documentList);
      }
   }

   private void registerScript(List<LcdpReadyListener.SourceConfig> sourceConfigList, List<LcdpReadyListener.SourceConfig> xlintSourceConfigList) {
      if (!sourceConfigList.isEmpty()) {
         Map<String, String> sourceMap = new HashMap<>();
         sourceConfigList.forEach(s -> {
            String var10000 = sourceMap.put(s.getClassName(), s.getSourceCode());
         });
         LoadMultipleResult result = DynamicClassLoader.getInstance().loadSourceCode(sourceMap);
         if (result.getErrorMessageMap() != null) {
            result.getErrorMessageMap().forEach((k, v) -> {
               LcdpReadyListener.SourceConfig sourceConfig = sourceConfigList.stream().filter(t -> k.equals(t.getClassName())).findAny().get();
               if (StringUtils.containsIgnoreCase(v, "-Xlint")) {
                  xlintSourceConfigList.add(sourceConfig);
               }

               log.error("============>脚本加载异常：类名为" + sourceConfig.getClassName() + "，异常日志如下：" + v);
               sourceConfigList.removeIf(t -> t.getClassName().equals(sourceConfig.getClassName()));
            });
            this.registerScript(sourceConfigList, xlintSourceConfigList);
         } else {
            result.getClassMap().forEach((k, c) -> {
               LcdpReadyListener.SourceConfig sourceConfig = sourceConfigList.stream().filter(t -> k.equals(t.getClassName())).findAny().orElse(null);
               if (sourceConfig != null) {
                  String beanName = sourceConfig.getClassName().toLowerCase();
                  SpringUtils.registerBean(beanName, (Class<?>)c);

                  try {
                     CodeHintsManager.register(sourceConfig.getHintName(), SpringUtils.getBean(beanName).getClass());
                     if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(beanName.toLowerCase()).getClass())) {
                        LcdpBaseService scriptService = SpringUtils.getBean(beanName.toLowerCase());
                        String tableName = scriptService.getTable();
                        RedisHelper.put("TABLE_SCRIPT_MAPPING_CACHE", tableName, beanName.toLowerCase());
                        RedisHelper.put("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName, sourceConfig.getPath());
                     }
                  } catch (Exception var7) {
                     log.error("============>脚本提醒加载异常：类名为" + sourceConfig.getClassName(), var7);
                  }
               }
            });
         }
      }
   }

   private void registerScript(List<LcdpReadyListener.SourceConfig> xlintSourceConfigList) {
      for (LcdpReadyListener.SourceConfig resource : xlintSourceConfigList) {
         String script = resource.getSourceCode();
         String beanName = resource.getClassName().toLowerCase();

         try {
            Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
            log.info("============>spring加载类" + resource.getClassName());
            SpringUtils.registerBean(beanName, clazz);
         } catch (Exception var8) {
            log.error("============>脚本加载异常：类名为" + resource.getClassName(), var8);
         }

         CodeHintsManager.register(resource.getHintName(), SpringUtils.getBean(beanName).getClass());
         if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(beanName).getClass())) {
            LcdpBaseService scriptService = SpringUtils.getBean(beanName);
            String tableName = scriptService.getTable();
            RedisHelper.put("TABLE_SCRIPT_MAPPING_CACHE", tableName, beanName);
            RedisHelper.put("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName, resource.getPath());
         }
      }
   }

   private void registerMapper(String path, boolean isPro, String content) {
      try {
         LcdpMapperUtils.loadMapper(path, isPro, content);
      } catch (Exception var5) {
         log.error("============>加载mapper：" + path + "异常，异常日志如下：" + var5.getMessage());
      }
   }

   private void cutConfig(String sourceConfig, LcdpModulePageCompBean page) {
      if (sourceConfig.length() > 3000) {
         page.setConfig(sourceConfig.substring(0, 3000));
         page.setConfigExtend(sourceConfig.substring(3000));
      } else {
         page.setConfig(sourceConfig);
         page.setConfigExtend(null);
      }
   }

   private static class SourceConfig {
      private final String className;
      private final String hintName;
      private final String sourceCode;
      private final String path;

      public SourceConfig(String className, String hintName, String sourceCode, String path) {
         this.className = className;
         this.hintName = hintName;
         this.sourceCode = sourceCode;
         this.path = path;
      }

      public String getClassName() {
         return this.className;
      }

      public String getHintName() {
         return this.hintName;
      }

      public String getSourceCode() {
         return this.sourceCode;
      }

      public String getPath() {
         return this.path;
      }
   }
}
