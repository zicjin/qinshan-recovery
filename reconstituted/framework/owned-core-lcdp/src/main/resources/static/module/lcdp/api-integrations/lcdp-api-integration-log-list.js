lcdpApiIntegration.logPage = {

    getGridParam : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-api-integration-edit-list-grid',
            url : lcdpApiIntegration.baseUrl + '/queries/raw',
            service : 'lcdpApiIntegrationServiceImpl',
            dbTable : 'T_LCDP_API',
            columns : lcdpApiIntegration.getEditListColumns(),
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'lcdp-api-integration-edit-list-request-grid', 'lcdp-api-integration-edit-list-response-grid' ]);
                }
            },
            onRowActive : function (index, row) {

                Gikam.getAsyncComp('lcdp-api-integration-edit-list-request-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(lcdpApiIntegration.baseUrl + '/{id}/requests/queries', {
                            id : row.id
                        })
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'LCDP.MODULE.API_INTEGRATIONS.BUTTON.NOTIFIER',
                icon : 'setting',
                onClick : function () {

                    workspace.window.load(IFM_CONTEXT + '/secure/core/module/lcdp/api-notifiers/page/edit-list');

                }
            } ]
        }
    },

    getReqGrid : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-api-integration-edit-list-request-grid',
            onCellClick : function (field, row) {
                field === 'requestBody' && _this.loadClobPage(row.requestBody, 'T_LCDP_API_REQ.REQUESTBODY');
            },
            onRendered : function () {
            },
            onRowActive : function (index, row) {

                Gikam.getAsyncComp('lcdp-api-integration-edit-list-response-grid').done(function (grid) {
                    grid.refresh({
                        url : Gikam.printf(lcdpApiIntegration.baseUrl + '/requests/{id}/responses/queries', {
                            id : row.id
                        })
                    });
                });
            },
            onLoadSuccess : function (data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'lcdp-api-integration-edit-list-response-grid' ]);
                }
            },
            columns : lcdpApiIntegration.getReqColumnList(),
        };
    },

    getResGrid : function () {
        var _this = this;
        return {
            type : 'grid',
            id : 'lcdp-api-integration-edit-list-response-grid',
            onCellClick : function (field, row) {
                field === 'log' && _this.loadClobPage(row.log, 'T_LCDP_API_RES.LOG');
                field === 'responseBody' && _this.loadClobPage(row.responseBody, 'T_LCDP_API_RES.RESPONSEBODY');
            },
            onRendered : function () {

            },
            columns : lcdpApiIntegration.getResColumnList(),
        }
    },

    loadClobPage : function (data, title) {

        var modal = Gikam.create('modal', {
            title : title,
            width : 1100,
            height : 550
        });

        var form = Gikam.create('form', {
            renderTo : modal.window.$dom,
            titleWidth : 0,
            fields : [ {
                field : 'log',
                type : 'textarea',
                value : data,
                height : 500
            } ],
            columns : 1
        });

    },

    create : function () {
        var _this = this;
        Gikam.create('layout', {
            id : 'lcdp-api-integration-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '40%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getReqGrid(), this.getResGrid() ]
            }
        });
    },

    init : function () {
        this.create();
    }
};
