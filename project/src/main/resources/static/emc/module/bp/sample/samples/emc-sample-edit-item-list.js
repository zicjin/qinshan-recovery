emcSample.editItemPage = {

    getEditItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-edit-item-list-grid',
            url : emcSample.baseUrl + Gikam.printf('/select-item/{id}', {
                id :  _this.param.id
            }),
            columns : emcSample.getEditItemListColumns(),
            onLoadSuccess : function() {
                if(!Gikam.isEmpty(Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid'))) {
                    var resultData =  Gikam.getComp('emc-sample-expand-detail-list-origin-data-order-task-result-grid').getData();
                    var data = _this.unique(resultData);
                    data.map(function(data) {
                        Gikam.getComp('emc-sample-edit-item-list-grid').selectRowById(data.itemId);
                    });
                }

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-sample-edit-item-list-grid').getSelections();
                    // var folderData = Gikam.getComp('emc-sample-data-input-detail-list-grid').getSelections();
                    if(Gikam.isEmpty(data)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.getLastModal().close(data);
                }
            } ]
        }
    },
    
    // 去重
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
            id : 'emc-sample-edit-item-layout',
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
