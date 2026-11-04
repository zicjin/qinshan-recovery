package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.MapUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRejectableTaskDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnRuntimeDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.CommentStatus;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.diagram.TaskStatus;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnExtService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnExtServiceImpl implements CoreBpmnExtService {
   @Autowired
   private CoreBpmnInstanceService instanceService;
   @Autowired
   private CoreBpmnProcService procService;
   @Autowired
   private CoreBpmnCommentService commentService;
   @Autowired
   private CoreBpmnDiagramService diagramService;
   @Autowired
   private CoreBpmnInstanceTaskService instanceTaskService;
   @Autowired
   private CoreBpmnTargetService targetService;

   @Override
   public <T extends Persistable<ID>, ID extends Serializable> CoreBpmnRuntimeDTO selectBpmnRuntimeInfo(String table, String id) {
      CoreBpmnRuntimeDTO runtime = new CoreBpmnRuntimeDTO();
      CoreBpmnInstanceBean filter = new CoreBpmnInstanceBean();
      filter.setTargetId(CoreBpmnHelper.getTargetId(table, id));
      CoreBpmnInstanceBean instance = this.instanceService.getDao().selectOneIfPresent(filter);
      if (instance != null) {
         runtime.setInstanceId(instance.getId());
         runtime.setProcId(instance.getProcId());
         runtime.setDiagramId(instance.getDiagramId());
      } else {
         GenericService<T, ID> service = ApplicationContextHelper.getServiceByTable(table);
         String processStatus = null;
         Long procId = null;
         if (service != null) {
            Class<ID> idType = (Class<ID>)service.getDao().getEntityContext().getIdContext().getType();
            ID actualId = (ID)ConvertUtils.convert(id, idType);
            processStatus = service.selectColumnById(actualId, "PROCESSSTATUS");
            if (StringUtils.startsWithIgnoreCase(processStatus, ProcessStatus.DRAFT.name())) {
               procId = service.selectProcId(actualId);
            }
         } else {
            GenericCloudResource<T, ID> feignClient = ApplicationContextHelper.getFeignClientByTable(table);
            if (feignClient != null) {
               Class<ID> idType = (Class<ID>)ResolvableType.forClass(feignClient.getClass()).as(GenericCloudResource.class).getGeneric(1).resolve();
               ID actualId = (ID)ConvertUtils.convert(id, idType);
               processStatus = feignClient.selectColumnById(actualId, "PROCESSSTATUS");
               if (StringUtils.startsWithIgnoreCase(processStatus, ProcessStatus.DRAFT.name())) {
                  procId = feignClient.selectProcId(actualId);
               }
            } else {
               Map<String, Object> itemMap = ApplicationContextHelper.getBean(MapDao.class).selectById(TableContext.of(table), id);
               processStatus = MapUtils.getString(itemMap, "PROCESSSTATUS");
               if (StringUtils.startsWithIgnoreCase(processStatus, ProcessStatus.DRAFT.name())) {
                  procId = this.selectProcId(itemMap, table);
               }
            }
         }

         if (procId == null) {
            CoreBpmnCommentBean comment = this.commentService
               .selectFirstByFilter(
                  SearchFilter.instance()
                     .match("TARGETID", CoreBpmnHelper.getTargetId(table, id))
                     .filter(MatchPattern.EQ)
                     .match("PROCID", null)
                     .filter(MatchPattern.DIFFER),
                  new Order[]{Order.desc("ID")}
               );
            if (comment != null) {
               procId = comment.getProcId();
            }
         }

         runtime.setProcessStatus(processStatus);
         if (procId != null) {
            CoreBpmnProcBean proc = this.procService.selectById(procId);
            runtime.setProcId(proc.getId());
            runtime.setDiagramId(proc.getDiagramId());
         }
      }

      return runtime;
   }

   @Override
   public List<CoreBpmnRejectableTaskDTO> selectBpmnRuntimeRejectableTaskList(RestJsonWrapperBean wrapper) {
      List<String> idList = wrapper.parseId(String.class);
      String table = Objects.requireNonNull(wrapper.getParamValue("bpmn_table"));
      String currentStatusCode = wrapper.getParamValue("bpmn_currentStatusCode");
      List<CoreBpmnRejectableTaskDTO> returnRejectableTaskList = new ArrayList<>();
      List<CoreBpmnCommentBean> commentFilterList = new ArrayList<>();

      for (String id : idList) {
         CoreBpmnCommentBean commentFilter = new CoreBpmnCommentBean();
         commentFilter.setTargetId(CoreBpmnHelper.getTargetId(table, id));
         commentFilterList.add(commentFilter);
      }

      List<CoreBpmnCommentBean> selectedCommentList = this.commentService
         .getDao()
         .selectList(commentFilterList, Arrays.asList("TARGETID"), CollectionUtils.emptyList(), Order.desc("ID"));
      if (!selectedCommentList.isEmpty()) {
         for (CoreBpmnCommentBean lastComment = selectedCommentList.get(0);
            CommentStatus.CS_APPROVE.name().equalsIgnoreCase(lastComment.getStatus());
            lastComment = selectedCommentList.get(0)
         ) {
            selectedCommentList.remove(0);
            if (selectedCommentList.isEmpty()) {
               break;
            }
         }
      }

      List<CoreBpmnInstanceBean> instanceFilterList = new ArrayList<>();

      for (String id : idList) {
         CoreBpmnInstanceBean instanceFilter = new CoreBpmnInstanceBean();
         instanceFilter.setTargetId(CoreBpmnHelper.getTargetId(table, id));
         instanceFilterList.add(instanceFilter);
      }

      List<CoreBpmnInstanceBean> selectedInstanceList = this.instanceService
         .getDao()
         .selectList(instanceFilterList, Arrays.asList("TARGETID"), CollectionUtils.emptyList());
      List<Long> instIdList = selectedInstanceList.stream().map(i -> i.getId()).collect(Collectors.toList());
      List<CoreBpmnInstanceTaskBean> selectedInstanceTaskList = this.instanceTaskService
         .selectAuditableInstanceTaskList(LocalContextHelper.getLoginUserId(), instIdList);

      for (String id : idList) {
         CoreBpmnInstanceBean instance = selectedInstanceList.stream()
            .filter(i -> i.getTargetId().contentEquals(CoreBpmnHelper.getTargetId(table, id)))
            .findAny()
            .orElse(null);
         if (instance == null) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_STATUS_UPDATED");
         }

         List<CoreBpmnInstanceTaskBean> taskList = selectedInstanceTaskList.stream()
            .filter(t -> instance.getId().equals(t.getInstId()))
            .collect(Collectors.toList());
         if (taskList.isEmpty()) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.NOT_AUTHORIZED");
         }

         if (taskList.size() > 1 && !StringUtils.isEmpty(currentStatusCode)) {
            taskList.removeIf(t -> !t.getStatusCode().equals(currentStatusCode));
         }

         Document document = this.diagramService.getDocument(instance.getDiagramId());
         Set<String> previousStatusCodeList = new HashSet<>();

         for (CoreBpmnInstanceTaskBean task : taskList) {
            previousStatusCodeList.addAll(
               BpmnDiagramHelper.getPreviousStatusCodeList(document, BpmnDiagramHelper.getProcessElementByStatusCode(document, task.getStatusCode()))
            );
         }

         if (previousStatusCodeList.isEmpty()) {
            CoreBpmnRejectableTaskDTO startEvent = new CoreBpmnRejectableTaskDTO();
            startEvent.setStatusCode(CommentStatus.SUBMIT.name());
            startEvent.setTaskName(BpmnDiagramHelper.getStartNodeName(document));
            return Arrays.asList(startEvent);
         }

         List<CoreBpmnRejectableTaskDTO> rejectableTaskList = new ArrayList<>();

         for (CoreBpmnCommentBean comment : selectedCommentList) {
            if (comment.getTargetId().equals(CoreBpmnHelper.getTargetId(table, id))) {
               if (previousStatusCodeList.contains(comment.getStatusCode())) {
                  CoreBpmnRejectableTaskDTO rejectableTask = new CoreBpmnRejectableTaskDTO();
                  rejectableTask.setStatusCode(comment.getStatusCode());
                  rejectableTask.setTaskName(comment.getTaskName());
                  if (StringUtils.startsWithIgnoreCase(rejectableTask.getStatusCode(), ProcessStatus.DRAFT.name())
                     && StringUtils.isEmpty(rejectableTask.getTaskName())) {
                     rejectableTask.setTaskName(BpmnDiagramHelper.getStartNodeName(document));
                  }

                  if (!rejectableTaskList.contains(rejectableTask)) {
                     rejectableTaskList.add(rejectableTask);
                  }
               }

               if (CommentStatus.SUBMIT.name().equalsIgnoreCase(comment.getStatus())) {
                  break;
               }
            }
         }

         if (returnRejectableTaskList.isEmpty()) {
            returnRejectableTaskList.addAll(rejectableTaskList);
         } else {
            returnRejectableTaskList.removeIf(r -> !rejectableTaskList.contains(r));
         }
      }

      return returnRejectableTaskList;
   }

   @Override
   public String selectBpmnDiagramSvg(Long id, @Nullable String dataTable, @Nullable String dataId) {
      CoreBpmnDiagramBean diagram = this.diagramService.selectById(id);
      String svg = diagram.getSvg();
      if (!StringUtils.isBlank(dataTable) && !StringUtils.isBlank(dataId)) {
         List<String> activatedElementIdList = new ArrayList<>();
         String targetId = CoreBpmnHelper.getTargetId(dataTable, dataId);
         CoreBpmnCommentBean commentFilter = new CoreBpmnCommentBean();
         commentFilter.setTargetId(targetId);
         List<CoreBpmnCommentBean> commentList = this.commentService.selectList(commentFilter, new Order[]{Order.desc("ID")});
         Map<String, CommentStatus> map = new HashMap<>();

         for (CoreBpmnCommentBean comment : commentList) {
            if (!map.containsKey(comment.getTaskId())) {
               map.put(comment.getTaskId(), CommentStatus.valueOf(comment.getStatus()));
            }

            if (CommentStatus.SUBMIT.name().equals(comment.getStatus())) {
               break;
            }
         }

         activatedElementIdList.addAll(map.keySet());
         Document document = this.diagramService.getDocument(id);
         CoreBpmnInstanceBean instance = this.instanceService
            .selectFirstByFilter(SearchFilter.instance().match("TARGETID", targetId).filter(MatchPattern.EQ), new Order[0]);
         boolean rejectedToStart = false;
         if (instance != null) {
            CoreBpmnInstanceTaskBean taskFilter = new CoreBpmnInstanceTaskBean();
            taskFilter.setInstId(instance.getId());
            taskFilter.setStatus(TaskStatus.CREATED.name());

            for (CoreBpmnInstanceTaskBean task : this.instanceTaskService.selectList(taskFilter, new Order[0])) {
               map.put(task.getTaskId(), null);
            }
         } else if (!commentList.isEmpty()) {
            CoreBpmnCommentBean lastComment = commentList.get(0);
            if (CommentStatus.APPROVED.name().equals(lastComment.getStatus())) {
               Element endElement = BpmnDiagramHelper.getProcessElement(document, ProcessElementType.END_EVENT);
               String endId = XmlUtils.getAttributeValue(endElement, "id");
               activatedElementIdList.add(endId);
               map.put(endId, CommentStatus.END);
            } else if (CommentStatus.REJECT.name().equals(lastComment.getStatus())) {
               activatedElementIdList.clear();
               map.clear();
               map.put(lastComment.getTaskId(), CommentStatus.valueOf(lastComment.getStatus()));
               rejectedToStart = true;
            }
         }

         if (rejectedToStart) {
            svg = this.fillColor(svg, map, Arrays.asList(BpmnDiagramHelper.getProcessElement(document, commentList.get(0).getTaskId())));
         } else {
            Map<String, Object> actualDataMap = this.getDataMap(dataTable, dataId);
            List<Element> activatedElementList = BpmnDiagramHelper.getActivatedElementList(document, activatedElementIdList, actualDataMap);
            svg = this.fillColor(svg, map, activatedElementList);
         }
      }

      return svg;
   }

   private String fillColor(String svg, Map<String, CommentStatus> map, List<Element> activatedElementList) {
      int dataElementIdPos = 0;
      List<String> activatedSequenceOrGatwayIdList = activatedElementList.stream()
         .filter(
            e -> {
               ProcessElementType elementType = BpmnDiagramHelper.getProcessElementType(e);
               return ProcessElementType.SEQUENCE_FLOW.equals(elementType)
                  || ProcessElementType.PARALLEL_GATEWAY.equals(elementType)
                  || ProcessElementType.EXCLUSIVE_GATEWAY.equals(elementType);
            }
         )
         .map(e -> XmlUtils.getAttributeValue(e, "id"))
         .collect(Collectors.toList());
      svg = this.prepandSequenceflowCompletedArrow(svg);

      while (true) {
         dataElementIdPos = svg.indexOf("data-element-id=\"", dataElementIdPos + 1);
         if (dataElementIdPos <= 0) {
            return svg;
         }

         int dataElementIdStartPos = svg.indexOf("\"", dataElementIdPos + 1);
         int dataElementIdEndPos = svg.indexOf("\"", dataElementIdStartPos + 1);
         String dataElementId = svg.substring(dataElementIdStartPos + 1, dataElementIdEndPos);
         if (StringUtils.startsWithIgnoreCase(dataElementId, "SequenceFlow")) {
            if (activatedSequenceOrGatwayIdList.contains(dataElementId)) {
               svg = this.updateSequenceFlowColor(svg, dataElementId);
            }
         } else if (!StringUtils.startsWithIgnoreCase(dataElementId, "ExclusiveGateway") && !StringUtils.startsWithIgnoreCase(dataElementId, "ParallelGateway")
            )
          {
            CommentStatus commentStatus = map.get(dataElementId);
            if (commentStatus == null && StringUtils.endsWith(dataElementId, "_label")) {
               commentStatus = map.get(StringUtils.removeEnd(dataElementId, "_label"));
            }

            svg = this.updateBackgroundColor(svg, dataElementId, commentStatus, map.containsKey(dataElementId));
            svg = this.updateStrokeColor(svg, dataElementId, commentStatus, map.containsKey(dataElementId));
            svg = this.updateTextColor(svg, dataElementId, commentStatus, map.containsKey(dataElementId));
         } else if (activatedSequenceOrGatwayIdList.contains(dataElementId)) {
            svg = this.updateGatewayColor(svg, dataElementId);
         }
      }
   }

   private String prepandSequenceflowCompletedArrow(String svg) {
      int pos = svg.indexOf("<defs>");
      return pos <= 0
         ? svg
         : svg.substring(0, pos)
            + "<defs><marker id=\"sequenceflow-completed-arrow\" viewBox=\"0 0 20 20\" refX=\"11\" refY=\"10\" markerWidth=\"10\" markerHeight=\"10\" orient=\"auto\"><path d=\"M 1 5 L 11 10 L 1 15 Z\" style=\"fill: #a1ebc2; stroke-width: 0px; stroke-linecap: round; stroke-dasharray: 10000, 1; stroke: white;\"/></marker></defs>"
            + svg.substring(pos);
   }

   private String updateSequenceFlowColor(String svg, String dataElementId) {
      int elementPos = svg.indexOf("data-element-id=\"" + dataElementId + "\"");
      if (elementPos <= 0) {
         return svg;
      } else {
         int strokePos = svg.indexOf("stroke:", elementPos);
         if (strokePos <= 0) {
            return svg;
         } else {
            int semicolonPos = svg.indexOf(";", strokePos);
            if (semicolonPos <= 0) {
               return svg;
            } else {
               svg = svg.substring(0, strokePos) + "stroke:#a1ebc2" + svg.substring(semicolonPos);
               int urlStartPos = svg.indexOf("'#", semicolonPos);
               if (urlStartPos <= 0) {
                  return svg;
               } else {
                  int urlEndPost = svg.indexOf("'", urlStartPos + 1);
                  return svg.substring(0, urlStartPos + 1) + "#sequenceflow-completed-arrow" + svg.substring(urlEndPost);
               }
            }
         }
      }
   }

   private String updateGatewayColor(String svg, String dataElementId) {
      int elementPos = svg.indexOf("data-element-id=\"" + dataElementId + "\"");
      if (elementPos <= 0) {
         return svg;
      } else {
         int firstStrokePos = svg.indexOf("stroke:", elementPos);
         if (firstStrokePos <= 0) {
            return svg;
         } else {
            int firstSemicolonPos = svg.indexOf(";", firstStrokePos);
            if (firstStrokePos <= 0) {
               return svg;
            } else {
               svg = svg.substring(0, firstStrokePos) + "stroke:#a1ebc2" + svg.substring(firstSemicolonPos);
               int fillPos = svg.indexOf("\"fill:", firstSemicolonPos);
               if (fillPos <= 0) {
                  return svg;
               } else {
                  int fillSemicolonPos = svg.indexOf(";", fillPos);
                  if (fillSemicolonPos <= 0) {
                     return svg;
                  } else {
                     svg = svg.substring(0, fillPos) + "\"fill:#a1ebc2" + svg.substring(fillSemicolonPos);
                     int secondStrokePos = svg.indexOf("stroke:", fillSemicolonPos);
                     if (secondStrokePos <= 0) {
                        return svg;
                     } else {
                        int secondSemicolonPos = svg.indexOf(";", secondStrokePos);
                        return secondSemicolonPos <= 0 ? svg : svg.substring(0, secondStrokePos) + "stroke:#a1ebc2" + svg.substring(secondSemicolonPos);
                     }
                  }
               }
            }
         }
      }
   }

   private String updateBackgroundColor(String svg, String dataElementId, CommentStatus status, boolean todo) {
      int elementPos = svg.indexOf("data-element-id=\"" + dataElementId + "\"");
      if (elementPos <= 0) {
         return svg;
      } else {
         int fillPos = svg.indexOf("fill:", elementPos);
         if (fillPos <= 0) {
            return svg;
         } else {
            int semicolonPos = svg.indexOf(";", fillPos);
            if (semicolonPos <= 0) {
               return svg;
            } else {
               if (!CommentStatus.SUBMIT.equals(status)) {
                  if (CommentStatus.APPROVE.equals(status)
                     || CommentStatus.APPROVED.equals(status)
                     || CommentStatus.CS_END.equals(status)
                     || CommentStatus.END.equals(status)) {
                     svg = svg.substring(0, fillPos) + "fill:#e7faef" + svg.substring(semicolonPos);
                  } else if (CommentStatus.REJECT.equals(status)) {
                     svg = svg.substring(0, fillPos) + "fill:#ffecec" + svg.substring(semicolonPos);
                  } else if (status == null) {
                     if (todo) {
                        svg = svg.substring(0, fillPos) + "fill:#e7f4ff" + svg.substring(semicolonPos);
                     } else {
                        svg = svg.substring(0, fillPos) + "fill:#f4f4f5" + svg.substring(semicolonPos);
                     }
                  }
               }

               return svg;
            }
         }
      }
   }

   private String updateStrokeColor(String svg, String dataElementId, CommentStatus status, boolean todo) {
      int elementPos = svg.indexOf("data-element-id=\"" + dataElementId + "\"");
      if (elementPos <= 0) {
         return svg;
      } else {
         int strokePos = svg.indexOf("stroke:", elementPos);
         if (strokePos <= 0) {
            return svg;
         } else {
            int semicolonPos = svg.indexOf(";", strokePos);
            if (semicolonPos <= 0) {
               return svg;
            } else {
               if (CommentStatus.SUBMIT.equals(status)
                  || CommentStatus.APPROVE.equals(status)
                  || CommentStatus.APPROVED.equals(status)
                  || CommentStatus.CS_END.equals(status)
                  || CommentStatus.END.equals(status)) {
                  svg = svg.substring(0, strokePos) + "stroke:#a1ebc2" + svg.substring(semicolonPos);
               } else if (CommentStatus.REJECT.equals(status)) {
                  svg = svg.substring(0, strokePos) + "stroke:#ffb6b6" + svg.substring(semicolonPos);
               } else if (status == null) {
                  if (todo) {
                     svg = svg.substring(0, strokePos) + "stroke:#a3d3ff" + svg.substring(semicolonPos);
                  } else {
                     svg = svg.substring(0, strokePos) + "stroke:#d3d4d6" + svg.substring(semicolonPos);
                  }
               }

               return svg;
            }
         }
      }
   }

   private String updateTextColor(String svg, String dataElementId, CommentStatus status, boolean todo) {
      int elementPos = svg.indexOf("data-element-id=\"" + dataElementId + "\"");
      if (elementPos <= 0) {
         return svg;
      } else {
         int textPos = svg.indexOf("<text ", elementPos);
         if (textPos <= 0) {
            return svg;
         } else {
            int fillPos = svg.indexOf("fill:", textPos);
            if (fillPos <= 0) {
               return svg;
            } else {
               int semicolonPos = svg.indexOf(";", fillPos);
               if (CommentStatus.SUBMIT.equals(status)
                  || CommentStatus.APPROVE.equals(status)
                  || CommentStatus.APPROVED.equals(status)
                  || CommentStatus.CS_END.equals(status)
                  || CommentStatus.END.equals(status)) {
                  svg = svg.substring(0, fillPos) + "fill:#13ce66" + svg.substring(semicolonPos);
               } else if (CommentStatus.REJECT.equals(status)) {
                  svg = svg.substring(0, fillPos) + "fill:#ff4949" + svg.substring(semicolonPos);
               } else if (status == null) {
                  if (todo) {
                     svg = svg.substring(0, fillPos) + "fill:#1890ff" + svg.substring(semicolonPos);
                  } else {
                     svg = svg.substring(0, fillPos) + "fill:#000; fill-opacity: 0.65" + svg.substring(semicolonPos);
                  }
               }

               return svg;
            }
         }
      }
   }

   @Override
   public Page<CoreBpmnTargetBean> selectBpmnRuntimeTargetList(RestJsonWrapperBean wrapper) {
      return this.targetService.selectPaginationByFilter(SearchFilter.instance().match("PROCESSSTATUS", "APPROVE").filter(MatchPattern.SB), wrapper);
   }

   private Long selectProcId(Map<String, Object> map, String tableName) {
      List<CoreBpmnProcBean> procList = this.procService.selectRuntimeBpmnProcList(tableName, LocalContextHelper.getLoginOrgId());
      if (procList.isEmpty()) {
         throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
      } else {
         List<CoreBpmnProcBean> matchProcList = new ArrayList<>();

         for (CoreBpmnProcBean proc : procList) {
            if (StringUtils.isBlank(proc.getExpression())) {
               matchProcList.add(proc);
            } else if (CoreBpmnHelper.evalExpression(proc.getExpression(), map)) {
               if (!matchProcList.isEmpty() && !StringUtils.isBlank(matchProcList.get(0).getExpression())) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
               }

               matchProcList.add(0, proc);
            }
         }

         if (matchProcList.isEmpty()) {
            throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
         } else if (matchProcList.size() == 1) {
            return matchProcList.get(0).getId();
         } else if (!StringUtils.isBlank(matchProcList.get(0).getExpression())) {
            return matchProcList.get(0).getId();
         } else {
            List<CoreBpmnProcBean> orgQtyMatchProcList = matchProcList.stream()
               .filter(procx -> !StringUtils.startsWith(procx.getExt$Item("orgqty"), "0"))
               .collect(Collectors.toList());
            if (orgQtyMatchProcList.size() == 1) {
               return orgQtyMatchProcList.get(0).getId();
            } else {
               throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
            }
         }
      }
   }

   private <T extends Auditable<ID>, ID extends Serializable> Map<String, Object> getDataMap(String dataTable, String dataId) {
      GenericAuditableService<T, ID> dataService = ApplicationContextHelper.getServiceByTable(dataTable);
      if (dataService != null) {
         Class<ID> idType = (Class<ID>)dataService.getDao().getEntityContext().getIdContext().getType();
         ID actualId = (ID)ConvertUtils.convert(dataId, idType);
         List<T> bpmnVarsItemList = dataService.getBpmnVarsItemList((List<ID>)Arrays.asList(actualId));
         Map<ID, Map<String, Object>> bpmnVars = dataService.getBpmnVars(null, bpmnVarsItemList);
         return bpmnVars.get(actualId);
      } else {
         GenericCloudResource<T, ID> feignClient = ApplicationContextHelper.getFeignClientByTable(dataTable);
         if (feignClient != null) {
            Class<ID> idType = (Class<ID>)ResolvableType.forClass(feignClient.getClass()).as(GenericCloudResource.class).getGeneric(1).resolve();
            ID actualId = (ID)ConvertUtils.convert(dataId, idType);
            T actualData = (T)feignClient.selectDetail(actualId);
            return BeanUtils.toMap(actualData);
         } else {
            String beanName = RedisHelper.get("TABLE_SCRIPT_MAPPING_CACHE", dataTable);
            if (!StringUtils.isEmpty(beanName)) {
               Object springBean = SpringUtils.getBean(beanName);
               if (springBean != null) {
                  List<Object> bpmnVarsItemList = (List<Object>)ReflectionUtils.invokeMethod(
                     ReflectionUtils.getUniqueMethodByName(springBean.getClass(), "getBpmnVarsItemList"), springBean, Arrays.asList(dataId)
                  );
                  if (bpmnVarsItemList != null && !bpmnVarsItemList.isEmpty()) {
                     return MapUtils.toMap(bpmnVarsItemList.get(0));
                  }
               }
            }

            return ApplicationContextHelper.getBean(MapDao.class).selectById(TableContext.of(dataTable), dataId);
         }
      }
   }
}
