//@ sourceURL=emc-info-config-choose-list.js
emcInfoConfig.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-info-config-choose-list-grid',
            service : 'emcInfoConfigServiceImpl',
            url : emcInfoConfig.baseUrl + '/queries/raw',
            columns : emcInfoConfig.getEditListColumns(),
            requestData : {
                parentId : 'root'
            },
            onLoadSuccess : function(data) {
                if (data.length > 0) {
                    this.activeRowByIndex(0);
                } else {
                    var categoryGrid = Gikam.getComp('emc-info-config-choose-list-category-grid');
                    categoryGrid.cleanData();
                }
            },
            onRowActive : function(index, row) {
                Gikam.getComp('emc-info-config-choose-list-category-grid').refresh({
                    url : Gikam.printf(emcInfoConfig.baseUrl + '/queries/raw'),
                    requestData : {
                        parentId_SEQ : row.id
                    }
                });
            },
            toolbar : [ {
            	type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                	var selections = Gikam.getComp('emc-info-config-choose-list-category-grid').getSelections();
                    if (selections.length != 1) {
                        Gikam.alert("GIKAM.TIP.CHOOSE_ONE_DATA");
                        return;
                    }
                    Gikam.getLastModal().close(selections);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
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
                    id : 'emc-info-config-choose-list-category-grid',
                    service : 'emcInfoConfigServiceImpl',
                    deleteFormatter : function(row) {
                        return row.configName || row.id;
                    },
                    columns : emcInfoConfig.getCategoryListColumns(),

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
                    id : 'emc-info-config-choose-list-report-grid',
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
                            var data = Gikam.getComp('emc-info-config-choose-list-category-grid').getActivedRow();
                            if (!data) {
                                Gikam.alert('EMC.MODULE.BP.INFO.INFOCONFIGS.TIP.CHOOSE_SMALLCATEGORY');
                            }
                            Gikam.preInsert({
                                fields : emcInfoConfig.getReportListColumns(),
                                url : emcInfoConfig.baseUrl + Gikam.param({
                                    parentId : data.id
                                }),
                            }).done(function() {
                                Gikam.getComp('emc-info-config-choose-list-report-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            Gikam.getComp('emc-info-config-choose-list-report-grid').deleteRows(emcInfoConfig.baseUrl)
                        }
                    } ]
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-info-config-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : '20%',
                items : [ this.getGridParam() ]
            },
            center : {
            	items : [ _this.getCategoryGridParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
