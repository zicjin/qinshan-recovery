Gikam.extend(coreRoundingRule.choosePage, {
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
            checkOnActive : false,
            onRowActive  : function(index) {
                this.selectRowByIndex(index);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var rows = Gikam.getComp('core-rounding-rule-choose-list-grid').getSelections();
                    if (rows && rows.length) {
                        if (rows[0].id !== 'NONE'){
                            var digit = rows[0].param;
                            if (digit != null && digit !== '' && digit !== undefined) {
                                var reg = /^(0|[1-9][0-9]*)$/;
                                var tip = "CORE.MODULE.SYS.ROUNDING_RULES.TIP.ENTER_NUMBER";
                                if (rows[0].id === 'EPA' || rows[0].id === 'EMC') {
                                    reg = /^([1-9][0-9]*)$/;
                                    tip = "CORE.MODULE.SYS.ROUNDING_RULES.TIP.ENTER_POSITIVE_NUMBER";
                                }

                                if (reg.test(digit)) {
                                    Gikam.getLastModal().close(rows[0].id + ',' + digit);
                                } else {
                                    Gikam.alert(tip)
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
    }
});
