var coreOfficeDs = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/office/dses',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'dsName',
            title : 'T_CORE_OFFICE_DS.DSNAME',
            validators : [ 'strLength[0,42]', 'notEmpty', 'remote[/secure/core/module/office/dses/action/validate-unique]' ],
            preInsert : true,
            colspan : 1
        }, {
            field : 'displayCategory',
            title : 'T_CORE_OFFICE_DS.DISPLAYCATEGORY',
            validators : [ 'notEmpty' ],
            preInsert : true,
            colspan : 1,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeDisplayCategory'
            },
            onChange : function(name, value, index) {
                var form = this;
                var data = form.getData();

                // 禁用自动保存
                form.disableAutoSave();
                workspace.window.showMask(true);

                if (data.displayCategory != 'list') {
                    form.setData({
                        repeatHeader : '0'
                    }, false);
                    this.toFieldsReadonly([ 'repeatHeader' ]);
                } else {
                    this.toFieldsEdit([ 'repeatHeader' ]);
                }

                workspace.window.closeMask();
                form.enableAutoSave();
            }
        }, {
            field : 'repeatHeader',
            title : 'T_CORE_OFFICE_DS.REPEATHEADER',
            colspan : 1,
            type : 'simpleCheckbox'
        }, {
            field : 'sqlContent',
            title : 'T_CORE_OFFICE_DS.SQLCONTENT',
            height : 250,
            colspan : 3,
            type : 'textarea',
            onChange : function(name, value, index) {
                var form = this;
                var data = form.getData();

                if (data.sqlCategory != 'procedure' && !Gikam.isEmpty(data.id)) {
                    // 禁用自动保存
                    form.disableAutoSave();
                    workspace.window.showMask(true);

                    var url = coreOfficeDs.baseUrl + '/{id}/action/update-sql-if-valid'
                    // 对于有效的SQL,进行保存操作
                    Gikam.put(Gikam.printf(url, {
                        id : data.id
                    }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).done(function(r) {
                        var json = JSON.parse(r);
                        if (json.valid) {
                            Gikam.getComp('core-office-ds-field-list-grid').refresh();
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
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'dsName',
            title : 'T_CORE_OFFICE_DS.DSNAME',
            type : 'link',
            width : 400
        }, {
            field : 'displayCategory',
            title : 'T_CORE_OFFICE_DS.DISPLAYCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeDisplayCategory'
            },
            width : 100
        }, {
            field : 'sqlContent',
            title : 'T_CORE_OFFICE_DS.SQLCONTENT',
            type : 'richText',
            sort : false,
            width : 400
        }, Gikam.status.getActiveColumn() ]
    },

    getFieldEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'sqlColumnName',
            title : 'T_CORE_OFFICE_DS_FIELD.SQLCOLUMNNAME',
            width : 150
        }, {
            field : 'showColumnName',
            title : 'T_CORE_OFFICE_DS_FIELD.SHOWCOLUMNNAME',
            validators : [ 'strLength[0,64]', 'notEmpty' ],
            editor : true,
            width : 150
        }, {
            field : 'isPicture',
            title : 'T_CORE_OFFICE_DS_FIELD.ISPICTURE',
            editor : true,
            type : 'simpleCheckbox',
            width : 100,
            onChange : function(field, value, index) {
                var grid = this;
                grid.disableAutoSave();

                if (value == 0) { // 取消图片
                    grid.setData({
                        index : index,
                        isPicture : value
                    });
                    grid.toFieldsReadonly(index, [ 'pictureWidth', 'pictureHeight' ]);
                    grid.toFieldsEdit(index, [ 'writeBack' ]);
                } else { // 勾选图片
                    grid.setData({
                        index : index,
                        writeBack : 0,
                    });
                    grid.toFieldsEdit(index, [ 'pictureWidth', 'pictureHeight' ]);
                    grid.toFieldsReadonly(index, [ 'writeBack', 'updateTableName', 'updateColumnName' ]);
                }

                grid.enableAutoSave();
            }
        }, {
            field : 'pictureWidth',
            title : 'T_CORE_OFFICE_DS_FIELD.PICTUREWIDTH',
            validators : [ 'number', 'numRange[1,1000]' ],
            editor : true,
            width : 100
        }, {
            field : 'pictureHeight',
            title : 'T_CORE_OFFICE_DS_FIELD.PICTUREHEIGHT',
            validators : [ 'number', 'numRange[1,1000]' ],
            editor : true,
            width : 100
        }, {
            field : 'writeBack',
            title : 'T_CORE_OFFICE_DS_FIELD.WRITEBACK',
            editor : true,
            type : 'simpleCheckbox',
            genericQuery : true,
            width : 100,
            onChange : function(field, value, index) {
                var grid = this;
                grid.disableAutoSave();

                if (value == 0) { // 取消反写
                    grid.setData({
                        index : index,
                        writeBack : value
                    });
                    grid.toFieldsReadonly(index, [ 'updateTableName', 'updateColumnName' ]);
                    grid.toFieldsEdit(index, [ 'isPicture' ]);
                } else { // 勾选反写
                    grid.setData({
                        index : index,
                        isPicture : 0,
                    });
                    grid.toFieldsEdit(index, [ 'updateTableName', 'updateColumnName' ]);
                    grid.toFieldsReadonly(index, [ 'isPicture', 'pictureWidth', 'pictureHeight' ]);
                }

                grid.enableAutoSave();
            }
        }, {
            field : 'updateTableName',
            title : 'T_CORE_OFFICE_DS_FIELD.UPDATETABLENAME',
            genericQuery : true,
            editor : true,
            type : 'choose',
            category : 'table',
            single : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        updateTableName : rows[0].id
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    updateTableName : '',
                    updateColumnName : ''
                });
            },
            onChange : function(field, value, index) {
                this.setData({
                    updateColumnName : '',
                    index : index
                })
            },
            width : 200
        }, {
            field : 'updateColumnName',
            title : 'T_CORE_OFFICE_DS_FIELD.UPDATECOLUMNNAME',
            genericQuery : true,
            editor : true,
            type : 'choose',
            category : 'table-column',
            single : true,
            onBeforeChoose : function(index) {
                return {
                    table : this.getData()[index].updateTableName
                }
            },
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        updateColumnName : rows[0].columnName
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    updateColumnName : ''
                });
            },
            width : 200
        } ]
    },

});