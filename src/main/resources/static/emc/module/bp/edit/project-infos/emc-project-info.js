var emcProjectInfo = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',

    workflow : {
        dbTable : 'T_EMC_PROJECT',
        columns : [ {
            field : 'id',
            title : 'T_EMC_PROJECT.ID'
        } ]
    },

    // 项目列表字段
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
            width : 120,
            formatter : function(i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            width : 110
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
        }, Gikam.status.getBpmnColumn(emcProjectInfo.workflow.dbTable), {
            field : 'projNode',
            title : 'T_EMC_PROJECT.PROJNODE',
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

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO'
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
        }, Gikam.status.getBpmnColumn(emcProjectInfo.workflow.dbTable), ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            validators : [ 'strLength[0,500]' ],
            preInsert : true,
        }, {
            field : 'taskDate',
            title : 'T_EMC_PROJECT.TASKDATE',
            type : 'date',
            preInsert : true
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            preInsert : true
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
            field : 'ext$.biddername',
            title : 'T_EMC_PROJECT.BIDDERNAME',
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
                    Gikam.getJsonSync(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/biz-categories/' + value, Gikam.getJsonWrapper({}, [ '', [] ])).done(function(row) {
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
            field : 'sendUsername',
            title : 'T_EMC_PROJECT.SENDUSERNAME',
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
                        sendUsername : rows[0].userName,
                        ext$ : {
                            sendUserId : rows[0].id,
                        },
                    });
                }
            },
            onClean : function() {
                this.setData({
                    sendUsername : '',
                    ext$ : {
                        sendUserId : '',
                    },
                });
            },
        }, {
            field : 'sendDate',
            title : 'T_EMC_PROJECT.SENDDATE',
            type : 'date'
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

    getBaseInfoFormFields1 : function() {
        return [ {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
            type : 'comboBox',
            category : 'bizCategoryProject',
            validators : [ 'notEmpty', 'strLength[0,500]' ],
            editable : true
        }, {
            field : 'ext$.contractno',
            title : 'T_EMC_PROJECT.CONTRACTNO',
            type : 'choose',
            category : 'contract',
            single : false,
            validators : [ 'notEmpty' ],
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    if (rows.length > 1) {
                        var entrustOrgFlag = rows.some(function (row) {
                            return row.entrustOrg !== rows[0].entrustOrg;
                        });
                        if (entrustOrgFlag) {
                            Gikam.alert('EMC.MODULE.MDM.CONTRACTS.TIP.INCONSISTENT_ENTRUST_ORG');
                            return;
                        }
                    }
                    this.setData({
                        ext$ : {
                            contractid : rows.map(function (row) {
                                return row.id
                            }).join(),
                            contractno : rows.map(function (row) {
                                return row.contractNo
                            }).join(),
                        },
                        entrustOrgId : rows[0].entrustOrg,
                        entrustOrgName : rows[0].ext$.entrustorgname,
                        entrustOrgAddress : rows[0].ext$.entrustorgaddress,
                        entrustOrgContact : rows[0].ext$.entrustor,
                        entrustOrgPhone : rows[0].ext$.entrustorgphone,
                        testOrgId : rows[0].entrustOrg,
                        testOrgName : rows[0].ext$.entrustorgname,
                        testOrgAddress : rows[0].ext$.entrustorgaddress,
                        testOrgContact : rows[0].ext$.entrustor,
                        testOrgPhone : rows[0].ext$.entrustorgphone
                    });
                }
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                        contractid : '',
                        contractno : '',
                    },
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
            validators : [ 'notEmpty' ],
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
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
            field : 'testOrgAddress',
            title : 'T_EMC_PROJECT.TESTORGADDRESS',
            validators : [ 'notEmpty', 'strLength[0,90]' ],
        }, {
            field : 'testOrgContact',
            title : 'T_EMC_PROJECT.TESTORGCONTACT',
            type : 'choose',
            category : 'clientContact',
            validators : [ 'notEmpty' ],
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
            validators : [ 'notEmpty', ]
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
            category : 'emp',
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
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        }, {
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
            validators : [ 'notEmpty', 'strLength[0,500]' ],
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
            validators : [ 'strLength[0,90]' ],
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
            category : 'emp',
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
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        }, {
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
            validators : [ 'notEmpty', 'strLength[0,500]' ],
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
            category : 'emp',
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
            field : 'description',
            title : 'T_EMC_PROJECT.DESCRIPTION',
            validators : [ 'notEmpty', 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        }, {
            field : 'remark',
            title : 'T_EMC_PROJECT.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'textarea',
            colspan : 3,
        } ];
    },

    // 样品信息字段
    getOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO'
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME',
            validators : [ 'strLength[0,120]' ],
            editor : true
        }, {
            field : 'parentOrderNo',
            title : 'T_EMC_ORDER.PARENTORDERNO',
            validators : [ 'strLength[0,10]' ],
            editor : true
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            type : 'date',
            editor : true
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            type : 'time',
            editor : true
        }, {
            field : 'orderNum',
            title : 'T_EMC_ORDER.ORDERNUM',
            validators : [ 'integer' ],
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK',
            validators : [ 'strLength[0,170]' ],
            editor : true
        }, ];
    },

    // 分析项组选择列表字段
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

    // 测试任务字段
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
            category : 'org',
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.LABELNO'
        }, {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
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
            title : 'T_EMC_ORDER_TASK.SUBPACKAGENO'
        }, ];
    },

    // 样品批更新字段
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
        } ];
    },

    // 容器组更新字段
    getOrderContainerGroupUpdateFields : function() {
        return [ {
            field : 'containerGroup',
            title : 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, ];
    },

    // 样品分析项组字段
    getOrderItemGroupColumns : function() {
        return [ {
            checkbox : true
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
            title : 'T_EMC_ORDER_ITEM_GROUP.CONTAINERGROUP'
        }, {
            field : 'containerCategoryCode',
            title : 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYCODE',
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
        } ];
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

    // 监测方案列表字段
    getSchemeEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_PROJ_SCHEME.FOLDERID',
            width : 130
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINTID',
            readonly : true,
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_PROJ_SCHEME.MONITORPOINT',
            editor : true,
        }, {
            field : 'cycle',
            title : 'T_EMC_PROJ_SCHEME.CYCLE',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            readonly : true,
            width : 80
        }, {
            field : 'frequency',
            title : 'T_EMC_PROJ_SCHEME.FREQUENCY',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            readonly : true,
            width : 80
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
            readonly : true,
            type : 'choose',
            category : 'org',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        sampleOrgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    sampleOrgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            }
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,170]' ],
            type : 'richText',
            readonly : true,
        } ];
    },

    // 监测方案-样品列表字段
    getOrderEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'sampleNum',
            title : 'T_EMC_PROJ_ORDER.SAMPLENUM',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true,
            width : 100
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
            single : true,
            onBeforeChoose : function(index) {
                var activedRow = Gikam.getComp('emc-proj-order-edit-list-grid').getActivedRow();
                var schemeActivedRow = Gikam.getComp('emc-proj-scheme-edit-list-grid').getActivedRow();
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
                        methodId : rows[0].methodId,
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
            single : true,
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
            validators : [ 'number' ],
            width : 100
        }, {
            field : 'analysisFee',
            title : 'T_EMC_PROJ_ORDER.ANALYSISFEE',
            editor : true,
            validators : [ 'number' ],
            width : 100
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
                var activedRow = Gikam.getComp('emc-proj-scheme-edit-list-grid').getActivedRow();
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
                    Gikam.put(emcProject.baseUrl + '/' + activedRow.projId, Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-proj-order-edit-list-grid').refresh();
                    })
                }
            }
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
            validators : [ 'strLength[0,100]' ],
            editor : true,
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
            validators : [ 'strLength[0,120]' ],
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
        },{
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            editor : true,
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        } ,{
            field : 'ext$.isblank',
            title : 'T_EMC_ORDER.ISBLANK',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }  ];
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
            editor : true
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
                var grid = Gikam.getComp('emc-project-info-edit-list-order-item-group-grid');
                var activedRow = Gikam.getComp('emc-project-info-edit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(value)) {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        orderId : grid.getData()[rowIndex].orderId,
                        subcontractorId : value
                    } ];
                    Gikam.put('/secure/emc/module/bp/bp/orders/item-groups/action/subcontractor-edit', Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-info-edit-list-order-item-group-grid').refresh();
                    })
                } else {
                    var selections = [ {
                        id : grid.getData()[rowIndex].id,
                        orderId : grid.getData()[rowIndex].orderId,
                        subcontractorId : '',
                        subPackageNo : ''
                    } ];
                    Gikam.put(emcProjectInfo.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-project-info-edit-list-order-item-group-grid').refresh();
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
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            editor : true,
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
            width : 130
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            editor : true,
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
            editor : true,
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
            editor : true,
            width : 130
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            editor : true,
            width : 130
        }, {
            field : 'river',
            title : 'T_EMC_FOLDER.RIVER',
            editor : true,
            width : 130
        }, {
            field : 'tributary',
            title : 'T_EMC_FOLDER.TRIBUTARY',
            editor : true,
            width : 130
        }, {
            field : 'remark',
            title : 'T_EMC_FOLDER.REMARK',
            editor : true
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
        }, ];
    },

    // 采样单
    getProjectSampleColumns : function() {
        return [ {
            checkbox : true
        }, {
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
            field : 'acceptedDate',
            title : 'T_EMC_SAMPLE.ACCEPTEDDATE',
            type : 'date',
            width : 150,
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_SAMPLE.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ],
        } ];
    },

    // 采样单 - 样品与测试
    getSampleTestColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
            editor : true,
            width : 130
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
            editor : true,
            width : 130
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC'
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            width : 130
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
            editor : true,
            width : 130
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
            title : 'T_EMC_ORDER.CYCLE',
            width : 80
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 80
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 80
        } ];
    },

    // 采样单 - 测试信息
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
        }, {
            field : 'storeCondition',
            title : 'T_EMC_ORDER_TASK.STORECONDITION',
            width : 100,
        }, {
            field : 'ext$.sampleweight',
            title : 'T_EMC_ORDER_TASK.SAMPLEWEIGHT',
            width : 100,
            editor : true
        } ];
    },

    // 审核记录
    getAuditRecordListColumns : function() {
        return [ {
            field : 'logDesc',
            title : 'T_EMC_AUDIT_RECORD.LOGDESC'
        }, {
            field : 'taskName',
            title : 'T_EMC_AUDIT_RECORD.TASKNAME',
            width : 120
        }, {
            field : 'nextTaskName',
            title : 'T_EMC_AUDIT_RECORD.NEXTTASKNAME',
            width : 120
        }, {
            field : 'operateCategory',
            title : 'T_EMC_AUDIT_RECORD.OPERATECATEGORY',
            width : 100
        }, {
            field : 'comments',
            title : 'T_EMC_AUDIT_RECORD.COMMENT'
        }, {
            field : 'createdByName',
            title : 'T_EMC_AUDIT_RECORD.CREATEDBYNAME',
            width : 100
        }, {
            field : 'createdTime',
            title : 'T_EMC_AUDIT_RECORD.CREATEDTIME',
            width : 100
        }, {
            field : 'auditCategory',
            title : 'T_EMC_AUDIT_RECORD.AUDITCATEGORY'
        } ];
    },

    // 报告数据
    getReportEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field : 'ext$.reportname',
            title : 'T_EMC_REPORT.REPORTCONFIGNAME',
            type : 'choose',
            category : 'report-config',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    reportConfigId_SEQ : 'ORIGINAL_RECORD_REPORT',
                };
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        reportConfigLineId : rows[0].id,
                        ext$ : {
                            reportname : rows[0].reportName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    reportConfigLineId : '',
                    ext$ : {
                        reportname : ''
                    }
                });
            },
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
            title : 'T_EMC_REPORT.REMARK'
        } ];
    },

    // 报告数据 - 点位信息列表
    getReportFolderEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 130
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
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE',
            width : 120
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME',
            width : 120
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            width : 120
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
            width : 120
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
            width : 120
        } ];
    },

    // 报告数据 - 样品信息列表
    getReportOrderEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
            width : 120
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120
        }, {
            field : 'clientOrderName',
            title : 'T_EMC_ORDER.CLIENTORDERNAME'
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC'
        }, {
            field : 'sendDate',
            title : 'T_EMC_ORDER.SENDDATE',
            width : 120
        }, {
            field : 'sendTime',
            title : 'T_EMC_ORDER.SENDTIME',
            width : 120
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        } ];
    },

    // 查询结果 - 样品信息
    getQueryResultOrderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 130
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
            width : 120
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
            width : 120
        }, {
            field : 'orderDesc',
            title : 'T_EMC_ORDER.ORDERDESC'
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            width : 120

        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
            width : 120
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
            title : 'T_EMC_ORDER.CYCLE',
            width : 80
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 80
        }, {
            field : 'sampleNum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 80
        } ];
    },

    // 查询结果 - 测试信息
    getQueryResultTestColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER_TASK.QCNAME'
        }, {
            field : 'cupNo',
            title : 'T_EMC_ORDER_TASK.CUPNO',
            width : 70,
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER_TASK.ORDERID',
            width : 120,
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_ORDER_TASK.MONITORPOINTNAME',
            width : 100,
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
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINTIME',
            type : 'time',
            width : 100,
        }, {
            field : 'ext$.labelno',
            title : 'T_EMC_ORDER_TASK.LABELNO',
        }, {
            field : 'equiptAcquNo',
            title : 'T_EMC_ORDER_TASK.EQUIPTACQUNO',
        }, {
            field : 'ext$.parentorderno',
            title : 'T_EMC_ORDER_TASK.PARENTORDERNO',
            width : 120,
        }, {
            field : 'stockBatchNo',
            title : 'T_EMC_ORDER_TASK.STOCKBATCHNO',
        } ];
    },

    // 查询结果 - 结果公共列
    getResultPublicColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            width : 120
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            width : 120,
            formatter : function(i, v, r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
            width : 120
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            width : 120
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
            width : 80
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            width : 120
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
            width : 120
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
            width : 120
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            width : 120
        } ]
    },

    // 查询结果 - 结果
    getResultColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK_RESULT.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTMETHODBASIS',
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME',
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
            field : 'highLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.HIGHLIMIT',
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
        }, {
            field : 'indexLowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXLOWLIMIT',
        }, {
            field : 'indexHighLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.INDEXHIGHLIMIT',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
        }, {
            field : 'relativeDeviation',
            title : 'T_EMC_ORDER_TASK_RESULT.RELATIVEDEVIATION',
        }, {
            field : 'average ',
            title : 'T_EMC_ORDER_TASK_RESULT.AVERAGE',
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
            validators : [ 'phone' ]
        } ];
    },

    getBatchInputFields : function() {
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
            validators : [ 'strLength[0,120]' ],
            editor : true
        } ,{
            field : 'ext$.isblank',
            title : 'T_EMC_ORDER.ISBLANK',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        }];
    },

});
