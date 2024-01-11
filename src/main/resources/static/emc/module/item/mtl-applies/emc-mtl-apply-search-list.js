emcMtlApply.searchPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'emc-mtl-apply-search-list-grid',
            url : emcMtlApply.baseUrl + '/queries/searchable',
            service : 'emcMtlApplyServiceImpl',
            columns : emcMtlApply.getAuditListColumns(),
            requestData : {
                processStatus_SEQ : 'done'
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_MTL_APPLY', Gikam.getComp('emc-mtl-apply-search-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-mtl-apply-search-list-layout',
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
