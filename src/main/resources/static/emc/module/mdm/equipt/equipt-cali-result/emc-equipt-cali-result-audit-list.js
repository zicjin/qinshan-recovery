emcEquiptCaliResult.auditPage = {

    getGridParam : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.CALIBRATIONRESULT',
            items : [ {
            type : 'grid',
            id : 'emc-equipt-cali-result-audit-list-grid',
            url : emcEquiptCaliResult.baseUrl + '/queries/tasks',
            service : 'emcEquiptCaliResultServiceImpl',
            requestData : _this.param,
            columns : emcEquiptCaliResult.getAuditListColumns(),
                deleteFormatter : function(row) {
                    return row.equiptId || row.id;
                },
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }else {
                        var uploader = Gikam.getComp('emc-equipt-cali-result-audit-list-uploader');
                        if(uploader && uploader.grid.model) {
                            uploader.setOptions({
                                bizId : '',
                            });
                            uploader.grid.cleanData();
                        }
                        Gikam.getComp('emc-equipt-cali-result-norm-audit-list-grid').cleanData();
                    }
                },
                onRowActive : function(index, row) {
                    _this.onNormActive(row);
                },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.PASS',
                icon : 'pass',
                onClick : function() {
                    _this.pass();
                }
            }, {
                type : 'button',
                text : 'GIKAM.WORKFLOW.BUTTON.REJECT',
                icon : 'reject',
                onClick : function() {
                    _this.reject();
                    }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('DINGAN_EQCALI ', Gikam.getComp('emc-equipt-cali-result-audit-list-grid').getSelections());
                }
            } ]
            } ],
        }
    },

    getNormParam : function(){
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-cali-result-norm-audit-list-grid',
            service : 'emcEquiptCaliResultNormServiceImpl',
            url : emcEquiptCaliResult.baseUrl2 + '/queries/raw',
            columns : emcEquiptCaliResult.getNormEditListColumns(),
            deleteFormatter : function(row) {
                return row.ext$.testcategorydesc || row.id;
            },
        }
    },


    getProjectAnnexPanel : function() {
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.ANNEX',
            items : [ {
                type : 'uploader',
                id : 'emc-equipt-cali-result-audit-list-uploader',
                dbTable : 'T_EMC_EQUIPT_CALI_RESULT',
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-equipt-cali-result-audit-list-base-info-tab',
            panels : [  _this.getGridParam(), _this.getProjectAnnexPanel() ],
        }
    },
    onNormActive : function(row) {
        var _this = this;
        if(row){
            Gikam.getAsyncComp('emc-equipt-cali-result-norm-audit-list-grid').done(function(item) {
                item.refresh({
                    url : _this.getTestNormUrl(row.id)
                });
            });
        }
        Gikam.getAsyncComp('emc-equipt-cali-result-audit-list-uploader').done(function(uploader) {
            uploader.setOptions({
                bizId : row.id
            });
            uploader.grid.refresh();
        });
    },

    getTestNormUrl : function(ItemId) {
        if (ItemId) {
            return Gikam.printf(emcEquiptCaliResult.baseUrl + '/{id}' + '/test-methods/queries', {
                id : ItemId
            })
        }
    },

    pass : function() {
        var grid = Gikam.getComp('emc-equipt-cali-result-audit-list-grid');
        var gridData = Gikam.getComp('emc-equipt-cali-result-audit-list-grid').getSelections();
        var url = emcEquiptCaliResult.baseUrl + '/equipt-plan';
        Gikam.create('workflow').pass({
            data : grid.getSelections(),
            pageObject : emcEquiptCaliResult
        }).done(function() {
            Gikam.post(url,Gikam.getJsonWrapper({}, [ '', gridData ]))
            grid.refresh();
        });

    },

    reject : function() {
        var grid = Gikam.getComp('emc-equipt-cali-result-audit-list-grid');
        Gikam.create('workflow').reject({
            data : grid.getSelections(),
            pageObject : emcEquiptCaliResult
        }).done(function() {
            grid.refresh();
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-cali-result-audit-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '60%',
                items : [ _this.getBaseInfoTab() ]
            },
            center : {
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_CALI_RESULT_NORM.CALINORM',
                        items : [ _this.getNormParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function(param) {
        param.reviewerId_SEQ=workspace.user.id;
        this.param = param;
        this.create();
    }
};
