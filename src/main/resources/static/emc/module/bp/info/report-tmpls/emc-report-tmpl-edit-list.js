emcReportTmpl.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-tmpl-edit-list-grid',
            url : emcReportTmpl.baseUrl + '/queries/raw',
            service : 'emcReportTmplServiceImpl',
            toolbar : _this.getBtnToolbarParam(),
            columns : emcReportTmpl.getEditListColumns(),
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getComp('emc-report-tmpl-edit-list-line-grid').refresh({
                        url: Gikam.printf(emcReportTmpl.baseUrl + "/{id}/queries", {
                            id : row.id
                        })
                    });
                }
            },
            deleteFormatter : function(row) {
                return row.tmplName || row.orderNo || row.id;
            }
        }
    },
    
    getBtnToolbarParam : function() {
        var _this = this;
        return [ {
            type : 'btnGroup',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-grid').insert(emcReportTmpl.baseUrl, [ {} ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-grid').deleteRows(emcReportTmpl.baseUrl);
                },
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.ACTIVE',
                icon : 'enable',
                onClick : function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-grid').activateRows(emcReportTmpl.baseUrl + '/action/activate');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DEACTIVE',
                icon : 'disable',
                onClick : function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-grid').deactivateRows(emcReportTmpl.baseUrl + '/action/deactivate');
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.COPYTO',
                icon: 'copy',
                onClick: function() {
                    var grid = Gikam.getComp('emc-report-tmpl-edit-list-grid');
                    if (grid.getSelections().length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width : 800,
                        height : 500,
                        title : 'EMC.MODULE.BP.INFO.REPORTTMPLS.MODAL.COPYTO',
                        url : emcReportTmpl.baseUrl + '/page/choose-list',
                        onAfterClose : function(data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }
                            var id = data.map(function(item) {
                                return item.id
                            })
                            var url = Gikam.printf(emcReportTmpl.baseUrl + '/{id}/copyto', {
                                id: grid.getSelections()[0].id
                            });
                            workspace.window.showMask(true);
                            Gikam.postText(url, Gikam.getJsonWrapper({}, [ 'emcReportTmplServiceImpl', data ])).done(function(data) {
                                Gikam.getComp('emc-report-tmpl-edit-list-line-grid').refresh();
                            }).always(function(){
                                workspace.window.closeMask();
                            });
                        }
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_REPORT_tmpl', Gikam.getComp('emc-report-tmpl-edit-list-grid').getSelections());
                }
            }]
        }]
    },

   getLineGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-report-tmpl-edit-list-line-grid',
            columns : emcReportTmpl.getLineEditListColumns(),
            service : 'emcReportTmplLineServiceImpl',
            toolbar :_this.lineToolBar(),
            deleteFormatter : function(row) {
                return row.reportName || row.id;
            }
        }
    },
    
    lineToolBar : function() {
        return [ {
            type : 'btnGroup',
            items : [ {
                type: 'button',
                text: 'GIKAM.BUTTON.INSERT',
                icon: 'add',
                onClick: function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-line-grid').insert(Gikam.printf(emcReportTmpl.baseUrl + '/{id}/lines', {
                        id: Gikam.getComp('emc-report-tmpl-edit-list-grid').getActivedRow().id
                    }));
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.DELETE',
                icon: 'remove-row',
                onClick: function() {
                    Gikam.getComp('emc-report-tmpl-edit-list-line-grid').deleteRows(Gikam.printf(emcReportTmpl.baseUrl + '/lines'));
                },
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.COPY',
                icon: 'copy',
                onClick: function() {
                    var grid = Gikam.getComp('emc-report-tmpl-edit-list-line-grid');
                    if (grid.getSelections().length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.getComp('emc-report-tmpl-edit-list-line-grid').insert(emcReportTmpl.baseUrl + "/copy-line", Gikam.getComp('emc-report-tmpl-edit-list-line-grid').getSelections());
                },
            }, {
                type: 'button',
                text: 'T_EMC_REPORT_TMPL_LINE.CONFIG',
                icon: 'edit',
                onClick: function() {
                    var grid = Gikam.getComp('emc-report-tmpl-edit-list-line-grid');
                    if (grid.getSelections().length != 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width : 800,
                        height : 350,
                        title : 'EMC.MODULE.BP.INFO.REPORTTMPLS.MODAL.CONFIG',
                        url : emcReportTmpl.baseUrl + '/page/line-edit' + Gikam.param({
                            id : Gikam.getComp('emc-report-tmpl-edit-list-line-grid').getSelections()[0].id
                        }),
                        onAfterClose : function(data) {
                            if (!Gikam.isEmpty(data)) {
                                workspace.window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcReportTmplLineServiceImpl', [data] ])).done(function (data) {
                                    Gikam.getComp('emc-report-tmpl-edit-list-line-grid').refresh();
                                }).always(function() {
                                    workspace.window.closeMask();
                                });
                            }
                        }
                    });
                }
            } ]
        } ]
    },

    create: function() {
        var _this = this;
        Gikam.create('layout', {
            id: 'emc-report-tmpl-edit-list-layout',
            renderTo: workspace.window.$dom,
            west: {
                width: '35%',
                items: [_this.getGridParam()]
            },
            center: {
                items: [_this.getLineGridParam()]
            }
        });
        return _this;
    },

    init: function() {
        this.create();
    }
};