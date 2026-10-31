emcMonitorTmpl.samplingCostPage = {

    getGridParam : function() {
        var _this = this, columns = emcMonitorTmpl.getSamplingCostListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-monitor-scheme-sampling-cost-list-grid',
            url : emcMonitorTmpl.baseUrl + '/sampling-costs/queries',
            requestData : requestData,
            columns : columns,
            service : 'emcMonitorSchemeSamplingCostServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    let samplingCost = {
                        monitorTmplId : _this.param.monitorTmplId,
                        monitorSchemeId : _this.param.monitorSchemeId_SEQ
                    }
                    Gikam.getComp('emc-monitor-scheme-sampling-cost-list-grid').insert(emcMonitorTmpl.baseUrl + '/sampling-costs/action/insert', [ samplingCost ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-monitor-scheme-sampling-cost-list-grid').deleteRows(emcMonitorTmpl.baseUrl + '/sampling-costs/action/delete');
                }
            }, {
                type: 'button',
                text: 'GIKAM.BUTTON.COPYTO',
                icon: 'copy',
                onClick: function() {
                    var grid = Gikam.getComp('emc-monitor-scheme-sampling-cost-list-grid');
                    var selections = grid.getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }
                    Gikam.create('modal', {
                        width : 800,
                        height : 500,
                        title : 'EMC.MODULE.BP.INFO.REPORTTMPLS.MODAL.COPYTO',
                        url : '/secure/emc/module/mdm/bizmdm/monitor-tmpls/page/scheme-choose-list'+ Gikam.param({
                            monitorTmplId_SEQ : _this.param.monitorTmplId,
                            id_DIFFER : _this.param.monitorSchemeId_SEQ
                        }),
                        onAfterClose : function(data) {
                            if (Gikam.isEmpty(data)) {
                                return;
                            }
                            var url = emcMonitorTmpl.baseUrl + '/action/copy-to-sampling-cost';
                            workspace.window.showMask(true);
                            Gikam.postText(url, Gikam.getJsonWrapper({}, [ '', data ],[ '', selections ])).done(function(data) {
                                Gikam.alert('复制成功');
                            }).always(function(){
                                workspace.window.closeMask();
                            });
                        }
                    })
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
