var emcOrderReceive = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/order/order-receive",

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'labelCode',
            title : 'T_EMC_ORDER_LABEL.LABELCODE',
        }, {
            field : 'createdByName',
            title : 'T_EMC_ORDER_LABEL.OPERATOR',
        }, {
            field : 'createdTime',
            title : 'T_EMC_ORDER_LABEL.SCANDATE',
            formatter : function (i, v) {
                if (v) {
                    return v.split(" ")[0]
                }
            }
        }, {
            field : 'createdTime',
            title : 'T_EMC_ORDER_LABEL.SCANTIME',
            formatter : function (i, v) {
                if (v) {
                    return v.split(" ")[1]
                }
            }
        }, {
            field : 'match',
            title : 'T_EMC_ORDER_LABEL.MATCH',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'autoLabelCode',
            title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.FORM.AUTOSCAN',
            validators : [ 'strLength[0, 33]' ],
            onInput : function (value) {
                if (value && value.length === 16) {
                    var param = {
                        labelCode : value,
                        match : '0'
                    }
                    Gikam.post(emcOrderReceive.baseUrl+'/action/match', Gikam.getJsonWrapper(null, [ '', [ param ] ])).done(function (data) {
                        var mp3Success = "/static/emc/audio/success.mp3";
                        var mp3Failure = "/static/emc/audio/failure.mp3";
                        var mp3 = new Audio(mp3Success);
                        var mp3f = new Audio(mp3Failure);
                    	if (data === 0) {
                        	Gikam.toast('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.EXIST', 3, 'top');
                            setTimeout(()=>{
                            	mp3f.play();
                            },150);
                            Gikam.getComp('emc-order-receive-base-info-form').setData({
                                autoLabelCode : '',
                                manualLabelCode : ''
                            });
                            return;
                        }
                        setTimeout(()=>{
                            mp3.play();
                        },150);
                        Gikam.getComp('emc-order-receive-order-container-edit-list-grid').refresh();
                        //Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                        Gikam.getComp('emc-order-receive-base-info-form').setData({
                        	autoLabelCode : '',
                            manualLabelCode : ''
                        })
                    });
                }
            }
        },
        {
            field : 'manualLabelCode',
            title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.FORM.MANAUAL',
            validators : [ 'strLength[0, 33]' ],
            onKeyDown : function (code, value) {
                if (code === 13 && value) {
                    var param = {
                        labelCode : value,
                        match : '0'
                    }
                    Gikam.post(emcOrderReceive.baseUrl+'/action/match', Gikam.getJsonWrapper(null, [ '', [ param ] ])).done(function (data) {
                        var mp3Success = "/static/emc/audio/success.mp3";
                        var mp3Failure = "/static/emc/audio/failure.mp3";
                        var mp3 = new Audio(mp3Success);
                        var mp3f = new Audio(mp3Failure);
                        if (data === 0) {
                        	Gikam.toast('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.EXIST', 3, 'top');
                            setTimeout(()=>{
                            	mp3f.play();
                            },150);
                            Gikam.getComp('emc-order-receive-base-info-form').setData({
                                autoLabelCode : '',
                                manualLabelCode : ''
                            });
                            return;
                        }
                        setTimeout(()=>{
                            mp3.play();
                        },150);
                        Gikam.getComp('emc-order-receive-order-container-edit-list-grid').refresh();
                        //Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                        Gikam.getComp('emc-order-receive-base-info-form').setData({
                            autoLabelCode : '',
                            manualLabelCode : ''
                        })
                    });
                }
            }
        } ];
    },

    getOrderLabelScanProjColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 130,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 150
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
            width : 150,
        } ];
    },

    getOrderLabelScanedColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
        }];
    },

    getOrderLabelNotScanColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'labelNo',
            title : 'T_EMC_ORDER.LABELNO',
        }];
    },

    getOrderContainerColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.projno',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 130,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME'
        }, {
            field : 'ext$.sampleno',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 150
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
            width : 150,
        }, {
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 140,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'isReceiveScan',
            title : 'T_EMC_ORDER_CONTAINER.ISSCAN',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'acceptedDate',
            title : 'T_EMC_ORDER_CONTAINER.ACCEPTEDDATE',
        }, {
            field : 'acceptedTime',
            title : 'T_EMC_ORDER_CONTAINER.ACCEPTEDTIME',
        }, {
            field : 'ext$.sampledate',
            title : 'T_EMC_ORDER_TASK.SAMPLEBEGINDATE',
        }, {
            field : 'sampleReceiverName',
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor : true,
            onBeforeChoose : function() {
                return {
                    parentOrgId_SEQ : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        sampleReceiverName : rows[0].userName,
                        sampleReceiverId : rows[0].id
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    sampleReceiverName : '',
                    sampleReceiverId : ''
                });
            },
            validators : [ 'notEmpty' ],
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERNAME',
        }, {
            field : 'sampleReceiverDate',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ],
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERDATE',
        }, {
            field : 'sampleReceiverTime',
            type : 'time',
            editor : true,
            validators : [ 'notEmpty' ],
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERTIME',
        }];
    },
    getBatchInputFields : function() {
        return [ {
            field : 'sampleReceiverName',
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERNAME',
            editor : true,
            category : 'user-simple',
            type : 'choose',
            single : true,
            value : workspace.user.id,
            onBeforeChoose : function() {
                return {
                    parentOrgId_SEQ : workspace.user.ext$.parentorgid
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        sampleReceiverName : rows[0].userName,
                        sampleReceiverId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    sampleReceiverName : '',
                    sampleReceiverId : ''
                });
            }
        }, {
            field : 'sampleReceiverDate',
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERDATE',
            type : 'date',
            editor : true,
            value : Gikam.DateUtils.formatter(new Date(), 'yyyy-MM-dd')
        }, {
            field : 'sampleReceiverTime',
            title : 'T_EMC_ORDER_CONTAINER.SAMPLERECEIVERTIME',
            type : 'time',
            editor : true,
            value : Gikam.DateUtils.formatter(new Date(), 'hh:mm')
        } ]
    },
})
