var emcQualityControlChart = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/quality-control-charts',

    getQueryFormFields : function() {
        return [ {
            field : 'testCategory',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.TEST_CATEGORY',
            type : 'select',
            search : true,
            category : 'testCategory',
            onChange : function() {
                var _form = Gikam.getComp('emc-quality-control-chart-search-list-form');
                var testCategory = _form.getData().testCategory;
                Gikam.getComp('emc-quality-control-chart-search-list-form').setData({
                    testName : null,
                    itemName : null,
                    qcCategory : null,
                    userName : null
                });
                var url = IFM_CONTEXT + '/secure/emc/module/sa/quality-control-charts/tests/queries/selectable';
                if (Gikam.isNotEmpty(testCategory)) {
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            testCategoryId_SEQ : testCategory
                        }
                    })).done(function(items) {
                        var testArr = items.map(function(item) {
                            return {
                                text : item.testName,
                                value : item.testId
                            };
                        });
                        _form.setSelectOptions('testName', testArr, true);
                    });
                } else {
                    _form.setSelectOptions('testName', [], true);
                }
            }
        }, {
            field : 'testName',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.TEST_NAME',
            type : 'select',
            search : true,
            onChange : function() {
                var _form = Gikam.getComp('emc-quality-control-chart-search-list-form');
                var testId = _form.getData().testName;
                Gikam.getComp('emc-quality-control-chart-search-list-form').setData({
                    itemName : null,
                    qcCategory : null,
                    userName : null
                });
                var url = IFM_CONTEXT + '/secure/emc/module/sa/quality-control-charts/items/queries/selectable';
                if (Gikam.isNotEmpty(testId)) {
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            testId_SEQ : testId
                        }
                    })).done(function(items) {
                        var itemArr = items.map(function(item) {
                            return {
                                text : item.itemName,
                                value : item.itemId
                            };
                        });
                        _form.setSelectOptions('itemName', itemArr, true);
                    });
                } else {
                    _form.setSelectOptions('itemName', [], true);
                }
            }
        }, {
            field : 'itemName',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.ITEM_NAME',
            type : 'select',
            search : true,
            onChange : function() {
                var _form = Gikam.getComp('emc-quality-control-chart-search-list-form');
                var testId = _form.getData().testName;
                var itemId = _form.getData().itemName;
                Gikam.getComp('emc-quality-control-chart-search-list-form').setData({
                    qcCategory : null,
                    userName : null
                });
                var url = IFM_CONTEXT + '/secure/emc/module/sa/quality-control-charts/qc-categories/queries/selectable';
                if (Gikam.isNotEmpty(testId) && Gikam.isNotEmpty(itemId)) {
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            testId_SEQ : testId,
                            itemId_SEQ : itemId
                        }
                    })).done(function(items) {
                        var qcCategoryArr = items.map(function(item) {
                            return {
                                text : item.qcName,
                                value : item.qcCategoryId
                            };
                        });
                        _form.setSelectOptions('qcCategory', qcCategoryArr, true);
                    });
                } else {
                    _form.setSelectOptions('qcCategory', [], true);
                }
            }
        }, {
            field : 'qcCategory',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.QC_CATEGORY',
            type : 'select',
            search : true,
            onChange : function() {
                var _form = Gikam.getComp('emc-quality-control-chart-search-list-form');
                var testId = _form.getData().testName;
                var itemId = _form.getData().itemName;
                var qcCategoryId = _form.getData().qcCategory;
                Gikam.getComp('emc-quality-control-chart-search-list-form').setData({
                    userName : null
                });
                var url = IFM_CONTEXT + '/secure/emc/module/sa/quality-control-charts/users/queries/selectable';
                if (Gikam.isNotEmpty(testId) && Gikam.isNotEmpty(itemId) && Gikam.isNotEmpty(qcCategoryId)) {
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            testId_SEQ : testId,
                            itemId_SEQ : itemId,
                            qcCategoryId_SEQ : qcCategoryId
                        }
                    })).done(function(items) {
                        var userArr = items.map(function(item) {
                            return {
                                text : item.inputUserName,
                                value : item.inputUserId
                            };
                        });
                        _form.setSelectOptions('userName', userArr, true);
                    });
                } else {
                    _form.setSelectOptions('userName', [], true);
                }
            }
        }, {
            field : 'userName',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.USER_NAME',
            search : true,
            type : 'select',
        }, {
            field : 'startDate',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.START_DATE',
            type : 'date',
        }, {
            field : 'endDate',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.END_DATE',
            type : 'date',
        }, {
            field : 'pageSize',
            title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.PAGE_SIZE',
        } ];
    },

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'runId',
            title : 'T_EMC_ORDER_TASK_RESULT.BATCHNO',
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULT'
        }, {
            field : 'ext$.analysisdate',
            title : 'T_EMC_ORDER_TASK.ANALYSISDATE'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK_RESULT.TESTNAME'
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
        }, {
            field : 'standQty',
            title : 'T_EMC_ORDER_TASK_RESULT.STANDQTY',
        } ];
    },

});
