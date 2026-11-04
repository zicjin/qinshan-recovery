//@ sourceURL=core-equipt-serial-listener.js
coreEquiptAcqConfig.serialListener = {
    client : null,
    subscription : null,
    equipt : null,
    action : null,

    isConnected : function() {
        var _this = this;
        return _this.client && _this.client.connected;
    },

    connect : function() {
        var _this = this;

        _this.disconnectIfConnected();
        var url = 'http://localhost:9778/ws';
        if (_this.equipt.acqCategory == 'tcp') {
            url = IFM_CONTEXT + '/ws';
        }
        _this.client = Stomp.over(new SockJS(url));
        _this.client.connect({}, function(frame) {
            _this.updateConnectionStatus();
            _this.subscribe();
            Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.CONNECT_SUCCESS');
        }, function(error) {
            _this.equipt = null;
            Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.CONNECT_FAILURE');

            _this.updateConnectionStatus();
        });
    },

    disconnectIfConnected : function() {
        var _this = this;
        if (_this.isConnected()) {
            _this.unsubscribe();
            _this.client.disconnect(function() {
                _this.client = null;
            });
        }
    },

    subscribe : function() {
        var _this = this;

        if (_this.isConnected()) {

            var topic = '/topic/tcp-ports/' + _this.equipt.ip + ':' + _this.equipt.port;

            if (_this.equipt.acqCategory == 'serial') {
                topic = '/topic/serial-ports';
            }
            _this.subscription = _this.client.subscribe(topic, function(response) {
                var originalData = response.body;
                /* 对仪器采集数据处理 1,配置了开始标记/结束标记 2,退而取这个方案 脚本解析 */
                var effectiveValue = _this.getEffectiveValue(response.body, _this.equipt);
                // 去掉空格
                effectiveValue = effectiveValue.replace(/\ +/g, "");
                // 去掉回车换行
                effectiveValue = effectiveValue.replace(/[\r\n]/g, "");
                response.body = effectiveValue;
                var matchValue = response.body.match(/[\d\.]/g);
                var getedValue = matchValue && matchValue.join("");
                var equipt = _this.equipt;

                _this.callback(matchValue, getedValue, equipt, originalData);

            });
        }
    },

    callback : function(matchValue, getedValue, equipt, originalData) {

        var data = Gikam.getLastModal().getData();
        var action = data.action;
        var scope = data.scope;
        if (Gikam.isFunction(action)) {
            action.call(scope, matchValue, getedValue, equipt, originalData);
        }

    },

    unsubscribe : function() {
        var _this = this;
        if (_this.subscription) {
            _this.subscription.unsubscribe();
            _this.subscription = null;
        }
    },

    open : function(acqCategory, equipt, action, param) {
        var _this = this;

        var def = Gikam.getDeferred();

        if (acqCategory === 'serial') {// 串口连接
            var url = 'http://localhost:9778/serial-ports/' + equipt.com + '/open';

            Gikam.postText(url, JSON.stringify(param)).done(function(result) {

                if (!result) {
                    Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.CONNECT_FAILURE');
                    return;
                }

                _this.equipt = equipt;
                _this.connect();

                _this.action = action + '(response.body, getedValue, equipt)';

                def.resolve();

            }).fail(function() {

            }).always(function() {
                var lastModal = Gikam.getLastModal();
                lastModal && lastModal.window.closeMask();
            });

        } else {// 网口连接
            var url = coreEquiptAcqConfig.baseUrl + '/' + equipt.id + '/action/connect';

            Gikam.put(url, param).done(function(result) {
                if (result === 'fail') {
                    Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.CONNECT_FAILURE');
                } else {

                    _this.equipt = equipt;
                    _this.connect();

                    _this.action = action + '(response.body, getedValue, equipt)';

                    def.resolve();
                }
            }).fail(function(info) {

            }).always(function() {
                var lastModal = Gikam.getLastModal();
                lastModal && lastModal.window.closeMask();
            })

        }

        return def;
    },

    clear : function(callback, params, action) {
        var _this = this;
        _this.disconnectIfConnected();
        if (!_this.equipt) {
            return;
        }
        var url = 'http://localhost:9778/tcp-ports/clear';
        if (_this.equipt.acqCategory == 'serial') {
            url = 'http://localhost:9778/serial-ports/clear';
            Gikam.del(url, null).done(function(result) {
                var response = {}, getedValue = null, update = false;
                _this.equipt = null;
                _this.updateConnectionStatus()
                _this.action = null;
            });
        } else {
            var response = {}, getedValue = null, update = false;
            _this.equipt = null;
            _this.updateConnectionStatus();
            _this.action = null;
        }
    },

    updateConnectionStatus : function() {

    },

    isMasking : function() {
        var modal = Gikam.getLastModal();
        if (!modal) {
            return 'no-modal';
        }
        return (modal.window.model.$children[0].dataLoading);
    },

    /* 对仪器采集数据处理 1,配置了开始标记/结束标记 2,退而取这个方案 脚本解析 */
    getEffectiveValue : function(reposeValue, equipt) {
        if (Gikam.isEmpty(reposeValue)) {
            return reposeValue;
        }
        var processingValue = reposeValue;
        var haveSymbolFlag = false;
        if (Gikam.isNotEmpty(equipt.beginSymbol) && processingValue.indexOf(equipt.beginSymbol) != -1) {
            var beginIndex = processingValue.indexOf(equipt.beginSymbol) + equipt.beginSymbol.length;
            processingValue = processingValue.substring(beginIndex, processingValue.length);
            haveSymbolFlag = true;
        }
        if (Gikam.isNotEmpty(equipt.endSymbol) && processingValue.indexOf(equipt.endSymbol) != -1) {
            var endIndex = processingValue.indexOf(equipt.endSymbol);
            processingValue = processingValue.substring(0, endIndex);
            haveSymbolFlag = true;
        }
        if (!haveSymbolFlag && Gikam.isNotEmpty(equipt.analysisFormula)) {
            var funObject = {
                process : eval("(" + equipt.analysisFormula + ")")
            };
            processingValue = funObject.process(processingValue);
        }
        return processingValue;
    }
}