var emcRunAudit = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',

    getEditListColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(emcRunAudit.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_RUN.PROJNO',
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
        },{
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            type : 'time',
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            type : 'time'
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            category : 'emp',
            type : 'choose',
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            category : 'equipt',
            type : 'choose',
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
            }
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
            validators : [ 'strLength[0, 22]' ]
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
            validators : [ 'strLength[0, 66]' ]
        } ];
    },

    getTestRunColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'id',
            title : 'T_EMC_RUN.RUNID'
        }, {
            field : 'backReason',
            title : 'T_EMC_RUN.BACKREASON',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            editor : true,
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
            editor : true,
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
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-list-test-run-grid').getActivedRow().id,
                        checkUserName : rows[0].id
                    });
                }
            }
        }, {
            field : 'equiptId',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
            field : 'calibrationCurve',
            title : 'T_EMC_RUN.CALIBRATIONCURVE',
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            editor : true,
            type : 'dateTime',
            editable : true
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            editor : true,
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
            editor : true,
            validators : [ 'strLength[0, 30]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            editor : true,
            validators : [ 'strLength[0, 30]' ]
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            editor : true,
            type : 'dateTime'
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            editor : true,
            validators : [ 'strLength[0, 10]' ]
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
            editor : true,
            validators : [ 'strLength[0, 22]' ]
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
            editor : true,
            validators : [ 'strLength[0, 66]' ]
        } ];
    },

    getRunAuditShowAllField : function() {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var resultRunAllGrid = Gikam.getComp('emc-run-audit-list-test-run-grid');

                var showFlags = value;
                if (showFlags == '1') {
                    resultRunAllGrid.refresh({
                        requestData : {
                            showTotalFlag : '1'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    resultRunAllGrid.refresh({
                        requestData : {
                            showTotalFlag : '0',
                            checkUserId : workspace.user.id,
                            createdById_DIFFER : workspace.user.id
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    workflow : {
        dbTable : 'T_EMC_RUN',
        columns : [ {
            field : 'id',
            title : 'T_EMC_RUN.ID'
        } ]
    }

});
