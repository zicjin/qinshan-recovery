emcPatrolScheme.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-patrol-edit-list-grid',
            url : emcPatrolScheme.baseUrl + '/queries/raw/month',
            service : 'emcPatrolServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcPatrolScheme.getEditListColumns()
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
                    Gikam.getComp('emc-patrol-edit-list-grid').insert(emcPatrolScheme.baseUrl + "/monthInsert");
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.ITEM.PATROLS.BUTTON.BATCH_SET_USER',
                icon : 'modify',
                onClick : function() {
                    _this.batchSetPatrolUser();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_PATROL', Gikam.getComp('emc-patrol-edit-list-grid').getSelections());
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
    },

    batchSetPatrolUser : function() {
        var selections = Gikam.getComp('emc-patrol-edit-list-grid').getSelections();
        if (selections.length === 0) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('user-simple').title,
            url : Gikam.choose.getConfig('user-simple').url + Gikam.param({
                single : true,
            }),
            width : 320,
            onAfterClose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    selections.map(function(item) {
                        Gikam.getComp('emc-patrol-edit-list-grid').setData({
                            id : item.id,
                            patrolUserId : rows[0].id,
                            patrolUserName : rows[0].userName
                        });
                    });
                }
            }
        });
    }
};
