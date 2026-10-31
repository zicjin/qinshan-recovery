Gikam.extend(coreUser.detailPage, {

    getPermissionGridParam : function() {
        var _this = this, panels = [];

        Gikam.each(this.param.typeArray, function(index, value) {

            var toolbar = [ {
                type : 'button',
                id : 'add-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertPermissionRow(value);
                }
            }, {
                type : 'button',
                id : 'remove-' + value.id + '-btn',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deletePermissionRow(value);
                }
            } ];

            panels.push({
                title : value.typeName,
                id : 'core-user-detail-' + value.id + '-tab-panel',
                items : [ {
                    type : 'simpleGrid',
                    id : 'core-user-detail-' + value.id + '-permission-list-grid',
                    url : Gikam.isEmpty(_this.param.id) ? '' : IFM_CONTEXT+value.gridQueryUrl,
                    requestData : {
                        'userId_SEQ' : (value.id === 'T_CORE_ROLE') ? _this.param.id : '',
                        'user_assigned' : _this.param.id,
                        'permissionTypeId' : (value.id === 'T_CORE_ROLE' || value.id === 'T_CORE_ORG') ? '' : value.id,
                    },
                    deleteFormatter : function(row) {

                        if (value.id === 'T_CORE_ORG') {
                            return row.ext$.orgname || row.targetId || row.id;
                        } else if (value.id === 'T_CORE_ROLE') {
                            return row.ext$.rolename || row.id;
                        } else {
                            return row.targetId || row.id;
                        }

                    },
                    toolbar : toolbar,
                    columns : [ {
                        checkbox : true
                    } ].concat(eval('[' + value.gridColumnSettings + ']'))
                } ]
            });
        });

        return {
            type : 'tab',
            id : 'core-user-detail-permission-tab',
            width : 900,
            panels : panels
        }
    },

});
