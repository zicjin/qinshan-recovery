coreLaunchNewProject.editPage = {

    getToolBarButtons : function() {
        var _this = this;
        return [ {
            type : 'button',
            text : 'GIKAM.BUTTON.INSERT',
            id : 'core-launch-new-project-edit-list-add-button',
            icon : 'add',
            onClick : function() {
                workspace.window.showMask(true);
                // 新增一条数据
                Gikam.postSync(coreLaunchNewProject.baseUrl, Gikam.getJsonWrapper({}, [ '', [ {} ] ])).done(function(item) {
                    var grid = Gikam.getComp('core-launch-new-project-edit-list-grid');
                    grid.refresh().done(function(i) {
                        grid.selectRowByIndex(0);
                        var form = Gikam.getComp('core-launch-new-project-apply-form');
                        form.refresh({
                            url : Gikam.printf(coreLaunchNewProject.baseUrl + '/{id}', {
                                id : item
                            })
                        }, true);
                        form.toFieldsEdit([ 'projectName', 'reason', 'resources', 'carryOrgName' ]);
                    });
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            type : 'button',
            text : 'GIKAM.BUTTON.DELETE',
            id : 'core-launch-new-project-edit-list-remove-button',
            icon : 'remove-row',
            onClick : function() {
                Gikam.getComp('core-launch-new-project-edit-list-grid').deleteRows(coreLaunchNewProject.baseUrl);
            }
        }, {
            type : 'button',
            text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
            id : 'core-launch-new-project-edit-list-submit-button',
            icon : 'submit',
            onClick : function() {
                _this.submit();
            }
        } ];
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-launch-new-project-edit-list-grid',
            url : coreLaunchNewProject.baseUrl + '/queries/raw',
            columns : coreLaunchNewProject.getListColumns(),
            requestData : {
                processStatus_IN : 'draft,draft_rejected'
            },
            onCellClick : function(field, row) {
                var form = Gikam.getComp('core-launch-new-project-apply-form');
                form.refresh({
                    url : Gikam.printf(coreLaunchNewProject.baseUrl + '/{id}', {
                        id : row.id
                    })
                }, true);
                form.toFieldsEdit([ 'projectName', 'reason', 'resources', 'carryOrgName' ]);
            },
            deleteFormatter : function(row) {
                return row.projectNo;
            },
            toolbar : _this.getToolBarButtons()
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-launch-new-project-edit-list-grid');
        var form = Gikam.getComp('core-launch-new-project-apply-form');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreLaunchNewProject
        }).done(function() {
            grid.refresh();
            form.cleanData().done(function() {
                form.toFieldsReadonly([ 'projectName', 'reason', 'resources', 'carryOrgName' ]);
            })
        });
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreLaunchNewProject,
            config : coreLaunchNewProject.pageConfig,
            prior : 'edit'
        }
        Gikam.create('layout', {
            id : 'core-launch-new-project-edit-list-layout',
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig,
            west : {
                width : '50%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ coreLaunchNewProject.getBtnToolbar('core-launch-new-project-edit-list-grid'), coreLaunchNewProject.getDetailTab() ]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
