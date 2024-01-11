var emcOrderQuery = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/order-queries',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.SERVICE_NAME'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
        }, {
            field : 'ext$.orderstatus',
            title : 'T_EMC_ORDER.STATUS',
            width : 100,
            styleFormatter : function(row) {
                var backgroundColor = 'red'
                if (row.ext$.orderstatus === '1') {
                    backgroundColor = 'palegreen';
                }

                return {
                    backgroundColor : backgroundColor
                }
            },
            formatter : function(i, v) {
                if ('1' === v) {
                    return Gikam.propI18N('T_EMC_ORDER.DONE');
                } else {
                    return Gikam.propI18N('T_EMC_ORDER.CHECKING');
                }
            }
        }, {
            field : 'orderCategory',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORY',
        } ];
    },

    // 样品数据查询表单
    getQueryFormFields : function() {
        return [ {
            field : 'beginDate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            type : 'date'
        }, {
            field : 'endDate',
            title : '~',
            type : 'date',
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'choose',
            category : 'bizCategory',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var bizCategoryName = '';
                    var bizCategoryCode = '';
                    Gikam.each(rows, function(i, item) {
                        bizCategoryName += ',' + rows[i].bizCategoryName;
                        bizCategoryCode += ',' + rows[i].bizCategoryId;
                    });
                    bizCategoryName = bizCategoryName.substring(1);
                    bizCategoryCode = bizCategoryCode.substring(1);
                    this.setData({
                        bizCategoryName : bizCategoryName,
                        bizCategoryCode : bizCategoryCode,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    bizCategoryName : '',
                    bizCategoryCode : '',
                });
            }
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG',
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG',
        }, {
            field : 'bizCategoryCode',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYID',
            readonly : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.SERVICE_NAME',
        }, {
            field : 'sampleCategory',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORY',
            type : 'choose',
            category : 'sampleCategory',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleCategory = '';
                    Gikam.each(rows, function(i, item) {
                        sampleCategory += ',' + rows[i].monitorTmplName;
                    });
                    sampleCategory = sampleCategory.substring(1);
                    this.setData({
                        sampleCategory : sampleCategory
                    });
                }
            },
            onClean : function() {
                this.setData({
                    sampleCategory : ''
                });
            }
        } ];
    },

    // 样品基本信息
    getBaseInfoFormFields : function () {
        return [ {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
            readonly : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_MONITOR_TMPL.SAMPLECATEGORY',
            readonly : true
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.SERVICE_NAME',
            readonly : true
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            readonly : true
        }, {
            field : 'ext$.sampleusername',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME',
            readonly : true
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_PROJECT.ENTRUSTORG',
            readonly : true
        }, {
            field : 'ext$.orderstatus',
            title : 'T_EMC_ORDER.STATUS',
            readonly : true
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG',
            readonly : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        } ];
    },

    // 监测数据
    getResultListColumns : function () {
        return [ {
            field : 'ext$.testname',
            title : 'T_EMC_ORDER_TASK.TESTMETHOD',
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_ORDER_TASK.METHOD',
        }, {
            field : 'ext$.testmethodbasis',
            title : 'T_EMC_ORDER_TASK.METHODDESC',
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
        }, {
            field : 'ext$.analysisusername',
            title : 'T_EMC_ORDER_TASK_RESULT.ANALYSISUSER',
        }, {
            field : 'ext$.checkusername',
            title : 'T_EMC_ORDER_TASK_RESULT.AUDITUSER',
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.STATUS',
            width : 80,
            styleFormatter : function(row) {
                var backgroundColor = 'red'
                if (row.resultStatus === 'Done' || row.resultStatus === 'OOS') {
                    backgroundColor = 'palegreen';
                }

                return {
                    backgroundColor : backgroundColor
                }
            },
            formatter : function(i, v, r) {
                if ('Done' === v || 'OOS' === v) {
                    return Gikam.propI18N('T_EMC_ORDER_TASK_RESULT.TESTED');
                } else {
                    return Gikam.propI18N('T_EMC_ORDER_TASK_RESULT.TESTING');
                }
            }
        } ];
    }
});
