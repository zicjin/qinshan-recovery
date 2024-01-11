emcTestMethod.itemDetailPage = {

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'EMC.MODULE.MDM.BIZMDM.TESTS.TITLE.TEST_METHOD_ITEM_DETAIL',
                items : [ {
                    type : 'form',
                    id : 'emc-test-method-item-detail-form',
                    fields : emcTestMethod.getItemFormFields(),
                    columns : 3,
                    service : 'emcItemServiceImpl',
                    url : Gikam.printf(emcTestMethod.baseUrl + '/items/{itemId}', {
                        itemId : _this.param.id
                    }),
                    onBeforeUpdate : function(data, keys) {
                        var form = Gikam.getComp('emc-test-method-item-detail-form')
                        if (Gikam.isNotEmpty(data.itemName)) {
                            if (!form.validate()) {
                                return false;
                            }
                        }
                    }
                } ]
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            cls : 'detail-page',
            center : {
                items : [ this.getBaseInfoForm() ]
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
