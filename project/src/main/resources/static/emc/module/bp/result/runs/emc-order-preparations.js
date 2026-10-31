var emcOrderPreparations = Gikam.getPageObject({
    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/result/runs',
    getOrderPreparationsEditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcOrderPreparations.workflow.dbTable), {
            field : 'projNo',
            title : 'T_EMC_RUN.PROJNO',
            readonly : true,
            width : 100,
        }, {
            field : 'projName',
            title : 'T_EMC_RUN.PROJNAME',
        }, {
            field : 'bizCategoryId',
            title : 'T_EMC_RUN.BIZCATEGORY',
            type : 'select',
            search : true,
            category : 'bizCategory',
            firstBlank : false,
        },];
    },

    getOrderPreparationsColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_RUN.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_RUN.ORDERCATEGORY',
            width : 120,
        }, {
            field : 'ext$.airdrytype',
            title : 'T_EMC_RUN.AIRDRYTYPE',
            editor : true,
			width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'airdrytype'
            },
        }, {
            field : 'ext$.dryingtemp',
            title : 'T_EMC_RUN.DRYINGTEMP',
            editor : true,
        }, {
            field : 'ext$.grindmode',
            title : 'T_EMC_RUN.GRINDMODE',
            editor : true,
			width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'grindmode'
            },
        }, {
            field : 'ext$.samplecontainer',
            title : 'T_EMC_RUN.SAMPLECONTAINER',
            editor : true,
			width : 100,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplecontainer'
            },
        }, {
            field : 'ext$.wetweight',
            title : 'T_EMC_RUN.WETWEIGHT',
            editor : true,  
        }, {
            field : 'ext$.dryweight',
            title : 'T_EMC_RUN.DRYWEIGHT',
            editor : true,
        } , {
            field : 'ext$.impurityweight',
            title : 'T_EMC_RUN.IMPURITYWEIGHT',
            editor : true,
        } , {
            field : 'ext$.granularity02',
            title : 'T_EMC_RUN.GRANULARITY02',
            editor : true,
        } , {
            field : 'ext$.granularity025',
            title : 'T_EMC_RUN.GRANULARITY025',
            editor : true,
        } , {
            field : 'ext$.granularity015',
            title : 'T_EMC_RUN.GRANULARITY015',
            editor : true,
        } , {
            field : 'ext$.discardweight',
            title : 'T_EMC_RUN.DISCARDWEIGHT',
            editor : true,
        }  ];
    },
    
    getOrderPreparationsBatchUpdateFields : function() {
        return [  {
            field : 'ext$.airdrytype',
            title : 'T_EMC_RUN.AIRDRYTYPE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'airdrytype'
            },
        }, {
            field : 'ext$.dryingtemp',
            title : 'T_EMC_RUN.DRYINGTEMP',
        }, {
            field : 'ext$.grindmode',
            title : 'T_EMC_RUN.GRINDMODE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'grindmode'
            },
        }, {
            field : 'ext$.samplecontainer',
            title : 'T_EMC_RUN.SAMPLECONTAINER',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'samplecontainer'
            },           
        }, {
            field : 'ext$.wetweight',
            title : 'T_EMC_RUN.WETWEIGHT',
        }, {
            field : 'ext$.dryweight',
            title : 'T_EMC_RUN.DRYWEIGHT',
        } , {
            field : 'ext$.impurityweight',
            title : 'T_EMC_RUN.IMPURITYWEIGHT',
        } , {
            field : 'ext$.granularity02',
            title : 'T_EMC_RUN.GRANULARITY02',
        } , {
            field : 'ext$.granularity025',
            title : 'T_EMC_RUN.GRANULARITY025',
        } , {
            field : 'ext$.granularity015',
            title : 'T_EMC_RUN.GRANULARITY015',
        } , {
            field : 'ext$.discardweight',
            title : 'T_EMC_RUN.DISCARDWEIGHT',
        } ];
    },
    
    workflow : {
        dbTable : 'T_EMC_RUN',
        columns : [ {
            field : 'id',
            title : 'T_EMC_RUN.ID'
        } ]
    }

});
