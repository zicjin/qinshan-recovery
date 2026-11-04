coreUserTrain.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-user-train-audit-list-grid',
            url : coreUserTrain.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            columns : coreUserTrain.getAuditListColumns(),
            activateFirstRow : true,
            onCellClick : function(field, row) {
                field === 'trainNo' && _this.loadDetailPage(row.id);
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('qms-user-train-audit-list-uploader').grid.cleanData();
                }
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-user-train-audit-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            } ]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-user-train-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreUserTrain
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-user-train-audit-list-grid');
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
            sourcePage : 'review'
        }));
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-user-train-audit-list-uploader',
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
        }
    },
    
    create : function() {
        Gikam.create('layout', {
            id : 'core-user-train-audit-list-layout',
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

    init : function(param) {
        this.param = param;
        this.create();
    }
};
