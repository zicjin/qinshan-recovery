package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.base.LcdpResultDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessCommentBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessDiagramCandidatorDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessDiagramDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeActionBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeMenuBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeRoleBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessPositionDTO;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpBizSignUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeRoleBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpNodeSignUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessCommentService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeActionService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeMenuService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeRoleService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeUserService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpBizSignUserService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeRoleService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeUserService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstService;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpNodeSignUserService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.service.impl.LcdpResourceServiceImpl;
import com.sunwayworld.cloud.module.lcdp.support.LcdpScriptTemplateHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpBizProcessServiceImpl implements LcdpBizProcessService {
   private static final Logger log = LoggerFactory.getLogger(LcdpResourceServiceImpl.class);
   @Autowired
   private LcdpBizProcessDao lcdpBizProcessDao;
   @Autowired
   private LcdpBizProcessNodeService lcdpBizProcessNodeService;
   @Autowired
   private LcdpBizProcessNodeUserService lcdpBizProcessNodeUserService;
   @Autowired
   private LcdpBizProcessNodeRoleService lcdpBizProcessNodeRoleService;
   @Autowired
   private LcdpBizProcessNodeActionService lcdpBizProcessNodeActionService;
   @Autowired
   private LcdpBizProcessCommentService lcdpBizProcessCommentService;
   @Autowired
   private LcdpBpNodeSignUserService lcdpBpNodeSignUserService;
   @Autowired
   private LcdpBizProcessNodeMenuService lcdpBizProcessNodeMenuService;
   @Autowired
   private LcdpBpInstService lcdpBpInstService;
   @Autowired
   private LcdpBpInstNodeService lcdpBpInstNodeService;
   @Autowired
   private LcdpBpInstNodeRoleService lcdpBpInstNodeRoleService;
   @Autowired
   private LcdpBpInstNodeUserService lcdpBpInstNodeUserService;
   @Autowired
   private LcdpBpBizSignUserService lcdpBpBizSignUserService;
   @Autowired
   private CoreMenuService coreMenuService;
   @Autowired
   private CoreI18nService coreI18nService;
   @Autowired
   private CoreI18nMessageService coreI18nMessageService;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private CoreRolePermissionService coreRolePermissionService;
   @Autowired
   private LcdpResourceService lcdpResourceService;
   @Autowired
   private MapDao mapDao;
   @Autowired
   private CoreTodoConfigService coreTodoConfigService;
   private static final ThreadLocal<LcdpBizProcessServiceImpl.SelectMenuRequiredData> LOCAL_SELECT_MENU_REQUIRED_DATA = new ThreadLocal<>();
   @Autowired
   @Lazy
   private CoreSystemConfigService systemConfigService;

   public LcdpBizProcessDao getDao() {
      return this.lcdpBizProcessDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpBizProcessBean bp = jsonWrapper.parseUnique(LcdpBizProcessBean.class);
      Long id = ApplicationContextHelper.getNextIdentity();
      bp.setId(id);
      bp.setVersion(1L);
      this.getDao().insert(bp);
      return id;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      LcdpBizProcessService.super.activate(wrapper);
      LcdpBizProcessBean bp = wrapper.parseUnique(LcdpBizProcessBean.class);
      String bpCode = this.selectColumnById(bp.getId(), "BPCODE");
      List<LcdpBizProcessNodeActionBean> bpActionList = this.lcdpBizProcessNodeActionService
         .selectListByFilter(SearchFilter.instance().match("BPID", bp.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeMenuBean> bpNodeMenuList = this.lcdpBizProcessNodeMenuService
         .selectListByFilter(SearchFilter.instance().match("BPID", bp.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService
         .selectListByFilter(SearchFilter.instance().match("BPID", bp.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeRoleBean> allBpNodeRoleList = this.lcdpBizProcessNodeRoleService
         .selectListByFilter(SearchFilter.instance().match("BPID", bp.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<String> menuIdList = bpNodeMenuList.stream()
         .map(LcdpBizProcessNodeMenuBean::getMenuId)
         .filter(menuId -> !StringUtils.isEmpty(menuId))
         .distinct()
         .collect(Collectors.toList());
      menuIdList.addAll(
         bpNodeMenuList.stream()
            .map(LcdpBizProcessNodeMenuBean::getParentMenuId)
            .filter(menuId -> !StringUtils.isEmpty(menuId))
            .distinct()
            .collect(Collectors.toList())
      );
      List<CoreRolePermissionBean> existedCoreRolePermissionList = this.coreRolePermissionService
         .selectListByFilter(SearchFilter.instance().match("TARGETID", menuIdList).filter(MatchPattern.OR), new Order[0]);
      List<CoreMenuBean> insertMenuList = new ArrayList<>();
      List<CoreMenuBean> updateMenuList = new ArrayList<>();
      List<CoreTodoConfigBean> insertTodoConfigList = new ArrayList<>();
      List<CoreRolePermissionBean> insertRolePermissionList = new ArrayList<>();
      List<CoreI18nBean> menuI18nList = new ArrayList<>();
      List<CoreI18nMessageBean> insertMenuI18nMessageList = new ArrayList<>();
      bpNodeMenuList.forEach(
         e -> {
            List<LcdpBizProcessNodeRoleBean> bpNodeRoleList = allBpNodeRoleList.stream()
               .filter(f -> Objects.equals(e.getId(), f.getBpNodeId()))
               .collect(Collectors.toList());
            LcdpBizProcessNodeBean bpNode = bpNodeList.stream().filter(f -> Objects.equals(e.getId(), f.getId())).findFirst().orElse(null);
            CoreI18nBean menuI18n = this.getI18nCodeByBpNodeMenu(bpCode, bpNode.getBpNodeCode(), e.getMenuName());
            List<CoreI18nMessageBean> i18nMessageList = this.getI18nMessageByBpNodeMenu(menuI18n, e);
            menuI18nList.add(menuI18n);
            insertMenuI18nMessageList.addAll(i18nMessageList);
            List<CoreRolePermissionBean> existedParentMenuRoleList = existedCoreRolePermissionList.stream()
               .filter(f -> Objects.equals(e.getParentMenuId(), f.getTargetId()))
               .collect(Collectors.toList());

            for (LcdpBizProcessNodeRoleBean bpNodeRole : bpNodeRoleList) {
               if (existedParentMenuRoleList.stream().noneMatch(parentMenuRole -> bpNodeRole.getRoleId().equals(parentMenuRole.getRoleId()))) {
                  insertRolePermissionList.addAll(this.getBpMenuRolePermissionList(e.getParentMenuId(), ArrayUtils.asList(bpNodeRole)));
               }
            }

            if (!StringUtils.isEmpty(e.getMenuId()) && !"1".equals(e.getRegenerateFlag())) {
               CoreMenuBean coreMenu = this.getMenuByBpNodeMenu(e, menuI18n.getCode());
               CoreMenuBean ifExistMenu = this.coreMenuService.selectByIdIfPresent(coreMenu.getId());
               if (ifExistMenu != null) {
                  updateMenuList.add(coreMenu);
               } else {
                  insertMenuList.add(coreMenu);
               }

               List<CoreRolePermissionBean> existedMenuRoleList = existedCoreRolePermissionList.stream()
                  .filter(f -> Objects.equals(e.getMenuId(), f.getTargetId()))
                  .collect(Collectors.toList());

               for (LcdpBizProcessNodeRoleBean bpNodeRolex : bpNodeRoleList) {
                  if (existedMenuRoleList.stream().noneMatch(menuRole -> bpNodeRolex.getRoleId().equals(menuRole.getRoleId()))) {
                     insertRolePermissionList.addAll(this.getBpMenuRolePermissionList(e.getMenuId(), ArrayUtils.asList(bpNodeRolex)));
                  }
               }
            } else {
               String menuId = "BP_" + e.getId();
               e.setMenuId(menuId);
               CoreMenuBean menu = this.getMenuByBpNodeMenu(e, menuI18n.getCode());
               insertMenuList.add(menu);
               List<CoreRolePermissionBean> rolePermissionList = this.getBpMenuRolePermissionList(e.getMenuId(), bpNodeRoleList);
               if (!rolePermissionList.isEmpty()) {
                  insertRolePermissionList.addAll(rolePermissionList);
               }
            }

            if (!StringUtils.isEmpty(e.getTodoScript())) {
               CoreTodoConfigBean todoConfig = this.getTodoConfigByBpNodeMenu(e);
               insertTodoConfigList.add(todoConfig);
            }
         }
      );
      if (insertMenuList.stream().anyMatch(e -> null == e.getResourceId())) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.NO_BIND_PAGE");
      } else {
         bpNodeMenuList.stream().forEach(e -> e.setRegenerateFlag("0"));
         this.lcdpBizProcessNodeMenuService.getDao().update(bpNodeMenuList, "MENUID", "REGENERATEFLAG");
         List<Long> i18nIdList = this.coreI18nService.getDao().selectIdList(menuI18nList, ArrayUtils.asList("CODE"));
         i18nIdList.forEach(this.coreI18nService::deleteCascadeById);
         this.coreMenuService.getDao().insert(insertMenuList);
         this.coreTodoConfigService.getDao().insert(insertTodoConfigList);
         this.coreI18nService.getDao().insert(menuI18nList);
         this.coreI18nMessageService.getDao().insert(insertMenuI18nMessageList);
         this.coreMenuService
            .getDao()
            .update(updateMenuList, "BPID", "RESOURCEID", "RESOURCENAME", "RESOURCEPATH", "MENUPARAM", "PARENTID", "I18NCODE", "MENUNAME", "ICON", "TODOICON");
         this.coreRolePermissionService.getDao().insert(insertRolePermissionList);
         RedisHelper.evict("T_CORE_MENU", "ALL");
         this.registerSpring(bpActionList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void modify(RestJsonWrapperBean wrapper) {
      LcdpBizProcessBean bp = wrapper.parseUnique(LcdpBizProcessBean.class);
      Long id = bp.getId();
      LcdpBizProcessBean maxBp = this.selectFirstByFilter(
         SearchFilter.instance().match("BPCODE", bp.getBpCode()).filter(MatchPattern.SEQ), new Order[]{Order.desc("VERSION")}
      );
      if (maxBp != null) {
         bp.setVersion(maxBp.getVersion() + 1L);
      }

      bp.setId(ApplicationContextHelper.getNextIdentity());
      bp.setActivatedFlag("0");
      List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeMenuBean> bpNodeMenuList = this.lcdpBizProcessNodeMenuService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeUserBean> bpNodeUserList = this.lcdpBizProcessNodeUserService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeRoleBean> bpNodeRoleList = this.lcdpBizProcessNodeRoleService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeActionBean> bpNodeActionList = this.lcdpBizProcessNodeActionService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBpNodeSignUserBean> bpSignUserList = this.lcdpBpNodeSignUserService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeBean> newBpNodeList = new ArrayList<>();
      List<LcdpBizProcessNodeMenuBean> newBpNodeMenuList = new ArrayList<>();
      List<LcdpBizProcessNodeUserBean> newBpNodeUserList = new ArrayList<>();
      List<LcdpBizProcessNodeRoleBean> newBpNodeRoleList = new ArrayList<>();
      List<LcdpBizProcessNodeActionBean> newBpNodeActionList = new ArrayList<>();
      List<LcdpBpNodeSignUserBean> newBpSignUserList = new ArrayList<>();

      for (LcdpBizProcessNodeBean bpNode : bpNodeList) {
         Long bpNodeId = bpNode.getId();
         LcdpBizProcessNodeBean newBpNode = new LcdpBizProcessNodeBean();
         BeanUtils.copyProperties(bpNode, newBpNode);
         newBpNode.setId(ApplicationContextHelper.getNextIdentity());
         newBpNode.setBpId(bp.getId());
         newBpNodeList.add(newBpNode);
         LcdpBizProcessNodeMenuBean bpNodeMenu = bpNodeMenuList.stream().filter(f -> ObjectUtils.equals(bpNodeId, f.getId())).findFirst().orElse(null);
         if (bpNodeMenu != null) {
            bpNodeMenu.setId(newBpNode.getId());
            bpNodeMenu.setBpId(bp.getId());
            newBpNodeMenuList.add(bpNodeMenu);
         }

         List<LcdpBizProcessNodeUserBean> filterBpNodeUserList = bpNodeUserList.stream()
            .filter(f -> ObjectUtils.equals(bpNodeId, f.getBpNodeId()))
            .collect(Collectors.toList());
         filterBpNodeUserList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setBpId(bp.getId());
            e.setBpNodeId(newBpNode.getId());
         });
         newBpNodeUserList.addAll(filterBpNodeUserList);
         List<LcdpBizProcessNodeRoleBean> filterBpNodeRoleList = bpNodeRoleList.stream()
            .filter(f -> ObjectUtils.equals(bpNodeId, f.getBpNodeId()))
            .collect(Collectors.toList());
         filterBpNodeRoleList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setBpId(bp.getId());
            e.setBpNodeId(newBpNode.getId());
         });
         newBpNodeRoleList.addAll(filterBpNodeRoleList);
         List<LcdpBizProcessNodeActionBean> filterBpNodeActionList = bpNodeActionList.stream()
            .filter(f -> ObjectUtils.equals(bpNodeId, f.getBpNodeId()))
            .collect(Collectors.toList());
         filterBpNodeActionList.forEach(
            e -> {
               Long bpActionId = e.getId();
               e.setId(ApplicationContextHelper.getNextIdentity());
               e.setBpId(bp.getId());
               e.setBpNodeId(newBpNode.getId());
               if (!StringUtils.isEmpty(e.getCheckScriptJava())) {
                  String script = e.getCheckScriptJava();
                  String newScript = script.replaceFirst(
                     "public\\s+class\\s+" + JavaCompilerManager.getClassName(script), "public class LcdpBpActionCheck" + e.getId()
                  );
                  String newScriptClassContent = this.lcdpResourceService.replaceScriptContent(newScript);
                  e.setCheckScriptJava(newScript);
                  e.setCheckScriptClassContent(newScriptClassContent);
               }

               if (!StringUtils.isEmpty(e.getActionScript())) {
                  String script = e.getActionScript();
                  String newScript = script.replaceFirst(
                     "public\\s+class\\s+" + JavaCompilerManager.getClassName(script), "public class LcdpBpActionHandle" + e.getId()
                  );
                  String newScriptClassContent = this.lcdpResourceService.replaceScriptContent(newScript);
                  e.setActionScript(newScript);
                  e.setActionScriptClassContent(newScriptClassContent);
               }

               List<LcdpBpNodeSignUserBean> filterBpSignUserList = bpSignUserList.stream()
                  .filter(f -> ObjectUtils.equals(bpActionId, f.getBpActionId()))
                  .collect(Collectors.toList());
               filterBpSignUserList.forEach(a -> {
                  a.setId(ApplicationContextHelper.getNextIdentity());
                  a.setBpId(bp.getId());
                  a.setBpNodeId(newBpNode.getId());
                  a.setBpActionId(e.getId());
               });
               newBpSignUserList.addAll(filterBpSignUserList);
            }
         );
         List<LcdpBizProcessNodeActionBean> toUpdateNextNodeBpNodeActionList = bpNodeActionList.stream()
            .filter(f -> ObjectUtils.equals(bpNodeId, f.getNextBpNodeId()))
            .collect(Collectors.toList());
         toUpdateNextNodeBpNodeActionList.forEach(node -> node.setNextBpNodeId(newBpNode.getId()));
         newBpNodeActionList.addAll(filterBpNodeActionList);
      }

      this.getDao().insert(bp);
      this.lcdpBizProcessNodeService.getDao().insert(newBpNodeList);
      this.lcdpBizProcessNodeMenuService.getDao().insert(newBpNodeMenuList);
      this.lcdpBizProcessNodeUserService.getDao().insert(newBpNodeUserList);
      this.lcdpBizProcessNodeRoleService.getDao().insert(newBpNodeRoleList);
      this.lcdpBizProcessNodeActionService.getDao().insert(newBpNodeActionList);
      this.lcdpBpNodeSignUserService.getDao().insert(newBpSignUserList);
   }

   @Override
   public Page<LcdpBizProcessNodeBean> selectBpNodePaginationByBpId(Long id, RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessNodeService.selectPaginationByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public LcdpBizProcessNodeBean selectBpNodeDetail(Long bpNodeId, RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessNodeService.selectDetail(bpNodeId);
   }

   @Override
   public Page<LcdpBpInstBean> selectBpInstanceList(RestJsonWrapperBean wrapper) {
      return this.lcdpBpInstService.selectPagination(wrapper);
   }

   @Override
   public boolean determineInitialNode(String bpCode, String bpNodeCode) {
      if (!StringUtils.isEmpty(bpCode) && !StringUtils.isEmpty(bpNodeCode)) {
         LcdpBizProcessBean bpProcess = this.selectFirstByFilter(
            SearchFilter.instance().match("BPCODE", bpCode).filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
            new Order[]{Order.desc("VERSION")}
         );
         if (!ObjectUtils.isEmpty(bpProcess)) {
            LcdpBizProcessNodeBean firstNode = this.lcdpBizProcessNodeService
               .selectFirstByFilter(SearchFilter.instance().match("BPID", bpProcess.getId()).filter(MatchPattern.EQ), new Order[]{Order.asc("ORDERNO")});
            if (!ObjectUtils.isEmpty(firstNode) && !StringUtils.equals(firstNode.getBpNodeCode(), bpNodeCode)) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public RestValidationResultBean validateBpNodeUnique(Long id, RestJsonWrapperBean wrapper) {
      String json = wrapper.getParamValue("vu");
      JSONObject obj = JSONObject.parseObject(json);
      String bpNodeCode = obj.getString("bpNodeCode");
      LcdpBizProcessNodeBean filter = new LcdpBizProcessNodeBean();
      filter.setBpId(id);
      filter.setBpNodeCode(bpNodeCode);
      List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService.getDao().selectList(filter);
      return !bpNodeList.isEmpty()
         ? new RestValidationResultBean(false, I18nHelper.getMessage("core.validator.column.not-unique"))
         : new RestValidationResultBean(true);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertBpNode(Long bpId, RestJsonWrapperBean wrapper) {
      LcdpBizProcessBean bp = this.getDao().selectById(bpId);
      LcdpBizProcessNodeBean bpNode = wrapper.parseUnique(LcdpBizProcessNodeBean.class);
      bpNode.setId(ApplicationContextHelper.getNextIdentity());
      bpNode.setBpId(bpId);
      this.lcdpBizProcessNodeService.getDao().insert(bpNode);
      if ("0".equals(bpNode.getMenuAddFlag())) {
         return bpNode.getId();
      } else {
         LcdpBizProcessNodeMenuBean bpNodeMenu = new LcdpBizProcessNodeMenuBean();
         bpNodeMenu.setId(bpNode.getId());
         bpNodeMenu.setBpId(bpId);
         bpNodeMenu.setParentMenuId(bp.getParentMenuId());
         bpNodeMenu.setParentMenuName(bp.getParentMenuName());
         bpNodeMenu.setMenuName(bpNode.getBpNodeName());
         bpNodeMenu.setChineseMenuName(bpNode.getBpNodeName());
         bpNodeMenu.setEnglishMenuName(bpNode.getBpNodeCode());
         bpNodeMenu.setResourceId(bp.getResourceId());
         bpNodeMenu.setResourceName(bp.getResourceName());
         bpNodeMenu.setResourcePath(bp.getResourcePath());
         String menuParam = MessageFormat.format(
            "'{'\"{0}_EQ\": \"{1}\",\"bpCode\": \"{2}\",\"bpNodeCode\": \"{3}\"}",
            bp.getStatusField(),
            bpNode.getBpNodeCode(),
            bp.getBpCode(),
            bpNode.getBpNodeCode()
         );
         bpNodeMenu.setMenuParam(menuParam);
         this.lcdpBizProcessNodeMenuService.getDao().insert(bpNodeMenu);
         List<LcdpBizProcessNodeActionBean> bpNodeActionList = this.lcdpBizProcessNodeActionService
            .selectListByFilter(
               SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("NEXTBPNODECODE", null).filter(MatchPattern.EQ), new Order[0]
            );
         bpNodeActionList.forEach(e -> {
            e.setNextBpNodeId(bpNode.getId());
            e.setNextBpNodeCode(bpNode.getBpNodeCode());
            e.setNextBpNodeName(bpNode.getBpNodeName());
         });
         this.lcdpBizProcessNodeActionService.getDao().update(bpNodeActionList, "NEXTBPNODEID", "NEXTBPNODECODE", "NEXTBPNODENAME");
         LcdpBizProcessNodeActionBean passAction = new LcdpBizProcessNodeActionBean();
         passAction.setId(ApplicationContextHelper.getNextIdentity());
         passAction.setBpId(bpId);
         passAction.setBpNodeId(bpNode.getId());
         passAction.setBpActionCode("pass");
         passAction.setBpActionName("审核通过");
         this.lcdpBizProcessNodeActionService.getDao().insert(passAction);
         List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService
            .selectListByFilter(
               SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("MENUADDFLAG", "1").filter(MatchPattern.EQ),
               new Order[]{Order.desc("ORDERNO")}
            );
         if (bpNodeList != null && bpNodeList.size() > 1) {
            LcdpBizProcessNodeBean previousNode = bpNodeList.get(1);
            LcdpBizProcessNodeActionBean rejectAction = new LcdpBizProcessNodeActionBean();
            rejectAction.setId(ApplicationContextHelper.getNextIdentity());
            rejectAction.setBpId(bpId);
            rejectAction.setBpNodeId(bpNode.getId());
            rejectAction.setRejectFlag("1");
            rejectAction.setBpActionCode("reject");
            rejectAction.setBpActionName("审核拒绝");
            rejectAction.setNextBpNodeId(previousNode.getId());
            rejectAction.setNextBpNodeCode(previousNode.getBpNodeCode());
            rejectAction.setNextBpNodeName(previousNode.getBpNodeName());
            this.lcdpBizProcessNodeActionService.getDao().insert(rejectAction);
         }

         return bpNode.getId();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteBpNode(RestJsonWrapperBean wrapper) {
      List<Long> bpNodeIdList = wrapper.parseId(Long.class);
      if (!bpNodeIdList.isEmpty()) {
         List<LcdpBizProcessNodeBean> toDeleteBizProcessNodeList = this.lcdpBizProcessNodeService.selectListByIds(bpNodeIdList);
         if (!toDeleteBizProcessNodeList.isEmpty()) {
            List<String> bpNodeCodeList = toDeleteBizProcessNodeList.stream().map(LcdpBizProcessNodeBean::getBpNodeCode).collect(Collectors.toList());
            List<LcdpBizProcessNodeActionBean> toUpdateBizProcessNodeAction = this.lcdpBizProcessNodeActionService
               .selectListByFilter(
                  SearchFilter.instance()
                     .match("NEXTBPNODECODE", bpNodeCodeList)
                     .filter(MatchPattern.OR)
                     .match("BPID", toDeleteBizProcessNodeList.get(0).getBpId())
                     .filter(MatchPattern.EQ),
                  new Order[0]
               );
            toUpdateBizProcessNodeAction.forEach(action -> {
               action.setNextBpNodeId(null);
               action.setNextBpNodeCode(null);
               action.setNextBpNodeName(null);
            });
            this.lcdpBizProcessNodeActionService.getDao().update(toUpdateBizProcessNodeAction, "NEXTBPNODEID", "NEXTBPNODECODE", "NEXTBPNODENAME");
            this.lcdpBizProcessNodeService.delete(wrapper);
         }
      }
   }

   @Override
   public Page<LcdpBizProcessNodeUserBean> selectBpNodeUserPaginationByBpNodeId(Long id, RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessNodeUserService.selectPaginationByFilter(SearchFilter.instance().match("BPNODEID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertBpNodeUser(RestJsonWrapperBean wrapper) {
      List<LcdpBizProcessNodeUserBean> lcdpBizProcessNodeUserList = wrapper.parse(LcdpBizProcessNodeUserBean.class);
      lcdpBizProcessNodeUserList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
      this.lcdpBizProcessNodeUserService.getDao().insert(lcdpBizProcessNodeUserList);
      return lcdpBizProcessNodeUserList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteBpNodeUser(RestJsonWrapperBean wrapper) {
      this.lcdpBizProcessNodeUserService.delete(wrapper);
   }

   @Override
   public Page<LcdpBizProcessNodeRoleBean> selectBpNodeRolePaginationByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessNodeRoleService.selectPaginationByFilter(SearchFilter.instance().match("BPNODEID", bpNodeId).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertBpNodeRole(RestJsonWrapperBean wrapper) {
      List<LcdpBizProcessNodeRoleBean> lcdpBizProcessNodeRoleList = wrapper.parse(LcdpBizProcessNodeRoleBean.class);
      lcdpBizProcessNodeRoleList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
      this.lcdpBizProcessNodeRoleService.getDao().insert(lcdpBizProcessNodeRoleList);
      return lcdpBizProcessNodeRoleList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteBpNodeRole(RestJsonWrapperBean wrapper) {
      this.lcdpBizProcessNodeRoleService.delete(wrapper);
   }

   @Override
   public Page<LcdpBizProcessNodeActionBean> selectBpNodeActionPaginationByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessNodeActionService
         .selectPaginationByFilter(SearchFilter.instance().match("BPNODEID", bpNodeId).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertBpNodeAction(RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeActionBean nodeAction = wrapper.parseUnique(LcdpBizProcessNodeActionBean.class);
      nodeAction.setId(ApplicationContextHelper.getNextIdentity());
      LcdpBizProcessNodeActionBean maxNodeAction = this.lcdpBizProcessNodeActionService
         .selectFirstByFilter(SearchFilter.instance().match("BPNODEID", nodeAction.getBpNodeId()).filter(MatchPattern.EQ), new Order[]{Order.desc("ORDERNO")});
      if (maxNodeAction != null) {
         nodeAction.setOrderNo(maxNodeAction.getOrderNo() + 1L);
      }

      this.lcdpBizProcessNodeActionService.getDao().insert(nodeAction);
      return nodeAction.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteBpNodeAction(RestJsonWrapperBean wrapper) {
      this.lcdpBizProcessNodeActionService.delete(wrapper);
   }

   @Override
   public LcdpBizProcessNodeActionBean selectBpNodeAction(Long bpNodeId, String scriptType, RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeActionBean bpAction = this.lcdpBizProcessNodeActionService.selectByIdIfPresent(bpNodeId);
      if (bpAction != null) {
         String actionScript = bpAction.getActionScript();
         if ("action".equals(scriptType) && StringUtils.isEmpty(actionScript)) {
            bpAction.setActionScript(LcdpScriptTemplateHelper.generateBpActionScriptTemplate(bpNodeId));
         }

         String checkScriptJs = bpAction.getCheckScriptJs();
         if ("checkJs".equals(scriptType) && StringUtils.isEmpty(checkScriptJs)) {
            bpAction.setCheckScriptJs(LcdpScriptTemplateHelper.generateBpCheckScriptJsTemplate());
         }

         String checkScriptJava = bpAction.getCheckScriptJava();
         if ("checkJava".equals(scriptType) && StringUtils.isEmpty(checkScriptJava)) {
            bpAction.setCheckScriptJava(LcdpScriptTemplateHelper.generateBpCheckScriptTemplate(bpNodeId));
         }
      }

      return bpAction;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapNodeAction(Long firstId, Long secondId) {
      this.lcdpBizProcessNodeActionService.swap("ORDERNO", firstId, secondId);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void setActionDefault(RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeActionBean action = wrapper.parseUnique(LcdpBizProcessNodeActionBean.class);
      Long bpId = action.getBpId();
      List<LcdpBizProcessNodeActionBean> allActionList = this.lcdpBizProcessNodeActionService.getDao().selectListByOneColumnValue(bpId, "BPID");
      allActionList.stream().forEach(e -> e.setDefaultFlag("0"));
      this.lcdpBizProcessNodeActionService.getDao().update(allActionList, "DEFAULTFLAG");
      action.setDefaultFlag("1");
      this.lcdpBizProcessNodeActionService.getDao().update(action, "DEFAULTFLAG");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void cancelActionDefault(RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeActionBean action = wrapper.parseUnique(LcdpBizProcessNodeActionBean.class);
      action.setDefaultFlag("0");
      this.lcdpBizProcessNodeActionService.getDao().update(action, "DEFAULTFLAG");
   }

   @Override
   public Page<LcdpBizProcessCommentBean> selectBpCommentPagination(RestJsonWrapperBean wrapper) {
      return this.lcdpBizProcessCommentService.selectPagination(wrapper);
   }

   @Override
   public Page<LcdpBpNodeSignUserBean> selectSignUserPaginationByBpActionId(Long bpActionId, RestJsonWrapperBean wrapper) {
      return this.lcdpBpNodeSignUserService.selectPaginationByFilter(SearchFilter.instance().match("BPACTIONID", bpActionId).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertSignUser(RestJsonWrapperBean wrapper) {
      List<LcdpBpNodeSignUserBean> bpNodeSignUserList = wrapper.parse(LcdpBpNodeSignUserBean.class);
      bpNodeSignUserList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
      this.lcdpBpNodeSignUserService.getDao().insert(bpNodeSignUserList);
      return bpNodeSignUserList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteSignUser(RestJsonWrapperBean wrapper) {
      this.lcdpBpNodeSignUserService.delete(wrapper);
   }

   @Override
   public LcdpBizProcessNodeMenuBean selectBpNodeMenuDetailByBpNodeId(Long bpNodeId, RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeMenuBean bpNodeMenu = this.lcdpBizProcessNodeMenuService.selectDetail(bpNodeId);
      if (bpNodeMenu != null) {
         if (!StringUtils.isEmpty(bpNodeMenu.getTodoIcon())) {
            bpNodeMenu.setExt$Item("todoIconUrl", "/static/icon/todo/" + bpNodeMenu.getTodoIcon());
         }

         if (!StringUtils.isEmpty(bpNodeMenu.getIcon())) {
            bpNodeMenu.setExt$Item("menuIconUrl", this.getMenuIconUrl(bpNodeMenu.getIcon()));
         }
      }

      return bpNodeMenu;
   }

   @Override
   public List<LcdpBizProcessDiagramDTO> selectBpDiagramById(Long id, RestJsonWrapperBean wrapper) {
      boolean isBpInstDisgram = !StringUtils.isEmpty(wrapper.getParamValue("bizId"));
      List<LcdpBizProcessDiagramDTO> lcdpBpDiagramDTOS = new ArrayList<>();
      LcdpBizProcessBean lcdpBizProcess = this.selectById(id);
      List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[]{Order.asc("ORDERNO")});
      if (!bpNodeList.isEmpty()) {
         List<LcdpBizProcessNodeUserBean> bpNodeUserList = this.lcdpBizProcessNodeUserService
            .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
         List<LcdpBizProcessNodeActionBean> bpActionList = this.lcdpBizProcessNodeActionService
            .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
         String bpstatus = "";
         List<LcdpBizProcessCommentBean> bpCommentList = new ArrayList<>();
         LcdpBpInstBean lcdpBpInstBean = null;
         List<LcdpBpInstNodeBean> bpInstNodeList = new ArrayList<>();
         if (isBpInstDisgram) {
            String bizId = wrapper.getParamValue("bizId");
            Map<String, Object> bizMap = this.mapDao.selectById(TableContext.of(lcdpBizProcess.getTableName()), bizId);
            bpstatus = String.valueOf(bizMap.get(lcdpBizProcess.getStatusField()));
            bpCommentList.addAll(
               this.lcdpBizProcessCommentService
                  .selectListByFilter(
                     SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ).match("BIZID", bizId).filter(MatchPattern.EQ), new Order[0]
                  )
            );
            lcdpBpInstBean = this.lcdpBpInstService
               .selectFirstByFilter(
                  SearchFilter.instance().match("BIZID", bizId).filter(MatchPattern.EQ).match("BPID", id).filter(MatchPattern.EQ), new Order[0]
               );
            if (!ObjectUtils.isEmpty(lcdpBpInstBean)) {
               bpInstNodeList.addAll(
                  this.lcdpBpInstNodeService
                     .selectListByFilter(SearchFilter.instance().match("BPINSTID", lcdpBpInstBean.getId()).filter(MatchPattern.EQ), new Order[0])
               );
            }
         }

         int nodeSize = bpNodeList.size();
         int edgeFlag = 1;

         for (int i = 0; i < nodeSize; i++) {
            LcdpBizProcessNodeBean bpNode = bpNodeList.get(i);
            LcdpBpInstNodeBean bpInstNode = bpInstNodeList.stream().filter(instNode -> instNode.getBpNodeId() == bpNode.getId()).findFirst().orElse(null);
            boolean isWorkerNode = StringUtils.equals(bpNode.getBpNodeCode(), bpstatus);
            LcdpBizProcessDiagramDTO bpDiagramNode = this.addBizDiagram(
               i, nodeSize, isWorkerNode, isBpInstDisgram, bpNode, bpInstNode, bpNodeUserList, bpCommentList
            );
            lcdpBpDiagramDTOS.add(bpDiagramNode);
            List<LcdpBizProcessNodeActionBean> filterActionList = bpActionList.stream()
               .filter(f -> ObjectUtils.equals(f.getBpNodeId(), bpNode.getId()))
               .collect(Collectors.toList());
            boolean actionFlag = false;

            for (LcdpBizProcessNodeActionBean bpNodeAction : filterActionList) {
               if (bpNodeAction.getNextBpNodeCode() != null) {
                  if (i != nodeSize - 1 && bpNodeAction.getNextBpNodeCode().equals(bpNodeList.get(i + 1).getBpNodeCode())) {
                     this.addBizLine(lcdpBpDiagramDTOS, "straight-edge", bpNode, bpNodeAction, edgeFlag, i);
                  } else {
                     actionFlag = true;
                     this.addBizLine(lcdpBpDiagramDTOS, "orth-edge", bpNode, bpNodeAction, edgeFlag, i);
                  }
               }
            }

            if (actionFlag) {
               edgeFlag++;
            }
         }
      }

      return lcdpBpDiagramDTOS;
   }

   @Override
   public Page<LcdpBizProcessDiagramCandidatorDTO> selectTaskCandidatorsByBpId(Long id, RestJsonWrapperBean wrapper) {
      List<LcdpBizProcessDiagramCandidatorDTO> bpDiagramTaskCandidatorList = new ArrayList<>();
      List<LcdpBizProcessNodeBean> bpNodeList = this.lcdpBizProcessNodeService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeUserBean> bpNodeUserList = this.lcdpBizProcessNodeUserService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpBizProcessNodeRoleBean> bpNodeRoleList = this.lcdpBizProcessNodeRoleService
         .selectListByFilter(SearchFilter.instance().match("BPID", id).filter(MatchPattern.EQ), new Order[0]);

      for (LcdpBizProcessNodeBean bpNode : bpNodeList) {
         String user = bpNodeUserList.stream()
            .filter(f -> ObjectUtils.equals(f.getBpNodeId(), bpNode.getId()))
            .map(LcdpBizProcessNodeUserBean::getUserName)
            .collect(Collectors.joining("，"));
         String role = bpNodeRoleList.stream()
            .filter(f -> ObjectUtils.equals(f.getBpNodeId(), bpNode.getId()))
            .map(LcdpBizProcessNodeRoleBean::getRoleName)
            .collect(Collectors.joining("，"));
         LcdpBizProcessDiagramCandidatorDTO bpDiagramCandidator = new LcdpBizProcessDiagramCandidatorDTO();
         bpDiagramCandidator.setBpNodeName(bpNode.getBpNodeName());
         bpDiagramCandidator.setUser(user);
         bpDiagramCandidator.setRole(role);
         bpDiagramTaskCandidatorList.add(bpDiagramCandidator);
      }

      return new Pagination<>(bpDiagramTaskCandidatorList);
   }

   @Override
   public List<LcdpBizProcessNodeActionBean> selectBpNodeActionList(RestJsonWrapperBean wrapper) {
      List<String> bizIdList = wrapper.parseId(String.class);
      String bpCode = wrapper.getParamValue("bpCode");
      String bpNodeCode = wrapper.getParamValue("bpNodeCode");
      if (bizIdList.isEmpty() || StringUtils.isEmpty(bpCode)) {
         return CollectionUtils.emptyList();
      } else if (this.validateBizData(bizIdList, bpCode)) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_NULL_OR_NOSAME");
      } else {
         LcdpBizProcessBean bpProcess = null;
         String bizId = bizIdList.get(0);
         LcdpBpInstBean lcdpBpInstBean = this.lcdpBpInstService
            .selectFirstByFilter(
               SearchFilter.instance().match("BPCODE", bpCode).filter(MatchPattern.SEQ).match("BIZID", bizId).filter(MatchPattern.EQ), new Order[0]
            );
         if (!ObjectUtils.isEmpty(lcdpBpInstBean)) {
            bpProcess = this.selectById(lcdpBpInstBean.getBpId());
         } else {
            bpProcess = this.selectFirstByFilter(
               SearchFilter.instance().match("BPCODE", bpCode).filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
               new Order[]{Order.desc("VERSION")}
            );
         }

         if (ObjectUtils.isEmpty(bpProcess)) {
            return CollectionUtils.emptyList();
         } else {
            LcdpBizProcessNodeBean bpNode = this.lcdpBizProcessNodeService
               .selectFirstByFilter(
                  SearchFilter.instance().match("BPID", bpProcess.getId()).filter(MatchPattern.EQ).match("BPNODECODE", bpNodeCode).filter(MatchPattern.SEQ),
                  new Order[0]
               );
            if (bpNode == null) {
               throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPNODE_NOT_EXIST");
            } else {
               Map<String, Object> bizMap = this.mapDao.selectById(TableContext.of(bpProcess.getTableName()), bizIdList.get(0));
               String bpStatus = String.valueOf(bizMap.get(bpProcess.getStatusField()));
               if (!StringUtils.equals(bpNode.getBpNodeCode(), bpStatus)) {
                  throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_OPERATED");
               } else {
                  return this.lcdpBizProcessNodeActionService
                     .selectListByFilter(SearchFilter.instance().match("BPNODEID", bpNode.getId()).filter(MatchPattern.EQ), new Order[0]);
               }
            }
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void approval(RestJsonWrapperBean wrapper) {
      List<String> bizIdList = wrapper.parseId(String.class);
      String actionId = wrapper.getParamValue("actionId");
      String comment = wrapper.getParamValue("comment");
      LcdpBizProcessNodeActionBean bpAction = this.lcdpBizProcessNodeActionService.selectByIdIfPresent(Long.parseLong(actionId));
      if (bpAction == null) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPACTION_NOT_EXIST");
      } else {
         LcdpBizProcessBean bizProcess = this.getDao().selectByIdIfPresent(bpAction.getBpId());
         TableContext tableContext = TableContext.of(bizProcess.getTableName());
         List<Map<String, Object>> bizDataList = this.mapDao.selectListByIdList(tableContext, bizIdList);
         this.executeCheckScript(wrapper, comment, bpAction, bizDataList);
         this.executeBpData(bizDataList, bizProcess, bpAction, comment);
         this.executeActionScript(wrapper, comment, bpAction, bizDataList);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void approval(List<String> bizIdList, String bpCode, String bpNodeCode, String bpActionCode, String comment) {
      if (StringUtils.isBlank(bpCode)) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPCODE_NOT_EMPTY");
      } else {
         LcdpBizProcessBean bpFiler = new LcdpBizProcessBean();
         bpFiler.setBpCode(bpCode);
         LcdpBizProcessBean bp = this.getDao().selectFirstIfPresent(bpFiler, new Order[0]);
         if (bp == null) {
            throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_NOT_EXIST");
         } else {
            TableContext tableContext = TableContext.of(bp.getTableName());
            List<Map<String, Object>> bizDataList = this.mapDao.selectListByIdList(tableContext, bizIdList);
            if (bizDataList != null && bizDataList.size() != 0) {
               LcdpBizProcessNodeBean nodeFilter = new LcdpBizProcessNodeBean();
               nodeFilter.setBpId(bp.getId());
               if (StringUtils.isBlank(bpNodeCode)) {
                  nodeFilter.setBpNodeCode((String)bizDataList.get(0).get(bp.getStatusField()));
               } else {
                  nodeFilter.setBpNodeCode(bpNodeCode);
               }

               LcdpBizProcessNodeBean bpNode = this.lcdpBizProcessNodeService.getDao().selectFirstIfPresent(nodeFilter);
               if (bpNode == null) {
                  throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_NODE_NOT_EXIST");
               } else {
                  LcdpBizProcessNodeActionBean filter = new LcdpBizProcessNodeActionBean();
                  filter.setBpId(bp.getId());
                  filter.setBpNodeId(bpNode.getId());
                  filter.setBpActionCode(bpActionCode);
                  LcdpBizProcessNodeActionBean bpAction = this.lcdpBizProcessNodeActionService.getDao().selectFirstIfPresent(filter);
                  if (bpAction == null) {
                     throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPACTION_NOT_EXIST");
                  } else {
                     this.executeCheckScript(null, comment, bpAction, bizDataList);
                     this.executeBpData(bizDataList, bp, bpAction, comment);
                     this.executeActionScript(null, comment, bpAction, bizDataList);
                  }
               }
            } else {
               throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BIZ_DATA_ERROR");
            }
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void submit(List<Map<String, Object>> bizDataList, String tableName, String bpCode) {
      LcdpBizProcessBean bizProcess = this.selectFirstByFilter(
         SearchFilter.instance().match("BPCODE", bpCode).filter(MatchPattern.SEQ), new Order[]{Order.desc("VERSION")}
      );
      if (!ObjectUtils.isEmpty(bizProcess) && !StringUtils.equals(tableName, bizProcess.getTableName())) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPACTION_NOT_EXIST");
      } else {
         LcdpBizProcessNodeBean firstNode = this.lcdpBizProcessNodeService
            .selectFirstByFilter(SearchFilter.instance().match("BPID", bizProcess.getId()).filter(MatchPattern.SEQ), new Order[]{Order.desc("ORDERNO")});
         LcdpBizProcessNodeActionBean bpAction = this.lcdpBizProcessNodeActionService
            .selectFirstByFilter(SearchFilter.instance().match("BPNODEID", firstNode.getId()).filter(MatchPattern.SEQ), new Order[]{Order.desc("ORDERNO")});
         if (bpAction == null) {
            throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BPACTION_NOT_EXIST");
         } else {
            this.executeCheckScript(null, "编制提交", bpAction, bizDataList);
            this.executeBpData(bizDataList, bizProcess, bpAction, "编制提交");
            this.executeActionScript(null, "编制提交", bpAction, bizDataList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.INSTANT_SAVE")
   @Override
   public void saveScript(RestJsonWrapperBean wrapper) {
      LcdpBizProcessNodeActionBean bpAction = wrapper.parseUnique(LcdpBizProcessNodeActionBean.class);
      String scriptType = wrapper.getParamValue("scriptType");
      if ("action".equals(scriptType)) {
         String actionScript = bpAction.getActionScript();
         String actionScriptClassContent = this.lcdpResourceService.replaceScriptContent(actionScript);
         bpAction.setActionScriptClassContent(actionScriptClassContent);
      } else if ("checkJava".equals(scriptType)) {
         String checkScriptJava = bpAction.getCheckScriptJava();
         String checkScriptClassContent = this.lcdpResourceService.replaceScriptContent(checkScriptJava);
         bpAction.setCheckScriptClassContent(checkScriptClassContent);
      }

      this.lcdpBizProcessNodeActionService.getDao().updateIfChanged(bpAction);
   }

   private void executeCheckScript(RestJsonWrapperBean wrapper, String comment, LcdpBizProcessNodeActionBean bpAction, List<Map<String, Object>> bizDataList) {
      String checkScriptJava = bpAction.getCheckScriptClassContent();
      if (!StringUtils.isEmpty(checkScriptJava)) {
         String className = "LcdpBpActionCheck" + bpAction.getId();
         LcdpResultDTO resultDTO = (LcdpResultDTO)SpringUtils.invoke(className.toLowerCase(), "check", bizDataList, bpAction, comment, wrapper);
         if (resultDTO != null && "500".equals(resultDTO.getCode())) {
            throw new CheckedException(resultDTO.getMessage());
         }
      }
   }

   private void executeActionScript(RestJsonWrapperBean wrapper, String comment, LcdpBizProcessNodeActionBean bpAction, List<Map<String, Object>> bizDataList) {
      String actionScript = bpAction.getActionScriptClassContent();
      if (!StringUtils.isEmpty(actionScript)) {
         String className = "LcdpBpActionHandle" + bpAction.getId();
         SpringUtils.invoke(className.toLowerCase(), "execute", bizDataList, bpAction, comment, wrapper);
      }
   }

   private void executeBpData(List<Map<String, Object>> bizDataList, LcdpBizProcessBean bizProcess, LcdpBizProcessNodeActionBean bpAction, String comment) {
      List<LcdpBizProcessCommentBean> insertBpCommentList = new ArrayList<>();
      List<LcdpBpInstBean> insertBpInstList = new ArrayList<>();
      List<LcdpBpInstNodeBean> insertBpInstNodeList = new ArrayList<>();
      List<LcdpBpInstNodeBean> updateBpInstNodeList = new ArrayList<>();
      List<LcdpBpInstNodeBean> deleteBpInstNodeList = new ArrayList<>();
      List<LcdpBpInstNodeRoleBean> insertBpInstNodeRoleList = new ArrayList<>();
      List<LcdpBpInstNodeUserBean> insertBpInstNodeUserList = new ArrayList<>();
      List<LcdpBpBizSignUserBean> insertBpBizSignUserList = new ArrayList<>();
      List<LcdpBpBizSignUserBean> deleteBpBizSignUserList = new ArrayList<>();
      List<String> bizIdList = bizDataList.stream().map(bizData -> MapUtils.getString(bizData, "ID")).collect(Collectors.toList());
      LcdpBizProcessNodeBean bpNode = this.lcdpBizProcessNodeService.selectById(bpAction.getBpNodeId());
      Long bpId = bpAction.getBpId();
      LcdpBizProcessNodeBean nextBpNode = this.lcdpBizProcessNodeService.selectById(bpAction.getNextBpNodeId());
      List<LcdpBpNodeSignUserBean> nextBpNodeSignUserList = this.lcdpBpNodeSignUserService
         .selectListByFilter(
            SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("BPNODEID", nextBpNode.getId()).filter(MatchPattern.EQ), new Order[0]
         );
      List<LcdpBizProcessNodeRoleBean> nextBpNodeRoleList = this.lcdpBizProcessNodeRoleService
         .selectListByFilter(
            SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("BPNODEID", nextBpNode.getId()).filter(MatchPattern.EQ), new Order[0]
         );
      List<LcdpBizProcessNodeUserBean> nextBpNodeUserList = this.lcdpBizProcessNodeUserService
         .selectListByFilter(
            SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("BPNODEID", nextBpNode.getId()).filter(MatchPattern.EQ), new Order[0]
         );
      List<LcdpBizProcessNodeBean> toRollbackBpInstNodes = this.lcdpBizProcessNodeService
         .selectListByFilter(
            SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.EQ).match("ORDERNO", nextBpNode.getOrderNo()).filter(MatchPattern.NGOE),
            new Order[]{Order.asc("ORDERNO")}
         );
      boolean isReject = "1".equals(bpAction.getRejectFlag());
      boolean isCounterSign = "1".equals(bpAction.getCounterSignFlag());
      List<LcdpBpNodeSignUserBean> signUserList = new ArrayList<>();
      List<LcdpBpBizSignUserBean> allBizSignedUserList = new ArrayList<>();
      if (isCounterSign) {
         signUserList.addAll(
            this.lcdpBpNodeSignUserService
               .selectListByFilter(SearchFilter.instance().match("BPACTIONID", bpAction.getId()).filter(MatchPattern.EQ), new Order[0])
         );
         allBizSignedUserList.addAll(
            this.lcdpBpBizSignUserService
               .selectListByFilter(
                  SearchFilter.instance().match("BPACTIONID", bpAction.getId()).filter(MatchPattern.EQ).match("BIZID", bizIdList).filter(MatchPattern.OR),
                  new Order[0]
               )
         );
      }

      List<LcdpBpInstBean> allBizBpInstList = this.lcdpBpInstService
         .selectListByFilter(
            SearchFilter.instance().match("BPID", bpId).filter(MatchPattern.SEQ).match("BIZID", bizIdList).filter(MatchPattern.OR), new Order[0]
         );
      List<LcdpBpInstNodeBean> allBizBpInstNodeList = this.lcdpBpInstNodeService
         .selectListByFilter(
            SearchFilter.instance()
               .match("BPINSTID", allBizBpInstList.stream().map(LcdpBpInstBean::getId).collect(Collectors.toList()))
               .filter(MatchPattern.OR)
               .match("BPNODEID", bpNode.getId())
               .filter(MatchPattern.EQ)
               .match("STATUS", "COMPLETED")
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
      bizDataList.stream()
         .forEach(
            e -> {
               String bizId = MapUtils.getString(e, "ID");
               boolean handleSignFlag = false;
               LcdpBpInstBean bpInst = allBizBpInstList.stream().filter(bizBpInst -> StringUtils.equals(bizBpInst.getBizId(), bizId)).findFirst().orElse(null);
               LcdpBpInstNodeBean bpInstNode = null;
               if (ObjectUtils.isEmpty(bpInst)) {
                  bpInst = this.buildBpInst(bizId, bizProcess);
                  insertBpInstList.add(bpInst);
                  bpInstNode = this.buildBpInstNode(bpInst.getId(), bpNode, "CREATED");
                  insertBpInstNodeList.add(bpInstNode);
               } else {
                  final LcdpBpInstBean existingBpInst = bpInst;
                  bpInstNode = allBizBpInstNodeList.stream()
                     .filter(bizBpInstNode -> bizBpInstNode.getBpInstId() == existingBpInst.getId())
                     .findFirst()
                     .orElseThrow(() -> new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_OPERATED"));
               }

               if (isCounterSign) {
                  List<LcdpBpBizSignUserBean> concreteSignedUserList = allBizSignedUserList.stream()
                     .filter(signedUser -> StringUtils.equals(bizId, signedUser.getBizId()))
                     .collect(Collectors.toList());
                  if (concreteSignedUserList.stream()
                     .filter(signedUser -> StringUtils.equals(signedUser.getUserId(), LocalContextHelper.getLoginUserId()))
                     .findAny()
                     .isPresent()) {
                     throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_OPERATED");
                  }

                  LcdpBpBizSignUserBean concreteSignedUser = this.buildSignedUser(bizId, bpAction, bpNode, bizProcess);
                  insertBpBizSignUserList.add(concreteSignedUser);
                  concreteSignedUserList.add(concreteSignedUser);
                  handleSignFlag = signUserList.stream()
                     .allMatch(
                        signUser -> concreteSignedUserList.stream()
                           .filter(signedUser -> StringUtils.equals(signedUser.getUserId(), signUser.getUserId()))
                           .findAny()
                           .isPresent()
                     );
               }

               if (!isCounterSign || handleSignFlag) {
                  if (!ObjectUtils.isEmpty(nextBpNode)) {
                     LcdpBpInstNodeBean nextBpInstNode = this.buildBpInstNode(bpInst.getId(), nextBpNode, isReject ? "REJECTED" : "CREATED");
                     insertBpInstNodeList.add(nextBpInstNode);
                     this.addNodeAuditPermissions(
                        bizId,
                        bpInst.getSubmitUserId(),
                        nextBpInstNode,
                        nextBpNode,
                        nextBpNodeUserList,
                        nextBpNodeRoleList,
                        nextBpNodeSignUserList,
                        insertBpInstNodeUserList,
                        insertBpInstNodeRoleList
                     );
                  }

                  if (isReject && !toRollbackBpInstNodes.isEmpty()) {
                     LcdpBpInstBean finalBpInst = bpInst;
                     toRollbackBpInstNodes.forEach(node -> {
                        LcdpBpInstNodeBean toRollbackBpInstNode = new LcdpBpInstNodeBean();
                        toRollbackBpInstNode.setBpInstId(finalBpInst.getId());
                        toRollbackBpInstNode.setBpNodeId(node.getId());
                        deleteBpInstNodeList.add(toRollbackBpInstNode);
                        LcdpBpBizSignUserBean toRollbackBpInstNodeSignedUser = new LcdpBpBizSignUserBean();
                        toRollbackBpInstNodeSignedUser.setBizId(bizId);
                        toRollbackBpInstNodeSignedUser.setBpCode(bizProcess.getBpCode());
                        toRollbackBpInstNodeSignedUser.setBpNodeCode(bpNode.getBpNodeCode());
                        deleteBpBizSignUserList.add(toRollbackBpInstNodeSignedUser);
                     });
                  }

                  if (!ObjectUtils.isEmpty(bpInstNode) && !isReject) {
                     bpInstNode.setStatus("COMPLETED");
                     updateBpInstNodeList.add(bpInstNode);
                  }

                  e.put(bizProcess.getStatusField(), nextBpNode.getBpNodeCode());
                  e.put(bizProcess.getStatusDisplayField(), nextBpNode.getBpNodeName());
                  e.put(bizProcess.getRejectField(), bpAction.getRejectFlag());
               }

               LcdpBizProcessCommentBean bpComment = this.buildBpComment(bizId, comment, bpAction, bpNode, bizProcess);
               insertBpCommentList.add(bpComment);
            }
         );
      this.mapDao
         .update(
            TableContext.of(bizProcess.getTableName()),
            bizDataList,
            bizProcess.getStatusField(),
            bizProcess.getStatusDisplayField(),
            bizProcess.getRejectField()
         );
      this.lcdpBizProcessCommentService.getDao().insert(insertBpCommentList);
      this.lcdpBpBizSignUserService.getDao().insert(insertBpBizSignUserList);
      this.lcdpBpInstService.getDao().insert(insertBpInstList);
      List<Long> bpInstNodeidList = this.lcdpBpInstNodeService.getDao().selectIdList(deleteBpInstNodeList, ArrayUtils.asList("BPINSTID", "BPNODEID"));
      this.rollBackInstNodeDataByIdList(bpInstNodeidList);
      this.lcdpBpBizSignUserService.getDao().deleteBy(deleteBpBizSignUserList, "BIZID", "BPCODE", "BPNODECODE");
      this.lcdpBpInstNodeService.getDao().insert(insertBpInstNodeList);
      this.lcdpBpInstNodeService.getDao().update(updateBpInstNodeList, "STATUS");
      this.lcdpBpInstNodeRoleService.getDao().insert(insertBpInstNodeRoleList);
      this.lcdpBpInstNodeUserService.getDao().insert(insertBpInstNodeUserList);
   }

   private void registerSpring(List<LcdpBizProcessNodeActionBean> bpActionList) {
      for (LcdpBizProcessNodeActionBean action : bpActionList.stream()
         .filter(f -> !StringUtils.isEmpty(f.getActionScriptClassContent()) || !StringUtils.isEmpty(f.getCheckScriptClassContent()))
         .collect(Collectors.toList())) {
         try {
            Long actionId = action.getId();
            if (!StringUtils.isEmpty(action.getActionScriptClassContent())) {
               String className = "LcdpBpActionHandle" + actionId;
               String script = action.getActionScriptClassContent();
               log.info("============>spring加载类" + className);
               Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
               SpringUtils.registerBean(className.toLowerCase(), clazz);
            }

            if (!StringUtils.isEmpty(action.getCheckScriptClassContent())) {
               String className = "LcdpBpActionCheck" + actionId;
               String script = action.getCheckScriptClassContent();
               log.info("============>spring加载类" + className);
               Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
               SpringUtils.registerBean(className.toLowerCase(), clazz);
            }
         } catch (Exception var9) {
            log.error("============>" + var9.getMessage());
            throw new CheckedException(I18nHelper.getMessage("LCDP.MODULE.RESOURCES.TIP.BACKEND_SCRIPT_REGISTRATION_SPRING_ERROR"));
         }
      }
   }

   private boolean validateBizData(List<String> bizIdList, String bpCode) {
      if (bizIdList != null && bizIdList.size() > 0) {
         if (bizIdList.size() == 1) {
            return false;
         } else {
            List<LcdpBpInstBean> lcdpBpInsts = this.lcdpBpInstService
               .selectListByFilter(
                  SearchFilter.instance().match("BPCODE", bpCode).filter(MatchPattern.SEQ).match("BIZID", bizIdList).filter(MatchPattern.OR), new Order[0]
               );
            return !lcdpBpInsts.stream().allMatch(e -> ObjectUtils.equals(lcdpBpInsts.get(0).getBpId(), e.getBpId()));
         }
      } else {
         return true;
      }
   }

   private CoreMenuBean getMenuByBpNodeMenu(LcdpBizProcessNodeMenuBean bpMenu, String i18nCode) {
      CoreMenuBean menu = new CoreMenuBean();
      menu.setId(bpMenu.getMenuId());
      menu.setBpId(bpMenu.getBpId());
      menu.setMenuName(bpMenu.getMenuName());
      menu.setI18nCode(i18nCode);
      menu.setMenuInitName(bpMenu.getMenuName());
      menu.setParentId(bpMenu.getParentMenuId());
      menu.setOrderNo(bpMenu.getOrderNo());
      menu.setIcon(bpMenu.getIcon());
      menu.setTodoIcon(bpMenu.getTodoIcon());
      menu.setResourceId(bpMenu.getResourceId());
      menu.setResourceName(bpMenu.getResourceName());
      menu.setResourcePath(bpMenu.getResourcePath());
      menu.setMenuParam(bpMenu.getMenuParam());
      menu.setClientScript(bpMenu.getClientScript());
      return menu;
   }

   private CoreI18nBean getI18nCodeByBpNodeMenu(String bpCode, String bpNodeCode, String menuName) {
      CoreI18nBean i18n = new CoreI18nBean();
      i18n.setId(ApplicationContextHelper.getNextIdentity());
      i18n.setCode("LCDP.BP.AUTOGEN.MENU.MENUNAME." + bpCode.toUpperCase() + "." + bpNodeCode.toUpperCase());
      i18n.setDefaultMessage(menuName);
      i18n.setDescription(menuName);
      return i18n;
   }

   private List<CoreI18nMessageBean> getI18nMessageByBpNodeMenu(CoreI18nBean i18nCode, LcdpBizProcessNodeMenuBean e) {
      List<CoreI18nMessageBean> messageList = new ArrayList<>();
      CoreI18nMessageBean zh = new CoreI18nMessageBean();
      zh.setId(ApplicationContextHelper.getNextIdentity());
      zh.setI18nConfigId("zh-CN");
      zh.setI18nId(i18nCode.getId());
      zh.setMessage(e.getChineseMenuName());
      messageList.add(zh);
      CoreI18nMessageBean en = new CoreI18nMessageBean();
      en.setId(ApplicationContextHelper.getNextIdentity());
      en.setI18nConfigId("en-US");
      en.setI18nId(i18nCode.getId());
      en.setMessage(e.getEnglishMenuName());
      messageList.add(en);
      return messageList;
   }

   private List<CoreRolePermissionBean> getBpMenuRolePermissionList(String menuId, List<LcdpBizProcessNodeRoleBean> bpNodeRoleList) {
      List<CoreRolePermissionBean> rolePermissionList = new ArrayList<>();
      bpNodeRoleList.forEach(e -> {
         CoreRolePermissionBean rolePermission = new CoreRolePermissionBean();
         rolePermission.setId(ApplicationContextHelper.getNextIdentity());
         rolePermission.setRoleId(e.getRoleId());
         rolePermission.setTargetId(menuId);
         rolePermission.setPermissionTypeId(this.coreMenuService.getDao().getTable());
         rolePermissionList.add(rolePermission);
      });
      return rolePermissionList;
   }

   private void addBizLine(
      List<LcdpBizProcessDiagramDTO> lcdpBpDiagramDTOS,
      String type,
      LcdpBizProcessNodeBean bpNode,
      LcdpBizProcessNodeActionBean bpNodeAction,
      int edgeFlag,
      int index
   ) {
      LcdpBizProcessDiagramDTO lcdpBpDiagramDTO = new LcdpBizProcessDiagramDTO();
      lcdpBpDiagramDTO.setShape(type);
      lcdpBpDiagramDTO.setSource(bpNode.getBpNodeCode());
      lcdpBpDiagramDTO.setTarget(bpNodeAction.getNextBpNodeCode());
      lcdpBpDiagramDTO.setLabel(bpNodeAction.getBpActionName());
      if ("orth-edge".equals(type)) {
         lcdpBpDiagramDTO.addVertices(new LcdpBizProcessPositionDTO(50 + 180 * index, edgeFlag % 2 == 1 ? 0 : 130));
      }

      lcdpBpDiagramDTOS.add(lcdpBpDiagramDTO);
   }

   private LcdpBizProcessDiagramDTO addBizDiagram(
      int index,
      int nodeSize,
      boolean isWorkerNode,
      boolean isBpInstDisgram,
      LcdpBizProcessNodeBean bpNodeBean,
      LcdpBpInstNodeBean bpInstNode,
      List<LcdpBizProcessNodeUserBean> bpNodeUserList,
      List<LcdpBizProcessCommentBean> bpCommentList
   ) {
      LcdpBizProcessDiagramDTO bpDiagramNode = new LcdpBizProcessDiagramDTO();
      bpDiagramNode.setId(bpNodeBean.getBpNodeCode());
      bpDiagramNode.setWidth(index != 0 && index != nodeSize - 1 ? 100 : 60);
      bpDiagramNode.setHeight(60);
      bpDiagramNode.setPosition(
         index == 0 ? new LcdpBizProcessPositionDTO(0, 30) : new LcdpBizProcessPositionDTO(180 * index + (index == nodeSize - 1 ? 40 : 0), 30)
      );
      bpDiagramNode.setLabel(bpNodeBean.getBpNodeName());
      boolean isExistInstNode = !ObjectUtils.isEmpty(bpInstNode);
      boolean isReject = isExistInstNode && "REJECTED".equals(bpInstNode.getStatus());
      if (index == 0) {
         bpDiagramNode.setShape(isBpInstDisgram ? (isReject ? "refused-startNode" : "completed-startNode") : "startNode");
      } else if (index == nodeSize - 1) {
         bpDiagramNode.setShape(isBpInstDisgram && isExistInstNode ? "completed-endNode" : "endNode");
      } else {
         bpDiagramNode.setShape(isBpInstDisgram && isExistInstNode ? (isReject ? "refused-activity" : "completed-activity") : "activity");
      }

      String auditUser = this.getAuditUserByBpStatus(isWorkerNode, bpNodeBean, bpNodeUserList, bpCommentList);
      bpDiagramNode.addData(auditUser);
      return bpDiagramNode;
   }

   private String getAuditUserByBpStatus(
      boolean isWorkerNode, LcdpBizProcessNodeBean bpNodeBean, List<LcdpBizProcessNodeUserBean> bpNodeUserList, List<LcdpBizProcessCommentBean> bpCommentList
   ) {
      String auditUser = "";
      if (isWorkerNode) {
         auditUser = bpCommentList.stream()
            .filter(f -> Objects.equals(bpNodeBean.getBpNodeCode(), f.getBpNodeCode()))
            .map(LcdpBizProcessCommentBean::getAuditUserName)
            .collect(Collectors.joining("，"));
         if (!StringUtils.isEmpty(auditUser)) {
            auditUser = I18nHelper.getMessage("LCDP.MODULE.BPS.TIP.AUDITED_USER") + "：" + auditUser;
         }
      } else {
         auditUser = bpNodeUserList.stream()
            .filter(f -> bpNodeBean.getId().equals(f.getBpNodeId()))
            .map(LcdpBizProcessNodeUserBean::getUserName)
            .collect(Collectors.joining("，"));
         if (!StringUtils.isEmpty(auditUser)) {
            auditUser = I18nHelper.getMessage("LCDP.MODULE.BPS.TIP.NO_AUDIT_USER") + "：" + auditUser;
         }
      }

      return auditUser;
   }

   private CoreTodoConfigBean getTodoConfigByBpNodeMenu(LcdpBizProcessNodeMenuBean bpNodeMenu) {
      CoreTodoConfigBean todoConfig = new CoreTodoConfigBean();
      todoConfig.setId(ApplicationContextHelper.getNextIdentity());
      todoConfig.setName(bpNodeMenu.getMenuName());
      todoConfig.setMenuId(bpNodeMenu.getMenuId());
      todoConfig.setCategory("business");
      todoConfig.setActivatedFlag("1");
      todoConfig.setSelectSql(bpNodeMenu.getTodoScript());
      todoConfig.setIcon(bpNodeMenu.getTodoIcon());
      return todoConfig;
   }

   private String getMenuIconUrl(String menuIcon) {
      if (StringUtils.isEmpty(menuIcon)) {
         return null;
      } else {
         LcdpBizProcessServiceImpl.SelectMenuRequiredData data = LOCAL_SELECT_MENU_REQUIRED_DATA.get();
         if (data != null) {
            return data.getMenuIconUrlPrefix() + menuIcon;
         } else {
            CoreSystemConfigBean systemConfig = this.systemConfigService.selectById("system-config");
            return "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + menuIcon;
         }
      }
   }

   private LcdpBizProcessCommentBean buildBpComment(
      String bizId, String comment, LcdpBizProcessNodeActionBean bpAction, LcdpBizProcessNodeBean bpNode, LcdpBizProcessBean bizProcess
   ) {
      LcdpBizProcessCommentBean bpComment = new LcdpBizProcessCommentBean();
      bpComment.setId(ApplicationContextHelper.getNextIdentity());
      bpComment.setBpId(bizProcess.getId());
      bpComment.setBpActionId(bpAction.getId());
      bpComment.setBpActionCode(bpAction.getBpActionCode());
      bpComment.setBpActionName(bpAction.getBpActionName());
      bpComment.setTableName(bizProcess.getTableName());
      bpComment.setBizId(bizId);
      bpComment.setAuditUserId(LocalContextHelper.getLoginUserId());
      bpComment.setAuditUserName(LocalContextHelper.getLoginUserName());
      bpComment.setComments(comment);
      bpComment.setAuditTime(LocalDateTime.now());
      bpComment.setBpNodeCode(bpNode.getBpNodeCode());
      bpComment.setBpNodeName(bpNode.getBpNodeName());
      bpComment.setNextBpNodeCode(bpAction.getNextBpNodeCode());
      bpComment.setNextBpNodeName(bpAction.getNextBpNodeName());
      return bpComment;
   }

   private void addNodeAuditPermissions(
      String bizId,
      String submitUserId,
      LcdpBpInstNodeBean nextBpInstNode,
      LcdpBizProcessNodeBean nextBpNode,
      List<LcdpBizProcessNodeUserBean> nextBpNodeUserList,
      List<LcdpBizProcessNodeRoleBean> nextBpNodeRoleList,
      List<LcdpBpNodeSignUserBean> nextBpNodeSignUserList,
      List<LcdpBpInstNodeUserBean> insertBpInstNodeUserList,
      List<LcdpBpInstNodeRoleBean> insertBpInstNodeRoleList
   ) {
      List<String> userList = new ArrayList<>();
      List<Long> roleList = new ArrayList<>();
      String special = nextBpNode.getSpecial();

      try {
         if (StringUtils.equals(special, "expression")) {
            String expression = nextBpNode.getSpecialExpression();
            if (!StringUtils.isEmpty(expression)) {
               String[] dynamicValues = StringUtils.split(expression, ",");

               for (String value : dynamicValues) {
                  if (StringUtils.startsWith(value, "u:")) {
                     String user = StringUtils.removeStart(value, "u:");
                     userList.add(user);
                  } else if (StringUtils.startsWith(value, "r:")) {
                     String role = StringUtils.removeStart(value, "r:");
                     roleList.add(NumberUtils.parseLong(role));
                  }
               }
            }
         } else if (StringUtils.equals(special, "submitter")) {
            userList.add(submitUserId);
         } else if (StringUtils.equals(special, "selectSQL")) {
            String selectSQL = nextBpNode.getSpecialExpression();
            String replacedSelectBySql = this.getReplacedSql(selectSQL, bizId);
            List<Map<String, Object>> mapList = this.globalMapper.selectList(replacedSelectBySql);
            mapList.forEach(m -> userList.add((String)m.values().iterator().next()));
         } else {
            userList.addAll(nextBpNodeUserList.stream().map(LcdpBizProcessNodeUserBean::getUserId).collect(Collectors.toList()));
            roleList.addAll(nextBpNodeRoleList.stream().map(LcdpBizProcessNodeRoleBean::getRoleId).collect(Collectors.toList()));
            if (StringUtils.equals(special, "filterSQL")) {
               String filterSql = nextBpNode.getSpecialExpression();
               String replacedFilterBySql = this.getReplacedFilterBySql(filterSql, bizId, userList, roleList);
               List<Map<String, Object>> mapList = this.globalMapper.selectList(replacedFilterBySql);
               userList.clear();
               roleList.clear();
               mapList.forEach(m -> userList.add((String)m.values().iterator().next()));
            }
         }

         userList.addAll(nextBpNodeSignUserList.stream().map(LcdpBpNodeSignUserBean::getUserId).collect(Collectors.toList()));
         insertBpInstNodeRoleList.addAll(roleList.stream().map(nodeRole -> {
            LcdpBpInstNodeRoleBean instNodeRole = new LcdpBpInstNodeRoleBean();
            instNodeRole.setId(ApplicationContextHelper.getNextIdentity());
            instNodeRole.setBpInstNodeId(nextBpInstNode.getId());
            instNodeRole.setRoleId(nodeRole);
            return instNodeRole;
         }).collect(Collectors.toList()));
         insertBpInstNodeUserList.addAll(userList.stream().map(nodeUser -> {
            LcdpBpInstNodeUserBean instNodeUser = new LcdpBpInstNodeUserBean();
            instNodeUser.setId(ApplicationContextHelper.getNextIdentity());
            instNodeUser.setBpInstNodeId(nextBpInstNode.getId());
            instNodeUser.setUserId(nodeUser);
            return instNodeUser;
         }).collect(Collectors.toList()));
      } catch (Exception var20) {
         throw new ApplicationWarningException("LCDP.MODULE.BPS.EXCEPTION.BP_SPECIAL_ERROR");
      }
   }

   private LcdpBpBizSignUserBean buildSignedUser(
      String bizId, LcdpBizProcessNodeActionBean bpAction, LcdpBizProcessNodeBean bpNode, LcdpBizProcessBean bizProcess
   ) {
      LcdpBpBizSignUserBean concreteSignedUser = new LcdpBpBizSignUserBean();
      concreteSignedUser.setId(ApplicationContextHelper.getNextIdentity());
      concreteSignedUser.setBizId(bizId);
      concreteSignedUser.setBpActionId(bpAction.getId());
      concreteSignedUser.setBpActionCode(bpAction.getBpActionCode());
      concreteSignedUser.setBpActionName(bpAction.getBpActionName());
      concreteSignedUser.setBpId(bizProcess.getId());
      concreteSignedUser.setBpCode(bizProcess.getBpCode());
      concreteSignedUser.setBpNodeId(bpNode.getId());
      concreteSignedUser.setBpNodeCode(bpNode.getBpNodeCode());
      concreteSignedUser.setUserId(LocalContextHelper.getLoginUserId());
      return concreteSignedUser;
   }

   private LcdpBpInstNodeBean buildBpInstNode(Long bpInstId, LcdpBizProcessNodeBean bpNode, String status) {
      LcdpBpInstNodeBean bpInstNode = new LcdpBpInstNodeBean();
      bpInstNode.setId(ApplicationContextHelper.getNextIdentity());
      bpInstNode.setBpInstId(bpInstId);
      bpInstNode.setBpNodeCode(bpNode.getBpNodeCode());
      bpInstNode.setBpNodeId(bpNode.getId());
      bpInstNode.setStatus(status);
      return bpInstNode;
   }

   private LcdpBpInstBean buildBpInst(String bizId, LcdpBizProcessBean bizProcess) {
      LcdpBpInstBean bpInst = new LcdpBpInstBean();
      Long bpInstId = ApplicationContextHelper.getNextIdentity();
      bpInst.setBizId(bizId);
      bpInst.setId(bpInstId);
      bpInst.setBpId(bizProcess.getId());
      bpInst.setBpCode(bizProcess.getBpCode());
      bpInst.setTableName(bizProcess.getTableName());
      bpInst.setSubmitUserId(LocalContextHelper.getLoginUserId());
      return bpInst;
   }

   private String getReplacedSql(String sql, String id) {
      sql = StringUtils.replace(sql, "${id}", ObjectUtils.toString(id));
      sql = StringUtils.replace(sql, "${loginUser.id}", LocalContextHelper.getLoginUserId());
      sql = StringUtils.replace(sql, "${loginUser.userName}", LocalContextHelper.getLoginUserName());
      sql = StringUtils.replace(sql, "${loginUser.orgId}", LocalContextHelper.getLoginUser().getOrgId());
      return StringUtils.replace(sql, "${loginUser.orgName}", LocalContextHelper.getLoginUser().getOrgName());
   }

   private String getReplacedFilterBySql(String filterBySql, String id, List<String> userList, List<Long> roleList) {
      filterBySql = StringUtils.replace(filterBySql, "${id}", ObjectUtils.toString(id));
      filterBySql = StringUtils.replace(filterBySql, "${loginUser.id}", LocalContextHelper.getLoginUserId());
      filterBySql = StringUtils.replace(filterBySql, "${loginUser.userName}", LocalContextHelper.getLoginUserName());
      filterBySql = StringUtils.replace(filterBySql, "${loginUser.orgId}", LocalContextHelper.getLoginUser().getOrgId());
      filterBySql = StringUtils.replace(filterBySql, "${loginUser.orgName}", LocalContextHelper.getLoginUser().getOrgName());
      StringBuilder usersParam = new StringBuilder();

      for (String user : userList) {
         if (usersParam.length() > 0) {
            usersParam.append(", ");
         }

         usersParam.append("'").append(user).append("'");
      }

      if (usersParam.length() == 0) {
         usersParam.append("''");
      }

      filterBySql = StringUtils.replace(filterBySql, "${users}", usersParam.toString());
      StringBuilder rolesParam = new StringBuilder();

      for (Long role : roleList) {
         if (rolesParam.length() > 0) {
            rolesParam.append(", ");
         }

         rolesParam.append(role);
      }

      if (usersParam.length() == 0) {
         usersParam.append("''");
      }

      return StringUtils.replace(filterBySql, "${roles}", rolesParam.toString());
   }

   private void rollBackInstNodeDataByIdList(List<Long> bpInstNodeidList) {
      if (!ObjectUtils.isEmpty(bpInstNodeidList)) {
         this.lcdpBpInstNodeService.getDao().deleteByIdList(bpInstNodeidList);
         List<LcdpBpInstNodeUserBean> rollbackUserList = new ArrayList<>();
         List<LcdpBpInstNodeRoleBean> rollbackRoleList = new ArrayList<>();
         bpInstNodeidList.forEach(bpInstNodeid -> {
            LcdpBpInstNodeUserBean lcdpBpInstNodeUserBean = new LcdpBpInstNodeUserBean();
            LcdpBpInstNodeRoleBean lcdpBpInstNodeRoleBean = new LcdpBpInstNodeRoleBean();
            lcdpBpInstNodeUserBean.setBpInstNodeId(bpInstNodeid);
            lcdpBpInstNodeRoleBean.setBpInstNodeId(bpInstNodeid);
            rollbackUserList.add(lcdpBpInstNodeUserBean);
            rollbackRoleList.add(lcdpBpInstNodeRoleBean);
         });
         this.lcdpBpInstNodeUserService.getDao().deleteBy(rollbackUserList, "BPINSTNODEID");
         this.lcdpBpInstNodeRoleService.getDao().deleteBy(rollbackRoleList, "BPINSTNODEID");
      }
   }

   private static class SelectMenuRequiredData {
      private String loginUserId;
      private String loginOrgId;
      private Long loginRoleId;
      private String menuIconUrlPrefix;

      public static LcdpBizProcessServiceImpl.SelectMenuRequiredData of(String loginUserId, String loginOrgId, Long loginRoleId, String menuIconUrlPrefix) {
         LcdpBizProcessServiceImpl.SelectMenuRequiredData instance = new LcdpBizProcessServiceImpl.SelectMenuRequiredData();
         instance.loginUserId = loginUserId;
         instance.loginOrgId = loginOrgId;
         instance.loginRoleId = loginRoleId;
         instance.menuIconUrlPrefix = menuIconUrlPrefix;
         return instance;
      }

      public String getLoginUserId() {
         return this.loginUserId;
      }

      public String getLoginOrgId() {
         return this.loginOrgId;
      }

      public Long getLoginRoleId() {
         return this.loginRoleId;
      }

      public String getMenuIconUrlPrefix() {
         return this.menuIconUrlPrefix;
      }
   }
}
