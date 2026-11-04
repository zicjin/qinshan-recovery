var coreCodeGenerateConfig = Gikam.getPageObject({
  getEditListColumns: function () {
    return [
      {
        checkbox: true,
      },
      {
        index: true,
      },
      {
        field: "id",
        title: "T_CORE_CODE_GEN_CONFIG.ID",
        type: "link",
      },
      {
        field: "menuName",
        title: "T_CORE_CODE_GEN_CONFIG.MENUNAME",
      },
      {
        field: "packageName",
        title: "T_CORE_CODE_GEN_CONFIG.PACKAGENAME",
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG.CREATEDTIME",
        field: "createdTime",
        width: 100,
      },
    ];
  },

  getBaseInfoFormFields: function () {
    var _this = this;
    return [
      {
        field: "packageName",
        title: "T_CORE_CODE_GEN_CONFIG.PACKAGENAME",
        validators: ["notEmpty"],
        colspan: 2,
      },
      {
        field: "menuId",
        title: "T_CORE_CODE_GEN_CONFIG.MENUID",
        validators: ["notEmpty"],
      },
      {
        field: "parentMenuId",
        title: "T_CORE_CODE_GEN_CONFIG.PARENTMENUID",
      },
      {
        field: "menuName",
        title: "T_CORE_CODE_GEN_CONFIG.MENUNAME",
        validators: ["notEmpty"],
      },
      {
        field: "author",
        validators: ["notEmpty"],
        title: "T_CORE_CODE_GEN_CONFIG.AUTHOR",
      },
      {
        field: "ext$.menuiconurl",
        title: "T_CORE_CODE_GEN_CONFIG.MENUICON",
        type: "image",
        height: 30,
        align: "center",
        colspan: 2,
        enlarged: false,
        onClick: function () {
          _this.getShowImgPanel();
        },
        onError: function (image, value) {
          return (
            "/static/icon/menu/peony/white/" +
            value.substring(value.lastIndexOf("/") + 1)
          );
        },
      },
      {
        field: "func",
        validators: ["notEmpty"],
        title: "T_CORE_CODE_GEN_CONFIG.FUNC",
        colspan: 2,
        type: "checkbox",
      },
    ];
  },

  getTableListColumns: function () {
    return [
      {
        checkbox: true,
      },
      {
        field: "tableName",
        title: "T_CORE_CODE_GEN_CONFIG_TABLE.TABLENAME",
        editor: true,
        validators: ["notEmpty"],
      },
      {
        field: "tableDesc",
        title: "T_CORE_CODE_GEN_CONFIG_TABLE.TABLEDESC",
        editor: true,
        validators: ["notEmpty"],
      },
      {
        field: "type",
        title: "T_CORE_CODE_GEN_CONFIG_TABLE.TYPE",
        editor: true,
        type: "select",
        category: "sys",
        width: 86,
        param: {
          codeCategoryId: "tableType",
        },
        validators: ["notEmpty"],
      },
      {
        field: "foreignField",
        title: "T_CORE_CODE_GEN_CONFIG_TABLE.FOREIGNFIELD",
        editor: true,
      },
    ];
  },

  getFieldListColumns: function () {
    return [
      {
        checkbox: true,
      },
      {
        index: true,
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.FIELD",
        field: "field",
        editor: true,
        validators: ["notEmpty"],
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.FIELDNAME",
        field: "fieldName",
        editor: true,
        validators: ["notEmpty"],
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.FIELDTYPE",
        field: "fieldType",
        type: "select",
        category: "sys",
        width: 108,
        editor: true,
        param: {
          codeCategoryId: "fieldType",
        },
        onChange: function (field, value, rowIndex, oldValue, text) {
          var pageDisplay = value === "varchar" ? 1 : 0;

          this.setData({
            index: rowIndex,
            mainDisplay: pageDisplay,
            detailDisplay: pageDisplay,
          });
        },
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.FIELDLENGTH",
        field: "fieldLength",
        editor: true,
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.REQUIRE",
        field: "require",
        type: "select",
        width: 135,
        category: "sys",
        editor: true,
        param: {
          codeCategoryId: "yesorno",
        },
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.DATAREQUIRE",
        field: "dataRequire",
        type: "select",
        category: "sys",
        width: 135,
        editor: true,
        param: {
          codeCategoryId: "yesorno",
        },
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.MAINDISPLAY",
        field: "mainDisplay",
        type: "select",
        category: "sys",
        width: 135,
        editor: true,
        param: {
          codeCategoryId: "yesorno",
        },
      },
      {
        title: "T_CORE_CODE_GEN_CONFIG_FIELD.DETAILDISPLAY",
        field: "detailDisplay",
        type: "select",
        category: "sys",
        editor: true,
        width: 135,
        param: {
          codeCategoryId: "yesorno",
        },
      },
    ];
  },

  getShowImgPanel: function () {
    var _this = this;

    if (_this.menuIconsList.length === 0) {
      Gikam.getJson(IFM_CONTEXT + "/secure/core/module/sys/menus/icons/queries")
        .done(function (data) {
          data.forEach(function (item) {
            item.id = item.iconName;
            item.src = item.iconUrl;
          });
          _this.menuIconsList = data;
          _this.imgPanelHandle(false);
        })
        .fail(function (e) {
          _this.imgPanelHandle(true);
        });
    } else {
      this.imgPanelHandle(false);
    }
  },

  imgPanelHandle: function (error) {
    var _this = this;

    var iconPanel = Gikam.create("iconPanel", {
      renderTo: workspace.window.$dom,
      id: "core-code-generate-detail-icon-panel",
      title:
        "CORE.MODULE.CODEGENERATOR.CODEGENERATECONFIGS.MODAL.MENU_ICON_CONFIG",
      data: _this.menuIconsList,
      noRequest: error,
      onConfirm: function (data) {
        var form = Gikam.getComp(
          "core-code-generate-config-detail-base-info-form"
        );
        var menuIconUrl = data.iconUrl
          ? IFM_CONTEXT + data.iconUrl.replace(/blue|black/g, "white")
          : error
          ? "/static/icon/menu/peony/white/" + data.id + ".svg"
          : null;
        form.setData({
          menuIcon: error ? data.id + ".svg" : data.iconName,
          ext$: {
            menuiconurl: menuIconUrl,
          },
        });

        iconPanel.cancel();
      },
    });
  },

  baseUrl:
    IFM_CONTEXT + "/secure/core/module/codegenerator/code-generate-configs",

  menuIconsList: [],
});
