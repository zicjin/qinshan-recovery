coreMessage.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-message-search-list-grid',
            url : coreMessage.baseUrl + '/queries/searchable',
            columns : coreMessage.getSearchListColumns(),
            genericQueryFields : [ {
                field : 'ext$.receivedflag'
            }, {
                field : 'messageSubject'
            } ],
            onCellClick : function(field, row) {
                field === 'messageSubject' && _this.loadDetailPage(row.ext$.messageid, row.id, row.ext$.receivedflag);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id, receipentId, receivedFlag) {
        workspace.window.load(coreMessage.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            activatedFlag : '1',
            receipentId : receivedFlag == 0 ? receipentId : '',
            source : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};