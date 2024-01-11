var emcPropertyTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/property-tmpls',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_EMC_PROPERTY_TMPL.ID'
        }, {
            field : 'templateName',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATENAME',
            validators : [ 'strLength[0,21]' ],
            editor : true
        }, {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'templateUse'
            },
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_PROPERTY_TMPL.ID',
            readOnly : true,
        }, {
            field : 'templateName',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATENAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ],
        }, {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'templateUse'
            }
        } ];
    },

    getPropertyField : function() {
        return [ {
            checkbox : true
        }, {
            field : 'internalField',
            title : 'T_EMC_PROPERTY_FIELD.INTERNALFIELD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'internalField'
            },
            preInsert : true
        }, {
            field : 'fieldName',
            title : 'T_EMC_PROPERTY_FIELD.FIELDNAME',
            preInsert : true,
            validators : [ 'strLength[0,22]' ],
        }, {
            field : 'fieldType',
            title : 'T_EMC_PROPERTY_FIELD.FIELDTYPE',
            preInsert : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'fieldType'
            }
        } ];
    },

    getPropertyFieldColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'internalField',
            title : 'T_EMC_PROPERTY_FIELD.INTERNALFIELD',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'internalField'
            },
        }, {
            field : 'fieldName',
            title : 'T_EMC_PROPERTY_FIELD.FIELDNAME',
            validators : [ 'strLength[0,60]' ],
            editor : true,
        }, {
            field : 'fieldType',
            title : 'T_EMC_PROPERTY_FIELD.FIELDTYPE',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'fieldType'
            }
        }, {
            field : 'orderNo',
            title : 'T_EMC_PROPERTY_FIELD.ORDERNO',
            validators : [ 'integer','strLength[0,6]' ],
            editor : true,
            type : 'number',
            hasArrow : true
        }, {
            field : 'fieldDetail',
            title : 'T_EMC_PROPERTY_FIELD.DETAIL',
            width : 100,
            formatter : function(i, v, row) {
                return '<a href="javascript:;" onclick="emcPropertyTmpl.editPage.loadDetailPage(\'' + row.id + ',' + row.fieldType + '\')">详情</a>';
            }
        } ];
    },

    getFieldFormFields : function() {
        return [ {
            title : 'T_EMC_PROPERTY_FIELD.INTERNALFIELD',
            field : 'internalField',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'internalField'
            },
            preInsert : true
        }, {
            title : 'T_EMC_PROPERTY_FIELD.FIELDNAME',
            field : 'fieldName',
            preInsert : true,
            validators : [ 'strLength[0,10]' ],
        }, {
            title : 'T_EMC_PROPERTY_FIELD.FIELDTYPE',
            field : 'fieldType',
            readOnly : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'fieldType'
            }
        }, {
            title : 'T_EMC_PROPERTY_FIELD.REQUIRED',
            field : 'required',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_PROPERTY_TMPL.ID',
            type : 'link',
        }, {
            field : 'templateName',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATENAME',
        }, {
            field : 'templateUse',
            title : 'T_EMC_PROPERTY_TMPL.TEMPLATEUSE',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'templateUse'
            }
        } ];
    },

});
