coreReportConfig.choosePage = {
    getGridParam : function() {
        var _this = this;
        var columns = coreReportConfig.getChooseListColumns();
        var requestData = _this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'core-report-config-choose-list-grid',
            url : coreReportConfig.baseUrl + '/queries/choosable',
            columns : columns,
            filterOpen : true,
            requestData : requestData
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
                        Gikam.getLastModal().close(Gikam.getComp('core-report-config-choose-list-grid').getSelections());
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
                items : [ _this.getBtnToolbarParam(), _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        this.param = param;
        this.create();
    }
};