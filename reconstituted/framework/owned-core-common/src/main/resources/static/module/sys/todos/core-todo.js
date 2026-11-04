/**
 * 待办列表
 */
var coreTodo = Gikam.getPageObject({

    baseUrl : IFM_CONTEXT + '/core/module/sys/todos',

    getQueryListColumns : function() {
        return [ {
            field : 'index',
            title : '序号',
            width : 50,
            align : 'center',
            formatter : function(i, v, row) {
                return i + 1;
            }
        }, {
            field : 'modelName',
            width : 300,
            title : '内容提示',
            type : 'link'
        }, {
            field : 'qty',
            title : '项数',
            type : 'link'
        } ]
    }
});