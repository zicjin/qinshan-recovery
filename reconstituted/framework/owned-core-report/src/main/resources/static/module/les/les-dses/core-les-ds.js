var coreLesDs = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/les/les-dses',

    getBaseInfoFormFields : function() {
        return [ {
            field : 'lesDsName',
            title : 'T_CORE_LES_DS.LESDSNAME',
            validators : [ 'strLength[0,42]', 'notEmpty' ],
            preInsert : true,
            colspan : 3
        }, {
            field : 'sqlContent',
            title : 'T_CORE_LES_DS.SQLCONTENT',
            height : 480,
            colspan : 3,
            type : 'textarea',
            onChange : function(name, value, index) {
                var form = this;
                var data = form.getData();

                if (data.sqlCategory != 'procedure' && !Gikam.isEmpty(data.id)) {
                    // 禁用自动保存
                    form.disableAutoSave();
                    workspace.window.showMask(true);

                    var url = coreLesDs.baseUrl + '/{id}/action/update-sql-if-valid'
                    // 对于有效的SQL,进行保存操作
                    Gikam.put(Gikam.printf(url, {
                        id : data.id
                    }), Gikam.getJsonWrapper(null, [ '', [ data ] ])).done(function(r) {
                        var json = JSON.parse(r);
                        if (json.valid) {
                            Gikam.getComp('core-les-ds-field-list-grid').refresh();
                        } else {
                            // SQL不合法提示
                            Gikam.alert(json.message);
                        }
                    }).always(function() {
                        workspace.window.closeMask();
                        form.enableAutoSave();
                    });
                }
            }
        } ]
    },

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'lesDsName',
            title : 'T_CORE_LES_DS.LESDSNAME',
            type : 'link',
            width : 400
        }, {
            field : 'sqlContent',
            title : 'T_CORE_LES_DS.SQLCONTENT',
            type : 'richText',
            sort : false,
            width : 400
        }, Gikam.status.getActiveColumn() ]
    },

    getFieldEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'columnName',
            title : 'T_CORE_LES_DS_FIELD.COLUMNNAME',
            width : 290
        }, {
            field : 'lesColumnName',
            title : 'T_CORE_LES_DS_FIELD.LESCOLUMNNAME',
            validators : [ 'strLength[0,64]' ],
            editor : true,
            width : 290
        } ]
    },

    getDsChooseListColumns : function(isSingle) {
        var columns =  [ {
            checkbox : true
        }, {
            field : 'lesDsName',
            title : 'T_CORE_LES_DS.LESDSNAME',
            width : 290
        } ];
        
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }

        return columns;
    },

    getDsFieldChooseListColumns : function(isSingle) {
        var columns =  [ {
            checkbox : true
        }, {
            field : 'lesColumnName',
            title : 'T_CORE_LES_DS_FIELD.LESCOLUMNNAME',
            width : 280
        } ];
        
        if (isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }

        return columns;
    },

});