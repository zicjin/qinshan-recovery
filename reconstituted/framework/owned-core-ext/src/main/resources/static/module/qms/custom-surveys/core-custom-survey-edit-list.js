coreCustomSurvey.editPage = {

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
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-survey-edit-list-grid',
            url : coreCustomSurvey.baseUrl + '/queries',
            columns : coreCustomSurvey.getCustomSurveyEditListColumns(true),
            service : 'coreCustomSurveyServiceImpl',
            deleteFormatter : function(row) {
                return row.customerName;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-custom-survey-edit-list-survey-item-grid', 'core-custom-survey-edit-list-survey-info-form' ]);
                    Gikam.getComp('qms-custom-survey-edit-list-uploader').grid.cleanData();
                }
            },
            onBeforeUpdate  : function(row, keys) {
                if (row.row.processStatus == 'done') {
                    Gikam.alert('CORE.MODULE.QMS.CUSTOMSURVEYS.TIP.CAN_NOT_MODIFY_COMPLETE_CUSTOM_SURVEY');
                    return false;
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-custom-survey-edit-list-survey-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}', {
                            id : row.id
                        })
                    }).done(function() {
                        if (Gikam.getComp('core-custom-survey-edit-list-grid').getActivedRow().processStatus == 'done') {
                            form.toFieldsReadonly(['customerAdvice']);
                            Gikam.getComp('core-custom-survey-edit-list-grid').toFieldsReadonly(index, ['folderNo']);
                        } else {
                            form.toFieldsEdit(['customerAdvice']);
                            Gikam.getComp('core-custom-survey-edit-list-grid').toFieldsEdit(index, ['folderNo']);
                        }
                    });
                });
                Gikam.getAsyncComp('core-custom-survey-edit-list-survey-item-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-items/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        if (Gikam.getComp('core-custom-survey-edit-list-grid').getActivedRow().processStatus == 'done') {
                            Gikam.each(grid.getData(), function(index, row) {
                                grid.toFieldsReadonly(index, ['surveyContent', 'evaluate']);
                            });
                        };
                    });
                });
                Gikam.getAsyncComp('qms-custom-survey-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.QMS.CUSTOMSURVEYS.MODAL.ADD',
                        fields : coreCustomSurvey.getCustomSurveyInsertFields(),
                        url : coreCustomSurvey.baseUrl,
                        disableNotEmptyValidate : true
                    }).done(function(id) {
                        Gikam.getComp('core-custom-survey-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-edit-list-grid');
                    var hasDone = false;
                    Gikam.each(grid.getSelections(), function(index, row) {
                        if (row.processStatus == 'done') 
                            hasDone = true;
                    })
                    if (hasDone) {
                        Gikam.alert('CORE.MODULE.QMS.CUSTOMSURVEYS.TIP.CAN_NOT_DELETE_COMPLETE_CUSTOM_SURVEY');
                        return;
                    }
                    Gikam.getComp('core-custom-survey-edit-list-grid').deleteRows(coreCustomSurvey.baseUrl);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.CUSTOMSURVEYS.BUTTON.COMPLETE',
                icon : 'default',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-edit-list-grid');
                    if (grid.getSelections() < 1) {
                        Gikam.alert('CORE.MODULE.QMS.CUSTOMSURVEYS.TIP.AT_LEAST_SELECT_ONE');
                        return;
                    }
                    var hasDone = false;
                    Gikam.each(grid.getSelections(), function(index, row) {
                        if (row.processStatus == 'done') 
                            hasDone = true;
                    })
                    if (hasDone) {
                        Gikam.alert('CORE.MODULE.QMS.CUSTOMSURVEYS.TIP.SELECT_NEW_TO_COMPLETE');
                        return;
                    }
                    workspace.window.showMask(true);
                    Gikam.put(coreCustomSurvey.baseUrl + '/action/complete', Gikam.getJsonWrapper({}, [ '', grid.getSelections() ])).done(function(item) {
                        grid.refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            } ]
        }
    },
    
    getSurveyItemGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-edit-list-survey-item-grid',
            service : 'coreCustomSurveyItemServiceImpl',
            page : false,
            columns : coreCustomSurvey.getSurveyItemListColumns(true)
        };
    },

    getCustomSurveyCommentInfo : function() {
        return {
            type : 'form',
            service : 'coreCustomSurveyServiceImpl',
            id : 'core-custom-survey-edit-list-survey-info-form',
            fields : coreCustomSurvey.getCustomSurveyFormFields(),
            columns : 1
        }
    },

    getSurveySumGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-survey-edit-list-survey-sum-grid',
            service : 'coreCustomSurveySumServiceImpl',
            url : coreCustomSurvey.baseUrl + '/survey-sums/queries',
            requestData : {
                processStatus_SB : 'draft'
            },
            columns : coreCustomSurvey.getSurveySumListColumns(true),
            deleteFormatter : function(row) {
                return row.statisNo;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'core-custom-survey-edit-list-survey-sum-ret-grid', 'core-custom-survey-edit-list-sum-info-form' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('core-custom-survey-edit-list-sum-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(coreCustomSurvey.baseUrl + '/{id}/survey-sums/queries', {
                            id : row.id
                        })
                    }).done(function() {
                        var status = Gikam.getComp('core-custom-survey-edit-list-survey-sum-grid').getActivedRow().processStatus;
                        if (status.indexOf('draft') == 0) {
                            form.toFieldsReadonly(['technicalAdvice', 'qualityAdvice']);
                        } else {
                            form.toFieldsEdit(['technicalAdvice', 'qualityAdvice']);
                        }
                    });
                });
                Gikam.getAsyncComp('core-custom-survey-edit-list-survey-sum-ret-grid').done(function(grid) {
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
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    _this.insertSum();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-custom-survey-edit-list-survey-sum-grid').deleteRows(Gikam.printf(coreCustomSurvey.baseUrl + '/survey-sums'));
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-edit-list-survey-sum-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : coreCustomSurvey
                    }).done(function() {
                        grid.refresh();
                    });
                }
            } ]
        };
    },

    insertSum : function() {
        var modal = Gikam.create('modal', {
            title: 'CORE.MODULE.QMS.CUSTOMSURVEYS.MODAL.ADD_CUSTOM_SURVEY_SUM',
            width: 400,
            height: 350
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-custom-survey-sum-insert-form',
                    columns : 1,
                    fields : coreCustomSurvey.getSumInsertFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-custom-survey-sum-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var insertData = form.getData();
                            workspace.window.showMask(true);
                            Gikam.post(coreCustomSurvey.baseUrl + '/survey-sums', Gikam.getJsonWrapper({}, ['', [ insertData ]])).done(function(item) {
                                if (item == -1) {
                                    Gikam.alert('CORE.MODULE.QMS.CUSTOM_SURVEY.TIP.ENABLE_FIND_CUSTOM_SURVEY');
                                    return;
                                }
                                modal.close();
                                Gikam.getComp('core-custom-survey-edit-list-survey-sum-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    getSurveySumRetGrid : function() {
        return {
            type : 'grid',
            id : 'core-custom-survey-edit-list-survey-sum-ret-grid',
            service : 'coreCustomSurveySumRetServiceImpl',
            page : false,
            columns : coreCustomSurvey.getSurveySumRetListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var grid = Gikam.getComp('core-custom-survey-edit-list-survey-sum-grid');
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
            id : 'core-custom-survey-edit-list-sum-info-form',
            fields : coreCustomSurvey.getSurveySumFormFields(),
            columns : 1
        }
    },

    getCustomSurveyAttachment : function() {
        return {
            type : 'uploader',
            id : 'qms-custom-survey-edit-list-uploader',
            dbTable : 'T_CORE_CUSTOM_SURVEY',
            bizId : 0
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-survey-edit-list-layout',
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
