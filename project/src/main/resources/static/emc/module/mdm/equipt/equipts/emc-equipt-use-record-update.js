emcEquiptUseRecord.updatePage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-equipt-use-record-edit-list-grid',
            readonly : _this.param.readOnly === 'true',
            columns : emcEquiptUseRecord.getUseRecordEditListColumns(),
            url : Gikam.printf(emcEquipt.baseUrl + '/{sampleId}/use-records/queries', {
                sampleId : _this.param.sampleId
            }) + Gikam.param({
                selectKey : 'SAMPLEID'
            }),
            service : 'emcEquiptUseRecordServiceImpl',
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.BUTTON.BATCH_UPDATE_EQUIPT_RECORD',
                id : 'emc-equipt-use-record-update-button-batch-update',
                icon : 'edit',
                onClick : function() {
                    _this.batchUpdateRecord();
                }
            } ]
        }
    },

    createPage : function() {
        var _this = this;

        Gikam.create('layout', {
            center : {
                items : [ this.getGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });

        return this;

    },

    init : function(param) {

        this.param = param;

        this.createPage();
    },

    batchUpdateRecord : function() {
        var selections = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getSelections();
        if (selections.length > 0) {
            Gikam.create('modal', {
                width : 600,
                height : 300,
                title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TITLE.BATCH_UPDATE_EQUIPT_RECORD',
                url : emcEquipt.baseUrl + '/page/batch-update-record-detail',
                onAfterClose : function(data) {
                    if (!Gikam.isEmpty(data)) {
                        selections.map(function(item) {
                            Gikam.getComp('emc-equipt-use-record-edit-list-grid').setData({
                                id : item.id,
                                usedTime : data.usedTime,
                                beforeUseStatus : data.beforeUseStatus,
                                afterUseStatus : data.afterUseStatus
                            });
                        });
                    }

                }
            });
        } else {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
        }
    },
};
