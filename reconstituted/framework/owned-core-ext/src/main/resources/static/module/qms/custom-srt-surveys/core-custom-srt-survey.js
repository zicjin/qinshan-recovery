var coreCustomSrtSurvey = Gikam.getPageObject({

    getListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreCustomSrtSurvey.workflow.dbTable), {
            field : 'surveyNo',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.SURVEYNO'
        }, {
            field : 'createdByName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.CREATEDTIME'
        } ];
    },

    getSrtSurveyItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'surveyContent',
            title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.SURVEYCONTENT',
            editor : true
        }, {
            field : 'result',
            title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.RESULT',
            editor : true
        }, {
            field : 'remark',
            title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.REMARK',
            editor : true
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreCustomSrtSurvey.workflow.dbTable), {
            field : 'id',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.ID',
            type : 'link'
        }, {
            field : 'surveyNo',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.SURVEYNO'
        }, {
            field : 'auditUserId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERID'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERNAME'
        }, {
            field : 'auditDate',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITDATE'
        }, {
            field : 'qsOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QSORGADVICE'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QMADVICE'
        }, {
            field : 'remark',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.REMARK'
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGID'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGNAME'
        }, {
            field : 'beAuditOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGADVICE'
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(coreCustomSrtSurvey.workflow.dbTable), {
            field : 'id',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.ID',
            type : 'link'
        }, {
            field : 'surveyNo',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.SURVEYNO'
        }, {
            field : 'auditUserId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERID'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERNAME'
        }, {
            field : 'auditDate',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITDATE'
        }, {
            field : 'qsOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QSORGADVICE'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QMADVICE'
        }, {
            field : 'remark',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.REMARK'
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGID'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGNAME'
        }, {
            field : 'beAuditOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGADVICE'
        } ];
    },

    getChooseListColumns : function() {
        var columns = [ {
            field : 'id',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.ID'
        }, {
            field : 'surveyNo',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.SURVEYNO'
        }, {
            field : 'auditUserId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERID'
        }, {
            field : 'auditUserName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERNAME'
        }, {
            field : 'auditDate',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITDATE'
        }, {
            field : 'qsOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QSORGADVICE'
        }, {
            field : 'qmAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.QMADVICE'
        }, {
            field : 'remark',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.REMARK'
        }, {
            field : 'beAuditOrgId',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGID'
        }, {
            field : 'beAuditOrgName',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGNAME'
        }, {
            field : 'beAuditOrgAdvice',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGADVICE'
        } ];
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    getBasicInfoPanel : function() {
        var _this = this;
        return {
            title : 'CUSTOMSRTSURVEYS.TAB.BASE_INFO',
            id : 'core-custom-srt-survey-basic-panel',
            fill : true,
            items : [ {
                type : 'form',
                id : 'core-custom-srt-survey-basic-form',
                service : 'CoreCustomSrtSurveyServiceImpl',
                columns : 1,
                autoSave : true,
                fields : [ {
                    field : 'auditUserName',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITUSERNAME',
                    readonly : true,
                    type : 'choose',
                    single : true,
                    category : 'user',
                    validators : [ 'notEmpty' ],
                    targetFields : [ {
                        auditUserName : 'userName'
                    }, {
                        auditUserId : 'id'
                    } ]
                }, {
                    field : 'auditDate',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.AUDITDATE',
                    type : 'date',
                    readonly : true,
                    validators : [ 'notEmpty' ]
                }, {
                    field : 'beAuditOrgName',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGNAME',
                    type : 'choose',
                    readonly : true,
                    single : true,
                    category : 'org',
                    validators : [ 'notEmpty' ],
                    targetFields : [ {
                        beAuditOrgName : 'orgName'
                    }, {
                        beAuditOrgId : 'orderNo'
                    } ]
                } ]
            }, {
                type : 'grid',
                id : 'core-custom-srt-survey-item-list-grid',
                page : false,
                height : 250,
                service : 'CoreCustomSrtSurveyItemServiceImpl',
                columns : [ {
                    checkbox : true
                }, {
                    field : 'surveyContent',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.SURVEYCONTENT'
                }, {
                    field : 'result',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.RESULT',
                    editor : true,
                    type : 'select',
                    firstBlank : false,
                    readonly : true,
                    category : 'confirmFlag'
                }, {
                    field : 'remark',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.REMARK',
                    editor : true,
                    readonly : true
                } ],
                deleteFormatter : function(row) {
                    return row.surveyContent;
                },
                toolbar : [ {
                    type : 'button',
                    id : 'core-custom-srt-survey-item-list-grid-add-button',
                    text : 'CUSTOMSRTSURVEYS.ADD_RESEARCH_CONTENT.BUTTON',
                    icon : 'add',
                    onClick : function() {
                        var id = Gikam.getComp("core-custom-srt-survey-basic-form").getData().id;
                        if (id == undefined) {
                            Gikam.alert('CUSTOMSRTSURVEYS.MSG.PLEASE_CHOOSE_ONE_ITEM_BEFORE');
                            return;
                        }
                        Gikam.preInsert({
                            modalTitle : 'CUSTOMSRTSURVEYS.ADD_RESEARCH_CONTENT.BUTTON',
                            fields : [ {
                                field : 'surveyContent',
                                title : 'T_CORE_CUSTOM_SRT_SURVEY_ITEM.SURVEYCONTENT',
                                validators : [ 'notEmpty' ],
                                preInsert : true
                            } ],
                            url : Gikam.printf(_this.baseUrl + '/{id}/srt-survey-items', {
                                id : id
                            })
                        }).done(function(itemId) {
                            Gikam.getComp('core-custom-srt-survey-item-list-grid').refresh({
                                url : Gikam.printf(_this.baseUrl + '/{id}/srt-survey-items/queries', {
                                    id : id
                                })
                            });
                        });
                    }
                }, {
                    type : 'button',
                    text : 'CUSTOMSRTSURVEYS.REMOVE_RESEARCH_CONTENT.BUTTON',
                    id : 'core-custom-srt-survey-item-list-grid-remove-button',
                    icon : 'remove',
                    onClick : function() {
                        var id = Gikam.getComp("core-custom-srt-survey-basic-form").getData().id;
                        if (id == undefined) {
                            Gikam.alert('CUSTOMSRTSURVEYS.MSG.PLEASE_CHOOSE_ONE_ITEM_BEFORE');
                            return;
                        }
                        Gikam.getComp('core-custom-srt-survey-item-list-grid').deleteRows(Gikam.printf(_this.baseUrl + '/{id}/srt-survey-items', {
                            id : id
                        }));
                    }
                } ]
            }, {
                type : 'form',
                id : 'core-custom-srt-survey-audit-form',
                service : 'coreCustomSrtSurveyServiceImpl',
                columns : 1,
                autoSave : true,
                fields : [ {
                    field : 'beAuditOrgAdvice',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.BEAUDITORGADVICE',
                    type : 'textarea',
                    readonly : true,
                    height : 50,
                    validators : [ 'notEmpty' ]
                }, {
                    field : 'qsOrgAdvice',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.QSORGADVICE',
                    type : 'textarea',
                    readonly : true,
                    height : 50,
                    validators : [ 'notEmpty' ]
                }, {
                    field : 'qmAdvice',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.QMADVICE',
                    type : 'textarea',
                    readonly : true,
                    height : 50,
                    validators : [ 'notEmpty' ]
                }, {
                    field : 'remark',
                    title : 'T_CORE_CUSTOM_SRT_SURVEY.REMARK',
                    type : 'textarea',
                    height : 30,
                    readonly : true
                } ]
            } ]
        }
    },

    getAttachmentPanel : function() {
        return {
            title : 'GIKAM.FILE.ATTACHMENT',
            items : [ {
                id : 'core-custom-srt-surveys-attachment-panel-uploader',
                type : 'uploader',
                dbTable : 'T_CORE_CUSTOM_SRT_SURVEY',
                bizId : 0,
                onBeforeDelete : function() {
                    var rows = this.grid.data;
                    var canDelete = true;
                    Gikam.each(rows, function(index, row) {
                        if (workspace.user.id != row.createdById) {
                            canDelete = false;
                            Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                            return;
                        }
                    })
                    return canDelete;
                }
            } ]
        }
    },

    getBtnToolbar : function(gridId) {
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp(gridId).getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_CUSTOM_SRT_SURVEY', {
                        id : rows[0].id
                    });
                }
            } ]
        };
    },

    getDetailTab : function() {
        var _this = this;
        return {
            id : 'core-custom-srt-surveys-detail-tab',
            type : 'tab',
            fill : true,
            panels : [ _this.getBasicInfoPanel(), _this.getAttachmentPanel() ]
        }
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/custom-srt-surveys',

    workflow : {
        dbTable : 'T_CORE_CUSTOM_SRT_SURVEY',
        columns : [ {
            field : 'id',
            title : 'T_CORE_CUSTOM_SRT_SURVEY.ID'
        } ]
    }

});