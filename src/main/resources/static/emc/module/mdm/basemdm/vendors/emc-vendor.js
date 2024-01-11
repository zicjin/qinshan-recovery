var emcVendor = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'vendorName',
            title : 'T_EMC_VENDOR.VENDORNAME',
            validators : [ 'strLength[0,40]' ],
            editor : true
        }, {
            field : 'vendorCategory',
            title : 'T_EMC_VENDOR.VENDORCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'vendorCategory'
            },
            width : 90,
        }, {
            field : 'contact',
            title : 'T_EMC_VENDOR.CONTACT',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'phone',
            title : 'T_EMC_VENDOR.PHONE',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'email',
            title : 'T_EMC_VENDOR.EMAIL',
            validators : [ 'email', 'strLength[0,36]' ],
            editor : true
        }, {
            field : 'fax',
            title : 'T_EMC_VENDOR.FAX',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'post',
            title : 'T_EMC_VENDOR.POST',
            validators : [ 'number', 'strLength[0,6]' ],
            editor : true,
            width : 90,
        }, {
            field : 'address',
            title : 'T_EMC_VENDOR.ADDRESS',
            validators : [ 'strLength[0,20]' ],
            editor : true
        }, {
            field : 'evaluationLevel',
            title : 'T_EMC_VENDOR.EVALUATIONLEVEL',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'remark',
            title : 'T_EMC_VENDOR.REMARK',
            validators : [ 'strLength[0,180]' ],
            editor : true
        } ];
    },

    getChooseListColumns : function() {
        return [ {
            field : 'vendorName',
            title : 'T_EMC_VENDOR.VENDORNAME',
            validators : [ 'strLength[0,40]' ],
        }, {
            field : 'vendorCategory',
            title : 'T_EMC_VENDOR.VENDORCATEGORY',
            type : 'select',
            search : true,
            category : 'sys',
            param : {
                codeCategoryId : 'vendorCategory'
            },
            width : 90,
        } ]
    },

    getMtlListColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_VENDOR_MTL.ORDERNO',
            validators : [ 'integer' ],
            editor : true
        }, {
            field : 'supplyMtlName',
            title : 'T_EMC_VENDOR_MTL.SUPPLYMTLNAME',
            validators : [ 'strLength[0,20]' ],
            editor : true
        }, {
            field : 'supplyCategory',
            title : 'T_EMC_VENDOR_MTL.SUPPLYCATEGORY',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'supplySpec',
            title : 'T_EMC_VENDOR_MTL.SUPPLYSPEC',
            validators : [ 'strLength[0,12]' ],
            editor : true
        }, {
            field : 'supplyTime',
            title : 'T_EMC_VENDOR_MTL.SUPPLYTIME',
            type : 'dateTime',
            editor : true
        }, {
            field : 'supplyQty',
            title : 'T_EMC_VENDOR_MTL.SUPPLYQTY',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'totalAmount',
            title : 'T_EMC_VENDOR_MTL.TOTALAMOUNT',
            validators : [ 'number' ],
            editor : true
        }, {
            field : 'qualityRecord',
            title : 'T_EMC_VENDOR_MTL.QUALITYRECORD',
            validators : [ 'strLength[0,20]' ],
            editor : true
        }, {
            field : 'ext$.auditusername',
            title : 'T_EMC_VENDOR_MTL.AUDITUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var grid = Gikam.getComp('emc-vendor-mtl-list-grid');
                    grid.disableAutoSave();
                    var rowData = grid.getActivedRow();
                    var data = {};
                    data.id = rowData.id;
                    data.auditUser = rows[0].id;
                    Gikam.put(Gikam.printf(emcVendor.baseUrl + "/{id}", {
                        id : rowData.vendorId
                    }), Gikam.getJsonWrapper(null, [ 'emcVendorMtlServiceImpl', [ data ] ])).done(function() {
                        grid.setData({
                            id : Gikam.getComp('emc-vendor-mtl-list-grid').getActivedRow().id,
                            ext$ : {
                                auditusername : rows[0].userName
                            }
                        }, false);
                        grid.enableAutoSave();
                    });
                }
            },
            onClean : function() {
                var grid = Gikam.getComp('emc-vendor-mtl-list-grid');
                grid.setData({
                    id : grid.getActivedRow().id,
                    auditUser : '',
                    ext$ : {
                        auditusername : ''
                    }
                });
            },
            editor : true
        }, {
            field : 'ext$.authorizeusername',
            title : 'T_EMC_VENDOR_MTL.AUTHORIZEUSER',
            type : 'choose',
            category : 'user-simple',
            single : true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    var grid = Gikam.getComp('emc-vendor-mtl-list-grid');
                    grid.disableAutoSave();
                    var rowData = grid.getActivedRow();
                    var data = {};
                    data.id = rowData.id;
                    data.authorizeUser = rows[0].id;
                    Gikam.put(Gikam.printf(emcVendor.baseUrl + "/{id}", {
                        id : rowData.vendorId
                    }), Gikam.getJsonWrapper(null, [ 'emcVendorMtlServiceImpl', [ data ] ])).done(function() {
                        grid.setData({
                            id : Gikam.getComp('emc-vendor-mtl-list-grid').getActivedRow().id,
                            ext$ : {
                                authorizeusername : rows[0].userName
                            }
                        }, false);
                        grid.enableAutoSave();
                    });
                }
            },
            onClean : function() {
                var grid = Gikam.getComp('emc-vendor-mtl-list-grid');
                grid.setData({
                    id : grid.getActivedRow().id,
                    authorizeUser : '',
                    ext$ : {
                        authorizeusername : ''
                    }
                });
            },
            editor : true
        } ];
    },

    baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/vendors'

});
