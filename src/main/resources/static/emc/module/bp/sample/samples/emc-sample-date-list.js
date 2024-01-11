emcSampleDate.editPage = {

    /** 样品列表 */
    getGridParam : function() {
        var _this = this, requestData = _this.param;
        return {
            type : 'grid',
            id : 'emc-sample-date-order-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/queries/raw',
            service : 'emcOrderServiceImpl',
            columns : emcSampleDate.getOrderListColumns(),
            requestData : requestData,
            page : true,
            pageSize : 50,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT',
                icon : 'add',
                onClick : function() {
                    _this.batchInput();
                }
            } ]
        }
    },

    /** 批录入 */
    batchInput : function() {
        var _this = this;
        var selections = Gikam.getComp('emc-sample-date-order-list-grid').getSelections();
        if (Gikam.isEmpty(selections)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        Gikam.create('modal', {
            width : 600,
            height : 313,
            title : 'EMC.MODULE.BP.SAMPLE.SAMPLES.MODAL.BATCH_INPUT',
            url : emcSampleDate.baseUrl + '/page/sample-test-batch',
            onAfterClose : function(data) {
                if (!Gikam.isEmpty(data)) {
                    workspace.window.showMask(true);
                    selections.map(function(item) {
                        item.sampleBeginDate = data.sampleBeginDate;
                        item.sampleBeginTime = data.sampleBeginTime;
                        item.sampleCompleteDate = data.sampleCompleteDate;
                        item.sampleCompleteTime = data.sampleCompleteTime;
                    });
                    Gikam.put(emcSampleDate.baseUrl + '/' + selections[0].id, Gikam.getJsonWrapper({}, [ 'emcOrderServiceImpl', selections ])).done(function() {
                        Gikam.getComp('emc-sample-date-order-list-grid').refresh()
                    }).always(function() {
                        workspace.window.closeMask();
                    });
                }
            }
        });
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            },
        });

        Gikam.getAsyncComp('emc-sample-date-order-list-grid').done(function(grid) {
            grid.options.checkOneOnActive = true;
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
