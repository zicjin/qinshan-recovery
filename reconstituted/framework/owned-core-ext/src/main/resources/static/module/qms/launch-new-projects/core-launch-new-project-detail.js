coreLaunchNewProject.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_LAUNCH_NEW_PROJECT', _this.param.id);
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CLOUD.GIKAM.QMS.LAUNCHNEWPROJECTS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreLaunchNewProjectServiceImpl',
                    id : 'core-launch-new-project-detail-base-info-form',
                    fields : coreLaunchNewProject.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getSouthTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ _this.getNewProjectItemGrid() ]
        }
    },

    getNewProjectItemGrid : function() {
        var _this = this;
        return {
            title : 'CLOUD.GIKAM.QMS.LAUNCHNEWPROJECTS.TAB.NEW_PROJECT_ITEM',
            items : [ {
                type : 'grid',
                id : 'core-launch-new-project-edit-list-new-project-item-grid',
                service : 'coreLaunchNewProjectItemServiceImpl',
                url : _this.getCompUrl('newProjectItemGrid'),
                deleteFormatter : function(row) {
                    return row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add-row',
                    onClick : function() {
                        Gikam.getComp('core-launch-new-project-edit-list-new-project-item-grid').insert(Gikam.printf(coreLaunchNewProject.baseUrl + '/{id}/new-project-items', {
                            id : _this.param.id
                        }), [ {} ]);
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove-row',
                    onClick : function() {
                        Gikam.getComp('core-launch-new-project-edit-list-new-project-item-grid').deleteRows(Gikam.printf(coreLaunchNewProject.baseUrl + '/{id}/new-project-items', {
                            id : _this.param.id
                        }));
                    }
                } ],
                columns : coreLaunchNewProject.getNewProjectItemListColumns()
            } ]
        };
    },

    urlManager : {
        baseInfoForm : coreLaunchNewProject.baseUrl + '/{id}',
        newProjectItemGrid : coreLaunchNewProject.baseUrl + '/{id}/new-project-items/queries',
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
            north : {
                height : '50%',
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            center : {
                items : [ this.getSouthTab() ]
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
