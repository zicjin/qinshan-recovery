emcHomeAttachment.chooseDateDetailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-home-attachment-choose-date-detail-form').getData().endDate);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'back',
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
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.ITEM.HOMEATTACHMENTS.TITLE.CHOOSE_END_DATE',
                items : [ {
                    type : 'form',
                    autoSave : true,
                    columns : 1,
                    id : 'emc-home-attachment-choose-date-detail-form',
                    fields : emcHomeAttachment.getChooseDateFields()
                } ]
            } ]
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

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
