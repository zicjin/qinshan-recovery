package com.sunwayworld.cloud.module.item.workspacescheme.service.impl;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.service.CoreWorkspacePanelService;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserPanelLayoutRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceUserSchemeDao;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemePanelConfigService;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemeService;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceUserSchemePanelConfigService;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceUserSchemeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreWorkspaceUserSchemeServiceImpl implements CoreWorkspaceUserSchemeService {
   private static final Long CUSTEM_SCHEME_ID = 1L;
   private static final String CUSTEM_SCHEME_NAME = "自定义方案";
   @Autowired
   private CoreWorkspaceUserSchemeDao userSchemeDao;
   @Autowired
   private CoreWorkspaceUserSchemePanelConfigService userSchemePanelConfigService;
   @Autowired
   private CoreWorkspaceSchemeService schemeService;
   @Autowired
   private CoreWorkspaceSchemePanelConfigService schemePanelConfigService;
   @Autowired
   private CoreWorkspacePanelService panelService;

   public CoreWorkspaceUserSchemeDao getDao() {
      return this.userSchemeDao;
   }

   @Override
   public CoreWorkspaceSchemePanelConfigRepresentation selectUserPanelConfig() {
      List<CoreWorkspaceSchemeBean> schemeList = this.schemeService.selectAuthorizedList();
      if (schemeList.isEmpty()) {
         return new CoreWorkspaceSchemePanelConfigRepresentation();
      } else {
         CoreWorkspaceSchemeBean customScheme = new CoreWorkspaceSchemeBean();
         customScheme.setId(CUSTEM_SCHEME_ID);
         customScheme.setSchemeName("自定义方案");
         schemeList.add(customScheme);
         CoreWorkspaceSchemePanelConfigRepresentation representation = new CoreWorkspaceSchemePanelConfigRepresentation();
         representation.setSchemeList(schemeList);
         List<CoreWorkspaceUserSchemeBean> userSchemeList = this.selectListByFilter(
            SearchFilter.instance()
               .match("USERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ)
               .match("ROLEID", LocalContextHelper.getLoginRoleId())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
         if (userSchemeList.isEmpty()) {
            return new CoreWorkspaceSchemePanelConfigRepresentation();
         } else {
            CoreWorkspaceUserSchemeBean userScheme = userSchemeList.get(0);
            long schemeId = userScheme.getSchemeId();
            representation.setSchemeId(schemeId);
            List<CoreWorkspaceUserSchemePanelConfigBean> userPanelConfigList = this.userSchemePanelConfigService
               .selectListByFilter(SearchFilter.instance().match("USERSCHEMEID", userScheme.getId()).filter(MatchPattern.EQ), new Order[0]);
            List<CoreWorkspaceSchemePanelConfigBean> schemePanelConfigList = new ArrayList<>();

            for (CoreWorkspaceUserSchemePanelConfigBean userSchemePanelConfig : userPanelConfigList) {
               CoreWorkspaceSchemePanelConfigBean schemePanelConfig = new CoreWorkspaceSchemePanelConfigBean();
               schemePanelConfig.setPanelId(userSchemePanelConfig.getPanelId());
               schemePanelConfig.setConfig(userSchemePanelConfig.getConfig());
               schemePanelConfigList.add(schemePanelConfig);
            }

            representation.setPanelList(schemePanelConfigList);
            if (schemeId == CUSTEM_SCHEME_ID) {
               List<CoreWorkspaceSchemeBean> authoritySchemeList = this.schemeService.selectAuthorizedList();
               List<CoreWorkspaceSchemePanelConfigBean> authoritySchemePanelConfigList = this.schemePanelConfigService
                  .selectListByFilter(
                     SearchFilter.instance()
                        .match("SCHEMEID", authoritySchemeList.stream().map(e -> e.getId()).collect(Collectors.toList()))
                        .filter(MatchPattern.OR),
                     new Order[0]
                  );
               List<CoreWorkspacePanelBean> panelList = this.panelService
                  .selectListByIds(authoritySchemePanelConfigList.stream().map(e -> e.getPanelId()).distinct().collect(Collectors.toList()));
               representation.setSystemPanelList(panelList);
            } else {
               List<CoreWorkspaceSchemePanelConfigBean> schemePanelList = this.schemePanelConfigService
                  .selectListByFilter(SearchFilter.instance().match("SCHEMEID", schemeId).filter(MatchPattern.EQ), new Order[0]);
               List<CoreWorkspacePanelBean> panelList = this.panelService
                  .getDao()
                  .selectListByIds(schemePanelList.stream().map(e -> e.getPanelId()).collect(Collectors.toList()));
               representation.setSystemPanelList(panelList);
            }

            return representation;
         }
      }
   }

   @Cacheable(
      value = {"core-user-panel-config-layout"},
      keyGenerator = "workspacePanelConfigKeyGenerator",
      unless = "#result == null"
   )
   @Transactional
   @Override
   public CoreWorkspaceUserPanelLayoutRepresentation selectUserPanelLayout() {
      List<CoreWorkspaceUserSchemeBean> userSchemeList = this.selectListByFilter(
         SearchFilter.instance()
            .match("USERID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.EQ)
            .match("ROLEID", LocalContextHelper.getLoginRoleId())
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      List<CoreWorkspaceUserSchemePanelConfigBean> userPanelConfigList = new ArrayList<>();
      if (userSchemeList.isEmpty()) {
         List<CoreWorkspaceSchemeBean> schemeList = this.schemeService.selectAuthorizedList();
         if (schemeList.isEmpty()) {
            return null;
         }

         Long schemeId = schemeList.get(0).getId();
         Long userSchemeId = ApplicationContextHelper.getNextIdentity();
         CoreWorkspaceUserSchemeBean userScheme = new CoreWorkspaceUserSchemeBean();
         userScheme.setId(userSchemeId);
         userScheme.setUserId(LocalContextHelper.getLoginUserId());
         userScheme.setRoleId(LocalContextHelper.getLoginRoleId());
         userScheme.setSchemeId(schemeList.get(0).getId());
         this.getDao().insert(userScheme);

         for (CoreWorkspaceSchemePanelConfigBean schemePanelConfig : this.schemePanelConfigService
            .selectListByFilter(SearchFilter.instance().match("SCHEMEID", schemeId).filter(MatchPattern.EQ), new Order[0])) {
            CoreWorkspaceUserSchemePanelConfigBean userSchemePanelConfig = new CoreWorkspaceUserSchemePanelConfigBean();
            userSchemePanelConfig.setId(ApplicationContextHelper.getNextIdentity());
            userSchemePanelConfig.setUserSchemeId(userSchemeId);
            userSchemePanelConfig.setPanelId(schemePanelConfig.getPanelId());
            userSchemePanelConfig.setConfig(schemePanelConfig.getConfig());
            userPanelConfigList.add(userSchemePanelConfig);
         }

         this.userSchemePanelConfigService.getDao().insert(userPanelConfigList);
      } else {
         CoreWorkspaceUserSchemeBean userScheme = userSchemeList.get(0);
         userPanelConfigList = this.userSchemePanelConfigService
            .selectListByFilter(SearchFilter.instance().match("USERSCHEMEID", userScheme.getId()).filter(MatchPattern.EQ), new Order[0]);
      }

      CoreWorkspaceUserPanelLayoutRepresentation representation = new CoreWorkspaceUserPanelLayoutRepresentation();
      List<CoreWorkspacePanelBean> panelList = this.panelService
         .selectListByIds(userPanelConfigList.stream().map(l -> l.getPanelId()).collect(Collectors.toList()));
      panelList.forEach(e -> {
         if (!StringUtils.isEmpty(e.getI18nCode())) {
            e.setTitle(e.getI18nCode());
         }
      });
      userPanelConfigList.forEach(
         l -> panelList.stream()
            .filter(p -> l.getPanelId().equals(p.getId()))
            .forEach(p -> representation.addPanel(p.getTitle(), p.getUrl(), l.getConfig(), p.getTitleDisplay(), p.getRefreshFlag()))
      );
      return representation;
   }

   @Transactional
   @CacheEvict(
      value = {"core-user-panel-config-layout"},
      keyGenerator = "workspacePanelConfigKeyGenerator"
   )
   @Override
   public void save(RestJsonWrapperBean wrapper) {
      String loginUserId = LocalContextHelper.getLoginUserId();
      Long loginRoleId = LocalContextHelper.getLoginRoleId();
      List<CoreWorkspaceUserSchemeBean> userSchemeList = this.selectListByFilter(
         SearchFilter.instance().match("USERID", loginUserId).filter(MatchPattern.EQ).match("ROLEID", loginRoleId).filter(MatchPattern.EQ), new Order[0]
      );
      CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
      userSchemeList.forEach(t -> tableService.deleteCascade(PersistableMetadataHelper.getTableName(CoreWorkspaceUserSchemeBean.class), t.getId()));
      CoreWorkspaceUserSchemeBean userScheme = wrapper.parseUnique(CoreWorkspaceUserSchemeBean.class);
      Long id = ApplicationContextHelper.getNextIdentity();
      userScheme.setId(id);
      userScheme.setRoleId(loginRoleId);
      userScheme.setUserId(loginUserId);
      this.getDao().insert(userScheme);
      List<CoreWorkspaceUserSchemePanelConfigBean> userSchemePanelList = wrapper.parse(CoreWorkspaceUserSchemePanelConfigBean.class);
      userSchemePanelList.forEach(c -> {
         c.setId(ApplicationContextHelper.getNextIdentity());
         c.setUserSchemeId(id);
      });
      this.userSchemePanelConfigService.getDao().insert(userSchemePanelList);
   }

   @Override
   public CoreWorkspaceSchemePanelConfigRepresentation selectPanelConfigBySchemeId(Long schemeId, RestJsonWrapperBean wrapper) {
      CoreWorkspaceSchemePanelConfigRepresentation representation = new CoreWorkspaceSchemePanelConfigRepresentation();
      List<CoreWorkspaceSchemePanelConfigBean> schemePanelConfigList = new ArrayList<>();
      if (schemeId != CUSTEM_SCHEME_ID) {
         schemePanelConfigList = this.schemePanelConfigService
            .selectListByFilter(SearchFilter.instance().match("SCHEMEID", schemeId).filter(MatchPattern.EQ), new Order[0]);
         representation.setSystemPanelList(
            this.panelService.selectListByIds(schemePanelConfigList.stream().map(e -> e.getPanelId()).collect(Collectors.toList()))
         );
      } else {
         List<CoreWorkspaceSchemeBean> authoritySchemeList = this.schemeService.selectAuthorizedList();
         List<CoreWorkspaceSchemePanelConfigBean> authoritySchemePanelConfigList = this.schemePanelConfigService
            .selectListByFilter(
               SearchFilter.instance().match("SCHEMEID", authoritySchemeList.stream().map(e -> e.getId()).collect(Collectors.toList())).filter(MatchPattern.OR),
               new Order[0]
            );
         List<CoreWorkspacePanelBean> panelList = this.panelService
            .selectListByIds(authoritySchemePanelConfigList.stream().map(e -> e.getPanelId()).distinct().collect(Collectors.toList()));
         representation.setSystemPanelList(panelList);
      }

      List<CoreWorkspaceUserSchemeBean> userSchemeList = this.selectListByFilter(
         SearchFilter.instance()
            .match("USERID", LocalContextHelper.getLoginUserId())
            .filter(MatchPattern.EQ)
            .match("ROLEID", LocalContextHelper.getLoginRoleId())
            .filter(MatchPattern.EQ)
            .match("SCHEMEID", schemeId)
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      if (!userSchemeList.isEmpty()) {
         List<CoreWorkspaceUserSchemePanelConfigBean> userPanelConfigList = this.userSchemePanelConfigService
            .selectListByFilter(SearchFilter.instance().match("USERSCHEMEID", userSchemeList.get(0).getId()).filter(MatchPattern.EQ), new Order[0]);
         List<CoreWorkspaceSchemePanelConfigBean> schemePanelList = new ArrayList<>();

         for (CoreWorkspaceUserSchemePanelConfigBean userSchemePanelConfig : userPanelConfigList) {
            CoreWorkspaceSchemePanelConfigBean schemePanelConfig = new CoreWorkspaceSchemePanelConfigBean();
            schemePanelConfig.setPanelId(userSchemePanelConfig.getPanelId());
            schemePanelConfig.setConfig(userSchemePanelConfig.getConfig());
            schemePanelList.add(schemePanelConfig);
         }

         representation.setPanelList(schemePanelList);
      } else {
         representation.setPanelList(schemePanelConfigList);
      }

      return representation;
   }

   @Bean
   public KeyGenerator workspacePanelConfigKeyGenerator() {
      return (t, m, p) -> this.getRedisKey();
   }

   private String getRedisKey() {
      return LocalContextHelper.getLoginUserId() + (LocalContextHelper.getLoginRoleId() == null ? "" : LocalContextHelper.getLoginRoleId());
   }
}
