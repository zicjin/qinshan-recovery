var emcMonitorAbility = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'status',
            title : 'T_EMC_MONITOR_ABILITY.STATUS',
            type : 'select',
            search : true,
            width : 100,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            },
            editor : true,
            width : 65
        }, {
            field : 'monitorAbilityNo',
            title : 'T_EMC_MONITOR_ABILITY.MONITORABILITYNO',
            editor : true,
            validators : [ 'strLength[0,12]' ]
        }, {
            field : 'monitorName',
            title : 'T_EMC_MONITOR_ABILITY.MONITORNAME',
            validators : [ 'strLength[0,200]' ],
            editor : true
        }, {
            field : 'monitorObjId',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJID',
            validators : [ 'strLength[0,32]' ],
            editor : true
        }, {
            field : 'monitorObjName',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJNAME',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'projCategory',
            title : 'T_EMC_MONITOR_ABILITY.PROJCATEGORY',
            validators : [ 'strLength[0,50]' ],
            editor : true
        }, {
            field : 'methodSource',
            title : 'T_EMC_MONITOR_ABILITY.METHODSOURCE',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'authentication',
            title : 'T_EMC_MONITOR_ABILITY.AUTHENTICATION',
            type : 'select',
            search : true,
            width : 100,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'approval',
            title : 'T_EMC_MONITOR_ABILITY.APPROVAL',
            type : 'select',
            search : true,
            width : 100,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true
        }, {
            field : 'instruction',
            title : 'T_EMC_MONITOR_ABILITY.INSTRUCTION',
            validators : [ 'strLength[0,100]' ],
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_MONITOR_ABILITY.REMARK',
            type : 'richText',
            validators : [ 'strLength[0,200]' ],
            editor : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_EMC_MONITOR_ABILITY.ID',
            readOnly : true,
        }, {
            field : 'monitorName',
            title : 'T_EMC_MONITOR_ABILITY.MONITORNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'monitorObjId',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJID',
            preInsert : true,
            validators : [ 'strLength[0,32]' ]
        }, {
            field : 'monitorObjName',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJNAME',
            preInsert : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            field : 'projCategory',
            title : 'T_EMC_MONITOR_ABILITY.PROJCATEGORY',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'methodSource',
            title : 'T_EMC_MONITOR_ABILITY.METHODSOURCE',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'authentication',
            title : 'T_EMC_MONITOR_ABILITY.AUTHENTICATION',
            validators : [ 'strLength[0,10]' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'approval',
            title : 'T_EMC_MONITOR_ABILITY.APPROVAL',
            validators : [ 'strLength[0,10]' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'instruction',
            title : 'T_EMC_MONITOR_ABILITY.INSTRUCTION',
            validators : [ 'strLength[0,10]' ]
        }, {
            field : 'status',
            title : 'T_EMC_MONITOR_ABILITY.STATUS',
            validators : [ 'strLength[0,10]' ],
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            }
        }, {
            field : 'remark',
            title : 'T_EMC_MONITOR_ABILITY.REMARK',
            type : 'textarea',
            validators : [ 'strLength[0,170]' ],
            colspan : 3

        } ];
    },

    getTestItemColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'ext$.testcategorydesc',
            title : 'T_EMC_MONITOR_ABILITY_TEST.TESTCATEGORY'
        }, {
            field : 'ext$.testname',
            title : 'T_EMC_MONITOR_ABILITY_TEST.TESTNAME'
        }, {
            field : 'ext$.methodname',
            title : 'T_EMC_MONITOR_ABILITY_TEST.METHOD',
            type : 'link'
        }, ];
    },

    getEquipItemColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'defaultFlag',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.DEFAULTFLAG',
            width : 100,
            editor : true,
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
        }, {
            field : 'ext$.equiptids',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.EQUIPTID'
        }, {
            field : 'ext$.equiptname',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.EQUIPTNAME'
        }, {
            field : 'ext$.department',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.DEPARTMENT',
            editor : true,
            category : 'org-simple',
            type : 'choose',
            single : true,
            onBeforeChoose : function(rows) {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').setData({
                        id : Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').getActivedRow().id,
                        departmentId : rows[0].id,
                        ext$ : {
                            department : rows[0].orgName,
                        }
                    });
                }
            },
            onClean : function() {
                Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').setData({
                    id : Gikam.getComp('emc-monitor-ability-edit-list-equipt-grid').getActivedRow().id,
                    departmentId : '',
                    ext$ : {
                        department : ''
                    }
                });
            },
        }, {
            field : 'ext$.status',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'equiptStatus',
            },
        }, {
            field : 'ext$.maintainuserid',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.MAINTAINUSERID'
        }, {
            field : 'ext$.address',
            title : 'T_EMC_MONITOR_ABILITY_EQUIPT.ADDRESS'
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'status',
            title : 'T_EMC_MONITOR_ABILITY.STATUS',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'activatedFlag',
            }
        }, {
            field : 'id',
            title : 'T_EMC_MONITOR_ABILITY.ID',
            type : 'link'
        }, {
            field : 'monitorName',
            title : 'T_EMC_MONITOR_ABILITY.MONITORNAME'
        }, {
            field : 'monitorObjId',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJID'
        }, {
            field : 'monitorObjName',
            title : 'T_EMC_MONITOR_ABILITY.MONITOROBJNAME'
        }, {
            field : 'projCategory',
            title : 'T_EMC_MONITOR_ABILITY.PROJCATEGORY'
        }, {
            field : 'methodSource',
            title : 'T_EMC_MONITOR_ABILITY.METHODSOURCE'
        }, {
            field : 'authentication',
            title : 'T_EMC_MONITOR_ABILITY.AUTHENTICATION',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'approval',
            title : 'T_EMC_MONITOR_ABILITY.APPROVAL',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            field : 'instruction',
            title : 'T_EMC_MONITOR_ABILITY.INSTRUCTION'
        }, {
            field : 'remark',
            title : 'T_EMC_MONITOR_ABILITY.REMARK'
        } ];
    },

    getEmpItemColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'empId',
            title : 'T_EMC_MONITOR_ABILITY_EMP.EMPID'
        }, {
            field : 'ext$.username',
            title : 'T_EMC_MONITOR_ABILITY_EMP.EMPUSERNAME'
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/monitor-abilities',

    workflow : {
        dbTable : 'T_EMC_MONITOR_ABILITY',
        columns : [ {
            field : 'id',
            title : 'T_EMC_MONITOR_ABILITY.ID'
        } ]
    }
});
