var dataSource = Gikam.getPageObject({

    getEditListColumns : function() {
        return [ {
            index : true
        }, {
            field : 'sheetname',
            title : 'sheet页'
        }, {
            field : 'address',
            title : '位置'
        }, {
            field : 'datasourcename',
            title : '数据源'
        }, {
            field : 'dataclass',
            title : '反存类型'
        } ];
    },
});
