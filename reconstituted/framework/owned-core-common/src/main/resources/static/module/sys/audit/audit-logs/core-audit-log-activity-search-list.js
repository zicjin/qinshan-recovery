coreAuditLog.queryPage = {

    getQueryTab : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.SEARCH_BY_FILTER',
                items : [ {
                    type : 'layout',
                    resize : false,
                    west : {
                        width : '80%',
                        items : [ this.getQueryForm() ]
                    },
                    center : {
                        padding : '13px 0 0',
                        items : [ this.getBtnToolbar() ]
                    }
                } ]
            } ]
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'search',
                onClick : function() {
                    _this.searchAuditLogList();
                }
            } ]
        }
    },

    getQueryForm : function() {
        return {
            type : 'form',
            columns : 3,
            autoSave : false,
            id : 'core-audit-log-search-list-query-form',
            fields : coreAuditLog.getBaseActivityInfoFormFields(),
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-audit-log-search-list-grid',
            columns : coreAuditLog.getActivitySearchListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    _this.openAuditLogActivityDetail();
                }
            } ]
        }
    },

    // 查询审计信息列表
    searchAuditLogList : function() {

        var formData = Gikam.getComp('core-audit-log-search-list-query-form').getData();

        Gikam.getComp('core-audit-log-search-list-grid').refresh({
            url : coreAuditLog.baseUrl + '/activities/queries',
            requestData : {
                targettable_SEQ : formData.targetTable,
                createdTime_TGOE : formData.startDate,
                createdTime_TLOE : formData.endDate,
                activity_SEQ : formData.activity,
                comment_CISC : formData.comment,
                targetid_CISC : formData.targetId,
                username_CISC : formData.ext$.username,
            }
        })
    },

    // 弹出审计视图页面
    openAuditLogActivityDetail : function() {
        var selectedRows = Gikam.getComp('core-audit-log-search-list-grid').getSelections();
        if (Gikam.isEmpty(selectedRows) || selectedRows.length > 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        };
        // 后续需要优化直接定位到该审计活动
        Gikam.viewAudit(selectedRows[0].ext$.targettable, selectedRows[0].ext$.targetid);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-audit-log-search-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 150,
                items : [ this.getQueryTab() ]
            },
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};