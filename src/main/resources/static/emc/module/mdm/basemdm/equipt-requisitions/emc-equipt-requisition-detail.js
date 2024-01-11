emcEquiptRequisition.detailPage = {

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : _this.param.operType === 'edit' ? [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ] : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
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
            type : 'form',
            url : _this.getCompUrl('baseInfoForm'),
            service : 'emcEquiptRequisitionServiceImpl',
            id : 'emc-equipt-requisition-detail-base-info-form',
            fields : emcEquiptRequisition.getDetailFormColumns(_this.param.operType),
            columns : 3
        };
    },

    urlManager : {
        baseInfoForm : emcEquiptRequisition.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-equipt-requisition-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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

    save : function(callback, showLoading) {
        workspace.window.save({
            url : emcEquiptRequisition.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id + '/v/' + Gikam.getComp('emc-equipt-requisition-detail-base-info-form').getData().version),
            page : this,
            showLoading : showLoading
        }).done(function() {
            callback && callback();
        });
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoTab(this.param.operType) ]
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
