coreAnnualPlan.searchPage = {

    getGridParam : function() {
        var _this = this;
        var backButton = {};
        if (!Gikam.isEmpty(workspace.window.$param.id)) {
            backButton = {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            }
        }
        return {
            type : 'grid',
            id : 'core-annual-plan-search-list-grid',
            url : coreAnnualPlan.baseUrl + '/queries/searchable',
            activateFirstRow : true,
            columns : coreAnnualPlan.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'annualPlanNo' && _this.loadDetailPage(row.id);
            },
            onRowActive  : function(index, row) {
                Gikam.getAsyncComp('qms-annual-plan-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                });
            },
            toolbar : [ backButton ]
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'core-annual-plan-search-list-layout',
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

    getAttachment : function() {
        return {
            type : 'tab',
            panels : [ {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'qms-annual-plan-search-list-uploader',
                    dbTable : 'T_CORE_ANNUAL_PLAN',
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
    
    loadDetailPage : function(id) {
        workspace.window.load(IFM_CONTEXT + '/secure/core/module/qms/annual-plans/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'search'
        }));
    },
    
    init : function() {
        this.create();
    }
};
