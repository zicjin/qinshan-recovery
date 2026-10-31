emcStandardMtl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-standard-mtl-edit-list-grid',
            url : emcStandardMtl.baseUrl + '/queries/raw',
            columns : emcStandardMtl.getEditListColumns(),
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
                        modalTitle : 'EMC.MODULE.ITEM.SCM.STANDARDMTLS.TAB.STANDARD_MTL_ADD',
                        fields : emcStandardMtl.getBaseInfoFormFields(),
                        url : emcStandardMtl.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-standard-mtl-edit-list-grid').deleteRows(emcStandardMtl.baseUrl);
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
                    var grid = Gikam.getComp('emc-standard-mtl-edit-list-grid');
                    Gikam.viewAudit('T_EMC_STANDARD_MTL', grid.getSelections());
                }
            }  ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-standard-mtl-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcStandardMtl
        }).done(function() {
            grid.refresh();
        });
    },
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-standard-mtl-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.STANDARDMTLS.MODAL.STANDARD_MTL',
            width : Gikam.width.XL,
            height : 300,
            url : emcStandardMtl.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-standard-mtl-edit-list-grid').refresh();
            }
        });
    },

    init : function() {
        this.create();
    }
};
