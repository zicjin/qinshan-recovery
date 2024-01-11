emcMtlUseRecord.applyPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'pass',
                onClick : function() {
                    _this.save();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 1,
            id : 'emc-mtl-apply-base-info-form',
            fields : emcMtlUseRecord.getApplyFormFields(),
            titleWidth : 80
        }
    },

    save : function() {
        var _this = this;
        var form = Gikam.getComp('emc-mtl-apply-base-info-form');
        if(form.validate()) {
            data = form.getData();
            var selections = Gikam.getComp('emc-mtl-apply-edit-list-grid').getSelections();
            var insertData = selections.map(function(value, index) {
                data.stockId = value.id;
                data.mtlId=value.ext$.mtlid;
                return data;
            });
            Gikam.getLastModal().window.showMask(true);
            Gikam.put(Gikam.printf(emcMtlUseRecord.baseUrl + '/{id}/action/apply', {
                id : selections[0].id
            }), Gikam.getJsonWrapper({}, [ null, insertData ])).always(function() {
                Gikam.getLastModal().close();
                Gikam.getLastModal().window.closeMask();
            });
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBtnToolbar(), this.getBaseInfoTab() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function() {
        this.createPage();
    }
};
