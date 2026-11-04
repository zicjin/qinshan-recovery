var coreCustomQuery = Gikam.getPageObject({

    menuUrl : IFM_CONTEXT + '/secure/core/module/sys/menus',

    menuIconsList : [],

    getMacthItems : function (category){
        var items = Gikam.jQuery.Deferred();
        var selectItems = [];
        Gikam.select.getItemList({ category : category }).done(
            function (data) {
                for (let rElement of data) {
                    if(!Gikam.isEmpty(rElement.code)){
                        selectItems.push({
                            text : rElement.codeName,
                            value : rElement.code
                        })
                    }
                }
                items.resolve(selectItems);
            }
        )
        return items;
    },

    getDsCategory : function (value) {
        var dsCategory = [];
        Gikam.getJson(IFM_CONTEXT + '/secure/core/module/sys/codes/queries/selectable?codeCategoryId=dsCategory').done(
            function (r) {
                for (let rElement of r) {
                    if(value === 'choose'){
                        if(rElement.code === 'system'){
                            dsCategory.push({
                                text : rElement.codeName,
                                value : rElement.code
                            })
                        }
                    }else {
                        dsCategory.push({
                            text : rElement.codeName,
                            value : rElement.code
                        })
                    }
                }
            }
        )
        return dsCategory;
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'queryName',
            title : 'T_CORE_CUSTOM_QUERY.QUERYNAME',
            type : 'link',
            width : 200
        }, {
            field : 'queryCategory',
            title : 'T_CORE_CUSTOM_QUERY.QUERYCATEGORY',
            type : 'select',
            category : 'query-category',
            width : 150
        }, {
            field : 'ext$.reportname',
            title : 'T_CORE_CUSTOM_QUERY.REPORTNAME',
            width : 200
        }, {
            field : 'ext$.dsname',
            title : 'T_CORE_CUSTOM_QUERY.DSNAME',
            width : 200
        }, {
            field : 'customSql',
            title : 'T_CORE_CUSTOM_QUERY.CUSTOMSQL',
            width : 200,
            type : 'richText',
            sort : false
        }, {
            field : 'publishStatus',
            title : 'T_CORE_CUSTOM_QUERY.PUBLISHSTATUS',
            type : 'select',
            category : 'publish-state',
            width : 150
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_CUSTOM_QUERY.PUBLISHMENU',
            width : 200
        } ];
    },

    getCustomFormFields : function () {
        return [{
            field : 'queryName',
            title : 'T_CORE_CUSTOM_QUERY.QUERYNAME'
        }, {
            field : 'queryCategory',
            title : 'T_CORE_CUSTOM_QUERY.QUERYCATEGORY',
            type : 'select',
            category : 'query-category',
            readonly : true
        }, {
            field : 'ext$.reportname',
            title : 'T_CORE_CUSTOM_QUERY.REPORTNAME',
            type : 'choose',
            category : 'report-config',
            single : true,
            onAfterChoose : function (rows) {
                this.setData({
                    reportConfigLineId : rows[0].id,
                    ext$ : {reportname : rows[0].reportName}
                })
            }
        }, {
            field : 'ext$.dsname',
            title : 'T_CORE_CUSTOM_QUERY.DSNAME',
            readonly : true
        }, {
            field : 'publishStatus',
            title : 'T_CORE_CUSTOM_QUERY.PUBLISHSTATUS',
            type : 'select',
            category : 'publish-state',
            readonly : true
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_CUSTOM_QUERY.PUBLISHMENU',
            readonly : true
        },
        //     {
        //     field : 'customSql',
        //     title : 'T_CORE_CUSTOM_QUERY.CUSTOMSQL',
        //     type : 'textarea',
        //     height : 100,
        //     onChange : function (field, value, index) {
        //         var form = this;
        //         workspace.window.showMask(true);
        //         form.disableAutoSave();
        //         var url = coreCustomQuery.baseUrl + '/{sql}/action/check-sql'
        //         // 对于有效的SQL,进行保存操作
        //         Gikam.post(Gikam.printf(url, {
        //             sql : value
        //         })).done(function(r) {
        //             if (!r.valid) {
        //                 Gikam.alert('CORE.MODULE.CUSTOM.CUSTOM_QUERIES.TIP.SQL_ERROR');
        //             }else {
        //                 Gikam.put(Gikam.printf(coreCustomQuery.baseUrl + '/{customQueryId}/{sql}/action/add-fields', {
        //                     customQueryId : form.getData().id,
        //                     sql : value
        //                 })).done(function () {
        //                     Gikam.getComp('core-custom-query-field-list-grid').refresh();
        //                 })
        //             }
        //         }).always(function() {
        //             workspace.window.closeMask();
        //             form.disableAutoSave();
        //         });
        //     }
        // }
        ]
    }
    ,

    getBaseInfoFormFields : function() {
        return [ {
            field : 'queryName',
            title : 'T_CORE_CUSTOM_QUERY.QUERYNAME',
            preInsert : true,
            titleWidth : 240,
            width : 700,
            validators : [ 'notEmpty', 'strLength[0,40]' ]
        }, {
            field : 'queryCategory',
            title : 'T_CORE_CUSTOM_QUERY.QUERYCATEGORY',
            type : 'select',
            category : 'query-category',
            preInsert : true,
            hidden : true,
            titleWidth : 240,
            width : 700,
            validators : [ 'notEmpty' ],
            onChange : function (field, value) {
                // if(value == 'fine'){
                //     this.showFields(['report']);
                //     this.hideFields(['customSql']);
                //     this.removeFieldValidator('customSql',['notEmpty']);
                //     this.addFieldValidator('report',['notEmpty'])
                // }
                // if(value == 'grid'){
                //     this.showFields(['customSql']);
                //     this.hideFields(['report']);
                //     this.removeFieldValidator('report',['notEmpty']);
                //     this.addFieldValidator('customSql',['notEmpty'])
                // }
            }
        }
        ];
    },

    getFrDataResource : function () {
        return [{
            field : 'report',
            title : 'T_CORE_CUSTOM_QUERY.REPORT',
            genericQuery : true,
            editor : true,
            type : 'choose',
            titleWidth : 240,
            width : 700,
            category : 'report-config',
            preInsert : true,
            single : true,
            onBeforeChoose : function() {
                return {
                    activatedFlag_SEQ : '1'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        reportConfigLineId : rows[0].id,
                        report : rows[0].reportName
                    })
                }
            },
            validators : [ 'notEmpty' ]
        }]
    },

    getDataResource : function () {
        return[
        //     {
        //     field : 'dsType',
        //     title : 'T_CORE_CUSTOM_QUERY.DSTYPE',
        //     titleWidth : 240,
        //     width : 700,
        //     validators : [ 'notEmpty' ],
        //     type : 'select',
        //     category : 'ds-type',
        //     onChange : function (field, value, index) {
        //         if(value == 'customSql'){
        //             this.showFields(['customSql']);
        //             this.hideFields(['customDs']);
        //             this.removeFieldValidator('customDs',['notEmpty']);
        //             this.addFieldValidator('customSql',['notEmpty'])
        //         }
        //         if(value == 'customDs'){
        //             this.showFields(['customDs']);
        //             this.hideFields(['customSql']);
        //             this.removeFieldValidator('customSql',['notEmpty']);
        //             this.addFieldValidator('customDs',['notEmpty'])
        //         }
        //     }
        // },
            {
            field : 'customDs',
            title : 'T_CORE_CUSTOM_QUERY.CUSTOMDS',
            genericQuery : true,
            editor : true,
            type : 'choose',
            category : 'custom-ds',
            hidden : true,
            titleWidth : 240,
            width : 700,
            single : true,
            validators : [ 'notEmpty' ],
            onBeforeChoose : function() {
                return {
                    activatedFlag_SEQ : '1'
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        customDsId : rows[0].id,
                        customDs : rows[0].dsName
                    })
                }
            }
        },
        //     {
        //     field : 'customSql',
        //     title : 'T_CORE_CUSTOM_QUERY.CUSTOMSQL',
        //     type : 'textarea',
        //     editor : true,
        //     hidden : true,
        //     titleWidth : 240,
        //     width : 700,
        //     height : 260,
        //     validators : [ 'notEmpty' ],
        //     onChange : function(name, value, index) {
        //         var form = this;
        //         workspace.window.showMask(true);
        //         var url = coreCustomQuery.baseUrl + '/{sql}/action/check-sql'
        //         // 对于有效的SQL,进行保存操作
        //         Gikam.post(Gikam.printf(url, {
        //             sql : value
        //         })).done(function(r) {
        //             if (!r.valid) {
        //                 Gikam.alert('CORE.MODULE.CUSTOM.CUSTOM_QUERIES.TIP.SQL_ERROR');
        //                 form.setData({
        //                     customSql : ''
        //                 })
        //             }
        //         }).always(function() {
        //             workspace.window.closeMask();
        //         });
        //     }
        // },
        ]
    },

    getFieldEditListColumns : function (items) {
        return [ {
            checkbox : true
        },{
            field : 'fieldName',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.FIELDNAME',
            width : 100
        }, {
            field : 'columnName',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.COLUMNNAME',
            editor : true,
            width : 150,
            validators : [ 'strLength[0,40]' ]
        }, {
            field : 'fieldType',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.FIELDTYPE',
            type : 'select',
            firstBlank : false,
            category : 'field-category',
            width : 100,
            onChange : function(field, value, index) {
                if (value === 'link') {
                    this.toFieldsReadonly(index, ['dsCategory','dataSource','customSql']);
                    this.toFieldsEdit(index, ['linkHref']);
                    this.setData({
                        index : index,
                        dsCategory : '',
                        dataSource : '',
                        customSql : '',
                        fieldType : value
                    });
                } else if (value === 'select') {
                    this.toFieldsReadonly(index, ['linkHref','dataSource','customSql']);
                    this.toFieldsEdit(index, ['dsCategory']);
                    this.setData({
                        index : index,
                        linkHref : '',
                        dataSource : '',
                        customSql : '',
                        fieldType : value
                    });
                } else {
                    this.toFieldsReadonly(index, ['linkHref','dsCategory','dataSource','customSql']);
                    this.setData({
                        index : index,
                        linkHref : '',
                        dsCategory : '',
                        dataSource : '',
                        customSql : '',
                        fieldType : value
                    });
                }
            },
            editor : true
        }, {
            field : 'width',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.WIDTH',
            editor : true,
            width : 100,
            validators : [ 'strLength[0,6]','number' ]
        }, {
            field : 'linkHref',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.LINKHREF',
            editor : true,
            width : 200,
            validators : [ 'strLength[0,120]' ],
            onEditorInitReadonly : function(row) {
                if (row.fieldType === 'select') {
                    return true;
                } else if (row.fieldType === 'link') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'dsCategory',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.DSCATEGORY',
            type : 'select',
            category : 'ds-category',
            firstBlank : false,
            editor : true,
            width : 130,
            onChange : function (field, value, index) {
                if(value === 'system'){
                    Gikam.getComp('core-custom-query-field-list-grid').getField(index,'dataSource').setItems(items);
                    this.toFieldsReadonly(index, ['customSql']);
                    this.toFieldsEdit(index, ['dataSource']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        customSql : ''
                    });
                }else if(value === 'customSql'){
                    this.toFieldsReadonly(index, ['dataSource']);
                    this.toFieldsEdit(index, ['customSql']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        dataSource : ''
                    });
                }
            },
            onEditorInitReadonly : function(row) {
                if (row.fieldType === 'select') {
                    return false;
                } else if (row.fieldType === 'link') {
                    return true;
                }
                return true;
            }
        }, {
            field : 'dataSource',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.DATASOURCE',
            type : 'select',
            editor : true,
            search : true,
            width : 120,
            onEditorInitReadonly : function(row) {
                if (row.fieldType === 'select' && row.dsCategory === 'system') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'customSql',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.CUSTOMSQL',
            editor : true,
            sort : false,
            width : 300,
            onEditorInitReadonly : function(row) {
                if (row.fieldType === 'select' && row.dsCategory === 'customSql') {
                    return false;
                } else {
                    return true;
                }
            },
            onChange : function (field, value, index) {
                if(!Gikam.isEmpty(value)){
                    Gikam.postSync(coreCustomQuery.baseUrl + '/action/check-custom-sql',Gikam.getJsonWrapper({
                        f : {
                            sql : value
                        }
                    })).done(function (data) {
                        if (!data.valid){
                            Gikam.alert(data.message);
                            this.setData({
                                customSql : ''
                            })
                        }
                    })
                }
            }
        }, {
            field : 'defaultCondition',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.DEFAULTCONDITION',
            editor : true,
            type : 'simpleCheckbox',
            width : 130,
            onChange : function (name, value, index) {
                if(value == '1'){
                    Gikam.postText(Gikam.printf(coreCustomQuery.baseUrl + '/query-condition/source-field/{fieldId}', {
                        fieldId : this.getData()[index].id
                    })).done(function () {
                        Gikam.getComp('core-custom-query-condition-list-grid').refresh();
                    })
                }
                if(value == '0'){
                    Gikam.del(Gikam.printf(coreCustomQuery.baseUrl + '/query-condition/source-by-field/{fieldId}', {
                        fieldId : this.getData()[index].id
                    })).done(function () {
                        Gikam.getComp('core-custom-query-condition-list-grid').refresh();
                    })
                }
            }
        }, {
            field : 'show',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.SHOW',
            editor : true,
            type : 'simpleCheckbox',
            width : 100,
            onChange : function (name, value, index) {
            }
        } ];

    },

    getChooseFieldListColumns : function () {
        return [ {
            field : 'fieldName',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.FIELDNAME',
            width : 300
        }, {
            field : 'columnName',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.COLUMNNAME',
            width : 400
        }, {
            field : 'fieldType',
            title : 'T_CORE_CUSTOM_QUERY_FIELD.FIELDTYPE',
            type : 'select',
            category : 'field-category',
            width : 200
        }];

    },

    getButtonEditListColumns : function () {
        return [ {
            checkbox : true
        },{
            field : 'buttonName',
            title : 'T_CORE_CUSTOM_QUERY_BUTTON.BUTTONNAME',
            editor : true,
            width : 200
        }, {
            field : 'buttonIcon',
            type : 'select',
            editor : true,
            category : 'button-icon',
            title : 'T_CORE_CUSTOM_QUERY_BUTTON.BUTTONICON',
            width : 200
        }, {
            field : 'buttonLogic',
            type : 'richText',
            editor : true,
            title : 'T_CORE_CUSTOM_QUERY_BUTTON.BUTTONLOGIC',
            width : 400
        }]

    },

    getConditionEditListColumns : function (items,dsCategory) {
        var _this = this;
        return [ {
            checkbox : true
        }, {
            field : 'fieldName',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.FIELDNAME',
            width : 120
        }, {
            field : 'title',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.TITLE',
            editor : true,
            width : 120
        }, {
            field : 'conditionCategory',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CONDITIONCATEGORY',
            type : 'select',
            firstBlank : false,
            category : 'condition-field-category',
            width : 120,
            editor : true,
            onChange : function (name, value, index) {
                this.toFieldsReadonly(index, ['dsCategory','dataSource','customSql']);
                var conditionGrid = Gikam.getComp('core-custom-query-condition-list-grid');
                if (value === 'string'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    coreCustomQuery.getMacthItems('string-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })
                }
                if (value === 'number'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    coreCustomQuery.getMacthItems('number-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })
                }
                if (value === 'date'){
                    this.setData({
                        index : index,
                        matchPattern : '',
                        conditionCategory : value
                    });
                    this.toFieldsReadonly(index, ['matchPattern']);
                }
                if (value === 'datetime'){
                    this.setData({
                        index : index,
                        matchPattern : '',
                        conditionCategory : value
                    });
                    this.toFieldsReadonly(index, ['matchPattern']);
                }
                if (value === 'choose' || value === 'select'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    if(conditionGrid.getField(index,'dsCategory').value === 'system'){
                        this.toFieldsEdit(index, ['dataSource']);
                        this.toFieldsEdit(index, ['dsCategory']);
                    }else {
                        this.toFieldsReadonly(index, ['dataSource','customSql']);
                        this.toFieldsEdit(index, ['dsCategory']);
                    }
                    if(value === 'select'){
                        this.setData({
                            index : index,
                            dataSource : '',
                            customSql : '',
                            chooseField : '',
                            conditionCategory : value
                        });
                        conditionGrid.getField(index,'dsCategory').setItems(coreCustomQuery.getDsCategory('select'));
                        conditionGrid.getField(index,'dataSource').setItems(items);
                        this.toFieldsReadonly(index, ['chooseField']);
                    }else {
                        this.setData({
                            index : index,
                            dataSource : '',
                            customSql : '',
                            conditionCategory : value
                        });
                        conditionGrid.getField(index,'dsCategory').setItems(coreCustomQuery.getDsCategory('choose'));
                        conditionGrid.getField(index,'dataSource').setItems(dsCategory);
                        this.toFieldsEdit(index, ['chooseField']);
                    }
                    coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })

                } else {
                    let _this = this;
                    this.setData({
                        index : index,
                        dataSource : '',
                        customSql : '',
                        dsCategory : '',
                        chooseField : '',
                        conditionCategory : value
                    }).then(function () {
                        _this.toFieldsReadonly(index, ['dataSource','dsCategory','customSql','chooseField']);
                    });
                }
            }
        }, {
            field : 'dsCategory',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DSCATEGORY',
            type : 'select',
            firstBlank : false,
            width : 180,
            editor : true,
            onChange : function (field, value, index) {
                var conditionGrid = Gikam.getComp('core-custom-query-condition-list-grid');
                if( value === 'system'){
                    this.toFieldsReadonly(index, ['customSql']);
                    this.toFieldsEdit(index, ['dataSource']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        customSql : ''
                    });
                    if(conditionGrid.getField(index,'conditionCategory').value === 'select'){
                        conditionGrid.getField(index,'dataSource').setItems(items);
                    }
                    if(conditionGrid.getField(index,'conditionCategory').value === 'choose'){
                        this.toFieldsEdit(index, ['chooseField']);
                        conditionGrid.getField(index,'dataSource').setItems(dsCategory);
                    }
                }else {
                    this.toFieldsReadonly(index, ['dataSource']);
                    this.toFieldsEdit(index, ['customSql']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        dataSource : ''
                    });
                }
            },
            onEditorInitReadonly : function(row) {
                if (row.conditionCategory === 'select' || row.conditionCategory === 'choose') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'dataSource',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DATASOURCE',
            type : 'select',
            editor : true,
            width : 120,
            search : true,
            onEditorInitReadonly :function (row) {
                if ((row.conditionCategory === 'select' || row.conditionCategory === 'choose') && row.dsCategory === 'system') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'customSql',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CUSTOMSQL',
            editor : true,
            sort : false,
            width : 240,
            onEditorInitReadonly :function (row) {
                if (row.conditionCategory === 'select' && row.dsCategory === 'customSql') {
                    return false;
                } else {
                    return true;
                }
            },
            onChange : function (field, value, index) {
                if(!Gikam.isEmpty(value)){
                    Gikam.post(coreCustomQuery.baseUrl + '/action/check-custom-sql',Gikam.getJsonWrapper({
                        f : {
                            sql : value
                        }
                    })).done(function (data) {
                        if (!data.valid){
                            Gikam.alert(data.message);
                        }
                    })
                }
            }
        }, {
            field : 'chooseField',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CHOOSEFIELD',
            width : 160,
            editor : true,
            onEditorInitReadonly :function (row) {
                if (row.conditionCategory === 'choose' && row.dsCategory === 'system') {
                    return false;
                } else {
                    return true;
                }
            },
        }, {
            field : 'matchPattern',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.MATCHPATTERN',
            type : 'select',
            firstBlank : false,
            selectedIndex : 0,
            width : 120,
            category : '',
            editor : true,
            onEditorInitReadonly :function (row) {
                if (row.conditionCategory === 'date' || row.conditionCategory === 'datetime') {
                    return true;
                } else {
                    return false;
                }
            }
        }]

    },

    getFrConditionEditListColumns : function (items,dsCategory) {
        return [ {
            checkbox : true
        }, {
            field : 'frReportDsName',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.FRREPORTDSNAME',
            width : 180
        }, {
            field : 'fieldName',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.FIELDNAME',
            width : 140
        }, {
            field : 'title',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.TITLE',
            editor : true,
            width : 140
        }, {
            field : 'conditionCategory',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CONDITIONCATEGORY',
            type : 'select',
            firstBlank : false,
            category : 'condition-field-category',
            width : 120,
            editor : true,
            onChange : function (name, value, index) {
                //debugger;
              //  this.toFieldsReadonly(index, ['dsCategory','dataSource','customSql']);
                var conditionGrid = Gikam.getComp('core-custom-query-condition-fr-list-grid');
                if (value === 'string'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    coreCustomQuery.getMacthItems('string-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })
                }
                if (value === 'number'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    coreCustomQuery.getMacthItems('number-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })
                }
                if(value === 'date' || value === 'datetime'){
                    this.setData({
                        index : index,
                        matchPattern : '',
                        conditionCategory : value
                    });
                }
                if (value === 'choose' || value === 'select'){
                    this.toFieldsEdit(index, ['matchPattern']);
                    if(conditionGrid.getField(index,'dsCategory').value === 'system'){
                        this.toFieldsEdit(index, ['dataSource']);
                        this.toFieldsEdit(index, ['dsCategory']);
                    }else {
                        this.toFieldsReadonly(index, ['dataSource','customSql']);
                        this.toFieldsEdit(index, ['dsCategory']);
                    }
                    if(value === 'select'){
                        this.setData({
                            index : index,
                            dataSource : '',
                            customSql : '',
                            chooseField : '',
                            conditionCategory : value
                        });
                        conditionGrid.getField(index,'dsCategory').setItems(coreCustomQuery.getDsCategory('select'));
                        conditionGrid.getField(index,'dataSource').setItems(items);
                        this.toFieldsReadonly(index, ['chooseField']);
                    }else {
                        this.setData({
                            index : index,
                            dataSource : '',
                            customSql : '',
                            conditionCategory : value
                        });
                        conditionGrid.getField(index,'dsCategory').setItems(coreCustomQuery.getDsCategory('choose'));
                        conditionGrid.getField(index,'dataSource').setItems(dsCategory);
                        this.toFieldsEdit(index, ['chooseField']);
                    }
                    coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                        conditionGrid.getField(index,'matchPattern').setItems(data);
                    })
                }  else {
                    let _this = this;
                    this.setData({
                        index : index,
                        dataSource : '',
                        customSql : '',
                        dsCategory : '',
                        chooseField : '',
                        conditionCategory : value
                    }).then(function () {
                        _this.toFieldsReadonly(index, ['dataSource','dsCategory','customSql','chooseField']);
                    });
                }
            }
        }, {
            field : 'dsCategory',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DSCATEGORY',
            type : 'select',
            firstBlank : false,
            width : 180,
            editor : true,
            onChange : function (field, value, index) {
                var conditionGrid = Gikam.getComp('core-custom-query-condition-fr-list-grid');
                if( value === 'system'){
                    this.toFieldsReadonly(index, ['customSql']);
                    this.toFieldsEdit(index, ['dataSource']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        customSql : ''
                    });
                    if(conditionGrid.getField(index,'conditionCategory').value === 'select'){
                        conditionGrid.getField(index,'dataSource').setItems(items);
                    }
                    if(conditionGrid.getField(index,'conditionCategory').value === 'choose'){
                        this.toFieldsEdit(index, ['chooseField']);
                        conditionGrid.getField(index,'dataSource').setItems(dsCategory);
                    }
                }else {
                    this.toFieldsReadonly(index, ['dataSource']);
                    this.toFieldsEdit(index, ['customSql']);
                    this.setData({
                        index : index,
                        dsCategory : value,
                        dataSource : ''
                    });
                }
            },
            onEditorInitReadonly : function(row) {
                if (row.conditionCategory === 'select' || row.conditionCategory === 'choose') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'dataSource',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DATASOURCE',
            type : 'select',
            editor : true,
            width : 120,
            search : true,
            onEditorInitReadonly :function (row) {
                if ((row.conditionCategory === 'select' || row.conditionCategory === 'choose') && row.dsCategory === 'system') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'customSql',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CUSTOMSQL',
            editor : true,
            sort : false,
            width : 240,
            onEditorInitReadonly : function (row) {
                if (row.conditionCategory === 'select' && row.dsCategory === 'customSql') {
                    return false;
                } else {
                    return true;
                }
            },
            onChange : function (field, value, index) {
                if(!Gikam.isEmpty(value)){
                    Gikam.post(coreCustomQuery.baseUrl + '/action/check-custom-sql',Gikam.getJsonWrapper({
                        f : {
                            sql : value
                        }
                    })).done(function (data) {
                        if (!data.valid){
                            Gikam.alert(data.message);
                        }
                    })
                }
            }
        }, {
            field : 'chooseField',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CHOOSEFIELD',
            width : 160,
            editor : true,
            onEditorInitReadonly : function (row) {
                if (row.conditionCategory === 'choose' && row.dsCategory === 'system') {
                    return false;
                } else {
                    return true;
                }
            },
        }, {
            field : 'matchPattern',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.MATCHPATTERN',
            type : 'select',
            firstBlank : false,
            width : 120,
            category : '',
            selectedIndex : 0,
            editor : true,
            onEditorInitReadonly : function (row) {
                if (row.conditionCategory === 'date' || row.conditionCategory === 'datetime') {
                    return true;
                } else {
                    return false;
                }
            }
        }]

    },

    getFrConditionAddListColumns : function (items,dsCategory) {
        return [ {
            checkbox : true
        }, {
            field : 'frReportDsName',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.FRREPORTDSNAME',
            preInsert : true
        }, {
            field : 'fieldName',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.FIELDNAME',
            preInsert : true
        }, {
            field : 'title',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.TITLE',
            preInsert : true
        }, {
            field : 'conditionCategory',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CONDITIONCATEGORY',
            type : 'select',
            firstBlank : false,
            category : 'condition-field-category',
            preInsert : true,
            onChange : function (name, value, index) {
                var _this = this
                if (value === 'string'){
                    this.showFields(['matchPattern']);
                    this.addFieldValidator('matchPattern',['notEmpty']);
                    coreCustomQuery.getMacthItems('string-match-pattern').done(function (data) {
                        _this.getField('matchPattern').setItems(data);
                    })
                }
                if (value === 'number'){
                    this.showFields(['matchPattern']);
                    this.addFieldValidator('matchPattern',['notEmpty']);
                    coreCustomQuery.getMacthItems('number-match-pattern').done(function (data) {
                        _this.getField('matchPattern').setItems(data);
                    })
                }
                if (value === 'choose'){
                    this.showFields(['matchPattern']);
                    this.addFieldValidator('matchPattern',['notEmpty']);
                    this.showFields(['dsCategory']);
                    this.addFieldValidator('dsCategory',['notEmpty']);
                    this.showFields(['chooseField']);
                    this.addFieldValidator('chooseField',['notEmpty','strLength[0,24]']);
                    this.hideFields(['customSql']);
                    this.removeFieldValidator('customSql',['notEmpty']);
                    this.getField('dataSource').setItems(dsCategory);
                    this.getField('dsCategory').setItems(coreCustomQuery.getDsCategory('choose'));
                    coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                        _this.getField('matchPattern').setItems(data);
                    })
                }
                else if (value === 'select'){
                    this.showFields(['matchPattern']);
                    this.addFieldValidator('matchPattern',['notEmpty']);
                    this.showFields(['dsCategory']);
                    this.addFieldValidator('dsCategory',['notEmpty']);
                    this.hideFields(['chooseField']);
                    this.removeFieldValidator('chooseField',['notEmpty']);
                    this.getField('dsCategory').setItems(coreCustomQuery.getDsCategory('select'));
                    this.getField('dataSource').setItems(items);
                    coreCustomQuery.getMacthItems('select-match-pattern').done(function (data) {
                        _this.getField('matchPattern').setItems(data);
                    })
                } else if(value === 'date' || value === 'datetime'){
                    this.hideFields(['dsCategory']);
                    this.hideFields(['dataSource']);
                    this.hideFields(['customSql']);
                    this.hideFields(['chooseField']);
                    this.hideFields(['matchPattern']);
                    this.removeFieldValidator('dsCategory',['notEmpty']);
                    this.removeFieldValidator('dataSource',['notEmpty']);
                    this.removeFieldValidator('customSql',['notEmpty']);
                    this.removeFieldValidator('chooseField',['notEmpty']);
                    this.removeFieldValidator('matchPattern',['notEmpty'])
                } else{
                    this.hideFields(['dsCategory']);
                    this.hideFields(['dataSource']);
                    this.hideFields(['customSql']);
                    this.hideFields(['chooseField']);
                    this.removeFieldValidator('dsCategory',['notEmpty']);
                    this.removeFieldValidator('dataSource',['notEmpty']);
                    this.removeFieldValidator('customSql',['notEmpty']);
                    this.removeFieldValidator('chooseField',['notEmpty'])
                }
            }
        }, {
            field : 'dsCategory',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DSCATEGORY',
            type : 'select',
            firstBlank : false,
            category: 'ds-category',
            preInsert : true,
            onChange : function (field, value, index) {
                if(value === 'system'){
                    if(this.getField('conditionCategory').value === 'choose'){
                        this.getField('dataSource').setItems(dsCategory);
                    }else {
                        this.getField('dataSource').setItems(items);
                    }
                    this.showFields(['dataSource']);
                    this.hideFields(['customSql']);
                    this.removeFieldValidator('customSql',['notEmpty'])
                    this.addFieldValidator('dataSource',['notEmpty'])

                }else {
                    this.hideFields(['dataSource']);
                    this.showFields(['customSql']);
                    this.removeFieldValidator('dataSource',['notEmpty'])
                    this.addFieldValidator('customSql',['notEmpty'])
                }
            }
        }, {
            field : 'dataSource',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.DATASOURCE',
            type : 'select',
            search : true,
            preInsert : true
        }, {
            field : 'customSql',
            titleWidth : 130,
            type : 'textarea',
            height : '60',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CUSTOMSQL',
            preInsert : true
        }, {
            field : 'chooseField',
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.CHOOSEFIELD',
            titleWidth : 180,
            preInsert : true
        }, {
            field : 'matchPattern',
            titleWidth : 130,
            title : 'T_CORE_CUSTOM_QUERY_CONDITION.MATCHPATTERN',
            type : 'select',
            firstBlank : false,
            category : '',
            selectedIndex : 0,
            preInsert : true
        }]

    },

    getReleaseColumns : function () {
        var _this = this;
        return [
            {
                field : 'menuName',
                title : 'T_CORE_MENU.MENUNAME',
                width : 350,
                validators : ['notEmpty','strLength[0,22]']
            }, {
                field : 'parentName',
                title : 'T_CORE_MENU.PARENTNAME',
                type : 'choose',
                category : 'menu',
                single : true,
                width : 350,
                validators : ['notEmpty'],
                onAfterChoose : function(rows, index) {
                    if (rows) {
                        this.setData({
                            parentName : rows[0].menuName,
                            parentId : rows[0].id,
                        })
                    }
                },
            }, {
                field : 'menuIconUrl',
                title : 'T_CORE_MENU.MENUICON',
                type : 'image',
                height : 30,
                align : 'center',
                enlarged : false,
                onClick : function() {
                    _this.getShowImgPanel('menuIconUrl');
                },
                onError : function(image, value) {
                    return '/static/icon/menu/peony/white/' + value.substring(value.lastIndexOf('/') + 1);
                }
            }
        ]
    },

    getShowImgPanel : function(field) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList
        }
        var urlObj = {
            menuIconUrl : _this.menuUrl + '/icons/queries'
        };
        if (listObj[field].length === 0) {
            Gikam.getJson(urlObj[field]).done(function(data) {
                data.forEach(function(item) {
                    item.id = item.iconName;
                    item.src = IFM_CONTEXT + item.iconUrl;
                });
                field === 'menuIconUrl' && (_this.menuIconsList = data);
                _this.imgPanelHandle(field, false);
            }).fail(function(e) {
                _this.imgPanelHandle(field, true);
            });
        } else {
            this.imgPanelHandle(field, false);
        }
    },

    imgPanelHandle : function(field, error) {
        var _this = this;
        var listObj = {
            menuIconUrl : this.menuIconsList
        }
        var titleObj = {
            menuIconUrl : '菜单图标配置',
        }
        var iconPanel = Gikam.create('iconPanel', {
            renderTo : workspace.window.$dom,
            id : 'core-menu-edit-list-icon-panel',
            title : titleObj[field],
            data : listObj[field],
            noRequest : error,
            onConfirm : function(data) {
                var form = Gikam.getComp('core-custom-query-release-list-form');
                var iconUrl = data.iconUrl ? data.iconUrl.replace(/blue|black/g, 'white') : error ? '/static/icon/menu/peony/white/' + data.id + '.svg' : null;
                field === 'menuIconUrl' && form.setData({
                    icon : error ? data.id + '.svg' : data.iconName,
                    menuIconUrl : iconUrl
                });
                iconPanel.cancel();
            }
        });
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/custom-queries'
});
