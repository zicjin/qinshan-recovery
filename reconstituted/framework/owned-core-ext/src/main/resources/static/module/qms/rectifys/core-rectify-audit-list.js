coreRectify.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-rectify-audit-list-grid',
            service : 'coreRectifyServiceImpl',
            columns : coreRectify.getListColumns(),
            url : coreRectify.baseUrl + '/queries/tasks',
            requestData : {
                bpmn_statusCode : _this.param.bpmn_statusCode
            },
            onCellClick : function(field, row) {
                field === 'rectifyNo' && coreRectify.loadDetailPage(row.id, 'detailPageReview');
            },
            onRowActive : function(index, row) {
                Gikam.getComp('core-rectify-audit-list-item-grid').refresh({
                    url : Gikam.printf(coreRectify.baseUrl + '/{id}/items/queries', {
                        id : row.id
                    })
                });
            },
            toolbar : [{
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                id : 'core-rectify-audit-list-grid-pass-button',
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
                id : 'core-rectify-audit-list-grid-reject-button',
                icon : 'reject',
                onClick : function() {
                    _this.setUserInfo();
                    _this.reject();
                }
            }]
        }
    },

    getItemGrid : function(id) {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-rectify-audit-list-item-grid',
            service : 'coreRectifyItemServiceImpl',
            columns : _this.getItemListColumns(),
            toolbar : [{
                type : 'button',
                text : 'CORE.MODULE.QMS.RECTIFY.RECTIFY_ITEM.BUTTON.ADD',
                icon : 'add-row',
                onClick : function() {
                    Gikam.getComp('core-rectify-audit-list-item-grid').insert(Gikam.printf(coreRectify.baseUrl + '/{id}/items', {
                        id : id
                    }), [{}]);
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.QMS.RECTIFY.RECTIFY_ITEM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('core-rectify-audit-list-item-grid').deleteRows(Gikam.printf(coreRectify.baseUrl + '/{id}/items', {
                        id : id
                    }));
                }
            }]
        };
    },

    setUserInfo : function() {
        var _this = this;
        var grid = Gikam.getComp('core-rectify-audit-list-grid');
        var rows = grid.getSelections();
        if(Gikam.isEmpty(rows)) {
            return;
        }
        rows[0].qualityUserName = _this.param.bpmn_statusCode;
        Gikam.put(this.baseUrl + '/0', Gikam.getJsonWrapper({}, ['', rows]));
    },

    validate : function() {
        var _this = this;
        var grid = Gikam.getComp('core-rectify-audit-list-grid');
        var itemGrid = Gikam.getComp('core-rectify-audit-list-item-grid');
        switch(_this.param.bpmn_statusCode) {
            case 'qmAudit':
                return itemGrid.validate();
            case 'groupReview':
                return grid.validate();
            case 'rectifyCarryPlan':
                return itemGrid.validate();
            default:
                return true;
        }
    },

    pass : function() {
        var grid = Gikam.getComp('core-rectify-audit-list-grid');
        var itemGrid = Gikam.getComp('core-rectify-audit-list-item-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : coreRectify
        }).done(function() {
            grid.refresh();
            itemGrid.cleanData();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('core-rectify-audit-list-grid');
        var itemGrid = Gikam.getComp('core-rectify-audit-list-item-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : coreRectify
        }).done(function() {
            grid.refresh();
            itemGrid.cleanData();
        });
    },

    getItemListColumns : function() {
        var _this = this;
        var statusCode = _this.param.bpmn_statusCode;
        var columns = coreRectify.getItemListColumns();
        if(statusCode === 'groupReview') {
            columns = coreRectify.getGroupAuditItemListColumns();
        }
        if(statusCode === 'rectifyCarryPlan' || statusCode === 'rectifyCarryPlanAudit') {
            columns = coreRectify.getPlanItemListColumns();
        }
        if(statusCode === 'execute' || statusCode === 'executeAudit' || statusCode === 'archive') {
            columns = coreRectify.getExecuteItemListColumns();
        }
        return columns;
    },

    create : function() {
        var _this = this;
        var pageConfig = {
            pageObject : coreRectify,
            config : coreRectify.pageConfig,
            prior : this.param.bpmn_statusCode
        };
        Gikam.create('layout', {
            id : 'core-rectify-audit-list-layout',
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
