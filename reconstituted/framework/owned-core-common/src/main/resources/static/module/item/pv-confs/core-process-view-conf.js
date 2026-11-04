var coreProcessViewConf = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/core/module/item/process-view-confs',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getActiveColumn(), {
            field : 'confName',
            title : 'T_CORE_PV_CONF.CONFNAME',
            width : 230
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'confName',
            title : 'T_CORE_PV_CONF.CONFNAME',
            validators : [ 'strLength[0,40]' ],
            preInsert : true
        }, {
            field : 'masterFilter',
            title : 'T_CORE_PV_CONF.MASTERFILTER',
            type : 'simpleCheckbox'
        }, {
            field : 'masterFilterConf',
            title : 'T_CORE_PV_CONF.MASTERFILTERCONF',
            type : 'textarea',
            height : 100,
            colspan : 2
        } ];
    },

    getGridConfFields : function() {
        return [ {
            field : 'gridSql',
            title : 'T_CORE_PV_SUB_CONF.GRIDSQL',
            type : 'textarea',
            height : 100,
            width : 800
        } ];
    },

    getGridConfListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, Gikam.status.getActiveColumn(), {
            field : 'field',
            title : 'T_CORE_PV_GRID_CONF.FIELD'
        }, {
            field : 'title',
            title : 'T_CORE_PV_GRID_CONF.TITLE',
            editor : true,
            validators : [ 'strLength[0,20]' ]
        }, {
            field : 'width',
            title : 'T_CORE_PV_GRID_CONF.WIDTH',
            editor : true,
            type : 'number',
            isInteger : true
        }, {
            field : 'type',
            title : 'T_CORE_PV_GRID_CONF.TYPE',
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'columnType'
            }
        }, {
            field : 'typeConf',
            title : 'T_CORE_PV_GRID_CONF.TYPECONF',
            editor : true,
            type : 'select',
            category : 'codeCategory'
        } ];
    },

    getMasterConfListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'masterConfNo',
            title : 'T_CORE_PV_MASTER_CONF.MASTERCONFNO',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'masterConfName',
            title : 'T_CORE_PV_MASTER_CONF.MASTERCONFNAME',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'dataSearch',
            title : 'T_CORE_PV_MASTER_CONF.DATASEARCH',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'totalSql',
            title : 'T_CORE_PV_MASTER_CONF.TOTALSQL',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewMasterConfServiceImpl\',\'totalSql\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.totalSql) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'doneSql',
            title : 'T_CORE_PV_MASTER_CONF.DONESQL',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewMasterConfServiceImpl\',\'doneSql\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.doneSql) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'subFilter',
            title : 'T_CORE_PV_MASTER_CONF.SUBFILTER',
            type : 'simpleCheckbox',
            width : 150,
            editor : true
        }, {
            field : 'subFilterConf',
            title : 'T_CORE_PV_MASTER_CONF.SUBFILTERCONF',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewMasterConfServiceImpl\',\'subFilterConf\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.subFilterConf) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'displayGridData',
            title : 'T_CORE_PV_MASTER_CONF.DISPLAYGRIDDATA',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'statusSql',
            title : 'T_CORE_PV_MASTER_CONF.STATUSSQL',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewMasterConfServiceImpl\',\'statusSql\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.statusSql) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'gridConf',
            title : 'T_CORE_PV_SUB_CONF.GRIDCONF',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.loadGridConfPage(\'{index}\',\'master\')">{name}</a>', {
                    index : i,
                    name : '列表配置'
                });
            }
        } ];
    },

    getSubConfListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'subConfNo',
            title : 'T_CORE_PV_SUB_CONF.SUBCONFNO',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'subConfName',
            title : 'T_CORE_PV_SUB_CONF.SUBCONFNAME',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'dataSearch',
            title : 'T_CORE_PV_SUB_CONF.DATASEARCH',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'totalSql',
            title : 'T_CORE_PV_SUB_CONF.TOTALSQL',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewSubConfServiceImpl\',\'totalSql\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.totalSql) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'doneSql',
            title : 'T_CORE_PV_SUB_CONF.DONESQL',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.editSql(\'{index}\',\'coreProcessViewSubConfServiceImpl\',\'doneSql\')">{name}</a>', {
                    index : i,
                    name : Gikam.isEmpty(r.doneSql) ? '维护SQL' : '已维护'
                });
            }
        }, {
            field : 'displayGridData',
            title : 'T_CORE_PV_SUB_CONF.DISPLAYGRIDDATA',
            type : 'simpleCheckbox',
            editor : true
        }, {
            field : 'gridConf',
            title : 'T_CORE_PV_SUB_CONF.GRIDCONF',
            sort : false,
            formatter : function(i, v, r) {
                return Gikam.printf('<a href="javascript:;" onclick="coreProcessViewConf.editPage.loadGridConfPage(\'{index}\',\'sub\')">{name}</a>', {
                    index : i,
                    name : '列表配置'
                });
            }
        } ];
    },
});
