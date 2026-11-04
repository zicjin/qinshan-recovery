coreScheduleTaskLog.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.SYS.SCHEDULE_TASKS.TAB.SCHEDULE_TASK_LOG',
                items : [ {
                    type : 'form',
                    id : 'core-schedule-task-log-detail-base-info-form',
                    url : Gikam.printf(coreScheduleTaskLog.baseUrl + '/{id}', {
                        id : _this.param.id
                    }),
                    service : 'coreScheduleTaskLogServiceImpl',
                    fields : coreScheduleTaskLog.getBaseInfoFormFields()
                } ]
            } ]
        }
    },
    
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : workspace.window.$dom,
            readonly : true
        });
        return this;
    },
    
    init : function(param) {
        this.param = param;
        
        this.createPage();
    }
}