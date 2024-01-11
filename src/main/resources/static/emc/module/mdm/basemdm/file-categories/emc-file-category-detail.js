emcFileCategory.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-file-category-detail-back-button',
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
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FILECATEGORIES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-file-category-detail-base-info-form',
                    columns : 2,
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcFileCategoryServiceImpl',
                    fields : emcFileCategory.getBaseInfoFormFields(),
                    onRendered : function() {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'fileCategoryNo' ]);
                        }
                    }
                } ]
            } ]
        }
    },

    getOrgOrgPermission : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-file-category-detail-org-tab',
            width : Gikam.width.M,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.FILECATEGORIES.TAB.ORG_PERMISSION',
                items : [ {
                    type : 'simpleGrid',
                    id : 'emc-file-category-detail-org-permission-grid',
                    url : '/secure/emc/module/mdm/org-permissions/queries',
                    service : 'emcOrgPermissionServiceImpl',
                    requestData : {
                        permissionTypeId_SEQ : 'T_EMC_FILE_CATEGORY',
                        targetId_SEQ : _this.param.id,
                    },
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.orgName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertOrgPermission();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteOrgPermission();
                        }
                    }, ],
                    columns : emcFileCategory.getOrgPermissionColumns()
                } ]
            } ]
        }
    },

    insertOrgPermission : function() {
        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org').title,
            url : Gikam.choose.getConfig('org').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_ORG_PERMISSION',
                targetUserIdColumn : 'ORGID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : _this.param.id,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_EMC_FILE_CATEGORY',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        orgId : item.id,
                        orgName : item.orgName,
                        permissionTypeId : 'T_EMC_FILE_CATEGORY',
                        targetId : _this.param.id,
                    }
                });
                Gikam.getComp('emc-file-category-detail-org-permission-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData);
            },
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-file-category-detail-org-permission-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    urlManager : {
        baseInfoForm : emcFileCategory.baseUrl + '/{id}',
        orgPermissionGrid : '/secure/emc/module/mdm/org-permissions/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-file-category-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-file-category-detail-org-permission-grid' : this.getCompUrl('orgPermissionGrid'),
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
            id : 'emc-file-category-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab(), this.getOrgOrgPermission() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
