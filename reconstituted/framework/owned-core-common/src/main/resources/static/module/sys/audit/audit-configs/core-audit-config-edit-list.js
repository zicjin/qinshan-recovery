coreAuditConfig.editPage = {
    getAuditConfigGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            single : true,
            page : true,
            checkOnActive : true,
            id : 'core-audit-config-edit-list-grid',
            url : coreAuditConfig.baseUrl + '/queries',
            columns : coreAuditConfig.getEditListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.BUTTON.INIT',
                icon : 'view-audit',
                onClick : function() {
                    _this.initAuditConfig();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.MODAL.ADD',
                        height : 250,
                        fields : coreAuditConfig.getPreInsertBaseInfoFormFields(),
                        url : coreAuditConfig.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('core-audit-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-audit-config-edit-list-grid').deleteRows(coreAuditConfig.baseUrl);
                }
            } ],
            deleteFormatter : function(row) {
                return row.name;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ "core-audit-config-detail-base-info-form", "core-audit-config-detail-column-grid" ]);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    _this.param.id = row.id;
                }
                
                _this.onConfigActive(row);
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.N,
            panels : [ {
                title : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-audit-config-detail-base-info-form',
                    service : 'coreAuditConfigServiceImpl',
                    fields : coreAuditConfig.getBaseInfoFormFields(),
                    autoSave : true,
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-audit-config-edit-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                    onLoadSuccess : function(data) {
                        Gikam.getComp('core-audit-config-detail-column-grid').refresh({
                            url : _this.getCompUrl('configColumnGrid')
                        });
                    }
                } ]
            } ]
        }
    },

    getAuditConfigColumnTabParam : function() {
        var _this = this;

        return {
            type : 'tab',
            id : 'core-audit-config-detail-grid-tab',
            width : Gikam.width.N,
            panels : [ {
                title : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TAB.COLUMN_GRID',
                items : [ {
                    type : 'grid',
                    id : 'core-audit-config-detail-column-grid',
                    url : _this.getCompUrl('configListGrid'),
                    service : 'coreAuditConfigColumnServiceImpl',
                    columns : coreAuditConfig.getAuditConfigColumnList(),
                    page : false,
                    order : false,
                    filter : false,
                    toolbar : [ {
                        type : 'button',
                        text : 'CORE.MODULE.AUDIT.AUDIT_CONFIG_COLUMNS.BUTTON.INIT',
                        icon : 'view-audit',
                        onClick : function() {
                            var grid = Gikam.getComp('core-audit-config-edit-list-grid');
                            
                            var row = grid.getActivedRow();
                            
                            if (row) {
                                _this.initColumnAudit(row.id);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.AUDIT.AUDIT_CONFIG_COLUMNS.BUTTON.ENABLE',
                        icon : 'enable',
                        onClick : function() {
                            var grid = Gikam.getComp('core-audit-config-edit-list-grid');
                            
                            var row = grid.getActivedRow();
                            
                            if (row) {
                                _this.enableColumnAudit(row.id);
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.AUDIT.AUDIT_CONFIG_COLUMNS.BUTTON.DISABLE',
                        icon : 'disable',
                        onClick : function() {
                            var grid = Gikam.getComp('core-audit-config-edit-list-grid');
                            
                            var row = grid.getActivedRow();
                            
                            if (row) {
                                _this.disableColumnAudit(row.id);
                            }
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreAuditConfig.baseUrl + '/{id}',
        configColumnGrid : coreAuditConfig.baseUrl + '/{id}/config-columns'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-audit-config-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-audit-config-detail-column-grid' : this.getCompUrl('configColumnGrid'),
        });
    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    onConfigActive : function(row) {
        var _this = this;
        if (row) {
            var form = Gikam.getComp('core-audit-config-detail-base-info-form');
            form.cleanData();
            form.refresh({
                url : Gikam.printf(coreAuditConfig.baseUrl + '/{id}', {
                    id : row.id
                })
            });
        }
    },
    
    initAuditConfig : function() {
        workspace.window.showMask(true);
        Gikam.put(Gikam.printf(coreAuditConfig.baseUrl + '/action/init')).done(function() {
            Gikam.getComp('core-audit-config-edit-list-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    initColumnAudit : function(id) {
        workspace.window.showMask(true);
        Gikam.put(Gikam.printf(coreAuditConfig.baseUrl + '/{id}/config-columns/action/init', {
            id : id
        })).done(function() {
            Gikam.getComp('core-audit-config-detail-column-grid').refresh();
        }).always(function() {
            workspace.window.closeMask();
        });
    },

    enableColumnAudit : function(id) {
        var _this = this, grid = Gikam.getComp('core-audit-config-detail-column-grid'), options = {
            url : Gikam.printf(coreAuditConfig.baseUrl + '/{id}/config-columns/action/enable-audit', {
                id : id
            }), // 请求的地址
            flag : {
                name : 'auditable',// 标志的名称
                expectedValue : '0', // 标志的期望值
                unexpectedMsg : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.CHOOSE_CANCEL_COLUMNS', // 标志不正确时要提示的信息
            },
            confirm : {
                msg : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.ENABLE_CHOOSE_COLUMNS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.AUDIT', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return Gikam.propI18N(row.configId + '.' + row.column);
            }
        };

        return grid.processRows(options);
    },

    disableColumnAudit : function(id) {
        var _this = this, grid = Gikam.getComp('core-audit-config-detail-column-grid'), options = {
            url : Gikam.printf(coreAuditConfig.baseUrl + '/{id}/config-columns/action/disable-audit', {
                id : id
            }), // 请求的地址
            flag : {
                name : 'auditable',// 标志的名称
                expectedValue : '1', // 标志的期望值
                unexpectedMsg : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.CHOOSE_ENABLE_COLUMNS', // 标志不正确时要提示的信息
            },
            confirm : {
                msg : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.DISABLE_CHOOSE_COLUMNS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.AUDIT.AUDIT_CONFIGS.TIP.AUDIT', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return Gikam.propI18N(row.configId + '.' + row.column);
            }
        };

        return grid.processRows(options);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            west : {
                width : 500,
                items : [ _this.getAuditConfigGridParam() ]
            },
            
            center : {
                items : [ {
                    type : 'layout',
                    
                    north : {
                        height : '300',
                        items : [_this.getBaseInfoTab() ],
                    },
                    
                    center : {
                        items : [ _this.getAuditConfigColumnTabParam() ]
                    }
                } ]
            },
            
            renderTo : workspace.window.$dom,
        });
    },

    init : function() {
        this.param = {};
        this.create();
    }
};