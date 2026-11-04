var coreElnDs = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/eln/eln-dses',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'elnDsName',
            title : 'T_CORE_ELN_DS.ELNDSNAME',
            validators : [ 'strLength[0,42]', 'notEmpty', 'remote[/secure/core/module/eln/eln-dses/action/validate-unique]' ],
            preInsert : true,
        }, {
            field : 'scope',
            title : 'T_CORE_ELN_DS.SCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDsScope'
            },
            validators : [ 'notEmpty' ],
            preInsert : true,
        }, {
            field : 'displayCategory',
            title : 'T_CORE_ELN_DS.DISPLAYCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDisplayCategory'
            },
            onChange : function(name, value, index) {
                var form = this;
                var data = form.getData();

                // 禁用自动保存
                form.disableAutoSave();
                workspace.window.showMask(true);

                if (data.displayCategory != 'list') {
                    // 隐藏pivotCategory
                    form.setData({
                        pivotCategory : '0'
                    }, false);
                    this.toFieldsReadonly([ 'pivotCategory' ]);
                    Gikam.getComp('core-eln-ds-field-list-grid') && Gikam.getComp('core-eln-ds-field-list-grid').hideColumns([ 'pivotColumnCategory' ]);
                } else {
                    // 取消隐藏pivotCategory
                    this.toFieldsEdit([ 'pivotCategory' ]);
                    if (data.pivotCategory == '1') {
                        Gikam.getComp('core-eln-ds-field-list-grid').showColumns([ 'pivotColumnCategory' ]);
                    }
                }

                workspace.window.closeMask();
                form.enableAutoSave();
            },
            validators : [ 'notEmpty' ],
            preInsert : true,
        }, {
            field : 'pivotCategory',
            title : 'T_CORE_ELN_DS.PIVOTCATEGORY',
            type : 'simpleCheckbox',
            onChange : function(name, value, index) {
                var form = this;
                // 禁用自动保存
                form.disableAutoSave();
                workspace.window.showMask(true);

                if (value != '1') {
                    // 隐藏字段信息Grid pivotColumnCategory列
                    Gikam.getComp('core-eln-ds-field-list-grid').hideColumns([ 'pivotColumnCategory' ]);
                } else {
                    // 取消隐藏字段信息Grid pivotColumnCategory列
                    Gikam.getComp('core-eln-ds-field-list-grid').showColumns([ 'pivotColumnCategory' ]);
                }

                workspace.window.closeMask();
                form.enableAutoSave();
            }
        }, {
            field : 'sqlContent',
            title : 'T_CORE_ELN_DS.SQLCONTENT',
            height : 300,
            colspan : 4,
            type : 'textarea',
            onChange : function(name, value, index) {
                var form = this;
                var data = form.getData();

                if (data.sqlCategory != 'procedure' && !Gikam.isEmpty(data.id)) {
                    // 禁用自动保存
                    form.disableAutoSave();
                    workspace.window.showMask(true);

                    var url = coreElnDs.baseUrl + '/{id}/action/update-sql-if-valid'
                    // 对于有效的SQL,进行保存操作
                    Gikam.put(Gikam.printf(url, {
                        id : data.id
                    }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).done(function(r) {
                        var json = JSON.parse(r);
                        if (json.valid) {
                            Gikam.getComp('core-eln-ds-field-list-grid').refresh();
                        } else {
                            // SQL不合法提示
                            Gikam.alert(json.message);
                        }
                    }).always(function() {
                        workspace.window.closeMask();
                        form.enableAutoSave();
                    });
                }
            }
        }, {
            field : 'sqlClause',
            title : 'T_CORE_ELN_DS.SQLCLAUSE',
            type : 'textarea',
            colspan : 4
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'scope',
            title : 'T_CORE_ELN_DS.SCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDsScope'
            },
            genericQuery : true,
        }, {
            field : 'elnDsName',
            title : 'T_CORE_ELN_DS.ELNDSNAME',
            type : 'link',
            genericQuery : true,
            width : 200
        }, {
            field : 'sqlContent',
            title : 'T_CORE_ELN_DS.SQLCONTENT',
            type : 'richText',
            sort : false,
            width : 300
        }, {
            field : 'sqlClause',
            title : 'T_CORE_ELN_DS.SQLCLAUSE',
            type : 'textarea',
            width : 300
        }, {
            field : 'displayCategory',
            title : 'T_CORE_ELN_DS.DISPLAYCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDisplayCategory'
            },
            genericQuery : true,
        }, Gikam.status.getActiveColumn() ]
    },

    getFieldEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'tableName',
            title : 'T_CORE_ELN_DS_FIELD.TABLENAME',
            genericQuery : true,
            editor : true,
            type : 'choose',
            category : 'table',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        tableName : rows[0].id
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    tableName : '',
                    columnName : ''
                });
            },
            onChange : function(field, value, index) {
                this.setData({
                    columnName : '',
                    index : index
                })
            },
            width : 300
        }, {
            field : 'columnName',
            title : 'T_CORE_ELN_DS_FIELD.COLUMNNAME',
            genericQuery : true,
            editor : true,
            type : 'choose',
            category : 'table-column',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    table : this.getData()[index].tableName
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        columnName : rows[0].columnName
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    columnName : ''
                });
            },
            width : 200
        }, {
            field : 'elnColumnName',
            title : 'T_CORE_ELN_DS_FIELD.ELNCOLUMNNAME',
            validators : [ 'strLength[0,64]' ],
            genericQuery : true,
            width : 200
        }, {
            field : 'writeBack',
            title : 'T_CORE_ELN_DS_FIELD.WRITEBACK',
            editor : true,
            type : 'simpleCheckbox',
            genericQuery : true,
            width : 100
        }, {
            field : 'pivotColumnCategory',
            title : 'T_CORE_ELN_DS_FIELD.PIVOTCOLUMNCATEGORY',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'pivotColumnCategory'
            },
            width : 200
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'scope',
            title : 'T_CORE_ELN_DS.SCOPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDsScope'
            },
            genericQuery : true,
        }, {
            field : 'elnDsName',
            title : 'T_CORE_ELN_DS.ELNDSNAME',
            validators : [ 'strLength[0,42]', "notEmpty" ],
            genericQuery : true,
            width : 200
        }, {
            field : 'sqlContent',
            title : 'T_CORE_ELN_DS.SQLCONTENT',
            type : 'textarea',
            width : 300
        }, {
            field : 'sqlClause',
            title : 'T_CORE_ELN_DS.SQLCLAUSE',
            type : 'textarea',
            width : 300
        }, {
            field : 'displayCategory',
            title : 'T_CORE_ELN_DS.DISPLAYCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreElnDisplayCategory'
            },
            genericQuery : true,
        } ]
    },

});
