Gikam.extend(coreRoleUser.matrixPage, {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-role-user-matrix-grid',
            columns : coreRoleUser.getMatrixColumns(),
            page : true,
            generalButtonGroup : ['refresh', 'export'],
            service : 'coreRoleUserServiceImpl',
            url : coreRoleUser.baseUrl + '/queries/matrix-rows',
            onRendered : function() {
                Gikam.postSync(coreRoleUser.baseUrl + '/queries/matrix-headers').done(function(columns) {
                    columns.map(function(data) {
                        data['width'] = '100';
                        data['sort'] = false;
                    });
                    var oldColumns = coreRoleUser.getMatrixColumns();
                    columns = oldColumns.concat(columns);

                    Gikam.getComp('core-role-user-matrix-grid').refreshColumns(columns);
                });
            },
            onLoadSuccess : function() {
                Gikam.postSync(coreRoleUser.baseUrl + '/queries/matrix-headers').done(function(columns) {
                    columns.map(function(data) {
                        data['width'] = '100';
                        data['sort'] = false;
                    });
                    var oldColumns = coreRoleUser.getMatrixColumns();
                    columns = oldColumns.concat(columns);

                    Gikam.getComp('core-role-user-matrix-grid').refreshColumns(columns);
                });
            },
        }
    },
});