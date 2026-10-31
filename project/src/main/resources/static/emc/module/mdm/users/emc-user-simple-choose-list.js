Gikam.extend(coreUser.simpleChoosePage, {

    getGridParam : function() {
        var _this = this, columns = coreUser.getSimpleChooseListColumns();
        _this.param.single ? columns.unshift({
            radio : true
        }) : columns.unshift({
            checkbox : true
        });

        _this.param.status_SEQ = 'activated';
        _this.param.org_filter = '1';

        return {
            type : 'grid',
            id : 'core-user-choose-list-grid',
            url : coreUser.baseUrl + '/queries/choosable',
            filterOpen : true,
            requestData : _this.param,
            columns : columns,
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    Gikam.getLastModal().close(Gikam.getComp('core-user-choose-list-grid').getSelections());
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

});
