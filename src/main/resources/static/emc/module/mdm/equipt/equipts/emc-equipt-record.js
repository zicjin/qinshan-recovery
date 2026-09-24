var emcEquiptRecord = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'completeFlag',
            title : 'T_EMC_EQUIPT_RECORD.COMPLETEFLAG',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'completeFlag',
            },
            width : 90
        }, {
            field : 'ext$.codename',
            title : 'T_EMC_EQUIPT_PLAN.MAINTEVENT',
            width : 100,
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
            validators : [ 'notEmpty' ],
        },
        {
            field : 'verifyOrg',
            title : 'T_EMC_EQUIPT_RECORD.VERIFYORG',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            },
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
            validators : [ 'notEmpty' ],

        }, {
            field : 'ext$.cycle',
            title : 'T_EMC_EQUIPT_RECORD.CYCLE',
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle'
            },
            onChange : function (field, value, index) {
                var data = this.getData()[index];
                if (data.mainDate && value) {
                    var cycleExpArray = ['0-0-1', '0-0-7', '0-1-0', '0-2-0', '0-3-0', '0-4-0', '0-6-0', '1-0-0', '2-0-0', '3-0-0', '5-0-0'];
                    var tempDate = new Date(data.mainDate);
                    var cycleExp = cycleExpArray[parseInt(value) - 1]
                    if (cycleExp) {
                        var cycleValues = cycleExp.split('-');
                        var cycleYear = cycleValues[0];
                        var cycleMonth = cycleValues[1];
                        var cycleDay = cycleValues[2];
                        tempDate.setFullYear(tempDate.getFullYear() + parseInt(cycleYear));
                        tempDate.setMonth(tempDate.getMonth() + parseInt(cycleMonth));
                        tempDate.setDate(tempDate.getDate() + parseInt(cycleDay));
                        tempDate.setDate(tempDate.getDate()-1);
                        var newEndDate = Gikam.DateUtils.formatter(tempDate, 'yyyy-MM-dd');
                        this.setData({
                            index : index,
                            id : data.id,
                            ext$ : {
                                cycle : value
                            },
                            maintEndDate : newEndDate

                        })
                    }
                }
            },
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
            width : 100,
            validators : [ 'notEmpty' ],
        }, {
           field : 'mainDate',
           title : 'T_EMC_EQUIPT_RECORD.CREATEDTIME',
           type : 'date',
           editor: true,
           onBeforeEditorRender : function(row) {
               if (row.completeFlag == '2') {
                   return false;
               }
               return true;
           },
           validators : [ 'notEmpty' ],
           onChange : function (field, value, index) {
               var data = this.getData()[index];
               if (data.ext$.cycle && value) {
                   var cycleExpArray = ['0-0-1', '0-0-7', '0-1-0', '0-2-0', '0-3-0', '0-4-0', '0-6-0', '1-0-0', '2-0-0', '3-0-0', '5-0-0'];
                   var tempDate = new Date(value);
                   var cycleExp = cycleExpArray[parseInt(data.ext$.cycle) - 1]
                   if (cycleExp) {
                       var cycleValues = cycleExp.split('-');
                       var cycleYear = cycleValues[0];
                       var cycleMonth = cycleValues[1];
                       var cycleDay = cycleValues[2];
                       tempDate.setFullYear(tempDate.getFullYear() + parseInt(cycleYear));
                       tempDate.setMonth(tempDate.getMonth() + parseInt(cycleMonth));
                       tempDate.setDate(tempDate.getDate() + parseInt(cycleDay));
                       tempDate.setDate(tempDate.getDate()-1);
                       var newEndDate = Gikam.DateUtils.formatter(tempDate, 'yyyy-MM-dd');
                       this.setData({
                           index : index,
                           id : data.id,
                           mainDate : value,
                           maintEndDate : newEndDate
                       })
                   }
               }
           },
        }, {
           field : 'maintEndDate',
           title : 'T_EMC_EQUIPT_RECORD.MAINTENDDATE',
           onBeforeEditorRender : function(row) {
               if (row.completeFlag == '2') {
                   return false;
               }
               return true;
           },
           validators : [ 'notEmpty' ],
        },{
           field : 'ext$.gist',
           title : 'T_EMC_EQUIPT_RECORD.GIST',
           editor: true,
           multiple : true,
           search: true,
           type: 'select',
           category: 'sys',
           param: {
               codeCategoryId: 'inspectionStandard'
           },
           onBeforeEditorRender : function(row) {
               if (row.completeFlag == '2') {
                   return false;
               }
               return true;
           },
           // validators : [ 'notEmpty' ],
        },{
           field : 'certCategory',
           title : 'T_EMC_EQUIPT_RECORD.CERTCATEGORY',
           editor : true,
           onBeforeEditorRender : function(row) {
               if (row.completeFlag == '2') {
                   return false;
               }
               return true;
           },
           type : 'select',
           search : true,
           category : 'sys',
           param : {
               codeCategoryId : 'certificatetype'
           },
           validators : [ 'notEmpty' ],
        },{
           field : 'remark',
           title : 'T_EMC_EQUIPT_RECORD.REMARK',
           editor : true,
           onBeforeEditorRender : function(row) {
               if (row.completeFlag == '2') {
                   return false;
               }
               return true;
           },
           validators : [ 'strLength[0, 540]' ],
        },{
            field : 'certNo',
            title : 'T_EMC_EQUIPT_RECORD.CERTNO',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
            validators : ['strLength[0, 36]' ],
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_RECORD.CREATEDBYNAME',
       },{
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_RECORD.DATE'
        }];
    },

    getMaintRecordTab : function() {
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.RECORD',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-record-edit-list-grid',
                service : 'emcEquiptRecordServiceImpl',
                page : true,
                deleteFormatter : function(row) {
                    return row.ext$.codename || row.id;
                },
                toolbar : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.INSERT',
                    icon : 'add',
                    onClick : function() {
                        Gikam.create('modal', {
                            width : 300,
                            height : 250,
                            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.MODAL.RECORD_INSERT',
                            url : emcEquipt.baseUrl + '/page/record-add?equiptId=' + Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                            onAfterClose : function(data) {
                                if (Gikam.isEmpty(data)) {
                                    return;
                                }
                                Gikam.getComp('emc-equipt-record-edit-list-grid').insert(emcEquipt.baseUrl + "/records", [ {
                                    equiptId : Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                                    maintId : data.maintId,
                                    verifyOrg : data.verifyOrg
                                } ]);

                            }
                        })
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-record-edit-list-grid').deleteRows(emcEquipt.baseUrl + '/records');
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.COMPLETE',
                    icon : 'edit',
                    onClick : function() {

                        data = Gikam.getComp('emc-equipt-record-edit-list-grid').getSelections();

                        if(!Gikam.getComp('emc-equipt-record-edit-list-grid').validate(data)){
                            return;
                        }
                        for ( var index in data) {
                            if (data[index].completeFlag == "2") {
                                Gikam.alert('EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.RECORD_COMP');
                                return;
                            }

                        }

                        workspace.window.showMask(true);
                        Gikam.put(emcEquipt.baseUrl + "/records/action/complete", Gikam.getJsonWrapper({}, [ '', data ])).done(function(data) {
                            Gikam.getComp('emc-equipt-record-edit-list-grid').refresh();
                            Gikam.getComp('emc-equipt-plan-edit-list--grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                } ],
                columns : emcEquiptRecord.getEditListColumns()
            } ]
        }

    }

});
