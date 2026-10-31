var emcDataQuery = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/data-queries',

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'registDate',
            title : 'T_EMC_PROJECT.REGISTDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        } ];
    },

    // 数据查询
    getBaseInfoFormFields : function() {
        return [ {
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
        }, {
            field : 'beginDate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
            type : 'date',
            validators : [ 'notEmpty' ],
        }, {
            field : 'endDate',
            title : '~',
            type : 'date',
            validators : [ 'notEmpty' ],
        }, {
            field : 'bizCategoryName',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYNAME',
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
            field : 'bizCategoryCode',
            title : 'T_EMC_BIZ_CATEGORY.BIZCATEGORYID',
            readonly : true
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME',
            type : 'choose',
            category : 'monitorPoint',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var monitorPointNo = '';
                    var monitorPointName = '';
                    Gikam.each(rows, function(i, item) {
                        monitorPointNo += ',' + rows[i].monitorPointNo;
                        monitorPointName += ',' + rows[i].monitorPointName;
                    });
                    monitorPointNo = monitorPointNo.substring(1);
                    monitorPointName = monitorPointName.substring(1);
                    this.setData({
                        monitorPointNo : monitorPointNo,
                        monitorPointName : monitorPointName
                    });
                }
            },
            onClean : function() {
                this.setData({
                    monitorPointNo : '',
                    monitorPointName : ''
                });
            }
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNO',
            readonly : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            validators : [ 'notEmpty' ],
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER_TASK_RESULT.ORDERNO',
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORGNAME',
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORGNAME',
        } ];
    },

});
