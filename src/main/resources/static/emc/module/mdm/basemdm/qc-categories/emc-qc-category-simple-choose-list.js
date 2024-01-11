emcQcCategory.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcQcCategory.getSimpleChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-qc-category-simple-choose-list-grid',
            url : emcQcCategory.baseUrl + '/queries/choosable',
            requestData : requestData,
            columns : columns,
            onLoadSuccess : function(data) {
                var grid = Gikam.getComp('emc-qc-category-simple-choose-list-grid');
                if (!Gikam.isEmpty(_this.param.selectedQcCategoryId)) {
                    var selectedQcCategoryIdArray = _this.param.selectedQcCategoryId.split(",");

                    selectedQcCategoryIdArray.forEach(function(id) {
                        grid.selectRowById(parseInt(id));
                    });
                }

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-qc-category-simple-choose-list-grid').getSelections());
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};