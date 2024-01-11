var emcFixedasset = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'assetCategory',
            title : 'T_EMC_FIXEDASSET.ASSETCATEGORY',
            category : 'sys',
            param : {
                codeCategoryId : 'assetCategory'
            },
            search : true,
            type : 'select',
        }, {
            field : 'assetName',
            title : 'T_EMC_FIXEDASSET.ASSETNAME',
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'assetNo',
            title : 'T_EMC_FIXEDASSET.ASSETNO',
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'model',
            title : 'T_EMC_FIXEDASSET.MODEL',
            validators : [ 'strLength[0,36]' ]
        } ];
    },

    getPreInsertFields : function() {
        return [ {
            field : 'assetCategory',
            title : 'T_EMC_FIXEDASSET.ASSETCATEGORY',
            editor : true,
            category : 'sys',
            param : {
                codeCategoryId : 'assetCategory'
            },
            type : 'select',
            search : true,
            preInsert : true
        }, {
            field : 'assetName',
            title : 'T_EMC_FIXEDASSET.ASSETNAME',
            editor : true,
            validators : [ 'strLength[0,36]' ],
            preInsert : true
        } ];
    },

    getBaseInfoFormFields : function() {
        return [ {
            field : 'assetCategory',
            title : 'T_EMC_FIXEDASSET.ASSETCATEGORY',
            editor : false,
            category : 'sys',
            param : {
                codeCategoryId : 'assetCategory'
            },
            search : true,
            type : 'select',
        }, {
            field : 'assetName',
            title : 'T_EMC_FIXEDASSET.ASSETNAME',
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'assetNo',
            title : 'T_EMC_FIXEDASSET.ASSETNO',
            editor : false,
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'model',
            title : 'T_EMC_FIXEDASSET.MODEL',
            editor : true,
            validators : [ 'strLength[0,36]' ]
        }, {
            field : 'quantity',
            title : 'T_EMC_FIXEDASSET.QUANTITY',
            editor : true,
            validators : [ 'strLength[0,15]' ],
            isInteger : true,
            type : 'number',
            hasArrow : true,
        }, {
            field : 'price',
            title : 'T_EMC_FIXEDASSET.PRICE',
            editor : true,
            validators : [ 'strLength[0,15],number' ],
            type : 'number',
            hasArrow : true,
        }, {
            field : 'totalAmount',
            title : 'T_EMC_FIXEDASSET.TOTALAMOUNT',
            editor : true,
            validators : [ 'strLength[0,15],number' ],
            type : 'number',
            hasArrow : true,
        }, {
            field : 'purchaseTime',
            title : 'T_EMC_FIXEDASSET.PURCHASETIME',
            editor : true,
            type : 'date'
        }, {
            field : 'factoryNo',
            title : 'T_EMC_FIXEDASSET.FACTORYNO',
            editor : true,
            validators : [ 'strLength[0,36]' ],
        }, {
            field : 'responsityOrgName',
            title : 'T_EMC_FIXEDASSET.RESPONSITYORG',
            editor : true,
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {

                    this.setData({
                        id : Gikam.getComp('emc-fixedasset-edit-list-grid').getActivedRow().id,
                        responsityOrgId : rows[0].id,
                        responsityOrgName : rows[0].orgName
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-fixedasset-edit-list-grid').getActivedRow().id,
                    responsityOrgId : '',
                    responsityOrgName : ''
                });
            }
        }, {
            field : 'useOrgName',
            title : 'T_EMC_FIXEDASSET.USEORG',
            editor : true,
            type : 'choose',
            category : 'org-simple',
            single : true,
            onBeforeChoose : function() {
                return {
                    orgCategory_SEQ : 'department'
                }
            },
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : Gikam.getComp('emc-fixedasset-edit-list-grid').getActivedRow().id,
                        useOrgId : rows[0].id,
                        useOrgName : rows[0].orgName
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : Gikam.getComp('emc-fixedasset-edit-list-grid').getActivedRow().id,
                    useOrgId : '',
                    useOrgName : ''
                });

            }
        }, {
            field : 'useModify',
            title : 'T_EMC_FIXEDASSET.USEMODIFY',
            editor : true,
            validators : [ 'strLength[0,36]' ],
        }, {
            field : 'storePlace',
            title : 'T_EMC_FIXEDASSET.STOREPLACE',
            editor : true,
            validators : [ 'strLength[0,36]' ],
        }, {
            field : 'remark',
            title : 'T_EMC_FIXEDASSET.REMARK',
            type : 'text',
            editor : true,
            validators : [ 'strLength[0,540]' ],
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/fixedassets',

    workflow : {
        dbTable : 'T_EMC_FIXEDASSET',
        columns : [ {
            field : 'id',
            title : 'T_EMC_FIXEDASSET.ID'
        } ]
    }
});
