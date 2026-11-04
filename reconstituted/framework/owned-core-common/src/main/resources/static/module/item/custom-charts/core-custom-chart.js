var coreCustomChart = Gikam.getPageObject({
  getEditListColumns: function () {
    return [
      {
        checkbox: true,
      },
      {
        index: true,
      },
      {
        field: "chartNo",
        title: "T_CORE_CUSTOM_CHART.CHARTNO",
        type: "link",
        width: 200,
      },
      {
        field: "chartName",
        title: "T_CORE_CUSTOM_CHART.CHARTNAME",
        editor: true,
        width: 200,
        onBeforeEditorRender: function (row) {
          if ("publish" == row.publishStatus || "1" != row.version) {
            return false;
          }
          return true;
        },
      },
      {
        field: "publishStatus",
        title: "T_CORE_CUSTOM_CHART.PUBLISHSTATUS",
        type: "select",
        category: "publish-status",
        width: 200,
      },
      {
        field: "publishGoal",
        title: "T_CORE_CUSTOM_CHART.PUBLISHGOAL",
        width: 200,
      },
      {
        field: "menuName",
        title: "T_CORE_CUSTOM_CHART.MENUNAME",
        width: 200,
      },
      {
        field: "createdByName",
        title: "T_CORE_CUSTOM_CHART.CREATEDBYNAME",
        width: 200,
      },
      {
        field: "createdTime",
        type: "dateTime",
        title: "T_CORE_CUSTOM_CHART.CREATEDTIME",
        width: 200,
      },
      {
        field: "version",
        title: "T_CORE_CUSTOM_CHART.VERSION",
        width: 200,
      },
    ];
  },

  menuIconsList: [],
  todoIconsList: [],
  getMenuInfoFormFields: function (selectedChartName) {
    var _this = this;
    return [
      {
        field: "chartName",
        title: "T_CORE_MENU.MENUNAME",
        preInsert: true,
        value: selectedChartName,
        readonly: true,
      },
      {
        field: "menuIconUrl",
        title: "T_CORE_MENU.MENUICON",
        type: "image",
        height: 30,
        align: "center",
        enlarged: false,
        preInsert: true, //预新增的标识
        onClick: function () {
          _this.getShowImgPanel("menuIconUrl");
        },
        onError: function (image, value) {
          return (
            "/static/icon/menu/peony/white/" +
            value.substring(value.lastIndexOf("/") + 1)
          );
        },
      },
      {
        field: "menuName",
        title: "T_CORE_MENU.ID",
        type: "choose",
        category: "menu",
        single: true,
        targetFields: [
          {
            menuId: "id",
          },
          {
            menuName: "menuName",
          },
        ],
        preInsert: true,
        validators: ["notEmpty"],
      },
    ];
  },

  getBaseInfoFormFields: function () {
    return [
      {
        field: "chartName",
        title: "T_CORE_CUSTOM_CHART.CHARTNAME",
        preInsert: true,
        validators: ["notEmpty", "strLength[0,30]"],
      },
    ];
  },

  getShowImgPanel: function (field) {
    var _this = this;
    var listObj = {
      menuIconUrl: this.menuIconsList,
      todoIconUrl: this.todoIconsList,
    };
    var urlObj = {
      menuIconUrl: _this.menuUrl + "/icons/queries",
      todoIconUrl: _this.menuUrl + "/todo-icons/queries",
    };
    if (listObj[field].length === 0) {
      Gikam.getJson(urlObj[field])
        .done(function (data) {
          data.forEach(function (item) {
            item.id = item.iconName;
            item.src = IFM_CONTEXT + item.iconUrl;
          });
          field === "menuIconUrl" && (_this.menuIconsList = data);
          field === "todoIconUrl" && (_this.todoIconsList = data);
          _this.imgPanelHandle(field, false);
        })
        .fail(function (e) {
          _this.imgPanelHandle(field, true);
        });
    } else {
      this.imgPanelHandle(field, false);
    }
  },

  imgPanelHandle: function (field, error) {
    var _this = this;
    var listObj = {
      menuIconUrl: this.menuIconsList,
      todoIconUrl: this.todoIconsList,
    };
    var titleObj = {
      menuIconUrl: Gikam.propI18N(
        "CORE.MODULE.ITEM.CUSTOMCHARTS.TYPE.MENUICONSCONFIG"
      ), //菜单图标配置
      todoIconUrl: Gikam.propI18N(
        "CORE.MODULE.ITEM.CUSTOMCHARTS.TYPE.WAITICONSCONFIG"
      ), //待办图标配置
    };
    var iconPanel = Gikam.create("iconPanel", {
      renderTo: workspace.window.$dom,
      id: "core-menu-edit-list-icon-panel",
      title: titleObj[field],
      data: listObj[field],
      noRequest: error,
      onConfirm: function (data) {
        var form = Gikam.getComp("form");
        var iconUrl = data.iconUrl
          ? data.iconUrl.replace(/blue|black/g, "white")
          : error
          ? "/static/icon/menu/peony/white/" + data.id + ".svg"
          : null;
        field === "menuIconUrl" &&
          form.setData({
            icon: error ? data.id + ".svg" : data.iconName,
            menuIconUrl: iconUrl,
          });
        field === "todoIconUrl" &&
          form.setData({
            todoIcon: data.iconName,
            todoIconUrl: iconUrl,
          });
        iconPanel.cancel();
      },
    });
  },

  // getCustomChartVersionListColumns : function(){
  //     return [ {
  //         index : true
  //     }, {
  //         field : 'version',
  //         title : 'T_CORE_CUSTOM_CHART.VERSION',
  //         type: 'link'
  //     }];
  // },

  baseUrl: IFM_CONTEXT + "/secure/core/module/item/custom-charts",
  menuUrl: IFM_CONTEXT + "/secure/core/module/sys/menus",
});
