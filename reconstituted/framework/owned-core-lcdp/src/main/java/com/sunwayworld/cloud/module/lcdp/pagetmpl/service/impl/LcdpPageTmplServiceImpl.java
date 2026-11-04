package com.sunwayworld.cloud.module.lcdp.pagetmpl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplChildTableDTO;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.support.LcdpModuleTempHelper;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplCompBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplConfigBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplDTO;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.LcdpPageTmplDao;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplCompService;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplConfigService;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.service.LcdpPageTmplService;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldDTO;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Repository
@GikamBean
public class LcdpPageTmplServiceImpl implements LcdpPageTmplService {
   @Autowired
   private LcdpPageTmplDao lcdpPageTmplDao;
   @Autowired
   private LcdpPageTmplCompService lcdpPageTmplCompService;
   @Autowired
   private LcdpPageTmplConfigService lcdpPageTmplConfigService;
   @Autowired
   private LcdpResourceHistoryService lcdpResourceHistoryService;
   @Autowired
   private LcdpModulePageCompService lcdpModulePageCompService;
   @Autowired
   private LcdpResourceLockService resourceLockService;
   @Autowired
   @Lazy
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpTableService lcdpTableService;
   @Autowired
   @Lazy
   private LcdpResourceCheckoutRecordService resourceCheckoutRecordService;
   public static List<String> compList = ArrayUtils.asList("TabPanel", "GridToolbar", "Grid", "Button");

   public LcdpPageTmplDao getDao() {
      return this.lcdpPageTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByPageTmpl(RestJsonWrapperBean wrapper) {
      LcdpPageTmplDTO pageTmplDTO = wrapper.parseUnique(LcdpPageTmplDTO.class);
      LcdpResourceBean module = this.resourceService.selectById(pageTmplDTO.getParentId());
      LcdpResourceBean category = this.resourceService.selectById(module.getParentId());
      pageTmplDTO.setModulePath(category.getResourceName() + "." + module.getResourceName() + ".");
      pageTmplDTO.setModuleName(module.getResourceName());
      LcdpResourceBean view = new LcdpResourceBean();
      view.setParentId(pageTmplDTO.getParentId());
      view.setResourceDesc(pageTmplDTO.getResourceDesc());
      view.setResourceName(pageTmplDTO.getResourceName());
      view.setResourceCategory("view");
      view.setId(ApplicationContextHelper.getNextIdentity());
      view.setPath(category.getResourceName() + "." + module.getResourceName() + ".page." + view.getResourceName());
      this.resourceService.getDao().insert(view);
      pageTmplDTO.setViewId(view.getId());
      pageTmplDTO.setViewPath(view.getPath());
      List<LcdpPageTmplCompBean> pageTmplCompList = this.generatePageCompList(pageTmplDTO);
      this.createViewResource(pageTmplDTO, view, pageTmplCompList);
      return view.getId();
   }

   private void createViewResource(LcdpPageTmplDTO pageTmplDTO, LcdpResourceBean view, List<LcdpPageTmplCompBean> pageTmplCompList) {
      LcdpResourceHistoryBean resourceHistory = new LcdpResourceHistoryBean();
      BeanUtils.copyProperties(view, resourceHistory);
      resourceHistory.setId(ApplicationContextHelper.getNextIdentity());
      resourceHistory.setResourceId(view.getId());
      resourceHistory.setVersion(1L);
      resourceHistory.setSubmitFlag("no");
      resourceHistory.setEffectFlag("no");
      resourceHistory.setContent(this.generatePageContent(pageTmplDTO));
      this.lcdpResourceHistoryService.getDao().insert(resourceHistory);
      List<LcdpModulePageCompBean> tempPageCompList = pageTmplCompList.stream().map(comp -> {
         LcdpModulePageCompBean pageComp = new LcdpModulePageCompBean();
         BeanUtils.copyProperties(comp, pageComp);
         pageComp.setModulePageId(view.getId());
         pageComp.setModulePageHistoryId(resourceHistory.getId());
         pageComp.setModulePageVersion(1L);
         return pageComp;
      }).collect(Collectors.toList());
      List<LcdpModulePageCompBean> pageCompList = this.resourceService.generatePageComps(tempPageCompList);
      this.lcdpModulePageCompService.getDao().insert(pageCompList);
      LcdpResourceLockBean lock = new LcdpResourceLockBean();
      lock.setId(ApplicationContextHelper.getNextIdentity());
      lock.setLockUserId(LocalContextHelper.getLoginUserId());
      lock.setResourceId(view.getId().toString());
      lock.setResourceCategory(view.getResourceCategory());
      this.resourceLockService.getDao().insert(lock);
      List<LcdpResourceBean> resourceList = new ArrayList<>();
      resourceList.add(view);
      this.resourceCheckoutRecordService.checkoutResource(resourceList);
   }

   private List<LcdpPageTmplCompBean> generatePageCompList(LcdpPageTmplDTO pageTmplDTO) {
      LcdpPageTmplConfigBean tmplConfig = this.lcdpPageTmplConfigService
         .selectFirstByFilter(
            SearchFilter.instance()
               .match("pageTmplId", pageTmplDTO.getPageTmplId())
               .filter(MatchPattern.EQ)
               .match("UPLOADERFLAG", pageTmplDTO.getUploaderFlag())
               .filter(MatchPattern.EQ),
            new Order[0]
         );
      List<LcdpPageTmplCompBean> pageCompList = this.lcdpPageTmplCompService
         .selectListByFilter(SearchFilter.instance().match("tmplConfigId", tmplConfig.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<LcdpPageTmplCompBean> newCompList = new ArrayList<>();
      LcdpPageTmplDTO dealTmplDTO = new LcdpPageTmplDTO();
      BeanUtils.copyProperties(pageTmplDTO, dealTmplDTO);
      String masterTableName = dealTmplDTO.getMasterTableName();
      LcdpTableDTO masterTableDTO = this.lcdpTableService.selectPhysicalTableInfo(masterTableName);
      List<LcdpTableFieldDTO> masterFieldList = masterTableDTO.getFieldList();
      masterFieldList = masterFieldList.stream().skip(1L).limit(40L).collect(Collectors.toList());
      LcdpPageTmplCompBean masterDataGrid = pageCompList.stream()
         .filter(comp -> "Grid".equals(comp.getType()) && "master".equals(comp.getCompTag()))
         .findFirst()
         .orElse(null);
      if (!ObjectUtils.isEmpty(masterDataGrid)) {
         this.dealMasterGrid(dealTmplDTO, newCompList, masterFieldList, masterDataGrid);
      }

      LcdpPageTmplCompBean formComp = pageCompList.stream()
         .filter(comp -> "Form".equals(comp.getType()) && "master".equals(comp.getCompTag()))
         .findFirst()
         .orElse(null);
      if (!ObjectUtils.isEmpty(formComp)) {
         this.dealMasterForm(dealTmplDTO, newCompList, masterFieldList, formComp);
      }

      List<LcdpModuleTmplChildTableDTO> childTableList = dealTmplDTO.getChildTableList();
      List<String> childTableNameList = childTableList.stream().map(LcdpModuleTmplChildTableDTO::getTableName).collect(Collectors.toList());
      List<LcdpTableDTO> childTableDTOList = this.lcdpTableService.selectPhysicalTableInfoList(childTableNameList);
      List<LcdpPageTmplCompBean> tabList = pageCompList.stream().filter(comp -> "Tab".equals(comp.getType())).collect(Collectors.toList());
      LcdpPageTmplCompBean tab = new LcdpPageTmplCompBean();
      List<LcdpPageTmplCompBean> childTabList = tabList.stream().filter(comp -> "child".equals(comp.getCompTag())).collect(Collectors.toList());
      if (childTabList.isEmpty() && !tabList.isEmpty()) {
         tab = tabList.get(0);
      } else if (!childTabList.isEmpty()) {
         tab = childTabList.get(0);
      }

      JSONObject tabConfig = new JSONObject();
      JSONArray tabChildrenWidgetId = new JSONArray();
      if (!ObjectUtils.isEmpty(tab.getConfig())) {
         tabConfig = JSON.parseObject(tab.getConfig());
         tabChildrenWidgetId = (JSONArray)tabConfig.get("childrenWidgetId");
      }

      if (!childTableDTOList.isEmpty()) {
         Map<String, LcdpTableDTO> tableName2TableDTOMap = childTableDTOList.stream()
            .collect(Collectors.toMap(LcdpTableDTO::getTableName, Function.identity()));
         List<LcdpPageTmplCompBean> gridCompList = pageCompList.stream()
            .filter(comp -> compList.contains(comp.getType()) && "child".equals(comp.getCompTag()))
            .collect(Collectors.toList());
         if (!gridCompList.isEmpty()) {
            tableName2TableDTOMap.forEach(
               (table, tableDTO) -> {
                  List<LcdpTableFieldDTO> fieldDTOList = tableDTO.getFieldList()
                     .stream()
                     .filter(field -> !StringUtils.equals("ID", field.getFieldName()))
                     .collect(Collectors.toList());
                  tableDTO.setFieldList(fieldDTOList);
                  this.copyChildPanelAndGrid(dealTmplDTO, gridCompList, tableDTO, newCompList);
               }
            );
         }

         String tabId = tab.getId();
         gridCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).forEach(comp -> comp.setParentId("remove"));
         List<LcdpPageTmplCompBean> newTabPanelList = newCompList.stream()
            .filter(comp -> "TabPanel".equals(comp.getType()) && null == comp.getCompTag() && comp.getParentId().equals(tabId))
            .collect(Collectors.toList());
         List<String> newTabPanelIdList = newTabPanelList.stream().map(LcdpPageTmplCompBean::getId).collect(Collectors.toList());
         if (!newTabPanelIdList.isEmpty()) {
            tabChildrenWidgetId.remove("child".equals(tab.getCompTag()) ? 0 : 1);
            Object uploaderId = null;
            int dealIndex = "child".equals(tab.getCompTag()) ? 0 : 1;
            if (tabChildrenWidgetId.size() > dealIndex) {
               uploaderId = tabChildrenWidgetId.get(tabChildrenWidgetId.size() - 1);
               tabChildrenWidgetId.remove(tabChildrenWidgetId.size() - 1);
            }

            for (String id : newTabPanelIdList) {
               tabChildrenWidgetId.add(id);
            }

            if (null != uploaderId) {
               tabChildrenWidgetId.add(uploaderId);
            }
         }
      } else {
         pageCompList.stream()
            .filter(comp -> "child".equals(comp.getCompTag()) && "TabPanel".equals(comp.getType()))
            .forEach(comp -> comp.setParentId("remove"));
         if (tabChildrenWidgetId.size() > 0) {
            if (dealTmplDTO.getPageTmplId() != LcdpConstant.PAGE_TMPL_UP_DOWN) {
               tabChildrenWidgetId.remove("child".equals(tab.getCompTag()) ? 0 : 1);
            } else {
               tabChildrenWidgetId.remove(0);
            }
         }
      }

      if (tabChildrenWidgetId.size() == 0) {
         tab.setParentId("remove");
         String tabId = tab.getId();
         pageCompList.stream().filter(comp -> "LayoutCenter".equals(comp.getType()) && !StringUtils.equals("master", comp.getCompTag())).forEach(comp -> {
            int removeId = 0;
            JSONObject layoutCenterConfig = JSON.parseObject(comp.getConfig());
            JSONArray childrenWidgetId = (JSONArray)layoutCenterConfig.get("childrenWidgetId");

            for (int ix = 0; ix < childrenWidgetId.size(); ix++) {
               if (childrenWidgetId.getString(ix).equals(tabId)) {
                  removeId = ix;
               }
            }

            childrenWidgetId.remove(removeId);
            layoutCenterConfig.put("childrenWidgetId", childrenWidgetId);
            comp.setConfig(layoutCenterConfig.toJSONString());
         });
      } else {
         tabConfig.put("childrenWidgetId", tabChildrenWidgetId);
         tab.setConfig(tabConfig.toJSONString());
      }

      List<LcdpPageTmplCompBean> uploaderList = pageCompList.stream().filter(comp -> "Uploader".equals(comp.getType())).collect(Collectors.toList());
      if (!uploaderList.isEmpty()) {
         LcdpPageTmplCompBean uploaderComp = uploaderList.get(0);
         JSONObject uploaderConfig = JSON.parseObject(uploaderComp.getConfig());
         uploaderConfig.put("id", String.format(uploaderConfig.getString("id"), dealTmplDTO.getModuleName().toLowerCase()));
         uploaderConfig.put("dbTable", dealTmplDTO.getMasterTableName());
         uploaderConfig.put("preview", true);
         uploaderConfig.put("gridPage", true);
         uploaderConfig.remove("toolbarAlign");
         uploaderConfig.put("dependentWidgetId", dealTmplDTO.getDependentWidgetId());
         uploaderComp.setConfig(uploaderConfig.toJSONString());
      }

      List<LcdpPageTmplCompBean> windowToolbarList = pageCompList.stream().filter(comp -> "WindowToolbar".equals(comp.getType())).collect(Collectors.toList());
      if (!windowToolbarList.isEmpty()) {
         windowToolbarList.forEach(toolbar -> {
            JSONObject toolBarConfig = JSON.parseObject(toolbar.getConfig());
            toolBarConfig.remove("toolbarAlign");
            toolbar.setConfig(toolBarConfig.toJSONString());
         });
      }

      List<LcdpPageTmplCompBean> gridToolbarList = pageCompList.stream().filter(comp -> "GridToolbar".equals(comp.getType())).collect(Collectors.toList());
      List<LcdpPageTmplCompBean> newGridToolBarList = newCompList.stream().filter(comp -> "GridToolbar".equals(comp.getType())).collect(Collectors.toList());
      if (!gridToolbarList.isEmpty()) {
         if (!newGridToolBarList.isEmpty()) {
            gridToolbarList.addAll(newGridToolBarList);
         }

         gridToolbarList.forEach(toolbar -> {
            JSONObject toolBarConfig = JSON.parseObject(toolbar.getConfig());
            toolBarConfig.remove("toolbarAlign");
            toolbar.setConfig(toolBarConfig.toJSONString());
         });
      }

      List<LcdpPageTmplCompBean> TabPanelList = pageCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).collect(Collectors.toList());
      TabPanelList.forEach(tabPanelx -> {
         JSONObject panelConfigx = JSON.parseObject(tabPanelx.getConfig());
         JSONObject title = panelConfigx.getJSONObject("title");
         if (StringUtils.equals(title.getString("zh-CN"), "基本信息")) {
            title.put("type", "i18n");
            title.put("i18nCode", "GIKAM.FORM.BASE_INFO");
            title.remove("en-US");
            panelConfigx.put("title", title);
            tabPanelx.setConfig(panelConfigx.toJSONString());
         }
      });
      pageCompList.addAll(newCompList);
      List<LcdpPageTmplCompBean> buttonList = pageCompList.stream().filter(comp -> "Button".equals(comp.getType())).collect(Collectors.toList());
      if (!buttonList.isEmpty()) {
         for (int i = 0; i < buttonList.size(); i++) {
            JSONObject buttonConfig = JSON.parseObject(buttonList.get(i).getConfig());
            if (buttonConfig.getString("id").startsWith("Button_")) {
               buttonConfig.put("id", "Button_" + (i + 1));
               if (StringUtils.equals(buttonConfig.getString("iconType"), "submit")) {
                  buttonConfig.put("iconType", "batch-submit");
               }
            }

            if (buttonConfig.getString("id").startsWith("detail-button-submit") && StringUtils.equals(buttonConfig.getString("iconType"), "submit")) {
               buttonConfig.put("iconType", "batch-submit");
            }

            this.dealButtonI18NCode(buttonConfig);
            buttonList.get(i).setConfig(buttonConfig.toJSONString());
         }
      }

      List<LcdpPageTmplCompBean> tabPanelList = pageCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).collect(Collectors.toList());
      List<String> idIndexList = tabPanelList.stream().map(tabPanelx -> {
         JSONObject panelConfigx = JSON.parseObject(tabPanelx.getConfig());
         return panelConfigx.getString("id").split("_")[1];
      }).filter(indexx -> !StringUtils.isEmpty(indexx) && NumberUtils.isNumber(indexx)).collect(Collectors.toList());
      Long index = 0L;
      if (!idIndexList.isEmpty()) {
         index = idIndexList.stream().map(Long::valueOf).max(Comparator.comparing(Function.identity())).get();
      }

      for (int i = 0; i < tabPanelList.size(); i++) {
         LcdpPageTmplCompBean tabPanel = tabPanelList.get(i);
         JSONObject panelConfig = JSON.parseObject(tabPanel.getConfig());
         if (panelConfig.getString("id").startsWith("TabPanel_")) {
            panelConfig.put("id", "TabPanel_" + (index + (i + 1)));
         }

         tabPanelList.get(i).setConfig(panelConfig.toJSONString());
      }

      return pageCompList;
   }

   private void dealMasterGrid(
      LcdpPageTmplDTO dealTmplDTO, List<LcdpPageTmplCompBean> newCompList, List<LcdpTableFieldDTO> masterFieldList, LcdpPageTmplCompBean masterDataGrid
   ) {
      JSONObject masterGridConfig = JSON.parseObject(masterDataGrid.getConfig());
      String gridId = String.format(masterGridConfig.getString("id"), dealTmplDTO.getModuleName().toLowerCase());
      LcdpPageTmplCompBean processStatus = null;
      List<LcdpTableFieldDTO> dealFiledList = masterFieldList.stream().map(field -> {
         LcdpTableFieldDTO fieldDTO = new LcdpTableFieldDTO();
         BeanUtils.copyProperties(field, fieldDTO);
         return fieldDTO;
      }).collect(Collectors.toList());
      List<LcdpTableFieldDTO> createFieldList = dealFiledList.stream()
         .filter(mf -> LcdpConstant.CREATE_COLUMN_LIST.contains(mf.getFieldName().toUpperCase()))
         .collect(Collectors.toList());
      dealFiledList.removeAll(createFieldList);
      LcdpTableFieldDTO isHaveBpField = dealFiledList.stream()
         .filter(field -> StringUtils.equalsIgnoreCase("PROCESSSTATUS", field.getFieldName()))
         .findFirst()
         .orElse(null);
      dealFiledList = dealFiledList.stream().filter(field -> !StringUtils.equalsIgnoreCase("PROCESSSTATUS", field.getFieldName())).collect(Collectors.toList());
      if (isHaveBpField != null) {
         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get("processStatus");
         String fieldConfig = String.format(fieldTmpl, dealTmplDTO.getMasterTableName());
         processStatus = new LcdpPageTmplCompBean();
         processStatus.setId(StringUtils.randomUUID());
         processStatus.setParentId(masterDataGrid.getId());
         processStatus.setType("GridColumn");
         processStatus.setConfig(fieldConfig);
      }

      List<LcdpTableFieldDTO> columnConfigList = this.buildGridColumnConfigList(gridId, dealFiledList);
      columnConfigList.addAll(this.buildGridColumnConfigList(gridId, createFieldList));
      List<LcdpPageTmplCompBean> gridColumnList = new ArrayList<>();
      gridColumnList.addAll(this.bulidCommonColumns(masterDataGrid.getId()));
      if (processStatus != null) {
         gridColumnList.add(processStatus);
      }

      columnConfigList.forEach(
         filed -> {
            LcdpPageTmplCompBean gridColumn = new LcdpPageTmplCompBean();
            gridColumn.setId(StringUtils.randomUUID());
            gridColumn.setParentId(masterDataGrid.getId());
            gridColumn.setType("GridColumn");
            JSONObject columnConfig = JSON.parseObject(
               filed.getExt$Item("columnconfig") == null ? filed.getExt$Item("columnconfig") : filed.getExt$Item("columnconfig").replace("\\", "\\\\")
            );
            columnConfig.put("editor", true);
            if (LcdpConstant.CREATE_COLUMN_LIST.contains(filed.getFieldName().toUpperCase()) || LcdpConstant.PAGE_TMPL_CHOOSE == dealTmplDTO.getPageTmplId()) {
               columnConfig.put("editor", false);
            }

            gridColumn.setConfig(columnConfig.toJSONString());
            gridColumnList.add(gridColumn);
         }
      );
      if (LcdpConstant.PAGE_TMPL_CHOOSE == dealTmplDTO.getPageTmplId()) {
         gridColumnList.forEach(column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (columnConfig.get("checkbox") != null && columnConfig.getBoolean("checkbox")) {
               columnConfig.put("getSelectType__$S$__", dealTmplDTO.getModuleName().toLowerCase() + "_choose_list_grid_selection_getselectiontype");
               column.setConfig(columnConfig.toJSONString());
            }
         });
      }

      gridColumnList.forEach(
         column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (!StringUtils.equals(columnConfig.getString("gridColumnType"), "checkbox")
               && !StringUtils.equals(columnConfig.getString("gridColumnType"), "index")) {
               JSONObject title = columnConfig.getJSONObject("title");
               title.put("type", "i18n");
               title.put("i18nCode", (dealTmplDTO.getMasterTableName() + "." + columnConfig.getString("field")).toUpperCase());
               title.remove("en-US");
               columnConfig.put("title", title);
               column.setConfig(columnConfig.toJSONString());
            }
         }
      );
      List<String> gridColumnIdList = gridColumnList.stream().map(LcdpPageTmplCompBean::getId).collect(Collectors.toList());
      masterGridConfig.put("childrenWidgetId", gridColumnIdList.toArray());
      newCompList.addAll(gridColumnList);
      masterGridConfig.put("id", gridId);
      String urlPath = dealTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(dealTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      String gridUrl = String.format(masterGridConfig.getString("url"), urlPath);
      String urlSuffix = ".selectPaginationData";
      if (LcdpConstant.PAGE_TMPL_CHOOSE == dealTmplDTO.getPageTmplId()) {
         urlSuffix = ".selectChoosablePaginationData";
      }

      gridUrl = urlPath + urlSuffix;
      masterGridConfig.put("url", gridUrl);
      String instantSavePath = urlPath + ".updateData";
      masterGridConfig.put("instantSavePath", instantSavePath);
      masterGridConfig.put("getRequestData__$S$__", "getGridRequestData");
      masterGridConfig.put("onRowActive__$S$__", "");
      masterGridConfig.put("onLoadSuccess__$S$__", "");
      masterGridConfig.put("confirmField", columnConfigList.stream().findFirst().get().getFieldName().toLowerCase());
      masterDataGrid.setConfig(this.dealGridConfig(masterGridConfig));
      dealTmplDTO.setDependentWidgetId(gridId);
   }

   private void dealMasterForm(
      LcdpPageTmplDTO dealTmplDTO, List<LcdpPageTmplCompBean> newCompList, List<LcdpTableFieldDTO> masterFieldList, LcdpPageTmplCompBean formComp
   ) {
      List<LcdpPageTmplCompBean> formFieldList = new ArrayList<>();
      JSONObject formConfig = JSON.parseObject(formComp.getConfig());
      String formId = String.format(formConfig.getString("id"), dealTmplDTO.getModuleName().toLowerCase());
      List<LcdpTableFieldDTO> fieldDTOList = this.buildFormFieldConfigList(formId, masterFieldList);
      fieldDTOList.forEach(fieldDTO -> {
         LcdpPageTmplCompBean formField = new LcdpPageTmplCompBean();
         formField.setId(StringUtils.randomUUID());
         formField.setParentId(formComp.getId());
         formField.setType(fieldDTO.getFieldType());
         formField.setConfig(this.fieldToAddValidateConfig(fieldDTO, "field"));
         JSONObject fieldConfig = JSON.parseObject(formField.getConfig());
         if (LcdpConstant.CREATE_COLUMN_LIST.contains(fieldDTO.getFieldName().toUpperCase())) {
            fieldConfig.put("readonly", true);
            formField.setConfig(fieldConfig.toJSONString());
         }

         JSONObject title = fieldConfig.getJSONObject("title");
         title.put("type", "i18n");
         title.put("i18nCode", (dealTmplDTO.getMasterTableName() + "." + fieldConfig.getString("field")).toUpperCase());
         title.remove("en-US");
         fieldConfig.put("title", title);
         formField.setConfig(fieldConfig.toJSONString());
         formFieldList.add(formField);
      });
      newCompList.addAll(formFieldList);
      List<String> formFieldIdList = formFieldList.stream().map(LcdpPageTmplCompBean::getId).collect(Collectors.toList());
      formConfig.put("childrenWidgetId", formFieldIdList.toArray());
      formConfig.put("id", formId);
      String urlPath = dealTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(dealTmplDTO.getMasterTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      String formUrl = String.format(formConfig.getString("url"), urlPath);
      formConfig.put("url", formUrl);
      String formInstantSavePath = String.format(formConfig.getString("instantSavePath"), urlPath);
      formConfig.put("instantSavePath", formInstantSavePath);
      if (LcdpConstant.PAGE_TMPL_LEFT_RIGHT == dealTmplDTO.getPageTmplId()) {
         formConfig.put("dependentWidgetId", dealTmplDTO.getDependentWidgetId());
         formConfig.put("onUpdated__$S$__", "onUpdateFormData");
      }

      formComp.setConfig(this.dealFormConfig(formConfig));
      if (StringUtils.isEmpty(dealTmplDTO.getDependentWidgetId())) {
         dealTmplDTO.setDependentWidgetId(formId);
      }
   }

   private List<LcdpTableFieldDTO> buildFormFieldConfigList(String moduleName, List<LcdpTableFieldDTO> masterFieldList) {
      for (LcdpTableFieldDTO tableFieldDTO : masterFieldList) {
         String fieldType = tableFieldDTO.getFieldType();
         if ("varchar".equalsIgnoreCase(fieldType) || "clob".equalsIgnoreCase(fieldType)) {
            tableFieldDTO.setFieldType("text");
         }

         tableFieldDTO.setFieldType(this.firstUppercase(tableFieldDTO.getFieldType())[1]);
         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get(tableFieldDTO.getFieldType());
         String fieldName = tableFieldDTO.getFieldName().toLowerCase();
         String fieldConfig = String.format(fieldTmpl, fieldName, tableFieldDTO.getFieldComment(), fieldName, moduleName + "_" + fieldName);
         tableFieldDTO.setExt$Item("fieldconfig", fieldConfig);
      }

      return masterFieldList;
   }

   private void copyChildPanelAndGrid(
      LcdpPageTmplDTO pageTmplDTO, List<LcdpPageTmplCompBean> gridCompList, LcdpTableDTO tableDTO, List<LcdpPageTmplCompBean> newCompList
   ) {
      LcdpPageTmplCompBean tabPanelComp = gridCompList.stream().filter(comp -> "TabPanel".equals(comp.getType())).findFirst().get();
      LcdpPageTmplCompBean copyTabPanel = new LcdpPageTmplCompBean();
      BeanUtils.copyProperties(tabPanelComp, copyTabPanel);
      copyTabPanel.setId(StringUtils.randomUUID());
      copyTabPanel.setCompTag(null);
      JSONObject panelConfig = JSON.parseObject(copyTabPanel.getConfig());
      Map<String, Object> titlaMap = panelConfig.getObject("title", Map.class);
      titlaMap.put("en-US", tableDTO.getTableName());
      titlaMap.put("zh-CN", tableDTO.getTableDesc());
      panelConfig.put("title", titlaMap);
      LcdpPageTmplCompBean gridComp = gridCompList.stream().filter(comp -> "Grid".equals(comp.getType())).findFirst().get();
      LcdpPageTmplCompBean copyGrid = new LcdpPageTmplCompBean();
      BeanUtils.copyProperties(gridComp, copyGrid);
      copyGrid.setId(StringUtils.randomUUID());
      copyGrid.setParentId(copyTabPanel.getId());
      panelConfig.put("childrenWidgetId", new String[]{copyGrid.getId()});
      copyTabPanel.setConfig(panelConfig.toJSONString());
      JSONObject gridConfig = JSON.parseObject(copyGrid.getConfig());
      String gridId = String.format(
         gridConfig.getString("id"),
         pageTmplDTO.getModuleName().toLowerCase(),
         tableDTO.getTableName().toLowerCase().replaceFirst("t_", "").replaceAll("_", "-")
      );
      List<LcdpTableFieldDTO> fieldList = tableDTO.getFieldList().stream().limit(50L).collect(Collectors.toList());
      List<LcdpTableFieldDTO> createFieldList = fieldList.stream()
         .filter(mf -> LcdpConstant.CREATE_COLUMN_LIST.contains(mf.getFieldName().toUpperCase()))
         .collect(Collectors.toList());
      fieldList.removeAll(createFieldList);
      List<LcdpTableFieldDTO> columnConfigList = this.buildGridColumnConfigList(gridId, fieldList);
      columnConfigList.addAll(this.buildGridColumnConfigList(gridId, createFieldList));
      List<LcdpPageTmplCompBean> gridColumnList = new ArrayList<>();
      gridColumnList.addAll(this.bulidCommonColumns(copyGrid.getId()));
      columnConfigList.forEach(field -> {
         LcdpPageTmplCompBean gridColumn = new LcdpPageTmplCompBean();
         gridColumn.setId(StringUtils.randomUUID());
         gridColumn.setParentId(copyGrid.getId());
         gridColumn.setType("GridColumn");
         JSONObject columnConfig = JSON.parseObject(field.getExt$Item("columnconfig"));
         if (LcdpConstant.CREATE_COLUMN_LIST.contains(field.getFieldName().toUpperCase())) {
            columnConfig.put("editor", false);
            field.setExt$Item("columnconfig", columnConfig.toJSONString());
         }

         gridColumn.setConfig(this.fieldToAddValidateConfig(field, "column"));
         gridColumnList.add(gridColumn);
      });
      gridColumnList.forEach(
         column -> {
            JSONObject columnConfig = JSON.parseObject(column.getConfig());
            if (!StringUtils.equals(columnConfig.getString("gridColumnType"), "checkbox")
               && !StringUtils.equals(columnConfig.getString("gridColumnType"), "index")) {
               JSONObject title = columnConfig.getJSONObject("title");
               title.put("type", "i18n");
               title.put("i18nCode", (tableDTO.getTableName() + "." + columnConfig.getString("field")).toUpperCase());
               title.remove("en-US");
               columnConfig.put("title", title);
               column.setConfig(columnConfig.toJSONString());
            }
         }
      );
      List<String> gridColumnIdList = gridColumnList.stream().map(LcdpPageTmplCompBean::getId).collect(Collectors.toList());
      gridConfig.put("childrenWidgetId", gridColumnIdList.toArray());
      gridConfig.put("id", gridId);
      String urlPath = pageTmplDTO.getModulePath()
         + "server.Lcdp"
         + this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1]
         + "Service";
      gridConfig.put("url", String.format(gridConfig.getString("url"), urlPath));
      gridConfig.put("getRequestData__$S$__", "get" + this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1] + "RequestData");
      String instantSavePath = urlPath + ".updateData";
      gridConfig.put("instantSavePath", instantSavePath);
      LcdpPageTmplCompBean gridToolbarComp = gridCompList.stream().filter(comp -> "GridToolbar".equals(comp.getType())).findFirst().get();
      LcdpPageTmplCompBean copyGridToolbar = new LcdpPageTmplCompBean();
      BeanUtils.copyProperties(gridToolbarComp, copyGridToolbar);
      copyGridToolbar.setId(StringUtils.randomUUID());
      copyGridToolbar.setParentId(copyGrid.getId());
      gridConfig.put("toolbar_childrenWidgetId", new String[]{copyGridToolbar.getId()});
      gridConfig.put("confirmField", columnConfigList.stream().findFirst().get().getFieldName().toLowerCase());
      if (LcdpConstant.PAGE_TMPL_DETAIL != pageTmplDTO.getPageTmplId()) {
         gridConfig.put("dependentWidgetId", pageTmplDTO.getDependentWidgetId());
      }

      copyGrid.setConfig(this.dealGridConfig(gridConfig));
      JSONObject gridToobarConfig = JSON.parseObject(copyGridToolbar.getConfig());
      List<LcdpPageTmplCompBean> buttonCompList = gridCompList.stream().filter(comp -> "Button".equals(comp.getType())).collect(Collectors.toList());
      Map<String, String> buttonIdMap = new HashMap<>();
      List<LcdpPageTmplCompBean> copyButtonList = buttonCompList.stream().map(button -> {
         LcdpPageTmplCompBean copyButton = new LcdpPageTmplCompBean();
         BeanUtils.copyProperties(button, copyButton);
         copyButton.setId(StringUtils.randomUUID());
         buttonIdMap.put(button.getId(), copyButton.getId());
         copyButton.setParentId(copyGridToolbar.getId());
         copyButton.setConfig(String.format(button.getConfig(), this.firstUppercase(tableDTO.getTableName().replaceFirst("T_", "").toLowerCase())[1]));
         return copyButton;
      }).collect(Collectors.toList());
      JSONArray gridToolbarChildrenWidgetId = gridToobarConfig.getJSONArray("childrenWidgetId");

      for (int i = 0; i < gridToolbarChildrenWidgetId.size(); i++) {
         gridToolbarChildrenWidgetId.set(i, buttonIdMap.get(gridToolbarChildrenWidgetId.getString(i)));
      }

      gridToobarConfig.put("childrenWidgetId", gridToolbarChildrenWidgetId);
      copyGridToolbar.setConfig(gridToobarConfig.toJSONString());
      newCompList.add(copyTabPanel);
      newCompList.add(copyGrid);
      newCompList.add(copyGridToolbar);
      newCompList.addAll(copyButtonList);
      newCompList.addAll(gridColumnList);
   }

   private List<LcdpTableFieldDTO> buildGridColumnConfigList(String moduleName, List<LcdpTableFieldDTO> masterFieldList) {
      for (LcdpTableFieldDTO tableFieldDTO : masterFieldList) {
         String fieldType = tableFieldDTO.getFieldType();
         if ("varchar".equalsIgnoreCase(fieldType) || "clob".equalsIgnoreCase(fieldType) || "number".equalsIgnoreCase(fieldType)) {
            tableFieldDTO.setFieldType("text");
         }

         String fieldTmpl = LcdpModuleTempHelper.FIELD_TMPL_MAP.get(tableFieldDTO.getFieldType() + "GridColumn");
         String fieldName = tableFieldDTO.getFieldName().toLowerCase();
         String fieldConfig = String.format(fieldTmpl, fieldName, tableFieldDTO.getFieldComment(), moduleName + "_" + fieldName, fieldName);
         tableFieldDTO.setExt$Item("columnconfig", fieldConfig);
      }

      return masterFieldList;
   }

   private List<LcdpPageTmplCompBean> bulidCommonColumns(String parentId) {
      List<LcdpPageTmplCompBean> gridColumnList = new ArrayList<>();
      LcdpPageTmplCompBean checkBox = new LcdpPageTmplCompBean();
      checkBox.setId(StringUtils.randomUUID());
      checkBox.setParentId(parentId);
      checkBox.setType("GridColumn");
      checkBox.setConfig(LcdpModuleTempHelper.FIELD_TMPL_MAP.get("checkbox"));
      LcdpPageTmplCompBean index = new LcdpPageTmplCompBean();
      index.setId(StringUtils.randomUUID());
      index.setParentId(parentId);
      index.setType("GridColumn");
      index.setConfig(LcdpModuleTempHelper.FIELD_TMPL_MAP.get("index"));
      gridColumnList.add(checkBox);
      gridColumnList.add(index);
      return gridColumnList;
   }

   private String fieldToAddValidateConfig(LcdpTableFieldDTO field, String type) {
      JSONObject fieldConfig = null;
      if (StringUtils.equals("column", type)) {
         fieldConfig = JSON.parseObject(field.getExt$Item("columnconfig"));
      } else if (StringUtils.equals("field", type)) {
         fieldConfig = JSON.parseObject(field.getExt$Item("fieldconfig"));
      }

      String fieldType = field.getFieldType();
      if (StringUtils.equalsIgnoreCase("text", fieldType)) {
         String fieldLength = field.getFieldLength();
         List<String> validateList = new ArrayList<>();
         if (fieldLength != null && ConvertUtils.convert(fieldLength, Long.class, 0L) > 3L) {
            Long length = ConvertUtils.convert(fieldLength, Long.class);
            long validateLength = length / 3L;
            validateList.add("strLength[0," + validateLength + "]");
         }

         fieldConfig.put("validators", validateList.toArray());
      }

      return fieldConfig.toJSONString();
   }

   private String[] firstUppercase(String tableName) {
      String newName = "";
      String firstNewName = "";

      for (int a = 0; a < tableName.length(); a++) {
         char chr = tableName.charAt(a);
         if (a == 0) {
            newName = newName + chr;
            firstNewName = firstNewName + String.valueOf(chr).toUpperCase();
         } else if ("_".equals(String.valueOf(chr))) {
            newName = newName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            firstNewName = firstNewName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            a++;
         } else {
            newName = newName + chr;
            firstNewName = firstNewName + chr;
         }
      }

      return new String[]{newName, firstNewName};
   }

   private String dealGridConfig(JSONObject gridConfig) {
      gridConfig.put("sort", true);
      gridConfig.put("page", true);
      gridConfig.put("serverSearch", true);
      gridConfig.put("cellValueFillByDrag", false);
      gridConfig.put("showCheckedBadgeGt", 1);
      gridConfig.put("contextMenuConfig", false);
      gridConfig.remove("filterOpen");
      gridConfig.remove("toolbarAlign");
      gridConfig.remove("editorInvisible");
      gridConfig.remove("columnsFill");
      gridConfig.remove("contentAlign");
      gridConfig.remove("checkOnActive");
      gridConfig.remove("checkOneOnActive");
      gridConfig.remove("badgeOverflowCount");
      gridConfig.remove("checkContinuous");
      return gridConfig.toJSONString();
   }

   private String generatePageContent(LcdpPageTmplDTO pageTmplDTO) {
      TemplateEngine engine = new TemplateEngine();
      ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
      if (LcdpConstant.PAGE_TMPL_LEFT_RIGHT == pageTmplDTO.getPageTmplId()) {
         resolver.setPrefix("thymeleaf/left-right/");
      } else if (LcdpConstant.PAGE_TMPL_DETAIL == pageTmplDTO.getPageTmplId()) {
         resolver.setPrefix("thymeleaf/penetration/");
      } else {
         resolver.setPrefix("thymeleaf/up-down/");
      }

      resolver.setSuffix(".js");
      resolver.setTemplateMode("TEXT");
      resolver.setCharacterEncoding("UTF-8");
      engine.setTemplateResolver(resolver);
      Context context = new Context();
      LcdpPageTmplDTO dealDTO = new LcdpPageTmplDTO();
      BeanUtils.copyProperties(pageTmplDTO, dealDTO);
      dealDTO.setMasterTableName(dealDTO.getMasterTableName().toLowerCase().replaceAll("_", "-"));
      dealDTO.setMasterScriptPath("");
      dealDTO.setMasterScriptPath("");
      List<LcdpModuleTmplChildTableDTO> childTableList = dealDTO.getChildTableList();
      if (!childTableList.isEmpty()) {
         for (LcdpModuleTmplChildTableDTO child : childTableList) {
            child.setScriptPath(
               pageTmplDTO.getModulePath() + "server.Lcdp" + this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1] + "Service"
            );
            child.setCompName(child.getTableName().toLowerCase().replaceFirst("t_", "").replaceAll("_", "-"));
            child.setFunctionName(this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1]);
            child.setAssociatedField(child.getAssociatedField().toLowerCase());
            child.setChildUrlName("Lcdp" + this.firstUppercase(child.getTableName().replaceFirst("T_", "").toLowerCase())[1]);
         }
      }

      dealDTO.setBpFlag("1".equals(dealDTO.getBpFlag()) ? "true" : "false");
      dealDTO.setUploaderFlag("1".equals(dealDTO.getUploaderFlag()) ? "true" : "false");
      dealDTO.setPreInsertFlag("1".equals(dealDTO.getPreInsertFlag()) ? "true" : "false");
      dealDTO.setModuleName(dealDTO.getModuleName().toLowerCase());
      String masterTableName = pageTmplDTO.getMasterTableName();
      LcdpTableDTO tableDTO = this.lcdpTableService.selectPhysicalTableInfo(masterTableName);
      LcdpTableFieldDTO preInsertDTO = tableDTO.getFieldList().size() > 1
         ? tableDTO.getFieldList().stream().skip(1L).findFirst().get()
         : tableDTO.getFieldList().stream().findFirst().get();
      LcdpTableFieldDTO linkDTO = tableDTO.getFieldList().size() > 1
         ? tableDTO.getFieldList().stream().skip(1L).findFirst().get()
         : tableDTO.getFieldList().stream().findFirst().get();
      linkDTO.setFieldName(linkDTO.getFieldName().toLowerCase());
      context.setVariable("moduleTmpl", dealDTO);
      context.setVariable("preInsert", preInsertDTO);
      context.setVariable("masterTableName", pageTmplDTO.getMasterTableName());
      context.setVariable("link", linkDTO);
      if (LcdpConstant.PAGE_TMPL_LEFT_RIGHT == pageTmplDTO.getPageTmplId()) {
         context.setVariable("preInsertFormPath", dealDTO.getModulePath() + "page." + dealDTO.getResourceName() + "EditPage");
      } else {
         context.setVariable("preInsertFormPath", dealDTO.getModulePath() + "page." + dealDTO.getResourceName() + "DetailPage");
      }

      return engine.process(LcdpConstant.PAGE_TMPL_ID_TO_PAGE_TYPE_MAP.get(pageTmplDTO.getPageTmplId()), context);
   }

   private String dealFormConfig(JSONObject formConfig) {
      formConfig.put("layout", "inline");
      formConfig.put("titleAlign", "right");
      return formConfig.toJSONString();
   }

   private void dealButtonI18NCode(JSONObject buttonConfig) {
      String method = buttonConfig.getString("onClick__$S$__");
      String buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get(method);
      if (method.startsWith("insert")) {
         buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get("insert");
      }

      if (method.startsWith("delete")) {
         buttonI118NCode = LcdpConstant.BUTTON_I18N_MAP.get("delete");
      }

      JSONObject text = buttonConfig.getJSONObject("text");
      text.put("type", "i18n");
      text.put("i18nCode", buttonI118NCode);
      text.remove("en-US");
      buttonConfig.put("text", text);
   }
}
