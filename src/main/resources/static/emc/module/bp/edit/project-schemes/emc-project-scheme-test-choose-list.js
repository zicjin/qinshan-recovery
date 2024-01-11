emcProjectScheme.chooseTestPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-scheme-choose-list-grid',
            url : emcProjectScheme.schemeUrl + '/queries',
            columns : emcProjectScheme.getSchemeEditListColumns(),
            readonly : true,
            page : true,
            requestData : _this.param,
            onRowActive : function(index, row) {
                Gikam.getComp('emc-project-scheme-choose-test-list-grid').refresh({
                    url : Gikam.printf(emcProjectScheme.schemeUrl + '/{id}/proj-orders/queries', {
                        id : row.id
                    }),
                    order : {
                        schemeName : 'asc'
                    }
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getTestGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-project-scheme-choose-test-list-grid',
            columns : emcProjectScheme.getOrderEditListColumns(),
            readonly : true,
            page : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    let selections = Gikam.getComp('emc-project-scheme-choose-test-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    let scheme = Gikam.getComp('emc-project-scheme-choose-list-grid').getActivedRow();

                    let data = {
                        scheme : scheme,
                        tests : selections
                    };

                    Gikam.getLastModal().close(data);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-project-scheme-choose-test-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            east : {
                width : '50%',
                items: [ this.getTestGridParam() ]
            },
            center : {
                items : [ this.getGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
