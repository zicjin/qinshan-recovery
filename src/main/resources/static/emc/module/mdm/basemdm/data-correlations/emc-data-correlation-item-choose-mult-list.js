emcDataCorrelation.itemChooseMultChoosePage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.BACK',
                icon : 'back',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getShuttleFrameParam : function() {
        var _this = this;
        return {
            type : 'shuttleFrame',
            id : 'emc-data-correlation-item-choose-mult-list-shuttle-frame',
            columns : emcDataCorrelation.getItemChooseMultEditListColumns(),
            leftGridUrl : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/data-correlations/{id}/items/chooseable', {
                id : _this.param.id
            }),
            leftRequestData : {
                'ext$.testcategoryid_EQ' : '0', // 如果key带ext$前缀，则应用单引号括起来
                target_filter : JSON.stringify({
                    type : 'unassigned',
                    targetTable : "T_EMC_DATA_CORRELATION_ITEM",
                    targetMatchColumn : "ITEMID",
                    thisMatchColumn : "ITEMID",
                    filter : [ {
                        targetFilterColumn : 'CORRELATIONID',
                        targetFilterValue : _this.param.id
                    } ]
                })
            },
            rightGridUrl : Gikam.printf(IFM_CONTEXT + '/secure/emc/module/mdm/basemdm/data-correlations/{id}/items/queries', {
                id : _this.param.id
            }),
            filterOpen : true,
        }
    },
    
    save : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-data-correlation-item-choose-mult-list-shuttle-frame').getSelectList();
        var chooseData = rows ? rows.map(function(row) {
            return {
                correlationId : _this.param.id,
                testId : row.testId,
                methodId : row.methodId,
                itemId : row.itemId
            };
        }) : [];
        Gikam.getLastModal().window.showMask(true);
        Gikam.put(Gikam.printf('/secure/emc/module/mdm/basemdm/data-correlations/{id}/items', {
            id : _this.param.id
        }), Gikam.getJsonWrapper({}, [ 'emcDataCorrelationItemServiceImpl', chooseData ])).done(function() {
            Gikam.getLastModal().close();
        }).always(function() {
            Gikam.getLastModal().window.closeMask();
        });
    },

    create : function() {
        var _this = this;
        // 
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBtnToolbar(), this.getShuttleFrameParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }

}