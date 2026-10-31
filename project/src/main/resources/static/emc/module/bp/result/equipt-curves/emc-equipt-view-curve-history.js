emcEquiptCurve.viewCurvePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-view-equipt-curve-history-list-grid',
            url : emcEquiptCurve.baseUrl + '/queries/raw',
            columns : emcEquiptCurve.getChooseListColumns(),
            onCellClick : function(field, row) {
                field === 'equiptCurveNo' && _this.editCurvePage(row);
            },
            requestData : {
                equiptId_SEQ : _this.param.equiptId,
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.COPY',
                icon : 'copy',
                onClick : function() {
                    _this.copy('copy')
                }
            },{
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selections = Gikam.getComp('emc-view-equipt-curve-history-list-grid').getSelections();
                    selections.forEach(row => {
                        if (row.createdById !== workspace.user.id) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.EQUIPTCURVES.TIP.CANNOT_DELETE');
                            return;
                        }else {
                            Gikam.getComp('emc-view-equipt-curve-history-list-grid').deleteRows(emcEquiptCurve.baseUrl);
                        }
                    })
                }
            } ]
        }
    },

    editCurvePage : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width : 1400,
            height : 600,
            url : emcEquiptCurve.baseUrl + '/page/data-result-input' + Gikam.param({
                equiptCurveId : row.id,
                // status : row.status,
                createdUserId : row.createdById,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-view-equipt-curve-history-list-grid').refresh();
            }
        });
    },

    copy : function(oper) {
        var gridData = Gikam.getComp('emc-view-equipt-curve-history-list-grid').getSelections();
        if (gridData.length != 1) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }
        // 校验选择已发布的曲线
        /*if (gridData[0].status != 'published') {
            Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.SELECT_PUBLISHED');
            return;
        }*/

        Gikam.getLastModal().window.showMask(true);
        var url = emcEquiptCurve.baseUrl + '/action/copy';
        Gikam.post(url, Gikam.getJsonWrapper({
            operation : oper,
            copyDate : '0'
        }, [ '', gridData ])).always(function() {
            Gikam.getLastModal().window.closeMask();
            Gikam.getComp('emc-view-equipt-curve-history-list-grid').refresh();
            Gikam.getComp('emc-equipt-curve-choose-list-grid').refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-view-equipt-curve-history-list-layout',
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
