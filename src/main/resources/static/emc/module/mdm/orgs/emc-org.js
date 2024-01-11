Gikam.extend(coreOrg, {
    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'id',
            title : 'T_CORE_ORG.ID',
            type : 'link',
        }, {
            field : 'orgName',
            title : 'T_CORE_ORG.ORGNAME',
        }, {
            field : 'orgCategory',
            title : 'T_CORE_ORG.ORGCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        }, {
            field : 'ext$.orgshortname',
            title : 'T_EMC_ORG.SUBSIDIARYNAME'
        }, {
            field : 'ext$.orderaccept',
            title : 'T_EMC_ORG.ORDERACCEPT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.sampledepartment',
            title : 'T_EMC_ORG.SAMPLEDEPARTMENT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.orgenglishname',
            title : 'EMC.MODULE.MDM.ORGS.ORGENGLISHNAME',
        }, {
            field : 'ext$.orgpostcode',
            title : 'EMC.MODULE.MDM.ORGS.ORGPOSTCODE',
        }, {
            field : 'ext$.orgreservedphone',
            title : 'EMC.MODULE.MDM.ORGS.ORGCOMPLAINTSHOTLINE',
        }, {
            field : 'ext$.orgcomplaintshotline',
            title : 'EMC.MODULE.MDM.ORGS.ORGRESERVEDPHONE',
        }, {
            field : 'ext$.orgaddress',
            title : 'EMC.MODULE.MDM.ORGS.ORGADDRESS',
        }, {
            field : 'ext$.orgemail',
            title : 'T_EMC_ORG.ORGEMAIL',
        }, {
            field : 'ext$.erpmerchantid',
            title : 'T_EMC_ORG.ERPMERCHANTID',
        }, {
            field : 'ext$.erpgbid',
            title : 'T_EMC_ORG.ERPGBID',
        }, {
            field : 'ext$.controlnumber',
            title : 'T_EMC_ORG.CONTROLNUMBER',
        }, {
            field : 'ext$.qccontrolnumber',
            title : 'T_EMC_ORG.QCCONTROLNUMBER',
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ORG.ID',
        }, {
            field : 'orgName',
            title : 'T_CORE_ORG.ORGNAME',
        }, {
            field : 'orgCategory',
            title : 'T_CORE_ORG.ORGCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        }, {
            field : 'ext$.orgshortname',
            title : 'T_EMC_ORG.SUBSIDIARYNAME'
        }, {
            field : 'ext$.orderaccept',
            title : 'T_EMC_ORG.ORDERACCEPT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.sampledepartment',
            title : 'T_EMC_ORG.SAMPLEDEPARTMENT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'id',
            title : 'T_CORE_ORG.ID',
            preInsert : true,
            validators : [ 'remote[/secure/core/module/mdm/orgs/{id}/action/validate-unique]', 'notEmpty', 'strLength[0,16]' ]
        }, {
            field : 'orgName',
            title : 'T_CORE_ORG.ORGNAME',
            preInsert : true,
            validators : [ 'notEmpty', 'strLength[0,85]' ]
        }, {
            field : 'orgCategory',
            title : 'T_CORE_ORG.ORGCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'orgcategory'
            }
        }, {
            field : 'ext$.orgshortname',
            title : 'T_EMC_ORG.SUBSIDIARYNAME'
        }, {
            field : 'ext$.quayear',
            title : 'T_EMC_ORG.QUAYEAR'
        }, {
            field : 'ext$.orderaccept',
            title : 'T_EMC_ORG.ORDERACCEPT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.sampledepartment',
            title : 'T_EMC_ORG.SAMPLEDEPARTMENT',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno'
            }
        }, {
            field : 'ext$.orgenglishname',
            title : 'EMC.MODULE.MDM.ORGS.ORGENGLISHNAME_SON',
            editor : true
        }, {
            field : 'ext$.orgpostcode',
            title : 'EMC.MODULE.MDM.ORGS.ORGPOSTCODE',
            editor : true
        }, {
            field : 'ext$.orgreservedphone',
            title : 'EMC.MODULE.MDM.ORGS.ORGCOMPLAINTSHOTLINE',
            editor : true
        }, {
            field : 'ext$.orgcomplaintshotline',
            title : 'EMC.MODULE.MDM.ORGS.ORGRESERVEDPHONE',
            editor : true
        }, {
            field : 'ext$.orgaddress',
            title : 'EMC.MODULE.MDM.ORGS.ORGADDRESS',
            editor : true
        }, {
            field : 'ext$.orgemail',
            title : 'T_EMC_ORG.ORGEMAIL',
        }, {
            field : 'ext$.erpmerchantid',
            title : 'T_EMC_ORG.ERPMERCHANTID',
        }, {
            field : 'ext$.erpgbid',
            title : 'T_EMC_ORG.ERPGBID',
        }, {
            field : 'ext$.controlnumber',
            title : 'T_EMC_ORG.CONTROLNUMBER',
        }, {
            field : 'ext$.qccontrolnumber',
            title : 'T_EMC_ORG.QCCONTROLNUMBER',
        } ]
    }
});
