emcProjScheme.updateIndexPage = {

    getProjSchemeGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-proj-scheme-grid',
            url : emcProjScheme.baseUrl + '/queries/raw',
            columns : emcProjScheme.getUpdateIndexSchemeListColumns(),
            service : 'emcProjSchemeServiceImpl',
            checkOnActive : true,
            editor : false,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(1);
                } else {
                    Gikam.getComp('emc-update-index-proj-order-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onSchemeActive(row);
            },
            onRendered : function() {
                this.hideColumns([ 'ext$.monitortmplname' ]);
            },
            group : {
                fields : [ 'ext$.monitortmplname' ],
                formatter : function(row) {
                    return Gikam.propI18N('T_EMC_PROJ_SCHEME.ORDERCATEGORY') + ':' + row.ext$.monitortmplname;
                }
            },
            requestData : {
                ID_IN : _this.param.ids
            }
        };
    },

    onSchemeActive : function(row) {
        var _this = this;
        if (Gikam.isNumber(row.id)) {
            Gikam.getAsyncComp('emc-update-index-proj-order-grid').done(function(grid) {
                grid.refresh({
                    url : _this.getOrderUrl(row.id)
                });
            });
        }
    },

    getOrderUrl : function(projSchemeId) {
        if (projSchemeId) {
            return Gikam.printf(emcProjScheme.baseUrl + '/{id}/proj-orders/queries', {
                id : projSchemeId
            })
        }
    },

    getProjOrderGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-proj-order-grid',
            url : _this.getOrderUrl(),
            editor : false,
            columns : emcProjScheme.getUpdateIndexOrderColumns(),
            service : 'emcProjOrderServiceImpl',
            onLoadSuccess : function(data) {
            	if(Gikam.isEmpty(data)){
            		return;
            	}
                var url = '/secure/emc/module/mdm/bizmdm/index-categories/queries/selectByProjScheme';
                var allProjSchemes = Gikam.getComp('emc-update-index-proj-scheme-grid').getData();
                var projSchemes = [];
                allProjSchemes.map(function(item) {

                    if( Gikam.isNumber(item.id) ){
                    	projSchemes.push(item);
                    }
                });
                Gikam.post(url, Gikam.getJsonWrapper({}, [ '', projSchemes ])).done(function(items) {
                	Gikam.getComp('emc-update-index-index-category-select-form').setSelectOptions('indexCategoryId', items.rows.map(function(item) {
                        return {
                            text : item.stdName,
                            value : item.id
                        };
                    }), true);
                });
            },
            toolbar : [ {
                type : 'button',
                text : '取消评价',
                icon : 'edit',
                onClick : function() {
                    _this.cancelIndex();
                }
            },{
                type: 'form',
                autoSave: false,
                fields: emcProjScheme.getIndexShowAllField(),
            } ]
        };
    },

    cancelIndex : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-update-index-proj-order-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        Gikam.confirm('取消评价', '是否取消评价', function() {
            var saveData = selections.map(function(row) {
                return {
                    id : row.id,
                    indexId : '',
                    indexCategoryId : ''
                };
            });

            workspace.window.showMask(true);
            Gikam.put('/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcProjOrderServiceImpl', saveData ])).done(function(id) {
                Gikam.getComp('emc-update-index-proj-order-grid').refresh();
            }).always(function() {
                workspace.window.closeMask();
            });
        })
    },

    // 指标类别下拉框
    getOrderIndexSelectForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-update-index-index-category-select-form',
            columns : 1,
            titleWidthAuto : true,
            fields : [ {
                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX_CATEGORY',
                field : 'indexCategoryId',
                type : 'select',
                inputWidth: 300,
                search : true,
                onChange : function(field, value) {
                    if (!Gikam.isEmpty(value)) {
                        Gikam.getAsyncComp('emc-update-index-order-index-grid').done(function(grid) {
                            grid.refresh({
                                url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/{id}/order-indexes/queries', {
                                    id : value
                                }),
                            });
                        });
                    } else {
                        var indexSetGrid = Gikam.getComp('emc-update-index-index-set-grid');
                        if (!Gikam.isEmpty(indexSetGrid)) {
                            indexSetGrid.cleanData();
                        }
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
            id : 'emc-update-index-order-index-grid',
            columns : emcProjScheme.getUpdateIndexOrderIndexColumns(),
            service : 'emcOrderIndexServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-update-index-order-index-grid').activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-update-index-index-set-grid').cleanData();
                }
            },
            // onRowActive : function(index, row) {
            //
            //     Gikam.getAsyncComp('emc-update-index-index-set-grid').done(function(grid) {
            //         grid.refresh({
            //             url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/order-indexes/{indexId}/index-sets/queries', {
            //                 indexId : row.id
            //             }),
            //         });
            //     });
            //
            // },
            onRowActive: function (index, row) {
                _this.onFieldParamActive(row);
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

    onFieldParamActive: function (row) {
        var _this = this;
        if (row) {
            var targetComp = Gikam.getComp('emc-update-index-index-set-grid');
            // var indexCategoryRow = Gikam.getComp('emc-update-index-index-category-select-form').getData();
            if (!Gikam.isEmpty(row.ext$.propertytmplid)) {
                // 根据标准设置，查询属性设置字段信息
                Gikam.getJson('/secure/emc/module/mdm/basemdm/property-tmpls/index-sets/' + row.ext$.propertytmplid).done(function (columns) {
                    var addColumns = columns.rows.map(function (items) {
                        if (items.fieldType == 'fieldMultiSelect') {
                            var selectFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                editor: true,
                            };
                            selectFields['param'] = {
                                codeCategoryId: items.sysCode
                            };
                            if (items.required == '1') {
                                selectFields['validators'] = ['notEmpty'];
                            }
                            return selectFields;
                        } else if (items.fieldType == 'fieldSelect') {
                            var selectFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                editor: true,
                            };
                            selectFields['param'] = {
                                codeCategoryId: items.sysCode
                            };
                            if (items.required == '1') {
                                selectFields['validators'] = ['notEmpty'];
                            }
                            return selectFields;
                        } else if (items.fieldType == 'fieldContext') {
                            var contextFields = {
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                validators: [],
                                editor: true,
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
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                validators: [],
                                editor: true,
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
                                field: 'ext$.' + items.internalField,
                                title: items.fieldName,
                                type: 'date',
                                editor: true,
                            };
                            if (items.required == '1') {
                                dateFields['validators'] = ['notEmpty'];
                            }
                            return dateFields;
                        }
                    })
                    var targetColumns = emcProjScheme.getUpdateIndexIndexSetColumns().concat(addColumns);
                    targetComp.refreshColumns(targetColumns);
                    if (row.ext$.propertytmplid == 89) {
                        targetComp.hideColumns(['indexLowLimit', 'indexHighLimit']);
                    }

                    // 指标设置数据
                    targetComp.refresh({
                        url: IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/order-indexes/' + row.id + '/index-sets/queries'
                    });
                });
            }
        }
    },

    // 指标分析项表
    getIndexSetGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-update-index-index-set-grid',
            // columns : emcProjScheme.getUpdateIndexIndexSetColumns(),
            service : 'emcIndexSetServiceImpl',
            onUpdated: function (row, key) {
                // key 0 是 当前行的 id, key n 是当前修改的列的 field
                // id 对应 propertyData 表的 bizId
                var newKey = key[1].replace("ext$.", "");
                if (key.length === 2) {
                    var saveData = {};
                    saveData['bizId'] = row.id
                    saveData[Object.keys(row.ext$)[0]] = Object.values(row.ext$)[0]
                    workspace.window.showMask(true);
                    Gikam.put('/secure/emc/module/mdm/basemdm/property-tmpls/1', Gikam.getJsonWrapper({}, ['emcPropertyDataServiceImpl', [saveData]])).done(function (id) {
                        Gikam.getComp('emc-update-index-index-set-grid').refresh();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            },
        };
    },

    // 更换指标
    updateOrderIndex : function() {
        var orderIndexData = Gikam.getComp('emc-update-index-order-index-grid').getSelections();
        if (Gikam.isEmpty(orderIndexData)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_INDEX');
            return;
        }

        var projSchemeSelections = Gikam.getComp('emc-update-index-proj-scheme-grid').getSelections();
        var projOrderSelections = Gikam.getComp('emc-update-index-proj-order-grid').getSelections();

        if (Gikam.isEmpty(projSchemeSelections)) {
            Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_FOLDER');
            return;
        }
        if (Gikam.isEmpty(projOrderSelections)) {
            Gikam.alert('至少选择一个分析项组');
            return;
        }

        workspace.window.showMask(true);
        Gikam.postSync(emcProjScheme.baseUrl + '/action/update-order-index', Gikam.getJsonWrapper({},
            [ 'emcProjSchemeServiceImpl', projSchemeSelections ],[ 'emcProjOrdereServiceImpl', projOrderSelections ], [ 'emcOrderIndexServiceImpl', orderIndexData ])).always(function() {
            Gikam.getComp('emc-update-index-proj-scheme-grid').refresh();
            workspace.window.closeMask();
        });


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
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.FOLDER',
                                items : [ this.getProjSchemeGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.SCHEME',
                                items : [ this.getProjOrderGridParam() ]
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
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX',
                                items : [ this.getOrderIndexGridParam() ]
                            } ]
                        } ]
                    },
                    center : {
                        items : [ {
                            type : 'tab',
                            fill : true,
                            panels : [ {
                                title : 'EMC.MODULE.BP.PROJECT.PROJSCHEMES.TITLE.INDEX_ITEM',
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
    }
};
