coreCustomSurvey.auditPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'T_CORE_CUSTOM_SURVEY_SUM.SERVICE_NAME',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '40%',
                        items : [ _this.getSurveySumGrid() ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            panels : [ {
                                title : 'CORE.MODULE.QMS.CUSTOMSURVEYS.TAB.SURVEY_SUM',
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '50%',
                                        items : [ _this.getSurveySumRetGrid() ]
                                    },
                                    center : {
                                        items : [ _this.getCustomSurveySumCommentInfo() ]
                                    }
                                } ]
                            } ]
                        } ]
                    }
                } ]
            } ]
        }
    },
    
    getSurveySumGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-survey-audit-list-survey-sum-grid',
            service : 'coreCustomSurveySumServiceImpl',
            url : coreCustomSurvey.baseUrl + '/survey-sums/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreCustomSurvey.getSurveySumListColumns(false),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-custom-survey-audit-list-survey-sum-ret-grid', 'core-custom-survey-audit-list-sum-info-form' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-custom-survey-audit-list-sum-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-sums/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        var grid = Gikam.getComp('core-custom-survey-audit-list-survey-sum-grid');
                        var statusCode = _this.getStatusCode(grid.getActivedRow().id)
                        if (statusCode == 'techAudit') {
                            form.toFieldsEdit(['technicalAdvice']);
                            form.toFieldsReadonly(['qualityAdvice']);
                        } else if (statusCode == 'quaAudit'){
                            form.toFieldsReadonly(['technicalAdvice']);
                            form.toFieldsEdit(['qualityAdvice']);
                        } else {
                            form.toFieldsReadonly(['technicalAdvice', 'qualityAdvice']);
                        }
                    });
                });
                Gikam.getAsyncComp('core-custom-survey-audit-list-survey-sum-ret-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-sum-rets/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-audit-list-survey-sum-grid');
                    Gikam.create('workflow').pass({
                        data : grid.getSelections(),
                        pageObject : coreCustomSurvey
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-audit-list-survey-sum-grid');
                    Gikam.create('workflow').reject({
                        data : grid.getSelections(),
                        pageObject : coreCustomSurvey
                    }).done(function() {
                        grid.refresh();
                    });
                }
            } ]
        };
    },
    
    getSurveySumRetGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-audit-list-survey-sum-ret-grid',
            service : 'coreCustomSurveySumRetServiceImpl',
            page : false,
            columns : coreCustomSurvey.getSurveySumRetListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-audit-list-survey-sum-grid');
                    if (!Gikam.isEmpty(grid.getActivedRow().id)) {
                        Gikam.viewReport('QMS_CUSTOM_SURVEY', {
                            id : grid.getActivedRow().id
                        });
                    }
                }
            } ]
        };
    },

    getCustomSurveySumCommentInfo : function() {
        return {
            type : 'form',
            service : 'coreCustomSurveySumServiceImpl',
            id : 'core-custom-survey-audit-list-sum-info-form',
            fields : coreCustomSurvey.getSurveySumFormFields(),
            columns : 1
        }
    },

    getCustomSurveyAttachment : function() {
        return {
            type : 'uploader',
            id : 'qms-custom-survey-audit-list-uploader',
            dbTable : 'T_CORE_CUSTOM_SURVEY',
            bizId : 0
        }
    },

    getStatusCode : function(id){
        var statusCode, processStatus;
        var _this = this;
        var url = coreCustomSurvey.baseUrl + '/{id}/info/status';
        workspace.window.showMask(true);
        Gikam.getTextSync(Gikam.printf(url, {
            id : id
        })).done(function(item) {
            var statusDto = Gikam.parseJson(item);
            statusCode = statusDto.auditStatus.statusCode;
        }).always(function() {
            workspace.window.closeMask();
        });
        return statusCode;
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-survey-audit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
