emcPatrolHandle.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-patrol-edit-list-grid',
            url : emcPatrolHandle.baseUrl + '/queries/raw/month/handleUser',
            service : 'emcPatrolServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcPatrolHandle.getEditListColumns()
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'EMC.MODULE.ITEM.PATROLS.ICON.ALL',
                icon : 'select',
                onClick : function() {
                    Gikam.getComp('emc-patrol-edit-list-grid').refresh({
                        url : emcPatrolHandle.baseUrl + '/queries/raw/month'
                    });

                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.PATROLS.ICON.PER',
                icon : 'select',
                onClick : function() {
                    Gikam.getComp('emc-patrol-edit-list-grid').refresh({
                        url : emcPatrolHandle.baseUrl + '/queries/raw/month/handleUser'
                    });
                }
            } ]
        } ];

    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-patrol-edit-list-layout',
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
