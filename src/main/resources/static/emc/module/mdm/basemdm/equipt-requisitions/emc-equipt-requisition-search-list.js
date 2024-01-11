emcEquiptRequisition.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-requisition-search-list-grid',
            url : emcEquiptRequisition.baseUrl + '/queries/searchable',
            columns : emcEquiptRequisition.getSearchListColumns(),
            onCellClick : function(field, row) {
                field === 'ext$.equiptcode' && _this.loadDetailPage(row.id);
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-equipt-requisition-search-list-grid').done(function(grid) {
                    Gikam.getComp('emc-equipt-requisition-search-list-uploader').setOptions({
                        bizId : row.id,
                        readonly : true
                    });
                });
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-equipt-requisition-search-list-grid').activeRowByIndex(0);
                } else if(Gikam.getComp('emc-equipt-requisition-search-list-uploader')) {
                    Gikam.getComp('emc-equipt-requisition-search-list-uploader').grid.cleanData();
                }
            }
        }
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-equipt-requisition-search-list-uploader',
            dbTable : 'T_EMC_EQUIPT_REQUISITION',
            readonly : true
        }
    },
    
    loadDetailPage : function(id) {
        var _this = this;
        Gikam.create('modal', {
            title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.MODAL.DETAIL',
            url : emcEquiptRequisition.baseUrl + '/page/detail' + Gikam.param({
                id : id,
                operType : 'search'
            }),
            width : 900,
            height : 230,
            onAfterClose : function() {
                Gikam.getComp('emc-equipt-requisition-search-list-grid').refresh();
            }
        });
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-requisition-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            },
            south : {
                height : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.EQUIPTREQUISITIONS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
