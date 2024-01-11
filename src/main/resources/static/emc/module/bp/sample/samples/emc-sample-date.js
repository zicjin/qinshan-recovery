var emcSampleDate = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',

    getOrderListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.monitorpointname',
            title : 'T_EMC_FOLDER.MONITORPOINTNAME',
            width : 100,
        }, {
            field : 'ext$.monitorpointno',
            title : 'T_EMC_FOLDER.MONITORPOINTNO',
            width : 100,
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 110,
        }, {
            field : 'sampleBeginDate',
            title : 'T_EMC_ORDER.SAMPLEBEGINDATE',
            type : 'date',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-date-order-list-grid').getData()[rowIndex];
                var start = new Date(grid.sampleBeginDate);
                var end = new Date(grid.sampleCompleteDate);
                if (start && end && start.getTime() > end.getTime()){
                    Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DATE_LESS_THEN");
                    return;
                }
            }
        }, {
            field : 'sampleBeginTime',
            title : 'T_EMC_ORDER.SAMPLEBEGINTIME',
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-date-order-list-grid').getData()[rowIndex];
                var startDate = new Date(grid.sampleBeginDate);
                var endDate = new Date(grid.sampleCompleteDate);
                if(startDate && endDate && startDate.getTime() === endDate.getTime()){
                    var startTime = grid.sampleBeginTime;
                    var endTime = grid.sampleCompleteTime;
                    if(startTime && endTime && startTime >= endTime){
                        Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.TIME_LESS_THEN");
                        return;
                    }
                }
            }
        }, {
            field : 'sampleCompleteDate',
            title : 'T_EMC_ORDER.SAMPLECOMPLETEDATE',
            type : 'date',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-date-order-list-grid').getData()[rowIndex];
                var start = new Date(grid.sampleBeginDate);
                var end = new Date(grid.sampleCompleteDate);
                if (start && end && start.getTime() > end.getTime()){
                    Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.DATE_LESS_THEN");
                    return;
                }
            }
        }, {
            field : 'sampleCompleteTime',
            title : 'T_EMC_ORDER.SAMPLECOMPLETETIME',
            type : 'time',
            editor : true,
            width : 110,
            onChange : function(field, value, rowIndex, oldValue, text) {
                var grid = Gikam.getComp('emc-sample-date-order-list-grid').getData()[rowIndex];
                var startDate = new Date(grid.sampleBeginDate);
                var endDate = new Date(grid.sampleCompleteDate);
                if(startDate && endDate && startDate.getTime() === endDate.getTime()){
                    var startTime = grid.sampleBeginTime;
                    var endTime = grid.sampleCompleteTime;
                    if(startTime && endTime && startTime >= endTime){
                        Gikam.alert("EMC.MODULE.BP.SAMPLE.SAMPLES.ORDER.TIP.TIME_LESS_THEN");
                        return;
                    }
                }
            }
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
            width : 130
        } ];
    }

});
