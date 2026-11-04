coreRole.choosePage = {

    getRoleGridParam : function() {
        var _this = this, columns = coreRole.getChooseListColumns();
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        
        return {
            type : 'grid',
            id : 'core-role-choose-list-grid',
            url : coreRole.baseUrl + '/queries/choosable',
            requestData : _this.param,
            genericQueryFields : [ {
                field : 'roleName'
            }, {
                field : 'roleDesc'
            } ],
            columns : columns,
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-role-choose-list-grid').getSelections());
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getRoleGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        
        this.create();
    }
};