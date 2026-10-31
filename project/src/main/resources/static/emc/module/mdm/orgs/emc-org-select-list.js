coreOrg.selectPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var formData = Gikam.getComp('emc-org-select-list-form').getData();
                    Gikam.getLastModal().close(formData);
                }
            } ]
        }
    },
    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-org-select-list-form',
            columns : 1,
            fields : [ {
                field : 'sampleOrgId',
                title : 'T_EMC_PROJ_SCHEME.SAMPLEORGID',
                editor : true,
                type : 'select',
                search : true,
                category : 'org',
                param : {
                    sampleDepartment_SEQ : '1',
                     parentId_SB: workspace.user.ext$.parentorgid
                },
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-org-select-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getBtnToolbar(), _this.getFormParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
