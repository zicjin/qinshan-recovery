package com.sunwayworld.module.sys.bpmn.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnImportDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnProcDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcOrgService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreBpmnProcServiceImpl implements CoreBpmnProcService {
   @Autowired
   private CoreBpmnProcDao procDao;
   @Autowired
   private CoreBpmnDiagramService diagramService;
   @Lazy
   @Autowired
   private CoreBpmnProcOrgService procOrgService;
   @Lazy
   @Autowired
   private CoreBpmnProcService proxyInstance;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;

   public CoreBpmnProcDao getDao() {
      return this.procDao;
   }

   @Cacheable(
      value = {"T_CORE_BPMN_PROC_LATEST.BY_PROCCODE"},
      key = "#procCode",
      unless = "#result == null"
   )
   @Override
   public CoreBpmnProcBean selectLatestProc(String procCode) {
      CoreBpmnProcBean filter = new CoreBpmnProcBean();
      filter.setProcCode(procCode);
      return this.getDao().selectFirst(filter, new Order[]{Order.desc("PROCVERSION")});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<Long> procIdList = wrapper.parseId(Long.class);
      List<CoreBpmnProcBean> procList = this.getDao().selectListByIds(procIdList, new Order[0]);
      CoreBpmnProcService.super.delete(wrapper);
      this.diagramService.getDao().deleteByIdList(procList.stream().map(CoreBpmnProcBean::getDiagramId).collect(Collectors.toList()));
   }

   @Override
   public String getBpmnInputTextUrl(Long id) {
      CoreBpmnProcBean proc = this.getDao().selectById(id);
      CoreBpmnDiagramBean diagram = this.diagramService.selectById(proc.getDiagramId());
      CoreBpmnImportDTO bpmnImport = new CoreBpmnImportDTO();
      bpmnImport.setServiceId(proc.getServiceId());
      bpmnImport.setProcCode(proc.getProcCode());
      bpmnImport.setExpression(proc.getExpression());
      bpmnImport.setExt$(proc.getExt$());
      bpmnImport.setDiagram(diagram.getDiagram());
      bpmnImport.setSvg(diagram.getSvg());
      CoreBpmnProcOrgBean procOrgFilter = new CoreBpmnProcOrgBean();
      procOrgFilter.setProcId(id);
      List<CoreBpmnProcOrgBean> procOrgList = this.procOrgService.selectList(procOrgFilter, new Order[]{Order.asc("ID")});
      bpmnImport.setUsedOrgIdList(procOrgList.stream().map(o -> o.getUsedOrgId()).collect(Collectors.toList()));
      FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.temp, "bpmn.json");
      FileUtils.write(FilePathManager.getLocalPath(filePath), JSON.toJSONString(bpmnImport));
      this.fileManager.upload(filePath, FilePathManager.getLocalPath(filePath));
      return this.fileManager.getDownloadUrl(filePath);
   }

   @Override
   public String getBpmnInputTextUrl(RestJsonWrapperBean wrapper) {
      List<CoreBpmnProcBean> procList = wrapper.parse(CoreBpmnProcBean.class);
      List<Long> procIdList = procList.stream().map(e -> e.getId()).collect(Collectors.toList());
      List<Long> diagramIdList = procList.stream().map(e -> e.getDiagramId()).collect(Collectors.toList());
      List<CoreBpmnDiagramBean> diagramList = this.diagramService.selectListByIds(diagramIdList);
      List<CoreBpmnProcOrgBean> procOrgList = this.procOrgService
         .selectListByFilter(SearchFilter.instance().match("PROCID", procIdList).filter(MatchPattern.OR), new Order[]{Order.asc("ID")});
      List<CoreBpmnImportDTO> bpmnImportList = new ArrayList<>();
      procList.stream()
         .forEach(
            proc -> {
               CoreBpmnImportDTO bpmnImport = new CoreBpmnImportDTO();
               bpmnImport.setServiceId(proc.getServiceId());
               bpmnImport.setProcCode(proc.getProcCode());
               bpmnImport.setExpression(proc.getExpression());
               bpmnImport.setExt$(proc.getExt$());
               CoreBpmnDiagramBean diagram = diagramList.stream().filter(d -> d.getId().equals(proc.getDiagramId())).findFirst().orElse(null);
               bpmnImport.setDiagram(diagram.getDiagram());
               bpmnImport.setSvg(diagram.getSvg());
               List<String> filterUsedOrgIdList = procOrgList.stream()
                  .filter(po -> proc.getId().equals(po.getProcId()) && !StringUtils.isEmpty(po.getUsedOrgId()))
                  .map(po -> po.getUsedOrgId())
                  .collect(Collectors.toList());
               bpmnImport.setUsedOrgIdList(filterUsedOrgIdList);
               bpmnImportList.add(bpmnImport);
            }
         );
      FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.temp, "bpmn.json");
      FileUtils.write(FilePathManager.getLocalPath(filePath), JSON.toJSONString(bpmnImportList));
      this.fileManager.upload(filePath, FilePathManager.getLocalPath(filePath));
      return this.fileManager.getDownloadUrl(filePath);
   }

   @Override
   public Page<CoreBpmnProcOrgBean> selectProcOrgPagination(Long id, RestJsonWrapperBean wrapper) {
      return this.procOrgService.selectPaginationByFilter(SearchFilter.instance().match("PROCID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public List<CoreBpmnProcBean> selectRuntimeBpmnProcList(String tableName, String usedOrgId) {
      return this.getDao().selectRuntimeBpmnProcList(tableName, usedOrgId);
   }

   @Override
   public List<CoreBpmnProcBean> selectBpmnProcListByServiceId(String serviceId) {
      return this.getDao().selectBpmnProcListByServiceId(serviceId);
   }
}
