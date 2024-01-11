emcIndexCategory.previewPage = {

    // 指标预览
    getIndexPreviewParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-index-preview-list-grid',
            url : emcIndexCategory.baseUrl + '/index-preview/queries',
            columns : emcIndexCategory.getIndexPreviewColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.getLastModal().window.showMask(true);
                    var selections = Gikam.getComp('emc-index-preview-list-grid').getSelections();
                    Gikam.postText(emcIndexCategory.baseUrl + '/index/insert', Gikam.getJsonWrapper({}, [ 'emcIndexPreviewServiceImpl', selections ])).done(function() {
                        Gikam.getLastModal().window.closeMask();
                        Gikam.alert('EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TIP.ADD_SUCCESS');
                    }).fail(function(str) {
                        Gikam.getLastModal().window.closeMask();
                    });
                }
            } ],
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-index-preview-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getIndexPreviewParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
