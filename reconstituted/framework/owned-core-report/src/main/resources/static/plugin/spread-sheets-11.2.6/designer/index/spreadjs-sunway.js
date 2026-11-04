/**
 * 这个文件是自定义，整合业务所需
 */

// 反存 数据
var idData;
var repeatData;
(function ($, window, undefined) {
    var elems = $([]),
        jq_resize = $.resize = $.extend($.resize, {}),
        timeout_id,
        str_setTimeout = 'setTimeout',
        str_resize = 'resize',
        str_data = str_resize + '-special-event',
        str_delay = 'delay',
        str_throttle = 'throttleWindow';
    jq_resize[str_delay] = 250;
    jq_resize[str_throttle] = true;
    $.event.special[str_resize] = {
        setup: function () {
            if (!jq_resize[str_throttle] && this[str_setTimeout]) {
                return false;
            }
            var elem = $(this);
            elems = elems.add(elem);
            $.data(this, str_data, {
                w: elem.width(),
                h: elem.height()
            });
            if (elems.length === 1) {
                loopy();
            }
        },
        teardown: function () {
            if (!jq_resize[str_throttle] && this[str_setTimeout]) {
                return false;
            }
            var elem = $(this);
            elems = elems.not(elem);
            elem.removeData(str_data);
            if (!elems.length) {
                clearTimeout(timeout_id);
            }
        },
        add: function (handleObj) {
            if (!jq_resize[str_throttle] && this[str_setTimeout]) {
                return false;
            }
            var old_handler;

            function new_handler(e, w, h) {
                var elem = $(this),
                    data = $.data(this, str_data);
                data.w = w !== undefined ? w : elem.width();
                data.h = h !== undefined ? h : elem.height();
                old_handler.apply(this, arguments);
            }

            if ($.isFunction(handleObj)) {
                old_handler = handleObj;
                return new_handler;
            } else {
                old_handler = handleObj.handler;
                handleObj.handler = new_handler;
            }
        }
    };

    function loopy() {
        timeout_id = window[str_setTimeout](function () {
            elems.each(function () {
                var elem = $(this),
                    width = elem.width(),
                    height = elem.height(),
                    data = $.data(this, str_data);
                if (width !== data.w || height !== data.h) {
                    elem.trigger(str_resize, [data.w = width, data.h = height]);
                }
            });
            loopy();
        }, jq_resize[str_delay]);
    }
})(jQuery, this);
/**js计算误差
 * 级别：Public 类型：Math 功能：
 */
$.extend(Math, {
    //除法函数，用来得到精确的除法结果  
    // 说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。  
    //调用：divide(arg1,arg2)  
    //返回值：arg1除以arg2的精确结果  
    divide: function (arg1, arg2) {
        if (arg2 == 0 || isNaN(arg2)) {
            return 0;
        }
        var t1 = 0, t2 = 0, r1, r2;
        try { t1 = arg1.toString().split(".")[1].length } catch (e) { }
        try { t2 = arg2.toString().split(".")[1].length } catch (e) { }
        with (Math) {
            r1 = Number(arg1.toString().replace(".", ""))
            r2 = Number(arg2.toString().replace(".", ""))
            return (r1 / r2) * pow(10, t2 - t1);
        }
    },
    //乘法函数，用来得到精确的乘法结果  
    //说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。  
    //调用：multiply(arg1,arg2)  
    //返回值：arg1乘以 arg2的精确结果 
    multiply: function (arg1, arg2) {
        var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
        try { m += s1.split(".")[1].length } catch (e) { }
        try { m += s2.split(".")[1].length } catch (e) { }
        return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
    },
    //加法函数，用来得到精确的加法结果  
    //说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。  
    //调用：add(arg1,arg2) 
    // 返回值：arg1加上arg2的精确结果  
    add: function (arg1, arg2) {
        var r1, r2, m, c;
        try { r1 = arg1.toString().split(".")[1].length } catch (e) { r1 = 0 }
        try { r2 = arg2.toString().split(".")[1].length } catch (e) { r2 = 0 }
        c = Math.abs(r1 - r2);
        m = Math.pow(10, Math.max(r1, r2))
        if (c > 0) {
            var cm = Math.pow(10, c);
            if (r1 > r2) {
                arg1 = Number(arg1.toString().replace(".", ""));
                arg2 = Number(arg2.toString().replace(".", "")) * cm;
            }
            else {
                arg1 = Number(arg1.toString().replace(".", "")) * cm;
                arg2 = Number(arg2.toString().replace(".", ""));
            }
        }
        else {
            arg1 = Number(arg1.toString().replace(".", ""));
            arg2 = Number(arg2.toString().replace(".", ""));
        }
        return (arg1 + arg2) / m
    },
    //减法函数，用来得到精确的减法结果  
    //说明：javascript的减法结果会有误差，在两个浮点数相减的时候会比较明显。这个函数返回较为精确的减法结果。  
    //调用：subtract(arg1,arg2) 
    // 返回值：arg1减去arg2的精确结果  
    subtract: function (arg1, arg2) {
        var r1, r2, m, n;
        try { r1 = arg1.toString().split(".")[1].length } catch (e) { r1 = 0 }
        try { r2 = arg2.toString().split(".")[1].length } catch (e) { r2 = 0 }
        m = Math.pow(10, Math.max(r1, r2));
        //动态控制精度长度
        n = (r1 >= r2) ? r1 : r2;
        return ((arg1 * m - arg2 * m) / m).toFixed(n);
    },
    //四舍五入函数，用来得到精确的舍五入结果  
    //说明：javascript的舍五入结果会有误差，这个函数返回较为精确的舍五入结果。  
    //调用：fixed(val,precision) 
    // 返回值：对val进行四舍五入保留precision 精度  
    fixed: function (val, precision) {
        var num = Math.pow(10, precision);
        //调用Math 原始的round方法
        return Math.round(val * num) / num;
    }
});
String.prototype.endsWith = String.prototype.endsWith || function (endStr) {
    if (endStr === null) return false;
    if (endStr === "") return true;
    return this.lastIndexOf(endStr) == this.length - endStr.length;
}//IE拓展方法
String.prototype.startsWith = String.prototype.startsWith || function (startStr) {
    if (startStr === null) return false;
    if (startStr === "") return true;
    return this.indexOf(startStr) == 0;
}//IE拓展方法
Number.prototype.toFixedELN = function (len) {
    var result = parseInt(Math.add(Math.multiply(this, Math.pow(10, len)), 0.5), 10) / Math.pow(10, len);
    var s_x = result.toString();
    var pos_decimal = s_x.indexOf('.');
    if (len == 0)
        return s_x;
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + len) {
        s_x += '0';
    }
    return s_x;
}
function MarkConditionRule(comparisionOperator, text, style, ranges) {
    GC.Spread.Sheets.ConditionalFormatting.NormalConditionRule.apply(this, [
        GC.Spread.Sheets.ConditionalFormatting.RuleType.specificTextRule,
        ranges,
        style,
        comparisionOperator,
        null,
        null,
        text
    ]);
    this.name = "MarkConditionRule";
}
MarkConditionRule.prototype = new GC.Spread.Sheets.ConditionalFormatting.NormalConditionRule();
MarkConditionRule.prototype.evaluate = function (sheet, row, col, actual) {
    var style = new GC.Spread.Sheets.Style();
    var bingingPath = sheet.getCell(row, col).bindingPath();
    var tag = sheet.getTag(row, col);
    if (operateType == "0")
        if (bingingPath) {
            style.watermark = '◤';
            style.cellPadding = "1";
            style.labelOptions = {
                foreColor: 'red',
                visibility: 0,
                font: '10px Arial',
                margin: '0px 0px 0px 0px'
            };

        } else if (tag) {
            if ((typeof tag === "string") && (tag.startsWith("重复区域"))) {
                style.watermark = '◤';
                style.cellPadding = "1";
                style.labelOptions = {
                    foreColor: 'red',
                    visibility: 0,
                    font: '10px Arial',
                    margin: '0px 0px 0px 0px'
                };
            }
        } else {
            var tables = sheet.tables.all();
            if (tables) {
                for (var j = 0; j < tables.length; j++) {//j代表的是第几个table
                    if (tables[j].bindingPath()) {
                        var range = tables[j].dataRange();	//z代表的是在这个列表的第几列
                        if (range.row <= row && row < (range.row + range.rowCount))
                            if (range.col <= col && col < (range.col + range.colCount))
                                if (tables[j].getColumnDataField(col - range.col)) {
                                    style.watermark = '◤';
                                    style.cellPadding = "1";
                                    style.labelOptions = {
                                        foreColor: 'red',
                                        visibility: 0,
                                        font: '10px Arial',
                                        margin: '0px 0px 0px 0px'
                                    };
                                }
                    }
                }
            }
        }
    if (operateType == "1") {
        var rowtag = sheet.getTag(row, -1);
        if (rowtag && rowtag.rowuuid && rowtag.changecols && (("," + rowtag.changecols + ",").indexOf("," + col + ",") != -1)) {
            style.watermark = '◥';
            style.cellPadding = "1";
            var range = new GC.Spread.Sheets.Range(row, col, 1, 1);
            var spans = sheet.getSpans(range);
            var width = sheet.getColumnWidth(col);
            if (spans && spans.length == 1) {
                for (var i = 1; i < spans[0].colCount; i++) {
                    width += sheet.getColumnWidth(col + i);
                }
            }
            var marginvalue = '0px 0px 0px ' + (width - 15) + 'px';
            style.labelOptions = {
                foreColor: 'red',
                visibility: 0,
                font: '10px Arial',
                margin: marginvalue
            };
        }
    }
    return style;
}

//扩展单元格类型 开始
//三维签名单元格类型
function SWSignCellType(items, size, isHorizontal) {
    this.typeName = "SWSignCellType";
    this.size = size || 10;
}
SWSignCellType.prototype = new GC.Spread.Sheets.CellTypes.Base();
SWSignCellType.prototype.paint = function (ctx, value, x, y, w, h, style, context) {
    style = style || new GC.Spread.Sheets.Style();//默认格式
    if (value) {
        var signInfo = value.split("@@");
        var newValue = signInfo[1];//日期
        var signPic = signInfo[2];

        style.hAlign = GC.Spread.Sheets.HorizontalAlign.right;
        style.vAlign = GC.Spread.Sheets.VerticalAlign.center;
        style.backgroundImage = signPic;
        style.backgroundImageLayout = GC.Spread.Sheets.ImageLayout.none;
        style.diagonalUp = undefined;

        spreadNS.CellTypes.Base.prototype.paint.apply(this, [ctx, newValue, x, y, w, h, style, context]);
    } else {
        style.diagonalUp = new GC.Spread.Sheets.LineBorder("black", GC.Spread.Sheets.LineStyle.thin);
        spreadNS.CellTypes.Base.prototype.paint.apply(this, [ctx, "", x, y, w, h, style, context]);
    }
};
var swSignCellTypeDialog;
SWSignCellType.prototype.createEditorElement = function () {
    if (swSignCellTypeDialog === undefined) {
        swSignCellTypeDialog = new designer.SWSignCellTypeDialog();
    }
    swSignCellTypeDialog.open();
};
SWSignCellType.prototype.isReservedKey = function (e) {
    //cell type handle tab key by itself
    return (e.keyCode === GC.Spread.Commands.Key.tab && !e.ctrlKey && !e.shiftKey && !e.altKey);
};
SWSignCellType.prototype.isEditingValueChanged = function (oldValue, newValue) {
    return oldValue == newValue;
};

// 自动获取签名单元格类型
function SWAutoSignCellType(items, size, isHorizontal) {
    this.typeName = "SWAutoSignCellType";
    this.size = size || 10;
}
SWAutoSignCellType.prototype = new GC.Spread.Sheets.CellTypes.Base();
SWAutoSignCellType.prototype.paint = function (ctx, value, x, y, w, h, style, context) {
    style = style || new GC.Spread.Sheets.Style();//默认格式
    if (value) {
        var signInfo = value.split("@@");
        var newValue = signInfo[1];//日期
        var signPic = signInfo[2];

        style.hAlign = GC.Spread.Sheets.HorizontalAlign.right;
        style.vAlign = GC.Spread.Sheets.VerticalAlign.center;
        style.backgroundImage = signPic;
        style.backgroundImageLayout = GC.Spread.Sheets.ImageLayout.none;
        style.diagonalUp = undefined;

        spreadNS.CellTypes.Base.prototype.paint.apply(this, [ctx, newValue, x, y, w, h, style, context]);
    } else {
        style.diagonalUp = new GC.Spread.Sheets.LineBorder("black", GC.Spread.Sheets.LineStyle.thin);
        spreadNS.CellTypes.Base.prototype.paint.apply(this, [ctx, "", x, y, w, h, style, context]);
    }
};
var SWAutoSignCellTypeDialog;
SWAutoSignCellType.prototype.createEditorElement = function () {
    /*if (SWAutoSignCellTypeDialog === undefined) {
        SWAutoSignCellTypeDialog = new designer.SWAutoSignCellTypeDialog();
    }
    SWAutoSignCellTypeDialog.open();*/
};
SWAutoSignCellType.prototype.isReservedKey = function (e) {
    //cell type handle tab key by itself
    return (e.keyCode === GC.Spread.Commands.Key.tab && !e.ctrlKey && !e.shiftKey && !e.altKey);
};
SWAutoSignCellType.prototype.isEditingValueChanged = function (oldValue, newValue) {
    return oldValue == newValue;
};
//扩展单元格类型 结束

/**
 * 获取已经注册的字体数量
 */
function getRegisteredFontCount() {
    var num = 0;
    var registeredFonts = GC.Spread.Sheets.PDF.PDFFontsManager._registeredFonts;
    for (var fontName in registeredFonts) {
        var fontConfig = registeredFonts[fontName];
        for(var weightName in fontConfig) {
            if(fontConfig[weightName]) {
               num += 1;
            }
        }
    }
    return num;
}

/**
 * operationType
 * 1：注册字体并上传PDF
 * 2：注册字体并弹出PDF选项（左上角-文件-导出PDF）
 */
function setFont(operationType, tipFlag, saveType) {
    // 开启遮罩
    _root.find('.loading-placeholder').removeClass('hidden');

    const fontTtfs = [
        {
            name: "宋体",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simsun.ttf",
        },
        {
            name: "宋体",
            type: "bold",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simsun-bold.ttf",
        },
        {
            name: "楷体",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simkai.ttf",
        },
        {
            name: "sunway-font",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/gikam/main/fonts/sunway-font.ttf",
        },
        {
            name: "times new roman",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/times.ttf",
        },
        {
            name: "times new roman",
            type: "bold",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/timesbd.ttf",
        },
        {
            name: "黑体",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simhei.ttf",
        },
        {
            name: "仿宋",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simfang.ttf",
        },
        {
            name: "calibri",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/calibri.ttf",
        },
        {
            name: "simli",
            type: "normal",
            serverPath: IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/index/font/simli.ttf",
        }
    ];

    for (let key=0; key<fontTtfs.length; key++) {
        let name = fontTtfs[key].name;
        let type = fontTtfs[key].type;
        let serverPath = fontTtfs[key].serverPath;
        let xhr = new XMLHttpRequest();
        xhr.open('GET', serverPath, true);
        xhr.responseType = 'blob';
        xhr.onload = function (e) {
            if (this.status == 200) {
                let blob = this.response;
                // 将Blob 对象转换成 ArrayBuffer
                let reader = new FileReader();
                reader.onload = function (e) {
                    let fontrrayBuffer = reader.result;
                    let fonts = GC.Spread.Sheets.PDF.PDFFontsManager.getFont(name) || {};
                    fonts[type] = fontrrayBuffer;
                    GC.Spread.Sheets.PDF.PDFFontsManager.registerFont(name, fonts);
                    GC.Spread.Sheets.PDF.PDFFontsManager.fallbackFont = function (font) {
                        var fontInfoArray = font.split(' ');
                        var fontName = fontInfoArray[fontInfoArray.length - 1];
                        if (fontName === 'mtcorsva') {
                            return fonts.normal;
                        }
                    }
                    _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                    if (getRegisteredFontCount() === fontTtfs.length) {
                        checkfontsuccess = 1;
                    }
                    // 这里才算字体注册完毕，开始导出PDF
                    if(operationType === 1 && checkfontsuccess === 1) {
                        designer.wrapper.spread.savePDF(function (blob) {
                            var formData = new FormData();
                            formData.append("file", new File([blob], document.title.replace("ELN:", "") + ".pdf", {type:"application/pdf;charset=utf-8"}));
                            formData.append("name", document.title.replace("ELN:", "") + ".pdf"); // 文件名
                            if (uploadBizId != null) {
                                formData.append("targetId", tableName + "$" + uploadBizId);
                            } else {
                                formData.append("targetId", tableName + "$" + bizId);
                            }
                            formData.append("bizCategory", "document");
                            
                            var request = new XMLHttpRequest();
                            request.open("POST", IFM_CONTEXT + "/core/module/item/files" ,true);
                            request.send(formData);
                            
                            request.onreadystatechange = function () {
                                if (request.status == 200) {
                                    if (tipFlag) {
                                        popPromptMessage(designer.res.sunwayTips.pdfUploadSuccess);
                                    }
                                    if ("saveleave" == saveType) {
                                        window.onbeforeunload = null;
                                        if (_Gikam.getLastModal()) {
                                            _Gikam.getLastModal().close();
                                        }
                                        window.close();
                                        closeWP();
                                    }
                                    _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                                } else {
                                    designer.MessageBox.show(designer.res.sunwayTips.pdfUploadFailed, designer.res.sunwayTips.tipTitle, 3);
                                    _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                                }
                            }
                        }); 
                    }
                    _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                }
                reader.readAsArrayBuffer(blob);
            } else {
                designer.MessageBox.show(designer.res.sunwayTips.loadFontFailed, designer.res.sunwayTips.tipTitle, 3);
                _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                return;
            }
        };
        xhr.send();
    }
}



/**
 * 加载时为字体赋值（只用于导出pdf）
 */
var checkfontsuccess = 0;
function registerServerFont(name, type, serverPath) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', serverPath, true);
    xhr.responseType = 'blob';
    xhr.onload = function (e) {
        if (this.status == 200) {
            var blob = this.response;
            // 将Blob 对象转换成 ArrayBuffer
            var reader = new FileReader();
            reader.onload = function (e) {
                var fontrrayBuffer = reader.result;
                var fonts = GC.Spread.Sheets.PDF.PDFFontsManager.getFont(name) || {};
                fonts[type] = fontrrayBuffer;
                GC.Spread.Sheets.PDF.PDFFontsManager.registerFont(name, fonts);
            }
            reader.readAsArrayBuffer(blob);
            checkfontsuccess++;
        } else {
            // 
        }
    };
    xhr.send();
}

/**
 * 加载eln模板数据（从后台获取）
 */
var _root;//参数传递，只是用一次，用后置空
// 在审计追踪的时候，加载完数据后，如果点击刷新数据，如果加载的时候才有的uuid，刷新数据出来的json是没有uuid。
// 在加载完数据的时候，记录这个json内容。刷新数据的时候，把这个参数请求回去。
// 点击过保存之后，这个字段需要置空，在使用这个参数的时候会判断是否为空。
var jsonformodifyrecord;
var isclickinitialmodel; //要求审计追踪
function loadElnModelData(root) {
    _root = root;
    if (preload == "1") { // 预加载ELN页面进行缓存
        setFont();
    } else { // 预加载时会报错
        
        if(isTest){
            $("#saveleave").remove();
            $("#saveservers").remove();
            $("#modifyrecordli").remove();
            $("#initelnli").remove();
        }
        
        // 隐藏上传PDF按钮
        if(!uploadPdf) {
            $("#uploadpdfli").remove();
        }
        
        var data = {
                'cond.operatetype': operateType,
                "cond.fileid": fileid
        };
        
        var _urlParams = getUrlParams();
        for (var p in _urlParams) {
            data["cond." + p] = _urlParams[p];
        }
        var url = null;
        if (operateType == '0') {
            // 编辑模板
            url = IFM_CONTEXT + "/secure/core/module/eln/eln-tmpls/" + elnTmplId + "/versions/" + versionId;
        } else if (operateType == '1') {
            // 测试模板（预览模板）查询数据URL: {id}/versions/{versionId}/eln-tests
            url = IFM_CONTEXT + "/secure/core/module/eln/eln-tmpls/" + elnTmplId + "/versions/" + versionId + "/eln-tests"
            if (elnTmplId && !isTest) {
                if (!bizId) {
                    // 遮罩开启状态
                    designer.MessageBox.show(designer.res.sunwayTips.missingBizParam, designer.res.sunwayTips.tipTitle, 3); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
                    return;
                }
                // ELN实例
                url = IFM_CONTEXT + "/secure/core/module/eln/elns/tmpls/" + elnTmplId + "/queries"
                if (elnId) {
                    url = IFM_CONTEXT + "/secure/core/module/eln/elns/" + elnId +"/tmpls/" + elnTmplId + "/queries"
                }
            }
        }
        var elnLoadProgress = new ELNLoaderProgress({
            title : designer.res.sunwayTips.maskTitleRequestData
        });
        elnLoadProgress.run();
        $.ajax({
            type: "post",
            data : getJsonWrapper({ f : _urlParams}),
            dataType: "json",
            url: url,
            contentType: 'application/json',
            success: function (result) {
                if ("0" == operateType) {
                    elnDsParams = result.elnParamList;
                }
                if (_root)
                    _root.find('.loading-placeholder').addClass('hidden');
                elnLoadProgress.changeValue(30);
                setTimeout(function() {
                    resultDataManage(result);
                    elnLoadProgress.changeValue(60);
                    
                        setTimeout(function() {
                            
                            
                            if ("1" == operateType && "1" == modifyrecordflag) {
                                jsonformodifyrecord = designer.wrapper.spread.toJSON({
                                    includeBindingSource: true
                                });
                                $('.ribbon-bar').gcuiribbon('setButtonsChecked', {
                                    'modifyrecord': true
                                });
                                modifyrecord($("#modifyrecord").prop("checked"));
                            }
                            /**
                             * 当预览或者查看时隐藏相应的工具栏按钮
                             */
                            $("#protect-sheet").find(".gcui-ribbon-abbrevgroup").remove();
                            designer.wrapper.spread.options.allowContextMenu = true;
                            if ("1" == operateType) {
                                $("#templateli").remove();
                                $("#ClearBindingPathli").remove();
                                $("#eln_formulaTab").remove();
                                $("#eln_dataTab").remove();
                                $("#celltypegroup").remove();
                                $("#stylesgroup").remove();
                                $("#cellsgroup").remove();
                                $("#editinggroup").remove();
                                $("#protect-sheet").remove();
                                $("#unprotect-sheet").remove();
                                $("#lock-cells").remove();
                                $("#unlock-cells").remove();
                                $("#previewdata").remove();
                                $("#special-characters").remove();
                                if ("1" != previewType) {
                                    $("#ribbonColseDiv").hide();
                                }
                                // 移除右键菜单
                                var spread = designer.wrapper.spread;
                                var menuData = spread.contextMenu.menuData;
                                var newMenuData = [];
                                menuData.forEach(function (item) {
                                    if(item){
                                        if(item.name === "designer.sunwayMenus"){
                                            return;
                                        }
                                        newMenuData.push(item);
                                    }
                                });
                                spread.contextMenu.menuData = newMenuData;
                                // 实例参数中包含readonly，则将表格置为只读
                                if (!isTest && (elnId || bizId) && JSON.parse(result.elnParam).readonly === "1") {
                                    var spread = designer.wrapper.spread;
                                    var activeSheet = spread.getActiveSheet();
                                    // activeSheet.options.isProtected = true;
                                    
                                    var option = {
                                            allowSelectLockedCells:false, // 不允许选中
                                            allowSelectUnlockedCells:false,
                                            allowFilter: false,
                                            allowSort: false,
                                            allowResizeRows: false,
                                            allowResizeColumns: false,
                                            allowEditObjects: false,
                                            allowDragInsertRows: false,
                                            allowDragInsertColumns: false,
                                            allowInsertRows: false,
                                            allowInsertColumns: false,
                                            allowDeleteRows: false,
                                            allowDeleteColumns: false
                                    };
                                    activeSheet.options.protectionOptions = option;
                                    activeSheet.options.isProtected = true;
                                    
                                    spread.options.newTabVisible = false; // 增加sheet页按钮不可见
                                    spread.options.tabEditable  = false;//禁用编辑名称
                                    spread.options.allowSheetReorder  = false;//禁用拖拽排序
                                    spread.options.allowContextMenu = false;
                                    // 工具栏
                                    foldButton(true);
                                    
                                    // 切换sheet页时监听事件
                                    spread.bind(spreadNS.Events.ActiveSheetChanged, function (sender, args) {
                                        var activeSheet = spread.getActiveSheet();
                                        activeSheet.options.protectionOptions = option;
                                        activeSheet.options.isProtected = true;
                                    });
                                }
                            } else {
                                $("#modifyrecordli").remove();//修改历史按钮隐藏
                                $("#ribbonColseDiv").hide();
                                $("#initelnli").hide();
                            }
                            $(window).trigger("resize");
                            
                            foldButton();
                            ribbonColse();
                            
                            // 进度条到98%
                            elnLoadProgress.changeValue(98);
                            // 关闭进度条
                            setTimeout(function() {
                                elnLoadProgress.closeProgress();
                            }, 500);
                            /*添加监听*/
                            GC.Spread.Sheets.Designer.wrapper.spread.commandManager().addListener("anyscLicenser",function(){
                                var sheet = GC.Spread.Sheets.Designer.wrapper.spread.getActiveSheet();
                                isPrintLineVisible(sheet.printLineVisible || false);
                            });
                            /*添加监听让切换sheet页时上下角标正常*/
                            GC.Spread.Sheets.Designer.wrapper.spread.bind(spreadNS.Events.ActiveSheetChanged, function (sender, args) {
                                var sheet = GC.Spread.Sheets.Designer.wrapper.spread.getActiveSheet();
                                var rowCount = sheet.getRowCount();
                                var columnCount = sheet.getColumnCount();
                                for (var row = 0; row < rowCount; row++) {
                                    for (var col = 0; col < columnCount; col++) {
                                        var cv = sheet.getValue(row, col);
                                        if (cv && cv.indexOf) {
                                            var suba = '<sub>';
                                            var subb = '</sub>';
                                            var supa = '<sup>';
                                            var supb = '</sup>';
                                            var subai = cv.indexOf(suba);
                                            var subbi = cv.indexOf(subb);
                                            var supai = cv.indexOf(supa);
                                            var supbi = cv.indexOf(supb);
                                            if ((subai != -1 && subai < subbi) || (supai != -1 && supai < supbi)) {
                                                sheet.suspendPaint();
                                                var cell = sheet.getCell(row, col);
                                                cell.cellType(new HTMLCellType());//指定是上下标单元格
                                                sheet.resumePaint();
                                            }
                                        }
                                    }
                                }
                            });
                            var xIndex = 0, yIndex = 0;
                            window.document.addEventListener('paste',function(e) {
                                if ( !(e.clipboardData && e.clipboardData.items) ) {
                                    return;
                                }
                                var checkItem = e.clipboardData.items[0];
                                var clipboardDataLength = e.clipboardData.items.length;
                                if (clipboardDataLength != 1 && checkItem.type != "image/png") {
                                    return;
                                }
                                
                                xIndex = 0;
                                yIndex = 0;
                                var activeSheet = GC.Spread.Sheets.Designer.wrapper.spread.getActiveSheet();
                                var selections = activeSheet.getSelections();
                                var selection = selections[0];
                                for(var i = 0; i < selection.col; i++){
                                    xIndex += activeSheet.getColumnWidth(selection.col + i);
                                }
                                for(var i = 0; i < selection.row; i++){
                                    yIndex += activeSheet.getRowHeight(selection.row + i);
                                }
                                for (var i = 0, len = clipboardDataLength; i < len; i++) {
                                    var item = e.clipboardData.items[i];
                                    if (item.kind === "file") {
                                        var f= item.getAsFile();
                                        var reader=new FileReader()
                                        reader.onload=function (e) {
                                            var newImage = new Image();
                                            newImage.src = e.target.result;
                                            var imgSrc = e.target.result;
                                            // spreadjs api: pictures.add(name,src,x,y,width,height)
                                            try {
                                                activeSheet.pictures.add(guid(), imgSrc, parseInt(xIndex), parseInt(yIndex), newImage.width, newImage.height);
                                            } catch {
                                                console.log("图片粘贴重复");
                                            }
                                        }
                                        reader.readAsDataURL(f)
                                    }
                                }
                            });
                            // 定制监听粘贴事件，解决Excel复制粘贴数值精度丢失问题
                            GC.Spread.Sheets.Designer.wrapper.spread.bind(GC.Spread.Sheets.Events.ClipboardPasted, function (sender, args) {
                                var activeSheet = GC.Spread.Sheets.Designer.wrapper.spread.getActiveSheet();
                                var pasteData = args.pasteData.html;
                                
                                var $trDivHTML = $('<div>').appendTo('body').css('height',0).html(pasteData).find('tr');
                                var copyData = "";
                                $trDivHTML.each(function(i, trItem) {
                                    var rowDataItem = trItem.outerText;
                                    copyData += rowDataItem + "@huan-hang@"
                                })
                                $trDivHTML.remove();

                                var pasteDataArray = copyData.split("@huan-hang@");
                                for (var i = 0; i < args.cellRange.rowCount; i++) {
                                    for (var j = 0; j < args.cellRange.colCount; j++) {
                                        var rowNumArray = pasteDataArray[i];
                                        var colNumArray = rowNumArray.split("\t");
                                        var num = "";
                                        // 当遇到复制上面的行是合并单元格时的修复，当实际复制的列数大于数据数组长度时，取数组最后一个元素，防止数组下标越界
                                        if(j >= colNumArray.length){
                                            num = colNumArray[colNumArray.length - 1].trim();
                                        } else {
                                            num = colNumArray[j].trim();
                                        }
                                        if(!isNaN(num) && num.length > 0){
                                            var x = String(num).indexOf(".") + 1;//得到小数点的位置
                                            var y = String(num).length - x;//小数点的位数
                                            var formatterStr = "0.";
                                            if(String(num).indexOf("+")>=0 || String(num).indexOf("-")>=0){
                                                activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).formatter("@");
                                                activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).value(num);
                                                continue;
                                            }
                                            if(x == 0){
                                                activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).formatter("0");
                                                continue;
                                            } 
                                            
                                            if(y > 0) {
                                                for(var k=0; k < y; k++){
                                                    formatterStr += "0";
                                                }
                                                activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).formatter(formatterStr);
                                                continue;
                                            }
                                        } else if (num.indexOf("\n") >= 0) {
                                            num = num.replace("huan-hang", "");
                                            
                                            activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).value(num);
                                            activeSheet.getCell(args.cellRange.row + i, args.cellRange.col + j).wordWrap(true);
                                        }
                                    }
                                    for (var j = 0; j < args.cellRange.colCount; j++) {
                                        activeSheet.autoFitColumn(args.cellRange.col + j);
                                        if (activeSheet.getColumnWidth(args.cellRange.col + j) <= 62) {
                                            activeSheet.setColumnWidth(args.cellRange.col + j, 62);
                                        }
                                    }
                                    activeSheet.autoFitRow(args.cellRange.row + i);
                                }
                            });

                        },0);
                },0);
            },
            error : function(r) {
                // 遮罩开启状态
                designer.MessageBox.show(JSON.parse(r.responseText).message, designer.res.sunwayTips.tipTitle, 3); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
            }
        });
    }
}

/**
 * 隐藏标题栏
 */
function foldButton(flag) {
    if (flag) {
        var $ribbon = $(".ribbon"), $header = $(".header"), $content = $(".content .fill-spread-content"), $ribbonFold = $(".ribbonFold");
        $("#foldButton").attr("src", IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/ribbon/downward.png");
        ribbonPanelHieght = 109;
        $ribbon.css("overflow", "hidden");
        $ribbon.height($ribbon.height() - ribbonPanelHieght);
        $header.height($header.height() - ribbonPanelHieght);
        $content.css({
            top: $content.offset().top - ribbonPanelHieght
        });
        $("#foldButton").hide();
    } else {
        $("#foldButton").click(function () {
            updateRibbonPanel();
        });
        var isCollapsed = false;
        function updateRibbonPanel() {
            var ribbonPanelHieght = 109;
            var $ribbon = $(".ribbon"), $header = $(".header"), $content = $(".content .fill-spread-content"), $ribbonFold = $(".ribbonFold");
            if (isCollapsed) {
                $("#foldButton").attr("src", IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/ribbon/retract.png");
                ribbonPanelHieght = -109;
                $ribbon.css("overflow", "visible");
            } else {
                $("#foldButton").attr("src", IFM_CONTEXT + "/static/plugin/spread-sheets-11.2.6/designer/ribbon/downward.png");
                ribbonPanelHieght = 109;
                $ribbon.css("overflow", "hidden");
            }
            $ribbon.height($ribbon.height() - ribbonPanelHieght);
            $header.height($header.height() - ribbonPanelHieght);
            $content.css({
                top: $content.offset().top - ribbonPanelHieght
            });
            
            isCollapsed = !isCollapsed;
            designer.wrapper.spread.refresh();
        }
    }
}

/**
 * 加载数据返回时的管理
 */
function resultDataManage(result, datatype) {
    if (result != null) {
        modifyrecordflag = result.modifyrecordflag;
        if (!isEmpty(result.elntitle)) {
            document.title = "ELN:" + result.elntitle;
        }
        if (elnTmplId && !isTest) {
            elnId = result.id;
            if (!isEmpty(result.elnName)) {
                document.title = "ELN:" + result.elnName;
            }
        }
        if (previewType == '1') {
            jsonData = elntext;
        } else if (!isEmpty(result.elnTmplContent)) {
            jsonData = JSON.parse(result.elnTmplContent);
        } else if (!isEmpty(result.elnContent)) {
            jsonData = JSON.parse(pako.ungzip( result.elnContent, { to: 'string' } ));
        }
        if (!jQuery.isEmptyObject(jsonData)) {
            try {
                designer.wrapper.reset();
                designer.wrapper.spread.fromJSON(jsonData);
                actions.currentFilePath = "";
                actions.isFileModified = false;
                updateWindowTitle();
                designer.ribbon.updateRibbonBarStyle();
                designer.wrapper.spreadElement.trigger("FileOpened");
                designer.fileMenu.closeFileScreen();
            } catch (ex) {
                designer.fileMenu.closeFileScreen();
                // spreadjs未初始化成功，一般是由于外网部署没配置spreadjs license，或本地访问外网修改host别名改成了域名
                var activeSheet = designer.wrapper.spread.getActiveSheet();
                if (!activeSheet) {
                    designer.MessageBox.show(designer.res.ribbon.data.initError, designer.res.title, 3 /* error */);
                    return;
                }
            }
        }
        if (!isEmpty(result.dataSource)) {
            designer.ribbon._loadBindingPathData(result.dataSource);
        }
        if (!isEmpty(result.data)) {
            dataSrcObj = JSON.parse(result.data);
            oldDataSrcObj = JSON.parse(result.data);
        }
        var spread = designer.wrapper.spread;
        // 清除单元格选中，对应工具栏tab页不跳转至表格
        spread.getActiveSheet().clearSelection();
        if (!jQuery.isEmptyObject(dataSrcObj)) {
            var source = new GC.Spread.Sheets.Bindings.CellBindingSource(dataSrcObj);

            var sheetCount = spread.getSheetCount();
            var sheet;
            for (var sheetIdx = 0; sheetIdx < sheetCount; sheetIdx++) {
                var formulas = {};
                sheet = spread.getSheet(sheetIdx);
                sheet.suspendCalcService(false);
                sheet.suspendDirty(false);
                
                var sheetTag = sheet.getTag(-1, -1);
                sheet.suspendPaint();

                if (operateType == "1" && sheetTag) {
                    //浮动信息处理
                    if (sheetTag["flowInfo"]) {
                        var hlist = [], vlist = [], extendInfos = sheetTag["flowInfo"];

                        for (var p in extendInfos) {
                            var extendInfo = extendInfos[p];
                            if (!extendInfo) continue;

                            if (extendInfo.type == "0") {//处理纵向扩展
                                vlist.push(extendInfo);
                            } else if (extendInfo.type == "1") {//处理横向扩展
                                hlist.push(extendInfo);
                                var extendData = dataSrcObj["列表数据"][extendInfo["bindingPathPrefix"]];
                                extendInfo.colCount = extendData ? extendData.length : extendInfo.colCount;//总列数
                                //显示浮动表头
                                if (extendInfo.colCount > 0) {
                                    //页面超出浮动列数量时，添加页面列，并留一列空白
                                    if ((extendInfo.col + extendInfo.colCount) >= sheet.getColumnCount()) {
                                        sheet.addColumns(sheet.getColumnCount(), (extendInfo.col + extendInfo.colCount + 1) - sheet.getColumnCount());
                                    }

                                    var refFlowInfo = extendInfo.refFlowInfo ? extendInfos[extendInfo.refFlowInfo] : null;
                                    if (refFlowInfo && refFlowInfo.type == "0") {
                                        refFlowInfo.colCount += extendInfo.colCount;
                                        refFlowInfo.refFlowInfo = extendInfo;
                                    }
                                    for (var i = 0; i < extendInfo.colCount; i++) {
                                        sheet.setValue(extendInfo.row, extendInfo.col + i, extendData[i][extendInfo["dataKey"]]);
                                        try {
                                            var copyRowCount = 1;
                                            if (refFlowInfo && refFlowInfo.rowCount > 0) {
                                                copyRowCount += refFlowInfo.rowCount;
                                            }

                                            sheet.copyTo(extendInfo.row, extendInfo.col, extendInfo.row, extendInfo.col + i, copyRowCount, 1, GC.Spread.Sheets.CopyToOptions.style);
                                        } catch (err) {
                                            console.log("row:" + extendInfo.row + "col:" + (extendInfo.col + i));
                                        }
                                    }
                                }
                            }
                        }
                        //纵向扩展单元格与横向同步
                        for (var i = 0; i < vlist.length; i++) {
                            var extendInfo = extendInfos[p];

                            var bindColumns = [];
                            var table = sheet.tables.add("tableRecords" + i, extendInfo.row, extendInfo.col, extendInfo.rowCount, extendInfo.colCount);
                            table.autoGenerateColumns(false);
                            var dataKey = extendInfo.dataKey;
                            if (dataKey && dataKey.length > 0) {
                                var dataKeys = dataKey.split(",");
                                for (var i = 0; i < dataKeys.length; i++) {
                                    bindColumns.push(createTableColumn(dataKeys[i]));
                                }
                            }
                            for (var i = bindColumns.length; i < extendInfo.colCount; i++) {
                                var name = sheet.getValue(extendInfo.refFlowInfo.row, extendInfo.col + i);
                                bindColumns.push(createTableColumn(name));
                            }

                            table.bindColumns(bindColumns);
                            table.bindingPath("列表数据." + extendInfo.bindingPathPrefix);
                            table.bandRows(false);
                            table.filterButtonVisible(false);
                            sheet.DataAutoCellTypes = false;
                        }
                    }
                }

                var tables = sheet.tables.all();
                // 行转列前端逻辑
                if (tables) {
                    for (var j = 0; j < tables.length; j++) { //j代表的是第几个table
                        var table = tables[j];
                        /**
                         * V14.1.3版本调整
                         * V14做了表格增强的功能，其中表格增强的功能与表格绑定有部分存在冲突，对此做了相关的支持，如果您需要原来的表格绑定自增方式，可以在对应的table对象中设置table.expandBoundRows(true),这样将会按照原来的行自增的方式进行自增。
                         */
                        table.expandBoundRows(true);

                        if (!table.bindingPath()) {
                            continue;
                        }
                        var bindingPath = table.bindingPath();
                        var pathArr = bindingPath.split(".");
                        var dataList = dataSrcObj[pathArr[0]][pathArr[1]];


                        var range = table.dataRange(); //z代表的是在这个列表的第几列
                        var columns = table.Nz;
                        var bColumns = [];
                        var eColumns = [];
                        var allColumns = [];
                        var newColumns = [];
                        var isB = null;
                        for (key in columns) {
                            var c = columns[key];
                            if (c._ps.dataField != "...") {
                                allColumns.push(c._ps.dataField);
                                var o = {};
                                o.name = c._ps.name;
                                o.dataField = c._ps.dataField;
                                if (isB === null) {
                                    bColumns.push(o);
                                } else {
                                    eColumns.push(o);
                                }
                            } else {
                                isB = parseInt(key);
                            }
                        }
                        if (isB == null) {
                            continue;
                        }
                        if (dataList.length > 0) {
                            for (key in dataList[0]) {
                                console.log(key)
                                var x = allColumns.filter(function(elt, i, array) {
                                    return elt == key;
                                });
                                if (!x || x.length == 0) {
                                    var o = {};
                                    o.name = key;
                                    o.dataField = key;
                                    newColumns.push(o)
                                }
                            }

                        }
                        for(var i = 0; i < newColumns.length; i++){
                            bColumns.push(newColumns[i]);
                        }
                        for(var i = 0; i < eColumns.length; i++){
                            bColumns.push(eColumns[i]);
                        }
                        
                        /*IE 不兼容 ES6写法
                         * bColumns.push(...newColumns);
                        bColumns.push(...eColumns);*/
                        var tableName = table.name();
                        sheet.addRows(range.row + range.rowCount, range.rowCount + 1);
                        // 判断如果动态扩展的列数差多了当前sheet页的列数，则增加当前sheet页的列数
                        var lastColumnIndex = range.col + range.colCount - 1 + newColumns.length
                        var activeSheetColumnCount = sheet.getColumnCount();
                        if (lastColumnIndex > activeSheetColumnCount) {
                            sheet.setColumnCount(lastColumnIndex);
                        }

                        kt = sheet.tables.add("tableRecords" + j, range.row + range.rowCount, range.col, range.rowCount + 1, range.colCount - 1 + newColumns.length);
                        var y = bColumns.map(function(elt, i, array) {
                            var tableColumn = new spreadNS.Tables.TableColumn();
                            tableColumn.name(elt.name);
                            tableColumn.dataField(elt.dataField);
                            return tableColumn;
                        });
                        kt.bindColumns(y);
                        kt.bindingPath(bindingPath);
                        kt.bandRows(false);
                        kt.filterButtonVisible(false);
                        kt.autoGenerateColumns(false);
                        kt.DataAutoCellTypes = false;

                        if (isB != 0) {
                            for (var ri = 0; ri <= range.rowCount; ri++) {
                                sheet.copyTo(range.row - 1 + ri, range.col, range.row + range.rowCount + ri, range.col, 1, isB, GC.Spread.Sheets.CopyToOptions.style);
                                sheet.copyTo(range.row - 1 + ri, range.col, range.row + range.rowCount + ri, range.col, 1, isB, GC.Spread.Sheets.CopyToOptions.formula);
                            }
                        }
                        if (isB) {
                            for (var ri = 0; ri <= range.rowCount; ri++) {
                                for (var ci = 0; ci < newColumns.length; ci++) {

                                    sheet.copyTo(range.row - 1 + ri, range.col + isB, range.row + range.rowCount + ri, range.col + isB + ci, 1, 1, GC.Spread.Sheets.CopyToOptions.style);
                                    sheet.copyTo(range.row - 1 + ri, range.col + isB, range.row + range.rowCount + ri, range.col + isB + ci, 1, 1, GC.Spread.Sheets.CopyToOptions.formula);
                                }
                            }
                        }
                        if (isB != columns.length - 1) {
                            for (var ri = 0; ri <= range.rowCount; ri++) {
                                sheet.copyTo(range.row - 1 + ri, range.col + isB + 1, range.row + range.rowCount + ri, range.col + isB + newColumns.length, 1, range.colCount - 1 - isB, GC.Spread.Sheets.CopyToOptions.style);
                                sheet.copyTo(range.row - 1 + ri, range.col + isB + 1, range.row + range.rowCount + ri, range.col + isB + newColumns.length, 1, range.colCount - 1 - isB, GC.Spread.Sheets.CopyToOptions.formula);
                            }
                        }
                        sheet.deleteRows(range.row - 1, range.rowCount + 1)
                        kt.name(tableName);
                        for (var z = 0; z < range.colCount; z++) {
                            var formula = sheet.getFormula(range.row, range.col + z);
                            if (!isEmpty(formula)) {
                                formulas[j + "|" + (range.col + z)] = formula; //参数一：第几个列表；参数二：sheet页第几列
                            }
                        }
                        var spans = sheet.getSpans(range);
                        for (var w = 0; w < spans.length; w++) { //获取当前列表里面的合并信息
                            if (spans[w].row == range.row) {
                                if (spans[w].rowCount > rowtc)
                                    rowtc = spans[w].rowCount;
                            }
                        }

                    }
                }

                //获取绑定列表的公式集
                var rowtc = 1;
                tables = sheet.tables.all();
                if (tables) {
                    for (var j = 0; j < tables.length; j++) {//j代表的是第几个table
                        var range = tables[j].dataRange();	//z代表的是在这个列表的第几列
                        for (var z = 0; z < range.colCount; z++) {
                            var formula = sheet.getFormula(range.row, range.col + z);
                            if (!isEmpty(formula)) {
                            	var changeForula = getNumebrFromString(formula, range.row);
                                if (changeForula) {
                                    formulas[j + "|" + (range.col + z) + "|1"] = changeForula; //参数一：第几个列表；参数二：sheet页第几列
                                } else {
                                    formulas[j + "|" + (range.col + z)] = formula; //参数一：第几个列表；参数二：sheet页第几列
                                }
                            }
                        }
                        var spans = sheet.getSpans(range);
                        for (var w = 0; w < spans.length; w++) {//获取当前列表里面的合并信息
                            if (spans[w].row == range.row) {
                                if (spans[w].rowCount > rowtc)
                                    rowtc = spans[w].rowCount;
                            }
                        }
                    }
                }
                //检查是不是数据源里面没有单元格绑的数据&&这个单元格在模板上写了数据=将这个格子的数据变成空，针对详细数据，表格没问题
                // checkAndDeleteBindingData(sheet, source);
                var lengthtc = null;

                var dropDownData = source.xf["下拉框数据"] != null ? JSON.parse(JSON.stringify(source.xf["下拉框数据"])) : null;
                // 绑定下拉框数据（比如需要反写的详细数据涉及下拉框，就必须先初始化下拉框数据）
                initBindingdropDownData(sheet, dropDownData);
                // 绑定详细、列表数据
                sheet.setDataSource(source);

                if (sheetIdx == 0) {
                    var sheetTag = sheet.getTag(-1, -1);
                    if (typeof sheetTag === "string") {
                        sheetTag = { "textTag": sheetTag };
                    } else if (!sheetTag) {
                        sheetTag = {};
                    }
                    sheetTag["datasource"] = dataSrcObj;
                    
                    if (result.idData) {
                        idData = result.idData;
                        sheetTag.idData = result.idData;
                    }
                    sheet.setTag(-1, -1, sheetTag);
                }
                // 解决：绑定数据源单元格设置自动换行，渲染数据后没有自适应行高
                /*if ("1" == operateType && sheetIdx == 0) {
                    // 获取所有数据源绑定信息
                    makeDataForDatasourceShow();
                    for (var i = 0; i < dataForDatasourceShow.data.length; i++) {
                        var dsItem = dataForDatasourceShow.data[i];
                        var dsSheetItem = dsItem.sheet;
                        if (dsSheetItem) {
                            dsSheetItem.autoFitRow(dsItem.row);
                        }
                    }
                }*/
                //重新赋值公式给绑定的列数据
                if (tables) {
                    for (key in formulas) {
                        var keys = key.split("|");
                        var range = tables[keys[0]].dataRange();
                        // 此方法影响了公式
                        if (keys.length > 2) {
                            sheet.setFormula(range.row, keys[1], formulas[key].replace(/manyFormulaMark/g, 1 + range.row + ""));
                        } else {
                            sheet.setFormula(range.row, keys[1], formulas[key]);
                        }
                    }
                    for (var j = 0; j < tables.length; j++) {
                        copyTableStyle(sheet, tables[j], lengthtc, true);
                        initCellMerge(sheet, tables[j]);
                    }
                }
                sheet.resumeCalcService(true);
                sheet.resumeDirty(true);
                sheet.resumePaint();
            }
            elnresultsids = null;//有数据源的情况下，不需要resultsids

            
        } else {
            var sheetCount = spread.getSheetCount();
            var sheet, ds;
            if (sheetCount > 0) {
                spread.suspendPaint();
                var sheetTag = spread.getSheet(0).getTag(-1, -1);

                if (operateType == "1" && sheetTag) {
                    //数据源处理
                    if (sheetTag.idData) {
                        idData = sheetTag.idData;
                    }
                    if (sheetTag.datasource) {
                        ds = new GC.Spread.Sheets.Bindings.CellBindingSource(sheetTag["datasource"]);

                        // 从ELN实例中取出JSON渲染表格后需要对公式重新处理
                        for (var i = 0; i < sheetCount; i++) {
                            sheet = spread.getSheet(i);
                            sheet.suspendCalcService(false);
                            sheet.suspendDirty(false);
                            
                            tables = sheet.tables.all();
                            var formulas = {};
                            if (tables) {
                                for (var j = 0; j < tables.length; j++) { //j代表的是第几个table
                                    var range = tables[j].dataRange(); //z代表的是在这个列表的第几列
                                    for (var z = 0; z < range.colCount; z++) {
                                        var formula = sheet.getFormula(range.row, range.col + z);
                                        if (!isEmpty(formula)) {
                                            var changeForula = getNumebrFromString(formula, range.row);
                                            if (changeForula) {
                                                formulas[j + "|" + (range.col + z) + "|1"] = changeForula; //参数一：第几个列表；参数二：sheet页第几列
                                            } else {
                                                formulas[j + "|" + (range.col + z)] = formula; //参数一：第几个列表；参数二：sheet页第几列
                                            }
                                        }
                                    }
                                    var spans = sheet.getSpans(range);
                                }
                            }
                            sheet.setDataSource(ds);
                            if (tables) {
                                for (key in formulas) {
                                    var keys = key.split("|");
                                    var range = tables[keys[0]].dataRange();
                                    // 此方法影响了公式
                                    if (keys.length > 2) {
                                        sheet.setFormula(range.row, keys[1], formulas[key].replace(/manyFormulaMark/g, 1 + range.row + ""));
                                    } else {
                                        sheet.setFormula(range.row, keys[1], formulas[key]);
                                    }

                                }
                                for (var j = 0; j < tables.length; j++) {
                                    copyTableStyle(sheet, tables[j], lengthtc, false);
                                }
                            }
                            sheet.resumeCalcService(true);
                            sheet.resumeDirty(true);
                        }
                    }
                }

                spread.resumePaint();
            }

            if (result.elnresultsids)
                elnresultsids = result.elnresultsids;//当没有数据源时，自己拼接数据源，需要resultsids进行反存。
            clearC();
        }
        /**
         * 保护单元格设置关闭
         * */
        /*if ("1" == operateType) {
            var spread = designer.wrapper.spread;
            var sheetCount = spread.getSheetCount();
            var sheet;
            for (var i = 0; i < sheetCount; i++) {
                sheet = spread.getSheet(i);
                sheet.options.isProtected = false;
            }
        }*/
        // 给自动签名单元格赋值
        if ("1" == operateType) {
            var spread = designer.wrapper.spread;
            var sheetCount = spread.getSheetCount();
            var sheet;
            spread.suspendPaint();
            for (var i = 0; i < sheetCount; i++) {
                sheet = spread.getSheet(i);
                var sheetTag = sheet.getTag(-1, -1);
                if (sheetTag) {
                    setAutoSignCellValue(sheet, sheetTag);
                }
            }
            spread.resumePaint();
        }
    }
    // 给数据源单元格做红色三角标记
    /*if ("0" == operateType)
        viewDateSourceMark();
    if ("1" == operateType){
        viewDateSourceMarkForResult();
    }*/
        
    // 单元格默认样式
    locateDefaultStyle();
    
    if (operateType == "1") {
        // 审计跟踪、上下角标渲染
        changeRecords();

        var pagenoarr = new Array();
        if (pageno != "" && pageno != null) {
            var arr = pageno.split(",");
            for (var i = 0; i < arr.length; i++) {
                if (arr[i].indexOf("-") == -1) {
                    if (arr[i] != "")
                        pagenoarr.push(Number(arr[i]));
                } else {
                    if (arr[i] != "-") {
                        var arrmore = arr[i].split("-");
                        var start = Number(arrmore[0]);
                        var end = Number(arrmore[1]);
                        for (var j = start; j <= end; j++) {
                            pagenoarr.push(j);
                        }
                    }
                }
            }
        }

        if (pagenoarr.length != 0) {
            var max = pagenoarr[pagenoarr.length - 1];
            var spread = designer.wrapper.spread;
            var sheetCount = spread.getSheetCount();
            if (max > sheetCount) {
                //return;
            } else {
                var show = "," + pagenoarr.join(",") + ",";
                for (var i = 1; i < sheetCount + 1; i++) {
                    if (show.indexOf("," + i + ",") == -1) {
                        var sheet = spread.getSheet(i - 1);
                        if (sheet != null && sheet != undefined)
                            sheet.visible(false);
                    } else {
                        var sheet = spread.getSheet(i - 1);
                        if (sheet != null && sheet != undefined)
                            sheet.visible(true);
                    }
                }
            }
        }
    }

    // 绑定编辑结束事件，如果编辑内容是存在上下角标则进行修饰后显示
    designer.wrapper.spread.bind(spreadNS.Events.EditEnded, function (e, args) {
        var sheet = designer.wrapper.spread.getActiveSheet();
        var cv = sheet.getValue(args.row, args.col) + "";
        if (cv && !sheet.getFormula(args.row, args.col) && cv.indexOf) {
            var suba = '<sub>';
            var subb = '</sub>';
            var supa = '<sup>';
            var supb = '</sup>';
            var subai = cv.indexOf(suba);
            var subbi = cv.indexOf(subb);
            var supai = cv.indexOf(supa);
            var supbi = cv.indexOf(supb);
            if ((subai != -1 && subai < subbi) || (supai != -1 && supai < supbi)) {
                sheet.suspendPaint();
                var cell = sheet.getCell(args.row, args.col);
                cell.cellType(new HTMLCellType());//指定是上下标单元格
                
                // 判断如果添加上标，则将高度设置为28
                var tempHeight = sheet.getRowHeight(args.row, GC.Spread.Sheets.SheetArea.viewport);
                var tempValue = cell.text();
                if (cornermark === 'sub' || tempValue.indexOf('sub') !== -1) {
                    if (tempHeight < 28) sheet.setRowHeight(args.row, 28);
                } else {
                    sheet.autoFitRow(args.row);
                }
                sheet.resumePaint();
            }
        }

            //将单元格值设置为富文本
        if(!args.editingText){
            return;
        }

        let editingText = args.editingText;
        let richText = [];
        let style = args.sheet.getStyle(args.row,args.col) || new GC.Spread.Sheets.Style();
        let isSetRichText = false;
        for(let i = 0; i < editingText.length; i++){
            let code = editingText[i].charCodeAt();
            style = _Gikam.deepExtend(style);
            if(code >= 57344){
                let fontElement = $("<span></span>");
                fontElement.css("font", style.font || '9pt 宋体' );
                fontElement.css("font-family", 'sunway-font');
                style.font = fontElement.css("font");
                fontElement = null;
                isSetRichText = true;
            }

            richText.push({
                style: style,
                text:  editingText[i]
            });
        }

        if(isSetRichText){
            args.sheet.setValue(args.row, args.col, {richText});
        }
        
    });
    spread.resumePaint();
    
    // ELN渲染完成后改变地址栏地址，供本网页正常刷新使用
    window.history.replaceState('forward',null,window.IFM_CONTEXT.replace(location.origin, '') + '/secure/core/module/eln/elns/page/spreadjs-web-designer' + location.search)
    // 若打开ELN界面保持界面不动，监听鼠标移动事件每过5分钟会请求一次刷新token的请求，核心Gikam的功能
    var _opener = opener || parent; // window.open时 opener 有效，iframe（预览）时 parent 有效
    _opener.Gikam && _opener.Gikam.KeepAlive && _opener.Gikam.KeepAlive.observe(document)
}

/**
 * 去掉所有表格的属性，解决ELN中支持表格上的合并单元格，但导出Excel后不支持表格上合并单元格问题
 * 采用sheet.tables.remove的api方式，但由于SpreadJS V11.2.6版本该api存在数据源数据丢失问题，所以采用remove之前先暂存数据，remove之后再填充数据
 */
function removeTableProperties() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    for (var i = 0; i < sheetCount; i++) {
        var sheet = spread.getSheet(i);
        sheet.suspendCalcService(false);
        sheet.suspendDirty(false);
        
        var tables = sheet.tables.all();
        if (tables) {
            for (var j = 0; j < tables.length; j++) { //j代表的是第几个table
                var tableByDataSource = tables[j];
                var row = tableByDataSource.dataRange().row;
                var col = tableByDataSource.dataRange().col;
                var rowCount = tableByDataSource.dataRange().rowCount;
                var colCount = tableByDataSource.dataRange().colCount;
                console.log(row, col, rowCount, colCount);
                var arr = sheet.getArray(row-1,col,rowCount+1,colCount);
                sheet.tables.remove(tableByDataSource, GC.Spread.Sheets.Tables.TableRemoveOptions.keepData | GC.Spread.Sheets.Tables.TableRemoveOptions.keepStyle)
                sheet.setArray(row-1,col, arr);
            }
        }
    }
}

function initBindingdropDownData(sheet, dropDownData) {
    var bindingPathArray = getBindingPathKeys(sheet),
        info,
        cellType,
        data,
        keys;
    for (var i = 0; i < bindingPathArray.length; i++) {
        info = bindingPathArray[i];
        if (info.type == 0 && info.bindingPath.startsWith("下拉框数据.")
            && (cellType = sheet.getCellType(info.row, info.col))
            && (cellType instanceof spreadNS.CellTypes.ComboBox)) {
            data = dropDownData[info.bindingPath.substr(6)];
            if (data && data.length > 0) {
                cellType.items(data).editorValueType(spreadNS.CellTypes.EditorValueType.value);
                var cellValue = sheet.getValue(info.row, info.col);
                sheet.setBindingPath(info.row, info.col, null);
                if (cellValue && cellValue instanceof Array) {
                    sheet.setValue(info.row, info.col, null);
                }
            }
        }
    }
}

function createTableColumn(dataField, name) {
    var tableColumn = new spreadNS.Tables.TableColumn();
    tableColumn.name(name || dataField);
    tableColumn.dataField(dataField);
    return tableColumn;
}

function initCellMerge(sheet, table) {
    if (!table) return;
    var range = table.dataRange();
    var spans = sheet.getSpans(range);

    //配置合并
    var tag, tagObj, rIdx, cIdx, val, preVal, span, cell, cellColSeting, cellColSetings = {};
    for (var i = 0; i < range.rowCount; i++) {
        for (var j = 0; j < range.colCount; j++) {
            rIdx = range.row + i;
            cIdx = range.col + j;

            span = sheet.getSpans(new GC.Spread.Sheets.Range(rIdx, cIdx, 1, 1))[0];
            if (span && span.col != cIdx) {
                continue;
            }

            cellColSeting = cellColSetings[cIdx];
            if (!cellColSeting) {
                tag = sheet.getTag(rIdx, cIdx);
                if (typeof tag === "string") {
                    if (String(tag).trim().indexOf("{") == 0) {
                        tagObj = JSON.parse(tag);
                        cellColSeting = cellColSetings[cIdx] = tagObj;
                    }
                } else if (typeof tag === "object") {
                    cellColSeting = cellColSetings[cIdx] = tag;
                }
            }

            var colTag = sheet.getTag(-1, cIdx);
            if (colTag && colTag.indexOf("verticalMerger&") == 0) { // 设置后，colTag示例：verticalMerger&3
                /* var mergeCount = colTag.split("&")[1];
                if (mergeCount && parseInt(mergeCount) > 0) {
                    try {
                        span = sheet.getSpans(new GC.Spread.Sheets.Range(rIdx, cIdx, 1, 1))[0];
                        if (span) {
                            sheet.addSpan(span.row, span.col, parseInt(mergeCount), 1, GC.Spread.Sheets.SheetArea.viewport);
                        } else {
                            sheet.addSpan(rIdx, cIdx, parseInt(mergeCount), 1, GC.Spread.Sheets.SheetArea.viewport);
                        }
                    } catch (err) {
                        console.log("err:" + (span ? ("span:" + span.row + " " + span.col) : ("rIdx:" + rIdx + " " + cIdx)));
                    }
                }*/
                // 不指定行数合并行，上下单元格值相同自动合并
                val = sheet.getValue(rIdx, cIdx);//当前单元格值
                preVal = sheet.getValue(rIdx - 1, cIdx);//上一单元格值
                if (val == preVal) {
                    try {
                        span = sheet.getSpans(new GC.Spread.Sheets.Range(rIdx - 1, cIdx, 1, 1))[0];
                        if (span) {
                            sheet.addSpan(span.row, span.col, span.rowCount + 1, span.colCount, GC.Spread.Sheets.SheetArea.viewport);
                        } else {
                            sheet.addSpan(rIdx - 1, cIdx, 2, 1, GC.Spread.Sheets.SheetArea.viewport);
                        }
                    } catch (err) {
                        console.log("err:" + (span ? ("span:" + span.row + " " + span.col) : ("rIdx:" + rIdx + " " + cIdx)));
                    }
                }
            }
        }
    }
}

function getBindingPathKeys(sheet) {
    sheet.suspendPaint();
    var rowCount = sheet.getRowCount();
    var columnCount = sheet.getColumnCount();
    var tables = sheet.tables.all();
    var bindingPathKey;
    var bindingPathArray = [];

    for (var i = 0; i < rowCount; i++) {
        for (var j = 0; j < columnCount; j++) {
            bindingPathKey = sheet.getBindingPath(i, j);
            if (bindingPathKey) {
                bindingPathArray.push({
                    bindingPath: bindingPathKey,
                    type: 0,
                    row: i,
                    col: j
                });
            }
        }
    }

    if (tables && tables.length > 0) {
        for (var i = 0; i < tables.length; i++) {
            bindingPathArray.push({
                type: 1,
                bindingPath: tables[i].bindingPath(),
                range: tables[i].range()
            });
        }
    }

    sheet.resumePaint();
    return bindingPathArray;
}

function lockData() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        sheet.options.isProtected = true;
        var defaultStyle = sheet.getDefaultStyle();
        defaultStyle.locked = false;
        sheet.setDefaultStyle(defaultStyle);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        for (var col = 0; col < columnCount; col++) {
            var colStyle = sheet.getStyle(-1, col);
            if (colStyle && colStyle.locked) {
                colStyle.locked = false;
                sheet.setStyle(-1, col, colStyle);
            }
        }
        for (var row = 0; row < rowCount; row++) {
            var rowStyle = sheet.getStyle(row, -1);
            if (rowStyle && rowStyle.locked) {
                rowStyle.locked = false;
                sheet.setStyle(row, -1, rowStyle);
            }
            for (var col = 0; col < columnCount; col++) {
                var cellStyle = sheet.getStyle(row, col);
                if (cellStyle && cellStyle.locked) {
                    cellStyle.locked = false;
                    sheet.setStyle(row, col, cellStyle);
                }
                var bindingPath = sheet.getBindingPath(row, col);
                if (bindingPath && (bindingPath.indexOf("详细数据") > -1) && (bindingPath.length > 4)) {
                    sheet.getCell(row, col).locked(true);
                }
            }
        }

        var tables = sheet.tables.all();
        if (tables) {
            for (var j = 0; j < tables.length; j++) {
                if (tables[j].bindingPath() && (tables[j].bindingPath().indexOf("列表反写数据") == -1)) {
                    var range = tables[j].dataRange();
                    var spans = sheet.getSpans(range);
                    sheet.getRange(range.row, range.col, range.rowCount, range.colCount).locked(true);
                }
            }
        }
    }
}

/**
 *  检查是不是数据源里面没有单元格绑的数据&&这个单元格在模板上写了数据=将这个格子的数据变成空，针对详细数据，表格没问题
 */
function checkAndDeleteBindingData(sheet, source) {
    var rowCount = sheet.getRowCount();
    var columnCount = sheet.getColumnCount();
    for (var row = 0; row < rowCount; row++) {
        for (var col = 0; col < columnCount; col++) {
            var cell, bingdingPath, value, cstyle;
            if (cstyle = sheet.getStyle(row, col, GC.Spread.Sheets.SheetArea.viewport, true), cstyle != null && cstyle != undefined)
                if (cstyle.watermark)
                    if (cstyle.watermark == '◤') {
                        cstyle.watermark = null;
                        cstyle.cellPadding = null;
                        cstyle.labelOptions = null;
                        sheet.setStyle(row, col, cstyle, GC.Spread.Sheets.SheetArea.viewport)
                    }
            if (cell = sheet.getCell(row, col), cell.value() != null && cell.value() != undefined && cell.value() != "")
                if (bingdingPath = cell.bindingPath(), bingdingPath && bingdingPath.indexOf("详细数据") == 0)
                    try {
                        value = source.xf[bingdingPath];
                        if (value == null || value == undefined || value == "") {
                            try {
                                var bindPaths = bingdingPath.split(".");
                                value = source.xf;
                                for (var _idx = 0; _idx < bindPaths.length; _idx++) {
                                    value = value ? value[bindPaths[_idx]] : null;
                                }
                                //value = eval("source.xf." + bingdingPath)
                            } catch (e) {
                                console.log(e.message);
                            }

                            if (value == null || value == undefined || value == "") {
                                sheet.setValue(row, col, null);
                            }
                        }
                    } catch (err) {
                        console.log(err);
                        sheet.setValue(row, col, null);
                    }
        }
    }
}
/**
 * 上下角标  上"sup" 下"sub"
 */
var spreadNS = GC.Spread.Sheets;
function cornermark(cornermark) {
    var cornermarkstart = "<" + cornermark + ">";
    var cornermarkend = "</" + cornermark + ">";
    var userSelection;
    if (window.getSelection) {//一般浏览器 
        userSelection = window.getSelection();
    }
    else if (document.selection) {//IE浏览器、Opera 
        userSelection = document.selection.createRange();
    }
    var userSelectionx = userSelection.toString();
    // 整个文本
    var text = userSelection.anchorNode.data;
    // 起点
    var anchor = userSelection.anchorOffset;
    // 终点
    var focus = userSelection.focusOffset;//extentOffset
    var length = text.length;
    if (anchor > focus) {
        anchor += focus;
        focus = anchor - focus;
        anchor -= focus;
    }
    if (anchor == focus || length == (focus - anchor)) {
        designer.MessageBox.show(designer.res.sunwayTips.selectCells, designer.res.sunwayTips.tipTitle, 2); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
        return;
    }
    var s1 = text.substring(0, anchor);
    var s2 = text.substring(anchor, focus);
    var s3 = text.substring(focus, length);
    var ss = s1 + cornermarkstart + s2 + cornermarkend + s3;

    var spread = designer.wrapper.spread;
    var activeSheet = spread.getActiveSheet();
    var rowindex = activeSheet.getActiveRowIndex();
    var columnindex = activeSheet.getActiveColumnIndex();
    userSelection.anchorNode.replaceData(anchor, focus - anchor, cornermarkstart + s2 + cornermarkend);
    activeSheet.suspendPaint();
    activeSheet.setValue(rowindex, columnindex, userSelection.anchorNode.parentElement.innerText);
    activeSheet.resumePaint();
    activeSheet.suspendPaint();
    var cell = activeSheet.getCell(rowindex, columnindex);
    cell.cellType(new HTMLCellType());//指定是上下标单元格
    activeSheet.resumePaint();
    // 判断如果添加上标，则将高度设置为25
    var tempHeight = activeSheet.getRowHeight(rowindex, GC.Spread.Sheets.SheetArea.viewport);
    var tempValue = cell.text();
    if (cornermark === 'sub' || tempValue.indexOf('sub') !== -1) {
        if (tempHeight < 28) activeSheet.setRowHeight(rowindex, 28);
    } else {
        activeSheet.autoFitRow(rowindex);
    }
}

function HTMLCellType(items, size, isHorizontal) {
    this.typeName = "HTMLCellType";
    this.allowOverflow = false;//必须要，否则会出现字符重合在一起的情况，当内容多余格子的长度的时候
    this._size = size;
}
HTMLCellType.prototype = new spreadNS.CellTypes.Text();
HTMLCellType.prototype.paint = function (ctx, value, x, y, w, h, style, context) {

    if (value && value.indexOf) {
    var suba = '<sub>';
    var subb = '</sub>';
    var supa = '<sup>';
    var supb = '</sup>';
    var subai = value.indexOf(suba);
    var subbi = value.indexOf(subb);
    var supai = value.indexOf(supa);
    var supbi = value.indexOf(supb);
    if (!((subai != -1 && subai < subbi) || (supai != -1 && supai < supbi))) {
        var sheet = context.sheet || spread.getActiveSheet();
        sheet.suspendPaint();
        var cell = sheet.getCell(context.row, context.col);
        cell.cellType(null);//指定是上下标单元格
        sheet.resumePaint();
        return;
    }} else if (!value || !value.indexOf){
        var sheet = context.sheet || spread.getActiveSheet();
        sheet.suspendPaint();
        var cell = sheet.getCell(context.row, context.col);
        cell.cellType(null);//指定是上下标单元格
        sheet.resumePaint();
        return;
    }
    var bakvalue = value;//处理上下标和数据源绑定一起不显示红点问题
    spreadNS.CellTypes.Text.prototype.paint.apply(this, arguments);
    value = bakvalue;
    if (value == '' || value == null || value === undefined) {
        return;
    }

    // 判断是不是有居中等格式
    var xcstyle = style;
    // 是否左右居中   0靠左 1居中 2靠右
    var hAlign = style.hAlign;
    // 是否上下居中   0靠上 1居中 2靠下
    var vAlign = style.vAlign;

    var wordWrap = style.wordWrap;


    var thisrow = context.row;
    var thiscol = context.col;
    var spread = designer.wrapper.spread;
    var activeSheet = context.sheet || spread.getActiveSheet();
    var thiscell = activeSheet.getCell(thisrow, thiscol);
    var thisfont = thiscell.font();
    var fontvalue = thisfont;
    if (thisfont.indexOf("700 ") != -1)
        fontvalue = thisfont.replace("700 ", "");
    var thiscolor = thiscell.foreColor() || "black";//设置字体颜色

    var newObj = value.toString();
    var showObj = value.toString();

    var lll = 0;


    // 绘制上下标显示
    ctx.save();
    ctx.rect(x, y, w, h);//表示具有特殊位置的矩形，以及其在二维空间中的宽度和高度。
    ctx.clip();
    ctx.fillStyle = thiscell.backColor() || "white";//设置背景颜色
    ctx.fillRect(x, y, w, h);
    ctx.font = style.font;
    ctx.textAlign = "right";//设置打印 - 上下标防止出现错位
    //设置全局参数
    var sblength = x + 2;
    var sbhight = y + 13;

    if (!wordWrap) {



        //计算整个展示效果占据的宽度
        var showwidth = 0;
        //计算整个展示效果占据的高度
        var showhigh = 0;
        sfont = fontvalue.toString();
        var pxindex = sfont.indexOf("px");
        sfont = sfont.substring(0, pxindex);
        if (sfont.indexOf(" ") != -1) {
            var arr = sfont.split(" ");
            sfont = arr[arr.length - 1];
        }
        sfont = Number(sfont);
        showhigh = sfont > 18 ? sfont : 18;
        while (showObj.indexOf("<sup>") != -1 || showObj.indexOf("<sub>") != -1) {
            var supindex = showObj.indexOf("<sup>");
            var subindex = showObj.indexOf("<sub>");
            if (subindex == -1 || (supindex != -1 && supindex < subindex)) {//上标在前
                var sb = showObj.substring(0, supindex);
                var end = showObj.indexOf("</sup>");
                var sup = showObj.substring(supindex + 5, end);
                var se = showObj.substring(end + 6, showObj.length);
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    showwidth += ctx.measureText(sb).width;
                    //ctx.textBaseline = "middle";
                    //ctx.fillText(sb, showwidth, y+13);
                }
                ctx.font = "8pt 宋体";
                showObj = se;
                if (ctx.measureText(sup).width > lll)
                    lll = ctx.measureText(sup).width;
            } else {//下标在前
                var sb = showObj.substring(0, subindex);
                var end = showObj.indexOf("</sub>");
                var sub = showObj.substring(subindex + 5, end);
                var se = showObj.substring(end + 6, showObj.length);
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    showwidth += ctx.measureText(sb).width;
                }
                ctx.font = "8pt 宋体";
                showObj = se;
                if (ctx.measureText(sub).width > lll)
                    lll = ctx.measureText(sub).width;
            }
            //判断一个上标/下标 后面是不是接着正文
            if (showObj.indexOf("<sub>") != 0 && showObj.indexOf("<sup>") != 0) {
                showwidth += lll;
                lll = 0;
            }
            //判断后面是否没有了角标---没有的话--需要自己把它做成正文
            if (!(showObj.indexOf("<sup>") != -1 || showObj.indexOf("<sub>") != -1)) {
                var sb = showObj;
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    showwidth += ctx.measureText(sb).width;
                }
            }
        }

        var ll = 0;

        if (hAlign != undefined && hAlign == 1) {
            sblength += (w - showwidth) / 2;
        } else if (hAlign != undefined && hAlign == 2) {
            sblength += (w - showwidth - 2 - 2);
        }

        if (vAlign != undefined && vAlign == 1) {
            sbhight += (h - showhigh) / 2;
        } else if (vAlign != undefined && vAlign == 2) {
            sbhight += (h - showhigh);
        }

        if (newObj.indexOf("<sup>") != -1 || newObj.indexOf("<sub>") != -1) { } else {
            var sb = newObj;
            if (sb != "") {
                ctx.font = thisfont;
                ctx.fillStyle = thiscolor;
                ctx.textBaseline = "middle";
                ctx.fillText(sb, sblength + ctx.measureText(sb).width, sbhight);
            }
        }
        while (newObj.indexOf("<sup>") != -1 || newObj.indexOf("<sub>") != -1) {
            var supindex = newObj.indexOf("<sup>");
            var subindex = newObj.indexOf("<sub>");
            if (subindex == -1 || (supindex != -1 && supindex < subindex)) {//上标在前
                var sb = newObj.substring(0, supindex);
                var end = newObj.indexOf("</sup>");
                var sup = newObj.substring(supindex + 5, end);
                var se = newObj.substring(end + 6, newObj.length);
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    sblength += ctx.measureText(sb).width;
                    ctx.textBaseline = "middle";
                    ctx.fillText(sb, sblength, sbhight);
                }
                ctx.font = "8pt 宋体";
                ctx.textBaseline = "alphabetic";
                ctx.fillText(sup, sblength + ctx.measureText(sup).width, sbhight);
                newObj = se;
                if (ctx.measureText(sup).width > ll)
                    ll = ctx.measureText(sup).width;
            } else {//下标在前
                var sb = newObj.substring(0, subindex);
                var end = newObj.indexOf("</sub>");
                var sub = newObj.substring(subindex + 5, end);
                var se = newObj.substring(end + 6, newObj.length);
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    sblength += ctx.measureText(sb).width;
                    ctx.textBaseline = "middle";
                    ctx.fillText(sb, sblength, sbhight);
                }
                ctx.font = "8pt 宋体";
                ctx.textBaseline = "top";
                ctx.fillText(sub, sblength + ctx.measureText(sub).width, sbhight);
                newObj = se;
                if (ctx.measureText(sub).width > ll)
                    ll = ctx.measureText(sub).width;
            }
            //判断一个上标/下标 后面是不是接着正文
            if (newObj.indexOf("<sub>") != 0 && newObj.indexOf("<sup>") != 0) {
                sblength += ll;
                ll = 0;
            }
            //判断后面是否没有了角标---没有的话--需要自己把它做成正文
            if (!(newObj.indexOf("<sup>") != -1 || newObj.indexOf("<sub>") != -1)) {
                var sb = newObj;
                if (sb != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    ctx.fillText(sb, sblength + ctx.measureText(sb).width, sbhight);
                }
            }
        }
    } else {

        var showString = showObj;
        //循环
        var beforeString = "";
        var x_use = 0;
        var y_use = 0;
        var has_sub = 0;
        var has_sup = 0;
        var x_offset = 0;
        var y_offset = 0;

        var sub_x = null;//下标占用量//用来比较上下标同时存在时应取x占用量的
        var sup_x = null;//上标占用量//用来比较上下标同时存在时应取x占用量的
        while (showString.length >= 0) {
            if (x_use == 0 && beforeString == "") {
                //if(hAlign!=0||vAlign!=0)
                // 是否左右居中   0靠左 1居中 2靠右
                // 是否上下居中   0靠上 1居中 2靠下
                var datainfo = getAlignmentShowInfo(thisfont, fontvalue, showString, y_use, ctx, value, x, y, w, h, style, context);
                if (datainfo.widthflag)
                    x_offset = datainfo.width;
                else {
                    x_offset = 0;
                }
                if (datainfo.heightflag)
                    y_offset = datainfo.height;
            }
            if (showString.length == 0) {//循环结束前
                if (beforeString != "") {//正文部分//需要显示渲染出来
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    ctx.fillText(beforeString, x_offset + sblength + ctx.measureText(beforeString).width + x_use, y_offset + sbhight + y_use);

                }
                break;//跳出循环
            }
            if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//不是角标
                ctx.font = thisfont;
                ctx.fillStyle = thiscolor;
                ctx.textBaseline = "middle";
                if ((ctx.measureText(beforeString + showString.charAt(0)).width + 2 + 2 + x_use) < w && "\n" != showString.charAt(0)) {
                    beforeString += showString.charAt(0);
                    showString = showString.substring(1, showString.length);
                } else {//换行
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    ctx.fillText(beforeString, x_offset + sblength + ctx.measureText(beforeString).width + x_use, y_offset + sbhight + y_use);
                    // 上下角标行间距调整
                    y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                    x_use = 0;
                    beforeString = "";
                    has_sub = 0;
                    has_sup = 0;
                    if ("\n" == showString.charAt(0)){
                        showString = showString.substring(1, showString.length);

                    }
                }

            } else {//遇到角标
                if (beforeString != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    ctx.fillText(beforeString, x_offset + sblength + ctx.measureText(beforeString).width + x_use, y_offset + sbhight + y_use);
                    //遇到角标
                    x_use += ctx.measureText(beforeString).width;
                    beforeString = "";
                }
                var supindex = showString.indexOf("<sup>");
                var subindex = showString.indexOf("<sub>");
                if ((supindex < subindex || subindex == -1) && supindex != -1) {//上标在前
                    var end = showString.indexOf("</sup>");
                    var sup = showString.substring(supindex + 5, end);
                    showString = showString.substring(end + 6, showString.length);
                    if (has_sup == 0)
                        has_sup = 0.5;
                    ctx.font = "8pt 宋体";
                    ctx.textBaseline = "alphabetic";
                    ctx.fillText(sup, x_offset + sblength + ctx.measureText(sup).width + x_use + 2, y_offset + sbhight + y_use);
                    if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//后面不紧紧跟着角标
                        var x_use_thistime;
                        x_use_thistime = ctx.measureText(sup).width;
                        if (sub_x != null) {
                            if (sub_x > x_use_thistime) {
                                x_use_thistime = sub_x;
                            }
                            sub_x = null;
                        }
                        if ((x_use_thistime + 2 + 2 + x_use) < w)
                            x_use += x_use_thistime + 2;
                        else {//换行
                            y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                            x_use = 0;
                            beforeString = "";//这句代码可省略
                            has_sub = 0;
                            has_sup = 0;
                        }
                    } else {//后面有角标_不换行_不算x占用量
                        sup_x = ctx.measureText(sup).width;
                    }
                } else {//下标在前
                    var end = showString.indexOf("</sub>");
                    var sub = showString.substring(subindex + 5, end);
                    showString = showString.substring(end + 6, showString.length);
                    if (has_sub == 0)
                        has_sub = 0.5;
                    ctx.font = "8pt 宋体";
                    ctx.textBaseline = "top";
                    ctx.fillText(sub, x_offset + sblength + ctx.measureText(sub).width + x_use + 2, y_offset + sbhight + y_use);
                    if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//后面不紧紧跟着角标
                        var x_use_thistime;
                        x_use_thistime = ctx.measureText(sub).width;
                        if (sup_x != null) {
                            if (sup_x > x_use_thistime) {
                                x_use_thistime = sup_x;
                            }
                            sup_x = null;
                        }
                        if ((x_use_thistime + 2 + 2 + x_use) < w)
                            x_use += x_use_thistime + 2;
                        else {//换行
                            y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                            x_use = 0;
                            beforeString = "";//这句代码可省略
                            has_sub = 0;
                            has_sup = 0;
                        }
                    } else {//后面有角标_不换行_不算x占用量
                        sub_x = ctx.measureText(sub).width;
                    }
                }
            }

        }
    }
    ctx.restore();
};

HTMLCellType.prototype.getEditorValue = function (editorContext) {
    return $(editorContext)[0].innerText;
}
HTMLCellType.prototype.getAutoFitHeight = function (value, text, style, zoomFactor, context) {
    var jqueryobj = $("<Canvas id='jx' width='10' height='10'></canvas>");
    var ctx = jqueryobj[0].getContext('2d');
    var xcstyle = style;
    var hAlign = style.hAlign;
    var vAlign = style.vAlign;
    var wordWrap = style.wordWrap;
    var thisrow = context.row;
    var thiscol = context.col;
    var spread = designer.wrapper.spread;
    var activeSheet = context.sheet;
    var thiscell = activeSheet.getCell(thisrow, thiscol);
    var thisfont = thiscell.font();
    var fontvalue = thisfont;
    if (thisfont.indexOf("700 ") != -1)
        fontvalue = thisfont.replace("700 ", "");
    var thiscolor = thiscell.foreColor();//设置字体颜色
    var newObj = value.toString();
    var showObj = value.toString();
    var lll = 0;
    var range = new GC.Spread.Sheets.Range(thisrow, thiscol, 1, 1);
    var spans = activeSheet.getSpans(range);
    var w = activeSheet.getColumnWidth(thiscol);
    var h = activeSheet.getRowHeight(thisrow);
    if (spans && spans.length == 1) {
        for (var i = 1; i < spans[0].colCount; i++) {
            w += activeSheet.getColumnWidth(thiscol + i);
        }
        for (var i = 1; i < spans[0].rowCount; i++) {
            h += activeSheet.getRowHeight(thisrow + i);
        }
    }
    if (!wordWrap) {

        this._size = (3 * (1) + parseInt(fontvalue)) * 96 / 72;
    } else {
        var showString = showObj;
        var beforeString = "";
        var x_use = 0;
        var y_use = 0;
        var has_sub = 0;
        var has_sup = 0;
        var sub_x = null;//下标占用量//用来比较上下标同时存在时应取x占用量的
        var sup_x = null;//上标占用量//用来比较上下标同时存在时应取x占用量的
        while (showString.length >= 0) {
            if (showString.length == 0) {//循环结束前
                if (beforeString != "") {//正文部分//需要显示渲染出来
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                }
                break;//跳出循环
            }
            if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//不是角标
                ctx.font = thisfont;
                ctx.fillStyle = thiscolor;
                ctx.textBaseline = "middle";
                if ((ctx.measureText(beforeString + showString.charAt(0)).width + 2 + 2 + x_use) < w && "\n" != showString.charAt(0)) {
                    beforeString += showString.charAt(0);
                    showString = showString.substring(1, showString.length);
                } else {//换行
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                    x_use = 0;
                    beforeString = "";
                    has_sub = 0;
                    has_sup = 0;
                    if ("\n" == showString.charAt(0)){
                        showString = showString.substring(1, showString.length);

                    }
                }
            } else {//遇到角标
                if (beforeString != "") {
                    ctx.font = thisfont;
                    ctx.fillStyle = thiscolor;
                    ctx.textBaseline = "middle";
                    x_use += ctx.measureText(beforeString).width;
                    beforeString = "";
                }
                var supindex = showString.indexOf("<sup>");
                var subindex = showString.indexOf("<sub>");
                if ((supindex < subindex || subindex == -1) && supindex != -1) {//上标在前
                    var end = showString.indexOf("</sup>");
                    var sup = showString.substring(supindex + 5, end);
                    showString = showString.substring(end + 6, showString.length);
                    if (has_sup == 0)
                        has_sup = 0.5;
                    ctx.font = "8pt 宋体";
                    ctx.textBaseline = "alphabetic";
                    if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//后面不紧紧跟着角标
                        var x_use_thistime;
                        x_use_thistime = ctx.measureText(sup).width;
                        if (sub_x != null) {
                            if (sub_x > x_use_thistime) {
                                x_use_thistime = sub_x;
                            }
                            sub_x = null;
                        }
                        if ((x_use_thistime + 2 + 2 + x_use) < w)
                            x_use += x_use_thistime + 2;
                        else {//换行
                            y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                            x_use = 0;
                            beforeString = "";//这句代码可省略
                            has_sub = 0;
                            has_sup = 0;
                        }
                    } else {//后面有角标_不换行_不算x占用量
                        sup_x = ctx.measureText(sup).width;
                    }
                } else {//下标在前
                    var end = showString.indexOf("</sub>");
                    var sub = showString.substring(subindex + 5, end);
                    showString = showString.substring(end + 6, showString.length);
                    if (has_sub == 0)
                        has_sub = 0.5;
                    ctx.font = "8pt 宋体";
                    ctx.textBaseline = "top";
                    if (showString.indexOf("<sub>") != 0 && showString.indexOf("<sup>") != 0) {//后面不紧紧跟着角标
                        var x_use_thistime;
                        x_use_thistime = ctx.measureText(sub).width;
                        if (sup_x != null) {
                            if (sup_x > x_use_thistime) {
                                x_use_thistime = sup_x;
                            }
                            sup_x = null;
                        }
                        if ((x_use_thistime + 2 + 2 + x_use) < w)
                            x_use += x_use_thistime + 2;
                        else {//换行
                            y_use += (3 * (1) + parseInt(fontvalue)) * 96 / 72;
                            x_use = 0;
                            beforeString = "";//这句代码可省略
                            has_sub = 0;
                            has_sup = 0;
                        }
                    } else {//后面有角标_不换行_不算x占用量
                        sub_x = ctx.measureText(sub).width;
                    }
                }
            }
        }
        this._size = y_use + (3 * (1) + parseInt(fontvalue)) * 96 / 72;
    }
    return this._size;
}

// 提示单元格绑定数据源名称类型
function TipCellType() {
    this.typeName = "TipCellType";
}
TipCellType.prototype = new GC.Spread.Sheets.CellTypes.Text();

TipCellType.prototype.getHitInfo = function (x, y, cellStyle, cellRect, context) {
    return {
        x: x,
        y: y,
        row: context.row,
        col: context.col,
        cellStyle: cellStyle,
        cellRect: cellRect,
        sheetArea: context.sheetArea
    };
}
TipCellType.prototype.processMouseEnter = function (hitinfo) {
    // 模板编辑时才提示数据源名称
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    var bindDataSourceName = activeSheet.getBindingPath(hitinfo.row, hitinfo.col);
    if (operateType == '0' && bindDataSourceName) {
        if (!this._toolTipElement) {
            var div = document.createElement("div");
            $(div).css("position", "absolute")
            .css("border", "1px #C0C0C0 solid")
            .css("box-shadow", "1px 2px 5px rgba(0,0,0,0.4)")
            .css("font", "9pt Arial")
            .css("background", "white")
            .css("padding", 5);
            
            this._toolTipElement = div;
        }
        
        $(this._toolTipElement).text("绑定数据源：" + bindDataSourceName)
        .css("top", hitinfo.y + 150)
        .css("left", hitinfo.x + 15);
        $(this._toolTipElement).hide();
        document.body.insertBefore(this._toolTipElement, null);
        $(this._toolTipElement).show("fast");
    }
};
TipCellType.prototype.processMouseLeave = function (hitinfo) {
    // 模板编辑时才提示数据源名称
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    var bindDataSourceName = activeSheet.getBindingPath(hitinfo.row, hitinfo.col);
    if (this._toolTipElement && operateType == '0' && bindDataSourceName) {
        document.body.removeChild(this._toolTipElement);
        this._toolTipElement = null;
    }
};

/**
 * 保存json对象进数据库
 */
function elnSaveToServers(type) {
    var elnSaveProgress = new ELNLoaderProgress({
        title : designer.res.sunwayTips.maskTitleSave
    });
    elnSaveProgress.run();
    // 开启遮罩
    //_root.find('.loading-placeholder').removeClass('hidden');
    //控制判断是否走完了两个ajax请求，走完了就可以关窗口/提示完成。不使用同步
    var wart = false;

    var spread = designer.wrapper.spread;
    var json = spread.toJSON({
        includeBindingSource: true
    });
    if (elnresultsids) {
        makeSourceData();
    }
    var oldData = oldDataSrcObj;
    oldDataSrcObj = JSON.parse(JSON.stringify(dataSrcObj));//每次保存之后更新原来的对比数据
    if ("1" == operateType ) {
        getDirtyData(intialitionValue);
    }
    var saverequestdata = {
        'elnTmplContent': operateType == "0" ? JSON.stringify(json) : JSON.stringify(spread.getActiveSheet().getDataSource() ? spread.getActiveSheet().getDataSource().xf : JSON.stringify(json)),
        "id": elnId
    };
    // 更新版本表中模板内容
    var requestUrl = IFM_CONTEXT + "/secure/core/module/eln/eln-tmpls/" + versionId + "/versions";
    var requestDataForSave = getJsonWrapper(null, [ "coreElnTmplVersionServiceImpl", [ saverequestdata ]]);
    if (operateType == "1") {
        saverequestdata = {
            'elnContent' : pako.gzip(JSON.stringify(json), {to: "string"}),
            'id' : elnId,
            'dirtyRecords' : JSON.stringify(dirtyrecords),
            'data' : spread.getActiveSheet().getDataSource() ? JSON.stringify(spread.getActiveSheet().getDataSource().xf) : null, // 后台保存至表里
            'idData' : idData, // 反写的必要
        };
        requestDataForSave = getJsonWrapper(null, [ "", [ saverequestdata ]]);
        if (elnTmplId && !isTest) {
            // 更新ELN实例内容
            requestUrl = IFM_CONTEXT + "/secure/core/module/eln/elns/" + elnId + "/action/update";
        }
    }
    elnSaveProgress.changeValue(30);
    $.ajax({
        type: "put",
        data: requestDataForSave,
        dataType: "text",
        url: requestUrl,
        contentType: 'application/json',
        success: function (result) {
            if (result && JSON.parse(result).message == "core.security.login.unauthorized") {
                _root.find('.loading-placeholder').addClass('hidden');
                // 进度条到98%
                // 关闭进度条
                window.setTimeout(function() {
                    elnSaveProgress.closeProgress();
                }, 0);
                designer.MessageBox.show(designer.res.sunwayTips.userLoginTimeOut, designer.res.sunwayTips.tipTitle, 3); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
                return;
            }
            if (elnresultsids || elnresultsidsarray) { 
                //将数据源置空_写法统一_否则后台没有考虑到其他写法
                dataSrcObj = {};
                oldDataSrcObj = {};
            }
            dirtyrecords = {};//清空脏数据
            intialitionValue = null;//初始化/刷新 按钮点击情况清空
            jsonformodifyrecord = null;//置空刷新数据需要的参数
            isclickinitialmodel = null;//置空初始化按钮追踪
            var sheetCount = spread.getSheetCount();
            var sheet;
            // 清除各sheet页脏数据
            for (var i = 0; i < sheetCount; i++) {
                sheet = spread.getSheet(i);
                sheet.clearPendingChanges();
            }
            isChanged = 0;
            if (autoExportPDF == "1" && operateType == "1" && tableName) {
                setFont(1, false, type);
            }
            // 关闭遮罩
            _root.find('.loading-placeholder').addClass('hidden');
            if ("close" == type) {
                window.onbeforeunload = null;
                window.close();
            } else {
                if ("saveleave" == type && autoExportPDF != "1") {
                    window.onbeforeunload = null;
                    if (_Gikam.getLastModal()) {
                        _Gikam.getLastModal().close();
                    }
                    window.close();
                    closeWP();
                } else {
                    setTimeout(function() {
                        // 进度条到98%
                        elnSaveProgress.changeValue(98);
                        // 关闭进度条
                        window.setTimeout(function() {
                            elnSaveProgress.closeProgress();
                        }, 0);
                        popPromptMessage(designer.res.sunwayTips.saveSuccess);
                        wart = false;
                    }, 0);
                }
            }
        },
        error : function(r) {
            // 异常关闭遮罩
            _root.find('.loading-placeholder').addClass('hidden');
            // 进度条到98%
            elnSaveProgress.changeValue(98);
            // 关闭进度条
            window.setTimeout(function() {
                elnSaveProgress.closeProgress();
            }, 0);
            designer.MessageBox.show(JSON.parse(r.responseText).message || designer.res.sunwayTips.saveError, designer.res.sunwayTips.tipTitle, 3); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
        }
    });

}

function elnUploadPdf(){
    if (tableName && bizId) {
        addFontsToFontManager();
        setPrintInfo();

        // 注册字体
        if (checkfontsuccess == 1) { // 当已经注册过了
            designer.wrapper.spread.savePDF(function (blob) {
                var formData = new FormData();
                formData.append("file", new File([blob], document.title.replace("ELN:", "") + ".pdf", {type:"application/pdf;charset=utf-8"}));
                formData.append("name", document.title.replace("ELN:", "") + ".pdf"); // 文件名
                formData.append("targetId", tableName + "$" + bizId);
                formData.append("bizCategory", "document");
                
                var request = new XMLHttpRequest();
                request.open("POST", IFM_CONTEXT + "/core/module/item/files" ,true);
                request.send(formData);
                
                request.onreadystatechange = function () {
                    if (request.readyState == 4 && request.status == 200) {
                        popPromptMessage(designer.res.sunwayTips.pdfUploadSuccess);
                        
                        _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                    } else {
                        designer.MessageBox.show(designer.res.sunwayTips.pdfUploadFailed, designer.res.sunwayTips.tipTitle, 3);
                        _root.find('.loading-placeholder').addClass('hidden'); // 关闭遮罩
                    }
                }
            });
        } else {
            setFont(1, true);
        }
    }
}

// 初始化ELN实例（清空ELN内容，重新加载数据）
function initEln(){
    var saverequestdata = {
            'elnContent' : null,
            'id' : elnId,
        };
        requestDataForSave = getJsonWrapper(null, [ "", [ saverequestdata ]]);
        if (elnTmplId && !isTest) {
            // 更新ELN实例内容
            requestUrl = IFM_CONTEXT + "/secure/core/module/eln/elns/" + elnId + "/action/update";
        }
    
    
    $.ajax({
        type: "put",
        data: requestDataForSave,
        dataType: "text",
        url: requestUrl,
        contentType: 'application/json',
        success: function (result) {
            // 重新加载页面，会根据数据源查询数据后重新渲染页面
            window.location.href = elnUrl;
        },
        error : function(r) {
            // 异常关闭遮罩
            _root.find('.loading-placeholder').addClass('hidden');
            designer.MessageBox.show(JSON.parse(r.responseText).message || designer.res.sunwayTips.initError, designer.res.sunwayTips.tipTitle, 3); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
        }
    });
}

/**
 * 设置导出pdf字体（当前字体没有设置对应字体时执行）
 */
function addFontsToFontManager() { }

//设置导出pdf样式
function setPrintInfo() {
    /*var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var rowStart = -1;
        var rowEnd = -1;
        for (var row = 0; row < rowCount; row++) {
            var tag = sheet.getTag(row, -1);
            if ("打印标题" == tag || (tag && "打印标题" == tag["打印标题"])) {
                if (rowStart == -1) {
                    rowStart = row;
                }
                rowEnd = row;
            }
        }
        var printInfo = sheet.printInfo();
        // printInfo.showGridLine(false);
        // printInfo.orientation(GC.Spread.Sheets.Print.PrintPageOrientation.portrait); // 纵向打印
        // printInfo.orientation(GC.Spread.Sheets.Print.PrintPageOrientation.landscape); // 横向打印
        printInfo.showBorder(false);
        printInfo.showColumnHeader(GC.Spread.Sheets.Print.PrintVisibilityType.hide);
        printInfo.showRowHeader(GC.Spread.Sheets.Print.PrintVisibilityType.hide);
        printInfo.centering(GC.Spread.Sheets.Print.PrintCentering.horizontal);
        printInfo.paperSize(new GC.Spread.Sheets.Print.PaperSize(GC.Spread.Sheets.Print.PaperKind.a4));
        //针对现场导入的模板取消其原打印行数设置
        printInfo.rowEnd(-1);
        printInfo.columnEnd(-1);
        printInfo.margin({ top: 80, bottom: 110, left: 20, right: 20, header: 20, footer: 40 });//页眉页脚
        //针对现场导入的模板取消其页眉页脚
        printInfo.headerLeft("");
        printInfo.headerCenter("");
        printInfo.headerRight("");
        printInfo.footerCenter("");
        printInfo.footerLeft("");
        printInfo.footerRight("");
        if (rowStart > -1) {
            printInfo.repeatRowStart(rowStart);
        }
        if (rowEnd > -1) {
            printInfo.repeatRowEnd(rowEnd);
        }
    }*/
}

function getDirtyData(intialitionValue) {
    if ("initializationElnData" == intialitionValue) {
        var spread = designer.wrapper.spread;
        var sheetCount = spread.getSheetCount();
        var sheet;
        for (var i = 0; i < sheetCount; i++) {
            if (!dirtyrecords[i]) {
                dirtyrecords[i] = {};
            }
            sheet = spread.getSheet(i);
            var rowCount = sheet.getRowCount();
            var columnCount = sheet.getColumnCount();
            for (var row = 0; row < rowCount; row++) {
                var rowtag = sheet.getTag(row, -1);
                var isnewrow = false;
                if (rowtag && rowtag.rowuuid) {
                } else {
                    isnewrow = true;
                    var tagvalue = rowtag;
                    rowtag = {};
                    if (tagvalue)
                        rowtag[tagvalue] = tagvalue;
                    rowtag.rowuuid = guid();
                    sheet.setTag(row, -1, rowtag);
                }
                for (var col = 0; col < columnCount; col++) {
                    if (isnewrow) {
                        if (!colrowvalue[i][rowtag.rowuuid + "," + col]) {
                            colrowvalue[i][rowtag.rowuuid + "," + col] = {};
                        }
                        colrowvalue[i][rowtag.rowuuid + "," + col]["value"] = (sheet.getValue(row, col) == undefined ? "empty" : sheet.getValue(row, col));
                    } else {
                        var thiscellvalue = sheet.getValue(row, col) == undefined ? "empty" : sheet.getValue(row, col);
                        if (thiscellvalue != colrowvalue[i][rowtag.rowuuid + "," + col]["value"]) {
                            if (!dirtyrecords[i][rowtag.rowuuid + "," + col]) {
                                dirtyrecords[i][rowtag.rowuuid + "," + col] = {};
                            }
                            dirtyrecords[i][rowtag.rowuuid + "," + col]["new"] = thiscellvalue;
                            dirtyrecords[i][rowtag.rowuuid + "," + col]["old"] = colrowvalue[i][rowtag.rowuuid + "," + col]["value"];
                            dirtyrecords[i][rowtag.rowuuid + "," + col]["row"] = row;
                            colrowvalue[i][rowtag.rowuuid + "," + col]["value"] = thiscellvalue;
                            if (!(rowtag.changecols && (("," + rowtag.changecols + ",").indexOf("," + col + ",") != -1))) {
                                rowtag.changecols ? (rowtag.changecols += ("," + col)) : (rowtag.changecols = ("" + col));
                                sheet.setTag(row, -1, rowtag);
                            }
                        }
                    }
                }
            }
        }
    } else if (!intialitionValue) {
        var spread = designer.wrapper.spread;
        var sheetCount = spread.getSheetCount();
        var sheet;
        for (var i = 0; i < sheetCount; i++) {
            if (!dirtyrecords[i]) {
                dirtyrecords[i] = {};
            }
            sheet = spread.getSheet(i);
            var cells = sheet.getDirtyCells();
            cells.map(function (item) {
                var col = item.col;
                var row = item.row;
                var rowtag = sheet.getTag(row, -1);
                var oldValue = item.oldValue;
                var newValue = item.newValue;
                if (rowtag && newValue != colrowvalue[i][rowtag.rowuuid + "," + col]["value"]) {
                    if (!dirtyrecords[i][rowtag.rowuuid + "," + col]) {
                        dirtyrecords[i][rowtag.rowuuid + "," + col] = {};
                    }
                    dirtyrecords[i][rowtag.rowuuid + "," + col]["new"] = newValue;
                    dirtyrecords[i][rowtag.rowuuid + "," + col]["old"] = colrowvalue[i][rowtag.rowuuid + "," + col]["value"];
                    dirtyrecords[i][rowtag.rowuuid + "," + col]["row"] = row;
                    colrowvalue[i][rowtag.rowuuid + "," + col]["value"] = newValue;
                    if (!(rowtag.changecols && (("," + rowtag.changecols + ",").indexOf("," + col + ",") != -1))) {
                        rowtag.changecols ? (rowtag.changecols += ("," + col)) : (rowtag.changecols = ("" + col));
                        sheet.setTag(row, -1, rowtag);
                    }
                }
            })
        }
    }
}

/**
 * 预览eln模板页面
 */
var previewParamsSettingDialog;
function previewdata() {
    if (elnDsParams && elnDsParams.length > 0) {
        if (previewParamsSettingDialog === undefined) {
            previewParamsSettingDialog = new designer.SWPreviewParamsSettingDialog();
            $(".ui-dialog").css("width","500px");
            previewParamsSettingDialog.__sw_callback = previewdata_callback;
        }
        previewParamsSettingDialog.__sw_params = elnDsParams;
        previewParamsSettingDialog.open();
    } else {
        previewdata_callback();
    }
}

function previewdata_callback(params) {
    var paramList = [];
    paramList.push("&fileid=" + fileid);
    if (params) {
        for (var p in params) {

            var v = params[p];
            /*var vArr = v.split(",");
            v = "";
            for (var i = 0; i < vArr.length; i++) {
                v += (i == 0 ? vArr[i] : ("','" + vArr[i]));
            }*/

            paramList.push(p + "=" + v);
        }
    }

    modelType = 1;
    $('#myModaldataSoure').modal({
        keyboard : false
        // 点击预览弹窗左右空白区域不关闭弹窗
        //,backdrop: 'static'
    });
    $('#modalbodydataSoure').html(null); // 每一次打开都清除iframe，否则会被完整缓存
    var iframeHtml = '<iframe id="ELNPREVIEW" name="MODAL_DIALOG" frameborder=0 width="100%" height="100%" src="'
        + IFM_CONTEXT
        + '/static/plugin/spread-sheets-11.2.6/designer/index/index.html?operateType=1&previewType=1&isTest=true'
        + paramList.join("&") + '&IFM_CONTEXT=' + IFM_CONTEXT + '&elnTmplId=' + elnTmplId + '&versionId=' + versionId
        + '"/>';
    $('#modalbodydataSoure').append(iframeHtml);
}

/**
 * 预览关闭
 */
function ribbonColse() {
    $("#ribbonColse").click(function () {
        parent.$('#myModaldataSoure').modal('hide');
    });
}

/**
 * 模态框弹出提示信息
 * */
function popPromptMessage(message) {
    designer.MessageBox.show(message, designer.res.sunwayTips.tipTitle, 1); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
}

/**
 * 设置默认字体为宋体
 */
function locateDefaultStyle() {
    var spread = designer.wrapper.spread;
    var sheet = spread.getActiveSheet();
    var defaultStyle = new GC.Spread.Sheets.Style();
    defaultStyle.font = "9pt 宋体,sunway-font";
    defaultStyle.locked = false;
    sheet.setDefaultStyle(defaultStyle);
}

/**
 * 数据表格样式复制（复制第一行的样式）(针对绑定的列表数据)
 */
//需求:在多行表格的情况下并表格的数据条数少于表格一行数据占据的行数的时候，实际出来的行数是少于编写模板的时候的行数的(具体的行数是数据源带出来的数据的条数)
//解决方案:在setDataSource之前判断出需要的数据条数，即当是上面的情况下，我们需要为数据源设置数据。并且将正确数据的情况记录。加载行数的时候使用设置的数据。实际显示数据的时候使用正确的数据
function copyTableStyle(sheet, table, lengthtc, isInit) {//参数三：数组->key：表格信息->value：rowtc->做row隐藏操作
    if (!table.bindingPath())
        return;
    var bindingPath = table.bindingPath(); // 获取表格绑定数据源
    var range = table.dataRange(); // 获取表格范围
    var rowHeight = sheet.getRowHeight(range.row); // 获取表格第一行的行高，用来复制给渲染出来的行样式
    var spans = sheet.getSpans(range); // 表格中合并信息数组
    var spansColumns = [];
    var rowtc = 1; //默认情况下一条数据只占用一行,这里获取最大合并单元格数
    var judgeForMoreRowCopy = false; // 默认不是多行使用一行数据
    if (isInit) { // 初始化：复制样式、公式、合并单元格
        for (var i = 0; i < spans.length; i++) { //获取当前列表里面的合并信息
            var colTag = sheet.getTag(-1, spans[i].col);
            if (colTag && colTag.indexOf("verticalMerger&") == 0) { // 如果是纵向自动扩展的不需要重复复制了
                continue;
            }
            if (spans[i].row == range.row) { // 如果合并信息中的行和表格范围行相同
                var spansColumn = { row: spans[i].row, col: spans[i].col, rowCount: spans[i].rowCount, colCount: spans[i].colCount };
                if (spans[i].rowCount >= rowtc) { // 如果合并的行数大于1
                    rowtc = spans[i].rowCount; // 记录合并行数
                    var dataField = table.getColumnDataField(spans[i].col - range.col);
                    if (dataField != null && dataField != undefined && dataField.trim().length) {
                        judgeForMoreRowCopy = true;
                    }
                }
                spansColumns.push(spansColumn);
                //sheet.removeSpan(spans[i].row, spans[i].col);//取消合并
            }
        }
        var is_humiture = false;//防止刷新数据的时候 两次合并处理
        if (rowtc > 1 && !is_humiture && judgeForMoreRowCopy) {
            var looptimes = lengthtc || range.rowCount;
            sheet.addRows(range.row, rowtc * (looptimes));//插入需要的行-合并行数*数据条数
            for (var i = 1; i <= looptimes; i++) {//复制格式以及复制公式以及设置行高
                for (var key in spansColumns) {
                    var row = spansColumns[key].row;
                    var col = spansColumns[key].col;
                    var colCount = spansColumns[key].colCount;
                    var rowCount = spansColumns[key].rowCount;
                    sheet.addSpan(range.row + rowtc * (i - 1), col, rowCount, colCount);
                }
                sheet.copyTo(range.row + looptimes * rowtc, -1, range.row + rowtc * (i - 1), -1, rowtc,
                        range.colCount, GC.Spread.Sheets.CopyToOptions.style);
                sheet.copyTo(range.row + looptimes * rowtc + (i - 1), -1, range.row + rowtc * (i - 1), -1, 1,
                        range.colCount, GC.Spread.Sheets.CopyToOptions.value);
                sheet.copyTo(range.row + looptimes * rowtc, -1, range.row + rowtc * (i - 1), -1, 1,
                        range.colCount, GC.Spread.Sheets.CopyToOptions.formula);
                //sheet.setRowHeight(range.row + rowtc * (i - 1), rowHeight);
            }
            sheet.deleteRows(range.row + looptimes * rowtc, range.rowCount);
        } else if (rowtc > 1 && !is_humiture && !judgeForMoreRowCopy) {
            for (var i = 1; i < range.rowCount; i++) {
                // Copy Style
                sheet.copyTo(range.row + i - 1, -1, range.row + i, -1, 1,
                        range.colCount, GC.Spread.Sheets.CopyToOptions.style);
                // Copy Formula
                sheet.copyTo(range.row + i - 1, -1, range.row + i, -1, 1,
                        range.colCount, GC.Spread.Sheets.CopyToOptions.formula);
                // 高度设置
                //sheet.setRowHeight(range.row + i, rowHeight);
                // 合并单元格
                for (var key in spansColumns) {
                    var row = spansColumns[key].row;
                    var col = spansColumns[key].col;
                    var colCount = spansColumns[key].colCount;
                    try {//温湿度这里会在刷新数据的时候报错//当有列合并的时候
                        sheet.addSpan(range.row + i, col, 1, colCount);
                    } catch (e) {
                        console.log("合并单元格err" + (range.row + i) + "-" + col + "-" + 1 + "-" + colCount)
                    }
                }
            }
            // 合并单元格
            for (var key in spansColumns) {
                var row = spansColumns[key].row;
                var col = spansColumns[key].col;
                var colCount = spansColumns[key].colCount;
                var rowCount = spansColumns[key].rowCount;
                if (rowCount > 1) {
                    sheet.addSpan(range.row, col, range.rowCount, colCount);
                }
            }
        } else {
            for (var i = 1; i < range.rowCount; i++) {
                // Copy Style
                sheet.copyTo(range.row + i - 1, -1, range.row + i, -1, 1, range.colCount, GC.Spread.Sheets.CopyToOptions.style);
                // Copy Formula
                sheet.copyTo(range.row + i - 1, -1, range.row + i, -1, 1, range.colCount, GC.Spread.Sheets.CopyToOptions.formula);
                // 高度设置
                //sheet.setRowHeight(range.row + i, rowHeight);
                // 合并单元格
                for (var key in spansColumns) {
                    var row = spansColumns[key].row;
                    var col = spansColumns[key].col;
                    var colCount = spansColumns[key].colCount;
                    try {//温湿度这里会在刷新数据的时候报错//当有列合并的时候
                        sheet.addSpan(range.row + i, col, 1, colCount);
                    } catch (e) {
                        console.log("合并单元格err" + (range.row + i) + "-" + col + "-" + 1 + "-" + colCount)
                    }
                }
            }
        }
    } else { // 非初始化：复制公式
        for (var i = 1; i < range.rowCount; i++) {
            // Copy Formula
            sheet.copyTo(range.row + i - 1, -1, range.row + i, -1, 1, range.colCount, GC.Spread.Sheets.CopyToOptions.formula);
        }
    }
    
}


/**
 * 监听选择数据源(预览)模态框关闭
 */
function closeModelListener() {
    $('#myModaldataSoure').on('hide.bs.modal', function () {
        modelType = 0;
        var frame = $('iframe');
        var iframe = frame[0];
        try {
            iframe.src = "about:blank";
            iframe.contentWindow.document.write('');
            iframe.contentWindow.document.clear();
            iframe.parentNode.removeChild(iframe);
        } catch (e) {
        }
        $('#modalbodydataSoure').html("");
    });
}

/**
 * 新页面监听关闭
 */
function newPageClose() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    var length = 0;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var dirtyRows = sheet.getDirtyRows();
        length = length + dirtyRows.length;
    }
    if (length > 0 || isChanged == 1) {
        var userAgent = navigator.userAgent;
        if (userAgent.indexOf("Firefox") > 0) {
            return false;
        } else {
            return "您有未保存的操作，是否确定要关闭此页面？";
        }
    }
}

/**
 * 页面确认关闭时执行
 * */
function onunload_handler() {
    if (topTab) {
        topTab.isOpenEln = '0';
        if(bizId){
            topTab.isOpenElnBizId = '0';
        }
    }
    if (pageObject) {
        var objectArray = pageObject.split(".");
        var page = window.opener;
        for (i = 0; i < objectArray.length; i++) {
            page = page[objectArray[i]];
            if (!page) {
                return;
            }
        }
        page.elnCallBack();
    }
}

/**
 * 获取父页面的json对象
 */
function queryParentSpreadJson() {
    var spread = designer.wrapper.spread;
    var json = spread.toJSON({
        includeBindingSource: false
    });
    return json;
}

/**
 * 获取url参数
 */
function queryUrlValueByName(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(decodeURI(r[2]));
    return null;
}

/**
     * 获取url后面的参数
     * @returns {{}}
     */
function getUrlParams() {
    var url = location.search;
    var params = {};
    if (url.indexOf("?") !== -1) {
        var strs = url.substr(1).split("&");
        for (var i = 0; i < strs.length; i++) {
            params[strs[i].split("=")[0]] = decodeURIComponent(strs[i].split("=")[1]);
        }
    }
    return params;
}

/**
 * 查看字符串是否为空
 */
function isEmpty(obj) {
    if (obj === null)
        return true;
    if (typeof obj === 'undefined') {
        return true;
    }
    if (typeof obj === 'string') {
        if (obj === "") {
            return true;
        }
        var reg = new RegExp("^([ ]+)|([　]+)$");
        return reg.test(obj);
    }
    return false;
}

/**
 * 精确JS乘法计算
 */
function jsPlusMul(arg1, arg2) {
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try {
        m += s1.split(".")[1].length;
    } catch (e) {
    }
    try {
        m += s2.split(".")[1].length;
    } catch (e) {
    }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", ""))
            / Math.pow(10, m);
}

/**
 * 精确JS除法计算
 */
function jsPlusDiv(arg1, arg2) {
    var t1 = 0, t2 = 0, r1, r2;
    try {
        t1 = arg1.toString().split(".")[1].length;
    } catch (e) {
    }
    try {
        t2 = arg2.toString().split(".")[1].length;
    } catch (e) {
    }
    with (Math) {
        r1 = Number(arg1.toString().replace(".", ""));
        r2 = Number(arg2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
}

/**
 * 禁止页面使用F5刷新
 * */
function disableF5(event) {
    if (event) {
        var keyCode = event.keyCode ? event.keyCode
                : event.which ? event.which : event.charCode;
        if (keyCode == 116) {
            event.keyCode = 0;
            event.returnValue = false;
            return false
        }
    }
}

/**
 * 补齐有效位数（针对末尾小数点）
 * */
function supplementValidDigit(value, places) {
    var isnegative = 0;

    if (value < 0) {
        value = -value;
        isnegative = 1;
    }

    var valueStr = String(value);
    var dot = valueStr.indexOf(".");
    var length = valueStr.length;
    if (dot > 0) {
        if (value > 1) {
            if ((length - 1) < places) {
                for (var i = 0; i < (places - length + 1) ; i++) {
                    valueStr += '0';
                }
            }
        } else {
            for (var i = (dot + 1) ; i < length; i++) {
                var v = valueStr.charAt(i);
                if (parseInt(v) > 0) {
                    if (length - i < places) {
                        for (var j = 0; j < (places - length + i) ; j++) {
                            valueStr += '0';
                        }
                    }
                    break;
                }
            }
        }
    } else {
        if (length < places) {
            valueStr += '.';
            for (var i = 0; i < (places - length) ; i++) {
                valueStr += '0';
            }
        }
    }

    if (isnegative == 1) {
        valueStr = "-" + valueStr;
    }
    if (places == 0 && valueStr.length > 0 && valueStr.substring(valueStr.length - 1, valueStr.length) == ".") {
        valueStr = valueStr.substring(0, valueStr.length - 1)
    }
    return valueStr;
}

/**
 * 关闭窗口--当eln页面刷新后不能关闭
 */
function closeWP() {
    var Browser = navigator.appName;
    var indexB = Browser.indexOf('Explorer');

    if (indexB > 0) {
        var indexV = navigator.userAgent.indexOf('MSIE') + 5;
        var Version = navigator.userAgent.substring(indexV, indexV + 1);

        if (Version >= 7) {
            window.open('', '_self', '');
            window.close();
        } else if (Version == 6) {
            window.opener = null;
            window.close();
        } else {
            window.opener = '';
            window.close();
        }

    } else {
        window.close();
    }
}
/**
* 点击数值的时候-将如果是字符串类型的数字 格式化成 数值
*/
function formatnumber(spread) {
    var activeSheet = spread.getActiveSheet();
    var selectedRanges = activeSheet.getSelections();
    for (var i = 0; i < selectedRanges.length; i++) {
        var row = selectedRanges[i].row;
        var col = selectedRanges[i].col;
        var rowc = selectedRanges[i].rowCount;
        var colc = selectedRanges[i].colCount;
        for (var m = 0; m < rowc; m++)
            for (var n = 0; n < colc; n++) {
                var cell = activeSheet.getCell(row + m, col + n);
                var str = activeSheet.getValue(row + m, col + n);
                if (typeof (str) == "number")
                    continue;
                var k = Number(str);
                if (!isNaN(k)) {
                    activeSheet.suspendPaint();
                    activeSheet.setValue(row + m, col + n, k);
                    activeSheet.resumePaint();
                }
            }
    }
}
/**
*elnxc-a 保存pdf 输入文件名称的时候影响单元格值--去掉active状态。
*此方法在  fileMenu.js文件中使用，四次使用，五个按钮
*/
function outputFocus() {
    var spread = designer.wrapper.spread;
    spread.focus(false);
    $("#saveAsFileName").focus();//文件名称输入框
}
/**
 * 
 * @param open
 * @returns
 */
function modifyrecord(open) {
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    if (open) {
        minitDesignModeSliderPanel();
        var sliderPanel = $(".slider-panel");
        sliderPanel.sliderpanel("open", "panel1");
    } else {
        mexitTemplateDesignMode();
    }
    activeSheet.repaint();
}
var k = 0;
function minitDesignModeSliderPanel() {
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    //slider panel
    var sliderPanel = $(".slider-panel");
    sliderPanel.sliderpanel();
    var sliderPanelContainer = sliderPanel.sliderpanel("getContainer", "panel1");
    // header
    var designModePanelHeader = $('.design-mode-panel-header');
    if (designModePanelHeader.length <= 0) {
        var sliderPanelHeader = $("<div>").css({
            "height": "4px",
            "background": "-webkit-gradient(linear, left top, left bottom, color-stop(15%,#E4E5E8), color-stop(85%, #E4E5E8))",
            "color": "rgba(0, 0, 0, 0.6)"
        }).addClass("design-mode-panel-header").appendTo(sliderPanelContainer);

    }
    // content
    var designModePanelContent = $('.design-mode-panel-content');
    treeBindMethod();
    if (designModePanelContent.length <= 0) {
        var sliderPanelContent = $("<div>").css({
            "width": "100%",
            "overflow": "auto",
            "position": "absolute",
            "top": "4px",
            "bottom": "0"
        }).addClass("slider-panel-content design-mode-panel-content").appendTo(sliderPanelContainer);

        var tree = "<div id='cellAtRecord' style='height: 100%;width: 100%;'></div>";
        $(tree).appendTo(sliderPanelContent);
        $(".slider-panel").resize(function () {
            if (changerecordsTree)
                changerecordsTree.refresh();
        });
    }
    ajaxMakeTree(true);


}
function treeBindMethod() {
    designer.wrapper.spread.bind(GC.Spread.Sheets.Events.ActiveSheetChanged, function (sender, args) {
        ajaxMakeTree();
    });
    designer.wrapper.spread.bind(GC.Spread.Sheets.Events.SelectionChanging, function (e, info) {
        ajaxMakeTree();
    })
}
var changerecordsTree = null;
function ajaxMakeTree(needajax) {
    var sliderPandel = $(".slider-panel");
    if (!needajax && !sliderPandel.is(":visible")) { } else
        document.getElementById("cellAtRecord").innerHTML = "";
        $.ajax({
            type: "post",
            data: bootstrap_table_getparams(),
            dataType: "json",
            url: bootstrap_table_geturl(),
            success: function (result) {
                var zNodes = null;
                if (result.rows && result.rows.length) {
                    zNodes = [];
                    result.rows.forEach(function(item) {
                        var node = {}
                        if(item.newValue && item.createdByName){
                            node.newValue = item.newValue;
                            node.createdByName = item.createdByName;
                            node.createdTime = item.createdTime;
                            zNodes.push(node);
                        }
                    })
                    zNodes.push({
                        newValue : result.rows[result.rows.length - 1].oldValue
                    });
                }
                _Gikam.create('layout',{
                    renderTo : document.getElementById("cellAtRecord"),
                    center : {
                        items : [{
                            type : 'elnAuditTrail',
                            id : 'elnAuditTrail',
                            list : zNodes
                        }]
                    }
                });
            }
        });
}
function addDiyDom(treeId, treeNode) {
    var spaceWidth = $(".slider-panel").width() - 50;
    var switchObj = $("#" + treeNode.tId + "_switch"),
    icoObj = $("#" + treeNode.tId + "_ico");
    switchObj.remove();
    icoObj.parent().before(switchObj);
    var spantxt = $("#" + treeNode.tId + "_span").html();
    $("#" + treeNode.tId + "_span").css({
        "white-space": "nowrap",
        "overflow": "hidden",
        "text-overflow": "ellipsis",
        "width": spaceWidth,
        "display": "-moz-inline-box",
        "display": "inline-block"
    })
}
function bootstrap_table_getparams() {
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    var rowtag = activeSheet.getTag(activeSheet.getActiveRowIndex(), -1);
    var rowuuid = "nothisrow";
    if (rowtag && rowtag.rowuuid) {
        rowuuid = rowtag.rowuuid;
    }
    var params = {
        'cond.sheet': designer.wrapper.spread.getActiveSheetIndex(),
        'cond.rowuuidcol': rowuuid + "," + activeSheet.getActiveColumnIndex(),
        'cond.filename': "nothisfile"
    };
    return params;
}
function bootstrap_table_geturl() {
    var activeSheet = designer.wrapper.spread.getActiveSheet();
    var rowtag = activeSheet.getTag(activeSheet.getActiveRowIndex(), -1);
    var rowuuid = "nothisrow";
    if (rowtag && rowtag.rowuuid) {
        rowuuid = rowtag.rowuuid;
    }
    var params = {
            'cond.sheet': designer.wrapper.spread.getActiveSheetIndex(),
            'cond.rowuuidcol': rowuuid + "," + activeSheet.getActiveColumnIndex(),
            'cond.filename': "nothisfile"
    };
    // /{elnId}/{sheet}/{colUuidCol}/eln-at-records/queries/raw
    return IFM_CONTEXT + '/secure/core/module/eln/elns/' + elnId + '/' + designer.wrapper.spread.getActiveSheetIndex() + '/' + params['cond.rowuuidcol'] + '/eln-at-records/queries/raw';
}
function mexitTemplateDesignMode() {
    var sliderPandel = $(".slider-panel");
    if (sliderPandel.is(":visible")) {
        sliderPandel.sliderpanel("close", "panel1");
    }
    designer.wrapper.spread.unbind(GC.Spread.Sheets.Events.ActiveSheetChanged);
    designer.wrapper.spread.unbind(GC.Spread.Sheets.Events.SelectionChanging);
}
function getChangedCellData(dirtyItem) {
    return ["Cell (", dirtyItem.row, ",", dirtyItem.col, ") changed from ", dirtyItem.oldValue, " to ", dirtyItem.newValue].join("");
}
function getDirtyCellData(dirtyItem) {
    return ["Cell (", dirtyItem.row, ",", dirtyItem.col, ") oldValue: ", dirtyItem.oldValue, " newValue: ", dirtyItem.newValue].join("");
}

var dirtyrecords = {};
var colrowvalue = {};
var intialitionValue = null;//是否按过初始化按钮
function changeRecords() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        if (!colrowvalue[i]) {
            colrowvalue[i] = {};
        }
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        for (var row = 0; row < rowCount; row++) {
            var rowtag = sheet.getTag(row, -1);
            if (rowtag && rowtag.rowuuid) {

            } else {
                var tagvalue = rowtag;
                rowtag = {};
                if (tagvalue)
                    rowtag[tagvalue] = tagvalue;
                rowtag.rowuuid = guid();
                sheet.setTag(row, -1, rowtag);
            }
            for (var col = 0; col < columnCount; col++) {
                if (!colrowvalue[i][rowtag.rowuuid + "," + col]) {//去掉row在命名中的作用
                    colrowvalue[i][rowtag.rowuuid + "," + col] = {};
                }
                colrowvalue[i][rowtag.rowuuid + "," + col]["value"] = (sheet.getValue(row, col) == undefined ? "empty" : sheet.getValue(row, col));
                var cv = sheet.getValue(row, col);
                if (cv && cv.indexOf) {
                    var suba = '<sub>';
                    var subb = '</sub>';
                    var supa = '<sup>';
                    var supb = '</sup>';
                    var subai = cv.indexOf(suba);
                    var subbi = cv.indexOf(subb);
                    var supai = cv.indexOf(supa);
                    var supbi = cv.indexOf(supb);
                    if ((subai != -1 && subai < subbi) || (supai != -1 && supai < supbi)) {
                        sheet.suspendPaint();
                        var cell = sheet.getCell(row, col);
                        cell.cellType(new HTMLCellType());//指定是上下标单元格
                        sheet.resumePaint();
                    }
                }
            }
        }
    }
}
function viewDateSourceMark() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        var style = new GC.Spread.Sheets.Style();
        var ranges = [new GC.Spread.Sheets.Range(0, 0, rowCount, columnCount)];
        sheet.conditionalFormats.addRule(new MarkConditionRule(
                null, null, style, ranges));
    }
    spread.bind(GC.Spread.Sheets.Events.ActiveSheetChanged, function (event, data) {
        var haveRule = false;
        var ruletest = data.newSheet.conditionalFormats.getRules().forEach(
                function (currentValue, index, arr) {
                    if (currentValue.name == 'MarkConditionRule')
                        haveRule = true;
                });
        if (!haveRule) {
            var rowCount = data.newSheet.getRowCount();
            var columnCount = data.newSheet.getColumnCount();
            var style = new GC.Spread.Sheets.Style();
            var ranges = [new GC.Spread.Sheets.Range(0, 0, rowCount, columnCount)];
            data.newSheet.conditionalFormats.addRule(new MarkConditionRule(
                    null, null, style, ranges));
        }
    });
}
function viewDateSourceMarkForResult() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        var style = new GC.Spread.Sheets.Style();
        var ranges = [new GC.Spread.Sheets.Range(0, 0, rowCount, columnCount)];
        sheet.conditionalFormats.addRule(new MarkConditionRule(
                null, null, style, ranges));
    }
}
function clearC() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        for (var row = 0; row < rowCount; row++) {
            for (var col = 0; col < columnCount; col++) {

                var cstyle = sheet.getStyle(row, col, GC.Spread.Sheets.SheetArea.viewport, true);
                if (cstyle)
                    if (cstyle.watermark)
                        if (cstyle.watermark == '◤') {
                            cstyle.watermark = null;
                            cstyle.cellPadding = null;
                            cstyle.labelOptions = null;
                            sheet.setStyle(row, col, cstyle, GC.Spread.Sheets.SheetArea.viewport)
                        }
            }
        }
    }
}
function guid() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (new Date().getTime() + "-" + S4() + S4());
}
function makeSourceData() {
    var dataObj = {};
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        for (var row = 0; row < rowCount; row++) {
            for (var col = 0; col < columnCount; col++) {
                var bindingPath = sheet.getBindingPath(row, col);
                if (bindingPath && bindingPath.indexOf("详细反写数据.单结果反存") == 0 && bindingPath.length > 13) {
                    var bindingKey = bindingPath.substring(13);
                    if (!dataObj["详细反写数据"])
                        dataObj["详细反写数据"] = {};
                    if (!dataObj["详细反写数据"]["单结果反存"])
                        dataObj["详细反写数据"]["单结果反存"] = {};
                    var value = sheet.getValue(row, col);
                    dataObj["详细反写数据"]["单结果反存"][bindingKey] = (value != undefined && value != null) ? value : "";
                }
            }
        }
        // 列表反存
        var tables = sheet.tables.all();
        if (tables) {
            for (var j = 0; j < tables.length; j++) {//j代表的是第几个table
                if (tables[j].bindingPath() && tables[j].bindingPath() === '列表反写数据.多结果列表反存') {
                    var range = tables[j].dataRange();  //z代表的是在这个列表的第几列
                    var spans = sheet.getSpans(range);
                    var rowtc = 1;//默认情况下一条数据只占用一行,这里获取最大合并单元格数
                    for (var w = 0; w < spans.length; w++) {//获取当前列表里面的合并信息
                        if (spans[w].row == range.row) {
                            if (spans[w].rowCount > rowtc)
                                rowtc = spans[w].rowCount;
                        }
                        if (spans[w].rowCount == 2) {
                            var value1 = sheet.getValue(range.row, spans[w].col);
                            var value2 = sheet.getValue(range.row + 1, spans[w].col);
                            if (value1 && value2 && value1 == value2) {
                                rowtc = 1;
                                break;
                            }
                        }
                    }
                    //将不需要进行多结果反存的列表去掉
                    for (var rowi = 0; rowi < range.rowCount; rowi += rowtc) {
                        var colObj = null;
                        for (var coli = 0; coli < range.colCount; coli++) {
                            var bindingField = tables[j].getColumnDataField(coli);
                            if (!colObj)
                                colObj = {};
                            var value = sheet.getValue(range.row + rowi, range.col + coli)
                            colObj[bindingField] = (value != undefined && value != null) ? value : "";
                        }
                        if (colObj) {
                            if (!dataObj["列表反写数据"])
                                dataObj["列表反写数据"] = {};
                            if (!dataObj["列表反写数据"]["多结果列表反存"])
                                dataObj["列表反写数据"]["多结果列表反存"] = new Array();
                            var arrayLength = dataObj["列表反写数据"]["多结果列表反存"].length;
                            //去掉加1的条件，此处会出现很多为null的
                            dataObj["列表反写数据"]["多结果列表反存"][arrayLength] = colObj;
                        }
                    }
                }
            }
        }
    }
    if (!((dataObj["列表反写数据"] && dataObj["详细反写数据"]) || (!dataObj["列表反写数据"] && !dataObj["详细反写数据"])) && (elnresultsids instanceof Array)) {
        var oldDataObj = {};
        if (dataObj["列表反写数据"]) {
            //若eln中存在多条多结果反存，新增对此处传值的增加处理elnresultsids.length
            for (var index = 0; index < dataObj["列表反写数据"]["多结果列表反存"].length; index++) {
                if (!oldDataObj["列表反写数据"])
                    oldDataObj["列表反写数据"] = {};
                if (!oldDataObj["列表反写数据"]["多结果列表反存"])
                    oldDataObj["列表反写数据"]["多结果列表反存"] = new Array();
                var arrayLength = oldDataObj["列表反写数据"]["多结果列表反存"].length;
                var colObj = {};
                colObj.resultsid = elnresultsids[index % elnresultsids.length];
                oldDataObj["列表反写数据"]["多结果列表反存"][arrayLength] = colObj;
            }
            oldDataSrcObj = oldDataObj;
        } else {//详细反存也需要olddata
            if (!oldDataObj["详细反写数据"])
                oldDataObj["详细反写数据"] = {};
            if (!oldDataObj["详细反写数据"]["单结果反存"])
                oldDataObj["详细反写数据"]["单结果反存"] = {};
            oldDataObj["详细反写数据"]["单结果反存"]["resultsid"] = elnresultsids[0];
            oldDataSrcObj = oldDataObj;
        }
        dataSrcObj = dataObj;
    }
}

var dataForDatasourceShow;
function eln_DatasourceShow(obj) {
    if (!obj)
        makeDataForDatasourceShow();
    window.open(IFM_CONTEXT + "/secure/core/module/eln/elns/page/bind-data-source");
}

function makeDataForDatasourceShow() {
    dataForDatasourceShow = {};
    dataForDatasourceShow.data = [];
    dataForDatasourceShow.type = null;
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var sheetname = sheet.name();
        var rowCount = sheet.getRowCount();
        var columnCount = sheet.getColumnCount();
        for (var row = 0; row < rowCount; row++) {
            for (var col = 0; col < columnCount; col++) {
                var bindingPath = sheet.getBindingPath(row, col);
                var tag = sheet.getTag(row, col);
                var dataobj = null;
                if (tag && (tag.startsWith("结果") || tag.startsWith("结论") || tag.startsWith("标准规定"))) {
                    dataobj = setDataobj(dataForDatasourceShow.data.length, dataobj, sheetname, row, col, tag, "标签反存", null, null, sheet);
                }
                if (bindingPath) {
                    if (bindingPath.indexOf("详细反写数据.单结果反存") == 0)
                        dataobj = setDataobj(dataForDatasourceShow.data.length, dataobj, sheetname, row, col, bindingPath, "详细反存", null, null, sheet);
                    else
                        dataobj = setDataobj(dataForDatasourceShow.data.length, dataobj, sheetname, row, col, bindingPath, "", null, null, sheet);
                }
                if (dataobj) {
                    if (!dataobj.dataclass)
                        dataForDatasourceShow.data.push(dataobj);
                    else
                        dataForDatasourceShow.data.splice(0, 0, dataobj);
                }
            }
        }
        // 列表反存
        var tables = sheet.tables.all();
        if (tables) {
            for (var j = 0; j < tables.length; j++) {//j代表的是第几个table
                var dataobj = null;
                var bindingPath = tables[j].bindingPath();
                var range = tables[j].dataRange();
                if (bindingPath && bindingPath.indexOf('列表反写数据.多结果列表反存') != -1) {
                    dataobj = setDataobj(dataForDatasourceShow.data.length, dataobj, sheetname, range.row, range.col, bindingPath, "列表反存", range.rowCount, range.colCount, sheet);
                } else {
                    dataobj = setDataobj(dataForDatasourceShow.data.length, dataobj, sheetname, range.row, range.col, bindingPath, "", range.rowCount, range.colCount, sheet);
                }
                if (dataobj) {
                    if (!dataobj.dataclass)
                        dataForDatasourceShow.data.push(dataobj);
                    else
                        dataForDatasourceShow.data.splice(0, 0, dataobj);
                }
            }
        }
    }
}

function setDataobj(gridDataIndex, dataobj, sheetname, row, col, datasourcename, dataclass, rowCount, colCount, sheet) {
    if (!dataobj) {
        dataobj = {};
        dataobj["sheetname"] = sheetname;
        dataobj["address"] = GC.Spread.Sheets.CalcEngine.rangeToFormula(new GC.Spread.Sheets.Range(row, col, 1, 1), -1, -1, 15, false);
        if (rowCount && colCount) {
            dataobj["address"] += ("开始，往下" + rowCount + "行 ，往右" + colCount + "列");
        }
        dataobj["datasourcename"] = datasourcename;
        dataobj["dataclass"] = dataclass;
    } else {
        dataobj["datasourcename"] += "," + datasourcename;
        dataobj["dataclass"] += "," + dataclass;
    }
    dataobj["row"] = row;
    dataobj["index"] = gridDataIndex;
    dataobj["id"] = "";
    dataobj["sheet"] = sheet;
    if (dataclass) {
        if (dataForDatasourceShow.type) {
            if (dataForDatasourceShow.type.indexOf(dataclass) == -1)
                dataForDatasourceShow.type += "," + dataclass;
        } else {
            dataForDatasourceShow.type = dataclass;
        }
    }
    return dataobj;
}

function _getTableName() {
    var spread = designer.wrapper.spread;
    var sheets = spread.sheets, count = sheets.length;
    for (var i = 0; i < 100000; i++) {
        var name = "gcTable" + i;
        for (var j = 0; j < count; j++) {
            var sheet = sheets[j];
            var table = sheet.tables.findByName(name);
            if (table) {
                break;
            }
            if (j === count - 1) {
                return name;
            }
        }
    }
};

function getAlignmentShowInfo(thisfont, fontvalue, showString, y_use, ctx, value, x, y, w, h, style, context) {
    var normal = "";
    var small = "";
    var backinfo = {};
    var parm = [];
    var times = 0;
    if (y_use == 0) {
        backinfo.height = HTMLCellType.prototype.getAutoFitHeight(value, value, style, null, context);
        backinfo.heightflag = true;
    }
    while (true) {
        var supindex = showString.indexOf("<sup>");
        var subindex = showString.indexOf("<sub>");
        if (supindex == -1 && subindex == -1) {
            normal += showString;
            break;
        }
        times++;
        var supindex = showString.indexOf("<sup>");
        var subindex = showString.indexOf("<sub>");
        var sb,
            suporsub,
            se;
        if (subindex == -1 || (supindex != -1 && supindex < subindex)) { //上标在前
            sb = showString.substring(0, supindex);
            var end = showString.indexOf("</sup>");
            suporsub = showString.substring(supindex + 5, end);
            se = showString.substring(end + 6, showString.length);
        } else {
            sb = showString.substring(0, subindex);
            var end = showString.indexOf("</sub>");
            suporsub = showString.substring(subindex + 5, end);
            se = showString.substring(end + 6, showString.length);
        }
        showString = se;
        normal += sb;
        small += suporsub;
    }
    ctx.font = style.font;
    var showWidth = ctx.measureText(normal).width;
    ctx.font = "8pt 宋体";
    showWidth += (times * 4 + ctx.measureText(small).width);
    var hAlign = style.hAlign;
    var vAlign = style.vAlign;
    if (w > showWidth) { //此行是最后一行
        if (hAlign != undefined && hAlign == 1) {
            backinfo.width = (w - showWidth) / 2;
        } else if (hAlign != undefined && hAlign == 2) {
            backinfo.width = (w - showWidth - 2 - 2);
        } else {
            backinfo.width = 0;
        }
        backinfo.widthflag = true;
    }
    if (vAlign != undefined && vAlign == 1) {
        backinfo.height = (h - backinfo.height) / 2;
    } else if (vAlign != undefined && vAlign == 2) {
        backinfo.height = (h - backinfo.height);
    } else {
        backinfo.height = 0;
    }
    return backinfo;
}

GC.Spread.Sheets.CellTypes.ComboBox.prototype.paintValue = function (ctx, value, x, y, w, h, style, options) {
    var DefaultDropDownButtonWidth = 17, DefaultMaxVisibleItemCount = 20;
    var self = this, btnWidth = DefaultDropDownButtonWidth, txtWidth = Math.max(0, w - btnWidth - 1);

    if (style.hAlign === 3) {
        style.hAlign = 0;
    }

    if (txtWidth > 0 && h > 0) {
        GC.Spread.Sheets.CellTypes.Base.prototype.paintValue.call(self, ctx, self.getText(value, options), x, y, txtWidth, h, style, options);
    }
    ctx.save();
    if (btnWidth > w || btnWidth > h) {
        ctx.rect(x, y, w, h);
        ctx.clip();
    }
    ctx.beginPath();
    ctx.lineWidth = 2;
    ctx.fillStyle = 'black';
    ctx.moveTo(x + w - btnWidth + 3, y + (h - 2) / 2 - 2.5);
    ctx.lineTo(x + w - btnWidth + 6, y + (h - 2) / 2 + 3.5);
    ctx.lineTo(x + w - btnWidth + 9, y + (h - 2) / 2 - 2.5);
    ctx.fill();
    ctx.restore();
}
GC.Spread.Sheets.CellTypes.ComboBox.prototype.getAutoFitHeight = function (value, text, cellStyle, zoomFactor, context) {
    var spread = designer.wrapper.spread;
    return GC.Spread.Sheets.CellTypes.Context.ep(value, text, cellStyle, zoomFactor, context) + 11.363636363636365;
}

function getJsonWrapper() {
    var args = Array.prototype.slice.apply(arguments);
    var JsonWrapper = function() {
        var _this = this;
        args.forEach(function(item, i) {
            if (i === 0) {
                item ? _this.p = item : null;
            } else {
                _this['b' + (i === 1 ? '' : (i - 1))] = item;
            }
        });
    };
    return JSON.stringify(new JsonWrapper());
}

/**
 * 添加修约规则
 */
function setCusFunc() {
    var EpaFunction = function () {
        this.name = "EPA";
        this.minArgs = 1;
        this.maxArgs = 2;
    };
    EpaFunction.prototype = new GC.Spread.CalcEngine.Functions.Function();
    EpaFunction.prototype.description = function () {
        return {
            description: "科学记数法保留value为指定位数有效数字，去掉的位数四舍五入",
            parameters: [{
                name: "value",
                repeatable: false,
                optional: false
            }, {
                name: "places",
                repeatable: false,
                optional: false
            }]
        }
    }
    EpaFunction.prototype.isContextSensitive = function () {
        return true;
    }
    EpaFunction.prototype.evaluate = function (context, value, places) {
        var context = arguments[0];
        var num;
        if (!isNaN(parseInt(value)) && !isNaN(parseInt(places))) {
            var viewport = designer.wrapper.spread.getActiveSheet().getFormatter(context.row, context.column, GC.Spread.Sheets.SheetArea.viewport);
            if (isEmpty(viewport)) {
                designer.wrapper.spread.getActiveSheet().setFormatter(context.row, context.column, "@", GC.Spread.Sheets.SheetArea.viewport);
            }
            value = NP.strip(parseFloat(value));
            if (value < 0) {
                num = -value;
            } else {
                num = value;
            }

            if (value == 0 || places < 1)
                return value;

            var p = Math.floor(Math.log(num) / Math.LN10);
            var n = num * Math.pow(10, -p);

            var x = n.toFixedELN(places - 1);



            if (value < 0) {
                x = -x;
            }
            return x + "E" + p;
        } else {
            return "#VALUE!";
        }
    }
    var epa = new EpaFunction();

    var FdaFunction = function () {
        this.name = "FDA";
        this.minArgs = 1;
        this.maxArgs = 2;
    };
    FdaFunction.prototype = new GC.Spread.CalcEngine.Functions.Function();
    FdaFunction.prototype.description = function () {
        return {
            description: "对value进行四舍六入五留双修约，保留小数点后指定位数",
            parameters: [{
                name: "value",
                repeatable: false,
                optional: false
            }, {
                name: "places",
                repeatable: false,
                optional: false
            }]
        }
    }
    FdaFunction.prototype.isContextSensitive = function () {
        return true;
    }
    FdaFunction.prototype.evaluate = function (context, value, places) {
        var context = arguments[0];
        var num;
        if (!isNaN(parseInt(value)) && !isNaN(parseInt(places))) {

            var viewport = designer.wrapper.spread.getActiveSheet().getFormatter(context.row, context.column, GC.Spread.Sheets.SheetArea.viewport);
            if (isEmpty(viewport)) {
                designer.wrapper.spread.getActiveSheet().setFormatter(context.row, context.column, "@", GC.Spread.Sheets.SheetArea.viewport);
            }
            //此处value为负数时报错；
            //          value = NP.strip(value,value.toString().length);
            value = NP.strip(parseFloat(value));
            if (value < 0) {
                num = -value;
            } else {
                num = value;
            }

            if (places < 0)
                return value;
            var v = Number(num);
            var s = v + "";
            var result = "";
            if (s.indexOf(".") != -1) {
                var s_1 = s.substring(s.indexOf(".") + 1);
                if (s_1.length == parseInt(places) + 1) {
                    if (s_1.endsWith("5")) {
                        var s_2 = s.substr(0, s.length - 1);
                        var n = Number(s_2);
                        var s_n = n.toFixedELN(places);
                        var x = s_n.substring(s_n.length - 1);
                        if (Number(x) % 2 == 0) {
                            result = s_n;
                        }
                    }
                }
            }
            if (result == "") {
                result = v.toFixedELN(places);
            }

            if (value < 0) {
                result = "-" + result;
                if (result.startsWith("-0")) {
                    if (Number(result) == 0)
                        result = result.substring(1, result.length);
                }
            }

            return result;
        } else {
            return "#VALUE!";
        }
    }
    var fda = new FdaFunction();

    var IsoFunction = function () {
        this.name = "ISO";
        this.minArgs = 1;
        this.maxArgs = 2;
    };
    IsoFunction.prototype = new GC.Spread.CalcEngine.Functions.Function();
    IsoFunction.prototype.description = function () {
        return {
            description: "对value进行四舍五入，保留小数点后指定位数",
            parameters: [{
                name: "value",
                repeatable: false,
                optional: false
            }, {
                name: "places",
                repeatable: false,
                optional: false
            }]
        }
    }
    IsoFunction.prototype.isContextSensitive = function () {
        return true;
    }
    IsoFunction.prototype.evaluate = function (context, value, places) {
        var context = arguments[0];
        var num;
        if (!isNaN(parseInt(value)) && !isNaN(parseInt(places))) {

            var viewport = designer.wrapper.spread.getActiveSheet().getFormatter(context.row, context.column, GC.Spread.Sheets.SheetArea.viewport);
            if (isEmpty(viewport)) {
                designer.wrapper.spread.getActiveSheet().setFormatter(context.row, context.column, "@", GC.Spread.Sheets.SheetArea.viewport);
            }
            value = NP.strip(parseFloat(value));
            if (value < 0) {
                num = -value;
            } else {
                num = value;
            }

            if (places < 0)
                return value;
            var v = Number(value);

            var x = v.toFixedELN(places);

            return x;
        } else {
            return "#VALUE!";
        }
    }
    var iso = new IsoFunction();

    /** 自定义函数开始 */
    var EmcFunction = function () {
        this.name = "EMC";
        this.minArgs = 1;
        this.maxArgs = 2;
    };
    EmcFunction.prototype = new GC.Spread.CalcEngine.Functions.Function();
    EmcFunction.prototype.description = function () {
        return {
            description: "对value进行四舍六入五留双修约，保留指定位有效数字",
            parameters: [{
                name: "value",
                repeatable: false,
                optional: false
            }, {
                name: "places",
                repeatable: false,
                optional: false
            }]
        }
    }
    EmcFunction.prototype.isContextSensitive = function () {
        return true;
    }
    EmcFunction.prototype.evaluate = function (context, value, places) {
        var context = arguments[0];
        var num;
        if (!isNaN(parseFloat(value)) && !isNaN(parseInt(places))) {
            var viewport = designer.wrapper.spread.getActiveSheet().getFormatter(context.row, context.column, GC.Spread.Sheets.SheetArea.viewport);
            if (isEmpty(viewport)) {
                designer.wrapper.spread.getActiveSheet().setFormatter(context.row, context.column, "@", GC.Spread.Sheets.SheetArea.viewport);
            }
            value = NP.strip(parseFloat(value));
            if (value < 0) {
                num = -value;
            } else {
                num = value;
            }

            if (value == 0 || places < 1)
                return value;
            //判断整数部分长度够不够
            var v = Number(num);
            var s = num + "";
            var result = "";
            var where_s = null;
            if (s.startsWith("0")) {//必为0.多少,需要考虑从哪一位开始算是有效数字
                var s_1 = s.substr(2, s.length);//去掉0.
                var where_1 = s_1.indexOf(s_1.match("[1-9]"));
                where_s = where_1 + places;//判断之后要保留的小数位数
                var s_1 = s.substring(s.indexOf(".") + 1);
                if (s_1.length == parseInt(where_s) + 1) {
                    if (s_1.endsWith("5")) {
                        var s_2 = s.substr(0, s.length - 1);
                        var n = Number(s_2);
                        var s_n = n.toFixedELN(where_s);
                        var x = s_n.substring(s_n.length - 1);
                        if (Number(x) % 2 == 0) {
                            result = s_n;
                        }
                    }
                }
            } else {//从第一位开始就是有效数字，需要考虑是整数占了几位有效数字，还需要保留几位小数
                var int_length = (parseInt(v) + "").length;
                if (int_length > places) {
                    // 这种情况需要转为科学计算器表达方式，调用接口
                    var calculateRequestData = {
                        id: 'EMC',
                        originalResult: value,
                        param: 'EMC,' + places
                    };
                    $.ajax({
                        type: 'post',
                        async: false,
                        data: getJsonWrapper(null, [ '', [ calculateRequestData ]]),
                        url: IFM_CONTEXT + '/secure/core/module/sys/rounding-rules/action/calculate',
                        contentType: 'application/json',
                        success: function (result) {
                            // success
                            value = result.revisionResult;
                        },
                        error : function(r) {
                            value = "#VALUE!";
                        }
                    });
                    return value;
                } else {
                    where_s = places - int_length;
                    if (s.indexOf(".") != -1) {
                        var s_1 = s.substring(s.indexOf(".") + 1);
                        if (s_1.length == parseInt(where_s) + 1) {
                            if (s_1.endsWith("5")) {
                                var s_2 = s.substr(0, s.length - 1);
                                var n = Number(s_2);
                                var s_n = n.toFixedELN(where_s);
                                var x = s_n.substring(s_n.length - 1);
                                if (Number(x) % 2 == 0) {
                                    result = s_n;
                                }
                            }
                        }
                    }
                }
            }

            if (result == "") {
                result = v.toFixedELN(where_s);
            }
            if (result.indexOf("1") == 0 && s.indexOf("0.") == 0) {
                if (result.indexOf(".") != -1) {
                    if (where_s == 1)
                        result = result.substring(0, 1);
                    else {
                        result = result.substring(0, where_s + 1);
                    }
                } else {
                    result = result.substring(0, where_s + 1);
                }
            }

            if (value < 0) {
                result = "-" + result;
                if (result.startsWith("-0")) {
                    if (Number(result) == 0)
                        result = result.substring(1, result.length);
                }
            }

            return result;
        } else {
            return "#VALUE!";
        }
    }
    var emc = new EmcFunction();
    
    function getFormulaFunction() {
        this.name = "GETFORMULA";
        this.maxArgs = 3;
        this.minArgs = 1;
    }
    getFormulaFunction.prototype = new GC.Spread.CalcEngine.Functions.Function();
    getFormulaFunction.prototype.description = function () {
        return {
            description: "获取指定区间的值，经过计算后得到计算公式参数，第一个参数指定第一行数据，第二个参数指定第二行数据，第三个参数指定计算结果类型：1：斜率，2：截距，3：相关系数",
            parameters: [{
                name: "arg1",
                repeatable: false,// 可重复的
                optional: false// 可选的
            },{
                name: "arg2",
                repeatable: false,// 可重复的
                optional: false// 可选的
            },{
                name: "arg3",
                repeatable: false,// 可重复的
                optional: false// 可选的
            }]
        }
    }
    getFormulaFunction.prototype.acceptsReference = function (argIndex) {
        return true;
    }
    getFormulaFunction.prototype.isContextSensitive = function(){
        return true;
    }
    // arg1是第一行的数据、arg2是第二行的数据、arg3是指定获取曲线参数值
    getFormulaFunction.prototype.evaluate = function (context,arg1,arg2,arg3) {
        if(arg1.getRowCount() == 1 && arg1.getColumnCount() >= 1 && arg2.getRowCount() == 1 && arg2.getColumnCount() >= 1) {
            var sheet = context.source.getSheet();
            //var sheet = designer.wrapper.spread.getActiveSheet();
            var selectedValueA = []; // 第1行的值
            var selectedValueB = []; // 第2行的值
            var result = "#VALUE!";
            
            // 用setTimeout来人为干预JS代码的运行顺序，这样就不会跟机制冲突了    参考论坛：https://gcdn.grapecity.com.cn/showtopic-48436-1-122.html
            setTimeout(function () {
                for(var i=0;i<arg1.getRowCount();i++){
                    for(var j=0;j<arg1.getColumnCount();j++){
                        // 不计算隐藏行/列
                        if(sheet.getColumnVisible(arg1.getColumn()+j) && sheet.getRowVisible(arg1.getRow()+i)){
                            var value = sheet.getValue(arg1.getRow()+i,arg1.getColumn()+j);
                            if(value != null){
                                if(i === 0){ // 第一行
                                    selectedValueA.push(value);
                                }
                            }
                        }
                    }
                }
                for(var i=0;i<arg2.getRowCount();i++){
                    for(var j=0;j<arg2.getColumnCount();j++){
                        // 不计算隐藏行/列
                        if(sheet.getColumnVisible(arg2.getColumn()+j) && sheet.getRowVisible(arg2.getRow()+i)){
                            var value = sheet.getValue(arg2.getRow()+i,arg2.getColumn()+j);
                            if(value != null){
                                if(i === 0){ // 第一行
                                    selectedValueB.push(value);
                                }
                            }
                        }
                    }
                }
                result = calcResult(selectedValueA, selectedValueB, arg3);
                // 组织spreadjs自定义公式内部循环调用    参考论坛：https://gcdn.grapecity.com.cn/showtopic-59113-1-1.html
                sheet.suspendCalcService(false);
                sheet.setValue(context.row, context.column, result);
                sheet.resumeCalcService(true);
            }, 0);
            return result; 
        } else {
            designer.MessageBox.show(designer.res.sunwayTips.selectRegionAgain, designer.res.sunwayTips.tipTitle, 2);
        }

        return "#VALUE!";
    };
    var getFormula = new getFormulaFunction();

    /** 自定义函数结束 核心保留ISO,FDA,EPA,EMC */
    // GETFORMULA 是属于环保定制获取曲线计算参数，暂放核心
    mathFunction = [{ name: "ISO", fun: iso }, { name: "FDA", fun: fda }, { name: "EPA", fun: epa }, { name: "EMC", fun: emc }, {name: "GETFORMULA", fun: getFormula}];
}

function calcResult(array_x, array_y, resultCategory) {
    if (array_x.length > 1 && array_y.length > 1 && array_x.length === array_y.length) {

        var blankcal = array_y[0]; // 曲线零点均值
        array_y.forEach(function(item, index, arr) { // y数组每个元素减去blankcal后重新赋值
            arr[index] = (new BigDecimal(item + "").subtract(new BigDecimal(blankcal + ""))).toString();
        })

        var array_x_length = new BigDecimal(array_x.length + "");
        // sigma 西格玛
        var sigma_xy = BigDecimal.ZERO;
        var sigma_x = BigDecimal.ZERO;
        var sigma_y = BigDecimal.ZERO;
        var sigma_x2 = BigDecimal.ZERO;
        var sigma_y2 = BigDecimal.ZERO;
        var averageValue_x = BigDecimal.ZERO;
        var averageValue_y = BigDecimal.ZERO;

        array_x.forEach(function(item, index, arr) {
            var value_x = new BigDecimal(item + "");
            var value_y = new BigDecimal(array_y[index] + ""); // 当前X数组中的元素，对应Y数组中同索引位置的元素
            sigma_xy = sigma_xy.add(value_x.multiply(value_y));
            sigma_x = sigma_x.add(value_x);
            sigma_y = sigma_y.add(value_y);
            sigma_x2 = sigma_x2.add(value_x.multiply(value_x));
            sigma_y2 = sigma_y2.add(value_y.multiply(value_y));
        });

        averageValue_x = sigma_x.divide(array_x_length, 8, BigDecimal.ROUND_HALF_UP);// 保留小数位

        averageValue_y = sigma_y.divide(array_x_length, 8, BigDecimal.ROUND_HALF_UP);

        var bu = (array_x_length.multiply(sigma_xy)).subtract(sigma_x.multiply(sigma_y));
        var bl = (array_x_length.multiply(sigma_x2)).subtract(sigma_x.multiply(sigma_x));

        var slope = BigDecimal.ZERO;
        if (bl.compareTo(BigDecimal.ZERO) != 0) {
            // slope = bu.divide(bl);
            slope = bu.divide(bl, 8, BigDecimal.ROUND_HALF_UP);
        }

        // BigDecimal intercept = averageValue_y.subtract(slope.multiply(averageValue_x));
        var intercept = averageValue_y.subtract(slope.multiply(averageValue_x)).setScale(8, BigDecimal.ROUND_HALF_UP);
        var r2u = (array_x_length.multiply(sigma_xy)).subtract(sigma_x.multiply(sigma_y));
        var r2l = new BigDecimal(
                Math.sqrt(
                    (
                        (array_x_length.multiply(sigma_x2)).subtract(sigma_x.multiply(sigma_x))
                    ).multiply(
                        (array_x_length.multiply(sigma_y2)).subtract(sigma_y.multiply(sigma_y))
                    )
                ) + ""
        );
        var linearCoefficient = BigDecimal.ZERO;
        if (r2l.compareTo(BigDecimal.ZERO) != 0) {
            // linearCoefficient=r2u.divide(r2l);
            linearCoefficient = r2u.divide(r2l, 6, BigDecimal.ROUND_HALF_UP);
        }

        /* 这里后端的做法是，查询修约公式，对公式的结果进行修约，询问后说在ELN里面得到结果后用修约公式进行修约 */
        var slopeStr = slope.toString();
        var interceptStr = intercept.toString();
        var linearCoefficientStr = linearCoefficient.toString();
        if ("1.0000" === linearCoefficientStr) {
            linearCoefficientStr = "0.9999";
        }
        console.log("斜率：" + slopeStr + ",截距：" + interceptStr + ",相关系数：" + linearCoefficientStr);

        if (resultCategory === 1) {
            return slopeStr;
        } else if (resultCategory === 2) {
            return interceptStr;
        } else if (resultCategory === 3) {
            return linearCoefficientStr;
        }
        return null;
    } else {
        designer.MessageBox.show(designer.res.sunwayTips.selectRegionAgain, designer.res.sunwayTips.tipTitle, 2);
    }

}

function clearAreaIfNoValue() {
    var spread = designer.wrapper.spread;
    var sheetCount = spread.getSheetCount();
    var sheet;
    for (var i = 0; i < sheetCount; i++) {
        sheet = spread.getSheet(i);
        var rowCount = sheet.getRowCount();
        var colCount = sheet.getColumnCount();
        var rowStart = -1;
        var rowEnd = -1;
        var colStart = -1;
        var colEnd = -1;
        var clearBindingPath;
        for (var row = 0; row < rowCount; row++) {
            for (var col = 0; col < colCount; col++) {
                var tag = sheet.getTag(row, col);
                if ("清除内容" == tag || (tag && "清除内容" == tag["清除内容"])) {
                    if (colStart == -1) {
                        rowStart = row;
                        colStart = col;
                    }
                    colEnd = col;
                    rowEnd = row;
                }
            }
        }
        for (var row = rowStart; row <= rowEnd; row++) {
            for (var col = colStart; col <= colEnd; col++) {
                // sheet.getCell(row, col).bindingPath()
                var table = sheet.tables.find(row, col);
                if (table && table.bindingPath()) {
                    clearBindingPath = table.bindingPath();
                }
                if (sheet.getBindingPath(row, col)) {
                    clearBindingPath = sheet.getBindingPath(row, col);
                }
            }
        }
        if (clearBindingPath) { // 示例：列表数据.数据源名称
            var bindingPathArray = clearBindingPath.split(".");
            var clearSourceData = dataSrcObj[bindingPathArray[0]][bindingPathArray[1]]; // 从数据源查询的数据中找到需要清空的数据源对应的数据，如果为空就清空ELN内容，否则不清空
            // instance.clear(row, column, rowCount, columnCount, area, type);
            if (rowStart != -1 && colStart != -1 && clearSourceData.length <= 1) {
                sheet.deleteRows(rowStart, rowEnd-rowStart + 1);
            }
        }
    }
}

function getNumebrFromString(string, row) {
    var result = {};
    var s = "";
    var regPos = /^\d$/; // 非负整数 
    var regPosAZ = /^[A-Z]$/;
    for (i = 0; i < string.length; i++) {
        var c = string.charAt(i);
        if (regPos.test(c)) {
            s += c;
        } else {
            if (s.length > 0) {
                var z = i - s.length - 1;
                if (z >= 0 && regPosAZ.test(string.charAt(z))) {
                    result[i - s.length] = s;
                }
                s = "";
            }
        }
    }
    if (s.length > 0) {
        var z = i - s.length - 1;
        if (z >= 0 && regPosAZ.test(string.charAt(z))) {
            result[i - s.length] = s;
        }
        s = "";
    }
    var change = false;
    var x = 0;
    for (key in result) {
        var n = result[key];
        var mark = "manyFormulaMark";
        if (n == (row + 1) + "") {
            var y = mark.length - n.length;
            
            string = string.substr(0, parseInt(key) + x) + mark + string.substring(parseInt(key) + x + n.length)
            x += y;
           change = true; 
        }
    }
    
    return change ? string : null;
}

// 添加打印区域标记
function isPrintLineVisible(state) {
    var sheet = GC.Spread.Sheets.Designer.wrapper.spread.getActiveSheet();
    
    // 判断是否存在打印区域设置
    if (!sheet.printLine) {
        sheet.printLine = {
            rowBorderList: [],
            columnBorderList: []
        }
    } else if (state) {
        isPrintLineVisible(false);
    }

    // 存储打印区域设置状态
    sheet.printLineVisible = state;

    // 设置打印区域样式
    var color = 'black';
    var lineStyle = GC.Spread.Sheets.LineStyle.dotted;

    if (!state) lineStyle = GC.Spread.Sheets.LineStyle.empty;

    var lineBorder = new GC.Spread.Sheets.LineBorder(color, lineStyle);
    var sheetArea = GC.Spread.Sheets.SheetArea.viewport;

    // 获取表格总列数
    var rowCount = sheet.getRowCount();
    // 获取表格总行数
    var columnCount = sheet.getColumnCount();

    // 获取打印区域
    var rowBorderList = sheet.printLine.rowBorderList;
    var columnBorderList = sheet.printLine.columnBorderList;

    // 判断是否取消打印区域
    if (!state) {
        // 重置打印区域边框
        for (var i = 0, length = rowBorderList.length; i < length; i++) {
            sheet.getRange(rowBorderList[i], 0, 1, columnCount)
                 .setBorder(lineBorder, { bottom: true }, sheetArea);
        }
        for (var i = 0, length = columnBorderList.length; i < length; i++) {
            sheet.getRange(0, columnBorderList[i], rowCount, 1)
                 .setBorder(lineBorder, { right: true }, sheetArea);
        }
        sheet.printLine = {
            rowBorderList: [],
            columnBorderList: []
        }
        return;
    }

    // 定义A4纸高度与宽度
    var height_pixels = 873;
    var width_pixels = 595;
    // 定义A4纸最大打印高度与宽度
    var max_width_pixels = 641;
    var max_height_pixels = 892;

    // 记录当前位置宽度
    var tempRowPixels = 0;
    var tempStartY = 0;

    for (var i = 0; i < rowCount; i++) {
        var pixels = sheet.getRowHeight(i, sheetArea);
        tempRowPixels += pixels;
        if (tempRowPixels >= height_pixels){
            if (tempRowPixels > max_height_pixels) {
                i--;
            }
            if (tempRowPixels === pixels && pixels > max_height_pixels) {
                i++;
            }
            rowBorderList.push(i);
            tempRowPixels = 0;
            sheet.getRange(i, 0, 1, columnCount)
             .setBorder(lineBorder, { bottom: true }, sheetArea);
        }
    }
 
    var tempColumnPixels = 0;

    for (var i = 0; i < columnCount; i++) {
        var pixels = sheet.getColumnWidth(i, sheetArea);
        tempColumnPixels += pixels;
        if (tempColumnPixels >= width_pixels) {
            if (tempColumnPixels > max_width_pixels) {
                i--;
            }
            if (tempColumnPixels === pixels && pixels > max_width_pixels) {
                i++;
            }
            columnBorderList.push(i);
            tempColumnPixels = 0;
            sheet.getRange(0, i, rowCount, 1)
             .setBorder(lineBorder, { right: true }, sheetArea);
        }
    }
};

// 为Date添加format方法，方便获取格式化后的时间
Date.prototype.format = function(fmt) { 
    var o = { 
       "M+" : this.getMonth()+1,                 //月份 
       "d+" : this.getDate(),                    //日 
       "h+" : this.getHours(),                   //小时 
       "m+" : this.getMinutes(),                 //分 
       "s+" : this.getSeconds(),                 //秒 
       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
       "S"  : this.getMilliseconds()             //毫秒 
   }; 
   if(/(y+)/.test(fmt)) {
           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
   }
    for(var k in o) {
       if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
   return fmt; 
}

function setAutoSignCellValue(sheet, sheetTag) {
    if (sheetTag["swAutoSignCellType"]) {
        var autoSignCellData = sheetTag["swAutoSignCellType"];
        var _opener = opener || parent; // window.open时 opener 有效，iframe（预览）时 parent 有效

        if (_opener.Gikam && _opener.Gikam.loginUser && !_opener.Gikam.loginUser.id) {
            _opener = parent.opener; // 编辑模式下点击预览按钮时属于去找父类窗口的打开对象
        }
        if (_opener && _opener.Gikam) { // 防止复制链接打开时opener为空报错
            var signValue = _opener.Gikam.loginUser.id + "@@" + new Date().format("yyyy-MM-dd") + "@@" + IFM_CONTEXT + _opener.Gikam.loginUser.ext$.signatureurl;
            sheet.setValue(autoSignCellData.row, autoSignCellData.col, signValue);

            //取消斜线
            var range = sheet.getRange(autoSignCellData.row, autoSignCellData.col);
            range.diagonalUp(null);
        }
    }
}
