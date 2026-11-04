package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitDTO;
import com.sunwayworld.cloud.module.les.bean.CoreLesUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateDao;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateService;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesTemplateServiceImpl implements CoreLesTemplateService {
   @Autowired
   private CoreLesTemplateDao templateDao;
   @Autowired
   @Lazy
   private CoreLesTemplateUnitService unitService;
   @Autowired
   @Lazy
   private CoreLesUnitService coreLesUnitService;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Lazy
   @Autowired
   private CoreFileManager fileManager;

   public CoreLesTemplateDao getDao() {
      return this.templateDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreLesTemplateBean template = wrapper.parseUnique(CoreLesTemplateBean.class);
      template.setId(ApplicationContextHelper.getNextIdentity());
      template.setTemplateNo(this.getLesTemplateNo());
      template.setBaseId(template.getId());
      this.getDao().insert(template);
      return template.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long newVersion(Long id, RestJsonWrapperBean wrapper) {
      GikamConcurrentLocker.block(this.getDao().getTable());
      CoreLesTemplateBean oldTemplate = this.getDao().selectById(id);
      CoreLesTemplateBean newTemplate = new CoreLesTemplateBean();
      BeanUtils.copyProperties(oldTemplate, newTemplate, "approvedById", "approvedByName", "approveDate", "effectiveDate");
      newTemplate.setId(ApplicationContextHelper.getNextIdentity());
      newTemplate.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
      newTemplate.setTemplateStatus("draft");
      EntityHelper.assignCreatedElement(newTemplate);
      CoreLesTemplateBean maxVersionTemplate = this.selectFirstByFilter(
         SearchFilter.instance().match("BASEID", oldTemplate.getBaseId()).filter(MatchPattern.EQ), new Order[]{Order.desc("TEMPLATEVERSION")}
      );
      newTemplate.setTemplateVersion(maxVersionTemplate.getTemplateVersion() + 1L);
      this.getDao().insert(newTemplate);
      List<CoreLesTemplateUnitBean> unitList = this.unitService
         .selectListByFilter(SearchFilter.instance().match("TEMPLATEID", id).filter(MatchPattern.EQ), new Order[0]);
      this.copyUnit(newTemplate.getId(), unitList);
      return newTemplate.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long copy(Long copiedId, RestJsonWrapperBean wrapper) {
      CoreLesTemplateBean template = wrapper.parseUnique(CoreLesTemplateBean.class);
      CoreLesTemplateBean copiedTemplate = this.getDao().selectById(copiedId);
      CoreLesTemplateBean newTemplate = new CoreLesTemplateBean();
      BeanUtils.copyProperties(copiedTemplate, newTemplate, "approvedById", "approvedByName", "approveDate", "effectiveDate");
      newTemplate.setId(ApplicationContextHelper.getNextIdentity());
      newTemplate.setTemplateNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "T-WS-"));
      newTemplate.setProcessStatus(ProcessStatus.DRAFT.name().toLowerCase());
      newTemplate.setTemplateStatus("draft");
      newTemplate.setTemplateName(template.getTemplateName());
      newTemplate.setTemplateVersion(1L);
      newTemplate.setLesTemplateCategory(template.getLesTemplateCategory());
      EntityHelper.assignCreatedElement(newTemplate);
      newTemplate.setBaseId(newTemplate.getId());
      this.getDao().insert(newTemplate);
      List<CoreLesTemplateUnitBean> unitList = this.unitService
         .selectListByFilter(SearchFilter.instance().match("TEMPLATEID", copiedId).filter(MatchPattern.EQ), new Order[0]);
      this.copyUnit(newTemplate.getId(), unitList);
      return newTemplate.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void expireTemplate(RestJsonWrapperBean wrapper) {
      List<CoreLesTemplateBean> templateList = wrapper.parse(CoreLesTemplateBean.class);
      templateList.forEach(m -> m.setTemplateStatus("expired"));
      this.getDao().update(templateList, new String[]{"TEMPLATESTATUS"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
      List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = CoreLesTemplateService.super.completeTask(wrapper);
      List<Long> idList = instanceStatusList.stream().map(e -> e.getId()).collect(Collectors.toList());
      List<CoreLesTemplateBean> templateList = this.selectListByIds(idList);
      List<Long> baseIdList = templateList.stream().map(e -> e.getBaseId()).collect(Collectors.toList());
      List<CoreLesTemplateBean> allVersionTemplateList = this.selectListByFilter(
         SearchFilter.instance().match("BASEID", baseIdList).filter(MatchPattern.OR), new Order[0]
      );
      Map<Long, List<CoreLesTemplateBean>> templateMap = allVersionTemplateList.stream().collect(Collectors.groupingBy(CoreLesTemplateBean::getBaseId));
      List<CoreLesTemplateBean> templateList4UpdateStatus = new ArrayList<>();
      LocalDateTime now = LocalDateTime.now();
      CoreUserBean loginUser = LocalContextHelper.getLoginUser();
      templateList.forEach(m -> {
         if (ProcessStatus.DONE.name().toLowerCase().equals(m.getProcessStatus())) {
            m.setTemplateStatus("current");
            m.setApprovedById(loginUser.getId());
            m.setApprovedByName(loginUser.getUserName());
            m.setApproveDate(now);
            if (templateMap.containsKey(m.getBaseId())) {
               List<CoreLesTemplateBean> pqMethodList = templateMap.get(m.getBaseId());
               pqMethodList.forEach(p -> {
                  if (p.getTemplateStatus().equals("current")) {
                     p.setTemplateStatus("activated");
                     templateList4UpdateStatus.add(p);
                  }
               });
            }
         }
      });
      this.getDao().update(templateList, new String[]{"TEMPLATESTATUS", "APPROVEDBYID", "APPROVEDBYNAME", "APPROVEDATE"});
      if (!templateList4UpdateStatus.isEmpty()) {
         this.getDao().update(templateList4UpdateStatus, new String[]{"TEMPLATESTATUS"});
      }

      return instanceStatusList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertUnit(Long id, RestJsonWrapperBean wrapper) {
      CoreLesTemplateUnitBean unit = wrapper.parseUnique(CoreLesTemplateUnitBean.class);
      unit.setId(ApplicationContextHelper.getNextIdentity());
      unit.setTemplateId(id);
      unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      if (StringUtils.isEmpty(unit.getUnitTitle())) {
         CoreLesUnitBean lesUnit = this.coreLesUnitService.selectById(unit.getUnitCategory());
         unit.setUnitTitle(lesUnit.getUnitName());
      }

      Long preUnitId = NumberUtils.parseLong(wrapper.getParamValue("preUnitId"));
      if (preUnitId > 0L) {
         CoreLesTemplateUnitBean preUnit = this.unitService.selectById(preUnitId);
         CoreLesTemplateUnitBean filter = new CoreLesTemplateUnitBean();
         filter.setTemplateId(id);
         filter.setParentId(preUnit.getParentId());
         List<Long> unitIdList = this.unitService.getDao().selectIdList(filter, Order.asc("ORDERNO"));
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
            List<CoreLesTemplateUnitBean> postUnitList = new ArrayList<>();

            for (Long postId : postIdList) {
               CoreLesTemplateUnitBean postTemplateUnit = new CoreLesTemplateUnitBean();
               postTemplateUnit.setId(postId);
               postTemplateUnit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
               postUnitList.add(postTemplateUnit);
            }

            this.unitService.getDao().update(postUnitList, "ORDERNO");
         }
      }

      this.unitService.getDao().insert(unit);
      return unit.getId();
   }

   @Override
   public CoreLesTemplateUnitBean selectUnit(String id, Long unitId) {
      return this.unitService.selectById(unitId);
   }

   @Override
   public Page<CoreLesTemplateUnitDTO> selectUnitTreePagination(String id, RestJsonWrapperBean wrapper) {
      List<CoreLesTemplateUnitBean> sectionList = this.unitService
         .selectListByFilter(SearchFilter.instance().match("TEMPLATEID", Long.valueOf(id)).filter(MatchPattern.EQ), wrapper);
      TreeDescriptor<CoreLesTemplateUnitBean> descriptor = new TreeDescriptor<>("id");
      descriptor.setParseTreeNodeParentIdFunc(t -> "" + t.getParentId());
      descriptor.setParseTreeNodeTextFunction(t -> t.getUnitCategory() + " " + t.getUnitTitle());
      descriptor.setOrderComparator((u1, u2) -> u1.getOrderNo().compareTo(u2.getOrderNo()));
      descriptor.addConsumer((t, n) -> n.setExpanded(true));
      List<CoreLesTemplateUnitDTO> treeNodeList = TreeHelper.parseTreeNode(sectionList, descriptor, CoreLesTemplateUnitDTO.class);
      String href = "/secure/core/module/item/les-templates/" + id + "/template-units/tree-grid";
      treeNodeList.forEach(n -> n.setHref(href));
      return new Pagination<>(treeNodeList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void saveUnit(String id, Long unitId, RestJsonWrapperBean wrapper) {
      this.unitService.save(unitId, wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteUnit(String id, Long unitId) {
      List<CoreLesTemplateUnitBean> childUnitList = this.unitService.getDao().selectListByOneColumnValue(unitId, "PARENTID");
      if (!childUnitList.isEmpty()) {
         childUnitList.forEach(e -> e.setParentId(null));
         this.unitService.getDao().update(childUnitList, "PARENTID");
      }

      this.unitService.getDao().delete(unitId);
   }

   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapUnit(Long firstId, Long secondId) {
      this.unitService.swap("orderNo", firstId, secondId);
   }

   @Override
   public void dedentSection(Long unitId, RestJsonWrapperBean wrapper) {
      this.unitService.dedentSection(unitId, wrapper);
   }

   @Override
   public void indentSection(Long unitId, Long parentId, RestJsonWrapperBean wrapper) {
      this.unitService.indentSection(unitId, parentId, wrapper);
   }

   @Override
   public Page<CoreFileBean> selectLesFilePagination(RestJsonWrapperBean wrapper) {
      CoreLesTemplateUnitBean unit = wrapper.parseUnique(CoreLesTemplateUnitBean.class);
      List<CoreLesTemplateUnitBean> unitList = this.unitService.selectList(unit, new Order[0]);
      if (unitList.isEmpty()) {
         return new Pagination<>();
      } else {
         List<String> targetIdList = new ArrayList<>();
         unitList.stream().forEach(e -> targetIdList.add("T_CORE_LES_TEMPLATE_UNIT$" + e.getId()));
         List<CoreFileBean> fileList = this.fileService.getDao().selectListByOneColumnValues(targetIdList, "targetId");
         if (fileList.isEmpty()) {
            return new Pagination<>();
         } else {
            fileList.forEach(f -> f.setDownloadUrl(this.fileManager.getDownloadUrl(f)));
            return new Pagination<>(fileList);
         }
      }
   }

   @Cacheable(
      value = {"T_CORE_LES_UNIT_BY_TEMPLATE_ID"},
      key = "#templateId"
   )
   @Override
   public List<CoreLesTemplateUnitBean> selectUnitList(Long templateId) {
      return this.unitService.selectListByFilter(SearchFilter.instance().match("TEMPLATEID", templateId).filter(MatchPattern.EQ), new Order[0]);
   }

   @Override
   public RestValidationResultBean submitValidate(RestJsonWrapperBean wrapper) {
      List<CoreLesTemplateBean> templateList = wrapper.parse(CoreLesTemplateBean.class);
      List<Long> baseIdList = templateList.stream().map(e -> e.getBaseId()).distinct().collect(Collectors.toList());
      List<CoreLesTemplateBean> versionTemplateList = this.selectListByFilter(
         SearchFilter.instance().match("BASEID", baseIdList).filter(MatchPattern.OR), new Order[0]
      );
      boolean validateFlag = true;
      StringBuilder messageBuilder = new StringBuilder();

      for (CoreLesTemplateBean template : templateList) {
         if (versionTemplateList.stream()
            .anyMatch(
               f -> f.getBaseId().equals(template.getBaseId())
                  && f.getTemplateVersion() < template.getTemplateVersion()
                  && !ProcessStatus.DONE.name().toLowerCase().equals(f.getProcessStatus())
            )) {
            validateFlag = false;
            messageBuilder.append(
               template.getTemplateNo()
                  + ":"
                  + I18nHelper.getMessage("CORE.MODULE.LES.LES_TEMPLATES.TIP.EXISTS_LOWER_VERSION_TEMPLATE_PROCESSSATUS_UNDONE")
                  + "<br />"
            );
         }
      }

      return new RestValidationResultBean(validateFlag, messageBuilder.toString());
   }

   public void setProperty(CoreLesTemplateBean lesTemplate, boolean dataRepeatInsertFlag) {
      lesTemplate.setId(ApplicationContextHelper.getNextIdentity());
      lesTemplate.setTemplateVersion(1L);
      lesTemplate.setTemplateNo(this.getLesTemplateNo());
      lesTemplate.setBaseId(lesTemplate.getId());
   }

   private String getLesTemplateNo() {
      return ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "T-WS-");
   }

   private void copyUnit(Long templateId, List<CoreLesTemplateUnitBean> unitList) {
      if (!unitList.isEmpty()) {
         List<Pair<Long, Long>> pairList = unitList.stream()
            .filter(f -> !ObjectUtils.isEmpty(f.getParentId()))
            .map(e -> Pair.of(e.getId(), e.getParentId()))
            .collect(Collectors.toList());
         List<CoreLesTemplateUnitBean> insertUnitList = unitList.stream().map(u -> {
            CoreLesTemplateUnitBean insertUnit = new CoreLesTemplateUnitBean();
            BeanUtils.copyProperties(u, insertUnit);
            insertUnit.setId(ApplicationContextHelper.getNextIdentity());
            insertUnit.setParentId(null);
            insertUnit.setExt$Item("sourceId", u.getId().toString());
            insertUnit.setTemplateId(templateId);
            EntityHelper.assignCreatedElement(insertUnit);
            return insertUnit;
         }).collect(Collectors.toList());
         if (!pairList.isEmpty()) {
            pairList.forEach(
               e -> {
                  CoreLesTemplateUnitBean unit = insertUnitList.stream()
                     .filter(f -> f.getExt$Item("sourceId").equals(((Long)e.getFirst()).toString()))
                     .findFirst()
                     .get();
                  CoreLesTemplateUnitBean parentUnit = insertUnitList.stream()
                     .filter(f -> f.getExt$Item("sourceId").equals(((Long)e.getSecond()).toString()))
                     .findFirst()
                     .get();
                  unit.setParentId(parentUnit.getId());
               }
            );
         }

         this.unitService.getDao().insert(insertUnitList);
      }
   }
}
