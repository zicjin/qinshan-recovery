var emcMonitorTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/monitor-tmpls',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_MONITOR_TMPL.ORDERNO',
            type : 'number',
            editor : true,
            width : 80,
        }, Gikam.status.getActiveColumn(), {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_TMPL.MONITORTMPLGROUP',
            type : 'choose',
            category : 'monitorTmplGroup',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        monitorTmplGroupId : rows[0].id,
                        ext$ : {
                            monitortmplgroup : rows[0].monitorTmplGroup,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    monitorTmplGroupId : '',
                    ext$ : {
                        monitortmplgroup : '',
                    }
                });
            },
            editor : true
        }, {
            field : 'monitorTmplName',
            title : 'T_EMC_MONITOR_TMPL.MONITORTMPLNAME',
            editor : true
        }, {
            field : 'id',
            title : 'T_EMC_MONITOR_TMPL.ID',
            width : 120,
        }, {
            field : 'cycle',
            title : 'T_EMC_MONITOR_TMPL.CYCLE',
            editor : true,
            width : 60,
        }, {
            field : 'frequency',
            title : 'T_EMC_MONITOR_TMPL.FREQUENCY',
            editor : true,
            width : 60,
        }, ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_TMPL.MONITORTMPLGROUP',
            type : 'choose',
            category : 'monitorTmplGroup',
        }, {
            field : 'id',
            title : 'T_EMC_MONITOR_TMPL.ID'
        }, {
            field : 'monitorTmplName',
            title : 'T_EMC_MONITOR_TMPL.MONITORTMPLNAME'
        }, ];
    },

    getSchemeListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            type : 'link',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
        }, {
            field : 'testId',
            title : 'T_EMC_MONITOR_SCHEME.TESTID',
            width : 90,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MONITOR_SCHEME.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_MONITOR_SCHEME.ITEMGROUP'
        }, {
            field : 'sortNum',
            title : 'T_EMC_MONITOR_SCHEME.SORTNUM',
            editor : true,
            type : 'number',
            hasArrow : true,
            range : '[1, 1000]'
        }, {
            field : 'ext$.defaultflag',
            title : 'T_EMC_MONITOR_SCHEME.DEFAULTFLAG',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'ext$.enableflag',
            title : 'T_EMC_MONITOR_SCHEME.ENABLEFLAG',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'period',
            title : 'T_EMC_MONITOR_SCHEME.PERIOD',
            type : 'number',
            editor : true,
            onBeforeEditorRender : function(row) {
                return row.schemeName === 'default';
            },
        }, {
            field : 'techPrice',
            title : 'T_EMC_MONITOR_SCHEME.TECHPRICE',
            editor : true,
            onBeforeEditorRender : function(row) {
                return row.schemeName === 'default';
            },
        }, {
            field : 'salePrice',
            title : 'T_EMC_MONITOR_SCHEME.SALEPRICE',
            editor : true,
            onBeforeEditorRender : function(row) {
                return row.schemeName === 'default';
            },
        }, {
            field : 'samplingCost',
            title : 'T_EMC_MONITOR_SCHEME.SAMPLINGCOST',
            formatter : function(i, v, row) {
                return Gikam.printf('<a href="javascript:;" onclick="emcMonitorTmpl.editPage.loadSamplingCostPage(\'{id}\')">{name}</a>', {
                    id : row.id,
                    name : '采样费用'
                });
            }
        }, {
            field : 'skuGbId',
            title : 'SKU编号',
            formatter : function (index, value, row) {
                if (value) {
                    return row.skuGbId + '';
                }
                return value;
            }
        } ];
    },

    getSchemeFormFields : function() {
        var _this = this;

        return [ {
            field : 'testCategory',
            title : '测试类型',
            type : 'select',
            search : true,
            category : 'testCategory',
            onChange : function(name, value) {
                var leftGrid = Gikam.getComp('emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame').leftGrid;

                leftGrid.setOptions({
                    requestData : {
                        testCategoryId_SEQ : value
                    }
                });

                leftGrid.refresh();
            }
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            validators : [ 'notEmpty', 'strLength[0,40]' ]
        } ];
    },

    getSchemeFormEditFields : function(param) {
        var _this = this;

        return [ {
            field : 'testCategory',
            title : '测试类型',
            type : 'select',
            search : true,
            category : 'testCategory',
            onChange : function(name, value) {
                var leftGrid = Gikam.getComp('emc-monitor-tmpl-scheme-addition-scheme-choose-mult-list-shuttle-frame').leftGrid;

                leftGrid.setOptions({
                    requestData : {
                        testCategoryId_SEQ : value
                    }
                });

                leftGrid.refresh();
            }
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            onChange : function(field, value, index) {
                var saveData = {
                    id : param.monitorSchemeId,
                    schemeName : value,
                };

                Gikam.getLastModal().window.showMask(true);
                Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcMonitorSchemeServiceImpl', [saveData] ])).done(function(id) {
                }).always(function() {
                    Gikam.getLastModal().window.closeMask();
                });
            },
            validators : [ 'notEmpty', 'strLength[0,40]' ]
        } ];
    },

    getSchemeAdditionSimpleFormFields : function() {
        var _this = this;
        return [ {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            validators : [ 'notEmpty', 'strLength[0,21]' ],
            readonly : true,
        } ];
    },

    getTestMethodChooseListColumns : function() {
        return [ {
            field : 'ext$.testid',
            width : 95,
            title : 'T_EMC_MONITOR_SCHEME.TESTID'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MONITOR_SCHEME.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field : 'itemGroup',
            width : 100,
            title : 'T_EMC_MONITOR_SCHEME.ITEMGROUP'
        } ];
    },

    getSchemeChooseListColumns : function() {
        return [ {
            field : 'ext$.monitortmplgroup',
            title : 'T_EMC_MONITOR_TMPL_GROUP.MONITORTMPLGROUP'
        }, {
            field : 'ext$.monitortmplname',
            title : 'T_EMC_MPC_TMPL.MONITORTMPLNAME'
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_MONITOR_SCHEME.ITEMGROUP'
        } ];
    },

    getSchemeMethodChooseListColumns : function() {
        return [ {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_METHOD_VERSION.TESTMETHODBASIS'
        } ];
    },

    getItemGroupShuttleFrameColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testmethodtype',
            title : 'T_EMC_TEST_METHOD_TESTMETHODTYPE',
            type : 'select',
            search : false,
            category : 'sys',
            param : {
                codeCategoryId : 'testMethodType',
            }
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_PROJ_ORDER.TESTNAME',
            width : 100
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_PROJ_ORDER.METHODNAME'
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_PROJ_ORDER.TESTMETHODBASIS'
        }, {
            field : 'itemGroup',
            title : 'T_EMC_PROJ_ORDER.ITEMGROUP'
        } ];
    },

    getSchemeTestMethodChooseListColumns : function() {
        return [ {
            field : 'testId',
            title : 'T_EMC_MONITOR_SCHEME.TESTID'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_INDEX_TEST_ITEM.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_SCHEME.METHODNAME'
        } ];
    },

    getOrderCategoryChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MONITOR_TMPL.ORDERCATEGORYID'
        }, {
            field : 'monitorTmplName',
            title : 'T_EMC_MONITOR_TMPL.ORDERCATEGORYNAME'
        }, ];
    },

    getSchemeCostEditListColumns : function () {
        return [{
            checkbox : true
        }, {
            field : 'sortNum',
            title : 'T_EMC_MONITOR_SCHEME_COST.SORTNUM',
            type : 'number',
            editor : true,
            width : 80,
        }, {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME_COST.SCHEMENAME',
        }, {
            field : 'period',
            title : 'T_EMC_MONITOR_SCHEME_COST.PERIOD',
            type : 'number',
            editor : true,
        }, {
            field : 'techPrice',
            title : 'T_EMC_MONITOR_SCHEME_COST.TECHPRICE',
            editor : true,
        }, {
            field : 'salePrice',
            title : 'T_EMC_MONITOR_SCHEME_COST.SALEPRICE',
            editor : true,
        }, {
            field : 'multipleFactor',
            title : 'T_EMC_MONITOR_SCHEME_COST.MULTIPLEFACTOR',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'samplingFeeType',
            title : 'T_EMC_MONITOR_SCHEME_COST.SAMPLINGFEETYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplingFeeType',
            },
        }, {
            field : 'analyzeFeeType',
            title : 'T_EMC_MONITOR_SCHEME_COST.ANALYZEFEETYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'analyzeFeeType',
            },
        }, {
            field : 'analyzeFeeFactor',
            title : 'T_EMC_MONITOR_SCHEME_COST.ANALYZEFEEFACTOR',
            editor : true,
        }, {
            field : 'analyzeFeeMaximum',
            title : 'T_EMC_MONITOR_SCHEME_COST.ANALYZEFEEMAXIMUM',
            editor : true,
        }, {
            field : 'ext$.isenable',
            title : 'T_EMC_MONITOR_SCHEME.ISENABLE',
            type : 'select',
            search : true,
            editor : false,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'skuGbId',
            title : 'SKU编号',
            formatter : function (index, value, row) {
                console.info(index, value, row)
                if (value) {
                    return row.skuGbId + '';
                }
                return value;
            }
        }]
    },

    getBatchInputFields : function() {
        return [  {
            field : 'ext$.defaultflag',
            title : 'T_EMC_MONITOR_SCHEME.DEFAULTFLAG',
            type : 'select',
            search : true,
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'period',
            title : 'T_EMC_MONITOR_SCHEME.PERIOD',
            type : 'number',
            editor : true,
        }, {
            field : 'techPrice',
            title : 'T_EMC_MONITOR_SCHEME.TECHPRICE',
            editor : true,
        }, {
            field : 'salePrice',
            title : 'T_EMC_MONITOR_SCHEME.SALEPRICE',
            editor : true,
        }];
    },

    getSchemeNameFields : function() {
        return [ {
            field : 'schemeName',
            title : 'T_EMC_MONITOR_SCHEME.SCHEMENAME',
            editor : true,
        }];
    },

    getSamplingCostListColumns : function() {
        return [ {
            field : 'samplingType',
            title : 'T_EMC_MONITOR_SCHEME_SAMPLING_COST.SAMPLINGTYPE',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplingCostType',
            },
        }, {
            field : 'samplingCost',
            title : 'T_EMC_MONITOR_SCHEME_SAMPLING_COST.SAMPLINGCOST',
            editor : true,
        }, {
            field : 'defaultCost',
            title : 'T_EMC_MONITOR_SCHEME_SAMPLING_COST.DEFAULTCOST',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        } ];
    },

});
