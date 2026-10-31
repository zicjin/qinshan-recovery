emcEquiptPerCheck.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-per-check-search-list-grid',
            url : emcEquiptPerCheck.baseUrl + '/queries/searchable',
            columns : emcEquiptPerCheck.getSearchListColumns(),
            page : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    var uploader = Gikam.getComp('emc-equipt-per-check-search-list-uploader');
                    if(uploader && uploader.grid.model) {
                        uploader.setOptions({
                            bizId : '',
                        });
                        uploader.grid.cleanData();
                    }
                    Gikam.cleanCompData([ 'emc-equipt-per-check-search-list-base-info-form' ]);
                }
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-equipt-per-check-search-list-base-info-form').done(function(form) {
                    form.refresh({
                        url : Gikam.printf(emcEquiptPerCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                Gikam.getAsyncComp('emc-equipt-per-check-search-list-uploader').done(function(uploader) {
                    uploader.setOptions({
                        bizId : row.id
                    });
                    uploader.grid.refresh();
                });
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.PREVIEW',
                icon : 'print',
                onClick : function() {
                    Gikam.viewReport('DINGAN_EQPER_CHECK', Gikam.getComp('emc-equipt-per-check-search-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-equipt-per-check-search-list-grid').deleteRows(emcEquiptPerCheck.baseUrl);
                }
            } ]
        }
    },
    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTPERCHECKS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcEquiptPerCheckServiceImpl',
                    id : 'emc-equipt-per-check-search-list-base-info-form',
                    readonly : true,
                    fields : emcEquiptPerCheck.getBaseInfoFormSupFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-equipt-per-check-search-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            },{
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPT_RESULT.TAB.ANNEX',
                items : [ {
                    type : 'uploader',
                    id : 'emc-equipt-per-check-search-list-uploader',
                    dbTable : 'T_EMC_EQUIPT_PER_CHECK',
                } ]
            } ]
        }
    },


    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-equipt-per-check-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : '50%',
                items : [ this.getGridParam() ]
            },
            center : {
                items : [ this.getBaseInfoTab() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
