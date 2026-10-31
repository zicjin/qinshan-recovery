emcCurve.viewCurvePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-view-curve-history-list-grid',
            url : emcCurve.baseUrl + '/queries/raw',
            columns : emcCurve.getChooseListColumns(),
            onCellClick : function(field, row) {
                field === 'curveNo' && _this.editCurvePage(row);
            },
            requestData : {
                equiptId_SEQ : _this.param.equiptId,
                testId_SEQ : _this.param.testId,
                methodId_SEQ : _this.param.methodId
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copy('copy')
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.SELECT',
                icon : 'select',
                onClick : function() {
                    var gridData = Gikam.getComp('emc-view-curve-history-list-grid').getSelections();
                    if (gridData.length !== 1) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
                        return;
                    }

                    var noPublished = gridData.some(item => item.status !== 'published');
                    if (noPublished) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.SELECT_PUBLISHED');
                        return;
                    }

                    Gikam.getLastModal().close(gridData);
                }
            } ]
        }
    },

    editCurvePage : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width : 1400,
            height : 600,
            url : emcCurve.baseUrl + '/page/data-result-input' + Gikam.param({
                curveId : row.id,
                folderId : row.folderId,
                status : row.status
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-view-curve-history-list-grid').refresh();
            }
        });
    },

    copy : function(oper) {
        var gridData = Gikam.getComp('emc-view-curve-history-list-grid').getSelections();
        if (gridData.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        // 校验选择已发布的曲线
        if (gridData[0].status != 'published') {
            Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.SELECT_PUBLISHED');
            return;
        }

        Gikam.getLastModal().window.showMask(true);
        var url = emcCurve.baseUrl + '/action/copy';
        Gikam.post(url, Gikam.getJsonWrapper({
            operation : oper,
            copyDate : '0'
        }, [ '', gridData ])).always(function() {
            Gikam.getLastModal().window.closeMask();
            Gikam.getComp('emc-view-curve-history-list-grid').refresh();
            Gikam.getComp('emc-curve-choose-list-grid').refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-view-curve-history-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
