package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.CompileException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramSequenceFlowConditionDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDiagramDao;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskRoleService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnDiagramServiceImpl implements CoreBpmnDiagramService {
   @Autowired
   private CoreBpmnDiagramDao diagramDao;
   @Autowired
   @Lazy
   private CoreBpmnDiagramService proxyInstance;
   @Autowired
   private CoreUserService userService;
   @Autowired
   private CoreRoleService roleService;
   @Autowired
   private CoreRoleUserService roleUserService;
   @Autowired
   private CoreBpmnInstanceService instanceService;
   @Autowired
   private CoreBpmnInstanceTaskService instanceTaskService;
   @Autowired
   private CoreBpmnInstanceTaskUserService instanceTaskUserService;
   @Autowired
   private CoreBpmnInstanceTaskRoleService instanceTaskRoleService;

   public CoreBpmnDiagramDao getDao() {
      return this.diagramDao;
   }

   public String selectColumnById(Long id, String column) {
      CoreBpmnDiagramBean diagram = this.proxyInstance.selectById(id);
      Method method = ReflectionUtils.findReadMethod(this.getDao().getType(), column);
      return method != null ? ConvertUtils.convert(ReflectionUtils.invokeMethod(method, diagram), String.class) : diagram.getExt$Item(column);
   }

   @Cacheable(
      value = {"T_CORE_BPMN_DIAGRAM.DOCUMENT"},
      key = "'' + #id",
      unless = "#result == null"
   )
   @Override
   public Document getDocument(Long id) {
      Document diagram = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(this.selectColumnById(id, "DIAGRAM")));
      this.loadScript(diagram);
      return diagram;
   }

   @Override
   public List<CoreBpmnDiagramTaskCandidatorDTO> selectTaskCandidatorList(Long id, RestJsonWrapperBean wrapper) {
      List<CoreBpmnDiagramTaskCandidatorDTO> taskCandidatorList = new ArrayList<>();
      Document document = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(this.selectColumnById(id, "DIAGRAM")));
      if (document == null) {
         return CollectionUtils.emptyList();
      } else {
         String itemId = ServletUtils.getCurrentRequest().getParameter("itemId");
         CoreBpmnInstanceBean instanceBean = this.instanceService
            .selectFirstByFilter(SearchFilter.instance().match("TARGETID", "$" + itemId).filter(MatchPattern.SE), new Order[0]);
         if (instanceBean != null) {
            for (CoreBpmnInstanceTaskBean taskBean : this.instanceTaskService
               .selectListByFilter(SearchFilter.instance().match("INSTID", instanceBean.getId()).filter(MatchPattern.EQ), new Order[0])) {
               Set<String> allUserIdSet = new HashSet<>();
               CoreBpmnDiagramTaskCandidatorDTO taskCandidator = new CoreBpmnDiagramTaskCandidatorDTO();
               taskCandidator.setStatusCode(taskBean.getStatusCode());
               taskCandidator.setTaskName(taskBean.getTaskName());
               List<CoreBpmnInstanceTaskUserBean> taskUserList = this.instanceTaskUserService
                  .selectListByFilter(SearchFilter.instance().match("INSTTASKID", taskBean.getId()).filter(MatchPattern.EQ), new Order[0]);
               if (!CollectionUtils.isEmpty(taskUserList)) {
                  List<String> userIdList = taskUserList.stream().map(CoreBpmnInstanceTaskUserBean::getUserId).collect(Collectors.toList());
                  List<String> userNameList = this.userService.selectListByIds(userIdList).stream().map(CoreUserBean::getUserName).collect(Collectors.toList());
                  taskCandidator.setUserId(StringUtils.join(userIdList, ","));
                  taskCandidator.setUser(StringUtils.join(userNameList, ","));
                  allUserIdSet.addAll(userIdList);
               }

               List<CoreBpmnInstanceTaskRoleBean> taskRoleList = this.instanceTaskRoleService
                  .selectListByFilter(SearchFilter.instance().match("INSTTASKID", taskBean.getId()).filter(MatchPattern.EQ), new Order[0]);
               if (!CollectionUtils.isEmpty(taskRoleList)) {
                  List<Long> roleIdList = taskRoleList.stream().map(CoreBpmnInstanceTaskRoleBean::getRoleId).collect(Collectors.toList());
                  taskCandidator.setRoleId(StringUtils.join(roleIdList, ","));
                  List<String> roleNameList = this.roleService.selectListByIds(roleIdList).stream().map(CoreRoleBean::getRoleName).collect(Collectors.toList());
                  taskCandidator.setRole(StringUtils.join(roleNameList, ","));
                  List<String> roleUserIdList = this.roleUserService
                     .selectListByFilter(SearchFilter.instance().match("ROLEID", roleIdList).filter(MatchPattern.OR), new Order[0])
                     .stream()
                     .map(CoreRoleUserBean::getUserId)
                     .collect(Collectors.toList());
                  allUserIdSet.addAll(roleUserIdList);
               }

               List<String> allUserNameList = this.userService
                  .selectListByIds(new ArrayList<>(allUserIdSet))
                  .stream()
                  .map(CoreUserBean::getUserName)
                  .distinct()
                  .collect(Collectors.toList());
               taskCandidator.setAllUserId(StringUtils.join(allUserIdSet, ","));
               taskCandidator.setAllUser(StringUtils.join(allUserNameList, ","));
               taskCandidatorList.add(taskCandidator);
            }
         } else {
            for (Element userTask : BpmnDiagramHelper.getTaskList(document)) {
               CoreBpmnDiagramTaskCandidatorDTO taskCandidatorx = new CoreBpmnDiagramTaskCandidatorDTO();
               taskCandidatorx.setStatusCode(XmlUtils.getAttributeValue(userTask, "statusCode"));
               taskCandidatorx.setTaskName(XmlUtils.getAttributeValue(userTask, "name"));
               Set<String> allUserIdSetx = new HashSet<>();
               String dynamic = BpmnDiagramHelper.getUserTaskAssigneeDynamic(userTask);
               String selectBySql = BpmnDiagramHelper.getUserTaskAssigneeSelectBySql(userTask);
               if (StringUtils.isBlank(dynamic) && !StringUtils.isBlank(selectBySql)) {
                  taskCandidatorx.setSpecial(
                     "(" + I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.OPTIONS.ASSIGNEE.SELECT_BY_SQL") + ") " + selectBySql
                  );
                  List<String> userIdList = CoreBpmnHelper.getCandidatorList(selectBySql, itemId, null);
                  allUserIdSetx.addAll(userIdList);
               } else {
                  List<String> userIdList = new ArrayList<>();
                  List<Long> roleIdList = new ArrayList<>();
                  Map<String, String> userMap = BpmnDiagramHelper.getUserTaskCandidatorValue(userTask);
                  if (!userMap.isEmpty()) {
                     taskCandidatorx.setUserId(StringUtils.join(userMap.keySet(), ","));
                     List<String> userNameList = this.userService
                        .selectListByIds(new ArrayList<>(userMap.keySet()))
                        .stream()
                        .map(CoreUserBean::getUserName)
                        .collect(Collectors.toList());
                     taskCandidatorx.setUser(StringUtils.join(userNameList, ","));
                     userIdList.addAll(userMap.keySet());
                     allUserIdSetx.addAll(userIdList);
                  }

                  Map<Long, String> roleMap = BpmnDiagramHelper.getUserTaskRoleValue(userTask);
                  if (!roleMap.isEmpty()) {
                     taskCandidatorx.setRoleId(StringUtils.join(roleMap.keySet(), ","));
                     List<String> roleNameList = this.roleService
                        .selectListByIds(new ArrayList<>(roleMap.keySet()))
                        .stream()
                        .map(CoreRoleBean::getRoleName)
                        .collect(Collectors.toList());
                     taskCandidatorx.setRole(StringUtils.join(roleNameList, ","));
                     roleIdList.addAll(roleMap.keySet());
                     List<String> roleUserIdList = this.roleUserService
                        .selectListByFilter(SearchFilter.instance().match("ROLEID", roleIdList).filter(MatchPattern.OR), new Order[0])
                        .stream()
                        .map(CoreRoleUserBean::getUserId)
                        .collect(Collectors.toList());
                     allUserIdSetx.addAll(roleUserIdList);
                  }

                  if (BpmnDiagramHelper.isUserTaskAssigneeInitiator(userTask)) {
                     taskCandidatorx.setSpecial(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.OPTIONS.ASSIGNEE.INITIATOR"));
                  } else if (!StringUtils.isBlank(dynamic)) {
                     taskCandidatorx.setSpecial("(" + I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.OPTIONS.ASSIGNEE.DYNAMIC") + ") " + dynamic);
                  } else {
                     String filterBySql = BpmnDiagramHelper.getUserTaskAssigneeFilterBySql(userTask);
                     if (!StringUtils.isBlank(filterBySql)) {
                        taskCandidatorx.setSpecial(
                           "(" + I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.OPTIONS.ASSIGNEE.FILTER_BY_SQL") + ") " + filterBySql
                        );
                        List<String> filteredUserIdList = CoreBpmnHelper.getCandidatorList(filterBySql, itemId, userIdList, roleIdList, null);
                        allUserIdSetx.addAll(filteredUserIdList);
                     }
                  }
               }

               List<String> allUserNameList = this.userService
                  .selectListByIds(new ArrayList<>(allUserIdSetx))
                  .stream()
                  .map(CoreUserBean::getUserName)
                  .distinct()
                  .collect(Collectors.toList());
               taskCandidatorx.setAllUserId(StringUtils.join(allUserIdSetx, ","));
               taskCandidatorx.setAllUser(StringUtils.join(allUserNameList, ","));
               taskCandidatorList.add(taskCandidatorx);
            }
         }

         return taskCandidatorList;
      }
   }

   @Cacheable(
      value = {"T_CORE_BPMN_DIAGRAM.SEQUENCEFLOWCONDITION"},
      key = "'' + #id"
   )
   @Override
   public List<CoreBpmnDiagramSequenceFlowConditionDTO> selectSequenceFlowConditionList(Long id) {
      List<CoreBpmnDiagramSequenceFlowConditionDTO> conditionList = new ArrayList<>();
      Document document = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(this.selectColumnById(id, "DIAGRAM")));
      if (document == null) {
         return CollectionUtils.emptyList();
      } else {
         for (Element sequenceFlow : BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.SEQUENCE_FLOW)) {
            String conditionExpression = BpmnDiagramHelper.getConditionExpression(sequenceFlow);
            if (!StringUtils.isEmpty(conditionExpression)) {
               CoreBpmnDiagramSequenceFlowConditionDTO condition = new CoreBpmnDiagramSequenceFlowConditionDTO();
               condition.setConditionExpression(conditionExpression);
               Element sourceElement = BpmnDiagramHelper.getSourceElement(document, sequenceFlow);
               condition.setSourceTask(this.getElementDesc(sourceElement));
               Element targetElement = BpmnDiagramHelper.getTargetElement(document, sequenceFlow);
               condition.setTargetTask(this.getElementDesc(targetElement));
               conditionList.add(condition);
            }
         }

         return conditionList;
      }
   }

   private String getElementDesc(Element element) {
      String name = XmlUtils.getAttributeValue(element, "name");
      StringBuilder sb = new StringBuilder();
      if (!StringUtils.isEmpty(name)) {
         sb.append(name).append("(");
      }

      if (ProcessElementType.START_EVENT.getName().equals(element.getName())) {
         sb.append(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.START_NODE"));
      } else if (ProcessElementType.EXCLUSIVE_GATEWAY.getName().equals(element.getName())) {
         sb.append(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.EXCLUSIVE_GATEWAY"));
      } else if (ProcessElementType.PARALLEL_GATEWAY.getName().equals(element.getName())) {
         sb.append(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.PARALLEL_GATEWAY"));
      } else if (ProcessElementType.END_EVENT.getName().equals(element.getName())) {
         sb.append(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.END_NODE"));
      } else {
         if (!ProcessElementType.USER_TASK.getName().equals(element.getName())) {
            throw new UnsupportedException(element.asXML());
         }

         sb.append(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_DIAGRAM.USER_TASK"));
      }

      if (!StringUtils.isEmpty(name)) {
         sb.append(")");
      }

      return sb.toString();
   }

   private void loadScript(Document document) {
      if (!ObjectUtils.isEmpty(document)) {
         List<Element> elementList = new ArrayList<>();
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.USER_TASK));
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.START_EVENT));
         elementList.addAll(BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.END_EVENT));
         elementList.forEach(element -> {
            String passScriptName = BpmnDiagramHelper.getPassScriptName(element);
            if (!StringUtils.isEmpty(passScriptName)) {
               String script = BpmnDiagramHelper.getPassScriptCode(element);
               this.registerScript(passScriptName, script);
            }

            String rejectScriptName = BpmnDiagramHelper.getRejectScriptName(element);
            if (!StringUtils.isEmpty(rejectScriptName)) {
               String script = BpmnDiagramHelper.getRejectScriptCode(element);
               this.registerScript(rejectScriptName, script);
            }

            String endScriptName = BpmnDiagramHelper.getEndScriptName(element);
            if (!StringUtils.isEmpty(endScriptName)) {
               String script = BpmnDiagramHelper.getEndScriptCode(element);
               this.registerScript(endScriptName, script);
            }
         });
      }
   }

   private void registerScript(String scriptName, String script) {
      try {
         Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
         SpringUtils.registerBean(scriptName.toLowerCase(), clazz);
      } catch (CompileException var4) {
         throw new CheckedException(var4.getMessage(), var4);
      }
   }
}
