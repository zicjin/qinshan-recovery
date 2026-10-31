emcSampleSet.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sample-set-edit-list-grid',
            url : emcSampleSet.baseUrl + '/queries/raw',
            service : 'emcSampleSetServiceImpl',
            dbTable : 'T_EMC_SAMPLE_SET',
            columns : emcSampleSet.getEditListColumns(),
            onCellClick : function(field, row) {
                field === 'id' && _this.loadDetailPage(row.id);
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.BP.BPS.MODAL.ADD',
                        fields : emcSampleSet.getBaseInfoFormFields(),
                        url : emcSampleSet.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-sample-set-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-sample-set-edit-list-grid').deleteRows(emcSampleSet.baseUrl);
                }
            } ]
        }
    },

    loadDetailPage : function(id) {
        workspace.window.load(emcSampleSet.baseUrl + '/page/detail' + Gikam.param({
            id : id
        }));
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-sample-set-edit-list-layout',
            renderTo : workspace.window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
