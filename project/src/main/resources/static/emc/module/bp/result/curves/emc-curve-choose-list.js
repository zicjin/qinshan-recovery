emcCurve.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-curve-choose-list-grid',
            url : emcCurve.baseUrl + '/queries/raw',
            columns : emcCurve.getChooseListColumns(),
            service : 'emcCurveServiceImpl',
            requestData : {
                equiptId_SEQ : _this.param.equiptId,
                testId_IN : _this.param.testId,
                methodId_SEQ : _this.param.methodId,
                targetType : 'EXPIREDATE_OUT',
                analysisBeginDate : _this.param.analysisBeginDate,
            },
            onCellClick : function(field, row) {
                field === 'curveNo' && _this.editCurvePage(row);
            },
            deleteFormatter : function(row) {
                return row.curveNo || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    _this.add();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.EDIT',
                icon : 'edit',
                onClick : function() {
                    _this.copy('edit');
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-curve-choose-list-grid').deleteRows(emcCurve.baseUrl);
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.VIEW',
                icon : 'browse',
                onClick : function() {
                    _this.viewCurve();
                }
            }, {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.SELECT',
                icon : 'select',
                onClick : function() {
                    var gridData = Gikam.getComp('emc-curve-choose-list-grid').getSelections();
                    if (gridData.length > 0) {
                        var flag = false;
                        var chooseData = gridData.map(function(item) {
                            // 校验选择已发布的曲线
                            if (item.status != 'published') {
                                flag = true;
                            }
                        });
                        if (flag) {
                            Gikam.alert('EMC.MODULE.BP.RESULT.CURVES.TIP.SELECT_PUBLISHED');
                            return;
                        }
                        Gikam.getLastModal().close(gridData);
                    } else {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    }
                }
            } ]
        }
    },

    add : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.SELECT_ITEM_QC',
            width : 800,
            height : 600,
            url : emcCurve.baseUrl + '/page/select-item-qc' + Gikam.param({
                testId : this.param.testId,
                methodId : this.param.methodId,
                equiptId : this.param.equiptId,
                testMethodId :  this.param.testMethodId,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-curve-choose-list-grid').refresh();
            }
        });
    },

    editCurvePage : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width : '75%',
            height : '75%',
            url : emcCurve.baseUrl + '/page/data-result-input' + Gikam.param({
                curveId : row.id,
                folderId : row.folderId,
                status : row.status
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-curve-choose-list-grid').refresh();
            }
        });
    },

    viewCurve : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.VIEW_CURVE',
            width : 1400,
            height : 600,
            url : emcCurve.baseUrl + '/page/view-curve-history' + Gikam.param({
                equiptId : this.param.equiptId,
                testId : this.param.testId,
                methodId : this.param.methodId
            }),
            onAfterClose : function (rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getLastModal().close(rows);
                }
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-curve-choose-list-layout',
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
