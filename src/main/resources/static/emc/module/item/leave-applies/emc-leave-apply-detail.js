emcLeaveApply.detailPage = {

    getBtnToolbar : function(operType) {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : _this.param.operType === 'query' ? [ {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ] : [ {
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
            } ]
        }
    },

    getBaseInfoTab : function(operType) {
        var _this = this;
        return {
            type : 'form',
            url : _this.getCompUrl('baseInfoForm'),
            service : 'emcLeaveApplyServiceImpl',
            id : 'emc-leave-apply-detail-base-info-form',
            fields : emcLeaveApply.getDetailFormColumns(operType),
            columns : 3,
            onLoadSuccess : function(data) {
                var form = Gikam.getComp('emc-leave-apply-detail-base-info-form');
                // 根据是否取证判断所取证件的是否可编辑
                if (form && data && operType == 'edit' && data.needCredentials === '1') {
                    form.toFieldsEdit([ 'credentials' ]);
                } else {
                    form.toFieldsReadonly([ 'credentials' ]);
                }

                // 根据操作类型判断还证日期是否可编辑
                if (operType === 'replenish') {
                    form.toFieldsEdit([ 'revertDate' ]);
                } else {
                    form.toFieldsReadonly([ 'revertDate' ]);
                }
            }
        };
    },

    urlManager : {
        baseInfoForm : emcLeaveApply.baseUrl + '/{id}'
    },

    setCompUrl : function() {
        Gikam.setCompUrl({
            'emc-leave-apply-detail-base-info-form' : this.getCompUrl('baseInfoForm')
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
        Gikam.getLastModal.window.save({
            url : emcLeaveApply.baseUrl + (Gikam.isEmpty(this.param.id) ? '' : '/' + this.param.id + '/v/' + Gikam.getComp('emc-leave-apply-detail-base-info-form').getData().version),
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
