coreRoundingRule.choosePage = {
    getMenuGroupGridParam : function() {
        var _this = this, columns = coreRoundingRule.getChooseListColumns(), requestData = this.param;
        columns.unshift({
            radio : true
        });
        return {
            type : 'grid',
            id : 'core-rounding-rule-choose-list-grid',
            url : coreRoundingRule.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            order : false,
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var rows = Gikam.getComp('core-rounding-rule-choose-list-grid').getSelections();
                    if (rows && rows.length) {
                        if (rows[0].id != 'NONE'){
                            var digit = rows[0].param;
                            if (digit != null && digit != '' && digit != undefined) {
                                var reg = /^(0|[1-9][0-9]*)$/;
                                if (reg.test(digit)) {
                                    Gikam.getLastModal().close(rows[0].id + ',' + digit);
                                } else {
                                    Gikam.alert("CORE.MODULE.SYS.ROUNDING_RULES.TIP.ENTER_NUMBER")
                                }
                            } else {
                                Gikam.alert("CORE.MODULE.SYS.ROUNDING_RULES.TIP.NO_VALUE")
                            }
                        } else {
                            Gikam.getLastModal().close('无修约规则');
                        }
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
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getMenuGroupGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};