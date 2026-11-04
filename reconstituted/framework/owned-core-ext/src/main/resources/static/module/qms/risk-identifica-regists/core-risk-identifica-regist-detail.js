coreRiskIdentificaRegist.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                id : 'core-risk-identifica-regist-detail-form-back-button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.RISK_IDENTIFICA_REGIST.BUTTON.INSERT',
                id : 'core-risk-identifica-regist-detail-form-insert-button',
                icon : 'add',
                onClick : function() {
                    workspace.window.showMask(true);
                    Gikam.post(coreRiskIdentificaRegist.baseUrl, Gikam.getJsonWrapper({}, [ '', [ {} ] ])).done(function(id) {
                        Gikam.getComp('core-risk-identifica-regist-detail-regist-form').refresh({
                            url : Gikam.printf(coreRiskIdentificaRegist.baseUrl + '/{id}', {
                                id : id
                            })
                        });
                        Gikam.getComp('core-risk-identifica-regist-detail-identifica-factor-grid').refresh({
                            url : Gikam.printf(coreRiskIdentificaRegist.baseUrl + '/{id}/risk-identifica-factors/queries', {
                                id : id
                            })
                        });
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                id : 'core-risk-identifica-regist-detail-form-remove-button',
                icon : 'remove',
                onClick : function() {
                    var formData = Gikam.getComp('core-risk-identifica-regist-detail-regist-form').getData();
                    workspace.window.showMask(true);
                    Gikam.del(coreRiskIdentificaRegist.baseUrl, Gikam.getJsonWrapper({}, [ '', [ formData ] ])).done(function(items) {
                        workspace.window.goBack();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }, {
                type : 'button',
                id : 'core-risk-identifica-regist-detail-form-submit-button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    if (Gikam.getComp('core-risk-identifica-regist-detail-regist-form').validate()) {
                        var data = Gikam.getComp('core-risk-identifica-regist-detail-identifica-factor-grid').getData();
                        if (data.length == 0) {
                            Gikam.alert('T_CORE_RISK_IDENTIFICA_REGIST.ALERT.ATLEASTONECHOOSE');
                            return;
                        }
                        _this.submit();
                    }
                }
            } ]
        }
    },

    // 表单
    getBaseInfoForm : function() {
        var _this = this;
        return {
            id : 'core-risk-identifica-regist-detail-regist-form',
            type : 'form',
            columns : 3,
            autoSave : true,
            service : 'coreRiskIdentificaRegistServiceImpl',
            url : _this.getCompUrl('baseInfoForm'),
            fields : coreRiskIdentificaRegist.getBaseInfoFormFields(),
        }
    },

    getRiskIdentificaFactorGrid : function() {
        var _this = this;
        return {
            id : 'core-risk-identifica-regist-detail-identifica-factor-grid',
            type : 'grid',
            columns : coreRiskIdentificaRegist.getRiskIdentificaFactorListColumns(),
            url : _this.getCompUrl('riskIdentificaFactorGrid'),
            deleteFormatter : function(row) {
                return row.riskDesc || row.id;
            },
            page : false,
            toolbar : [ {
                type : 'button',
                id : 'core-risk-identifica-regist-detail-factor-grid-add-button',
                text : 'CORE.MODULE.QMS.RISK_IDENTIFICA_REGIST.BUTTON.ADD_RISK_FACTOR',
                icon : 'add-row',
                onClick : function() {
                    _this.addRiskIdentificaFactor();
                }
            }, {
                type : 'button',
                id : 'core-risk-identifica-regist-detail-factor-grid-remove-button',
                text : 'CORE.MODULE.QMS.RISK_IDENTIFICA_REGIST.BUTTON.DELETE_RISK_FACTOR',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-risk-identifica-regist-detail-identifica-factor-grid').deleteRows(coreRiskIdentificaRegist.baseUrl + '/risk-identifica-factors');
                }
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreRiskIdentificaRegist.baseUrl + '/{id}',
        riskIdentificaFactorGrid : coreRiskIdentificaRegist.baseUrl + '/{id}/risk-identifica-factors/queries',
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    submit : function() {
        var form = Gikam.getComp('core-risk-identifica-regist-detail-regist-form');
        Gikam.create('workflow').submit({
            pageObject : coreRiskIdentificaRegist,
            data : [ form.getData() ]
        }).done(function() {
            workspace.window.goBack();
        });
    },

    addRiskIdentificaFactor : function() {
        var _this = this;

        var insertUrl = Gikam.printf(coreRiskIdentificaRegist.baseUrl + '/{id}/risk-identifica-factors', {
            id : _this.param.id
        });

        Gikam.create('modal', {
            title : Gikam.choose.getConfig('risk-factor').title,
            url : '/secure/core/module/qms/risk-factors/page/choose-list',
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = new Array();
                Gikam.each(rows, function(index, row) {
                    if (Gikam.isEmpty(row.children)) {
                        chooseData.push({
                            riskFactorId : row.id,
                            riskDesc : row.riskFactor
                        });
                    }
                })
                Gikam.getComp('core-risk-identifica-regist-detail-identifica-factor-grid').insert(insertUrl, chooseData);
            }
        });
    },

    createPage : function() {
        var _this = this;
        var pageConfig = {
            bizId : _this.param.id,
            pageObject : coreRiskIdentificaRegist,
            config : _this.pageConfig,
            prior : _this.param.sourcePage
        }
        Gikam.create('layout', {
            north : {
                height : '20%',
                items : [ _this.getBtnToolbar(), _this.getBaseInfoForm() ]
            },
            center : {
                items : [ _this.getRiskIdentificaFactorGrid() ]
            },
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }

}