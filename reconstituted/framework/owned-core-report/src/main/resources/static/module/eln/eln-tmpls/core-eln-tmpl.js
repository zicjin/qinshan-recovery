var coreElnTmpl = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/eln/eln-tmpls',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'elnTmplName',
            title : 'T_CORE_ELN_TMPL.ELNTMPLNAME',
            validators : [ 'strLength(0,170]', 'notEmpty' ],
            colspan : 2,
            preInsert : true,
        }, {
            field : 'writeBackCategory',
            title : 'T_CORE_ELN_TMPL.WRITEBACKCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'writeBackCategory'
            },
        }, {
            field : 'bizCategory',
            title : 'T_CORE_ELN_TMPL.BIZCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'bizCategory'
            },
        }, {
            field : 'script',
            title : 'T_CORE_ELN_TMPL.SCRIPT',
            type : 'textarea',
            colspan : 2,
            height : 200,
        }, {
            field : 'remark',
            title : 'T_CORE_ELN_TMPL.REMARK',
            validators : [ 'strLength(0,170]' ],
            type : 'textarea',
            colspan : 2,
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'activatedFlag',
            title : 'GIKAM.ACTIVE.TITLE.STATUS',
            type : 'select',
            editor : true,
            items : [ {
                text : '',
                value : ''
            }, {
                text : Gikam.propI18N('GIKAM.ACTIVE.STATUS.NO'),
                value : '0'
            }, {
                text : Gikam.propI18N('GIKAM.ACTIVE.STATUS.YES'),
                value : '1'
            } ],
            width : 100
        }, {
            field : 'elnTmplName',
            title : 'T_CORE_ELN_TMPL.ELNTMPLNAME',
            validators : [ 'strLength[0,170]', "notEmpty" ],
            editor : true,
            width : 200
        }, {
            field : 'writeBackCategory',
            title : 'T_CORE_ELN_TMPL.WRITEBACKCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'writeBackCategory'
            },
            editor : true,
        }, {
            field : 'bizCategory',
            title : 'T_CORE_ELN_TMPL.BIZCATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'bizCategory'
            },
            editor : true,
        }, {
            field : 'remark',
            title : 'T_CORE_ELN_TMPL.REMARK',
            editor : true,
        }, {
            field : 'createdByName',
            title : 'T_CORE_ELN_TMPL.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_ELN_TMPL.CREATEDTIME',
        } ]
    },

    getDsEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.elndsname',
            title : 'T_CORE_ELN_TMPL_DS.ELNDSNAME',
            genericQuery : true,
            width : 200
        } ]
    },

    getVersionEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'versionNo',
            title : 'T_CORE_ELN_TMPL_VERSION.VERSIONNO',
            genericQuery : true,
        }, {
            field : 'createdByName',
            title : 'T_CORE_ELN_TMPL_VERSION.CREATEDBYNAME',
        }, {
            field : 'createdTime',
            title : 'T_CORE_ELN_TMPL_VERSION.CREATEDTIME',
        }, {
            field : 'activatedFlag',
            title : 'GIKAM.ACTIVE.TITLE.STATUS',
            type : 'select',
            editor : true,
            items : [ {
                text : '',
                value : ''
            }, {
                text : Gikam.propI18N('GIKAM.ACTIVE.STATUS.NO'),
                value : '0'
            }, {
                text : Gikam.propI18N('GIKAM.ACTIVE.STATUS.YES'),
                value : '1'
            } ],
            width : 100
        } ]
    },

    getElnTmplEditListColumns : function() {
        return [ {
            field : 'elnTmplName',
            title : 'T_CORE_ELN_TMPL.ELNTMPLNAME',
            genericQuery : true,
        } ];
    },

    getTestTmplInfoFormFields : function() {
        return [ {
            field : 'elnTmplParam',
            title : 'T_CORE_ELN_TMPL_VERSION.ELNTMPLPARAM',
            type : 'textarea',
            height : '150',
            validators : [ 'strLength(0,85]', 'notEmpty' ],
            colspan : 3,
        } ]
    },

});
