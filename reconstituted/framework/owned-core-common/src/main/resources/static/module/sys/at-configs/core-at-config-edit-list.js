coreAtConfig.editPage = {
    getAuditTrailableTreeGridParam : function() {
        var _this = this;
        return {
            type : 'treeGrid',
            single : true,
            page : false,
            /*commonSearch: false,*/
            checkOnActive : true,
            checkNodeOnActive : true,
            /*serverSearch : false,*/
            id : 'core-at-config-audit-trailable-tree-grid',
            url : coreAtConfig.baseUrl + '/audit-trailable/tree-grid',
            columns : coreAtConfig.getAuditTrailableTreeGridColumns(),
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                }
            },
            onNodeSelected : function(param, bool) {
                if (bool) {
                    _this.param.id = param.id;
                    Gikam.getComp('core-at-config-detail-base-info-form')
                            .refresh({
                                url : _this.getCompUrl('baseInfoForm')
                            });
                }
            }
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.N,
            panels : [ {
                title : 'CORE.MODULE.SYS.AT-CONFIGS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-at-config-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'coreAuditTrailConfigServiceImpl',
                    fields : coreAtConfig.getBaseInfoFormFields(),
                    autoSave : true,
                    onLoadSuccess : function(data) {
                        this.setData({
                            serviceName : Gikam.propI18N(data.id
                                    + '.SERVICE_NAME')
                        });

                        Gikam.getComp('core-act-config-line-list-grid')
                                .refresh({
                                    url : _this.getCompUrl('configListGrid')
                                });
                    },
                    onUpdated : function() {
                        var grid = Gikam.getComp('core-at-config-audit-trailable-tree-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    }
                } ]
            } ]
        }
    },

    getAtConfigLineTabParam : function() {
        var _this = this;

        return {
            type : 'tab',
            id : 'core-act-config-detail-grid-tab',
            width : Gikam.width.N,
            panels : [ {
                title : 'CORE.MODULE.SYS.AT-CONFIGS.TAB.LINE_GRID',
                items : [ {
                    type : 'grid',
                    commonSearch: false,
                    id : 'core-act-config-line-list-grid',
                    url : _this.getCompUrl('configListGrid'),
                    columns : coreAtConfig.getAtConfigListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'CORE.MODULE.SYS.AT-CONFIGS.BUTTON.AUDIT',
                        icon : 'enable',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.auditAtConfigLine();
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'CORE.MODULE.SYS.AT-CONFIGS.BUTTON.CANCEL',
                        icon : 'disable',
                        onClick : function() {
                            if (_this.param.id) {
                                _this.cancelAtConfigLine();
                            }
                        }
                    } ]
                } ]
            } ]
        }
    },

    urlManager : {
        baseInfoForm : coreAtConfig.baseUrl + '/{id}',
        configListGrid : coreAtConfig.baseUrl + '/{id}/at-config-lines',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-at-config-detail-base-info-form' : this
                    .getCompUrl('baseInfoForm'),
            'core-act-config-line-list-grid' : this
                    .getCompUrl('configListGrid'),
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

    changeAuditTrail : function() {
        var baseData = Gikam.getComp('core-at-config-detail-base-info-form')
                .getData();

        if (baseData.id) {
            workspace.window
                    .save({
                        url : coreAtConfig.baseUrl + '/' + baseData.id,
                        page : this,
                        showLoading : true
                    })
                    .done(
                            function() {
                                var grid = Gikam
                                        .getComp('core-at-config-audit-trailable-tree-grid');
                                grid.setData({
                                    id : baseData.id,
                                    auditTrail : baseData.auditTrail
                                });
                            });
        }
    },

    auditAtConfigLine : function() {
        var _this = this, grid = Gikam
                .getComp('core-act-config-line-list-grid'), options = {
            url : Gikam.printf(coreAtConfig.baseUrl
                    + '/{id}/at-config-lines/action/audit', {
                id : _this.param.id
            }), // 请求的地址
            flag : {
                name : 'auditTrail',// 标志的名称
                expectedValue : '0', // 标志的期望值
                unexpectedMsg : 'CORE.MODULE.SYS.AT-CONFIGS.CHOOSE_CANCEL_ITEM', // 标志不正确时要提示的信息
            },
            confirm : {
                msg : 'CORE.MODULE.SYS.AT-CONFIGS.TIP.WHETHER_TO_AUDIT_FOLLOWING_ITEMS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.SYS.AT-CONFIGS.TIP.CONFIRM_TO_AUDIT', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return Gikam.propI18N(row.configId + '.' + row.columnId);
            }
        };

        return grid.processRows(options);
    },

    cancelAtConfigLine : function() {
        var _this = this, grid = Gikam
                .getComp('core-act-config-line-list-grid'), options = {
            url : Gikam.printf(coreAtConfig.baseUrl
                    + '/{id}/at-config-lines/action/cancel', {
                id : _this.param.id
            }), // 请求的地址
            flag : {
                name : 'auditTrail',// 标志的名称
                expectedValue : '1', // 标志的期望值
                unexpectedMsg : 'CORE.MODULE.SYS.AT-CONFIGS.CHOOSE_AUDIT_ITEM', // 标志不正确时要提示的信息
            },
            confirm : {
                msg : 'CORE.MODULE.SYS.AT-CONFIGS.TIP.WHETHER_TO_CANCEL_FOLLOWING_ITEMS', // 确认弹出框中左侧的信息
                title : 'CORE.MODULE.SYS.AT-CONFIGS.TIP.CONFIRM_TO_CANCEL', // 确认弹出框中的标题
            },
            formatter : function(row) {
                return Gikam.propI18N(row.configId + '.' + row.columnId);
            }
        };

        return grid.processRows(options);
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            west : {
                width : 390,
                items : [ _this.getAuditTrailableTreeGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : 150,
                        items : [ _this.getBaseInfoTab() ]
                    },
                    center : {
                        items : [ _this.getAtConfigLineTabParam() ]
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