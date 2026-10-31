emcDataQuery.editPage = {

    getFormParam : function() {
        var _this = this;
        return {
            type : 'form',
            columns : 4,
            id : 'emc-data-query-edit-list-form',
            fields : emcDataQuery.getBaseInfoFormFields(),
            onRendered : function() {
                Gikam.getComp('emc-data-query-edit-list-form').hideFields([ 'bizCategoryCode' ]);
            }
        }
    },

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-data-query-edit-list-grid',
            columns : emcDataQuery.getEditListColumns(),
            initRequest : true,
            refreshFromSearch : false,
            page :true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.SEARCH',
                icon : 'preview',
                onClick : function() {
                    _this.queryData();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLEAN_CONDITION',
                icon : 'cancel',
                onClick : function() {
                    _this.cleanData();
                }
            } ]
        }
    },

    queryData : function() {
        var formData = Gikam.getComp('emc-data-query-edit-list-form');
        console.log(formData.getData());
        if (formData.validate()) {
            var gridData = Gikam.getComp('emc-data-query-edit-list-grid');
            var bizCategoryCode = formData.getField('bizCategoryCode').value;
            var sampleCategory = formData.getField('sampleCategory').value;
            var monitorPointNo = formData.getField('monitorPointNo').value;
            var beginDate = formData.getField('beginDate').value;
            if(beginDate === '' ||beginDate.trim().length === 0){
                beginDate = null;
            }
            var endDate = formData.getField('endDate').value;
            if(endDate === '' ||endDate.trim().length === 0){
                endDate = null;
            }
            console.log(beginDate);
            console.log(endDate);
            var projNo = formData.getField('projNo').value;
            var projName = formData.getField('projName').value;
            var orderNo = formData.getField('orderNo').value;
            var entrustOrgName = formData.getField('entrustOrgName').value;
            var testOrgName = formData.getField('testOrgName').value;
            workspace.window.showMask(true);
            Gikam.postSync(IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/data-query-titles/queries', Gikam.getJsonWrapper({
                f : {
                    BIZCATEGORYCODE_IN : bizCategoryCode,
                    ORDERCATEGORY_IN : sampleCategory,
                    MONITORPOINTNO_IN : monitorPointNo,
                    SAMPLEBEGINDATE_DGOE : beginDate,
                    SAMPLEBEGINDATE_DLOE : endDate,
                    PROJNO_SC : projNo,
                    PROJNAME_SC : projName,
                    ORDERNO_SC : orderNo,
                    ENTRUSTORGNAME_SC : entrustOrgName,
                    TESTORGNAME_SC : testOrgName,
                    ORDERTASKSTATUS_SEQ : 'done',
                    REPORTDISPLAY_SEQ : '1'
                }
            }, [ '', [] ])).done(function(columns) {
                var oldColumns = emcDataQuery.getEditListColumns();

                var condArr = [];
                columns.forEach(function(column) {
                    column.styleFormatter = function textStyleFormatter() {
                        return {
                            backgroundColor : 'palegreen'
                        }
                    };
                    condArr.push(column);
                });
                var newColumns = oldColumns.slice(0, 8).concat(condArr, oldColumns.slice(8));
                gridData.refreshColumns(newColumns);
            });
            gridData.setOptions({
                url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/data-query-rows/queries',
                requestData : {
                    BIZCATEGORYCODE_IN : bizCategoryCode,
                    ORDERCATEGORY_IN : sampleCategory,
                    MONITORPOINTNO_IN : monitorPointNo,
                    SAMPLEBEGINDATE_DGOE : beginDate,
                    SAMPLEBEGINDATE_DLOE : endDate,
                    PROJNO_SC : projNo,
                    PROJNAME_SC : projName,
                    ORDERNO_SC : orderNo,
                    ENTRUSTORGNAME_SC : entrustOrgName,
                    TESTORGNAME_SC : testOrgName,
                    ORDERTASKSTATUS_SEQ : 'done',
                    REPORTDISPLAY_SEQ : '1'
                }
            });
            gridData.refresh().always(function() {
                workspace.window.closeMask();
            });
        }
    },
    
    cleanData : function() {
        Gikam.getComp('emc-data-query-edit-list-form').cleanData();
        var gridData = Gikam.getComp('emc-data-query-edit-list-grid');
        gridData.setOptions({
            url : IFM_CONTEXT + '/secure/emc/module/bp/bp/order-task-results/data-query-rows/queries',
            requestData : {
                BIZCATEGORYCODE_IN : '',
                ORDERCATEGORY_IN : '',
                MONITORPOINTNO_IN : '',
                SAMPLEBEGINDATE_DGOE : '',
                SAMPLEBEGINDATE_DLOE : '',
                PROJNO_SC : '',
                PROJNAME_SC : '',
                ORDERNO_SC : '',
                ENTRUSTORGNAME_SC : '',
                TESTORGNAME_SC : '',
                ORDERTASKSTATUS_SEQ : 'done',
                REPORTDISPLAY_SEQ : '1'
            }
        });
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'emc-data-query-edit-list-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '28%',
                items : [ _this.getFormParam() ]
            },
            center : {
                items : [ _this.getGridParam() ]
            },
        });
    },

    init : function() {
        this.create();
    }
};
