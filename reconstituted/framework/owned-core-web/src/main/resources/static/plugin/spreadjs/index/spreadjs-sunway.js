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
        designer.MessageBox.show("请选择部分内容！", "提示", 2); /* 第三个参数控制弹窗显示图标：1提示  2警告 3错误 */
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

function addSunwayMenu() {
    var spread = designer.wrapper.spread;

    var commandManager = spread.commandManager();


    var insertRows = {
        text: "插入多行",
        name: "insertRows",
        command: "rowsCount",
        workArea: "rowHeader"

    };
    spread.contextMenu.menuData.push(insertRows);

    var insertRowsByCounts = {
        canUndo: false,
        execute: function (spread, options) {
            if(options.commandOptions){
                var sheet = spread.getSheetFromName(options.sheetName)
                sheet.suspendPaint()
                sheet.addRows(options.activeRow,parseInt(options.commandOptions))
                sheet.resumePaint()

            }
        }
    };
    commandManager.register("rowsCount", insertRowsByCounts, null, false, false, false, false);

    spread.contextMenu.menuView = new CustomMenuView();
}

function CustomMenuView() {
}
CustomMenuView.prototype = new GC.Spread.Sheets.ContextMenu.MenuView();
CustomMenuView.prototype.createMenuItemElement = function (menuItemData) {
    var self = this;
    if (menuItemData.name === "insertRows") {
        var containers = GC.Spread.Sheets.ContextMenu.MenuView.prototype.createMenuItemElement.call(self, menuItemData);
        var supMenuItemContainer = containers[0];
        var btndownBlock = createBtndown();

        var inputBlock = createInput();
        var btnupBlock = createBtnup();
        supMenuItemContainer.appendChild(btndownBlock);

        supMenuItemContainer.appendChild(inputBlock);
        supMenuItemContainer.appendChild(btnupBlock);

        return supMenuItemContainer;
    } else {
        var menuItemView = GC.Spread.Sheets.ContextMenu.MenuView.prototype.createMenuItemElement.call(self, menuItemData);
        return menuItemView;
    }
}
CustomMenuView.prototype.getCommandOptions = function (menuItemData, host, event) {
    if (menuItemData && menuItemData.name === "insertRows") {
        // var ele = event.target || event.srcElement;
        var ele = document.getElementsByClassName("inputBlock")[0]
        return ele.value;
    }
};

function createBtnup(){
    var inputBlock = document.createElement('input');
    inputBlock.type = 'button';
    inputBlock.value = '+';
    inputBlock.className = 'btnBlock';
    inputBlock.style = 'width: 20px; border: 1px solid #eee;';
    inputBlock.onclick = function(ev){
        if(ev.target) {
            ev.stopPropagation()
            var ele = document.getElementsByClassName("inputBlock")[0]
            ele.value =parseInt(ele.value)+1 ;
        }

    }
    return inputBlock;
}
function createBtndown(){
    var inputBlock = document.createElement('input');
    inputBlock.type = 'button';
    inputBlock.value = '-';
    inputBlock.className = 'btnBlock';
    inputBlock.style = 'width: 20px; border: 1px solid #eee;';
    inputBlock.onclick = function(ev){
        if(ev.target) {
            ev.stopPropagation()
            var ele = document.getElementsByClassName("inputBlock")[0]
            if(parseInt(ele.value)>1){
                ele.value =parseInt(ele.value)-1 ;
            }
        }
    }
    return inputBlock;
}
function createInput() {
    var inputBlock = document.createElement('input');

    inputBlock.value = '1';
    inputBlock.className = 'inputBlock';
    inputBlock.style = 'width: 30px; border: 1px solid #eee;';
    inputBlock.onclick = function(ev){
        if(ev.target) {
            ev.stopPropagation()
        }
    }
    inputBlock.onkeydown = function(ev){
        if(ev.target) {
            ev.stopPropagation()
        }
    }
    return inputBlock;
}

