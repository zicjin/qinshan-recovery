var emcDetectionfactor = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.parentorgname',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.PARENTORGNAME'
        }
        /*, {
            field : 'monitorTmplGroupName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.MONITORTMPLNAME'
        }*/, {
            field : 'testCategoryId',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTCATEGORYID',
            width : 130,
            type : 'select',
            search : true,
            category : 'testCategory'
        }, {
            field : 'testName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTNAME',
            width : 170,
        }, {
            field : 'itemGroupName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ITEMGROUPNAME',
            width : 170,
        },{
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            },
        }, {
            field : 'ext$.methodnamee',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.METHODNAME',
            width : 170,
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTMETHODBASIS',
            width : 170,
        }, {
            field : 'samplePrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.SAMPLEPRICE',
            editor : true
        }, {
            field : 'analysisPrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ANALYSISPRICE',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.REMARK',
            editor : true
        },{
            field : 'createdByName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.CREATEDBYNAME',
            readonly : true
        },{
            field : 'createdTime',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.CREATEDTIME',
            readonly : true
        }
        /*, {
            field : 'trialScope',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TRIALSCOPE',
            type : 'select',
            multiple : true,
            category : 'sys',
            param : {
                codeCategoryId : 'trialScope'
            },
        }*/
        ];
    },

    getChooseListColumns  : function() {
        return [ {
            field : 'monitorTmplGroupName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.MONITORTMPLNAME'
        }, {
            field : 'itemGroupName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTNAME',
            width : 170,
        }, {
            field : 'ext$.methodnamee',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.METHODNAME',
            width : 170,
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTMETHODBASIS',
            width : 170,
        }, {
            field : 'samplePrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.SAMPLEPRICE',
            editor : true
        }, {
            field : 'analysisPrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ANALYSISPRICE',
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.REMARK',
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ID',
            preInsert : true
        }, {
            field : 'monitortmplId',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.MONITORTMPLID'
        }, {
            field : 'testId',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTID'
        }, {
            field : 'methodId',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.METHODID'
        }, {
            field : 'samplePrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.SAMPLEPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'analysisPrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ANALYSISPRICE',
            validators : [ 'strLength[0,11]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.REMARK',
            validators : [ 'strLength[0,90]' ]
        }, ];
    },

    getDetectionfactorBatchUpdateFields : function(){
        return [{
            field : 'samplePrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.SAMPLEPRICE',
        }, {
            field : 'analysisPrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ANALYSISPRICE',
        }];
    },

    getAddCustomPriceFields : function(){
        return [/*{
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUPDETECTION',
            field : 'monitorTmplGroupName',
            type : 'choose',
            category : 'monitorTmplGroup',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var monitorTmplGroupName = Gikam.getComp('emc-emcDetectionfactor-add-custom-price-form').getField('monitorTmplGroupName');
                    monitorTmplGroupName.value = rows[0].monitorTmplGroup;
                }
            },
        },*/{
            field : 'testCategoryId',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTCATEGORYID',
            type : 'select',
            search : true,
            category : 'testCategory'
        },{
            field : 'itemGroupName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.TESTNAME',
        },  {
            field : 'methodName',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.METHODNAME',
            type : 'choose',
            category : 'method',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    // var methodName = Gikam.getComp('emc-emcDetectionfactor-add-custom-price-form').getField('methodName');
                    // methodName.value = rows[0].methodName;
                    this.setData({
                        methodId : rows[0].id,
                        methodName : rows[0].methodName,
                    });
                }
            },
        },{
            field : 'samplePrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.SAMPLEPRICE',
        }, {
            field : 'analysisPrice',
            title : 'T_EMC_QUOTATION_DETECTIONFACTOR.ANALYSISPRICE',
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/detectionfactors',
    schemeUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes',
    testUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests',
});
