var coreOfficeTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/office/tmpls',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'tmplName',
            title : 'T_CORE_OFFICE_TMPL.TMPLNAME',
            validators : [ 'strLength(0,170]', 'notEmpty' ],
            colspan : 2,
            preInsert : true,
        }, {
            field : 'bizCategory',
            title : 'T_CORE_OFFICE_TMPL.BIZCATEGORY',
            preInsert : true,
            colspan : 1,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeBizCategory'
            }
        }, {
            field : 'documentType',
            title : 'T_CORE_OFFICE_TMPL.DOCUMENTTYPE',
            validators : [ 'notEmpty' ],
            preInsert : true,
            colspan : 1,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeDocumentType'
            }
        }, {
            field : 'remark',
            title : 'T_CORE_OFFICE_TMPL.REMARK',
            validators : [ 'strLength[0,170]', "notEmpty" ],
            type : 'textarea',
            colspan : 2,
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'tmplName',
            title : 'T_CORE_OFFICE_TMPL.TMPLNAME',
            validators : [ 'strLength[0,45]', "notEmpty" ],
            editor : true,
            width : 200
        }, {
            field : 'bizCategory',
            title : 'T_CORE_OFFICE_TMPL.BIZCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeBizCategory'
            },
            width : 150
        }, {
            field : 'documentType',
            title : 'T_CORE_OFFICE_TMPL.DOCUMENTTYPE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'coreOfficeDocumentType'
            },
            width : 100
        }, {
            field : 'ext$.filename',
            title : 'T_CORE_OFFICE_TMPL.FILENAME',
            width : 250
        }, {
            field : 'remark',
            title : 'T_CORE_OFFICE_TMPL.REMARK',
            validators : [ 'strLength[0,45]'],
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_CORE_OFFICE_TMPL.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_OFFICE_TMPL.CREATEDTIME',
        }, Gikam.status.getActiveColumn() ]
    },

    getOfficeTmplEditListColumns : function() {
        return [ {
            field : 'tmplName',
            title : 'T_CORE_OFFICE_TMPL.TMPLNAME',
            genericQuery : true,
        } ];
    },

    workflow : {
        dbTable : 'T_CORE_OFFICE_TMPL',
    }
});
