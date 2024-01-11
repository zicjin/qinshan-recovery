var emcProjectQc = Gikam.getPageObject({

    projectBaseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/projects',
    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/project/project-qcs",

    getProjectListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcProject.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            readonly : true,
            width : 100,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return "<div style='vertical-align: middle'>"+img + v+"</div>";
                }else{
                    return v;
                }
            },
        }, {
            field : 'projName',
            title : 'T_EMC_PROJECT.PROJNAME',
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT.CREATEDBYNAME',
            width : 80,
        }, {
            field : 'testOrgName',
            title : 'T_EMC_PROJECT.TESTORG'
        }, {
            field : 'entrustOrgName',
            title : 'T_EMC_PROJECT.ENTRUSTORG'
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_PROJECT.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
        }, {
            field : 'urgent',
            title : 'T_EMC_PROJECT.URGENT',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        } ];
    },

    getQcListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderCategory',
            title : 'T_EMC_PROJECT_QC.ORDERCATEGORY',
            width : 300
        } ];
    },

    getQcLineListColumns : function () {
        return [ {
            checkbox : true
        }, {
            field : 'testName',
            title : 'T_EMC_PROJECT_QC.TESTNAME',
        }, {
            field : 'totalQty',
            title : 'T_EMC_PROJECT_QC.TOTALQTY',
        } ]
    },

});
