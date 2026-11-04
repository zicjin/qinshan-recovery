package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.CompileException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnImportDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDraftDao;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftOrgService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcOrgService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreBpmnDraftServiceImpl implements CoreBpmnDraftService {
   @Autowired
   private CoreBpmnDraftDao draftDao;
   @Autowired
   private CoreBpmnDraftOrgService draftOrgService;
   @Autowired
   private CoreBpmnDiagramService diagramService;
   @Autowired
   @Lazy
   private CoreBpmnProcService procService;
   @Autowired
   @Lazy
   private CoreBpmnProcOrgService procOrgService;

   public CoreBpmnDraftDao getDao() {
      return this.draftDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreBpmnDiagramBean diagram = new CoreBpmnDiagramBean();
      diagram.setId(ApplicationContextHelper.getNextIdentity());
      this.diagramService.getDao().insert(diagram);
      CoreBpmnDraftBean draft = wrapper.parseUnique(CoreBpmnDraftBean.class);
      draft.setId(ApplicationContextHelper.getNextIdentity());
      draft.setDiagramId(diagram.getId());
      this.getDao().insert(draft);
      return draft.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertByProcId(Long procId) {
      CoreBpmnProcBean proc = this.procService.selectById(procId);
      CoreBpmnDiagramBean procDiagram = this.diagramService.selectById(proc.getDiagramId());
      CoreBpmnProcOrgBean procOrgFilter = new CoreBpmnProcOrgBean();
      procOrgFilter.setProcId(procId);
      List<CoreBpmnProcOrgBean> procOrgList = this.procOrgService.selectList(procOrgFilter, new Order[]{Order.asc("ID")});
      CoreBpmnDiagramBean diagram = new CoreBpmnDiagramBean();
      BeanUtils.copyProperties(procDiagram, diagram);
      diagram.setId(ApplicationContextHelper.getNextIdentity());
      Document document = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(diagram.getDiagram()));
      List<Element> elementList = new ArrayList<>();
      elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.USER_TASK));
      elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.START_EVENT));
      elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.END_EVENT));
      elementList.forEach(element -> {
         String id = XmlUtils.getAttributeValue(element, "id");
         String passScriptCode = BpmnDiagramHelper.getPassScriptCode(element);
         if (!StringUtils.isEmpty(passScriptCode)) {
            String scriptName = BpmnDiagramHelper.getPassScriptName(element);
            String newScriptCode = passScriptCode.replaceFirst(scriptName, "BpmnPass" + diagram.getId() + id);
            Attribute passScript = element.attribute("passScript");
            passScript.setValue(newScriptCode);
         }

         String rejectScriptCode = BpmnDiagramHelper.getRejectScriptCode(element);
         if (!StringUtils.isEmpty(rejectScriptCode)) {
            String scriptName = BpmnDiagramHelper.getRejectScriptName(element);
            String newScriptCode = rejectScriptCode.replaceFirst(scriptName, "BpmnReject" + diagram.getId() + id);
            Attribute passScript = element.attribute("rejectScript");
            passScript.setValue(newScriptCode);
         }

         String endScriptName = BpmnDiagramHelper.getEndScriptName(element);
         if (!StringUtils.isEmpty(endScriptName)) {
            String script = BpmnDiagramHelper.getEndScriptCode(element);
            String newScriptCode = script.replaceFirst(endScriptName, "BpmnEnd" + diagram.getId() + id);
            Attribute passScript = element.attribute("endScript");
            passScript.setValue(newScriptCode);
         }
      });
      diagram.setDiagram(document.asXML());
      EntityHelper.assignCreatedElement(diagram);
      this.diagramService.getDao().insert(diagram);
      CoreBpmnDraftBean draft = new CoreBpmnDraftBean();
      BeanUtils.copyProperties(proc, draft);
      draft.setId(ApplicationContextHelper.getNextIdentity());
      draft.setProcVersion(draft.getProcVersion() + 1L);
      draft.setDiagramId(diagram.getId());
      EntityHelper.assignCreatedElement(draft);
      this.getDao().insert(draft);
      if (!procOrgList.isEmpty()) {
         List<CoreBpmnDraftOrgBean> draftOrgList = new ArrayList<>();

         for (CoreBpmnProcOrgBean procOrg : procOrgList) {
            CoreBpmnDraftOrgBean draftOrg = new CoreBpmnDraftOrgBean();
            draftOrg.setId(ApplicationContextHelper.getNextIdentity());
            draftOrg.setDraftId(draft.getId());
            draftOrg.setUsedOrgId(procOrg.getUsedOrgId());
            draftOrgList.add(draftOrg);
         }

         this.draftOrgService.getDao().insert(draftOrgList);
      }

      return draft.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void submit(Long id) {
      CoreBpmnDraftBean draft = this.selectById(id);
      Document document = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(this.diagramService.selectColumnById(draft.getDiagramId(), "DIAGRAM")));
      this.validateScript(document);
      CoreBpmnDraftOrgBean draftOrgFiltre = new CoreBpmnDraftOrgBean();
      draftOrgFiltre.setDraftId(id);
      List<CoreBpmnDraftOrgBean> draftOrgList = this.draftOrgService.selectList(draftOrgFiltre, new Order[]{Order.asc("ID")});
      CoreBpmnProcBean proc = new CoreBpmnProcBean();
      BeanUtils.copyProperties(draft, proc);
      proc.setId(ApplicationContextHelper.getNextIdentity());
      EntityHelper.assignCreatedElement(proc);
      this.procService.getDao().insert(proc);
      if (!draftOrgList.isEmpty()) {
         List<CoreBpmnProcOrgBean> procOrgList = new ArrayList<>();

         for (CoreBpmnDraftOrgBean draftOrg : draftOrgList) {
            CoreBpmnProcOrgBean procOrg = new CoreBpmnProcOrgBean();
            procOrg.setId(ApplicationContextHelper.getNextIdentity());
            procOrg.setProcId(proc.getId());
            procOrg.setUsedOrgId(draftOrg.getUsedOrgId());
            procOrgList.add(procOrg);
         }

         this.procOrgService.getDao().insert(procOrgList);
      }

      this.getDao().delete(draft.getId());
      this.draftOrgService.getDao().deleteBy(draftOrgList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<CoreBpmnDraftBean> deleteList = wrapper.parse(CoreBpmnDraftBean.class);
      if (!deleteList.isEmpty()) {
         List<Long> diagramIdList = deleteList.stream().map(d -> d.getDiagramId()).collect(Collectors.toList());
         diagramIdList.stream().filter(i -> i != null).forEach(i -> this.diagramService.delete(i));
         this.getDao().deleteBy(deleteList, new String[0]);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.IMPORT)
   @Audit("AUDIT.IMPORT")
   @Override
   public void importBpmn(List<CoreBpmnImportDTO> bpmnImportList) {
      List<String> procCodeList = bpmnImportList.stream().map(i -> i.getProcCode()).collect(Collectors.toList());
      List<CoreBpmnDraftBean> selectedDraftList = this.getDao().selectListByOneColumnValues(procCodeList, "PROCCODE", new Order[0]);
      this.getDao().deleteBy(selectedDraftList, new String[0]);
      List<Long> diagramIdList = selectedDraftList.stream().map(d -> d.getDiagramId()).collect(Collectors.toList());
      diagramIdList.stream().filter(i -> i != null).forEach(i -> this.diagramService.delete(i));
      List<CoreBpmnDiagramBean> insertDiagramList = new ArrayList<>();
      List<CoreBpmnDraftBean> insertDraftList = new ArrayList<>();
      List<CoreBpmnDraftOrgBean> insertDraftOrgList = new ArrayList<>();

      for (CoreBpmnImportDTO bpmnImport : bpmnImportList) {
         CoreBpmnDiagramBean diagram = new CoreBpmnDiagramBean();
         diagram.setId(ApplicationContextHelper.getNextIdentity());
         diagram.setDiagram(bpmnImport.getDiagram());
         diagram.setSvg(bpmnImport.getSvg());
         insertDiagramList.add(diagram);
         CoreBpmnDraftBean draft = new CoreBpmnDraftBean();
         draft.setId(ApplicationContextHelper.getNextIdentity());
         draft.setProcCode(bpmnImport.getProcCode());
         draft.setServiceId(bpmnImport.getServiceId());
         draft.setExpression(bpmnImport.getExpression());
         draft.setDiagramId(diagram.getId());
         if (bpmnImport.getUsedOrgIdList() != null) {
            for (String usedOrgId : bpmnImport.getUsedOrgIdList()) {
               CoreBpmnDraftOrgBean draftOrg = new CoreBpmnDraftOrgBean();
               draftOrg.setId(ApplicationContextHelper.getNextIdentity());
               draftOrg.setDraftId(draft.getId());
               draftOrg.setUsedOrgId(usedOrgId);
               insertDraftOrgList.add(draftOrg);
            }
         }

         MapperParameter parameter = new MapperParameter();
         parameter.setFilter(SearchFilter.instance().match("PROCCODE", bpmnImport.getProcCode()).filter(MatchPattern.SEQ));
         parameter.put("service", "modifiable");
         List<CoreBpmnProcBean> procList = this.procService.selectList(parameter);
         if (!procList.isEmpty()) {
            draft.setProcVersion(procList.get(0).getProcVersion() + 1L);
         }

         insertDraftList.add(draft);
      }

      this.diagramService.getDao().insert(insertDiagramList);
      this.getDao().insert(insertDraftList);
      this.draftOrgService.getDao().insert(insertDraftOrgList);
   }

   @Override
   public Page<CoreBpmnDraftOrgBean> selectDraftOrgPagination(Long id, RestJsonWrapperBean wrapper) {
      return this.draftOrgService.selectPaginationByFilter(SearchFilter.instance().match("DRAFTID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertDraftOrg(Long id, RestJsonWrapperBean wrapper) {
      List<CoreBpmnDraftOrgBean> draftOrgList = wrapper.parse(CoreBpmnDraftOrgBean.class);
      draftOrgList.forEach(o -> {
         o.setId(ApplicationContextHelper.getNextIdentity());
         o.setDraftId(id);
      });
      this.draftOrgService.getDao().insert(draftOrgList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteDraftOrg(Long id, RestJsonWrapperBean wrapper) {
      this.draftOrgService.delete(wrapper);
   }

   private void validateScript(Document document) {
      try {
         List<Element> elementList = new ArrayList<>();
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.USER_TASK));
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.START_EVENT));
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.END_EVENT));
         elementList.forEach(element -> {
            String passScriptName = BpmnDiagramHelper.getPassScriptName(element);
            if (!StringUtils.isEmpty(passScriptName)) {
               String script = BpmnDiagramHelper.getPassScriptCode(element);
               JavaCompilerManager.compile(script);
            }

            String rejectScriptName = BpmnDiagramHelper.getRejectScriptName(element);
            if (!StringUtils.isEmpty(rejectScriptName)) {
               String script = BpmnDiagramHelper.getRejectScriptCode(element);
               JavaCompilerManager.compile(script);
            }

            String endScriptName = BpmnDiagramHelper.getEndScriptName(element);
            if (!StringUtils.isEmpty(endScriptName)) {
               String script = BpmnDiagramHelper.getEndScriptCode(element);
               JavaCompilerManager.compile(script);
            }
         });
      } catch (CompileException var3) {
         throw new CheckedException(var3.getMessage(), var3);
      }
   }
}
