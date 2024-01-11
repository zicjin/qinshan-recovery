var emcEquiptUseRecord = Gikam.getPageObject({
    getUseRecordListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_EQUIPT_USE_RECORD.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_EQUIPT_USE_RECORD.SAMPLENO'
        }, {
            field : 'runId',
            title : 'T_EMC_EQUIPT_USE_RECORD.RUNID'
        }, {
            field : 'testId',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTID'
        }, {
            field : 'testName',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTNAME'
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_USE_RECORD.EQUIPTID'
        }, {
            field : 'beforeUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.BEFOREUSESTATUS'
        }, {
            field : 'startTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.STARTTIME'
        }, {
            field : 'afterUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.AFTERUSESTATUS'
        }, {
            field : 'endTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.ENDTIME'
        }, {
            field : 'userName',
            title : 'T_EMC_EQUIPT_USE_RECORD.USERNAME'
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_EQUIPT_USE_RECORD.PROJNAME'
        }, {
            field : 'ext$.testorgname',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTORGNAME'
        }, ];
    },

    getUseRecordEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_EQUIPT_USE_RECORD.PROJNO'
        }, {
            field : 'sampleNo',
            title : 'T_EMC_EQUIPT_USE_RECORD.SAMPLENO'
        }, {
            field : 'testId',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTID'
        }, {
            field : 'testName',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTNAME'
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_USE_RECORD.EQUIPTID'
        }, {
            field : 'usedTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.USEDTIME',
            type : 'dateTime',
            editor : true
        }, {
            field : 'beforeUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.BEFOREUSESTATUS',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'afterUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.AFTERUSESTATUS',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'userName',
            title : 'T_EMC_EQUIPT_USE_RECORD.USERID',
            editor : true,
            single : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        userId : rows[0].id,
                        userName : rows[0].userName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    userId : '',
                    userName : '',
                });
            }
        } ];
    },

    getUseRecordEditListColumns2 : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.equiptno',
            title : 'T_EMC_EQUIPT_USE_RECORD.EQUIPTID'
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_EQUIPT.EQUIPTNAME'
        }, {
            field : 'testName',
            title : 'T_EMC_EQUIPT_USE_RECORD.TESTNAME'
        }, {
            field : 'equiptCondition',
            title : 'T_EMC_EQUIPT_USE_RECORD.EQUIPTCONDITION',
            editor : true
        }, {
            field : 'orderQty',
            title : 'T_EMC_EQUIPT_USE_RECORD.ORDERQTY',
            editor : true
        }, {
            field : 'ext$.temperature',
            title : 'T_EMC_EQUIPT_USE_RECORD.TEMPERATURE',
            width : 100,
            editor : true
        }, {
            field : 'startTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.STARTTIME',
            type : 'dateTime',
            editor : true,
            onChange : function(field, value, rowIndex, oldValue) {
                var data = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getData();
                if (!Gikam.isEmpty(data)) {
                    Gikam.post(emcEquipt.baseUrl + "/use-records/queries", Gikam.getJsonWrapper({
                            f : {
                                equiptno_SEQ : data[rowIndex].ext$.equiptno,
                                startTime_TL : data[rowIndex].startTime,
                                endTime_TGOE : data[rowIndex].startTime
                            },
                            n : -1
                        })).done(function (str) {
                            if (str.total>0) {
                                Gikam.alert('EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.DETE.SELECT');
                            }
                    });
                }
            }
        }, {
            field : 'beforeUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.BEFOREUSESTATUS',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'endTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.ENDTIME',
            type : 'dateTime',
            editor : true,
            onChange: function (field, value, rowIndex, oldValue) {
                var data = Gikam.getComp('emc-equipt-use-record-edit-list-grid').getData();
                if (!Gikam.isEmpty(data)) {
                    Gikam.post(emcEquipt.baseUrl + "/use-records/queries", Gikam.getJsonWrapper({
                        f: {
                            equiptno_SEQ: data[rowIndex].ext$.equiptno,
                            startTime_TLOE: data[rowIndex].endTime,
                            endTime_TG: data[rowIndex].endTime
                        },
                        n: -1
                    })).done(function (str) {
                        if (str.total > 0) {
                            Gikam.alert('EMC.MODULE.MDM.EQUIPT.EQUIPTS.TIP.DETE.SELECT');
                        }
                    });
                }
            }
        }, {
            field : 'afterUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.AFTERUSESTATUS',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'userName',
            title : 'T_EMC_EQUIPT_USE_RECORD.USERID',
            editor : true,
            single : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        userId : rows[0].id,
                        userName : rows[0].userName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    userId : '',
                    userName : '',
                });
            }
        } ];
    },

    getBatchUpdateRecordFields : function() {
        return [ {
            field : 'equiptCondition',
            title : 'T_EMC_EQUIPT_USE_RECORD.EQUIPTCONDITION',
        }, {
            field : 'orderQty',
            title : 'T_EMC_EQUIPT_USE_RECORD.ORDERQTY',
        }, {
            field : 'startTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.STARTTIME',
            type : 'dateTime'
        }, {
            field : 'endTime',
            title : 'T_EMC_EQUIPT_USE_RECORD.ENDTIME',
            type : 'dateTime',
        }, {
            field : 'beforeUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.BEFOREUSESTATUS',
            validators : [ 'strLength[0,12]' ],
        }, {
            field : 'afterUseStatus',
            title : 'T_EMC_EQUIPT_USE_RECORD.AFTERUSESTATUS',
            validators : [ 'strLength[0,12]' ],
        }, {
            field : 'userName',
            title : 'T_EMC_EQUIPT_USE_RECORD.USERID',
            single : true,
            type : 'choose',
            category : 'user-simple',
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        userId : rows[0].id,
                        userName : rows[0].userName,
                    });
                }
            },
            onClean : function(field, value, index) {
                this.setData({
                    index : index,
                    userId : '',
                    userName : '',
                });
            }
        } ];
    }

})
