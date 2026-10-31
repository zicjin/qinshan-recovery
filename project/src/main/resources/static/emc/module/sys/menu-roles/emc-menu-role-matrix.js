Gikam.extend(coreMenuRole.matrixPage, {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'treeGrid',
            id : 'core-menu-role-matrix-grid',
            columns : coreMenuRole.getMatrixColumns(),
            page : true,
            autoSave : false,
            generalButtonGroup : [ 'refresh', 'export' ],
            checkbox : false,
            url : coreMenuRole.baseUrl + '/queries/matrix-rows',
            onRendered : function() {
                Gikam.postSync(coreMenuRole.baseUrl + '/queries/matrix-headers').done(function(columns) {
                    columns.map(function(data) {
                        data['width'] = '100';
                        data['sort'] = false;
                        data.onChange = function(field, value, index) {
                            var grid = Gikam.getComp('core-menu-role-matrix-grid');
                            var gridData = grid.getData();
                            var row = gridData[index];

                            var selectedRows = _this.getSelectedRows(value, field, row, gridData);
                            var updatedNodes = _this.getUpdatedNodes(value, field, row, selectedRows);
                            _this.doUpdate(updatedNodes, field, selectedRows, gridData);
                        }
                    });
                    var oldColumns = coreMenuRole.getMatrixColumns();
                    columns = oldColumns.concat(columns);
                    Gikam.getComp('core-menu-role-matrix-grid').refreshColumns(columns);
                });
            }
        }
    },
});