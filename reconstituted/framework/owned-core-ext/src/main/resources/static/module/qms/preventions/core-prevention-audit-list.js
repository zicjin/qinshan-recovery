corePrevention.auditPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-prevention-audit-list-grid',
            service : 'corePreventionServiceImpl',
            columns : corePrevention.getListColumns(),
            url : corePrevention.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            onCellClick : function(field, row) {
                field === 'preventionNo' && corePrevention.loadDetailPage(row.id, 'detailPageReview');
            },
            onRowActive : function(index, row) {
                Gikam.getComp('core-prevention-audit-list-item-grid').refresh({
                    url : Gikam.printf(corePrevention.baseUrl + '/{id}/items/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                id : 'core-prevention-audit-list-grid-pass-button',
                icon : 'pass',
                onClick : function() {
                    if(Gikam.isFalse(_this.validate()))
                        return;
                    _this.setUserInfo();
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                id : 'core-prevention-audit-list-grid-reject-button',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            }]
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-prevention-audit-list-grid');
        var itemGrid = Gikam.getComp('core-prevention-audit-list-item-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : corePrevention
        }).done(function() {
            grid.refresh();
            itemGrid.cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-prevention-audit-list-grid');
        var itemGrid = Gikam.getComp('core-prevention-audit-list-item-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : corePrevention
        }).done(function() {
            grid.refresh();
            itemGrid.cleanData();
        });
    },

    setUserInfo : function() {
        var _this = this;
        var grid = Gikam.getComp('core-prevention-audit-list-grid');
        var rows = grid.getSelections();
        if(Gikam.isEmpty(rows)) {
            return;
        }
        rows[0].qualityUserName = _this.param.bpmn_statusCode;
        Gikam.put(corePrevention.baseUrl + '/0', Gikam.getJsonWrapper({}, ['', rows]));
    },

    validate : function() {
        var _this = this;
        var grid = Gikam.getComp('core-prevention-audit-list-grid');
        var itemGrid = Gikam.getComp('core-prevention-audit-list-item-grid');
        switch(_this.param.bpmn_statusCode) {
            case 'qmAudit':
                return itemGrid.validate();
            case 'groupReview':
                return grid.validate();
            case 'solutionsPlan':
                return itemGrid.validate();
            default:
                return true;
        }
    },

    getItemGrid : function(id) {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-prevention-audit-list-item-grid',
            service : 'corePreventionItemServiceImpl',
            columns : _this.getItemListGridColumns(),
            toolbar : [{
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-prevention-audit-list-item-grid').insert(Gikam.printf(corePrevention.baseUrl + '/{id}/items', {
                        id : id
                    }), [{}]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-prevention-audit-list-item-grid').deleteRows(Gikam.printf(corePrevention.baseUrl + '/{id}/items', {
                        id : id
                    }));
                }
            }]
        };
    },

    getItemListGridColumns : function() {
        var _this = this;
        var statusCode = _this.param.bpmn_statusCode;
        var columns = corePrevention.getItemListColumns();
        if(statusCode === 'groupReview') {
            columns = corePrevention.getGroupAuditItemListColumns();
        }
        if(statusCode === 'solutionsPlan' || statusCode === 'solutionsPlanAudit') {
            columns = corePrevention.getPlanItemListColumns();
        }
        if(statusCode === 'execute' || statusCode === 'executeAudit' || statusCode === 'archive') {
            columns = corePrevention.getExecuteItemListColumns();
        }
        return columns;
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : corePrevention,
            config : corePrevention.pageConfig,
            prior : _this.param.bpmn_statusCode
        };
        Gikam.create('layout', {
            id : 'core-prevention-audit-list-layout',
            renderTo : workspace.window.$dom,
            pageConfig : pageConfig,
            west : {
                width : '35%',
                items : [_this.getGridParam()]
            },
            center : {
                items : [_this.getItemGrid()]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
