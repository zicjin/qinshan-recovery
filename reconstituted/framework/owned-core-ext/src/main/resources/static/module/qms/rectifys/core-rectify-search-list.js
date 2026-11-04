coreRectify.searchPage = {

    getGridParam : function() {
        return {
            type : 'grid',
            id : 'core-rectify-search-list-grid',
            url : coreRectify.baseUrl + '/queries',
            columns : coreRectify.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'rectifyNo' && coreRectify.loadDetailPage(row.id, 'detailPageReview');
            }
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-rectify-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [this.getGridParam()]
            }
        });
    },

    init : function() {
        this.create();
    }
};
