emcEquipt.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcEquipt.getChooseListColumns(), requestData = this.param;

        if (requestData.biz === 'orderTask') {
            Gikam.extend(requestData, {
                target_equiptassigned : '1',
                testMethodId : requestData.testMethodId,
                maintenanceUser_assigned : '1',
                maintenanceUserId : workspace.user.id
            });
        }
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        return {
            type : 'grid',
            id : 'emc-equipt-choose-list-grid',
            url : emcEquipt.baseUrl + '/queries/choosable',
            requestData : requestData,
            filterOpen : true,
            columns : columns,
            page : true,
            checkContinuous :!_this.param.single,
            onLoadSuccess : function() {
                if (Gikam.isNotEmpty(_this.param.targetIds)) {
                    var equiptGrid = Gikam.getComp('emc-equipt-choose-list-grid');
                    var equiptIds = _this.param.targetIds.split(',');
                    equiptIds.map(function(id){
                        equiptGrid.selectRowById(parseInt(id));
                    });
                }
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-equipt-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ]
        }
    },

    getBtnToolbarParam : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'btnGroup',
                items : [ {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CONFIRM',
                    icon : 'select',
                    onClick : function() {
                        Gikam.getLastModal().close(Gikam.getComp('emc-equipt-choose-list-grid').getSelections());
                    }
                }, {
                    type : 'button',
                    text : 'GIKAM.BUTTON.CANCEL',
                    icon : 'cancel',
                    onClick : function() {
                        Gikam.getLastModal().close();
                    }
                } ]
            } ]
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
