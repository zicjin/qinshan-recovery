emcMtlCategory.editPage = {

    getMtlCategoryGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-category-edit-list-grid',
            url : emcMtlCategory.baseUrl + '/queries/raw',
            columns : emcMtlCategory.getMtlCategoryColumns(),
            service : 'emcMtlCategoryServiceImpl',
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.mtlCategoryName || row.mtlCategoryId || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-mtl-category-edit-list-org-permission-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onMtlCategoryActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        titleWidth : 100,
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.MTLCATEGORIES.MODAL.MTL_CATEGORY_ADD',
                        fields : emcMtlCategory.getPreInsertFields(),
                        url : emcMtlCategory.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-mtl-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-mtl-category-edit-list-grid').deleteRows(emcMtlCategory.baseUrl);
                }
            } ]
        }
    },

    onMtlCategoryActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-mtl-category-edit-list-org-permission-grid').setOptions({
                requestData : {
                    permissionTypeId_SEQ : 'T_EMC_MTL_CATEGORY',
                    targetId_SEQ : row.id,
                },
            });
            Gikam.getComp('emc-mtl-category-edit-list-org-permission-grid').refresh({
                url : '/secure/emc/module/mdm/org-permissions/queries',
            });
        }
    },

    getOrgPermissionGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-mtl-category-edit-list-org-permission-grid',
            url : '',
            service : 'emcOrgPermissionServiceImpl',
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.orgName || row.id;
            },
            columns : emcMtlCategory.getOrgPermissionColumns(),
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
            }, ]
        }
    },

    insertOrgPermission : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-mtl-category-edit-list-grid').getActivedRow();
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
                targetFilterValue1 : 'T_EMC_MTL_CATEGORY',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        orgId : item.id,
                        orgName : item.orgName,
                        permissionTypeId : 'T_EMC_MTL_CATEGORY',
                        targetId : activeRow.id,
                    }
                });
                Gikam.getComp('emc-mtl-category-edit-list-org-permission-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData);
            },
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-mtl-category-edit-list-org-permission-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-mtl-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '28%',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.MTLCATEGORIES.TAB.BASE_INFO',
                        items : [ _this.getMtlCategoryGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.MTLCATEGORIES.TAB.ORG_PERMISSION',
                        items : [ _this.getOrgPermissionGridParam() ]
                    } ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
