emcUser.shuttleFrameEditPage = {
    getBtnToolbar : function() {
        var _this = this;
        return {
            type : 'btnToolbar',
            items : [ {
                type : 'button',
                text : 'GIKAM.BUTTON.SAVE',
                icon : 'save',
                onClick : function() {
                    _this.save();
                }
            } ]
        }
    },

    save : function() {
        var _this = this;
        var rows = Gikam.getComp('emc-user-shuttle-frame-edit-list-shuttle-frame').getSelectList();

        if (!rows.length) {
            Gikam.alert('GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM');
            return;
        }

        var userIds = [], userNames = [];
        rows.map(function(row) {
            userIds.push(row.id);
            userNames.push(row.userName);
        });
        if (Gikam.isNotEmpty(_this.param.testMethodIds)) {
            var chooseData = [];
            var testMethodIds = _this.param.testMethodIds.split(',');
            testMethodIds.map(function(testMethodId) {
                chooseData.push({
                    inputUserId : userIds.join(),
                    inputUserName : userNames.join(),
                    id : testMethodId,
                })
            });

            Gikam.getLastModal().window.showMask(true);
            Gikam.put('/secure/emc/module/mdm/bizmdm/tests/' + testMethodIds[0], Gikam.getJsonWrapper({}, [ 'emcTestMethodServiceImpl', chooseData ])).done(function() {
                Gikam.getLastModal().close();
            }).always(function() {
                Gikam.getLastModal().window.closeMask();
            });
        } else {
            Gikam.getLastModal().close(rows);
        }
    },

    getShuttleFrameParam : function() {
        var _this = this;

        return {
            type : 'shuttleFrame',
            id : 'emc-user-shuttle-frame-edit-list-shuttle-frame',
            columns : emcUser.getChooseListColumns(),
            rightGridUrl : Gikam.isNotEmpty(_this.param.firstInputUserIds) ? '/secure/core/module/mdm/users/queries/choosable' : '',
            rightRequestData : {
                id_IN : _this.param.firstInputUserIds
            },
            data : _this.leftData,
            onRendered : function() {
                _this.shuttleFrame = this;
            },
        }
    },

    create : function() {
        var _this = this;
        Gikam.postSync('/secure/core/module/mdm/users/queries/choosable', Gikam.getJsonWrapper({
            f : _this.param,
            n : -1,
        }, [ '', [] ])).done(function(d) {
            var rows = d.rows;
            if (Gikam.isNotEmpty(_this.param.firstInputUserIds)) {
                var inputUserIdArray = _this.param.firstInputUserIds.split(',');
                for (var i = rows.length - 1; i >= 0; i--) {
                    if (inputUserIdArray.indexOf(rows[i].id) > -1) {
                        rows.splice(i, 1);
                    }
                }
            }
            _this.leftData = rows;
        });
        Gikam.create('layout', {
            renderTo : Gikam.getLastModal().window.$dom,
            center : {
                items : [ this.getBtnToolbar(), this.getShuttleFrameParam() ]
            }
        });
    },

    init : function(param) {
        this.param = param;
        this.create();
    },

}