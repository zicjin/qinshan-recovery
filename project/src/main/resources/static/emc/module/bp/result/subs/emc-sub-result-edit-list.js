emcSubResult.editPage = {

    getProjectGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sub-result-edit-list-project-grid',
            url : '/secure/emc/module/bp/project/projects/queries/undoable',
            columns : emcSubResult.getProjectListColumns(),
            page:true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-sub-result-edit-list-folder-grid').cleanData();
                    Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            requestData : {
                targetType : 'subcontractor',
                org_filter : '1',
            },
            toolbar : [
                {
                    type : 'button',
                    text : 'EMC.MODULE.BP.RESULT.SUB.FILE.ATTACHMENT',
                    icon : 'default',
                    onClick : function() {
                        var selections = Gikam.getComp('emc-sub-result-edit-list-project-grid').getSelections();

                        if (selections.length !== 1) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                            return;
                        }

                        Gikam.create('modal', {
                            title : 'EMC.MODULE.BP.RESULT.SUB.FILE.ATTACHMENT',
                            url : '/secure/emc/module/mdm/file/files/page/view' + Gikam.param({
                                id : selections[0].id,
                                table : 'T_EMC_PROJECT'
                            }),
                            onAfterClose : function() {
                                Gikam.getComp('emc-sub-result-edit-list-project-grid').refresh();
                            }
                        });
                    }
                }
            ]
        }
    },

    getFolderGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SUBCONTRACTORS.TAB.FOLDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-sub-result-edit-list-folder-grid',
                    columns : emcSubResult.getFolderColumns(),
                    checkOnActive : true,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onFolderActive(row);
                    },
                    toolbar : []
                } ]
            } ]
        }
    },

    getResultGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.SUBCONTRACTORS.TAB.RESULT_SET',
                items : [ {
                    type : 'grid',
                    id : 'emc-sub-result-edit-list-order-task-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcSubResult.getOrderTaskResultColumns(),
                    onUpdated : function(data, fields, index) {
                        if (fields.indexOf('originalResult') > -1) {
                            workspace.window.showMask();
                            Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/change-result', Gikam.getJsonWrapper({}, [ '', [ data ] ])).done(function() {
                                workspace.window.closeMask();
                                Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refreshRowById(data.id);
                            }).fail(function() {
                                workspace.window.closeMask();
                            });
                        }
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'add',
                        onClick : function() {
                            _this.batchInput();
                        }
                    },{
                        type : 'button',
                        text : 'EMC.MODULE.BP.INFO.REPORTTMPLS.MODAL.UPDATE_TEST',
                        icon : 'add',
                        onClick : function() {
                            _this.updateItems();
                        }
                    } ]
                } ]
            } ]
        }
    },

    updateItems : function () {
        var _this = this;
        Gikam.confirm('EMC.MODULE.BP.INFO.MODAL.SYNC_TEST_TMPL_INFO', 'EMC.MODULE.BP.INFO.MODAL.IS_SYNC_TEST_TMPL_INFO', function() {
            var selections = Gikam.getComp('emc-sub-result-edit-list-folder-grid').getSelections();
            var data = Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').getData();
            if (selections.length == 0) {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_DATA');
                return;
            }
            workspace.window.showMask(true);
            Gikam.postText('/secure/emc/module/bp/result/runs/action/sync-sub-tmpl-infos', Gikam.getJsonWrapper(null, [ 'emcFolderServiceImpl', selections ], [ 'emcOrderTaskResultServiceImpl', data ])).done(function() {
                Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        });
    },

    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 220,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.originalResult = formData.originalResult;
                                    item.reportDisplay = formData.reportDisplay;
                                });
                                workspace.window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/instant', Gikam.getJsonWrapper({}, [ 'emcOrderTaskResultServiceImpl', selections ])).done(function () {
                                    if (Gikam.isEmpty(formData.originalResult)) {
                                        Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refresh();
                                        return;
                                    }

                                    var results = selections.map(item => ({id : item.id}));
                                    Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/change-result', Gikam.getJsonWrapper({}, [ '', results ])).done(function() {
                                        workspace.window.closeMask();
                                        Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refresh();
                                    }).fail(function() {
                                        workspace.window.closeMask();
                                    });
                                }).always(function () {
                                    workspace.window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 85,
            fields: emcSubResult.getBatchInputFields(),
            autoSave: false
        });
    },

    onProjectActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sub-result-edit-list-folder-grid').setOptions({
                url : '/secure/emc/module/bp/bp/folders/queries',
                requestData : {
                    targetType : 'subcontractor',
                    projId_SEQ : row.id
                }
            });
            Gikam.getComp('emc-sub-result-edit-list-folder-grid').refresh();
        }
    },

    onFolderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').setOptions({
                url : '/secure/emc/module/bp/bp/orders/order-task-results',
                requestData : {
                    targetType : 'subcontractor',
                    targetFolderId : row.id
                }
            });
            Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refresh();
        }
    },

    getRightLayout : function() {
        var _this = this;
        return {
            type : 'layout',
            north : {
                height : 250,
                items : [ _this.getFolderGrid() ]
            },
            center : {
                items : [ _this.getResultGrid() ]
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '330',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.SUBCONTRACTORS.TAB.PROJECT',
                        items : [ _this.getProjectGrid() ]
                    } ]
                } ]
            },
            center : {
                items : [ this.getRightLayout() ]
            }
        });
    },

    init : function() {
        this.create();
    }

}
