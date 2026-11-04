var coreReportConfig = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/report-configs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_REPORT_CONFIG.ID',
            field : 'id',
            type : 'link',
            genericQuery : true,
            width : 200
        }, {
            title : 'T_CORE_REPORT_CONFIG.NAME',
            field : 'name',
            genericQuery : true,
            width : 300
        }, {
            title : 'T_CORE_REPORT_CONFIG.REMARK',
            field : 'remark',
            width : 300
        }, {
            title : 'T_CORE_REPORT_CONFIG.CATEGORY',
            field : 'category',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportCategory'
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_CORE_REPORT_CONFIG.ID',
            field : 'id',
            type : 'link',
            width : 200,
            genericQuery : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG.NAME',
            field : 'name',
            width : 300,
            genericQuery : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG.REMARK',
            width : 300,
            field : 'remark',
        }, {
            title : 'T_CORE_REPORT_CONFIG.CATEGORY',
            field : 'category',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportCategory'
            }
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
        }, {
            field : 'category',
            title : 'T_CORE_REPORT_CONFIG.CATEGORY',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'reportCategory'
            }
        } ]
    },

    getLineSelectFormFields : function() {
        return [ {
            field : 'reportName',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
            type : 'select',
            category : 'reportConfigLine',
            param : {
                reportConfigId : coreReportConfig.lineSelectPage.param.reportConfigId,
                filter_bizCategory : 'master'
            }
        } ]
    },

    getLineListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            index : true
        }, Gikam.status.getActiveColumn(), {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCONFIGLINENO',
            field : 'reportConfigLineNo'
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
            field : 'reportName',
            width : 200,
            editor : true,
            validators : [ 'notEmpty', 'strLength(0, 90]', ]
        }, {
            field : 'reportCategory',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCATEGORY',
            type : 'select',
            category : 'report-config-category',
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.FILENAME',
            field : 'ext$.filename',
            width : 200
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.DATACENTERREPORT',
            field : 'dataCenterReportName',
            width : 200,
            type : 'choose',
            category : 'data-center-report',
            single : true,
            editor : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    if (rows[0].nodeType === 'folder') { // 不能选择目录文件夹节点
                        Gikam.alert('CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_CAN_NOT_CHOOSE_FOLDER_CENTER_REPORT_DATA');
                        return;
                    }
                    this.setData({
                        index : index,
                        dataCenterReportId : rows[0].id,
                        dataCenterReportName : rows[0].nodeName,
                        dataCenterReportUrl : Gikam.dataCenter.url + Gikam.printf('/gateway/df/open-api/v1/forms/{id}/action/view', {
                            id : rows[0].id
                        })
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    dataCenterReportId : '',
                    dataCenterReportName : '',
                    dataCenterReportUrl : ''
                });
            }
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.PARAM',
            field : 'param',
            editor : true,
            width : 200
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.DEFAULTPARAM',
            field : 'defaultParam',
            editor : true,
            width : 200
        } ]
    },

    getLinePreInsertFormFields : function() {
        return [ {
            field : 'reportConfigLineNo',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCONFIGLINENO',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength(0, 36]', 'remote[/secure/core/module/sys/report-configs/lines/action/validate-unique]', ]
        }, {
            field : 'reportName',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength(0, 90]', ]
        }, {
            field : 'reportCategory',
            title : 'T_CORE_REPORT_CONFIG_LINE.REPORTCATEGORY',
            preInsert : true,
            type : 'select',
            category : 'report-config-category',
            firstBlank : false
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.PARAM',
            field : 'param'
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.DEFAULTPARAM',
            field : 'defaultParam'
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

    getUserPermissionListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG.USERID',
            field : 'userId',
        }, {
            title : 'T_CORE_REPORT_CONFIG.USERNAME',
            field : 'ext$.username',
            genericQuery : true,
        } ]
    },

    getRolePermissionListColumns : function() {
        return [ {
            checkbox : true,
        }, {
            title : 'T_CORE_REPORT_CONFIG.ROLENAME',
            field : 'ext$.rolename'
        } ]
    },

    getSubReportEditListColumns : function() {
        return [ {
            checkbox : true,
            field : 'subReportCheckBox'
        }, {
            title : 'T_CORE_REPORT_CONFIG_LINE.FILENAME',
            field : 'name'
        } ]
    },
});