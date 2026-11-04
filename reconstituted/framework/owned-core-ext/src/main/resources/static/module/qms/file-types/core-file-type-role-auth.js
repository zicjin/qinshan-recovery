var coreFileRoleAuth = Gikam.getPageObject({

    getRoleAuditData : function(roleIds, obj) {
        var modal = Gikam.create('modal', {
            title: 'MODULE.QMS.FILE.FILETYPES.MODAL.ADD_FILE_CATEGORY_ROLEAUTH',
            width: 1200,
            height: 600
        });
        Gikam.create('layout', {
            renderTo: modal.window.$dom,
            west : {
                width : '50%',
                items : [ {
                    type : 'grid',
                    id : 'core-file-category-insert-role-list-grid',
                    url : IFM_CONTEXT + '/secure/core/module/sys/roles/queries',
                    requestData : {
                        id_DIFFER : roleIds
                    },
                    columns : [ {
                        checkbox : true
                    }, {
                        field : 'id',
                        title : 'T_CORE_ROLE.ID'
                    }, {
                        field : 'roleName',
                        title : 'T_CORE_ROLE.ROLENAME'
                    } ],
                    toolbar : [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var roleListGrid = Gikam.getComp('core-file-category-insert-role-list-grid');
                            var fileAuditGrid = Gikam.getComp('core-file-category-insert-file-audit-grid');
                            var selectedRowIndex = roleListGrid.model.$children[0].checkeds;
                            var addUserData = [];
                            var chooseData = Gikam.jQuery.map(roleListGrid.getData(), function(rowData, index) {
                                if (Gikam.jQuery.inArray(index, selectedRowIndex) < 0) {
                                    return rowData;
                                }
                                addUserData.push(rowData);
                            });
                            var choosenData = fileAuditGrid.getData() ? fileAuditGrid.getData() : [];
                            var choosenData = choosenData.concat(addUserData);
                            
                            roleListGrid.refreshByData({
                                rows : chooseData
                            });
                            
                            fileAuditGrid.refreshByData({
                                rows : choosenData
                            });
                        }
                    }]
                }]
            },
            center : {
                items : [ {
                    type : 'grid',
                    id : 'core-file-category-insert-file-audit-grid',
                    columns : [ {
                        checkbox : true
                    }, {
                        index : true
                    }, {
                        field : 'id',
                        title : 'T_CORE_ROLE.ID'
                    }, {
                        field : 'roleName',
                        title : 'T_CORE_ROLE.ROLENAME'
                    }, {
                        field : 'readFlag',
                        title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.READFLAG',
                        type : 'simpleCheckbox',
                        editor : true
                    }, {
                        field : 'printFlag',
                        title : 'T_CORE_FILE_CATEGORY_ROLE_AUTH.PRINTFLAG',
                        type : 'simpleCheckbox',
                        editor : true
                    } ],
                    toolbar : [{
                        type: 'button',
                        text: 'GIKAM.BUTTON.DELETE',
                        'class': 'blue',
                        onClick : function() {
                            var roleListGrid = Gikam.getComp('core-file-category-insert-role-list-grid');
                            var fileAuditGrid = Gikam.getComp('core-file-category-insert-file-audit-grid');
                            var selectedRowIndex = fileAuditGrid.model.$children[0].checkeds;
                            var addUserData = [];
                            var chooseData = Gikam.jQuery.map(fileAuditGrid.getData(), function(rowData, index) {
                                if (Gikam.jQuery.inArray(index, selectedRowIndex) < 0) {
                                    return rowData;
                                }
                                addUserData.push(rowData);
                            });
                            var choosenData = roleListGrid.getData() ? roleListGrid.getData() : [];
                            var choosenData = choosenData.concat(addUserData);
                            
                            roleListGrid.refreshByData({
                                rows : choosenData
                            });
                            
                            fileAuditGrid.refreshByData({
                                rows : chooseData
                            });
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CONFIRM',
                        'class': 'blue',
                        onClick : function() {
                            var rows = Gikam.getComp('core-file-category-insert-file-audit-grid').getData();
                            Gikam.each(rows, function(index, row) {
                                rows[index].roleId = row.id;
                            })
                            if (!Gikam.isEmpty(rows))
                                obj.insertRoleAuthData(rows);
                            modal.close();
                        }
                    }, {
                        type: 'button',
                        text: 'GIKAM.BUTTON.CANCEL',
                        'class': 'blue',
                        onClick : function() {
                            modal.close();
                        }
                    }]
                }]
            }
        });
    },
    
});
