var emcReportTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/info/report-tmpls',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_REPORT_TMPL.ORDERNO',
            editor : true,
            isInteger : true,
            type : 'number',
            hasArrow : true,
            width : 80
        }, {
            field : 'activatedFlag',
            title : 'T_EMC_REPORT_TMPL.ACTIVATEDFLAG',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag'
            },
            width : 50
        }, {
            field : 'tmplName',
            title : 'T_EMC_REPORT_TMPL.TMPLNAME',
            editor : true,
            width : 150
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT_TMPL.REMARK',
            editor : true,
            width : 150
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_REPORT_TMPL.ORDERNO'
        }, {
            field : 'tmplName',
            title : 'T_EMC_REPORT_TMPL.TMPLNAME'
        }, {
            field : 'remark',
            title : 'T_EMC_REPORT_TMPL.REMARK'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'monitorTmplGroupId',
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP',
            type : 'select',
            category : 'monitorTmplGroup',
            onChange : function(field, value) {
                var _form = this;
                if (!Gikam.isEmpty(value)) {
                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/queries/selectable';
                    url += Gikam.param({
                        monitorTmplGroupId : value
                    });
                    Gikam.getJson(url).done(function(items) {
                        _form.setSelectOptions('monitorTmplId', items.map(function(item) {
                            return {
                                text : item.monitorTmplName,
                                value : item.id
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                monitorTmplId : items[0].id
                            });
                        } else {
                            _form.setData({
                                monitorTmplId : "",
                            });
                        }
                    });
                }
            }
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_REPORT_TMPL_LINE.MONITORTMPLID',
            type : "select",
            category : 'monitorTmpl',
            onChange : function(field, value) {
                var _form = this;
                _form.setData({
                    schemeName : "",
                });
                if (!Gikam.isEmpty(value)) {
                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/schemes/queries/selectable';
                    url += Gikam.param({
                        monitorTmplId : value
                    });
                    Gikam.getJson(url).done(function(items) {
                        _form.setSelectOptions('schemeName', items.map(function(item) {
                            return {
                                text : item.schemeName,
                                value : item.schemeName
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                schemeName : items[0].schemeName
                            });
                        } else {
                            _form.setData({
                                schemeName : "",
                            });
                        }
                    });
                }
            }
        }, {
            field : 'schemeName',
            title : 'T_EMC_REPORT_TMPL_LINE.SCHEMENAME',
            type : "select",
            category : 'monitorScheme',
            onChange : function(field, value) {
                var _form = this;
                if (!Gikam.isEmpty(value)) {
                    var url = Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls/{tmplId}/schemes/queries', {
                        tmplId : _form.getField('monitorTmplId').value
                    });
                    Gikam.post(url, Gikam.getJsonWrapper({
                        f : {
                            schemeName_SEQ : value,
                        }
                    })).done(function(items) {
                        _form.setSelectOptions('testId', items.rows.map(function(item) {
                            return {
                                text : item.ext$.testname,
                                value : item.testMethodId
                            };
                        }), true);
                        if (items.rows.length > 0) {
                            _form.setData({
                                testId : items.rows[0].testMethodId
                            });
                        } else {
                            _form.setData({
                                testId : "",
                            });
                        }
                    });
                }
            }
        }, {
            field : 'testId',
            title : 'T_EMC_REPORT_TMPL_LINE.TESTID',
            type : "select",
            category : 'testMethod',
            onChange : function(field, value) {
                var _form = this;
                if (!Gikam.isEmpty(value)) {
                    var url = IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/tests/items/queries/selectable';
                    url += Gikam.param({
                        testMethodId : value
                    });
                    Gikam.getJson(url).done(function(items) {
                        _form.setSelectOptions('itemId', items.map(function(item) {
                            return {
                                text : item.itemName,
                                value : item.id
                            };
                        }), true);
                        if (items.length > 0) {
                            _form.setData({
                                itemId : items[0].id
                            });
                        } else {
                            _form.setData({
                                itemId : "",
                            });
                        }
                    });
                }
            }
        }, {
            field : 'itemId',
            title : 'T_EMC_REPORT_TMPL_LINE.ITEMID',
            type : "select",
            category : 'testItem',
        } ];
    },

    getLineEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_REPORT_TMPL_LINE.ORDERNO',
            type : "number",
            editor : true,
            hasArrow : true,
        }, {
            field : 'reportName',
            title : 'T_EMC_REPORT_TMPL_LINE.REPORTNAME',
            editor : true,
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_REPORT_TMPL_LINE.MONITORTMPLID'
        }, {
            field : 'schemeName',
            title : 'T_EMC_REPORT_TMPL_LINE.SCHEMENAME'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_REPORT_TMPL_LINE.TESTID'
        }, {
            field : 'ext$.itemname',
            title : 'T_EMC_REPORT_TMPL_LINE.ITEMID'
        }, {
            field : 'itemAliasName',
            title : 'T_EMC_REPORT_TMPL_LINE.ITEMALIASNAME',
            editor : true
        } ];
    },
});
