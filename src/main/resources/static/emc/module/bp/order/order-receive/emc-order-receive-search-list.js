emcOrderReceive.searchPage = {

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
                    id : 'emc-order-receive-order-container-search-list-grid',
                    columns : emcOrderReceive.getOrderContainerColumns(),
                    filterOpen : true,
                    page : true,
                    readonly : true,
                    url : '/secure/emc/module/bp/bp/orders/receive/containers/queries',
                    requestData : {
                        targetType : 'orderReceiveSearch',
                        sampleOrgId_SB: workspace.user.ext$.parentorgid
                    },
                    service : 'EmcOrderContainerServiceImpl',
                    checkOnActive : true,
                    rowStyleFormatter: function(row) {
                        if(row.isReceiveScan === '1') {
                            return 'background-color : #2ECC40';
                        }
                    },
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
            // north : {
            //     height : 91,
            //     items : [ _this.getBaseInfoTab() ]
            // },
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
