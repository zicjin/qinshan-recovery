emcRunWeighing.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcRunWeighing.getChooseListColumns(), requestData = this.param;
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-run-copy-weight-list-grid',
            url : emcRunWeighing.baseUrl + '/queries/raw',
            requestData : {
                id_DIFFER : _this.param.id,
                orgId_SB : workspace.user.ext$.parentorgid,
                weighingUserId_SEQ : workspace.user.id,
			},
            order : {
                'createdTime' : 'desc'
            },
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-run-copy-weight-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('emc-run-copy-weight-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [  this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
