emcEquiptCaliResult.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.CALIBRATIONRESULT',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-cali-result-search-list-grid',
                url : emcEquiptCaliResult.baseUrl + '/queries/searchable',
                columns : emcEquiptCaliResult.getSearchListColumns(),
                activateFirstRow : true,
                activeOnCheck : true,
                page : true,
                onLoadSuccess : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        this.activeRowByIndex(0);
                    }else {
                        var uploader = Gikam.getComp('emc-equipt-cali-result-search-list-uploader');
                        if(uploader && uploader.grid.model) {
                            uploader.setOptions({
                                bizId : '',
                            });
                            uploader.grid.cleanData();
                        }
                        Gikam.getComp('emc-equipt-cali-result-norm-search-list-grid').cleanData();
                    }
                },
                onRowActive : function(index, row) {
                    _this.onNormActive(row);
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.PREVIEW',
                    icon : 'print',
                    onClick : function() {
                        Gikam.viewReport('DINGAN_EQCALI', Gikam.getComp('emc-equipt-cali-result-search-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-cali-result-search-list-grid').deleteRows(emcEquiptCaliResult.baseUrl);
                    }
                } ]
            } ],
        }
    },

    getNormParam : function(){
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-cali-result-norm-search-list-grid',
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
                id : 'emc-equipt-cali-result-search-list-uploader',
                dbTable : 'T_EMC_EQUIPT_CALI_RESULT',
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            fill : true,
            id : 'emc-equipt-cali-result-search-list-base-info-tab',
            panels : [  _this.getGridParam(), _this.getProjectAnnexPanel() ],
        }
    },

    onNormActive : function(row) {
        var _this = this;
        if(row){
            Gikam.getAsyncComp('emc-equipt-cali-result-norm-search-list-grid').done(function(item) {
                item.refresh({
                    url : _this.getTestNormUrl(row.id)
                });
            });
        }
        Gikam.getAsyncComp('emc-equipt-cali-result-search-list-uploader').done(function(uploader) {
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-cali-result-search-list-layout',
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

    init : function() {
        this.create();
    }
};
