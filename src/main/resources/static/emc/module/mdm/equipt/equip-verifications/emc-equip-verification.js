var emcEquipVerification = Gikam.getPageObject({

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipVerification.workflow.dbTable), {
            field: 'name',
            title: 'T_EMC_EQUIP_VERIFICATION.NAME',
        }, {
            field: 'number',
            title: 'T_EMC_EQUIP_VERIFICATION.NUMBER'
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_VERIFICATION.MODEL'
        }, {
            field: 'accuracy',
            title: 'T_EMC_EQUIP_VERIFICATION.ACCURACY'
        }, {
            field : 'ext$.cycle',
            title : 'T_EMC_EQUIP_VERIFICATION.CYCLE',
            type : 'select',
            editor: true,
            category : 'sys',
            param : {
                codeCategoryId : 'cycle',
            },
            onChange : function (field, value, index) {
                var data = this.getData()[index];
                if (data.maintDate && value) {
                    var cycleExpArray = ['0-0-1', '0-0-7', '0-1-0', '0-2-0', '0-3-0', '0-4-0', '0-6-0', '1-0-0', '2-0-0', '3-0-0', '5-0-0'];
                    var tempDate = new Date(data.maintDate);
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
                            ext$ : {cycle : value},
                            validity : newEndDate
                        })
                    }
                }
            }
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONDATE',
            type : 'date',
            editor: 'true',
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
                            maintDate : value,
                            validity : newEndDate
                        })
                    }
                }
            },
        }, {
            field: 'validity',
            title: 'T_EMC_EQUIP_VERIFICATION.VALIDITY',
            validators: ['notEmpty'],
            type: 'date'
        }, {
            field: 'unit',
            title: 'T_EMC_EQUIP_VERIFICATION.UNIT',
            validators: ['notEmpty'],
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            }
        }, {
            field: 'ext$.verificationreason',
            title: 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONREASON',
            validators: ['notEmpty'],
            editor: true,
            search: true,
            multiple : true,
            width: 300,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field: 'result',
            title: 'T_EMC_EQUIP_VERIFICATION.RESULT',
            validators: ['notEmpty'],
            editor: true,
            width: 300
        }, {
            field: 'certCategory',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTCATEGORY',
            validators: ['notEmpty'],
            editor : true,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId: 'certificatetype',
            },
        }, {
            field: 'certNo',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTNO',
            validators: ['notEmpty'],
            editor: true,
        }, {
            field : 'confirmerName',
            title : 'T_EMC_EQUIP_VERIFICATION.CONFIRMERNAME',
            editor : true,
            validators : ['notEmpty'],
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-equip-verification-edit-list-grid').getActivedRow().id,
                        confirmerName : rows[0].userName,
                        confirmerId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-equip-verification-edit-list-grid').getActivedRow().id,
                    confirmerName : '',
                    confirmerId : ''
                });
            }
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_VERIFICATION.REMARK',
            validators: ['notEmpty'],
            editor: true,
            width: 300
        }];
    },

    getAuditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipVerification.workflow.dbTable), {
            field: 'name',
            title: 'T_EMC_EQUIP_VERIFICATION.NAME',
        }, {
            field: 'number',
            title: 'T_EMC_EQUIP_VERIFICATION.NUMBER'
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_VERIFICATION.MODEL'
        }, {
            field: 'accuracy',
            title: 'T_EMC_EQUIP_VERIFICATION.ACCURACY'
        }, {
            field : 'ext$.cycle',
            title : 'T_EMC_EQUIP_VERIFICATION.CYCLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle',
            }
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONDATE',
            type : 'date'
        }, {
            field: 'validity',
            title: 'T_EMC_EQUIP_VERIFICATION.VALIDITY',
            validators: ['notEmpty'],
            type: 'date'
        }, {
            field: 'unit',
            title: 'T_EMC_EQUIP_VERIFICATION.UNIT',
            validators: ['notEmpty'],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            }
        }, {
            field: 'ext$.verificationreason',
            title: 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONREASON',
            validators: ['notEmpty'],
            width: 300,
            search: true,
            type: 'select',
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field: 'certCategory',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTCATEGORY',
            validators: ['notEmpty'],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId: 'certificatetype',
            },
        }, {
            field: 'certNo',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTNO',
            validators: ['notEmpty'],
            width: 300,
        }, {
            field: 'result',
            title: 'T_EMC_EQUIP_VERIFICATION.RESULT',
            validators: ['notEmpty'],
            width: 300
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_VERIFICATION.REMARK',
            validators: ['notEmpty'],
            width: 300
        }];
    },

    getSearchListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipVerification.workflow.dbTable), {
            field: 'name',
            title: 'T_EMC_EQUIP_VERIFICATION.NAME',
        }, {
            field: 'number',
            title: 'T_EMC_EQUIP_VERIFICATION.NUMBER'
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_VERIFICATION.MODEL'
        }, {
            field: 'accuracy',
            title: 'T_EMC_EQUIP_VERIFICATION.ACCURACY'
        }, {
            field : 'ext$.cycle',
            title : 'T_EMC_EQUIP_VERIFICATION.CYCLE',
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'cycle',
            }
        }, {
            field : 'maintDate',
            title : 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONDATE',
            type : 'date',
            editor: 'true',
        }, {
            field: 'validity',
            title: 'T_EMC_EQUIP_VERIFICATION.VALIDITY',
            validators: ['notEmpty'],
            type: 'date'
        }, {
            field: 'unit',
            title: 'T_EMC_EQUIP_VERIFICATION.UNIT',
            validators: ['notEmpty'],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'verifyOrg',
            }
        }, {
            field: 'ext$.verificationreason',
            title: 'T_EMC_EQUIP_VERIFICATION.VERIFICATIONREASON',
            validators: ['notEmpty'],
            width: 300,
            type: 'select',
            search: true,
            category: 'sys',
            param: {
                codeCategoryId: 'inspectionStandard'
            }
        }, {
            field: 'certCategory',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTCATEGORY',
            validators: ['notEmpty'],
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId: 'certificatetype',
            },
        }, {
            field: 'certNo',
            title: 'T_EMC_EQUIP_VERIFICATION.CERTNO',
            validators: ['notEmpty'],
        }, {
            field: 'result',
            title: 'T_EMC_EQUIP_VERIFICATION.RESULT',
            validators: ['notEmpty'],
            width: 300
        }, {
            field: 'confirmerName',
            title: 'T_EMC_EQUIP_VERIFICATION.CONFIRMERNAME',
            validators: ['notEmpty'],
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_VERIFICATION.REMARK',
            validators: ['notEmpty'],
            width: 300
        }];
    },

    baseUrl: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equip-verifications',

    workflow: {
        dbTable: 'T_EMC_EQUIP_VERIFICATION',
        columns: [{
            field: 'id',
            title: 'T_EMC_EQUIP_VERIFICATION.ID'
        }]
    }

});
