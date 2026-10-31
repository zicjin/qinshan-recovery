emcClient.pointEditPage = {

    getPointGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-monitor-point-edit-list-grid',
            url : Gikam.printf(emcClient.baseUrl + '/{id}/monitor-points/queries', {
                id : _this.param.testOrgId
            }),
            columns : emcClient.getMonitorPointColumns(),
            service : 'emcMonitorPointServiceImpl',
            deleteFormatter : function(row) {
                return row.monitorPointName || row.monitorPointNo || row.id;
            },
            onCellClick : function(field, row) {
                field === 'ext$.indexname' && _this.loadIndexDetailPage(row.indexId);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.insertMonitorPoint();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    _this.deleteMonitorPoint();
                }
            } ]
        }
    },

    deleteMonitorPoint : function() {
        var url = Gikam.printf(emcClient.baseUrl + '/monitor-points');
        Gikam.getComp('emc-monitor-point-edit-list-grid').deleteRows(url);
    },

    insertMonitorPoint : function() {
        Gikam.preInsert({
            modalTitle : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.ADD_MONITOR_POINT',
            height : 370,
            fields : emcClient.getMonitorPointFormFields(),
            url : emcClient.baseUrl + '/' + this.param.testOrgId +'/monitor-points',
        }).done(function(id) {
            Gikam.getComp('emc-monitor-point-edit-list-grid').refresh();
        });
    },

    loadIndexDetailPage : function(id) {
        Gikam.create('modal', {
            height : 400,
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/index-categories/page/order-index-detail' + Gikam.param({
                id : id,
            })
        });
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-point-edit-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getPointGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
