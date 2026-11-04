coreBpmnProc.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            page : true,
            id : 'core-bpmn-proc-list-grid',
            url : coreBpmnProc.baseUrl + '/queries/searchable',
            columns : coreBpmnProc.getSearchListColumns(),
            groupBy : [ 'procCode' ],
            groupFormatter : function(rows) {
                return rows[0].ext$.servicename + ' ' + rows[0].procCode;
            },
            deleteFormatter : function(row) {
                return row.procCode;
            },
            genericQueryFields : [ {
                field : 'id'
            }, {
                field : 'ext$.servicename'
            } ],
            onCellClick : function(field, row) {
                field === 'procCode' && _this.loadDetailPage(row.id);
            },
            requestData : { // 表头上方的查询条件首次查询时不起作用，暂时这么处理
                latest_only : '1'
            },
            toolbar : [ {
                type : 'form',
                columns : 3,
                fields : [ {
                    title : 'CORE.MODULE.SYS.T_CORE_BPMN_PROC.FILTER.FILTER_LATEST_ONLY',
                    field : 'latest_only',
                    type : 'select',
                    value : '1',
                    items : Gikam.getYesOrNoSelectItems()
                } ]
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('core-bpmn-proc-list-grid').deleteRows(coreBpmnProc.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EXPORT',
                icon : 'export',
                onClick : function() {
                    var grid = Gikam.getComp('core-bpmn-proc-list-grid');

                    var selectedRows = grid.getSelections();
                    if (Gikam.isEmpty(selectedRows)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    var getBpmnInputTextUrl = coreBpmnProc.baseUrl + '/bpmn-input-url';

                    Gikam.postText(getBpmnInputTextUrl, Gikam.getJsonWrapper({}, [ '', selectedRows ])).done(function(url) {
                        Gikam.download(IFM_CONTEXT + url);
                    });
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-bpmn-proc',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    loadDetailPage : function(id) {
        workspace.window.load(coreBpmnProc.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    init : function() {
        this.create();
    }
};