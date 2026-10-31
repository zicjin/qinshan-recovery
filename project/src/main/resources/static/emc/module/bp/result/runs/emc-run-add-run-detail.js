emcRun.addRunDetailPage = {

    getRunGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-add-run-detail-run-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/unique-orders/queries',
            requestData:{
                status_SEQ : 'logged',
                analysisType_SEQ : 'analysis',
                // authority_org : '1',
                runId:'empty',
                testMethodId_IN : _this.param.testMethodId,
                projId_IN : _this.param.projId,
                orderCategory_IN : _this.param.orderCategory,
                ORDERDATESTR_IN : _this.param.orderDate,
                /*startAcceptedDate : '2023-01-31'*/
                // pretreatmentStatus_SEQ : '1',
                // org_filter : '1'
            },
            order : {
                'ext$.projname' : 'asc',
                'ext$.orderno' :'asc'
            },
            headRequestData : ((Gikam.getComp('emc-run-edit-list-grid')
                && Gikam.getComp('emc-run-edit-list-grid').getOptions().headRequestData
                && Gikam.getComp('emc-run-edit-list-grid').getOptions().headRequestData['ext$.projno_CISC']
            ) ? {'ext$.projno_CISC' : Gikam.getComp('emc-run-edit-list-grid').getOptions().headRequestData['ext$.projno_CISC']} : {}),
            columns : emcRun.getAddRunListColumns(),
            rowStyleFormatter : function(row) {
                if (row.ext$.projectremind === '1') {
                    return {
                        backgroundColor: 'yellow'
                    }
                }
            },
            filterOpen : true,
            page : true,
            // onRendered : function() {
            //     var grid = Gikam.getComp('emc-run-edit-list-grid');
            //     if(grid && grid.getFilterRequestData()){
            //         Gikam.getComp('emc-run-add-run-detail-run-grid').setOptions({
            //             headRequestData : grid.getFilterRequestData()
            //         })
            //     }
            // },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var leftData = Gikam.getComp('emc-run-add-run-detail-run-grid').getSelections();
                    var rightData = Gikam.getComp('emc-run-add-run-detail-equipt-grid').getSelections();
                    if(Gikam.isEmpty(leftData)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_AT_LEAST_ONE_SAMPLE');
                        return;
                    }
                    if(Gikam.isEmpty(rightData)) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.CHOOSE_AT_LEAST_ONE_EQUIPT');
                        return;
                    }

                    let methodIds = Array.from(new Set(leftData.map(item => item.methodId)));
                    if (methodIds.length > 1) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.TOO_MANY_METHOD');
                        return;
                    }

                    leftData.forEach(item => item.testMethodIds = _this.param.testMethodId);

                    Gikam.getLastModal().close([leftData, rightData]);
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                id : 'emc-run-add-run-detail-back-button',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.ADD_PHFLAG',
                icon : 'add',
                hidden : true,
                onClick : function() {
                    _this.addPhFlag();
                }
            },{
                type : 'button',
                text : 'EMC.MODULE.BP.RESULT.RUNS.BUTTON.DEL_PHFLAG',
                icon : 'remove',
                hidden : true,
                onClick : function() {
                    _this.delPhFlag();
                }
            }  ]
        }
    },

    getEquiptGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-add-run-detail-equipt-grid',
            url : emcRun.baseUrl + Gikam.printf('/equipt/{testMethodId}', {
                testMethodId :  _this.param.testMethodId,
            }),
            columns : emcRun.getEquiptChooseListColumns(),
            filterOpen : true,
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    data.forEach(function(e, index) {
                        if (e.ext$.defaultflag === '1') {
                            Gikam.getComp('emc-run-add-run-detail-equipt-grid').activeRowByIndex(index);
                            return;
                        }
                    });

                }
            }
        }
    },

    addPhFlag : function () {
        var selections = Gikam.getComp('emc-run-add-run-detail-run-grid').getSelections();
        //添加ph标识
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/add-phflag', Gikam.getJsonWrapper({}, [ 'pfOrderTaskServiceImpl', selections ])).done(function (data) {
            Gikam.getComp('emc-run-add-run-detail-run-grid').refresh();
        })
    },

    delPhFlag : function(){
        var selections = Gikam.getComp('emc-run-add-run-detail-run-grid').getSelections();
        //添加ph标识
        Gikam.postText(IFM_CONTEXT + '/secure/emc/module/bp/bp/orders/action/del-phflag', Gikam.getJsonWrapper({}, [ 'pfOrderTaskServiceImpl', selections ])).done(function (data) {
            Gikam.getComp('emc-run-add-run-detail-run-grid').refresh();
        })
    },


    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-add-run-detail-layout',
            west : {
                width : '50%',
                items : [ this.getRunGridParam() ]
            },
            center : {
                items : [ this.getEquiptGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    unique : function(arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i]]) {
                res.push(arr[i]);
                json[arr[i]] = 1;
            }
        }
        return res;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
