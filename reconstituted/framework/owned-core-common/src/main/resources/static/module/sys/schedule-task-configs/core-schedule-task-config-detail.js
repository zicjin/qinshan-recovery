coreScheduleTaskConfig.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_CORE_SCHEDULE_TASK_CONFIG', _this.param.id);
                }
            }, {
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
            width : Gikam.width.M,
            panels : [ {
                title : 'CORE.MODULE.SYS.SCHEDULE_TASKS.TAB.SCHEDULE_TASK_CONFIG',
                items : [ {
                    type : 'form',
                    id : 'core-schedule-task-config-detail-base-info-form',
                    url : Gikam.printf(coreScheduleTaskConfig.baseUrl + '/{id}', {
                        id : _this.param.id
                    }),
                    service : 'coreScheduleTaskConfigServiceImpl',
                    autoSave : true,
                    fields : coreScheduleTaskConfig.getBaseInfoFormFields(),
                    onLoadSuccess : function() {
                        Gikam.getComp('core-schedule-task-config-detail-base-info-form').toFieldsReadonly([ 'uid', 'param', 'remark' ]);
                    }
                } ]
            } ]
        }
    },

    createPage : function() {
        var _this = this;

        Gikam.getJson(coreScheduleTaskConfig.baseUrl + '/' + this.param.id + '/info/status').done(function(status) {
            _this.param.type = status.ext$.type;
            _this.param.readonly = (status.activeStatus.activatedFlag === '1' && status.ext$.type === 'service');

            Gikam.create('layout', {
                center : {
                    items : [ _this.getBtnToolbar(), _this.getBaseInfoTab() ]
                },
                renderTo : workspace.window.$dom,
                readonly : _this.param.readonly
            });
        })

        return this;
    },

    urlManager : {
        baseInfoForm : coreScheduleTaskConfig.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-schedule-task-config-detail-base-info-form' : this.getCompUrl('baseInfoForm')
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    init : function(param) {
        this.param = param;

        this.createPage();
    }
}