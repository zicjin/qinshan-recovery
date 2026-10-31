var emcProjectEditLog = Gikam.getPageObject({

    getSearchListColumns : function() {
        return [ {
            checkbox : true
        },  {
            field : 'projNo',
            title : 'T_EMC_PROJECT_EDIT_LOG.PROJNO'
        }, {
            field : 'isSample',
            title : 'T_EMC_PROJECT_EDIT_LOG.ISSAMPLE',
            type : 'select',
            search : true,
            category : 'sys',
            editor : false,
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'operateInfo',
            title : 'T_EMC_PROJECT_EDIT_LOG.OPERATEINFO'
        }, {
            field : 'createdByName',
            title : 'T_EMC_PROJECT_EDIT_LOG.CREATEDBYNAME'
        }, {
            field : 'createdTime',
            title : 'T_EMC_PROJECT_EDIT_LOG.CREATEDTIME'
        }];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/bp/project/project-edit-logs'
});
