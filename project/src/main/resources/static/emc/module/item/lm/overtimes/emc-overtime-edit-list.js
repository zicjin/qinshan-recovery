emcOvertime.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-overtime-edit-list-grid',
            url : emcOvertime.baseUrl + '/queries/raw',
            service : 'emcOvertimeServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcOvertime.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.userName || row.id;
            }
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-overtime-edit-list-grid').insert(emcOvertime.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-overtime-edit-list-grid').deleteRows(emcOvertime.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-overtime-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : emcOvertime
                    }).done(function() {
                        grid.refresh();
                    });
                }
            } ,
            {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_OVERTIME', Gikam.getComp('emc-overtime-edit-list-grid').getSelections());
                }
            }]
        } ];

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-overtime-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
