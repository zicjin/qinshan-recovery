emcOaOrgRelation.editPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-oa-org-relation-edit-list-grid',
            url : emcOaOrgRelation.baseUrl + '/queries/raw',
            service : 'emcOaOrgRelationServiceImpl',
            dbTable : 'T_EMC_OA_ORG_RELATION',
            columns : emcOaOrgRelation.getEditListColumns(),
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.INSERT',
                icon : 'add',
                onClick : function() {
                    Gikam.preInsert({
                        modalTitle : 'EMC.MODULE.MDM.OAS.MODAL.ADD',
                        fields : emcOaOrgRelation.getBaseInfoFormFields(),
                        url : emcOaOrgRelation.baseUrl
                    }).done(function(id) {
                        Gikam.getComp('emc-oa-org-relation-edit-list-grid').refresh();
                    });
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.DELETE',
                icon : 'remove-row',
                onClick : function() {
                    Gikam.getComp('emc-oa-org-relation-edit-list-grid').deleteRows(emcOaOrgRelation.baseUrl);
                }
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-oa-org-relation-edit-list-layout',
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
