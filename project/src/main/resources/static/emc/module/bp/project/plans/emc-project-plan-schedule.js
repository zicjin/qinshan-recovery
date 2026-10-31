emcProjectPlan.schedulePage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'save',
                onClick : function() {
                    _this.update();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    baseForm : null,
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.PROJECT.PLANS.TITLE.EDIT_SCHEDULE',
                items : [ {
                    type : 'form',
                    id : 'emc-project-plan-schedule-base-info-form',
                    columns : 1,
                    fields : emcProjectPlan.getScheduleInfoFormFields(),
                    onRendered : function() {
                        _this.baseForm = this;
                        this.setData({
                            id : _this.param.id,
                            startTime : _this.param.startTime,
                            endTime : _this.param.endTime,
                            cron : _this.param.cron,
                            projName : _this.param.projName,
                        });
                    },
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

    update : function() {
        var _this = this;
        if (this.baseForm.validate()) {
            var row = this.baseForm.getData();
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(emcProject.baseUrl + '/schedules/change', Gikam.getJsonWrapper.apply(null, [ null, [ null, [ row ] ] ])).done(function() {
                Gikam.getComp('emc-project-plan-edit-list-grid').refresh();
            }).always(function () {
                Gikam.getLastModal().window.closeMask();
                Gikam.getLastModal().close();
            });
        }
    },
}