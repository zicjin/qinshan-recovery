coreUserTrain.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-user-train-search-list-grid',
            url : coreUserTrain.baseUrl + '/queries/searchable',
            columns : coreUserTrain.getSearchListColumns(),
            activateFirstRow : true,
            onCellClick : function(field, row) {
                field === 'trainNo' && _this.loadDetailPage(row.id);
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-user-train-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
        }
    },

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-user-train-search-list-uploader',
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
            id : 'core-user-train-search-list-layout',
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

    loadDetailPage : function(id) {
        workspace.window.load(coreUserTrain.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};
