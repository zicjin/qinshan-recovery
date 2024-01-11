emcRun.batchEditItemPage = {

    getEditItemGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'emc-run-batch-edit-item-list-grid',
            url : emcRun.baseUrl + '/edited-items/queries',
            requestData : _this.param,
            columns : emcRun.getEditItemListColumns(),
            group : {
                fields : [ 'ext$.testname' ],
                formatter : function(row) {
                    return row.ext$.testname;
                }
            },
            onLoadSuccess : function(data) {
                let itemGrid = Gikam.getComp('emc-run-batch-edit-item-list-grid');
                data.filter(item => item.ext$.selected === '1' && item.$rowStyle !== 'group').forEach(item => itemGrid.selectRowById(item.id));
            },
            toolbar : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.CONFIRM',
                icon : 'select',
                onClick : function() {
                    var selections = Gikam.getComp('emc-run-batch-edit-item-list-grid').getSelections();
                    if (Gikam.isEmpty(selections)) {
                        Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    let testMethodIds = Array.from(new Set(selections.map(item => item.testMethodId)));

                    let orderTaskIdStr = _this.param.id_IN;
                    let orderTaskIds = orderTaskIdStr.split(',');

                    if (testMethodIds.length !== orderTaskIds.length) {
                        Gikam.alert('EMC.MODULE.BP.RESULT.RUNS.TIP.KEEP_AT_LEAST_ONE_ITEM');
                        return;
                    }

                    Gikam.getLastModal().close(selections);
                }
            } ]
        }
    },

    createPage : function() {
        Gikam.create('layout', {
            id : 'emc-run-add-run-detail-layout',
            center : {
                items : [ this.getEditItemGridParam() ]
            },
            renderTo : Gikam.getLastModal().window.$dom
        });
        return this;
    },

    init : function(param) {
        this.param = param;
        this.createPage();
    }
}
