/* Owned shell: authentication and unchanged business fragment hosting. */
(function () {
  'use strict';
  var pages = {
    personnel: { title: '人员查询', menuIds: ['100001'], path: '/secure/emc/module/mdm/emp/emps/page/edit-list?readonly=1', object: function () { return emcEmp.editPage; }, init: 'emcEmp.editPage.init' },
    equipment: { title: '设备查询', menuIds: ['101501', '20251000001'], path: '/secure/emc/module/mdm/equipt/equipts/page/edit-list?readonly=1', object: function () { return emcEquipt.editPage; }, init: 'emcEquipt.editPage.init' },
    consumables: { title: '耗材查询', menuIds: ['430201', '20250800003'], path: '/secure/emc/module/item/consume/consumes/page/edit-list?readonly=1', object: function () { return emcConsume.editPage; }, init: 'emcConsume.editPage.init' },
    roleMenuMatrix: { title: '角色菜单矩阵', menuIds: ['100900'], custom: true, init: function (target) { return window.OwnedRoleMenuMatrix.init(target); } },
    roleUserMatrix: { title: '角色用户矩阵', menuIds: ['100900'], custom: true, init: function (target) { return window.OwnedRoleUserMatrix.init(target); } },
    organizationManagement: { title: '组织管理', menuIds: ['100900'], custom: true, init: function (target) { return window.OwnedOrganizationManagement.init(target); } },
    roleManagement: { title: '角色管理', menuIds: ['100900'], custom: true, init: function (target) { return window.OwnedRoleManagement.init(target); } },
    menuManagement: { title: '菜单管理', menuIds: ['100900'], custom: true, init: function (target) { return window.OwnedMenuManagement.init(target); } },
    resultRuns: { title: '样品测试批结果', menuIds: ['300100'], custom: true, init: function (target) { return window.OwnedResultRuns.init(target); } },
    orderRetains: { title: '留样记录', menuIds: ['300200'], custom: true, init: function (target) { return window.OwnedOrderRetains.init(target); } },
    // Attachment download has its own per-file grant and is deliberately not
    // inferred from the personnel/equipment/consumables menu IDs.
    attachments: { title: '附件下载', always: true, custom: true, path: '/secure/emc/module/index/workspace/download-attachment', init: function (target) { return window.OwnedAttachmentDownload.init(target); } },
    attachmentManagement: { title: '附件管理', permissionKey: 'attachmentManagement', path: '/owned/attachments/management', object: function () { return window.OwnedAttachmentManagement; }, init: 'OwnedAttachmentManagement.init' }
  };
  var requestedPage = new URLSearchParams(location.search).get('page');
  var selected = null, authorized = Object.create(null), menuTree = [], menuState = 'unloaded';
  var sessionGeneration = 0, menuRequestVersion = 0, pageRequestVersion = 0;
  var loginView, workspaceView, status, mount;
  function setStatus(message, error) { status.textContent = message || ''; status.className = error ? 'error' : ''; }
  function clearWorkspace() { Gikam.reset(); mount.replaceChildren(); mount.setAttribute('aria-label', '查询工作区'); }
  function clearMenus() { authorized = Object.create(null); menuTree = []; menuState = 'unloaded'; Gikam.setCapabilities({ consumableImportExport: false, consumableStockWrite: false, consumableMaterialWrite: false, personnelMainWrite: false, equipmentWrite: false, equipmentPlanWrite: false }); document.getElementById('menu-nav').replaceChildren(); }
  function showLogin(message) { sessionGeneration++; menuRequestVersion++; pageRequestVersion++; clearWorkspace(); clearMenus(); setStatus(''); document.getElementById('session-name').textContent = ''; workspaceView.hidden = true; loginView.hidden = false; document.getElementById('logout').hidden = true; document.getElementById('login-error').textContent = message || ''; }
  function showWorkspace() { sessionGeneration++; menuRequestVersion++; pageRequestVersion++; setStatus(''); loginView.hidden = true; workspaceView.hidden = false; document.getElementById('logout').hidden = false; }
  function hasMenu(name) { var page = pages[name]; return !!page && (page.always || (page.permissionKey ? authorized[page.permissionKey] : page.menuIds.some(function (id) { return authorized[id]; }))); }
  function renderMenus() {
    var nav = document.getElementById('menu-nav'); nav.replaceChildren();
    Object.keys(pages).forEach(function (name) {
      if (!hasMenu(name)) return;
      var page = pages[name], link = document.createElement('a');
      link.id = name + '-link'; link.href = '/?page=' + encodeURIComponent(name); link.textContent = page.title;
      link.addEventListener('click', function (event) { event.preventDefault(); loadMenus(name).catch(function () {}); });
      nav.appendChild(link);
    });
    if (menuTree.length) {
      var tree = document.createElement('div'); tree.className = 'owned-menu-tree';
      appendMenuTree(menuTree, tree);
      nav.appendChild(tree);
    }
  }
  function appendMenuTree(nodes, parent) {
    (nodes || []).forEach(function (menu) {
      if (!menu || menu.id == null) return;
      var children = Array.isArray(menu.children) ? menu.children : [], text = menu.text || menu.menuName || menu.menuInitName || String(menu.id), href = menu.href || '';
      if (children.length) {
        var details = document.createElement('details'), summary = document.createElement('summary');
        summary.textContent = text; details.appendChild(summary);
        var nested = document.createElement('div'); nested.className = 'owned-menu-children'; appendMenuTree(children, nested); details.appendChild(nested); parent.appendChild(details);
      } else if (href) {
        var link = document.createElement('a'); link.textContent = text; link.href = href; link.setAttribute('data-menu-id', String(menu.id)); parent.appendChild(link);
      }
    });
  }
  function parseMenus(body) {
    var nodes = Array.isArray(body) ? body : (body && Array.isArray(body.menus) ? body.menus : null);
    if (!nodes) throw new Error('服务器未返回可用菜单');
    var result = Object.create(null);
    menuTree = nodes;
    function visit(menu) {
      if (!menu || menu.id == null) return;
      var id = String(menu.id);
      Object.keys(pages).forEach(function (name) { if (pages[name].menuIds && pages[name].menuIds.indexOf(id) >= 0) result[id] = true; });
      (Array.isArray(menu.children) ? menu.children : []).forEach(visit);
    }
    nodes.forEach(visit);
    return result;
  }
  function fetchMenus() {
    function decode(response) {
      return response.text().then(function (body) {
        var parsed; try { parsed = body ? JSON.parse(body) : null; } catch (error) { throw new Error('服务器未返回有效菜单'); }
        if (!response.ok) { var failure = new Error('当前账号菜单暂不可用（HTTP ' + response.status + '）'); failure.status = response.status; throw failure; }
        return parsed;
      });
    }
    return fetch('/secure/core/module/sys/menus/tree/root', { method: 'POST', credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }).then(decode).catch(function () {
      return fetch('/secure/core/module/sys/menus', { credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }).then(decode);
    });
  }
  function loadMenus(preferred) {
    var currentSession = sessionGeneration, currentRequest = ++menuRequestVersion;
    menuState = 'loading';
    return fetchMenus().then(function (body) {
      if (currentSession !== sessionGeneration || currentRequest !== menuRequestVersion) return null;
      authorized = parseMenus(body); menuState = 'ready'; renderMenus();
      function probe(url) {
        return fetch(url, { credentials: 'same-origin', cache: 'no-store', headers: { Accept: 'application/json' } }).then(function (response) {
          if (response.status === 403 || response.status === 404) return false;
          if (!response.ok) return false;
          return response.json().then(function (value) { return value && value.available === true; }, function () { return false; });
        }).catch(function () { return false; });
      }
        return Promise.all([probe('/owned/attachments/management-available'), probe('/owned/consumables/import-export-available'), probe('/owned/consumables/stock-write-available'), probe('/owned/consumables/material-write-available'), probe('/owned/personnel/main-write-available'), probe('/owned/equipment/write-available')]).then(function (available) {
        if (currentSession !== sessionGeneration || currentRequest !== menuRequestVersion) return null;
        authorized.attachmentManagement = available[0];
        authorized.consumableImportExport = available[1];
        authorized.consumableStockWrite = available[2];
        authorized.consumableMaterialWrite = available[3];
        authorized.personnelMainWrite = available[4];
        authorized.equipmentWrite = available[5];
          Gikam.setCapabilities({ consumableImportExport: available[1], consumableStockWrite: available[2], consumableMaterialWrite: available[3], personnelMainWrite: available[4], equipmentWrite: available[5], equipmentPlanWrite: available[5] });
        renderMenus();
        var names = Object.keys(pages).filter(function (name) { return !pages[name].always && hasMenu(name); });
        selected = preferred && hasMenu(preferred) ? preferred : (requestedPage && hasMenu(requestedPage) ? requestedPage : (names[0] || null));
        if (preferred && !hasMenu(preferred)) {
          clearWorkspace(); setStatus('当前账号无' + (pages[preferred] ? pages[preferred].title : '该入口') + '权限。', true); return null;
        }
        if (!selected) { clearWorkspace(); setStatus('当前账号没有可用的查询入口。当前账号无人员查询权限。可使用附件下载或退出。'); return null; }
        return loadOriginalPage(selected);
      });
    }).catch(function (error) {
      if (currentSession !== sessionGeneration || currentRequest !== menuRequestVersion) return null;
      // A successful login remains a live session. Hide every route when the
      // menu lookup fails, keep logout available, and require an explicit
      // retry/login rather than guessing a default page.
      authorized = Object.create(null); menuState = 'failed'; Gikam.setCapabilities({ consumableImportExport: false, consumableStockWrite: false, consumableMaterialWrite: false, personnelMainWrite: false, equipmentWrite: false, equipmentPlanWrite: false }); renderMenus(); clearWorkspace();
      setStatus('无法读取当前账号菜单，查询入口已关闭。' + (error.message || ''), true);
      return null;
    });
  }
  function loadOriginalPage(name) {
    if (!hasMenu(name)) {
      var denied = new Error('当前账号无' + (pages[name] ? pages[name].title : '该入口') + '权限。');
      denied.status = 403;
      return Promise.reject(denied);
    }
    var currentSession = sessionGeneration, currentRequest = ++pageRequestVersion;
    selected = name || selected; var page = pages[selected];
    clearWorkspace(); setStatus('正在打开' + page.title + '…'); mount.setAttribute('aria-label', page.title);
    if (page.custom) {
      window.workspace.window.$dom = mount; Gikam.setMount(mount);
      return Promise.resolve(page.init(mount)).then(function () { if (currentSession === sessionGeneration && currentRequest === pageRequestVersion) setStatus(''); });
    }
    return fetch(page.path, { credentials: 'same-origin', headers: { Accept: 'text/html' } }).then(function (response) {
      if (currentSession !== sessionGeneration || currentRequest !== pageRequestVersion) return null;
      if (!response.ok) { var error = new Error(response.status === 403 ? '当前账号无' + page.title + '权限，或会话已失效。' : '页面读取失败（HTTP ' + response.status + '）'); error.status = response.status; throw error; }
      return response.text();
    }).then(function (html) {
      if (html == null || currentSession !== sessionGeneration || currentRequest !== pageRequestVersion) return;
      var fragment = new DOMParser().parseFromString(html, 'text/html'), scripts = Array.from(fragment.querySelectorAll('script'));
      if (scripts.length !== 1 || scripts[0].src || !scripts[0].textContent.includes(page.init)) throw new Error(page.title + '页面片段格式不受支持。');
      window.workspace.window.$dom = mount; Gikam.setMount(mount); page.object().ready = false;
      var script = document.createElement('script'); script.textContent = scripts[0].textContent; mount.appendChild(script); setStatus('');
    }).catch(function (error) { if (currentSession !== sessionGeneration || currentRequest !== pageRequestVersion) throw error; clearWorkspace(); if (error.status === 401 || error.status === 403) showLogin(error.message); else setStatus(error.message, true); throw error; });
  }
  function login(event) {
    event.preventDefault(); var id = document.getElementById('login-id').value, passwordInput = document.getElementById('login-password'), password = passwordInput.value, submit = document.getElementById('login-submit');
    submit.disabled = true; document.getElementById('login-error').textContent = '';
    fetch('/open/security/public-key', { credentials: 'same-origin', cache: 'no-store' }).then(function (response) { if (!response.ok) throw new Error('无法取得登录公钥'); return response.text(); }).then(function (key) {
      var encrypted = window.OwnedRsa.encrypt(password, key), form = new URLSearchParams(); password = ''; passwordInput.value = ''; form.set('id', id); form.set('password', encrypted);
      return OwnedRequest('/core/module/sys/login', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'SECURITY-RSA-KEY': key, Accept: 'application/json' }, body: form });
    }).then(function (result) { if (!result || result.authenticated !== true) throw new Error('服务器未确认登录'); var identity = result.identity || {}; var label = identity.id || id; var organization = identity.orgName || identity.orgId; document.getElementById('session-name').textContent = organization ? label + ' · ' + organization : label; showWorkspace(); return loadMenus(); }).catch(function (error) { password = ''; passwordInput.value = ''; showLogin(error.message); }).finally(function () { submit.disabled = false; });
  }
  function logout() { var button = document.getElementById('logout'); button.disabled = true; sessionGeneration++; menuRequestVersion++; pageRequestVersion++; clearWorkspace(); clearMenus(); setStatus('正在退出登录…'); OwnedRequest('/core/module/sys/logout', { method: 'POST' }).then(function (result) { if (!result || result.authenticated !== false) throw new Error('服务器未确认退出'); showLogin('已退出登录。'); }).catch(function (error) { showLogin('退出尚未获服务器确认：' + error.message); }).finally(function () { button.disabled = false; }); }
  function boot() {
    loginView = document.getElementById('login-view'); workspaceView = document.getElementById('workspace-view'); status = document.getElementById('page-status'); mount = document.getElementById('owned-workspace');
    if (window.IndexPage) {
      window.IndexPage.setConfig({ ext$: { addomainsupport: '0', showloginauthoption: '0' }, id: 'login-config', pageStyle: 'kapok', pageSkin: 'green', loginOrg: '0', loginRole: '0', qrCode: '0', retrievePassword: '0', retrievePasswordWay: '1', mobileClient: '1' });
      new window.IndexPage({ system: '检测信息管理系统', title: '检测信息管理系统', xIconUrl: window.IFM_CONTEXT + '/static/emc/icon/favicon.ico', nativePassword: true, showExternalLink: false, logo: window.IFM_CONTEXT + '/static/emc/icon/logo.png', pageStyle: 'kapok', pageSkin: 'green' });
    }
    document.getElementById('login-form').addEventListener('submit', login); document.getElementById('logout').addEventListener('click', logout);
    window.addEventListener('owned-session-denied', function () { showLogin('当前会话已失效或查询权限已撤销。'); }); showLogin();
  }
  document.addEventListener('DOMContentLoaded', boot);
})();
