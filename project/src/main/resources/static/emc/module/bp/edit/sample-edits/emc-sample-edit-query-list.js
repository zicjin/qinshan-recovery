emcSampleEdit.queryPage = {

    getBaseInfo : function() {
        var _this = this;
        return {
            type : 'form',
            id : 'emc-sample-edit-query-form',
            fields : emcSampleEdit.getQueryFields(),
            columns : 2,
            autoSave : false,
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
                    var data = Gikam.getComp('emc-sample-edit-query-form').getData();
                    Gikam.getComp('emc-sample-edit-list-project-sample-grid').refresh({
                        url : '/secure/emc/module/bp/sample/samples/queries/all',
                        requestData : {
                            targetType : 'editSample',
                            projNo_EQ : data.projNo || '',
                            createdByName_EQ : data.createdByName || '',
                            projName_EQ : data.projName || '',
                            bizCategoryId_EQ : data.bizCategoryId || '',
                            sampleNo_EQ : data.sampleNo || '',

                            sceneUserId : data.sceneUserId || '',
                            sceneUserName : data.sceneUserName || '',
                            sampleUserName : data.sampleUserName || '',
                            sampleBeginDate : data.sampleBeginDate || '',
                            sampleCompleteDate : data.sampleCompleteDate || '',
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
                items : [ this.getBtnToolbar(), this.getBaseInfo() ]
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
