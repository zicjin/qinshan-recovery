/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

var IndexPage = function () {

    var defaultOptions = {
        errorMessage: null,
        requesting: false,
        language: 'zh',
        logo: void 0,
        data: {
            username: null,
            password: null,
            roleId: null,
            deptId: null
        }
    };

    var parent = Gikam.extend(Object.create(new Gikam.component.Base()), {

        createViewModel: function () {
            let _this = this;

            return new Gikam.Vue({
                el: '#app',

                data: function () {
                    return _this.options;
                },

                methods: {

                    keydownHandle: function (e) {
                        if (e.target.tagName === 'INPUT') {
                            parseInt(e.keyCode) === 13 && this.login(e.target);
                        }
                    },

                    changeHandle: function (field, value) {
                        Gikam.setFieldValue(this.data, field, value);
                    },

                    languageSwitch: function () {
                        this.language = this.language === 'zh' ? 'en' : 'zh';
                    },

                    validate: function () {
                        if (!this.data.username) {
                            this.errorMessage = '请输入用户名';
                            return false;
                        }
                        if (!this.data.password) {
                            this.errorMessage = '请输入密码';
                            return false;
                        }
                        this.errorMessage = null;
                        return true;

                    },

                    login: function () {
                        if (this.validate() === false) {
                            return;
                        }
                        let _this = this;
                        this.requesting = true;
                        Gikam.jQuery.ajax({
                            url: IFM_CONTEXT + '/core/module/sys/login',
                            type: 'post',
                            data: {
                                id: this.data.username,
                                password: Gikam.md5(this.data.password)
                            }
                        }).done(function () {
                            location.href = IFM_CONTEXT + '/module/index/workspaces';
                        }).fail(function (r) {
                            _this.requesting = false;
                            _this.errorMessage = Gikam.propI18N(JSON.parse(r.responseText).message);
                        });
                    }

                },

                computed: {
                    logoImage: function () {
                        return this.logo ? {
                            backgroundImage: `url(${this.logo})`
                        } : null
                    }
                }
            })
        },

        initSystemInfo: function () {
            Gikam.jQuery('title').text(this.options.title);
            Gikam.jQuery('#x-icon').attr('href', this.options.xIconUrl);
        },

        init: function () {
            let _this = this;
            Gikam.create('I18N', {
                url: this.options.I18NUrl,
                language: this.options.language
            }).done(function () {
                _this.model = _this.createViewModel();
                _this.initSystemInfo();
            });
        }
    });

    var IndexPage = function (options) {
        this.initialize(options, defaultOptions).init();
    };

    return Gikam.setPrototype(IndexPage, parent);
}();

Gikam.ignoreErrorUrl = ['/core/module/sys/logins/action/check-account'];
