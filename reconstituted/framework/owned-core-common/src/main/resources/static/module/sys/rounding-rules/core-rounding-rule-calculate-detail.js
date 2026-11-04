coreRoundingRule.calculateDetailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.ROUNDING_RULES.TITLE.TEST_ROUNDING_RULES',
                icon : 'preview',
                onClick : function() {
                    _this.calculate();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.SYS.ROUNDING_RULES.TITLE.TEST_ROUNDING_RULES',
                items : [ {
                    type : 'form',
                    id : 'core-rounding-rule-test-detail-base-info-form',
                    fields : coreRoundingRule.getCalculateFields(),
                    columns : 1,
                    titleWidth : 130,
                    autoSave : false,
                    onRendered : function() {
                        this.setData({
                            id : _this.param.id
                        });
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        return this;
    },

    init : function(param) {
        this.param = param;

        this.create();
    },

    calculate : function() {
        var form = Gikam.getComp('core-rounding-rule-test-detail-base-info-form');
        if (form.validate()) {
            var formData = form.getData();
            var digit = formData.param;
            var originalResult = formData.originalResult;
            var roundingScheme = null;
            if (digit != null && digit != '' && digit != undefined) {
                var reg = /^(0|[1-9][0-9]*)$/;
                if (reg.test(digit)) {
                    roundingScheme = formData.id + ',' + digit;
                } else {
                    Gikam.alert("CORE.MODULE.SYS.ROUNDING_RULES.TIP.ENTER_NUMBER");
                    return;
                }
            }
            formData.param = roundingScheme;
            Gikam.post(coreRoundingRule.baseUrl + '/action/calculate', Gikam.getJsonWrapper(null, [ '', [ formData ] ])).done(function(data) {
                Gikam.getComp('core-rounding-rule-test-detail-base-info-form').setData({
                    revisionResult : data.revisionResult
                });
            });
        }
    }
};