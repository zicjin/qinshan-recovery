emcSampleSamplingEquipt.editPage = {

    // 列表布局参数
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-sampling-equipt-edit-list-grid',
            url : emcSampleSamplingEquipt.baseUrl + '/order-tasks/queries',
            requestData : _this.param,
            columns : emcSampleSamplingEquipt.getOrderTaskListColumns(),
            page : true,
            pageSize : 100,
            service : 'emcOrderTaskServiceImpl',
            group : {
                fields : [ 'ext$.folderno', 'ext$.orderno' ],
                formatter : function(row) {
                    return row.ext$.folderno + " : " + row.ext$.orderno;
                }
            },
            readonly : _this.param.readonly,
            toolbar : [ {
                type : 'button',
                text : 'EMC.MODULE.BP.SAMPLE.SAMPLES.BUTTON.BATCH_INPUT',
                icon : 'add',
                hidden : _this.param.readonly,
                onClick : function() {
                    var selections = Gikam.getComp('emc-sampling-equipt-edit-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    Gikam.create('modal', {
                        width : '60%',
                        title : Gikam.choose.getConfig('equipt').title,
                        url : Gikam.choose.getConfig('equipt').url + Gikam.param({
                            equiptcategoryname_CISC : '采样设备',
                        }),
                        onAfterClose : function(rows) {
                            if (Gikam.isEmpty(rows)) {
                                return;
                            }

                            var samplingEquiptId = [];
                            var samplingEquiptName = [];
                            Gikam.each(rows, function(index, item) {
                                samplingEquiptId.push(item.id);
                                samplingEquiptName.push(item.equiptName);
                            });

                            selections.forEach(function (item) {
                                item.samplingEquiptId = samplingEquiptId.join(',');
                                item.samplingEquiptName = samplingEquiptName.join(',');
                            });

                            workspace.window.showMask(true);
                            Gikam.put(emcSampleSamplingEquipt.baseUrl + '/instant', Gikam.getJsonWrapper(null, [ 'emcOrderTaskServiceImpl', selections ])).done(function() {
                                Gikam.getComp('emc-sampling-equipt-edit-list-grid').refresh();
                            }).always(function() {
                                workspace.window.closeMask();
                            });
                        }
                    });
                }
            } ]
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
