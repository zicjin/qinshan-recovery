emcProjScheme.initSchemePage = {
    getMonitoringPointGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-proj-init-monitor-point-grid',
            url : emcClient.baseUrl + '/monitor-points/queries/choosable',
            columns : emcClient.getMonitorPointColumns(),
            service : 'emcMonitorPointServiceImpl',
            readonly : true,
            checkOnActive : false,
            requestData : {
                clientId_SEQ : _this.param.testOrgId,
                targetType : 'initScheme'
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.getComp('emc-proj-init-scheme-grid').cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.refreshGrids({
                    'emc-proj-init-scheme-grid' : emcClient.baseUrl + '/monitor-points/{monitorPointId}/schemes/queries',
                }, {
                    monitorPointId : row.id,
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-proj-init-monitor-point-grid').getSelections();
                    if (selections.length > 0) {
                        var chooseSchemeData = selections.map(function(item) {
                            return {
                                projId : _this.param.projId,
                                monitorPointCategoryId : item.monitorPointCategoryId,
                                monitorPointCategoryName : item.ext$.templatename,
                                monitorPointId : item.id,
                                monitorPointNo : item.monitorPointNo,
                                monitorPointName : item.monitorPointName,
                                indexId : item.indexId,
                                indexCategoryId : item.ext$.indexcategoryid,
                                cycle : item.defaultCycle,
                                frequency : item.defaultFrequency,
                            }
                        });

                        Gikam.getLastModal().window.showMask(true);
                        Gikam.post(emcProjScheme.baseUrl + '/action/init-insert', Gikam.getJsonWrapper(null, [ '', chooseSchemeData ])).always(function() {
                            Gikam.getLastModal().window.closeMask();
                            Gikam.getLastModal().close();
                        });
                    } else {
                        Gikam.alert('EMC.MODULE.BP.PROJECT.PROJSCHEMES.TIP.SELECT_POINT_DATA');
                    }
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        };
    },

    getMonitoringPointSchemeGrid : function() {
        var _this = this;
        return {
            type : 'grid',
            title : 'EMC.MODULE.MDM.BIZMDM.CLIENTS.TAB.MONITOR_POINT_SCHEME',
            id : 'emc-proj-init-scheme-grid',
            service : 'emcMonitorPointSchemeServiceImpl',
            columns : emcClient.getMonitorPointSchemeColumns(),
            readonly : true
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : "40%",
                items : [ this.getMonitoringPointGrid() ]
            },
            center : {
                items : [ this.getMonitoringPointSchemeGrid() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
