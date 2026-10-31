emcEquiptPurch.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-purch-edit-list-grid',
            url : emcEquiptPurch.baseUrl + '/queries/raw',
            columns : emcEquiptPurch.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'equiptName' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.equiptName || row.id;
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
                        modalTitle : 'EMC.MODULE.ITEM.SCM.EQUIPTPURCHS.TAB.EQUIPT_PURCH_ADD',
                        fields : emcEquiptPurch.getBaseInfoFormFields(),
                        url : emcEquiptPurch.baseUrl
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-equipt-purch-edit-list-grid').deleteRows(emcEquiptPurch.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            } , {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-equipt-purch-edit-list-grid');
                    Gikam.viewAudit('T_EMC_EQUIPT_PURCH', grid.getSelections());
                }
            } ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('emc-equipt-purch-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcEquiptPurch
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-purch-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.EQUIPTPURCHS.MODAL.EQUIPT_PURCH',
            width : Gikam.width.XL,
            height : 400,
            url : emcEquiptPurch.baseUrl + '/page/detail' + Gikam.param({
                id : id
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-equipt-purch-edit-list-grid').refresh();
            }
        });
    },

    init : function() {
        this.create();
    }
};
