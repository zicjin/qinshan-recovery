var coreAuditConfig = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/audit/audit-configs',
    
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_AUDIT_CONFIG.ID',
            readonly : true,
        }, {
            field : 'name',
            title : 'T_CORE_AUDIT_CONFIG.NAME',
            readonly : true
        }, {
            field : 'auditable',
            title : 'T_CORE_AUDIT_CONFIG.AUDITABLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'confirmFlag'
            },
            readonly : true
        }, {
            field : 'category',
            title : 'T_CORE_AUDIT_CONFIG.CATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'serviceCategory'
            }
        } ];
    },
    
    getPreInsertBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_AUDIT_CONFIG.ID',
            validators : [ 'remote[/secure/core/module/sys/audit/audit-configs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,32]' ],
            preInsert : true
        }, {
            field : 'name',
            title : 'T_CORE_AUDIT_CONFIG.NAME',
            validators : [ 'notEmpty', 'strLength[0,32]' ],
            preInsert : true
        }, {
            field : 'category',
            title : 'T_CORE_AUDIT_CONFIG.CATEGORY',
            type : 'select',
            validators : [ 'notEmpty' ],
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'serviceCategory'
            },
            preInsert : true
        } ]
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_AUDIT_CONFIG.ID',
            readonly : true
        }, {
            field : 'name',
            title : 'T_CORE_AUDIT_CONFIG.NAME',
            preInsert : true
        }, {
            field : 'auditable',
            title : 'T_CORE_AUDIT_CONFIG.AUDITABLE',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'confirmFlag'
            }
        }, {
            field : 'category',
            title : 'T_CORE_AUDIT_CONFIG.CATEGORY',
            type : 'select',
            category : 'sys',
            firstBlank : false,
            param : {
                codeCategoryId : 'serviceCategory'
            },
            preInsert : true
        }, {
            field : 'selectDescSql',
            title : 'T_CORE_AUDIT_CONFIG.SELECTDESCSQL',
            type : 'textarea',
            height : 70,
            colspan : 2
        }, {
            field : 'selectParentSql',
            title : 'T_CORE_AUDIT_CONFIG.SELECTPARENTSQL',
            type : 'textarea',
            height : 70,
            colspan : 2
        } ]
    },
    
    getAuditConfigColumnList : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_AUDIT_CONFIG_COLUMN.COLUMN',
            field : 'column'
        }, {
            title : 'T_CORE_AUDIT_CONFIG_COLUMN.COLUMNNAME',
            field : 'columnName',
            formatter : function(i, v, row) {
                return Gikam.propI18N(row.configId + '.' + row.column);
            }
        }, {
            title : 'T_CORE_AUDIT_CONFIG_COLUMN.SELECTVALUESQL',
            field : 'selectValueSql',
            width : 150,
            editor : true
        }, {
            title : 'T_CORE_AUDIT_CONFIG_COLUMN.AUDITABLE',
            field : 'auditable',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'confirmFlag'
            }
        } ]
    }
});