emcInfoConfig.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-config-edit-list-grid',
            service : 'emcInfoConfigServiceImpl',
            url : emcInfoConfig.baseUrl + '/queries/raw',
            columns : emcInfoConfig.getEditListColumns(),
            requestData : {
                parentId : 'root'
            },
            deleteFormatter : function(row) {
                return row.configName || row.id;
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var categoryGrid = Gikam.getComp('emc-info-config-edit-list-category-grid');
                    var reportGrid = Gikam.getComp('emc-info-config-edit-list-report-grid');
                    categoryGrid.cleanData();
                    reportGrid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-info-config-edit-list-category-grid').refresh({
                    url : Gikam.printf(emcInfoConfig.baseUrl + '/queries/raw'),
                    requestData : {
                        parentId_SEQ : row.id
                    }
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        fields : emcInfoConfig.getEditListColumns(),
                        url : emcInfoConfig.baseUrl + Gikam.param({
                            configType : 'BCY'
                        })
                    }).done(function() {
                        Gikam.getComp('emc-info-config-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-info-config-edit-list-grid').deleteRows(emcInfoConfig.baseUrl);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    var grid = Gikam.getComp('emc-info-config-edit-list-grid');
                    Gikam.viewAudit('T_EMC_INFO_CONFIG', grid.getSelections());
                }
            } ]
        }
    },

    getCategoryGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.INFO.INFOCONFIGS.TAB.CATEGORY',
                items : [ {
                    type : 'grid',
                    id : 'emc-info-config-edit-list-category-grid',
                    service : 'emcInfoConfigServiceImpl',
                    deleteFormatter : function(row) {
                        return row.configName || row.id;
                    },
                    columns : emcInfoConfig.getCategoryListColumns(),
                    onLoadSuccess : function(data) {
                        if (data.length > 0) {
                            this.activeRowByIndex(0);
                        } else {
                            var reportGrid = Gikam.getComp('emc-info-config-edit-list-report-grid');
                            reportGrid.cleanData();
                        }
                    },
                    onRowActive : function(index, row) {
                        Gikam.getComp('emc-info-config-edit-list-report-grid').refresh({
                            url : Gikam.printf(emcInfoConfig.baseUrl + '/queries/raw'),
                            requestData : {
                                parentId_SEQ : row.id
                            }
                        });
                    },
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-info-config-edit-list-grid').getActivedRow();
                            if (!data) {
                                Gikam.alert('EMC.MODULE.BP.INFO.INFOCONFIGS.TIP.CHOOSE_BIGCATEGORY');
                            }
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.BP.INFO.INFOCONFIGS.MODAL.CATEGORY_ADD',
                                fields : emcInfoConfig.getCategoryListColumns(),
                                url : emcInfoConfig.baseUrl + Gikam.param({
                                    configType : 'SCY',
                                    parentId : data.id
                                }),
                            }).done(function() {
                                Gikam.getComp('emc-info-config-edit-list-category-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-info-config-edit-list-category-grid').deleteRows(emcInfoConfig.baseUrl)
                        }
                    } ]
                } ]
            } ]
        }
    },

    getReportGridParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.BP.INFO.INFOCONFIGS.TAB.REPORT',
                items : [ {
                    type : 'grid',
                    id : 'emc-info-config-edit-list-report-grid',
                    service : 'emcInfoConfigServiceImpl',
                    deleteFormatter : function(row) {
                        return row.configName || row.id;
                    },
                    columns : emcInfoConfig.getReportListColumns(),
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            var data = Gikam.getComp('emc-info-config-edit-list-category-grid').getActivedRow();
                            if (!data) {
                                Gikam.alert('EMC.MODULE.BP.INFO.INFOCONFIGS.TIP.CHOOSE_SMALLCATEGORY');
                            }
                            Gikam.preInsert({
                                fields : emcInfoConfig.getReportListColumns(),
                                url : emcInfoConfig.baseUrl + Gikam.param({
                                    parentId : data.id
                                }),
                            }).done(function() {
                                Gikam.getComp('emc-info-config-edit-list-report-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-info-config-edit-list-report-grid').deleteRows(emcInfoConfig.baseUrl)
                        }
                    } ]
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-config-edit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '20%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    north : {
                        height : '350',
                        items : [ _this.getCategoryGridParam() ]
                    },
                    center : {
                        items : [ _this.getReportGridParam() ]
                    }
                } ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
