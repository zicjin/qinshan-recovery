emcEquipt.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcEquipt.getSimpleChooseListColumns(), requestData = this.param;

        if (requestData.biz == 'orderTask') {

            Gikam.extend(requestData, {
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_EQUIPT_TEST_METHOD",
                    targetMatchColumn : "EQUIPTID",
                    filter : [ {
                        targetFilterColumn : 'testMethodId',
                        targetFilterValue : requestData.testMethodId
                    } , {
                        targetFilterColumn : 'ANALYSISCATEGORY',
                        targetFilterValue :'sampling'
                    }]
                })
            });

        }
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-equipt-simple-choose-list-grid',
            url : emcEquipt.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            filterOpen : true,
            page : true,
            pageSize : 100,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-simple-choose-list-grid').getSelections());
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-equipt-simple-choose-list-grid').getSelections());
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