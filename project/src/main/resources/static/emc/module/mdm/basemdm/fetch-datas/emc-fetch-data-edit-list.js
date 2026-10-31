emcFetchData.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-fetch-data-edit-list-grid',
            url : emcFetchData.baseUrl + '/queries/raw',
            service : 'emcFetchDataServiceImpl',
            columns : emcFetchData.getEditListColumns(),
            checkOnActive : true,
            checkOneOnActive : true,
            deleteFormatter : function(row) {
                return row.tableName || row.id;
            },
            onRowActive : function(index, row) {
                _this.onFetchDataActive(row);
            },
            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ "emc-fetch-data-line-edit-list-grid" ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-fetch-data-edit-list-grid').insert(emcFetchData.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-fetch-data-edit-list-grid').deleteRows(emcFetchData.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('emc-fetch-data-edit-list-grid').activateRows(emcFetchData.baseUrl + "/action/activate");
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('emc-fetch-data-edit-list-grid').deactivateRows(emcFetchData.baseUrl + "/action/deactivate");
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.MDM.BASEMDM.FETCHDATAS.BUTTON.GENERATE',
                icon : 'add',
                onClick : function() {
                    _this.generateLine();
                }
            } ]
        }
    },

    getFetchDataLineGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-fetch-data-line-edit-list-grid',
            url : '',
            columns : emcFetchData.getFetchDataLineColumns(),
            service : 'emcFetchDataLineServiceImpl',
            checkOnActive : true,
            page : true,
            deleteFormatter : function(row) {
                return row.tableName || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var activedRow = Gikam.getComp("emc-fetch-data-edit-list-grid").getActivedRow();
                    if (!activedRow) {
                        return;
                    }
                    Gikam.getComp('emc-fetch-data-line-edit-list-grid').insert(emcFetchData.baseLineUrl, [ {
                        fetchDataId : activedRow.id
                    } ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-fetch-data-line-edit-list-grid').deleteRows(emcFetchData.baseLineUrl);
                }
            } ]
        }
    },

    onFetchDataActive : function(row) {
        var oldColumns = emcFetchData.getFetchDataLineColumns();
        var fields = ['fieldOne', 'fieldTwo', 'fieldThree'];
        var dynColumns = [];
        fields.forEach(function (field) {
            var title = field + 'Name';
            if (row[title]) {
                dynColumns.push({
                    field : field,
                    title : row[title],
                    editor : true
                });
            }
        });
        var newColumns = oldColumns.slice(0, 1).concat(dynColumns, oldColumns.slice(1, 2));
        Gikam.getComp("emc-fetch-data-line-edit-list-grid").refreshColumns(newColumns);
        Gikam.getComp("emc-fetch-data-line-edit-list-grid").refresh({
            url : emcFetchData.baseLineUrl + '/queries/raw',
            requestData : {
                fetchDataId_SEQ : row.id
            }
        })
    },

    generateLine : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-fetch-data-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var validation = selections.every(function (row) {
            return row.fieldOne && row.fieldTwo;
        });
        if (!validation) {
            Gikam.alert('EMC.MODULE.MDM.BASEMDM.FETCHDATAS.TIP.MISS_INFO');
            return;
        }

        Gikam.confirm('EMC.MODULE.MDM.BASEMDM.FETCHDATAS.TIP.CONFIRM_GENERATE', 'EMC.MODULE.MDM.BASEMDM.FETCHDATAS.TIP.CONFIRM_GENERATE', function() {
            workspace.window.showMask(true);
            Gikam.postText(emcFetchData.baseUrl + '/action/generate', Gikam.getJsonWrapper({}, [ '', selections ])).done(function() {
                var activedRow = Gikam.getComp("emc-fetch-data-edit-list-grid").getActivedRow();
                if (activedRow) {
                    _this.onFetchDataActive(activedRow);
                }
            }).always(function () {
                workspace.window.closeMask();
            });
        })

    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            west : {
                width : '65%',
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getFetchDataLineGridParam() ]
            }
        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
