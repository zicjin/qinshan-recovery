emcBizCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-biz-category-edit-list-grid',
            url : emcBizCategory.baseUrl + '/queries',
            service : 'emcBizCategoryServiceImpl',
            columns : emcBizCategory.getEditListColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.bizCategoryName || row.bizCategoryId || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                _this.onTestActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.BIZCATEGORIES.MODAL.ADD',
                        titleWidth : 100,
                        fields : emcBizCategory.getBaseInfoFormFields(),
                        param : {
                            sample : '1',
                            editMonitorInfo : '0'
                        },
                        url : emcBizCategory.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-biz-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-biz-category-edit-list-grid').deleteRows(emcBizCategory.baseUrl);
                }
            } ]
        }
    },

    onTestActive : function(row) {
        var _this = this;
        Gikam.getAsyncComp('emc-biz-category-project-edit-list-grid').done(function(grid) {
            grid.refresh({
                url : _this.getProjectUrl(row.id)
            });
        });
    },

    getProjectUrl : function(bizCategoryId) {
        if (bizCategoryId) {
            return Gikam.printf(emcBizCategory.baseUrl + '/{id}/projects/queries', {
                id : bizCategoryId
            })
        }
    },

    getBizCategoryProjGrid : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-biz-category-project-edit-list-grid',
            service : 'emcBizCategoryProjectServiceImpl',
            columns : emcBizCategory.getProjectEditListColumns(),
            checkOnActive : true,
            url : _this.getProjectUrl(),
            deleteFormatter : function(row) {
                return row.projName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var formData = Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow();
                    if (Gikam.isEmpty(formData.id)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.BIZCATEGORIES.PROJECT.MODAL.ADD',
                        fields : emcBizCategory.getProjectFormFields(),
                        url : emcBizCategory.baseUrl + '/' + formData.id + '/projects',
                        param : {
                            id : formData.id
                        },
                    }).done(function(id) {
                        Gikam.getComp('emc-biz-category-project-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var activeRow = Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow();
                    var url = Gikam.printf(emcBizCategory.baseUrl + '/{id}/projects', {
                        id : activeRow.id
                    });
                    Gikam.getComp('emc-biz-category-project-edit-list-grid').deleteRows(url);
                }
            } ]
        }
    },

    loadOrgPermission : function(id) {
        var _this = this;

        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.BIZCATEGORIES.MODAL.ORG_PERMISSION',
            height : '55%',
            width : '30%',
            url : emcBizCategory.baseUrl + '/page/org-permission' + Gikam.param({
                id : id
            }),
            onAfterClose : function(rows) {
                Gikam.getComp('emc-biz-category-edit-list-grid').refreshRowById(Gikam.getComp('emc-biz-category-edit-list-grid').getActivedRow().id);
            },
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-biz-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            /*north : {
                height : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ _this.getBizCategoryProjGrid() ]
            },*/
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
