var coreBpmnTarget = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_CORE_BPMN_TARGET.TARGETTABLE',
            field : 'ext$.targettablename'
        }, Gikam.status.getBpmnDynamicColumn('targetTable', 'targetId'), {
            title : 'T_CORE_BPMN_TARGET.TARGETID',
            field : 'targetId',
            width : 300
        }, {
            title : 'T_CORE_BPMN_TARGET.TARGETDESC',
            field : 'targetDesc',
            width : 300
        } ]
    },
    
    baseUrl : IFM_CONTEXT + '/secure/core/module/sys/bpmn-targets'
});