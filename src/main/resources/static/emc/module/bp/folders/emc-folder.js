var emcFolder = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/folders',

    getFolderListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
        }, {
            field : 'monitorPointAttribute',
            title : 'T_EMC_FOLDER.MONITORPOINTNOATTRIBUTE'
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME'
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME'
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE'
        } ];
    },

    getOrderTaskListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'runId',
            title : 'T_EMC_ORDER_TASK.RUNID'
        }, {
            field : 'analysisOrgName',
            title : 'T_EMC_ORDER_TASK.ANALYSISORGNAME'
        }, {
            field : 'cupNo',
            title : 'T_EMC_ORDER_TASK.CUPNO'
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
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
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME'
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO'
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME',
        } ];
    },

    getOrderTaskResultListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS'
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO'
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field : 'originalResult',
            styleFormatter : function(row) {
                if (row.scriptFlag != '1') {
                    return {
                        backgroundColor : 'palegreen'
                    }
                }
            },
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT'
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT'
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE'
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME'
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT'
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT'
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT'
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS'
        }, {
            field : 'relativeDeviation',
            title : 'T_EMC_ORDER_TASK_RESULT.RELATIVEDEVIATION'
        }, {
            field : 'average ',
            title : 'T_EMC_ORDER_TASK_RESULT.AVERAGE'
        } ];
    },

    getEquiptInfoFields : function() {
        return [ {
            field : 'equiptId',
            title : 'T_EMC_EQUIPT.EQUIPTID',
        }, {
            field : 'equiptName',
            title : 'T_EMC_EQUIPT.EQUIPTNAME',
        }, {
            field : 'equiptEnglishName',
            title : 'T_EMC_EQUIPT.EQUIPTENGLISHNAME',
        }, {
            field : 'specification',
            title : 'T_EMC_EQUIPT.SPECIFICATION',
        }, {
            field : 'manufacturer',
            title : 'T_EMC_EQUIPT.MANUFACTURER',
        }, {
            field : 'factoryDate',
            title : 'T_EMC_EQUIPT.FACTORYDATE',
            type : 'date'
        }, {
            field : 'factoryNo',
            title : 'T_EMC_EQUIPT.FACTORYNO',
        }, {
            field : 'price',
            title : 'T_EMC_EQUIPT.PRICE',
        }, {
            field : 'purchaseDate',
            title : 'T_EMC_EQUIPT.PURCHASEDATE',
            type : 'date'
        }, {
            field : 'arrivalDate',
            title : 'T_EMC_EQUIPT.ARRIVALDATE',
            type : 'date'
        }, {
            field : 'openDate',
            title : 'T_EMC_EQUIPT.OPENDATE',
            type : 'date'
        }, {
            field : 'installDate',
            title : 'T_EMC_EQUIPT.INSTALLDATE',
            type : 'date'
        }, ];
    },

    getResultLogListColumns : function() {
        return [ {
            field : 'createdById',
            title : 'T_EMC_RESULT_LOG.CREATEDBYID'
        }, {
            field : 'createdByName',
            title : 'T_EMC_RESULT_LOG.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_RESULT_LOG.CREATEDTIME'
        }, {
            field : 'reason',
            title : 'T_EMC_RESULT_LOG.REASON'
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_RESULT_LOG.ITEMNAME'
        }, {
            field : 'previousValue',
            title : 'T_EMC_RESULT_LOG.PREVIOUSVALUE'
        }, {
            field : 'value',
            title : 'T_EMC_RESULT_LOG.VALUE'
        }, {
            field : 'reason',
            title : 'T_EMC_RESULT_LOG.REASON'
        }, ];
    },

    getUpdateResultReasonFields : function() {
        return [ {
            field : 'reason',
            title : 'T_EMC_RESULT_LOG.REASON',
            type : 'textarea',
            height : 100,
            validators : [ 'strLength[0,20]', 'notEmpty' ]
        }, ];
    },

    getOrderTaskLogListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'operateCategory',
            title : 'T_EMC_ORDER_TASK_LOG.OPERATECATEGORY'
        }, {
            field : 'ext$.folderno',
            title : 'T_EMC_ORDER_TASK_LOG.FOLDERNO'
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK_LOG.ORDERNO'
        }, {
            field : 'createdTime',
            title : 'T_EMC_ORDER_TASK_LOG.CREATEDTIME'
        }, {
            field : 'createdByName',
            title : 'T_EMC_ORDER_TASK_LOG.CREATEDBYNAME'
        }, {
            field : 'testId',
            title : 'T_EMC_ORDER_TASK_LOG.TESTID'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK_LOG.METHODNAME'
        }, {
            field : 'module',
            title : 'T_EMC_ORDER_TASK_LOG.MODULE'
        } ];
    },

    getEquiptRecordColumns : function() {
        return [ {
            field : 'completeFlag',
            title : 'T_EMC_EQUIPT_RECORD.COMPLETEFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'completeFlag',
            },
        }, {
            field : 'maintEvent',
            title : 'T_EMC_EQUIPT_PLAN.MAINTEVENT',
        }, {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT_RECORD.VERIFYORG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
        }, {
            field : 'certNo',
            title : 'T_EMC_EQUIPT_RECORD.CERTNO',
        }, {
            field : 'certCategory',
            title : 'T_EMC_EQUIPT_RECORD.CERTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'certificatetype'
            },
        }, {
            field : 'certEffectDate',
            title : 'T_EMC_EQUIPT_RECORD.CERTEFFECTDATE',
            type : 'date',
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_RECORD.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_RECORD.CREATEDTIME',
        }, {
            field : 'status',
            title : 'T_EMC_EQUIPT_RECORD.STATUS',
        }, {
            field : 'startStatus',
            title : 'T_EMC_EQUIPT_RECORD.STARTSTATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            }
        }, {
            field : 'airSaturation',
            title : 'T_EMC_EQUIPT_RECORD.AIRSATURATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'workstate'
            }
        }, {
            field : 'addIce',
            title : 'T_EMC_EQUIPT_RECORD.ADDICE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'maintSituation',
            title : 'T_EMC_EQUIPT_RECORD.MAINTSITUATION',
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_RECORD.REMARK',
        }, ];
    },

});
