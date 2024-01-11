var emcTaskAssign = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME',
            type : 'link'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field : 'inputUserName',
            title : 'T_EMC_ORDER_TASK.INPUTUSERNAME',
        } ];
    },

    getPrepareShowAllField : function() {
        return [ {
            title : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.SHOW_ALL',
            field : 'showFlag',
            type : 'simpleCheckbox',
            refreshGrid : false,
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                emcTaskAssign.editPage.projId = null;
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-task-assign-list-grid');

                var showFlags = value;
                if (showFlags === '1') {
                    grid.refresh({
                        requestData : {
                            inputUserId : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData : {
                            inputUserId : workspace.user.id
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getTestRunColumns : function() {
        return [ {
            checkbox : true,
        }, Gikam.status.getBpmnColumn(emcTaskAssign.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_RUN.PROJNO',
        }, {
            field : 'id',
            title : 'T_EMC_RUN.RUNID',
            type : 'link',
            width : 120,
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
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        analysisUserName : rows[0].userName,
                        analysisUserId : rows[0].id
                    });

                    var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                    Gikam.post(emcTaskAssign.baseUrl + '/action/sync-analysis-user-change', Gikam.getJsonWrapper({
                        f : {
                            analysisUserName : rows[0].userName,
                            analysisUserId : rows[0].id
                        }
                    }, [ 'emcRunServiceImpl', [ activedRow ] ])).done(function() {
                        Gikam.getComp('emc-task-assign-list-grid').refresh();
                        Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                    analysisUserName : '',
                    analysisUserId : ''
                });
            },
        }, {
            field : 'ext$.weighingusername',
            title : 'T_EMC_RUN.WEIGHINGUSERNAME',
            editor : true,
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        ext$ : {
                            weighingusername : rows[0].userName,
                            weighinguserid : rows[0].id
                        }
                    });

                    var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                    Gikam.post(emcTaskAssign.baseUrl + '/action/sync-weight-user-change', Gikam.getJsonWrapper({
                        f : {
                            weighinguserid : rows[0].id,
                            weighingusername : rows[0].userName
                        }
                    }, [ 'emcRunServiceImpl', [ activedRow ] ])).done(function() {
                        Gikam.getComp('emc-task-assign-list-grid').refresh();
                        Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                    ext$ : {
                        weighingusername : '',
                        weighinguserid : ''
                    }
                });
            },
        }, {
            field : 'ext$.pretreatusername',
            title : 'T_EMC_RUN.PRETREATUSERNAME',
            editor : true,
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        ext$ : {
                            pretreatusername : rows[0].userName,
                            pretreatuserid : rows[0].id
                        }
                    });

                    var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                    Gikam.post(emcTaskAssign.baseUrl + '/action/sync-pretreat-user-change', Gikam.getJsonWrapper({
                        f : {
                            pretreatuserid : rows[0].id,
                            pretreatusername : rows[0].userName
                        }
                    }, [ 'emcRunServiceImpl', [ activedRow ] ])).done(function() {
                        Gikam.getComp('emc-task-assign-list-grid').refresh();
                        Gikam.getComp('emc-task-assign-list-test-run-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                    ext$ : {
                        pretreatusername : '',
                        pretreatuserid : ''
                    }
                });
            },
        }, {
            field : 'ext$.runanalysisorgid',
            title : 'T_EMC_SAMPLENUMBER.ORGID',
        }, {
            field : 'createdTime',
            title : 'T_EMC_RUN.CREATEDTIME',
        }, {
            field : 'ext$.adddate',
            title : 'T_EMC_RUN.ADDDATE',
            type :'date',
            editor :true
        }  ];
    },

    getTestRunFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_RUN.RUNID'
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
                    Gikam.getComp('emc-run-input-detail-form').setData({
                        analysisUserName : rows[0].userName,
                        analysisUserId : rows[0].id
                    });
                }
            },
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
                        partakeUserName : partakeUserNames.join(','),
                        partakeUserId : partakeUserIds.join(',')
                    });
                }
            },
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow();
                return {
                    targetType : 'analysisCheck',
                    analysisUserId : activedRow.analysisUserId
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-input-detail-form').setData({
                        checkUserId : rows[0].id,
                        checkUserName : rows[0].userName
                    });
                }
            },
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
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
            field : 'assistEquiptName',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            category : 'equipt',
            type : 'choose',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function(index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        assistEquiptId : assistEquiptId.join(','),
                        assistEquiptName : assistEquiptName.join(','),
                        assistEquiptModel : assistEquiptModel.join(),
                        assistEquiptNo : assistEquiptNo.join()
                    });
                }
            },
            onClean : function() {
                this.setData({
                    assistEquiptId : '',
                    assistEquiptName : '',
                    assistEquiptModel : '',
                    assistEquiptNo : ''
                });
            }
        }, {
            field : 'ext$.roomno',
            title : 'T_EMC_RUN.ROOMNO',
            editor : true,
            type : 'choose',
            category : 'envRecord',
            single : true,
            onBeforeChoose : function() {
                return {
                    monitorDate_SEQ : this.getData().analysisBeginDate
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$ : {
                            roomno : rows[0].ext$.roomno
                        },
                        temperature : rows[0].temperature,
                        humidity : rows[0].humidity
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        roomno : ''
                    },
                    temperature : '',
                    humidity : ''
                });
            }
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
            validators : [ 'strLength[0, 30]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            validators : [ 'strLength[0, 30]' ]
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            type : 'time'
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
            type : 'textarea',
            colspan : 3
        } ];
    },

    getSampleTestColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field : 'cupNo',
            title : 'T_EMC_ORDER_TASK.CUPNO',
            width : 70,
            validators : [ 'number', 'notEmpty' ]
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_ORDER_TASK.PROJNO',
            width : 120,
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER.LABELNO',
            width : 100,
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.samplebegintime',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width : 80,
            formatter : function(i, v, row) {
                if (row.qcCategoryCode === 'P-DUP') {
                    return '*****';
                }
                return v;
            }
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field : 'ext$.relatedorderno',
            title : 'T_EMC_ORDER.RELATEDORDERNO',
        } ];
    },

    getSampleTestSecondColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field : 'cupNo',
            title : 'T_EMC_ORDER_TASK.CUPNO',
            width : 70,
            validators : [ 'number', 'notEmpty' ]
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER_TASK.ORDERDESC',
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.samplebegintime',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor : true,
            validators : [ 'strLength[0, 120]' ]
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width : 80,
            formatter : function(i, v, row) {
                if (row.qcCategoryCode === 'P-DUP') {
                    return '*****';
                }
                return v;
            }
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field : 'ext$.relatedorderno',
            title : 'T_EMC_ORDER.RELATEDORDERNO',
        } ];
    },

    getResultPublicColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort : false
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            sort : false
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort : false
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            sort : false,
            textStyleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus : function(field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width : 400,
                        height : 300,
                        title : Gikam.choose.getConfig('itemResult').title,
                        url : Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single : true,
                            itemId_SEQ : data.itemId,
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index : index,
                                originalResult : rows[0].result,
                                reportResult : rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index : index,
                                originalResult : ''
                            });
                        }

                    }

                }
            }
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            sort : false,
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'resultRule',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTRULE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'resultRule'
            },
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
            validators : [ 'number' ],
            sort : false
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
            sort : false
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName
                    })
                }
            },
            sort : false
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor : true,
            validators : [ 'number' ],
            sort : false
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            sort : false
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            sort : false
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            sort : false,
            styleFormatter : function(row) {
                if (row.resultStatus === 'OOS') {
                    return {
                        backgroundColor : 'red'
                    }
                }
            }
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort : false
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort : false
        } ]
    },

    getResultColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
            sort : false
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP',
            sort : false
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
            sort : false
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
            sort : false
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width : 80,
            sort : false
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
            sort : false
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            sort : false,
            textStyleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.scriptFlag == '1') {
                    return false;
                }
                return true;
            },
            onFocus : function(field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width : 400,
                        height : 300,
                        title : Gikam.choose.getConfig('itemResult').title,
                        url : Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single : true,
                            itemId_SEQ : data.itemId,
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-run-result-grid').setData({
                                index : index,
                                originalResult : rows[0].result,
                                reportResult : rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                        }

                        if (forbidden) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.FORMAT_ERROR');
                            this.setData({
                                index : index,
                                originalResult : ''
                            });
                        }

                    }

                }
            },
            width : 130
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            width : 130,
            sort : false,
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
            sort : false
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName
                    })
                }
            },
            sort : false
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor : true,
            validators : [ 'number' ],
            width : 80,
            sort : false
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
            validators : [ 'number' ],
            width : 80,
            sort : false
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            width : 80,
            sort : false
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            width : 80,
            sort : false
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            width : 100,
            sort : false
        }, {
            field : 'relativeDeviation',
            title : 'T_EMC_ORDER_TASK_RESULT.RELATIVEDEVIATION',
            sort : false
        }, {
            field : 'average ',
            title : 'T_EMC_ORDER_TASK_RESULT.AVERAGE',
            sort : false
        } ];
    },

    getAddRunListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERNO'
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_ORDER_TASK.PROJNO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_ORDER_TASK.PROJNAME',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'itemGroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP',
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.accepteddate',
            title : 'T_EMC_ORDER_TASK.RECEIVEDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        } ];
    },

    getEditItemListColumns : function() {

        return [ {
            checkbox : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        } ];

    },

    getHistoricalDataColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            },
            width : 100,
        }, {
            field : 'ext$.samplebegintime',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'ext$.analysisdate',
            title : 'T_EMC_ORDER_TASK.ANALYSISDATE',
            type : 'date',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            },
            width : 100,
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        } ];
    },

    getHistoricalDataFields : function() {
        return [ {
            field : 'beginDate',
            title : 'T_EMC_ORDER_TASK_RESULT.BEGINDATE',
            type : 'date',
            editor : true,
            width : 400
        }, {
            field : 'endDate',
            title : 'T_EMC_ORDER_TASK_RESULT.ENDDATE',
            type : 'date',
            editor : true,
            width : 400
        } ]
    },

    getEquiptChooseListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
        }, {
            field : 'equiptCategoryId',
            title : 'T_EMC_EQUIPT.EQUIPTCATEGORYID',
            type : 'select',
            search : true,
            category : 'equiptCategory',
            validators : [ 'number' ]
        }, {
            field : 'usedDepartmentId',
            title : 'T_EMC_EQUIPT.USEDDEPARTMENTID',
        }, {
            field : 'sharing',
            title : 'T_EMC_EQUIPT.SHARING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'maintain',
            title : 'T_EMC_EQUIPT.MAINTAIN',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'sourceMethod',
            title : 'T_EMC_EQUIPT.SOURCEMETHOD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sourceMethod',
            },
        }, {
            field : 'sourceDate',
            title : 'T_EMC_EQUIPT.SOURCEDATE',
        } ];
    },

    getQcChooseListColumns : function() {
        return [ {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID'
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME',
        }, {
            field : 'addNum',
            title : 'T_EMC_QC_CATEGORY.ADDNUM',
            type : 'number',
            editor : true,
            isInteger : true
        } ];
    },

    getStockNoColumns : function() {
        return [ {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
            refreshGrid : false,
            validators : [ 'strLength[0,12]' ],
            editor : true
        } ];
    },

    getUnCompleteField : function() {
        return [ {
            title : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UN_COMPLETE',
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

                var testGrid = Gikam.getComp('emc-task-assign-list-sample-test-grid');
                var resultGrid = Gikam.getComp('emc-task-assign-list-result-grid');
                var showFlags = value;

                if (showFlags == '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData : {
                            originalResults : 'empty'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData : {
                            originalResults : 'empty'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData : {
                            originalResults : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData : {
                            originalResults : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getUnCompleteSecondField : function() {
        return [ {
            title : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.UN_COMPLETE',
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

                var testGrid = Gikam.getComp('emc-run-sample-test-grid');
                var resultGrid = Gikam.getComp('emc-run-result-grid');
                var showFlags = value;

                if (showFlags == '1') {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData : {
                            originalResults : 'empty'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData : {
                            originalResults : 'empty'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    // 刷新样品测试
                    testGrid.refresh({
                        requestData : {
                            originalResults : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                    // 刷新结果
                    resultGrid.refresh({
                        requestData : {
                            originalResults : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getRunShowAllField : function() {
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
                var resultRunAllGrid = Gikam.getComp('emc-task-assign-list-test-run-grid');
                var sampleTestGrid = Gikam.getComp('emc-task-assign-list-sample-test-grid');

                var showFlags = value;
                if (showFlags === '1') {
                    /*sampleTestGrid.setOptions({
                        requestData : {
                            analysisUserId_SC : ''
                        }
                    });*/

                    resultRunAllGrid.refresh({
                        requestData : {
                            showAllFlag : '1',
                            analysisUserId_SEQ : '',
                            analysisUserId : workspace.user.id,
                            checkUserId : workspace.user.id,
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    /*sampleTestGrid.setOptions({
                        requestData : {
                            analysisUserId_SC : workspace.user.id
                        }
                    });*/

                    resultRunAllGrid.refresh({
                        requestData : {
                            showAllFlag : '0',
                            analysisUserId_SEQ : workspace.user.id,
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getMethodEditColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY'
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        } ]
    },

    getSchemeMethodChooseListColumns : function() {
        return [ {
            radio : true
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_METHOD_VERSION.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP'
        } ];
    },

    getBatchInputFormFields : function() {
        return [ {
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
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        partakeUserName : partakeUserNames.join(','),
                        partakeUserId : partakeUserIds.join(',')
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
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
            validators : [ 'notEmpty' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var checkUserNames = [];
                    var checkUserIds = [];
                    Gikam.each(rows, function(index, item) {
                        checkUserNames.push(item.userName);
                        checkUserIds.push(item.id);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                        checkUserName : checkUserNames.join(','),
                        checkUserId : checkUserIds.join(',')
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-task-assign-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,
                    checkUserId : '',
                    checkUserName : ''
                });
            }
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            editor : true,
            type : 'time'
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            editor : true,
            type : 'time'
        }, {
            field : 'assistEquiptName',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            editor : true,
            category : 'equipt',
            type : 'choose',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_SEQ : '实验'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var assistEquiptId = [];
                    var assistEquiptName = [];
                    var assistEquiptModel = [];
                    var assistEquiptNo = [];
                    Gikam.each(rows, function(index, item) {
                        assistEquiptId.push(item.id);
                        assistEquiptName.push(item.equiptName);
                        assistEquiptModel.push(item.equiptName + " / " + item.specification);
                        assistEquiptNo.push(item.equiptId);
                    });
                    this.setData({
                        assistEquiptId : assistEquiptId.join(','),
                        assistEquiptName : assistEquiptName.join(','),
                        assistEquiptModel : assistEquiptModel.join(),
                        assistEquiptNo : assistEquiptNo.join()
                    });
                }
            },
            onClean : function() {
                this.setData({
                    assistEquiptId : '',
                    assistEquiptName : '',
                    assistEquiptModel : '',
                    assistEquiptNo : ''
                });
            }
        }, {
            field : 'ext$.roomno',
            title : 'T_EMC_RUN.ROOMNO',
            editor : true,
            type : 'choose',
            category : 'envRecord',
            single : true,
            onBeforeChoose : function() {
                return {
                    monitorDate_SEQ : this.getData().analysisBeginDate
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$ : {
                            roomno : rows[0].ext$.roomno
                        },
                        temperature : rows[0].temperature,
                        humidity : rows[0].humidity
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        roomno : ''
                    },
                    temperature : '',
                    humidity : ''
                });
            }
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
            editor : true,
            validators : [ 'strLength[0,30]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            editor : true,
            validators : [ 'strLength[0,30]' ]
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleMethod'
            },

        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
            editor : true
        } ];
    },

    getCopyBlankToChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_RUN.RUNID',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            width : 100,
            category : 'emp',
            type : 'choose',
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            width : 100,
            category : 'emp',
            type : 'choose',
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
            width : 100,
        } ]
    },

    getCopyBlankSpkcToChooseListColumns : function() {
        return [ {
            field : 'runId',
            title : 'T_EMC_RUN.RUNID',
            width : 120,
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.samplebegindate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.samplebegintime',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            validators : [ 'strLength[0, 120]' ]
        } ];
    },

    getRunBatchInputFields : function() {
        return [ {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            editor : true,
            width : 300,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
                return {
                    departmentid_SEQ : data[0].createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                        analysisUserName : rows[0].userName,
                        analysisUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    analysisUserName : '',
                    analysisUserId : ''
                });
            }
        }, {
            field : 'ext$.weighingusername',
            title : 'T_EMC_RUN.WEIGHINGUSERNAME',
            editor : true,
            width : 300,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
                return {
                    departmentid_SEQ : data[0].createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                        ext$ : {
                            weighingusername : rows[0].userName,
                            weighinguserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    ext$ : {
                        weighingusername : '',
                        weighinguserid : ''
                    }
                });
            }
        }, {
            field : 'ext$.pretreatusername',
            title : 'T_EMC_RUN.PRETREATUSERNAME',
            editor : true,
            width : 300,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var data = Gikam.getComp('emc-task-assign-list-test-run-grid').getSelections();
                return {
                    departmentid_SEQ :  data[0].createdByOrgId,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
/*                        id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,*/
                        ext$ : {
                            pretreatusername : rows[0].userName,
                            pretreatuserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
            	this.setData({
/*                    id : Gikam.getComp('emc-task-assign-list-test-run-grid').getActivedRow().id,*/
                    ext$ : {
                        pretreatusername : '',
                        pretreatuserid : ''
                    }
                });
            }
        }];
    },


    workflow : {
        dbTable : 'T_EMC_RUN',
        columns : [ {
            field : 'id',
            title : 'T_EMC_RUN.ID'
        } ]
    }
});
