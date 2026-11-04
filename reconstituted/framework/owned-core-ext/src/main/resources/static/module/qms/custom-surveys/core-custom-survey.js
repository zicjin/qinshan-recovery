var coreCustomSurvey = Gikam.getPageObject({

    getCustomSurveyEditListColumns : function(editable) {
        return [ {
            checkbox : true
        }, {
            field : 'processStatus',
            title : 'GIKAM.BPMN.FIELD.TITLE.STATUS',
            width : 60,
            type : 'select',
            items : [ {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.NEW'),
                value : 'draft'
            }, {
                text : Gikam.propI18N('GIKAM.BPMN.STATUS.DONE'),
                value : 'done'
            } ]
        }, {
            field : 'customerName',
            title : 'T_CORE_CUSTOM_SURVEY.CUSTOMERNAME',
            width : 90
        }, {
            field : 'folderNo',
            title : 'T_CORE_CUSTOM_SURVEY.FOLDERNO',
            width : 90,
            editor : editable
        }, {
            field : 'createdByName',
            title : 'T_CORE_CUSTOM_SURVEY.CREATEDBYNAME',
            width : 90
        }, {
            field : 'surveyDate',
            title : 'T_CORE_CUSTOM_SURVEY.SURVEYDATE',
            width : 90
        } ];
    },

    getCustomSurveyFormFields : function() {
        return [ {
            field : 'customerAdvice',
            title : 'T_CORE_CUSTOM_SURVEY.CUSTOMERADVICE',
            type : 'textarea',
            height : 75
        } ];
    },

    getCustomSurveyInsertFields : function() {
        return [ {
            field : 'customerName',
            title : 'CORE.MODULE.QMS.APPLYCOMPLAINT.TITLE.CUSTOMER_ORG',
            validators : [ 'notEmpty', 'strLength[0,200]' ],
            preInsert : true
        }, {
            field : 'folderNo',
            title : 'T_CORE_CUSTOM_SURVEY.FOLDERNO',
            validators : [ 'strLength[0,12]' ],
            preInsert : true
        }, {
            field : 'surveyDate',
            title : 'T_CORE_CUSTOM_SURVEY.SURVEYDATE',
            validators : [ 'notEmpty' ],
            type : 'date',
            preInsert : true
        } ];
    },
    
    getSurveyItemListColumns : function(editable) {
        return [ {
            checkbox : true
        }, {
            field : 'surveyContent',
            title : 'T_CORE_CUSTOM_SURVEY_ITEM.SURVEYCONTENT',
            width : '500',
            editor : editable,
            type : 'select',
            category : 'custom-survey-evaluate'
        }, {
            field : 'evaluate',
            title : 'T_CORE_CUSTOM_SURVEY_ITEM.EVALUATE',
            editor : editable,
            type : 'select',
            items : [ {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.VERYSATISFIEDCOUNT'),
                value : 5
            }, {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.SATISFIEDCOUNT'),
                value : 4
            }, {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.COMMONLYCOUNT'),
                value : 3
            }, {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.NEEDSIMPROVEDCOUNT'),
                value : 2
            }, {
                text : Gikam.propI18N('CORE.MODULE.QMS.CUSTOMSURVEYS.TITLE.DISSATISFIEDCOUNT'),
                value : 1
            } ]
        } ];
    },

    getSurveySumListColumns : function(editable) {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn('T_CORE_CUSTOM_SURVEY_SUM'), {
            field : 'statisNo',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.STATISNO'
        }, {
            field : 'customerName',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.CUSTOMERNAME',
            editor : editable
        }, {
            field : 'createdByName',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.CREATEDTIME'
        }, {
            field : 'startDate',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.STARTDATE'
        }, {
            field : 'endDate',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.ENDDATE'
        } ];
    },

    getSurveySumFormFields : function() {
        return [ {
            field : 'technicalAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.TECHNICALADVICE',
            type : 'textarea',
            height : 100,
            readonly : true
        }, {
            field : 'qualityAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.QUALITYADVICE',
            type : 'textarea',
            height : 100,
            readonly : true
        } ];
    },
    
    getTechAuditSurveySumFormFields : function() {
        return [ {
            field : 'technicalAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.TECHNICALADVICE',
            type : 'textarea',
            height : 100
        }, {
            field : 'qualityAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.QUALITYADVICE',
            type : 'textarea',
            height : 100,
            readonly : true
        } ];
    },
    
    getQuaAuditSurveySumFormFields : function() {
        return [ {
            field : 'technicalAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.TECHNICALADVICE',
            type : 'textarea',
            height : 100,
            readonly : true
        }, {
            field : 'qualityAdvice',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.QUALITYADVICE',
            type : 'textarea',
            height : 100
        } ];
    },
    
    getSumInsertFields : function() {
        return [ {
            field : 'customerName',
            title : 'CORE.MODULE.QMS.APPLYCOMPLAINT.TITLE.CUSTOMER_ORG',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'startDate',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.STARTDATE',
            validators : [ 'notEmpty' ],
            type : 'date',
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('core-custom-survey-sum-insert-form');
                var endDate = form.getData().endDate;
                if (!Gikam.isEmpty(endDate)) {
                    if (endDate < value) {
                        Gikam.alert('CORE.MODULE.QMS.CUSTOM_SURVEY.TITLE.ENDTIME_NOT_BEFORE_STARTTIME');
                        form.setData({
                            index : 0,
                            startDate : ''
                        });
                    }
                }
            }
        }, {
            field : 'endDate',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.ENDDATE',
            validators : [ 'notEmpty' ],
            type : 'date',
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = Gikam.getComp('core-custom-survey-sum-insert-form');
                var startDate = form.getData().startDate;
                if (!Gikam.isEmpty(startDate)) {
                    if (startDate > value) {
                        Gikam.alert('CORE.MODULE.QMS.CUSTOM_SURVEY.TITLE.ENDTIME_NOT_BEFORE_STARTTIME');
                        form.setData({
                            index : 0,
                            endDate : ''
                        });
                    }
                }
            }
        } ];
    },
    
    getSurveySumRetListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'surveyContent',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.SURVEYCONTENT',
            width : 180
        }, {
            field : 'total',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.TOTAL',
            width : 90
        }, {
            field : 'satisfiedCount',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.SATISFIEDCOUNT',
            width : 90
        }, {
            field : 'commonlyCount',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.COMMONLYCOUNT',
            width : 90
        }, {
            field : 'needsImprovedCount',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.NEEDSIMPROVEDCOUNT',
            width : 90
        }, {
            field : 'dissatisfiedCount',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.DISSATISFIEDCOUNT',
            width : 90
        }, {
            field : 'verySatisfiedCount',
            title : 'T_CORE_CUSTOM_SURVEY_SUM_RET.VERYSATISFIEDCOUNT',
            width : 90
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/custom-surveys',

    workflow : {
        dbTable : 'T_CORE_CUSTOM_SURVEY_SUM',
        columns : [ {
            field : 'id',
            title : 'T_CORE_CUSTOM_SURVEY_SUM.ID'
        } ]
    }

});
