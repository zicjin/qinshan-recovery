/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.validator;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.XmlUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.bpmn.CoreBpmnHelper;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDraftOrgBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import com.sunwayworld.module.sys.bpmn.diagram.BpmnDiagramHelper;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessElementType;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftOrgService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcOrgService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreBpmnDraftSubmittableValidator
implements DataValidator {
    @Autowired
    private CoreBpmnProcService procService;
    @Autowired
    private CoreBpmnProcOrgService procOrgService;
    @Autowired
    private CoreBpmnDraftService draftService;
    @Autowired
    private CoreBpmnDraftOrgService draftOrgService;
    @Autowired
    private CoreBpmnDiagramService diagramService;

    @Override
    public boolean doValid(Object ... args) {
        List<CoreBpmnProcBean> procList;
        List<Element> userTaskList;
        Long id = (Long)args[0];
        CoreBpmnDraftBean draft = (CoreBpmnDraftBean)this.draftService.selectById(id);
        Long diagramId = draft.getDiagramId();
        String diagram = this.diagramService.selectColumnById(diagramId, "DIAGRAM", String.class);
        if (StringUtils.isBlank(diagram)) {
            this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.CANNOT_EMPTY");
            return false;
        }
        Document document = XmlUtils.read(CoreBpmnHelper.getCorrectedDiagram(diagram));
        boolean flag = true;
        List<Element> startEventList = BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.START_EVENT);
        if (startEventList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.NO_START_EVENT");
            flag = false;
        } else if (startEventList.size() > 1) {
            this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MULTIPLE_START_EVENT");
            flag = false;
        }
        List<Element> endEventList = BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.END_EVENT);
        if (endEventList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.NO_END_EVENT");
            flag = false;
        } else if (endEventList.size() > 1) {
            this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.MULTIPLE_END_EVENT");
            flag = false;
        }
        if (flag && !(userTaskList = BpmnDiagramHelper.getProcessElementList(document, ProcessElementType.USER_TASK)).isEmpty()) {
            for (Element userTask : userTaskList) {
                String[] dynamicValues;
                String statusCode = XmlUtils.getAttributeValue(userTask, "statusCode");
                if (StringUtils.isEmpty(statusCode)) {
                    this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.STATUS_CODE_NOT_ASSIGNED");
                    return false;
                }
                Element startEvent = startEventList.get(0);
                Element endEvent = endEventList.get(0);
                if (statusCode.equals(XmlUtils.getAttributeValue(startEvent, "statusCode")) || statusCode.equals(XmlUtils.getAttributeValue(endEvent, "statusCode")) || userTaskList.stream().filter(u -> statusCode.equals(XmlUtils.getAttributeValue(u, "statusCode"))).count() > 1L) {
                    this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.STATUS_CODE_NOT_UNIQUE");
                    return false;
                }
                String name = XmlUtils.getAttributeValue(userTask, "name");
                if (StringUtils.isEmpty(name)) {
                    this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.TASK_NAME_NOT_ASSIGNED");
                    return false;
                }
                String dynamic = XmlUtils.getAttributeValue(userTask, "dynamic");
                if (StringUtils.isEmpty(dynamic)) continue;
                for (String value : dynamicValues = StringUtils.split(dynamic, ",")) {
                    if (StringUtils.startsWith(value, "u:") || StringUtils.startsWith(value, "r:")) continue;
                    this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.DYNAMIC_INVALID");
                    return false;
                }
            }
        }
        if (flag) {
            CoreBpmnProcBean filter = new CoreBpmnProcBean();
            filter.setProcCode(draft.getProcCode());
            CoreBpmnProcBean proc = this.procService.getDao().selectFirstIfPresent((CoreBpmnProcBean)filter, Order.desc("ID"));
            if (proc != null && proc.getProcVersion() + 1L != draft.getProcVersion()) {
                this.addConstraintViolation("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.INVALID_VERSION");
                return false;
            }
        }
        if (flag && !(procList = this.procService.selectBpmnProcListByServiceId(draft.getServiceId())).isEmpty()) {
            CoreBpmnDraftOrgBean draftOrgFilter = new CoreBpmnDraftOrgBean();
            draftOrgFilter.setDraftId(draft.getId());
            List<CoreBpmnDraftOrgBean> draftOrgList = this.draftOrgService.selectList(draftOrgFilter, new Order[0]);
            for (CoreBpmnProcBean proc : procList) {
                if (proc.getProcCode().equals(draft.getProcCode()) || !ObjectUtils.equals(StringUtils.trim(proc.getExpression()), StringUtils.trim(draft.getExpression()))) continue;
                CoreBpmnProcOrgBean procOrgFilter = new CoreBpmnProcOrgBean();
                procOrgFilter.setProcId(proc.getId());
                List<CoreBpmnProcOrgBean> procOrgList = this.procOrgService.selectList(procOrgFilter, new Order[0]);
                if (procOrgList.isEmpty()) {
                    if (!draftOrgList.isEmpty()) continue;
                    this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.DEFAULT_NOT_UNIQU", proc.getProcCode()));
                    return false;
                }
                if (draftOrgList.isEmpty() || !procOrgList.stream().anyMatch(o -> draftOrgList.stream().anyMatch(d -> d.getUsedOrgId().equals(o.getUsedOrgId())))) continue;
                this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_PROC.DIAGRAM.EXPRESSION_AND_ORG_NOT_UNIQUE", proc.getProcCode()));
                return false;
            }
        }
        return flag;
    }
}
