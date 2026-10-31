emcResult.dataSummaryPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-result-data-summary-list-grid',
            columns : emcResult.getEditListColumns(),
            onRendered : function() {
                _this.renderResultDataSummaryGrid('1');
            },
            toolbar : [ {
                type : 'form',
                fields : [ {
                    title : 'GIKAM.MODAL.DATA_SUMMARY',
                    field : 'summaryFlag',
                    type : 'simpleCheckbox',
                    value : '1',
                    styleFormatter : function() {
                        return {
                            paddingLeft : '0'
                        }
                    },
                    onChange : function(field, value, index) {
                        _this.renderResultDataSummaryGrid(value);
                    }
                } ]
            } ]
        }
    },

    renderResultDataSummaryGrid : function(summaryFlag) {
        Gikam.getLastModal().window.showMask(true);
        var gridData = Gikam.getComp('emc-result-data-summary-list-grid');

        Gikam.post(emcResult.baseUrl + '/data-titles/queries', Gikam.getJsonWrapper({
            summaryType : this.param.summaryType,
            summaryFlag : summaryFlag,
            ids : this.param.ids
        }, [ '', [] ])).done(function(columns) {
            var oldColumns = emcResult.getEditListColumns();

            var condArr = [];
            columns.forEach(function(column) {
                condArr.push(column);
            });

            var newColumns = oldColumns.slice(0, 4).concat(condArr, oldColumns.slice(4));
            gridData.refreshColumns(newColumns);
        });

        Gikam.post(emcResult.baseUrl + '/data-rows/queries', Gikam.getJsonWrapper({
            summaryType : this.param.summaryType,
            summaryFlag : summaryFlag,
            ids : this.param.ids
        }, [ '', [] ])).done(function(data) {
            gridData.loadData(data);
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-result-data-summary-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
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
