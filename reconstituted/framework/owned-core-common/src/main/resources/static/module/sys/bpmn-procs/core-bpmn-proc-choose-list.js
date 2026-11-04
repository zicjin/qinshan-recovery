coreBpmnProc.choosePage = {
    getGridParam : function() {
        var _this = this;
        var columns = coreBpmnProc.getChooseListColumns();
        var requestData = _this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'core-bpmn-proc-choose-list-grid',
            url : coreBpmnProc.baseUrl + '/queries/choosable',
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
                        Gikam.getLastModal().close(Gikam.getComp('core-bpmn-proc-choose-list-grid').getSelections());
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
            center : {
                items : [ _this.getGridParam() ]
            },
            north : {
                items : [ _this.getBtnToolbarParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
    },

    loadConfigDetailPage : function(id) {
        workspace.window.load(coreBpmnProc.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};