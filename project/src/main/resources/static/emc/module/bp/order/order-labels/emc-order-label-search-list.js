emcOrderLabel.searchPage = {

    getGridTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            fill : true,
            panels : [ {
                title : 'EMC.MODULE.BP.ORDER.ORDERLABELS.TAB.ADDEDLABEL',
                items : [ {
                    type : 'grid',
                    id : 'emc-order-label-search-list-grid',
                    url : emcOrderLabel.baseUrl + '/queries/searchable',
                    columns : emcOrderLabel.getEditListColumns(),
                    page:true,
                    toolbar : [ { 
                    	type : 'form',
		                id : 'emc-order-label-search-list-form',
		                autoSave : false,
		                fields:[{
		                    title : 'EMC.MODULE.MDM.BIZMDM.MONITORTMPLS.SHOW_ALL',
		                    field : 'showFlag',
		                    width : 100,
		                    type : 'simpleCheckbox',
		                    refreshGrid : false,
		                    styleFormatter : function () {
		                        return {
		                            paddingLeft : '0'
		                        }
		                    },
		                    order:{
		                        createdTime:'desc'
		                    },
		                    onChange : function (field, value) {
		                        let grid = Gikam.getComp('emc-order-label-search-list-grid');
		                        if(value=='1'){
		                            grid.refresh({
		                                url : emcOrderLabel.baseUrl + '/queries',
		                                requestData:{
		                                    createdById_SEQ : ''
		                                }});
		                        } else {
		                            grid.refresh({
		                                url : emcOrderLabel.baseUrl + '/queries',
		                                requestData:{
		                                    createdById_SEQ : workspace.user.id
		                            }});
		                        }
		                    }
		                } ]
		            } ]
                } ]
            } ]
        }
    },

    // 创建页面
    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridTab() ]
            }
        });
    },

    // 初始化
    init : function(param) {
        this.param = param;
        this.create();
    }
};