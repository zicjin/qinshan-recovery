var emcImprovement = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/improvements',
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcImprovement.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_IMPROVEMENT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_IMPROVEMENT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcImprovement.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_IMPROVEMENT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_IMPROVEMENT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcImprovement.workflow.dbTable), {
            field : 'qualityControlNo',
            title : 'T_EMC_IMPROVEMENT.QUALITYCONTROLNO',
            width : 100
        }, {
            field : 'createdByName',
            title : 'T_EMC_IMPROVEMENT.CREATEDBYNAME',
            width : 100
        } ];
    },

    getDetailFormFields : function() {
        return [{
            index : true
        }, {
            field : 'source',
            title : 'T_EMC_IMPROVEMENT.SOURCE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'improvementSource'
            }
        }, {
            field : 'sourceNo',
            title : 'T_EMC_IMPROVEMENT.SOURCENO',
            colspan : 2,
            validators : [ 'strLength[0,36]' ]
        } ,{
            field : 'responsibleLeader',
            title : 'T_EMC_IMPROVEMENT.RESPONSIBLELEADER',
            editor : true,
            validators : [ 'strLength[0,80]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                        ids : ids,
                        responsibleLeader : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                    ids : '',
                    responsibleLeader : '',
                });
            }
        }, {
            field : 'partakeUser',
            title : 'T_EMC_IMPROVEMENT.PARTAKEUSER',
            validators : [ 'strLength [0,120]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                        ids : ids,
                    	partakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                    ids : '',
                    partakeUser : '',
                });
            }
        }, {
            field : 'startReason',
            title : 'T_EMC_IMPROVEMENT.STARTREASON',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'purpose',
            title : 'T_EMC_IMPROVEMENT.PURPOSE',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'startTime',
            title : 'T_EMC_IMPROVEMENT.STARTTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'endTime',
            title : 'T_EMC_IMPROVEMENT.ENDTIME',
            type : 'date',
            validators : [ 'notEmpty' ]
        },  {
            field : 'evaluateUser',
            title : 'T_EMC_IMPROVEMENT.EVALUATEUSER',
            editor : true,
            validators : [ 'strLength[0,80]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                        ids : ids,
                        evaluateUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                    ids : '',
                    evaluateUser : '',
                });
            }
        }, {
            field : 'improveMethod',
            title : 'T_EMC_IMPROVEMENT.IMPROVEMETHOD',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'improveContent',
            title : 'T_EMC_IMPROVEMENT.IMPROVECONTENT',
            type : 'textarea',
            colspan : 3,
            height : 70,
            validators : [ 'strLength[0,180]' ]
        }, {
            field : 'actualPartakeUser',
            title : 'T_EMC_IMPROVEMENT.ACTUALPARTAKEUSER',
            editor : true,
            validators : [ 'strLength[0,180]' ],
            type : 'choose',
            category : 'user-simple',
            editable : true,
            single : false,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var ids = "";
                    var names = "";                    
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            ids += rows[index].id;
                            names += rows[index].userName;
                            break;
                        }
                        ids += rows[index].id + ",";
                        names += rows[index].userName + ",";
                    }
                    this.setData({
                        id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                        ids : ids,
                        actualPartakeUser : names
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-improvement-edit-list-grid').getSelections().id,
                    ids : '',
                    actualPartakeUser : '',
                });
            }
        }, {
            field : 'ext$.impprojname',
            title : 'T_EMC_IMPROVEMENT.IMPPROJNAME',
        }, {
            field : 'ext$.mainmeasures',
            title : 'T_EMC_IMPROVEMENT.MAINMEASURES',
        }, {
            field : 'ext$.department',
            title : 'T_EMC_IMPROVEMENT.DEPARTMENT',
        }, {
            field : 'ext$.completiontime',
            title : 'T_EMC_IMPROVEMENT.COMPLETIONTIME',
            type : 'date',
        }, {
            field : 'ext$.additionplanindex',
            title : 'T_EMC_IMPROVEMENT.ADDITIONPLANINDEX',
        }, {
            field : 'ext$.improvementplan',
            title : 'T_EMC_IMPROVEMENT.IMPROVEMENTPLAN',
            colspan : 2,
        }, {
            field : 'ext$.improvetplanrecord',
            title : 'T_EMC_IMPROVEMENT.IMPROVETPLANRECORD',
            colspan : 2,
        }, {
            field : 'ext$.trackverificat',
            title : 'T_EMC_IMPROVEMENT.TRACKVERIFICAT',
        }, {
            field : 'ext$.approvalcomments',
            title : 'T_EMC_IMPROVEMENT.APPROVALCOMMENTS',
        }];
    },
    
    getReportFormFields : function() {
        return [ {
            field : 'ext$.additionplanindex',
            title : 'T_EMC_IMPROVEMENT.ADDITIONPLANINDEX',
        }, {
            field : 'ext$.improvementplan',
            title : 'T_EMC_IMPROVEMENT.IMPROVEMENTPLAN',
        }, {
            field : 'ext$.improvetplanrecord',
            title : 'T_EMC_IMPROVEMENT.IMPROVETPLANRECORD',
        }, {
            field : 'ext$.trackverificat',
            title : 'T_EMC_IMPROVEMENT.TRACKVERIFICAT',
        }, {
            field : 'ext$.approvalcomments',
            title : 'T_EMC_IMPROVEMENT.APPROVALCOMMENTS',
        }, ];
    },
    
    workflow : {
        dbTable : 'T_EMC_IMPROVEMENT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_IMPROVEMENT.ID'
        } ]
    }
});
