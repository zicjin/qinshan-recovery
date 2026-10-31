var emcPurchApply = Gikam.getPageObject({
	
	baseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/purch-applies',
	chooseUrl : IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/vendors',
	
	getEditListColumns : function () {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcPurchApply.workflow.dbTable),{
        	field : 'purchaseCategory',
        	title : 'T_DINGAN_PURCH.PURCHASECATEGORY',
        	type : 'select',
        	category : 'sys',
        	param : {
        		codeCategoryId : 'purchaseCategory',
        	},
        	editor : true
        }, {
        	field : 'instruction',
        	title : 'T_DINGAN_PURCH.INSTRUCTION',
        	width : 400,
        	editor : true
        }, {
        	field : 'remark',
        	title : 'T_DINGAN_PURCH.REMARK',
        	width : 200,
        	editor : true
        }, {
        	field : 'createdByName',
        	title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
        	field : 'createdTime',
        	title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
			field : 'auditorName',
			title : 'T_DINGAN_PURCH.AUDITORNAME',
			editor : true,
			validators : ['notEmpty'],
			category : 'emp',
			type : 'choose',
			single : true,
			onAfterChoose : function(rows) {
				if (!Gikam.isEmpty(rows)) {
					this.setData({
						id : Gikam.getComp('emc-purch-apply-edit-list-grid').getActivedRow().id,
						auditorName : rows[0].userName,
						auditorId : rows[0].id
					});
				}
			},
			onClean : function() {
				this.setData({
					id : Gikam.getComp('emc-purch-apply-edit-list-grid').getActivedRow().id,
					auditorName : '',
					auditorId : ''
				});
			}
		}]
	},
	
	getPurchDetailColumns : function() {
		return [{
			checkbox : true
		}, {
			field : 'purchaseName',
			title : 'T_DINGAN_PURCH_DETAIL.PURCHASENAME',
			editor : true
		}, {
			field : 'specification',
			title : 'T_DINGAN_PURCH_DETAIL.SPECIFICATION',
			editor : true
		}, {
			field : 'technicalIndex',
			title : 'T_DINGAN_PURCH_DETAIL.TECHNICALINDEX',
			editor : true
		}, {
			field : 'supplier',
			title : 'T_DINGAN_PURCH_DETAIL.SUPPLIER',
			type : 'choose',
        	category : 'vendor',
        	single : true,
        	editor : true,
            onAfterChoose : function(rows,index) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                    	index : index,
                    	supplier : rows[0].vendorName,
                    });
                }
            },
            onClean : function() {
                this.setData({
                    supplier : ''
                });
            }
		}, {
			field : 'numb',
			title : 'T_DINGAN_PURCH_DETAIL.NUMB',
			editor : true
		}, {
			field : 'remark',
			title : 'T_DINGAN_PURCH_DETAIL.REMARK',
			width : 200,
			editor : true
		}]
	},
	
	getPurchAddColumns : function() {
		return [ {
        	field : 'purchaseCategory',
        	title : 'T_DINGAN_PURCH.PURCHASECATEGORY',
        	type : 'select',
        	category : 'sys',
        	param : {
        		codeCategoryId : 'purchaseCategory',
        	},
        	preInsert : true,
		}, {
			field : 'instruction',
			title : 'T_DINGAN_PURCH.INSTRUCTION',
			preInsert : true
		}, {
			field : 'remark',
			title : 'T_DINGAN_PURCH.REMARK',
			preInsert : true
		} ]
	},
	
	getAuditListColumns : function() {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcPurchApply.workflow.dbTable),{
        	field : 'purchaseCategory',
        	title : 'T_DINGAN_PURCH.PURCHASECATEGORY',
        	type : 'select',
        	category : 'sys',
        	param : {
        		codeCategoryId : 'purchaseCategory',
        	}
        }, {
        	field : 'instruction',
        	title : 'T_DINGAN_PURCH.INSTRUCTION',
        	width : 400
        }, {
        	field : 'remark',
        	title : 'T_DINGAN_PURCH.REMARK',
        	width : 200
        }, {
        	field : 'createdByName',
        	title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
        	field : 'createdTime',
        	title : 'T_DINGAN_PURCH.CREATEDTIME'
        }, {
			field : 'confirmerName',
			title : 'T_DINGAN_PURCH.CONFIRMERNAME',
			editor : true,
			validators : ['notEmpty'],
			category : 'emp',
			type : 'choose',
			single : true,
			onAfterChoose : function(rows) {
				if (!Gikam.isEmpty(rows)) {
					this.setData({
						id : Gikam.getComp('emc-purch-apply-audit-list-grid').getActivedRow().id,
						confirmerName : rows[0].userName,
						confirmerId : rows[0].id
					});
				}
			},
			onClean : function() {
				this.setData({
					id : Gikam.getComp('emc-purch-apply-audit-list-grid').getActivedRow().id,
					confirmerName : '',
					confirmerId : ''
				});
			}
		}]
	},

	getConfirmListColumns : function() {
		return [{
			checkbox: true
		}, Gikam.status.getBpmnColumn(emcPurchApply.workflow.dbTable),{
			field : 'purchaseCategory',
			title : 'T_DINGAN_PURCH.PURCHASECATEGORY',
			type : 'select',
			category : 'sys',
			param : {
				codeCategoryId : 'purchaseCategory',
			}
		}, {
			field : 'instruction',
			title : 'T_DINGAN_PURCH.INSTRUCTION',
			width : 400
		}, {
			field : 'remark',
			title : 'T_DINGAN_PURCH.REMARK',
			width : 200
		}, {
			field : 'createdByName',
			title : 'T_DINGAN_PURCH.CREATEDBYNAME'
		}, {
			field : 'createdTime',
			title : 'T_DINGAN_PURCH.CREATEDTIME'
		}]
	},
	
	getAuditPurchDetailColumns : function() {
		return [{
			checkbox : true
		}, {
			field : 'purchaseName',
			title : 'T_DINGAN_PURCH_DETAIL.PURCHASENAME'
		}, {
			field : 'specification',
			title : 'T_DINGAN_PURCH_DETAIL.SPECIFICATION'
		}, {
			field : 'technicalIndex',
			title : 'T_DINGAN_PURCH_DETAIL.TECHNICALINDEX'
		}, {
			field : 'supplier',
			title : 'T_DINGAN_PURCH_DETAIL.SUPPLIER'
		}, {
			field : 'numb',
			title : 'T_DINGAN_PURCH_DETAIL.NUMB',
		}, {
			field : 'remark',
			title : 'T_DINGAN_PURCH_DETAIL.REMARK',
			width : 200
		}]
	},
	
	getSearchListColumns : function() {
		return [{
            checkbox: true
        }, Gikam.status.getBpmnColumn(emcPurchApply.workflow.dbTable),{
        	field : 'purchaseCategory',
        	title : 'T_DINGAN_PURCH.PURCHASECATEGORY',
        	type : 'select',
        	category : 'sys',
        	param : {
        		codeCategoryId : 'purchaseCategory',
        	}
        }, {
        	field : 'instruction',
        	title : 'T_DINGAN_PURCH.INSTRUCTION',
        	width : 400
        }, {
        	field : 'remark',
        	title : 'T_DINGAN_PURCH.REMARK',
        	width : 200
        }, {
        	field : 'createdByName',
        	title : 'T_DINGAN_PURCH.CREATEDBYNAME'
        }, {
        	field : 'createdTime',
        	title : 'T_DINGAN_PURCH.CREATEDTIME'
        }]
	},
	
	getSearchPurchDetailColumns : function() {
		return [{
			checkbox : true
		}, {
			field : 'purchaseName',
			title : 'T_DINGAN_PURCH_DETAIL.PURCHASENAME'
		}, {
			field : 'specification',
			title : 'T_DINGAN_PURCH_DETAIL.SPECIFICATION'
		}, {
			field : 'technicalIndex',
			title : 'T_DINGAN_PURCH_DETAIL.TECHNICALINDEX'
		}, {
			field : 'supplier',
			title : 'T_DINGAN_PURCH_DETAIL.SUPPLIER'
		}, {
			field : 'numb',
			title : 'T_DINGAN_PURCH_DETAIL.NUMB',
		}, {
			field : 'remark',
			title : 'T_DINGAN_PURCH_DETAIL.REMARK',
			width : 200
		}]
	},
	
	getChooseListColumns : function() {
		return [ {
			field : 'vendorName',
			title : 'T_EMC_VENDOR.VENDORNAME'
		} ]
	},
	
    workflow : {
        dbTable : 'T_DINGAN_PURCH',
        columns : [ {
            field : 'id',
            title : 'T_DINGAN_PURCH.ID'
        } ]
    }
});