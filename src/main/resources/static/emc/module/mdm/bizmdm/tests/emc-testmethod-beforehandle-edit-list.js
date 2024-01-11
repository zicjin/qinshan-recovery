emcTestMethod.emcTestmethodBeforehandleEditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-testmethod-beforehandle-edit-list-grid',
            url : Gikam.printf(emcTestMethod.baseUrl + '/testmethods/{testMethodId}/beforehandles/queries', {
                testMethodId : _this.param.testMethodId
            }),
            service : 'emcTestmethodBeforehandleServiceImpl',
            dbTable : 'T_EMC_TESTMETHOD_BEFOREHANDLE',
            columns : emcTestMethod.getBeforehandleEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcTestMethod.baseUrl + '/testmethods/{testMethodId}/beforehandles', {
                        testMethodId : _this.param.testMethodId
                    });

                    Gikam.getComp('emc-testmethod-beforehandle-edit-list-grid').insert(url, [ {
                        testMethodId : _this.param.testMethodId
                    } ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    var url = Gikam.printf(emcTestMethod.baseUrl + '/testmethods/{testMethodId}/beforehandles', {
                        testMethodId : _this.param.testMethodId
                    });
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-testmethod-beforehandle-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },
};
