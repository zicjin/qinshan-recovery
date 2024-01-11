emcRevisionRule.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcRevisionRule.getChooseListColumns();
        columns.unshift({
            radio : true
        });
        return {
            type : 'simpleGrid',
            id : 'emc-revision-rule-choose-list-grid',
            filterOpen : true,
            url : emcRevisionRule.baseUrl + '/queries/choosable',
            columns : columns,
            checkOnActive : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var rows = Gikam.getComp('emc-revision-rule-choose-list-grid').getSelections();
                    if (rows && rows.length) {
                        if (rows[0].id != 'NONE'){
                            var digit = rows[0].effectiveDigit;
                            if (digit != null && digit != '' && digit != undefined) {
                                var reg = /^(0|[1-9][0-9]*)$/;
                                if (reg.test(digit)) {
                                    Gikam.getLastModal().close(rows[0].id + ',' + digit);
                                } else {
                                    Gikam.alert("EMC.MODULE.MDM.BASEMDM.REVISION_RULES.TIP.ENTER_NUMBER")
                                }
                            } else {
                                Gikam.alert("EMC.MODULE.MDM.BASEMDM.REVISION_RULES.TIP.NO_VALUE")
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
                        var rows = Gikam.getComp('emc-revision-rule-choose-list-grid').getSelections();
                        if (rows && rows.length) {
                            if (rows[0].id != 'NONE'){
                                var digit = rows[0].effectiveDigit;
                                if (digit != null && digit != '' && digit != undefined) {
                                    var reg = /^(0|[1-9][0-9]*)$/;
                                    if (reg.test(digit)) {
                                        Gikam.getLastModal().close(rows[0].id + ',' + digit);
                                    } else {
                                        Gikam.alert("EMC.MODULE.MDM.BASEMDM.REVISION_RULES.TIP.ENTER_NUMBER")
                                    }
                                } else {
                                    Gikam.alert("EMC.MODULE.MDM.BASEMDM.REVISION_RULES.TIP.NO_VALUE")
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