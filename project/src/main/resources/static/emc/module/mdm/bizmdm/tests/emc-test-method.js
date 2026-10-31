var emcTestMethod = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field: "activatedFlag",
            title: "GIKAM.ACTIVE.TITLE.STATUS",
            type: "select",
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
            width: 80,
        }, {
            field : 'testCategoryId',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
            editor :true,
            //readonly : true
        }, {
            field : 'testClass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
            editor : true,
        },{
            field : 'testMethodType',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            },
            editor : true
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        id : Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id,
                        testId : rows[0].id,
                        ext$ : {
                            testname : rows[0].testName,
                        }
                    });

                    let testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                    testMethod['testId'] = rows[0].id;
                    Gikam.postText(Gikam.printf(emcTestMethod.baseUrl + '/action/sync-test-id', {}), Gikam.getJsonWrapper(null, [ '', [testMethod] ]));
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow().id,
                    testId : '',
                    ext$ : {
                        testname : '',
                    }
                });
                let testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                testMethod['testId'] = '';
                Gikam.postText(Gikam.printf(emcTestMethod.baseUrl + '/action/sync-test-id', {}), Gikam.getJsonWrapper(null, [ '', [testMethod] ]));
            },
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            type : 'choose',
            width : 250,
            category : 'method',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        methodId : rows[0].id,
                        ext$ : {
                            testmethodbasis : rows[0].testMethodBasis,
                            methodname : rows[0].methodName
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
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
            field : 'methodScope',
            title : '方法范围',
            validators : [ 'strLength[0,200]' ],
            editor : true,
        },{
            field : 'sampleParam',
            title : 'T_EMC_ITEM.SAMPLEPARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        },{
            field : 'multipleItemGroup',
            title : '是否有多因子',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field: 'generateLabel',
            title: '是否生成标签',
            type: 'select',
            category: 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'ext$.testmethodaliasbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODALIASBASIS',
            readonly : true
        }, /*{
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_METHOD.ANALYSISORGID',
            type : 'choose',
            category : 'org-simple',
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
                        analysisOrgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    analysisOrgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            },
            editor : true
        },*/ {
            field : 'methodParam',
            title : 'T_EMC_TEST_METHOD.METHODPARAM',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.loadMethodParamPage(\'{id}\')">{name}</a>', {
                    id : row.id,
                    name : '方法参数'
                });
            }
        }, {
            field : 'conrolledNo',
            title : 'T_EMC_TEST_METHOD.CONROLLEDNO',
            validators : [ 'strLength[0,160]' ],
            editor : true
        }, /*{
            field : 'displayMonitorBasis',
            title : 'T_EMC_TEST_METHOD.DISPLAYMONITORBASIS',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'displayMonitorBasis',
            },
            editor : true
        }, {
            field : 'authentication',
            title : 'T_EMC_TEST_METHOD.AUTHENTICATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'approval',
            title : 'T_EMC_TEST_METHOD.APPROVAL',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, */{
            field : 'analysisRecordName',
            title : 'T_EMC_TEST_METHOD.ANALYSISRECORDNAME',
            editor : true
        }, {
            field : 'analysisFee',
            title : 'T_EMC_TEST_METHOD.ANALYSISFEE',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'sampleFee',
            title : 'T_EMC_TEST_METHOD.SAMPLEFEE',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'hours',
            title : '时效(天)',
            type : 'number',
            hasArrow : true,
            editor : true
        }, /*{
            field : 'defaultFlag',
            title : 'T_EMC_TEST_METHOD.DEFAULTFLAG',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        },*/ {
            field : 'testEnglishName',
            title : 'T_EMC_TEST_METHOD.TESTENGLISHNAME',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, /*{
            field : 'inputUserName',
            title : 'T_EMC_TEST_METHOD.INPUTUSERNAME',
            editor : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var userIds = [], userNames = [];
                    rows.map(function(row) {
                        userIds.push(row.id);
                        userNames.push(row.userName);
                    });
                    this.setData({
                        index : index,
                        inputUserId : userIds.join(),
                        inputUserName : userNames.join()
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    inputUserId : '',
                    inputUserName : ''
                });
            },
        },*/ {
            field : 'recoveryRate',
            title : 'T_EMC_TEST_METHOD.RECOVERYRATE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'recoveryRate',
            },
        }, {
            field : 'ext$.beforehandlename',
            title : 'T_EMC_TEST_METHOD.BEFOREHANDLE',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.beforeHandleEdit(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '配置前处理'
                });
            }
            /*type : 'choose',
            category : 'beforeHandleEdit',
            single : true,
            onBeforeChoose : function () {
                return {
                    testMethodId : this.getActivedRow().id
                }
            },
            onAfterChoose : function (rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        beforeHandleId : rows[0].id,
                        ext$:{
                            beforehandlename : rows[0].beforeHandleName
                        }
                    });
                }
            },
            onClean : function () {
                this.setData({
                    id : this.getActivedRow().id,
                    beforeHandleId : '',
                    ext$:{
                        beforehandlename : ''
                    }
                });
            },
            editor : true*/
        },/* {
            field : 'ext$.beforehandle2',
            title : 'T_EMC_TEST_METHOD.BEFOREHANDLE2',
            validators : [ 'strLength[0, 2000]' ],
            type : 'richText',
            editor : true
        },*/ {
            field : 'gsyl',
            title : '公式',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.viewScriptFile(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '预览'
                });
            }
        },{
            field : 'remark',
            title : 'T_EMC_TEST_METHOD.REMARK',
            validators : [ 'strLength[0, 300]' ],
            type : 'richText',
            editor : true
        }, {
            field : 'acqNoSuffix',
            title : 'T_EMC_TEST_METHOD.ACQNOSUFFIX',
            validators : [ 'strLength[0, 3]' ],
            editor : true
        }, {
            field : 'reportLimitType',
            title : 'T_EMC_TEST_METHOD.REPORTLIMITTYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportLimitType',
            },
        }, {
            field : 'tube',
            title : '吸收瓶类型',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'xishouping',
            }
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'choose',
            category : 'testCategory',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testCategoryId : rows[0].id,
                        ext$ : {
                            testcategory : rows[0].testCategoryDesc,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testCategoryId : '',
                    ext$ : {
                        testcategory : '',
                    }
                })
            },
            preInsert : true
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID',
            readonly : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testId : rows[0].id,
                        ext$ : {
                            testname : rows[0].testName,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testId : '',
                    ext$ : {
                        testname : '',
                    }
                })
            },
            preInsert : true
        }, {
            field : 'testEnglishName',
            title : 'T_EMC_TEST_METHOD.TESTENGLISHNAME',
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            type : 'choose',
            category : 'method',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        methodId : rows[0].id,
                        ext$ : {
                            testmethodbasis : rows[0].testMethodBasis,
                            methodname : rows[0].methodName
                        }
                    })
                }
            },
            preInsert : true
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_METHOD.ANALYSISORGID',
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
                        analysisOrgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    analysisOrgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            },
        }, {
            field : 'methodParam',
            title : 'T_EMC_TEST_METHOD.METHODPARAM',
            validators : [ 'strLength[0,42]' ]
        }, {
            field : 'conrolledNo',
            title : 'T_EMC_TEST_METHOD.CONROLLEDNO',
            validators : [ 'strLength[0,120]' ]
        }, {
            field : 'displayMonitorBasis',
            title : 'T_EMC_TEST_METHOD.DISPLAYMONITORBASIS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'displayMonitorBasis',
            }
        }, {
            field : 'authentication',
            title : 'T_EMC_TEST_METHOD.AUTHENTICATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'approval',
            title : 'T_EMC_TEST_METHOD.APPROVAL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'analysisRecordName',
            title : 'T_EMC_TEST_METHOD.ANALYSISRECORDNAME'
        }, {
            field : 'analysisFee',
            title : 'T_EMC_TEST_METHOD.ANALYSISFEE',
            validators : [ 'number' ]
        }, {
            field : 'sampleFee',
            title : 'T_EMC_TEST_METHOD.SAMPLEFEE',
            validators : [ 'number' ]
        }, {
            field : 'hours',
            title : 'T_EMC_TEST_METHOD.HOURS',
            validators : [ 'integer' ]
        }, {
            field : 'defaultFlag',
            title : 'T_EMC_TEST_METHOD.DEFAULTFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_TEST_METHOD.ACTIVATEDFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            }
        }, {
            field : 'inputUserName',
            title : 'T_EMC_TEST_METHOD.INPUTUSERNAME',
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var userIds = [], userNames = [];
                rows.map(function(row) {
                    userIds.push(row.id);
                    userNames.push(row.userName);
                });
                this.setData({
                    inputUserId : userIds.join(),
                    inputUserName : userNames.join()
                });
            },
            onClean : function() {
                this.setData({
                    inputUserId : '',
                    inputUserName : ''
                });
            },
        } ];
    },

    getItemGroupListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getActiveColumn(), {
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP',
            validators : [ 'strLength[0,40]' ],
            editor : true,
            type : 'script',
            onDataChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    id : this.getActivedRow().id,
                    itemGroup : replacedValue
                })
            }
        },{
            field : 'sortNo',
            title : '排序',
            type:'number',
            editor : true,
        } ];
    },

    getItemResultListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'result',
            title : 'T_EMC_ITEM_RESULT.RESULT',
            validators : [ 'strLength[0,100]' ],
            editor : true
        } ];
    },

    getItemResultChooseListColumns : function() {
        return [ {
            field : 'result',
            title : 'T_EMC_ITEM_RESULT.RESULT',
        } ];
    },

    getGroupItemListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_GROUP_ITEM.ITEMNAME'
        }, {
            field : 'ext$.itemaliasname',
            title : 'T_EMC_GROUP_ITEM.ITEMALIASNAME'
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ITEM.ORDERNO',
            width : 80
        }, {
            field : 'ext$.analysiscategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
        } ];
    },

    getItemListColumns : function() {
        return [ {
            checkbox : true,
        },  {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            width : 75,
            type : 'number',
            hasArrow : true,
            editor : true,
        }, {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            type : 'link',
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            type : 'script',
            editor : true,
            onDataChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    id : this.getActivedRow().id,
                    itemAliasName : replacedValue
                })
            }
        }, {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            editor : true,
            type : 'script',
            onDataChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    id : this.getActivedRow().id,
                    originalRecordName : replacedValue
                })
            }
        }, {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true,
        }, Gikam.status.getActiveColumn(),{
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName
                    })
                }
            }
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM.LOWLIMIT',
            validators : [ 'number' ],
            editor : true
        }, /*{
            field : 'ext$.lowlimit2',
            title : 'T_EMC_ITEM.LOWLIMIT2',
            validators : [ 'number' ],
            editor : true
        },*/ {
            field : 'ext$.limitunitname',
            title : 'T_EMC_ITEM.LIMITUNITNAME',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        ext$ : {
                            limitunitname : rows[0].unitName
                        }
                    })
                }
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    })
                }
            }
        }, {
            field : 'resultCategory',
            title : 'T_EMC_ITEM.RESULTCATEGORY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'resultCategory',
            },
            editor : true
        }, {
            field : 'sampleParam',
            title : 'T_EMC_ITEM.SAMPLEPARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
            editor : true
        }, {
            field : '',
            title : 'T_EMC_ITEM.ITEMQCINDEXANDREVISION',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.editQcIndexAndRevision(\'{id}\')">{name}</a>', {
                    id : r.id,
                    name : 'QC指标和分段修约'
                });
            }
        }, {
            field : 'script',
            title : 'T_EMC_ITEM.SCRIPT',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.popScript(\'{id}\', \'{testId}\', \'{testMethodId}\', \'{methodId}\')">{name}</a>', {
                    id : r.id,
                    testId : r.ext$.testid,
                    testMethodId : r.testMethodId,
                    methodId : r.ext$.methodid,
                    name : Gikam.isEmpty(r.script) ? '点击维护' : '已维护'
                });
            },
            width : 100
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',
            editor : true
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            editor : true,
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function(index) {
                var selectedQcCategoryId = this.getData()[index].qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        index : index,
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    qcCategoryId : '',
                    qcCategoryName : ''
                });
            }
        }, {
            field : 'originalRecordDisplay',
            title : 'T_EMC_ITEM.ORIGINALRECORDDISPLAY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : false,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
            editor : true,
            onDataChange : function (field, value, rowIndex, oldValue, text) {
                if (text.indexOf('数字') !== -1) {
                    this.setData({
                        index : rowIndex,
                        standSetId : value,
                        resultCategory : 'number'
                    });
                }
                if (text.indexOf('字符') !== -1) {
                    this.setData({
                        index : rowIndex,
                        standSetId : value,
                        resultCategory : 'char'
                    });
                }
            }
        }, {
            field : 'interceptRevision',
            title : 'T_EMC_ITEM.INTERCEPTREVISION',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'interceptRevision' : back
                    })
                }
            }
        }, {
            field : 'slopeRevision',
            title : 'T_EMC_ITEM.SLOPEREVISION',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'slopeRevision' : back
                    })
                }
            }
        }, {
            field : 'ext$.detectedquantity',
            title : 'T_EMC_ITEM.DETECTEDQUANTITY',
            editor : true
        }, {
            field : 'ext$.detectedquantityunit',
            title : 'T_EMC_ITEM.DETECTEDQUANTITYUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        ext$ : {
                            detectedquantityunit : rows[0].unitName
                        }
                    })
                }
            }
        }, {
            field : 'ext$.usetype',
            title : 'T_EMC_ITEM.USETYPE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'itemUseType',
            },
            editor : true
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'ext$.ignorezero',
            title : 'T_EMC_ITEM.IGNOREZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ],
            editor : true
        },  /*{
            field : 'limitFlag',
            title : 'T_EMC_ITEM.LIMITFLAG',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'limitFlag',
            },
            editor : true
        },*/ {
            field : 'itemResult',
            title : 'T_EMC_ITEM.ITEMRESULT',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.editResult(\'{id}\')">{name}</a>', {
                    id : r.id,
                    name : '配置结果'
                });
            }
        }, {
            field : 'pnFunc',
            title : 'T_EMC_ITEM.PNFUNC',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'pnFunc',
            },
            editor : true
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        },  {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
              field : 'ext$.limitscientific',
              title : '检出项是否科学计数法',
              type : 'select',
              search : false,
              category : 'sys',
              param : {
                  codeCategoryId : 'yesorno',
              },
              editor : true
          }, {
            field : 'negativeToZero',
            title : 'T_EMC_ITEM.NEGATIVETOZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'allowableError',
            title : 'T_EMC_ITEM.ALLOWABLEERROR',
            editor : true
        }, {
            field : 'ext$.coefficient',
            title : 'T_EMC_ITEM.COEFFICIENT',
            editor : true
        }, {
            field : 'ext$.nonsnrange',
            title : 'T_EMC_ITEM.NONSNRANGE',
            editor : true
        }, {
            field : 'ext$.issendtoresult',
            title : 'T_EMC_ITEM.ISSENDTORESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'ext$.theorycontent',
            title : '标点值',
            editor : true
        }, {
            field : 'ext$.judgebasis',
            title : 'T_EMC_ITEM.JUDGEBASIS',
            editor : true
        }, {
            field : 'ext$.sendcoefficient',
            title : 'T_EMC_ITEM.SENDCOEFFICIENT',
            validators : [ 'integer' ],
            type : 'number',
            editor : true
        } ];
    },

    getPreInsertFormFields : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var testMethodId = formData.id;
        return [ {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            validators : [ 'unique[' + emcTestMethod.baseUrl + '/methods/' + testMethodId + '/action/validate-unique]', 'strLength[0,42]' ],
            preInsert : true,
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            preInsert : true
        } ];
    },

    getItemFormFields : function() {
        return [ {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            readonly : true,
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            validators : [ 'strLength[0,42]' ],
            type : 'script',
            onDataChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    itemAliasName : replacedValue
                })
            }
        }, {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true
        }, {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM',
            validators : [ 'integer' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM.LOWLIMIT',
            validators : [ 'number' ]
        }, {
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        unitName : rows[0].unitName
                    })
                }
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'revisionRule' : back
                    })
                }
            }
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            }
        }, {
            field : 'originalRecordDisplay',
            title : 'T_EMC_ITEM.ORIGINALRECORDDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : true,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
            onDataChange : function(field, value, index, oldValue, text) {
                var resultCategory = 'number';
                if (text.indexOf('字符') > -1) {
                    resultCategory = 'char';
                }
                this.setData({
                    standSetId : value,
                    resultCategory : resultCategory
                });
            }
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            validators : [ 'strLength[0,42]' ]
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ]
        }, {
            field : 'resultCategory',
            title : 'T_EMC_ITEM.RESULTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'resultCategory',
            }
        }, {
            field : 'limitFlag',
            title : 'T_EMC_ITEM.LIMITFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'limitFlag',
            }
        }, {
            field : 'interceptRevision',
            title : 'T_EMC_ITEM.INTERCEPTREVISION',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'interceptRevision' : back
                    })
                }
            }
        }, {
            field : 'slopeRevision',
            title : 'T_EMC_ITEM.SLOPEREVISION',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'slopeRevision' : back
                    })
                }
            }
        }, {
            field : 'pnFunc',
            title : 'T_EMC_ITEM.PNFUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'pnFunc',
            }
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function() {
                var selectedQcCategoryId = this.getData().qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    qcCategoryId : '',
                    qcCategoryName : ''
                });
            },
        }, {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        } ];
    },

    getItemUpdateFormFields : function() {
        var formData = Gikam.getComp('emc-test-method-update-list-grid').getActivedRow();
        var testMethodId = formData.id
        return [ {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            validators : [ 'remote[' + emcTestMethod.baseUrl + '/methods/' + testMethodId + '/action/validate-unique{itemName}]', 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            preInsert : true
        }, {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM',
            validators : [ 'integer' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM.LOWLIMIT',
            validators : [ 'number' ]
        }, {
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        unitName : rows[0].unitName
                    })
                }
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'revisionRule' : back
                    })
                }
            }
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            }
        }, {
            field : 'originalRecordDisplay',
            title : 'T_EMC_ITEM.ORIGINALRECORDDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : true,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            validators : [ 'strLength[0,42]' ]
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ]
        }, {
            field : 'resultCategory',
            title : 'T_EMC_ITEM.RESULTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'resultCategory',
            }
        }, {
            field : 'limitFlag',
            title : 'T_EMC_ITEM.LIMITFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'limitFlag',
            }
        }, {
            field : 'interceptRevision',
            title : 'T_EMC_ITEM.INTERCEPTREVISION',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'interceptRevision' : back
                    })
                }
            }
        }, {
            field : 'slopeRevision',
            title : 'T_EMC_ITEM.SLOPEREVISION',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'slopeRevision' : back
                    })
                }
            }
        }, {
            field : 'pnFunc',
            title : 'T_EMC_ITEM.PNFUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'pnFunc',
            }
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function() {
                var selectedQcCategoryId = this.getData().qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    qcCategoryId : '',
                    qcCategoryName : ''
                });
            },
        }, {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        } ];
    },

    getQcIndexListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.qcname',
            title : 'T_EMC_ITEM_QCINDEX.QCCATEGORYID',
            type : 'choose',
            category : 'qcCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        qcCategoryId : rows[0].id,
                        ext$ : {
                            qcname : rows[0].qcName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    qcCategoryId : '',
                    ext$ : {
                        qcname : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'resultLowLimit',
            title : 'T_EMC_ITEM_QCINDEX.RESULTLOWLIMIT',
            editor : true
        }, {
            field : 'resultHighLimit',
            title : 'T_EMC_ITEM_QCINDEX.RESULTHIGHLIMIT',
            editor : true
        }, {
            field : 'decisionCategory',
            title : 'T_EMC_ITEM_QCINDEX.DECISIONCATEGORY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'decisionCategory',
            },
            editor : true
        }, {
            field : 'stdLowLimit',
            title : 'T_EMC_ITEM_QCINDEX.STDLOWLIMIT',
            editor : true
        }, {
            field : 'stdHighLimit',
            title : 'T_EMC_ITEM_QCINDEX.STDHIGHLIMIT',
            editor : true
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_EMP.ORGNAME',
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            width : 190,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'company',
                    parentId_SEQ : '01'
                }
            },
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        orgId : rows[0].id,
                        ext$ : {
                            orgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    orgId : '',
                    ext$ : {
                        orgname : ''
                    }
                });
            },
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ITEM_QCINDEX.ORDERCATEGORY',
            editor : true,
            type : 'choose',
            category : 'monitorTmpl',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    orgId_SB : this.getData()[index].orgId,
                }
            },
            onAfterChoose : function(rows,index) {
                var orgId = this.getData()[index].orgId;
                if(Gikam.isEmpty(orgId)){
                    Gikam.alert("请先选择所属单位，再选择样品类型！");
                    return ;
                }
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorTmplId : rows[0].id,
                        orderCategory : rows[0].monitorTmplName
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    monitorTmplId : '',
                    orderCategory : ''
                });
            },
        } ];
    },

    getRevisionListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'lowLimitSymbol',
            title : 'T_EMC_ITEM_REVISION.LOWLIMITSYMBOL',
            type : 'select',
            search : false,
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'lowLimitSymbol',
            },
            editor : true
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM_REVISION.LOWLIMIT',
            editor : true
        }, {
            field : 'highLimitSymbol',
            title : 'T_EMC_ITEM_REVISION.HIGHLIMITSYMBOL',
            type : 'select',
            search : false,
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'highLimitSymbol',
            },
            editor : true
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM_REVISION.HIGHLIMIT',
            editor : true
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM_REVISION.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    })
                }
            },
            editor : true
        } ];
    },

    getEquiptListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_TEST_METHOD_EQUIPT.EQUIPTNAME'
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_TEST_METHOD_EQUIPT.EQUIPTID'
        }, {
            field : 'ext$.orgname',
            title : 'T_EMC_TEST_METHOD_EQUIPT.USEDDEPARTMENTID'
        }, {
            field : 'defaultFlag',
            title : 'T_EMC_TEST_METHOD_EQUIPT.DEFAULTFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_TEST_METHOD_EQUIPT.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
            editor : true,
        }, {
            field : 'ext$.specification',
            title : 'T_EMC_TEST_METHOD_EQUIPT.SPECIFICATION'
        }, {
            field : 'ext$.factoryno',
            title : 'T_EMC_TEST_METHOD_EQUIPT.FACTORYNO'
        }, {
            field : 'ext$.status',
            title : 'T_EMC_TEST_METHOD_EQUIPT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            }
        }, {
            field : 'ext$.username',
            title : 'T_EMC_TEST_METHOD_EQUIPT.MAINTAINUSERID'
        } ];
    },

    getOrderCategoryListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_TEST_ORDER_CATEGORY.MONITORTMPLID',
            type : 'choose',
            category : 'monitorTmpl',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        monitorTmplId : rows[0].id,
                        ext$ : {
                            monitortmplname : rows[0].monitorTmplName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorTmplId : '',
                    ext$ : {
                        monitortmplname : ''
                    }
                });
            },
            editor : true,
        }, {
            field : 'ext$.containercategoryname',
            title : 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERCATEGORYID',
            type : 'choose',
            category : 'containerCategory',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        containerCategoryId : rows[0].id,
                        ext$ : {
                            containercategoryname : rows[0].containerCategoryName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    containerCategoryId : '',
                    ext$ : {
                        containercategoryname : ''
                    }
                });
            },
            editor : true,
        }, {
            field : 'containerGroup',
            title : 'T_EMC_TEST_ORDER_CATEGORY.CONTAINERGROUP',
            editor : true,
        }, {
            field : 'fixative',
            title : 'T_EMC_TEST_ORDER_CATEGORY.FIXATIVE',
            editor : true,
        }, {
            field : 'sampleqty',
            title : 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEQTY',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true
        }, ];
    },

    getDefaultQcListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'qcCategoryCode',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.QCCATEGORYCODE',
        }, {
            field : 'qcName',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.QCNAME',
        }, {
            field : 'addNum',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.ADDNUM',
            type : 'number',
            hasArrow : true,
            editor : true,
        }, {
            field : 'startOrderIndex',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.STARTORDERINDEX',
            type : 'number',
            hasArrow : true,
            editor : true,
        }, {
            field : 'intervalNum',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.INTERVALNUM',
            type : 'number',
            hasArrow : true,
            editor : true,
        }, {
            field : 'checkPoint',
            title : 'T_EMC_TEST_METHOD_DEFAULT_QC.CHECKPOINT',
            type : 'number',
            hasArrow : true,
            editor : true,
            isInteger : true,
            range : '[1,9]'
        } ];
    },

    getRelatedTestMethodListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testcategoryid',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            category : 'testCategory',
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_TEST_METHOD.TESTID',
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
         field : 'ext$.replaceflag',
         title : '是否淋洗液替换方法',
         type : 'select',
         category : 'yesorno',
         editor : true,
         width : 150
     } ];
    },

    getOperateInfoFormFields : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'choose',
            category : 'testCategory',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testCategoryId : rows[0].id,
                        ext$ : {
                            testcategory : rows[0].testCategoryDesc,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testCategoryId : '',
                    ext$ : {
                        testcategory : ''
                    }
                });
            },
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testId : rows[0].id,
                        ext$ : {
                            testname : rows[0].testName,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testId : '',
                    ext$ : {
                        testname : ''
                    }
                });
            },
            editor : true
        }, ];
    },

    getCopyItemFormFields : function() {
        var formData = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
        var testMethodId = formData.id;
        return [ {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            validators : [ 'unique[' + emcTestMethod.baseUrl + '/methods/' + testMethodId + '/action/validate-unique]', 'strLength[0,42]', 'notEmpty' ],
            preInsert : true
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            validators : [ 'strLength[0,42]', 'notEmpty' ],
            preInsert : true
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function() {
                var selectedQcCategoryId = this.getData().qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            }
        } ];
    },

    getItemOperateInfoFormFields : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            editor : true,
        },{
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            validators : [ 'strLength[0,42]' ],
            preInsert : true
        }, {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            editor : true,
            type : 'script',
            onChange : function(field, value, index) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    originalRecordName : replacedValue
                })
            }
        },{
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        unitName : rows[0].unitName
                    })
                }
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back) {
                if (back) {
                    this.setData({
                        'revisionRule' : back
                    })
                }
            }
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            }
        }, {
            field : 'originalRecordDisplay',
            title : 'T_EMC_ITEM.ORIGINALRECORDDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function() {
                var selectedQcCategoryId = this.getData().qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            }
        }, {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'negativeToZero',
            title : 'T_EMC_ITEM.NEGATIVETOZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'allowableError',
            title : 'T_EMC_ITEM.ALLOWABLEERROR',
            editor : true
        }, {
            field : 'ext$.coefficient',
            title : 'T_EMC_ITEM.COEFFICIENT',
            editor : true
        }, {
            field : 'ext$.nonsnrange',
            title : 'T_EMC_ITEM.NONSNRANGE',
            editor : true
        }, {
            field : 'ext$.issendtoresult',
            title : 'T_EMC_ITEM.ISSENDTORESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'ext$.theorycontent',
            title : '标点值',
            editor : true
        }, {
            field : 'ext$.judgebasis',
            title : 'T_EMC_ITEM.JUDGEBASIS',
            editor : true
        }, {
            field : 'ext$.sendcoefficient',
            title : 'T_EMC_ITEM.SENDCOEFFICIENT',
            validators : [ 'integer' ],
            type : 'number',
            editor : true
        }, {
            field : 'ext$.detectedquantity',
            title : 'T_EMC_ITEM.DETECTEDQUANTITY',
            editor : true
        }, {
            field : 'ext$.detectedquantityunit',
            title : 'T_EMC_ITEM.DETECTEDQUANTITYUNIT',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onBeforeChoose : function() {
                return {
                    filterOpen : true
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        ext$ : {
                            detectedquantityunit : rows[0].unitName
                        }
                    })
                }
            }
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY'
        }, {
            field : 'testClass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME'
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID'
        } ];
    },

    getItemChooseListColumns : function() {
        return [ {
            field : 'ext$.testid',
            title : 'T_EMC_ITEM.TESTID'
        }, {
            field : 'ext$.itemtestcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
        }, {
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        },{
            field : 'ext$.testname',
            title : 'T_EMC_ITEM.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ITEM.METHODNAME'
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
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME'
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
        } ];
    },

    getItemGroupChooseListColumns : function() {
        return [ {
            field : 'ext$.testcategoryid',
            title : 'T_EMC_DATA_CORRELATION_ITEM.TESTCATEGORYID',
            width : 130,
            type : 'select',
            search : true,
            category : 'testCategory'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            width : 130,
        },{
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP',
            width : 130,
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
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            width : 130,
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            width : 170,
        }, ];
    },

    getTestEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_TEST.ID'
        }, {
            field : 'testName',
            title : 'T_EMC_TEST.TESTNAME',
            validators : [ 'unique[' + emcTestMethod.baseUrl + '/tests/action/validate-unique]', 'notEmpty','strLength[0,400]' ],
            editor : true,
            width : 425,
            preInsert : true,
        }, {
            field: 'testFlag',
            title: '是否报告质控统计',
            type: 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }];
    },

    getTestChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_TEST.ID'
        }, {
            field : 'testName',
            title : 'T_EMC_TEST.TESTNAME'
        }, ];
    },

    getItemCopeToChooseListColumns : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY'
        }, {
            field : 'testClass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS'
        } ];
    },

    getScriptFormFields : function() {
        return [ {
            field : 'scriptTestName',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-item-script-detail-base-info-form').setData({
                        scriptTestId : rows[0].id,
                        scriptTestName : rows[0].testName,
                        scriptMethodId : null,
                        scriptMethodName : null,
                        scriptItemName : null
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-item-script-detail-base-info-form').setData({
                    scriptTestId : '',
                    scriptTestName : '',
                    scriptMethodId : null,
                    scriptMethodName : null,
                    scriptItemName : null
                });
            },
        }, {
            field : 'scriptMethodName',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            type : 'choose',
            category : 'testMethod',
            onBeforeChoose : function() {
                var formData = Gikam.getComp('emc-item-script-detail-base-info-form').getData();
                return {
                    testId_SEQ : formData.scriptTestId
                }
            },
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-item-script-detail-base-info-form').setData({
                        scriptTestMethodId : rows[0].id,
                        scriptMethodId : rows[0].methodId,
                        scriptMethodName : rows[0].ext$.methodname,
                        scriptItemName : null
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-item-script-detail-base-info-form').setData({
                    scriptTestMethodId : '',
                    scriptMethodId : '',
                    scriptMethodName : '',
                    scriptItemName : null
                });
            },
        }, {
            field : 'scriptItemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            type : 'choose',
            category : 'testItem',
            single : true,
            onBeforeChoose : function() {
                var formData = Gikam.getComp('emc-item-script-detail-base-info-form').getData();
                return {
                    testMethodId_SEQ : formData.scriptTestMethodId,
                }
            },
            onAfterChoose : function(rows) {
                if (rows) {
                    Gikam.getComp('emc-item-script-detail-base-info-form').setData({
                        scriptItemName : rows[0].itemName,
                    });
                }
            }
        }, {
            field : 'func',
            title : 'T_EMC_ITEM.FUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'calfunc'
            },
            onDataChange : function() {
                var formData = Gikam.getComp('emc-item-script-detail-base-info-form').getData();

                var script = formData.func + "(" + (Gikam.isEmpty(formData.scriptItemName) ? "" : formData.scriptItemName) + "); ";

                if (Gikam.isEmpty(formData.script)) {
                    script = script;
                } else {
                    script = formData.script + '\r' + script;
                }
                this.setData({
                    'script' : script
                });
            }
        }, {
            field : 'script',
            title : 'T_EMC_ITEM.SCRIPT',
            type : 'textarea',
            rows : 10,
            height : 300,
            onDataChange : function(field, value) {
                this.setData({
                    'script' : value,
                    'scriptFlag' : Gikam.isEmpty(value) ? '0' : '1',
                });
            }
        } ]
    },

    getTcTestMethodChooseListColumns : function() {
        return [ {
            field : 'testCategoryId',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
            editor : true,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME'
        }, {
            field : 'testId',
            title : 'T_EMC_TEST_METHOD.TESTID'
        } ];
    },

    getCheckMethodEditChooseListColumns : function() {
        return [ {
            field : 'methodId',
            title : 'T_EMC_ITEM.METHODID'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
        } ];
    },

    getItemUpdateListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            editor : true
        }, Gikam.status.getActiveColumn(), {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ITEM.LOWLIMIT',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName
                    })
                }
            }
        }, {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    })
                }
            }
        }, {
            field : 'script',
            title : 'T_EMC_ITEM.SCRIPT',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.updatePage.popScript(\'{index}\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.script) ? '点击维护' : '已维护'
                });
            },
            width : 100
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
            editor : true
        }, {
            field : 'originalRecordDisplay',
            title : 'T_EMC_ITEM.ORIGINALRECORDDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : true,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
            editor : true
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',
            editor : true
        }, {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            editor : true
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'resultCategory',
            title : 'T_EMC_ITEM.RESULTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'resultCategory',
            },
            editor : true
        }, {
            field : 'limitFlag',
            title : 'T_EMC_ITEM.LIMITFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'limitFlag',
            },
            editor : true
        }, {
            field : 'itemResult',
            title : 'T_EMC_ITEM.ITEMRESULT',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.updatePage.editResult(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '配置结果'
                });
            }
        }, {
            field : '',
            title : 'T_EMC_ITEM.ITEMQCINDEXANDREVISION',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.updatePage.editQcIndexAndRevision(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '配置QC指标和分段修约'
                });
            }
        }, {
            field : 'interceptRevision',
            title : 'T_EMC_ITEM.INTERCEPTREVISION',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'interceptRevision' : back
                    })
                }
            }
        }, {
            field : 'slopeRevision',
            title : 'T_EMC_ITEM.SLOPEREVISION',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'slopeRevision' : back
                    })
                }
            }
        }, {
            field : 'pnFunc',
            title : 'T_EMC_ITEM.PNFUNC',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'pnFunc',
            },
            editor : true
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            editor : true,
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function(index) {
                var selectedQcCategoryId = this.getData()[index].qcCategoryId;
                return {
                    selectedQcCategoryId : selectedQcCategoryId,
                    SETNAME_DIFFER : 'QC_CAL'
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var qcCategoryId = '';
                    var qcCategoryName = '';
                    rows.map(function(item, i) {
                        qcCategoryId += rows[i].id + ',';
                        qcCategoryName += rows[i].qcName + ',';
                    });

                    this.setData({
                        index : index,
                        qcCategoryId : qcCategoryId.substring(0, qcCategoryId.length - 1),
                        qcCategoryName : qcCategoryName.substring(0, qcCategoryName.length - 1)
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    qcCategoryId : '',
                    qcCategoryName : ''
                });
            }
        }, {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'negativeToZero',
            title : 'T_EMC_ITEM.NEGATIVETOZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'allowableError',
            title : 'T_EMC_ITEM.ALLOWABLEERROR',
            editor : true
        } ];
    },

    getTestMethodParamSelectColumns : function(testMethodId) {
        return [ {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            firstBlank : false,
            category : 'propertyTmpl',
            param : {
                templateUse : 'methodParam'
            },
            editor : true,
            onDataChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-fields').done(function(filed) {
                    var fileds = filed.rows.map(function(item) {

                        var baseFileds = {
                            field : item.internalField,
                            title : item.fieldName,
                        }
                        // 塞入type相关参数
                        var getFiledsFromType;
                        if (item.fieldType === 'fieldMultiSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                multiple : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                search : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldDate') {
                            getFiledsFromType = {
                                type : 'date'
                            }

                        } else if (item.fieldType == 'fieldContext') {
                            if (item.defaultValue) {
                            	if(item.fieldName == "计算公式"){
                                    getFiledsFromType = {
                                            value : item.defaultValue,
                                            type :'textarea',
                                            width : 1000,
                                            height : 150
                                        }
                            	}else{
                                    getFiledsFromType = {
                                            value : item.defaultValue
                                        }
                            	}
                            }
                        } else if (item.fieldType === 'script') {
                            getFiledsFromType = {
                                value : item.defaultValue,
                        		type : 'script',
                                width : 1000,
                                height : '80%'
                            }
                        }

                        // 塞入验证相关参数
                        var validators = [];
                        if (item.required == '1') {
                            validators.push('notEmpty');
                        }
                        if (item.fieldLength) {
                            validators.push('strLength[0,' + item.fieldLength + ']');
                        }

                        return Gikam.deepExtend({}, baseFileds, getFiledsFromType, validators)
                    })
                    formData.options.fields = fileds;
                    formData.registerValidators();
                    formData.refreshFields([ {
                        fields : fileds
                    } ])

                    Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-datas/' + testMethodId).done(function(data) {
                        formData.cleanData();
                        formData.setData(data, false);

                    });
                });
            },
        } ];
    },

    getTestMethodBatchParamSelectColumns : function() {
        return [ {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            firstBlank : false,
            category : 'propertyTmpl',
            param : {
                templateUse : 'methodParam'
            },
            editor : true,
            onDataChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-fields').done(function(filed) {
                    var fileds = filed.rows.map(function(item) {

                        var baseFileds = {
                            field : item.internalField,
                            title : item.fieldName,
                        }
                        // 塞入type相关参数
                        var getFiledsFromType;
                        if (item.fieldType === 'fieldMultiSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                multiple : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                search : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldDate') {
                            getFiledsFromType = {
                                type : 'date'
                            }

                        } else if (item.fieldType == 'fieldContext') {
                            if (item.defaultValue) {
                            	if(item.fieldName == "计算公式"){
                                    getFiledsFromType = {
                                            value : item.defaultValue,
                                            type :'textarea',
                                            width : 1000,
                                            height : 150
                                        }
                            	}else{
                                    getFiledsFromType = {
                                            value : item.defaultValue
                                        }
                            	}
                            }
                        }

                        // 塞入验证相关参数
                        var validators = [];
                        if (item.required == '1') {
                            validators.push('notEmpty');
                        }
                        if (item.fieldLength) {
                            validators.push('strLength[0,' + item.fieldLength + ']');
                        }

                        return Gikam.deepExtend({}, baseFileds, getFiledsFromType, validators)
                    })
                    formData.options.fields = fileds;
                    formData.registerValidators();
                    formData.refreshFields([ {
                        fields : fileds
                    } ])
                    /*Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-datas/' + testMethodId).done(function(data) {
                        formData.cleanData();
                        formData.setData(data, false);

                    });*/
                });
            },
        } ];
    },

    getRunTestMethodParamSelectColumns : function(runId) {
        return [ {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            firstBlank : false,
            category : 'propertyTmpl',
            param : {
                templateUse : 'methodParam'
            },
            editor : true,
            onDataChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-fields').done(function(filed) {
                    var fileds = filed.rows.map(function(item) {

                        var baseFileds = {
                            field : item.internalField,
                            title : item.fieldName,
                        }
                        // 塞入type相关参数
                        var getFiledsFromType;
                        if (item.fieldType === 'fieldMultiSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                multiple : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                search : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldDate') {
                            getFiledsFromType = {
                                type : 'date'
                            }

                        } else if (item.fieldType == 'fieldContext') {
                            if (item.defaultValue) {
                            	if(item.fieldName == "计算公式"){
                                    getFiledsFromType = {
                                            value : item.defaultValue,
                                            type :'textarea',
                                            width : 1000,
                                            height : 150
                                        }
                            	}else{
                                    getFiledsFromType = {
                                            value : item.defaultValue
                                        }
                            	}
                            }
                        } else if (item.fieldType === 'script') {
                            getFiledsFromType = {
                                    type : 'script',
                                    value : item.defaultValue,
                                    width : 1000,
                                    height : '80%'
                                }
                            }


                        // 塞入验证相关参数
                        var validators = [];
                        if (item.required == '1') {
                            validators.push('notEmpty');
                        }
                        if (item.fieldLength) {
                            validators.push('strLength[0,' + item.fieldLength + ']');
                        }

                        return Gikam.deepExtend({}, baseFileds, getFiledsFromType, {validators:validators})
                    })
                    formData.options.fields = fileds;
                    formData.registerValidators();
                    formData.refreshFields([ {
                        fields : fileds
                    } ])
                    // Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-order-task/' + tmplId + '/property-datas/' + orderTaskId).done(function(data) {
                    //     formData.cleanData();
                    //     formData.setData(data, false);
                    //
                    // });

                    Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-runs/property-datas/'  + runId).done(function(data) {
                        formData.cleanData();
                        formData.setData(data, false);

                    });

                });
            },
        } ];
    },

    getOrderTaskTestMethodParamSelectColumns : function(orderTaskId) {
        return [ {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            firstBlank : false,
            category : 'propertyTmpl',
            param : {
                templateUse : 'methodParam'
            },
            editor : true,
            onDataChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-method-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-methods/' + tmplId + '/property-fields').done(function(filed) {
                    var fileds = filed.rows.map(function(item) {

                        var baseFileds = {
                            field : item.internalField,
                            title : item.fieldName,
                        }
                        // 塞入type相关参数
                        var getFiledsFromType;
                        if (item.fieldType === 'fieldMultiSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                multiple : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldSelect') {
                            getFiledsFromType = {
                                type : 'select',
                                search : true,
                                category : 'sys',
                                param : {
                                    codeCategoryId : item.sysCode
                                }
                            }
                        } else if (item.fieldType === 'fieldDate') {
                            getFiledsFromType = {
                                type : 'date'
                            }

                        } else if (item.fieldType == 'fieldContext') {
                            if (item.defaultValue) {
                            	if(item.fieldName == "计算公式"){
                                    getFiledsFromType = {
                                            value : item.defaultValue,
                                            type :'textarea',
                                            width : 1000,
                                            height : 150
                                        }
                            	}else{
                                    getFiledsFromType = {
                                            value : item.defaultValue
                                        }
                            	}
                            }
                        } else if (item.fieldType == 'fieldContextArea') {
                            var contextAreaFields = {
                                field : 'ext$.' + item.internalField,
                                title : item.fieldName,
                                validators : [],
                                type : 'textarea',
                                editor : true,
                            };
                            if (item.required == '1') {
                                contextAreaFields.validators.push('notEmpty');
                            }
                            if (item.fieldLength) {
                                contextAreaFields.validators.push('strLength[0,' + item.fieldLength + ']');
                            }
                            return contextAreaFields;
                        } else if (item.fieldType === 'script') {
                            getFiledsFromType = {
                                type : 'script',
                                value : item.defaultValue,
                                width : 1000,
                                height : '80%'
                            }
                        }

                        // 塞入验证相关参数
                        var validators = [];
                        if (item.required == '1') {
                            validators.push('notEmpty');
                        }
                        if (item.fieldLength) {
                            validators.push('strLength[0,' + item.fieldLength + ']');
                        }

                        return Gikam.deepExtend({}, baseFileds, getFiledsFromType, validators)
                    })
                    formData.options.fields = fileds;
                    formData.registerValidators();
                    formData.refreshFields([ {
                        fields : fileds
                    } ])

                    Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/result-order-task/' + tmplId + '/property-datas/' + orderTaskId).done(function(data) {
                        formData.cleanData();
                        formData.setData(data, false);

                    });
                });
            },
        } ];
    },

    getCopyFormFields : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'choose',
            category : 'testCategory',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testCategoryId : rows[0].id,
                        ext$ : {
                            testcategory : rows[0].testCategoryDesc,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testCategoryId : '',
                    ext$ : {
                        testcategory : ''
                    }
                });
            },
        }, {
            field : 'testClass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
            editor : true,
        },{
            field : 'testMethodType',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            },
            editor : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            type : 'choose',
            category : 'test',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testId : rows[0].id,
                        ext$ : {
                            testname : rows[0].testName,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testId : '',
                    ext$ : {
                        testname : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            type : 'choose',
            category : 'method',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        methodId : rows[0].id,
                        ext$ : {
                            methodname : rows[0].methodName,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    methodId : '',
                    ext$ : {
                        methodname : ''
                    }
                });
            },
        }, ];
    },

    getCopyByCategoryFormFields : function() {
        return [ {
            field : 'ext$.testcategory',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'choose',
            category : 'testCategory',
            single : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        testCategoryId : rows[0].id,
                        ext$ : {
                            testcategory : rows[0].testCategoryDesc,
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    testCategoryId : '',
                    ext$ : {
                        testcategory : ''
                    }
                });
            },
        },  ];
    },


    getEmpChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_EMC_EMP.ID',
            field : 'id',
        }, {
            title : 'T_EMC_EMP.USERNAME',
            field : 'userName',
        } ];
    },

    getCopyOrganicItemFormFields : function() {
        return [ {
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP',
            validators : [ 'strLength[0, 21]', 'notEmpty' ],
        } ];
    },

    getBatchUpdateFormFields : function () {
        return [ {
            field : 'testMethodType',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            },
            editor : true
        }, {
            field : 'analysisFee',
            title : 'T_EMC_TEST_METHOD.ANALYSISFEE',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'sampleFee',
            title : 'T_EMC_TEST_METHOD.SAMPLEFEE',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'hours',
            title : 'T_EMC_TEST_METHOD.HOURS',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : true
        }, {
            field : 'recoveryRate',
            title : 'T_EMC_TEST_METHOD.RECOVERYRATE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'recoveryRate',
            },
        }, {
            field : 'conrolledNo',
            title : 'T_EMC_TEST_METHOD.CONROLLEDNO',
            validators : [ 'strLength[0,160]' ],
            editor : true
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_TEST_METHOD.BEFOREHANDLE',
            validators : [ 'strLength[0, 2000]' ],
            type : 'richText',
            editor : true
        }, {
            field : 'ext$.beforehandle2',
            title : 'T_EMC_TEST_METHOD.BEFOREHANDLE2',
            validators : [ 'strLength[0, 2000]' ],
            type : 'richText',
            editor : true
        }, {
            field : 'acqNoSuffix',
            title : 'T_EMC_TEST_METHOD.ACQNOSUFFIX',
            validators : [ 'strLength[0, 3]' ],
            editor : true
        }, {
            field : 'reportLimitType',
            title : 'T_EMC_TEST_METHOD.REPORTLIMITTYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'reportLimitType',
            },
        } ];
    },

    getBeforehandleEditListColumns: function() {
        return [ {
            checkbox : true
        }, {
            field : 'beforeHandleName',
            title : '名称',
            editor : true,
            width: 180,
        }, {
            field : 'beforeHandle',
            title : '内容',
            editor : true,
            width: 330,
        } ];
    },
    getBeforehandleChooseListColumns: function() {
        return [ {
            field : 'beforeHandleName',
            title : '名称',
            width: 180,
        }, {
            field : 'beforeHandle',
            title : '内容',
            width: 330,
        } ];
    },
});
