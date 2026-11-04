package com.sunwayworld.cloud.module.les.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.cloud.module.les.bean.CoreLesDsBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitDTO;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesInstanceDao;
import com.sunwayworld.cloud.module.les.service.CoreLesDsFieldService;
import com.sunwayworld.cloud.module.les.service.CoreLesDsService;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceService;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesInstanceServiceImpl implements CoreLesInstanceService {
   @Autowired
   private CoreLesInstanceDao instanceDao;
   @Autowired
   @Lazy
   private CoreLesInstanceUnitService instanceUnitService;
   @Autowired
   @Lazy
   private CoreLesTemplateService templateService;
   @Autowired
   @Lazy
   private CoreLesTemplateUnitService templateUnitService;
   @Autowired
   @Lazy
   private CoreLesDsService dsService;
   @Autowired
   @Lazy
   private CoreLesDsFieldService dsFieldService;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;
   @Autowired
   @Lazy
   private CoreLesUnitService coreLesUnitService;
   @Autowired
   @Lazy
   private CoreLesInstanceService proxyInstance;
   @Lazy
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreUserService userService;

   public CoreLesInstanceDao getDao() {
      return this.instanceDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByTemplateId(Long templateId, RestJsonWrapperBean wrapper) {
      String lesParam = wrapper.getParamValue("lesparam");
      String bizCategory = wrapper.getParamValue("bizCategory");
      Map<String, String> lesParamMap = JSONObject.parseObject(lesParam, new TypeReference<Map<String, String>>() {}, new Feature[0]);
      return this.proxyInstance.insertByTemplateId(templateId, bizCategory, lesParamMap);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByTemplateId(Long templateId, String bizCategory, Map<String, String> lesParamMap) {
      CoreLesTemplateBean template = this.templateService.selectById(templateId);
      List<CoreLesTemplateUnitBean> templateUnitList = this.templateService.selectUnitList(templateId);
      List<CoreLesUnitBean> lesUnitList = this.coreLesUnitService.selectAll();
      CoreLesInstanceBean instance = new CoreLesInstanceBean();
      instance.setId(ApplicationContextHelper.getNextIdentity());
      instance.setTemplateId(templateId);
      instance.setInstanceName(template.getTemplateName());
      instance.setLesCategory(template.getLesCategory());
      instance.setBizCategory(bizCategory);
      this.getDao().insert(instance);
      String unitParam = JSON.toJSONString(lesParamMap);
      List<CoreLesInstanceUnitBean> instanceUnitList = templateUnitList.stream().map(u -> {
         CoreLesInstanceUnitBean instanceUnit = new CoreLesInstanceUnitBean();
         instanceUnit.setId(ApplicationContextHelper.getNextIdentity());
         instanceUnit.setInstanceId(instance.getId());
         instanceUnit.setTemplateUnitId(u.getId());
         instanceUnit.setUnitCategory(u.getUnitCategory());
         if (StringUtils.isEmpty(u.getUnitTitle())) {
            CoreLesUnitBean lesUnit = lesUnitList.stream().filter(f -> f.getId().equals(u.getUnitCategory())).findFirst().get();
            instanceUnit.setUnitTitle(lesUnit.getUnitName());
         } else {
            instanceUnit.setUnitTitle(u.getUnitTitle());
         }

         instanceUnit.setUnitBody(u.getUnitBody());
         instanceUnit.setConfig(u.getConfig());
         instanceUnit.setOrderNo(u.getOrderNo());
         instanceUnit.setUnitParam(unitParam);
         instanceUnit.setEditFlag(u.getEditFlag());
         instanceUnit.setExportReport(u.getExportReport());
         return instanceUnit;
      }).collect(Collectors.toList());
      instanceUnitList.forEach(
         u -> {
            CoreLesTemplateUnitBean templateUnit = templateUnitList.stream().filter(tu -> u.getTemplateUnitId().equals(tu.getId())).findAny().get();
            if (templateUnit.getParentId() != null) {
               CoreLesInstanceUnitBean parentInstanceUnit = instanceUnitList.stream()
                  .filter(pu -> pu.getTemplateUnitId().equals(templateUnit.getParentId()))
                  .findAny()
                  .orElse(null);
               if (parentInstanceUnit != null) {
                  u.setParentId(parentInstanceUnit.getId());
               }
            }

            if (lesUnitList.stream().anyMatch(f -> f.getId().equals(u.getUnitCategory()) && "1".equals(f.getBindDs()))) {
               this.initInstUnit(u, lesParamMap);
            }
         }
      );
      this.instanceUnitService.getDao().insert(instanceUnitList);
      return instance.getId();
   }

   @Override
   public CoreLesInstanceUnitBean selectUnit(Long id, Long unitId) {
      return this.instanceUnitService.selectDetail(unitId);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void saveUnit(Long id, Long unitId, RestJsonWrapperBean wrapper) {
      this.instanceUnitService.save(unitId, wrapper);
      CoreLesInstanceUnitBean unit = wrapper.parseUnique(CoreLesInstanceUnitBean.class);
      if (unit != null && !StringUtils.isEmpty(unit.getReviewedById())) {
         unit.setReviewedByName(this.userService.selectById(unit.getReviewedById()).getUserName());
         unit.setReviewedTime(LocalDateTime.now());
         this.instanceUnitService.updateIfChanged(unit);
      }
   }

   @Override
   public Page<CoreLesInstanceUnitDTO> selectUnitTreePagination(Long id, RestJsonWrapperBean wrapper) {
      List<CoreLesInstanceUnitBean> sectionList = this.instanceUnitService
         .selectListByFilter(SearchFilter.instance().match("INSTANCEID", id).filter(MatchPattern.EQ), wrapper);
      TreeDescriptor<CoreLesInstanceUnitBean> descriptor = new TreeDescriptor<>("id");
      descriptor.setParseTreeNodeParentIdFunc(t -> "" + t.getParentId());
      descriptor.setParseTreeNodeTextFunction(t -> t.getUnitCategory() + " " + t.getUnitTitle());
      descriptor.setOrderComparator((u1, u2) -> u1.getOrderNo().compareTo(u2.getOrderNo()));
      descriptor.addConsumer((t, n) -> n.setExpanded(true));
      List<CoreLesInstanceUnitDTO> treeNodeList = TreeHelper.parseTreeNode(sectionList, descriptor, CoreLesInstanceUnitDTO.class);
      String href = "/secure/core/les/les-instances/" + id + "/instance-units/tree-grid";
      treeNodeList.forEach(n -> n.setHref(href));
      return new Pagination<>(treeNodeList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertUnit(Long id, RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = wrapper.parseUnique(CoreLesInstanceUnitBean.class);
      unit.setId(ApplicationContextHelper.getNextIdentity());
      unit.setInstanceId(id);
      unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      if (!StringUtils.isEmpty(unit.getConfig())) {
         JSONObject configJSONObject = JSONObject.parseObject(unit.getConfig());
         if (StringUtils.isEmpty(configJSONObject.getString("contentHtml"))) {
            configJSONObject.put("contentHtml", "<div></div>");
            unit.setConfig(JSONObject.toJSONString(configJSONObject));
         }
      }

      if (StringUtils.isEmpty(unit.getUnitTitle())) {
         CoreLesUnitBean lesUnit = this.coreLesUnitService.selectById(unit.getUnitCategory());
         unit.setUnitTitle(lesUnit.getUnitName());
      }

      Long preUnitId = NumberUtils.parseLong(wrapper.getParamValue("preUnitId"));
      if (preUnitId > 0L) {
         this.proxyInstance.updatePostUnitOrderByUnitId(preUnitId, id);
      }

      if (!StringUtils.isEmpty(unit.getReviewedById())) {
         unit.setReviewedByName(this.userService.selectById(unit.getReviewedById()).getUserName());
         unit.setReviewedTime(LocalDateTime.now());
      }

      this.instanceUnitService.getDao().insert(unit);
      return unit.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void updatePostUnitOrderByUnitId(Long preUnitId, Long id) {
      CoreLesInstanceUnitBean preUnit = this.instanceUnitService.selectById(preUnitId);
      CoreLesInstanceUnitBean filter = new CoreLesInstanceUnitBean();
      filter.setInstanceId(id);
      filter.setParentId(preUnit.getParentId());
      List<Long> unitIdList = this.instanceUnitService.getDao().selectIdList(filter, Order.asc("ORDERNO"));
      List<Long> postIdList = new ArrayList<>();
      boolean matched = false;

      for (Long unitId : unitIdList) {
         if (matched) {
            postIdList.add(unitId);
         } else {
            matched = preUnitId.equals(unitId);
         }
      }

      if (!postIdList.isEmpty()) {
         List<CoreLesInstanceUnitBean> postUnitList = new ArrayList<>();

         for (Long postId : postIdList) {
            CoreLesInstanceUnitBean postUnit = new CoreLesInstanceUnitBean();
            postUnit.setId(postId);
            postUnit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
            postUnitList.add(postUnit);
         }

         this.instanceUnitService.getDao().update(postUnitList, "ORDERNO");
      }
   }

   @Override
   public Page<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = wrapper.parseUnique(CoreLesInstanceUnitBean.class);
      List<CoreLesInstanceUnitBean> unitList = this.instanceUnitService.selectList(unit, new Order[0]);
      if (unitList.isEmpty()) {
         return new Pagination<>();
      } else {
         List<String> targetIdList = new ArrayList<>();
         unitList.stream().forEach(e -> targetIdList.add("T_CORE_LES_INSTANCE_UNIT$" + e.getId()));
         List<CoreFileBean> fileList = this.fileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");
         if (fileList.isEmpty()) {
            return new Pagination<>();
         } else {
            fileList.forEach(f -> f.setDownloadUrl(this.fileManager.getDownloadUrl(f)));
            return new Pagination<>(fileList);
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteUnit(Long id, Long unitId) {
      this.instanceUnitService.getDao().delete(unitId);
   }

   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapUnit(Long firstId, Long secondId) {
      this.instanceUnitService.swap("orderNo", firstId, secondId);
   }

   @Override
   public void dedentSection(Long unitId, RestJsonWrapperBean wrapper) {
      this.instanceUnitService.dedentSection(unitId, wrapper);
   }

   @Override
   public void indentSection(Long unitId, Long parentId, RestJsonWrapperBean wrapper) {
      this.instanceUnitService.indentSection(unitId, parentId, wrapper);
   }

   @Override
   public List<Map<String, Object>> selectLesUnitDataByUnitId(Long unitId, RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = this.instanceUnitService.selectById(unitId);
      JSONObject configJson = (JSONObject)JSONObject.parse(unit.getConfig());
      Long unitLesDsId = this.getLesDsId(configJson);
      Map<String, String> lesParamMap = JSONObject.parseObject(unit.getUnitParam(), Map.class);
      return this.getDataByDs(unitLesDsId, lesParamMap);
   }

   protected void initInstUnit(CoreLesInstanceUnitBean unit, Map<String, String> lesParamMap) {
      JSONObject unitBodyJson = (JSONObject)JSONObject.parse(unit.getUnitBody());
      JSONObject configJson = (JSONObject)JSONObject.parse(unit.getConfig());
      if ("formOrQueryList".equals(unit.getUnitCategory())) {
         Long unitLesDsId = this.getLesDsId(configJson);
         List<Map<String, Object>> resultMapList = this.getDataByDs(unitLesDsId, lesParamMap);
         if (resultMapList.size() > 0) {
            JSONArray dataArray = new JSONArray();
            resultMapList.forEach(m -> dataArray.add(m));
            unitBodyJson.put("data", dataArray);
            unit.setUnitBody(unitBodyJson.toJSONString());
         }
      } else if ("chapter".equals(unit.getUnitCategory())) {
         if (configJson == null) {
            return;
         }

         Long unitLesDsId = this.getLesDsId(configJson);
         if (unitLesDsId == null) {
            return;
         }

         List<Map<String, Object>> resultMapList = this.getDataByDs(unitLesDsId, lesParamMap);
         if (resultMapList.size() > 1) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.LES.INSTANCES.TIP.FORM_RESULT_COUNT_ERROR"));
         }

         if (resultMapList.size() == 1) {
            Map<String, Object> beanMap = resultMapList.get(0);
            JSONObject dsConfig = ObjectUtils.isEmpty(configJson.get("dsConfig")) ? null : (JSONObject)JSONObject.parse(configJson.get("dsConfig").toString());
            String replaceKey = dsConfig.getJSONObject("chapterData").getString("replaceKey");
            String dsColumnName = dsConfig.getJSONObject("chapterData").getString("dsColumnName");
            String dynamicValue = ObjectUtils.isEmpty(beanMap.get(dsColumnName)) ? "" : beanMap.get(dsColumnName).toString();
            unitBodyJson.put("content", StringUtils.replace(unitBodyJson.getString("content"), replaceKey, dynamicValue));
            unit.setUnitBody(JSONObject.toJSONString(unitBodyJson));
         }
      } else if ("ckeditor".equals(unit.getUnitCategory()) && unitBodyJson != null) {
         String content = unitBodyJson.getString("content");
         if (StringUtils.isEmpty(content)) {
            unitBodyJson.put("content", "");
            unit.setUnitBody(unitBodyJson.toJSONString());
            return;
         }

         boolean replaced = false;
         Pattern pattern = Pattern.compile("\\$\\{.*\\}");
         Matcher matcher = pattern.matcher(content);
         if (matcher.find()) {
            replaced = true;
         }

         if (!replaced) {
            unitBodyJson.put("content", content);
            unit.setUnitBody(JSON.toJSONString(unitBodyJson));
            return;
         }

         Document doc = Jsoup.parse(content);

         for (Element element : doc.getElementsByAttribute("lesDsId")) {
            Long lesDsId = NumberUtils.parseLong(element.attr("lesDsId"));
            String bindDsField = element.attr("bindDsField");
            List<Map<String, Object>> resultMapListx = this.getDataByDs(lesDsId, lesParamMap);
            if (resultMapListx.isEmpty()) {
               element.text("");
            } else {
               Map<String, Object> beanMap = resultMapListx.get(0);
               if (beanMap != null) {
                  element.text(ObjectUtils.isEmpty(beanMap.get(bindDsField.toUpperCase())) ? "" : beanMap.get(bindDsField.toUpperCase()).toString());
               }
            }

            element.removeAttr("class");
         }

         String removedStartContent = StringUtils.replace(doc.body().html(), "${<span", "<span");
         String removedEndContent = StringUtils.replace(removedStartContent, "span>}", "span>");
         unitBodyJson.put("content", removedEndContent);
         unit.setUnitBody(JSON.toJSONString(unitBodyJson));
      }
   }

   @Override
   public List<Map<String, Object>> getDataByDs(Long lesDsId, Map<String, String> lesParamMap) {
      if (lesDsId != null && lesParamMap != null && !lesParamMap.isEmpty()) {
         CoreLesDsBean lesDs = this.dsService.selectById(lesDsId);
         Map<String, String> paramMap = new CaseInsensitiveLinkedMap<>(lesParamMap);
         String sql = StringUtils.replaceBetween(lesDs.getSqlContent(), "#", "#", paramMap);
         List<Map<String, Object>> resultMapList = null;

         try {
            return this.globalMapper.selectList(sql);
         } catch (Exception var8) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.LES.INSTANCES.TIP.SQL_ERROR") + lesDs.getLesDsName());
         }
      } else {
         return CollectionUtils.emptyList();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long pasteUnit(Long id, RestJsonWrapperBean wrapper) {
      Long copyActiveId = NumberUtils.parseLong(wrapper.getParamValue("copyActiveId"));
      Long pasteActiveId = NumberUtils.parseLong(wrapper.getParamValue("pasteActiveId"));
      CoreLesInstanceUnitBean sourceUnit = this.instanceUnitService.selectById(copyActiveId);
      CoreLesInstanceUnitBean pasteActiveUnit = this.instanceUnitService.selectById(pasteActiveId);
      CoreLesInstanceUnitBean targetUnit = new CoreLesInstanceUnitBean();
      BeanUtils.copyProperties(sourceUnit, targetUnit, "id", "parentId", "orderNo");
      Long targetId = ApplicationContextHelper.getNextIdentity();
      targetUnit.setId(targetId);
      targetUnit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      targetUnit.setParentId(pasteActiveUnit.getParentId());
      List<CoreLesInstanceUnitBean> targetUnitList = new ArrayList<>();
      targetUnitList.add(targetUnit);
      if ("chapter".equals(sourceUnit.getUnitCategory())) {
         List<CoreLesInstanceUnitBean> instanceUnitList = this.instanceUnitService
            .selectListByFilter(SearchFilter.instance().match("INSTANCEID", id).filter(MatchPattern.EQ), new Order[]{Order.asc("ORDERNO")});
         List<CoreLesInstanceUnitBean> sourceChildrenUnitList = instanceUnitList.stream()
            .filter(u -> u.getParentId() != null && u.getParentId().equals(sourceUnit.getId()))
            .collect(Collectors.toList());
         targetUnitList.addAll(this.updateUnitParentId(instanceUnitList, sourceChildrenUnitList, id, targetId));
      }

      if (pasteActiveId > 0L) {
         this.proxyInstance.updatePostUnitOrderByUnitId(pasteActiveId, id);
      }

      this.instanceUnitService.getDao().insert(targetUnitList);
      return targetUnit.getId();
   }

   private Long getLesDsId(JSONObject configJson) {
      return configJson.getJSONObject("dsConfig").getLong("lesDsId");
   }

   private List<CoreLesInstanceUnitBean> updateUnitParentId(
      List<CoreLesInstanceUnitBean> instanceUnitList, List<CoreLesInstanceUnitBean> sourceUnitList, Long instanceId, Long parentId
   ) {
      List<CoreLesInstanceUnitBean> unitList = new ArrayList<>();

      for (CoreLesInstanceUnitBean unitBean : sourceUnitList) {
         Long unitOldId = unitBean.getId();
         Long unitNewId = ApplicationContextHelper.getNextIdentity();
         unitBean.setParentId(parentId);
         List<CoreLesInstanceUnitBean> sourceChildrenUnitList = instanceUnitList.stream()
            .filter(u -> u.getParentId() != null && u.getParentId().equals(unitOldId))
            .collect(Collectors.toList());
         if (sourceChildrenUnitList.size() > 0) {
            unitList.addAll(this.updateUnitParentId(instanceUnitList, sourceChildrenUnitList, instanceId, unitNewId));
         }

         unitBean.setId(unitNewId);
         unitList.add(unitBean);
      }

      return unitList;
   }
}
