coreTrainPlan.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                id : 'core-train-plan-detail-btn-submit',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    if (Gikam.getComp('core-train-plan-detail-base-info-form').validate()) {
                        _this.submit();
                    }
                }
            }, {
                id : 'core-train-plan-detail-btn-pass',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                id : 'core-train-plan-detail-btn-reject',
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }, {
                id : 'core-train-plan-detail-btn-back',
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
            panels : [ {
                title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-train-plan-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreTrainPlanServiceImpl',
                    fields : coreTrainPlan.getBaseInfoFormFields()
                } ]
            } ]
        }
    },

    getTrainPlanUserTabParam : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-train-plan-detail-user-tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'CORE.MODULE.TRAIN.TRAIN_PLAN.TAB.TRAIN_PLAN_USER',
                id : 'core-train-plan-detail-user-panel',
                items : [ {
                    type : 'grid',
                    id : 'core-train-plan-detail-user-list-grid',
                    url : _this.getCompUrl('trainPlanUserGrid'),
                    service : 'coreTrainPlanUserServiceImpl',
                    columns : coreTrainPlan.getUserColumns(),
                    deleteFormatter : function(row) {
                        return row.userName || row.id;
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add-row',
                        onClick : function() {
                            _this.insertUserRow();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove-row',
                        onClick : function() {
                            _this.deleteUserRow();
                        }
                    } ],
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-train-plan-detail-uploader',
                    dbTable : coreTrainPlan.dbTable,
                    bizId : _this.param.id,
                    mutliple : false
                } ]
            } ]
        }
    },

    insertUserRow : function() {
        var _this = this;
        var url = Gikam.printf(coreTrainPlan.baseUrl + '/{id}/users', {
            id : _this.param.id,
        });
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('mult-user').title,
            url : IFM_CONTEXT + Gikam.choose.getConfig('mult-user').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_CORE_TRAIN_PLAN_USER',
                targetUserIdColumn : 'USERID',
                targetFilterColumn : 'TRAINPLANID',
                targetFilterValue : _this.param.id,
                status_EQ : 'activated'
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        userId : item.id,
                        userName : item.userName,
                        trainPlanId : _this.param.id
                    }
                });
                Gikam.getComp('core-train-plan-detail-user-list-grid').insert(url, chooseData);
            }
        });
    },

    deleteUserRow : function() {
        var url = coreTrainPlan.baseUrl + '/users';
        Gikam.getComp('core-train-plan-detail-user-list-grid').deleteRows(url);
    },

    submit : function() {
        var _this = this;
        var form = Gikam.getComp('core-train-plan-detail-base-info-form');
        Gikam.create('workflow').submit({
            pageObject : coreTrainPlan,
            data : [ form.getData() ]
        }).done(function() {
            workspace.window.goBack();
            Gikam.getComp('core-train-plan-edit-list-grid').refresh({
                requestData : {
                    processStatus_IN : 'draft,draft_rejected,done'
                }
            });
        });
    },

    pass : function() {
        var _this = this;
        var form = Gikam.getComp('core-train-plan-detail-base-info-form');
        Gikam.create('workflow').pass({
            pageObject : coreTrainPlan,
            data : [ form.getData() ]
        }).done(function() {
            workspace.window.goBack();
        });
    },

    reject : function() {
        var _this = this;
        if (_this.param.id) {
            var form = Gikam.getComp('core-train-plan-detail-base-info-form');
            Gikam.create('workflow').reject({
                pageObject : coreTrainPlan,
                data : [ form.getData() ]
            }).done(function() {
                workspace.window.goBack();
            })
        }
    },

    urlManager : {
        baseInfoForm : coreTrainPlan.baseUrl + '/{id}',
        trainPlanUserGrid : coreTrainPlan.baseUrl + '/{id}/users/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-train-plan-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-train-plan-detail-user-list-grid' : this.getCompUrl('trainPlanUserGrid')
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

    create : function() {
        var _this = this;
        var options = {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getTrainPlanUserTabParam() ]
            },
            renderTo : workspace.window.$dom,
        };
        if (_this.param.readonly === '1') {
            options.readonly = true;
        } else {
            options.pageConfig = {
                pageObject : coreTrainPlan,
                bizId : _this.param.id
            }
        }
        Gikam.create('layout', options);
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
}