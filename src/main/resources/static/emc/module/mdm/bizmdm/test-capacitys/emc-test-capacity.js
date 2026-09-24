var emcTestCapacity = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field: 'ext$.activatedflag',
            title: 'GIKAM.ACTIVE.TITLE.STATUS',
            type: 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
            width: 80,
            readonly : true,
        }, {
            field : 'ext$.testcategoryid',
            title : 'T_EMC_TEST_METHOD.TESTCATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
            readonly : true,
        }, {
            field : 'ext$.testclass',
            title : 'T_EMC_TEST_METHOD.TESTCLASS',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'testClass',
            },
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_TEST_METHOD.TESTID',
            readonly : true,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            readonly : true,
        }, {
            field: 'reportTestName',
            title: '报告中显示名称',
            editor: true,
            width: 150
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            width : 250,
            readonly : true,
        }, {
             field : 'ext$.maxcapacity',
             title : '实验室最大承载量',
             width : 250,
             type : 'number',
             editor : true
         }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
            field : 'ext$.methodscope',
            title : '方法范围',
            validators : [ 'strLength[0,200]' ],
            editor : true,
        },{
            field : 'ext$.testmethodaliasbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODALIASBASIS',
            readonly : true
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
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            },
        }, {
            field : 'ext$.methodno',
            title : 'T_EMC_METHOD.METHODNO',
            readonly : true,
        }, {
            field : 'methodParam',
            title : '方法参数1',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestCapacity.editPage.loadMethodParamPage(\'{id}\')">{name}</a>', {
                    id : row.id,
                    flag : 1,
                    name : '方法参数'
                });
            }
        // }, {
        //     field : 'ext$.methodparam',
        //     title : '方法参数2',
        //     formatter : function(i, v, row) {
        //         return Gikam.printf('<a href="javascript:;" onclick="emcTestCapacity.editPage.loadMethodParamPage2(\'{id}\')">{name}</a>', {
        //             id : row.id,
        //             flag : 0,
        //             name : '方法参数'
        //         });
        //     }
        }, {
            field : 'ext$.analysisorgname',
            title : 'T_EMC_TEST_METHOD.ANALYSISORGID',
            editor : true,
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
                            analysisorgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    analysisOrgId : '',
                    ext$ : {
                        analysisorgname : ''
                    }
                });
            },
        }, {
            field : 'ext$.conrolledno',
            title : 'T_EMC_TEST_METHOD.CONROLLEDNO',
            editor : true,
        }, {
            field : 'ext$.authentication',
            title : 'CMA认证',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.approval',
            title : 'CNAS认可',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        // }, {
        //      field : 'ext$.userange',
        //      title : '适用范围',
        //      editor : true,
         }, {
            field : 'ext$.outsourcing',
            title : 'T_EMC_TEST_CAPACITY.OUTSOURCING',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.analysisrecordname',
            title : 'T_EMC_TEST_METHOD.ANALYSISRECORDNAME',
            editor : true,
        }, {
            field : 'ext$.analysisfee',
            title : 'T_EMC_TEST_METHOD.ANALYSISFEE',
            editor : true,
        }, {
            field : 'ext$.samplefee',
            title : 'T_EMC_TEST_METHOD.SAMPLEFEE',
            editor : true,
        }, {
            field : 'ext$.hours',
            title : 'T_EMC_TEST_METHOD.HOURS',
            type : 'number',
            hasArrow : true,
            editor : true,
        }, {
            field : 'ext$.defaultflag',
            title : 'T_EMC_TEST_METHOD.DEFAULTFLAG',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.testenglishname',
            title : 'T_EMC_TEST_METHOD.TESTENGLISHNAME',
            readonly : true,
        }, {
            field : 'ext$.inputusername',
            title : 'T_EMC_TEST_METHOD.INPUTUSERNAME',
            editor : true,
            width : 100,
            category : 'emp',
            type : 'choose',
            // single : true,
            onBeforeChoose : function() {
                // var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    orgid_SEQ : workspace.user.ext$.parentorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var userNames = [];
                    var userIds = [];
                    Gikam.each(rows, function(index, item) {
                        userNames.push(item.userName);
                        userIds.push(item.id);
                    });
                    Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                        id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                        ext$ : {
                        	inputusername : userNames.join(','),
                        	inputuserid : userIds.join(','),
                            analysisorgname : rows[0].ext$.departmentname
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                    id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                    ext$ : {
                    	inputusername : '',
                    	inputuserid : ''
                    }
                });
            }
        }, {
            field : 'ext$.recoveryrate',
            title : 'T_EMC_TEST_METHOD.RECOVERYRATE',
            readonly : true,
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
                return Gikam.printf('<a href="javascript:;" onclick="emcTestCapacity.editPage.beforeHandlequery(\'{index}\')">{name}</a>', {
                    index : i,
                    name : '查看前处理'
                });
            }
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_TEST_METHOD.REMARK',
            type : 'richText',
            readonly : true
        }, {
            field : 'ext$.effectivedate',
            title : 'T_EMC_TEST_CAPACITY.EFFECTIVEDATE',
            type : 'date',
            editor : true
        },{
            field : 'ext$.testitemsort',
            title : 'T_EMC_TEST_CAPACITY.TESTITEMSORT',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'testitemsort'
            },
        },{
            field : 'ext$.siterecordno',
            title : 'T_EMC_TEST_CAPACITY.SITERECORDNO',
            editor : true,
        },{
            field : 'ext$.weighingusername',
            title : 'T_EMC_RUN.WEIGHINGUSERNAME',
            editor : true,
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.ext$.analysisorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                        id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                        ext$ : {
                            weighingusername : rows[0].userName,
                            weighinguserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                    id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                    ext$ : {
                        weighingusername : '',
                        weighinguserid : ''
                    }
                });
            }
        }, {
            field : 'ext$.pretreatusername',
            title : 'T_EMC_RUN.PRETREATUSERNAME',
            editor : true,
            width : 100,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.ext$.analysisorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                        id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                        ext$ : {
                            pretreatusername : rows[0].userName,
                            pretreatuserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-test-capacity-edit-list-grid').setData({
                    id : Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow().id,
                    ext$ : {
                        pretreatusername : '',
                        pretreatuserid : ''
                    }
                });
            }
        }/*, {
            field : 'ext$.batchno',
            title : 'T_EMC_STOCK.BATCHNO',
            editor : true,

        }*/,{
            field : 'ext$.batchno',
            title : 'T_EMC_STOCK.BATCHNO',
            type : 'choose',
            category : 'mtlStock',
            single : true,
            editor :true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    MTLORGID_SEQ: workspace.user.ext$.parentorgid,
                }
            },
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                this.setData({
                    index : index,

                    ext$ : {
                    	batchno : rows[0].batchNo,
                    }
                });
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    ext$ : {
                    	batchno : '',
                    }
                });
            }

        },{
            field : 'ext$.isdefault',
            title : 'T_EMC_TEST_CAPACITY.ISDEFAULT',
            type : 'select',
            editor :true,
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        },{
            field : 'ext$.qcrecordcontrolno',
            title : '质控记录受控编号',
            editor :true
        },{
            field : 'ext$.calicurvecontrolno',
            title : '校准曲线受控编号',
            editor :true
        }, {
            field : 'tube',
            title : '是否前后管',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }  ];
    },

    getItemGroupListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP',
            readonly : true
        }, {
            field : 'relatedItemGroup',
            title : 'T_EMC_CAPACITY_ITEM_GROUP.RELATEDITEMGROUP',
            editor : true,
            type : 'choose',
            category : 'capacityItemGroup',
            onBeforeChoose : function() {
                return {
                    id_DIFFER : this.getActivedRow().id,
                    testCapacityId_SEQ : this.getActivedRow().testCapacityId
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    let relatedItemGroupId = rows.map(item => item.id).join();
                    let relatedItemGroup = rows.map(item => item.itemGroup).join();
                    this.setData({
                        id : this.getActivedRow().id,
                        relatedItemGroupId : relatedItemGroupId,
                        relatedItemGroup : relatedItemGroup,
                        relatedFlag : '1'
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    relatedItemGroupId : '',
                    relatedItemGroup : '',
                    relatedFlag : ''
                })
            },
        } ];
    },

    getItemGroupChooseListColumns : function() {
        return [ {
            field : 'itemGroup',
            title : 'T_EMC_ITEM_GROUP.ITEMGROUP',
            readonly : true
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
                        ext$ : {
                            methodId : rows[0].id,
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
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_TEST_CAPACITY.ID',
            preInsert : true
        }, {
            field : 'testMethodId',
            title : 'T_EMC_TEST_CAPACITY.TESTMETHODID'
        }, {
            field : 'orgId',
            title : 'T_EMC_TEST_CAPACITY.ORGID'
        }, ];
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

    getBatchInputFields : function() {
        return [ {
            field : 'ext$.analysisorgname',
            title : 'T_EMC_TEST_METHOD.ANALYSISORGID',
            editor : true,
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
                            analysisorgname : rows[0].orgName
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    analysisOrgId : '',
                    ext$ : {
                        analysisorgname : ''
                    }
                });
            },
        }, {
            field : 'ext$.authentication',
            title : 'T_EMC_TEST_METHOD.AUTHENTICATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.approval',
            title : 'T_EMC_TEST_METHOD.APPROVAL',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.outsourcing',
            title : 'T_EMC_TEST_CAPACITY.OUTSOURCING',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,
        }, {
            field : 'ext$.effectivedate',
            title : 'T_EMC_TEST_CAPACITY.EFFECTIVEDATE',
            type : 'date',
            editor : true
        } , {
            field : 'ext$.conrolledno',
            title : 'T_EMC_TEST_METHOD.CONROLLEDNO',
            editor : true,
        }, {
            field : 'ext$.analysisrecordname',
            title : 'T_EMC_TEST_METHOD.ANALYSISRECORDNAME',
            editor : true,
        },{
            field : 'ext$.testitemsort',
            title : 'T_EMC_TEST_CAPACITY.TESTITEMSORT',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'testitemsort'
            },
        },{
            field : 'ext$.siterecordno',
            title : 'T_EMC_TEST_CAPACITY.SITERECORDNO',
            editor : true,
        }, {
            field : 'ext$.inputusername',
            title : 'T_EMC_TEST_METHOD.INPUTUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            // single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.ext$.analysisorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var userIds = [], userNames = [];
                    rows.map(function (row) {
                        userIds.push(row.id);
                        userNames.push(row.userName);
                    });
                    this.setData({
                        ext$ : {
                            inputusername : userNames.join(),
                            inputuserid : userIds.join(),
                        }
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    ext$ : {
                    	inputusername : '',
                    	inputuserid : ''
                    }
                });
            }
        },{
            field : 'ext$.weighingusername',
            title : 'T_EMC_RUN.WEIGHINGUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.ext$.analysisorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                        ext$ : {
                            weighingusername : rows[0].userName,
                            weighinguserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    ext$ : {
                        weighingusername : '',
                        weighinguserid : ''
                    }
                });
            }
        }, {
            field : 'ext$.pretreatusername',
            title : 'T_EMC_RUN.PRETREATUSERNAME',
            editor : true,
            category : 'emp',
            type : 'choose',
            single : true,
            onBeforeChoose : function() {
                var activedRow = Gikam.getComp('emc-test-capacity-edit-list-grid').getActivedRow();
                return {
                    departmentid_SEQ : activedRow.ext$.analysisorgid,
                    status_DIFFER : 'locked'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                	this.setData({
                        ext$ : {
                            pretreatusername : rows[0].userName,
                            pretreatuserid : rows[0].id
                        }
                    });
                }
            },
            onClean : function() {
            	this.setData({
                    ext$ : {
                        pretreatusername : '',
                        pretreatuserid : ''
                    }
                });
            }
        }, {
            field : 'ext$.batchno',
            title : 'T_EMC_STOCK.BATCHNO',
            type : 'choose',
            category : 'mtlStock',
            single : true,
            editor :true,
            onAfterChoose : function(rows, index) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                this.setData({
                    index : index,

                    ext$ : {
                    	batchno : rows[0].batchNo,
                    }
                });
            },
            onClean : function() {
                this.setData({
                    ext$ : {
                    	batchno : '',
                    }
                });
            }
        },{
            field : 'ext$.isdefault',
            title : 'T_EMC_TEST_CAPACITY.ISDEFAULT',
            type : 'select',
            editor :true,
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        },{
            field : 'ext$.qcrecordcontrolno',
            title : '质控记录受控编号',
            editor :true
        },{
            field : 'ext$.calicurvecontrolno',
            title : '校准曲线受控编号',
            editor :true
        },{
            field : 'ext$.methodscope',
            title : '方法范围',
            validators : [ 'strLength[0,200]' ],
            editor : true,
        }  ];
    },


    getBatchInputAnalysis : function() {
        return [ {
            field : 'analysisCategory',
            title : 'T_EMC_TEST_METHOD_EQUIPT.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
            editor : true,
        }];
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

    getTestCapacityParamSelectColumns : function(testCapacityId,flag1) {
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
            onChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-capacity-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-capacity/' + tmplId + '/property-fields').done(function(filed) {
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
                    Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-capacity/' + tmplId + '/property-datas/' + testCapacityId+'/flag/'+flag1).done(function(data) {
                        formData.cleanData();
                        formData.setData(data, false);

                    });
                });
            },
        } ];
    },

    getTestCapacityBatchParamSelectColumns : function() {
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
            onChange : function(field, value, index, oldValue, text) {

                var formData = Gikam.getComp('emc-method-param-edit-test-capacity-list-form');
                var tmplId = value;

                // 获取数据

                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/test-capacity/' + tmplId + '/property-fields').done(function(filed) {
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

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testid',
            title : 'T_EMC_TEST_METHOD.TESTID',
            readonly : true,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
            readonly : true,
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_TEST_METHOD.METHODNAME',
            width : 250,
            readonly : true,
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_TEST_METHOD.TESTMETHODBASIS',
            readonly : true
        }, {
            field : 'ext$.methodno',
            title : 'T_EMC_METHOD.METHODNO',
            readonly : true,
        } ];
    },

    getItemListColumns : function() {
        return [ {
            checkbox : true,
            fixed : true
        },  {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            width : 75,
            type : 'number',
            hasArrow : true,
            editor : false,
            fixed : true
        }, {
            field : 'itemName',
            title : 'T_EMC_ITEM.ITEMNAME',
            type : 'link',
            fixed : true
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_ITEM.ITEMALIASNAME',
            type : 'script',
            editor : false,
            fixed : true,
            onChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    id : this.getActivedRow().id,
                    itemAliasName : replacedValue
                })
            }
        }, {
            field : 'repeatItem',
            title : 'T_EMC_ITEM.REPEATITEM',
            validators : [ 'integer' ],
            type : 'number',
            hasArrow : true,
            editor : false,
            fixed : true
        }, Gikam.status.getActiveColumn(),{
            field : 'unitName',
            title : 'T_EMC_ITEM.UNITNAME',
            editor : false,
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
        }, {
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
            editor : false
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
            editor : false
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
            editor : false,
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
            editor : false
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : false,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
            editor : false,
            onChange : function (field, value, rowIndex, oldValue, text) {
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
            editor : false,
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
            editor : false,
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
            editor : false
        }, {
            field : 'ext$.detectedquantityunit',
            title : 'T_EMC_ITEM.DETECTEDQUANTITYUNIT',
            editor : false,
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
            editor : false
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        }, {
            field : 'ext$.ignorezero',
            title : 'T_EMC_ITEM.IGNOREZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        },  {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            editor : false,
            type : 'script',
            onChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    id : this.getActivedRow().id,
                    originalRecordName : replacedValue
                })
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'itemResult',
            title : 'T_EMC_ITEM.ITEMRESULT',
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="emcTestMethod.editPage.editResult(\'{index}\')">{name}</a>', {
                    index : i,
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
            editor : false
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        },  {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        }, {
            field : 'negativeToZero',
            title : 'T_EMC_ITEM.NEGATIVETOZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : false
        }, {
            field : 'allowableError',
            title : 'T_EMC_ITEM.ALLOWABLEERROR',
        }, {
            field : 'ext$.theorycontent',
            title : '标点值',
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
        } ];
    },

    getBatchItemFormFields : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_ITEM.ORDERNO',
            validators : [ 'integer' ],
            type : 'number',
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
        }, {
            field : 'ext$.limitunitname',
            title : 'T_EMC_ITEM.LIMITUNITNAME',
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
                        ext$ : {
                            limitunitname : rows[0].unitName
                        }
                    })
                }
            }
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ITEM.REVISIONRULE',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
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
        }, {
            field : 'analysisCategory',
            title : 'T_EMC_ITEM.ANALYSISCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analysisCategory',
            },
        }, {
            field : 'defaultResult',
            title : 'T_EMC_ITEM.DEFAULTRESULT',
        }, {
            field : 'qcCategoryName',
            title : 'T_EMC_ITEM.QCCATEGORYID',
            editor : false,
            type : 'choose',
            category : 'qcCategory',
            onBeforeChoose : function(index) {
                var selectedQcCategoryId = this.getData().qcCategoryId;
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
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ITEM.REPORTDISPLAY',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'standSetId',
            title : 'T_EMC_ITEM.STANDSETID',
            type : 'select',
            search : false,
            category : 'standardSetCategory',
            param : {
                standardSet : 'Default'
            },
            onChange : function (field, value, rowIndex, oldValue, text) {
                if (text.indexOf('数字') !== -1) {
                    this.setData({
                        standSetId : value,
                        resultCategory : 'number'
                    });
                }
                if (text.indexOf('字符') !== -1) {
                    this.setData({
                        standSetId : value,
                        resultCategory : 'char'
                    });
                }
            }
        }, {
            field : 'interceptRevision',
            title : 'T_EMC_ITEM.INTERCEPTREVISION',
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
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
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        'slopeRevision' : back
                    })
                }
            }
        }, {
            field : 'ext$.detectedquantity',
            title : 'T_EMC_ITEM.DETECTEDQUANTITY',
        }, {
            field : 'ext$.detectedquantityunit',
            title : 'T_EMC_ITEM.DETECTEDQUANTITYUNIT',
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
        }, {
            field : 'param',
            title : 'T_EMC_ITEM.PARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'ext$.ignorezero',
            title : 'T_EMC_ITEM.IGNOREZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        },  {
            field : 'originalRecordName',
            title : 'T_EMC_ITEM.ORIGINALRECORDNAME',
            type : 'script',
            onChange : function (field, value) {
                let replacedValue = value.replace(/\s_mst\w*="\d+"/g, '').replace(/<p><br><\/p>/g, '').replace(/<p>/g, '').replace(/<\/p>/g, '');
                this.setData({
                    originalRecordName : replacedValue
                })
            }
        }, {
            field : 'highLimit',
            title : 'T_EMC_ITEM.HIGHLIMIT',
            validators : [ 'number' ],
        }, {
            field : 'pnFunc',
            title : 'T_EMC_ITEM.PNFUNC',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'pnFunc',
            },
        }, {
            field : 'limitRevision',
            title : 'T_EMC_ITEM.LIMITREVISION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        },  {
            field : 'calQcResult',
            title : 'T_EMC_ITEM.CALQCRESULT',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'scientificNotation',
            title : 'T_EMC_ITEM.SCIENTIFICNOTATION',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'negativeToZero',
            title : 'T_EMC_ITEM.NEGATIVETOZERO',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'allowableError',
            title : 'T_EMC_ITEM.ALLOWABLEERROR',
        }, {
            field : 'ext$.theorycontent',
            title : '标点值',
        }, {
            field : 'sampleParam',
            title : 'T_EMC_ITEM.SAMPLEPARAM',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/test/test-capacitys'
});
