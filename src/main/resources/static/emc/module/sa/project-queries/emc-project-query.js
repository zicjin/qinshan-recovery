var emcProjectQuery = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/project-queries',
    projectUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
    sampleUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',
    reportUrl : IFM_CONTEXT + '/secure/emc/module/bp/report/reports',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
            styleFormatter : function(row) {
                if (row.status === 'cancel') {
                    return {
                        color : 'red'
                    }
                }
            },
        }, {
            field : 'erpOrderNumber',
            title : 'T_EMC_PROJECT.ERPORDERNUMBER',
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, {
            field : 'ext$.samplesource',
            title : 'T_EMC_PROJECT.SAMPLESOURCE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field : 'projCreatedTime',
            title : 'T_EMC_PROJECT.PROJCREATEDTIME',
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, Gikam.status.getBpmnColumn('T_EMC_PROJECT'), {
            field : 'projNode',
            title : 'T_EMC_PROJECT.PROJNODE',
        }, {
            field : 'ext$.biddername',
            title : 'T_EMC_PROJECT.BIDDERNAME',
        }, {
            field : 'sendUsername',
            title : 'T_EMC_PROJECT.SENDUSERNAME'
        }, {
            field : 'ext$.projectamount',
            title : 'T_EMC_PROJECT.PROJECTAMOUNT',
        }, {
            field : 'ext$.completestatus',
            title : 'T_EMC_PROJECT.COMPLETESTATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            editor : true,
            }, {
            field : 'ext$.lastestaccepteddate',
            title : 'T_EMC_PROJECT.LASTESTACCEPTEDDATE',
            type : 'date',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.completedate',
            title : 'T_EMC_REPORT.COMPLETEDATE',
            type : 'date',
            editor : true
        }, {
            field : 'ext$.deliverydate',
            title : 'T_EMC_REPORT.DELIVERYDATE',
            type : 'date',
            editor : true
        }, {
            field : 'status',
            title : 'EMC.MODULE.BP.EDIT.PROJECTINFOS.TITLE.STATUS',
            formatter : function(i, v, r) {
                if (v === 'cancel') {
                    return Gikam.propI18N('EMC.MODULE.BP.EDIT.PROJECTINFOS.TITLE.CANCEL_STATUS');
                } else {
                    return Gikam.propI18N('EMC.MODULE.BP.EDIT.PROJECTINFOS.TITLE.NORMAL_STATUS');
                }
            },
            styleFormatter : function(row) {
                if (row.status === 'cancel') {
                    return {
                        color : 'red'
                    }
                }
            },
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            readonly : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true
        }, {
            field : 'erpOrderNumber',
            title : 'T_EMC_PROJECT.ERPORDERNUMBER',
            readonly : true
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            validators : [ 'notEmpty', 'strLength[0,500]' ],
            editable : true,
            colspan : 2
        }, {
            field : 'ext$.biddername',
            title : 'T_EMC_PROJECT.BIDDERNAME',
            readonly : true
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
            readonly : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                if (!Gikam.isEmpty(value)) {
                    var _form = this;
                    Gikam.getJson(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/' + value, Gikam.getJsonWrapper({}, [ '', [] ])).done(function(row) {
                        let projectNo = _form.getData().projNo;
                        projectNo = projectNo.substring(0, projectNo.length - 1) + row.bizCategoryId;
                        _form.setData({
                            bizCategoryId : value,
                            bizCategoryCode : row.bizCategoryId,
                            projNo : projectNo
                        });
                    });
                }
            },
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'projCreatedTime',
            title : 'T_EMC_PROJECT.PROJCREATEDTIME',
            readonly : true
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
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
            //colspan : 2,
            type : 'choose',
            category : 'client',
            single : true,
            validators : [ 'notEmpty', ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var entrustOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    this.setData({
                        entrustOrgId : rows[0].id,
                        entrustOrgName : rows[0].clientName,
                        entrustOrgAddress : entrustOrgAddress,
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id : rows[0].id
                    }), Gikam.getJsonWrapper({}, [ 'emcClientServiceImpl', rows ]));

                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/'+rows[0].id+'/contacts/queries';
                    Gikam.post(url).done(function(items) {
                        if (!Gikam.isEmpty(items.rows)) {
                            _this.setData({
                                entrustOrgContact : items.rows[0].lxrName,
                                entrustOrgPhone : items.rows[0].lxrPhone,
                            });
                        }else{
                            _this.setData({
                                entrustOrgContact : '',
                                entrustOrgPhone : '',
                            });
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    entrustOrgId : '',
                    entrustOrgName : '',
                    entrustOrgAddress : '',
                    entrustOrgContact : '',
                    entrustOrgPhone : '',
                });
            },
            editable : true
        }, {
            field : 'entrustOrgAddress',
            title : 'T_EMC_PROJECT.ENTRUSTORGADDRESS',
            validators : [ 'notEmpty', ],
            colspan : 2
        }, {
            field : 'entrustOrgContact',
            title : 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
        }, {
            field : 'entrustOrgPhone',
            title : 'T_EMC_PROJECT.ENTRUSTORGPHONE',
        }, {}, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
            //colspan : 2,
            type : 'choose',
            category : 'subClient',
            single : true,
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    id_SEQ : this.getData().entrustOrgId,
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var testOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    var noMonitorPlace = Gikam.isEmpty(rows[0].ext$.monitorplace);
                    this.setData({
                        testOrgId : !noMonitorPlace ? '' : rows[0].id,
                        testOrgName : noMonitorPlace ? rows[0].clientName : rows[0].ext$.monitorplace,
                        testOrgAddress : noMonitorPlace ? testOrgAddress : '',
                    });

                    var addIndex = Gikam.isEmpty(rows[0].addIndex) ? 0 : rows[0].addIndex;
                    rows[0].addIndex = addIndex + 1;
                    Gikam.put(Gikam.printf('/secure/emc/module/mdm/bizmdm/clients/{id}', {
                        id : rows[0].id
                    }), Gikam.getJsonWrapper({}, [ 'emcClientServiceImpl', rows ]));

                    if (noMonitorPlace) {
                        var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/clients/'+rows[0].id+'/contacts/queries';
                        Gikam.post(url).done(function(items) {
                            if (!Gikam.isEmpty(items.rows)) {
                                _this.setData({
                                    testOrgContact : items.rows[0].lxrName,
                                    testOrgPhone : items.rows[0].lxrPhone,
                                });
                            }else{
                                _this.setData({
                                    testOrgContact : '',
                                    testOrgPhone : '',
                                });
                            }
                        });
                    }
                }
            },
            onClean : function() {
                this.setData({
                    testOrgId : '',
                    testOrgName : '',
                    testOrgAddress : '',
                    testOrgContact : '',
                    testOrgPhone : '',
                });
            },
            editable : true
        },  {
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'notEmpty' ],
            colspan : 2
        },  {
            field : 'testOrgContact',
            title : 'T_EMC_PROJECT.TESTORGCONTACT'
        },  {
            field : 'testOrgPhone',
            title : 'T_EMC_PROJECT.TESTORGPHONE'
        }, {}, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'qcCheck',
            title : 'T_EMC_PROJECT.QCCHECK',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.reporttype',
            title : 'T_EMC_PROJECT.REPORTTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportType'
            },
            validators : [ 'notEmpty' ],
        }, {
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            type : 'date',
        }, {
            field: 'reportAptitude',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportAptitude'
            },
            title: 'T_EMC_PROJECT.REPORTAPTITUDE'
        }, {
            field: 'reportUse',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportUse'
            },
            title: 'T_EMC_PROJECT.REPORTUSE'
        }, {
            field: 'pretreatment',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'pretreatmentRemark'
            },
            title: 'T_EMC_PROJECT.PRETREATMENT'
        }, {
            field: 'needQcReport',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            title: 'T_EMC_PROJECT.NEEDQCREPORT'
        }, {
            field: 'needSplitReport',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            title: 'T_EMC_PROJECT.NEEDSPLITREPORT'
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
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodAssigner'
            },
            title: 'T_EMC_PROJECT.TESTMETHODASSIGNER'
        }, {
            field: 'mergeReport',
            title: 'T_EMC_PROJECT.MERGEREPORT'
        }, {
            field: 'reportPostAddress',
            title: 'T_EMC_PROJECT.REPORTPOSTADDRESS'
        }, {
            field: 'addressee',
            title: 'T_EMC_PROJECT.ADDRESSEE'
        }, {
            field: 'recipientPhone',
            title: 'T_EMC_PROJECT.RECIPIENTPHONE'
        }, {
            field : 'detectPurpose',
            title : 'T_EMC_PROJECT.DETECTPURPOSE',
            validators : [ 'strLength[0,300]' ],
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,800]' ],
            type : 'textarea',
            colspan : 3,
        } ];
    },
    // 基本信息
    getBaseInfoFormFieldsFirst : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            readonly : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
            type : 'choose',
            category : 'client',
            readonly : true
        }, {
            field : 'entrustOrgAddress',
            title : 'T_EMC_PROJECT.ENTRUSTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            readonly : true
        }, {
            field : 'entrustOrgContact',
            title : 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            readonly : true
        }, {
            field : 'entrustOrgPhone',
            title : 'T_EMC_PROJECT.ENTRUSTORGPHONE',
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
            type : 'choose',
            category : 'subClient',
            readonly : true
        }, {
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            readonly : true
        }, {
            field : 'testOrgContact',
            title : 'T_EMC_PROJECT.TESTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            readonly : true
        }, {
            field : 'testOrgPhone',
            title : 'T_EMC_PROJECT.TESTORGPHONE',
            validators : [ 'notEmpty', ],
            readonly : true
        }, {
            field : 'testOrgEmail',
            title : 'T_EMC_PROJECT.TESTORGEMAIL',
            readonly : true
        }, {
            field : 'testOrgFixedPhone',
            title : 'T_EMC_PROJECT.TESTORGFIXEDPHONE',
            readonly : true
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            readonly : true
        }, {
            field : 'ext$.reporttype',
            title : 'T_EMC_PROJECT.REPORTTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportType'
            },
            validators : [ 'notEmpty' ],
            readonly : true
        }, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.qualitycontrol',
            title : 'T_EMC_PROJECT.QUALITYCONTROL',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.detect',
            title : 'T_EMC_PROJECT.DETECT',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'ext$.operationorg',
            title : 'T_EMC_PROJECT.OPERATIONORG',
            readonly : true
        }/*, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            category : 'emp',
            type : 'choose',
            readonly : true
        }, {
            field : 'projectQcUserName',
            title : 'T_EMC_PROJECT.PROJECTQCUSERNAME',
            readonly : true,
            category : 'emp',
            type : 'choose',
            validators : [ 'notEmpty' ]
        }*/, {
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            type : 'date',
            readonly : true
        }, {
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'cancelReason',
            title : 'T_EMC_PROJECT.CANCEL_REASON',
            type : 'textarea',
            colspan : 3,
            readonly : true
        } ];
    },

    getBaseInfoFormFieldsSecond : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            readonly : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
            type : 'choose',
            category : 'client',
            readonly : true
        }, {
            field : 'entrustOrgAddress',
            title : 'T_EMC_PROJECT.ENTRUSTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            readonly : true
        }, {
            field : 'entrustOrgContact',
            title : 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            readonly : true
        }, {
            field : 'entrustOrgPhone',
            title : 'T_EMC_PROJECT.ENTRUSTORGPHONE',
            validators : [ 'notEmpty', 'phone' ],
            readonly : true
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
            type : 'choose',
            category : 'subClient',
            readonly : true
        }, {
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            readonly : true
        }, {
            field : 'testOrgContact',
            title : 'T_EMC_PROJECT.TESTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            readonly : true
        }, {
            field : 'testOrgPhone',
            title : 'T_EMC_PROJECT.TESTORGPHONE',
            validators : [ 'notEmpty', 'phone' ],
            readonly : true
        }, {
            field : 'testOrgEmail',
            title : 'T_EMC_PROJECT.TESTORGEMAIL',
        }, {
            field : 'testOrgFixedPhone',
            title : 'T_EMC_PROJECT.TESTORGFIXEDPHONE',
        }, {
            field : 'entrustDate',
            title : 'T_EMC_PROJECT.ENTRUSTDATE',
            type : 'date',
            readonly : true
        }, {
            field : 'sendType',
            title : 'T_EMC_PROJECT.SENDTYPE',
            validators : [ 'strLength[0,10]' ],
            readonly : true
        }, {
            field : 'sendDate',
            title : 'T_EMC_PROJECT.SENDDATE',
            type : 'date',
            readonly : true
        }, {
            field : 'entrustMethod',
            title : 'T_EMC_PROJECT.ENTRUSTMETHOD',
            validators : [ 'strLength[0,10]' ],
            readonly : true
        }, {
            field : 'subPackageDesc',
            title : 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators : [ 'strLength[0,10]' ],
            readonly : true
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            readonly : true
        }, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.qualitycontrol',
            title : 'T_EMC_PROJECT.QUALITYCONTROL',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.detect',
            title : 'T_EMC_PROJECT.DETECT',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'qcCheck',
            title : 'T_EMC_PROJECT.QCCHECK',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }, {
            field : 'ext$.operationorg',
            title : 'T_EMC_PROJECT.OPERATIONORG',
            readonly : true
        }/*, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            readonly : true
        }*/, {
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            type : 'date',
            readonly : true
        }, {
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'cancelReason',
            title : 'T_EMC_PROJECT.CANCEL_REASON',
            type : 'textarea',
            colspan : 3,
            readonly : true
        } ];
    },

    getBaseInfoFormFieldsThrid : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            readonly : true
        }, {
            field : 'monitorOrgName',
            title : 'T_EMC_PROJECT.MONITORORGNAME',
            readonly : true
        }, {
            field : 'projAddress',
            title : 'T_EMC_PROJECT.PROJADDRESS',
            readonly : true
        }, {
            field : 'subPackageDesc',
            title : 'T_EMC_PROJECT.SUBPACKAGEDESC',
            readonly : true
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            readonly : true
        }, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.qualitycontrol',
            title : 'T_EMC_PROJECT.QUALITYCONTROL',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }/*, {
            field : 'ext$.detect',
            title : 'T_EMC_PROJECT.DETECT',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
            readonly : true
        }*/, {
            field : 'ext$.operationorg',
            title : 'T_EMC_PROJECT.OPERATIONORG',
            readonly : true
        }/*, {
            field : 'reportEditUserName',
            title : 'T_EMC_PROJECT.REPORTEDITUSERNAME',
            readonly : true
        }*/, {
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            readonly : true
        }, {
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            type : 'textarea',
            colspan : 3,
            readonly : true
        }, {
            field : 'cancelReason',
            title : 'T_EMC_PROJECT.CANCEL_REASON',
            type : 'textarea',
            colspan : 3,
            readonly : true
        } ];
    },

    // 样品
    getOrderColumns : function() {
        return [ {
            index : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            validators : [ 'integer' ],
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ],
        }, ];
    },

    // 监测方案列表字段
    getProjSchemeListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID',
            width : 130
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_PROJ_SCHEME.MONITORTMPLNAME',
            width : 130
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            type : 'number',
            width : 80
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            type : 'number',
            width : 80
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            type : 'choose',
            category : 'org',
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            type : 'richText',
        } ];
    },

    // 监测方案-样品列表字段
    getProjOrderListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM',
            type : 'number',
            width : 100
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            }
        }, {
            field : 'schemeName',
            title : 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODID',
            type : 'choose',
            category : 'monitorSchemeMethod',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'ext$.stdname',
            title : 'T_EMC_PROJ_ORDER.INDEXCATEGORYID'
        }, {
            field : 'ext$.indexname',
            title : 'T_EMC_PROJ_ORDER.INDEXNAME',
            type : 'choose',
            category : 'indexCategory',
        }, {
            field : 'indexId',
            title : 'T_EMC_PROJ_ORDER.INDEXID'
        }, {
            field : 'ext$.index1',
            title : 'T_EMC_PROJ_ORDER.INDEXID1'
        }, {
            field : 'ext$.index2',
            title : 'T_EMC_PROJ_ORDER.INDEXID2'
        }, {
            field : 'ext$.index3',
            title : 'T_EMC_PROJ_ORDER.INDEXID3'
        }, {
            field : 'sampleFee',
            title : 'T_EMC_PROJ_ORDER.SAMPLEFEE',
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE',
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_PROJ_ORDER.SUBPACKAGENO'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_PROJ_ORDER.SUBCONTRACTOR',
            type : 'select',
            category : 'subcontractor',
        }, {
            field : 'ext$.subcontract1',
            title : '采样前分包',
            type : 'simpleCheckbox',
            editor : false
        }, {
            field : 'ext$.remark',
            title : '备注',
        }, ];
    },

    // 样品登记
    getOrderRegistrationColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 120,
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'sampleAddress',
            title : 'T_EMC_ORDER.SAMPLEADDRESS',
            validators : [ 'strLength[0,40]' ],
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
            width : 100,
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ],
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 120,
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            validators : [ 'integer' ],
            width : 80,
        },{
            field : 'ext$.isblank',
            title : 'T_EMC_ORDER.ISBLANK',
            type : 'select',
            search : true,
            readonly :true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'ext$.acceptedtime',
            title : 'T_EMC_SAMPLE.ACCEPTEDTIME',
        }, {
            field : 'ext$.volume',
            title : 'T_EMC_ORDER.VOLUME',
            width : 160,
        } , {
            field : 'ext$.packing',
            title : 'T_EMC_ORDER.PACKING',
            width : 80,
        }, ];
    },

    // 样品登记 - 样品分析项组
    getOrderItemGroupColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'originalOrderNo',
            title : 'T_EMC_ORDER_TASK.ORIGINALORDERNO',
        }, {
            field : 'schemeName',
            title : 'T_EMC_ORDER_ITEM_GROUP.SCHEMENAME'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
        }, {
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            }
        }, {
            field : 'itemGroup',
            title : 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_ITEM_GROUP.METHODNAME',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_ITEM_GROUP.CONTAINERGROUP',
            width : 80
        }, {
            field : 'containerCategoryCode',
            title : 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYCODE',
            width : 90
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYNAME'
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_ITEM_GROUP.FIXATIVE'
        }, {
            field : 'analysisOrgName',
            title : 'T_EMC_ORDER_ITEM_GROUP.ANALYSISORGNAME'
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_ITEM_GROUP.QCNAME',
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_ORDER_ITEM_GROUP.SUBPACKAGENO'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_ITEM_GROUP.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
        }, {
            field : 'dataSharing',
            title : 'T_EMC_ORDER_ITEM_GROUP.DATASHARING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }, ];
    },

    // 采样 - 样品组
    getSamplePlanEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 130
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
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
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
            width : 130
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            validators : [ 'notEmpty' ],
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        sceneUserId : rows[0].id,
                        sceneUserName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sceneUserId : '',
                    sceneUserName : '',
                });
            },
            width : 130
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            validators : [ 'notEmpty' ],
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
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sampleUserId : '',
                    sampleUserName : '',
                });
            },
            width : 130
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            width : 130
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            width : 130
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            width : 130,
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            width : 130,
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK',
        } ];
    },

    // 采样 - 样品
    getSamplePrepareOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 130
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 100
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 100
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 100
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 130
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCCATEGORYNO'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, ];
    },

    getProjectSampleColumns : function() {
        return [ {
            index : true
        }, Gikam.status.getBpmnColumn('T_EMC_SAMPLE'), {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field : 'projNo',
            title : 'T_EMC_SAMPLE.PROJNO'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field : 'createdByName',
            title : 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field : 'detailInfo',
            title : 'T_EMC_SAMPLE.DETAILINFO',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcProjectQuery.editPage.loadSamplePropertyPage(\'{id}\', \'{tmplId}\', \'{monitorTmplId}\')">{name}</a>', {
                    id : row.id,
                    tmplId : row.ext$.tmplid,
                    monitorTmplId : row.monitorTmplId,
                    name : '详情信息'
                });
            }
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
        }, {
            field : 'reportConfigLineId',
            title : 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
            type : 'select',
            search : true,
            category : 'reportConfigLine',
            param : {
                reportConfigId : 'SAMPLE_REPORT'
            },
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
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO'
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
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE'
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, ];
    },

    getSampleTestColumns : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            type : 'comboBox',
            category : 'sys',
            param : {
                codeCategoryId : 'orderdesc',
            },
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
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
        } ];
    },

    getOrderTaskResultColumns : function() {
        return [ {
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
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width : 80,
        }, {
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            styleFormatter : function(row) {
                if (row.scriptFlag != '1') {
                    return {
                        backgroundColor : 'palegreen'
                    }
                }
            },
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'revisionRule',
            width : 80,
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.SHOWINREPORT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
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

    getSamplePrepareOrderContainerColumns : function() {
        return [ {
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

    getAuditRecordListColumns : function() {
        return [ {
            field : 'logDesc',
            title : 'T_EMC_AUDIT_RECORD.LOGDESC'
        }, {
            field : 'taskName',
            title : 'T_EMC_AUDIT_RECORD.TASKNAME'
        }, {
            field : 'nextTaskName',
            title : 'T_EMC_AUDIT_RECORD.NEXTTASKNAME'
        }, {
            field : 'operateCategory',
            title : 'T_EMC_AUDIT_RECORD.OPERATECATEGORY'
        }, {
            field : 'comments',
            title : 'T_EMC_AUDIT_RECORD.COMMENT'
        }, {
            field : 'createdByName',
            title : 'T_EMC_AUDIT_RECORD.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_AUDIT_RECORD.CREATEDTIME'
        }, {
            field : 'auditCategory',
            title : 'T_EMC_AUDIT_RECORD.AUDITCATEGORY'
        } ];
    },

    getReportListColumns : function() {
        return [ {
            index : true
        }, Gikam.status.getBpmnColumn('T_EMC_REPORT'), {
            field : 'ext$.projno',
            title : 'T_EMC_REPORT.PROJID'
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_REPORT.PROJNAME'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field : 'ext$.reportname',
            title : 'T_EMC_REPORT.REPORTCONFIGNAME',
            type : 'choose',
            category : 'report-config',
        }, {
            field : 'monitorInfo',
            title : 'T_EMC_REPORT.MONITORINFO',
            type : 'richText',
        }, {
            field : 'testConclusion',
            title : 'T_EMC_REPORT.TESTCONCLUSION',
            type : 'richText',
        }, {
            field : 'workCondition',
            title : 'T_EMC_REPORT.WORKCONDITION',
            type : 'richText',
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT.REMARK',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcReport.editPage.editRemark(\'{index}\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(v) ? '配置备注' : v
                });
            }
        }, {
            field : 'ext$.projusername',
            title : 'T_EMC_REPORT.PROJUSERID'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_REPORT.TESTORGNAME'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_REPORT.ENTRUSTORGNAME'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_REPORT.BIZCATEGORY'
        }, ];
    },

    getFolderEditListColumns : function() {
        return [ {
            index : true
        }, {
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

    getOrderEditListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO'
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME'
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC'
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE'
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        } ];
    },

    getOrderInfoColumns : function() {
        return [ {
            index : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date'
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date'
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
        }, {
            field : 'qcCategoryCode',
            title : 'T_EMC_ORDER.QCCATEGORYCODE',
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
        } ];
    },

    getOrderTaskInfoColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP'
        }, {
            field : 'analysisOrgName',
            title : 'T_EMC_ORDER_TASK.ANALYSISORGNAME',
        }, {
            field : 'status',
            title : 'T_EMC_ORDER_TASK.STATUS',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    if (v === 'draft') {
                        return '采样中';
                    } else if (v === 'preLogged') {
                        return '样品接收';
                    } else if (v === 'done') {
                        return '报告编制';
                    } else {
                        return '分析中';
                    }
                } else {
                    return '';
                }
            }
        }, {
            field : 'confirmUserName',
            title : 'T_EMC_ORDER_TASK.CONFIRMUSERNAME',
        }, {
            field : 'receiveUserName',
            title : 'T_EMC_ORDER_TASK.RECEIVEUSERNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_ORDER_TASK.ANALYSISUSERNAME',
        }, {
            field : 'inputUserName',
            title : 'T_EMC_ORDER_TASK.INPUTUSERNAME',
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_ORDER_TASK.EQUIPTNAME',
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
        }, {
            field : 'containerCategoryName',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME',
        }, {
            field : 'runId',
            title : 'T_EMC_ORDER_TASK.RUNID',
        }, {
            field : 'samplingSubpackage',
            title : 'T_EMC_ORDER_TASK.SAMPLINGSUBPACKAGE',
            formatter : function(i, v, r) {
                if (v === '1' && !Gikam.isEmpty(r.subcontractorId)) {
                    return '现场分包';
                } else if (!Gikam.isEmpty(r.subcontractorId)) {
                    return '登记分包';
                } else {
                    return '';
                }
            }
        } ];
    },

    getOrderTaskResultInfoColumns : function() {
        return [ {
            index : true
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            formatter : function(i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        } ];
    },

    getQueryFields : function() {
        return [ {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
        }, {
            field : 'createdByOrgName',
            title : 'T_EMC_PROJECT.CREATEDBYORGNAME',
        }, {
            field : 'registStartDate',
            title : 'T_EMC_PROJECT.REGISTSTARTDATE',
            type : 'date',
        }, {
            field : 'registEndDate',// 登记结束日期
            title : 'T_EMC_PROJECT.REGISTENDDATE',
            type : 'date',
        }, {
            field : 'samplingStartDate',
            title : 'T_EMC_PROJECT.SAMPLINGSTARTDATE',
            type : 'date'
        }, {
            field : 'samplingEndDate',
            title : 'T_EMC_PROJECT.SAMPLINGENDDATE',
            type : 'date'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, {
            field : 'taskNo',
            title : 'T_EMC_PROJECT.TASKNO',
        } ]
    },

    getProjectDirectorListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'empId',
            title : 'T_EMC_PROJECT_DIRECTOR.EMPID'
        }, {
            field : 'ext$.username',
            title : 'T_EMC_PROJECT_DIRECTOR.USERNAME'
        }, {
            field : 'phone',
            title : 'T_EMC_PROJECT_DIRECTOR.PHONE',
            editor : true,
            validators : [ 'phone' ]
        } ];
    },

    getProjectCompleteColums : function() {
        return [ {
            checkbox : true
        }, {
            field : 'reportNo',
            title : 'T_EMC_REPORT.REPORTNO',
            editor : true
        }, {
            field : 'ext$.completedate',
            title : 'T_EMC_REPORT.COMPLETEDATE',
            type : 'date',
            validators : [ 'notEmpty', ],
            editor : true
        }, {
            field : 'ext$.deliverydate',
            title : 'T_EMC_REPORT.DELIVERYDATE',
            type : 'date',
            validators : [ 'notEmpty', ],
            editor : true
        } ];
    },

    getProjectReportColumns : function () {
        return [ {
            checkbox: true
        }, {
            field: 'name',
            title: 'GIKAM.FILE.FILENAME',
            search: true,
            width: 250,
            formatter : function(i, v, r) {
                // 如果是word文档则打开编辑否则为下载文件
                var typeArray = [
                    'doc', 'docm', 'docx', 'dot', 'dotm', 'dotx',
                    'epub', 'fodt', 'htm', 'html', 'mht', 'odt',
                    'ott', 'pdf', 'rtf', 'txt', 'djvu', 'xps',
                    'csv', 'fods', 'ods', 'ots', 'xls', 'xlsm',
                    'xlsx', 'xlt', 'xltm', 'xltx','fodp', 'odp',
                    'otp', 'pot', 'potm', 'potx','pps', 'ppsm',
                    'ppsx', 'ppt', 'pptm', 'pptx'
                ];
                if (typeArray.includes(r.fileExt)) {
                    var documentParam = `fileId: '${r.id}'`;
                    return `<a href="javascript:;" onclick="Gikam.openFileByOffice({${documentParam}})">${r.name}</a>`;
                } else {
                    return `<a href="javascript:;" onclick="Gikam.printFile('${r.id}', true, null, false, true)">${r.name}</a>`;
                }
            }
        }, {
            field: 'createdByName',
            title: 'GIKAM.FILE.CREATEDBYNAME',
            search: true,
            width: 180
        }, {
            field: 'createdTime',
            title: 'GIKAM.FILE.CREATEDTIME'
        } ]
    },

    getProjectSubpackageListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_SUBPACKAGE.STATUS',
            formatter : function(index, x, row) {
                if (row.status === '1') {
                    return '已委托';
                }else if (row.status === '0'){
                    return '未委托';
                }else {
                    return '已完成';
                }
            }
        }, {
            field : 'subpackageNo',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGENO',
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_SUBPACKAGE.PROJNAME',
        }, {
            field : 'projNo',
            title : 'T_EMC_SUBPACKAGE.PROJNO',
        }, {
            field : 'contractNo',
            title : 'T_EMC_SUBPACKAGE.CONTRACTNO',
        }, {
            field : 'ext$.bizcategoryid',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
        }, {
            field : 'createdByName',
            title : 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            width : 80,
        }, {
            field : 'createdTime',
            title : 'T_EMC_SUBPACKAGE.CREATEDTIME',
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'sampling',
            title : 'T_EMC_SUBPACKAGE.SAMPLING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        } ];
    },

    getProjectSubpackageDetailListColumns : function() {
        return [ {
            checkbox : true
        } , {
            field : 'ext$.projname',
            title : 'T_EMC_SUBPACKAGE.PROJNAME',
        }, {
            field : 'projNo',
            title : 'T_EMC_SUBPACKAGE.PROJNO',
        }, {
            field : 'ext$.bizcategoryid',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
        }, {
            field : 'createdByName',
            title : 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            width : 80,
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'sampling',
            title : 'T_EMC_SUBPACKAGE.SAMPLING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'contact',
            title : 'T_EMC_SUBPACKAGE.CONTACT',
            editor:true
        } , {
            field : 'contactPhone',
            title : 'T_EMC_SUBPACKAGE.CONTACTPHONE',
            editor:true
        } , {
            field : 'contactEmail',
            title : 'T_EMC_SUBPACKAGE.CONTACTEMAIL',
            editor:true
        } , {
            field : 'subpackageTotalPrice',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGETOTALPRICE',
            editor:true
        } , {
            field : 'completeDate',
            title : 'T_EMC_SUBPACKAGE.COMPLETEDATE',
            type : 'date',
            editor:true
        }, {
            field : 'siteContact',
            title : 'T_EMC_SUBPACKAGE.SITECONTACT',
            editor:true
        } , {
            field : 'sitePhone',
            title : 'T_EMC_SUBPACKAGE.SITEPHONE',
            editor:true
        }   ];
    },

    getSubpackageOrderTaskColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
        } ];
    },

    getSubpackageOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 120,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO2',
            validators : [ 'strLength[0,100]' ],
            editor : true,
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCNAME',
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
            editor : true,
            width : 100,
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            validators : [ 'integer' ],
            editor : true,
            width : 80,
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,540]' ],
            editor : true
        } ];
    },

    getSubpackageFolderColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field: 'ext$.monitortmplname',
            title: 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLID',
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            editor : true
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            range : '[1,24]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            range : '[1,24]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,512]' ],
            type : 'richText',
            editor : true
        } ];
    },

    getSubpackageSampleOrderTaskColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM',
            range : '[1,100]',
            isInteger : true,
            width : 85,
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'testName',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width : 130
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
        },];
    },

    //选择监测方案的测试
    getProjOrderChooseColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        },{
            field : 'ext$.folderno',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width : 130
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
        },];
    },

    //选择监测方案的测试
    getOrderItemGroupChooseColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        },{
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
        }, {
            field : 'testName',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width : 130
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
        },];
    },

    getSubpackageQueryBaseInfoFormFields : function() {
        return [  {
            field : 'ext$.projname',
            title : 'T_EMC_SUBPACKAGE.PROJNAME',
            readonly : true
        }, {
            field : 'projNo',
            title : 'T_EMC_SUBPACKAGE.PROJNO',
            readonly : true
        }, {
            field : 'ext$.bizcategoryid',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
            readonly : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_SUBPACKAGE.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG',
            readonly : true
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG',
            readonly : true
        }, {
            field : 'sampling',
            title : 'T_EMC_SUBPACKAGE.SAMPLING',
            type : 'select',
            search : true,
            category : 'sys',
            readonly : true,
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'contact',
            title : 'T_EMC_SUBPACKAGE.CONTACT',
            editor:true
        } , {
            field : 'contactPhone',
            title : 'T_EMC_SUBPACKAGE.CONTACTPHONE',
            editor:true
        } , {
            field : 'contactEmail',
            title : 'T_EMC_SUBPACKAGE.CONTACTEMAIL',
            editor:true
        } , {
            field : 'subpackageTotalPrice',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGETOTALPRICE',
            editor:true
        } , {
            field : 'completeDate',
            title : 'T_EMC_SUBPACKAGE.COMPLETEDATE',
            type : 'date',
            editor:true,
        }, {
            field : 'siteContact',
            title : 'T_EMC_SUBPACKAGE.SITECONTACT',
            editor:true
        } , {
            field : 'sitePhone',
            title : 'T_EMC_SUBPACKAGE.SITEPHONE',
            editor:true
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_ITEM_GROUP.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true,
        }   ];
    },

});
