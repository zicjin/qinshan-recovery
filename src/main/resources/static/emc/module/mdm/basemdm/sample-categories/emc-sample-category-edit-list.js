emcSampleCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-category-edit-list-grid',
            url : emcSampleCategory.baseUrl + '/queries/raw',
            requestData : {
                org_filter : '1',
            },
            service : 'emcMonitorTmplServiceImpl',
            columns : emcSampleCategory.getEditListColumns(),
            checkOnActive : true,
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sample-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam(), ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
