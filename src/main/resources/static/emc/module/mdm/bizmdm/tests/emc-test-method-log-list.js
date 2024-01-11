emcTestMethod.logPage = {
    
    getGridParam : function() {
        var _this = this;
        return {
            type: 'grid',
            id : 'emc-test-method-edit-list-history-grid',
            url : emcTestMethod.baseUrl + '/log/queries',
            columns : [ {
               field: 'versions',
               title: 'T_EMC_TEST_METHOD_LOG.VERSIONS',
           } ],
            requestDate : {
                testMethodId_EQ : _this.param.id
            },
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-test-method-edit-list-history-record-grid' ]);
                }
            },
            order : {
                'id' : 'asc',
            },
            onRowActive : function(index, row) {
                var first = row.createdTime
                var second = '';
                var firDate = new Date(row.createdTime);
                var secDate = new Date();
                var data = Gikam.getComp('emc-test-method-edit-list-history-grid').getData();
                if(index < data.length - 1) {
                    second = data[index + 1].createdTime;
                    var secDate = new Date(data[index + 1].createdTime);
                    var tempDate = '';
                    if(secDate < firDate) {
                        tempDate = first;
                        first = second;
                        second = tempDate;
                    }
                  
                }
                Gikam.getAsyncComp('emc-test-method-edit-list-history-record-grid').done(function(grid) {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/core/module/sys/at-records/queries',
                        requestData : {
                            createdTime_TGOE : first,
                            createdTime_TLOE : second,
                            targetId_SEQ : _this.param.tableName + '$' + _this.param.id
                        }
                    });
                });
            },
        }
    },
    
    getRecordGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-method-edit-list-history-record-grid',
            columns : [
                {
                    title: 'T_CORE_AT_RECORD.CREATEDBYNAME',
                    field: 'createdByName'
                },
                {
                    title: 'T_CORE_AT_RECORD.CREATEDTIME',
                    field: 'createdTime'
                },
                {
                    title: 'T_CORE_AT_RECORD.AUDITTRAILNAMENEW',
                    field: 'auditTrailNameNew'
                }
            ],
            onLoadSuccess : function(data) {
                if (!Gikam.isEmpty(data)) {
                    this.activeRowByIndex(0);
                } else {
                    Gikam.cleanCompData([ 'emc-test-method-edit-list-history-record-line-grid' ]);
                }
            },
            onRowActive : function(index, row) {
                Gikam.getAsyncComp('emc-test-method-edit-list-history-record-line-grid').done(function(grid) {
                    grid.refresh({
                        url : IFM_CONTEXT + '/secure/core/module/sys/at-records/record-lines/queries',
                        requestData : {
                            recordId_SEQ : row.id
                        }
                    });
                });
            },
            onBeforeLoad : function(data) {
                data.forEach(item => {
                    item.auditTrailNameNew = Gikam.propI18N(item.auditTrailName);
                });
            }
        };
    },
    
    getRecordLineGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-test-method-edit-list-history-record-line-grid',
            columns : [
                {
                    title : 'T_CORE_AT_RECORD_LINE.COLUMNNAME',
                    field : 'columnNew'
                }, {
                    title : 'T_CORE_AT_RECORD_LINE.OLDVALUE',
                    field : 'oldValue'
                }, {
                    title : 'T_CORE_AT_RECORD_LINE.NEWVALUE',
                    field : 'newValue'
                }
            ],
            onBeforeLoad : function(data) {
                data.forEach(item => {
                    item.columnNew = Gikam.propI18N(item.tableName + '.' + item.column.toUpperCase());
                });
            }
        };
    },
    

    create : function() {
        var _this = this;
        
        Gikam.create('layout', {
            west: {
                width : '15%',
                items: [ _this.getGridParam() ]
            },
            center : {
                items : [ _this.getRecordGridParam() ]
            },
            east : {
                width : '40%',
                items : [ _this.getRecordLineGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
    },
    
    init : function(param) {
        this.param = param;
        this.create();
    }
};