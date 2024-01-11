emcOrderReceive.editPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.ADDLABEL_RECEIVE',
                items : [ {
                    type : 'form',
                    id : 'emc-order-receive-base-info-form',
                    columns : 2,
                    autoSave : false,
                    url : '',
                    service : 'emcOrderReceiveServiceImpl',
                    fields : emcOrderReceive.getBaseInfoFormFields(),
                } ]
            } ]
        }
    },

    getOrderLabelProjectGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-order-receive-proj-edit-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples/queries',
            columns : emcOrderReceive.getOrderLabelScanProjColumns(),
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
                targetType : 'orderReceive',
                orgId_SB: workspace.user.ext$.parentorgid
            },

            onRowActive : function(index, row) {
                if (row) {
                    Gikam.getAsyncComp('emc-order-receive-order-container-edit-list-grid').done(function (grid) {
                        grid.refresh({
                            requestData : {
                                sampleId : row.id,
                                targetType : 'orderReceive',
                                /*scanStatus_IN: ','*/
                            },
                            url : '/secure/emc/module/bp/bp/orders/containers/queries',
                        })
                    });
                    /*Gikam.getAsyncComp('emc-order-receive-scaned-edit-list-grid').done(function (grid) {
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
                    Gikam.cleanCompData([ 'emc-order-receive-noscan-edit-list-grid' ]);
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
                    url : emcOrderReceive.baseUrl + '/queries/searchable',
                    columns : emcOrderReceive.getEditListColumns(),
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
                                url : emcOrderReceive.baseUrl
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
                                     Gikam.getComp('emc-order-receive-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-receive-scaned-edit-list-grid').refresh();
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
                    id : 'emc-order-receive-order-container-edit-list-grid',
                    columns : emcOrderReceive.getOrderContainerColumns(),
                    filterOpen : true,
                    page : true,
                    url : '/secure/emc/module/bp/bp/orders/receive/containers/queries',
                    requestData : {
                        targetType : 'orderReceive',
                        sampleOrgId_SB: workspace.user.ext$.parentorgid
                    },
                    service : 'EmcOrderContainerServiceImpl',
                    checkOnActive : true,
                    toolbar : [{
                        type : 'button',
                        text : 'GIKAM.WORKFLOW.BUTTON.SUBMIT',
                        icon : 'submit',
                        onClick : function() {
                            _this.submitOrderReceive();
                        }
                    },{
                        type : 'button',
                        text : 'GIKAM.BUTTON.BATCH_INPUT',
                        icon : 'edit',
                        onClick : function() {
                            _this.batchInput();
                        }
                    }],
                    rowStyleFormatter: function(row) {
                        if(row.isReceiveScan === '1') {
                            return 'background-color : #2ECC40';
                        }
                    },
                } ]
            } ]
        }
    },
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-receive-order-container-edit-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var modal = Gikam.create('modal', {
            title: "GIKAM.BUTTON.BATCH_INPUT",
            width : 400,
            height : 270,
        });
        var layout = Gikam.create('layout', {
            renderTo: modal.window.$dom,
            center: {
                items: [ {
                    type: 'btnToolbar',
                    items: [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CONFIRM',
                        icon : 'select',
                        onClick : function() {
                            if (form.validate()) {
                                var formData = form.getData();
                                selections.forEach(function (item) {
                                    item.sampleReceiverDate = formData.sampleReceiverDate;
                                    item.sampleReceiverId = formData.sampleReceiverId;
                                    item.sampleReceiverName = formData.sampleReceiverName;
                                    item.sampleReceiverTime = formData.sampleReceiverTime;
                                })
                                Gikam.getLastModal().window.showMask(true);
                                Gikam.put(IFM_CONTEXT + '/secure/gikam/actions/instant', Gikam.getJsonWrapper({}, [ 'emcOrderContainerServiceImpl', selections ])).done(function (data) {
                                    Gikam.getComp('emc-order-receive-order-container-edit-list-grid').refresh();
                                }).always(function () {
                                    Gikam.getLastModal().window.closeMask();
                                    modal.close();
                                });
                            }
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.BUTTON.CANCEL',
                        icon : 'cancel',
                        onClick : function () {
                            modal.close();
                        }
                    } ]
                }]
            }
        });

        var form = Gikam.create('form', {
            renderTo: layout.options.center.$dom,
            columns: 1,
            titleWidth: 80,
            fields: emcOrderReceive.getBatchInputFields(),
            autoSave: false
        });
    },
    submitOrderReceive : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-order-receive-order-container-edit-list-grid').getSelections();
        var validated = Gikam.getComp('emc-order-receive-order-container-edit-list-grid').validate(selections);
        if (!validated) {
            return;
        }
        Gikam.getComp('emc-order-receive-order-container-edit-list-grid').processRows({
            url : '/secure/emc/module/bp/order/order-receive/action/receive',
            confirm : {
                msg : 'EMC.MODULE.BP.ORDER.ORDERRECEIVE.TIP.CONFIRMSUBMIT',
                title : 'GIKAM.TIP.CONFIRMSUBMIT',
            },
            formatter : function(row) {
                return row.orderContainerNo;
            },
            method : 'put'
        }).done(function() {
            Gikam.getComp('emc-order-receive-order-container-edit-list-grid').refresh();
            Gikam.getComp('emc-order-receive-proj-edit-list-grid').refresh();
        });
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
                    id : 'emc-order-receive-noscan-edit-list-grid',
                    columns : emcOrderReceive.getOrderLabelNotScanColumns(),
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
                    id : 'emc-order-receive-scaned-edit-list-grid',
                    columns : emcOrderReceive.getOrderLabelScanedColumns(),
                    filterOpen : true,
                    page : true,
                    service : 'PfOrderServiceImpl',
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.BUTTON.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            var rows = Gikam.getComp('emc-order-receive-scaned-edit-list-grid').getSelections();
        	                var url = IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/undo-scanned';
        	                if (Gikam.isEmpty(rows)) {
        	                    Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        	                    return;
        	                }
                            Gikam.confirm('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.DELETE', 'EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.CONFIRM_TO_DELETE', function() {
                            	 Gikam.postText(url, Gikam.getJsonWrapper(null, [ 'PfOrderServiceImpl', rows])).done(function() {
                                	 Gikam.getComp('emc-order-receive-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-receive-scaned-edit-list-grid').refresh();
                                 }).always(function() {
                                	 Gikam.getComp('emc-order-receive-noscan-edit-list-grid').refresh();
                                     Gikam.getComp('emc-order-receive-scaned-edit-list-grid').refresh();
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
                    // west : {
                    //     width : '30%',
                    //     items : [ _this.getOrderLabelProjectGridParam() ]
                    // },
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
