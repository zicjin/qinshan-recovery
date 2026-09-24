emcReport.editItemPage = {

    getEditItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-edit-item-list-grid',
            url : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/{id}/items/queries', {
                id : _this.param.testMethodId
            }),
            requestData : {
                ANALYSISCATEGORY_SEQ: 'calculate',
            },
            columns : emcReport.getEditItemListColumns(),
            onLoadSuccess : function() {
                var resultGrid = Gikam.getComp(_this.param.resultGridId);
                var resultData = _this.unique(resultGrid.getData());

                // 勾选已经有的分析项
                resultData.map(function(item) {
                    Gikam.getComp('emc-report-edit-item-list-grid').selectRowById(item.itemId);
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-report-edit-item-list-grid').getSelections();
                    if (Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.getLastModal().close(data);
                }
            } ]
        }
    },

    unique : function(arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i].itemId]) {
                res.push(arr[i]);
                json[arr[i].itemId] = 1;
            }
        }
        return res;
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-report-edit-item-list-layout',
            center : {
                items : [ this.getEditItemGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
