var emcOrderStock = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + "/secure/emc/module/bp/order/order-stock",

    getOrderStockProjColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'projNo',
            title : 'T_EMC_PROJECT.PROJNO',
            width : 200,
            formatter : function(i, v ,r) {
                if(r.urgent==1){
                    var img = "<img src='/static/emc/icon/urgent.png' width='15' height='15'>";
                    return img + v;
                }else{
                    return v;
                }
            },
        }, {
            field : 'ext$.projname',
            title : 'T_EMC_PROJECT.PROJNAME',
            width : 500,
        }, {
            field : 'ext$.ordernums',
            title : 'T_EMC_SAMPLE.ORDERNUMS',
            width : 150,
            editor :true,
        }, {
            field : 'sampleNo',
            title : 'T_EMC_SAMPLE.SAMPLENO',
            width : 150
        }, {
            field : 'orderCategory',
            title : 'T_EMC_SAMPLE.ORDERCATEGORY',
            width : 150,
        } ];
    },
//已领用需要归还人和领用人
    getOrderLabelNotStockColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        },  {
            field : 'ext$.receiver',
            title : 'T_EMC_ORDER.RECEIVER',
            width : 100,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        receiverId : rows[0].id,
                    	ext$ :{                    		
                    		receiver : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    receiverId : '',
                	ext$ :{                    		
                		receiver : '',
                	}
                });
            },

        }, {
            field : 'ext$.returner',
            title : 'T_EMC_ORDER.RETURNER',
            width : 100,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        returnerId : rows[0].id,
                    	ext$ :{                    		
                    		returner : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    returnerId : '',
                	ext$ :{                    		
                		returner : '',
                	}
                });
            },

        },{
            field : 'ext$.stockdate',
            title : 'T_EMC_ORDER.STOCKDATE',
            type : 'date',
            width : 120,
            editor : true,
        },{
            field : 'ext$.returndate',
            title : 'T_EMC_ORDER.RETURNDATE',
            type : 'date',
            width : 120,
            editor : true,
        },{
            field : 'ext$.stockstatus',
            title : 'T_EMC_ORDER.STOCKSTATUS',
            width : 120,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,  
        }];
    },
    //未领用正常
    getOrderStockedColumns : function() {
        return [ {
            checkbox : true
        }, {
            field : 'orderNo',
            title : 'T_EMC_ORDER.ORDERNO',
            width : 120,
        }, {
            field : 'orderCategory',
            title : 'T_EMC_ORDER.ORDERCATEGORY',
        },  {
            field : 'ext$.receiver',
            title : 'T_EMC_ORDER.RECEIVER',
            width : 100,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        receiverId : rows[0].id,
                    	ext$ :{                    		
                    		receiver : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    receiverId : '',
                	ext$ :{                    		
                		receiver : '',
                	}
                });
            },

        }, {
            field : 'ext$.returner',
            title : 'T_EMC_ORDER.RETURNER',
            width : 100,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        id : this.getActivedRow().id,
                        returnerId : rows[0].id,
                    	ext$ :{                    		
                    		returner : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    id : this.getActivedRow().id,
                    returnerId : '',
                	ext$ :{                    		
                		returner : '',
                	}
                });
            },

        },{
            field : 'ext$.stockdate',
            title : 'T_EMC_ORDER.STOCKDATE',
            type : 'date',
            width : 120,
            editor : true,
        },{
            field : 'ext$.returndate',
            title : 'T_EMC_ORDER.RETURNDATE',
            type : 'date',
            width : 120,
            editor : true,
        },{
            field : 'ext$.stockstatus',
            title : 'T_EMC_ORDER.STOCKSTATUS',
            width : 120,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },
            editor : true,  
        }];
    },
    
    getNoStockBatchColumns : function() {
        return [ {
            field : 'ext$.receiver',
            title : 'T_EMC_ORDER.RECEIVER',
            width : 300,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receiverId : rows[0].id,
                    	ext$ :{                    		
                    		receiver : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    receiverId : '',
                	ext$ :{                    		
                		receiver : '',
                	}
                });
            },

        }, {
            field : 'ext$.returner',
            title : 'T_EMC_ORDER.RETURNER',
            width : 300,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        returnerId : rows[0].id,
                    	ext$ :{                    		
                    		returner : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    returnerId : '',
                	ext$ :{                    		
                		returner : '',
                	}
                });
            },

        },{
            field : 'ext$.stockdate',
            title : 'T_EMC_ORDER.STOCKDATE',
            type : 'date',
            width : 300,
            editor : true,
        },{
            field : 'ext$.returndate',
            title : 'T_EMC_ORDER.RETURNDATE',
            type : 'date',
            width : 300,
            editor : true,
        },{
            field : 'ext$.stockstatus',
            title : 'T_EMC_ORDER.STOCKSTATUS',
            width : 300,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },

            editor : true,  
        }];
    },

    getStockedBatchColumns : function() {
        return [ {
            field : 'ext$.receiver',
            title : 'T_EMC_ORDER.RECEIVER',
            width : 300,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        receiverId : rows[0].id,
                    	ext$ :{                    		
                    		receiver : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    receiverId : '',
                	ext$ :{                    		
                		receiver : '',
                	}
                });
            },

        }, {
            field : 'ext$.returner',
            title : 'T_EMC_ORDER.RETURNER',
            width : 300,
            preInsert : true,
            type : 'choose',
            category : 'user-simple',
            single : true,
            editor :true,
            onAfterChoose : function(rows) {
                if (!Gikam.isEmpty(rows)) {
                    this.setData({
                        returnerId : rows[0].id,
                    	ext$ :{                    		
                    		returner : rows[0].userName,
                    	}
  
                        
                    });
                }
            },
            onClean : function() {
                this.setData({
                    returnerId : '',
                	ext$ :{                    		
                		returner : '',
                	}
                });
            },

        },{
            field : 'ext$.stockdate',
            title : 'T_EMC_ORDER.STOCKDATE',
            type : 'date',
            width : 300,
            editor : true,
        },{
            field : 'ext$.returndate',
            title : 'T_EMC_ORDER.RETURNDATE',
            type : 'date',
            width : 300,
            editor : true,
        },{
            field : 'ext$.stockstatus',
            title : 'T_EMC_ORDER.STOCKSTATUS',
            width : 300,
            type : 'select',
            category : 'sys',
            param : {
                codeCategoryId : 'yesorno',
            },

            editor : true,  
        }];
    },


})