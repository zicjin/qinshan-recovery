emcEquipt.caliChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcEquipt.getCaliChooseListColumns(), requestData = this.param;
        var url = '';
        if (requestData.pageType == 'equiptCalibration') {
            url = '/cali-queries/choosable';
        }
        if (requestData.pageType == 'equiptVerification') {
            url = '/verify-queries/choosable';
        }
        if (requestData.pageType == 'duringCheck') {
            url = '/per-check-queries/choosable';
        }

        if (requestData.biz == 'orderTask') {
            Gikam.extend(requestData, {
                target_equiptassigned : '1',
                testMethodId : requestData.testMethodId
            });

        }
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        return {
            type : 'grid',
            id : 'emc-equipt-cali-choose-list-grid',
            url : emcEquipt.baseUrl + url,
            requestData : requestData,
            filterOpen : true,
            page : true,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-cali-choose-list-grid').getSelections());
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-equipt-cali-choose-list-grid').getSelections());
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
