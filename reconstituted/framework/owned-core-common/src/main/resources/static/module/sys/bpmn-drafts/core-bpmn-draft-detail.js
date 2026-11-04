coreBpmnDraft.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    if (!Gikam.isEmpty(_this.param.id)) {
                        _this.submit();
                    }
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.BUTTON.DESIGN',
                icon : 'edit',
                onClick : function() {
                    if (Gikam.isEmpty(_this.param.diagramId)) {
                        return;
                    }

                    var src = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-designer?id=' + _this.param.diagramId + "&" + new Date().getTime();
                    var modal = Gikam.create('modal', {
                        title : "CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.MODAL.TITLE_DESIGN",
                        onBeforeClose : function() {
                            var $param = diagramIframe.iframe.contentWindow.$param;
                            var saveUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + _this.param.diagramId;
                            workspace.window.showMask(true);
                            Gikam.put(saveUrl, Gikam.getJsonWrapper(null, [ '', [ {
                                'id' : _this.param.diagramId,
                                'diagram' : $param['diagram'],
                                'svg' : $param['svg']
                            } ] ])).done(function() {
                                _this.refreshBpmnViewer(_this.param.diagramId);
                                Gikam.getComp('core-bpmn-candidator-list-grid').refresh();
                                Gikam.getComp('core-bpmn-sequence-flow-condition-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        },
                        onAfterClose : function() {

                        }
                    });
                    var diagramIframe = Gikam.create('iframe', {
                        src : src,
                        height : '100%',
                        renderTo : modal.window.$dom
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_BPMN_DRAFT', _this.param.id);
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
            fill : false,
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    id : 'core-bpmn-draft-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreBpmnDraftServiceImpl',
                    columns : 3,
                    autoSave : true,
                    fields : coreBpmnDraft.getBaseInfoFormFields(),
                    onRendered : function() {
                    },
                    onLoadSuccess : function(data) {
                        if (data) {
                            if (data.procVersion && data.procVersion > 1) {
                                this.toFieldsReadonly([ 'serviceId', 'procCode' ]);
                            }

                            _this.param.diagramId = data.diagramId;

                            _this.createDiagramInfoTab();
                        }
                    }
                } ]
            } ]
        }
    },

    getBpmnViewerInfoTab : function() {
        var _this = this;

        return {
            id : 'core-bpmn-draft-detail-base-info-iframe-tab',
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.BPMNVIWERINFO',
                items : [ {
                    type : 'iframe',
                    id : 'core-bpmn-draft-detail-base-info-iframe',
                    height : 300,
                    src : IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-viewer?id=' + _this.param.diagramId + "&" + new Date().getTime()
                } ]
            } ]
        }
    },

    getBpmnDiagramInfoTab : function() {
        var _this = this;
        return {
            id : 'core-bpmn-diagram-info-list-tab',
            type : 'tab',
            width : Gikam.width.M,
            height : 300,
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.TASK_CANDIDATOR_INFO',
                items : [ {
                    type : 'grid',
                    filter : false,
                    page : false,
                    toolbarHidden : true,
                    id : 'core-bpmn-candidator-list-grid',
                    url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + _this.param.diagramId + '/queries/task-candidators',
                    columns : coreBpmnDraft.getTaskCandidatorListColumns(),
                } ]
            }, {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.SEQUENCE_FLOW_CONDITION_INFO',
                items : [ {
                    type : 'grid',
                    filter : false,
                    page : false,
                    toolbarHidden : true,
                    id : 'core-bpmn-sequence-flow-condition-list-grid',
                    url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + _this.param.diagramId + '/queries/sequence-flow-conditions',
                    columns : coreBpmnDraft.getSequenceFlowConditionListColumns(),
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
                    id : 'core-bpmn-used-org-list-grid',
                    url : _this.getCompUrl('usedOrgGrid'),
                    service : 'coreBpmnDraftServiceImpl',
                    height : 200,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            if (!Gikam.isEmpty(_this.param.id)) {
                                _this.insertUsedOrgRow();
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            if (!Gikam.isEmpty(_this.param.id)) {
                                _this.deleteUsedOrgRow();
                            }
                        }
                    } ],
                    columns : coreBpmnDraft.getUsedOrgListColumns(),
                    genericQuery : false,
                    deleteFormatter : function(row) {
                        return row.ext$.usedorgname;
                    },
                } ]
            } ]
        }
    },

    insertUsedOrgRow : function() {
        var roleData = Gikam.getComp('core-bpmn-draft-detail-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        Gikam.create('modal', {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.MODAL.TITLE_USED_ORG_CHOOSE',
            url : IFM_CONTEXT + Gikam.choose.getConfig('org-simple').url + '?target_filter='
                         + encodeURI("{type:'unassigned',targetTable:'T_CORE_BPMN_DRAFT_ORG',targetMatchColumn:'USEDORGID',thisMatchColumn:'ID',filter:\"[{targetFilterColumn:'DRAFTID',targetFilterValue:'" + roleData.id + "'}]\"}"),
            onAfterClose : function(rows) {
                if (rows) {
                    var chooseData = rows.map(function(item) {
                        return {
                            usedOrgId : item.id
                        }
                    });

                    var insertUsedOrgUrl = Gikam.printf(coreBpmnDraft.baseUrl + '/{id}/bpmn-draft-orgs', {
                        id : roleData.id
                    });

                    Gikam.getComp('core-bpmn-used-org-list-grid').insert(insertUsedOrgUrl, chooseData).done(function() {
                    });
                }
            }
        });
    },

    deleteUsedOrgRow : function() {
        var roleData = Gikam.getComp('core-bpmn-draft-detail-base-info-form').getData();

        if (!roleData.id) {
            return;
        }

        var deleteUsedOrgUrl = Gikam.printf(coreBpmnDraft.baseUrl + '/{id}/bpmn-draft-orgs', {
            id : roleData.id
        });

        Gikam.getComp('core-bpmn-used-org-list-grid').deleteRows(deleteUsedOrgUrl).done(function() {
        });
    },

    refreshBpmnViewer : function(diagramId) {
        var iframeUrl = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-viewer?id=' + diagramId + "&" + new Date().getTime();

        Gikam.getComp('core-bpmn-draft-detail-base-info-iframe').refresh({
            src : iframeUrl
        });
    },

    refreshBpmnCandidatorList : function(diagramId) {
        var getTaskCandidatorUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + diagramId + '/queries/task-candidators';

        Gikam.getComp('core-bpmn-candidator-list-grid').refresh({
            url : getTaskCandidatorUrl
        });
    },

    refreshBpmnSequenceFlowConditionList : function(diagramId) {
        var getSequenceFlowConditionUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + diagramId + '/queries/sequence-flow-conditions';

        Gikam.getComp('core-bpmn-sequence-flow-condition-list-grid').refresh({
            url : getSequenceFlowConditionUrl
        });
    },

    urlManager : {
        baseInfoForm : coreBpmnDraft.baseUrl + '/{id}',
        usedOrgGrid : coreBpmnDraft.baseUrl + '/{id}/bpmn-draft-orgs/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-bpmn-draft-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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

    submit : function() {
        var _this = this;

        var data = Gikam.getComp('core-bpmn-draft-detail-base-info-form').getData();

        var submitUrl = Gikam.printf(coreBpmnDraft.baseUrl + '/{id}/action/submit', {
            id : data.id
        });
        workspace.window.showMask(true);
        Gikam.put(submitUrl).done(function() {
            workspace.window.goBack();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    createDiagramInfoTab : function() {
        if (!Gikam.getComp('core-bpmn-diagram-info-list-tab')) {
            var bpmnDiagramInfoTab = this.getBpmnDiagramInfoTab();
            bpmnDiagramInfoTab.renderTo = Gikam.getComp('core-bpmn-draft-detail-layout').options.center.$dom;
            Gikam.create('tab', bpmnDiagramInfoTab);
        }

        if (!Gikam.getComp('core-bpmn-draft-detail-base-info-iframe-tab')) {
            var bpmnViewerInfoTab = this.getBpmnViewerInfoTab();
            bpmnViewerInfoTab.renderTo = Gikam.getComp('core-bpmn-draft-detail-layout').options.center.$dom;
            Gikam.create('tab', bpmnViewerInfoTab);
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'core-bpmn-draft-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getBpmnUsedOrgParam() ]
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