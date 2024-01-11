var emcAuditedAnalysis = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/sa/audited-analysiss',
    runUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'ext$.projectno',
            title : 'T_EMC_PROJECT.PROJNO',
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'orderCategory',
            title : 'T_EMC_REPORT.ORDERCATEGORY'
        }, {
            field : 'ext$.projcreatedbyname',
            title : 'T_EMC_PROJECT.CREATEDBYNAME'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'ext$.entrustorgname',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'ext$.bizcategoryname',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
        }, ];
    },

    getRunColumns : function() {
        return [ {
            index : true,
        }, {
            field : 'id',
            title : 'T_EMC_RUN.RUNID',
            type : 'link'
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
        }, {
            field : 'testName',
            title : 'T_EMC_RUN.TESTNAME',
        }, {
            field : 'analysisUserName',
            title : 'T_EMC_RUN.ANALYSISUSERNAME',
            category : 'emp',
            type : 'choose',
        }, {
            field : 'partakeUserName',
            title : 'T_EMC_RUN.PARTAKEUSERNAME',
            category : 'emp',
            type : 'choose',
        }, {
            field : 'checkUserName',
            title : 'T_EMC_RUN.CHECKUSERNAME',
            category : 'emp',
            type : 'choose',
        }, {
            field : 'ext$.equiptcode',
            title : 'T_EMC_RUN.EQUIPTID',
        }, {
            field : 'calibrationCurve',
            title : 'T_EMC_RUN.CALIBRATIONCURVE',
        }, {
            field : 'analysisBeginDate',
            title : 'T_EMC_RUN.ANALYSISBEGINDATE',
            type : 'date'
        }, {
            field : 'analysisBeginTime',
            title : 'T_EMC_RUN.ANALYSISBEGINTIME',
            type : 'dateTime',
        }, {
            field : 'analysisEndDate',
            title : 'T_EMC_RUN.ANALYSISENDDATE',
            type : 'date'
        }, {
            field : 'analysisEndTime',
            title : 'T_EMC_RUN.ANALYSISENDTIME',
            type : 'dateTime'
        }, {
            field : 'ext$.assistequiptname',
            title : 'T_EMC_RUN.ASSISTEQUIPTID',
            category : 'equipt',
            type : 'choose',
        }, {
            field : 'temperature',
            title : 'T_EMC_RUN.TEMPERATURE',
        }, {
            field : 'humidity',
            title : 'T_EMC_RUN.HUMIDITY',
        }, {
            field : 'receiveDate',
            title : 'T_EMC_RUN.RECEIVEDATE',
            type : 'date'
        }, {
            field : 'receiveTime',
            title : 'T_EMC_RUN.RECEIVETIME',
            type : 'dateTime'
        }, {
            field : 'handleDate',
            title : 'T_EMC_RUN.HANDLEDATE',
            type : 'date'
        }, {
            field : 'handleMethod',
            title : 'T_EMC_RUN.HANDLEMETHOD',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'handleMethod'
            },
        }, {
            field : 'beforeHandle',
            title : 'T_EMC_RUN.BEFOREHANDLE',
        }, {
            field : 'methodParam',
            title : 'T_EMC_RUN.METHODPARAM',
        } ];
    },
});
