var emcSampleSamplingEquipt = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/sample/samples',

    getOrderTaskListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.folderno',
            title : 'T_EMC_FOLDER.FOLDERNO',
        }, {
            field : 'ext$.orderno',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 200
        }, {
            field : 'testName',
            title : 'T_EMC_ORDER_TASK.TESTNAME',
        }, {
            field : 'samplingEquiptName',
            title : 'T_EMC_ORDER_TASK.SAMPLINGEQUIPTNAME',
            editor : true,
            category : 'equipt',
            type : 'choose',
            onBeforeChoose : function() {
                return {
                    equiptcategoryname_CISC : '采样设备',
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var samplingEquiptId = [];
                    var samplingEquiptName = [];
                    Gikam.each(rows, function(index, item) {
                        samplingEquiptId.push(item.id);
                        samplingEquiptName.push(item.equiptName);
                    });
                    this.setData({
                        id : Gikam.getComp('emc-sampling-equipt-edit-list-grid').getActivedRow().id,
                        samplingEquiptId : samplingEquiptId.join(','),
                        samplingEquiptName : samplingEquiptName.join(','),
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-sampling-equipt-edit-list-grid').getActivedRow().id,
                    samplingEquiptId : '',
                    samplingEquiptName : '',
                });
            }
        } ];
    },

});
