var emcInvoice = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            radio : true
        }, Gikam.status.getBpmnColumn(emcInvoice.workflow.dbTable), {
            field : 'sysNo',
            title : 'T_EMC_INVOICE.SYSNO',
            styleFormatter : function(row) {
                if (row.doneFlag === '1') {
                    return {
                        color : 'green'
                    }
                }
            },
        }, {
            field : 'invoiceName',
            title : 'T_EMC_INVOICE.INVOICENAME',
            editor : true,
            validators : [ 'notEmpty' ]
        },{
            field : 'ext$.invoiceprojname',
            title : 'T_EMC_INVOICE.INVOICEPROJNAME',
            editor : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'invoiceNo',
            title : 'T_EMC_INVOICE.INVOICENO',
            editor : true
        }, {
            field : 'invoiceCategory',
            title : 'T_EMC_INVOICE.INVOICECATEGORY',
            editor : true,
            type : 'select',
            category: 'sys',
            param : {
                codeCategoryId : 'invoiceCategory'
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'makeDate',
            title : 'T_EMC_INVOICE.MAKEDATE',
            type : 'date',
            editor : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'drawer',
            title : 'T_EMC_INVOICE.DRAWER',
            editor : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_INVOICE.INVOICEAMOUNT',
            editor : true,
        }, {
            field : 'ext$.uninvoicedamount',
            title : 'T_EMC_INVOICE.UNINVOICEAMOUNT',
            type : 'number'
        }, {
            field : 'contractName',
            title : 'T_EMC_INVOICE.CONTRACTNAME',
            editor : true,
            preInsert : true,
            type : 'choose',
            single : true,
            category : 'contract',
            onBeforeChoose : function (){
                var date = new Date();
                date.setFullYear(date.getFullYear()-3);
                return {
                    createdTime_DGOE : Gikam.DateUtils.formatter(date, 'yyyy-MM-dd')
               }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var contractNames = rows.map(function (item) {
                        return item.contractName;
                    }).join(',');
                    var contractIds = rows.map(function (item) {
                        return item.id
                    }).join(',');
                    this.setData({
                        index : index,
                        contractName : contractNames,
                        contractId : contractIds,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    contractName : '',
                    contractId : '',
                });
            },
            validators : [ 'notEmpty' ]
        }, {
            field : 'projectName',
            title : 'T_EMC_INVOICE.PROJECTNAME',
            editor : true,
            type : 'choose',
            category: 'project',
            onBeforeChoose : function (){
                var date = new Date();
                date.setFullYear(date.getFullYear()-3);
                return {
                    createdTime_DGOE : Gikam.DateUtils.formatter(date, 'yyyy-MM-dd')
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var projectNames = rows.map(function (item) {
                        return item.projName;
                    }).join(',');
                    var projectIds = rows.map(function (item) {
                        return item.id
                    }).join(',');
                    this.setData({
                        index : index,
                        projectName : projectNames,
                        projectId : projectIds,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projectName : '',
                    projectId : '',
                });
            },
        }, {
            field : 'createdByName',
            title : 'T_EMC_INVOICE.CREATEDBYNAME',
        },{
            field : 'createdTime',
            title : 'T_EMC_INVOICE.CREATEDTIME',
        },{
            field : 'createdByOrgName',
            title : 'T_EMC_INVOICE.CREATEDBYORGNAME',
        },{
            field : 'remark',
            title : 'T_EMC_INVOICE.REMARK',
            editor : true
        },{
            field : 'auditUser',
            title : 'T_EMC_INVOICE.AUDITUSER',
            type: 'choose',
            category : 'emp',
            single : true,
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        index : index,
                        auditUser : rows[0].userName,
                        auditUserId : rows[0].id,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    auditUser : '',
                    auditUserId : '',
                });
            },
            editor : true,
            validators : [ 'notEmpty' ]
        }];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'sysNo',
            title : 'T_EMC_INVOICE.SYSNO',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'invoiceName',
            title : 'T_EMC_INVOICE.INVOICENAME',
            validators : [ 'strLength[0,100]' ]
        }, {
            field : 'invoiceNo',
            title : 'T_EMC_INVOICE.INVOICENO',
            validators : [ 'strLength[0,100]' ]
        }, {
            field : 'invoiceCategory',
            title : 'T_EMC_INVOICE.INVOICECATEGORY',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'makeDate',
            title : 'T_EMC_INVOICE.MAKEDATE'
        }, {
            field : 'drawerId',
            title : 'T_EMC_INVOICE.DRAWERID',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'drawer',
            title : 'T_EMC_INVOICE.DRAWER',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_INVOICE.INVOICEAMOUNT',
            type : 'number',
            preInsert : true
        }, {
            field : 'contractId',
            title : 'T_EMC_INVOICE.CONTRACTID'
        }, {
            field : 'contractName',
            title : 'T_EMC_INVOICE.CONTRACTNAME',
            validators : [ 'strLength[0,30]' ]
        }, {
            field : 'projectId',
            title : 'T_EMC_INVOICE.PROJECTID'
        }, {
            field : 'projectName',
            title : 'T_EMC_INVOICE.PROJECTNAME',
            validators : [ 'strLength[0,100]' ]
        }, {
            field : 'remark',
            title : 'T_EMC_INVOICE.REMARK',
            validators : [ 'strLength[0,100]' ]
        }, ];
    },

    getListPreInsertColumns : function (){
        return [{
            field : 'returnedAmount',
            title : 'T_EMC_RETURNED.RETURNEDAMOUNT',
            preInsert : true,
            type : 'number',
            editor : false
        }]
    },

    getListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_RETURNED.INVOICEAMOUNT',
        }, {
            field : 'returnedAmount',
            title : 'T_EMC_RETURNED.RETURNEDAMOUNT',
            type : 'number',
            editor : true,
            onChange : function(field, value, index, oldValue, text) {
                var row = this.getData()[index];
                var data = this.getData();
                var returnedAmount = 0;
                data.map(function (item) {
                    returnedAmount = (returnedAmount - 0) + (item.returnedAmount - 0);
                })
                var result = row.invoiceAmount - 0 - returnedAmount;
                if (result < 0){
                    Gikam.alert('EMC.MODULE.MDM.INVOICES.TIP.OVER')
                    this.setData({
                        index : index,
                        returnedAmount : oldValue
                    })
                } else {
                    this.setData({
                        index : index,
                        returnedAmount : value,
                        uncollectedAmount : result
                    })
                }
            }
        }, {
            field : 'uncollectedAmount',
            title : 'T_EMC_RETURNED.UNCOLLECTEDAMOUNT'
        }, {
            field : 'makeUser',
            title : 'T_EMC_RETURNED.MAKEUSER',
            editor : true
        }, {
            field : 'makeTime',
            title : 'T_EMC_RETURNED.MAKETIME',
            type : 'date',
            editor : true
        },{
            field : 'remark',
            title : 'T_EMC_RETURNED.REMARK',
            editor : true
        },{
            field : 'createdTime',
            title : 'T_EMC_RETURNED.CREATEDTIME',
        },];
    },

    getSearchInvoiceListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_RETURNED.INVOICEAMOUNT',
        }, {
            field : 'returnedAmount',
            title : 'T_EMC_RETURNED.RETURNEDAMOUNT',
            type : 'number'
        }, {
            field : 'uncollectedAmount',
            title : 'T_EMC_RETURNED.UNCOLLECTEDAMOUNT'
        }, {
            field : 'makeUser',
            title : 'T_EMC_RETURNED.MAKEUSER',
        }, {
            field : 'makeTime',
            title : 'T_EMC_RETURNED.MAKETIME',
            type : 'date',
        },{
            field : 'remark',
            title : 'T_EMC_RETURNED.REMARK',
        },{
            field : 'createdTime',
            title : 'T_EMC_RETURNED.CREATEDTIME',
        },];
    },

    getAuditListColumns : function() {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInvoice.workflow.dbTable), {
            field : 'sysNo',
            title : 'T_EMC_INVOICE.SYSNO'
        }, {
            field : 'invoiceName',
            title : 'T_EMC_INVOICE.INVOICENAME'
        }, {
            field : 'ext$.invoiceprojname',
            title : 'T_EMC_INVOICE.INVOICEPROJNAME'
        },{
            field : 'invoiceNo',
            title : 'T_EMC_INVOICE.INVOICENO',
            editor : true,
            validators : [ 'notEmpty' ]
        }, {
            field : 'invoiceCategory',
            title : 'T_EMC_INVOICE.INVOICECATEGORY',
            type : 'select',
            category: 'sys',
            param : {
                codeCategoryId : 'invoiceCategory'
            },
        }, {
            field : 'makeDate',
            title : 'T_EMC_INVOICE.MAKEDATE'
        }, {
            field : 'drawer',
            title : 'T_EMC_INVOICE.DRAWER'
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_INVOICE.INVOICEAMOUNT'
        }, {
            field : 'ext$.uninvoicedamount',
            title : 'T_EMC_INVOICE.UNINVOICEAMOUNT',
            type : 'number'
        },{
            field : 'contractName',
            title : 'T_EMC_INVOICE.CONTRACTNAME'
        }, {
            field : 'projectName',
            title : 'T_EMC_INVOICE.PROJECTNAME'
        },{
            field : 'createdByName',
            title : 'T_EMC_INVOICE.CREATEDBYNAME',
        },{
            field : 'createdTime',
            title : 'T_EMC_INVOICE.CREATEDTIME',
        },{
            field : 'createdByOrgName',
            title : 'T_EMC_INVOICE.CREATEDBYORGNAME',
        }, {
            field : 'remark',
            title : 'T_EMC_INVOICE.REMARK'
        } ];
    },

    getReturnedAuditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_RETURNED.INVOICEAMOUNT',
        }, {
            field : 'returnedAmount',
            title : 'T_EMC_RETURNED.RETURNEDAMOUNT',
            type : 'number'
        }, {
            field : 'uncollectedAmount',
            title : 'T_EMC_RETURNED.UNCOLLECTEDAMOUNT'
        }, {
            field : 'makeUser',
            title : 'T_EMC_RETURNED.MAKEUSER'
        }, {
            field : 'makeTime',
            title : 'T_EMC_RETURNED.MAKETIME',
            type : 'date'
        },{
            field : 'remark',
            title : 'T_EMC_RETURNED.REMARK'
        },{
            field : 'createdTime',
            title : 'T_EMC_RETURNED.CREATEDTIME',
        },];
    },

    getSearchListColumns : function(personal) {
        return [ {
            checkbox : true
        }, Gikam.status.getBpmnColumn(emcInvoice.workflow.dbTable), {
            field : 'sysNo',
            title : 'T_EMC_INVOICE.SYSNO'
        }, {
            field : 'invoiceName',
            title : 'T_EMC_INVOICE.INVOICENAME'
        }, {
            field : 'ext$.invoiceprojname',
            title : 'T_EMC_INVOICE.INVOICEPROJNAME',
            validators : [ 'notEmpty' ]
        },{
            field : 'invoiceNo',
            title : 'T_EMC_INVOICE.INVOICENO'
        }, {
            field : 'invoiceCategory',
            title : 'T_EMC_INVOICE.INVOICECATEGORY'
        }, {
            field : 'makeDate',
            title : 'T_EMC_INVOICE.MAKEDATE'
        }, {
            field : 'drawer',
            title : 'T_EMC_INVOICE.DRAWER'
        }, {
            field : 'invoiceAmount',
            title : 'T_EMC_INVOICE.INVOICEAMOUNT'
        }, {
            field : 'ext$.uninvoicedamount',
            title : 'T_EMC_INVOICE.UNINVOICEAMOUNT',
            type : 'number'
        }, {
            field : 'contractName',
            title : 'T_EMC_INVOICE.CONTRACTNAME'
        },  {
            field : 'projectName',
            title : 'T_EMC_INVOICE.PROJECTNAME',
            editor : personal == 1,
            type : 'choose',
            category: 'project',
            onBeforeChoose : function (){
                var date = new Date();
                date.setFullYear(date.getFullYear()-3);
                return {
                    createdTime_DGOE : Gikam.DateUtils.formatter(date, 'yyyy-MM-dd')
                }
            },
            onAfterChoose : function(rows, index) {
                if (!Gikam.isEmpty(rows)) {
                    var projectNames = rows.map(function (item) {
                        return item.projName;
                    }).join(',');
                    var projectIds = rows.map(function (item) {
                        return item.id
                    }).join(',');
                    this.setData({
                        index : index,
                        projectName : projectNames,
                        projectId : projectIds,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    projectName : '',
                    projectId : '',
                });
            },
        }, {
            field : 'createdByName',
            title : 'T_EMC_INVOICE.CREATEDBYNAME',
        },{
            field : 'createdTime',
            title : 'T_EMC_INVOICE.CREATEDTIME',
        },{
            field : 'createdByOrgName',
            title : 'T_EMC_INVOICE.CREATEDBYORGNAME',
        },{
            field : 'remark',
            title : 'T_EMC_INVOICE.REMARK'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/invoices',

    workflow : {
        dbTable : 'T_EMC_INVOICE',
        columns : [ {
            field : 'id',
            title : 'T_EMC_INVOICE.ID'
        } ]
    }

});
