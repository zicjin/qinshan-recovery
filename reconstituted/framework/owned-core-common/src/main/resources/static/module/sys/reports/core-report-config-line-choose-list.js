coreReportConfig.lineChoosePage = {
    getGridParam : function() {
        var _this = this, columns = coreReportConfig.getLineChooseListColumns(), requestData = this.param;
        this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            page : true,
            id : 'core-report-config-line-choose-list-grid',
            url : coreReportConfig.baseUrl + '/lines/queries/choosable',
            columns : columns,
            filterOpen : true,
            genericQueryFields : [ {
                field : 'reportName'
            }, {
                field : 'ext$.filename'
            } ],
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
                        Gikam.getLastModal().close(Gikam.getComp('core-report-config-line-choose-list-grid').getSelections());
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
        param.filter_bizCategory = 'master';
        this.param = param;
        this.create();
    }
};