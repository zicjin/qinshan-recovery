emcFileApply.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'Grid',
            id : 'emc-file-apply-search-list-grid',
            url : emcFileApply.baseUrl + '/queries',
            columns : emcFileApply.getSearchListColumns(),
            requestData : {
                filterUserId : '1',
            },
            onCellClick : function(field, row) {
                if (field === 'fileNo' && row.processStatus != 'done') {
                    Gikam.alert('EMC.MODULE.BP.MDM.FILE.FILEAPPLIES.TIP.CAN_NOT_VIEW');
                }
                if (field === 'fileNo' && row.processStatus === 'done') {
                    _this.loadDetailPage(row.fileId);
                }
            },
        }
    },
    
    loadDetailPage : function(id) {
        workspace.window.load(IFM_CONTEXT + '/secure/emc/module/mdm/file/files/page/detail' + Gikam.param({
            id : id,
            operType : 'search'
        }));
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-file-apply-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
