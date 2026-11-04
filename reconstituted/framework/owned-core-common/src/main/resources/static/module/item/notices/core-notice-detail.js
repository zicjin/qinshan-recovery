coreNotice.detailPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            id : 'core-notice-detail-tab',
            fill : true,
            panels : [ {
                title : 'CORE.MODULE.ITEM.NOTICES.TAB.BASE_INFO',
                items : [ {
                    type : 'form',
                    id : 'core-notice-detail-base-info-form',
                    readonly : true,
                    columns : 3,
                    url : _this.getCompUrl(),
                    fields : coreNotice.getBaseInfoFormFields(),
                } ]
            }, {
                title : 'GIKAM.FILE.ATTACHMENT',
                items : [ {
                    type : 'uploader',
                    id : 'core-notice-detail-uploader',
                    dbTable : 'T_CORE_NOTICE',
                    bizId : _this.param.id,
                    readonly : true
                } ]
            } ]
        }
    },

    getCompUrl : function() {
        if (Gikam.isEmpty(this.param.id)) {
            return '';
        }
        return Gikam.printf(coreNotice.baseUrl + '/{id}', {
            id : this.param.id
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getBaseInfoTab() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
