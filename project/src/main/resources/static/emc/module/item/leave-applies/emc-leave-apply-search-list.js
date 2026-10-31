emcLeaveApply.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-leave-apply-search-list-grid',
            url : emcLeaveApply.baseUrl + '/queries/searchable',
            service : 'emcLeaveApplyServiceImpl',
            columns : emcLeaveApply.getSearchListColumns(_this.param.oper_type),
            requestData : {
                processStatus_SEQ : _this.param.oper_type == 'replenish' ? 'done':''
            },
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_LEAVE_APPLY', Gikam.getComp('emc-leave-apply-search-list-grid').getSelections());
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.LEAVEAPPLIES.MODAL.DETAIL',
            url : emcLeaveApply.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                operType : _this.param.oper_type
            }),
            width : 900,
            height : 300,
            onAfterClose : function() {
                if (_this.param.oper_type !== 'replenish') {
                    return;
                }
                Gikam.getComp('emc-leave-apply-search-list-grid').refresh();
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-leave-apply-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
