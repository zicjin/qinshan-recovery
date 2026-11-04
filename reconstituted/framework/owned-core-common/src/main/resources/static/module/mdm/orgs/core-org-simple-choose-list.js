coreOrg.simpleChoosePage = {

    getGridParam : function() {
        var _this = this, columns = coreOrg.getSimpleChooseListColumns(), requestData = this.param;
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });
        requestData.parentId = 'root';
        return {
            type : 'grid',
            id : 'core-org-simple-choose-list-grid',
            url : coreOrg.baseUrl + '/queries/choosable',
            requestData : requestData,
            useStorageColumns:false,
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-org-simple-choose-list-grid').getSelections());
                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CANCEL',
                icon : 'cancel',
                onClick : function() {
                    Gikam.getLastModal().close();
                }
            } ],
            columns : columns
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
                        Gikam.getLastModal().close(Gikam.getComp('core-org-simple-choose-list-grid').getSelections());
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
            id : 'core-org-simple-choose-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            west : {
                width : 200,
                items : [ {
                    type : 'tree',
                    id : 'core-org-simple-choose-list-tree',
                    name : 'CORE.MODULE.MDM.ORGS.TAB.ORGANIZATION',
                    url : coreOrg.baseUrl + '/tree/{parentId}',
                    onNodeSelected : function(nodeId) {
                        Gikam.getComp('core-org-simple-choose-list-grid').refresh({
                            requestData : {
                                parentId : Gikam.isEmpty(nodeId) ? 'root' : nodeId
                            }
                        });
                    }
                } ]
            },
            center : {
                items : [ _this.getGridParam() ]
            }
        });
    },
    init : function(param) {
        this.param = param;
        this.create();
    }
};