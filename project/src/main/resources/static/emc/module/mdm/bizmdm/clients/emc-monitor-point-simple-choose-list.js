emcClient.monitorPointSimpleChoosePage = {

    getGridParam : function() {
        var _this = this, columns = emcClient.getMonitorPointSimpleChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });        
                
        if(_this.param.notExistFilter === '1') {
            Gikam.extend(requestData, {
                target_filter : JSON.stringify({
                    type : '',
                    targetTable : 'T_EMC_INFO_TMPL_POINT',
                    targetMatchColumn : 'MONITORPOINTID',
                    thisMatchColumn : 'ID',
                    filter : [ {
                        targetFilterColumn : 'INFOTMPLID',
                        targetFilterValue : _this.param.tmplId
                    } ]
                })
            });
        }
        
        return {
            type : 'grid',
            id : 'emc-monitor-point-simple-choose-list-grid',
            url : emcClient.baseUrl + '/monitor-points/queries/choosable',
            requestData : requestData,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-monitor-point-simple-choose-list-grid').getSelections());
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-monitor-point-simple-choose-list-grid').getSelections());
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