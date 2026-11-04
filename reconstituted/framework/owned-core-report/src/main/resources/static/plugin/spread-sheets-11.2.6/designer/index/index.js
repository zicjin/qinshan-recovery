(function () {
    'use strict';

    var designer = GC.Spread.Sheets.Designer;

    var needSuspend;
    var tipWidth = 600;

    /** added by zhanghang@sunwayworld.com begin **/
    operateType = queryUrlValueByName("operateType");
    previewType = queryUrlValueByName("previewType");
    /**
     * 当预览时json数据不从数据库获取，直接获取当前页面的数据
     */
    if (previewType == 1) {
        elntext = parent.window.queryParentSpreadJson();
    }
    IFM_CONTEXT = queryUrlValueByName("IFM_CONTEXT");
    bizId = queryUrlValueByName("bizId");
    elnId = queryUrlValueByName("elnId");
    elnTmplId = queryUrlValueByName("elnTmplId");
    versionId = queryUrlValueByName("versionId");
    isTest = queryUrlValueByName("isTest");
    fileid = queryUrlValueByName("fileid");
    //ELN显示页
    pageno = queryUrlValueByName("pageno");
    uploadPdf = isEmpty(queryUrlValueByName("uploadPdf")) || queryUrlValueByName("uploadPdf") == "false" ? false : true; // 默认false
    tableName = queryUrlValueByName("tableName");
    uploadBizId = queryUrlValueByName("uploadBizId");
    autoExportPDF = queryUrlValueByName("autoExportPDF");
    preload = queryUrlValueByName("preload");
    var elnUrl = queryUrlValueByName("elnUrl");

    uploadPdfName = queryUrlValueByName("uploadPdfName");
    bizCategory = queryUrlValueByName("bizCategory") || "document";

    function updateLayout() {
        $(".content").css('height', $(".content").children('.fill-spread-content').height());
        $(".header").css("width", $(window).width() + "px");
        if ($(".ribbon-bar").data("gcui-gcuiribbon")) {
            $(".ribbon-bar").data("gcui-gcuiribbon").updateRibbonSize();
        }
        var spread = designer.wrapper.spread;
        if (spread && spread.isPaintSuspended()) {
            spread.resumePaint();
            spread.refresh();
            needSuspend = true;
        }
    }
    /** added by zhanghang@sunwayworld.com end * */

    var _windowResizeTimer = null;

    function _doWindowResize() {
        if (_windowResizeTimer) {
            window.clearTimeout(_windowResizeTimer);
        }
        _windowResizeTimer = window.setTimeout(function () {
            updateLayout();
            _windowResizeTimer = null;
            if (needSuspend) {
                needSuspend = false;
                window.setTimeout(function () {
                    designer.wrapper.spread.suspendPaint();
                }, 300);
            }
        }, 100); //now delay 100ms to resize designer
    }

    $(document).ready(function () {
        designer.loader.loadContent();
        $(window).resize(_doWindowResize);
        $(window).resize();

        $("#verticalSplitter").draggable({
            axis: "y",
            containment: ".container",
            scroll: false,
            zIndex: 100,
            stop: function (event, ui) {
                var $this = $(this), top = $this.offset().top, offset = top - $(".header").height(), $content = $(".content .fill-spread-content");

                // adjust size of related items
                var oldHeight = $("#formulaBarText").height();
                var newHeight = oldHeight + offset;
                var ORIGINAL_FORMULABARTEXT_HEIGHT = 20;
                if (newHeight < ORIGINAL_FORMULABARTEXT_HEIGHT) { // 20: original height of formulaBarText
                    // reset to default
                    $("#formulaBarText").css({ height: ORIGINAL_FORMULABARTEXT_HEIGHT });
                    top = top + ORIGINAL_FORMULABARTEXT_HEIGHT - newHeight;
                } else {
                    $("#formulaBarText").css({ height: newHeight });
                }
                $content.css({ top: top + 10 }); // 10: height of the space for verticalSplitter
                $content.parent().css({ height: $content.height() });
                $(".header").css({ height: top });
                $this.css({ top: 0 });
                designer.wrapper.spread.refresh();
            }
        });

        function disableDragDrop(dataTransfer) {
            if (dataTransfer) {
                dataTransfer.effectAllowed = "none";
                dataTransfer.dropEffect = "none";
            }
        }

        window.addEventListener("dragenter", function (e) {
            e = e || event;
            e.preventDefault();
            disableDragDrop(e.dataTransfer);
        }, false);
        window.addEventListener("dragover", function (e) {
            e = e || event;
            e.preventDefault();
            disableDragDrop(e.dataTransfer);
        }, false);
        window.addEventListener("drop", function (e) {
            e = e || event;
            e.preventDefault();
            disableDragDrop(e.dataTransfer);
        }, false);

    });

    designer.loader.ready(function () {
        //To Fix the designer resize performance issues.
        $(window).unbind("resize.gcuiribbon");
        $("#verticalSplitter").show();
        updateLayout();
    });

    /** added by zhanghang@sunwayworld.com begin **/
    // window.history.replaceState('forward',null,window.IFM_CONTEXT.replace(location.origin, '') + '/secure/core/module/eln/elns/page/spreadjs-web-designer' + location.search)

    /**
     * 禁止页面使用F5刷新
     * */
    document.onkeydown = disableF5;

    /**
     * 页面刷新或者直接关闭浏览器提示
     * */
    window.onbeforeunload = newPageClose;

    /**
     * 页面关闭时去执行列表更新
     * */
    window.onunload = onunload_handler;

    /** added by zhanghang@sunwayworld.com end * */
})();
/** added by zhanghang@sunwayworld.com begin * */
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
/** added by zhanghang@sunwayworld.com end * */