emcOrderRetain.choosePage = {

    getBaseFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-order-retain-choose-base-info-form',
            columns : 2,
            url : '',
            service : 'emcOrderRetainServiceImpl',
            fields : emcOrderRetain.getBaseInfoFormFields(),
            autoSave : false
        }
    },

    getGridParam : function() {
        var _this = this, columns = emcOrderRetain.getOrderChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        return {
            type : 'grid',
            id : 'emc-order-retain-choose-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/choosable',
            columns : columns,
            page : true,
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
                        var data = {
                            formData : Gikam.getComp('emc-order-retain-choose-base-info-form').getData(),
                            selections : Gikam.getComp('emc-order-retain-choose-list-grid').getSelections()
                        }
                        if (Gikam.getComp('emc-order-retain-choose-base-info-form').validate()) {
                            if (Gikam.isEmpty(data.selections)) {
                                Gikam.alert('EMC.MODULE.BP.ORDER.ORDERRETAINS.TIP.CHOOSE_AT_LEAST_ONE_ORDER');
                                return;
                            }
                            Gikam.getLastModal().close(data);
                        }
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
            resize : false,
            north : {
                height : 99,
                items : [_this.getBtnToolbarParam(), _this.getBaseFormParam(), ]
            },
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
