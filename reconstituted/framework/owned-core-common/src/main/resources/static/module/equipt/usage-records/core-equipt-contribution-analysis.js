//@ sourceURL=core-equipt-contribution-analysis.js
coreEquiptContributionAnalysis = {

    getTotalContributionEChartParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.EQUIPT.USAGERECORDS.TITLE.TOTAL_CONTRIBUTION',
                items : [ {
                    type : 'echarts',
                    id : 'core-equipt-contribution-analysis-echarts-total',
                    config : _this.getTotalContributionConfig(),
                    option : _this.getTotalContributionOption({
                        'analysisDimension' : '2'
                    }),
                    onReady : function(echarts) {
                        echarts.on('click', function(params) {
                            var month = params['name'];
                            var year = params['seriesName'];
                            Gikam.create('modal', {
                                title : 'CORE.MODULE.EQUIPT.USAGERECORDS.MODAL.EQUIPT_USE_LINE',
                                width : 795,
                                height : 300,
                                url : IFM_CONTEXT + '/secure/core/module/equipt/usage-records/page/month-contribution-search-list' + Gikam.param({
                                    'month' : month,
                                    'year' : year
                                }),
                            })
                        })
                    }
                } ]
            } ],
        }
    },

    getTotalContributionConfig : function() {
        var _this = this;
        return {
            search : {
                type : 'form',
                id : 'form',
                fields : [ {
                    title : 'T_CORE_EQUIPT_USAGE_RECORD.STARTYEAR',
                    field : 'analysisDimension',
                    type : 'select',
                    category : 'equipt-analysis-dimension',
                    firstBlank : false,
                    selectedIndex : 1,
                    onChange : function(field, value) {
                        var data = {
                            'analysisDimension' : value
                        }
                        Gikam.getComp('core-equipt-contribution-analysis-echarts-total').setOption(_this.getTotalContributionOption(data), true);
                    }
                } ],
                onRendered : function() {
                    var form = Gikam.getComp('form');
                    var items = form.getField("analysisDimension").options.items;
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
        }
    },

    getTotalContributionData : function(data) {
        var chartData;
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/usage-records/contributions/queries', Gikam.getJsonWrapper(data)).done(function(data) {
            chartData = data;
        });
        return chartData
    },

    getTotalContributionOption : function(data) {
        var _this = this;
        var chartData = _this.getTotalContributionData(data);
        return {
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            tooltip : {
                trigger : 'axis',
            },
            legend : {
                data : chartData['category']
            },
            grid : {
                top : '12%',
                bottom : '13%',
                left : '6%',
                right : '6%'
            },
            toolbox : {},
            xAxis : {
                type : 'category',
                boundaryGap : false,
                data : [ '1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月' ]
            },
            yAxis : [ {
                type : 'value',
                position : 'left',
            }, {
                name : '单位：元',
                position : 'right',
                axisLabel : {
                    show : false
                },
                axisTick : {
                    show : false
                },
                axisLine : {
                    show : false
                },
            } ],
            series : _this.setTotalContributionSeries(chartData)
        }
    },

    setTotalContributionSeries : function(chartData) {
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
        return series;
    },

    getCategoryContributionEChartParam : function() {
        var _this = this;
        return {
            type : 'tab',
            panels : [ {
                title : 'CORE.MODULE.EQUIPT.USAGERECORDS.MODAL.EQUIPT_CATEGORY_CONTRIBUTION',
                items : [ {
                    type : 'echarts',
                    id : 'core-equipt-contribution-analysis-category-echarts',
                    config : _this.getCategoryContributionConfig(),
                    option : _this.getCategoryContributionOption({
                        'analysisDimension' : '3'
                    }),
                    onReady : function(echarts) {
                        echarts.on('click', function(params) {
                            var categoryName = params['seriesName'];
                            var year = params['name'];
                            Gikam.create('modal', {
                                title : 'CORE.MODULE.EQUIPT.USAGERECORDS.MODAL.EQUIPT_USAGE_RECORD',
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
                } ]
            } ]
        }
    },

    getCategoryContributionConfig : function() {
        var _this = this;
        return {
            search : {
                type : 'form',
                id : 'core-equipt-contribution-analysis-search-form',
                fields : [ {
                    title : 'T_CORE_EQUIPT_USAGE_RECORD.DEVICECATEGORY',
                    field : 'equiptCategory',
                    type : 'select',
                    category : 'equipt-category',
                    onChange : function(field, value) {
                        var form = Gikam.getComp('core-equipt-contribution-analysis-search-form');
                        var analysisDimension = form.getField('analysisDimension').value;
                        var data = {
                            'equiptCategory' : value,
                            'analysisDimension' : analysisDimension
                        }
                        Gikam.getComp('core-equipt-contribution-analysis-category-echarts').setOption(_this.getCategoryContributionOption(data), true);
                    }
                }, {
                    title : 'T_CORE_EQUIPT_USAGE_RECORD.ANALYSISDIMENSION',
                    field : 'analysisDimension',
                    type : 'select',
                    category : 'equipt-analysis-dimension',
                    firstBlank : false,
                    selectedIndex : 2,
                    onChange : function(field, value) {
                        var form = Gikam.getComp('core-equipt-contribution-analysis-search-form');
                        var equiptCategory = form.getField('equiptCategory').value;
                        var data = {
                            'equiptCategory' : equiptCategory,
                            'analysisDimension' : value
                        }
                        Gikam.getComp('core-equipt-contribution-analysis-category-echarts').setOption(_this.getCategoryContributionOption(data), true);
                    }
                } ]
            }
        }
    },

    getEquiptCategoryContributionData : function(data) {
        var chartData;
        Gikam.postSync(IFM_CONTEXT + '/secure/core/module/equipt/usage-records/equipt-category-contributions/queries', Gikam.getJsonWrapper(data)).done(function(data) {
            chartData = data;
        });
        return chartData
    },

    getCategoryContributionOption : function(data) {
        var _this = this;
        var chartData = _this.getEquiptCategoryContributionData(data);
        return {
            color : [ '#819FF7', '#81F7BE', '#58D3F7', '#FFBF00', '#F79F81', '#FA5858', '#F7819F', '#BCA9F5' ],
            tooltip : {
                trigger : 'axis',
                axisPointer : {
                    type : 'shadow'
                }
            },
            legend : {
                icon : 'circle',
                data : chartData['category']
            },
            grid : {
                top : '14%',
                bottom : '14%',
                left : '6%',
                right : '6%'
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
                position : 'left',
            }, {
                name : '单位：元',
                position : 'right',
                axisLabel : {
                    show : false
                },
                axisTick : {
                    show : false
                },
                axisLine : {
                    show : false
                },
            } ],
            series : _this.setEquiptCategoryContributionSeries(chartData)
        }
    },

    setEquiptCategoryContributionSeries : function(chartData) {
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
            id : 'core-equipt-contribution-analysis-layout',
            renderTo : workspace.window.$dom,
            north : {
                height : '50%',
                items : [ _this.getTotalContributionEChartParam() ]
            },
            center : {
                items : [ _this.getCategoryContributionEChartParam() ]
            }
        });
    },

    init : function() {
        this.create();
    }
}