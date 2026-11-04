var coreTopology = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/core/module/topologies',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'code',
            title : 'T_CORE_TOPOLOGY.CODE',
            readonly : true,
        }, {
            field : 'usage',
            title : 'T_CORE_TOPOLOGY.USAGE',
            readonly : true,
        }, {
            field : 'createdTime',
            title : 'T_CORE_TOPOLOGY.CREATEDTIME',
            readonly : true,
        } ];
    },
    
    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_TOPOLOGY.ID',
            readonly : true
        }, {
            field : 'code',
            title : 'T_CORE_TOPOLOGY.CODE',
            validators : [ 'remote[/secure/core/module/topologies/action/validate-unique]', 'notEmpty', 'strLength[0,64]' ],
            preInsert : true
        }, {
            field : 'usage',
            title : 'T_CORE_TOPOLOGY.USAGE',
            validators : [ 'strLength[0,64]' ],
            preInsert : true
        } ];
    }
});
