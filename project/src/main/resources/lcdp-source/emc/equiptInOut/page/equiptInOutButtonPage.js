var baseUrl = "emc.equiptInOut.server.LcdpEmcEquiptInoutService";
var pageParam = CURRENT_WINDOW.getPageParam();

var formFields = [
    {
        field: 'username',
        title: '用户',
        // type: 'text',
        // category: 'user',
        // validators: ['notEmpty'],
        // single: true,
        // onAfterChoose: function (rows) {
        //     if (!Gikam.isEmpty(rows)) {
        //         this.setData({
        //             userid: rows[0].id,
        //             username: rows[0].userName,
        //         });
        //     }
        // },

    }, {
        field: 'password',
        title: '密码',
        validators: ['notEmpty', 'strLength[0,64]'],
        type: 'Password',
    },];

function onBeforePageRender(next) {
    next();
}

function button_1_onclick() {
    pageParam.type = 'out';
    denglu();
}

function button_2_onclick() {
    pageParam.type = 'in';
    denglu();
}


function denglu() {
    let modal = Gikam.create('modal', {
        title: "用户登录",
        width: 400,
        height: 200,
    });
    var layout = Gikam.create('layout', {
        renderTo: modal.window.$dom,
        center: {
            items: [{
                type: 'btnToolbar',
                items: [{
                    type: 'button',
                    id: 'xfp-equipt-in-out-confirm',
                    text: 'GIKAM.BUTTON.CONFIRM',
                    class: 'blue',
                    onClick: function () {
                        confirm();
                    }
                }, {
                    type: 'button',
                    text: 'GIKAM.BUTTON.CANCEL',
                    onClick: function () {
                        modal.close();
                    }
                }]
            }]
        }
    });

    modal.bindButtonEnter('xfp-equipt-in-out-confirm');

    var form = Gikam.create('form', {
        id: 'xfp-equipt-in-out-user-info-form',
        renderTo: layout.options.center.$dom,
        columns: 1,
        titleWidth: 80,
        fields: formFields,
        autoSave: false,
        onRendered: function () {
            var form = Gikam.getComp('xfp-equipt-in-out-user-info-form');

            setTimeout(function () {
                form.getField('username').focus()
            }, 500)
        },
    });
}

function confirm() {

    var form = Gikam.getComp('xfp-equipt-in-out-user-info-form');
    if (form.validate()) {

        Gikam.getLastModal().window.showMask(true);
        var checkurl = baseUrl + '.checkPassword';

        Gikam.post(checkurl, Gikam.getJsonWrapper({ 'username': form.getData().username, 'password': form.getData().password }, [null, []])).done(function (res) {

            var editPagePath = "emc.equiptInOut.page.equiptInOutEditPage";
            workspace.window.loadPage(editPagePath, {
                type: pageParam.type,
                userid: form.getData().userid,
                username: form.getData().username
            });

        }).always(function () {
            Gikam.getLastModal().window.closeMask();
            Gikam.getLastModal().close();
        });
    }
}