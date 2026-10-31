emcTestMethod.resultListPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-item-result-list-grid',
            columns : emcTestMethod.getItemResultListColumns(),
            url : Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/results/queries', {
                itemId : _this.param.itemId
            }),
            service : 'emcItemResultServiceImpl',
            deleteFormatter : function(row) {
                return row.result || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertResultRow();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteResultRow();
                }
            } ]
        };
    },

    insertResultRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/results', {
            itemId : _this.param.itemId
        });

        Gikam.getComp('emc-item-result-list-grid').insert(url, [ {
            itemId : _this.param.itemId
        } ]);
    },

    deleteResultRow : function() {
        var _this = this;
        var url = Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}/results', {
            itemId : _this.param.itemId
        });
        Gikam.getComp('emc-item-result-list-grid').deleteRows(url);
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}