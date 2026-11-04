package com.sunwayworld.cloud.module.lcdp.sys.bpmn.engine.impl;

import com.sunwayworld.cloud.module.lcdp.base.LcdpAuditableDTO;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.bpmn.engine.impl.CoreBpmnRuntimeServiceImpl;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class LcdpBpmnRuntimeServiceImpl extends CoreBpmnRuntimeServiceImpl {
   @Autowired
   private CoreBpmnProcService procService;
   @Autowired
   private CoreBpmnInstanceService instanceService;

   @Override
   public <T extends Auditable<ID>, ID extends Serializable> Map<ID, CoreBpmnProcBean> selectRuntimeBpmnProcList(List<T> itemList) {
      if (itemList.isEmpty()) {
         return CollectionUtils.emptyMap();
      } else {
         List<T> rawItemList = new ArrayList<>();
         List<T> approveItemList = new ArrayList<>();

         for (T item : itemList) {
            if (StringUtils.startsWithIgnoreCase(item.getProcessStatus(), ProcessStatus.DRAFT.name())) {
               rawItemList.add(item);
            } else if (StringUtils.startsWithIgnoreCase(item.getProcessStatus(), ProcessStatus.APPROVE.name())) {
               approveItemList.add(item);
            }
         }

         Map<ID, CoreBpmnProcBean> runtimeMap = new HashMap<>();
         if (!rawItemList.isEmpty()) {
            String tableName = null;
            if (LcdpAuditableDTO.class.equals(itemList.get(0).getClass())) {
               tableName = ((LcdpAuditableDTO)itemList.get(0)).getTableName();
            } else {
               tableName = PersistableMetadataHelper.getTableName(itemList.get(0).getClass());
            }

            List<CoreBpmnProcBean> procList = this.procService.selectRuntimeBpmnProcList(tableName, LocalContextHelper.getLoginOrgId());
            if (procList.isEmpty()) {
               throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
            }

            for (T rawItem : rawItemList) {
               List<CoreBpmnProcBean> matchProcList = new ArrayList<>();

               for (CoreBpmnProcBean proc : procList) {
                  if (StringUtils.isBlank(proc.getExpression())) {
                     matchProcList.add(proc);
                  } else {
                     Map<String, Object> vars = BeanUtils.deeplyToMap(rawItem);
                     if (CoreBpmnHelper.evalExpression(proc.getExpression(), vars)) {
                        if (!matchProcList.isEmpty() && !StringUtils.isBlank(matchProcList.get(0).getExpression())) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                        }

                        matchProcList.add(0, proc);
                     }
                  }
               }

               if (matchProcList.isEmpty()) {
                  throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.ENGINE.PROCESS_NOT_EXISTS", I18nHelper.getMessage(tableName + ".SERVICE_NAME"));
               }

               if (matchProcList.size() == 1) {
                  runtimeMap.put(rawItem.getId(), matchProcList.get(0));
               } else if (!StringUtils.isBlank(matchProcList.get(0).getExpression())) {
                  runtimeMap.put(rawItem.getId(), matchProcList.get(0));
               } else {
                  for (CoreBpmnProcBean procx : matchProcList) {
                     if (!StringUtils.startsWith(procx.getExt$Item("orgqty"), "0")) {
                        if (runtimeMap.get(rawItem.getId()) != null) {
                           throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                        }

                        runtimeMap.put(rawItem.getId(), procx);
                     }
                  }

                  if (runtimeMap.get(rawItem.getId()) == null) {
                     throw new BpmnException("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MATCH_MULTIPLE_PROC");
                  }
               }
            }
         }

         if (!approveItemList.isEmpty()) {
            String tableNamex = PersistableMetadataHelper.getTableName(itemList.get(0).getClass());
            List<String> targetIdList = approveItemList.stream().map(i -> tableNamex + "$" + i.getId()).collect(Collectors.toList());
            List<CoreBpmnInstanceBean> instanceList = this.instanceService
               .selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR), new Order[0]);
            if (instanceList != null && instanceList.size() > 0) {
               approveItemList.forEach(
                  a -> {
                     CoreBpmnProcBean var10000 = runtimeMap.put(
                        a.getId(),
                        this.procService
                           .selectById(instanceList.stream().filter(i -> i.getTargetId().equals(tableNamex + "$" + a.getId())).findAny().get().getProcId())
                     );
                  }
               );
            }
         }

         return runtimeMap;
      }
   }
}
