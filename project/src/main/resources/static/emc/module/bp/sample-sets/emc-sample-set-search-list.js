emcSampleSet.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-set-search-list-grid',
            url : emcSampleSet.baseUrl + '/queries/searchable',
            columns : emcSampleSet.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sample-set-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcSampleSet.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
