Gikam.extend(coreReportConfig, {

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            title : 'T_CORE_REPORT_CONFIG.ID',
            field : 'id',
            type : 'link',
        }, {
            title : 'T_CORE_REPORT_CONFIG.NAME',
            field : 'name',
        }, {
            field : 'ext$.reportconfigcategory',
            title : '报表分类',
            editor : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG.REMARK',
            field : 'remark',
        } ]
    },


    getMenuRoleColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.rolename',
            title : 'T_CORE_ROLE.ROLENAME',
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_REPORT_CONFIG.ID',
            validators : [ 'remote[/secure/core/module/sys/report-configs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,42]' ],
            preInsert : true,
        }, {
            field : 'name',
            title : 'T_CORE_REPORT_CONFIG.NAME'
        }, {
            field : 'remark',
            title : 'T_CORE_REPORT_CONFIG.REMARK',
            type : 'textarea',
            validators : [ 'strLength[0,256]' ]
        } ]
    },

    getLineListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCONFIGLINENO',
            field : 'reportConfigLineNo',
            readonly : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
            field : 'reportName',
            editor : true,
        }, {
            field : 'reportCategory',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'reportCategory'
            },
        }, {
            field : 'ext$.reportconfiglinecategory',
            title : '报表分类',
            editor : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.FILENAME',
            field : 'ext$.filename'
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.ORDERNO',
            field : 'orderNo',
            type : 'number',
            isInteger : true,
            width : 40,
            hasArrow : true,
            editor : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTPATH',
            field : 'ext$.reportpath',
            type : 'richText',
            editor : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.PARAM',
            field : 'param',
            type : 'richText',
            editor : true
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.DEFAULTPARAM',
            field : 'defaultParam',
            editor : true
        }, {
            field : 'ext$.menuname',
            title : 'T_CORE_REPORT_CONFIG_LINE.MENUID',
            width : 200,
            editor : true,
            type : 'choose',
            category : 'menu',
            onAfterChoose : function(rows, index) {
                if (rows) {
                    var menuId = '', menuName = '';
                    Gikam.each(rows, function(index) {
                        if(menuId) {
                            menuId += ',';
                            menuName += ',';
                        }
                        menuId += rows[index].id;
                        menuName += rows[index].menuName;
                    });
                    var grid = this;
                    grid.setData({
                        index : index,
                        ext$ : {
                            menuid : menuId,
                            menuname : menuName
                        }
                    });
                }
            }
        } ]
    },

    getLineChooseListColumns : function() {
        return [ {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
            field : 'reportName',
            genericQuery : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.FILENAME',
            field : 'ext$.filename',
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.PARAM',
            field : 'param',
        } ]
    },
});
