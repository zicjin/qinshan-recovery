emcQcCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-qc-category-edit-list-grid',
            url : emcQcCategory.baseUrl + '/queries/raw',
            service : 'emcQcCategoryServiceImpl',
            columns : emcQcCategory.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.qcCategoryId || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.QCCATEGORIES.MODAL.ADD',
                        fields : emcQcCategory.getBaseInfoFormFields(),
                        url : emcQcCategory.baseUrl,
                    }).done(function(id) {
                        Gikam.getComp('emc-qc-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-qc-category-edit-list-grid').deleteRows(emcQcCategory.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-qc-category-edit-list-layout',
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
