emcBizCategory.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-biz-category-detail-back-button',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.L,
            panels : [ {
                title : 'EMC.MODULE.MDM.BASEMDM.BIZCATEGORIES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-biz-category-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    service : 'emcBizCategoryServiceImpl',
                    fields : emcBizCategory.getBaseInfoFormFields(),
                    columns : 3,
                    onRendered : function() {
                        if (_this.param.id) {
                            this.toFieldsReadonly([ 'id' ]);
                            this.toFieldsReadonly([ 'bizCategoryId' ]);
                            this.removeFieldValidator('id', [ 'remote' ]);
                        }
                    }
                } ]
            } ]
        }
    },
    
    

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-biz-category-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-biz-category-detail-org-permission-grid' : this.getCompUrl('orgPermissionGrid'),
        });

    },

    getCompUrl : function(name) {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(this.urlManager[name], {
            id : this.param.id
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-biz-category-detail-layout',
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
