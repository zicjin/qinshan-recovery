var emcSubpackage = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/subpackages",

    getEditListColumns : function() {
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
            field : 'subpackageCategory',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGECATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'subpackageType',
            },
        }, {
            field : 'projNo',
            title : 'T_EMC_SUBPACKAGE.PROJNO',
        }, {
            field : 'innerSubcontractorId',
            title : '分包方式',
            formatter : function(i, v ,r) {
                return v ? '内部分包' : '外部分包';
            },
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

    getAcceptListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_SUBPACKAGE.STATUS',
            formatter : function(index, x, row) {
                if (row.status === '1') {
                    return '已创建合同';
                }
                return '未创建合同';
            }
        }, {
            field : 'subpackageNo',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGENO',
        }, {
            field : 'ext$.bizcategoryid',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
        }, {
            field : 'createdByName',
            title : 'T_EMC_SUBPACKAGE.ENTRUSTOR',
            width : 80,
        }, {
            field : 'createdTime',
            title : 'T_EMC_SUBPACKAGE.CREATEDTIME',
        }, {
            field : 'testOrgName',
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

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_SUBPACKAGE.STATUS',
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
            field : 'testOrgName',
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

    getTaskListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_ITEM_GROUP.METHODNAME',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_SUBPACKAGE.ID',
            validators : [ 'strLength[0, 19]' ],
            preInsert : true,
        }, {
            field : 'subpackageNo',
            title : 'T_EMC_SUBPACKAGE.SUBPACKAGENO',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        }, {
            field : 'projId',
            title : 'T_EMC_SUBPACKAGE.PROJID',
            validators : [ 'strLength[0, 19]' ],
            preInsert : true,
        }, {
            field : 'projNo',
            title : 'T_EMC_SUBPACKAGE.PROJNO',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        }, {
            field : 'contractNo',
            title : 'T_EMC_SUBPACKAGE.CONTRACTNO',
            validators : [ 'strLength[0, 36]' ],
            preInsert : true,
        } ];
    },

    getBaseInfoFormFields : function() {
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
        }   ];
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
                // parentId_SB : workspace.user.ext$.parentorgid
            },
        }, {
            field : 'remark',
            title : 'T_EMC_PROJ_SCHEME.REMARK',
            validators : [ 'strLength[0,512]' ],
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
        }, {
            field : 'ext$.subcontract1',
            title : '采样前分包',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'ext$.subcontract2',
            title : '采样后分包',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
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
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 120,
        }, {
            field : 'qcCategoryCode',
            title : 'T_EMC_ORDER.QCCATEGORYCODE',
        }, {
            field : 'qcName',
            title : 'T_EMC_ORDER.QCNAME',
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
            validators : [ 'strLength[0,540]' ],
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
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        parentOrderNo : rows[0].orderNo
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    parentOrderNo : ''
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
        }   ];
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
                    Gikam.put(emcProject.baseUrl + '/' + activedRow.id, Gikam.getJsonWrapper({}, [ 'emcOrderItemGroupServiceImpl', selections ])).done(function() {
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
        }, {
            field : 'ext$.subcontract2',
            title : '采样后分包',
            type : 'simpleCheckbox',
            editor : true,
            onChange : function(field, value, index) {
                console.info(field, value)
                if (value === '1') {
                    this.setData({
                        index : index,
                        ext$ : {
                            subcontract2 : value,
                            subpackagetype : 'orderSubpackage'
                        }
                    })
                } else {
                    this.setData({
                        index : index,
                        ext$ : {
                            subcontract2 : value,
                            subpackagetype : ''
                        }
                    })
                }
            }
        }, {
            field : 'ext$.remark',
            title : '备注',
            editor : true
        } ];
    },

})
