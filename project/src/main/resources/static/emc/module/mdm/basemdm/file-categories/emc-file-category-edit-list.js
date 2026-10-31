emcFileCategory.editPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'emc-file-category-edit-list-grid',
            url : emcFileCategory.baseUrl + '/queries/raw',
            service : 'emcFileCategoryServiceImpl',
            columns : emcFileCategory.getEditListColumns(),
            service:'emcFileCategoryServiceImpl',
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.fileCategoryNo || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }else{
                    Gikam.cleanCompData([ 'emc-file-category-detail-org-permission-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-file-category-detail-org-permission-grid').refresh({
                        requestData : {
                            permissionTypeId_SEQ : 'T_EMC_FILE_CATEGORY',
                            targetId_SEQ : row.id,
                        },
                        url : '/secure/emc/module/mdm/org-permissions/queries'
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.FILECATEGORIES.MODAL.FILE_CATEGORY_ADD',
                        fields : emcFileCategory.getBaseInfoFormFields(),
                        url : emcFileCategory.baseUrl
                    }).done(function() {
                        Gikam.getComp('emc-file-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-file-category-edit-list-grid').deleteRows(emcFileCategory.baseUrl);
                }
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
                    type : 'grid',
                    id : 'emc-file-category-detail-org-permission-grid',
                    url : '',
                    service : 'emcOrgPermissionServiceImpl',
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
        var activeRow = Gikam.getComp('emc-file-category-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org').title,
            url : Gikam.choose.getConfig('org').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_ORG_PERMISSION',
                targetUserIdColumn : 'ORGID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : activeRow.id,
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
                        targetId : activeRow.id,
                    }
                });
                Gikam.getComp('emc-file-category-detail-org-permission-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData);
            },
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-file-category-detail-org-permission-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-file-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '20%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getOrgOrgPermission() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
