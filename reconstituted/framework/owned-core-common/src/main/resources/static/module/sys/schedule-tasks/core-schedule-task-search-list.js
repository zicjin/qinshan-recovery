coreScheduleTask.searchListPage = {
    getTaskGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-schedule-task-list-grid',
            url : IFM_CONTEXT + '/secure/core/module/sys/schedule-tasks/queries/searchable',
            columns : coreScheduleTask.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadTaskDetailPage(row.id);
            }
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getTaskGridParam() ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    loadTaskDetailPage : function(id) {
        if (id) {
            workspace.window.load(IFM_CONTEXT + '/secure/core/module/sys/schedule-tasks/page/detail' + Gikam.param({
                id : id
            }));
        }
    },

    init : function() {
        this.create();
    }
};