emcStaffAbalityCheck.searchPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-staff-abality-check-search-list-grid',
            url : emcStaffAbalityCheck.baseUrl + '/queries/raw',
            service : 'emcStaffAbalityCheckServiceImpl',
            dbTable : 'T_EMC_STAFF_ABALITY_CHECK',
            columns : emcStaffAbalityCheck.getEditListColumns(),
            readonly: true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-staff-abality-check-search-list-base-info-form' ]);
                };
                var uploader = Gikam.getComp('emc-staff-abality-check-search-list-uploader');
                uploader.setOptions({
                    bizId : ''
                });
                uploader.grid.cleanData();
            },
            onRowActive : function(index, row) {

                Gikam.getAsyncComp('emc-staff-abality-check-search-list-base-info-form').done(function(form) {
                    form.setReadonly(true);
                    form.refresh({
                        url : Gikam.printf(emcStaffAbalityCheck.baseUrl + '/{id}', {
                            id : row.id
                        })
                    });
                });
                var uploader = Gikam.getComp('emc-staff-abality-check-search-list-uploader');
                uploader.setOptions({
                    bizId : row.id
                })
                uploader.grid.refresh();

            },
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.ITEM.STAFFABILITYCHECKS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    service : 'emcStaffAbalityCheckServiceImpl',
                    dbTable : 'T_EMC_STAFF_ABALITY_CHECK',
                    id : 'emc-staff-abality-check-search-list-base-info-form',
                    fields : emcStaffAbalityCheck.getBaseInfoFormFields(),
                    onUpdated : function() {
                        var grid = Gikam.getComp('emc-staff-abality-check-search-list-grid');
                        grid.refreshRowById(grid.getActivedRow().id);
                    },
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'emc-staff-abality-check-search-list-uploader',
                    dbTable : 'T_EMC_STAFF_ABALITY_CHECK',
                    onBeforeUpload : function() {
                        this.setUploadData({})
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-staff-abality-check-search-list-layout',
            renderTo : workspace.window.$dom,
            west : {
                width : 300,
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
