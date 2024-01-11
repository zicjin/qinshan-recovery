var emcSample = Gikam.getPageObject({

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
            editor : true,
            width : 90,
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true,
            width : 90,
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            editor : true,
            width : 90,
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            editor : true,
            width : 90,
        },{
            field : 'ext$.riverwidth',
            title : 'T_EMC_FOLDER.RIVERWIDTH',
            editor : true,
        }, {
            field : 'ext$.riverdepth',
            title : 'T_EMC_FOLDER.RIVERDEPTH',
            editor : true,
        }, {
            field : 'ext$.currentspeed',
            title : 'T_EMC_FOLDER.CURRENTSPEED',
            editor : true,
        }, {
            field : 'ext$.flow',
            title : 'T_EMC_FOLDER.FLOW',
            editor : true,
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK',
            editor : true,
        }, {
            field : 'source',
            title : 'T_EMC_FOLDER.SOURCE'
        }];
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
            editor : true,
            width : 90,
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true,
            width : 90,
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            editor : true,
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            editor : true,
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
            field : 'ext$.sampleweight',
            title : 'T_EMC_ORDER.SAMPLEWEIGHT',
            width : 130,
            editor : true
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
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 150
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP'
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
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION',
            width : 100,
        } ];
    },

    getProjectListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 90,
            formatter : function(i, v ,r) {
                var value = '<a href="javascript:;">' + v + '</a>';
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + value;
                }else{
                    return value;
                }
            },
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
        }, Gikam.status.getBpmnColumn(emcSample.workflow.dbTable), {
            field : 'handStatus',
            title : '交接情况',
            width : 110,
            formatter : function(i, v, r) {
                if(v == 'preLogged' && r.ext$.analysiscount != '0') {
                    return '已提交到样品接收';
                }
                if(r.ext$.analysiscount == '0') {
                    return '不需要样品接收';
                }
                if(v != 'preLogged' && r.ext$.analysiscount != '0'){
                    return '未提交到样品接收';
                }
            },
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 110,
            formatter: function (i, v, row) {
                if (!Gikam.isEmpty(v)) {
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.editPage.loadSampleExpandPage(\'{id}\', \'{propertyTmplId}\', \'{monitorTmplId}\',\'{sampleFlag}\', \'{projId}\', \'{orderCategory}\', \'{sampleUserId}\', \'{sampleDate}\')">{v}</a>', {
                        v: v,
                        id: row.id,
                        projId: row.projId,
                        propertyTmplId: row.propertyTmplId,
                        monitorTmplId: row.monitorTmplId,
                        sampleFlag: row.sampleFlag,
                        orderCategory: row.orderCategory,
                        sampleUserId: row.sampleUserId,
                        sampleDate: row.sampleDate
                    });
                }
            },
            sort: false,
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO',
            width : 90
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            width : 90
        }, {
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
            	var monitorTmplId = row.monitorTmplId;
            	if(monitorTmplId != null){
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : row.monitorTmplId,
                        name : '详情信息'
                    });
            	}else{
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : "undefined",
                        name : '详情信息'
                    });
            	}

            }
        }, {
            field : 'ext$.sampleusername',
            title : 'T_EMC_SAMPLE.SAMPLEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'emp',
            onBeforeChoose : function(index) {
                return {
                    status_DIFFER : 'deprecated',
                    targetType : 'samplePlan',
                    SAMPLEDEPARTMENT_SEQ : '1',
                    USERORGID_SB : workspace.user.ext$.parentorgid
                }
            },
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
                        ext$ : {
                            sampleuserid : sampleUserIds.join(','),
                            sampleusername : sampleUserNames.join(','),
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        sampleuserid : '',
                        sampleusername : '',
                    }
                });
            },
        }, {
            field : 'dataInput',
            title : 'T_EMC_SAMPLE.DATAINPUT',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcSample.editPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            },
            width : 110
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
            validators : [ 'notEmpty' ]
        }, {
            field : 'ext$.archivereportlineid',
            title : 'T_EMC_SAMPLE.ARCHIVEREPORTLINEID',
            type : 'select',
            search : true,
            multiple : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingReport'
            },
            editor : true,
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ]
        }, {
            field : 'calibrationId',
            title : 'T_EMC_SAMPLE.CALIBRATION',
            validators : [ 'strLength[0,150]' ],
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcSample.editPage.loadCalibrationPage(\'' + row.calibrationId + '\')">查看详情</a>';
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
        }, Gikam.status.getBpmnColumn(emcSample.workflow.dbTable), {
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
                return '<a href="javascript:;" onclick="emcSample.auditPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            }
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
            validators : [ 'notEmpty' ]
        }, {
            field : 'calibrationId',
            title : 'T_EMC_SAMPLE.CALIBRATION',
            validators : [ 'strLength[0,150]' ],
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcSample.auditPage.loadCalibrationPage(\'' + row.id + '\')">查看详情</a>';
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
        }, {
            field : 'receiveReportLineId',
            title : 'T_EMC_SAMPLE.RECEIVEREPORTLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingNumber'
            },
            editor : true,
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.archivereportlineid',
            title : 'T_EMC_SAMPLE.ARCHIVEREPORTLINEID',
            type : 'select',
            search : true,
            multiple : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingReport'
            },
            editor : true,
        }, {
            field : 'ext$.reviewdate',
            title : 'T_EMC_SAMPLE.REVIEWDATE',
            editor : true,
            type : 'date',
        }, {
            field : 'ext$.auditdate',
            title : 'T_EMC_SAMPLE.AUDITDATE',
            editor : true,
            type : 'date',
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
                return '<a href="javascript:;" onclick="emcSample.auditPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
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
            validators : [ 'notEmpty'],
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            width : 110,
            validators : [ 'notEmpty'],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
        }, {
            field : 'ext$.vocbottle',
            title : 'T_EMC_ORDER.VOCBOTTLE',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'vocBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var vocNumbers = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                        	vocNumbers += rows[index].vocNumber;
                            break;
                        }
                        vocNumbers += rows[index].vocNumber + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                        	vocbottle : vocNumbers
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                    	vocbottle : ''
                    },
                });
            }
        }, {
            field : 'ext$.oilfilmno',
            title : 'T_EMC_ORDER.OILFILMNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'oilFilm',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                        	oilfilmno : rows[0].oilFilmNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                    	oilfilmno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplebottleno',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'sampleBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleBottleNo = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            sampleBottleNo += rows[index].sampleBottleNo;
                            break;
                        }
                        sampleBottleNo += rows[index].sampleBottleNo + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            samplebottleno : sampleBottleNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        samplebottleno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplingdepth',
            title : 'T_EMC_ORDER.SAMPLINGDEPTH',
            editor : true
        }, {
            field : 'ext$.color',
            title : 'T_EMC_ORDER.COLOR',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderColor'
            },
        }, {
            field : 'ext$.texture',
            title : 'T_EMC_ORDER.TEXTURE',
            editor : true
        }, {
            field : 'ext$.odor',
            title : 'T_EMC_ORDER.ODOR',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderSmell'
            },
        }, {
            field : 'ext$.density',
            title : 'T_EMC_ORDER.DENSITY',
            editor : true
        }, {
            field : 'ext$.structure',
            title : 'T_EMC_ORDER.STRUCTURE',
            editor : true
        }, {
            field : 'ext$.gravel',
            title : 'T_EMC_ORDER.GRAVEL',
            editor : true
        }, {
            field : 'ext$.othermatter',
            title : 'T_EMC_ORDER.OTHERMATTER',
            editor : true
        }, {
            field : 'ext$.pidvalue',
            title : 'T_EMC_ORDER.PIDVALUE',
            editor : true
        }, {
            field : 'ext$.batchsize',
            title : 'T_EMC_ORDER.BATCHSIZE',
            editor : true
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            editor : true,
            validators : [ 'strLength[0,36]' ],
            width : 110,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true,
        }  ];
    },

    // 土壤样品和测试
    getSampleTestColumns1 : function() {
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
            validators : [ 'notEmpty' ],
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
        }, {
            field : 'ext$.vocbottle',
            title : 'T_EMC_ORDER.VOCBOTTLE',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'vocBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var vocNumbers = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                        	vocNumbers += rows[index].vocNumber;
                            break;
                        }
                        vocNumbers += rows[index].vocNumber + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                        	vocbottle : vocNumbers
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                    	vocbottle : ''
                    },
                });
            }
        }, {
            field : 'ext$.oilfilmno',
            title : 'T_EMC_ORDER.OILFILMNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'oilFilm',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            oilfilmno : rows[0].oilFilmNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        oilfilmno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplebottleno',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'sampleBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleBottleNo = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            sampleBottleNo += rows[index].sampleBottleNo;
                            break;
                        }
                        sampleBottleNo += rows[index].sampleBottleNo + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            samplebottleno : sampleBottleNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        samplebottleno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplingdepth',
            title : 'T_EMC_ORDER.SAMPLINGDEPTH',
            editor : true
        }, {
            field : 'ext$.color',
            title : 'T_EMC_ORDER.COLOR',
            editable : true,
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderColorNew',
            },
            editor : true,
        }, {
            field : 'ext$.texture',
            title : 'T_EMC_ORDER.TEXTURE',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderTexture',
            },
            editor : true,
        }, {
            field : 'ext$.odor',
            title : 'T_EMC_ORDER.ODOR',
            editor : true,
            type : 'comboBox',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderSmell'
            },
        }, {
            field : 'ext$.density',
            title : 'T_EMC_ORDER.DENSITY',
            editable :true,
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderDensity',
            },
            editor : true,
        }, {
            field : 'ext$.structure',
            title : 'T_EMC_ORDER.STRUCTURE',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderStructure',
            },
            editor : true,
        }, {
            field : 'ext$.gravel',
            title : 'T_EMC_ORDER.GRAVEL',

            editor : true
        }, {
            field : 'ext$.othermatter',
            title : 'T_EMC_ORDER.OTHERMATTER',

            editor : true
        }, {
            field : 'ext$.pidvalue',
            title : 'T_EMC_ORDER.PIDVALUE',
            editor : true
        }, {
            field : 'ext$.weight',
            title : 'T_EMC_ORDER.WEIGHT',
            editor : true
        }, {
            field : 'ext$.rootcontent',
            title : 'T_EMC_ORDER.ROOTCONTENT',
            type : 'comboBox',
            validators : [ 'strLength[0,60]' ],
            category : 'sys',
            param : {
                codeCategoryId : 'rootContent',
            },
            editor : true,
        }, {
            field : 'ext$.humidity',
            title : 'T_EMC_ORDER.HUMIDITY',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'humidity',
            },
            editor : true,
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            editor : true,
            width : 110,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true,
        }, ];
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
            field : 'ext$.sampleweight',
            title : 'T_EMC_ORDER_TASK.SAMPLEWEIGHT',
            width : 100,
            editor : true
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
                if (status.pageSource != undefined && status.pageSource == 'report') {
                    return {}
                }
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
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS'
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

    getOrderResultBatchInputColumns : function() {
        return [ {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 200
        }, {
            field : 'testName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY'
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
            index : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO'
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO2',
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
            field : 'ext$.runids',
            title : 'T_EMC_ORDER_TASK.RUNID'
        }, {
            field : 'ext$.equiptid',
            title : 'T_EMC_ORDER_TASK.EQUIPTID'
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME'
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
            validators : [ 'strLength[0,36]' ],
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE',
            editor : true,
            validators : [ 'strLength[0,66]' ],
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION',
            editor : true,
            validators : [ 'strLength[0,120]' ],
        } ];
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
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION'
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
                var projectDataInputAllGrid = Gikam.getComp('emc-sample-edit-list-project-grid');

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
                var projectDataInputAllGrid = Gikam.getComp('emc-sample-edit-list-project-prepare-grid');

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
                var folderAllGrid = Gikam.getComp('emc-sample-edit-list-grid');

                var showFlags = value;

                if (showFlags == '1') {
                    folderAllGrid.refresh({
                        requestData : {
                            sampleUserId : '',
                            sceneUserId : '',
                            orgId_SB: workspace.user.ext$.parentorgid
                        }
                    }).done(function() {
                        workspace.window.closeMask();
                    });
                } else {
                    folderAllGrid.refresh({
                        requestData : {
                            sampleUserId : workspace.user.id,
                            sceneUserId : workspace.user.id,
                            orgId_SB: workspace.user.ext$.parentorgid
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
        }, {
            field : 'samplingDepth',
            title : 'T_EMC_SAMPLE.SAMPLINGDEPTH',
        }, {
            field : 'samplingNum',
            title : 'T_EMC_SAMPLE.SAMPLINGNUM',
        } ];
    },

    getSoliOrderDescFormFields : function() {
        return [ {
            field : 'ext$.samplingdepth',
            title : 'T_EMC_ORDER.SAMPLINGDEPTH',
        }, {
            field : 'ext$.color',
            title : 'T_EMC_ORDER.COLOR',
            /*editable : true,
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderColorNew',
            },*/
        }, {
            field : 'ext$.othermatter',
            title : 'T_EMC_ORDER.OTHERMATTER',

            editor : true
        }, {
            field : 'ext$.texture',
            title : 'T_EMC_ORDER.TEXTURE',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderTexture',
            },
            editor : true,
        }/*, {
            field : 'ext$.odor',
            title : 'T_EMC_ORDER.ODOR',
            editor : true,
            type : 'comboBox',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderSmell'
            },
        }, {
            field : 'ext$.density',
            title : 'T_EMC_ORDER.DENSITY',
            editable :true,
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderDensity',
            },
            editor : true,
        }, {
            field : 'ext$.structure',
            title : 'T_EMC_ORDER.STRUCTURE',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderStructure',
            },
            editor : true,
        }, {
            field : 'ext$.gravel',
            title : 'T_EMC_ORDER.GRAVEL',

            editor : true
        }*//*, {
            field : 'ext$.pidvalue',
            title : 'T_EMC_ORDER.PIDVALUE',
            editor : true
        }, {
            field : 'ext$.weight',
            title : 'T_EMC_ORDER.WEIGHT',
            editor : true
        }*/, {
            field : 'ext$.rootcontent',
            title : 'T_EMC_ORDER.ROOTCONTENT',
            type : 'comboBox',
            validators : [ 'strLength[0,60]' ],
            category : 'sys',
            param : {
                codeCategoryId : 'rootContent',
            },
            editor : true,
        }, {
            field : 'ext$.humidity',
            title : 'T_EMC_ORDER.HUMIDITY',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'humidity',
            },
            editor : true,
        }/*, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true,
        }*/
        ];
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
                var activedRow = Gikam.getComp('emc-sample-edit-list-data-input-test-grid').getActivedRow();
                if (Gikam.isEmpty(value)) {
                    Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/delete-subcontractor', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [ activedRow ] ])).done(function() {
                        Gikam.getComp('emc-sample-edit-list-data-input-test-grid').refreshRowByIndex(rowIndex);
                    });

                    return false;
                }

                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/action/edit-subcontractor', Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', [ activedRow ] ])).done(function() {
                    Gikam.getComp('emc-sample-edit-list-data-input-test-grid').refreshRowByIndex(rowIndex);
                });

                return false;
            }
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO'
        } ];
    },

    getVocBottleColumns : function() {
        return [ {
            field : 'vocNumber',
            title : 'T_EMC_VOC_WEIGHTING.VOCNUMBER'
        },{
            field : 'projNo',
            title : 'T_EMC_VOC_WEIGHTING.PROJNO'
        }, {
            field : 'emptyBottleWeight',
            title : 'T_EMC_VOC_WEIGHTING.EMPTYBOTTLEWEIGHT'
        }];
    },

    getOilFilmColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'oilFilmNo',
            title : 'T_EMC_OIL_FILM.OILFILMNO',
        }, {
            field : 'weighingUserName',
            title : 'T_EMC_OIL_FILM.WEIGHINGUSERNAME',
        }, {
            field : 'projId',
            title : 'T_EMC_OIL_FILM.PROJID',
        }, {
            field : 'projNo',
            title : 'T_EMC_OIL_FILM.PROJNO',
            type : 'choose',
            category : 'project',
            single : true,
            onBeforeChoose : function() {
                return {
        	            orgId_SB : workspace.user.ext$.parentorgid,
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        projId :  rows[0].id,
                        projNo : rows[0].projNo,
                        projName : rows[0].projName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projNo : '',
                    projName : '',
                });
            },
        }, {
            field : 'weighingDate',
            title : 'T_EMC_OIL_FILM.WEIGHINGDATE',
            type : 'date',
        }, {
            field : 'emptyFilterBarrelMOneOne',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEONE',
        }, {
            field : 'emptyFilterBarrelMOneTwo',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONETWO',
        }, {
            field : 'emptyFilterBarrelMOneThree',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONETHREE',
        }, {
            field : 'emptyFilterBarrelMOneFour',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEFOUR',
        }, {
            field : 'emptyFilterBarrelMOneFive',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONEFIVE',
        }, {
            field : 'emptyFilterBarrelMOneSix',
            title : 'T_EMC_OIL_FILM.EMPTYFILTERBARRELMONESIX',
        } ];
    },

    getSamplePrepareFolderColumns: function () {
        return [ {
            checkbox : true
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
            editor : true,
            width : 90,
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true,
            width : 90,
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            editor : true,
            width : 90,
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            editor : true,
            width : 90,
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK',
            editor : true,
        } ];
    },

    getDataInputOrderColumns: function () {
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
            validators : [ 'notEmpty'],
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            width : 110,
            validators : [ 'notEmpty'],
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-edit-list-data-input-order-task-grid').getData()[rowIndex];
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
        }, {
            field : 'ext$.vocbottle',
            title : 'T_EMC_ORDER.VOCBOTTLE',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'vocBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var vocNumbers = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            vocNumbers += rows[index].vocNumber;
                            break;
                        }
                        vocNumbers += rows[index].vocNumber + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            vocbottle : vocNumbers
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        vocbottle : ''
                    },
                });
            }
        }, {
            field : 'ext$.oilfilmno',
            title : 'T_EMC_ORDER.OILFILMNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'oilFilm',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            oilfilmno : rows[0].oilFilmNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        oilfilmno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplebottleno',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'sampleBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_SEQ: workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleBottleNo = "";
                    for ( let index in rows) {
                        if (index == rows.length - 1) {
                            sampleBottleNo += rows[index].sampleBottleNo;
                            break;
                        }
                        sampleBottleNo += rows[index].sampleBottleNo + ",";
                    }
                    this.setData({
                        id : this.getActivedRow().id,
                        ext$ : {
                            samplebottleno : sampleBottleNo
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        samplebottleno : ''
                    },
                });
            }
        }, {
            field : 'ext$.samplingdepth',
            title : 'T_EMC_ORDER.SAMPLINGDEPTH',
            editor : true
        }, {
            field : 'ext$.color',
            title : 'T_EMC_ORDER.COLOR',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderColor'
            },
        }, {
            field : 'ext$.texture',
            title : 'T_EMC_ORDER.TEXTURE',
            editor : true
        }, {
            field : 'ext$.odor',
            title : 'T_EMC_ORDER.ODOR',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orderSmell'
            },
        }, {
            field : 'ext$.density',
            title : 'T_EMC_ORDER.DENSITY',
            editor : true
        }, {
            field : 'ext$.structure',
            title : 'T_EMC_ORDER.STRUCTURE',
            editor : true
        }, {
            field : 'ext$.gravel',
            title : 'T_EMC_ORDER.GRAVEL',
            editor : true
        }, {
            field : 'ext$.othermatter',
            title : 'T_EMC_ORDER.OTHERMATTER',
            editor : true
        }, {
            field : 'ext$.pidvalue',
            title : 'T_EMC_ORDER.PIDVALUE',
            editor : true
        }, {
            field : 'ext$.batchsize',
            title : 'T_EMC_ORDER.BATCHSIZE',
            editor : true
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            editor : true,
            validators : [ 'strLength[0,36]' ],
            width : 110,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true,
        }  ];
    },

    getContainerColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
            width: 200,
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.LABELNUMBER',
            width: 140
        }, {
            field: 'ext$.material',
            title: 'T_EMC_CONTAINER_CATEGORY.MATERIAL'
        }, {
            field: 'ext$.color',
            title: 'T_EMC_CONTAINER_CATEGORY.COLOR'
        }, {
            field: 'ext$.capacity',
            title: 'T_EMC_CONTAINER_CATEGORY.CAPACITY'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE',
            width: 300,
        }, {
            field: 'samplingVolume',
            title: 'T_EMC_ORDER_TASK.SAMPLINGVOLUME'
        }, {
            field: 'storageCondition',
            title: 'T_EMC_ORDER_TASK.STORAGECONDITION'
        }, {
            field: 'containerOrderNo',
            title: 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERORDERNO'
        }];
    },

    getSampleExpandTaskDataListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field: 'ext$.num',
            title: 'T_EMC_ORDER_TASK.ORDERNUM',
        }, {
            field: 'ext$.progress',
            title: 'T_EMC_ORDER_TASK.PROGRESS',
        }]
    },

    getSampleExpandResultDataListColumns: function () {
        return [{
            index: true
        }, {
            checkbox: true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO',
            width: 140,
            fixed: true
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCNAME',
            fixed: true
        }]
    },

    getSampleDataCheckListColumns: function () {
        return [{
            index: true
        }, {
            field: 'remind',
            title: 'T_EMC_RUN_REMIND.REMIND',
            width: 200
        }, {
            field: 'ext$.matter',
            title: 'T_EMC_RUN_REMIND.MATTER',
            width: 200
        }, {
            field: 'ext$.result',
            title: 'T_EMC_RUN_REMIND.RESULT',
            styleFormatter: function (row) {
                if (row.ext$.result === '异常') {
                    return {
                        color: 'red'
                    }
                } else {
                    return {
                        color: 'green'
                    }
                }
            },
            width: 200
        }, {
            field: 'ext$.explain',
            title: 'T_EMC_RUN_REMIND.EXPLAIN',
            width: 200
        }];
    },

    getSampleEquiptColumns: function () {
        return [{
            index: true
        }, {
            field: 'ext$.testname',
            title: '测试名称'
        }, {
            field: 'ext$.methodname',
            title: '测试方法'
        }, {
            field: 'ext$.equiptno',
            title: 'T_EMC_EQUIPT.EQUIPTID'
        }, {
            field: 'ext$.equiptname',
            title: 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field: 'ext$.factoryno',
            title: 'T_EMC_EQUIPT.FACTORYNO'
        }, {
            field: 'ext$.specification',
            title: 'T_EMC_EQUIPT.SPECIFICATION'
        }, {
            field: 'ext$.status',
            title: 'T_EMC_EQUIPT.STATUS',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'equiptStatus',
            }
        }];
    },

    getBrowseRecordsProjRcdCategoryColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'sampleNo',
            title: 'T_EMC_SAMPLE.SAMPLENO',
        }, {
            field: 'reportConfigLineId',
            title: 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
            type: 'select',
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'samplingNumber'
            }
        }];
    },

    awaitSampleListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.projno',
            title: '任务编号',
        }, {
            field: 'ext$.projname',
            title: '任务名称',
        }, {
            field: 'ext$.testorgname',
            title: '受检单位',
        }, {
            field: 'ext$.projcreatedbyname',
            title: '登记人',
        }, {
            field: 'ext$.folderno',
            title: '点位编号',
        }, {
            field: 'ext$.monitorpointname',
            title: '点位名称',
        }, {
            field: 'ext$.plansampledate',
            title: '计划采样日期',
            type: 'date'
        }, {
            field: 'ext$.sampleusername',
            title: '采样人',
        }, {
            field: 'orderNo',
            title: '样品编号',
        }, {
            field: 'orderCategory',
            title: '样品类型',
        }, {
            field: 'ext$.testname',
            title: '监测项目',
        }];
    },

    getContainerPrepareColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.num',
            title: '所需容器数'
        }, {
            field: 'ext$.monitornum',
            title: '相关点位数'
        }, {
            field: 'ext$.material',
            title: 'T_EMC_CONTAINER_CATEGORY.MATERIAL'
        }, {
            field: 'ext$.color',
            title: 'T_EMC_CONTAINER_CATEGORY.COLOR'
        }, {
            field: 'ext$.capacity',
            title: 'T_EMC_CONTAINER_CATEGORY.CAPACITY'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE',
            width: 300,
        }, {
            field: 'samplingVolume',
            title: 'T_EMC_ORDER_TASK.SAMPLINGVOLUME'
        }, {
            field: 'storageCondition',
            title: 'T_EMC_ORDER_TASK.STORAGECONDITION'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME',
            width: 200
        }];
    },

    getOrderContainerChooseListColumns : function() {
        return [ {
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 150
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP'
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
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION',
            width : 100,
        }  ];
    },
    getSampleQuerySearchColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcSample.workflow.dbTable), {
            field : 'handStatus',
            title : '交接情况',
            width : 110,
            formatter : function(i, v, r) {
                if(v == 'preLogged' && r.ext$.analysiscount != '0') {
                    return '已提交到样品接收';
                }
                if(r.ext$.analysiscount == '0') {
                    return '不需要样品接收';
                }
                if(v != 'preLogged' && r.ext$.analysiscount != '0'){
                    return '未提交到样品接收';
                }
            },
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 110,
            formatter: function (i, v, row) {
                if (!Gikam.isEmpty(v)) {
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.sampleQuerySearchPage.loadSampleExpandPage(\'{id}\', \'{propertyTmplId}\', \'{monitorTmplId}\',\'{sampleFlag}\', \'{projId}\', \'{orderCategory}\', \'{sampleUserId}\', \'{sampleDate}\')">{v}</a>', {
                        v: v,
                        id: row.id,
                        projId: row.projId,
                        propertyTmplId: row.propertyTmplId,
                        monitorTmplId: row.monitorTmplId,
                        sampleFlag: row.sampleFlag,
                        orderCategory: row.orderCategory,
                        sampleUserId: row.sampleUserId,
                        sampleDate: row.sampleDate
                    });
                }
            },
            sort: false,
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO',
            width : 90
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            width : 90
        }, {
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
                var monitorTmplId = row.monitorTmplId;
                if(monitorTmplId != null){
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.sampleQuerySearchPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : row.monitorTmplId,
                        name : '详情信息'
                    });
                }else{
                    return Gikam.printf('<a href="javascript:;" onclick="emcSample.sampleQuerySearchPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : "undefined",
                        name : '详情信息'
                    });
                }

            }
        }, {
            field : 'ext$.sampleusername',
            title : 'T_EMC_SAMPLE.SAMPLEUSERNAME',
            type : 'choose',
            category : 'emp',
            onBeforeChoose : function(index) {
                return {
                    status_DIFFER : 'deprecated',
                    targetType : 'samplePlan',
                    SAMPLEDEPARTMENT_SEQ : '1',
                    USERORGID_SB : workspace.user.ext$.parentorgid
                }
            },
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
                        ext$ : {
                            sampleuserid : sampleUserIds.join(','),
                            sampleusername : sampleUserNames.join(','),
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                        sampleuserid : '',
                        sampleusername : '',
                    }
                });
            },
        }, {
            field : 'dataInput',
            title : 'T_EMC_SAMPLE.DATAINPUT',
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcSample.sampleQuerySearchPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
            },
            width : 110
        }, {
            field : 'reportConfigLineId',
            title : 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingReport'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'ext$.archivereportlineid',
            title : 'T_EMC_SAMPLE.ARCHIVEREPORTLINEID',
            type : 'select',
            search : true,
            multiple : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'samplingReport'
            },
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            validators : [ 'strLength[0,170]' ]
        }, {
            field : 'calibrationId',
            title : 'T_EMC_SAMPLE.CALIBRATION',
            validators : [ 'strLength[0,150]' ],
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcSample.sampleQuerySearchPage.loadCalibrationPage(\'' + row.calibrationId + '\')">查看详情</a>';
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
            validators : [ 'notEmpty' ]
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
