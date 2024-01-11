var emcRunEdit = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/edit/run-edits',
    runUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',

    getTestRunColumns : function() {
        return [ {
            checkbox : true,
        }, {
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
            field : 'projNo',
            title : 'T_EMC_RUN.PROJNO',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            editor : true,
            width : 100,
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
                        id : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id,
                        partakeUserName : partakeUserNames.join(','),
                        partakeUserId : partakeUserIds.join(',')
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id,
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
            width : 100,
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_RUN.EQUIPTID',
            width : 100,
        }, {
            field : 'curveNo',
            title : 'T_EMC_RUN.SELECTRUN',
            width : 120,
            type : 'choose',
            category : 'curve',
            onBeforeChoose : function(index) {
                var row = Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getData()[index];
                return {
                    equiptId : row.equiptId,
                    testId : row.testId,
                    methodId : row.methodId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var curveIds = '';
                    var curveNos = '';
                    rows.map(function(item, i) {
                        curveIds += rows[i].id + ',';
                        curveNos += rows[i].curveNo + ',';
                    });

                    this.setData({
                        index : index,
                        curveId : curveIds.substring(0, curveIds.length - 1),
                        curveNo : curveNos.substring(0, curveNos.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    curveId : '',
                    curveNo : ''
                });
            },
            editor : true
        }, {
            field : 'ext$.minanalysisbegindate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'ext$.maxnanalysisenddate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            editor : true,
            type : 'time',
            width : 100,
            validators : [ 'notEmpty' ]
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            editor : true,
            validators : [ 'notEmpty' ],
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            editor : true,
            width : 100,
            validators : [ 'notEmpty' ],
            type : 'time'
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            editor : true,
            category : 'equipt',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-run-edit-edit-list-test-run-grid').setData({
                        id : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id,
                        assistEquiptId : rows[0].id,
                        ext$ : {
                            assistequiptname : rows[0].equiptName
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-run-edit-edit-list-test-run-grid').setData({
                    id : Gikam.getComp('emc-run-edit-edit-list-test-run-grid').getActivedRow().id,
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
            width : 100,
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            width : 100,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,30]' ]
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            editor : true,
            type : 'time'
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            editor : true,
            width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleMethod'
            },

        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
            editor : true,
            width : 100,
            validators : [ 'strLength[0, 22]' ]
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
            editor : true,
            width : 100,
            validators : [ 'strLength[0, 66]' ]
        }, {
            field : 'weighingReportLineId',
            title : 'T_EMC_RUN.WEIGHINGREPORTLINEID',
            editor : true,
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'analReport',
                filter_userId : workspace.user.id,
            }
        }, {
            field : 'auditedDate',
            title : 'T_EMC_RUN.AUDITEDDATE',
            editor : true,
            type : 'date'
        }, {
            field : 'ext$.isshowstdsolution',
            title : 'T_EMC_RUN.ISSHOWSTDSOLUTION',
            editor :true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
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
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
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
            field : 'ext$.accepteddate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
        } , {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
            editor : true,
            validators : [ 'strLength[0, 32]' ]
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width : 80,
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field : 'ext$.isconvert',
            title : 'T_EMC_ORDER.ISCONVERT',
            editor :false,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
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
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            styleFormatter : function(row) {
                if (row.scriptFlag != '1') {
                    return {
                        backgroundColor : 'palegreen'
                    }
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
                            Gikam.getComp('emc-run-edit-edit-list-result-grid').setData({
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
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            editor : true,
            validators : [ 'number' ]
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
            }
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true,
        } ]
    },

    getShowAllField : function() {
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
                workspace.window.showMask(true);
                var grid = Gikam.getComp('emc-run-edit-edit-list-test-run-grid');

                var showFlags = value;
                if (showFlags == '1') {
                    grid.refresh({
                        url : emcRunEdit.runUrl + '/queries/all',
                        requestData : {
                            analysisUserId_SC : '',
                            id_SC : '',
                            testName_SC : '',
                            analysisBeginDate_DGOE : '',
                            analysisEndDate_DLOE : '',
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    grid.refresh({
                        requestData : {
                            url : emcRunEdit.runUrl + '/queries/all',
                            analysisUserId_SC : workspace.user.id,
                            id_SC : '',
                            testName_SC : '',
                            analysisBeginDate_DGOE : '',
                            analysisEndDate_DLOE : '',
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
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
                var testGrid = Gikam.getComp('emc-run-edit-edit-list-sample-test-grid');
                var resultGrid = Gikam.getComp('emc-run-edit-edit-list-result-grid');
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

    getTempHumidityListColumns : function() {
        return [ {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
            editor :true,
            width : 150
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            editor :true,
            width : 150
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'ext$.temphumidityequipt',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.TEMPHUMIDITYEQUIPT',
        }, {
            field : 'beginDate',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDate',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'beginDateOne',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATEONE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDateOne',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATEONE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'beginDateTwo',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATETWO',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDateTwo',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATETWO',
            type : 'date',
            width : 200,
            editor :true,
        }];
    },


    getBatchInputTempHumColumns : function() {
        return [  {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
            editor :true,
            width : 150
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
            editor :true,
            width : 150
        }, {
            field : 'beginDate',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDate',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'beginDateOne',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATEONE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDateOne',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATEONE',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'beginDateTwo',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.BEGINDATETWO',
            type : 'date',
            width : 200,
            editor :true,
        }, {
            field : 'endDateTwo',
            title : 'T_EMC_RUN_TEMP_HUMIDITY.ENDDATETWO',
            type : 'date',
            width : 200,
            editor :true,
        }];
    },

    getRunTaskOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO'
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        } ];
    },

    getQueryFields : function() {
        return [ {
            field : 'runId',
            title : 'T_EMC_RUN.RUNID',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            type : 'date'
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            type : 'date'
        } ]
    },

});
