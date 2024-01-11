var emcOrderReceipt = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/order/order-receipts',

    getSampleListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 130,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'ext$.testorgname',
            width : 180,
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'ext$.entrustorgname',
            width : 180,
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 150
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
            width : 150,
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            width : 120
        }, {
            field : 'ext$.samplesenddate',
            width : 150,
            title : 'T_EMC_SAMPLE.SAMPLEBEGINDATE'
        }, {
            field : 'ext$.samplesendusername',
            title : 'T_EMC_SAMPLE.SAMPLESENDUSERNAME'
        }, {
            field : 'accepteUserName',
            title : 'T_EMC_SAMPLE.ACCEPTEUSERNAME',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            validators : [ 'notEmpty' ],
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        acceptedUserId : rows[0].id,
                        accepteUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    acceptedUserId : '',
                    accepteUserName : '',
                });
            },
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
        }, {
            field : 'acceptedTime',
            title : 'T_EMC_SAMPLE.ACCEPTEDTIME',
            type : 'time',
            editor : true,
            validators : [ 'notEmpty' ],
        } , {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK'
        }, {
            field : 'sepSampleTime',
            title : 'T_EMC_SAMPLE.SEPSAMPLETIME',
            type : 'date',
            width : 150,
            readonly : true
        }, {
            field : 'ext$.compeletedate',
            title : 'T_EMC_SAMPLE.COMPELETEDATE',
            type : 'date',
            width : 150,
            editor: true,
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
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcOrderReceipt.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\', \'{bizCategorycode}\')">{name}</a>', {
                    id : row.id,
                    tmplId : row.ext$.tmplid,
                    monitorTmplId : row.monitorTmplId,
                    bizCategorycode : row.ext$.bizcategorycode,
                    name : '详情信息'
                });
            },
            width : 100,
        }, {
            field : 'id',
            title : 'GIKAM.BUTTON.APPENDIX_INFO',
            formatter : function(i, v) {
                return '<a href="javascript:;" onclick="emcOrderReceipt.editPage.viewSampleFile(\'' + v + '\')">附件信息</a>';
            },
            width : 120
        }, {
            field : 'qcUserName',
            title : 'T_EMC_SAMPLE.QCUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                return {
                    orderQcUser_SEQ : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-order-receipt-edit-list-sample-grid').getActivedRow().id,
                        qcUserName : rows[0].userName,
                        qcUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-order-receipt-edit-list-sample-grid').getActivedRow().id,
                    qcUserName : '',
                    qcUserId : ''
                });
            }
        }, {
            field : 'sampleCheck',
            title : 'T_EMC_SAMPLE.SAMPLECHECK',
            editor : true,
            type : 'select',
            validators : ['notEmpty'],
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            },
            onChange : function(field, value, rowIndex) {
                if (value === 'yes') {
                    this.setData({
                        index : rowIndex,
                        sampleCheck : value,
                        usedContainer : value,
                        storageValid : value,
                        exchangeValid : value,
                        samplingValid : value,
                        markValid : value,
                        containerValid : value
                    });
                }
            }
        }, {
            field : 'usedContainer',
            title : 'T_EMC_SAMPLE.USEDCONTAINER',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'storageValid',
            title : 'T_EMC_SAMPLE.STORAGEVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'exchangeValid',
            title : 'T_EMC_SAMPLE.EXCHANGEVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'samplingValid',
            title : 'T_EMC_SAMPLE.SAMPLINGVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'markValid',
            title : 'T_EMC_SAMPLE.MARKVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'containerValid',
            title : 'T_EMC_SAMPLE.CONTAINERVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'printFlag',
            title : 'T_EMC_SAMPLE.PRINTFLAG',
            editor : true,
        } ];
    },

    getBatchInputFields : function() {
        return [ {
            field : 'qcUserName',
            title : 'T_EMC_SAMPLE.QCUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                return {
                    orderQcUser_SEQ : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        qcUserName : rows[0].userName,
                        qcUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    qcUserName : '',
                    qcUserId : ''
                });
            }
        }, {
            field : 'sampleCheck',
            title : 'T_EMC_SAMPLE.SAMPLECHECK',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'printFlag',
            title : 'T_EMC_SAMPLE.PRINTFLAG',
            editor : true,
        }, {
            field : 'acceptedUserId',
            title : 'T_EMC_SAMPLE.ACCEPTEDUSERID',
            editor : true,

        }, {
            field : 'accepteUserName',
            title : 'T_EMC_SAMPLE.ACCEPTEUSERNAME',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        acceptedUserId : rows[0].id,
                    	accepteUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    acceptedUserId : '',
                    accepteUserName : '',
                });
            },
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            editor : true,
        }, {
            field : 'ext$.compeletedate',
            title : 'T_EMC_SAMPLE.COMPELETEDATE',
            type : 'date',
            editor: true,
        } ]
    },

    getOrderListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 130,
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_RECEIPT.MONITORPOINTNAME'
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_ORDER_RECEIPT.MONITORPOINTNO'
        }, {
            field : 'ext$.storagelocation',
            title : 'T_EMC_ORDER.STORAGELOCATION',
            editor :true,
        }, {
            field : 'ext$.storageconditions',
            title : 'T_EMC_ORDER.STORAGECONDITIONS',
            editor :true,
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME'
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO',
            formatter : function(i, v, row) {
                if (!v) {
                    return 'N/A'
                }
                return v;
            }
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            width : 140,
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            width : 140,
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            width : 140,
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            width : 140,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 120,
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO'
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 100,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 100,
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 100,
        } ];
    },

    getTestListColumns : function() {
        return [ {
            checkbox : true
        },{
            index : true
        }, {
            field : 'schemeName',
            title : 'T_EMC_ORDER_TASK.SCHEMENAME',
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME'
        }, {
            field : 'ext$.assigndate',
            title : 'T_EMC_ORDER_TASK.ASSIGNDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS'
        } ];
    },

    getContainerListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION'
        }, {
            field : 'isScan',
            title : 'T_EMC_ORDER_CONTAINER.ISSCAN'
        } ];
    },

    getQcCategoryChooseListColumns : function() {
        return [ {
            field : 'qcCategoryId',
            title : 'T_EMC_QC_CATEGORY.QCCATEGORYID'
        }, {
            field : 'qcName',
            title : 'T_EMC_QC_CATEGORY.QCNAME'
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
        }, ];
    },

    getBatchInputOrderFields : function() {
        return [  {
            field : 'ext$.storagelocation',
            title : 'T_EMC_ORDER.STORAGELOCATION',
            editor :true,
        }, {
            field : 'ext$.storageconditions',
            title : 'T_EMC_ORDER.STORAGECONDITIONS',
            editor :true,
        }, ];
    },

    getAddTestListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 130,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 150
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
            width : 150,
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME',
            width : 120
        }, {
            field : 'accepteUserName',
            title : 'T_EMC_SAMPLE.ACCEPTEUSERNAME',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            validators : [ 'notEmpty' ],
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        acceptedUserId : rows[0].id,
                        accepteUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    acceptedUserId : '',
                    accepteUserName : '',
                });
            },
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.assigndate',
            title : 'T_EMC_SAMPLE.ASSIGNDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
        } , {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK'
        }, {
            field : 'sepSampleTime',
            title : 'T_EMC_SAMPLE.SEPSAMPLETIME',
            type : 'date',
            width : 150,
            readonly : true
        }, {
            field : 'ext$.compeletedate',
            title : 'T_EMC_SAMPLE.COMPELETEDATE',
            type : 'date',
            width : 150,
            editor: true,
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
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcOrderReceipt.editAddTestPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\', \'{bizCategorycode}\')">{name}</a>', {
                    id : row.id,
                    tmplId : row.ext$.tmplid,
                    monitorTmplId : row.monitorTmplId,
                    bizCategorycode : row.ext$.bizcategorycode,
                    name : '详情信息'
                });
            },
            width : 100,
        }, {
            field : 'id',
            title : 'GIKAM.BUTTON.APPENDIX_INFO',
            formatter : function(i, v) {
                return '<a href="javascript:;" onclick="emcOrderReceipt.editAddTestPage.viewSampleFile(\'' + v + '\')">附件信息</a>';
            },
            width : 120
        }, {
            field : 'qcUserName',
            title : 'T_EMC_SAMPLE.QCUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                return {
                    orderQcUser_SEQ : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-order-receipt-edit-list-sample-grid').getActivedRow().id,
                        qcUserName : rows[0].userName,
                        qcUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-order-receipt-edit-list-sample-grid').getActivedRow().id,
                    qcUserName : '',
                    qcUserId : ''
                });
            }
        }, {
            field : 'sampleCheck',
            title : 'T_EMC_SAMPLE.SAMPLECHECK',
            editor : true,
            type : 'select',
            validators : ['notEmpty'],
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            },
            onChange : function(field, value, rowIndex) {
                if (value === 'yes') {
                    this.setData({
                        index : rowIndex,
                        sampleCheck : value,
                        usedContainer : value,
                        storageValid : value,
                        exchangeValid : value,
                        samplingValid : value,
                        markValid : value,
                        containerValid : value
                    });
                }
            }
        }, {
            field : 'usedContainer',
            title : 'T_EMC_SAMPLE.USEDCONTAINER',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'storageValid',
            title : 'T_EMC_SAMPLE.STORAGEVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'exchangeValid',
            title : 'T_EMC_SAMPLE.EXCHANGEVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'samplingValid',
            title : 'T_EMC_SAMPLE.SAMPLINGVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'markValid',
            title : 'T_EMC_SAMPLE.MARKVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'containerValid',
            title : 'T_EMC_SAMPLE.CONTAINERVALID',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'conformClaim'
            }
        }, {
            field : 'printFlag',
            title : 'T_EMC_SAMPLE.PRINTFLAG',
            editor : true,
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_SAMPLE',
        columns : [ {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO'
        } ]
    }

});
