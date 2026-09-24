var emcContract = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/contracts',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }/*, Gikam.status.getBpmnColumn(emcContract.workflow.dbTable), {
            field : 'ext$.reportconfiglineid',
            title : 'T_EMC_CONTRACT.REPORTCONFIGLINEID',
            editor : true,
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            validators : [ 'notEmpty' ],
            param : {
                reportConfigId : 'contract'
            }
        }*/, {
            field : 'contractNo',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }/*, {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
            editor : true
        }*/, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG'
        }, {
            field : 'ext$.contractcategory',
            title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'contractCategory',
            },
        }/*, {
            field : 'ext$.detectcategory',
            title : 'T_EMC_CONTRACT.DETECTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'detectCategory',
            },
        }*/ ];
    },

    /** 基本信息 - 分组 */
    getGroupBaseInfoFormFields : function() {
        return  [{
            title: 'EMC.MODULE.MDM.CONTRACTS.TITLE.ENTRUSTOR',
            fields: [{
                field : 'ext$.entrustorgname',
                title : 'T_EMC_CONTRACT.ENTRUSTORG',
                validators : [ 'notEmpty', 'strLength[0, 120]' ],
                type : 'choose',
                category : 'client',
                single : true,
                editable : true,
                onBeforeChoose : function() {
                    return {
                        clientTag_SEQ : 'entrustor'
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            entrustOrg : rows[0].id,
                            ext$ : {
                                entrustorgname : rows[0].clientName,
                                entrustorgaddress : rows[0].address,
                                entrustorglicense : rows[0].creditCode,
                                entrustor : rows[0].ext$.lxrname,
                                entrustorgphone : rows[0].ext$.lxrphone
                            },
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        entrustOrg : '',
                        ext$ : {
                            entrustorgname : '',
                            entrustorgaddress : '',
                            entrustorglicense : '',
                            entrustor : '',
                            entrustorgphone : ''
                        },
                    });
                }
            }, {
                field : 'ext$.entrustorgaddress',
                title : 'T_EMC_CONTRACT.ORGADDRESS',
                validators : [ 'notEmpty', 'strLength[0, 255]' ],
                colspan : 2
            }/*, {
                field : 'ext$.entrustorglicense',
                title : 'T_EMC_CONTRACT.BUSINESSLICENSE',
                validators : [ 'notEmpty', 'strLength[0, 66]' ],
            }*/, {
                field : 'ext$.entrustor',
                title : 'T_EMC_CONTRACT.ENTRUSTOR',
                validators : [ 'notEmpty', 'strLength[0, 36]' ],
            }, {
                field : 'ext$.entrustorgphone',
                title : 'T_EMC_CONTRACT.PHONE',
                validators : [ 'notEmpty', 'strLength[0, 30]' ],
            }]
        }, {
            title: 'EMC.MODULE.MDM.CONTRACTS.TITLE.CONTRACTOR',
            fields: [{
                field : 'ext$.contractorname',
                title : 'T_EMC_CONTRACT.ORGNAME',
                validators : [ 'strLength[0, 120]' ],
                type : 'choose',
                category : 'client',
                single : true,
                editable : true,
                onBeforeChoose : function() {
                    return {
                        clientTag_SEQ : 'contractor'
                    }
                },
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            ext$ : {
                                contractororgid : rows[0].id,
                                contractorname : rows[0].clientName,
                                contractoraddress : rows[0].address,
                                contractorlicense : rows[0].creditCode,
                                contractorbank : rows[0].ext$.openingbank,
                                contractoraccount : rows[0].ext$.bankaccount,
                                contractorphone : rows[0].phone
                            }
                        });
                    }
                },
                onClean : function() {
                    this.setData({
                        ext$ : {
                            contractororgid : '',
                            contractorname : '',
                            contractoraddress : '',
                            contractorlicense : '',
                            contractorbank : '',
                            contractoraccount : '',
                            contractorphone : ''
                        }
                    });
                }
            }, {
                field : 'ext$.contractoraddress',
                title : 'T_EMC_CONTRACT.ORGADDRESS',
                validators : [ 'strLength[0, 255]' ],
                colspan : 2
            }/*, {
                field : 'ext$.contractorlicense',
                title : 'T_EMC_CONTRACT.BUSINESSLICENSE',
                validators : [ 'strLength[0, 66]' ],
            }, {
                field : 'ext$.contractorbank',
                title : 'T_EMC_CONTRACT.OPENINGBANK',
                validators : [ 'strLength[0, 120]' ],
            }, {
                field : 'ext$.contractoraccount',
                title : 'T_EMC_CONTRACT.BANKACCOUNT',
                validators : [ 'strLength[0, 66]' ],
            }*/, {
                field : 'ext$.contractorphone',
                title : 'T_EMC_CONTRACT.PHONE',
                validators : [ 'strLength[0, 30]' ],
            }]
        }, {
            title: '',
            fields: [ {
                field : 'contractNo',
                title : 'T_EMC_CONTRACT.CONTRACTNO',
                validators : [ 'strLength[0, 30]' ],
                readonly : true
            }/*, {
                field : 'contractName',
                title : 'T_EMC_CONTRACT.CONTRACTNAME',
                validators : [ 'notEmpty', 'strLength[0, 66]' ],
            }*/, {
                field : 'ext$.contractcategory',
                title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'contractCategory',
                },
                validators : [ 'notEmpty' ],
                readonly : true
            }/*, {
                field : 'ext$.detectionnature',
                title : 'T_EMC_CONTRACT.DETECTIONNATURE',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'detectionNature',
                },
                validators : [ 'notEmpty' ],
            }, {
                field : 'ext$.consultcategory',
                title : 'T_EMC_CONTRACT.CONSULTCATEGORY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'consultCategory',
                },
            }, {
                field : 'ext$.detectcategory',
                title : 'T_EMC_CONTRACT.DETECTCATEGORY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'detectCategory',
                },
                validators : [ 'notEmpty' ]
            }, {
                field : 'ext$.sampleway',
                title : 'T_EMC_CONTRACT.SAMPLEWAY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'sampleWay'
                },
            }, {
                field : 'ext$.detectiontime',
                title : 'T_EMC_CONTRACT.DETECTIONTIME',
                type : 'date',
            }, {
                field : 'ext$.samplevariety',
                title : 'T_EMC_CONTRACT.SAMPLEVARIETY',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'sampleVariety'
                },
            }, {
                field : 'ext$.reportform',
                title : 'T_EMC_CONTRACT.REPORTFORM',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'reportForm'
                },
            }, {
                field : 'ext$.detectionorder',
                title : 'T_EMC_CONTRACT.DETECTIONORDER',
                validators : [ 'strLength[0, 270]' ],
                type : 'textarea',
                colspan : 3,
            }, {
                field : 'ext$.audit',
                title : 'T_EMC_CONTRACT.AUDIT',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'yesorno'
                },
            }*/, {
                field : 'createdByName',
                title : 'T_EMC_CONTRACT.CREATEDBYNAME',
                readonly : true
            }/*, {
                field : 'ext$.subpackage',
                title : 'T_EMC_CONTRACT.SUBPACKAGE',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'yesorno'
                },
            }, {
                field : 'ext$.nonstandardmethod',
                title : 'T_EMC_CONTRACT.NONSTANDARDMETHOD',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'yesorno'
                },
            }, {
                field : 'ext$.resultevaluation',
                title : 'T_EMC_CONTRACT.RESULTEVALUATION',
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'yesorno'
                },
            }, {
                field : 'ext$.relevantdata',
                title : 'T_EMC_CONTRACT.RELEVANTDATA',
                validators : [ 'strLength[0, 300]' ],
                type : 'textarea',
                colspan : 3,
            }, {
                field : 'ext$.entrustdate',
                title : 'T_EMC_CONTRACT.ENTRUSTDATE',
                validators : [ 'notEmpty', 'strLength[0, 19]' ],
                type : 'date',
                onChange : function (field, value) {
                    this.setData({
                        ext$ : {
                            entrustdate : value
                        },
                        reviewDate : value
                    });
                }
            }, {
                field : 'ext$.testfee',
                title : 'T_EMC_CONTRACT.TESTFEE',
                validators : [ 'notEmpty', 'strLength[0, 30]' ],
            }, {
                field : 'ext$.reviewername',
                title : 'T_EMC_CONTRACT.ACCEPTOR',
                validators : [ 'strLength[0, 36]' ],
                type : 'choose',
                category : 'emp',
                single : true,
                onAfterChoose : function(rows) {
                    if (!Gikam.isEmpty(rows)) {
                        this.setData({
                            reviewer : rows[0].id,
                            ext$ : {
                                reviewername : rows[0].userName
                            }
                        });
                    }
                },
            }, {
                field : 'reviewDate',
                title : 'T_EMC_CONTRACT.ACCEPTDATE',
                validators : [ 'strLength[0, 19]' ],
                type : 'date'
            }, {
                field : 'ext$.receivereportway',
                title : 'T_EMC_CONTRACT.RECEIVEREPORTWAY',
                validators : [ 'strLength[0, 30]' ],
                type : 'select',
                category : 'sys',
                param : {
                    codeCategoryId : 'receiveReportWay',
                },
            }, {
                field : 'ext$.completiondate',
                title : 'T_EMC_CONTRACT.COMPLETIONDATE',
                type : 'date'
            }, {}, {}*/, {
                field : 'remark',
                title : 'T_EMC_CONTRACT.REMARK',
                validators : [ 'strLength[0, 300]' ],
                type : 'textarea',
                colspan : 3,
            } ]
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'contraceSource',
            title : 'T_EMC_CONTRACT.CONTRACESOURCE',
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'monitorPurpose',
            title : 'T_EMC_CONTRACT.MONITORPURPOSE',
            type : 'textarea',
            rows : 5,
            height : 60,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'monitorOrder',
            title : 'T_EMC_CONTRACT.MONITORORDER',
            type : 'textarea',
            rows : 5,
            height : 60,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'cmaf',
            title : 'T_EMC_CONTRACT.CMAF',
            type : 'textarea',
            rows : 5,
            height : 60,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'contractReport',
            title : 'T_EMC_CONTRACT.CONTRACTREPORT',
            type : 'textarea',
            rows : 5,
            height : 60,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'otherInfo',
            title : 'T_EMC_CONTRACT.OTHERINFO',
            type : 'textarea',
            rows : 5,
            height : 60,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'ext$.empname',
            title : 'T_EMC_CONTRACT.REVIEWER',
            type : 'choose',
            category : 'emp',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reviewer : rows[0].id,
                        ext$ : {
                            empname : rows[0].userName
                        }
                    });
                }
            },
            width : 300
        }, {
            field : 'reviewDate',
            title : 'T_EMC_CONTRACT.REVIEWDATE',
            type : 'date',
            width : 300
        }, {
            field : 'reviewAddress',
            title : 'T_EMC_CONTRACT.REVIEWADDRESS',
            width : 400,
            validators : [ 'strLength[0,90]' ]
        }, {
            field : 'beginDate',
            title : 'T_EMC_CONTRACT.BEGINDATE',
            type : 'date',
            width : 300
        }, {
            field : 'endDate',
            title : 'T_EMC_CONTRACT.ENDDATE',
            type : 'date',
            width : 300
        }, {
            field : 'reviewOpinion',
            title : 'T_EMC_CONTRACT.REVIEWOPINION',
            validators : [ 'strLength[0,200]' ]
        } ];
    },

    getReviewContentColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contentName',
            title : 'T_EMC_REVIEW_CONTENT.CONTENTNAME',
            width : 200
        }, {
            field : 'satisfied',
            title : 'T_EMC_REVIEW_CONTENT.SATISFIED',
            width : 150,
            editor : true,
            type : 'simpleCheckbox'
        }, {
            field : 'explain',
            title : 'T_EMC_REVIEW_CONTENT.EXPLAIN',
            width : 300,
            editor : true,
            sort : false
        } ]
    },
    getExpenseCategoryColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'expenseCategory',
            title : 'T_EMC_EXPENSE_CATEGORY.EXPENSECATEGORY',
            editor : true
        }, {
            field : 'priceTerm',
            title : 'T_EMC_EXPENSE_CATEGORY.PRICETERM',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'priceTerm'
            }
        }, {
            field : 'otherExpense',
            title : 'T_EMC_EXPENSE_CATEGORY.OTHEREXPENSE',
            editor : true
        }, {
            field : 'totalPrice',
            title : 'T_EMC_EXPENSE_CATEGORY.TOTALPRICE'
        }, {
            field : 'originalReport',
            title : 'T_EMC_EXPENSE_CATEGORY.ORIGINALREPORT',
            editor : true,
            type : 'number',
            hasArrow : true,
        }, {
            field : 'dupliCatereport',
            title : 'T_EMC_EXPENSE_CATEGORY.DUPLICATEREPORT',
            editor : true,
            type : 'number',
            hasArrow : true,
        }, {
            field : 'remark',
            title : 'T_EMC_EXPENSE_CATEGORY.REMARK',
            editor : true
        } ]
    },
    getMonitorItemColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MONITOR_ITEM.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_ITEM.METHODNAME',
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_MONITOR_ITEM.ITEMGROUP',
        }, {
            field : 'ext$.foldernum',
            title : 'T_EMC_MONITOR_ITEM.FOLDERNUM',
            editor : true,
            type : 'number',
            hasArrow : true,
            range : '[1, 1000]'
        }, {
            field : 'ext$.frequency',
            title : 'T_EMC_MONITOR_ITEM.FREQUENCY',
            editor : true,
            type : 'number',
            hasArrow : true,
            range : '[1, 1000]'
        }, {
            field : 'ext$.samplenum',
            title : 'T_EMC_MONITOR_ITEM.SAMPLENUM',
            editor : true,
            type : 'number',
            hasArrow : true,
            range : '[1, 1000]'
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_MONITOR_ITEM.REMARK',
            editor : true,
        } ]
    },

    getSplitContractColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'businessOpportunityName',
            title : 'T_EMC_SPLIT_CONTRACT.BUSINESSOPPORTUNITYNAME',
        }, {
            field : 'productionLineName',
            title : 'T_EMC_SPLIT_CONTRACT.PRODUCTIONLINENAME',
        }, {
            field : 'industryName',
            title : 'T_EMC_SPLIT_CONTRACT.INDUSTRYNAME',
        }, {
            field : 'price',
            title : 'T_EMC_SPLIT_CONTRACT.PRICE',
            type : 'number',
            hasArrow : true,
            formatter : function (i, v, r) {
                return (Number(v) / 100).toFixed(2);
            }
        }, {
            field : 'status',
            title : 'T_EMC_SPLIT_CONTRACT.STATUS',
            formatter : function (i, v, r) {
                if (v === '0') {
                    return '未生效';
                } else if (v === '1') {
                    return '未下单';
                } else if (v === '2') {
                    return '已下单'
                } else if (v === '3') {
                    return '已完成'
                } else if (v === '4') {
                    return '已作废'
                }
            }
        } ]
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcContract.workflow.dbTable), {
            field : 'ext$.reportconfiglineid',
            title : 'T_EMC_CONTRACT.REPORTCONFIGLINEID',
            type : 'select',
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'contract'
            }
        }, {
            field : 'reviewReportLineId',
            title : 'T_EMC_CONTRACT.REVIEWREPORTLINEID',
            type : 'select',
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'contractReview'
            },
            editor : true
        }, {
            field : 'contractNo',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }, {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG'
        }, {
            field : 'ext$.contractcategory',
            title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'contractCategory',
            },
        }, {
            field : 'ext$.detectcategory',
            title : 'T_EMC_CONTRACT.DETECTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'detectCategory',
            },
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcContract.workflow.dbTable), {
            field : 'ext$.reportconfiglineid',
            title : 'T_EMC_CONTRACT.REPORTCONFIGLINEID',
            type : 'select',
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'contract'
            }
        }, {
            field : 'contractNo',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }, {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG'
        }, {
            field : 'ext$.contractcategory',
            title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'contractCategory',
            },
        }, {
            field : 'ext$.detectcategory',
            title : 'T_EMC_CONTRACT.DETECTCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'detectCategory',
            },
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
            validators : [ 'strLength[0, 66]' ],
            preInsert : true,
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG',
            preInsert : true,
            single : true,
            type : 'choose',
            category : 'client',
            onBeforeChoose : function() {
                return {
                    clientTag_SEQ : 'entrustor'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        entrustOrg : rows[0].id,
                        ext$ : {
                            entrustorgname : rows[0].clientName,
                            entrustorgaddress : rows[0].address,
                            entrustorglicense : rows[0].creditCode,
                            entrustorgbank : rows[0].ext$.openingbank,
                            entrustorgaccount : rows[0].ext$.bankaccount,
                            entrustorgphone : rows[0].phone
                        }
                    });
                }
            }
        }, {
            field : 'ext$.contractcategory',
            title : 'T_EMC_CONTRACT.CONTRACTCATEGORY',
            validators : [ 'strLength[0, 30]' ],
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'contractCategory',
            },
            onChange : function (field, value, rowIndex, oldValue, text) {
                if (value === 'consult') {
                    this.showFields([ 'ext$.consultcategory' ]);
                    this.addFieldValidator( 'ext$.consultcategory' , ['notEmpty'] )
                } else {
                    this.hideFields([ 'ext$.consultcategory' ]);
                    this.removeFieldValidator( 'ext$.consultcategory' , ['notEmpty'] )
                }
            }
        }, {
            field : 'ext$.consultcategory',
            title : 'T_EMC_CONTRACT.CONSULTCATEGORY',
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'consultCategory',
            },
        }, {
            field : 'ext$.detectcategory',
            title : 'T_EMC_CONTRACT.DETECTCATEGORY',
            validators : [ 'strLength[0, 30]' ],
            preInsert : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'detectCategory',
            },
        }, {
            field : 'ext$.registerdate',
            title : 'T_EMC_CONTRACT.REGISTERDATE',
            validators : [ 'strLength[0, 30]' ],
            preInsert : true,
            type : 'date'
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'contractNo',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }, {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
        }, {
            field : 'ext$.signusername',
            title : '合同签约人'
        }, {
          field : 'ext$.clientname',
          title : '客户名称'
      }, {
          field : 'ext$.contractorgname',
          title : '乙方'
      },{
            field: 'ext$.signusername',
            title: '乙方联系人'
        }, {
              field: 'ext$.signdate',
              title: '签订日期'
          }, {
              field: 'ext$.contractprice',
              title: '合同金额'
          }, {
              field: 'ext$.projallprice',
              title: '合同委托金额'
          }];
    },

    getSplitChooseListColumns : function() {
        return [ {
            field : 'ext$.contractno',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG',
        }, {
            field : 'businessOpportunityName',
            title : 'T_EMC_SPLIT_CONTRACT.BUSINESSOPPORTUNITYNAME',
        }, {
            field : 'productionLineName',
            title : 'T_EMC_SPLIT_CONTRACT.PRODUCTIONLINENAME'
        }, {
            field : 'industryName',
            title : 'T_EMC_SPLIT_CONTRACT.INDUSTRYNAME',
        }, {
            field : 'price',
            title : 'T_EMC_SPLIT_CONTRACT.PRICE',
            formatter : function (i, v, r) {
                return (Number(v) / 100).toFixed(2);
            }
        } ];
    },

    getBatchUpdateFormFields : function () {
        return [ {
            field : 'ext$.foldernum',
            title : 'T_EMC_MONITOR_ITEM.FOLDERNUM',
            editor : true,
            type : 'number',
            hasArrow : true,
            value : 1,
            range : '[1, 1000]',
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.frequency',
            title : 'T_EMC_MONITOR_ITEM.FREQUENCY',
            editor : true,
            type : 'number',
            hasArrow : true,
            value : 1,
            range : '[1, 1000]',
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.samplenum',
            title : 'T_EMC_MONITOR_ITEM.SAMPLENUM',
            editor : true,
            type : 'number',
            value : 1,
            hasArrow : true,
            range : '[1, 1000]',
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_MONITOR_ITEM.REMARK',
            type : 'textarea',
            editor : true,
        } ]
    },

    getReviewContentBatchInputColumns : function() {
        return [ {
            field : 'satisfied',
            title : 'T_EMC_REVIEW_CONTENT.SATISFIED',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'explain',
            title : 'T_EMC_REVIEW_CONTENT.EXPLAIN',
        } ]
    },

    getReviewBasicInfoFields : function () {
        return [ {
            field : 'reviewTitle',
            title : 'T_EMC_REVIEW_BASIC_INFO.REVIEWTITLE',
            validators : [ 'notEmpty' ],
        }, {
            field : 'reviewOrgName',
            title : 'T_EMC_REVIEW_BASIC_INFO.REVIEWORGNAME',
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'org-simple',
            single : false,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var orgIds = rows.map(row => (row.id)).join();
                    var orgNames = rows.map(row => (row.orgName)).join();

                    this.setData({
                        reviewOrgId : orgIds,
                        reviewOrgName : orgNames
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reviewOrgId : '',
                    reviewOrgName : ''
                });
            },
        }, {
            field : 'reviewUserName',
            title : 'T_EMC_REVIEW_BASIC_INFO.REVIEWUSERNAME',
            validators : [ 'notEmpty' ],
            category : 'emp',
            type : 'choose',
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var userNames = [];
                    var userIds = [];
                    Gikam.each(rows, function(index, item) {
                        userNames.push(item.userName);
                        userIds.push(item.id);
                    });
                    this.setData({
                        reviewUserName : userNames.join(','),
                        reviewUserId : userIds.join(',')
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reviewUserName : '',
                    reviewUserId : ''
                });
            }
        }, {
            field : 'reviewTime',
            title : 'T_EMC_REVIEW_BASIC_INFO.REVIEWTIME',
            type : 'date',
            validators : [ 'notEmpty' ],
        } ]
    },

    getCopyListColumns : function() {
        return [ {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_CONTRACT.ENTRUSTORG'
        }, {
            field : 'contractName',
            title : 'T_EMC_CONTRACT.CONTRACTNAME',
        }, {
            field : 'contractNo',
            title : 'T_EMC_CONTRACT.CONTRACTNO'
        }, {
            field : 'createdByName',
            title : 'T_EMC_CONTRACT.CREATEDBYNAME'
        }, {
            field : 'totalPrice',
            title : 'T_EMC_CONTRACT.TOTALPRICE'
        } ];
    },

    getCopyListField : function() {
        return [ {
            field : 'year',
            title : 'T_EMC_CONTRACT.YEAR',
            type : 'year',
            onChange : function(field, value) {
                var grid = Gikam.getComp('emc-contract-copy-list-grid');
                grid.refresh({
                    requestData : {
                        year_SEQ : value
                    }
                });
            }
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_CONTRACT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_CONTRACT.ID'
        } ]
    }

});
