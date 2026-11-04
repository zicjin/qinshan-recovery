package com.sunwayworld.boot.listener;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.GikamBeanContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.io.excel.ExcelFunctionRegistry;
import com.sunwayworld.framework.jdk.hints.core.CodeHintsManager;
import com.sunwayworld.framework.loop.LoopConfiguration;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.security.sso.impl.AdDomainSsoAuthenticator;
import com.sunwayworld.framework.security.sso.impl.QrCodeLoginSsoAuthenticator;
import com.sunwayworld.framework.security.sso.impl.SmsLoginSsoAuthenticator;
import com.sunwayworld.framework.security.sso.impl.TenantSsoAuthenticator;
import com.sunwayworld.framework.security.sso.impl.WechatSsoAuthenticator;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.config.helper.SystemConfigHelper;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import java.util.List;
import java.util.Set;
import org.apache.catalina.Valve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPreparedListener implements ApplicationListener<ApplicationPreparedEvent> {
   private static final Logger logger = LogManager.getLogger(ApplicationPreparedListener.class);
   @Autowired
   private RedisTemplate<String, Object> redisTemplate;
   @Autowired
   @Lazy
   private ServletWebServerApplicationContext context;
   @Autowired
   @Lazy
   private TenantManager tenantManager;
   @Value("#{'${sunway.cache.post-start.evict.name}'.split(',')}")
   private List<String> evictCacheNameList;
   @Value("${sunway.app.stand-alone.enabled:true}")
   private Boolean standalone;

   public void onApplicationEvent(ApplicationPreparedEvent event) {
      boolean cacheDeleted = false;
      if (!ApplicationContextHelper.isProfileActivated("pro")) {
         logger.info("Not production mode, try to delete redis cache.");

         try {
            Set<String> keys = this.redisTemplate.keys("*");
            this.redisTemplate.delete(keys);
            cacheDeleted = true;
            logger.info("Redis cache deleted.");
         } catch (Exception var4) {
            logger.warn("Delete redis cache fail, cause : " + var4.getMessage());
         }
      }

      if (ApplicationContextHelper.isProfileActivated("tenant")) {
         this.tenantManager.reload();
      }

      if (!cacheDeleted) {
         this.tenantManager.call("CLASS_CACHE_EVICT", () -> {
            for (Class<?> persistableClass : ClassUtils.getPredicatedClasses("com.sunwayworld", c -> Persistable.class.isAssignableFrom(c))) {
               DatabaseManager.evictCache((Class<? extends Persistable<?>>)persistableClass);
            }

            if (!CollectionUtils.isEmpty(this.evictCacheNameList)) {
               this.redisTemplate.delete(this.evictCacheNameList);
            }
         });
      }

      CodeHintsManager.init();
      CoreI18nCache.instance();
      MybatisHelper.updateRuntimeConfiguration();
      MybatisHelper.updateRuntimeMapper();
      GikamBeanContext.init();
      SsoAuthenticatorRegistry.INSTANCE.register("wechat", WechatSsoAuthenticator.class);
      SsoAuthenticatorRegistry.INSTANCE.register("AD", AdDomainSsoAuthenticator.class);
      SsoAuthenticatorRegistry.INSTANCE.register("tenantLogin", TenantSsoAuthenticator.class);
      if ("1".equals(SystemConfigHelper.getLoginConfig().getQrCode())) {
         SsoAuthenticatorRegistry.INSTANCE.register("qrCodeLogin", QrCodeLoginSsoAuthenticator.class);
      }

      if ("1".equals(SystemConfigHelper.getLoginConfig().getSmsLogin())) {
         SsoAuthenticatorRegistry.INSTANCE.register("smsLogin", SmsLoginSsoAuthenticator.class);
      }

      if (this.standalone) {
         this.customizeTomcatErrorReport();
      }

      ExcelFunctionRegistry.register();
      ApplicationContextHelper.getBean(LoopConfiguration.class).init();
   }

   private void customizeTomcatErrorReport() {
      TomcatWebServer tws = (TomcatWebServer)this.context.getWebServer();

      for (Valve v : tws.getTomcat().getHost().getPipeline().getValves()) {
         if (v instanceof ErrorReportValve) {
            ((ErrorReportValve)v).setShowReport(false);
            ((ErrorReportValve)v).setShowServerInfo(false);
         }
      }
   }
}
