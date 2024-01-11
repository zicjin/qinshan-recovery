emcCurve.selectItemQcPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-select-item-list-grid',
            url : IFM_CONTEXT + '/secure/emc/module/mdm/bizmdm/tests/items/queries/choosable',
            columns : emcCurve.getItemListColumns(),
            requestData : {
                testId_IN : _this.param.testId,
                methodId_SEQ : _this.param.methodId,
                setName_IN : "NUMERIC_CON,NUMERIC_CON_NOB,NUMERIC_CON_NOI",
                testMethodId_IN :  _this.param.testMethodId,

            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var itemGridData = Gikam.getComp('emc-select-item-list-grid').getSelections();
                    var qcGridData = Gikam.getComp('emc-select-qc-list-shuttle-frame').getSelectList();
                    if (itemGridData.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }
                    if (qcGridData.length < 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.SELECT_QC');
                        return;
                    }

                    var url = emcCurve.baseUrl;
                    Gikam.post(url, Gikam.getJsonWrapper({
                        testId : itemGridData[0].ext$.testid,
                        methodId : itemGridData[0].ext$.methodid,
                        equiptId : _this.param.equiptId,
                        testMethodId :  itemGridData[0].testMethodId,
                    }, [ 'emcItemServiceImpl', itemGridData ], [ 'emcQcCategoryServiceImpl', qcGridData ])).done(function() {
                        Gikam.getLastModal().close();
                    });
                }
            } ]
        }
    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-select-qc-list-shuttle-frame',
            columns : emcCurve.getQcCategoryListColumns(),
            leftGridUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/qc-categories/queries/choosable',
            leftRequestData : {
                setName_SEQ : 'QC_CAL'
            },
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            north : {
                height : "25%",
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getShuttleFrameParam() ]
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
