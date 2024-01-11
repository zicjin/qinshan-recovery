emcReport.updateIndexPage = {
    // 检测样品检测表
    getOrderTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-task-update-index-list-order-task-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries',
            requestData : {
                FOLDERID_IN : _this.param.ids,
                TESTCAPCITYORGID_SEQ : workspace.user.ext$.parentorgid,
                analysisType_SEQ : 'analysis',
            },
            columns : emcReport.getUpdateIndexOrderTaskColumns(),
            service : 'emcOrderTaskServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-order-task-update-index-list-order-task-grid').activeRowByIndex(0);
                } else if (Gikam.getComp('emc-order-task-update-index-list-order-task-result-grid')) {
                    Gikam.getComp('emc-order-task-update-index-list-order-task-result-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-order-task-update-index-list-order-task-result-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf('/secure/emc/module/bp/bp/order-tasks/{orderTaskId}/results/distinct/queries', {
                            orderTaskId : row.id
                        }),
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.REPORT.REPORTS.BUTTON.CLEAR_SELECTED_INDEX',
                icon : 'edit',
                onClick : function() {
                    _this.clearOrderIndex();
                }
            } ]
        };
    },

    // 检测分析项表
    getOrderTaskResultGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-task-update-index-list-order-task-result-grid',
            columns : emcReport.getUpdateIndexOrderTaskResultColumns(),
            service : 'emcOrderTaskResultServiceImpl'
        };
    },

    // 指标类别下拉框
    getOrderIndexSelectForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-order-task-update-index-list-select-form',
            columns : 1,
            titleWidthAuto : true,
            fields : [ {
                title : 'EMC.MODULE.BP.REPORT.REPORTS.TITLE.INDEX_CATEGORY',
                field : 'indexCategoryId',
                type : 'select',
                search : true,
                onChange : function(field, value) {
                    if (Gikam.isEmpty(value)) {
                        Gikam.getComp('emc-order-task-update-index-list-order-index-grid').cleanData();
                        _this.onDynamicGeneratIndexSetField();
                    } else {
                        Gikam.getAsyncComp('emc-order-task-update-index-list-order-index-grid').done(function(grid) {
                            grid.refresh({
                                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/{indexCategoryId}/indexs/queries', {
                                    indexCategoryId : value
                                }),
                            });
                        });
                    }
                }
            } ]
        };
    },

    // 指标表
    getOrderIndexGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-task-update-index-list-order-index-grid',
            columns : emcReport.getUpdateIndexOrderIndexColumns(),
            service : 'emcOrderIndexServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-order-task-update-index-list-order-index-grid').activeRowByIndex(0);
                } else if (Gikam.getComp('emc-order-task-update-index-list-index-test-item-grid')) {
                    _this.onDynamicGeneratIndexSetField();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-order-task-update-index-list-index-set-grid').done(function(grid) {
                    var selectForm = Gikam.getComp('emc-order-task-update-index-list-select-form');
                    var indexCategoryId = selectForm.getData().indexCategoryId;
                    Gikam.getJson('/secure/emc/module/mdm/bizmdm/index-categories/' + indexCategoryId).done(function(indexCategory) {
                        _this.onDynamicGeneratIndexSetField(indexCategory.propertyTmplId, row);
                    });
                });
            },
            toolbar : [ this.getOrderIndexSelectForm(), {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'edit',
                onClick : function() {
                    _this.updateOrderIndex();
                }
            } ]
        };
    },

    // 动态生成指标分析项表的列和数据
    onDynamicGeneratIndexSetField : function(propertyTmplId, row) {
        var _this = this;
        var targetComp = Gikam.getComp('emc-order-task-update-index-list-index-set-grid')
        if (!Gikam.isEmpty(propertyTmplId) && row) {
            // 根据标准设置，查询属性设置字段信息
            Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/index-sets/' + propertyTmplId).done(function(columns) {
                var addColumns = columns.rows.map(function(items) {
                    if (items.fieldType == 'fieldMultiSelect') {
                        var selectFields = {
                            field : 'ext$.' + items.internalField,
                            title : items.fieldName,
                            editor : true,
                        };
                        selectFields['param'] = {
                            codeCategoryId : items.sysCode
                        };
                        if (items.required == '1') {
                            selectFields['validators'] = [ 'notEmpty' ];
                        }
                        return selectFields;
                    } else if (items.fieldType == 'fieldSelect') {
                        var selectFields = {
                            field : 'ext$.' + items.internalField,
                            title : items.fieldName,
                            editor : true,
                        };
                        selectFields['param'] = {
                            codeCategoryId : items.sysCode
                        };
                        if (items.required == '1') {
                            selectFields['validators'] = [ 'notEmpty' ];
                        }
                        return selectFields;
                    } else if (items.fieldType == 'fieldContext') {
                        var contextFields = {
                            field : 'ext$.' + items.internalField,
                            title : items.fieldName,
                            validators : [],
                            editor : true,
                        };
                        if (items.required == '1') {
                            contextFields.validators.push('notEmpty');
                        }
                        if (items.fieldLength) {
                            contextFields.validators.push('strLength[0,' + items.fieldLength + ']');
                        }
                        if (items.defaultValue) {
                            contextFields['value'] = items.defaultValue;
                        }
                        return contextFields;
                    } else if (items.fieldType == 'fieldContextArea') {
                        var contextAreaFields = {
                            field : 'ext$.' + items.internalField,
                            title : items.fieldName,
                            validators : [],
                            editor : true,
                        };
                        if (items.required == '1') {
                            contextAreaFields.validators.push('notEmpty');
                        }
                        if (items.fieldLength) {
                            contextAreaFields.validators.push('strLength[0,' + items.fieldLength + ']');
                        }
                        return contextAreaFields;
                    } else if (items.fieldType == 'fieldDate') {
                        var dateFields = {
                            field : 'ext$.' + items.internalField,
                            title : items.fieldName,
                            type : 'date',
                            editor : true,
                        };
                        if (items.required == '1') {
                            dateFields['validators'] = [ 'notEmpty' ];
                        }
                        return dateFields;
                    }
                })
                var targetColumns = emcReport.getUpdateIndexIndexSetColumns().concat(addColumns);
                targetComp.refreshColumns(targetColumns);

                _this.propertyDataColumns = addColumns;

                // 指标设置数据
                Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/order-indexes/{orderIndexId}/index-sets/queries', {
                    orderIndexId : row.id
                })).done(function(data) {
                    targetComp.refreshByData(data);
                });
            });
        } else if(targetComp) {
            targetComp.cleanData();
        }
    },

    // 指标分析项表
    getIndexSetGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-task-update-index-list-index-set-grid',
            columns : emcReport.getUpdateIndexIndexSetColumns(),
            service : 'emcIndexSetServiceImpl'
        };
    },

    // 指标分析项表
    getUpdateIndexIndexSetColumns : function() {
        var columns = [];
        columns.push({
            checkbox : true
        }, {
            field : 'evalIndex',
            title : 'T_EMC_INDEX_SET.EVALINDEX',
            editor : true,
        }, {
            field : 'ext$.indexItemName',
            title : 'T_EMC_INDEX_SET.INDEXITEMID',
            type : 'choose',
            category : 'indexItem',
            editor : true,
            onAfterChoose : function(rows) {
                if (rows) {
                    this.setData({
                        indexItemId : rows[0].id,
                        ext$ : {
                            indexItemName : rows[0].indexItemName,
                        }
                    });
                }
            },
            onClean : function() {
                this.setData({
                    indexItemId : '',
                    ext$ : {
                        indexItemName : '',
                    }
                });
            },
        });

        columns.push({
            field : 'ext$.fieldname',
            title : 'T_EMC_PROPERTY_FIELD.FIELDNAME',
        });

        return columns;
    },
    
    // 更换指标
    updateOrderIndex : function() {
        var activeRow = Gikam.getComp('emc-order-task-update-index-list-order-index-grid').getActivedRow();
        if (Gikam.isEmpty(activeRow)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        
        // 更新检测样品测试表的指标信息
        var taskSelections = Gikam.getComp('emc-order-task-update-index-list-order-task-grid').getSelections();
        if(Gikam.isEmpty(taskSelections)) {
            Gikam.alert('EMC.MODULE.BP.REPORT.REPORTS.TIP.SELECT_ORDER_TASK');
            return;
        }
        taskSelections.map(function(item) {
            item.indexCategoryId = activeRow.indexCategoryId;
            item.indexId = activeRow.id;
        });
        Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/' + taskSelections[0].id, Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', taskSelections ])).done(function() {
            Gikam.getComp('emc-order-task-update-index-list-order-task-grid').refresh();
        });
        
        // 用指标分析项表的高低限去更新检测分析项表中同一项目的高低限
        var indexSetData = Gikam.getComp('emc-order-task-update-index-list-index-set-grid').getData();
        if(Gikam.isEmpty(indexSetData)){
            return;
        }
        workspace.window.showMask(true);
        var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/results/action/update-index'
        Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', taskSelections ], [ 'emcIndexSetServiceImpl', indexSetData ])).done(function() {
            Gikam.getComp('emc-order-task-update-index-list-order-task-grid').refresh();
            Gikam.getComp('emc-order-task-update-index-list-order-task-result-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },
    
    // 清空指标
    clearOrderIndex : function() {
        Gikam.confirm('EMC.MODULE.BP.REPORT.REPORTS.TIP.CONFIRM_TO_CLEAN', 'EMC.MODULE.BP.REPORT.REPORTS.TIP.CLEAN_INDEX', function() {
         // 清空检测样品测试表的指标信息
            var taskSelections = Gikam.getComp('emc-order-task-update-index-list-order-task-grid').getSelections();
            if (taskSelections.length > 0) {
                taskSelections.map(function(item) {
                    item.indexCategoryId = '';
                    item.indexId = '';
                });
                
                workspace.window.showMask(true);
                Gikam.put(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/' + taskSelections[0].id, Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', taskSelections ])).done(function() {
                    Gikam.getComp('emc-order-task-update-index-list-order-task-grid').refresh();
                }).always(function() {
                    workspace.window.closeMask();
                });
            } else {
                Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            }
            
            // 清空检测分析项表的高低限
            var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/results/action/clean-index'
            workspace.window.showMask(true);
            Gikam.post(url, Gikam.getJsonWrapper({}, [ 'emcOrderTaskServiceImpl', taskSelections ])).done(function() {
                Gikam.getComp('emc-order-task-update-index-list-order-task-result-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },
    
    // 加载指标类别下拉选项数据
    loadIndexCategory : function() {
        var _this = this;
        Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/queries', Gikam.getJsonWrapper({
            f : {
                activatedFlag_SEQ : '1',
                target_filter : JSON.stringify({
                    type : 'assigned',
                    targetTable : "T_EMC_MONITOR_TMPL",
                    targetMatchColumn : "MONITORTMPLGROUPID",
                    thisMatchColumn : "MONITORTMPLGROUPID",
                    filter : [ {
                        targetFilterColumn : 'ID',
                        targetFilterValue : _this.param.monitorTmplId
                    } ]
                })
            },
            n : -1,
            s : 50
        })).done(function(r) {
            Gikam.getAsyncComp('emc-order-task-update-index-list-select-form').done(function(form) {
                //设置下拉选项
                _this.setFormSelectFieldItems(form, 'indexCategoryId', r.rows, 'id', 'stdName');
            });
        }); 
    },
    
    setFormSelectFieldItems(form, field, data, valueCol, textCol){
        var items = [];
        if(data) {
            items = data.filter(function(item) {
                return !Gikam.isEmpty(item[textCol]);
            }).map(function(item) {
                return {
                    value : item[valueCol],
                    text : item[textCol]
                };
            });
        }
        form.setSelectOptions(field, items);
    },
    
    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : '50%',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_TASK',
                                items : [ this.getOrderTaskGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_TASK_RESULT',
                                items : [ this.getOrderTaskResultGridParam() ]
                            } ]
                        } ]
                    }
                } ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '50%',
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.ORDER_INDEX',
                                items : [ this.getOrderIndexGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.REPORT.REPORTS.TAB.INDEX_TEST_ITEM',
                                items : [ this.getIndexSetGridParam() ]
                            } ]
                        } ]
                    }
                } ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
    },

    init : function(param) {
        this.param = param;
        this.createPage();
        // 加载指标类别下拉选项数据
        this.loadIndexCategory();
    }
};
