var emcEquiptPerCheckRecord = Gikam.getPageObject({

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
            validators : [ 'notEmpty' ]
        }, {
            field : 'cycle',
            title : 'T_EMC_EQUIPT_PER_CHECK.CYCLE',
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
                field : 'maintDate',
                title : 'T_EMC_EQUIPT_PER_CHECK.MAINTDATE',
                type : 'date',
                editor: true,
                onBeforeEditorRender : function(row) {
                    if (row.completeFlag == '2') {
                        return false;
                    }
                    return true;
                },
                validators : [ 'notEmpty' ]

            }, {
            field : 'checkReason',
            title : 'T_EMC_EQUIPT_PER_CHECK.CHECKREASON',
            editor : true,
            type: 'select',
            search: true,
            category: 'sys',
            multiple : true,
            param: {
                codeCategoryId: 'inspectionStandard'
            },
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            }
        }, {
            field : 'checkEquipt',
            title : 'T_EMC_EQUIPT_PER_CHECK.EQUIPTMENT',
            editor : true,
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
        }, {
            field : 'remark',
            title : 'T_EMC_EQUIPT_PER_CHECK.REMARK',
            editor : true,
            validators : [ 'strLength[0, 540]' ],
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
        } , {
            field: 'checkUsernames',
            title: 'T_EMC_EQUIPT_PER_CHECK.CHECKUSERNAMES',
            editor: true,
            type: 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var sampleUserIds = [];
                    var sampleUserNames = [];
                    Gikam.each(rows, function (index, item) {
                        sampleUserIds.push(item.id);
                        sampleUserNames.push(item.userName);
                    });
                    this.setData({
                        index: index,
                        checkUsers: sampleUserIds.join(','),
                        checkUsernames: sampleUserNames.join(','),
                    });
                }},
            onBeforeEditorRender : function(row) {
                if (row.completeFlag == '2') {
                    return false;
                }
                return true;
            },
        }, {
            field : 'createdByName',
            title : 'T_EMC_EQUIPT_PER_CHECK.RECORDER',
        }, {
            field : 'createdTime',
            title : 'T_EMC_EQUIPT_PER_CHECK.RDATE',
        }];
    },

    getPerCheckRecordTab : function() {
        return {
            title : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.TAB.PER_CHECK_RECORD',
            items : [ {
                type : 'grid',
                id : 'emc-equipt-per-check-record-edit-list-grid',
                service : 'emcEquiptPerCheckRecordServiceImpl',
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
                            url : emcEquipt.baseUrl + '/page/per-check-record-add?equiptId=' + Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                            onAfterClose : function(data) {
                                if (Gikam.isEmpty(data)) {
                                    return;
                                }
                                Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').insert(emcEquipt.baseUrl + "/per-check-record", [ {
                                    equiptId : Gikam.getComp('emc-equipt-edit-list-grid').getActivedRow().id,
                                    maintId : data.maintId
                                } ]);

                            }
                        })
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.DELETE',
                    icon : 'remove',
                    onClick : function() {
                        Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').deleteRows(emcEquipt.baseUrl + '/per-check-record');
                    }
                }, {
                    type : 'button',
                    text : 'EMC.MODULE.MDM.EQUIPT.EQUIPTS.COMPLETE',
                    icon : 'edit',
                    onClick : function() {

                        data = Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').getSelections();

                        if(!Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').validate(data)){
                            return;
                        }
                        for ( var index in data) {
                            if (data[index].completeFlag == "2") {
                                Gikam.alert('EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.RECORD_COMP');
                                return;
                            }
                        }
                        workspace.window.showMask(true);
                        Gikam.put(emcEquipt.baseUrl + "/per-check-records/action/complete", Gikam.getJsonWrapper({}, [ '', data ])).done(function(data) {
                            Gikam.getComp('emc-equipt-per-check-plan-edit-list-grid').refresh();
                            Gikam.getComp('emc-equipt-per-check-record-edit-list-grid').refresh();
                        }).always(function() {
                            workspace.window.closeMask();
                        });
                    }
                } ],
                columns : emcEquiptPerCheckRecord.getEditListColumns()
            } ]
        }

    }

});
