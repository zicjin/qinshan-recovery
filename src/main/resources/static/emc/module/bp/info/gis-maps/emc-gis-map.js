var emcGisMap = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/info/gis-maps',

    getEditListColumns : function() {
        return [ {
            field : 'clientOrderName',
            title : 'T_EMC_REPORT_RESULT.CLIENTORDERNAME',
        }, {
            field : 'ext$.foldermonitorpointname',
            title : 'T_EMC_REPORT_RESULT.MONITORPOINTNAME',
        }, {
            field : 'testName',
            title : 'T_EMC_REPORT_RESULT.TESTNAME',
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_REPORT_RESULT.ITEMALIASNAME',
        }, {
            field : 'reportResult',
            title : 'T_EMC_REPORT_RESULT.REPORTRESULT',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_REPORT_RESULT.RESULTSTATUS',
        }, {
            field : 'ext$.taskno',
            title : 'T_EMC_PROJECT.TASKNO',
        }, {
            field : 'ext$.longitude',
            title : 'T_EMC_FOLDER.LONGITUDE',
        }, {
            field : 'ext$.latitude',
            title : 'T_EMC_FOLDER.LATITUDE',
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'beginDate',
            title : 'T_EMC_GIS_MAP.BEGINDATE',
            type : 'date',
            onChange : function(field, value) {
                var _form = this;
                var url = emcGisMap.baseUrl + '/monitorPointCategories/queries';
                var endDate = _form.getField('endDate').value;
                if (!Gikam.isEmpty(endDate)) {
                    workspace.window.showMask(true);
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            beginDate : value,
                            endDate : _form.getField('endDate').value
                        }
                    })).done(function(items) {
                        _form.setSelectOptions('monitorPointCategory', items.map(function(item) {
                            return {
                                text : item.ext$.monitorpointcategory,
                                value : item.ext$.monitorpointcategoryid
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                monitorPointCategory : items[0].ext$.monitorpointcategoryid
                            });
                        } else {
                            _form.setData({
                                monitorPointCategory : '',
                                testName : '',
                                itemAliasName : ''

                            });
                        }
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'endDate',
            title : 'T_EMC_GIS_MAP.ENDDATE',
            type : 'date',
            onChange : function(field, value) {
                var _form = this;
                var beginDate = _form.getField('beginDate').value;
                var url = emcGisMap.baseUrl + '/monitorPointCategories/queries';
                workspace.window.showMask(true);
                Gikam.post(url, Gikam.getJsonWrapper({
                    f : {
                        beginDate : beginDate,
                        endDate : value
                    }
                })).done(function(items) {
                    _form.setSelectOptions('monitorPointCategory', items.map(function(item) {
                        return {
                            text : item.ext$.monitorpointcategory,
                            value : item.ext$.monitorpointcategoryid
                        };
                    }), true);
                    if (items.length > 0) {
                        _form.setData({
                            monitorPointCategory : items[0].ext$.monitorpointcategoryid
                        });
                    } else {
                        _form.setData({
                            monitorPointCategory : '',
                            testName : '',
                            itemAliasName : ''

                        });
                    }
                }).always(function() {
                    workspace.window.closeMask();
                });
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_GIS_MAP.MONITORPOINTCATEGORY',
            type : 'select',
            search : true,
            onChange : function(field, value) {
                var _form = this;
                if (!Gikam.isEmpty(value)) {
                    var url = emcGisMap.baseUrl + '/testNames/queries';
                    workspace.window.showMask(true);
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            beginDate : _form.getField('beginDate').value,
                            endDate : _form.getField('endDate').value,
                            monitorPointCategory : value
                        }
                    })).done(function(items) {
                        _form.setSelectOptions('testName', items.map(function(item) {
                            return {
                                text : item.testName,
                                value : item.testMethodId
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                testName : items[0].testMethodId
                            });
                        } else {
                            _form.setData({
                                testName : '',
                                itemAliasName : ''

                            });
                        }
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        }, {
            field : 'testName',
            title : 'T_EMC_GIS_MAP.TESTNAME',
            type : 'select',
            search : true,
            onChange : function(field, value) {
                var _form = this;
                if (!Gikam.isEmpty(value)) {
                    var url = emcGisMap.baseUrl + '/itemAliasNames/queries';
                    workspace.window.showMask(true);
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            beginDate : _form.getField('beginDate').value,
                            endDate : _form.getField('endDate').value,
                            monitorPointCategory : _form.getField('monitorPointCategory').value,
                            testName : value
                        }
                    })).done(function(items) {
                        _form.setSelectOptions('itemAliasName', items.map(function(item) {
                            return {
                                text : item.itemAliasName,
                                value : item.itemId
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                itemAliasName : items[0].itemId
                            });
                        } else {
                            _form.setData({
                                itemAliasName : ''

                            });
                        }
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_GIS_MAP.ITEMALIASNAME',
            type : 'select',
            search : true,
            onChange : function(field, value) {

            }
        } ];
    }

});
