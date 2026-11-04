coreApplyComplaint.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-apply-complaint-edit-list-grid',
            url : coreApplyComplaint.baseUrl + '/queries/raw',
            columns : coreApplyComplaint.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'applyComplaintNo' && _this.loadDetailPage(row.id);
            },
            deleteFormatter : function(row) {
                return row.applyComplaintNo;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertApplyComplain();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-apply-complaint-edit-list-grid').deleteRows(coreApplyComplaint.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    var grid = Gikam.getComp('core-apply-complaint-edit-list-grid');
                    Gikam.create('workflow').submit({
                        data : grid.getSelections(),
                        pageObject : coreApplyComplaint
                    }).done(function() {
                        grid.refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-apply-complaint-edit-list-grid').getSelections();
                    if (rows.length != 1) {
                        Gikam.alert('QMS.MODULE.ANNUALPLAN.PRINT.ONLY_SELECT_ONE_ROW');
                        return;
                    }
                    Gikam.viewReport('QMS_APPLY_COMPLAINT', {
                        id : rows[0].id
                    });
                }
            } ]
        }
    },

    insertApplyComplain : function() {
        var modal = Gikam.create('modal', {
            title: 'CORE.MODULE.QMS.APPLYCOMPLAINTS.MODAL.ADD',
            width: 400,
            height: 350
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type : 'form',
                    id : 'core-apply-complain-insert-form',
                    columns : 1,
                    fields : coreApplyComplaint.getInsertApplyComplainFields()
                }, {
                    type: 'btnToolbar',
                    items: [ {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var form = Gikam.getComp('core-apply-complain-insert-form');
                            if (Gikam.isFalse(form.validate()))
                                return;
                            var insertData = form.getData();
                            workspace.window.showMask(true);
                            Gikam.post(coreApplyComplaint.baseUrl, Gikam.getJsonWrapper({}, ['', [ insertData ]])).done(function(item) {
                                modal.close();
                                Gikam.getComp('core-apply-complaint-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        onClick : function() {
                            modal.close();
                        }
                    } ]
                } ]
            }
        });
    },
    
    loadDetailPage : function(id) {
        workspace.window.load(coreApplyComplaint.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'edit'
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-apply-complaint-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
