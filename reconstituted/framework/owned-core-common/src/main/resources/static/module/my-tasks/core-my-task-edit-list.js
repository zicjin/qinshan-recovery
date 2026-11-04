coreMyTask.editPage = {
    getMyTask : function() {
        return {
            type : 'schedule',
            id : 'core-my-task-edit-list-schedule',
            onRendered : function(schedule) {
                Gikam.post(coreMyTask.baseUrl + '/queries', Gikam.getJsonWrapper({
                    f : {
                        createdById_SEQ : workspace.user.id
                    },
                    n : -1
                })).done(function(data) {
                    var scheduleItem = [];
                    if (data.rows.length > 0) {
                        scheduleItem = data.rows.map(function(row) {
                            return {
                                id : row.id,
                                start_date : row.taskStartTime,
                                end_date : row.taskEndTime,
                                text : row.taskDesc
                            }
                        });
                    }
                    schedule.parse(scheduleItem);
                });
            }
        }
    },

    create : function() {
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getMyTask() ]
            }
        });
    },

    init : function() {
        this.create();
    }
}