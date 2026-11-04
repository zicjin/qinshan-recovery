coreCustomChart.editPage = {
  getGridParam: function () {
    var _this = this;
    return {
      type: "grid",
      id: "core-custom-chart-edit-list-grid",
      url: coreCustomChart.baseUrl + "/queries/raw",
      columns: coreCustomChart.getEditListColumns(),
      service: "coreCustomChartServiceImpl",
      onCellClick: function (field, row) {
        field === "chartNo" && _this.loadDetailPage(row.id, row.publishStatus);
      },
      deleteFormatter: function (row) {
        return row.chartName || row.id;
      },
      onRowActive: function (index, row) {
        Gikam.getAsyncComp("core-custom-chart-version-edit-list-grid").done(
          function (grid) {
            grid.refresh({
              url: coreCustomChart.baseUrl + "/query-version",
              requestData: {
                id: row.id,
                chartName: row.chartName,
              },
            });
          }
        );
      },
      toolbar: [
        {
          type: "button",
          text: "GIKAM.BUTTON.INSERT",
          icon: "add",
          onClick: function () {
            Gikam.preInsert({
              id: "core-custom-chart-edit-list-form",
              modalTitle: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.ADD",
              fields: coreCustomChart.getBaseInfoFormFields(),
              url: coreCustomChart.baseUrl,
              titleWidth: 100,
              disableNotEmptyValidate: true,
            }).done(function (id) {
              _this.loadDetailPage(id, "");
            });
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.COPY", //复制
          icon: "copy",
          onClick: function () {
            _this.copy();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.MODIFY", //变更
          icon: "modify",
          onClick: function () {
            _this.modify();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.DELETE",
          icon: "remove",
          onClick: function () {
            _this.remove();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.PUBLISHTOMENU", //发布至菜单
          icon: "activate",
          onClick: function () {
            _this.publishToMenu();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.PUBLISHTOWKPANEL", //发布至首页桌面
          icon: "activate",
          onClick: function () {
            _this.publishToWKPanel();
          },
        },
        {
          type: "button",
          text: "CORE.MODULE.ITEM.CUSTOMCHARTS.BUTTON.REVOKE", //撤销
          icon: "disable",
          onClick: function () {
            _this.revoke();
          },
        },
      ],
    };
  },
  modify: function () {
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (Gikam.isEmpty(selectedRows)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }
    if (selectedRows[0].publishStatus === "noPublish") {
      Gikam.alert("CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.YES_PUBLISH");
      return;
    }
    workspace.window.showMask(true);
    Gikam.put(
      coreCustomChart.baseUrl + "/action/modify",
      Gikam.getJsonWrapper({}, ["CoreCustomChartServiceImpl", selectedRows])
    )
      .done(function () {
        Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
      })
      .always(function () {
        workspace.window.closeMask();
      });
  },
  copy: function () {
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (Gikam.isEmpty(selectedRows)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }
    workspace.window.showMask(true);
    Gikam.put(
      coreCustomChart.baseUrl + "/action/copy",
      Gikam.getJsonWrapper({}, ["CoreCustomChartServiceImpl", selectedRows])
    )
      .done(function () {
        Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
      })
      .always(function () {
        workspace.window.closeMask();
      });
  },
  publishToWKPanel: function () {
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (selectedRows.length != 1) {
      Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
      return;
    }
    if (selectedRows[0].publishStatus !== "noPublish") {
      Gikam.alert("CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.YES_PUBLISH_ITEM");
      return;
    }
    if (selectedRows[0].wkPanelId) {
      Gikam.create("confirm", {
        title: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.MODEL.PUBLISHTOMENU"
        ),
        message: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISH_AGAIN"
        ),
        onYesClick: function () {
          Gikam.put(
            coreCustomChart.baseUrl + "/action/publish-wkpanel",
            Gikam.getJsonWrapper(null, [
              "CoreCustomChartServiceImpl",
              selectedRows,
            ])
          )
            .done(function () {
              Gikam.alert(
                "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISHWKPANELSUCCESS_ITEM"
              );
              Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
            })
            .always(function () {});
        },
      });
    } else {
      Gikam.create("confirm", {
        title: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.CONFIRM_TO_PUBLISHTOWKPANEL"
        ),
        message: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.WHETHER_TO_PUBLISHTOWKPANEL_FOLLOWING_ITEMS"
        ),
        onYesClick: function () {
          Gikam.put(
            coreCustomChart.baseUrl + "/action/publish-wkpanel",
            Gikam.getJsonWrapper(null, [
              "CoreCustomChartServiceImpl",
              selectedRows,
            ])
          )
            .done(function () {
              Gikam.alert(
                "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISHWKPANELSUCCESS_ITEM"
              );
              Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
            })
            .always(function () {});
        },
      });
    }
  },
  publishToMenu: function () {
    var _this = this;
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (selectedRows.length != 1) {
      Gikam.alert("GIKAM.TIP.CHOOSE_ONE_ITEM");
      return;
    }
    if (selectedRows[0].publishStatus !== "noPublish") {
      Gikam.alert("CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.YES_PUBLISH_ITEM");
      return;
    }
    if (selectedRows[0].menuId) {
      Gikam.create("confirm", {
        title: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.MODEL.PUBLISHTOMENU"
        ),
        message: Gikam.propI18N(
          "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISH_AGAIN"
        ),
        onYesClick: function () {
          Gikam.put(
            coreCustomChart.baseUrl + "/action/publish-menu",
            Gikam.getJsonWrapper(
              {
                id: selectedRows[0].id,
                chartName: selectedRows[0].chartName,
                menuId: selectedRows[0].menuId,
              },
              ["CoreCustomChartServiceImpl", selectedRows]
            )
          )
            .done(function () {
              Gikam.alert(
                "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISHSUCCESS_ITEM"
              );
              Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
            })
            .always(function () {});
        },
      });
    } else {
      _this.selectedChartName = selectedRows[0].chartName;
      var modal = Gikam.create("modal", {
        id: "CORE.MODULE.ITEM.CUSTOMCHARTS.MODEL.PUBLISHTOMENU",
        width: 500,
        height: 350,
        showCloseBtn: true,
      });
      Gikam.create("layout", {
        renderTo: modal.window.$dom,
        north: {
          height: 5,
          items: [
            {
              type: "btnToolbar",
              items: [
                {
                  type: "button",
                  text: "GIKAM.BUTTON.CONFIRM",
                  icon: "default",
                  onClick: function () {
                    var data = Gikam.getComp("form").getData();
                    workspace.window.showMask(true);
                    Gikam.put(
                      coreCustomChart.baseUrl + "/action/publish-menu",
                      Gikam.getJsonWrapper(
                        {
                          id: selectedRows[0].id,
                          chartName: selectedRows[0].chartName,
                          menuIcon: data.icon,
                          parentMenuId: data.menuId,
                          parentMenuName: data.menuName,
                          menuId: selectedRows[0].menuId,
                        },
                        ["CoreCustomChartServiceImpl", selectedRows]
                      )
                    )
                      .done(function () {
                        Gikam.getComp(
                          "core-custom-chart-edit-list-grid"
                        ).refresh();
                      })
                      .always(function () {
                        Gikam.alert(
                          "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.PUBLISHSUCCESS_ITEM"
                        );
                        workspace.window.closeMask();
                        Gikam.getLastModal().close();
                      });
                  },
                },
                {
                  type: "button",
                  text: "GIKAM.BUTTON.CANCEL",
                  icon: "cancel",
                  onClick: function () {
                    Gikam.getLastModal().close();
                  },
                },
              ],
            },
          ],
        },
        center: {
          items: [
            {
              type: "tab",
              fill: true,
              panels: [
                {
                  title: "CORE.MODULE.ITEM.CUSTOMCHARTS.MODEL.PUBLISHTOMENU",
                  items: [
                    {
                      type: "form",
                      id: "form",
                      columns: 1,
                      fields: coreCustomChart.getMenuInfoFormFields(
                        _this.selectedChartName
                      ),
                    },
                  ],
                },
              ],
            },
          ],
        },
      });
    }
  },
  loadDetailPage: function (id, publishStatus) {
    if (
      publishStatus !== "noPublish" &&
      publishStatus != null &&
      publishStatus != ""
    ) {
      url = "/page/display/display-menu";
      param = {
        id: id,
        detailMenu: "detailMenu",
      };
    } else {
      url = "/page/detail";
      param = {
        id: id,
      };
    }
    workspace.window.load(coreCustomChart.baseUrl + url + Gikam.param(param));
  },

  revoke: function () {
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (Gikam.isEmpty(selectedRows)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }
    for (var i = 0; i < selectedRows.length; i++) {
      var publishStatus = selectedRows[i].publishStatus;
      //只能撤回已发布的
      if (publishStatus === "noPublish") {
        Gikam.alert("CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.CHOOSE_PUBLISH_ITEM");
        return;
      }
    }
    Gikam.create("confirm", {
      title: Gikam.propI18N(
        "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.CONFIRM_TO_REVOKE"
      ),
      message: Gikam.propI18N(
        "CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.CONFIRM_TO_REVOKE"
      ),
      onYesClick: function () {
        Gikam.put(
          coreCustomChart.baseUrl + "/action/revoke",
          Gikam.getJsonWrapper(null, [
            "CoreCustomChartServiceImpl",
            selectedRows,
          ])
        ).done(function (data) {
          Gikam.getComp("core-custom-chart-edit-list-grid").refresh();
        });
      },
    });
  },

  remove: function () {
    var selectedRows = Gikam.getComp(
      "core-custom-chart-edit-list-grid"
    ).getSelections();
    if (Gikam.isEmpty(selectedRows)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }
    //只能删除未启用的(.some()用于数组中是否满足指定条件)
    for (var i = 0; i < selectedRows.length; i++) {
      var publishStatus = selectedRows[i].publishStatus;
      if (publishStatus !== "noPublish") {
        Gikam.alert("CORE.MODULE.ITEM.CUSTOMCHARTS.TIP.CHOOSE_ONPUBLISH_ITEM");
        return;
      }
    }
    Gikam.getComp("core-custom-chart-edit-list-grid").deleteRows(
      coreCustomChart.baseUrl
    );
  },

  create: function () {
    var _this = this;
    Gikam.create("layout", {
      id: "core-custom-chart-edit-list-layout",
      renderTo: workspace.window.$dom,
      center: {
        items: [_this.getGridParam()],
      },
    });
  },

  init: function () {
    this.create();
  },
};
