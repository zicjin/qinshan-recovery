var emcProject = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
    schemeUrl: IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes',
    orderUrl: IFM_CONTEXT + '/secure/emc/module/bp/bp/orders',

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcProject.workflow.dbTable), {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true,
            width: 100,
            formatter: function (i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return "<div style='vertical-align: middle'>" + img + v + "</div>";
                } else {
                    return v;
                }
            },
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            width: 80,
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
            // }, {
            //     field : 'ext$.samplesource',
            //     title : 'T_EMC_PROJECT.SAMPLESOURCE',
            //     type : 'select',
            //     search : true,
            //     category : 'sys',
            //     param : {
            //         codeCategoryId : 'sampleSource',
            //     },
        }, {
            field: 'urgent',
            title: 'T_EMC_PROJECT.URGENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            editor: true,
        }, {
            //     field : 'ext$.biddername',
            //     title : 'T_EMC_PROJECT.BIDDERNAME',
            // }, {
            field: 'projCreatedTime',
            title: 'T_EMC_PROJECT.PROJCREATEDTIME',
        },
            //     {
            //     field : 'ext$.projectamount',
            //     title : 'T_EMC_PROJECT.PROJECTAMOUNT',
            // }
            /*, {
                field : 'schemeReportLineId',
                title : 'T_EMC_PROJECT.SCHEMEREPORTLINEID',
                type : 'select',
                search : true,
                category : 'reportConfigLine',
                param : {
                    reportConfigId : 'rasproject'
                },
                editor : true,
                validators : [ 'notEmpty' ],
            }*/];
    },

    getAuditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcProject.workflow.dbTable), {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true,
            width: 100,
            formatter: function (i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, /*{
            field : 'erpOrderNumber',
            title : 'T_EMC_PROJECT.ERPORDERNUMBER',
        },*/ {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            width: 80,
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG',
            type: 'link'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
        }];
    },

    getPreInsertFields: function () {
        return [{
            field: 'year',
            title: '任务年度',
            type: 'year',
            validators: ['notEmpty'],
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy'),
            preInsert: true
        }, {
            field: 'bizClassId',
            title: '业务类别',
            type: 'select',
            search: true,
            firstBlank: false,
            category: 'bizClass',
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        bizClassId_EQ: value,
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('bizCategoryId', items.map(function (item) {
                            return {
                                text: item.bizCategoryName,
                                value: item.id,
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                bizCategoryId: value,
                            });
                        } else {
                            _form.setData({
                                bizCategoryId: '',
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('bizCategoryId', []);
                }
            },
            editable: true,
            preInsert: true
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            onChange: function (field, value, rowIndex, oldValue, text) {
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/' + value;
                var _form = this;
                Gikam.getJson(url).done(function (row) {
                    _form.setData({
                        sample: row.sample,
                    });
                    if (row.showSample == '1') {
                        _form.showFields(['sample']);
                    } else {
                        _form.hideFields(['sample']);
                    }
                    if ("TW" == row.bizCategoryId) {
                        _form.addFieldValidator('projectType', ['notEmpty']);
                    } else {
                        _form.removeFieldValidator('projectType', ['notEmpty']);
                    }
                });
                if ("年度检测" === text) {
                    _form.showFields(['ext$.contractno']);
                } else {
                    _form.hideFields(['ext$.contractno']);
                    _form.removeFieldValidator('ext$.contractno', ['notEmpty']);
                }
                if (!Gikam.isEmpty(value)) {
                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/projects/queries/selectable';
                    url += Gikam.param({
                        bizCategoryId: value,
                        activatedFlag: '1',
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('projName', items.map(function (item) {
                            return {
                                text: item.projName,
                                value: item.id
                            };
                        }), true);
                    });
                }
            },
            editable: true,
            preInsert: true
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            validators: ['strLength[0, 500]'],
            preInsert: true,
            editor: true,
        }, {
            field: 'projectType',
            title: '任务分类',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'projectType',
            },
            preInsert: true,
            editor: true,
        }, {
            field: 'taskDate',
            title: 'T_EMC_PROJECT.TASKDATE',
            type: 'date',
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            preInsert: true
        }, {
            field: 'ext$.contractno',
            title: 'T_EMC_PROJECT.CONTRACTNO',
            type: 'choose',
            category: 'crmContract',
            single: true,
            preInsert: true,
            onBeforeChoose: function () {
                return {
                    targetType: 'createProject'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    console.info(rows)
                    this.setData({
                        ext$: {
                            contractid: rows[0].id,
                            contractno: rows[0].contractNo,
                            contractname: rows[0].contractName,
                            signusername: rows[0].signuserName,

                        },
                        entrustOrgName: rows[0].ext$.clientname,
                        entrustOrgContact: rows[0].ext$.clientcontact,
                        entrustOrgPhone: rows[0].ext$.clientcontactphone,
                        // entrustOrgAddress:rows[0].ext$.clientaddress,
                        testOrgName: rows[0].ext$.testorgname,
                        testOrgContact: rows[0].ext$.testorgcontact,
                        testOrgPhone: rows[0].ext$.testorgphone,
                        testOrgAddress: rows[0].ext$.testorgaddress
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        contractid: '',
                        contractno: '',
                        biddername: ''
                    },
                    splitContractId: '',
                    entrustOrgId: '',
                    entrustOrgName: '',
                    // entrustOrgAddress : '',
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                    testOrgId: '',
                    testOrgName: '',
                    testOrgAddress: '',
                    testOrgContact: '',
                    testOrgPhone: ''
                });
            }
        }, {
            field: 'sample',
            title: 'T_EMC_PROJECT.SAMPLE',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            preInsert: true
        }];
    },

    getBaseInfoFormFields11: function () {
        return [{
            field: 'ext$.contractno',
            title: 'T_EMC_PROJECT.CONTRACTNO',
            editor: true,
//               editable:true,
            type: 'choose',
            category: 'crmContract',
            onBeforeChoose: function () {
                return {
                    orgid_filter: '1'
                }
            },
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var contractIds = rows.map(function (item) {
                        return item.id
                    }).join(',');
                    var contractNos = rows.map(function (item) {
                        return item.contractNo
                    }).join(',');
                    var contractNames = rows.map(function (item) {
                        return item.contractName;
                    }).join(',');
                    var signuserNames = rows.map(function (item) {
                        return item.ext$.signusername;
                    }).join(',');
                    this.setData({
                        ext$: {
                            contractid: contractIds,
                            contractno: contractNos,
                            contractname: contractNames,
                            signusername: signuserNames,
                            belongarea: rows[0].ext$.projectplace
                        },
                        entrustOrgName: rows[0].ext$.clientname,
                        entrustOrgContact: rows[0].ext$.clientcontact,
                        entrustOrgPhone: rows[0].ext$.clientcontactphone,
                        entrustOrgAddress: rows[0].ext$.clientaddress,
                        testOrgName: rows[0].ext$.testorgname,
                        testOrgContact: rows[0].ext$.testorgcontact,
                        testOrgPhone: rows[0].ext$.testorgphone,
                        testOrgAddress: rows[0].ext$.testorgaddress
                    });
                    Gikam.getAsyncComp('emc-project-edit-list-contract-grid').done(function(grid) {
                        grid.refresh({
                            url : emcProject.baseUrl + '/all-proj/queries',
                            requestData : {
                                CONTRACTNO_SEQ : Gikam.isEmpty(contractNos) ? '1' : contractNos
                            }
                        });
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        contractid: contractIds,
                        contractno: contractNos,
                        contractname: contractNames,
                    }
                });
            },
            validators: ['notEmpty']
        }, {
            field: 'ext$.contractname',
            title: '合同名称',
            readonly: true
        }, {
            field: 'ext$.signusername',
            title: '合同签约人',
            readonly: true
        },];
    },
    getBaseInfoFormFields12: function () {
        return [{
            field: 'entrustOrgName',
            title: '单位名称',
            //colspan : 2,
            type: 'choose',
            category: 'client',
            single: true,
            validators: ['notEmpty',],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var entrustOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    this.setData({
                        entrustOrgId: rows[0].id,
                        entrustOrgName: rows[0].clientName,
                        entrustOrgAddress: entrustOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));

                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                    Gikam.post(url).done(function (items) {
                        if (!Gikam.isEmpty(items.rows)) {
                            _this.setData({
                                entrustOrgContact: items.rows[0].lxrName,
                                entrustOrgPhone: items.rows[0].lxrPhone,
                            });
                        } else {
                            _this.setData({
                                entrustOrgContact: '',
                                entrustOrgPhone: '',
                            });
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    entrustOrgId: '',
                    entrustOrgName: '',
                    entrustOrgAddress: '',
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'entrustOrgContact',
            title: '联系人',
            type: 'choose',
            category: 'clientContact',
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                return {
                    clientId_SEQ: this.getData().entrustOrgId,
                }
            },
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        entrustOrgContact: rows[0].lxrName,
                        entrustOrgPhone: rows[0].lxrPhone,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'entrustOrgPhone',
            title: '联系电话',
        }, {
            field: 'entrustOrgAddress',
            title: '单位地址',
            validators: ['notEmpty',],
            colspan: 2
        },];
    },
    getBaseInfoFormFields13: function () {
        return [{
            field: 'testOrgName',
            title: '单位名称',
            //colspan : 2,
            type: 'choose',
            category: 'client',
            single: true,
            validators: ['notEmpty'],
            // onBeforeChoose : function() {
            //     return {
            //         id_SEQ : this.getData().entrustOrgId,
            //     }
            // },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var testOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    this.setData({
                        testOrgId: rows[0].id,
                        testOrgName: rows[0].clientName,
                        testOrgAddress: testOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));


                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                    Gikam.post(url).done(function (items) {
                        if (!Gikam.isEmpty(items.rows)) {
                            _this.setData({
                                testOrgContact: items.rows[0].lxrName,
                                testOrgPhone: items.rows[0].lxrPhone,
                            });
                        } else {
                            _this.setData({
                                testOrgContact: '',
                                testOrgPhone: '',
                            });
                        }
                    });

                }
            },
            onClean: function () {
                this.setData({
                    testOrgId: '',
                    testOrgName: '',
                    testOrgAddress: '',
                    testOrgContact: '',
                    testOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'testOrgContact',
            title: '联系人',
            type: 'choose',
            category: 'clientContact',
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                return {
                    clientId_SEQ: this.getData().testOrgId,
                }
            },
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        testOrgContact: rows[0].lxrName,
                        testOrgPhone: rows[0].lxrPhone,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    testOrgContact: '',
                    testOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'testOrgPhone',
            title: '联系电话'
        }, {
            field: 'testProjectName',
            title: '项目名称',
            colspan: 1.5
        }, {
            field: 'testOrgAddress',
            title: '单位地址',
            validators: ['notEmpty'],
            colspan: 1.5
        },];
    },
    getBaseInfoFormFields14: function () {
        return [{
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
//            type : 'comboBox',
//            category : 'bizCategoryProject',
            validators: ['notEmpty', 'strLength[0,500]'],
            editable: true,
            colspan: 2
        }, {
            field: 'period',
            title: 'T_EMC_PROJECT.PERIOD',
            validators: ['notEmpty'],
        }, {
            field: 'urgent',
            title: 'T_EMC_PROJECT.URGENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'sendType',
            title: 'T_EMC_PROJECT.SENDTYPE',
            type: 'select',
            items: [{
                text: '邮寄',
                value: '邮寄'
            }, {
                text: '自送样',
                value: '自送样'
            },]
        }, {
            field: 'projType',
            title: '任务类型',
            type: 'select',
            items: [{
                text: '周',
                value: '周'
            }, {
                text: '月',
                value: '月'
            }, {
                text: '季度',
                value: '季度'
            }, {
                text: '半年',
                value: '半年'
            }, {
                text: '年度',
                value: '年度'
            }, {
                text: '其他',
                value: '其他'
            },]
        }, {
            field: 'ext$.subpackage',
            title: 'T_EMC_PROJECT.SUBPACKAGE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'subcontractorId',
            title: '分包单位',
            type: 'select',
            search: true,
            category: 'subcontractor',
            editor: true
        }, {
            field: 'subPackageDesc',
            title: 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators: ['strLength[0,10]'],
        }, {
            field: 'createdByName',
            title: '登记人',
            readonly: true
        },  {
            field: 'entrustDate',
            title: 'T_EMC_PROJECT.ENTRUSTDATE',
            type: 'date',
            validators: ['notEmpty'],
        }, {
            field: 'compleDate',
            title: 'T_EMC_PROJECT.COMPLEDATE',
            type: 'date',
        },{
            field: 'projCreatedTime',
            title: '登记日期',
            readonly: true,
        }, {
            field: 'requiredCompleteDate',
            title: '预计进场时间',
            type: 'date'
        }, {
            field: 'reportAptitude',
            title: 'T_EMC_PROJECT.REPORTAPTITUDE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportAptitude'
            },
            validators: ['notEmpty'],
        }, {
            field: 'projectPrice',
            title: '任务金额',
            validators: ['notEmpty','number'],
        }, {
            field: 'amountDeterm',
            title: '金额待定',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        },{
            field: 'businessUserName',
            title: '业务员',
            category: 'user',
            type: 'choose',
            single: true,
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        businessUserName: rows[0].userName,
                        businessUserId: rows[0].id
                    });
                }
            },
            onClean: function () {
                this.setData({
                    businessUserName: '',
                    businessUserId: ''
                });
            }
        }, {
            field: 'ext$.accountingflag',
            title: '单独核算',
            preInsert: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            editor: true,
        }, {
            field: 'ext$.belongarea',
            title: '所属区域',
            type: 'choose',
            category: 'emc-adm-area',
            onAfterChoose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.setData({
                        ext$ : {
                            belongarea : data,
                        }
                    });
                }
            },
            colspan: 2,
        }, {
            field: 'projSection',
            title: '分项工程',
            validators: ['strLength[0,40]'],
        }, {
            field: 'customerSource',
            title: '客户来源',
            validators: ['strLength[0,40]'],
        }, {
            field: 'detectPurpose',
            title: 'T_EMC_PROJECT.DETECTPURPOSE',
            validators: ['strLength[0,300]'],
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJECT.REMARK',
            validators: ['strLength[0,800]'],
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'remarkOne',
            title: '备注1',
            validators: ['strLength[0,400]','notEmpty'],
            type: 'textarea',
            colspan: 3,
            placeholder : '折扣率',
        }, {
            field: 'remarkTwo',
            title: '备注2',
            validators: ['strLength[0,400]'],
            type: 'textarea',
            colspan: 3,
            placeholder : '年度监测方案的字段/基他检测或者结算注意事项',
        }, {
            field: 'ext$.reportremark',
            title: '报告备注',
            validators: ['strLength[0,400]'],
            type: 'textarea',
            colspan: 3,
        }];
    },

    getBaseInfoFormFields1: function () {
        return [{
            field: 'ext$.contractno',
            title: 'T_EMC_PROJECT.CONTRACTNO',
            readonly: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true
        }, /*{
            field : 'erpOrderNumber',
            title : 'T_EMC_PROJECT.ERPORDERNUMBER',
            readonly : true
        },*/ {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            type: 'comboBox',
            category: 'bizCategoryProject',
            validators: ['notEmpty', 'strLength[0,500]'],
            editable: true,
            colspan: 2
        }, {
            field: 'labInnerNo',
            title: 'T_EMC_PROJECT.LABINNERNO',
        }, {
            field: 'ext$.biddername',
            title: 'T_EMC_PROJECT.BIDDERNAME',
            readonly: true
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
            readonly: true,
            onChange: function (field, value, rowIndex, oldValue, text) {
                if (!Gikam.isEmpty(value)) {
                    var _form = this;
                    Gikam.getJson(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/' + value, Gikam.getJsonWrapper({}, ['', []])).done(function (row) {
                        let projectNo = _form.getData().projNo;
                        projectNo = projectNo.substring(0, projectNo.length - 1) + row.bizCategoryId;
                        _form.setData({
                            bizCategoryId: value,
                            bizCategoryCode: row.bizCategoryId,
                            projNo: projectNo
                        });
                    });
                }
            },
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly: true
        }, {
            field: 'projCreatedTime',
            title: 'T_EMC_PROJECT.PROJCREATEDTIME',
            readonly: true,
        }, /*{
            field : 'ext$.customername',
            title : 'T_EMC_PROJECT.CUSTOMERNAME',
            validators : [ 'notEmpty' ],
            colspan : 2
        }, {
            field : 'ext$.customercontact',
            title : 'T_EMC_PROJECT.CUSTOMERCONTACT',
            validators : [ 'notEmpty' ],
        },*/ {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORGNAME',
            //colspan : 2,
            type: 'choose',
            category: 'client',
            single: true,
            validators: ['notEmpty',],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var entrustOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    this.setData({
                        entrustOrgId: rows[0].id,
                        entrustOrgName: rows[0].clientName,
                        entrustOrgAddress: entrustOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));

                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                    Gikam.post(url).done(function (items) {
                        if (!Gikam.isEmpty(items.rows)) {
                            _this.setData({
                                entrustOrgContact: items.rows[0].lxrName,
                                entrustOrgPhone: items.rows[0].lxrPhone,
                            });
                        } else {
                            _this.setData({
                                entrustOrgContact: '',
                                entrustOrgPhone: '',
                            });
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    entrustOrgId: '',
                    entrustOrgName: '',
                    entrustOrgAddress: '',
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'entrustOrgAddress',
            title: 'T_EMC_PROJECT.ENTRUSTORGADDRESS',
            validators: ['notEmpty',],
            colspan: 2
        }, {
            field: 'entrustOrgContact',
            title: 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
        }, {
            field: 'entrustOrgPhone',
            title: 'T_EMC_PROJECT.ENTRUSTORGPHONE',
        }, {}, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORGNAME',
            //colspan : 2,
            type: 'choose',
            category: 'subClient',
            single: true,
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                return {
                    id_SEQ: this.getData().entrustOrgId,
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var testOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    var noMonitorPlace = Gikam.isEmpty(rows[0].ext$.monitorplace);
                    this.setData({
                        testOrgId: !noMonitorPlace ? '' : rows[0].id,
                        testOrgName: noMonitorPlace ? rows[0].clientName : rows[0].ext$.monitorplace,
                        testOrgAddress: noMonitorPlace ? testOrgAddress : '',
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));

                    if (noMonitorPlace) {
                        var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                        Gikam.post(url).done(function (items) {
                            if (!Gikam.isEmpty(items.rows)) {
                                _this.setData({
                                    testOrgContact: items.rows[0].lxrName,
                                    testOrgPhone: items.rows[0].lxrPhone,
                                });
                            } else {
                                _this.setData({
                                    testOrgContact: '',
                                    testOrgPhone: '',
                                });
                            }
                        });
                    }
                }
            },
            onClean: function () {
                this.setData({
                    testOrgId: '',
                    testOrgName: '',
                    testOrgAddress: '',
                    testOrgContact: '',
                    testOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'testOrgAddress',
            title: 'T_EMC_PROJECT.TESTORGADDRESS',
            validators: ['notEmpty'],
            colspan: 2
        }, {
            field: 'testOrgContact',
            title: 'T_EMC_PROJECT.TESTORGCONTACT'
        }, {
            field: 'testOrgPhone',
            title: 'T_EMC_PROJECT.TESTORGPHONE'
        }, {}, {
            field: 'ext$.subpackage',
            title: 'T_EMC_PROJECT.SUBPACKAGE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'qcCheck',
            title: 'T_EMC_PROJECT.QCCHECK',
            type: 'select',
            category: 'sys',
            firstBlank: false,
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'ext$.reporttype',
            title: 'T_EMC_PROJECT.REPORTTYPE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportType'
            },
            validators: ['notEmpty'],
            onChange: function (field, value) {
                console.info(field, value)
            }
        }, {
            field: 'reportFee',
            title: 'T_EMC_PROJECT.REPORTFEE',
        }, {
            field: 'urgent',
            title: 'T_EMC_PROJECT.URGENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'compleDate',
            title: 'T_EMC_PROJECT.COMPLEDATE',
            type: 'date',
        }, {
            field: 'projAmount',
            title: 'T_EMC_PROJECT.PROJAMOUNT',
            type: 'number',
        }, {
            field: 'reportTitle',
            title: 'T_EMC_PROJECT.REPORTTITLE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportTitle'
            },
            validators: ['notEmpty'],
        }, {
            field: 'periodType',
            title: 'T_EMC_PROJECT.PERIODTYPE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'periodType'
            },
            validators: ['notEmpty'],
            /*onChange : function (field, value) {
                this.setData({
                    periodType : value
                })
                // 对于加急和特急的，必须填写具体的周期
                if (value === '2' || value === '3') {
                    this.addFieldValidator('period', ['notEmpty']);
                } else {
                    this.removeFieldValidator('period', ['notEmpty']);
                }
            }*/
        }, {
            field: 'period',
            title: 'T_EMC_PROJECT.PERIOD',
            validators: ['notEmpty'],
        }, {
            field: 'reportAptitude',
            title: 'T_EMC_PROJECT.REPORTAPTITUDE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportAptitude'
            },
            validators: ['notEmpty'],
        }, {
            field: 'reportUse',
            title: 'T_EMC_PROJECT.REPORTUSE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportUse'
            },
            validators: ['notEmpty'],
        }, {
            field: 'otherReportUse',
            title: 'T_EMC_PROJECT.OTHERREPORTUSE',
        }, {
            field: 'pretreatment',
            title: 'T_EMC_PROJECT.PRETREATMENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'pretreatmentRemark'
            },
            validators: ['notEmpty'],
        }, {
            field: 'needQcReport',
            title: 'T_EMC_PROJECT.NEEDQCREPORT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
            validators: ['notEmpty'],
        }, {
            field: 'qcReportType',
            title: 'T_EMC_PROJECT.QCREPORTTYPE',
        }, {
            field: 'needSplitReport',
            title: 'T_EMC_PROJECT.NEEDSPLITREPORT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
            validators: ['notEmpty'],
        }, {
            field: 'subcontractSupplier',
            title: 'T_EMC_PROJECT.SUBCONTRACTSUPPLIER'
        }, {
            field: 'reportEnName',
            title: 'T_EMC_PROJECT.REPORTENNAME'
        }, {
            field: 'reportCnName',
            title: 'T_EMC_PROJECT.REPORTCNNAME'
        }, {
            field: 'reportEnAddress',
            title: 'T_EMC_PROJECT.REPORTENADDRESS'
        }, {
            field: 'reportCnAddress',
            title: 'T_EMC_PROJECT.REPORTCNADDRESS'
        }, {
            field: 'testingLab',
            title: 'T_EMC_PROJECT.TESTINGLAB'
        }, {
            field: 'testMethodAssigner',
            title: 'T_EMC_PROJECT.TESTMETHODASSIGNER',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testMethodAssigner'
            },
            validators: ['notEmpty'],
        }, /*{
            field: 'mergeReport',
            title: 'T_EMC_PROJECT.MERGEREPORT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        },*/ {
            field: 'reportPostAddress',
            title: 'T_EMC_PROJECT.REPORTPOSTADDRESS'
        }, {
            field: 'addressee',
            title: 'T_EMC_PROJECT.ADDRESSEE'
        }, {
            field: 'recipientPhone',
            title: 'T_EMC_PROJECT.RECIPIENTPHONE'
        }, {
            field: 'detectPurpose',
            title: 'T_EMC_PROJECT.DETECTPURPOSE',
            validators: ['strLength[0,300]'],
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJECT.REMARK',
            validators: ['strLength[0,800]'],
            type: 'textarea',
            colspan: 3,
        }];
    },

    getBaseInfoFormFields2: function () {
        return [{
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            type: 'comboBox',
            category: 'bizCategoryProject',
            validators: ['notEmpty', 'strLength[0,500]'],
            editable: true
        }, {
            field: 'ext$.contractno',
            title: 'T_EMC_PROJECT.CONTRACTNO',
            type: 'choose',
            category: 'contract',
            single: true,
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            contractid: rows[0].id,
                            contractno: rows[0].contractNo,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        contractid: '',
                        contractno: '',
                    }
                });
            }
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly: true
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORGNAME',
            type: 'choose',
            category: 'client',
            single: true,
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var entrustOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    this.setData({
                        entrustOrgId: rows[0].id,
                        entrustOrgName: rows[0].clientName,
                        entrustOrgAddress: entrustOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));

                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                    Gikam.post(url).done(function (items) {
                        if (!Gikam.isEmpty(items.rows)) {
                            _this.setData({
                                entrustOrgContact: items.rows[0].lxrName,
                                entrustOrgPhone: items.rows[0].lxrPhone,
                            });
                        } else {
                            _this.setData({
                                entrustOrgContact: '',
                                entrustOrgPhone: '',
                            });
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    entrustOrgId: '',
                    entrustOrgName: '',
                    entrustOrgAddress: '',
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'entrustOrgAddress',
            title: 'T_EMC_PROJECT.ENTRUSTORGADDRESS',
            validators: ['notEmpty', 'strLength[0,90]'],
        }, {
            field: 'entrustOrgContact',
            title: 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
            type: 'choose',
            category: 'clientContact',
            validators: ['notEmpty'],
            onBeforeChoose: function () {
                return {
                    clientId_SEQ: this.getData().entrustOrgId,
                }
            },
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        entrustOrgContact: rows[0].lxrName,
                        entrustOrgPhone: rows[0].lxrPhone,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    entrustOrgContact: '',
                    entrustOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'entrustOrgPhone',
            title: 'T_EMC_PROJECT.ENTRUSTORGPHONE',
            validators: ['notEmpty', 'phone']
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORGNAME',
            type: 'choose',
            category: 'subClient',
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var testOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    var noMonitorPlace = Gikam.isEmpty(rows[0].ext$.monitorplace);
                    this.setData({
                        testOrgId: !noMonitorPlace ? '' : rows[0].id,
                        testOrgName: noMonitorPlace ? rows[0].clientName : rows[0].ext$.monitorplace,
                        testOrgAddress: testOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id: rows[0].id
                    }), Gikam.getJsonWrapper({}, ['emcClientServiceImpl', rows]));

                    if (noMonitorPlace) {
                        var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/' + rows[0].id + '/contacts/queries';
                        Gikam.post(url).done(function (items) {
                            if (!Gikam.isEmpty(items.rows)) {
                                _this.setData({
                                    testOrgContact: items.rows[0].lxrName,
                                    testOrgPhone: items.rows[0].lxrPhone,
                                });
                            } else {
                                _this.setData({
                                    testOrgContact: '',
                                    testOrgPhone: '',
                                });
                            }
                        });
                    }
                }
            },
            onClean: function () {
                this.setData({
                    testOrgId: '',
                    testOrgName: '',
                    testOrgAddress: '',
                    testOrgContact: '',
                    testOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'testOrgAddress',
            title: 'T_EMC_PROJECT.TESTORGADDRESS',
            validators: ['strLength[0, 90]'],
        }, {
            field: 'testOrgContact',
            title: 'T_EMC_PROJECT.TESTORGCONTACT',
            type: 'choose',
            category: 'clientContact',
            onBeforeChoose: function () {
                return {
                    clientId_SEQ: this.getData().testOrgId,
                }
            },
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        testOrgContact: rows[0].lxrName,
                        testOrgPhone: rows[0].lxrPhone,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    testOrgContact: '',
                    testOrgPhone: '',
                });
            },
            editable: true
        }, {
            field: 'testOrgPhone',
            title: 'T_EMC_PROJECT.TESTORGPHONE',
            validators: ['phone'],
        }, {
            field: 'testOrgEmail',
            title: 'T_EMC_PROJECT.TESTORGEMAIL',
        }, {
            field: 'testOrgFixedPhone',
            title: 'T_EMC_PROJECT.TESTORGFIXEDPHONE',
        }, {
            field: 'entrustDate',
            title: 'T_EMC_PROJECT.ENTRUSTDATE',
            type: 'date',
        }, {
            field: 'sendType',
            title: 'T_EMC_PROJECT.SENDTYPE',
            validators: ['strLength[0,10]'],
        }, {
            field: 'sendDate',
            title: 'T_EMC_PROJECT.SENDDATE',
            type: 'date',
        }, {
            field: 'entrustMethod',
            title: 'T_EMC_PROJECT.ENTRUSTMETHOD',
            validators: ['strLength[0,10]'],
        }, {
            field: 'subPackageDesc',
            title: 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators: ['strLength[0,10]'],
        }, {
            field: 'ext$.reportorgname',
            title: 'T_EMC_PROJECT.REPORTORG',
            type: 'choose',
            category: 'org-simple',
            single: true,
            onBeforeChoose: function () {
                return {
                    orgCategory_SEQ: 'department'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportOrgId: rows[0].id,
                        ext$: {
                            reportorgname: rows[0].orgName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    reportOrgId: '',
                    ext$: {
                        reportorgname: '',
                    }
                });
            },
        }, {
            field: 'ext$.subpackage',
            title: 'T_EMC_PROJECT.SUBPACKAGE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }/*, {
            field : 'ext$.qualitycontrol',
            title : 'T_EMC_PROJECT.QUALITYCONTROL',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }*/, {
            field: 'urgent',
            title: 'T_EMC_PROJECT.URGENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }/*, {
            field : 'ext$.detect',
            title : 'T_EMC_PROJECT.DETECT',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }*/, {
            field: 'qcCheck',
            title: 'T_EMC_PROJECT.QCCHECK',
            type: 'select',
            category: 'sys',
            firstBlank: false,
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'ext$.operationorg',
            title: 'T_EMC_PROJECT.OPERATIONORG',
        }/*, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            editor : true,
            category : 'reportEditEmp',
            type : 'choose',
            single : true,
            validators : ['notEmpty'],
            onBeforeChoose : function() {
                return {
                    editReport_SEQ : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportEditUserName : rows[0].userName,
                        reportEditUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reportEditUserName : '',
                    reportEditUserId : ''
                });
            }
        }*/, {
            field: 'ext$.reporttype',
            title: 'T_EMC_PROJECT.REPORTTYPE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'reportType'
            },
            validators: ['notEmpty'],
        }, {
            field: 'compleDate',
            title: 'T_EMC_PROJECT.COMPLEDATE',
            type: 'date',
        }, {
            field: 'projAmount',
            title: 'T_EMC_PROJECT.PROJAMOUNT',
            type: 'number',
        }, {
            field: 'reportDeliveryWay',
            title: 'T_EMC_CONTRACT.RECEIVEREPORTWAY',
            validators: ['strLength[0, 30]'],
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'receiveReportWay',
            },
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.ENTRUSTCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
            readonly: true
        }, {
            field: 'ext$.samplesource',
            title: 'T_EMC_PROJECT.SAMPLESOURCE',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'sampleSource',
            },
            readonly: true
        }, {
            field: 'description',
            title: 'T_EMC_PROJECT.DESCRIPTION',
            validators: ['strLength[0,170]'],
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJECT.REMARK',
            validators: ['strLength[0,800]'],
            type: 'textarea',
            colspan: 3,
        }];
    },

    getBaseInfoFormFields3: function () {
        return [{
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            type: 'comboBox',
            category: 'bizCategoryProject',
            validators: ['notEmpty', 'strLength[0,500]'],
            editable: true
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly: true
        }, {
            field: 'ext$.contractno',
            title: 'T_EMC_PROJECT.CONTRACTNO',
            type: 'choose',
            category: 'contract',
            single: true,
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$: {
                            contractid: rows[0].id,
                            contractno: rows[0].contractNo,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    ext$: {
                        contractid: '',
                        contractno: '',
                    }
                });
            }
        }, {
            field: 'monitorOrgName',
            title: 'T_EMC_PROJECT.MONITORORGNAME',
            validators: ['notEmpty', 'strLength[0,24]']
        }, {
            field: 'projAddress',
            title: 'T_EMC_PROJECT.PROJADDRESS',
            validators: ['notEmpty']
        }, {
            field: 'subPackageDesc',
            title: 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators: ['strLength[0,10]'],
        }, {
            field: 'ext$.reportorgname',
            title: 'T_EMC_PROJECT.REPORTORG',
            type: 'choose',
            category: 'org-simple',
            single: true,
            onBeforeChoose: function () {
                return {
                    orgCategory_SEQ: 'department'
                }
            },
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportOrgId: rows[0].id,
                        ext$: {
                            reportorgname: rows[0].orgName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    reportOrgId: '',
                    ext$: {
                        reportorgname: '',
                    }
                });
            }
        }, {
            field: 'ext$.subpackage',
            title: 'T_EMC_PROJECT.SUBPACKAGE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }/*, {
            field : 'ext$.qualitycontrol',
            title : 'T_EMC_PROJECT.QUALITYCONTROL',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }*/, {
            field: 'urgent',
            title: 'T_EMC_PROJECT.URGENT',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }/*, {
            field : 'ext$.detect',
            title : 'T_EMC_PROJECT.DETECT',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }*/, {
            field: 'ext$.operationorg',
            title: 'T_EMC_PROJECT.OPERATIONORG',
        }/*, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            editor : true,
            category : 'reportEditEmp',
            type : 'choose',
            single : true,
            validators : ['notEmpty'],
            onBeforeChoose : function() {
                return {
                    editReport_SEQ : '1',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportEditUserName : rows[0].userName,
                        reportEditUserId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reportEditUserName : '',
                    reportEditUserId : ''
                });
            }
        }*/, {
            field: 'compleDate',
            title: 'T_EMC_PROJECT.COMPLEDATE',
            type: 'date',
        }, {
            field: 'projAmount',
            title: 'T_EMC_PROJECT.PROJAMOUNT',
            type: 'number',
        }, {
            field: 'ext$.receivereportway',
            title: 'T_EMC_CONTRACT.RECEIVEREPORTWAY',
            validators: ['strLength[0, 30]'],
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'receiveReportWay',
            },
        }, {
            field: 'description',
            title: 'T_EMC_PROJECT.DESCRIPTION',
            validators: ['strLength[0,170]'],
            type: 'textarea',
            colspan: 3,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJECT.REMARK',
            validators: ['strLength[0,800]'],
            type: 'textarea',
            colspan: 3,
        }];
    },

    getOrderColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderNum',
            title: 'T_EMC_ORDER.ORDERNUM',
            validators: ['integer'],
            // editor : true,
            width: 80,
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO',
            width: 120,
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO2',
            validators: ['strLength[0,100]'],
            editor: true,
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER.ORDERCATEGORY',
            width: 120,
        }, {
            field: 'qcCategoryCode',
            title: 'T_EMC_ORDER.QCCATEGORYCODE',
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCNAME',
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,100]', 'notEmpty'],
            editor: true,
        }, {
            field: 'orderQty',
            title: 'T_EMC_ORDER.ORDERQTY',
            editor: true,
        }, {
            field: 'sampleAddress',
            title: 'T_EMC_ORDER.SAMPLEADDRESS',
            validators: ['strLength[0,40]'],
            editor: true,
        }, /*{
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
            editor : true
        },*/ {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
            validators: ['notEmpty'],
            editor: true,
            width: 100,
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME',
            type: 'time',
            validators: ['notEmpty'],
            editor: true,
            width: 100,
        }, {
            field: 'ext$.sender',
            title: 'T_EMC_ORDER.SENDER',
            editor: true,
            editable: true,
            type: 'choose',
            category: 'user-simple',
            single: true,
            validators: ['notEmpty'],
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        senderId: rows[0].id,
                        ext$: {
                            sender: rows[0].userName,
                        },
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    senderId: '',
                    ext$: {
                        sender: '',
                    },

                });
            },
            // }, {
            //     field : 'ext$.checker',
            //     title : 'T_EMC_ORDER.CHECKER',
            //     editor : true,
            //     editable : true,
            //     type : 'choose',
            //     category : 'user-simple',
            //     single : true,
            //     onAfterChoose : function(rows) {
            //         if (!Gikam.isEmpty(rows)) {
            //             this.setData({
            //                 id : this.getActivedRow().id,
            //             	checkerId : rows[0].id,
            //             	ext$ : {
            //                 	checker : rows[0].userName,
            //                 },
            //             });
            //         }
            //     },
            //     onClean : function() {
            //         this.setData({
            //             id : this.getActivedRow().id,
            //         	checkerId : '',
            //         	ext$ : {
            //             	checker : '',
            //             },
            //         });
            //     },
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,540]'],
            editor: true
            // }, {
            //     field : 'parentOrderNo',
            //     title : 'T_EMC_ORDER.PARENTORDERNO',
            //     width : 120,
            //     editor : true,
            //     type : 'choose',
            //     category : 'order',
            //     single : true,
            //     onBeforeChoose : function() {
            //         let row = this.getActivedRow();
            //         if (row.qcCategoryCode === 'N/A') {
            //             Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NA_CAT_NOT_SET_PARENTNO');
            //             return false;
            //         }
            //
            //         return {
            //             projId_SEQ : row.projId,
            //             id_DIFFER : row.id
            //         }
            //     },
            //     onAfterChoose : function(rows) {
            //         if (!Gikam.isEmpty(rows)) {
            //             this.setData({
            //                 id : this.getActivedRow().id,
            //                 parentOrderNo : rows[0].orderNo
            //             });
            //         }
            //     },
            //     onClean : function() {
            //         this.setData({
            //             id : this.getActivedRow().id,
            //             parentOrderNo : ''
            //         });
            //     }
            // }, {
            //     field : 'ext$.volume',
            //     title : 'T_EMC_ORDER.VOLUME',
            //     editor : true,
            //     width : 160,
            // } , {
            //     field : 'ext$.packing',
            //     title : 'T_EMC_ORDER.PACKING',
            //     editor : true,
            //     width : 80,
            // } ,{
            //     field : 'ext$.isblank',
            //     title : 'T_EMC_ORDER.ISBLANK',
            //     editor : true,
            //     type : 'select',
            //     items:[
            //         { text: '是', value : '是'},
            //         { text: '否', value : '否'},
            //     ]
            // search : true,
            // category : 'sys',
            // param : {
            //     codeCategoryId : 'yesorno'
            // },
        }];
    },

    getItemGroupChooseListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_INDEX_TEST_ITEM.TESTNAME'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_MONITOR_SCHEME.ITEMGROUP'
        }];
    },

    getOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'schemeName',
            title: 'T_EMC_ORDER_TASK.SCHEMENAME'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTMETHOD'
        }, {
            field: 'ext$.itemgroup',
            title: 'T_EMC_ORDER_TASK.ITEMGROUP'
        }, {
            field: 'analysisOrgId',
            title: 'T_EMC_ORDER_TASK.ANALYSISORGID'
        }, {
            field: 'analysisOrgId',
            title: 'T_EMC_ORDER_TASK.ANALYSISORG',
            type: 'select',
            search: true,
            category: 'org-simple',
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER_TASK.LABELNO'
        }, {
            field: 'containerGroup',
            title: 'T_EMC_ORDER_TASK.CONTAINERGROUP',
        }, {
            field: 'ext$.containercategorycode',
            title: 'T_EMC_ORDER_TASK.CONTAINERCATEGORYCODE',
        }, {
            field: 'ext$.containercategory',
            title: 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_TASK.METHOD',
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_TASK.METHODDESC',
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER_TASK.QCCATEGORY',
            type: 'choose',
            category: 'qcCategory',
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: Gikam.getComp('emc-project-edit-list-order-task-grid').getActivedRow().id,
                        qcCategoryId: rows[0].id,
                        ext$: {
                            qcname: rows[0].qcName,
                        }
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: Gikam.getComp('emc-project-edit-list-order-task-grid').getActivedRow().id,
                    qcCategoryId: '',
                    ext$: {
                        qcname: '',
                    }
                });
            },
            editor: true
        }, {
            field: 'subPackageNo',
            title: 'T_EMC_ORDER_TASK.SUBPACKAGENO',
            width: 100,
        }, {
            field: 'subcontractorId',
            title: 'T_EMC_ORDER_TASK.SUBCONTRACTOR',
            type: 'select',
            search: true,
            category: 'subcontractor',
            editor: true
        },];
    },

    getOrderBatchUpdateFields: function () {
        return [{
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME',
            type: 'time',
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC',
            validators: ['strLength[0,21]'],
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,540]']
        }, {
            field: 'ext$.volume',
            title: 'T_EMC_ORDER.VOLUME',
            validators: ['strLength[0,120]'],
        }, {
            field: 'ext$.packing',
            title: 'T_EMC_ORDER.PACKING',
            validators: ['strLength[0,120]'],
        }, {
            field: 'ext$.sender',
            title: 'T_EMC_ORDER.SENDER',
            editor: true,
            editable: true,
            type: 'choose',
            category: 'user-simple',
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        senderId: rows[0].id,
                        ext$: {
                            sender: rows[0].userName,
                        },
                    });
                }
            },
            onClean: function () {
                this.setData({
                    senderId: '',
                    ext$: {
                        sender: '',
                    },
                });
            },
        }, {
            field: 'ext$.checker',
            title: 'T_EMC_ORDER.CHECKER',
            editor: true,
            editable: true,
            type: 'choose',
            category: 'user-simple',
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        checkerId: rows[0].id,
                        ext$: {
                            checker: rows[0].userName,
                        },
                    });
                }
            },
            onClean: function () {
                this.setData({
                    checkerId: '',
                    ext$: {
                        checker: '',
                    },
                });
            },
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO2',
            validators: ['strLength[0,100]'],
            editor: true,
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,100]'],
            editor: true
        }, {
            field: 'ext$.isblank',
            title: 'T_EMC_ORDER.ISBLANK',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }];
    },

    getOrderContainerGroupUpdateFields: function () {
        return [{
            field: 'containerGroup',
            title: 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_ITEM_GROUP.FIXATIVE'
        }, {
            field: 'storeCondition',
            title: 'T_EMC_ORDER_ITEM_GROUP.STORECONDITION'
        }];
    },

    getOrderItemGroupColumns: function () {
        return [{
            checkbox: true
        },{
            index: true
        // }, {
        //     field: 'sortNum',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.SORTNUM',
        }, /*{
            field : 'originalOrderNo',
            title : 'T_EMC_ORDER_TASK.ORIGINALORDERNO',
            editor : true
        }, {
            field : 'batchNo',
            title : 'T_EMC_ORDER_ITEM_GROUP.BATCHNO',
            styleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
        },*/ {
            field: 'schemeName',
            title: 'T_EMC_ORDER_ITEM_GROUP.SCHEMENAME'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
        }, {
            field: 'ext$.testmethodtype',
            title: 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type: 'select',
            search: false,
            category: 'sys',
            param: {
                codeCategoryId: 'testMethodType',
            }
        }, {
            field: 'itemGroup',
            title: 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
        }, {
            field: 'methodName',
            title: 'T_EMC_ORDER_ITEM_GROUP.METHODNAME',
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
        },{
            field: 'taskLowLimit',
            title : '标准下限',
            editor : true,
        },{
            field: 'taskHighLimit',
            title : '标准上限',
            editor : true,
        },{
            field : 'specifyMethod',
            title : '客户指定方法',
            editor : true,
            type : 'simpleCheckbox'
        // }, {
        //     field: 'containerGroup',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERGROUP',
        //     width: 80
        // }, {
        //     field: 'containerCategoryCode',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYCODE',
        //     width: 90
        // }, {
        //     field: 'containerCategoryName',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYNAME'
        // }, {
        //     field: 'fixative',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.FIXATIVE'
        // }, {
        //     field: 'ext$.sampleweight',
        //     title: 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT'
        // }, {
        //     field: 'storeCondition',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.STORECONDITION'
        // }, {
        //     field: 'analysisOrgName',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.ANALYSISORGNAME'
        // }, {
        //     field: 'qcName',
        //     title: 'T_EMC_ORDER_ITEM_GROUP.QCNAME',
        }, {
            field: 'subPackageNo',
            title: 'T_EMC_ORDER_ITEM_GROUP.SUBPACKAGENO'
        }, {
            field: 'subcontractorId',
            title: 'T_EMC_ORDER_ITEM_GROUP.SUBCONTRACTOR',
            type: 'select',
            search: true,
            category: 'subcontractor',
            editor: true,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-project-edit-list-order-item-group-grid');
                var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [{
                        id: grid.getData()[rowIndex].id,
                        orderId: grid.getData()[rowIndex].orderId,
                        subcontractorId: value
                    }];
                    Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/action/subcontractor-edit', Gikam.getJsonWrapper({}, ['emcOrderItemGroupServiceImpl', selections])).done(function () {
                        Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
                    })
                } else {
                    var selections = [{
                        id: grid.getData()[rowIndex].id,
                        orderId: grid.getData()[rowIndex].orderId,
                        subcontractorId: '',
                        subPackageNo: ''
                    }];
                    Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, ['emcOrderItemGroupServiceImpl', selections])).done(function () {
                        Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
                    })
                }
            }
        }, {
            field: 'qualification',
            title: 'T_EMC_ORDER_ITEM_GROUP.QUALIFICATION',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'dataSharing',
            title: 'T_EMC_ORDER_ITEM_GROUP.DATASHARING',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno'
            },
        }, {
            field: 'ext$.subcontract2',
            title: '采样后分包',
            type: 'simpleCheckbox',
            editor: true,
            onChange: function (field, value, index) {
                console.info(field, value)
                if (value === '1') {
                    this.setData({
                        index: index,
                        ext$: {
                            subcontract2: value,
                            subpackagetype: 'orderSubpackage'
                        }
                    })
                } else {
                    this.setData({
                        index: index,
                        ext$: {
                            subcontract2: value,
                            subpackagetype: ''
                        }
                    })
                }
            }
        }, {
            field: 'ext$.stdname',
            title: 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field: 'ext$.indexname',
            title: 'T_EMC_PROJ_ORDER.INDEXNAME',
            type: 'choose',
            category: 'indexCategory',
            single: true,
            cleanable: false,
            onBeforeChoose: function () {
                return {
                    //proj_order_assigned : '1'
                }
            },
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        indexId: rows[0].id,
                        indexCategoryId: rows[0].indexCategoryId,
                        ext$: {
                            stdname: rows[0].ext$.stdname,
                            indexname: rows[0].indexName,
                            index1: rows[0].index1,
                            index2: rows[0].index2,
                            index3: rows[0].index3
                        }
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    indexId: '',
                    indexCategoryId: '',
                    ext$: {
                        stdname: '',
                        indexname: '',
                        index1: '',
                        index2: '',
                        index3: ''
                    }
                })
            },
            editor: true
        }, {
            field: 'indexId',
            title: 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field: 'ext$.index1',
            title: 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field: 'ext$.index2',
            title: 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field: 'ext$.index3',
            title: 'T_EMC_PROJ_ORDER.INDEXID3'
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        }];
    },

    getChooseListColumns: function () {
        return [{
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true,
        }, {
            field: 'labInnerNo',
            title: 'T_EMC_PROJECT.LABINNERNO',
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
        }]
    },

    getProjectQueryFormFields: function () {
        return [{
            field: 'bizClassId',
            title: '业务类别',
            type: 'select',
            search: true,
            firstBlank: false,
            category: 'bizClass',
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        bizClassId_EQ: value,
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('bizCategoryId', items.map(function (item) {
                            return {
                                text: item.bizCategoryName,
                                value: item.id,
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                bizCategoryId: value,
                            });
                        } else {
                            _form.setData({
                                bizCategoryId: '',
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('bizCategoryId', []);
                }
            },
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            preInsert: false
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            validators: ['strLength[0,500]'],
            preInsert: false,
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME',
            type: 'choose',
            category: 'user-simple',
            single: true,
            onAfterChoose: function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        createdById: rows[0].id,
                        createdByName: rows[0].userName,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    createdById: '',
                    createdByName: '',
                });
            },
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'monitorTmplId',
            title: 'T_EMC_ORDER_TASK.ORDERCATEGORY',
            type: 'select',
            search: true,
            category: 'monitorTmpl',
            preInsert: false
        }, {
            field: 'taskYear',
            title: 'T_EMC_PROJECT.YEAR',
        }, {
            field: 'taskMonth',
            title: 'T_EMC_PROJECT.MONTH',
            validators: ['integer'],
            type: 'number',
            hasArrow: true
        }];
    },

    getProjectCopyToQueryFormFields: function () {
        return [{
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            category: 'bizCategory',
            readonly: true
        }, {
            field: 'sample',
            title: 'T_EMC_PROJECT.SAMPLE',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
            readonly: true
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            validators: ['strLength[0,500]'],
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
        },];
    },

    getProjectQueryListColumns: function () {
        return [{
            radio: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            readonly: true,
        }, {
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
        }, {
            field: 'taskDate',
            title: 'T_EMC_PROJECT.TASKDATE',
        }, {
            field: 'createdByName',
            title: 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field: 'testOrgName',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
        }, Gikam.status.getBpmnColumn(emcProject.workflow.dbTable),];
    },

    getMonitorPointListColumns: function () {
        return [{
            field: 'monitorPointCategoryName',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE'
        }, {
            field: 'monitorPointNo',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINT'
        }, {
            field: 'itemGroups',
            title: 'T_EMC_PROJ_SCHEME.INDEXNAME'
        }, {
            field: 'cycle',
            width: 120,
            title: 'T_EMC_PROJ_SCHEME.CYCLE'
        }, {
            field: 'frequency',
            width: 120,
            title: 'T_EMC_PROJ_SCHEME.FREQUENCY'
        }, {
            field: 'orgName',
            title: 'T_EMC_PROJ_SCHEME.SAMPLEORGID'
        }];
    },

    getProjectOrderColumns: function () {
        return [{
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO',
            validators: ['strLength[0,10]'],
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO',
            width: 140,
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,21]'],
        }, {
            field: 'parentOrderNo',
            title: 'T_EMC_ORDER.PARENTORDERNO',
            validators: ['strLength[0,10]'],
            width: 140,
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC',
            validators: ['strLength[0,21]'],
        }, {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
            width: 120,
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME',
            type: 'time',
            width: 110,
        }, {
            field: 'orderNum',
            title: 'T_EMC_ORDER.ORDERNUM',
            width: 100,
            validators: ['integer'],
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,540]'],
        },];
    },

    getCopyFormFields: function () {
        return [{
            field: 'taskDate',
            title: 'T_EMC_PROJECT.COPYTOTASKDATE',
            type: 'date',
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            validators: ['notEmpty']
        }, {
            field: 'copyQty',
            title: 'T_EMC_PROJECT.COPYQTY',
            value: 1,
            validators: ['notEmpty', 'integer', 'numRange[1,20]'],
            type: 'number',
            hasArrow: true,
            validators: ['notEmpty']
            // readonly : true
        }, {
            field: 'bizClassId',
            title: '业务类别',
            type: 'select',
            search: true,
            firstBlank: false,
            category: 'bizClass',
            onChange: function (field, value, rowIndex, oldValue, text) {
                var _form = this;
                var url = IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/queries/selectable';
                if (!Gikam.isEmpty(value)) {
                    url += Gikam.param({
                        bizClassId_EQ: value,
                    });
                    Gikam.getJson(url).done(function (items) {
                        _form.setSelectOptions('bizCategoryId', items.map(function (item) {
                            return {
                                text: item.bizCategoryName,
                                value: item.id,
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                bizCategoryId: value,
                            });
                        } else {
                            _form.setData({
                                bizCategoryId: '',
                            });
                        }
                    });
                } else {
                    _form.setSelectOptions('bizCategoryId', []);
                }
            },
            validators: ['notEmpty']
        }, {
            field: 'bizCategoryId',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            validators: ['notEmpty']
        },{
            field: 'year',
            title: '任务年度',
            type: 'year',
            validators: ['notEmpty'],
            value: Gikam.DateUtils.formatter(new Date(), 'yyyy'),
        },{
            field: 'projName',
            title: 'T_EMC_PROJECT.PROJNAME',
            validators: ['strLength[0,500]','notEmpty'],
        } /*{
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'splitContract',
            single : true,
            preInsert : true,
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    targetType : 'createProject'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$ : {
                            contractid : rows[0].contractId,
                            contractno : rows[0].ext$.contractno,
                            biddername : (rows[0].ext$.salername || '') + '/' + (rows[0].ext$.salerphone || '')
                        },
                        splitContractId : rows[0].id,
                        entrustOrgId : rows[0].entrustOrg,
                        entrustOrgName : rows[0].ext$.entrustorgname,
                        entrustOrgAddress : rows[0].ext$.entrustorgaddress,
                        entrustOrgContact : rows[0].ext$.entrustor,
                        entrustOrgPhone : rows[0].ext$.entrustorgphone,
                        testOrgId : rows[0].entrustOrg,
                        testOrgName : rows[0].ext$.entrustorgname,
                        testOrgAddress : rows[0].ext$.entrustorgaddress,
                        testOrgContact : rows[0].ext$.entrustor,
                        testOrgPhone : rows[0].ext$.entrustorgphone,
                        reportPostAddress : rows[0].ext$.entrustorgaddress,
                        addressee : rows[0].ext$.entrustor,
                        recipientPhone : rows[0].ext$.entrustorgphone,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        contractid : '',
                        contractno : '',
                        biddername : ''
                    },
                    splitContractId : '',
                    entrustOrgId : '',
                    entrustOrgName : '',
                    entrustOrgAddress : '',
                    entrustOrgContact : '',
                    entrustOrgPhone : '',
                    testOrgId : '',
                    testOrgName : '',
                    testOrgAddress : '',
                    testOrgContact : '',
                    testOrgPhone : ''
                });
            }
        }*/];
    },

    getAuditRecordListColumns: function () {
        return [{
            field: 'logDesc',
            title: 'T_EMC_AUDIT_RECORD.LOGDESC'
        }, {
            field: 'taskName',
            title: 'T_EMC_AUDIT_RECORD.TASKNAME'
        }, {
            field: 'nextTaskName',
            title: 'T_EMC_AUDIT_RECORD.NEXTTASKNAME'
        }, {
            field: 'operateCategory',
            title: 'T_EMC_AUDIT_RECORD.OPERATECATEGORY'
        }, {
            field: 'comments',
            title: 'T_EMC_AUDIT_RECORD.COMMENT'
        }, {
            field: 'createdByName',
            title: 'T_EMC_AUDIT_RECORD.CREATEDBYNAME'
        }, {
            field: 'createdTime',
            title: 'T_EMC_AUDIT_RECORD.CREATEDTIME'
        }, {
            field: 'auditCategory',
            title: 'T_EMC_AUDIT_RECORD.AUDITCATEGORY'
        }];
    },

    getProjectDirectorListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'empId',
            title: 'T_EMC_PROJECT_DIRECTOR.EMPID'
        }, {
            field: 'ext$.username',
            title: 'T_EMC_PROJECT_DIRECTOR.USERNAME'
        }, {
            field: 'phone',
            title: 'T_EMC_PROJECT_DIRECTOR.PHONE',
            editor: true,
            validators: ['phone']
        }];
    },

    getReportSendInfoListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'needCnReport',
            title: 'T_EMC_REPORT_SEND_INFO.NEEDCNREPORT',
            type: 'simpleCheckbox',
            editor: true,
        }, {
            field: 'cnReportCnt',
            title: 'T_EMC_REPORT_SEND_INFO.CNREPORTCNT',
            type: 'number',
            hasArrow: true,
            editor: true,
            isInteger: true,
        }, {
            field: 'needEnReport',
            title: 'T_EMC_REPORT_SEND_INFO.NEEDENREPORT',
            type: 'simpleCheckbox',
            editor: true,
        }, {
            field: 'enReportCnt',
            title: 'T_EMC_REPORT_SEND_INFO.ENREPORTCNT',
            type: 'number',
            hasArrow: true,
            editor: true,
            isInteger: true,
        }, {
            field: 'needCnEnReport',
            title: 'T_EMC_REPORT_SEND_INFO.NEEDCNENREPORT',
            type: 'simpleCheckbox',
            editor: true,
        }, {
            field: 'cnEnReportCnt',
            title: 'T_EMC_REPORT_SEND_INFO.CNENREPORTCNT',
            type: 'number',
            hasArrow: true,
            editor: true,
            isInteger: true,
        }];
    },

    getProjectOtherFeeListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'feeType',
            title: 'T_EMC_PROJECT_OTHER_FEE.FEETYPE',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'otherFeeType',
            },
            onChange: function (field, value, rowIndex, oldValue, text) {
                this.setData({
                    index: rowIndex,
                    feeType: value,
                    feeName: text
                });
            }
        }, {
            field: 'feeSinglePrice',
            title: 'T_EMC_PROJECT_OTHER_FEE.FEESINGLEPRICE',
            type: 'number',
            editor: true,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var row = this.getData()[rowIndex];
                if (!Gikam.isEmpty(row.feeSinglePrice) && !Gikam.isEmpty(row.feeCount)) {
                    let amount = parseFloat(row.feeSinglePrice) * parseFloat(row.feeCount);
                    amount = amount.toFixed(2);
                    this.setData({
                        index: rowIndex,
                        id: row.id,
                        feeSinglePrice: value,
                        amount: amount
                    })
                }
            }
        }, {
            field: 'feeCount',
            title: 'T_EMC_PROJECT_OTHER_FEE.FEECOUNT',
            type: 'number',
            hasArrow: true,
            editor: true,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var row = this.getData()[rowIndex];
                if (!Gikam.isEmpty(row.feeSinglePrice) && !Gikam.isEmpty(row.feeCount)) {
                    let amount = parseFloat(row.feeSinglePrice) * parseFloat(row.feeCount);
                    amount = amount.toFixed(2);
                    this.setData({
                        index: rowIndex,
                        id: row.id,
                        feeCount: value,
                        amount: amount
                    })
                }
            }
        }, {
            field: 'amount',
            title: 'T_EMC_PROJECT_OTHER_FEE.AMOUNT',
            type: 'number',
            editor: true,
        }];
    },

    getSchemeEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'sortNum',
            title: 'T_EMC_PROJ_SCHEME.SORTNUM',
            width: 80,
        }, {
            field: 'folderNo',
            title: 'T_EMC_PROJ_SCHEME.FOLDERID',
            width: 150,
        }, {
            field: 'monitorPointNo',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            editor: true
        }, {
            field: 'cycle',
            title: 'T_EMC_PROJ_SCHEME.CYCLE',
            range: '[1,24]',
            isInteger: true,
            width: 80,
            type: 'number',
            // hasArrow : true,
            editor: true,
            validators: ['notEmpty']
        }, {
            field: 'frequency',
            title: 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range: '[1,24]',
            isInteger: true,
            width: 80,
            type: 'number',
            // hasArrow : true,
            editor: true,
            validators: ['notEmpty']
        }, {
            field: 'sampleOrgId',
            title: 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor: true,
            type: 'select',
            search: true,
            category: 'org',
            param: {
                sampleDepartment_SEQ: '1',
                parentId_SEQ: workspace.user.ext$.parentorgid
            },
            // firstBlank: false,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJ_SCHEME.REMARK',
            validators: ['strLength[0,512]'],
            type: 'richText',
            editor: true
        }, {
            field: 'referFolder',
            title: '引用点位',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getSchemeEditListColumns1: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'folderNo',
            title: 'T_EMC_PROJ_SCHEME.FOLDERID',
            width: 150,
        }, {
            field: 'monitorPointNo',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTID',
            editable: true,
            /*            type : 'choose',
                        category : 'monitorPoint',
                        editable : false,
                        cleanable : false,
                        single : true,
                        onBeforeChoose : function() {
                            var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                            var testOrgId = formData.testOrgId ? formData.testOrgId : '-';
                            return {
                                clientId_SEQ : testOrgId
                            }
                        },
                        onAfterChoose : function(rows, index) {
                            if (!Gikam.isEmpty(rows)) {
                                this.setData({
                                    index : index,
                                    monitorPointId : rows[0].id,
                                    monitorPointNo : rows[0].monitorPointNo,
                                    monitorPointName : rows[0].monitorPointName,
                                    monitorPointCategoryId : rows[0].monitorPointCategoryId,
                                    monitorPointCategoryName : rows[0].ext$.templatename
                                });
                            }
                        },
                        onClean : function() {
                            this.setData({
                                id : this.getActivedRow().id,
                                monitorPointId : '',
                                monitorPointNo : '',
                                monitorPointName : '',
                                monitorPointCategoryId : '',
                                monitorPointCategoryName : ''
                            });
                        },*/
            editor: true
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            editor: true
            /*            type : 'choose',
                        category : 'monitorPoint',
                        editable : true,
                        cleanable : false,
                        single : true,
                        onBeforeChoose : function() {
                            var formData = Gikam.getComp('emc-project-edit-list-base-info-form').getData();
                            var testOrgId = formData.testOrgId ? formData.testOrgId : '-';
                            return {
                                clientId_SEQ : testOrgId
                            }
                        },
                        onAfterChoose : function(rows, index) {
                            if (!Gikam.isEmpty(rows)) {
                                this.setData({
                                    index : index,
                                    monitorPointId : rows[0].id,
                                    monitorPointNo : rows[0].monitorPointNo,
                                    monitorPointName : rows[0].monitorPointName,
                                    monitorPointCategoryId : rows[0].monitorPointCategoryId,
                                    monitorPointCategoryName : rows[0].ext$.templatename
                                });
                            }
                        },
                        onClean : function() {
                            this.setData({
                                id : this.getActivedRow().id,
                                monitorPointId : '',
                                monitorPointNo : '',
                                monitorPointName : '',
                                monitorPointCategoryId : '',
                                monitorPointCategoryName : ''
                            });
                        },
                        editor : true*/
        }, {
            field: 'cycle',
            title: 'T_EMC_PROJ_SCHEME.CYCLE',
            type: 'number',
            range: '[1,24]',
            isInteger: true,
            width: 80,
            // hasArrow : true,
            editor: true
        }, {
            field: 'frequency',
            title: 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range: '[1,24]',
            isInteger: true,
            width: 80,
            type: 'number',
            // hasArrow : true,
            editor: true
        }, {
            field: 'sampleOrgId',
            title: 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor: true,
            type: 'select',
            search: true,
            category: 'org',
            param: {
                sampleDepartment_SEQ: '1',
                parentId_SB: workspace.user.ext$.parentorgid
            },
            // firstBlank: false,
        }, {
            field: 'remark',
            title: 'T_EMC_PROJ_SCHEME.REMARK',
            validators: ['strLength[0,512]'],
            type: 'richText',
            editor: true
        }, {
            field: 'referFolder',
            title: '引用点位',
            editor: true,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getOrderEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'sortNum',
            title: 'T_EMC_PROJ_ORDER.SORTNUM',
        }, {
            field: 'sampleNum',
            title: 'T_EMC_PROJ_ORDER.SAMPLENUM',
            range: '[1,100]',
            isInteger: true,
            width: 85,
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field: 'ext$.testclass',
            title: '测试类别',
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'testClass',
            }
        }, {
            field: 'ext$.testmethodtype',
            title: 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type: 'select',
            search: false,
            category: 'sys',
            param: {
                codeCategoryId: 'testMethodType',
            }
        }, {
            field: 'schemeName',
            title: 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field: 'ext$.methodname',
            title: 'T_EMC_PROJ_ORDER.METHODID',
            type: 'choose',
            category: 'monitorSchemeMethod',
            cleanable: false,
            single: true,
            onBeforeChoose: function (index) {
                var activedRow = Gikam.getComp('emc-proj-order-edit-list-grid').getActivedRow();
                var schemeActivedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                return {
                    monitorTmplId_SEQ: schemeActivedRow.monitorTmplId,
                    schemeName_SEQ: activedRow.schemeName,
                    testId_SEQ: activedRow.testId,
                    itemGroup_SEQ: activedRow.itemGroup
                }
            },
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        methodId: rows[0].methodId ? rows[0].methodId : rows[0].ext$.methodid,
                        ext$: {
                            testmethodbasis: rows[0].ext$.testmethodbasis,
                            methodname: rows[0].ext$.methodname
                        }
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    methodId: '',
                    ext$: {
                        testmethodbasis: '',
                        methodname: ''
                    }
                })
            },
            editor: true
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width: 130
        }, {
            field: 'taskLowLimit',
            title : '标准下限',
            editor : true,
        },{
            field: 'taskHighLimit',
            title : '标准上限',
            editor : true,
        },{
            field : 'specifyMethod',
            title : '客户指定方法',
            editor : true,
            type : 'simpleCheckbox'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field: 'ext$.stdname',
            title: 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field: 'ext$.indexname',
            title: 'T_EMC_PROJ_ORDER.INDEXNAME',
            type: 'choose',
            category: 'indexCategory',
            single: true,
            cleanable: false,
            onBeforeChoose: function () {
                return {
                    proj_order_assigned: '1'
                }
            },
            onAfterChoose: function (rows, index) {
                if (rows) {
                    this.setData({
                        index: index,
                        indexId: rows[0].id,
                        indexCategoryId: rows[0].indexCategoryId,
                        ext$: {
                            stdname: rows[0].ext$.stdname,
                            indexname: rows[0].indexName,
                            index1: rows[0].index1,
                            index2: rows[0].index2,
                            index3: rows[0].index3
                        }
                    })
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    indexId: '',
                    indexCategoryId: '',
                    ext$: {
                        stdname: '',
                        indexname: '',
                        index1: '',
                        index2: '',
                        index3: ''
                    }
                })
            },
            editor: true
        }, {
            field: 'indexId',
            title: 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field: 'ext$.index1',
            title: 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field: 'ext$.index2',
            title: 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field: 'ext$.index3',
            title: 'T_EMC_PROJ_ORDER.INDEXID3'
        }/*, {
            field : 'sampleFee',
            title : 'T_EMC_PROJ_ORDER.SAMPLEFEE',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE',
            editor : true,
            validators : [ 'number' ]
        }*/, {
            field: 'subPackageNo',
            title: 'T_EMC_PROJ_ORDER.SUBPACKAGENO'
        }, {
            field: 'subcontractorId',
            title: 'T_EMC_PROJ_ORDER.SUBCONTRACTOR',
            type: 'select',
            search: true,
            category: 'subcontractor',
            editor: true,
            onChange: function (field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-proj-order-edit-list-grid');
                var activedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [{
                        id: grid.getData()[rowIndex].id,
                        projSchemeId: grid.getData()[rowIndex].projSchemeId,
                        subcontractorId: value
                    }];
                    Gikam.put('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/subcontractor-edit', Gikam.getJsonWrapper({}, ['emcProjOrderServiceImpl', selections])).done(function () {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                } else {
                    var selections = [{
                        id: grid.getData()[rowIndex].id,
                        projSchemeId: grid.getData()[rowIndex].projSchemeId,
                        subcontractorId: '',
                        subPackageNo: ''
                    }];
                    Gikam.put(emcProject.baseUrl + '/' + activedRow.projId, Gikam.getJsonWrapper({}, ['emcProjOrderServiceImpl', selections])).done(function () {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                }
            }
        }, {
            field: 'ext$.subcontract1',
            title: '采样前分包',
            type: 'simpleCheckbox',
            editor: true
        }, {
            field: 'ext$.subcontract2',
            title: '采样后分包',
            type: 'simpleCheckbox',
            editor: true
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        }/*, {
            field : 'dataSharing',
            title : 'T_EMC_PROJ_ORDER.DATASHARING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        },*/];
    },

    getProjectSubpackageListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'status',
            title: 'T_EMC_SUBPACKAGE.STATUS',
            formatter: function (index, x, row) {
                if (row.status === '1') {
                    return '已委托';
                } else if (row.status === '0') {
                    return '未委托';
                } else {
                    return '已完成';
                }
            }
        }, {
            field: 'subpackageNo',
            title: 'T_EMC_SUBPACKAGE.SUBPACKAGENO',
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_SUBPACKAGE.PROJNAME',
        }, {
            field: 'projNo',
            title: 'T_EMC_SUBPACKAGE.PROJNO',
        }, {
            field: 'contractNo',
            title: 'T_EMC_SUBPACKAGE.CONTRACTNO',
        }, {
            field: 'ext$.bizcategoryid',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
        }, {
            field: 'createdByName',
            title: 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            width: 80,
        }, {
            field: 'createdTime',
            title: 'T_EMC_SUBPACKAGE.CREATEDTIME',
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'sampling',
            title: 'T_EMC_SUBPACKAGE.SAMPLING',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }];
    },

    getProjectSubpackageDetailListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_SUBPACKAGE.PROJNAME',
        }, {
            field: 'projNo',
            title: 'T_EMC_SUBPACKAGE.PROJNO',
        }, {
            field: 'ext$.bizcategoryid',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
        }, {
            field: 'createdByName',
            title: 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            width: 80,
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field: 'sampling',
            title: 'T_EMC_SUBPACKAGE.SAMPLING',
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'contact',
            title: 'T_EMC_SUBPACKAGE.CONTACT',
            editor: true
        }, {
            field: 'contactPhone',
            title: 'T_EMC_SUBPACKAGE.CONTACTPHONE',
            editor: true
        }, {
            field: 'contactEmail',
            title: 'T_EMC_SUBPACKAGE.CONTACTEMAIL',
            editor: true
        }, {
            field: 'subpackageTotalPrice',
            title: 'T_EMC_SUBPACKAGE.SUBPACKAGETOTALPRICE',
            editor: true
        }, {
            field: 'completeDate',
            title: 'T_EMC_SUBPACKAGE.COMPLETEDATE',
            type: 'date',
            editor: true
        }, {
            field: 'siteContact',
            title: 'T_EMC_SUBPACKAGE.SITECONTACT',
            editor: true
        }, {
            field: 'sitePhone',
            title: 'T_EMC_SUBPACKAGE.SITEPHONE',
            editor: true
        }];
    },

    getSubpackageOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        }];
    },

    getSubpackageOrderColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER.ORDERCATEGORY',
            width: 120,
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO',
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,100]'],
            editor: true
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO2',
            validators: ['strLength[0,100]'],
            editor: true,
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCNAME',
        }, {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
            editor: true,
            width: 100,
        }, {
            field: 'orderNum',
            title: 'T_EMC_ORDER.ORDERNUM',
            validators: ['integer'],
            editor: true,
            width: 80,
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,540]'],
            editor: true
        }];
    },

    getSubpackageFolderColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.monitortmplname',
            title: 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLID',
        }, {
            field: 'folderNo',
            title: 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field: 'monitorPointName',
            title: 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            editor: true
        }, {
            field: 'cycle',
            title: 'T_EMC_PROJ_SCHEME.CYCLE',
            range: '[1,24]',
            isInteger: true,
            width: 120,
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'frequency',
            title: 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range: '[1,24]',
            isInteger: true,
            width: 120,
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'remark',
            title: 'T_EMC_PROJ_SCHEME.REMARK',
            validators: ['strLength[0,512]'],
            type: 'richText',
            editor: true
        }];
    },

    getSubpackageSampleOrderTaskColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'sampleNum',
            title: 'T_EMC_PROJ_ORDER.SAMPLENUM',
            range: '[1,100]',
            isInteger: true,
            width: 85,
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'testName',
            title: 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width: 130
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        },];
    },

    //选择监测方案的测试
    getProjOrderChooseColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.folderno',
            title: 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field: 'ext$.testname',
            title: 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field: 'ext$.testmethodbasis',
            title: 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width: 130
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        },];
    },

    //选择监测方案的测试
    getOrderItemGroupChooseColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.orderno',
            title: 'T_EMC_ORDER.ORDERNO',
        }, {
            field: 'testName',
            title: 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field: 'itemGroup',
            title: 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field: 'testMethodBasis',
            title: 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width: 130
        }, {
            field: 'ext$.remark',
            title: '备注',
            editor: true
        },];
    },

    getSubpackageBaseInfoFormFields: function () {
        return [{
            field: 'ext$.projname',
            title: 'T_EMC_SUBPACKAGE.PROJNAME',
            readonly: true
        }, {
            field: 'projNo',
            title: 'T_EMC_SUBPACKAGE.PROJNO',
            readonly: true
        }, {
            field: 'ext$.bizcategoryid',
            title: 'T_EMC_PROJECT.BIZCATEGORY',
            type: 'select',
            search: true,
            category: 'bizCategory',
            firstBlank: false,
            readonly: true
        }, {
            field: 'createdByName',
            title: 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            readonly: true
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG',
            readonly: true
        }, {
            field: 'entrustOrgName',
            title: 'T_EMC_PROJECT.ENTRUSTORG',
            readonly: true
        }, {
            field: 'sampling',
            title: 'T_EMC_SUBPACKAGE.SAMPLING',
            type: 'select',
            search: true,
            category: 'sys',
            readonly: true,
            param: {
                codeCategoryId: 'yesorno',
            },
        }, {
            field: 'contact',
            title: 'T_EMC_SUBPACKAGE.CONTACT',
            editor: true
        }, {
            field: 'contactPhone',
            title: 'T_EMC_SUBPACKAGE.CONTACTPHONE',
            editor: true
        }, {
            field: 'contactEmail',
            title: 'T_EMC_SUBPACKAGE.CONTACTEMAIL',
            editor: true
        }, {
            field: 'subpackageTotalPrice',
            title: 'T_EMC_SUBPACKAGE.SUBPACKAGETOTALPRICE',
            editor: true
        }, {
            field: 'completeDate',
            title: 'T_EMC_SUBPACKAGE.COMPLETEDATE',
            type: 'date',
            editor: true,
        }, {
            field: 'siteContact',
            title: 'T_EMC_SUBPACKAGE.SITECONTACT',
            editor: true
        }, {
            field: 'sitePhone',
            title: 'T_EMC_SUBPACKAGE.SITEPHONE',
            editor: true
        }, {
            field: 'subcontractorId',
            title: 'T_EMC_ORDER_ITEM_GROUP.SUBCONTRACTOR',
            type: 'select',
            search: true,
            category: 'subcontractor',
            editor: true,
        }];
    },

    getIndexShowAllField: function () {
        return [{
            title: '未选择标准',
            field: 'showFlag',
            type: 'simpleCheckbox',
            value: '0',
            refreshGrid : false,
            onChange: function (field, value, index) {
                var grid = Gikam.getComp('emc-update-index-order-item-group-grid');

                if (value === '1') {
                    grid.refresh({
                        requestData: {
                            indexId_ISNULL: '1',
                        }
                    }).done(function () {
                    });
                } else {
                    grid.refresh({
                        requestData: {
                            indexId_ISNULL: '',
                        }
                    }).done(function () {
                    });
                }
            },
        }]
    },


    getContractColumns: function () {
        return [{
            field: 'projNo',
            title: '任务单号',
        },  {
            field: 'entrustOrgName',
            title: '委托单位',
        },  {
            field: 'testOrgName',
            title: '受检单位',
        },{
            field: 'ext$.price',
            title: '决算金额',
        }];
    },

    getContractFormFields: function () {
        return [{
            field: 'contractPrice',
            title: '合同金额',
        },{
            field: 'totalPrice',
            title: '决算金额合计',
        },];
    },

    workflow: {
        dbTable: 'T_EMC_PROJECT',
        columns: [{
            field: 'id',
            title: 'T_EMC_PROJECT.ID'
        }]
    }
});
