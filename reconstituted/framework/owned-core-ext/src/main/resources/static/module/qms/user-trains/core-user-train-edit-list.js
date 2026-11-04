coreUserTrain.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-user-train-edit-list-grid',
            url : coreUserTrain.baseUrl + '/queries/raw',
            columns : coreUserTrain.getEditListColumns(),
            activateFirstRow : true,
            deleteFormatter : function(row) {
                return row.trainNo;
            },
            onCellClick : function(field, row) {
                field === 'trainNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('qms-user-train-edit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-user-train-edit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.ADD',
                icon : 'add',
                onClick : function() {
                    Gikam.postSync(coreUserTrain.baseUrl, Gikam.getJsonWrapper({}, [ '', [{}] ])).done(function(item) {
                        _this.loadDetailPage(item);
                    });
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-user-train-edit-list-grid').deleteRows(coreUserTrain.baseUrl).done(function() {
                        Gikam.getComp('qms-user-train-edit-list-uploader').grid.cleanData();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            }/*, {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.RETURN',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }*/  ]
        }
    },

    submit : function() {
        var grid = Gikam.getComp('core-user-train-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : coreUserTrain
        }).done(function() {
            grid.refresh();
        });
    },
    
    reject : function() {
        var grid = Gikam.getComp('core-user-train-edit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreUserTrain
        }).done(function() {
            grid.refresh();
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreUserTrain.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'edit'
        }));
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-user-train-edit-list-uploader',
                    dbTable : 'T_CORE_USER_TRAIN',
                    bizId : 0,
                    onBeforeDelete : function() {
                        var rows = this.grid.data;
                        var canDelete = true;
                        Gikam.each(rows, function(index, row) {
                            if (workspace.user.id != row.createdById) {
                                canDelete = false;
                                Gikam.alert('QMS.UPLOADER.ALERT.CANNOT_DELETE_FILE_UPLOADED_BY_OTHERS');
                                return;
                            }
                        })
                        return canDelete;
                   }
                } ]
            } ]
        };
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-user-train-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '60%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getAttachment() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
