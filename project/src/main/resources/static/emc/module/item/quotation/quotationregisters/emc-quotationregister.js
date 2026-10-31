var emcQuotationregister = Gikam.getPageObject({ 

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQuotationregister.workflow.dbTable), {
            field : 'quotationNo',
            title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNO'
        }, {
            field : 'quotationName',
            title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNAME',
            validators : [ 'notEmpty' ],
            editor : true,
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_QUOTATION_REGISTER.BIZCATEGORYNAME',
            editor : true,
            type: 'choose',
            validators : [ 'notEmpty' ],
            category : 'bizCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        bizCategoryName: rows[0].bizCategoryName,
                    });
                }},
        },  {
            field : 'corePersonName',
            title : 'T_EMC_QUOTATION_REGISTER.COREPERSONNAME',
            editor: true,
            type: 'choose',
            validators : [ 'notEmpty' ],
            category : 'emp',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index: index,
                        corePersonName: rows[0].userName,
                        corePersonCode: rows[0].id,
                    });
                }},
        }];
    },
    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcQuotationregister.workflow.dbTable), {
            field : 'quotationNo',
            title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNO'
        }, {
            field : 'quotationName',
            title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNAME',
            editor : true,
        },  {
            field : 'corePersonName',
            title : 'T_EMC_QUOTATION_REGISTER.COREPERSONNAME',
            editor : true,
        }];
    },
    getInformationTestEditListColumns : function() {
        return [ {
            checkbox : true
        },{
            field : 'testName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTNAME',
            width : 170,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_QUOTATION_INFORMATION.MONITORPOINTNAME',
            width : 120,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        },  {
            field : 'testSamplePrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTSAMPLEPRICE',
            width : 120,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        },  {
            field : 'testSampleDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTSAMPLEDISCOUNT',
            width : 70,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        },  {
            field : 'testSamplePriceAfterCount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTSAMPLEPRICEAFTERCOUNT',
            width : 140,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if (data.testSamplePriceAfterCount && data.testSamplePrice) {
                    var testSampleDiscount = (data.testSamplePriceAfterCount/data.testSamplePrice)*100;
                    if(testSampleDiscount % 1 ===0){
                        testSampleDiscount += ".0%";
                    }else{
                        testSampleDiscount = String(testSampleDiscount ).substring(0,4) + '%';
                    }
                    this.setData({
                        index : index,
                        id : data.id,
                        testSampleDiscount : testSampleDiscount,
                        testSamplePriceAfterCount : value
                    })
                }
            }
        }, {
            field : 'testAnalysisPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTANALYSISPRICE',
            width : 120,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'testAnalysisDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTANALYSISDISCOUNT',
            width : 70,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'testAnalysisPriceAfterCount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTANALYSISPRICEAFTERCOUNT',
            width : 140,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color: 'red'
                    }
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if (data.testAnalysisPriceAfterCount && data.testAnalysisPrice) {
                    var testAnalysisDiscount = (data.testAnalysisPriceAfterCount/data.testAnalysisPrice)*100;
                    if(testAnalysisDiscount % 1 ===0){
                        testAnalysisDiscount += ".0%";
                    }else{
                        testAnalysisDiscount = String(testAnalysisDiscount ).substring(0,4) + '%';
                    }
                    this.setData({
                        index : index,
                        id : data.id,
                        testAnalysisDiscount : testAnalysisDiscount,
                        testAnalysisPriceAfterCount : value
                    })
                }
            }
        },  {
            field : 'testMonitorNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMONITORNUM',
            width : 90,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'testDayNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDAYNUM',
            width : 60,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'testFrequency',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTFREQUENCY',
            width : 60,
            editor : true,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'totalTestPriceAfterCount',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTTOTALPRICE',
            width : 140,
            styleFormatter : function(row) {
                if (row.testName === '检测费用合计') {
                    return {
                        color : 'red'
                    }
                }
            }
        }, {
            field : 'testMethodName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMETHODNAME',
            width : 250
        }];
    },
    getInformationAddEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'addPriceName',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICENAME',
            width : 200
        }, {
            field : 'addUnitPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDUNITPRICE'
        }, {
            field : 'addNumber',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDNUMBER',
            editor : true
        }, {
            field : 'addPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.ADDPRICE'
        }];
    },
    getInformationTotalEditListColumns : function() {
        return [ {
            field : 'totalTestPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALTESTPRICE'
        }, {
            field : 'totalTestPriceAfterCount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALTESTPRICEAFTERCOUNT',
            width : 140
        }, {
            field : 'totalDiscount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALDISCOUNT',
            editor : true
        }, {
            field : 'totalPriceAfterTotalCount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPRICEAFTERTOTALCOUNT',
            width : 140
        }, {
            field : 'totalAddPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALADDPRICE'
        }, {
            field : 'totalPrice',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPRICE'
        }, {
            field : 'totalTaxAmount',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALTAXAMOUNT',
        }, {
            field : 'totalPriceAfterTax',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPRICEAFTERTAX',
            editor : true,
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if (data.totalAddPrice && data.totalTestPrice) {
                    var totalPrice = (value/1.06).toFixed(2);
                    var totalTaxAmount = (value - totalPrice).toFixed(2);
                    var totalPriceAfterTotalCount = (totalPrice - data.totalAddPrice).toFixed(2);
                    var totalDiscount = (totalPriceAfterTotalCount/data.totalTestPriceAfterCount*100).toFixed(2);
                    totalDiscount += "%";
                    this.setData({
                        index : index,
                        id : data.id,
                        totalPrice : totalPrice,
                        totalTaxAmount : totalTaxAmount,
                        totalPriceAfterTotalCount : totalPriceAfterTotalCount,
                        totalDiscount : totalDiscount,
                        totalPriceAfterTax : value
                    })
                }
            }
        }, {
            field : 'totalCost',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALCOST',
        }, {
            field : 'totalProfit',
            title : 'T_EMC_QUOTATION_INFORMATION.TOTALPROFIT',
        }];
    },
    getInformationBatchUpdateFields : function() {
        return [ {
            field : 'testMonitorNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMONITORNUM',
        }, {
            field : 'testDayNum',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTDAYNUM',
        }, {
            field : 'testFrequency',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTFREQUENCY',
        }];
    },
    getInformationItemEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTNAME',
            width : 250
        }, {
            field : 'testMethodName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMETHODNAME',
            width : 250
        }];
    },
    getInformationTestTotalForm : function() {
        return [{
            field : 'testName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTNAME'
        }, {
            field : 'testMethodName',
            title : 'T_EMC_QUOTATION_INFORMATION.TESTMETHODNAME'
        }];
    },

    getBaseInfoFormFields : function() {
        return [{
            title: '报价单信息',
            fields: [ {
                field : 'quotationName',
                validators : [ 'notEmpty' ],
                title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNAME',
            },{
                field : 'quotationNo',
                title : 'T_EMC_QUOTATION_REGISTER.QUOTATIONNO',
                readonly : true,
            },{
                field : 'corePersonName',
                title : 'T_EMC_QUOTATION_REGISTER.COREPERSONNAME',
                type: 'choose',
                validators : [ 'notEmpty' ],
                category : 'user-simple',
                single : true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            corePersonName: rows[0].userName,
                            corePersonCode: rows[0].id,
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        corePersonName: '',
                        corePersonCode: '',
                })}
            }, {
                field : 'bizCategoryName',
                title : 'T_EMC_QUOTATION_REGISTER.BIZCATEGORYNAME',
                type: 'choose',
                validators : [ 'notEmpty' ],
                category : 'bizCategoryQuotation',
                onBeforeChoose : function(){
                    return{
                        type : 'quotation',
                    }
                },
                single : true,
                onAfterChoose : function(rows, index) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            index: index,
                            bizCategoryName: rows[0].bizCategoryName,
                        });
                    }},
                onClean : function() {
                    this.setData({
                        bizCategoryName: '',
                    })}
            }, {
                field : 'contractCategory',
                title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
                validators : [ 'strLength[0, 30]' ],
                type : 'select',
                validators : [ 'notEmpty' ],
                category : 'sys',
                param : {
                    codeCategoryId : 'contractCategory',
                }
            }
            /*, {
                field : 'consultCategory',
                title : 'T_EMC_CONTRACT.CONSULTCATEGORY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'consultCategory',
                },
            }, {
                field : 'detectCategory',
                title : 'T_EMC_CONTRACT.DETECTCATEGORY',
                validators : [ 'strLength[0, 30]' ],
                type : 'select',
                validators : [ 'notEmpty' ],
                category : 'sys',
                param : {
                    codeCategoryId : 'detectCategory',
                },
            }*/]
        },{
            title: '客户信息',
            fields: [ {
                field : 'customerName',
                title : 'T_EMC_QUOTATION_REGISTER.CUSTOMERNAME',
                type: 'choose',
                category : 'contractClient',
                single : true,
                onBeforeChoose : function(index) {
                    return {
                        type : 'contract',
                        hiddenButton : true,
                        clientTag_SEQ : 'entrustor',
                        chargePersonCode_SC :workspace.user.id
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            customerName: rows[0].clientName,
                            customerAddress: rows[0].address,
                            customerPhone : rows[0].phone,
                            contactsName : rows[0].ext$.lxrname,
                            contactsPhone : rows[0].ext$.lxrphone,
                            contactsFax : rows[0].ext$.fax,
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        customerName: '',
                        customerAddress: '',
                        customerPhone : '',
                        contactsName : '',
                        contactsPhone : '',
                        contactsFax : '',
                    })}
            },{
                field : 'customerAddress',
                title : 'T_EMC_QUOTATION_REGISTER.CUSTOMERADDRESS',
            },{
                field : 'customerPhone',
                title : 'T_EMC_QUOTATION_REGISTER.CUSTOMERPHONE',
            },{
                field : 'contactsName',
                title : 'T_EMC_QUOTATION_REGISTER.CONTACTSNAME',
            },{
                field : 'contactsPhone',
                title : 'T_EMC_QUOTATION_REGISTER.CONTACTSPHONE',
            },{
                field : 'contactsFax',
                title : 'T_EMC_QUOTATION_REGISTER.CONTACTSFAX',
            }]
        },{
            title: '公司信息',
            fields: [ {
                field : 'companyName',
                title : 'T_EMC_QUOTATION_REGISTER.COMPANYNAME',
                type: 'choose',
                category : 'client',
                single : true,
                onBeforeChoose : function(index) {
                    return {
                        clientTag_SEQ : 'contractor',
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            companyName: rows[0].clientName,
                            companyAddress: rows[0].address,
                            companyFax : rows[0].mail
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        corePersonName: '',
                        corePersonCode: '',
                    })}
            },{
                field : 'companyAddress',
                title : 'T_EMC_QUOTATION_REGISTER.COMPANYADDRESS',
            },{
                field : 'companyFax',
                title : 'T_EMC_QUOTATION_REGISTER.COMPANYFAX',
            }]
        },{
            title: '备注说明',
            fields: [ {
                field : 'createdByName',
                title : 'T_EMC_QUOTATION_REGISTER.CREATEDBYNAME',
                readonly : true
            },{
                field : 'createdTime',
                title : 'T_EMC_QUOTATION_REGISTER.CREATEDTIME',
                readonly : true
            },{
                field : 'remark',
                title : 'T_EMC_QUOTATION_REGISTER.REMARK',
                type : 'textarea',
                colspan : 2,
                height : 100,
            }]},
        ]
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/quotationregisters',

    informationBaseUrl : IFM_CONTEXT + '/secure/emc/module/item/quotation/quotationinformations',

    workflow : {
        dbTable : 'T_EMC_QUOTATION_REGISTER',
        columns : [ {
            field : 'id',
            title : 'T_EMC_QUOTATION_REGISTER.ID'
        } ]
    }
});
