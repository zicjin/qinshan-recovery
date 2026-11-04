coreScheduleTask.scriptEditPage = {

    getBtnToolbar : function () {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function () {
                    var formData = Gikam.getComp('core-schedule-task-script-edit-form').getData();

                    workspace.window.showMask(true);
                    Gikam.put(coreScheduleTask.baseUrl + '/action/save-script', Gikam.getJsonWrapper({}, [ 'coreScheduleTaskServiceImpl', [ formData ] ])).done(function (id) {
                        Gikam.getLastModal().close();
                    }).always(function () {
                        workspace.window.closeMask();
                    });
                }
            } ]
        }
    },

    getBaseInfoTab : function () {
        var _this = this;
        return {
            type : 'form',
            url : Gikam.printf(coreScheduleTask.baseUrl + '/{id}', {
                id : _this.param.id
            }),
            service : 'coreScheduleTaskServiceImpl',
            id : 'core-schedule-task-script-edit-form',
            columns : 1,
            autoSave : false,
            fields : [ {
                field : 'taskScript',
                titleWidth : 0,
                title : 'T_CORE_SCHEDULE_TASK.TASKSCRIPT',
                type : 'codeEditor',
                language : 'java',
                height : 500
            } ]
        }
    },

    createPage : function () {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function (param) {
        this.param = param;
        this.createPage();
    }
}
