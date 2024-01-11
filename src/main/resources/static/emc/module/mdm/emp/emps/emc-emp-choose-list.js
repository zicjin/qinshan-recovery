emcEmp.choosePage = {

    getGridParam : function() {
        var _this = this, columns = emcEmp.getChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        _this.param.status_SEQ = 'activated';
        var order = null;
        if (_this.param.targetType === 'insertDirector') {
            requestData = {
                targetFilter : {
                    type : "insertDirector",
                    targetTable : "T_EMC_PROJECT_DIRECTOR",
                    targetMatchColumn : 'EMPID',
                    thisMatchColumn : 'ID',
                    targetFilter : {
                        PROJECTID : _this.param.projectId
                    }
                },
                orgId_SB: workspace.user.ext$.parentorgid
            }
            order = {
                departmentid : 'asc'
            }
        } else if(_this.param.sampleUserId !== undefined && Gikam.isNotEmpty(_this.param.sampleUserId)) {
            requestData = {
                status_DIFFER : _this.param.status_DIFFER,
                targetType : _this.param.targetType,
                sampleOrgId : _this.param.sampleOrgId,
            }
        }

        return {
            type : 'grid',
            id : 'emc-emp-choose-list-grid',
            url : emcEmp.baseUrl + '/queries/choosable',
            requestData : requestData,
            order : Gikam.isNotEmpty(order) ? order : null,
            filter : true,
            filterOpen : true,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('emc-emp-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ],
            onRendered : function() {
                if (_this.param.targetType == 'insertDirector') {
                    var grid = Gikam.getComp('emc-emp-choose-list-grid');
                    grid.refreshGroup({
                        fields : [ 'ext$.departmentid' ],
                        formatter : function(row) {
                            return row.ext$.departmentname;
                        },
                    });
                }
            },
            onLoadSuccess : function(){
                if(_this.param.targetType === 'changeSampleUser'){
                    var ids = _this.param.targetIds.split(',');
                    if(Gikam.isNotEmpty(ids)){
                        ids.map(function(id){
                            Gikam.getComp('emc-emp-choose-list-grid').selectRowById(id);
                        });
                    }
                }
                if(_this.param.sampleUserId !== undefined && Gikam.isNotEmpty(_this.param.sampleUserId)) {
                    var ids = _this.param.sampleUserId.split(',');
                    if (Gikam.isNotEmpty(ids)) {
                        ids.map(function(id){
                            Gikam.getComp('emc-emp-choose-list-grid').selectRowById(id);
                        });
                    }
                }
            }
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
                        Gikam.getLastModal().close(Gikam.getComp('emc-emp-choose-list-grid').getSelections());
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
