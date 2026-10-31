emcEquiptCurve.choosePage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-curve-choose-list-grid',
            url : emcEquiptCurve.baseUrl + '/queries/raw',
            columns : emcEquiptCurve.getChooseListColumns(),
            service : 'emcEquiptCurveServiceImpl',
            requestData : {
                equiptId_SEQ : _this.param.equiptId,
                targetType : 'EXPIREDATE_OUT',
                // testName_IN : _this.param.testMethodNames,
            },
            onCellClick : function(field, row) {
                field === 'equiptCurveNo' && _this.editCurvePage(row);
            },
            deleteFormatter : function(row) {
                return row.equiptCurveNo || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.CURVES.BUTTON.ADD_EQUIPT_CURVES',
                icon : 'add',
                onClick : function() {
                    _this.addEquiptCurve();
                }
            }/*, {
                type : 'button',
                text : 'GIKAM.BUTTON.EDIT',
                icon : 'edit',
                onClick : function() {
                    _this.copy('edit');
                }
            }*/, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    var selections = Gikam.getComp('emc-equipt-curve-choose-list-grid').getSelections();
                    selections.forEach(row => {
                        if (row.createdById !== workspace.user.id) {
                            /*Gikam.alert('EMC.MODULE.BP.RESULT.EQUIPTCURVES.TIP.SELECT');
                            return;
                            Gikam.confirm('EMC.MODULE.BP.RESULT.EQUIPTCURVES.MODAL.DELETE', 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.TIP.DELETE', function() {
                                Gikam.getComp('emc-equipt-curve-choose-list-grid').deleteRows(emcEquiptCurve.baseUrl);
                            });*/
                            Gikam.alert('EMC.MODULE.BP.RESULT.EQUIPTCURVES.TIP.CANNOT_DELETE');
                            return;
                        }else {
                            Gikam.getComp('emc-equipt-curve-choose-list-grid').deleteRows(emcEquiptCurve.baseUrl);
                        }
                    })
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
                    var gridData = Gikam.getComp('emc-equipt-curve-choose-list-grid').getSelections();
                    if (gridData.length > 0) {
                        Gikam.getLastModal().close(gridData);
                    } else {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                    }
                }
            } ]
        }
    },

    //通过仪器id做参数 关联测试新增
    addEquiptCurve : function() {
        var _this = this;
        Gikam.preInsert({
            fields : emcEquiptCurve.getPreInsertListColumns(),
            url : Gikam.printf(emcEquiptCurve.baseUrl + '/{equiptId}/add',{
                    equiptId : _this.param.equiptId,
            })
        }).done(function() {
            Gikam.getComp('emc-equipt-curve-choose-list-grid').refresh()
        });
    },

    editCurvePage : function(row) {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.CURVES.MODAL.RESULT_DATA_INPUT',
            width : '80%',
            height : '80%',
            url : emcEquiptCurve.baseUrl + '/page/data-result-input' + Gikam.param({
                equiptCurveId : row.id,
                // status : row.status,
                createdUserId : row.createdById,
            }),
            onAfterClose : function() {
                Gikam.getComp('emc-equipt-curve-choose-list-grid').refresh();
            }
        });
    },

    viewCurve : function() {
        Gikam.create('modal', {
            title : 'EMC.MODULE.BP.RESULT.EQUIPTCURVES.MODAL.VIEW_EQUIPTCURVE',
            width : 1400,
            height : 600,
            url : emcEquiptCurve.baseUrl + '/page/view-curve-history' + Gikam.param({
                equiptId : this.param.equiptId,
            })
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
