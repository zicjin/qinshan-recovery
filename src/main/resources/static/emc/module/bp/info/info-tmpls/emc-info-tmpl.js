var emcInfoTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/info/info-tmpls',

    getTmplBaseInfoFormFields : function() {
        return [ {
            field : 'tmplName',
            title : 'T_EMC_INFO_TMPL.TMPLNAME',
            preInsert : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getTmplEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'tmplName',
            title : 'T_EMC_INFO_TMPL.TMPLNAME',
            width : 100,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'ext$.excelname',
            title : 'T_EMC_INFO_TMPL.EXCELNAME',
            width : 120,
            type : 'link'
        }, {
            field : 'ext$.point',
            title : 'T_EMC_INFO_TMPL.POINT',
            width : 80,
            formatter : function(index, value, row, text) {
                return '<a href="javascript:;" style="color:blue;" onclick="emcInfoTmpl.editPage.loadTmplPointPage(\'' + row.id + '\')">' + Gikam.propI18N('T_EMC_INFO_TMPL.POINT') + '</a>';
            }
        }, {
            field : 'ext$.usedorgname',
            title : 'T_EMC_INFO_TMPL.USEDORGNAME',
            width : 120,
            editor : true,
            type : 'choose',
            category : 'org',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        usedOrgId : rows[0].id,
                        ext$ : {
                            usedorgname : rows[0].orgName
                        }
                    });
                }
            }
        } ];
    },

    getFieldEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 20
        }, {
            field : 'field',
            title : 'T_EMC_INFO_TMPL_FIELD.FIELD',
            width : 70
        }, {
            field : 'fieldTitle',
            title : 'T_EMC_INFO_TMPL_FIELD.FIELDTITLE',
            width : 120,
            editor : true,
            validators : [ 'notEmpty', 'strLength[0,60]' ]
        }, {
            field : 'width',
            title : 'T_EMC_INFO_TMPL_FIELD.WIDTH',
            width : 60,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'display',
            title : 'T_EMC_INFO_TMPL_FIELD.DISPLAY',
            width : 90,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'infoTmplFieldShow'
            },
            validators : [ 'notEmpty', 'strLength[0,36]' ]
        }, {
            field : 'lowLimit',
            title : 'T_EMC_INFO_TMPL_FIELD.LOWLIMIT',
            width : 90,
            editor : true,
            validators : [ 'strLength[0,30]' ]
        }, {
            field : 'unitName',
            title : 'T_EMC_INFO_TMPL_FIELD.UNITNAME',
            width : 90,
            editor : true,
            type : 'choose',
            category : 'unit',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var grid = this;
                    grid.setData({
                        index : index,
                        unitName : rows[0].unitName
                    });
                }
            },
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'revisionRule',
            title : 'T_EMC_INFO_TMPL_FIELD.REVISIONRULE',
            width : 90,
            editor : true,
            type : 'choose',
            category : 'rounding-rule',
            single : true,
            onAfterChoose : function(back, index) {
                var grid = this;
                if (back) {
                    grid.setData({
                        index : index,
                        'revisionRule' : back
                    });
                }
            },
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'transform',
            title : 'T_EMC_INFO_TMPL_FIELD.TRANSFORM',
            width : 100,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            },
            validators : [ 'notEmpty', 'strLength[0,3]' ]
        }, {
            field : 'primaryIndex',
            title : 'T_EMC_INFO_TMPL_FIELD.PRIMARYINDEX',
            width : 90,
            editor : true,
            type : 'number',
            validators : [ 'number' ]
        }, {
            field : 'primaryName',
            title : 'T_EMC_INFO_TMPL_FIELD.PRIMARYNAME',
            width : 110,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'infoTmplFieldPrimaryName',
                codeClassId : 'biz'
            },
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'bizField',
            title : 'T_EMC_INFO_TMPL_FIELD.BIZFIELD',
            width : 120,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        }, {
            field : 'defaultValue',
            title : 'T_EMC_INFO_TMPL_FIELD.DEFAULTVALUE',
            width : 80,
            editor : true,
            validators : [ 'strLength[0,60]' ]
        } ];
    },

    getItemBaseInfoFormFields : function() {
        return [ {
            checkbox : true
        }, {
            field : 'infoTmplFieldId',
            title : 'T_EMC_TMPL_FIELD_ITEM.INFOTMPLFIELDID',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'infoTmplField',
            param : {
                infoTmplId : emcInfoTmpl.editPage.getTmplId(),
                fieldTitle_DIFFER : null,
                filter_target_have_used : '1'
            },
            validators : [ 'number' ]
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_TMPL_FIELD_ITEM.MONITORTMPLID',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'monitorTmpl',
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = this;
                if (!Gikam.isEmpty(value)) {
                    ///test
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/tests/queries/raw', Gikam.getJsonWrapper({
                        f : {
                            target_filter : JSON.stringify({
                                type : 'assigned',
                                targetTable : 'T_EMC_MONITOR_SCHEME',
                                targetMatchColumn : 'TESTID',
                                thisMatchColumn : 'ID',
                                filter : [ {
                                    targetFilterColumn : 'MONITORTMPLID',
                                    targetFilterValue : value
                                } ]
                            })
                        },
                        n : -1,
                        s : 50
                    })).done(function(r) {
                        //设置相关列下拉选项
                        emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'testId', r.rows, 'id', 'testName');
                        emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'methodId', []);
                        emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'itemId', []);
                        //保存数据
                        form.setData({
                            monitorTmplId : value,
                            testId : null,
                            methodId : null,
                            itemId : null
                        });
                    });
                }
            }
        }, {
            field : 'testId',
            title : 'T_EMC_TMPL_FIELD_ITEM.TESTID',
            preInsert : true,
            type : 'select',
            search : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = this;
                if (!Gikam.isEmpty(value)) {
                    Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/methods/queries/raw', Gikam.getJsonWrapper({
                        f : {
                            target_filter : JSON.stringify({
                                type : 'assigned',
                                targetTable : 'T_EMC_MONITOR_SCHEME',
                                targetMatchColumn : 'METHODID',
                                thisMatchColumn : 'ID',
                                filter : [ {
                                    targetFilterColumn : 'MONITORTMPLID',
                                    targetFilterValue : form.getData().monitorTmplId
                                }, {
                                    targetFilterColumn : 'TESTID',
                                    targetFilterValue : value
                                } ]
                            })
                        },
                        n : -1,
                        s : 50
                    })).done(function(r) {
                        //设置相关列下拉选项
                        emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'methodId', r.rows, 'id', 'methodName');
                        emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'itemId', []);
                        //保存数据
                        form.setData({
                            testId : value,
                            methodId : null,
                            itemId : null
                        });
                    });
                }
            }
        }, {
            field : 'methodId',
            title : 'T_EMC_TMPL_FIELD_ITEM.METHODID',
            preInsert : true,
            type : 'select',
            search : true,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var form = this;
                var formData = form.options.data;
                Gikam.post(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/items/queries', Gikam.getJsonWrapper({
                    f : {
                        target_tmpl_item : '1',
                        targetMonitorTmplId : formData.monitorTmplId,
                        targetTestId : formData.testId,
                        targetMethodId : value
                    },
                    n : -1,
                    s : 50
                })).done(function(r) {
                    emcInfoTmpl.editPage.setFormSelectFieldItems(form, 'itemId', r.rows, 'id', 'itemName');
                    // 保存数据
                    form.setData({
                        methodId : value,
                        itemId : null
                    });
                });
            }
        }, {
            field : 'itemId',
            title : 'T_EMC_TMPL_FIELD_ITEM.ITEMID',
            preInsert : true,
            type : 'select'
        } ];
    },

    getItemEditListColumns : function() {
        return [ {
            checkbox : true,
            width : 30
        }, {
            field : 'infoTmplFieldId',
            title : 'T_EMC_TMPL_FIELD_ITEM.INFOTMPLFIELDID',
            width : 120,
            readonly : true,
            type : 'select',
            search : true,
            category : 'infoTmplField',
            validators : [ 'number' ]
        }, {
            field : 'monitorTmplId',
            title : 'T_EMC_TMPL_FIELD_ITEM.MONITORTMPLID',
            width : 160,
            readonly : true,
            type : 'select',
            search : true,
            category : 'monitorTmpl'
        }, {
            field : 'testId',
            title : 'T_EMC_TMPL_FIELD_ITEM.TESTID',
            width : 100,
            readonly : true,
            type : 'select',
            search : true,
            category : 'test'
        }, {
            field : 'methodId',
            title : 'T_EMC_TMPL_FIELD_ITEM.METHODID',
            width : 100,
            readonly : true,
            type : 'select',
            search : true,
            category : 'emcMethod'
        }, {
            field : 'itemId',
            title : 'T_EMC_TMPL_FIELD_ITEM.ITEMID',
            width : 100,
            readonly : true,
            type : 'select',
            search : true,
            category : 'testItem'
        } ];
    },

    // 模板已关联监测点
    getTmplPointEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_INFO_TMPL_POINT.MONITORPOINTNO'
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_INFO_TMPL_POINT.MONITORPOINTNAME'
        } ];
    },

    // 监测点
    getPointEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'monitorPointNo',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNO'
        }, {
            field : 'monitorPointName',
            title : 'T_EMC_MONITOR_POINT.MONITORPOINTNAME'
        } ];
    }
});
