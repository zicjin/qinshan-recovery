var emcCert = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/certs',

    empBaseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/emp/emps',

    getEmpEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
            title : 'T_EMC_EMP.ID'
        }, {
            field : 'userName',
            title : 'T_EMC_EMP.USERNAME'
        }, {
            field : 'ext$.departmentname',
            title : 'T_EMC_CERT.DEPARTMENTNAME'
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'certNo',
            title : 'T_EMC_CERT.CERTNO',
            editor : true,
            validators : [ 'strLength[0, 20]' ],
            onBeforeEditorRender : function(row, field) {
                if (row.status === 'auth') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'certName',
            title : 'T_EMC_CERT.CERTNAME',
            editor : true,
            validators : [ 'strLength[0,21]' ],
            onBeforeEditorRender : function(row, field) {
                if (row.status === 'auth') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'authDate',
            title : 'T_EMC_CERT.AUTHDATE',
            type : 'date',
            editor : true,
            onBeforeEditorRender : function(row, field) {
                if (row.status === 'auth') {
                    return false;
                } else {
                    return true;
                }
            },
        }, {
            field : 'expireDate',
            title : 'T_EMC_CERT.EXPIREDATE',
            type : 'date',
            editor : true,
            onBeforeEditorRender : function(row, field) {
                if (row.status === 'auth') {
                    return false;
                } else {
                    return true;
                }
            }
        }, {
            field : 'warningDays',
            title : 'T_EMC_CERT.WARNINGDAYS',
            editor : true,
            validators : [ 'number', 'strLength[0,20]' ],
            onBeforeEditorRender : function(row, field) {
                if (row.status === 'auth') {
                    return false;
                } else {
                    return true;
                }
            },
        }, {
            field : 'status',
            title : 'T_EMC_CERT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'neworauth'
            },
        }, ];
    },

    getCertTestMethodColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_CERT_TEST_METHOD.TESTID'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_CERT_TEST_METHOD.METHODID'
        }, {
            field : 'remark',
            title : 'T_EMC_CERT_TEST_METHOD.REMARK',
            editor : true,
            validators : [ 'strLength[0,170]' ],
        } ];
    },

    getCertMonitorAbilityColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitorobjid',
            title : 'T_EMC_CERT_MONITOR_ABILITY.MONITOROBJID'
        }, {
            field : 'ext$.monitorobjname',
            title : 'T_EMC_CERT_MONITOR_ABILITY.MONITOROBJNAME'
        }, {
            field : 'ext$.methodsource',
            title : 'T_EMC_CERT_MONITOR_ABILITY.METHODSOURCE'
        }, {
            field : 'ext$.instruction',
            title : 'T_EMC_CERT_MONITOR_ABILITY.INSTRUCTION'
        } ];
    },

    getCertLogColumns : function() {
        return [ {
            field : 'ext$.username',
            title : 'T_EMC_CERT_LOG.LOGINUSERID'
        }, {
            field : 'loginUserTime',
            title : 'T_EMC_CERT_LOG.LOGINUSERTIME'
        }, {
            field : 'logContent',
            title : 'T_EMC_CERT_LOG.LOGCONTENT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'neworauth'
            },
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'empId',
            title : 'T_EMC_CERT.EMPID',
        }, {
            field : 'ext$.username',
            title : 'T_EMC_CERT.USERNAME',
        }, {
            field : 'certNo',
            title : 'T_EMC_CERT.CERTNO',
        }, {
            field : 'certName',
            title : 'T_EMC_CERT.CERTNAME',
        }, {
            field : 'authDate',
            title : 'T_EMC_CERT.AUTHDATE',
        }, {
            field : 'expireDate',
            title : 'T_EMC_CERT.EXPIREDATE',
            type : 'date',
        }, ];
    },

});
