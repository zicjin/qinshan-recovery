var crmOpportunityContract = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/crm/contracts',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contractNo',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTNO'
        },  {
            field : 'contractAmount',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTAMOUNT'
        }, {
            field : 'contractCommencementDate',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTCOMMENCEMENTDATE'
        }, {
            field : 'contractOrderer',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTORDERER'
        } ];
    },

    getOpportunityFormFields : function() {
        return [ {
            field : 'opportunityNo',
            title : '委托编号',
            readonly : true
        }, {
            field : 'drawdate',
            title : '委托日期',
            type : 'date',
            validators : [ 'notEmpty' ]
        }, {
            field : 'ext$.customername',
            colspan : 2,
            title : '客户单位',
            type: 'choose',
            single : true,
            category: 'crmcustomer',
            validators : [ 'notEmpty' ],
        }, {
            field : 'bidder',
            type : 'select',
            title : '销售人员',
            validators : [ 'notEmpty' ]
        }, {
            field : 'customerContact',
            title : '联系人',
            type : 'select',
            validators : [ 'notEmpty' ],
        },  {
            field : 'authorizationUnit',
            colspan : 2,
            title : '委托单位',
            validators : [ 'notEmpty' ]
        }, {
            field : 'bidderPhone',
            title : '联系方式',
            validators : [ 'notEmpty' ]
        }, {
            field: 'bidderMail',
            title: '电子邮箱',
            validators : [ 'notEmpty' ]
        },  {
            field : 'inspectedUnit',
            colspan : 2,
            title : '受检单位',
            validators : [ 'notEmpty' ]
        }, {
            field : 'accountNo',
            title : '开户账号'
        }, {
            field : 'accountName',
            colspan : 2,
            title : '开户户名'
        }, {
            field : 'customerAddress',
            colspan : 2,
            title : '客户地址'
        }, {
            field : 'accountBank',
            colspan : 2,
            title : '开户行'
        }, {
            field : 'projectName',
            colspan : 2,
            title : '项目名称',
            validators : [ 'notEmpty' ]
        }, {
            field : 'reqsubtyp',
            title : '委托类别',
            colspan : 2,
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'REQSUB_TYPE' // 委托类别
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'sampleSource',
            title : '样品来源',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'SampleSource' // 样品来源
            },
        }, {
            field : 'reqCycle',
            title : '委托周期',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'REQ_CYCLE' // 委托周期
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'implementationTimes',
            title : '实施次数',
            type : 'number',
            validators : [ 'notEmpty' ]
        }, {
            field : 'maintainSecrecy',
            title : '保密要求',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'MAINTAIN_SECRECY' // 保密要求
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'inspectedContcts',
            title : '现场联系人'
        }, {
            field : 'inspectedPhone',
            title : '现场联系电话'
        }, {
            field : 'drawArea',
            colspan : 2,
            title : '采样地点'
        }, {
            field : 'authorizeWay',
            title : '委托方式',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'AUTHORIZE_WAY' // 委托方式
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'getReport',
            title : '报告提交方式',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'GETREPORT_WAY' // 报告提交方式
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'reportCyle',
            title : '报告周期',
            type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'REPORT_CYCLE' // 报告周期
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'fastestDays',
            title : '最快/最迟(天)',
            colspan : 0.6,
        } , {
            field : 'slowestDays',
            colspan : 0.3,
        }, {
            field : 'relateBill',
            title : '关联委托单',
            colspan : 1
        },  {
            field : 'remark',
            title : '备注说明',
            colspan : 3,
            type : 'textarea'
        } ];
    },

    getContractFormFields : function() {
        return [  {
            field : 'contractNo',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTNO',
            readonly : true
        }, {
            field : 'ext$.opportunityno',
            title : 'T_CRM_OPPORTUNITY_MAIN.OPPORTUNITYNO'
        }, {
            field : 'entrustUnit',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.ENTRUSTUNIT',
            readonly : true,
        }, {
            field : 'contractAmount',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTAMOUNT',
            readonly : true
        }, {
            field : 'contractCommencementDate',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTCOMMENCEMENTDATE',
            type : 'date'
        }, {
            field : 'contractEndDate',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTENDDATE',
            type : 'date'
        }, {
            field : 'signingSite',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.SIGNINGSITE',
        }, {
            field : 'performancePlace',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.PERFORMANCEPLACE'
        }, {
            field : 'reportNumber',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.REPORTNUMBER',
            value : '1'
        }, {
            field : 'contractNumber',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTNUMBER',
            value : '2'
        }, {
            field : 'partyAContractNumber',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.PARTYACONTRACTNUMBER',
            value : '1'
        }, {
            field : 'partyBContractNumber',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.PARTYBCONTRACTNUMBER',
            value : '1'
        }, {
            field : 'entrustedUnit',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.ENTRUSTEDUNIT',
            readonly : true
        }, {
            field : 'entrustedUnitContact',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.ENTRUSTEDUNITCONTACT'
        }, {
            field : 'entrustedUnitContactInformation',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.ENTRUSTEDUNITCONTACTINFORMATION'
        }, {
            field : 'entrustedUnitEmail',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.ENTRUSTEDUNITEMAIL'
        },  {
            field : 'reportingDays',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.REPORTINGDAYS'
        }, {
            field : 'appointmentForavisit',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.APPOINTMENTFORAVISIT'
        } ];
    },

    getContractPaymentColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'paymentType',
            title : 'T_CRM_OPPORTUNITY_PAYMENT.PAYMENTTYPE',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'paymentType'
            },
        }, {
            field : 'planProportion',
            title : 'T_CRM_OPPORTUNITY_PAYMENT.PLANPROPORTION',
            editor : true
        }, {
            field : 'planAmount',
            title : 'T_CRM_OPPORTUNITY_PAYMENT.PLANAMOUNT',
            editor : true
        }, {
            field : 'remark',
            title : 'T_CRM_OPPORTUNITY_PAYMENT.REMARK',
            editor : true
        } ];
    },

    getContractExpensesColumns : function() {
        return [ {
            checkbox : true
        },  {
            field : 'expensesType',
            title : '收费项目名称',
            onBeforeEditorRender : function(row){
                if(Gikam.isNotEmpty(row.expensesType)){
                    return false
                }
            }
        }, {
            field : 'price',
            title : '收费单价(元)',
            type : 'number',
        }, {
            field : 'count',
            title : '数量',
            type : 'number',
        } , {
            field : 'unit',
            title : '单位',
            type : 'select',
            category : 'sys',
            param: {
                codeCategoryId: 'unit'
            }
        } , {
            field : 'amount',
            title : '收费金额(元)'
        }  ];
    },

    getCommissionSheetColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testType',
            title : 'T_CRM_OPPORTUNITY_ORDERS.TESTTYPE',
        }, {
            field : 'potName',
            title : 'T_CRM_OPPORTUNITY_ORDERS.POTNAME',
        }, {
            field : 'potNo',
            title : 'T_CRM_OPPORTUNITY_ORDERS.POTNO',
        }, {
            field : 'potNum',
            title : 'T_CRM_OPPORTUNITY_ORDERS.POTNUM',
        }, {
            field : 'baoyanQuantity',
            title : 'T_CRM_OPPORTUNITY_ORDERS.BAOYANQUANTITY',
        }, {
            field : 'testingFrequency',
            title : 'T_CRM_OPPORTUNITY_ORDERS.TESTINGFREQUENCY',
        }, {
            field : 'testingDays',
            title : 'T_CRM_OPPORTUNITY_ORDERS.TESTINGDAYS',
        }, {
            field : 'numberOfInspections',
            title : 'T_CRM_OPPORTUNITY_ORDERS.NUMBEROFINSPECTIONS',
        }]
    },

    getOrderTaskColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testCode',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.TESTCODE',
        }, {
            field : 'testGroupName',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.TESTGROUPNAME',
        }, {
            field : 'itemGroup',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.ITEMGROUP',
        }, {
            field : 'testCriterion',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.TESTCRITERION',
        }, {
            field : 'testMethods',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.TESTMETHODS',
        }, {
            field : 'sampleCounts',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.SAMPLECOUNTS',
        }, {
            field : 'price',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.PRICE',
        }, {
            field : 'addPrice',
            title : 'T_CRM_OPPORTUNITY_ORDTASK.ADDPRICE',
        } ]
    },

    getOpportunityContractGridColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'contractNo',
            title : 'T_CRM_OPPORTUNITY_CONTRACT.CONTRACTNO',
        }, {
            field : 'ext$.reqcycle',
            title : '委托周期'
        }, {
            field : 'ext$.customername',
            title : '客户单位'
        },  {
            field : 'ext$.projectname',
            title : '项目名称',
        }, {
            field : 'ext$.samplesource',
            title : '样品来源',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
            multiple : true,
        }, {
            field : 'contractAmount',
            title : '合同金额'
        }, {
            field : 'contractCommencementDate',
            title : '成交日期'
        }, {
            field : 'createdByName',
            title : '下单人'
        }, {
            field : 'ext$.biddername',
            title : '销售人员'
        }, {
            field : 'ext$.implementationtimes',
            title : '剩余频次'
        }, {
            field : 'ext$.scannedcopyofcontract',
            title : '合同扫描件',
            type : 'simpleCheckbox'
        }, {
            field : 'ext$.originalcontract',
            title : '合同原件',
            type : 'simpleCheckbox'
        }, {
            field : 'validFlag',
            title : '是否有效',
            type : 'simpleCheckbox'
        } ];
    },

    getOpportunityMainFormFields : function() {
        return [ {
            field : 'opportunityNo',
            title : '委托编号',
            readonly : true
        }, {
            field : 'drawdate',
            title : '委托日期',
            type : 'date',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'customerName',
            colspan : 2,
            title : '客户单位',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'bidderName',
            title : '销售人员',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'customerContact',
            title : '联系人',
            validators : [ 'notEmpty' ],
            readonly : true
        },  {
            field : 'authorizationUnit',
            colspan : '1.6',
            title : '委托单位',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field: 'authorizationUnitShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [ {
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            } ]
        }, {
            field : 'bidderPhone',
            title : '联系方式',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field: 'bidderMail',
            title: '电子邮箱',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'inspectedUnit',
            colspan : '1.6',
            title : '受检单位',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field: 'inspectedUnitShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [ {
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            } ]
        }, {
            field: 'projectName',
            title: '项目名称',
            colspan : '1.6',
            validators: ['notEmpty'],
            readonly : true
        }, {
            field: 'projectNameShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [{
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            }]
        }, {
            field: 'entrustededUnitAdd',
            title: '委托单位地址',
            colspan : '1.6',
            validators: ['notEmpty'],
            readonly : true
        }, {
            field: 'entrustededUnitAddShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [{
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            }]
        }, {
            field : 'reqsubtyp',
            title : '委托类别',
            type : 'select',
            selectedIndex : 1,
            category : 'bizCategoryCode',
            multiple : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'sampleSource',
            title : '样品来源',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
            validators : [ 'notEmpty' ],
            readonly : false,
            multiple : true
        }, {
            field : 'inspectedAddr',
            colspan : '1.6',
            title : '受检地址',
            readonly : true
        }, {
            field: 'inspectedAddrShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [ {
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            } ]
        }, {
            field : 'reqCycle',
            title : '委托周期',
            /*type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'REQ_CYCLE' // 委托周期
            },*/
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'implementationTimes',
            title : '实施次数',
            type : 'number',
            value : '1',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'drawArea',
            colspan : '1.6',
            title : '采样地点',
            readonly : true
        }, {
            field: 'drawAreaShow',
            type: 'select',
            colspan : '0.4',
            placeholder : '报告显示',
            validators: ['notEmpty'],
            readonly : true,
            items: [ {
                'text': '是',
                'value': '是'
            }, {
                'text': '否',
                'value': '否'
            }, {
                'text': '待定',
                'value': '待定'
            } ]
        }, {
            field: 'reportType',
            title: '报告类型',
            validators: ['notEmpty']
        }, {
            field : 'authorizeWay',
            title : '委托方式',
            /*type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'AUTHORIZE_WAY' // 委托方式
            },*/
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'inspectedContcts',
            title : '现场联系人',
            readonly : true
        }, {
            field : 'inspectedPhone',
            title : '现场联系电话',
            readonly : true
        }, {
            field : 'getReport',
            title : '报告领取方式',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'receiveReportWay',
            },
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'urgentFlag',
            title : '是否加急',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field: 'reportContact',
            title: '报告联系人',
            validators: ['notEmpty'],
            readonly : true
        }, {
            field: 'reportPhone',
            title: '报告联系电话',
            validators: ['notEmpty'],
            readonly : true
        }, {
            field : 'maintainSecrecy',
            title : '保密要求',
            /*type : 'select',
            selectedIndex : 1,
            category : 'sys',
            param : {
                codeCategoryId : 'MAINTAIN_SECRECY' // 保密要求
            },*/
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'accountName',
            title : '开户户名',
            readonly : true
        }, {
            field : 'customerAddress',
            colspan : 2,
            title : '客户地址',
            readonly : true
        }, {
            field : 'accountBank',
            title : '开户行',
            readonly : true
        }, {
            field : 'fastestDays',
            title : '最快/最迟(天)',
            colspan : 0.6,
            value : '7',
            validators : [ 'notEmpty','number','numRange[0,999999]' ],
            readonly : true
        }, {
            field : 'slowestDays',
            colspan : 0.4,
            validators : [ 'notEmpty' ,'number','numRange[0,999999]'],
            value : '10',
            readonly : true
        }, {
            field : 'accountNo',
            title : '开户账号',
            readonly : true
        }, {
            field : 'relateBill',
            title : '关联委托单',
            readonly : true
        },  {
            field : 'remark',
            title : '备注说明',
            colspan : 4,
            type : 'textarea',
            readonly : true
        } ];
    },

    // 环境检品项目标签页-检验样品记录列表
    getHjOrderGridColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'potName',
            title : '点位名称',
            width : 82,
            editor : true
        } , {
            field : 'potNum',
            title : '点位数量',
            width : 82,
            editor : true,
            onChange : function (field, newvalue, index) {
                var numberOfInspections = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().numberOfInspections;
                var testingDays = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingDays;
                var testingFrequency = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingFrequency;
                var projectSubtotal = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().projectSubtotal;
                var totalPrice = 0;

                if (Gikam.isNotEmpty(projectSubtotal) && Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(numberOfInspections)&&Gikam.isNotEmpty(testingDays)) {
                    totalPrice =  newvalue * numberOfInspections * projectSubtotal * testingFrequency*testingDays;
                }
                crmOpportunityContract.hjDetailPage.getOrderGrid().setData({
                    index : index,
                    totalPrice : totalPrice,
                    potNum : newvalue
                })

                if (Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(numberOfInspections)&&Gikam.isNotEmpty(testingDays)) {
                    var data = crmOpportunityContract.hjDetailPage.getOrderTaskGrid().getData();

                    for (var i = 0; i < data.length; i++) {
                        var simpleCounts = newvalue * numberOfInspections * testingFrequency*testingDays*data[i].samplePoints;
                        crmOpportunityContract.hjDetailPage.getOrderTaskGrid().setData({
                            index : data[i].gridIndex,
                            sampleCounts : simpleCounts
                        })


                    }
                }

            }
        } , {
            field : 'numberOfInspections',
            title : '检测<br/>次数',
            width : 57,
            editor : true,
            type : 'number',
            onChange : function (field, newvalue, index) {
                var positionCount = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().potNum;
                var testingDays = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingDays;
                var testingFrequency = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingFrequency;
                var projectSubtotal = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().projectSubtotal;
                var totalPrice = 0;
                if (Gikam.isNotEmpty(projectSubtotal) && Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(testingDays)) {
                    totalPrice =  newvalue * positionCount * projectSubtotal * testingFrequency*testingDays;
                }
                crmOpportunityContract.hjDetailPage.getOrderGrid().setData({
                    index : index,
                    totalPrice : totalPrice,
                    numberOfInspections : newvalue
                })

                if (Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(testingDays)) {
                    var data = crmOpportunityContract.hjDetailPage.getOrderTaskGrid().getData();

                    for (var i = 0; i < data.length; i++) {
                        var simpleCounts = newvalue * positionCount * testingFrequency * testingDays*data[i].samplePoints;
                        crmOpportunityContract.hjDetailPage.getOrderTaskGrid().setData({
                            index: data[i].gridIndex,
                            sampleCounts: simpleCounts
                        })
                    }
                }

            }
        }, {
            field : 'testingDays',
            title : '监测<br/>天数', // 科室
            width : 57,
            editor : true,
            type : 'number',
            onChange : function (field, newvalue, index) {
                var positionCount = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().potNum;
                var numberOfInspections = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().numberOfInspections;
                var testingFrequency = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingFrequency;
                var projectSubtotal = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().projectSubtotal;
                var totalPrice = 0;

                if (Gikam.isNotEmpty(projectSubtotal) && Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(numberOfInspections)) {
                    totalPrice =  newvalue * positionCount * projectSubtotal * testingFrequency*numberOfInspections;
                }
                crmOpportunityContract.hjDetailPage.getOrderGrid().setData({
                    index : index,
                    totalPrice : totalPrice,
                    testingDays : newvalue
                })

                if (Gikam.isNotEmpty(testingFrequency) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(numberOfInspections)) {
                    var data = crmOpportunityContract.hjDetailPage.getOrderTaskGrid().getData();

                    for (var i = 0; i < data.length; i++) {
                        var simpleCounts = newvalue * positionCount * testingFrequency * numberOfInspections*data[i].samplePoints;
                        crmOpportunityContract.hjDetailPage.getOrderTaskGrid().setData({
                            index: data[i].gridIndex,
                            sampleCounts: simpleCounts
                        })
                    }
                }

            }
        }, {
            field : 'testingFrequency',
            title : '频次<br/>/天', // 科室
            width : 57,
            editor : true,
            type : 'number',
            onChange : function (field, newvalue, index) {
                var positionCount = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().potNum;
                var testingDays = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().testingDays;
                var numberOfInspections = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().numberOfInspections;
                var projectSubtotal = crmOpportunityContract.hjDetailPage.getOrderGrid().getActivedRow().projectSubtotal;
                var totalPrice = 0;
                if (Gikam.isNotEmpty(projectSubtotal) && Gikam.isNotEmpty(numberOfInspections) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(testingDays)) {
                    totalPrice =  newvalue * positionCount * projectSubtotal * numberOfInspections*testingDays;
                }
                crmOpportunityContract.hjDetailPage.getOrderGrid().setData({
                    index : index,
                    totalPrice : totalPrice,
                    testingFrequency : newvalue
                })

                if (Gikam.isNotEmpty(numberOfInspections) && Gikam.isNotEmpty(positionCount)&&Gikam.isNotEmpty(testingDays)) {
                    var data = crmOpportunityContract.hjDetailPage.getOrderTaskGrid().getData();

                    for (var i = 0; i < data.length; i++) {
                        var simpleCounts = newvalue * positionCount * numberOfInspections * testingDays*data[i].samplePoints;
                        crmOpportunityContract.hjDetailPage.getOrderTaskGrid().setData({
                            index: data[i].gridIndex,
                            sampleCounts: simpleCounts
                        })
                    }
                }

            }
        }, {
            field : 'sampleSource',
            title : '样品来源',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
            readonly : true
        }, {
            field : 'projectSubtotal',
            title : '项目小计',
            width : 82,
        }, {
            field : 'totalPrice',
            title : '项目总价',
            width : 82,
        } , {
            field : 'comments',
            title : '备注',
            width : 57,
            editor : true
        } ]
    },

    getContractHjOrderTaskGridColumns : function(){
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'subcontract2',
            title : '采样前分包',
            width : 130,
            sort : false,
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'subcontract',
            title : '采样后分包',
            width : 130,
            sort : false,
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'schemeName',
            title : '套餐名称', // 套餐名称
            width : 82,
            sort : false,
        }, {
            field : 'testNo',
            title : '检测项目名称', // 检测项目名称
            sort : false,
        }, {
            field : 'itemGroup',
            title : '分析项组', // 检测项目名称
            width : 82,
            sort : false,
        }, {
            field : 'samplePoints',
            title : '样品个数',
            editor : true,
            width : 82,
            sort : false,
            type : 'number',
            onBeforeEditorRender : function(row){
                if(row.firstPackage === '0'){
                    return false;
                }
            }
        }, {
            field : 'unitPrice',
            editor : true,
            title : '单价',
            width : 57,
            sort : false,
            onBeforeEditorRender : function(row){
                if(row.firstPackage === '0'){
                    return false;
                }
            }
        }, {
            field : 'addPrice',
            title : '合计', // 折扣系数
            width : 57,
            sort : false,
        }, {
            field : 'price',
            title : '原单价',
            sort : false,
        }, {
            field : 'sampleCounts',
            title : '样品总数',
            width : 82,
            sort : false,
        },  {
            field : 'testCriterion',
            title : '检验依据', // 检测类别
            type : 'choose',
            width : 82,
            sort : false,
            category : 'environment',
            targetFields : [ {
                testCriterion : 'testMethodBasis'
            } ]
        }, {
            field : 'executiveStandard',
            title : '执行标准', // 检测类别
            width : 82,
            sort : false,
            editor : true
        } , {
            field : 'remark',
            title : '备注',
            width : 57,
            sort : false,
            editor : true
        } ];
    },

    getExpenseFormFields: function () {
        return [{
            field: 'testingFee',
            title: '检测费用合计',
            readonly: true
        }, {
            field: 'guideFee',
            title: '检测指导价合计',
            readonly: true
        }, {
            field: 'incidentalFee',
            title: '采样费及增加报告费',
            readonly: true
        }, {
            field: 'reportFeeRate',
            title: '报告编制(%)'
        }, {
            field: 'reportFee',
            title: '报告编制费',
        }, {
            field: 'acceptReportFee',
            title: '验收报告编制费'
        }, {
            field: 'urgentFee',
            title: '加急费'
        }, {
            field: 'otherExpenses',
            title: '其他费用'
        }, {
            field: 'amountFee',
            title: '费用合计',
            readonly: true
        }, {
            field: 'discountAmount',
            title: '优惠金额'
        }, {
            field: 'discountRate',
            title: '折扣率(%)',
        }, {
            field: 'taxRate',
            title: '税率(%)'
        }, {
            field: 'taxAmount',
            title: '税额',
            readonly: true
        }, {
            field: 'dealAmount',
            title: '成交金额',
        }]
    },

    getHjExpensesGridColumns : function() {
        return [ {
            checkbox : true
        },  {
            field : 'expensesType',
            title : '收费项目名称',
            editor : true,
            type : 'comboBox',
            category: 'sys',
            param: {
                codeCategoryId: 'ExpensesType'
            },
            onBeforeEditorRender : function(row){
                if(Gikam.isNotEmpty(row.expensesType)){
                    return false
                }
            }
        }, {
            field : 'price',
            title : '收费单价(元)',
            type : 'number',
        }, {
            field : 'count',
            title : '数量',
            type : 'number',
        } , {
            field : 'unit',
            title : '单位',
            type : 'select',
            category : 'sys',
            param: {
                codeCategoryId: 'unit'
            }
        } , {
            field : 'amount',
            title : '收费金额(元)'
        }  ];
    },

    getOpportunityExpenseFormFields : function() {
        return [ {
            field : 'testingFee',
            title : '检测费用合计',
            readonly : true
        }, {
            field : 'guideFee',
            title : '检测指导价合计',
            readonly : true
        }, {
            field : 'incidentalFee',
            title : '采样费及增加报告费',
            readonly : true
        }, {
            field : 'reportFeeRate',
            title : '报告编制(%)'
        }, {
            field : 'reportFee',
            title : '报告编制费',
        }, {
            field : 'acceptReportFee',
            title : '验收报告编制费'
        }, {
            field : 'urgentFee',
            title : '加急费'
        }, {
            field : 'otherExpenses',
            title : '其他费用'
        }, {
            field : 'amountFee',
            title : '费用合计',
            readonly : true
        }, {
            field : 'discountAmount',
            title : '优惠金额'
        }, {
            field : 'discountRate',
            title : '折扣率(%)',
        }, {
            field : 'taxRate',
            title : '税率(%)'
        }, {
            field : 'taxAmount',
            title : '税额',
            readonly : true
        }, {
            field : 'dealAmount',
            title : '成交金额',
        } ]
    },

    getOpportunityContractPaymentColumns : function(){
        return [ {
            checkbox : true
        }, {
            field : 'paymentType',
            title : '付款方式',
        }, {
            field : 'planProportion',
            title : '计划比例',
            editor : true
        }, {
            field : 'planAmount',
            title : '计划金额',
            editor : true
        }, {
            field : 'remark',
            title : '备注',
            editor : true
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
