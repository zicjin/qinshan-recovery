emcQualitySupervision.editPage = {
    // 质量监督
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-edit-list-grid',
            url : emcQualitySupervision.baseUrl + '/queries/raw',
            columns : emcQualitySupervision.getEditListColumns(),
            service : 'emcQualitySupervisionServiceImpl',
            requestData : {
                createdById_SEQ : workspace.user.id
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-quality-supervision-edit-list-grid').activeRowByIndex(0);
                } else {
                    var palnGrid = Gikam.getComp('emc-quality-supervision-edit-list-plan-grid');
                    if (palnGrid) {
                        palnGrid.cleanData();
                        palnGrid.setOptions({
                            url : ''
                        });
                    }
                    var contGrid = Gikam.getComp('emc-quality-supervision-edit-list-cont-grid');
                    if (contGrid) {
                        contGrid.cleanData();
                        contGrid.setOptions({
                            url : ''
                        });
                    }
                    var uploader = Gikam.getComp('emc-quality-supervision-edit-list-uploader');
                    if (uploader) {
                        uploader.setOptions({
                            bizId : ''
                        });
                        uploader.grid.cleanData();
                    }
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-quality-supervision-edit-list-plan-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{supervisionId}/plans/queries', {
                            supervisionId : row.id
                        }),
                    });
                });
                var uploader = Gikam.getComp('emc-quality-supervision-edit-list-uploader');
                if (uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                }
            },
            deleteFormatter : function(row) {
                return row.supervisionCode;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var url = Gikam.printf(emcQualitySupervision.baseUrl);
                    Gikam.getComp('emc-quality-supervision-edit-list-grid').insert(url, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-quality-supervision-edit-list-grid').deleteRows(emcQualitySupervision.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                icon : 'submit',
                onClick : function() {
                    _this.submit();
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('qualitySupervisionPlan', Gikam.getComp('emc-quality-supervision-edit-list-grid').getSelections());
                }
              }  ]
            }
        },

    // 质量监督计划
    getPlanGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-edit-list-plan-grid',
            service : 'emcQualitySupervisionPlanServiceImpl',
            columns : emcQualitySupervision.getPlanEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    Gikam.getComp('emc-quality-supervision-edit-list-plan-grid').activeRowByIndex(0);
                } else if (Gikam.getComp('emc-quality-supervision-edit-list-cont-grid')) {
                    Gikam.getComp('emc-quality-supervision-edit-list-cont-grid').cleanData();
                }
            },
            deleteFormatter : function(row) {
                return row.planProject;
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-quality-supervision-edit-list-cont-grid').done(function(grid) {
                    grid.refresh({
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{planId}/conts/queries', {
                            planId : row.id
                        }),
                    });
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var supervisionRow = _this.getSupervisionRow();
                    if (!supervisionRow) {
                        Gikam.alert("EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TIP.ADD_PLAN_UNSELECTED");
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.MODAL.ADD_PAN',
                        fields : emcQualitySupervision.getPlanBaseInfoFormFields(),
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{supervisionId}/plans', {
                            supervisionId : supervisionRow.id
                        }),
                        width : 450,
                        height : 320,
                        titleWidth : 85
                    }).done(function(id) {
                        Gikam.getComp('emc-quality-supervision-edit-list-plan-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quality-supervision-edit-list-plan-grid').deleteRows(emcQualitySupervision.baseUrl + '/plans');
                }
            } ]
        };
    },

    // 质量监督内容
    getContGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-quality-supervision-edit-list-cont-grid',
            service : 'emcQualitySupervisionContServiceImpl',
            columns : emcQualitySupervision.getContEditListColumns(),
            deleteFormatter : function(row) {
                return row.contProject;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    var planRow = _this.getPlanRow();
                    if (!planRow) {
                        Gikam.alert("EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TIP.ADD_CONT_UNSELECTED");
                        return;
                    }
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.MODAL.ADD_CONT',
                        fields : emcQualitySupervision.getContBaseInfoFormFields(),
                        url : Gikam.printf(emcQualitySupervision.baseUrl + '/{planId}/conts', {
                            planId : planRow.id
                        }),
                        width : 450,
                        height : 230,
                        titleWidth : 50
                    }).done(function(id) {
                        Gikam.getComp('emc-quality-supervision-edit-list-cont-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-quality-supervision-edit-list-cont-grid').deleteRows(emcQualitySupervision.baseUrl + '/conts');
                }
            } ]
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
                        items : [ _this.getPlanGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.CONT',
                        items : [ _this.getContGridParam() ]
                    } ]
                } ]
            }
        };
    },

    getAttachmentGridParam : function() {
        var _this = this;
        return {
            type : 'uploader',
            id : 'emc-quality-supervision-edit-list-uploader',
            dbTable : 'T_EMC_QUALITY_SUPERVISION',
        }
    },

    submit : function() {
        var planGrid = Gikam.getComp('emc-quality-supervision-edit-list-plan-grid');
        if (!planGrid.validate(planGrid.getData()))
            return;
        var contGrid = Gikam.getComp('emc-quality-supervision-edit-list-cont-grid');
        if (!contGrid.validate(contGrid.getData()))
            return;
        var grid = Gikam.getComp('emc-quality-supervision-edit-list-grid');
        Gikam.create('workflow').submit({
            data : grid.getSelections(),
            pageObject : emcQualitySupervision
        }).done(function() {
            grid.refresh();
        });
    },

    getSupervisionRow : function() {
        var grid = Gikam.getComp('emc-quality-supervision-edit-list-grid');
        if (!grid || !grid.getActivedRow())
            return null;
        return grid.getActivedRow();
    },

    getPlanRow : function() {
        var grid = Gikam.getComp('emc-quality-supervision-edit-list-plan-grid');
        if (!grid || !grid.getActivedRow())
            return null;
        return grid.getActivedRow();
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-index-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 250,
                items : [ _this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.SUPERVISION',
                        items : [ _this.getPlanContParam() ]
                    }, {
                        title : 'EMC.MODULE.ITEM.QUALITYSUPERVISIONS.TAB.ATTACHMENT',
                        items : [ _this.getAttachmentGridParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
