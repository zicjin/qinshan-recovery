var emcResultInputSample = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',

    getSamplePrepareListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_FOLDER.TESTORGNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY',
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 90,
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'monitorPoint',
            single : true,
            formatter : function(rowIndex, cellValue) {
                if (cellValue && cellValue.indexOf('现场空白') >= 0) {
                    return '现场空白';
                }
                return cellValue;
            },
            onBeforeChoose : function() {
                return {
                    filter_projectId : this.getActivedRow().projId
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorPointName : rows[0].monitorPointName,
                        monitorPointNo : rows[0].monitorPointNo,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorPointName : '',
                    monitorPointNo : '',
                });
            },
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 110,
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            width : 110,
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME',
            width : 110,
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            width : 100,
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        sceneUserId : rows[0].id,
                        sceneUserName : rows[0].userName
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    sceneUserId : '',
                    sceneUserName : '',
                });
            },
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleUserIds = [];
                    var sampleUserNames = [];
                    Gikam.each(rows, function(index, item) {
                        sampleUserIds.push(item.id);
                        sampleUserNames.push(item.userName);
                    });
                    this.setData({
                        index : index,
                        sampleUserId : sampleUserIds.join(','),
                        sampleUserName : sampleUserNames.join(','),
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    sampleUserId : '',
                    sampleUserName : '',
                });
            }
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            width : 90,
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            width : 90,
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK'
        }, ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field : 'sampleOrgId',
            title : 'T_EMC_FOLDER.SAMPLEORGID'
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field : 'sceneUserId',
            title : 'T_EMC_FOLDER.SCENEUSERID'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME'
        }, {
            field : 'sampleUserId',
            title : 'T_EMC_FOLDER.SAMPLEUSERID'
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME'
        }, {
            field : 'monitorPointCategoryId',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORYID'
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK'
        }, {
            field : 'processStatus',
            title : 'T_EMC_FOLDER.PROCESSSTATUS'
        }, ];
    },

    getDataInputColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 100,
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 100,
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            width : 130,
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            width : 100,
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            width : 90,
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            width : 90,
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, ];
    },

    getSamplePrepareOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 70,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 70,
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 90,
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 100,
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO',
            width : 80,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, ];
    },

    // 样品测试
    getOrderTaskListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.monitorobjid',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME'
        }, {
            field : 'ext$.monitorobjname',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field : 'ext$.monitorobjname',
            title : 'T_EMC_ORDER_TASK.FIXATIVE'
        }, {
            field : 'ext$.monitorobjname',
            title : 'T_EMC_ORDER_TASK.TESTMETHOD'
        } ];
    },

    getSamplePrepareOrderContainerColumns : function() {

        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            width : 150,
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            width : 100,
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            width : 100,
        }, {
            field : 'ext$.sampleweight',
            title : 'T_EMC_ORDER_TASK.SAMPLEWEIGHT',
            width : 100,
            editor : true
        } ];
    },

    getProjectListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 90,
            type : 'link'
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            width : 90,
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'status',
            title : 'T_EMC_PROJECT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplingorcancel'
            },
            width : 80,
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            width : 90,
        }, ];
    },

    getProjectSampleColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcResultInputSample.workflow.dbTable), {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 110,
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO',
            width : 90,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            width : 90,
        }, {
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcResultInputSample.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                    id : row.id,
                    tmplId : row.ext$.tmplid,
                    monitorTmplId : row.monitorTmplId,
                    name : '详情信息'
                });
            },
            width : 100,
        }, {
            field : 'dataInput',
            title : 'T_EMC_SAMPLE.DATAINPUT',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcResultInputSample.editPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            },
            width : 110,
        }, {
            field : 'reportConfigLineId',
            title : 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingReport'
            },
            editor : true,
            validators : [ 'notEmpty' ],
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ],
        }, {
            field : 'calibrationId',
            title : 'T_EMC_SAMPLE.CALIBRATION',
            validators : [ 'strLength[0,150]' ],
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcResultInputSample.editPage.loadCalibrationPage(\'' + row.calibrationId + '\')">查看详情</a>';
            },
        }, {
            field : 'caliReportLineId',
            title : 'T_EMC_SAMPLE.CALIREPORTLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'calibrationReport'
            },
            editor : true,
            validators : [ 'notEmpty' ]
        } ];
    },

    getProjectAuditSampleColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcResultInputSample.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            validators : [ 'strLength[0,170]' ],
        }, {
            field : 'dataInput',
            title : 'T_EMC_SAMPLE.DATAINPUT',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcResultInputSample.auditPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            }
        }, {
            field : 'ext$.reportname',
            title : 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
        } ];
    },

    getAuditedSampleDataColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            validators : [ 'strLength[0,170]' ],
        }, {
            field : 'dataInput',
            title : 'T_EMC_SAMPLE.DATAINPUT',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcResultInputSample.auditPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            }
        } ];
    },

    // 样品和测试
    getSampleTestColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 100,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 110,
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getData()[rowIndex];
                var start = new Date(grid.sampleBeginDate);
                var end = new Date(grid.sampleCompleteDate);
                if (start && end && start.getTime() > end.getTime()){
                    Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DATE_LESS_THEN");
                    return;
                }
            }
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            /*type : 'time',*/
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getData()[rowIndex];
                var startDate = new Date(grid.sampleBeginDate);
                var endDate = new Date(grid.sampleCompleteDate);
                if(startDate && endDate && startDate.getTime() === endDate.getTime()){
                    var startTime = grid.sampleBeginTime;
                    var endTime = grid.sampleCompleteTime;
                    if(startTime && endTime && startTime > endTime){
                        Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.TIME_LESS_THEN");
                        return;
                    }
                }
            }
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            editor : true,
            validators : [ 'strLength[0,11]' ],
            width : 110,
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getData()[rowIndex];
                var start = new Date(grid.sampleBeginDate);
                var end = new Date(grid.sampleCompleteDate);
                if (start && end && start.getTime() > end.getTime()){
                    Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DATE_LESS_THEN");
                    return;
                }
            }
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            /*type : 'time',*/
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-result-input-list-data-input-order-task-grid').getData()[rowIndex];
                var startDate = new Date(grid.sampleBeginDate);
                var endDate = new Date(grid.sampleCompleteDate);
                if(startDate && endDate && startDate.getTime() === endDate.getTime()){
                    var startTime = grid.sampleBeginTime;
                    var endTime = grid.sampleCompleteTime;
                    if(startTime && endTime && startTime > endTime){
                        Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.TIME_LESS_THEN");
                        return;
                    }
                }
            }
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 130
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO',
            width : 75,
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
            width : 110,
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 60,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 60,
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 70,
        }, {
            field : 'horizontalCode',
            title : 'T_EMC_ORDER.HORIZONTALCODE',
            width : 90,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'horizontalCode',
            },
            editor : true
        }, {
            field : 'verticalCode',
            title : 'T_EMC_ORDER.VERTICALCODE',
            width : 90,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'verticalCode',
            },
            editor : true
        } ];
    },

    getOrderTaskResultColumns : function(status) {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 60,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 60,
        }, {
            field : 'ext$.samplenum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 80,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width : 90,
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            textStyleFormatter : function() {
                if (status.pageSource !== undefined && status.pageSource === 'report') {
                    return {}
                }
                return {
                    backgroundColor : 'palegreen'
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.scriptFlag === '1') {
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
                            Gikam.getComp('emc-order-task-result-edit-list-grid').setData({
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
                            Gikam.alert(value + '格式错误！请输入正确格式数值！');
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
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName,
                    });
                }
            },
            width : 100,
            editor : true,
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            width : 120,
            single : true,
            validators : [ 'strLength[0,30]' ],
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.SHOWINREPORT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true,
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'resultinputstatus'
            },
            width : 100
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_ORDER.REMARK',
        } ];
    },

    getOrderOperateInfoFormFields : function() {
        return [ {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
            editor : true,
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
            editor : true,
        } ];
    },

    getOrderBatchInfoFormFields : function() {
        return [ {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
        }, ];
    },

    getOrderTaskEquiptColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 110,
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
            width : 110,
        }, {
            field : 'ext$.equiptsid',
            title : 'T_EMC_ORDER_TASK.EQUIPTID',
            width : 90,
        }, {
            field : 'ext$.factoryno',
            title : 'T_EMC_ORDER_TASK.FACTORYNO',
            width : 110,
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME',
            width : 110,
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_ORDER_TASK.SPECIFICATION',
            width : 110,
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_ORDER_TASK.ANALYSISUSERNAME',
            width : 110,
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field : 'assistEquiptName',
            title : 'T_EMC_ORDER_TASK.ASSISTEQUIPTID',
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_ORDER_TASK.PARTAKEUSERNAME',
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_ORDER.REMARK',
        } ];
    },

    getOrderChooseListColumns : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_ORDER.REMARK'
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO'
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO'
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE'
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY'
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM'
        } ];
    },

    getOrderContainerFormFields : function() {
        return [ {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            editor : true,
            validators : [ 'strLength[0,21]' ],
            type : 'choose',
            category : 'containerCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        containerCategoryId : rows[0].id,
                        containerCategoryCode : rows[0].containerCategoryId,
                        containerCategoryName : rows[0].containerCategoryName,
                    });
                }
            },
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            editor : true,
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            editor : true,
            validators : [ 'strLength[0,10]' ],
        }, ];
    },

    // 采样单详情
    getSampleInfoFields : function() {
        return [ {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
        }, ];
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

    getBatchInputContainerColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
            colspan : 1,
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            colspan : 1,
            type : 'choose',
            category : 'containerCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        containerCategoryId : rows[0].id,
                        containerCategoryCode : rows[0].containerCategoryId,
                        containerCategoryName : rows[0].containerCategoryName,
                    });
                }
            },
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            colspan : 1,
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            colspan : 1,
        } ];
    },

    getRepeatNoFields : function() {
        return [ {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNUMBER',
            editor : true,
            value : 1,
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
        }, ];
    },

    getInvalidResultColumns : function() {
        return [ {
            field : 'operatedata',
            title : 'T_EMC_ORDER_TASK_RESULT.OPERATEDATA',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'operatedata'
            },
            colspan : 2,
        }, {
            field : 'reason',
            title : 'T_EMC_ORDER_TASK_RESULT.REASON',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'reason'
            },
            colspan : 2,
            validators : [ 'strLength[0,11]' ],
        }, ];
    },

    getProjectShowAllField : function() {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            refreshGrid : false,
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var projectDataInputAllGrid = Gikam.getComp('emc-sample-result-input-list-project-grid');

                var showFlags = value;

                if (showFlags == '1') {
                    projectDataInputAllGrid.refresh({
                        requestData : {
                            sampleUserId : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    projectDataInputAllGrid.refresh({
                        requestData : {
                            sampleUserId : workspace.user.id,
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getProjectPrepareShowAllField : function() {
        var flag = true;
        return [ {
            title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
            field : 'showFlag',
            refreshGrid : false,
            type : 'simpleCheckbox',
            styleFormatter : function() {
                return {
                    paddingLeft : '0'
                }
            },
            onChange : function(field, value, index) {
                workspace.window.showMask(true);
                var projectDataInputAllGrid = Gikam.getComp('emc-sample-result-input-list-project-prepare-grid');

                var showFlags = value;

                if (showFlags == '1') {
                    projectDataInputAllGrid.refresh({
                        requestData : {
                            sampleUserId : '',
                            projectType : 'folderProject',
                            folderStatus : 'draw'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    projectDataInputAllGrid.refresh({
                        requestData : {
                            sampleUserId : workspace.user.id,
                            projectType : 'folderProject',
                            folderStatus : 'draw'
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getSamplePrepareShowAllField : function() {
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
                var folderAllGrid = Gikam.getComp('emc-sample-result-input-list-grid');

                var showFlags = value;

                if (showFlags == '1') {
                    folderAllGrid.refresh({
                        requestData : {
                            sampleUserId : '',
                            sceneUserId : ''
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    folderAllGrid.refresh({
                        requestData : {
                            sampleUserId : workspace.user.id,
                            sceneUserId : workspace.user.id,
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        } ]
    },

    getOrderDescFormFields : function() {
        return [ {
            field : 'orderColor',
            title : 'T_EMC_SAMPLE.ORDERCOLOR',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderColor'
            },
        }, {
            field : 'orderSmell',
            title : 'T_EMC_SAMPLE.ORDERSMELL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderSmell'
            },
        }, {
            field : 'orderOilSlick',
            title : 'T_EMC_SAMPLE.ORDEROILSLICK',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderOilSlick'
            }
        }, {
            field : 'orderFloater',
            title : 'T_EMC_SAMPLE.ORDERFLOATER',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderFloater'
            }
        } ];
    },

    getSampleTestListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_TASK.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var activedRow = Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').getActivedRow();
                if (Gikam.isEmpty(value)) {
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/delete-subcontractor', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [ activedRow ] ])).done(function() {
                        Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').refreshRowByIndex(rowIndex);
                    });

                    return false;
                }

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/edit-subcontractor', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [ activedRow ] ])).done(function() {
                    Gikam.getComp('emc-sample-result-input-list-data-input-test-info-grid').refreshRowByIndex(rowIndex);
                });

                return false;
            }
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO'
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_SAMPLE',
        columns : [ {
            field : 'id',
            title : 'T_EMC_SAMPLE.ID',
            width : 20,
        } ]
    }

});
