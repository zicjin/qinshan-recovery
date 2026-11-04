coreCustomSrtSurvey.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-srt-survey-audit-list-grid',
            url : coreCustomSrtSurvey.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreCustomSrtSurvey.getListColumns(),
            onCellClick : function(field, row) {
                _this.compRefresh(row.id);
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }]
        }
    },

    compRefresh : function(id) {
        var _this = this;
        var comps = _this.compManager();
        var itemBaseUrl = coreCustomSrtSurvey.baseUrl + '/{id}/srt-survey-items/queries';
        comps.basicForm.refresh({
            url : Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}', {
                id : id
            })
        }, true);
        comps.auditForm.refresh({
            url : Gikam.printf(coreCustomSrtSurvey.baseUrl + '/{id}', {
                id : id
            })
        }, true);
        comps.itemGrid.refresh({
            url : Gikam.printf(itemBaseUrl, {
                id : id
            })
        }).done(function() {
            comps.auditForm.toFieldsEdit(['remark']);
            switch(_this.param.bpmn_statusCode) {
                case 'execute': {
                    var length = comps.itemGrid.getData().length;
                    for(var i = 0; i < length; i++) {
                        comps.itemGrid.toFieldsEdit(i, ['result', 'remark']);
                    }
                    break;
                }
                case 'auditOrgAdvice': {
                    comps.auditForm.toFieldsEdit(['beAuditOrgAdvice']);
                    break;
                }
                case 'qsOrgAdvice': {
                    comps.auditForm.toFieldsEdit(['qsOrgAdvice']);
                    break;
                }
                case 'qmAdvice': {
                    comps.auditForm.toFieldsEdit(['qmAdvice']);
                    break;
                }
                default:
                    break;
            }
        });
        Gikam.getAsyncComp('core-custom-srt-surveys-attachment-panel-uploader').done(function(uploader) {
            uploader.setOptions({
                bizId : id
            });
        });
    },

    compClean : function() {
        var comps = this.compManager();
        comps.basicForm.cleanData();
        comps.auditForm.cleanData();
        comps.itemGrid.cleanData();
        comps.auditForm.toFieldsReadonly(['beAuditOrgAdvice', 'qsOrgAdvice', 'qmAdvice', 'remark']);
    },

    compManager : function() {
        return {
            basicForm : Gikam.getComp('core-custom-srt-survey-basic-form'),
            auditForm : Gikam.getComp('core-custom-srt-survey-audit-form'),
            itemGrid : Gikam.getComp('core-custom-srt-survey-item-list-grid'),
            surveyGrid : Gikam.getComp('core-custom-srt-survey-audit-list-grid')
        }
    },

    submit : function() {
        var _this = this;
        var comps = this.compManager();
        Gikam.create('workflow').pass({
            data : comps.surveyGrid.getSelections(),
            pageObject : coreCustomSrtSurvey
        }).done(function() {
            comps.surveyGrid.refresh();
            _this.compClean();
            _this.isEnable = false;
        });
    },

    reject : function() {
        var _this = this;
        var comps = _this.compManager();
        Gikam.create('workflow').reject({
            data : comps.surveyGrid.getSelections(),
            pageObject : coreCustomSrtSurvey
        }).done(function() {
            comps.surveyGrid.refresh();
            _this.compClean();
            _this.isEnable = false;
        });
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreCustomSrtSurvey,
            config : coreCustomSrtSurvey.pageConfig,
            prior : _this.param.bpmn_statusCode
        };
        Gikam.create('layout', {
            id : 'core-custom-srt-survey-audit-list-layout',
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig,
            west : {
                width : '50%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [coreCustomSrtSurvey.getBtnToolbar('core-custom-srt-survey-audit-list-grid'),coreCustomSrtSurvey.getDetailTab()]
            },
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
