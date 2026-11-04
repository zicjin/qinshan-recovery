var corePageField = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/module/item/page-fields',

    getPageFieldColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'pfName',
            title : 'T_CORE_PAGE_FIELD.PFNAME',
            validators : [ 'strLength[0,40]' ],
            preInsert : true,
            editor : true
        }, {
            field : 'createdByName',
            title : 'T_CORE_PAGE_FIELD.CREATEDBYNAME'
        } ];
    },

    getFieldTypeColumn : function() {
        return {
            field : 'type',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.TYPE',
            editor : true,
            type : 'select',
            category : 'columnType',
            onChange : function(field, value, index) {
                if (value === 'CHOOSE') {
                    this.toFieldsReadonly(index, [ 'selectDs' ]);
                    this.toFieldsEdit(index, [ 'chooseDs' ]);
                    this.setData({
                        index : index,
                        selectDs : '',
                        type : value
                    });
                } else if (value === 'SELECT') {
                    this.toFieldsReadonly(index, [ 'chooseDs' ]);
                    this.toFieldsEdit(index, [ 'selectDs' ]);
                    this.setData({
                        index : index,
                        chooseDs : '',
                        type : value
                    });
                } else {
                    this.toFieldsReadonly(index, [ 'selectDs', 'chooseDs' ]);
                    this.setData({
                        index : index,
                        selectDs : '',
                        chooseDs : '',
                        type : value
                    });
                }
            }
        }
    },

    getPageCompColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'compId',
            title : 'T_CORE_PAGE_FIELD_COMP.COMPID',
            validators : [ 'strLength[0,60]' ],
            width : 200,
            editor : true
        }, {
            field : 'tableName',
            title : 'T_CORE_PAGE_FIELD_COMP.TABLENAME',
            width : 300
        }, {
            field : 'compCategory',
            title : 'T_CORE_PAGE_FIELD_COMP.COMPCATEGORY',
            editor : true,
            type : 'select',
            category : 'pageCompCategory',
            onChange : function(field, value, rowIndex) {
                corePageField.editPage.changeCompCategory(rowIndex);
            }
        } ];
    },

    getPageFormFieldColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fieldName',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.FIELDNAME'
        }, {
            field : 'field',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.FIELD',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'title',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.TITLE',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, corePageField.getFieldTypeColumn(), {
            field : 'selectDs',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.SELECTDS',
            editor : true,
            type : 'select',
            category : 'select-config',
            width : 150,
            onEditorInitReadonly : function(row) {
                if (row.type === 'SELECT') {
                    return false;
                } else if (row.type === 'CHOOSE') {
                    return true;
                }
                return true;
            }
        }, {
            field : 'chooseDs',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.CHOOSEDS',
            editor : true,
            type : 'select',
            category : 'choose-config',
            width : 150,
            onEditorInitReadonly : function(row) {
                if (row.type === 'SELECT') {
                    return true;
                } else if (row.type === 'CHOOSE') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'colspan',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.COLSPAN',
            editor : true,
            type : 'number',
            isInteger : true,
        }, {
            field : 'readonly',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.READONLY',
            editor : true,
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'value',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.VALUE',
            editor : true
        }, {
            field : 'extConf',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.EXTCONF',
            editor : true
        } ];
    },

    getPageGridFieldColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'fieldName',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.FIELDNAME'
        }, {
            field : 'field',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.FIELD',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'title',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.TITLE',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, corePageField.getFieldTypeColumn(), {
            field : 'selectDs',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.SELECTDS',
            editor : true,
            type : 'select',
            category : 'select-config',
            width : 150,
            onEditorInitReadonly : function(row) {
                if (row.type === 'SELECT') {
                    return false;
                } else if (row.type === 'CHOOSE') {
                    return true;
                }
                return true;
            }
        }, {
            field : 'chooseDs',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.CHOOSEDS',
            editor : true,
            type : 'select',
            category : 'choose-config',
            width : 150,
            onEditorInitReadonly : function(row) {
                if (row.type === 'SELECT') {
                    return true;
                } else if (row.type === 'CHOOSE') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'width',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.WIDTH',
            editor : true,
            isInteger : true,
            type : 'number',
        }, {
            field : 'editor',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.EDITOR',
            editor : true,
            type : 'select',
            category : 'yesorno'
        }, {
            field : 'extConf',
            title : 'T_CORE_PAGE_FIELD_COMP_FIELD.EXTCONF',
            editor : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'pfName',
            title : 'T_CORE_PAGE_FIELD.PFNAME'
        }, {
            field : 'createdByName',
            title : 'T_CORE_PAGE_FIELD.CREATEDBYNAME'
        } ];
    }

});
