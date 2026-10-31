emcTestMethod.itemCopyPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var copyForm = Gikam.getComp('emc-test-method-item-copy-form');
                    var formData = copyForm.getData();
                    var testMethod = Gikam.getComp('emc-test-method-edit-list-grid').getActivedRow();
                    // 复制提交时对分析项名称进行唯一性校验
                    Gikam.post(emcTestMethod.baseUrl + '/methods/' + testMethod.id + '/action/validate-unique', Gikam.getJsonWrapper({
                        vu : {
                            id : testMethod.id,
                            itemName : formData.itemName
                        }
                    })).done(function (data) {
                        if (!data.valid) {
                            copyForm.showValidateMessage([ {
                                title : 'T_EMC_ITEM.ITEMNAME',
                                message : data.message
                            } ]);
                            return;
                        }

                        var url = emcTestMethod.baseUrl + '/items/action/copy';
                        Gikam.getLastModal().window.showMask(true);
                        Gikam.post(url, Gikam.getJsonWrapper(null, [ 'emcItemServiceImpl', [ formData ] ])).always(function() {
                            Gikam.getLastModal().close();
                        }).always(function() {
                            Gikam.getLastModal().window.closeMask();
                        });
                    })
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBaseInfoForm : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-test-method-item-copy-form',
            fields : emcTestMethod.getCopyItemFormFields(),
            columns : 1,
            onRendered : function() {
                this.setData({
                    id : _this.param.id,
                    itemName : _this.param.itemName,
                    itemAliasName : _this.param.itemAliasName
                });
            }
        };
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoForm() ]
            },
            renderTo : Gikam.getLastModal().window.$dom,
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    },

}
