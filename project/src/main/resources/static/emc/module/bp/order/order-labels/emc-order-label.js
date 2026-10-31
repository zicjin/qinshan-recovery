var emcOrderLabel = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/order/order-labels",

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
                    Gikam.post(emcOrderLabel.baseUrl, Gikam.getJsonWrapper(null, [ '', [ param ] ])).done(function (data) {
                        var mp3Success = "/static/emc/audio/success.mp3";
                        var mp3Failure = "/static/emc/audio/failure.mp3";
                        var mp3 = new Audio(mp3Success);
                        var mp3f = new Audio(mp3Failure);
                    	if (data === 0) {
                        	Gikam.toast('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.EXIST', 3, 'top');
                            setTimeout(()=>{
                            	mp3f.play();
                            },150);
                            Gikam.getComp('emc-order-label-base-info-form').setData({
                                autoLabelCode : '',
                                manualLabelCode : ''
                            });
                            return;
                        }
                        setTimeout(()=>{
                            mp3.play();
                        },150);
                        Gikam.getComp('emc-order-label-order-container-edit-list-grid').refresh();
                        //Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                        Gikam.getComp('emc-order-label-base-info-form').setData({
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
                    Gikam.post(emcOrderLabel.baseUrl, Gikam.getJsonWrapper(null, [ '', [ param ] ])).done(function (data) {
                        var mp3Success = "/static/emc/audio/success.mp3";
                        var mp3Failure = "/static/emc/audio/failure.mp3";
                        var mp3 = new Audio(mp3Success);
                        var mp3f = new Audio(mp3Failure);
                        if (data === 0) {
                        	Gikam.toast('EMC.MODULE.BP.ORDER.ORDERLABELS.TIP.EXIST', 3, 'top');
                            setTimeout(()=>{
                            	mp3f.play();
                            },150);
                            Gikam.getComp('emc-order-label-base-info-form').setData({
                                autoLabelCode : '',
                                manualLabelCode : ''
                            });
                            return;
                        }
                        setTimeout(()=>{
                            mp3.play();
                        },150);
                        Gikam.getComp('emc-order-label-order-container-edit-list-grid').refresh();
                        //Gikam.getComp('emc-order-label-scaned-edit-list-grid').refresh();
                        Gikam.getComp('emc-order-label-base-info-form').setData({
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
            field : 'orderContainerNo',
            title : 'T_EMC_ORDER_CONTAINER.ORDERCONTAINERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        }, {
            field : 'isScan',
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
        }];
    },

})
