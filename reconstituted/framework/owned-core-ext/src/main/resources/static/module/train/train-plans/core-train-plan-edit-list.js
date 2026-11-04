coreTrainPlan.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-train-plan-edit-list-grid',
            url : coreTrainPlan.baseUrl + '/queries/raw',
            deleteFormatter : function(row) {
                return row.theme || row.id;
            },
            requestData : {
                processStatus_IN : 'draft,draft_rejected,done'
            },
            columns : coreTrainPlan.getEditListColumns(),
            onCellClick : function(field, row) {
                var readonly = 0;
                if (row.status === 'to_implement' || row.status === 'implemented') {
                    readonly = 1;
                }
                field === 'theme' && _this.loadDetailPage(row.id, readonly);
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.TRAIN.TRAIN_PLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.TRAIN.TRAIN_PLAN.MODAL.TITLE.ADD_TRAIN_PLAN',
                        fields : coreTrainPlan.getBaseInfoFormFields(),
                        url : coreTrainPlan.baseUrl,
                    }).done(function(id) {
                        _this.loadDetailPage(id);
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-train-plan-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    
                    var validFlag = true;
                    for (var i = 0; i < selectedRows.length; i++) {
                        var item = selectedRows[i];
                        if (item.status === 'implemented' || item.status === 'draft_rejected') {
                            validFlag = false;
                            break;
                        }
                    }
                    if (!validFlag) {
                        Gikam.alert("CORE.MODULE.TRAIN.TRAIN_PLAN.TIP.DELETE_VALIDATOR");
                        return;
                    }
                    Gikam.getComp('core-train-plan-edit-list-grid').deleteRows(coreTrainPlan.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var selectedRows = Gikam.getComp('core-train-plan-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
                        return;
                    }
                    
                    var validFlag = true;
                    for (var i = 0; i < selectedRows.length; i++) {
                        var item = selectedRows[i];
                        if (item.status === 'implemented' || item.status === 'to_implement') {
                            validFlag = false;
                            break;
                        }
                    }
                    if (!validFlag) {
                        Gikam.alert("CORE.MODULE.TRAIN.TRAIN_PLAN.TIP.SUBMIT_VALIDATOR");
                        return;
                    }
                    _this.submit();
                }
            } ]
        }
    },

    loadDetailPage : function(id, readonly) {
        workspace.window.load(coreTrainPlan.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            readonly : readonly
        }));
    },

    submit : function() {
        var grid = Gikam.getComp('core-train-plan-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreTrainPlan
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'glp-train-plan-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        var _this = this;
        _this.param = param;
        _this.create();
    }
};