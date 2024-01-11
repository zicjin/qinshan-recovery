emcProjectQuery.condQueryPage = {

    getBaseInfo : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-project-query-cond-query-form',
            fields : emcProjectQuery.getQueryFields(),
            columns : 2,
	        requestData : {
	            orgId_SB : workspace.user.ext$.parentorgid
	        }, 
        }
    },

    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'select',
                onClick : function() {
                    var data = Gikam.getComp('emc-project-query-cond-query-form').getData();
                    Gikam.getComp('emc-project-query-edit-list-grid').refresh({
                        url : emcProjectQuery.projectUrl + '/queries/all',
                        requestData : {
                            targetType : 'modifyProjectInfo',
                            projNo_SC : data.projNo || '',
                            projName_SC : data.projName || '',
                            testOrgName_SC : data.testOrgName || '',
                            entrustOrgName_SC : data.entrustOrgName || '',
                            createdByName_SC : data.createdByName || '',
                            createdByOrgName_SC : data.createdByOrgName || '',
                            bizCategoryId_SC : data.bizCategoryId || '',
                            taskNo_SC : data.taskNo || '',

                            registDate_DGOE : data.registStartDate || '',
                            registDate_DLOE : data.registEndDate || '',

                            samplingStartDate : data.samplingStartDate || '',
                            samplingEndDate : data.samplingEndDate || '',
                            orgId_SB : workspace.user.ext$.parentorgid,
                        }
                    });
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            center : {
                items : [ this.getBaseInfo() ]
            },
            south : {
                height : '100',
                items : [ this.getBtnToolbar() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
};
