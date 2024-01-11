emcOfficeMtl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-office-mtl-edit-list-grid',
            url : emcOfficeMtl.baseUrl + '/queries/raw',
            columns : emcOfficeMtl.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.mtlName || row.id;
            },
            onCellClick : function(field, row) {
                field === 'mtlName' && _this.loadDetailPage(row.id);
            },
            requestData : {
                filterOrgId : '1'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.SCM.OFFICEMTLS.TAB.OFFICE_MTL_ADD',
                        fields : emcOfficeMtl.getBaseInfoFormFields(),
                        url : emcOfficeMtl.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-office-mtl-edit-list-grid').deleteRows(emcOfficeMtl.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-office-mtl-edit-list-grid');
                    Gikam.viewAudit('T_EMC_OFFICE_MTL', grid.getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-office-mtl-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcOfficeMtl
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-office-mtl-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.OFFICEMTLS.MODAL.OFFICE_MTL',
            width : Gikam.width.XL,
            height : 260,
            url : emcOfficeMtl.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-office-mtl-edit-list-grid').refresh();
            }
        });
    },

    init : function() {
        this.create();
    }
};
