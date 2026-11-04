coreEquiptUsageRate = {

    getNorthEChartParam : function() {
        var _this = this;
        return {
            type : 'layout',
            west : {
                width : '70%',
                padding : '3px 2px 2px 2px',
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.TITLE.TOTAL_USAGE_RATE',
                        items : [ {
                            type : 'echarts',
                            id : 'core-equipt-usage-rate-north-echarts',
                            config : {
                                search : {
                                    type : 'form',
                                    id : 'form',
                                    fields : [ {
                                        title : 'T_CORE_EQUIPT_USAGE_RECORD.ANALYSISDIMENSION',
                                        field : 'analysisDimension',
                                        type : 'select',
                                        category : 'equipt-analysis-dimension',
                                        firstBlank : false,
                                        selectedIndex : 1,
                                        onChange : function(field, value) {
                                            var data = {
                                                'analysisDimension' : value
                                            }
                                            Gikam.getComp('core-equipt-usage-rate-north-echarts').setOption(_this.getTotalUsageRateOption(data), true);
                                        }
                                    } ],
                                    onRendered : function() {
                                        var form = Gikam.getComp('form');
                                        var options = form.getField("analysisDimension").options;
                                        var items = options.items;
                                        var date = new Date();
                                        var dataList = [];
                                        for (var i = 0; i < items.length; i++) {
                                            var year = date.getFullYear();
                                            var code = items[i]['code'];
                                            var startYear = year - code;
                                            var newText = startYear + ' — ' + (year - 1) + '年';
                                            dataList.push({
                                                text : newText,
                                                value : code
                                            })
                                        }
                                        Gikam.getComp('form').setSelectOptions('analysisDimension', dataList);
                                    }
                                }
                            },
                            option : _this.getTotalUsageRateOption({
                                'analysisDimension' : '2'
                            }),
                            onReady : function(echarts) {
                                echarts.on('click', function(params) {
                                    var month = params['name'];
                                    var year = params['seriesName'];
                                    Gikam.create('modal', {
                                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.MODAL.EQUIPT_USAGE_RATE',
                                        width : 600,
                                        height : 270,
                                        url : IFM_CONTEXT + '/secure/core/module/equipt/usage-records/page/month-usage-rate-search-list' + Gikam.param({
                                            'month' : month,
                                            'year' : year
                                        }),
                                    })
                                })
                            }
                        } ]
                    } ]
                } ]
            },
            east : {
                width : '30%',
                padding : '3px 2px 2px 3px',
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.TITLE.FIXED_ASSET',
                        items : [ {
                            type : 'echarts',
                            option : _this.getFixedAssetsOption()
                        } ]
                    } ]
                } ]
            }
        }
    },

    getTotalUsageRateData : function(params) {
        var chartData;
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/usage-records/usage-rates/queries', Gikam.getJsonWrapper(params)).done(function(data) {
            chartData = data;
        });
        return chartData
    },

    getTotalUsageRateOption : function(data) {
        var _this = this;
        var chartData = _this.getTotalUsageRateData(data);
        return {
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            tooltip : {
                trigger : 'axis',
                formatter : function(params) {
                    return _this.setTooltipFormatter(params);
                }
            },
            legend : {
                data : chartData['category']
            },
            grid : {
                top : '12%',
                bottom : '12%',
                left : '7%',
                right : '7%'
            },
            xAxis : {
                type : 'category',
                boundaryGap : false,
                data : [ '1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月' ]
            },
            yAxis : {
                type : 'value',
                axisLabel : {
                    formatter : '{value} %'
                }
            },
            series : _this.setTotalUsageRateSeries(chartData)
        }
    },

    setTooltipFormatter : function(params) {
        var formatterValue;
        for (var i = 0; i < params.length; i++) {
            var data = params[i];
            if (i == 0) {
                formatterValue = data['name'];
            }
            var value = data['value'] + '%';
            formatterValue = formatterValue + '<br />' + data['marker'] + data['seriesName'] + ' ' + value;
        }
        return formatterValue;
    },

    setTotalUsageRateSeries : function(chartData) {
        var series = [];
        var category = chartData.category;
        for (var i = 0; i < category.length; i++) {
            var data = category[i];
            series.push({
                name : data,
                type : 'line',
                smooth : true,
                areaStyle : {},
                data : chartData[data]
            });
        }
        return series
    },

    getFixedAssetsData : function() {
        var chartData;
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/equipts/fixed-assets/queries', Gikam.getJsonWrapper({}, [])).done(function(data) {
            chartData = data;
        });
        return chartData
    },

    getFixedAssetsOption : function() {
        var _this = this;
        var chartData = _this.getFixedAssetsData();
        return {
            tooltip : {
                trigger : 'item'
            },
            legend : {
                type : 'scroll',
                orient : 'vertical',
                icon : 'circle',
                right : 30,
                top : '10%',
                formatter : function(name) {
                    return _this.formatterName(name, chartData);
                }
            },
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            series : [ {
                type : 'pie',
                radius : [ '40%', '70%' ],
                center : [ '35%', '50%' ],
                avoidLabelOverlap : false,
                label : {
                    show : false,
                    position : 'center'
                },
                emphasis : {
                    label : {
                        show : true,
                        fontSize : '20',
                        fontWeight : 'bold'
                    }
                },
                labelLine : {
                    show : false
                },
                data : chartData
            } ]
        }
    },

    formatterName : function(name, chartData) {
        for (var i = 0; i < chartData.length; i++) {
            var data = chartData[i];
            var key = data.name;
            var value = data.value;
            if (key == name) {
                return name + '    ' + value;
            }
        }
        return name;
    },

    getWestEChartParam : function() {
        var _this = this;
        return {
            type : 'echarts',
            option : _this.getEquiptCategoryOption()
        }
    },

    getEquiptCategoryData : function() {
        var chartData = [];
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/equipts/category-statistics/queries').done(function(data) {
            chartData = data;
        });
        return chartData
    },

    getEquiptCategoryOption : function() {
        var _this = this;
        var chartData = _this.getEquiptCategoryData();
        return {
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            tooltip : {
                trigger : 'item'
            },
            legend : {
                type : 'scroll',
                orient : 'vertical',
                icon : 'circle',
                right : 30,
                top : '10%',
                formatter : function(name) {
                    return _this.formatterName(name, chartData);
                }
            },
            series : [ {
                type : 'pie',
                radius : [ '40%', '70%' ],
                center : [ '40%', '50%' ],
                avoidLabelOverlap : false,
                label : {
                    show : false,
                    position : 'center'
                },
                emphasis : {
                    label : {
                        show : true,
                        fontSize : '20',
                        fontWeight : 'bold'
                    }
                },
                labelLine : {
                    show : false
                },
                data : chartData
            } ]
        }
    },

    getEastEChartParam : function() {
        var _this = this;
        return {
            type : 'echarts',
            id : 'core-equipt-usage-rate-east-echarts',
            config : {
                search : {
                    type : 'form',
                    id : 'core-equipt-usage-rate-search-form',
                    fields : [ {
                        title : 'T_CORE_EQUIPT_USAGE_RECORD.DEVICECATEGORY',
                        field : 'equiptCategory',
                        type : 'select',
                        category : 'equipt-category',
                        onChange : function(field, value) {
                            var form = Gikam.getComp('core-equipt-usage-rate-search-form');
                            var analysisDimension = form.getField('analysisDimension').value;
                            var data = {
                                'equiptCategory' : value,
                                'analysisDimension' : analysisDimension
                            }
                            Gikam.getComp('core-equipt-usage-rate-east-echarts').setOption(_this.getEquiptCategoryUsageRateOption(data), true);
                        }
                    }, {
                        title : 'T_CORE_EQUIPT_USAGE_RECORD.ANALYSISDIMENSION',
                        field : 'analysisDimension',
                        type : 'select',
                        category : 'equipt-analysis-dimension',
                        firstBlank : false,
                        selectedIndex : 2,
                        onChange : function(field, value) {
                            var form = Gikam.getComp('core-equipt-usage-rate-search-form');
                            var equiptCategory = form.getField('equiptCategory').value;
                            var data = {
                                'equiptCategory' : equiptCategory,
                                'analysisDimension' : value
                            }
                            Gikam.getComp('core-equipt-usage-rate-east-echarts').setOption(_this.getEquiptCategoryUsageRateOption(data), true);
                        }
                    } ]
                }
            },
            option : _this.getEquiptCategoryUsageRateOption({
                'analysisDimension' : '3'
            }),
            onReady : function(echarts) {
                echarts.on('click', function(params) {
                    var categoryName = params['seriesName'];
                    var year = params['name'];
                    Gikam.create('modal', {
                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.MODAL.EQUIPT_CATEGORY_USAGE_RECORD',
                        width : 1000,
                        height : 300,
                        url : IFM_CONTEXT + '/secure/core/module/equipt/usage-records/page/search-list' + Gikam.param({
                            'equiptCategory_EQ' : categoryName,
                            'year' : year,
                            'modal' : true
                        }),
                    })
                })
            }
        }
    },

    getEquiptCategoryUsageRateData : function(data) {
        var chartData;
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/usage-records/equipt-category-usage-rates/queries', Gikam.getJsonWrapper(data)).done(function(data) {
            chartData = data;
        });
        return chartData;
    },

    getEquiptCategoryUsageRateOption : function(data) {
        var _this = this;
        var chartData = _this.getEquiptCategoryUsageRateData(data);
        return {
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            tooltip : {
                trigger : 'axis',
                axisPointer : {
                    type : 'shadow'
                },
                formatter : function(params) {
                    return _this.setTooltipFormatter(params);
                }
            },
            legend : {
                icon : 'circle',
                data : chartData['category']
            },
            grid : {
                top : '15%',
                bottom : '15%'
            },
            xAxis : [ {
                type : 'category',
                axisTick : {
                    show : false
                },
                data : chartData.xAxis
            } ],
            yAxis : [ {
                type : 'value',
                axisLabel : {
                    formatter : '{value} %'
                }
            } ],
            series : _this.setRateSeries(chartData)
        }
    },

    setRateSeries : function(chartData) {
        var series = [];
        var category = chartData.category;
        for (var i = 0; i < category.length; i++) {
            var data = category[i];
            series.push({
                name : data,
                type : 'bar',
                emphasis : {
                    focus : 'series'
                },
                data : chartData[data]
            });
        }
        return series
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-usage-rate-echarts-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getNorthEChartParam() ]
            },
            west : {
                width : '30%',
                padding : '0 3px 3px 3px',
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.TITLE.EQUIPT_CATEGORY',
                        items : [ _this.getWestEChartParam() ]
                    } ]
                } ]
            },
            center : {
                padding : '0 3px 3px 0',
                items : [ {
                    type : 'tab',
                    panels : [ {
                        title : 'CORE.MODULE.EQUIPT.USAGERECORDS.TITLE.EQUIPT_CATEGORY_USAGE_RATE',
                        items : [ _this.getEastEChartParam() ]
                    } ]
                } ]
            }
        });
    },

    init : function() {
        this.create();
    }
}