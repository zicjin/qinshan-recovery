emcMonitorAbility.detailPage = {

    getBaseInfoTab : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : '',
                items : [ {
                    type : 'form',
                    url : _this.getCompUrl(_this.param.id),
                    service : 'emcMonitorAbilityServiceImpl',
                    id : 'emc-monitor-ability-detail-base-info-form',
                    fields : emcMonitorAbility.getBaseInfoFormFields(),
                    columns : 3
                } ]
            } ]
        }
    },
    
    getCompUrl : function(itemId) {
        if (itemId) {
            return Gikam.printf(emcMonitorAbility.baseUrl + '/{id}', {
                id : itemId
            })
        }
    },
    
    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfoTab() ]
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
