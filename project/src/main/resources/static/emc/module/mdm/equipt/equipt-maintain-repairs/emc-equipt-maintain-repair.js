var emcEquiptMaintainRepair = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT'
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN'
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE'
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT'
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'maintainRepairDate',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRDATE'
        }, {
            field : 'maintainRepairConcent',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRCONCENT',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainRepairSign',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRSIGN',
            validators : [ 'strLength[0,66]' ]
        }, {
            field : 'equiptFailure',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.EQUIPTFAILURE',
            validators : [ 'strLength[0,200]' ]
        }, {
            field : 'maintainrepairunit',
            title : 'T_EMC_EQUIPT_MAINTAIN_REPAIR.MAINTAINREPAIRUNIT',
            validators : [ 'strLength[0,66]' ]
        }, ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equipt-maintain-repairs'
});
