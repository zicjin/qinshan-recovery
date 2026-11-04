coreTable.choosePage = {
    getTableGridParam : function() {
        var _this = this, columns = coreTable.getChooseListColumns();
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            page : true,
            id : 'core-table-choose-list-grid',
            url : coreTable.baseUrl + '/queries/choosable',
            requestData : _this.param,
            columns : columns,
            filterOpen : true
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
                        var selectedRows = Gikam.getComp('core-table-choose-list-grid').getSelections();
                        if (Gikam.isEmpty(selectedRows)) {
                            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                            return;
                        }
                        Gikam.getLastModal().close(selectedRows);
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
                items : [ this.getBtnToolbarParam(), this.getTableGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};