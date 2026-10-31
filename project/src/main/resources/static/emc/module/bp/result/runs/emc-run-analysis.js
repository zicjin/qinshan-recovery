// 首页分析录入
emcRun.runAnalysisPage = {
    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            fill : true,
            id : 'emc-run-analysis-grid',
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-tasks/queries/analysis',
            refreshFromSearch : false,
            requestData : {
                status_SEQ : 'preLogged',
                analysisType_SEQ : 'analysis',
                authority_org : '1',
                inputUserId_SC : window.top.workspace.user.id
            },
            columns : _this.getAnalysisListColumns(),
            generalButtonGroup : false,
            filter : false,
            fill : true,
            page : false
        }
    },

    getAnalysisListColumns : function() {
        return [ {
            field : 'testName',
            title : parent.Gikam.propI18N('T_EMC_ORDER_TASK.TESTNAME'),
            width : 140
        }, {
            field : 'ext$.orderno',
            title : parent.Gikam.propI18N('T_EMC_ORDER_TASK.ORDERNO'),
            width : 120
        }, {
            field : 'ext$.samplebegindate',
            title : parent.Gikam.propI18N('T_EMC_ORDER_TASK.SAMPLEBEGINDATE'),
            width : 100,
            formatter : function(i, v, r) {
                if (!Gikam.isEmpty(v)) {
                    return v.substring(0, 10);
                } else {
                    return '';
                }
            }
        } ];
    },
    
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-run-analysis-layout',
            renderTo : document.body,
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
};
