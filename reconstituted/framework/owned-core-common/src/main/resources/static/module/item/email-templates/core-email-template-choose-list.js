coreEmailTemplate.choosePage = {

    getGridParam : function() {
        var _this = this, columns = coreEmailTemplate.getChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            page : true,
            id : 'core-email-template-list-grid',
            url : coreEmailTemplate.baseUrl + '/queries/choosable',
            requestData : _this.param,
            filterOpen : true,
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'templateName'
            } ],
            columns : columns
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
                        Gikam.getLastModal().close(Gikam.getComp('core-email-template-list-grid').getSelections());
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
                items : [ this.getBtnToolbarParam(), this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        
        this.create();
    }
};