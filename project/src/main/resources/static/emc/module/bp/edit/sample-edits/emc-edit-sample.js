var emcSampleEdit = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/edit/sample-edits',
    sampleUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',

    getProjectSampleColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcSampleEdit.workflow.dbTable),{
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            type:'link'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
        }, {
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
            	var monitorTmplId = row.monitorTmplId;
            	if(monitorTmplId != null){
                    return Gikam.printf('<a href="javascript:;" onclick="emcSampleEdit.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : row.monitorTmplId,
                        name : '详情信息'
                    });
            	}else{
                    return Gikam.printf('<a href="javascript:;" onclick="emcSampleEdit.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                        id : row.id,
                        tmplId : row.ext$.tmplid,
                        monitorTmplId : "undefined",
                        name : '详情信息'
                    });
            	}

            }
        }, {
            field: 'sampleSceneUserName',
            title: '实际现场负责人',
            editor: true,
            type: 'choose',
            category: 'user-simple',
            width: 100,
            single: true,
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        sampleSceneUserId: rows[0].id,
                        sampleSceneUserName: rows[0].userName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    sampleSceneUserId: '',
                    sampleSceneUserName: '',
                });
            },
        },{
            field : 'ext$.sampleusername',
            title : 'T_EMC_SAMPLE.SAMPLEUSERNAME',
            editor : true,
            type : 'choose',
            category: 'shuttle-user',
            onBeforeChoose : function(index) {
                return {
                    // status_DIFFER : 'deprecated',
                    // targetType : 'samplePlan',
                    // SAMPLEDEPARTMENT_SEQ : '1',
                    parentorgid_SEQ: workspace.user.ext$.parentorgid
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
                return '<a href="javascript:;" onclick="emcSampleEdit.editPage.loadDataInputPage(\'' + row.id + '\')">数据录入</a>';
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
            validators : [ 'strLength[0,170]' ],
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
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            editor : true,
        } ];
    },

    getSampleTestColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
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
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderdesc',
            },
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
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            editor : true,
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field : 'remark',
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
                    orgId_filter : '1',
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
                    orgId_filter : '1',
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
        } ];
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
                    orgId_filter : '1',
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
        } , {
            field : 'ext$.samplebottleno',
            title : 'T_EMC_SAMPLE_BOTTLE.SAMPLEBOTTLENO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'sampleBottle',
            single : false,
            onBeforeChoose : function(index) {
                return {
                    orgId_filter : '1',
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
            validators : [ 'notEmpty', 'strLength[0,60]' ],
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
            field : 'ext$.oilfilmno',
            title : 'T_EMC_ORDER.OILFILMNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'oilFilm',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    orgId_filter : '1',
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

    getSamplePrepareOrderContainerColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE'
        } ];
    },

    getDataInputColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            editor : true,
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'monitorPoint',
            single : true,
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
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            editor : true,
            type : 'date',
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
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
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            editor : true
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            editor : true
        }, ];
    },

    getOrderTaskResultColumns : function() {
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
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME'
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width : 80,
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER.ORDERDESC',
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
            width : 80,
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

    getOrderTaskEquiptColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'ext$.equiptsid',
            title : 'T_EMC_ORDER_TASK.EQUIPTID'
        }, {
            field : 'ext$.factoryno',
            title : 'T_EMC_ORDER_TASK.FACTORYNO',
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME'
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_ORDER_TASK.SPECIFICATION',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_ORDER_TASK.ANALYSISUSERNAME',
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

    getQueryFields : function() {
        return [ {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        createdById : rows[0].id,
                        createdByName : rows[0].userName
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    createdById : '',
                    createdByName : '',
                });
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, {
            field : 'sceneUserId',
            title : 'T_EMC_FOLDER.SCENEUSERID',
            type : 'choose',
            category : 'user-simple',
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
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            type : 'choose',
            category : 'user-simple',
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
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER_SAMPLE_EDIT.SAMPLEBEGINDATE',
            type : 'date',
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER_SAMPLE_EDIT.SAMPLECOMPLETEDATE',
            type : 'date',
        } ]
    },

    workflow : {
        dbTable : 'T_EMC_SAMPLE',
        columns : [ {
            field : 'id',
            title : 'T_EMC_SAMPLE.ID'
        } ]
    }

});
