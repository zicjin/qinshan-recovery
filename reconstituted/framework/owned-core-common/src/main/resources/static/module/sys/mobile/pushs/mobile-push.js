var mobilePush = Gikam.getPageObject({

    baseUrl: IFM_CONTEXT + '/secure/core/module/sys/mobile/push_messages',
    baseUserUrl: IFM_CONTEXT + '/secure/core/module/sys/mobile/push_users',

    // 列项->主列表
    getEditListColumns: function () {
        return [{
            checkbox: true
        }, {
            field: 'pushFlag',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHFLAG',
            readonly: true,
            type: 'select',
            items: [{
                text: '',
                value: ''
            }, {
                text: Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_NO'),
                value: "0"
            }, {
                text: Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_YES'),
                value: "1"
            }],
            formatter: function (index, value, row, text) {
                if (row['pushFlag'] === "0") {
                    return '<a href="javascript:;" style="color:red;">' + Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_NO') + '</a>';
                } else {
                    return '<a href="javascript:;">' + Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_YES') + '</a>';
                }
            }
        }, {
            field: 'title',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.TITLE',
            readonly: true
        }, {
            field: 'pushByName',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHBYNAME',
            readonly: true
        }]
    },

    // 菜单详情页
    getBaseInfoFormFields: function () {
        return [{
            field: 'id',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.ID',
            readonly: true
        }, {
            field: 'title',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.TITLE',
            validators: ['notEmpty', 'strLength[0,64]'],
            preInsert: true
        }, {
            field: 'pushFlag',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHFLAG',
            readonly: true,
            type: 'select',
            items: [{
                text: '',
                value: ''
            }, {
                text: Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_NO'),
                value: "0"
            }, {
                text: Gikam.propI18N('CORE.MODULE.SYS.MOBILE_PUSHS.STATUS.SEND_YES'),
                value: "1"
            }]
        }, {
            field: 'pushedUserName',
            colspan: 3,
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHEDUSERNAME',
            type: 'choose',
            validators: ['notEmpty'],
            onChoose: function () {
                Gikam.create('modal', {
                    title: 'CORE.MODULE.SYS.MOBILE_PUSHS.MODAL.PUSHUSERCHOOSE',
                    url: IFM_CONTEXT + '/secure/core/module/sys/mobile/pushs/page/choose-list',
                    onAfterClose: function (rows) {
                        console.log(rows);
                        if (rows) {
                            var chooseData = rows.map(function (item) {
                                return {
                                    pushedUserId: item.id,
                                    pushedUserName: item.ext$.username
                                }
                            });
                            var pushedUserId = "", pushedUserName = "";
                            chooseData.forEach(function (data, i) {
                                if (i === 0) {
                                    pushedUserId += data.pushedUserId;
                                    pushedUserName += data.pushedUserName;
                                } else {
                                    pushedUserId += "," + data.pushedUserId;
                                    pushedUserName += "," + data.pushedUserName;
                                }
                            });
                            var form = Gikam.getComp('mobile-push-message-edit-list-base-info-form');
                            form.setData({
                                pushedUserId: pushedUserId,
                                pushedUserName: pushedUserName
                            }, true)
                        }
                    }
                });
            }
        }, {
            field: 'content',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.CONTENT',
            type: 'textarea',
            validators: ['notEmpty'],
            colspan: 3,
            height: 80
        }, {
            field: 'data',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.DATA',
            type: 'textarea',
            colspan: 3,
            height: 80
        }, {
            field: 'remark',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.REMARK',
            type: 'textarea',
            colspan: 3
        }, {
            field: 'pushByName',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHBYNAME',
            readonly: true
        }, {
            field: 'pushTime',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.PUSHTIME',
            readonly: true
        }, {
            field: 'createdByName',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.CREATEDBYNAME',
            readonly: true
        }, {
            field: 'createdTime',
            title: 'T_CORE_MOBILE_PUSH_MESSAGE.CREATEDTIME',
            readonly: true
        }];
    },

    //用户权限选择菜单
    getChooseListColumns: function () {
        return [{
            title: 'T_CORE_MOBILE_PUSH_USER.ID',
            field: 'id',
            search: true
        }, {
            title: 'T_CORE_MOBILE_PUSH_USER.USERNAME',
            field: 'ext$.username'
        }, {
            title: 'T_CORE_MOBILE_PUSH_USER.CLIENTID',
            field: 'clientId'
        }]
    }
});