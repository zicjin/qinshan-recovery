var coreImportTmpl = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'tmplCode',
            title : 'T_CORE_IMPORT_TMPL.TMPLCODE',
            validators : [ 'notEmpty' ],
            editor : false
        }, {
            field : 'tmplName',
            title : 'T_CORE_IMPORT_TMPL.TMPLNAME',
            validators : [ 'notEmpty' ],
            editor : true
        }, {
            field : 'tableName',
            title : 'T_CORE_IMPORT_TMPL.TABLENAME',
            validators : [ 'notEmpty' ],
            editor : true,
            type : 'choose',
            category : 'lcdp-table',
            cleanable : false,
            single : true,
            targetFields : [ {
                tableName : 'tableName'
            } ],
            onChange : function(field, value, index, oldValue) {
                Gikam.getAsyncComp('core-import-tmpl-edit-list-tmpl-config-grid').done(function(grid) {
                    coreImportTmpl.editPage.refreshConfigColumns(grid, value);
                    grid.refresh();
                });

            }
        }, {
            field : 'startRowIndex',
            title : 'T_CORE_IMPORT_TMPL.STARTROWINDEX',
            editor : true,
            type : 'number',
            width : 130,
            isInteger : true
        }, {
            field : 'ext$.filename',
            title : 'T_CORE_IMPORT_TMPL.FILENAME',
            type : 'link'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'tmplCode',
            title : 'T_CORE_IMPORT_TMPL.TMPLCODE',
            validators : [ 'unique[/secure/core/module/item/import-tmpls/action/validate-unique]', 'notEmpty' ],
            preInsert : true
        }, {
            field : 'tmplName',
            title : 'T_CORE_IMPORT_TMPL.TMPLNAME',
            validators : [ 'notEmpty' ],
            preInsert : true
        }, {
            field : 'tableName',
            title : 'T_CORE_IMPORT_TMPL.TABLENAME',
            validators : [ 'notEmpty' ],
            preInsert : true,
            type : 'choose',
            category : 'lcdp-table',
            cleanable : false,
            single : true,
            targetFields : [ {
                tableName : 'tableName'
            } ],
        }, ];
    },

    getTmplConfigListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'excelColumnNo',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.EXCELCOLUMNNO',
            validators : [ 'notEmpty' ],
            type : 'number',
            isInteger : true,
            editor : true
        }, {
            field : 'excelColumn',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.EXCELCOLUMN',
            editor : true
        }, {
            field : 'dbField',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.DBFIELD',
            validators : [ 'notEmpty' ],
            editor : true,
            type : 'select'
        }, {
            field : 'fieldType',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.FIELDTYPE',
            type : 'select',
            category : 'importTmplFieldType',
            editor : true,
            onChange : function(field, value, index) {
                if (value === 'select') {
                    this.toFieldsEdit(index, [ 'dataSource' ]);
                } else {
                    this.toFieldsReadonly(index, [ 'dataSource' ]);
                }

            }
        }, {
            field : 'dataSource',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.DATASOURCE',
            type : 'select',
            category : 'codeCategory',
            search : true,
            editor : true,
            onEditorInitReadonly : function(row) {
                if (row.fieldType === 'select') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'defaultValue',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.DEFAULTVALUE',
            editor : true,
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'remark',
            title : 'T_CORE_IMPORT_TMPL_CONFIG.REMARK',
            editor : true,
            validators : [ 'strLength[0,20]' ]
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/import-tmpls'
});
