coreCustomSurvey.searchPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'T_CORE_CUSTOM_SURVEY.SERVICE_NAME',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '40%',
                        items : [ _this.getSurveyGrid() ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            panels : [ {
                                title : 'CORE.MODULE.QMS.CUSTOMSURVEYS.TAB.SURVEY_ITEM',
                                items : [ {
                                    type : 'layout',
                                    north : {
                                        height : '40%',
                                        items : [ _this.getSurveyItemGrid() ]
                                    },
                                    center : {
                                        height : '20%',
                                        items : [ _this.getCustomSurveyCommentInfo() ]
                                    },
                                    south : {
                                        height : '40%',
                                        items : [ _this.getCustomSurveyAttachment() ]
                                    }
                                } ]
                            } ]
                        } ]
                    }
                } ]
            }, {
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

    getSurveyGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-search-list-grid',
            url : coreCustomSurvey.baseUrl + '/queries',
            columns : coreCustomSurvey.getCustomSurveyEditListColumns(false),
            service : 'coreCustomSurveyServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-custom-survey-search-list-survey-item-grid', 'core-custom-survey-search-list-survey-info-form' ]);
                    Gikam.getComp('qms-custom-survey-search-list-uploader').grid.cleanData();
                }
            },
            onBeforeUpdate  : function(row, keys) {
                if (row.row.processStatus == 'done') {
                    Gikam.alert('CORE.MODULE.QMS.CUSTOMSURVEYS.TIP.CAN_NOT_MODIFY_COMPLETE_CUSTOM_SURVEY');
                    return false;
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-custom-survey-search-list-survey-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }).done(function() {
                        form.toFieldsReadonly(['customerAdvice']);
                    });
                });
                Gikam.getAsyncComp('core-custom-survey-search-list-survey-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-items/queries', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('qms-custom-survey-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            }
        }
    },
    
    getSurveyItemGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-search-list-survey-item-grid',
            service : 'coreCustomSurveyItemServiceImpl',
            page : false,
            columns : coreCustomSurvey.getSurveyItemListColumns(false)
        };
    },

    getCustomSurveyCommentInfo : function() {
        return {
            type : 'form',
            service : 'coreCustomSurveyServiceImpl',
            id : 'core-custom-survey-search-list-survey-info-form',
            fields : coreCustomSurvey.getCustomSurveyFormFields(),
            columns : 1
        }
    },

    getSurveySumGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-search-list-survey-sum-grid',
            service : 'coreCustomSurveySumServiceImpl',
            url : coreCustomSurvey.baseUrl + '/survey-sums/queries',
            requestData : {
                processStatus_SPECIAL : 'approve_or_done'
            },
            columns : coreCustomSurvey.getSurveySumListColumns(false),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-custom-survey-search-list-survey-sum-ret-grid', 'core-custom-survey-search-list-sum-info-form' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-custom-survey-search-list-sum-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-sums/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        form.toFieldsReadonly(['technicalAdvice', 'qualityAdvice']);
                    });
                });
                Gikam.getAsyncComp('core-custom-survey-search-list-survey-sum-ret-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-sum-rets/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        
                    });
                });
            }
        };
    },
    
    getSurveySumRetGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-search-list-survey-sum-ret-grid',
            service : 'coreCustomSurveySumRetServiceImpl',
            page : false,
            columns : coreCustomSurvey.getSurveySumRetListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-search-list-survey-sum-grid');
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
            id : 'core-custom-survey-search-list-sum-info-form',
            fields : coreCustomSurvey.getSurveySumFormFields(),
            columns : 1
        }
    },

    getCustomSurveyAttachment : function() {
        return {
            type : 'uploader',
            id : 'qms-custom-survey-search-list-uploader',
            dbTable : 'T_CORE_CUSTOM_SURVEY',
            bizId : 0,
            readonly : true
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-survey-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
