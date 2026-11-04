var coreAtConfig = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/at-configs',
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_AT_CONFIG.ID',
            readonly : true,
        }, {
            field : 'serviceName',
            title : 'T_CORE_AT_CONFIG.SERVICE_NAME',
            readonly : true
        }, {
            field : 'auditTrail',
            title : 'T_CORE_AT_CONFIG.AUDITTRAIL',
            type : 'select',
            category : 'sys',
            /*firstBlank : false,*/
            param : {
                codeCategoryId : 'confirmFlag'
            },
            readonly : true
        } ]
    },
    
    getAuditTrailableTreeGridColumns : function() {
        return [ {
            title : 'T_CORE_AT_CONFIG.SERVICE_NAME',
            field : 'id',
            type : 'tree',
            width : 280,
            search : true,
            formatter : function(row) {
                return Gikam.propI18N(row.id + '.SERVICE_NAME');
            }
        }, {
            field : 'auditTrail',
            title : 'T_CORE_AT_CONFIG.AUDITTRAIL',
            type : 'select',
            width : 100,
            category : 'sys',
            param : {
                codeCategoryId : 'confirmFlag'
            },
            readonly : true
        } ]
    },
    
    getAtConfigListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_AT_CONFIG_LINE.COLUMNID',
            field : 'columnId',
            search : true,
            width : 200
        }, {
            title : 'T_CORE_AT_CONFIG_LINE.COLUMNNAME',
            field : 'columnName',
            sort : false,
            width : 200,
            formatter : function(i, v, row) {
                return Gikam.propI18N(row.configId + '.' + row.columnId);
            }
        }, {
            title : 'T_CORE_AT_CONFIG_LINE.AUDITTRAIL',
            field : 'auditTrail',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'confirmFlag'
            },
            readonly : true
        } ]
    }
});