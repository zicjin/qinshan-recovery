emcIndexCategory.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.VIEWAUDIT',
                icon : 'view-audit',
                onClick : function() {
                    Gikam.viewAudit('T_EMC_INDEX_CATEGORY', _this.param.id);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-index-category-detail-back-button',
                onClick : function() {
                    Gikam.getLastModal().window.goBack();
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
                title : 'EMC.MODULE.MDM.BIZMDM.INDEXCATEGORIES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'emc-order-index-detail-base-info-form',
                    columns : 2,
                    url : Gikam.printf(_this.getCompUrl('baseInfoForm'), { id : _this.param.id }),
                    service : 'emcIndexCategoryServiceImpl',
                    fields : emcIndexCategory.getBaseInfoFormFields(),
                    onRendered : function() {

                    }
                } ]
            } ]
        }
    },
    deleteIndexSet : function() {
        var formData = Gikam.getComp('emc-order-index-detail-base-info-form').getData();
        var url = Gikam.printf(emcIndexCategory.baseUrl + '/index-sets');
        Gikam.getComp('emc-index-set-detail-field-grid').deleteRows(url);
    },

    urlManager : {
        baseInfoForm : emcIndexCategory.baseUrl + '/{id}',
        fieldGrid : emcIndexCategory.baseUrl + '/{id}/order-indexes/queries',
        indexSetGrid : emcIndexCategory.baseUrl + '/{id}/index-sets/queries',
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-order-index-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'emc-order-index-detail-field-grid' : this.getCompUrl('fieldGrid'),
            'emc-index-set-detail-field-grid' : this.getCompUrl('indexSetGrid'),
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
            id : 'emc-index-category-detail-layout',
            center : {
                items : [this.getBaseInfoTab()]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
