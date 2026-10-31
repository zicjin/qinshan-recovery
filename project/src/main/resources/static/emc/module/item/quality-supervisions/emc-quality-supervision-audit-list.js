emcQualitySupervision.auditPage = {
    // 质量监督
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-audit-list-grid',
            url : emcQualitySupervision.baseUrl + '/queries/tasks',
            columns : emcQualitySupervision.getAuditListColumns(),
            requestData : _this.param,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-quality-supervision-audit-list-grid').activeRowByIndex(0);
                } else {
                    var palnGrid = Gikam.getComp('emc-quality-supervision-plan-audit-list-grid');
                    if (palnGrid) {
                        palnGrid.cleanData();
                        palnGrid.setOptions({
                            url : ''
                        });
                    }
                    var contGrid = Gikam.getComp('emc-quality-supervision-cont-audit-list-grid');
                    if (contGrid) {
                        contGrid.cleanData();
                        contGrid.setOptions({
                            url : ''
                        });
                    }
                    var uploader = Gikam.getComp('emc-quality-supervision-audit-list-uploader');
                    if (uploader) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-quality-supervision-plan-audit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{supervisionId}/plans/queries', {
                            supervisionId : row.id
                        }),
                    });
                });
                var uploader = Gikam.getComp('emc-quality-supervision-audit-list-uploader');
                if (uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('qualitySupervisionPlan', Gikam.getComp('emc-quality-supervision-audit-list-grid').getSelections());
                }
            } ]
        }
    },

    // 质量监督计划
    getPlanGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-plan-audit-list-grid',
            columns : emcQualitySupervision.getPlanAuditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-quality-supervision-plan-audit-list-grid').activeRowByIndex(0);
                } else if (Gikam.getComp('emc-quality-supervision-cont-audit-list-grid')) {
                    Gikam.getComp('emc-quality-supervision-cont-audit-list-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-quality-supervision-cont-audit-list-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{planId}/conts/queries', {
                            planId : row.id
                        }),
                    });
                });
            }
        };
    },

    // 质量监督内容
    getContGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-cont-audit-list-grid',
            columns : emcQualitySupervision.getContAuditListColumns()
        };
    },
    
    getPlanContParam : function() {
        var _this = this;
        return {
            type : 'layout',
            north : {
                height : '50%',
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.PLAN',
                        items : [ 
                            _this.getPlanGridParam() 
                        ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.CONT',
                        items : [ 
                            _this.getContGridParam() 
                        ]
                    } ]
                } ]
            }
        };
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-quality-supervision-audit-list-uploader',
            dbTable : 'T_EMC_QUALITY_SUPERVISION',
        }
    },
    

    pass : function() {
        var grid = Gikam.getComp('emc-quality-supervision-audit-list-grid');
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcQualitySupervision
        }).done(function() {
            grid.refresh();
        });
    },

    reject : function() {
        var grid = Gikam.getComp('emc-quality-supervision-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcQualitySupervision
        }).done(function() {
            grid.refresh();
        });
    },
    
    getSupervisionRow : function() {
        var grid = Gikam.getComp('emc-quality-supervision-audit-list-grid');
        if (!grid || !grid.getActivedRow())
            return null;
        return grid.getActivedRow();
    },

    getPlanRow : function() {
        var grid = Gikam.getComp('emc-quality-supervision-plan-audit-list-grid');
        if (!grid || !grid.getActivedRow())
            return null;
        return grid.getActivedRow();
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-index-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 250,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ 
                    {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.SUPERVISION',
                        items : [ 
                            _this.getPlanContParam() 
                        ]
                    },
                    {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    }
                    ]
                } ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
