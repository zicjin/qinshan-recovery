//@ sourceURL=core-dp-config.js
var coreDpConfig = Gikam.getPageObject({

    getChooseListColumns : function() {
        return [ {
            field : 'dpConfigName',
            title : 'T_CORE_DP_CONFIG.DPCONFIGNAME',
            width : 230
        }, {
            field : 'tableName',
            title : 'T_CORE_DP_CONFIG.TABLENAME',
            width : 290
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'dpConfigName',
            title : 'T_CORE_DP_CONFIG.DPCONFIGNAME',
            validators : [ 'strLength[0,20]' ],
            preInsert : true
        }, {
            field : 'tableName',
            title : 'T_CORE_DP_CONFIG.TABLENAME',
            validators : [ 'strLength[0,60]' ],
            preInsert : true,
            type : 'choose',
            category : 'table',
            cleanable : false,
            single : true,
            targetFields : [ {
                tableName : 'id'
            } ],
            onAfterChoose : function() {
                this.setData({
                    uniqueFields : ''
                });
            }
        }, {
            field : 'uniqueFields',
            title : 'T_CORE_DP_CONFIG.UNIQUEFIELDS',
            validators : [ 'strLength[0,120]' ],
            preInsert : true,
            type : 'choose',
            category : 'table-column',
            onBeforeChoose : function() {

                if (Gikam.isEmpty(this.getData().tableName)) {
                    Gikam.alert('CORE.MODULE.SYS.DPCONFIGS.TIP.SELECT_BIZ_TABLE_NAME');
                    return false;
                }

                return {
                    table : this.getData().tableName
                };
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        uniqueFields : rows.map(function(row, index) {
                            return row.columnName;
                        }).join()
                    });

                }
            },
            
        } ];
    },

    getLineListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'relateTableName',
            title : 'T_CORE_DP_CONFIG_LINE.RELATETABLENAME',
            editor : true,
            width : 230,
            type : 'choose',
            category : 'table',
            single : true,
            targetFields : [ {
                relateTableName : 'id'
            } ]
        }, {
            field : 'relateFieldName',
            title : 'T_CORE_DP_CONFIG_LINE.RELATEFIELDNAME',
            editor : true,
            width : 200,
            type : 'choose',
            category : 'table-column',
            single : true,
            onBeforeChoose : function(index) {

                if (Gikam.isEmpty(this.getData()[index].relateTableName)) {
                    Gikam.alert('CORE.MODULE.SYS.DPCONFIGS.TIP.SELECT_RELATE_TABLE_NAME');
                    return false;
                }

                return {
                    table : this.getData()[index].relateTableName
                };
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        index : index,
                        relateFieldName : rows[0].columnName
                    });

                }
            },
            onClean : function(field, value, rowIndex) {
                this.activeRowByIndex(rowIndex);
                this.setData({
                    id : this.getActivedRow().id,
                    relateFieldName : ''
                });
            }
        }, {
            field : 'fieldName',
            title : 'T_CORE_DP_CONFIG_LINE.FIELDNAME',
            editor : true,
            width : 200,
            type : 'choose',
            category : 'table-column',
            single : true,
            onBeforeChoose : function() {
                return {
                    table : Gikam.getComp('core-dp-config-edit-list-base-info-form').getData().tableName
                };
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        index : index,
                        fieldName : rows[0].columnName
                    });

                }
            },
            onClean : function(field, value, rowIndex) {
                this.activeRowByIndex(rowIndex);
                this.setData({
                    id : this.getActivedRow().id,
                    fieldName : ''
                });
            }
        }, {
            field : 'ext$.relateconfigname',
            title : 'T_CORE_DP_CONFIG_LINE.RELATECONFIG',
            editor : true,
            type : 'choose',
            category : 'dp-config',
            single : true,
            targetFields : [ {
                relateConfigId : 'id'
            }, {
                'ext$.relateconfigname' : 'dpConfigName'
            } ],
            width : 230
        } ];
    },

    getImportValidateColumns : function() {
        return [ {
            field : 'tableName',
            title : 'T_CORE_DP_CONFIG.TABLENAME',
            width : 350,
            formatter : function(i, v, row) {
                return Gikam.propI18N(row.tableName + '.SERVICE_NAME');
            }
        }, {
            field : 'operation',
            title : 'T_CORE_DP_CONFIG.OPERATION',
            editor : true,
            type : 'select',
            firstBlank : false,
            selectedIndex : 0,
            category : 'dp-config-import-operation'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/dp-configs'
});
