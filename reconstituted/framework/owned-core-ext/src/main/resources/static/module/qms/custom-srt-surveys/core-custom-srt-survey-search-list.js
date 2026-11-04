coreCustomSrtSurvey.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-custom-srt-survey-search-list-grid',
            url : coreCustomSrtSurvey.baseUrl + '/queries/searchable',
            columns : coreCustomSrtSurvey.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
                Gikam.getAsyncComp('core-custom-srt-surveys-attachment-panel-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-custom-srt-survey-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreCustomSrtSurvey.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};
