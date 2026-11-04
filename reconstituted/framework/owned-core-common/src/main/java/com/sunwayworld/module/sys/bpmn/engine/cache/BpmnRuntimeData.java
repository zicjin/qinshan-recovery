/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.engine.cache;

import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceNextTaskElementDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskHisBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;

public class BpmnRuntimeData<T extends Auditable<ID>, ID extends Serializable> {
    private List<CoreBpmnRuntimeSource<T, ID>> sourceList;
    private List<CoreBpmnInstanceBean> instanceList;
    private List<CoreBpmnInstanceTaskBean> unlimitedInstanceTaskList;
    private List<CoreBpmnInstanceTaskBean> instanceTaskList;
    private List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList;
    private Map<String, List<ID>> callbackMap = new HashMap<String, List<ID>>();
    private Map<String, List<ID>> endCallbackMap = new HashMap<String, List<ID>>();
    private Map<String, List<ID>> scriptMap = new HashMap<String, List<ID>>();
    private Map<String, List<ID>> endScriptMap = new HashMap<String, List<ID>>();
    private Map<String, List<String>> validateSqlMap = new HashMap<String, List<String>>();
    private Map<ID, CoreBpmnProcBean> procMap = new HashMap<ID, CoreBpmnProcBean>();
    private Map<ID, Document> documentMap = new HashMap<ID, Document>();
    private List<CoreBpmnInstanceNextTaskElementDTO<ID>> nextTaskElementList;
    private List<String> previousTaskAuditedByList;
    private Map<ID, CoreBpmnInstanceTaskHisBean> taskHisMap;
    private BpmnProcessData processData = new BpmnProcessData();

    public List<CoreBpmnRuntimeSource<T, ID>> getSourceList() {
        return this.sourceList;
    }

    public void setSourceList(List<CoreBpmnRuntimeSource<T, ID>> sourceList) {
        this.sourceList = sourceList;
    }

    public List<CoreBpmnInstanceBean> getInstanceList() {
        return this.instanceList;
    }

    public void setInstanceList(List<CoreBpmnInstanceBean> instanceList) {
        this.instanceList = instanceList;
    }

    public List<CoreBpmnInstanceTaskBean> getUnlimitedInstanceTaskList() {
        return this.unlimitedInstanceTaskList;
    }

    public void setUnlimitedInstanceTaskList(List<CoreBpmnInstanceTaskBean> unlimitedInstanceTaskList) {
        this.unlimitedInstanceTaskList = unlimitedInstanceTaskList;
    }

    public List<CoreBpmnInstanceTaskBean> getInstanceTaskList() {
        return this.instanceTaskList;
    }

    public void setInstanceTaskList(List<CoreBpmnInstanceTaskBean> instanceTaskList) {
        this.instanceTaskList = instanceTaskList;
    }

    public List<CoreBpmnInstanceCandidatorDTO> getInstanceCandidatorList() {
        return this.instanceCandidatorList;
    }

    public void setInstanceCandidatorList(List<CoreBpmnInstanceCandidatorDTO> instanceCandidatorList) {
        this.instanceCandidatorList = instanceCandidatorList;
    }

    public Map<String, List<ID>> getCallbackMap() {
        return this.callbackMap;
    }

    public void setCallbackMap(Map<String, List<ID>> callbackMap) {
        this.callbackMap = callbackMap;
    }

    public Map<String, List<ID>> getEndCallbackMap() {
        return this.endCallbackMap;
    }

    public void setEndCallbackMap(Map<String, List<ID>> endCallbackMap) {
        this.endCallbackMap = endCallbackMap;
    }

    public Map<String, List<ID>> getScriptMap() {
        return this.scriptMap;
    }

    public void setScriptMap(Map<String, List<ID>> scriptMap) {
        this.scriptMap = scriptMap;
    }

    public Map<String, List<ID>> getEndScriptMap() {
        return this.endScriptMap;
    }

    public void setEndScriptMap(Map<String, List<ID>> endScriptMap) {
        this.endScriptMap = endScriptMap;
    }

    public Map<String, List<String>> getValidateSqlMap() {
        return this.validateSqlMap;
    }

    public void setValidateSqlMap(Map<String, List<String>> validateSqlMap) {
        this.validateSqlMap = validateSqlMap;
    }

    public Map<ID, CoreBpmnProcBean> getProcMap() {
        return this.procMap;
    }

    public void setProcMap(Map<ID, CoreBpmnProcBean> procMap) {
        this.procMap = procMap;
    }

    public Map<ID, Document> getDocumentMap() {
        return this.documentMap;
    }

    public void setDocumentMap(Map<ID, Document> documentMap) {
        this.documentMap = documentMap;
    }

    public List<CoreBpmnInstanceNextTaskElementDTO<ID>> getNextTaskElementList() {
        return this.nextTaskElementList;
    }

    public void setNextTaskElementList(List<CoreBpmnInstanceNextTaskElementDTO<ID>> nextTaskElementList) {
        this.nextTaskElementList = nextTaskElementList;
    }

    public BpmnProcessData getProcessData() {
        return this.processData;
    }

    public void setProcessData(BpmnProcessData processData) {
        this.processData = processData;
    }

    public List<String> getPreviousTaskAuditedByList() {
        return this.previousTaskAuditedByList;
    }

    public void setPreviousTaskAuditedByList(List<String> previousTaskAuditedByList) {
        this.previousTaskAuditedByList = previousTaskAuditedByList;
    }

    public Map<ID, CoreBpmnInstanceTaskHisBean> getTaskHisMap() {
        return this.taskHisMap;
    }

    public void setTaskHisMap(Map<ID, CoreBpmnInstanceTaskHisBean> taskHisMap) {
        this.taskHisMap = taskHisMap;
    }

    public static class BpmnProcessData {
        private List<CoreBpmnInstanceBean> insertInstanceList = new ArrayList<CoreBpmnInstanceBean>();
        private List<CoreBpmnInstanceTaskBean> updateInstanceTaskStatusList = new ArrayList<CoreBpmnInstanceTaskBean>();
        private List<CoreBpmnInstanceTaskBean> insertInstanceTaskList = new ArrayList<CoreBpmnInstanceTaskBean>();
        private List<CoreBpmnInstanceTaskUserBean> insertInstanceTaskUserList = new ArrayList<CoreBpmnInstanceTaskUserBean>();
        private List<CoreBpmnInstanceTaskRoleBean> insertInstanceTaskRoleList = new ArrayList<CoreBpmnInstanceTaskRoleBean>();
        private List<CoreBpmnCommentBean> insertCommentList = new ArrayList<CoreBpmnCommentBean>();
        private List<Long> deleteInstanceTaskUserIdList = new ArrayList<Long>();
        private List<Long> deleteInstanceTaskRoleIdList = new ArrayList<Long>();
        private List<Long> deleteInstanceTaskIdList = new ArrayList<Long>();
        private List<CoreBpmnInstanceBean> deleteInstanceList = new ArrayList<CoreBpmnInstanceBean>();
        private List<Long> deleteCandidatorInstanceTaskIdList = new ArrayList<Long>();
        private List<CoreBpmnTargetBean> insertOrUpdateBpmnTargetList = new ArrayList<CoreBpmnTargetBean>();
        private List<CoreBpmnTargetBean> updateProcessStatusBpmnTargetList = new ArrayList<CoreBpmnTargetBean>();
        private List<String> deleteBpmnTargetIdList = new ArrayList<String>();
        private boolean async = false;
        private Runnable updateProcessStatusRunnable;

        public List<CoreBpmnInstanceBean> getInsertInstanceList() {
            return this.insertInstanceList;
        }

        public List<CoreBpmnInstanceTaskBean> getUpdateInstanceTaskStatusList() {
            return this.updateInstanceTaskStatusList;
        }

        public List<Long> getDeleteInstanceTaskUserIdList() {
            return this.deleteInstanceTaskUserIdList;
        }

        public List<Long> getDeleteInstanceTaskRoleIdList() {
            return this.deleteInstanceTaskRoleIdList;
        }

        public List<Long> getDeleteInstanceTaskIdList() {
            return this.deleteInstanceTaskIdList;
        }

        public List<CoreBpmnInstanceBean> getDeleteInstanceList() {
            return this.deleteInstanceList;
        }

        public List<CoreBpmnInstanceTaskBean> getInsertInstanceTaskList() {
            return this.insertInstanceTaskList;
        }

        public List<CoreBpmnInstanceTaskUserBean> getInsertInstanceTaskUserList() {
            return this.insertInstanceTaskUserList;
        }

        public List<CoreBpmnInstanceTaskRoleBean> getInsertInstanceTaskRoleList() {
            return this.insertInstanceTaskRoleList;
        }

        public List<CoreBpmnCommentBean> getInsertCommentList() {
            return this.insertCommentList;
        }

        public List<Long> getDeleteCandidatorInstanceTaskIdList() {
            return this.deleteCandidatorInstanceTaskIdList;
        }

        public List<CoreBpmnTargetBean> getInsertOrUpdateBpmnTargetList() {
            return this.insertOrUpdateBpmnTargetList;
        }

        public List<CoreBpmnTargetBean> getUpdateProcessStatusBpmnTargetList() {
            return this.updateProcessStatusBpmnTargetList;
        }

        public List<String> getDeleteBpmnTargetIdList() {
            return this.deleteBpmnTargetIdList;
        }

        public void addInsertInstance(CoreBpmnInstanceBean instance) {
            this.insertInstanceList.add(instance);
        }

        public void addUpdateInstanceTaskStatus(CoreBpmnInstanceTaskBean instanceTask) {
            this.updateInstanceTaskStatusList.add(instanceTask);
        }

        public void addDeleteInstanceTaskUserId(List<Long> instanceTaskUserIdList) {
            this.deleteInstanceTaskUserIdList.addAll(instanceTaskUserIdList);
        }

        public void addDeleteInstanceTaskRoleId(List<Long> instanceTaskRoleIdList) {
            this.deleteInstanceTaskRoleIdList.addAll(instanceTaskRoleIdList);
        }

        public void addDeleteInstanceTaskId(Long instanceTaskId) {
            this.deleteInstanceTaskIdList.add(instanceTaskId);
        }

        public void addDeleteInstanceTaskId(List<Long> instanceTaskIdList) {
            this.deleteInstanceTaskIdList.addAll(instanceTaskIdList);
        }

        public void addDeleteInstance(CoreBpmnInstanceBean instance) {
            this.deleteInstanceList.add(instance);
        }

        public void addInsertInstanceTask(CoreBpmnInstanceTaskBean instanceTask) {
            this.insertInstanceTaskList.add(instanceTask);
        }

        public void addInsertInstanceTaskUser(List<CoreBpmnInstanceTaskUserBean> instanceTaskUserList) {
            this.insertInstanceTaskUserList.addAll(instanceTaskUserList);
        }

        public void addInsertInstanceTaskRole(List<CoreBpmnInstanceTaskRoleBean> instanceTaskRoleList) {
            this.insertInstanceTaskRoleList.addAll(instanceTaskRoleList);
        }

        public void addInsertComment(CoreBpmnCommentBean comment) {
            this.insertCommentList.add(comment);
        }

        public void addDeleteCandidatorInstanceTaskId(Long instanceTaskId) {
            this.deleteCandidatorInstanceTaskIdList.add(instanceTaskId);
        }

        public void addInsertOrUpdateBpmnTarget(CoreBpmnTargetBean target) {
            this.insertOrUpdateBpmnTargetList.add(target);
        }

        public void addUpdateProcessStatusBpmnTarget(CoreBpmnTargetBean target) {
            this.updateProcessStatusBpmnTargetList.add(target);
        }

        public void addDeleteBpmnTargetIdList(String targetId) {
            this.deleteBpmnTargetIdList.add(targetId);
        }

        public boolean isAsync() {
            return this.async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }

        public Runnable getUpdateProcessStatusRunnable() {
            return this.updateProcessStatusRunnable;
        }

        public void setUpdateProcessStatusRunnable(Runnable updateProcessStatusRunnable) {
            this.updateProcessStatusRunnable = updateProcessStatusRunnable;
        }
    }
}
