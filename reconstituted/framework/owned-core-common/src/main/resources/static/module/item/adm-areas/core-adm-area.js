coreAdmArea = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'id',
            title : 'T_CORE_ADM_AREA.ID'
        }, {
            field : 'admAreaName',
            title : 'T_CORE_ADM_AREA.ADMAREANAME',
            editor : true
        } ]
    },

    getBaseInfoFormFields : function() {
        var _this = this;
        return [ {
            field : 'id',
            title : 'T_CORE_ADM_AREA.ID',
            preInsert : true,
            validators : [ 'remote[/secure/core/module/item/adm-areas/{id}/action/validate-unique', 'notEmpty', 'strLength[0,16]' ]
        }, {
            field : 'admAreaName',
            title : 'T_CORE_ADM_AREA.ADMAREANAME',
            preInsert : true
        } ]
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/item/adm-areas'

});