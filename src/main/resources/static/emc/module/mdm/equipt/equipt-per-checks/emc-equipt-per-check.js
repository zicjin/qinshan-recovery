var emcEquiptPerCheck = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-per-checks',

    workflow: {
        dbTable: 'T_EMC_EQUIPT_PER_CHECK',
        columns: [{
            field: 'id',
            title: 'T_EMC_EQUIPT_PER_CHECK.ID'
        }]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        },Gikam.status.getBpmnColumn(emcEquiptPerCheck.workflow.dbTable), {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_PER_CHECK.SPECIFICATION'
        }, {
            field : 'checkPerson',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPERSON'
        }];
    },

    getSupAuditListColumns : function() {
        return [ {
            checkbox : true
        },Gikam.status.getBpmnColumn(emcEquiptPerCheck.workflow.dbTable), {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_PER_CHECK.SPECIFICATION'
        }, {
            field : 'checkPerson',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPERSON'
        }];
    },

    getPrinAuditListColumns : function() {
        return [ {
            checkbox : true
        },Gikam.status.getBpmnColumn(emcEquiptPerCheck.workflow.dbTable), {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_PER_CHECK.SPECIFICATION'
        }, {
            field : 'checkPerson',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPERSON'
        }];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        },Gikam.status.getBpmnColumn(emcEquiptPerCheck.workflow.dbTable), {
            field : 'equiptNo',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTID'
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTNAME'
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT_PER_CHECK.SPECIFICATION'
        }, {
            field : 'checkPerson',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPERSON'
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
            validators : [ 'strLength[0,12]' ],
            editor: true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            },
        }, {
            field : 'checkTime',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKTIME',
            type : 'date'
        }, {
            field : 'equiptment',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            editor : true,
            multiple : true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
            editor : true,
            multiple : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkWay',
            },
        }, {
            field : 'checkProcess',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPROCESS',
            validators : [ 'strLength[0,120]' ]
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKRESULT',
            validators : [ 'strLength[0,22]' ]
        }, {
            field : 'checkConclusion',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKCONCLUSION',
            validators : [ 'strLength[0,22]' ],
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkConclusion',
            }
        }, {
            field : 'opinion',
            title : 'T_EMC_EQUIPT_PER_CHECK.OPINION',
            validators : [ 'strLength[0,22]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'ext$.recordername',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDER',
            category : 'user-simple',
            type : 'choose',
            validators : [ 'notEmpty','strLength[0,42]' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        recorder : rows[0].id,
                        ext$ : {
                            recordername : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    recorder : '',
                    ext$ : {
                        recordername : ''
                    }
                });
            },
        }, {
            field : 'recorderDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDERDATE',
            type : 'date',
            readonly : true,
        }, {
            field : 'ext$.directorname',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTOR',
            category : 'user-simple',
            type : 'choose',
            validators : [ 'notEmpty','strLength[0,42]' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        director : rows[0].id,
                        ext$ : {
                            directorname : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    director : '',
                    ext$ : {
                        directorname : ''
                    }
                });
            },
        }, {
            field : 'directorDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTORDATE',
            readonly : true
        }, {
            field : 'ext$.principalname',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPAL',
            readonly : true
        }, {
            field : 'principalDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPALDATE',
            readonly : true
        }, ];
    },

    getBaseInfoFormSupFields : function() {
        return [ {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
            readonly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            },
        }, {
            field : 'checkTime',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKTIME',
            readonly : true
        }, {
            field : 'equiptment',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            readonly : true
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            readonly : true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
            readonly : true
        }, {
            field : 'checkProcess',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPROCESS',
            readonly : true
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKRESULT',
            readonly : true
        }, {
            field : 'checkConclusion',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKCONCLUSION',
            readonly : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkConclusion',
            },
        }, {
            field : 'opinion',
            title : 'T_EMC_EQUIPT_PER_CHECK.OPINION',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            readonly: true,
        }, {
            field : 'ext$.recordername',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDER',
            readonly : true
        }, {
            field : 'recorderDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDERDATE',
            readonly : true
        }, {
            field : 'ext$.directorname',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTOR',
            readonly : true,
        }, {
            field : 'directorDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTORDATE',
            readonly : true,
            type : 'date'
        }, {
            field : 'ext$.principalname',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPAL',
            category : 'user-simple',
            type : 'choose',
            validators : [ 'notEmpty','strLength[0,42]' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        principal : rows[0].id,
                        ext$ : {
                            principalname : rows[0].userName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    principal : '',
                    ext$ : {
                        principalname : ''
                    }
                });
            },
        }, {
            field : 'principalDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPALDATE',
            readonly : true
        } ];
    },

    getBaseInfoFormPrinFields : function() {
        return [ {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
            readonly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            },
        }, {
            field : 'checkTime',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKTIME',
            readonly : true
        }, {
            field : 'equiptment',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            readonly : true
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            readonly : true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY',
            readonly : true
        }, {
            field : 'checkProcess',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPROCESS',
            readonly : true
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKRESULT',
            readonly : true
        }, {
            field : 'checkConclusion',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKCONCLUSION',
            readonly : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkConclusion',
            },
        }, {
            field : 'opinion',
            title : 'T_EMC_EQUIPT_PER_CHECK.OPINION',
            readonly : true
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            readonly: true,
        }, {
            field : 'ext$.recordername',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDER',
            readonly : true
        }, {
            field : 'recorderDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDERDATE',
            readonly : true
        }, {
            field : 'ext$.directorname',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTOR',
            readonly : true
        }, {
            field : 'directorDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTORDATE',
            readonly : true
        }, {
            field : 'ext$.principalname',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPAL',
            readonly : true,
        }, {
            field : 'principalDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPALDATE',
            readonly : true,
            type : 'date'
        } ];
    },

    getBaseInfoFormSearchFields : function() {
        return [ {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            },
        }, {
            field : 'checkTime',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKTIME'
        }, {
            field : 'equiptment',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT'
        }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field : 'checkWay',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKWAY'
        }, {
            field : 'checkProcess',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKPROCESS'
        }, {
            field : 'checkResult',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKRESULT'
        }, {
            field : 'checkConclusion',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKCONCLUSION',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'checkConclusion',
            }
        }, {
            field : 'opinion',
            title : 'T_EMC_EQUIPT_PER_CHECK.OPINION'
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
        }, {
            field : 'recorder',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDER'
        }, {
            field : 'recorderDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDERDATE'
        }, {
            field : 'director',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTOR'
        }, {
            field : 'directorDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.DIRECTORDATE'
        }, {
            field : 'principal',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPAL',
        }, {
            field : 'principalDate',
            title : 'T_EMC_EQUIPT_PER_CHECK.PRINCIPALDATE',
        } ];
    },

});
