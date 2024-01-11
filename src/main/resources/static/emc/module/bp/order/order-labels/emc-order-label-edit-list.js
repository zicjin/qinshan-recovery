emcOrderLabel.editPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.ADDLABEL',
                items : [ {
                    type : 'form',
                    id : 'emc-order-label-base-info-form',
                    columns : 2,
                    autoSave : false,
                    url : '',
                    service : 'emcOrderLabelServiceImpl',
                    fields : emcOrderLabel.getBaseInfoFormFields(),
                } ]
            } ]
        }
    },

    getOrderLabelProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-label-proj-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries',
            columns : emcOrderLabel.getOrderLabelScanProjColumns(),
            checkOnActive : true,
            group : {
                fields : [ 'projNo' ],
                formatter : function(row) {
                    return row.projNo;
                }
            },
            filterOpen : true,
            page : true,
            requestData : {
                status_DIFFER : 'draft',
                targetType : 'orderReceipt',
                orgId_SB: workspace.user.ext$.parentorgid
            },

            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getAsyncComp('emc-order-label-order-container-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            requestData : {
                                sampleId : row.id,
                                targetType : 'labelScan',
                                /*scanStatus_IN: ','*/
                            },
                            url : '/secure/emc/module/bp/bp/orders/containers/queries',
                        })
                    });
                    /*Gikam.getAsyncComp('emc-order-label-scaned-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            requestData : {
                                sampleId_SEQ : row.id,
                                termination_DIFFER : '1',
                                /!*scanStatus_SEQ : 'scaned'*!/
                            },
                            url : '/secure/emc/module/bp/bp/orders/queries',
                        })
                    });*/
                }
            },

/*            onLoadSuccess : function(data) {
                if (Gikam.isEmpty(data)) {
                    Gikam.cleanCompData([ 'emc-order-label-noscan-edit-list-grid' ]);
                } else {
                    this.activeRowByIndex(0);
                }
            },*/
        }
    },

/*    getGridTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.ADDEDLABEL',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-label-edit-list-grid',
                    url : emcOrderLabel.baseUrl + '/queries/searchable',
                    columns : emcOrderLabel.getEditListColumns(),
                    checkOnActive : true,
                    deleteFormatter : function(row) {
                        return row.labelCode || row.id;
                    },

                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.INSERT',
                        icon : 'add',
                        onClick : function() {
                            Gikam.preInsert({
                                modalTitle : 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.ADDLABEL',
                                fields : [ {
                                    field : 'labelCode',
                                    title : 'T_EMC_ORDER_LABEL.LABELCODE',
                                    validators : [ 'notEmpty', 'strLength[0, 33]' ],
                                    preInsert : true
                                } ],
                                url : emcOrderLabel.baseUrl
                            }).done(function (data) {
                                if (data === '0') {
                                    Gikam.alert("EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.EXIST");
                                    return;
                                }
                                Gikam.getComp('emc-order-label-edit-list-grid').refresh();
                            });
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            var rows = Gikam.getComp('emc-order-label-edit-list-grid').getSelections();
        	                var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/update-scan-status';
        	                if (Gikam.isEmpty(rows)) {
        	                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        	                    return;
        	                }
                            Gikam.confirm('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.DELETE', 'EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.CONFIRM_TO_DELETE', function() {
                            	 Gikam.postText(url, Gikam.getJsonWrapper(null, [ '', rows])).done(function() {
                                 }).always(function() {
                                     Gikam.getComp('emc-order-label-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-label-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                                 });
                            })


                        }
                    } ]
                } ]
            } ]
        }
    },*/

    getOrderTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title: '样品',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-label-order-container-edit-list-grid',
                    columns : emcOrderLabel.getOrderContainerColumns(),
                    filterOpen : true,
                    page : true,
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [],
                    rowStyleFormatter: function(row) {
                        if(row.isScan === '1') {
                            return 'background-color : #2ECC40';
                        }
                    },
                } ]
            } ]
        }
    },

    getNotScanTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title: 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.NOTSCANORDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-label-noscan-edit-list-grid',
                    columns : emcOrderLabel.getOrderLabelNotScanColumns(),
                    filterOpen : true,
                    page : true,
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [],
                } ]
            } ]
        }
    },

    getScanedTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title: 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.SCANEDORDER',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-label-scaned-edit-list-grid',
                    columns : emcOrderLabel.getOrderLabelScanedColumns(),
                    filterOpen : true,
                    page : true,
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            var rows = Gikam.getComp('emc-order-label-scaned-edit-list-grid').getSelections();
        	                var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/undo-scanned';
        	                if (Gikam.isEmpty(rows)) {
        	                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        	                    return;
        	                }
                            Gikam.confirm('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.DELETE', 'EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.CONFIRM_TO_DELETE', function() {
                            	 Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'PfOrderServiceImpl', rows])).done(function() {
                                	 Gikam.getComp('emc-order-label-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                                 }).always(function() {
                                	 Gikam.getComp('emc-order-label-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                                 });
                            })


                        }
                    }],
                } ]
            } ]
        }
    },




    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : workspace.window.$dom,
            resize : false,
            north : {
                height : 91,
                items : [ _this.getBaseInfoTab() ]
            },
            center : {
                items : [ {
                    type : 'layout',
                    west : {
                        width : '30%',
                        items : [ _this.getOrderLabelProjectGridParam() ]
                    },
                    center : {
                        items : [ _this.getOrderTab() ]
                    },
                    /*east : {
                        width : '35%',
                        items : [ _this.getScanedTab() ]
                    }*/
                } ]
            }

        });
    },

    // 初始化
    init : function() {
        this.create();
    }
};
