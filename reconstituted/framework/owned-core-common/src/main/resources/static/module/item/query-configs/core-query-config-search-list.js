coreQueryConfig.searchListPage = {
    getConfigGridParam : function() {
        var _this = this;

        var queryGroup = (_this.param ? (_this.param.queryGroup ? _this.param.queryGroup : '') : '');

        return {
            type : 'grid',
            id : 'core-query-config-list-grid',
            url : coreQueryConfig.baseUrl + '/queries/searchable',
            columns : coreQueryConfig.getSearchConfigListColumns(),
            requestData : {
                queryGroup_SEQ : queryGroup
            },
            genericQueryFields : [ {
                field : 'configName'
            } ],
            onSelect : function(row, $row) {
                _this.updateQueryGrid(row.id);
            }
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'layout',
            west : {
                width : 329,
                items : [ _this.getConfigGridParam() ]
            },
            renderTo : workspace.window.$dom,
        });
    },

    updateQueryGrid : function(configId) {
        var selectQueryColumnUrl = IFM_CONTEXT + '/secure/core/module/item/query-configs/{id}/query-config-columns';

        var columns = [ {
            index : true
        } ], searchItems = [];

        Gikam.getJson(Gikam.printf(selectQueryColumnUrl, {
            id : configId
        })).done(function(data) {
            data.forEach(function(item) {
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

            var resultListGrid = Gikam.getComp('core-query-result-list-grid');
            if (resultListGrid) {
                Gikam.jQuery('#core-query-result-list-grid').remove();
            }

            var selectUrl = IFM_CONTEXT + '/secure/core/module/item/query-configs/{id}/related-queries';

            var initRequest = false;
            var selection = Gikam.getComp('core-query-config-list-grid').getSelections()[0];
            if (selection.initQuery === '1') {
                initRequest = true;
            }
            var item = {
                type : 'grid',
                id : 'core-query-result-list-grid',
                name : selection.configName,
                $window : workspace.window,
                renderTo : Gikam.getComp('layout').options.center.$dom,
                fill : true,
                filterOpen : true,
                initRequest : initRequest,
                url : Gikam.printf(selectUrl, {
                    id : configId
                }),
                search : [ {
                    items : searchItems
                } ],
                columns : columns
            };
            Gikam.create(item.type, item);
        })
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};