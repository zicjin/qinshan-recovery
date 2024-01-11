var emcEquipCheck = Gikam.getPageObject({

    getEditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipCheck.workflow.dbTable), {
            field: 'contractNumber',
            title: 'T_EMC_EQUIP_CHECK.CONTRACTNUMBER',
            validators: ['strLength[0,12]'],
            preInsert: true
        }, {
            field: 'manufacturer',
            title: 'T_EMC_EQUIP_CHECK.MANUFACTURER',
            validators: ['strLength[0,12]'],
            preInsert: true
        }, {
            field: 'equiptName',
            title: 'T_EMC_EQUIP_CHECK.EQUIPTNAME',
            validators: ['strLength[0,12]'],
            preInsert: true
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_CHECK.MODEL',
            validators: ['strLength[0,12]'],
            preInsert: true
        }, {
            field: 'createdByName',
            title: 'T_EMC_EQUIP_CHECK.CREATEDBYNAME'
        }, {
            field: 'createdTime',
            title: 'T_EMC_EQUIP_CHECK.CREATEDTIME'
        }, {
            field: 'auditorName',
            title: 'T_EMC_EQUIP_CHECK.AUDITORID',
            editor : true,
            validators : ['notEmpty'],
            category : 'emp',
            type : 'choose',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        auditorName : rows[0].userName,
                        auditorId : rows[0].id
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    auditorName : '',
                    auditorId : ''
                });
            }
        }, {
            field: 'arrivalDate',
            title: 'T_EMC_EQUIP_CHECK.ARRIVALDATE',
            validators: ['notEmpty'],
            type: 'date',
            editor: true
        }, {
            field: 'factoryNumber',
            title: 'T_EMC_EQUIP_CHECK.FACTORYNUMBER',
            validators: ['notEmpty'],
            editor: true
        }, {
            field: 'appearanceInspection',
            title: 'T_EMC_EQUIP_CHECK.APPEARANCEINSPECTION',
            validators: ['notEmpty'],
            type: 'choose',
            single: false,
            editor: true,
            category: 'equiptAppearance',
            onAfterChoose: function (rows, index) {
                var appearanceActiveRow = Gikam.getComp('emc-equip-check-edit-list-grid').getActivedRow();
                if (!Gikam.isEmpty(rows)) {
                    var appearance = [];
                    Gikam.each(rows, function (index, item) {
                        appearance.push(item.description);
                    });
                    this.setData({
                        id: appearanceActiveRow.id,
                        appearanceInspection: appearance.join(';')
                    });
                }
            },
            onClean: function () {
                var appearanceActiveRow = Gikam.getComp('emc-equip-check-edit-list-grid').getActivedRow();
                this.setData({
                    id: appearanceActiveRow.id,
                    appearanceInspection: ''
                });
            }
        }, {
            field: 'runningCondition',
            title: 'T_EMC_EQUIP_CHECK.RUNNINGCONDITION',
            validators: ['notEmpty'],
            editor: true,
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_CHECK.REMARK',
            validators: ['notEmpty'],
            editor: true,
        }];
    },

    getAuditListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipCheck.workflow.dbTable), {
            field: 'contractNumber',
            title: 'T_EMC_EQUIP_CHECK.CONTRACTNUMBER'
        }, {
            field: 'manufacturer',
            title: 'T_EMC_EQUIP_CHECK.MANUFACTURER'
        }, {
            field: 'equiptName',
            title: 'T_EMC_EQUIP_CHECK.EQUIPTNAME'
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_CHECK.MODEL'
        }, {
            field: 'createdByName',
            title: 'T_EMC_EQUIP_CHECK.CREATEDBYNAME'
        }, {
            field: 'createdTime',
            title: 'T_EMC_EQUIP_CHECK.CREATEDTIME'
        }, {
            field: 'auditorName',
            title: 'T_EMC_EQUIP_CHECK.AUDITORID'
        }, {
            field: 'arrivalDate',
            title: 'T_EMC_EQUIP_CHECK.ARRIVALDATE'
        }, {
            field: 'factoryNumber',
            title: 'T_EMC_EQUIP_CHECK.FACTORYNUMBER'
        }, {
            field: 'appearanceInspection',
            title: 'T_EMC_EQUIP_CHECK.APPEARANCEINSPECTION'
        }, {
            field: 'runningCondition',
            title: 'T_EMC_EQUIP_CHECK.RUNNINGCONDITION'
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_CHECK.REMARK'
        }];
    },

    getSearchListColumns: function () {
        return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcEquipCheck.workflow.dbTable), {
            field: 'contractNumber',
            title: 'T_EMC_EQUIP_CHECK.CONTRACTNUMBER'
        }, {
            field: 'manufacturer',
            title: 'T_EMC_EQUIP_CHECK.MANUFACTURER'
        }, {
            field: 'equiptName',
            title: 'T_EMC_EQUIP_CHECK.EQUIPTNAME'
        }, {
            field: 'model',
            title: 'T_EMC_EQUIP_CHECK.MODEL'
        }, {
            field: 'createdByName',
            title: 'T_EMC_EQUIP_CHECK.CREATEDBYNAME'
        }, {
            field: 'createdTime',
            title: 'T_EMC_EQUIP_CHECK.CREATEDTIME'
        }, {
            field: 'auditorName',
            title: 'T_EMC_EQUIP_CHECK.AUDITORID'
        }, {
            field: 'arrivalDate',
            title: 'T_EMC_EQUIP_CHECK.ARRIVALDATE'
        }, {
            field: 'factoryNumber',
            title: 'T_EMC_EQUIP_CHECK.FACTORYNUMBER'
        }, {
            field: 'appearanceInspection',
            title: 'T_EMC_EQUIP_CHECK.APPEARANCEINSPECTION'
        }, {
            field: 'runningCondition',
            title: 'T_EMC_EQUIP_CHECK.RUNNINGCONDITION'
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_CHECK.REMARK'
        }];
    },

    getPartListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'name',
            title: 'T_EMC_EQUIP_PART.NAME',
            validators: ['notEmpty'],
            editor: true
        }, {
            field: 'number',
            title: 'T_EMC_EQUIP_PART.NUMBER',
            validators: ['notEmpty', 'integer'],
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_PART.REMARK',
            validators: ['notEmpty'],
            editor: true
        }];
    },

    getDataListColumns: function () {
        return [{
            checkbox: true
        }, {
            index: true
        }, {
            field: 'name',
            title: 'T_EMC_EQUIP_PART.NAME',
            validators: ['notEmpty'],
            editor: true
        }, {
            field: 'number',
            title: 'T_EMC_EQUIP_PART.NUMBER',
            validators: ['notEmpty', 'integer'],
            type: 'number',
            hasArrow: true,
            editor: true
        }, {
            field: 'remark',
            title: 'T_EMC_EQUIP_PART.REMARK',
            validators: ['notEmpty'],
            editor: true
        }];
    },

    baseUrl: IFM_CONTEXT + '/secure/emc/module/mdm/equipt/equip-checks',

    workflow: {
        dbTable: 'T_EMC_EQUIP_CHECK',
        columns: [{
            field: 'id',
            title: 'T_EMC_EQUIP_CHECK.ID'
        }]
    }
});
