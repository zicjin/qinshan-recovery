emcCar.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-car-edit-list-grid',
            url : emcCar.baseUrl + '/queries/raw',
            service : 'emcCarServiceImpl',
            columns : emcCar.getApplyEditListColumns(),
            toolbar : _this.getBtnToolbarParam(),
            deleteFormatter : function(row) {
                return row.applyUserName || row.id;
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
                    Gikam.getComp('emc-car-edit-list-grid').insert(emcCar.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {

                    Gikam.getComp('emc-car-edit-list-grid').deleteRows(emcCar.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-car-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : emcCar
                    }).done(function() {
                        grid.refresh();
                    });
                }
            },
            {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_CAR', Gikam.getComp('emc-car-edit-list-grid').getSelections());
                }
            } ]
        } ];

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-car-edit-list-layout',
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
