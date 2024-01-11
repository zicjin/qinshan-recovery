var emcAnalysisAudit = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'ext$.ordercategory',
            title : 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME'
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
        }, {
            field : 'projCreatedTime',
            title : 'T_EMC_PROJECT.PROJCREATEDTIME',
            type : 'date',
        }, {
            field : 'ext$.lastestaccepteddate',
            title : 'T_EMC_PROJECT.LASTESTACCEPTEDDATE',
            type : 'date',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, ];
    },

    getTestRunColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'id',
            title : 'T_EMC_RUN.RUNID',
            type : 'link',
            styleFormatter : function(row) {
                if (!Gikam.isEmpty(row.undoReason)) {
                    return {
                        backgroundColor : '#F08080'
                    }
                }
            },
        }, {
            field : 'undoReason',
            title : 'T_EMC_RUN.UNDOREASON',
        }, {
            field : 'checkStatus',
            title : 'T_EMC_RUN.CHECKSTATUS',
            type : 'simpleCheckbox',
            width : 80
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        analysisUserName : rows[0].userName,
                        analysisUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        analysisUserName : '',
                        analysisUserId : ''
                    });
            }
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            category : 'emp',
            type : 'choose',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function(index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName : partakeUserNames.join(','),
                        partakeUserId : partakeUserIds.join(',')
                    });
                }
            },
            onClean : function() {
                    this.setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName : '',
                        partakeUserId : ''
                    });
            }
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserId : rows[0].id,
                        checkUserName : rows[0].userName
                    });
                }
            },
            onClean : function() {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserId : '',
                        checkUserName : ''
                    });
            }
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
            field : 'calibrationCurve',
            title : 'T_EMC_RUN.CALIBRATIONCURVE',
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            type : 'dateTime',
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            type : 'dateTime'
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            category : 'equipt',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId : rows[0].id,
                        ext$ : {
                            assistequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId : '',
                        ext$ : {
                            assistequiptname : ''
                        }
                    });
            }
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            type : 'dateTime'
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleMethod'
            },
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
        }, {
            field : 'auditedDate',
            title : 'T_EMC_RUN.AUDITEDDATE',
            type : 'dateTime',
            editor :true
        } ];
    },

    getOrderTaskResultValidationListColumns : function() {
        return [ {
            field : 'ext$.relatename',
            title : 'T_EMC_ORDER_TASK_RESULT.RELATENAME',
            width : 130
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
            width : 120
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            width : 130
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            width : 130
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME',
            width : 130
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULT',
            width : 130,
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        } ];
    },

    getProjTestRunColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'id',
            title : 'T_EMC_RUN.RUNID',
            type : 'link'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        analysisUserName : rows[0].userName,
                        analysisUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    analysisUserName : '',
                    analysisUserId : ''
                });
            }
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            category : 'emp',
            type : 'choose',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var partakeUserNames = [];
                    var partakeUserIds = [];
                    Gikam.each(rows, function(index, item) {
                        partakeUserNames.push(item.userName);
                        partakeUserIds.push(item.id);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName : partakeUserNames.join(','),
                        partakeUserId : partakeUserIds.join(',')
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    partakeUserName : '',
                    partakeUserId : ''
                });
            }
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserId : rows[0].id,
                        checkUserName : rows[0].userName
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    checkUserId : '',
                    checkUserName : ''
                });
            }
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
            field : 'calibrationCurve',
            title : 'T_EMC_RUN.CALIBRATIONCURVE',
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            type : 'dateTime',
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            type : 'dateTime'
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            category : 'equipt',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId : rows[0].id,
                        ext$ : {
                            assistequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                    assistEquiptId : '',
                    ext$ : {
                        assistequiptname : ''
                    }
                });
            }
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            type : 'dateTime'
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleMethod'
            },
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
        }, {
            field : 'createdTime',
            title : 'T_EMC_RUN.CREATEDTIME',
            type : 'dateTime',
        } , {
            field : 'auditedDate',
            title : 'T_EMC_RUN.AUDITEDDATE',
            type : 'dateTime',
        } ];
    },
});
