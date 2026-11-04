coreBpmnProc.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.BUTTON.VIEW',
                icon : 'edit',
                onClick : function() {
                    var url = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-designer?id=' + _this.param.diagramId + "&" + new Date().getTime();
                    
                    Gikam.create('modal',{src:url})
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    id : 'core-bpmn-proc-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreBpmnProcServiceImpl',
                    columns : 3,
                    fields : coreBpmnProc.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        _this.param.diagramId = data.diagramId;
                        
                        _this.refreshBpmnViewer(data.diagramId);
                        _this.refreshBpmnCandidatorList(data.diagramId);
                        _this.refreshBpmnSequenceFlowConditionList(data.diagramId);
                    }
                } ]
            } ]
        }
    },
    
    getBpmnViewerInfoTab : function() {
        var _this = this;
        return {
        	id : 'core-bpmn-proc-detail-base-info-iframe-tab',
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.TAB.BPMNVIWERINFO',
                items : [ {
                    type : 'iframe',
                    id : 'core-bpmn-proc-detail-base-info-iframe',
                    height : 300,
                    src : ''
                } ]
            } ]
        }
    },
    
    getBpmnDiagramInfoTab : function() {
        var _this = this;
        return {
            id : 'core-bpmn-diagram-info-list-tab',
            type : 'tab',
            height : 300,
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.TASK_CANDIDATOR_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-bpmn-candidator-list-grid',
                    filter : false,
                    page : false,
                    toolbarHidden : true,
                    url : '',
                    columns : coreBpmnProc.getTaskCandidatorListColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.SEQUENCE_FLOW_CONDITION_INFO',
                items : [ {
                    type : 'grid',
                    id : 'core-bpmn-sequence-flow-condition-list-grid',
                    filter : false,
                    page : false,
                    toolbarHidden : true,
                    url : '',
                    columns : coreBpmnProc.getSequenceFlowConditionListColumns(),
                } ]
            } ]
        }
    },
    
    getBpmnUsedOrgParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : "CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.USED_ORG_INFO",
                items : [ {
                    type : 'grid',
                    filter : false,
                    page : false,
                    toolbarHidden : true,
                    height : 200,
                    id : 'core-bpmn-used-org-list-grid',
                    url : _this.getCompUrl('usedOrgGrid'),
                    columns : coreBpmnDraft.getUsedOrgListColumns(),
                    genericQuery : false
                } ]
            } ]
        }
    },
    
    refreshBpmnViewer : function (diagramId) {
        var iframeUrl = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-viewer?id=' + diagramId + "&" + new Date().getTime();
        
        Gikam.getComp('core-bpmn-proc-detail-base-info-iframe').refresh({src:iframeUrl});
    },
    
    refreshBpmnCandidatorList : function (diagramId) {
        var getTaskCandidatorUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + diagramId + '/queries/task-candidators';
        
        Gikam.getComp('core-bpmn-candidator-list-grid').refresh({url:getTaskCandidatorUrl});
    },
    
    refreshBpmnSequenceFlowConditionList : function (diagramId) {
        var getSequenceFlowConditionUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + diagramId + '/queries/sequence-flow-conditions';
        
        Gikam.getComp('core-bpmn-sequence-flow-condition-list-grid').refresh({url:getSequenceFlowConditionUrl});
    },
    
    urlManager : {
        baseInfoForm : coreBpmnProc.baseUrl + '/{id}',
        usedOrgGrid : coreBpmnProc.baseUrl + '/{id}/bpmn-proc-orgs/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-bpmn-proc-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getBpmnUsedOrgParam(), this.getBpmnDiagramInfoTab(), this.getBpmnViewerInfoTab() ]
            },
            renderTo : workspace.window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}