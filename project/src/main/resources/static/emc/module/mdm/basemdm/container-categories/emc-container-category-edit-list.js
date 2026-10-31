emcContainerCategory.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-container-category-edit-list-grid',
            service : 'emcContainerCategoryServiceImpl',
            url : emcContainerCategory.baseUrl + '/queries/raw',
            columns : emcContainerCategory.getEditListColumns(),
            deleteFormatter : function(row) {
                return row.containerCategoryId || row.id;
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.BASEMDM.CONTAINERCATEGORIES.MODAL.ADD',
                        fields : emcContainerCategory.getBaseInfoFormFields(),
                        url : emcContainerCategory.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-container-category-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove',
                onClick : function() {
                    Gikam.getComp('emc-container-category-edit-list-grid').deleteRows(emcContainerCategory.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-container-category-edit-list-layout',
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
