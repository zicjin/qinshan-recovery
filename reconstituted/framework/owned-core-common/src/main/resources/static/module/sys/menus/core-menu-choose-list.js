coreMenu.choosePage = {

    getMenuGridParam : function() {
        var _this = this;

        var columns = coreMenu.getChooseListColumns().concat();

        if ('1' !== _this.param.filter_reachable)
            columns.pop();

        return {
            type : 'treeGrid',
            id : 'core-menu-choose-list-grid',
            url : coreMenu.baseUrl + '/tree-grid',
            requestData : _this.param,
            single : ('1' === _this.param.single) ? true : false,
            columns : columns,
            page : false,
            order : {
                'parentId' : 'asc',
                'orderNo' : 'asc'
            },
            filterOpen : true,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('core-menu-choose-list-grid').getSelections();

                    if ('1' === _this.param.filter_reachable && selections.length > 0) {
                        if (selections.some(function(item) {
                            return Gikam.isEmpty(item.href) && Gikam.isEmpty(item.resourceName);
                        })) {
                            Gikam.alert("CORE.MODULE.SYS.MENUS.CHOOSE.TIP.HREF_OR_RESOURCENAME_NOT_EMPTY")
                            return;
                        }
                    }

                    Gikam.getLastModal().close(selections);
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

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getMenuGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;

        this.create();
    }
};