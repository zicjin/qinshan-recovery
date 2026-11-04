/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */
Gikam.Vue.component('row-menu-tree', {
    name: 'rowMenuTree',
    props: {
        nodes: Array,
        parentNode: Object,
        level: Number
    },
    data: function () {
        return {
            moreNavType: false, // 导航一 更多按钮状态
            navList: this.nodes,
            moreNavList: []
        }
    },
    watch: {
        navList: {
            handler: function () {
                let _this = this;
                this.$nextTick(function () {
                    _this.moreNavListFn();
                });
            },
            deep: true
        }
    },
    inject: ['workspace'],
    methods: {
        leaveMenuNode: function () {
            this.setDom && clearTimeout(this.setDom);
            this.setDom = null;
            this.workspace.closeSelectActive();
        },

        clickMenuNode: function (node, e) {
            // e && e.stopPropagation();
            if (node.href) {
                this.activeLeafMenu(node);
                this.workspace.model.simpleMenuAddActive(node);
                return;
            }
        },

        toggleMenuNode: function (node, e) {
            // e && e.stopPropagation();
            this.workspace.closeSelectActive();
            Gikam.Vue.set(node, 'expand', false);
            Gikam.jQuery('.nav-tab-list>li.expand>.select-list-container').css('left', 0);

            e && this.countHeight(node, e);
            Gikam.jQuery('.top-toolbar>.nav-tab-list').css('overflow', 'visible');
            if (this.workspace.trigger('beforeMenuToggle', node) === false) {
                return false;
            }

            let expandFlag = Gikam.isEmpty(node.expand) ? true : !node.expand;
            if (expandFlag) {
                this.nodes.filter(function (item) {
                    return item.expand;
                }).forEach(function (item) {
                    Gikam.Vue.set(item, 'expand', !expandFlag);
                    Gikam.jQuery('.nav-tab-list .select-list-container').hide();
                });
            }
            Gikam.jQuery('.nav-tab-list> li').removeClass('expand');
            if (!e) {
                return;
            }
            if (e.currentTarget.id === 'menu-more') {
                if (Gikam.jQuery('.nav-tab-list>li.more').hasClass('expand')) {
                    Gikam.jQuery('.nav-tab-list>li.more').removeClass('expand');
                } else {
                    Gikam.jQuery('.nav-tab-list>li.more').addClass('expand');
                }
                Gikam.jQuery('.nav-tab-list>li.nav-li').removeClass('expand');
                // Gikam.Vue.set(node, 'expand', false);
            } else {
                if (Gikam.jQuery(e.currentTarget).hasClass('expand')) {
                    Gikam.jQuery('.nav-tab-list>li').removeClass('expand');
                } else {
                    Gikam.jQuery(e.currentTarget).addClass('expand');
                }
                Gikam.jQuery('.nav-tab-list>li.more .select-list-container').hide();
                Gikam.Vue.set(node, 'expand', expandFlag);
            }
            let domNode = Gikam.jQuery(e.currentTarget);
            this.setDom && clearTimeout(this.setDom);
            this.setDom = null;
            this.setDom = setTimeout(function (e) {
                domNode.children('.select-list-container').toggleClass('show').slideToggle('fast');
            }, 200);
        },

        // 计算高度
        countHeight: function (node, e) {
            let bodyHight = Gikam.jQuery('.main-container').height();
            let length = node.children && node.children.length;
            if (e.currentTarget.id === 'menu-more') {
                length = this.moreNavList.length;
            }
            let height = length * 30 + 16;
            if (bodyHight - 80 < height) {
                height = bodyHight - 80;
            }
            Gikam.jQuery(e.currentTarget).find('.select-list-ul').css('height', height + 'px');
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
                    objectId: node.id
                });
                this.workspace.window.load(this.workspace.options.activeLeafNode.href);
            }
        },

        rememberAllLeafNode: function (node) {
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

            this.workspace.options.shownActiveLeafNode.forEach(function (item) {
                item.active = false;
            })

            let shownNode = this.workspace.options.shownActiveLeafNode.filter(function (item) {
                return item.id === node.id;
            });
            if (Gikam.isNotEmpty(shownNode)) {
                shownNode[0].active = true;
            }
            // Gikam.Vue.set(this.parentNode, 'active', true);
            // this.workspace.options.activeMenuNode.push(node, this.parentNode);
            this.rememberAllLeafNode(node);
            // this.activeParentsNode(this.$parent);
            Gikam.extend(this.workspace.options.activeLeafNode, node);
            this.loadPage(node);
            this.workspace.moveLeafNodeToMore();
            this.workspace.options.homeShow = false;
        },

        getIcon: function (node, level) {
            if (level === 1) {
                return Gikam.isEmpty(node.icon) ? 'common' : node.icon;
            }
        },

        // 更多
        moreNavListFn: function () {
            let pw = Gikam.jQuery('.nav-tab-list').width();
            let cw = 48;
            let n = 0;
            if (Gikam.jQuery('.nav-tab-list>li.nav-li').length === 0) {
                Gikam.jQuery('.nav-tab-list>li.more').css("left", '48px');
            }
            for (let i = 0; i < Gikam.jQuery('.nav-tab-list>li.nav-li').length; i++) {
                cw += Gikam.jQuery('.nav-tab-list>li.nav-li').eq(i).width();
                Gikam.jQuery('.nav-tab-list>li.more').css("left", cw + 'px');
                if (cw > pw) {
                    this.moreNavType = true;
                    n = i;
                    this.navList = this.nodes.slice(0, n);
                    this.moreNavList = this.nodes.slice(n);
                    break;
                }
            }
        }
    },
    mounted: function () {
        let _this = this;
        this.moreNavListFn();
        Gikam.jQuery(document).on('click', 'body', function () {
            _this.workspace.closeSelectActive();
        });

        Gikam.jQuery(window).resize(function () {
            _this.workspace.closeSelectActive();
            Gikam.jQuery('.top-toolbar>.nav-tab-list').css('overflow', 'hidden');
            _this.navList = _this.nodes;
            _this.$nextTick(function () {
                _this.moreNavListFn();
            });
        });
    },
    template:
        '<ul class="nav-tab-list" :level="level" @mouseleave="leaveMenuNode">' +
        '<li :id="\'menu-\'+item.id" class="nav-li" v-for="item in navList" v-on:click="clickMenuNode(item,$event)" v-on:mouseenter="toggleMenuNode(item,$event)" :class="{expand:item.expand,leaf:item.href}" :key="item.id" :level="level">' +
        '<p :ref="item.id" :class="{active:item.active}">{{item.text}}</p>' +
        '<row-tree-select v-if="item.children" :nodes="item.children" :level="level+1" :parentNode="item"></row-tree-select>' +
        '</li>' +
        '<li id="menu-more" v-if="nodes.length !== navList.length" v-on:mouseenter="toggleMenuNode(moreNavList,$event)" class="more" v-show="moreNavType" :level="level">' +
        '<p>更多</p>' +
        '<row-tree-select  :nodes="moreNavList" :level="level+1"></row-tree-select>' +
        '</li>' +
        '</ul>'
});

Gikam.Vue.component('row-tree-select', {
    name: 'rowTreeSelect',
    props: {
        nodes: {
            type: Array,
            default: function () {
                return []
            }
        },
        parentNode: Object,
        level: Number,
        posit: {
            top: 0,
            left: 0
        }
    },

    data: function () {
        return {
            underNode: {},
            position: this.posit,
            scrollTop: 0
        }
    },

    watch: {
        position: {
            handler: function (newPosition) {
                this.position = newPosition;
            },
            deep: true
        }
    },

    inject: ['workspace'],
    methods: {
        handleScroll: function (top) {
            this.scrollTop = top.scrollTop;
        },

        toggleMenuNode: function (node, e) {
            // e.stopPropagation();
            this.position = {
                top: Gikam.jQuery(e.currentTarget)[0].offsetTop - 4 - this.scrollTop + 'px',
                left: Gikam.jQuery(e.currentTarget)[0].clientWidth + 'px'
            };
            this.underNode = node;
            // 计算高度
            this.countHeight(node, e);

            if (this.workspace.trigger('beforeMenuToggle', node) === false) {
                return false;
            }
            if (node.href) {
                return;
            }
            let expandFlag = Gikam.isEmpty(node.expand) ? true : !node.expand;
            let _this = this;
            if (expandFlag) {
                this.nodes.filter(function (item) {
                    return item.expand;
                }).forEach(function (item) {
                    Gikam.Vue.set(item, 'expand', false);
                    Gikam.jQuery(_this.$refs[item.id][0]).next('.select-list-container').slideToggle('fast');
                });
            }
            Gikam.Vue.set(node, 'expand', expandFlag);
            let domLi = Gikam.jQuery(e.currentTarget);
            domLi.addClass("active");
            domLi.siblings('li').removeClass('active');
            domLi.parents('.select-list-ul').next('.select-list-container').hide();
            domLi.parents('.select-list-ul').next('.select-list-container').find('.select-list-container').hide();
            this.setDomNav && clearTimeout(this.setDomNav);
            this.setDomNav = null;
            this.setDomNav = setTimeout(function () {
                domLi.parents('.select-list-ul').next('.select-list-container').toggleClass('show').slideToggle('fast');
                setTimeout(function () {
                    Gikam.jQuery('.nav-tab-list>li.expand>.select-list-container').css('left', 0);
                    if (Gikam.jQuery(document).width() - Gikam.jQuery(window).width() > 0) {
                        let left = Gikam.jQuery('.nav-tab-list>li.expand>.select-list-container').css('left').split('px')[0];
                        Gikam.jQuery('.nav-tab-list>li.expand>.select-list-container').css('left', left + Gikam.jQuery(window).width() - 2 - Gikam.jQuery(document).width() + 'px')
                    }
                }, 50);
            }, 200);
        },

        clickMenuNode: function (node, e) {
            // e && e.stopPropagation();
            if (node.href) {
                this.activeLeafMenu(node);
                this.workspace.model.simpleMenuAddActive(node);
                Gikam.jQuery(e.currentTarget).parents('.select-list-ul').click();
                Gikam.jQuery('.nav-tab-list>li .select-list-container').hide();
                Gikam.jQuery('.nav-tab-list> li').removeClass('expand');
                return;
            }
        },

        // 计算高度
        countHeight: function (node, e) {
            let _this = this;
            this.$nextTick(function () {
                let bodyHight = Gikam.jQuery('.main-container').height();
                let length = node.children && node.children.length;
                let height = length * 30 + 16;
                let poorHeight = 0;
                if (Gikam.jQuery(e.currentTarget)[0]) {
                    poorHeight = bodyHight - 80 - Gikam.jQuery(e.currentTarget)[0].offsetTop;
                }
                if (poorHeight + _this.scrollTop < height) {
                    this.position = {
                        left: Gikam.jQuery(e.currentTarget)[0].clientWidth + 'px',
                        top: '4px'
                    };
                    if (height > bodyHight - 80) {
                        height = bodyHight - 80;
                    }
                }

                Gikam.jQuery(e.currentTarget).parents('.select-list-ul').next('.select-list-container').children('.select-list-ul').css('height', height + 'px');
            });
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
                let window = Gikam.component.Window.windowManager[node.id].childNodes[0];
                this.workspace.window = Gikam.getInstance(window);
                document.getElementById('business-container').appendChild(this.workspace.window.model.$el);
                this.workspace.window.model.$el.querySelectorAll('.grid,.from,.tree').forEach(function (item) {
                    Gikam.getInstance(item).refresh();
                });
            } else {
                this.workspace.window = Gikam.create('window', {
                    renderTo: document.getElementById('business-container'),
                    title: this.workspace.options.activeLeafNode.text,
                    objectId: node.id
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
            this.workspace.options.shownActiveLeafNode.forEach(function (item) {
                item.active = false;
            })
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
        }
    },
    template:
        '<div class="select-list-container" :class="\'sel-list\'+level" :level="level" :style="posit">' +
        '<div class="select-list-ul">' +
        '<scroll @handle-scroll="handleScroll">' +
        '<ul class="nav2" :level="level">' +
        '<li v-for="item in nodes" :id="\'menu-\'+item.id" v-on:click="clickMenuNode(item,$event)" v-on:mouseenter="toggleMenuNode(item,$event)" :ref="item.id" :class="{active:item.active, \'has-icon\':!!(item.ext$ && item.ext$.menuiconurl)}" :level="level">' +
        '<div v-if="item.ext$ && item.ext$.menuiconurl" class="menu-img" :style=\'{backgroundImage:"url("+item.ext$.menuiconurl+")"}\'></div>' +
        '<div>{{item.text}}</div>' +
        '<div v-if="item.children" class="icon">' +
        '<nextPageImg v-bind="{color: \'#999\',width:\'14px\',height:\'10px\'}"/>' +
        '</div>' +
        '</li>' +
        '</ul>' +
        '</scroll>' +
        '</div>' +
        '<row-tree-select v-if="underNode.children" v-show="false" :nodes="underNode.children" :level="level+1" :posit="position" :parentNode="underNode"></row-tree-select>' +
        '</div>'
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
        '<li class="items"><div class="node-text" @click="modifyIndexPanelConfig">首页配置</div></li>' +
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

        getUserPermission: function () {
            Gikam.post().done(function (data) {
                this.userPermission = data;
            })
        },

        modifyIndexPanelConfig: function () {
            Gikam.create('IndexPanelConfig', {
                renderTo: Gikam.createDom('div', document.body),
                onAfterClose: function () { }
            })
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
        '<div class="item" :class="item.icon" :style=\'{backgroundImage:(item.ext$ && item.ext$.todoiconurl)?"url("+item.ext$.todoiconurl+")":null}\' v-for="item in options.todoList" :icon="item.icon" @click="activeMenuNode(item)">' +
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
            this.seekMenuData(this.workspaceViewModel.menuList, item);
        },
        // 查找相应的导航数据
        seekMenuData: function (list, item) {
            for (let i = 0; i < list.length; i++) {
                if (list[i].id === item.menuId) {
                    this.workspaceViewModel.enterFromHisTab(list[i]);
                    return false;
                }
                if (list[i].children) {
                    if (this.seekMenuData(list[i].children, item) === false) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
});

Gikam.Vue.component('index-panel-config', {
    data: function () {
        return {
            //panel数据集合
            panelList: [],
            //首页部分分4份，每份的宽度
            panelWidth: null,
            //自定义panel高度
            panelHeight: 300
        }
    },

    mounted: function () {
        this.getPanelConfig();
    },

    methods: {
        getPanelConfig: function () {
            var _this = this;
            Gikam.getJson(Gikam.IFM_CONTEXT + '/secure/core/module/item/workspace-panel-user-configs/layouts').done(function (data) {
                _this.panelList = data.panelList;
                _this.initLocation();
            })
        },

        initLocation: function () {
            var _this = this;
            this.panelWidth = (document.body.clientWidth - 18) / 4;
            this.panelList = this.panelList.map(function (item) {
                const config = JSON.parse(item.config);
                item.panelConfig = config;
                item.style = {
                    width: config.width * _this.panelWidth + 'px',
                    left: config.position[0] * _this.panelWidth + 'px',
                    top: config.position[1] * _this.panelHeight + 'px',
                    height: config.height * _this.panelHeight + 'px'
                }
                return item;
            })
        }
    },

    template:
        '<div class="panel-container" id="indexPanelConfig">' +
        '<div class="panel" v-for="item in panelList" :key="item.panelId" :style="item.style">' +
        '<div class="panel-body">' +
        '<div class="panel-title">{{item.panelConfig.title}}</div>' +
        '<div class="panel-content">' +
        '<iframe :src="item.url" frameborder="no" scrolling="no"></iframe>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>'
});

var IndexPage = function () {

    var defaultOptions = {
        skin: 'green', // 绿色(默认)/深蓝/浅蓝：green/blue/light-blue
        skinColor: { 'green': '#27986D', 'blue': '#0076d5', 'light-blue': '#33a7e2' },
        menuModeType: 'top',  // 顶部导航/侧边栏: top/left
        title: null,
        xIconUrl: null,
        I18NUrl: [],
        menuList: [],
        allMenuList: [],
        childrenNavNode: [], // 子导航-用于首页2样式
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
            this.model = this.createModelView();
            this.initSystemInfo();
            this.getMenuTree();
            this.getLoginUserInfo();
            this.bindWindowResizeEvent();
            const _this = this;
            this.model.$nextTick(function () {
                _this.trigger('pageLoaded');
                if (this.menuList.length > 0) {
                    Gikam.jQuery('.side-main-nav>.main-nav>.first-level-nav ul> li')[0].click();
                }
            });
            this.modifyAddressUrl();
        },

        setConfig: function (props) {
            Object.assign(defaultOptions, props);
        },


        // 关闭top导航的下拉，和选中状态
        closeSelectActive: function () {
            Gikam.jQuery('.select-list-container').hide();
            Gikam.jQuery('.nav-tab-list> li').removeClass('expand');
            Gikam.jQuery('.nav-tab-list>li>.select-list-container').css('left', 0);
        },

        modifyAddressUrl: function () {
            window.history.replaceState({ status: 0 }, '', '/');
        },

        //用于扩展
        setDefaultOptions: function (options) {
            Object.assign(this, options);
        },

        initSystemInfo: function () {
            Gikam.jQuery('title').text(this.options.title);
            Gikam.jQuery('#x-icon').attr('href', this.options.xIconUrl);
        },

        getMenuTree: function () {
            let _this = this;

            Gikam.post(IFM_CONTEXT + '/secure/core/module/sys/menus/tree/root').done(function (menuTree) {
                _this.options.menuList = menuTree;
                _this.model.$refs.rowMenuTree.navList = _this.options.menuList;
                _this.options.allMenuList = Gikam.jQuery.extend(true, [], _this.options.menuList);
                setTimeout(function () {
                    this.workspace.model.childrenNav(_this.options.menuList[0]);
                    Gikam.jQuery('.menu-left-' + _this.options.menuList[0].id).addClass('active');
                });
            });
        },

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

        bindWindowResizeEvent: function () {
            var _this = this;
            Gikam.jQuery(window).on('resize', function () {
                _this.moveLeafNodeToMore();
                _this.model.$refs.indexPanelConfig.initLocation();
            })
        },

        // 查询快捷菜单列表
        getSimpleMenuList: function () {
            let userId = workspace.user ? workspace.user.id : null;
            Gikam.post(IFM_CONTEXT + '/secure/core/module/sys/shortcut-menus/queries', Gikam.getJsonWrapper({ f: { createdById_SEQ: userId } }, [null, []])).done(function (data) {
                workspace.options.simpleMenuList = data.rows;
                workspace.model.simpleMenuAddActive(workspace.options.activeLeafNode);
            });
        },

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

                        _this.sidebarExpand = true;
                        Gikam.jQuery('.side-main-nav').css({ 'width': '0', 'height': '0' });
                        _this.closeRightMenu('.right-key-menu');
                        // _this.closeRightMenu('.right-simple-menu');
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

                methods: {
                    getIcon: function (node, level) {
                        if (level === 1) {
                            return Gikam.isEmpty(node.icon) ? 'common' : node.icon;
                        }
                    },

                    // 导航二-获取子导航
                    childrenNav: function (node, e) {
                        this.childrenNavNode = [];
                        e && Gikam.jQuery(e.currentTarget).siblings().removeClass('active');
                        e && Gikam.jQuery(e.currentTarget).addClass('active');
                        let navLength = node.children && node.children.length;
                        if (!navLength || navLength === 0) {
                            return;
                        }
                        let rem = navLength % 4; // 余数
                        let item = (navLength - rem) / 4;
                        if (rem === 0) {
                            this.childrenNavNode.push(node.children.slice(0, item));
                            this.childrenNavNode.push(node.children.slice(item, 2 * item))
                            this.childrenNavNode.push(node.children.slice(2 * item, 3 * item))
                            this.childrenNavNode.push(node.children.slice(3 * item))
                        } else if (rem === 1) {
                            this.childrenNavNode.push(node.children.slice(0, item + 1));
                            this.childrenNavNode.push(node.children.slice(item + 1, 2 * item + 1))
                            this.childrenNavNode.push(node.children.slice(2 * item + 1, 3 * item + 1))
                            this.childrenNavNode.push(node.children.slice(3 * item + 1))
                        } else if (rem === 2) {
                            this.childrenNavNode.push(node.children.slice(0, item + 1));
                            this.childrenNavNode.push(node.children.slice(item + 1, 2 * item + 2))
                            this.childrenNavNode.push(node.children.slice(2 * item + 2, 3 * item + 2))
                            this.childrenNavNode.push(node.children.slice(3 * item + 2))
                        } else {
                            this.childrenNavNode.push(node.children.slice(0, item + 1));
                            this.childrenNavNode.push(node.children.slice(item + 1, 2 * item + 2))
                            this.childrenNavNode.push(node.children.slice(2 * item + 2, 3 * item + 3))
                            this.childrenNavNode.push(node.children.slice(3 * item + 3))
                        }
                    },

                    // 点击 下级目录
                    otherNavNode: function (node) {
                        if (node.children) {
                            return;
                        } else {
                            this.enterFromHisTab(node);
                            this.sidebarExpand = true;
                            Gikam.jQuery('.side-main-nav').css({ 'width': '0', 'height': '0' });
                        }
                    },

                    cleanHistoryPageMorePanel: function () {
                        let panel = null;
                        this.historyMenuPanel && (panel = 'historyMenuPanel');
                        this.userSettingPanel && (panel = 'userSettingPanel');
                        this.todoPanel && (panel = 'todoPanel');
                        if (panel) {
                            this[panel].$destroy();
                            Gikam.removeDom(this[panel].$el);
                            this[panel] = null;
                            Gikam.jQuery('.page-switch-tab .more.active').removeClass('active');
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
                                this.workspace.model.$refs.rowMenuTree.clickMenuNode(node);
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
                        // this.closeRightMenu('.right-key-menu');
                    },
                    // 关闭右键目录
                    closeRightMenu: function (className) {
                        let menu = document.querySelector(className);
                        menu.style.display = 'none';
                    },
                    // 点击快捷菜单
                    toggleSimpleMenuNode: function (node, e) {
                        this.clearSimpleActive();
                        this.simpleMenuAddActive(node)
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
                        // this.closeRightMenu('.right-simple-menu');
                    },

                    sidebarSwitch: function (e) {
                        e.stopPropagation();
                        this.sidebarExpand = !this.sidebarExpand;
                        Gikam.jQuery('.side-main-nav').css({ 'width': '838px', 'height': '484px' });
                        if (this.sidebarExpand) {
                            Gikam.jQuery('.side-main-nav').css({ 'width': '0', 'height': '0' });
                        }
                    },

                    showMoreHistoryMenu: function (event) {
                        if (this.historyMenuPanel) {
                            return;
                        }
                        this.cleanHistoryPageMorePanel();
                        var $more = Gikam.jQuery(event.target);
                        $more.addClass('active');
                        let top = $more.offset().top + $more.outerHeight() + 2;
                        var left = $more.offset().left + $more.outerWidth() - 150;
                        this.historyMenuPanel = new Gikam.Vue({
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

                    showMaxWindow: function () {
                        if (!this.sidebarExpand) {
                            Gikam.jQuery('.side-main-nav').css({ 'width': '100%', 'height': '100%' });
                        }
                    },

                    showUserSettingPanel: function (event) {
                        if (this.userSettingPanel) {
                            return;
                        }
                        this.cleanHistoryPageMorePanel();
                        var $user = Gikam.jQuery(event.currentTarget);
                        let top = $user.offset().top + $user.outerHeight() + 2;
                        var left = $user.offset().left + $user.outerWidth() - 156;
                        this.userSettingPanel = new Gikam.Vue({
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
                        if (this.todoPanel) {
                            return;
                        }
                        this.cleanHistoryPageMorePanel();
                        let $platform = Gikam.jQuery(event.currentTarget);
                        let top = $platform.offset().top + $platform.outerHeight() + 2;
                        let left = $platform.offset().left - 250;
                        let _this = this;
                        this.todoPanel = new Gikam.Vue({
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
                            let result = item.text.includes(filterText);
                            if (!result && Gikam.isNotEmpty(item.children)) {
                                let children = _this.menuFilter(filterText, item.children);
                                if (Gikam.isNotEmpty(children)) {
                                    item.children = children;
                                    return true;
                                }
                            }
                            return result;
                        });
                    },

                    menuSearch: function (e) {
                        const _this = this;
                        const promise = Gikam.jQuery.Deferred();
                        if (Gikam.isEmpty(e.target.value)) {
                            this.menuList = Gikam.extend(true, [], _this.allMenuList);
                            this.$nextTick(function () {
                                Gikam.jQuery('ul.show').each(function (index, item) {
                                    item.style.display = '';
                                });
                                promise.resolve();
                            });
                        } else {
                            if (timeoutSearch) {
                                clearTimeout(timeoutSearch);
                            }
                            timeoutSearch = setTimeout(function () {
                                _this.menuList = _this.menuFilter(e.target.value, Gikam.extend(true, [], _this.allMenuList));
                            }, 500);
                        }
                        return promise;
                    }

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

