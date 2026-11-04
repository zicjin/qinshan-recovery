coreBpmnTarget.oddPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-bpmn-target-list-grid',
            url : coreBpmnTarget.baseUrl + '/queries/searchable',
            columns : coreBpmnTarget.getSearchListColumns(),
            requestData : {
                processStatus_SB : 'APPROVE'
            },
            onCellClick : function (field, row) {
                // field === 'targetId' && _this.loadDetailPage(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.REJECT',
                icon : 'reject',
                onClick : function () {
                    var grid = Gikam.getComp('core-bpmn-target-list-grid');

                    var selectedRows = grid.getSelections();
                    if (selectedRows.length === 0) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var selectedHtml = '',
                        content = '<div>{selectedHtml}</div>';

                    Gikam.each(selectedRows, function (index, item) {
                        selectedHtml += '<div style="padding-top:5px;">' + item.targetDesc + '</div>';
                    });

                    Gikam.create('confirm', {
                        title : Gikam.propI18N('GIKAM.WORKFLOW.ODD.BUTTON.REJECT'),
                        subTitle : Gikam.propI18N('GIKAM.WORKFLOW.ODD.REJECT.TIP.CONFIRM'),
                        message : Gikam.printf(content, {selectedHtml : selectedHtml}),
                        onYesClick : function () {
                            workspace.window.showMask();
                            let def = Gikam.jQuery.Deferred();
                            let sendRequest = function () {
                                let selectedRow = selectedRows[ 0 ];


                                let url;
                                if (selectedRow.ext$.href) {
                                    url = IFM_CONTEXT + selectedRow.ext$.href + '/action/odd-reject-task';
                                }else {
                                    var requestUrl = coreBpmnTarget.baseUrl + '/{tableName}/lcdp-url/queries';
                                    Gikam.getTextSync(Gikam.printf(requestUrl, {
                                        tableName : selectedRow.targetTable
                                    })).done(function(item) {
                                        if(item){
                                            url = item + '.oddRejectTask';
                                        }
                                    });
                                }

                                if (!url) {
                                    workspace.window.closeMask();
                                    Gikam.alert('GIKAM.WORKFLOW.ODD.TIP.NOMATCH_BIZ_WORKFLOW');
                                    return;
                                }
                                let requestData = Gikam.getJsonWrapper({'bpmn_comment' : Gikam.propI18N('GIKAM.WORKFLOW.ODD.REJECT.OPINION.DEFAULT')}, [ '', [ {id : selectedRow.targetId} ] ]);

                                if (selectedRow.ext$.href) {
                                    Gikam.put(url, requestData).done(function () {
                                        selectedRows.splice(0, 1);

                                        selectedRows.length > 0 ? sendRequest() : (workspace.window.closeMask(), def.resolve());
                                    }).always(function () {
                                        workspace.window.closeMask();
                                        grid.refresh();
                                    });

                                }else {
                                    Gikam.post(url, requestData).done(function () {
                                        selectedRows.splice(0, 1);

                                        selectedRows.length > 0 ? sendRequest() : (workspace.window.closeMask(), def.resolve());
                                    }).always(function () {
                                        workspace.window.closeMask();
                                        grid.refresh();
                                    });
                                }
                            };

                            sendRequest();

                            return def;
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.UPDATE_BPMN',
                icon : 'edit',
                onClick : function () {
                    var grid = Gikam.getComp('core-bpmn-target-list-grid');

                    var selectedRows = grid.getSelections();
                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    var selectedRow = selectedRows[ 0 ];

                    var selectInstanceUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-insts/queries?targetId=' + selectedRow.id;

                    Gikam.post(selectInstanceUrl, Gikam.getJsonWrapper({f : {targetId_SEQ : selectedRow.id}})).done(function (instList) {
                        if (instList.total == 1) {
                            var diagramId = instList.rows[ 0 ].diagramId;

                            var src = IFM_CONTEXT + '/core/module/item/files/page/bpmn-diagram-designer?id=' + diagramId + "&" + new Date().getTime();
                            var modal = Gikam.create('modal', {
                                title : "CORE.MODULE.SYS.T_CORE_BPMN_DRAFT.MODAL.TITLE_DESIGN",
                                onBeforeClose : function () {
                                    var $param = diagramIframe.iframe.contentWindow.$param;
                                    var saveUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-diagrams/' + diagramId;
                                    workspace.window.showMask(true);
                                    Gikam.put(saveUrl, Gikam.getJsonWrapper(null, [ '', [ {
                                        'id' : diagramId,
                                        'diagram' : $param[ 'diagram' ],
                                        'svg' : $param[ 'svg' ]
                                    } ] ])).always(function () {
                                        workspace.window.closeMask();
                                    });
                                }
                            });
                            var diagramIframe = Gikam.create('iframe', {
                                src : src,
                                height : '100%',
                                renderTo : modal.window.$dom
                            });
                        } else {
                            Gikam.alert('GIKAM.WORKFLOW.ODD.TIP.MATCH_MUTIPLE_INST');
                        }
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.ODD.BUTTON.UPDATE_AUDITOR',
                icon : 'edit',
                onClick : function () {
                    var grid = Gikam.getComp('core-bpmn-target-list-grid');

                    var selectedRows = grid.getSelections();
                    if (selectedRows.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    var selectedRow = selectedRows[ 0 ];

                    var selectInstanceUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-insts/queries?targetId=' + selectedRow.id;

                    Gikam.post(selectInstanceUrl, Gikam.getJsonWrapper({f : {targetId_SEQ : selectedRow.id}})).done(function (instList) {
                        if (instList.total == 1) {
                            var instId = instList.rows[ 0 ].id;

                            var updateAuditorUrl = IFM_CONTEXT + '/secure/core/module/sys/bpmn-inst-tasks/page/edit-list?instId_SEQ=' + instId + '&status_SEQ=CREATED';
                            Gikam.create('modal', {
                                title : 'GIKAM.WORKFLOW.ODD.TITLE.UPDATE_AUDITOR',
                                url : updateAuditorUrl,
                                onAfterClose : function (rows) {
                                }
                            });
                        } else {
                            Gikam.alert('GIKAM.WORKFLOW.ODD.TIP.MATCH_MUTIPLE_INST');
                        }
                    });
                }
            } ]
        }
    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-bpmn-target',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function (row) {
        workspace.window.load(row.ext$.href + '/page/detail?id=' + row.targetId);
    },

    init : function (param) {
        this.param = param;
        this.create();
    }
};