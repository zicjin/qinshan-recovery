var emcDelivery = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/deliveries',
    sampleUrl: IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',
    projectUrl: IFM_CONTEXT + '/secure/emc/module/bp/project/projects',

    getProjectListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            formatter: function (i, v, r) {
                var value = '<a href="javascript:;">' + v + '</a>';
                if (r.ext$.urgent === '1') {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + value;
                } else {
                    return value;
                }
            },
        }, /*{
            field: 'erpOrderNumber',
            width: 180,
            title: 'T_EMC_PROJECT.ERPORDERNUMBER',
        },*/ {
            field: 'projName',
            title: 'T_EMC_FOLDER.PROJNAME',
        }, {
            field: 'ext$.biddername',
            title: 'T_EMC_PROJECT.BIDDERNAME',
        }];
    },

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'sampleNo',
            title: 'T_EMC_SAMPLE.DELIVERYNO',
        }, {
            field: 'orderCategory',
            title: 'T_EMC_SAMPLE.ORDERCATEGORY',
        }, {
            field: 'createdByName',
            title: 'T_EMC_SAMPLE.CREATEDBYNAME',
        }];
    },

    /** 样品 */
    getOrderColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
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
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,100]'],
            editor: true
        }, {
            field: 'sampleAddress',
            title: 'T_EMC_ORDER.SAMPLEADDRESS',
            validators: ['strLength[0,40]'],
            editor: true,
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC',
            validators: ['strLength[0,21]','notEmpty'],
            editor: true
        }, {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
            editor: true,
            width: 100,
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME',
            type: 'time',
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
        }, {
        //     field: 'ext$.checker',
        //     title: 'T_EMC_ORDER.CHECKER',
        //     editor: true,
        //     editable: true,
        //     type: 'choose',
        //     category: 'user-simple',
        //     single: true,
        //     onAfterChoose: function (rows) {
        //         if (!Gikam.isEmpty(rows)) {
        //             this.setData({
        //                 id: this.getActivedRow().id,
        //                 checkerId: rows[0].id,
        //                 ext$: {
        //                     checker: rows[0].userName,
        //                 },
        //             });
        //         }
        //     },
        //     onClean: function () {
        //         this.setData({
        //             id: this.getActivedRow().id,
        //             checkerId: '',
        //             ext$: {
        //                 checker: '',
        //             },
        //         });
        //     },
        // }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,170]'],
            editor: true
        // }, {
        //     field: 'parentOrderNo',
        //     title: 'T_EMC_ORDER.PARENTORDERNO',
        //     width: 120,
        //     editor: true,
        //     type: 'choose',
        //     category: 'order',
        //     single: true,
        //     onBeforeChoose: function () {
        //         let row = this.getActivedRow();
        //         if (row.qcCategoryCode === 'N/A') {
        //             Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NA_CAT_NOT_SET_PARENTNO');
        //             return false;
        //         }
        //
        //         return {
        //             projId_SEQ: row.projId,
        //             id_DIFFER: row.id
        //         }
        //     },
        //     onAfterChoose: function (rows) {
        //         if (!Gikam.isEmpty(rows)) {
        //             this.setData({
        //                 id: this.getActivedRow().id,
        //                 parentOrderNo: rows[0].orderNo
        //             });
        //         }
        //     },
        //     onClean: function () {
        //         this.setData({
        //             id: this.getActivedRow().id,
        //             parentOrderNo: ''
        //         });
        //     }
        // }, {
        //     field: 'orderNum',
        //     title: 'T_EMC_ORDER.ORDERNUM',
        //     validators: ['integer'],
        //     editor: true,
        //     width: 80,
        // }, {
        //     field: 'ext$.volume',
        //     title: 'T_EMC_ORDER.VOLUME',
        //     editor: true,
        //     width: 160,
        // }, {
        //     field: 'ext$.packing',
        //     title: 'T_EMC_ORDER.PACKING',
        //     editor: true,
        //     width: 80,
        // }, {
        //     field: 'ext$.isblank',
        //     title: 'T_EMC_ORDER.ISBLANK',
        //     editor: true,
        //     type: 'select',
        //     search: true,
        //     category: 'sys',
        //     param: {
        //         codeCategoryId: 'yesorno'
        //     },
        // }, {
        //     field: 'qcName',
        //     title: 'T_EMC_ORDER.QCNAME',
        }];
    },

    /** 样品 */
    getOrderSearchColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
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
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME',
            validators: ['strLength[0,100]'],
            editor: true
        }, {
            field: 'sampleAddress',
            title: 'T_EMC_ORDER.SAMPLEADDRESS',
            validators: ['strLength[0,40]'],
            editor: true,
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC',
            validators: ['strLength[0,21]'],
            editor: true
        }, {
            field: 'sendDate',
            title: 'T_EMC_ORDER.SENDDATE',
            type: 'date',
            editor: true,
            width: 100,
        }, {
            field: 'sendTime',
            title: 'T_EMC_ORDER.SENDTIME',
            type: 'time',
            editor: true,
            width: 100,
        }, {
            field: 'acceptedDate',
            title: 'T_EMC_ORDER.ACCEPTEDDATE',
            type: 'dateTime',
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
        }, {
        //     field: 'ext$.checker',
        //     title: 'T_EMC_ORDER.CHECKER',
        //     editor: true,
        //     editable: true,
        //     type: 'choose',
        //     category: 'user-simple',
        //     single: true,
        //     onAfterChoose: function (rows) {
        //         if (!Gikam.isEmpty(rows)) {
        //             this.setData({
        //                 id: this.getActivedRow().id,
        //                 checkerId: rows[0].id,
        //                 ext$: {
        //                     checker: rows[0].userName,
        //                 },
        //             });
        //         }
        //     },
        //     onClean: function () {
        //         this.setData({
        //             id: this.getActivedRow().id,
        //             checkerId: '',
        //             ext$: {
        //                 checker: '',
        //             },
        //         });
        //     },
        // }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
            validators: ['strLength[0,170]'],
            editor: true
        // }, {
        //     field: 'parentOrderNo',
        //     title: 'T_EMC_ORDER.PARENTORDERNO',
        //     width: 120,
        //     editor: true,
        //     type: 'choose',
        //     category: 'order',
        //     single: true,
        //     onBeforeChoose: function () {
        //         let row = this.getActivedRow();
        //         if (row.qcCategoryCode === 'N/A') {
        //             Gikam.alert('EMC.MODULE.BP.PROJECT.PROJECTS.TIP.NA_CAT_NOT_SET_PARENTNO');
        //             return false;
        //         }
        //
        //         return {
        //             projId_SEQ: row.projId,
        //             id_DIFFER: row.id
        //         }
        //     },
        //     onAfterChoose: function (rows) {
        //         if (!Gikam.isEmpty(rows)) {
        //             this.setData({
        //                 id: this.getActivedRow().id,
        //                 parentOrderNo: rows[0].orderNo
        //             });
        //         }
        //     },
        //     onClean: function () {
        //         this.setData({
        //             id: this.getActivedRow().id,
        //             parentOrderNo: ''
        //         });
        //     }
        // }, {
        //     field: 'orderNum',
        //     title: 'T_EMC_ORDER.ORDERNUM',
        //     validators: ['integer'],
        //     editor: true,
        //     width: 80,
        // }, {
        //     field: 'ext$.volume',
        //     title: 'T_EMC_ORDER.VOLUME',
        //     editor: true,
        //     width: 160,
        // }, {
        //     field: 'ext$.packing',
        //     title: 'T_EMC_ORDER.PACKING',
        //     editor: true,
        //     width: 80,
        // }, {
        //     field: 'ext$.isblank',
        //     title: 'T_EMC_ORDER.ISBLANK',
        //     editor: true,
        //     type: 'select',
        //     search: true,
        //     category: 'sys',
        //     param: {
        //         codeCategoryId: 'yesorno'
        //     },
        // }, {
        //     field: 'qcName',
        //     title: 'T_EMC_ORDER.QCNAME',
        }];
    },

    /** 测试信息 */
    getOrderItemGroupColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        },
            // {
            //     field : 'originalOrderNo',
            //     title : 'T_EMC_ORDER_TASK.ORIGINALORDERNO',
            //     editor : true
            // }, {
            //     field : 'batchNo',
            //     title : 'T_EMC_ORDER_ITEM_GROUP.BATCHNO',
            //     styleFormatter : function() {
            //         return {
            //             backgroundColor : 'palegreen'
            //         }
            //     },
            // },
            {
                field: 'ext$.ordercontainerno',
                width: 170,
                title: 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO'
            },
            {
                field: 'schemeName',
                title: 'T_EMC_ORDER_ITEM_GROUP.SCHEMENAME'
            }, {
                field: 'testName',
                title: 'T_EMC_ORDER_ITEM_GROUP.TESTNAME'
            }, {
                field: 'itemGroup',
                title: 'T_EMC_ORDER_ITEM_GROUP.ITEMGROUP'
            }, {
                field: 'methodName',
                title: 'T_EMC_ORDER_ITEM_GROUP.METHODNAME',
            }, {
                field: 'testMethodBasis',
                title: 'T_EMC_ORDER_ITEM_GROUP.TESTMETHODBASIS',
            }, {
                field: 'containerGroup',
                title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERGROUP',
                width: 80
            }, {
                field: 'containerCategoryCode',
                title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYCODE',
                width: 90
            }, {
                field: 'containerCategoryName',
                title: 'T_EMC_ORDER_ITEM_GROUP.CONTAINERCATEGORYNAME'
            }, {
                field: 'fixative',
                title: 'T_EMC_ORDER_ITEM_GROUP.FIXATIVE'
            }, {
                field: 'ext$.sampleweight',
                title: 'T_EMC_TEST_ORDER_CATEGORY.SAMPLEWEIGHT'
            }, {
                field: 'storeCondition',
                title: 'T_EMC_ORDER_ITEM_GROUP.STORECONDITION'
            }, {
                field: 'analysisOrgName',
                title: 'T_EMC_ORDER_ITEM_GROUP.ANALYSISORGNAME'
            }, {
                field: 'qcName',
                title: 'T_EMC_ORDER_ITEM_GROUP.QCNAME',
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
                field: 'dataSharing',
                title: 'T_EMC_ORDER_ITEM_GROUP.DATASHARING',
                type: 'select',
                search: true,
                category: 'sys',
                param: {
                    codeCategoryId: 'yesorno'
                },
            },];
    },

    getBatchInputFields: function () {
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
            validators: ['strLength[0,170]']
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
            onBeforeChoose: function () {
                return {
                    orgName_SB: '客服'
                }
            },
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
            onBeforeChoose: function () {
                return {
                    orgName_SB: '客服'
                }
            },
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
        }];
    },

    getDeliveryListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'ext$.projno',
            title: 'T_EMC_PROJECT.PROJNO',
            width: 130,
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_PROJECT.PROJNAME',
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'deliveryNo',
            title: 'T_EMC_DELIVERY.DELIVERYNO',
        }, {
            field: 'deliveryUserName',
            title: 'T_EMC_DELIVERY.DELIVERYUSERNAME',
            editor: true,
            type: 'choose',
            category: 'user-simple',
            validators: ['notEmpty'],
            single: true,
            onAfterChoose: function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id: this.getActivedRow().id,
                        deliveryUserId: rows[0].id,
                        deliveryUserName: rows[0].userName,
                    });
                }
            },
            onClean: function () {
                this.setData({
                    id: this.getActivedRow().id,
                    deliveryUserId: '',
                    deliveryUserName: '',
                });
            },
        }, {
            field: 'deliveryDate',
            title: 'T_EMC_DELIVERY.DELIVERYDATE',
            editor: true,
            type: 'date',
            validators: ['notEmpty'],
        }, {
            field: 'reportConfigLineId',
            title: 'T_EMC_SAMPLE.RECEIVEREPORTLINEID',
            type: 'select',
            search: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'samplingNumber'
            },
            editor: true,
            validators: ['notEmpty'],
        }, {
            field: 'createdByName',
            title: 'T_EMC_SAMPLE.CREATEDBYNAME',
            width: 120
        }]
    },

    getOrderContainerListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderContainerNo',
            title: 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width: 150
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'containerCategoryName',
            title: 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
            width: 150,
        }, {
            field: 'containerGroup',
            title: 'T_EMC_ORDER_TASK.CONTAINERGROUP',
            width: 100,
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE',
            width: 100,
        }, {
            field: 'storeCondition',
            title: 'T_EMC_ORDER_TASK.STORECONDITION',
            width: 100,
        }];
    },

    workflow: {
        dbTable: 'T_EMC_SAMPLE',
        columns: [{
            field: 'ID',
            title: 'T_EMC_FOLDER.ID'
        }]
    }

});
