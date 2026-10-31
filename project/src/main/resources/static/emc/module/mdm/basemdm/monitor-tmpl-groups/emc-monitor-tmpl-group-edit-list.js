emcMonitorTmplGroup.editPage = {

    getMonitorTmplGroupGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-tmpl-group-edit-list-grid',
            url : emcMonitorTmplGroup.baseUrl + '/queries/raw',
            columns : emcMonitorTmplGroup.getMonitorTmplGroupColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.monitorTmplGroup || row.id;
            },
            activateFormatter : function(row) {
                return row.monitorTmplGroup || row.id;
            },
            deactivateFormatter : function(row) {
                return row.monitorTmplGroup || row.id;
            },
            service : 'emcMonitorTmplGroupServiceImpl',
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-monitor-tmpl-group-edit-list-order-desc-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                _this.onMonitorTmplGroupActive(row);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.MONITORTMPLGROUPS.MODAL.MONITOR_TMPL_GROUP_ADD',
                        fields : emcMonitorTmplGroup.getMonitorTmplGroupColumns(),
                        url : emcMonitorTmplGroup.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-monitor-tmpl-group-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                hidden : true,
                onClick : function() {
                    Gikam.getComp('emc-monitor-tmpl-group-edit-list-grid').deleteRows(emcMonitorTmplGroup.baseUrl);
                }
            } ]
        }
    },

    onMonitorTmplGroupActive : function(row) {
        var _this = this;
        if (row) {
            Gikam.getComp('emc-monitor-tmpl-group-edit-list-order-desc-grid').refresh({
                url : _this.getOrderDescUrl(row.id),
            });
        }
    },
    getOrderDescUrl : function(monitorTmplGroupId) {
        if (monitorTmplGroupId) {
            return Gikam.printf(emcMonitorTmplGroup.baseUrl + '/{id}/order-descs/queries', {
                id : monitorTmplGroupId
            })
        }
    },
    getOrderDescGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-tmpl-group-edit-list-order-desc-grid',
            url : _this.getOrderDescUrl(),
            service : 'emcOrderDescServiceImpl',
            columns : emcMonitorTmplGroup.getOrderDescColumns(),
            checkOnActive : true,
            deleteFormatter : function(row) {
                return row.displayTextId || row.id;
            },
            activateFormatter : function(row) {
                return row.displayTextId || row.id;
            },
            deactivateFormatter : function(row) {
                return row.displayTextId || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertOrderDesc();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteOrderDesc();
                }
            }, ]
        }
    },

    insertOrderDesc : function() {
        var _this = this;
        var activeRow = Gikam.getComp('emc-monitor-tmpl-group-edit-list-grid').getActivedRow();
        if (!activeRow) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        Gikam.getComp('emc-monitor-tmpl-group-edit-list-order-desc-grid').insert(Gikam.printf(emcMonitorTmplGroup.baseUrl + '/{id}/order-descs', {
            id : activeRow.id,
        }), [ {} ]);
    },

    deleteOrderDesc : function() {
        Gikam.getComp('emc-monitor-tmpl-group-edit-list-order-desc-grid').deleteRows(Gikam.printf(emcMonitorTmplGroup.baseUrl + '/{id}/order-descs', {
            id : Gikam.getComp('emc-monitor-tmpl-group-edit-list-grid').getActivedRow().id,
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-monitor-tmpl-group-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : 300,
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.MONITORTMPLGROUPS.TAB.BASE_INFO',
                        items : [ _this.getMonitorTmplGroupGridParam() ]
                    } ]
                } ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    fill : true,
                    panels : [ {
                        title : 'EMC.MODULE.MDM.BASEMDM.MONITORTMPLGROUPS.TAB.ORDER_DESC',
                        items : [ _this.getOrderDescGridParam() ]
                    } ]
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
