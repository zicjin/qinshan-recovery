coreCustomQuery.reportMenuPage = {

    create : function (r,a) {
        var _this = this;

        var param = _this.param;

        var queryData = r;

        Gikam.create('layout', {
            id : 'layout',
            north:{
                height:'auto',
                items : [{
                    type : 'DynamicQuery',
                    onSearch : function (data) {
                        var param = _this.formatParam(data)
                        let requestParam = '&';
                        for (let paramKey in param) {
                            let val = param[paramKey];
                            let sqlParam = paramKey+'_param=';
                            for (let i = 0; i < val.length; i++) {
                                let str = _this.formatSql(val[i]);
                                if(val.length === 1){
                                    sqlParam+=str + '&';
                                }else {
                                    if(i===val.length-1){
                                        sqlParam+=str+'&';
                                    }else {
                                        sqlParam+=str + ' and ';
                                    }
                                }
                            }
                            requestParam += sqlParam;
                        }
                        Gikam.getComp('layout').getPosition('center').refreshItems([_this.getReportParam(_this.reportUrl + requestParam)]);
                    },
                    data : queryData
                }]
            },
            center : {
                items : [_this.getReportParam(_this.reportUrl)]
            },
            renderTo : Gikam.isNotEmpty(_this.param.modal) ? Gikam.getLastModal().window.$dom : workspace.window.$dom
        });


    },

    getReportParam :  function (url) {
        var _this = this;
        return {
            id : _this.param.reportConfigLineId,
            type : 'iframe',
            src : url
        }
    },

    formatSql : function (e) {
        let requestParam = ''
        if(!Gikam.isEmpty(e.value)){
            if(e.operator == 'EQ' || e.operator == 'SEQ'){
                requestParam +=  e.field + " = '" +e.value +"'"
            }
            if(e.operator == 'SB'){
                requestParam +=  e.field + " like '" + e.value +"%25'"
            }
            if(e.operator == 'SC'){
                requestParam +=  e.field + " like '%25" +e.value +"%25'"
            }
            if(e.operator == 'SE'){
                requestParam +=  e.field + " like '%25" +e.value + "'"
            }
            if(e.operator == 'NL'){
                requestParam +=  e.field + " < " +e.value
            }
            if(e.operator == 'NG'){
                requestParam +=  e.field + " > " +e.value
            }
            if(e.operator == 'NEQ'){
                requestParam +=  e.field + " = " +e.value
            }
            if(e.operator == 'DLOE'){
                if(Gikam.audit.trail.includes('mysql')){
                    requestParam +=   "date_format(" + e.field + ",'%25Y-%25m-%25d')"  + " <= " + "'" + e.value + "'"
                }else if(Gikam.audit.trail.includes('sqlserver')){
                    requestParam +=   "CONVERT(varchar(100)," + e.field + ",23)"  + " <= " + "'" + e.value + "'"
                }else {
                    requestParam +=  "to_char(" + e.field + ",'yyyy-MM-dd')" + " <= " + "'" + e.value + "'"
                }
            }
            if(e.operator == 'DGOE'){
                if(Gikam.audit.trail.includes('mysql')){
                    requestParam +=   "date_format(" + e.field + ",'%25Y-%25m-%25d')"  + " >= " + "'" + e.value + "'"
                }else if(Gikam.audit.trail.includes('sqlserver')){
                    requestParam +=   "CONVERT(varchar(100)," + e.field + ",23)"  + " >= " + "'" + e.value + "'"
                }else {
                    requestParam +=  "to_char(" + e.field + ",'yyyy-MM-dd')" + " >= " + "'" + e.value + "'"
                }
            }
            if(e.operator == 'TLOE'){
                if(Gikam.audit.trail.includes('mysql')){
                    requestParam +=   "date_format(" + e.field + ",'%25Y-%25m-%25d %25H:%25i:%25s')"  + " <= " + "'" + e.value + "'"
                }else if(Gikam.audit.trail.includes('sqlserver')){
                    requestParam +=   "CONVERT(varchar(100)," + e.field + ",20)"  + " <= " + "'" + e.value + "'"
                }else {
                    requestParam +=  "to_char(" + e.field + ",'yyyy-MM-dd hh:mm:ss')" + " <= " + "'" + e.value + "'"
                }
            }
            if(e.operator == 'TGOE'){
                if(Gikam.audit.trail.includes('mysql')){
                    requestParam +=   "date_format(" + e.field + ",'%25Y-%25m-%25d %25H:%25i:%25s')"  + " >= " + "'" + e.value + "'"
                }else if(Gikam.audit.trail.includes('sqlserver')){
                    requestParam +=   "CONVERT(varchar(100)," + e.field + ",20)"  + " >= " + "'" + e.value + "'"
                }else {
                    requestParam +=  "to_char(" + e.field + ",'yyyy-MM-dd hh:mm:ss')" + " >= " + "'" + e.value + "'"
                }
            }
        }
        return requestParam;
    },

    formatParam : function filterByDs(data) {
        var dataArr = [];
        data.map(e=>{
            if(e instanceof Array){
                e.map(s =>{
                    dataArr.push(s);
                })
            }else {
                dataArr.push(e);
            }
        })
        let arr_ds = [];
        let ds = '';
        for (let i = 0; i < dataArr.length; i++) {
            ds = dataArr[i].field.split('_')[0]
            arr_ds.push(ds);
        }

        const setArr = [...new Set(arr_ds)];

        let obj = {};
        for (let i = 0; i < setArr.length; i++) {
            let ds = setArr[i]
            var s = dataArr.filter(item => {
                if (item.field.split('_')[0] === ds && !Gikam.isEmpty(item.value)) {
                    return true
                } else {
                    return false
                }
            })
            obj[ds] = s.map(item => {
                return {
                    field: item.field.split("_")[1],
                    operator: item.operator,
                    value: item.value
                }
            })
        }
        return obj;
    },

    viewReport : function(reportConfigLineId, paramValueJson) {
        var reportViewUrl = Gikam.jQuery.Deferred();
        if(paramValueJson){
            paramValueJson = Gikam.extend(paramValueJson, paramValueJson.ext$);
            // 将key值转成小写
            for ( var key in paramValueJson) {
                paramValueJson[key.toLocaleLowerCase()] = paramValueJson[key];
                if (key.toLocaleLowerCase() != key) {
                    delete (paramValueJson[key]);
                }
            }
        }

        if (!Gikam.isEmpty(reportConfigLineId)) {

            var url = IFM_CONTEXT + '/secure/core/module/sys/report-configs/lines/queries';
            Gikam.post(url, Gikam.getJsonWrapper({
                f : {
                    id_SEQ : reportConfigLineId
                },
            })).done(function(r) {
                if (r.total >= 1) {
                    var reportLine = r.rows[0];
                    var fileName = encodeURIComponent(reportLine.ext$.filename);
                    var param = null;
                    if (!Gikam.isEmpty(reportLine.param)) {
                        param = (reportLine.param).replace(/{[\w]+}/g, function(match) {
                            return match.toLocaleLowerCase();
                        });
                    }
                    var reportPath = null;
                    if (reportLine.reportCategory === 'dataCenter' && !Gikam.isEmpty(reportLine.dataCenterReportUrl)) { // 数据中心报表
                        reportPath = reportLine.dataCenterReportUrl;
                    } else { // 帆软报表
                        reportPath = reportLine.reportConfigLineNo + '/' + fileName;
                    }
                    reportViewUrl.resolve(Gikam.getViewReportUrl(reportLine.reportCategory, reportPath, param, paramValueJson));
                }
            });
        } else {
            reportViewUrl.resolve(null);
        }
        return reportViewUrl;
    },

    initQueries : function () {
        var _this = this;
        Gikam.getJson(Gikam.printf(coreCustomQuery.baseUrl + '/{id}/build-high-query',{
            id : _this.param.customQueryId
        })).done(function (r) {
            _this.create(r);
        })
    },


    init :  function (param) {
        this.param = param;
        var _this = this;
        workspace.window.removeTitle();
        this.reportUrl = this.viewReport(param.reportConfigLineId,null).done(function (url) {
            _this.reportUrl = url;
            _this.initQueries();
        })
    }
};
