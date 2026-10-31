emcLeaveReplenish.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-leave-replenish-search-list-grid',
            url : emcLeaveReplenish.baseUrl + '/queries/searchable',
            columns : emcLeaveReplenish.getSearchListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_INFO_TMPL', Gikam.getComp('emc-leave-replenish-search-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-leave-replenish-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};


