var emcOrderHandover = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/emc/module/bp/order/order-handovers',

    getSampleListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'projNo',
            title: 'T_EMC_PROJECT.PROJNO',
            formatter: function (i, v, r) {
                if (r.urgent == 1) {
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                } else {
                    return v;
                }
            },
        }, {
            field: 'ext$.projname',
            title: 'T_EMC_PROJECT.PROJNAME'
        }, {
            field: 'ext$.testorgname',
            title: 'T_EMC_PROJECT.TESTORG'
        }, {
            field: 'sampleNo',
            title: 'T_EMC_SAMPLE.SAMPLENO'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_SAMPLE.ORDERCATEGORY'
        }, {
            field: 'createdByName',
            title: 'T_EMC_SAMPLE.CREATEDBYNAME'
        }, {
            field: 'reportConfigLineId',
            title: 'T_EMC_SAMPLE.REPORTCONFIGLINEID',
            type: 'select',
            search: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'samplingReport'
            },
            editor: true,
        }, {
            field: 'remark',
            title: 'T_EMC_SAMPLE.REMARK'
        }, {
            field: 'ext$.archivereportlineid',
            title: 'T_EMC_SAMPLE.ARCHIVEREPORTLINEID',
            type: 'select',
            search: true,
            multiple: true,
            category: 'reportConfigLine',
            param: {
                reportConfigId: 'samplingReport'
            },
            editor: true,
        }, {
            field: 'id',
            title: 'GIKAM.BUTTON.APPENDIX_INFO',
            formatter: function (i, v) {
                return '<a href="javascript:;" onclick="emcOrderHandover.searchAllPage.viewSampleFile(\'' + v + '\')">附件信息</a>';
            }
        }];
    },

    getOrderListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'orderNo',
            title: 'T_EMC_ORDER.ORDERNO'
        }, {
            field: 'ext$.monitorpointname',
            title: 'T_EMC_ORDER_RECEIPT.MONITORPOINTNAME'
        }, {
            field: 'ext$.monitorpointno',
            title: 'T_EMC_ORDER_RECEIPT.MONITORPOINTNO'
        }, {
            field: 'clientOrderName',
            title: 'T_EMC_ORDER.CLIENTORDERNAME'
        }, {
            field: 'qcName',
            title: 'T_EMC_ORDER.QCCATEGORYNO',
            formatter: function (i, v, row) {
                if (!v) {
                    return 'N/A'
                }
                return v;
            }
        }, {
            field: 'sampleBeginDate',
            title: 'T_EMC_ORDER.SAMPLEBEGINDATE'
        }, {
            field: 'sampleBeginTime',
            title: 'T_EMC_ORDER.SAMPLEBEGINTIME'
        }, {
            field: 'sampleCompleteDate',
            title: 'T_EMC_ORDER.SAMPLECOMPLETEDATE'
        }, {
            field: 'sampleCompleteTime',
            title: 'T_EMC_ORDER.SAMPLECOMPLETETIME'
        }, {
            field: 'orderCategory',
            title: 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field: 'parentOrderNo',
            title: 'T_EMC_ORDER.PARENTORDERNO',
        }, {
            field: 'orderDesc',
            title: 'T_EMC_ORDER.ORDERDESC'
        }, {
            field: 'remark',
            title: 'T_EMC_ORDER.REMARK',
        }, {
            field: 'labelNo',
            title: 'T_EMC_ORDER.LABELNO'
        }, {
            field: 'cycle',
            title: 'T_EMC_ORDER.CYCLE',
        }, {
            field: 'frequency',
            title: 'T_EMC_ORDER.FREQUENCY'
        }, {
            field: 'sampleNum',
            title: 'T_EMC_ORDER.SAMPLENUM'
        }];
    },

    getContainerListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'ext$.ordercontainerno',
            title: 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 120,
        }, {
            field: 'containerCategoryName',
            title: 'T_EMC_ORDER_TASK.CONTAINERCATEGORYNAME',
        }, {
            field: 'containerGroup',
            title: 'T_EMC_ORDER_TASK.CONTAINERGROUP'
        }, {
            field: 'fixative',
            title: 'T_EMC_ORDER_TASK.FIXATIVE'
        }, {
            field: 'testName',
            title: 'T_EMC_ORDER_TASK.TESTNAME'
        }, {
            field: 'storeCondition',
            title: 'T_EMC_ORDER_TASK.STORECONDITION'
        }];
    }

});
