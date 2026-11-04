coreApplyComplaint.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-apply-complaint-search-list-grid',
            url : coreApplyComplaint.baseUrl + '/queries/searchable',
            columns : coreApplyComplaint.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'applyComplaintNo' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.QMS.ANNUALPLAN.BUTTON.PRINT',
                icon : 'print',
                onClick : function() {
                    var rows = Gikam.getComp('core-apply-complaint-search-list-grid').getSelections();
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-apply-complaint-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreApplyComplaint.baseUrl + '/page/detail' + Gikam.param({
            id : id,
            sourcePage : 'search'
        }));
    },

    init : function() {
        this.create();
    }
};
