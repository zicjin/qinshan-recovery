var emcSubResult = Gikam.getPageObject({

    getProjectListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 120,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            width : 100,
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'status',
            title : 'T_EMC_PROJECT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplingorcancel'
            },
            width : 100,
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
        }, ];
    },

    getFolderColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_FOLDER.ORDERCATEGORY'
        }, {
            field : 'folderNo',
            title : 'T_EMC_FOLDER.FOLDERNO',
            width : 120,
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME'
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 100,
        }, {
            field : 'planSampleDate',
            title : 'T_EMC_FOLDER.PLANSAMPLEDATE'
        }, {
            field : 'sampleOrgName',
            title : 'T_EMC_FOLDER.SAMPLEORGNAME'
        }, {
            field : 'sceneUserName',
            title : 'T_EMC_FOLDER.SCENEUSERNAME'
        }, {
            field : 'sampleUserName',
            title : 'T_EMC_FOLDER.SAMPLEUSERNAME'
        }, {
            field : 'longitude',
            title : 'T_EMC_FOLDER.LONGITUDE'
        }, {
            field : 'latitude',
            title : 'T_EMC_FOLDER.LATITUDE'
        }, {
            field : 'monitorPointCategory',
            title : 'T_EMC_FOLDER.MONITORPOINTCATEGORY'
        }, {
            field : 'remark',
            title : 'T_EMC_ORDER.REMARK'
        }, ];
    },

    getOrderTaskResultColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'revisionRule',
            title : 'T_EMC_ORDER_TASK_RESULT.REVISIONRULE',
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            validators : [ 'strLength[0,30]' ],
            onAfterChoose : function(back, index) {
                if (back) {
                    this.setData({
                        index : index,
                        'revisionRule' : back
                    });
                    var dataList = [ null, [ '', [ this.getData()[index] ] ] ];
                    var url = '/secure/emc/module/bp/bp/order-task-results/action/change-result';
                    workspace.window.showMask();
                    var id = this.getData()[index].id;
                    Gikam.put(url, Gikam.getJsonWrapper.apply(null, dataList)).done(function() {
                        Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refresh();
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            },
        }, {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
            editor : true,
            width : 120,
            onBeforeEditorRender : function(row) {
                if (!Gikam.isEmpty(row.script)) {
                    return false;
                }
                return true;
            },
            onFocus : function(field, value, index) {
                var _this = this;
                var data = _this.getData()[index];
                if ('select' === data.resultCategory) {
                    Gikam.create('modal', {
                        width : 400,
                        height : 300,
                        title : Gikam.choose.getConfig('itemResult').title,
                        url : Gikam.choose.getConfig('itemResult').url + Gikam.param({
                            single : true,
                            itemId_SEQ : data.itemId,
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }
                            Gikam.getComp('emc-order-task-result-edit-list-grid').setData({
                                index : index,
                                originalResult : rows[0].result,
                                reportResult : rows[0].result,
                            });
                        }
                    });
                }
            },
            onChange : function(field, value, index) {
                var data = this.getData()[index];
                if ('number' === data.resultCategory) {
                    var forbidden = false;
                    if (Gikam.isNotEmpty(value)) {
                        var regF = /^(-?\d+)(\.\d+)?$/;
                        if (value.indexOf('0') == 0 && value.length > 1 && value.indexOf('0.') == -1) {
                            forbidden = true;
                            Gikam.alert(value + '格式错误！请输入正确格式数值！')
                            this.setData({
                                index : index,
                                originalResult : '',
                            });
                            return;
                        }
                        if ((value.indexOf('-0') == 0 && value.length == 2) || (value.indexOf('-0.') == 0 && value.length == 3)) {
                            forbidden = true;
                            Gikam.alert(value + '格式错误！请输入正确格式数值！')
                            this.setData({
                                index : index,
                                originalResult : '',
                            });
                            return;
                        }
                        if (!regF.test(value)) {
                            forbidden = true;
                            Gikam.alert(value + '格式错误！请输入正确格式数值！')
                            this.setData({
                                index : index,
                                originalResult : '',
                            });
                            return;
                        }
                    }
                    if (!forbidden) {
                        this.setData({
                            index : index,
                            originalResult : value,
                        });
                        /*var dataList = [ null, [ '', [ this.getData()[index] ] ] ];
                        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/action/change-result';
                        workspace.window.showMask();
                        Gikam.put(url, Gikam.getJsonWrapper.apply(null, dataList)).done(function() {
                            Gikam.getComp('emc-sub-result-edit-list-order-task-result-grid').refreshRowByIndex(index);
                        }).always(function() {
                            workspace.window.closeMask();
                        });*/
                    }
                } else if ('char' === data.resultCategory || Gikam.isEmpty(data.resultCategory)) {
                    this.setData({
                        index : index,
                        originalResult : value,
                        reportResult : value,
                    });
                }
            }
        }, {
            field : 'reportResult',
            title : 'T_EMC_ORDER_TASK_RESULT.REPORTRESULT',
            width : 120,
            formatter : function(i, v ,r) {
                return Gikam.scientificNotation(v);
            }
        }, {
            field : 'lowLimit',
            title : 'T_EMC_ORDER_TASK_RESULT.LOWLIMIT',
            editor : true,
            width : 120,
        }, {
            field : 'unitName',
            title : 'T_EMC_ORDER_TASK_RESULT.UNITNAME',
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        unitName : rows[0].unitName,
                    });
                }
            },
            editor : true,
            width : 120,
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.SHOWINREPORT',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            width : 120,
        }, {
            field : 'resultStatus',
            title : 'T_EMC_ORDER_TASK_RESULT.RESULTSTATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'resultinputstatus'
            },
            editor : false,
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_TEST_METHOD.TESTNAME',
        }, {
            field : 'itemName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMNAME'
        }, {
            field: 'itemAliasName',
            title : 'T_EMC_ORDER_TASK_RESULT.ITEMALIASNAME'
        },{
            field : 'ext$.orderdesc',
            title : 'T_EMC_ORDER.ORDERDESC',
        }, {
            field : 'cycle',
            title : 'T_EMC_ORDER.CYCLE',
            width : 100,
        }, {
            field : 'frequency',
            title : 'T_EMC_ORDER.FREQUENCY',
            width : 100,
        }, {
            field : 'ext$.samplenum',
            title : 'T_EMC_ORDER.SAMPLENUM',
            width : 100,
        }, {
            field : 'repeatNo',
            title : 'T_EMC_ORDER_TASK_RESULT.REPEATNO',
            width : 130,
        }, {
            field : 'ext$.remark',
            title : 'T_EMC_ORDER.REMARK',
        } ];
    },

    getBatchInputFields : function () {
        return [ {
            field : 'originalResult',
            title : 'T_EMC_ORDER_TASK_RESULT.ORIGINALRESULT',
        }, {
            field : 'reportDisplay',
            title : 'T_EMC_ORDER_TASK_RESULT.SHOWINREPORT',
            type : 'select',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
        } ]
    }

});
