emcTestCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-category-edit-list-grid',
            url : emcTestCategory.baseUrl + '/queries/raw',
            service : 'emcTestCategoryServiceImpl',
            columns : emcTestCategory.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.testCategoryDesc || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var grid = Gikam.getComp('emc-test-category-detail-org-permission-grid');
                    grid.setOptions({
                        url : ''
                    });
                    grid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onTestCategoryActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcTestCategory.baseUrl);
                    Gikam.getComp('emc-test-category-edit-list-grid').insert(url, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-test-category-edit-list-grid').deleteRows(emcTestCategory.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-test-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '20%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getOrgParam() ]
            }
        });
    },

    getOrgParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            width : Gikam.width.M,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.TESTCATEGORIES.TAB.ORG_PERMISSION',
                items : [ {
                    type : 'grid',
                    id : 'emc-test-category-detail-org-permission-grid',
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
                    columns : emcTestCategory.getOrgPermissionColumns()
                } ]
            } ]
        }
    },

    insertOrgPermission : function() {
        var activeRow = Gikam.getComp('emc-test-category-edit-list-grid').getActivedRow();

        var _this = this;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org').title,
            url : Gikam.choose.getConfig('org').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_ORG_PERMISSION',
                targetUserIdColumn : 'ORGID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : activeRow.id,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_EMC_TEST_CATEGORY',
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        orgId : item.id,
                        orgName : item.orgName,
                        permissionTypeId : 'T_EMC_TEST_CATEGORY',
                        targetId : activeRow.id,
                    }
                });
                Gikam.getComp('emc-test-category-detail-org-permission-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData).done(function() {
                    Gikam.getComp('emc-test-category-detail-org-permission-grid').refresh();
                });
            }
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-test-category-detail-org-permission-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    onTestCategoryActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-test-category-detail-org-permission-grid').refresh({
                url : IFM_CONTEXT + '/secure/emc/module/mdm/org-permissions/queries',
                requestData : {
                    permissionTypeId_SEQ : 'T_EMC_TEST_CATEGORY',
                    targetId_SEQ : row.id,
                }
            });
        }
    },

    urlManager : {
        baseInfoForm : emcTestCategory.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-test-category-detail-org-permission-grid' : this.getCompUrl('orgPermissionGrid'),
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
