coreQueryConfig.singleSearchListPage = {

    create : function () {
        var _this = this;

        var param = _this.param;

        var columns = [ {
            index : true
        } ];

        Gikam.getJson(Gikam.printf(coreQueryConfig.baseUrl + '/{id}/query-config-columns', {
            id : param.configId
        })).done(function (data) {
            data.forEach(function (item) {
                if (item.lastSuspendedFlag == '0') {

                    if (item.type === 'SELECT') {
                        columns.push({
                            title : item.columnName,
                            field : item.columnId.toLowerCase(),
                            filter : (item.filter == '1' ? true : false),
                            type : 'select',
                            category : 'sys',
                            param : {
                                codeCategoryId : item.typeConf
                            }
                        });
                    } else {
                        columns.push({
                            title : item.columnName,
                            field : item.columnId.toLowerCase(),
                            filter : (item.filter == '1' ? true : false)
                        });
                    }

                }

            });

            var selectUrl = IFM_CONTEXT + '/secure/core/module/item/query-configs/{id}/related-queries';


            Gikam.create('layout', {
                id : 'layout',
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'core-query-config-single-search-list-grid' + param.configId,
                        fill : true,
                        filterOpen : true,
                        initRequest : true,
                        url : Gikam.printf(coreQueryConfig.baseUrl + '/{id}/related-queries', {
                            id : param.configId
                        }),
                        columns : columns
                    } ]
                },
                renderTo : workspace.window.$dom,
            });

        })


    },

    init : function (param) {
        this.param = param;

        this.create();
    }
};