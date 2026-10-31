var emcSampleGasParameter = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results',

    getGridListColumns : function () {
        return [ {
            checkbox: true
        }, {
            index: true
        }, {
            title: '点位',
            field: 'monitorpointname',
            width: 120
        }, {
            title: '周期',
            field: 'cycle',
            width: 120
        }, {
            title: '时段',
            field: 'frequency',
            width: 120
        }, {
            title: '参数编号',
            field: 'sampleparamorderno',
            width: 120
        } ];
    },

    getResultListColumns : function () {
        return [ {
            checkbox: true
        }, {
            index: true
        }, {
            title: '点位',
            field: 'monitorpointname',
            width: 120
        }, {
            title: '周期',
            field: 'cycle',
            width: 120
        }, {
            title: '时段',
            field: 'frequency',
            width: 120
        } ];
    }

});
