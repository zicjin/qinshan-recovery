var coreQualityControlPlan = Gikam.getPageObject({

    getChooseListColumns : function(isSingle) {
        var columns = [{
            checkbox : true
        }, {
            field : 'itemName',
            title : 'T_CORE_QUALITY_CONTROL_PLAN.ITEMNAME'
        }];
        if(isSingle == 1) {
            columns.splice(0, 1, {
                radio : true
            })
        }
        return columns;
    },

    baseUrl : IFM_CONTEXT + '/secure/core/module/qms/quality-controls/quality-control-plans'
});
