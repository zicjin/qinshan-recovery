emcRun.qcChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcRun.getQcChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-run-qc-category-choose-list-grid',
            url : '/secure/emc/module/mdm/basemdm/qc-categories/queries/choosable',
            requestData : requestData,
            columns : columns,
            checkOnActive : false,
            onRowActive  : function(index) {
                this.selectRowByIndex(index);
            },
            onSelect: function (row) {
                _this.loadStockBatchNo();
            },
            onUnSelect : function(row) {
                _this.loadStockBatchNo();
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var qcSelections = Gikam.getComp('emc-run-qc-category-choose-list-grid').getSelections();
                    var formData = Gikam.getComp('emc-run-qc-category-choose-form').getData();
                    if (Gikam.isNotEmpty(qcSelections)) {
                        var flag = false;
                        for (i = 0; i < qcSelections.length; i++) {
                            var qcCategoryId = qcSelections[i].qcCategoryId;
                            if (qcCategoryId.indexOf('QC-STD') != -1) {
                                flag = true;
                                break;
                            }
                        }

                    }
                    Gikam.getLastModal().close([ qcSelections, formData ]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            }, {
                type : 'form',
                id : 'emc-run-qc-category-choose-form',
                fields : emcRun.getStockNoColumns(this.param),
                autoSave : false,
                onRendered : function() {
                    Gikam.getComp('emc-run-qc-category-choose-form').hideFields([ 'stockBatchNo' ]);
                }
            } ]
        }
    },

    loadStockBatchNo: function () {
        var qcSelections = Gikam.getComp('emc-run-qc-category-choose-list-grid').getSelections();
        var saveData = [];
        Gikam.each(qcSelections, function(index, item) {
            saveData.push({
                qcCategoryId : item.qcCategoryId,
            });
        });
        var arrvalue = "";
        for (var i = 0; i < saveData.length; i++) {
            arrvalue += saveData[i].qcCategoryId + ",";
        }
        if (arrvalue.length > 0) {
            arrvalue = arrvalue.substr(0, arrvalue.length - 1);
        }
        if (arrvalue.indexOf('QC-STD') != -1) {
            Gikam.getComp('emc-run-qc-category-choose-form').showFields([ 'stockBatchNo' ]);
        } else {
            Gikam.getComp('emc-run-qc-category-choose-form').hideFields([ 'stockBatchNo' ]);
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            id : 'qc-grid-layout',
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
