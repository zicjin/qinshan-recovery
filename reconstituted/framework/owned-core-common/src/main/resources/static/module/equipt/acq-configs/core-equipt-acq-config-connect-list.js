//@ sourceURL=core-equipt-acq-config-connect-list.js
coreEquiptAcqConfig.connectPage = {

    getGridParam : function() {
        var _this = this;
        return {
            type : 'grid',
            id : 'core-equipt-acq-config-connect-list-grid',
            url : coreEquiptAcqConfig.baseUrl + '/queries/choosable',
            columns : coreEquiptAcqConfig.getConnectListColumns(),
            requestData : {
                acqCategory_DIFFER : 'file',
                methodId : _this.param.methodId
            },
            toolbar : [ {
                type : 'button',
                text : 'CORE.MODULE.EQUIPT.ACQCONFIGS.BUTTON.CONNECT',
                icon : 'link',
                id : 'core-equipt-acq-config-connect-list-connect-button',
                onClick : function() {
                    _this.connect();
                }
            }, {
                type : 'button',
                text : 'CORE.MODULE.EQUIPT.ACQCONFIGS.BUTTON.DISCONNECT',
                id : 'core-equipt-acq-config-connect-list-disconnect-button',
                icon : 'disable',
                onClick : function() {

                    var serialListener = coreEquiptAcqConfig.serialListener;

                    var equipt = serialListener.equipt;
                    serialListener.clear();
                    Gikam.getComp('core-equipt-acq-config-connect-list-grid').setData({
                        id : equipt.id,
                        connectStatus : ''
                    });

                    Gikam.getComp('core-equipt-acq-config-connect-list-connect-button').show();
                    Gikam.getComp('core-equipt-acq-config-connect-list-disconnect-button').hide();

                }
            }, {
                type : 'button',
                text : 'GIKAM.BUTTON.CLOSE',
                icon : 'cancel',
                onClick : function() {
                    coreEquiptAcqConfig.serialListener.clear();
                    Gikam.getLastModal().close();
                }
            } ],
            onRendered : function() {
                Gikam.getComp('core-equipt-acq-config-connect-list-disconnect-button').hide();
            }
        }
    },

    connect : function() {
        var _this = this;

        var grid = Gikam.getComp('core-equipt-acq-config-connect-list-grid');
        var rows = grid.getSelections();

        if (Gikam.isEmpty(rows)) {
            Gikam.alert('GIKAM.TIP.CHOOSE_ONE_ITEM');
            return;
        }

        var equipt = rows[0];
        var action = _this.param.action;

        if (equipt.acqCategory == 'tcp') {

            if (Gikam.isEmpty(equipt.ip) || Gikam.isEmpty(equipt.port)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_IP_PORT');
                return;
            }

            Gikam.getLastModal().window.showMask(true);
            coreEquiptAcqConfig.serialListener.open('tcp', equipt, action).done(function() {
                grid.setData({
                    id : equipt.id,
                    connectStatus : 'connected'
                }, false);

                Gikam.getComp('core-equipt-acq-config-connect-list-connect-button').hide();
                Gikam.getComp('core-equipt-acq-config-connect-list-disconnect-button').show();
            });
        } else if (equipt.acqCategory == 'serial') {

            if (Gikam.isEmpty(equipt.com)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_COM');
                return;
            }

            if (Gikam.isEmpty(equipt.baudRate)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_BAUDRATE');
                return;
            }

            if (Gikam.isEmpty(equipt.dataBit)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_DATABIT');
                return;
            }

            if (Gikam.isEmpty(equipt.stopBit)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_STOPBIT');
                return;
            }

            if (Gikam.isEmpty(equipt.parityCheck)) {
                Gikam.alert('CORE.MODULE.EQUIPT.ACQCONFIGS.TIP.NO_PARITYCHECK');
                return;
            }

            Gikam.getLastModal().window.showMask(true);
            coreEquiptAcqConfig.serialListener.open('serial', equipt, action, {
                baudRate : equipt.baudRate,
                dataBits : equipt.dataBit,
                stopBits : equipt.stopBit,
                parity : equipt.parityCheck,
                end : equipt.endFlag
            }).done(function() {
                grid.setData({
                    id : equipt.id,
                    connectStatus : 'connected'
                }, false);

                Gikam.getComp('core-equipt-acq-config-connect-list-connect-button').hide();
                Gikam.getComp('core-equipt-acq-config-connect-list-disconnect-button').show();
            });
        }
    },

    create : function() {
        var _this = this;
        Gikam.create('layout', {
            id : 'core-equipt-acq-config-connect-list-layout',
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getGridParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    }
};
