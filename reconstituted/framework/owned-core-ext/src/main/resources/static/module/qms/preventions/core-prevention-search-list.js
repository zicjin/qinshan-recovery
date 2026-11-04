corePrevention.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-prevention-search-list-grid',
            url : corePrevention.baseUrl + '/queries',
            columns : corePrevention.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'preventionNo' && corePrevention.loadDetailPage(row.id, 'detailPageReview');
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-prevention-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [this.getGridParam()]
            }
        });
    },

    init : function() {
        this.create();
    }
}
;
