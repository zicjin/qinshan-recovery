coreRoundingRule.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.SYS.ROUNDING_RULES.BUTTON.LOCK',
                icon : 'pass',
                onClick : function() {
                    var formData = Gikam.getComp('core-rounding-rule-detail-base-info-form').getData();
                    if (formData.locked === '1') {
                        Gikam.alert('CORE.MODULE.SYS.ROUNDING_RULES.TIP.LOCKED');
                        return;
                    }
                    formData.locked = '1';
                    workspace.window.showMask(true);
                    Gikam.put(Gikam.printf(coreRoundingRule.baseUrl + '/{id}', {
                        id : formData.id
                    }), Gikam.getJsonWrapper(null, [ '', [ formData ] ])).done(function() {
                        Gikam.getComp('core-rounding-rule-detail-base-info-form').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.ROUNDING_RULES.BUTTON.UNLOCK',
                icon : 'reject',
                onClick : function() {
                    var formData = Gikam.getComp('core-rounding-rule-detail-base-info-form').getData();
                    if (formData.locked === '0') {
                        Gikam.alert('CORE.MODULE.SYS.ROUNDING_RULES.TIP.UNLOCKED');
                        return;
                    }
                    formData.locked = '0';
                    workspace.window.showMask(true);
                    Gikam.put(Gikam.printf(coreRoundingRule.baseUrl + '/{id}', {
                        id : formData.id
                    }), Gikam.getJsonWrapper(null, [ '', [ formData ] ])).done(function() {
                        Gikam.getComp('core-rounding-rule-detail-base-info-form').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.SYS.ROUNDING_RULES.BUTTON.TEST',
                icon : 'review',
                onClick : function() {
                    _this.testRoundingRule();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_ROUNDING_RULE', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.SYS.ROUNDING_RULES.TAB.BASEINFO',
                items : [ {
                    type : 'form',
                    id : 'core-rounding-rule-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreRoundingRuleServiceImpl',
                    fields : coreRoundingRule.getBaseInfoFormFields(),
                    onLoadSuccess : function(data) {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'id' ]);
                        }

                        if (data.locked === '1') {
                            this.toFieldsReadonly([ 'script' ]);
                        } else {
                            this.toFieldsEdit([ 'script' ]);
                        }
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
            renderTo : workspace.window.$dom
        });

        return this;
    },

    urlManager : {
        baseInfoForm : coreRoundingRule.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-rounding-rule-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    },

    testRoundingRule : function() {
        var formData = Gikam.getComp('core-rounding-rule-detail-base-info-form').getData();
        Gikam.create('modal', {
            width : 500,
            height : 400,
            title : 'CORE.MODULE.SYS.ROUNDING_RULES.TITLE.TEST_ROUNDING_RULES',
            url : coreRoundingRule.baseUrl + '/page/calculate-detail' + Gikam.param({
                id : formData.id
            }),
        });
    }
};