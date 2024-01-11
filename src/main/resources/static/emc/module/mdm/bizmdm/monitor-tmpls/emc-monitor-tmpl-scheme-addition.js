emcMonitorTmpl.schemeAdditionPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        var fields = emcMonitorTmpl.getSchemeFormFields();
        if (Gikam.isEmpty(_this.param.schemeName)) {
            fields.shift();
        }

        return {
            type : 'form',
            id : 'emc-monitor-tmpl-scheme-addition-form',
            columns : 2,
            fields : fields,
            onRendered : function() {
                _this.schemeForm = this;
                this.setData(_this.param);
                if (!Gikam.isEmpty(_this.param.schemeName)) {
                    this.toFieldsReadonly('schemeName');
                }
            }
        }
    },

    getConfirmBtnItem : function() {
        var _this = this;
        return {
            type: 'button',
            text: 'GIKAM.BUTTON.CONFIRM',
            icon : 'select',
            margin : '13px 0 0 15px',
            onClick : function () {
                _this.save();
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;

        var fields = emcMonitorTmpl.getSchemeFormFields();

        if (Gikam.isEmpty(_this.param.schemeName)) {
            fields.shift();
        }

        return {
            type : 'form',
            id : 'emc-monitor-tmpl-scheme-addition-form',
            columns : 1,
            fields : fields,
            onRendered : function() {
                _this.schemeForm = this;
                this.setData(_this.param);
                if (!Gikam.isEmpty(_this.param.schemeName)) {
                    this.toFieldsReadonly('schemeName');
                }
            }
        }
    },

    save : function() {
        var _this = this;
        var formData = Gikam.getComp('emc-monitor-tmpl-scheme-addition-form').getData();
        var rows = Gikam.getComp('emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame').getSelectList();
        if (!_this.schemeForm.validate()) {
            return;
        }
        if (!rows.length) {
            Gikam.alert('EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.TIP.CHOOSE_TEST');
            return;
        }

        var chooseData = rows.map(function(itemGroup) {
            return {
                testMethodId : itemGroup.testMethodId,
                testId : itemGroup.testId,
                methodId : itemGroup.ext$.methodid,
                itemGroupId : itemGroup.id,
                itemGroup : itemGroup.itemGroup,
                schemeName : formData.schemeName,
                monitorTmplId : _this.param.monitorTmplId
            }
        });

        Gikam.getLastModal().window.showMask(true);
        Gikam.postText(Gikam.printf(emcMonitorTmpl.baseUrl + '/{id}/schemes', {
            id : _this.param.monitorTmplId
        }), Gikam.getJsonWrapper(null, [ '', chooseData ])).done(function(id) {
            var scheme = {
                schemeId : id,
                monitorTmplId :  _this.param.monitorTmplId,
                schemeName : formData.schemeName
            }
            Gikam.getLastModal().close(scheme);
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });

    },

    getShuttleFrameParam : function() {
        var _this = this;

        var leftRequestData = {};
        if (Gikam.isEmpty(_this.param.schemeName)) {
            leftRequestData = {
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_MONITOR_SCHEME",
                    targetMatchColumn : "ITEMGROUPID",
                    filter : [ {
                        targetFilterColumn : 'schemeName',
                        targetFilterValue : 'default'
                    }, {
                        targetFilterColumn : 'monitorTmplId',
                        targetFilterValue : _this.param.monitorTmplId
                    } ]
                })
            };
        }
        var leftGridUrl = '/secure/emc/module/mdm/bizmdm/tests/item-groups/queries/choosable';
        if (!Gikam.isEmpty(_this.param.schemeType)&&'testCapacity'===_this.param.schemeType) {
            leftGridUrl = '/secure/emc/module/mdm/bizmdm/test/test-capacitys/item-groups/queries/choosable';
            leftRequestData.orgId_SB = workspace.user.ext$.parentorgid;
        }
        return {
            type : 'shuttleFrame',
            id : 'emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame',
            columns : emcMonitorTmpl.getItemGroupShuttleFrameColumns(),
            leftRequestData : leftRequestData,
            leftGridUrl : leftGridUrl,
            page : true,
            onRendered : function() {
                _this.shuttleFrame = this;
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            north : {
                height : 54,
                items : [ {
                    type : 'layout',
                    east : {
                        width : 150,
                        items : [ _this.getConfirmBtnItem() ]
                    },
                    center : {
                        items : [ _this.getBaseInfoForm() ]
                    }
                } ]
            },
            center : {
                items : [ _this.getShuttleFrameParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

}
