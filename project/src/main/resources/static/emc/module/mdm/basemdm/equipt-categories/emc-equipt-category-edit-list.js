emcEquiptCategory.editPage = {

    getEquiptCategoryGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-category-edit-list-grid',
            url : emcEquiptCategory.baseUrl + '/queries/raw',
            columns : emcEquiptCategory.getEquiptCategoryColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.equiptCategory || row.id;
            },
            service : 'emcEquiptCategoryServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-equipt-category-edit-list-equipt-maint-grid').cleanData();
                    Gikam.getComp('emc-equipt-category-edit-list-org-permission-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onEquiptCategoryActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.EQUIPTCATEGORIES.MODAL.EQUIPT_CATEGORY_ADD',
                        fields : emcEquiptCategory.getEquiptCategoryColumns(),
                        url : emcEquiptCategory.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-equipt-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-equipt-category-edit-list-grid').deleteRows(emcEquiptCategory.baseUrl);
                }
            } ]
        }
    },

    onEquiptCategoryActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-equipt-category-edit-list-equipt-maint-grid').refresh({
                url : _this.getEquiptMaintUrl(row.id),
            });
            Gikam.getComp('emc-equipt-category-edit-list-org-permission-grid').setOptions({
                requestData : {
                    permissionTypeId_SEQ : 'T_EMC_EQUIPT_CATEGORY',
                    targetId_SEQ : row.id,
                },
            });
            Gikam.getComp('emc-equipt-category-edit-list-org-permission-grid').refresh({
                url : '/secure/emc/module/mdm/org-permissions/queries',
            });
        }
    },
    getEquiptMaintUrl : function(equiptMaintId) {
        if (equiptMaintId) {
            return Gikam.printf(emcEquiptCategory.baseUrl + '/{id}/maints/queries', {
                id : equiptMaintId
            })
        }
    },
    getMaintAndOrgParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'emc-equipt-category-edit-list-maint-and-org-tab',
            fill : true,
            panels : [ {
                id : 'emc-equipt-category-edit-list-maint-panel',
                title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTCATEGORIES.TAB.EQUIPT_MAINT',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-category-edit-list-equipt-maint-grid',
                    url : _this.getEquiptMaintUrl(),
                    service : 'emcEquiptMaintServiceImpl',
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.maintEvent || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            _this.insertEquiptMaint();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteEquiptMaint();
                        }
                    }, ],
                    columns : emcEquiptCategory.getEquiptMaintColumns()
                } ]
            }, {
                title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTCATEGORIES.TAB.ORG_PERMISSION',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-category-edit-list-org-permission-grid',
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
                    columns : emcEquiptCategory.getOrgPermissionColumns()
                } ]
            } ]
        }
    },

    insertEquiptMaint : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-equipt-category-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.getComp('emc-equipt-category-edit-list-equipt-maint-grid').insert(Gikam.printf(emcEquiptCategory.baseUrl + '/{id}/maints', {
            id : activeRow.id
        }), [ {} ]);
    },

    deleteEquiptMaint : function() {
        Gikam.getComp('emc-equipt-category-edit-list-equipt-maint-grid').deleteRows(Gikam.printf(emcEquiptCategory.baseUrl + '/{id}/maints', {
            id : Gikam.getComp('emc-equipt-category-edit-list-grid').getActivedRow().id
        }));
    },

    insertOrgPermission : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-equipt-category-edit-list-grid').getActivedRow();
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
                targetFilterValue1 : 'T_EMC_EQUIPT_CATEGORY',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        orgId : item.id,
                        orgName : item.orgName,
                        permissionTypeId : 'T_EMC_EQUIPT_CATEGORY',
                        targetId : activeRow.id,
                    }
                });
                Gikam.getComp('emc-equipt-category-edit-list-org-permission-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData);
            },
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-equipt-category-edit-list-org-permission-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '28%',
                items : [ _this.getEquiptCategoryGridParam() ]
            },
            center : {
                items : [ _this.getMaintAndOrgParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
