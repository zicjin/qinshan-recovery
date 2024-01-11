var emcProjectScheme = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
    schemeUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/proj-schemes',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcProjectScheme.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
            width : 100,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return "<div style='vertical-align: middle'>"+img + v+"</div>";
                }else{
                    return v;
                }
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            width : 80,
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
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
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.biddername',
            title : 'T_EMC_PROJECT.BIDDERNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
        }, {
            field : 'projCreatedTime',
            title : 'T_EMC_PROJECT.PROJCREATEDTIME',
            type : 'date',
        }, {
            field : 'ext$.projectamount',
            title : 'T_EMC_PROJECT.PROJECTAMOUNT',
        }, {
            field : 'projNode',
            title : 'T_EMC_PROJECT.PROJNODE',
        } ];
    },

    getBaseInfoFormFields1 : function() {
        return [ {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            readonly : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true
        }, {
            field : 'ext$.biddername',
            title : 'T_EMC_PROJECT.BIDDERNAME',
            readonly : true
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            validators : [ 'notEmpty', 'strLength[0,60]' ],
            editable : true,
            colspan : 2
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.ENTRUSTCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
            readonly : false,
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
            field : 'ext$.customername',
            title : 'T_EMC_PROJECT.CUSTOMERNAME',
            validators : [ 'notEmpty' ],
            colspan : 2
        }, {
            field : 'ext$.customercontact',
            title : 'T_EMC_PROJECT.CUSTOMERCONTACT',
            validators : [ 'notEmpty' ],
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
            colspan : 2,
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
            field : 'ext$.customerphone',
            title : 'T_EMC_PROJECT.CUSTOMERPHONE',
            validators : [ 'notEmpty' ],
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
            colspan : 2,
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
        }, {
            field : 'ext$.samplesource',
            title : 'T_EMC_PROJECT.SAMPLESOURCE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
            readonly : true
        }, {
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
            colspan : 2
        }, {
            field : 'ext$.projectamount',
            title : 'T_EMC_PROJECT.PROJECTAMOUNT',
        }, {
            field : 'ext$.samplinglocation',
            title : 'T_EMC_PROJECT.DRAWAREA',
            validators : [ 'notEmpty', 'strLength[0,200]' ],
            colspan : 2
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'ext$.inspectedcontact',
            title : 'T_EMC_PROJECT.INSPECTEDCONTCTS',
            validators : [ 'notEmpty' ],
        }, {
            field : 'ext$.inspectedphone',
            title : 'T_EMC_PROJECT.INSPECTEDPHONE',
            validators : [ 'notEmpty' ],
        }, {
            field : 'projCreatedTime',
            title : 'T_EMC_PROJECT.PROJCREATEDTIME',
            type : 'date',
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
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
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
            field : 'reportDeliveryWay',
            title : 'T_EMC_CONTRACT.RECEIVEREPORTWAY',
            validators : [ 'strLength[0, 30]' ],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'receiveReportWay',
            },
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportOrgId : rows[0].id,
                        ext$ : {
                            reportorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reportOrgId : '',
                    ext$ : {
                        reportorgname : '',
                    }
                });
            }
        }, {
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } , {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } ];
    },

    getBaseInfoFormFields2 : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            editable : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            single : true,
            validators : [ 'notEmpty' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$ : {
                            contractid : rows[0].id,
                            contractno : rows[0].contractNo,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        contractid : '',
                        contractno : '',
                    }
                });
            }
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
            type : 'choose',
            category : 'client',
            single : true,
            validators : [ 'notEmpty' ],
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
            validators : [ 'notEmpty', 'strLength[0,90]' ],
        }, {
            field : 'entrustOrgContact',
            title : 'T_EMC_PROJECT.ENTRUSTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    clientId_SEQ : this.getData().entrustOrgId,
                }
            },
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        entrustOrgContact : rows[0].lxrName,
                        entrustOrgPhone : rows[0].lxrPhone,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    entrustOrgContact : '',
                    entrustOrgPhone : '',
                });
            },
            editable : true
        }, {
            field : 'entrustOrgPhone',
            title : 'T_EMC_PROJECT.ENTRUSTORGPHONE',
            validators : [ 'notEmpty', 'phone' ]
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
            type : 'choose',
            category : 'subClient',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var address = rows[0].address;
                    var _this = this;
                    var testOrgAddress = Gikam.isEmpty(address) ? '' : address;
                    var noMonitorPlace = Gikam.isEmpty(rows[0].ext$.monitorplace);
                    this.setData({
                        testOrgId : !noMonitorPlace ? '' : rows[0].id,
                        testOrgName : noMonitorPlace ? rows[0].clientName : rows[0].ext$.monitorplace,
                        testOrgAddress : testOrgAddress,
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
        }, {
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'strLength[0, 90]' ],
        }, {
            field : 'testOrgContact',
            title : 'T_EMC_PROJECT.TESTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            onBeforeChoose : function() {
                return {
                    clientId_SEQ : this.getData().testOrgId,
                }
            },
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        testOrgContact : rows[0].lxrName,
                        testOrgPhone : rows[0].lxrPhone,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    testOrgContact : '',
                    testOrgPhone : '',
                });
            },
            editable : true
        }, {
            field : 'testOrgPhone',
            title : 'T_EMC_PROJECT.TESTORGPHONE',
            validators : [ 'phone' ],
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
        }, {
            field : 'sendType',
            title : 'T_EMC_PROJECT.SENDTYPE',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'sendDate',
            title : 'T_EMC_PROJECT.SENDDATE',
            type : 'date',
        }, {
            field : 'entrustMethod',
            title : 'T_EMC_PROJECT.ENTRUSTMETHOD',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'subPackageDesc',
            title : 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportOrgId : rows[0].id,
                        ext$ : {
                            reportorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reportOrgId : '',
                    ext$ : {
                        reportorgname : '',
                    }
                });
            },
        }, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
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
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
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
            field : 'qcCheck',
            title : 'T_EMC_PROJECT.QCCHECK',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'yesorno'
            },
        }, {
            field : 'ext$.operationorg',
            title : 'T_EMC_PROJECT.OPERATIONORG',
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
            field : 'ext$.reporttype',
            title : 'T_EMC_PROJECT.REPORTTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportType'
            },
            validators : [ 'notEmpty' ],
        }, {
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            type : 'date',
        }, {
            field : 'reportDeliveryWay',
            title : 'T_EMC_CONTRACT.RECEIVEREPORTWAY',
            validators : [ 'strLength[0, 30]' ],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'receiveReportWay',
            },
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.ENTRUSTCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
            readonly: true
        }, {
            field : 'ext$.samplesource',
            title : 'T_EMC_PROJECT.SAMPLESOURCE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'sampleSource',
            },
            readonly : true
        }, {
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } , {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } ];
    },

    getBaseInfoFormFields3 : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            validators : [ 'notEmpty', 'strLength[0,42]' ],
            editable : true
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            readonly : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            single : true,
            validators : [ 'notEmpty' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        ext$ : {
                            contractid : rows[0].id,
                            contractno : rows[0].contractNo,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        contractid : '',
                        contractno : '',
                    }
                });
            }
        }, {
            field : 'monitorOrgName',
            title : 'T_EMC_PROJECT.MONITORORGNAME',
            validators : [ 'notEmpty', 'strLength[0,24]' ]
        }, {
            field : 'projAddress',
            title : 'T_EMC_PROJECT.PROJADDRESS',
            validators : [ 'notEmpty' ]
        }, {
            field : 'subPackageDesc',
            title : 'T_EMC_PROJECT.SUBPACKAGEDESC',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'ext$.reportorgname',
            title : 'T_EMC_PROJECT.REPORTORG',
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        reportOrgId : rows[0].id,
                        ext$ : {
                            reportorgname : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    reportOrgId : '',
                    ext$ : {
                        reportorgname : '',
                    }
                });
            }
        }, {
            field : 'ext$.subpackage',
            title : 'T_EMC_PROJECT.SUBPACKAGE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
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
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
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
            field : 'ext$.operationorg',
            title : 'T_EMC_PROJECT.OPERATIONORG',
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
            field : 'compleDate',
            title : 'T_EMC_PROJECT.COMPLEDATE',
            type : 'date',
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
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } , {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } ];
    },

    getOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO2',
            validators : [ 'strLength[0,100]' ],
            editor : true,
        }, {
            field : 'qcCategoryCode',
            title : 'T_EMC_ORDER_TASK.QCCATEGORYCODE'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 120,
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'sampleAddress',
            title : 'T_EMC_ORDER.SAMPLEADDRESS',
            validators : [ 'strLength[0,40]' ],
            editor : true,
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
            editor : true,
            width : 100,
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
            editor : true,
            width : 100,
        }, {
            field : 'ext$.sender',
            title : 'T_EMC_ORDER.SENDER',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                    	senderId : rows[0].id,
                    	ext$ : {
                    		sender : rows[0].userName,
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    senderId : '',
                	ext$ : {
                		sender : '',
                    },
                });
            },
        }, {
            field : 'ext$.checker',
            title : 'T_EMC_ORDER.CHECKER',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                    	checkerId : rows[0].id,
                    	ext$ : {
                        	checker : rows[0].userName,
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                	checkerId : '',
                	ext$ : {
                    	checker : '',
                    },
                });
            },
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ],
            editor : true
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            width : 120,
            editor : true,
            type : 'choose',
            category : 'order',
            single : true,
            onBeforeChoose : function() {
                let row = this.getActivedRow();
                if (row.qcCategoryCode === 'N/A') {
                    Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NA_CAT_NOT_SET_PARENTNO');
                    return false;
                }

                return {
                    projId_SEQ : row.projId,
                    id_DIFFER : row.id
                }
            },
            onAfterChoose : function(rows) {
                let row = this.getActivedRow();
                var orderId = row.id;
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        parentOrderNo : rows[0].orderNo
                    });
                }
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/edit-parentOrderNo', Gikam.getJsonWrapper({
                    f : {
                    	orderId : orderId
                    }
                }, [ 'emcOrderServiceImpl', rows])).done(function() {
                    Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    parentOrderNo : ''
                });
                Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/reset-parentOrderNo', Gikam.getJsonWrapper({
                    f : {
                    	orderId : this.getActivedRow().id
                    }
                }, [])).done(function() {
                    Gikam.getComp('emc-project-scheme-edit-list-order-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            }
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            validators : [ 'integer' ],
            editor : true,
            width : 80,
        }, {
            field : 'ext$.volume',
            title : 'T_EMC_ORDER.VOLUME',
            editor : true,
            width : 160,
        } , {
            field : 'ext$.packing',
            title : 'T_EMC_ORDER.PACKING',
            editor : true,
            width : 80,
        }   ];
    },

    getItemGroupChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_MONITOR_SCHEME.ITEMGROUP'
        } ];
    },

    getOrderTaskColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'schemeName',
            title : 'T_EMC_ORDER_TASK.SCHEMENAME'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTMETHOD'
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP'
        }, {
            field : 'analysisOrgId',
            title : 'T_EMC_ORDER_TASK.ANALYSISORGID'
        }, {
            field : 'analysisOrgId',
            title : 'T_EMC_ORDER_TASK.ANALYSISORG',
            type : 'select',
            search : true,
            category : 'org-simple',
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.LABELNO'
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP',
        }, {
            field : 'ext$.containercategorycode',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYCODE',
        }, {
            field : 'ext$.containercategory',
            title : 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME'
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_TASK.FIXATIVE'
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHOD',
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.METHODDESC',
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCCATEGORY',
            type : 'choose',
            category : 'qcCategory',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-project-edit-list-order-task-grid').getActivedRow().id,
                        qcCategoryId : rows[0].id,
                        ext$ : {
                            qcname : rows[0].qcName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-project-edit-list-order-task-grid').getActivedRow().id,
                    qcCategoryId : '',
                    ext$ : {
                        qcname : '',
                    }
                });
            },
            editor : true
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO',
            width : 100,
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_ORDER_TASK.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true
        }, ];
    },

    getOrderBatchUpdateFields : function() {
        return [ {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ]
        } , {
            field : 'ext$.volume',
            title : 'T_EMC_ORDER.VOLUME',
            validators : [ 'strLength[0,120]' ],
        } , {
            field : 'ext$.packing',
            title : 'T_EMC_ORDER.PACKING',
            validators : [ 'strLength[0,120]' ],
        } , {
            field : 'ext$.sender',
            title : 'T_EMC_ORDER.SENDER',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgName_SB : '客服'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	senderId : rows[0].id,
                    	ext$ : {
                    		sender : rows[0].userName,
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    senderId : '',
                	ext$ : {
                		sender : '',
                    },
                });
            },
        }, {
            field : 'ext$.checker',
            title : 'T_EMC_ORDER.CHECKER',
            editor : true,
            editable : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgName_SB : '客服'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	checkerId : rows[0].id,
                    	ext$ : {
                    		checker : rows[0].userName,
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                	checkerId : '',
                	ext$ : {
                		checker : '',
                    },
                });
            },
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO2',
            validators : [ 'strLength[0,100]' ],
            editor : true,
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }];
    },

    getOrderContainerGroupUpdateFields : function() {
        return [ {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field : 'fixative',
            title : 'T_EMC_ORDER_ITEM_GROUP.FIXATIVE'
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_ITEM_GROUP.STORECONDITION'
        } ];
    },

    getOrderItemGroupColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'originalOrderNo',
            title : 'T_EMC_ORDER_TASK.ORIGINALORDERNO',
            editor : true
        }, {
            field : 'ext$.subpackagetype',
            title : '分包类型',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'subpackageType'
            },
        }, {
            field : 'batchNo',
            title : 'T_EMC_ORDER_ITEM_GROUP.BATCHNO',
            styleFormatter : function() {
                return {
                    backgroundColor : 'palegreen'
                }
            },
        }, {
            field : 'schemeName',
            title : 'T_EMC_ORDER_ITEM_GROUP.SCHEMENAME'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
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
            field : 'ext$.sampleweight',
            title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT'
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_ITEM_GROUP.STORECONDITION'
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
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-project-edit-list-order-item-group-grid');
                var activedRow = Gikam.getComp('emc-project-edit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        orderId : grid.getData()[rowIndex].orderId,
                        subcontractorId : value
                    } ];
                    Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
                    })
                } else {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        orderId : grid.getData()[rowIndex].orderId,
                        subcontractorId : '',
                        subPackageNo : ''
                    } ];
                    Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-edit-list-order-item-group-grid').refresh();
                    })
                }
            }
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

    getChooseListColumns : function() {
        return [ {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        } ]
    },

    getProjectQueryFormFields : function() {
        return [ {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            category : 'bizCategory',
            preInsert : false
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : false,
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        createdById : rows[0].id,
                        createdByName : rows[0].userName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    createdById : '',
                    createdByName : '',
                });
            },
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_ORDER_TASK.ORDERCATEGORY',
            type : 'select',
            search : true,
            category : 'monitorTmpl',
            preInsert : false
        }, {
            field : 'taskYear',
            title : 'T_EMC_PROJECT.YEAR',
        }, {
            field : 'taskMonth',
            title : 'T_EMC_PROJECT.MONTH',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true
        } ];
    },

    getProjectQueryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'taskDate',
            title : 'T_EMC_PROJECT.TASKDATE',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, Gikam.status.getBpmnColumn(emcProjectScheme.workflow.dbTable), ];
    },

    getMonitorPointListColumns : function() {
        return [ {
            field : 'monitorPointCategoryName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTTYPE'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINT'
        }, {
            field : 'itemGroups',
            title : 'T_EMC_PROJ_SCHEME.INDEXNAME'
        }, {
            field : 'cycle',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.CYCLE'
        }, {
            field : 'frequency',
            width : 70,
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY'
        }, {
            field : 'orgName',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID'
        } ];
    },

    getProjectOrderColumns : function() {
        return [ {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
            validators : [ 'strLength[0,10]' ],
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 140,
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            validators : [ 'strLength[0,10]' ],
            width : 140,
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
            width : 120,
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
            width : 110,
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            width : 100,
            validators : [ 'integer' ],
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ],
        }, ];
    },

    getCopyFormFields : function() {
        return [ {
            field : 'taskDate',
            title : 'T_EMC_PROJECT.COPYTOTASKDATE',
            type : 'date',
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd'),
            validators : [ 'notEmpty' ]
        }, {
            field : 'copyQty',
            title : 'T_EMC_PROJECT.COPYQTY',
            value : 1,
            validators : [ 'notEmpty', 'integer', 'numRange[1,20]' ],
            type : 'number',
            hasArrow : true,
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            validators : [ 'notEmpty' ]
        }, ];
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
            validators : ['phone']
        } ];
    },

    getSchemeEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID'
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
            field : 'sampleOrgId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor : true,
            type : 'select',
            search : true,
            category : 'org',
            param : {
                sampleDepartment_SEQ : '1',
                parentId_SB : workspace.user.ext$.parentorgid
            },
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'richText',
            editor : true
        } ];
    },

    getSchemeEditListColumns1 : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID',
            editable : true,
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
            editor : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTNAME',
            editor : true
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
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            type : 'number',
            range : '[1,24]',
            isInteger : true,
            width : 85,
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
            field : 'sampleOrgId',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            editor : true,
            type : 'select',
            search : true,
            category : 'org',
            param : {
                sampleDepartment_SEQ : '1',
                parentId_SB : workspace.user.ext$.parentorgid
            },
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'richText',
            editor : true
        } ];
    },

    getOrderEditListColumns : function() {
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
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_PROJ_ORDER.SCHEMENAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODID',
            type : 'choose',
            category : 'monitorSchemeMethod',
            cleanable : false,
            single : true,
            onBeforeChoose : function(index) {
                var activedRow = Gikam.getComp('emc-proj-order-edit-list-grid').getActivedRow();
                var schemeActivedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                return {
                    monitorTmplId_SEQ : schemeActivedRow.monitorTmplId,
                    schemeName_SEQ : activedRow.schemeName,
                    testId_SEQ : activedRow.testId,
                    itemGroup_SEQ : activedRow.itemGroup
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        methodId : rows[0].methodId ? rows[0].methodId : rows[0].ext$.methodid,
                        ext$ : {
                            testmethodbasis : rows[0].ext$.testmethodbasis,
                            methodname : rows[0].ext$.methodname
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    methodId : '',
                    ext$ : {
                        testmethodbasis : '',
                        methodname : ''
                    }
                })
            },
            editor : true
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS',
            width : 130
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
            single : true,
            cleanable : false,
            onBeforeChoose : function() {
                return {
                    proj_order_assigned : '1'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        indexId : rows[0].id,
                        indexCategoryId : rows[0].indexCategoryId,
                        ext$ : {
                            stdname : rows[0].ext$.stdname,
                            indexname : rows[0].indexName,
                            index1 : rows[0].index1,
                            index2 : rows[0].index2,
                            index3 : rows[0].index3
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    indexId : '',
                    indexCategoryId : '',
                    ext$ : {
                        stdname : '',
                        indexname : '',
                        index1 : '',
                        index2 : '',
                        index3 : ''
                    }
                })
            },
            editor : true
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
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE',
            editor : true,
            validators : [ 'number' ]
        }, {
            field : 'subPackageNo',
            title : 'T_EMC_PROJ_ORDER.SUBPACKAGENO'
        }, {
            field : 'subcontractorId',
            title : 'T_EMC_PROJ_ORDER.SUBCONTRACTOR',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-proj-order-edit-list-grid');
                var activedRow = Gikam.getComp('emc-schemeedit-audit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        projSchemeId : grid.getData()[rowIndex].projSchemeId,
                        subcontractorId : value
                    } ];
                    Gikam.put('/secure/emc/module/bp/project/proj-schemes/proj-orders/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                } else {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        projSchemeId : grid.getData()[rowIndex].projSchemeId,
                        subcontractorId : '',
                        subPackageNo : ''
                    } ];
                    Gikam.put(emcProjectScheme.baseUrl + '/' + activedRow.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                }
            }
        }, {
            field : 'dataSharing',
            title : 'T_EMC_PROJ_ORDER.DATASHARING',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        },];
    },

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
            editor : true
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 70,
            editor : true
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            editor : true,
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

    getSamplePrepareOrderContainerColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testId',
            title : 'T_EMC_ORDER_TASK.TESTID'
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field : 'ext$.subpackagetype',
            title : '分包类型',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'subpackageType'
            },
        }, {
            field : 'ext$.itemgroup',
            title : 'T_EMC_ORDER_TASK.ITEMGROUP'
        }, {
            field : 'analysisOrgName',
            title : 'T_EMC_ORDER_TASK.ORG',
            width : 200,
        }, {
            field : 'methodName',
            title : 'T_EMC_ORDER_TASK.METHODNAME'
        }, {
            field : 'testMethodBasis',
            title : 'T_EMC_ORDER_TASK.TESTMETHODBASIS',
            width : 150,
        } ];
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
                } else if (row.status === '0'){
                    return '未委托';
                } else if (row.status === '2') {
                    return '已完成';
                } else {
                    return '未提交';
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

    getSubpackageOrderTaskColumns : function() {
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

    getSubpackageSchemeBaseInfoFormFields : function() {
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
            title : 'T_EMC_SUBPACKAGE.SUBCONTRACTORID',
            type : 'select',
            search : true,
            category : 'subcontractor',
            editor : true,
        }, {
            field : 'innerSubcontractorName',
            title : 'T_EMC_SUBPACKAGE.INNERSUBCONTRACTORNAME',
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'company'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        innerSubcontractorId : rows[0].id,
                        innerSubcontractorName : rows[0].orgName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    innerSubcontractorId : '',
                    innerSubcontractorName : '',
                });
            }
        } ];
    },

    workflow : {
        dbTable : 'T_EMC_PROJECT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PROJECT.ID'
        } ]
    }
});
