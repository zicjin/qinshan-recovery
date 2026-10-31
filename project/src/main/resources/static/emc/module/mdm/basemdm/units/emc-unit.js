var emcUnit = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/units',

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_EMC_UNIT.UNITNAME',
            field : 'unitName',
            type : 'link'
        }, {
            title : 'T_EMC_UNIT.UNITCATEGORY',
            field : 'unitCategory',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'unitCategory'
            },
            editor : true
        }, {
            title : 'T_EMC_UNIT.UNITFULLNAME',
            field : 'unitFullName',
            editor : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            title : 'T_EMC_UNIT.UNITSHORTNAME',
            field : 'unitShortName',
            editor : true,
            validators : [ 'strLength[0,21]' ]
        }, {
            title : 'T_EMC_UNIT.STANDARDUNIT',
            field : 'standardUnit',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            title : 'T_EMC_UNIT.BASICUNIT',
            field : 'basicUnit',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            title : 'T_EMC_UNIT.CALIBRATIONUNIT',
            field : 'calibrationUnit',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            title : 'T_EMC_UNIT.SORTNUM',
            field : 'sortNum',
            editor : true,
            type : 'number',
            hasArrow : true,
        } ]
    },

    getBaseInfoFormFields : function() {
        return [ {
            title : 'T_EMC_UNIT.UNITNAME',
            field : 'unitName',
            validators : [ 'strLength[0,64]' ],
            colspan : 3,
            preInsert : true
        }, {
            title : 'T_EMC_UNIT.UNITCATEGORY',
            field : 'unitCategory',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'unitCategory'
            },
            preInsert : true
        }, {
            title : 'T_EMC_UNIT.UNITFULLNAME',
            field : 'unitFullName',
            validators : [ 'strLength[0,21]' ]
        }, {
            title : 'T_EMC_UNIT.UNITSHORTNAME',
            field : 'unitShortName',
            validators : [ 'strLength[0,21]' ]
        }, {
            title : 'T_EMC_UNIT.STANDARDUNIT',
            field : 'standardUnit',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            title : 'T_EMC_UNIT.BASICUNIT',
            field : 'basicUnit',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            }
        }, {
            title : 'T_EMC_UNIT.CALIBRATIONUNIT',
            field : 'calibrationUnit',
            type : 'select',
            search : true,
            category : 'sys',
            editor : true,
            param : {
                codeCategoryId : 'yesorno',
            }
        } ]
    },

    getChooseListColumns : function() {
        return [ {
            title : 'T_EMC_UNIT.UNITNAME',
            field : 'unitName'
        }, {
            title : 'T_EMC_UNIT.UNITCATEGORY',
            field : 'unitCategory',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'unitCategory'
            },
        }, {
            title : 'T_EMC_UNIT.UNITFULLNAME',
            field : 'unitFullName'
        }, {
            title : 'T_EMC_UNIT.UNITSHORTNAME',
            field : 'unitShortName'
        }, {
            title : 'T_EMC_UNIT.SORTNUM',
            field : 'sortNum',
        } ]
    },

    getConvertListColumns : function() {
        return [ {
            checkbox : true
        }, {
            index : true
        }, {
            title : 'T_EMC_UNITCONVERT.CONVERTFACTOR',
            field : 'convertFactor',
            editor : true,
            validators : [ 'number' ],
        }, {
            title : 'T_EMC_UNITCONVERT.CONVERTUNITID',
            field : 'ext$.convertunitname',
            type : 'choose',
            category : 'unit',
            single : true,
            editor : true,
            onAfterChoose : function(rows, index) {
                if (rows) {
                    this.setData({
                        index : index,
                        convertUnitId : rows[0].id,
                        ext$ : {
                            convertunitname : rows[0].unitName
                        }
                    })
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    convertUnitId : '',
                    ext$ : {
                        convertunitname : '',
                    }
                });
            },
        } ]
    }

});
