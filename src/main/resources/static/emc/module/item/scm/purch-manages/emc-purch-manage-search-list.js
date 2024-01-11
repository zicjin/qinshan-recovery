var emcPurchManage = Gikam.getPageObject({});
emcPurchManage.searchPage = {

    getSearchColumns : function(purchObject) {
        return purchObject.getCheckListColumns();
    },

    getSearchGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.ITEM.SCM.EQUIPTPURCHS.TAB.EQUIPT_PURCH',
                items : [ {
                    type : 'grid',
                    id : 'emc-equipt-purch-search-list-grid',
                    url : emcEquiptPurch.baseUrl + '/queries/searchable',
                    requestData : _this.param,
                    columns : _this.getSearchColumns(emcEquiptPurch),
                    onCellClick : function(field, row) {
                        field === 'equiptName' && _this.loadDetailPage(row.id, emcEquiptPurch);
                    }
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.CONVENMTLS.TAB.CONVEN_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-conven-mtl-search-list-grid',
                    url : emcConvenMtl.baseUrl + '/queries/searchable',
                    requestData : _this.param,
                    columns : _this.getSearchColumns(emcConvenMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcConvenMtl);
                    }
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.OFFICEMTLS.TAB.OFFICE_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-office-mtl-search-list-grid',
                    url : emcOfficeMtl.baseUrl + '/queries/searchable',
                    requestData : _this.param,
                    columns : _this.getSearchColumns(emcOfficeMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcOfficeMtl);
                    }
                } ]
            }, {
                title : 'EMC.MODULE.ITEM.SCM.STANDARDMTLS.TAB.STANDARD_MTL',
                items : [ {
                    type : 'grid',
                    id : 'emc-standard-mtl-search-list-grid',
                    url : emcStandardMtl.baseUrl + '/queries/searchable',
                    requestData : _this.param,
                    columns : _this.getSearchColumns(emcStandardMtl),
                    onCellClick : function(field, row) {
                        field === 'mtlName' && _this.loadDetailPage(row.id, emcStandardMtl);
                    }
                } ]
            } ]
        }
    },

    loadDetailPage : function(id, purchObject) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.ITEM.SCM.' + purchObject.itemFolder + '.MODAL.' + purchObject.itemName,
            width : Gikam.width.XL,
            height : purchObject.searchModalHeight,
            url : purchObject.baseUrl + '/page/detail' + Gikam.param({
                id : id
            })
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-purch-manage-search-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getSearchGridParam() ]
            }
        });
    }

}