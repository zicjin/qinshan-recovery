emcBizCategory.orgPermissionEditPage = {

    getGridParam : function() {
        var _this = this;
        return {

            type : 'grid',
            id : 'emc-biz-category-org-permission-edit-list-grid',
            url : '/secure/emc/module/mdm/org-permissions/queries',
            service : 'emcOrgPermissionServiceImpl',
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.orgName || row.id;
            },
            requestData : {
                permissionTypeId_SEQ : 'T_EMC_BIZ_CATEGORY',
                targetId_SEQ : _this.param.id
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
            columns : emcBizCategory.getOrgPermissionColumns()

        }
    },

    insertOrgPermission : function() {
        var _this = this;
        var id = _this.param.id;
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('org').title,
            url : Gikam.choose.getConfig('org').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_ORG_PERMISSION',
                targetUserIdColumn : 'ORGID',
                targetFilterColumn : 'TARGETID',
                targetFilterValue : id,
                targetFilterColumn1 : 'PERMISSIONTYPEID',
                targetFilterValue1 : 'T_EMC_BIZ_CATEGORY',
                orgCategory_SEQ : 'department'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        orgId : item.id,
                        orgName : item.orgName,
                        permissionTypeId : 'T_EMC_BIZ_CATEGORY',
                        targetId : id,
                    }
                });
                Gikam.getComp('emc-biz-category-org-permission-edit-list-grid').insert('/secure/emc/module/mdm/org-permissions', chooseData).done(function(r) {
                    Gikam.getComp('emc-biz-category-org-permission-edit-list-grid').refresh();
                });
            },
        });
    },

    deleteOrgPermission : function() {
        Gikam.getComp('emc-biz-category-org-permission-edit-list-grid').deleteRows('/secure/emc/module/mdm/org-permissions');
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
