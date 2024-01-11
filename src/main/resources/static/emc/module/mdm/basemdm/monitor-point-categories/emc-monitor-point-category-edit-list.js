emcMonitorPointCategory.editPage = {

    getMonitorPointCategoryGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-point-category-edit-list-grid',
            url : emcMonitorPointCategory.baseUrl + '/queries/raw',
            columns : emcMonitorPointCategory.getMonitorPointCategoryColumns(),
            service : 'emcMonitorPointCategoryServiceImpl',
            checkOnActive : true,
            requestData : {
                orgId_SB : workspace.user.ext$.parentorgid
            },
            deleteFormatter : function(row) {
                return row.monitorPointType || row.id;
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-monitor-point-category-edit-list-monitor-tmpl-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onMonitorPointCategoryActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        url : emcMonitorPointCategory.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-monitor-point-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-monitor-point-category-edit-list-grid').deleteRows(emcMonitorPointCategory.baseUrl);
                }
            } ]
        }
    },

    onMonitorPointCategoryActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-monitor-point-category-edit-list-monitor-tmpl-grid').refresh({
                url : _this.getMonitorTmplUrl(row.id),
            });
        }
    },
    getMonitorTmplUrl : function(monitorTmplId) {
        if (monitorTmplId) {
            return Gikam.printf(emcMonitorPointCategory.baseUrl + '/{id}/tmpls/queries', {
                id : monitorTmplId
            })
        }
    },
    getMonitorTmplGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-point-category-edit-list-monitor-tmpl-grid',
            url : _this.getMonitorTmplUrl(),
            service : 'emcMpcTmplService',
            columns : emcMonitorPointCategory.getMonitorTmplColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.monitorTmplId || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertTmpl();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteTmpl();
                }
            } ]
        }
    },

    insertTmpl : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-monitor-point-category-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.create('modal', {
            title : Gikam.choose.getConfig('monitorTmpl').title,
            url : Gikam.choose.getConfig('monitorTmpl').url + Gikam.param({
                target_unassigned : '1',
                targetTable : 'T_EMC_MPC_TMPL',
                targetMonitorTmplIdColumn : 'monitorTmplId',
                targetFilterColumn : 'MONITORPOINTCATEGORYID',
                targetFilterValue : activeRow.id,
            }),
            onAfterClose : function(rows) {
                if (Gikam.isEmpty(rows)) {
                    return;
                }
                var chooseData = rows.map(function(item) {
                    return {
                        monitorTmplId : item.id
                    }
                });
                Gikam.getComp('emc-monitor-point-category-edit-list-monitor-tmpl-grid').insert(Gikam.printf(emcMonitorPointCategory.baseUrl + '/{id}/tmpls', {
                    id : activeRow.id,
                }), chooseData);
            }
        });
    },

    deleteTmpl : function() {
        Gikam.getComp('emc-monitor-point-category-edit-list-monitor-tmpl-grid').deleteRows(Gikam.printf(emcMonitorPointCategory.baseUrl + '/{id}/tmpls', {
            id : Gikam.getComp('emc-monitor-point-category-edit-list-grid').getActivedRow().id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-monitor-point-category-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '33%',
                items : [ _this.getMonitorPointCategoryGridParam() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.MONITORPOINTCATEGORIES.TAB.TMPL',
                        items : [ _this.getMonitorTmplGridParam() ]
                    } ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
