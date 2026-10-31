emcQualityControlChart.searchListPage = {

    getQueryFormTab : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.S,
            panels : [ {
                title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.QUERY_TERM',
                items : [ {
                    type : 'form',
                    id : 'emc-quality-control-chart-search-list-form',
                    columns : 1,
                    autoSave : false,
                    fields : emcQualityControlChart.getQueryFormFields(),
                } ]
            } ]
        }
    },

    resultGrid : null,
    getResultListParam : function() {
        var _this = this;
        return {
            type : 'tab',
            width : Gikam.width.N,
            panels : [ {
                title : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.QUERY_RESULT',
                items : [ {
                    type : 'grid',
                    id : 'emc-quality-control-chart-search-list-grid',
                    filterOpen : false,
                    url : '',
                    columns : emcQualityControlChart.getSearchListColumns(),
                    checkOnActive : true,
                    toolbar : [ {
                        type : 'button',
                        text : 'GIKAM.SEARCH',
                        icon : 'search',
                        onClick : function() {
                            _this.searchResultList();
                        }
                    }, {
                        type : 'button',
                        text : 'GIKAM.AT.DELETE',
                        icon : 'remove',
                        onClick : function() {
                            _this.removeData();
                        }
                    }, {
                        type : 'button',
                        text : 'EMC.MODULE.SA.QUALITYCONTROLCHARTS.BUTTON.REFRESH_CHART',
                        icon : 'review',
                        onClick : function() {
                            _this.refreshChart();
                        }
                    } ],
                    onRendered : function() {
                        _this.resultGrid = this;
                    },
                } ]
            } ]
        }
    },

    getChartsParam : function() {
        var _this = this;
        var option = null;
        return {
            type : 'echarts',
            id : 'emc-quality-control-chart-search-list-echarts-grid',
            option : {},
        }
    },

    create : function() {
        Gikam.create('layout', {
            id : 'emc-quality-control-chart-search-list-layout',
            north : {
                height : '50%',
                items : [ {
                    type : 'layout',
                    west : {
                        width : '30%',
                        items : [ this.getQueryFormTab() ]
                    },
                    center : {
                        width : '70%',
                        items : [ this.getResultListParam() ]
                    },
                } ]
            },
            center : {
                items : [ this.getChartsParam() ]
            },
            renderTo : workspace.window.$dom,
        });
        return this;
    },

    init : function() {
        this.create();
    },

    searchResultList : function() {
        var form = Gikam.getComp('emc-quality-control-chart-search-list-form').getData();
        var url = emcQualityControlChart.baseUrl + '/order-task-results/queries';
        
        this.resultGrid.refresh({
            url : url,
            requestData : {
                testCategoryId_SEQ : Gikam.isNotEmpty(form.testCategory) ? form.testCategory : null,
                testId_SEQ : Gikam.isNotEmpty(form.testName) ? form.testName : null,
                itemId_SEQ : Gikam.isNotEmpty(form.itemName) ? form.itemName : null,
                qcCategoryId_SEQ : Gikam.isNotEmpty(form.qcCategory) ? form.qcCategory : null,
                inputUserId_SEQ : Gikam.isNotEmpty(form.userName) ? form.userName : null,
                analysisDate_TGOE : Gikam.isNotEmpty(form.startDate) ? form.startDate : null,
                analysisDate_TLOE : Gikam.isNotEmpty(form.endDate) ? form.endDate : null,
                pageSize : Gikam.isNotEmpty(form.pageSize) ? form.pageSize : -1
            },
            order : {
                id : 'asc'
            },
            page : true
        });
    },

    getOption : function() {
        var _this = this;
        var series = [];
        var xData = [];
        var yData = [];
        var yAxisArray = [];
        var seriesNames = [];
        var json = {};

        var sumReport = 0; // 和
        var len = 0; // 数量
        var avg = 0; // 均值
        var variance = 0; // 方差
        var std = 0; // 标准差

        var deploy = {
            color : [ '#00d201', '#f5dd67', '#f5a670', '#b0b9ec', '#ea7f83', '#ee764d', '#6bc2c7', ' #79a9f2' ], // 折线图的颜色
            areaColor : 'rgba(0, 210, 1, 0.2)', // 折线图下的填充色
            textColor : 'rgba(0, 0, 0, 0.65)', // 文字的颜色
            yLineStyle : { // y轴上辅助线的样式
                type : 'dashed', // 虚线
                color : '#eef1f7' // 颜色
            },
            grid : { // 图表四周的位置
                top : 84,
                bottom : 36,
                size : 1000
            }
        };

        var datas = Gikam.isNotEmpty(Gikam.getComp('emc-quality-control-chart-search-list-grid').getData()) ? Gikam.getComp('emc-quality-control-chart-search-list-grid').getData() : [];

        var regex = /^[0-9]+.?[0-9]*$/;
        datas.map(function(item) {
            if (regex.test(item.reportResult)) {
                sumReport = sumReport + parseFloat(item.reportResult);
                len = len + 1;
            }
        });
        if (len === 0) {
            xData.push(Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.NO_DATA'));
            json.data = [ '0' ];
            json.type = 'line';
            json.itemStyle = {
                normal : {
                    label : {
                        show : true,
                        position : 'top',
                    }
                }
            };
            json.areaStyle = {
                color : deploy.areaColor
            };
            var yAxis = {
                type : 'value',
                splitLine : {
                    lineStyle : deploy.yLineStyle
                },
                axisLine : {
                    lineStyle : {
                        color : 'gray'
                    }
                },
                boundaryGap : true,
                axisLabel : {
                    color : 'rgba(0, 0 , 0, 0.65)'
                }
            };
            yAxisArray.push(yAxis);
            series.push(json);
        } else {
            avg = sumReport / len;

            var yData = [];
            datas.map(function(item) {
                if (regex.test(item.reportResult)) {
                    variance = variance + (parseFloat(item.reportResult) - avg) * (parseFloat(item.reportResult) - avg);

                    yData.push(parseFloat(item.reportResult));
                }
            });

            std = Math.sqrt(variance / (len - 1));

            // 组装x轴
            for (var i = 0; i < len; i++) {
                xData.push(i + 1);
            }
            series.push({
                data : yData,
                type : 'line',
                lineStyle : {
                    color : '#0000CD'
                },
                itemStyle : {
                    normal : {
                        label : {
                            show : true
                        }
                    }
                },
                markLine : {
                    symbol : "none",
                    data : [ {
                        yAxis : avg,
                        lineStyle : {
                            type : "solid",
                            color : "black",
                        },
                        label : {
                            position : 'end',
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_CL')
                        }
                    }, {
                        yAxis : avg + std,
                        lineStyle : {
                            type : "solid",
                            color : "blue",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_UAL')
                        }

                    }, {
                        yAxis : avg - std,
                        lineStyle : {
                            type : "solid",
                            color : "blue",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_LAL')
                        }
                    }, {
                        yAxis : avg + 2 * std,
                        lineStyle : {
                            type : "solid",
                            color : "yellow",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_UWL')
                        }
                    }, {
                        yAxis : avg - 2 * std,
                        lineStyle : {
                            type : "solid",
                            color : "yellow",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_LWL')
                        }
                    }, {
                        yAxis : avg + 3 * std,
                        lineStyle : {
                            type : "solid",
                            color : "red",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_ULC')
                        }
                    }, {
                        yAxis : avg - 3 * std,
                        lineStyle : {
                            type : "solid",
                            color : "red",
                        },
                        label : {
                            formatter : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TIP.CHART_LCL')
                        }
                    } ]
                }
            });
            
            // 组装y轴
            var minY = Math.floor((avg - 3 * std) / 10) * 10;
            var maxY = Math.ceil((avg + 3 * std) / 10) * 10;
            var yAxis = {
                type : 'value',
                splitLine : {
                    lineStyle : deploy.yLineStyle
                },
                axisLine : {
                    lineStyle : {
                        color : 'gray'
                    }
                },
                min : minY,
                max : maxY,
                splitNumber : 10,
                axisLabel : {
                    color : 'rgba(0, 0 , 0, 0.65)'
                },
            };
        }
        
        var option = {
            title : {
                left : 'center',
                text : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.COMMON_CONTROL_CHART'),
                subtext : Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.MEAN_VALUE') + avg + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.STANDARD_DEVIATION') + std + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.ADD_ONCE') + (avg + std) + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.REDUCE_ONCE') + (avg - std) + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.ADD_TWICE') + (avg + 2 * std) + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.REDUCE_TWICE') + (avg - 2 * std) + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.ADD_THIRD') + (avg + 3 * std) + '  ' + Gikam.propI18N('EMC.MODULE.SA.QUALITYCONTROLCHARTS.TITLE.REDUCE_THIRD') + (avg - 3 * std),
                grid : deploy.grid,
                color : deploy.color,
                show : true,
                textStyle : {
                    fontWeight : 'bold',
                    fontFamily : 'sans-serif',
                    fontSize : '14',
                    color : deploy.textColor
                },
                x : 8,
                y : 8,
            },
            legend : {
                data : seriesNames,
                type : 'scroll',
                top : '40',
                itemWidth : 8,
                textStyle : {
                    padding : [ 2, 0, 0, 0 ],
                    fontSize : 10
                },
                formatter : function(name) {
                    return name;
                }
            },
            tooltip : {
                trigger : 'axis',
                axisPointer : {
                    type : 'shadow'
                }
            },
            xAxis : {
                axisLine : {
                    lineStyle : {
                        color : deploy.yLineStyle
                    }
                },
                axisLabel : {
                    color : 'rgba(0, 0 , 0, 0.65)',
                },
                data : xData,
            },
            dataZoom : {
                type : 'inside',
                start : 0,
                end : 100
            },
            yAxis : [ yAxis ],
            series : series
        };
        return option;
    },

    refreshChart : function() {
        var option = this.getOption();
        var chart = Gikam.getComp('emc-quality-control-chart-search-list-echarts-grid');
        chart.model.$refs.vm.echarts.setOption(option, true);
    },
    
    removeData : function() {
        var selections = Gikam.getComp('emc-quality-control-chart-search-list-grid').getSelections();
        if(selections.length < 1){
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }
        var allDatas = Gikam.getComp('emc-quality-control-chart-search-list-grid').getData();
        var filterDatas = allDatas.filter((item) => !selections.some((removeItem) => item.id === removeItem.id));
        Gikam.getComp('emc-quality-control-chart-search-list-grid').loadData(filterDatas);
    },
};
