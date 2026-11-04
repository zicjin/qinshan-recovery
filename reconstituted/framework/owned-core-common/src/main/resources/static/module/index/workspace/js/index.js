/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */


Gikam.Vue.component('menu-tree', {
    name: 'menuTree',
    props: {
        nodes: Array,
        parentNode: Object,
        level: Number,
        skin: String,
    },

    inject: ['workspace'],
    methods: {

        toggleMenuNode: function (node, e) {
            if (this.workspace.trigger('beforeMenuToggle', node) === false) {
                return false;
            }

            //判断是否为子节点
            if (node.href) {
                this.activeLeafMenu(node);
                this.workspace.model.simpleMenuAddActive(node);
                this.workspace.model.showMenuBySize();
                return;
            }

            let expandFlag = Gikam.isEmpty(node.expand) ? true : !node.expand;
            let _this = this;
            const expandNodes = _this.nodes.filter(function (item) {
                return item.expand;
            });
            /**
             * 判断
             * 1.expandFlag为true
             * 2.expandNodes.length>1时 是查询出来的树
             * 结果  立即收回所有展开的数   展开点击的那个树节点
             */
            if (expandFlag || expandNodes.length > 1) {
                expandNodes.forEach(function (item) {
                    Gikam.Vue.set(item, 'expand', false);
                    Gikam.jQuery(_this.$refs[item.id][0]).next('ul').slideUp('fast');
                });
            }
            /**
             * 1.多个节点展开时点击某个节点 （保证expand 为true）
             * 2.单个节点关闭时  单个节点展开时   赋值 取反
             */
            if (expandNodes.length > 1) {
                expandFlag = true
            }
            Gikam.Vue.set(node, 'expand', expandFlag);
            Gikam.jQuery(e.currentTarget).next('ul').slideToggle('fast');
        },

        activeParentsNode: function (menuTree) {
            if (menuTree.parentNode) {
                Gikam.Vue.set(menuTree.parentNode, 'active', true);
                this.activeParentsNode(menuTree.$parent);
                this.workspace.options.activeMenuNode.push(menuTree.parentNode);
            } else {
                this.workspace.options.activeFirstMenu = menuTree.nodes.filter(function (item) {
                    return item.active;
                });
            }
        },

        cleanActiveNode: function () {
            if (Gikam.isNotEmpty(this.workspace.options.activeMenuNode)) {
                this.workspace.options.activeMenuNode.forEach(function (item) {
                    Gikam.Vue.set(item, 'active', false);
                });
            }
        },

        loadPage: function (node) {
            if (this.workspace.window) {
                Gikam.component.Window.rememberWindow(this.workspace.window);
            }
            if (Gikam.component.Window.windowManager[node.id]) {
                let window = Gikam.component.Window.windowManager[node.id].children[0];
                this.workspace.window = Gikam.getInstance(window);
                document.getElementById('business-container').append(this.workspace.window.model.$el);
                this.workspace.window.model.$el.querySelectorAll('.grid,.from,.tree').forEach(function (item) {
                    Gikam.getInstance(item).refresh();
                });
            } else {
                this.workspace.window = Gikam.create('window', {
                    renderTo: document.getElementById('business-container'),
                    title: this.workspace.options.activeLeafNode.text,
                    objectId: node.id,
                });
                this.workspace.window.load(this.workspace.options.activeLeafNode.href);
            }
        },

        rememberAllLeafNode: function (node) {
            //去除包含在更多隐藏数组中的重复项
            this.workspace.options.moreActiveLeafNode.forEach(function (item, index, arr) {
                if (item.id === node.id) {
                    arr.splice(index, 1);
                }
            })

            if (!this.workspace.options.shownActiveLeafNode.some(function (item) {
                return item.id === node.id;
            })) {
                let _this = this;
                setTimeout(function () {
                    _this.workspace.options.shownActiveLeafNode.unshift(node);
                }, 100);
            }
        },

        activeLeafMenu: function (node) {
            if (node.id === this.workspace.options.activeLeafNode.id) {
                return;
            }
            this.cleanActiveNode();
            Gikam.Vue.set(node, 'active', true);
            let shownNode = this.workspace.options.shownActiveLeafNode.filter(function (item) {
                return item.id === node.id;
            });
            if (Gikam.isNotEmpty(shownNode)) {
                shownNode[0].active = true;
            }
            Gikam.Vue.set(this.parentNode, 'active', true);
            this.workspace.options.activeMenuNode.push(node, this.parentNode);
            this.rememberAllLeafNode(node);
            this.activeParentsNode(this.$parent);
            Gikam.extend(this.workspace.options.activeLeafNode, node);
            this.loadPage(node);
            this.workspace.moveLeafNodeToMore();
            this.workspace.options.homeShow = false;
        },

        getIcon: function (node, level) {
            let _str = "";
            _str = node.active && !node.expand ? 'high-light' : '';
            if (level === 1) {
                return Gikam.isEmpty(node.icon) ? 'common ' + _str : node.icon + ' ' + _str;
            }
        },
        // 右键是否添加为快捷菜单
        rightSimpleMenu: function (node, e) {
            this.workspace.model.rightSimpleMenu(node, e);
        },
    },
    filters: {
        arrowColor: function (skin, item) {
            let _color = '#fff'
            if (skin === 'white') {
                _color = item.active && !item.expand ? '#fff' : 'rgba(0,0,0,.85)'
            }
            return _color
        }
    },
    template:
        '<ul>' +
        '<li v-for="item in nodes" :class="{expand:item.expand,leaf:item.href}" :key="item.id" :level="level">' +
        '<div class="node" :id="\'menu-\'+item.id" v-on:click="toggleMenuNode(item,$event)" v-on:contextmenu.prevent="rightSimpleMenu(item, $event)" :ref="item.id" :class="{active:item.active}">' +
        '<div class="menu-icon" :class="getIcon(item,level)">' +
        '</div>' +
        '<span class="menu-text" :title="item.text">{{item.text}}</span>' +
        '<div v-if="item.children" class="arrow-icon"><arrow-down :color= "skin | arrowColor(item)"></arrow-down></div>' +
        '</div>' +
        '<menuTree v-if="item.children" :nodes="item.children" :skin = skin :level="level+1" :parentNode="item"></menuTree>' +
        '</li>' +
        '</ul>'
});

const historyPageMore = Gikam.Vue.component('historyPageMore', {
    props: ['options'],
    inject: ['workspace'],
    template:
        '<div class="history-page-more" :style="{top:getTop(options.top),left:getLeft(options.left)}" @click.stop="">' +
        '<ul class="active-leaf-node-more-container container" v-if="workspace.options.moreActiveLeafNode.length>0">' +
        '<li class="items" v-for="node in workspace.options.moreActiveLeafNode" :key="node.id">' +
        '<div class="node-text" @click="clickMoreTab(node)">{{node.text}}</div>' +
        '<div class="close-btn" @click="closeMoreActiveNode(node)"></div>' +
        '</li>' +
        '</ul>' +
        '<ul class="close-node-container container">' +
        '<li class="items"><div class="node-text" @click="closeCurrentMenu">关闭当前标签</div></li>' +
        '<li class="items"><div class="node-text" @click="closeAllCurrentMenu">关闭所有标签</div></li>' +
        '<li class="items"><div class="node-text" @click="closeOtherCurrentMenu(true)">关闭其他标签</div></li>' +
        '</li>' +
        '</ul>' +
        '</div>',
    methods: {
        getTop: function (top) {
            return top + 'px';
        },

        getLeft: function (left) {
            return left + 'px';
        },
        clickMoreTab: function (node) {
            this.closeMoreActiveNode(node);
            this.workspace.model.enterFromHisTab(node);
        },
        closeMoreActiveNode: function (node) {
            this.workspace.options.moreActiveLeafNode.splice(this.workspace.options.moreActiveLeafNode.indexOf(node), 1);
            this.workspace.options.allActiveLeafNode.splice(this.workspace.options.allActiveLeafNode.indexOf(node), 1);
        },
        // 关闭当前
        closeCurrentMenu: function (type) {
            let node = this.workspace.options.activeLeafNode;
            if (type === 'right') {
                node = this.workspace.options.rightActiveNode;
            }

            if (!node || !node.id) {
                this.workspace.model.cleanHistoryPageMorePanel();
                return;
            }
            this.workspace.model.cleanHistoryPageMorePanel();
            this.workspace.model.$refs['menuTab' + node.id][0].querySelector('.close').click();
        },
        // 关闭全部
        closeAllCurrentMenu: function () {
            Gikam.Vue.set(this.workspace.options, 'shownActiveLeafNode', []);
            Gikam.Vue.set(this.workspace.options, 'moreActiveLeafNode', []);
            Gikam.Vue.set(this.workspace.options, 'allActiveLeafNode', []);
            Gikam.Vue.set(this.workspace.options, 'activeLeafNode', {});
            this.workspace.options.activeMenuNode.forEach(function (item) {
                item.active = false;
            });
            this.workspace.model.cleanHistoryPageMorePanel();
            this.workspace.options.homeShow = true;
        },
        // 关闭其他标签
        closeOtherCurrentMenu: function (type) {
            this.activeTabIndex('left', type);
            this.activeTabIndex('right', type);
            this.workspace.model.cleanHistoryPageMorePanel();
        },
        // 删除active的tab的左||右
        activeTabIndex: function (key, type) {
            let node = type ? this.workspace.options.activeLeafNode : this.workspace.options.rightActiveNode;
            this.workspace.model.enterFromHisTab(node);
            let shownActiveIndex = this.workspace.options.shownActiveLeafNode.map(function (item, index, arr) {
                return item.id;
            }).indexOf(node.id);

            let allActiveIndex = this.workspace.options.allActiveLeafNode.map(function (item, index, arr) {
                return item.id;
            }).indexOf(node.id);

            if (key === "left") {
                this.workspace.options.shownActiveLeafNode.splice(0, shownActiveIndex);
                this.workspace.options.allActiveLeafNode.splice(0, allActiveIndex);
            } else if (key === "right") {
                Gikam.Vue.set(this.workspace.options, 'moreActiveLeafNode', []);
                this.workspace.options.shownActiveLeafNode.splice(shownActiveIndex + 1);
                this.workspace.options.allActiveLeafNode.splice(allActiveIndex + 1);
            }
        },
        // 关闭左侧标签
        closeLeftCurrentMenu: function () {
            this.activeTabIndex('left');
            this.workspace.model.cleanHistoryPageMorePanel();
        },
        // 关闭右侧标签
        closeRightCurrentMenu: function () {
            this.activeTabIndex('right');
            this.workspace.model.cleanHistoryPageMorePanel();
        },
    }
});

Gikam.Vue.component('userSettingPanel', {
    props: ['options'],
    inject: ['workspaceViewModel'],
    template:
        '<div class="user-setting-panel" :style="{top:getTop(options.top),left:getLeft(options.left)}" @click.stop="">' +
        '<ul class="container">' +
        '<li class="items user-info">' +
        '<div class="node-text">' +
        '<div class="name">{{workspaceViewModel.user.userName}}</div><div class="dept">{{workspaceViewModel.user.ext$.loginorgname}}</div></div>' +
        '</li>' +
        '<li class="items"><div class="node-text" @click="modifyPassword">修改密码</div></li>' +
        '<li class="items"><div class="node-text" @click="logout">退出登录</div></li>' +
        '</li>' +
        '</ul>' +
        '</div>',
    methods: {
        getTop: function (top) {
            return top + 'px';
        },

        getLeft: function (left) {
            return left + 'px';
        },

        toggleRole: function () {

        },

        modifyPassword: function () {
            Gikam.create('modal', {
                title: '修改密码',
                width: 400,
                height: 400,
                url: IFM_CONTEXT + '/secure/core/module/mdm/users/page/changePassword'
            });
        },

        logout: function () {
            Gikam.postText(IFM_CONTEXT + '/core/module/sys/logout').done(function () {
                location.href = IFM_CONTEXT + "/";
            });
        }
    }
});

Gikam.Vue.component('todoListPanel', {
    props: ['options'],
    inject: ['workspaceViewModel'],
    template:
        '<div class="todo-list-panel" :style="getPosition()" @click.stop="">' +
        '<scroll>' +
        '<div class="container">' +
        '<div class="item" :class="item.icon" v-for="item in options.todoList" :icon="item.icon" @click="activeMenuNode(item)">' +
        '<div class="badge">{{item.qty}}</div>' +
        '{{item.modelName}}' +
        '</div>' +
        '</div>' +
        '</scroll>' +
        '</div>',
    methods: {
        getPosition: function () {
            return {
                top: this.options.top + 'px',
                left: this.options.left + 'px',
                maxHeight: this.options.maxHeight + 'px',
                height: this.options.maxHeight + 'px'
            }
        },
        activeMenuNode: function (item) {
            document.getElementById('menu-' + item.menuId).click();
        }
    }
});

var IndexPage = function () {

    var defaultOptions = {
        skin: 'black',
        title: null,
        xIconUrl: null,
        I18NUrl: [],
        menuList: [],
        allMenuList: [],
        window: null,
        logo: null,
        fullScreenLogo: null,
        user: {
            ext$: {}
        },
        sidebarExpand: true,
        menuMode: 'all',
        cacheWindow: {},
        allActiveLeafNode: [],
        moreActiveLeafNode: [],
        shownActiveLeafNode: [],
        activeMenuNode: [],
        simpleMenuList: [], // 快捷菜单列表
        activeLeafNode: {}, // 点击选中的tab
        rightActiveNode: {}, // 右键选中的tab-node
        simpleActiveNode: {}, // 侧边栏右键添加/取消快捷菜单
        homeShow: true,
        indexPanelPath: void 0,
        todoNum: 0,
        customData: {},
        todoList: [],
        tabRightMenu: [
            { key: 'refresh', menu: '刷新' },
            { key: 'now', menu: '关闭当前标签' },
            { key: 'left', menu: '关闭左侧标签' },
            { key: 'right', menu: '关闭右侧标签' },
            { key: 'other', menu: '关闭其他标签' },
            { key: 'all', menu: '关闭所有标签' }
        ]
    };

    var parent = Gikam.extend(Object.create(new Gikam.component.Base()), {

        init: function () {
            this.model = this.createModelView();  //创建视图
            this.initSystemInfo();
            this.getMenuTree();
            this.getLoginUserInfo();
            this.bindWindowResizeEvent();
            const _this = this;
            this.model.$nextTick(function () {
                _this.trigger('pageLoaded');
            });
            this.modifyAddressUrl();
        },

        modifyAddressUrl: function () {
            window.history.replaceState({ status: 0 }, '', '/');
        },

        //用于扩展
        setDefaultOptions: function (options) {
            Object.assign(this, options);
        },

        //向head内动态插入信息
        initSystemInfo: function () {
            Gikam.jQuery('title').text(this.options.title);
            Gikam.jQuery('#x-icon').attr('href', this.options.xIconUrl);
        },

        //获取菜单数据 options.menuList + options.allMenuList
        getMenuTree: function () {
            let _this = this;
            Gikam.post(IFM_CONTEXT + '/secure/core/module/sys/menus/tree/root').done(function (menuTree) {
                _this.options.menuList = menuTree;
                _this.options.allMenuList = Gikam.jQuery.extend(true, [], menuTree);
            }).fail(function () {
                _this.options.allMenuList = Gikam.jQuery.extend(true, [], _this.options.menuList);
            });
        },

        //获取个人信息  存入user options.user
        getLoginUserInfo: function () {
            let _this = this;
            return Gikam.getText(IFM_CONTEXT + '/core/module/sys/logins/account').done(function (result) {
                var user = JSON.parse(result);
                user.isAdmin = 'sysAdmin'.indexOf(this.userType) >= 0;
                _this.user = _this.options.user = user;
                _this.trigger('userLoadSuccess', user);
                _this.getSimpleMenuList();
            });
        },

        moveLeafNodeToMore: function () {
            let _this = this;
            setTimeout(function () {
                let $lastNodeItem = Gikam.jQuery('.page-switch-container>.items:last-child');
                if ($lastNodeItem.length === 0) {
                    return;
                }
                let $more = Gikam.jQuery('.page-switch-tab>.more');
                if ($lastNodeItem.offset().left + $lastNodeItem.outerWidth() + 5 >= $more.offset().left) {
                    _this.options.moreActiveLeafNode.push(_this.options.shownActiveLeafNode.pop());
                }
            }, 100);
        },

        //绑定全局屏幕滚动事件
        bindWindowResizeEvent: function () {
            var _this = this;
            Gikam.jQuery(window).on('resize', function () {
                _this.moveLeafNodeToMore();
                _this.model.showMenuBySize();
            })
        },

        // 查询快捷菜单列表
        getSimpleMenuList: function () {
            let userId = workspace.user ? workspace.user.id : null;
            Gikam.post(IFM_CONTEXT + '/secure/core/module/sys/shortcut-menus/queries/lists').done(function (data) {
                workspace.options.simpleMenuList = data;
                workspace.model.simpleMenuAddActive(workspace.options.activeLeafNode);
            });
        },

        //new创建view视图
        createModelView: function () {
            var _this = this;
            let timeoutSearch = null;
            return new Gikam.Vue({

                el: '#app',

                provide: {
                    workspace: _this
                },

                data: function () {
                    return _this.options;
                },

                mounted: function () {
                    var _this = this;
                    document.addEventListener('click', function () {
                        _this.cleanHistoryPageMorePanel();
                        _this.closeRightMenu('.right-key-menu');
                        _this.closeRightMenu('.right-simple-menu');
                    });
                },

                computed: {
                    logoImage: function () {
                        return this.logo ? {
                            backgroundImage: 'url(' + this.logo + ')'
                        } : null
                    },

                    fullScreenLogoImage: function () {
                        return this.fullScreenLogo ? {
                            backgroundImage: 'url(' + this.fullScreenLogo + ')'
                        } : null
                    }
                },

                created: function () {
                    this.showMenuBySize();
                },

                methods: {

                    //清除 更多菜单 ，并销毁组建
                    cleanHistoryPageMorePanel: function () {
                        if (this.panel) {
                            this.panel.$el.parentNode.removeChild(this.panel.$el);
                            this.panel.$destroy();
                            this.panel = null;
                        }
                    },

                    switchMenuMode: function () {
                        this.menuMode = this.menuMode === 'all' ? 'simple' : 'all';
                    },

                    logout: function () {
                        Gikam.postText(IFM_CONTEXT + '/core/module/sys/logout').done(function () {
                            location.href = IFM_CONTEXT + "/";
                        });
                    },

                    enterFromHisTab: function (node) {
                        let dom = document.getElementById('menu-' + node.id);
                        this.simpleMenuAddActive(node);
                        if (!dom) {
                            const input = document.querySelector('.sidebar .menu-search>input');
                            if (input) input.value = '';
                            this.menuSearch({ target: { value: '' } }).done(function () {
                                dom = document.getElementById('menu-' + node.id);
                                dom.click();
                            });
                        } else {
                            dom.click();
                        }
                        this.homeShow = false;
                    },

                    closeTabPage: function (node) {
                        var shownActiveLeafNodeIndex = this.shownActiveLeafNode.indexOf(node);
                        if (node.id === this.activeLeafNode.id) {
                            this.activeMenuNode.forEach(function (node) {
                                node.active = false;
                            });
                            _this.window.destroy();
                            _this.window = null;
                            _this.options.activeLeafNode.id = null;
                            this.shownActiveLeafNode.splice(shownActiveLeafNodeIndex, 1);
                            this.allActiveLeafNode.splice(this.allActiveLeafNode.indexOf(node), 1);
                            if (this.shownActiveLeafNode.length === 0) {
                                this.homeShow = true;
                                return;
                            }
                            if (this.shownActiveLeafNode.length - 1 < shownActiveLeafNodeIndex) {
                                shownActiveLeafNodeIndex = this.shownActiveLeafNode.length - 1;
                            }
                            this.$refs['menuTab' + this.shownActiveLeafNode[shownActiveLeafNodeIndex].id][0].click();
                        } else {
                            this.shownActiveLeafNode.splice(shownActiveLeafNodeIndex, 1);
                            this.allActiveLeafNode.splice(this.allActiveLeafNode.indexOf(node), 1);
                        }
                        //将moreActiveLeafNode 数据 导入shownActiveLeafNode一个
                        if (this.moreActiveLeafNode.length > 0) {
                            this.shownActiveLeafNode.push(this.moreActiveLeafNode.shift());
                        }

                    },
                    // 鼠标右击显示tab操作
                    closeOtherTab: function (node, e) {
                        this.rightActiveNode = node;
                        let menu = document.querySelector(".right-key-menu");

                        menu.style.left = e.clientX + 'px';
                        menu.style.top = e.clientY + 'px';

                        menu.style.display = 'block';
                    },
                    rightRefreshTab: function () {
                        // 右键目录=>刷新按钮
                    },
                    // 点击右键目录
                    rightMenuClick: function (key) {
                        let rightMenuComp = new Gikam.Vue({
                            el: Gikam.jQuery('<history-page-more ref="rightMenuClick" :options="options" v-show="false" v-if="notDestroy" />').appendTo('body')[0],
                            provide: {
                                workspace: _this
                            },
                            data: function () {
                                return {
                                    notDestroy: true,
                                    options: {
                                        top: 0,
                                        left: 0
                                    }
                                };
                            }
                        });

                        let rightMenuFn = {
                            refresh: this.rightRefreshTab,
                            now: rightMenuComp.$children[0].closeCurrentMenu,
                            left: rightMenuComp.$children[0].closeLeftCurrentMenu,
                            right: rightMenuComp.$children[0].closeRightCurrentMenu,
                            other: rightMenuComp.$children[0].closeOtherCurrentMenu,
                            all: rightMenuComp.$children[0].closeAllCurrentMenu
                        };
                        if (key === 'refresh') {
                            rightMenuFn[key](this.activeLeafNode);
                        } else if (key === 'now') {
                            rightMenuFn[key]('right');
                        } else {
                            rightMenuFn[key]();
                        }
                        rightMenuComp.notDestroy = false;
                        rightMenuComp = null;
                        this.closeRightMenu('.right-key-menu');
                    },
                    // 关闭右键目录
                    closeRightMenu: function (className) {
                        let menu = document.querySelector(className);
                        menu.style.display = 'none';
                    },
                    // 点击快捷菜单
                    toggleSimpleMenuNode: function (node, e) {
                        this.clearSimpleActive();
                        this.simpleMenuAddActive(node);
                        this.showMenuBySize();
                        document.getElementById('menu-' + node.menuId).click();
                    },
                    // 给快捷菜单加active属性
                    simpleMenuAddActive: function (node) {
                        this.clearSimpleActive();
                        let shownNode = this.simpleMenuList.filter(function (item) {
                            return item.menuId === node.id;
                        });
                        if (Gikam.isNotEmpty(shownNode)) {
                            shownNode[0].active = true;
                        }
                    },
                    // 清空快捷菜单选中样式
                    clearSimpleActive: function () {
                        this.simpleMenuList.forEach(function (item) {
                            Gikam.Vue.set(item, 'active', false);
                        });
                    },
                    // 右键是否添加为快捷菜单
                    rightSimpleMenu: function (node, e) {
                        this.simpleActiveNode = node;
                        if (node.children) {
                            return;
                        }
                        let menu = document.querySelector(".right-simple-menu");
                        menu.style.left = e.clientX + 'px';
                        menu.style.top = e.clientY - 142 + 'px';
                        menu.style.display = 'block';
                    },
                    // 快捷菜单添加和取消
                    simpleMenuChange: function (type) {
                        if (type === 'add') {
                            let json = {
                                menuId: this.simpleActiveNode.id
                            };
                            // 比较防止重复添加（后台没做快捷菜单去重处理）
                            for (let i = 0; i < this.simpleMenuList.length; i++) {
                                if (this.simpleMenuList[i].menuId === this.simpleActiveNode.id) {
                                    return;
                                }
                            }
                            Gikam.post(IFM_CONTEXT + '/secure/core/module/sys/shortcut-menus', Gikam.getJsonWrapper(null, [null, [json]])).done(function (data) {
                                parent.getSimpleMenuList();
                            });
                        }

                        if (type === 'cancel') {
                            let json = {
                                menuId: this.simpleActiveNode.menuId,
                                id: this.simpleActiveNode.id
                            };
                            Gikam.del(IFM_CONTEXT + '/secure/core/module/sys/shortcut-menus', Gikam.getJsonWrapper(null, ['coreShortcutMenuServiceImpl', [json]])).done(function (data) {
                                parent.getSimpleMenuList();
                            });
                        }
                        this.closeRightMenu('.right-simple-menu');
                    },

                    sidebarSwitch: function () {
                        this.sidebarExpand = !this.sidebarExpand;
                    },

                    showMoreHistoryMenu: function (event) {
                        if (this.panel) {
                            return;
                        }
                        var $more = Gikam.jQuery(event.target);
                        let top = $more.offset().top + $more.outerHeight() + 2;
                        var left = $more.offset().left + $more.outerWidth() - 150;
                        this.panel = new Gikam.Vue({
                            el: Gikam.jQuery('<history-page-more :options="options"/>').appendTo('body')[0],
                            provide: {
                                workspace: _this
                            },
                            data: function () {
                                return {
                                    options: {
                                        top: top,
                                        left: left
                                    }
                                };
                            }
                        });
                    },

                    showUserSettingPanel: function (event) {
                        if (this.panel) {
                            return;
                        }
                        var $user = Gikam.jQuery(event.currentTarget);
                        let top = $user.offset().top + $user.outerHeight() + 2;
                        var left = $user.offset().left + $user.outerWidth() - 156;
                        this.panel = new Gikam.Vue({
                            el: Gikam.jQuery('<user-setting-panel :options="options"/>').appendTo('body')[0],
                            provide: {
                                workspaceViewModel: this
                            },
                            data: function () {
                                return {
                                    options: {
                                        top: top,
                                        left: left
                                    }
                                };
                            }
                        });
                    },

                    showTodoListPanel: function (event) {
                        if (this.panel) {
                            return;
                        }
                        let $platform = Gikam.jQuery(event.currentTarget);
                        let top = $platform.offset().top + $platform.outerHeight() + 2;
                        let left = $platform.offset().left - 250;
                        let _this = this;
                        this.panel = new Gikam.Vue({
                            el: Gikam.jQuery('<todo-list-panel :options="options"/>').appendTo('body')[0],
                            provide: {
                                workspaceViewModel: this
                            },
                            data: function () {
                                return {
                                    options: {
                                        top: top,
                                        left: left,
                                        maxHeight: Gikam.jQuery(window).height() / 3,
                                        todoList: _this.todoList
                                    }
                                };
                            }
                        });
                    },

                    switchHomeAndBiz: function () {
                        this.homeShow = true;
                        _this.options.activeLeafNode = {};
                        _this.options.shownActiveLeafNode.forEach(function (item) {
                            item.active = false;
                        })
                    },

                    menuFilter: function (filterText, menuList) {
                        const _this = this;
                        return menuList.filter(function (item) {
                            let result = item.text.toLowerCase().includes(filterText);
                            if (!result && Gikam.isNotEmpty(item.children)) {
                                let children = _this.menuFilter(filterText, item.children);
                                if (Gikam.isNotEmpty(children)) {
                                    item.children = children;
                                    Gikam.Vue.set(item, 'expand', true);
                                    return true;
                                }
                            }
                            if (result) {
                                Gikam.Vue.set(item, 'expand', true);
                                if (Gikam.isNotEmpty(item.children)) {
                                    Gikam.each(item.children, function (index, item) {
                                        Gikam.Vue.set(item, 'expand', true);
                                    })
                                }
                            }
                            return result;
                        });
                    },

                    //搜索框input事件
                    menuSearch: function (e) {
                        const _this = this;
                        const promise = Gikam.jQuery.Deferred();
                        if (Gikam.isEmpty(e.target.value)) {
                            clearTimeout(timeoutSearch);//去除定时任务
                            this.menuList = Gikam.extend(true, [], _this.allMenuList);
                            this.$nextTick(function () {
                                Gikam.each(Gikam.jQuery('.expand .menu ul>li>ul'), function (index, item) {
                                    Gikam.jQuery(item).slideUp('fast');
                                })
                                promise.resolve();
                            });
                        } else {
                            if (timeoutSearch) {
                                clearTimeout(timeoutSearch);
                            }
                            timeoutSearch = setTimeout(function () {
                                _this.menuList = _this.menuFilter(e.target.value.toLowerCase(), Gikam.extend(true, [], _this.allMenuList));

                                _this.$nextTick(function () {
                                    Gikam.each(Gikam.jQuery('.expand .menu ul>li>ul'), function (index, item) {
                                        Gikam.jQuery(item).slideDown('fast');
                                    })
                                })
                            }, 500);
                        }
                        return promise;
                    },

                    //当屏幕尺寸在1366px~768px之间时  关闭菜单树
                    showMenuBySize: function () {
                        if (self.innerWidth > 768 && self.innerWidth < 1366) {
                            this.sidebarExpand = false;
                        } else {
                            this.sidebarExpand = true;
                        }
                    },
                }
            });
        },

        getTodoList: function () {
            const _this = this;
            const icons = ['ybjs', 'ypgl', 'ywsl', 'rwzp', 'bgbz'];
            Gikam.post(IFM_CONTEXT + '/core/module/sys/todos').done(function (data) {
                _this.options.todoNum = data.rows.reduce(function (total, item) {
                    return total + item.qty;
                }, 0);
                _this.options.todoList = data.rows.map(function (item, index) {
                    item.icon = icons[index % 5];
                    return item;
                });
            });
        },

        initTodo: function () {
            const _this = this;
            Gikam.getConstantValue('WORKSPACE-REFRESH-FREQUENCY').done(function (r) {
                const interval = Math.round(1000 * 60 * parseFloat(r.constantValue || 0.5));
                _this.getTodoList();
                setInterval(function () {
                    _this.getTodoList();
                }, interval);
            });

        }

    });

    var IndexPage = function (options) {
        this.listeners = {
            beforeMenuToggle: function (node) {
            },
            beforeCreate: function () {
                return new Gikam.jQuery.Deferred().resolve();
            },
            pageLoaded: function () { },
            userLoadSuccess: function () { },
        };
        this.initialize(options, defaultOptions);
        var _this = this;
        this.trigger('beforeCreate').then(function () {
            if (_this.options.indexPanelPath) {
                return Gikam.getText(_this.options.indexPanelPath);
            }
            return Gikam.jQuery.Deferred().resolve();
        }).done(function (html) {
            if (html) {
                document.querySelector('#home-container>scroll').innerHTML = html;
            }
        }).then(function () {
            return Gikam.create('I18N', {
                url: _this.options.I18NUrl,
                language: _this.options.language
            });
        }).done(function () {
            _this.init();
            _this.initTodo();
        });
    };

    return Gikam.setPrototype(IndexPage, parent);
}();