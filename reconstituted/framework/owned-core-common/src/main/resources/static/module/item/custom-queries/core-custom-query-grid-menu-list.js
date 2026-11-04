coreCustomQuery.gridMenuPage = {

    create : function (r) {
        var _this = this;

        var param = _this.param;

        var queryData = r;

        var columns = [ {
            index : true
        } ];

        var toolbar = [];

        Gikam.getJsonSync(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/query-buttons', {
            id : param.customQueryId
        })).done(function (data) {
            data.forEach(function (item){
                toolbar.push({
                    type : 'button',
                    text : item.buttonName,
                    icon : item.buttonIcon,
                    onClick : function () {
                        var thisGrid = Gikam.getComp('core-custom-query-grid-menu-list-grid'+param.customQueryId);
                        new Function('thisGrid',item.buttonLogic)(thisGrid);
                    }
                })
            })
        })

        Gikam.getJsonSync(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/query-fields', {
            id : param.customQueryId
        })).done(function (data) {
            data.forEach(function (item) {
                if(item.show === '1'){
                    if (item.fieldType === 'select') {
                        if(item.dsCategory === 'customSql'){
                            Gikam.getJsonSync(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/{field}/select-items',{
                                id : _this.param.customQueryId,
                                field : item.fieldName
                            })).done(function (data) {
                                var items = [];
                                data.forEach(function (item){
                                    items.push({
                                        value : Gikam.isEmpty(item.CODE) ? item.code : item.CODE,
                                        text : Gikam.isEmpty(item.TEXT) ? item.text : item.TEXT,
                                    })
                                })
                                columns.push({
                                    title : Gikam.isEmpty(item.columnName) ? item.fieldName : item.columnName,
                                    field : item.fieldName.toLowerCase(),
                                    type : 'select',
                                    items : items,
                                    width : item.width
                                });
                            })
                        }else {
                            columns.push({
                                title : Gikam.isEmpty(item.columnName) ? item.fieldName : item.columnName,
                                field : item.fieldName.toLowerCase(),
                                type : 'select',
                                category : item.dataSource,
                                width : item.width
                            });
                        }
                    } else if(item.fieldType === 'link'){
                        columns.push({
                            title : Gikam.isEmpty(item.columnName) ? item.fieldName : item.columnName,
                            field : item.fieldName.toLowerCase(),
                            type : 'link',
                            width : item.width
                        });
                    } else{
                        columns.push({
                            title : Gikam.isEmpty(item.columnName) ? item.fieldName : item.columnName,
                            field : item.fieldName.toLowerCase(),
                            width : item.width
                        });
                    }
                }

            });

            Gikam.create('layout', {
                id : 'layout',
                north:{
                    height:'auto',
                    items : [{
                        type : 'DynamicQuery',
                        onSearch : function (data) {
                            console.log(data);
                            var requestParam = [];
                            var child = [];
                            data.map(e => {
                                if(e instanceof Array){
                                    e.map(s => {
                                        child.push({
                                            name : s.field,
                                            match : s.operator,
                                            link : 'and',
                                            value : s.value
                                        })
                                    })
                                }else {
                                    if(!Gikam.isEmpty(e.value)){
                                        child.push({
                                            name : e.field,
                                            match : e.operator,
                                            link : 'and',
                                            value : e.value
                                        })
                                    }
                                }

                            });
                            requestParam.push({
                                children : child,
                                link : 'and'
                            })
                            workspace.window.showMask(true);
                            Gikam.postSync(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/custom-menu-queries', {
                                id : param.customQueryId
                            }), Gikam.getJsonWrapper({
                                af : requestParam,
                                n : 1,
                                s : 50
                            })).done(
                                function (data) {
                                    Gikam.getComp('core-custom-query-grid-menu-list-grid' + param.customQueryId).refreshByData({rows : data.rows});
                                }
                            ).always(function () {
                                setTimeout( workspace.window.closeMask,500);
                                // workspace.window.closeMask();
                            });
                        },
                        data : queryData
                    }]
                },
                center : {
                    items : [ {
                        type : 'grid',
                        id : 'core-custom-query-grid-menu-list-grid' + param.customQueryId,
                        initRequest : true,
                        url : Gikam.printf(coreCustomQuery.baseUrl + '/{id}/custom-menu-queries', {
                            id : param.customQueryId
                        }),
                        columns : columns,
                        toolbar : toolbar,
                        onCellClick : function(field, row, index) {
                            data.forEach(function (item) {
                                if (item.fieldType === 'link' && field === item.fieldName.toLowerCase()) {
                                    var url =Gikam.printf(item.linkHref,row);
                                    url = url + '&readonly=true'
                                    if(Gikam.isEmpty(_this.param.modal)){
                                        if(!Gikam.isEmpty(item.linkHref) && !item.linkHref.includes("/")){
                                            //低代码页面路径跳转
                                            workspace.window.loadPage(item.linkHref , row );
                                        }else {
                                            workspace.window.load( IFM_CONTEXT + url );
                                        }
                                    }
                                }
                            })
                        }
                    } ]
                },
                renderTo : Gikam.isNotEmpty(_this.param.modal) ? Gikam.getLastModal().window.$dom : workspace.window.$dom
            });

        })



    },

    initQueries : function () {
        var _this = this
        Gikam.getJson(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/build-high-query',{
            id : _this.param.customQueryId
        })).done(function (r) {
            _this.create(r);
        })
    },



    init : function (param) {
        this.param = param;
        workspace.window.removeTitle();
        this.initQueries();
    }
};
