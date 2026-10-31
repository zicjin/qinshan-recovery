emcTestMethod.tcTestMethodChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcTestMethod.getTcTestMethodChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-tc-test-method-choose-list-grid',
            url : emcTestMethod.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            readonly : true,
            onBeforeRefresh : function(requestData) {
                var chooseListGrid = this;
                if (Gikam.isEmpty(requestData) || Gikam.isEmpty(requestData.testCategoryId_EQ)) {
                    chooseListGrid.setOptions({
                        requestData : {
                            'scheme_only' : 'true'
                        }
                    });
                }
                return true;
            },
            onLoadSuccess : function(data) {
                this.setOptions({
                    requestData : {
                        'scheme_only' : null
                    }
                });
            },
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-tc-test-method-choose-list-grid').getSelections());
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-tc-test-method-choose-list-grid').getSelections());
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
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};