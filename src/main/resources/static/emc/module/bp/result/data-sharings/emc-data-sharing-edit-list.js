emcDataSharing.editPage = {

    getProjectGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-data-sharing-edit-list-project-grid',
            url : '/secure/emc/module/bp/project/projects/queries/undoable',
            columns : emcDataSharing.getProjectListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-data-sharing-edit-list-folder-grid').cleanData();
                    Gikam.getComp('emc-data-sharing-edit-list-order-task-result-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onProjectActive(row);
            },
            requestData : {
                targetType : 'dataSharing',
                orgId_SB : workspace.user.ext$.parentorgid
            },
        }
    },

    getFolderGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.DATA_SHARINGS.TITLE.FOLDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-data-sharing-edit-list-folder-grid',
                    columns : emcDataSharing.getFolderColumns(),
                    checkOnActive : true,
                    onLoadSuccess : function(data) {
                        if (!Gikam.isEmpty(data)) {
                            this.activeRowByIndex(0);
                        }
                    },
                    onRowActive : function(index, row) {
                        _this.onFolderActive(row);
                    },
                    toolbar : [{
                        type : 'button',
                        text : 'EMC.MODULE.BP.RESULT.DATA_SHARINGS.BUTTON.DATA_SHARE',
                        icon : 'pass',
                        onClick : function() {
                            _this.dataShare();
                        }
                    }]
                } ]
            } ]
        }
    },

    dataShare : function() {

        var _this = this;
        var selections = Gikam.getComp('emc-data-sharing-edit-list-folder-grid').getSelections();
        if (selections.length > 0) {

            Gikam.create('modal', {
                title : 'EMC.MODULE.BP.RESULT.RUNS.MODAL.FILTER_PROJECT',
                width : '45%',
                url : Gikam.choose.getConfig('project').url,
                onAfterClose : function(data) {
                    if (Gikam.isEmpty(data)) {
                        return;
                    }

                    var projectIds = data.map(function(e) {
                        return e.id;
                    });

                    projectIds = projectIds.join();

                    workspace.window.showMask(true);
                    Gikam.post('/secure/emc/module/bp/bp/folders/action/data-sharing', Gikam.getJsonWrapper({projectIds:projectIds}, [ 'emcFolderServiceImpl', selections ])).done(function() {
                    }).always(function() {
                        Gikam.getComp('emc-data-sharing-edit-list-folder-grid').refresh();
                        workspace.window.closeMask();
                    });

                }
            });
            

        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },

    getResultGrid : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.RESULT.DATA_SHARINGS.TITLE.RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-data-sharing-edit-list-order-task-result-grid',
                    service : 'emcOrderTaskResultServiceImpl',
                    columns : emcDataSharing.getOrderTaskResultColumns(),
                } ]
            } ]
        }
    },

    onProjectActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-data-sharing-edit-list-folder-grid').setOptions({
                url : '/secure/emc/module/bp/bp/folders/queries',
                requestData : {
                    targetType : 'dataSharing',
                    projId_SEQ : row.id
                }
            });
            Gikam.getComp('emc-data-sharing-edit-list-folder-grid').refresh();
        }
    },

    onFolderActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-data-sharing-edit-list-order-task-result-grid').setOptions({
                url : '/secure/emc/module/bp/bp/orders/order-task-results',
                requestData : {
                    targetType : 'dataSharing',
                    targetFolderId : row.id
                }
            });
            Gikam.getComp('emc-data-sharing-edit-list-order-task-result-grid').refresh();
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
            renderTo : workspace.window.$dom,
            west : {
                width : '330',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.BP.RESULT.DATA_SHARINGS.TITLE.PROJECT',
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
