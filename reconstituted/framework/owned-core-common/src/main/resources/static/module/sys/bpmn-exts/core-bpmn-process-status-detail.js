coreBpmnProcessStatus.detailPage = {
    getBaseCommentTab : function() {
        var _this = this;
        var displayMode = 'grid';
        var getDefaultOptions = Gikam.component.Workflow.getDefaultOptions;
        if(getDefaultOptions) {
            displayMode = getDefaultOptions().commentDisplayMode;
        }
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_COMMENT.TAB.COMMENT',
                items : [ {
                    type : displayMode,
                    filter : false,
                    page : false,
                    generalButtonGroup : ['refresh', 'export'],
                    url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-comments/queries?table=' + _this.param.table + '&id=' + _this.param.id,
                    columns : coreBpmnProcessStatus.getCommentListColumns(),
                } ]
            } ]
        }
    },

    getBpmnViewerInfoTab : function() {
        var _this = this;

        var url = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-viewer?id=' + _this.param.diagramId;

        if (!Gikam.isEmpty(_this.param.table)
                && !Gikam.isEmpty(_this.param.id)) {
            url += '&dataTable=' + _this.param.table + "&dataId=" + _this.param.id;
        }

        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.TAB.BPMNVIWERINFO',
                items : [ {
                    type : 'iframe',
                    src : url
                } ]
            } ]
        }
    },
    
    getGroupingCandidatorForm : function() {
        var _this = this;
        
        var url = Gikam.printf(IFM_CONTEXT + '/secure/core/module/sys/bpmn-insts/{id}/queries/grouping-candidators', {
            id : _this.param.instanceId
        })
        
        var form = {type : 'form'};
        
        Gikam.getJsonSync(url).done(function(data) {
            if (data && data.length > 0) {
                var text = '<span style="color:#007aff;font-size:15px;" class="icon-appoint-person" ></span>&nbsp;';
                for(i = 0, len = data.length; i < len; i++) {
                    var row = data[i];
                    
                    if (text !== '') {
                        text += '  ';
                    }
                    
                    text += row.taskName + ": " + row.candidator;
                }
                
                form = {
                    type : 'form',
                    caption: {
                        text : text
                    }
                };
            }
        })

        return form;
    },

    getBpmnDiagramInfoTab : function() {
        var _this = this;
        return [ {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.TASK_CANDIDATOR_INFO',
            items : [ {
                type : 'grid',
                filter : false,
                page : false,
                generalButtonGroup : ['refresh', 'export'],
                id : 'core-bpmn-candidator-list-grid',
                url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + _this.param.diagramId + '/queries/task-candidators?itemId=' + _this.param.id,
                columns : coreBpmnProcessStatus.getTaskCandidatorListColumns(),
            } ]
        }, {
            title : 'CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.TAB.SEQUENCE_FLOW_CONDITION_INFO',
            items : [ {
                type : 'grid',
                filter : false,
                page : false,
                generalButtonGroup : ['refresh', 'export'],
                id : 'core-bpmn-sequence-flow-condition-list-grid',
                url : IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + _this.param.diagramId + '/queries/sequence-flow-conditions',
                columns : coreBpmnProcessStatus.getSequenceFlowConditionListColumns(),
            } ]
        } ];
    },

    getBpmnMessageTab : function() {
        return {
            id : 'core-bpmn-diagram-info-list-message-tab',
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.TAB.BPMNVIWERINFO',
                items : [ {
                    type : 'grid',
                    filter : false,
                    height : 100,
                    id : 'core-bpmn-message-grid',
                    url : '',
                    columns : [],
                    genericQuery : false,
                    onRendered : function() {
                        this.hideBody().appendContent('<span id="message-span" style="color:red">流程未定义</span>');
                    }
                } ]
            } ]
        }
    },

    getBpmnTab : function() {
        var tab = this.getBaseCommentTab();
        if (Gikam.isEmpty(this.param.procId)) {
            if (this.param.processStatus !== 'done') {
                tab.panels.push(this.getBpmnMessageTab());
            }
        } else {
            tab.panels = tab.panels.concat(this.getBpmnDiagramInfoTab());
        }
        return tab;
    },

    createPage : function() {
        var _this = this;
        
        var items = {
                type : 'layout',
                center : {
                    items : [ _this.getBpmnTab() ]
                }
            };
        
        if (Gikam.isNotEmpty(_this.param.instanceId)) {
            items.north = {
                height : 36,
                items : [ _this.getGroupingCandidatorForm() ]
            }
        }
        
        var options = {
            center : {
                items : [ {
                    type : 'layout',
                    center : {
                        items : [ items ]
                    }
                } ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
            readonly : true
        };
        if (Gikam.isNotEmpty(_this.param.procId)) {
            options.north = {
                height : '50%',
                items : [ _this.getBpmnViewerInfoTab() ]
            }
        }
        Gikam.create('layout', options);
        return this;
    },

    init : function(param) {
        this.param = param;

        var _this = this;

        var url = IFM_CONTEXT + '/secure/core/module/sys/bpmn-exts/runtime-info?table=' + param.table + '&id=' + param.id;
        Gikam.getJson(url).done(function(v) {
            _this.param.instanceId = v.instanceId;
            _this.param.procId = v.procId;
            _this.param.diagramId = v.diagramId;
            _this.param.processStatus = v.processStatus;
            _this.createPage();
        });
    }
}