coreEmail.detailPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    workspace.window.goBack();
                }
            } ]
        }
    },

    getBaseInfoTab : function() {
        var _this = this;
        
        return {
            type : 'tab',
            width : 900,
            panels : [ {
                title : 'CORE.MODULE.ITEM.EMAILS.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-email-detail-base-info-form',
                    url : _this.getCompUrl('baseInfoForm'),
                    fields : coreEmail.getBaseInfoFormFields()
                } ]
            } ]
        }
    },
    
    getEmailReceiptentLineTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : '1000',
            panels : [ {
                title : 'CORE.MODULE.ITEM.EMAILS.TAB.RECEIPTED_INFO',
                items : [ {
                    type : 'simpleGrid',
                    id : 'core-email-receiptent-line-list-grid',
                    url : _this.getCompUrl("receiptentListGrid"),
                    columns : coreEmail.getEmailReceipentListColumns(),
                } ]
            } ]
        }
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            center : {
                items : [ _this.getBtnToolbar(), _this.getBaseInfoTab(), _this.getEmailReceiptentLineTab() ]
            },
            renderTo : workspace.window.$dom
        });
        
        return this;
    },

    urlManager : {
        baseInfoForm : coreEmail.baseUrl + '/{id}',
        receiptentListGrid : coreEmail.baseUrl + '/{id}/email-receipents/queries'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'core-email-detail-base-info-form' : this.getCompUrl('baseInfoForm'),
            'core-email-receiptent-line-list-grid' : this.getCompUrl('receiptentListGrid')
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
    
    init : function(param) {
        this.param = param;
        
        this.create();
    }
};