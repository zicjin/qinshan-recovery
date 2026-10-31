"use strict";
Gikam.extend(Gikam, {
  width: {
    S: 800,
    N: 900,
    M: 1000,
    L: 1100,
    XL: 1200,
    XXL: 1300,
    XXXL: 1400,
  },

  getConstantValue: function (key) {
    return Gikam.getJson(
      IFM_CONTEXT + "/secure/core/module/sys/constants/" + key
    );
  },

  getPermisionTypeAdmin: function (permisionTypeId) {
    return Gikam.getText(
      IFM_CONTEXT +
        "/core/module/sys/role-permissions/permision-type-admin/" +
        permisionTypeId
    );
  },

  viewAudit: function (tableName, id) {
    if (Array.isArray(id) && Gikam.isEmpty(id)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }
    var trail = Gikam.audit.trail;

    if (trail.indexOf("audit-trail") === -1) {
      Gikam.viewAuditLog(tableName, id);
      return;
    }

    var url = Gikam.printf(
      IFM_CONTEXT + "/secure/core/module/sys/at-configs/{tableName}",
      {
        tableName: tableName,
      }
    );
    Gikam.getJson(url).done(function (r) {
      if (r.auditTrail !== "1") {
        Gikam.alert("GIKAM.AT.NO_ACTIVATED_CONFIG");
      } else {
        Gikam.create("modal", {
          url:
            IFM_CONTEXT +
            "/secure/core/module/sys/at-configs/page/record-detail" +
            Gikam.param({
              tableName: tableName,
              id: Array.isArray(id) ? id[0].id : id,
            }),
          title: "GIKAM.AT.TITLE.VIEW",
          width: 900,
        });
      }
    });
  },

  viewAuditLog: function (tableName, id) {
    var url = Gikam.printf(
      IFM_CONTEXT + "/secure/core/module/sys/audit/audit-configs/{tableName}",
      {
        tableName: tableName,
      }
    );

    Gikam.getJson(url).done(function (r) {
      if (r.auditable !== "1") {
        Gikam.alert("GIKAM.AT.NO_ACTIVATED_CONFIG");
      } else {
        Gikam.create("modal", {
          url:
            IFM_CONTEXT +
            "/secure/core/module/sys/audit/audit-logs/page/record-detail" +
            Gikam.param({
              tableName: tableName,
              id: Array.isArray(id) ? id[0].id : id,
            }),
          title: "GIKAM.AT.TITLE.VIEW",
          width: 900,
        });
      }
    }).fail(function () {
      Gikam.alert("GIKAM.AT.NO_ACTIVATED_CONFIG");
    });
  },

  viewReportByConfigLine: function (reportConfigLineId, paramValueJson) {
    paramValueJson = Gikam.extend(paramValueJson, paramValueJson.ext$);
    // 将key值转成小写
    for (var key in paramValueJson) {
      paramValueJson[key.toLocaleLowerCase()] = paramValueJson[key];
      if (key.toLocaleLowerCase() != key) {
        delete paramValueJson[key];
      }
    }

    if (!Gikam.isEmpty(reportConfigLineId)) {
      var url =
        IFM_CONTEXT + "/secure/core/module/sys/report-configs/lines/queries";
      Gikam.post(
        url,
        Gikam.getJsonWrapper({
          f: {
            id_SEQ: reportConfigLineId,
            filter_bizCategory: "master",
          },
        })
      ).done(function (r) {
        if (r.total >= 1) {
          var reportLine = r.rows[0];
          var fileName = encodeURIComponent(reportLine.ext$.filename);
          var param = null;
          if (!Gikam.isEmpty(reportLine.param)) {
            param = reportLine.param.replace(/{[\w]+}/g, function (match) {
              return match.toLocaleLowerCase();
            });
          }

          var reportPath = null;
          if (
            reportLine.reportCategory === "dataCenter" &&
            !Gikam.isEmpty(reportLine.dataCenterReportUrl)
          ) {
            // 数据中心报表
            reportPath = reportLine.dataCenterReportUrl;
          } else {
            // 帆软报表
            reportPath = reportLine.reportConfigLineNo + "/" + fileName;
          }
          var reportViewUrl = Gikam.getViewReportUrl(
            reportLine.reportCategory,
            reportPath,
            param,
            paramValueJson
          );
          Gikam.create("modal", {
            src: reportViewUrl,
            title: "CORE.MODULE.SYS.REPORTS.TITLE.REPORTVIEW",
            iframe: true,
            width: Gikam.width.L,
          });
        }
      });
    } else {
      Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_FAILURE");
    }
  },

  viewReport: function (reportConfigId, paramValueJson) {
    paramValueJson = Gikam.extend(paramValueJson, paramValueJson.ext$);
    // 将key值转成小写
    for (var key in paramValueJson) {
      paramValueJson[key.toLocaleLowerCase()] = paramValueJson[key];
      if (key.toLocaleLowerCase() != key) {
        delete paramValueJson[key];
      }
    }

    var reportLineQueryUrl = Gikam.printf(
      "/secure/core/module/sys/report-configs/{id}/lines/queries",
      {
        id: reportConfigId,
      }
    );
    Gikam.post(
      IFM_CONTEXT + reportLineQueryUrl,
      Gikam.getJsonWrapper({
        f: {
          activatedFlag_SEQ: "1",
          filter_bizCategory: "master",
        },
      })
    ).done(function (r) {
      if (r.total === 0) {
        Gikam.alert("CORE.MODULE.SYS.REPORTS.TIP.VIEW_REPORT_FAILURE");
        return;
      } else if (r.total === 1) {
        var reportLine = r.rows[0];
        var fileName = encodeURIComponent(reportLine.ext$.filename);
        var param = null;
        if (!Gikam.isEmpty(reportLine.param)) {
          param = reportLine.param.replace(/{[\w]+}/g, function (match) {
            return match.toLocaleLowerCase();
          });
        }

        var reportPath = null;
        if (
          reportLine.reportCategory === "dataCenter" &&
          !Gikam.isEmpty(reportLine.dataCenterReportUrl)
        ) {
          // 数据中心报表
          reportPath = reportLine.dataCenterReportUrl;
        } else {
          // 帆软报表
          reportPath = reportLine.reportConfigLineNo + "/" + fileName;
        }

        var reportViewUrl = Gikam.getViewReportUrl(
          reportLine.reportCategory,
          reportPath,
          param,
          paramValueJson
        );
        Gikam.create("modal", {
          src: reportViewUrl,
          title: "CORE.MODULE.SYS.REPORTS.TITLE.REPORTVIEW",
          iframe: true,
          width: Gikam.width.L,
        });
      } else {
        Gikam.create("modal", {
          url:
            IFM_CONTEXT +
            "/secure/core/module/sys/report-configs/page/line-select" +
            Gikam.param({
              reportConfigId: reportConfigId,
            }),
          title: "CORE.MODULE.SYS.REPORTS.LINE_SELECT.TAB.CHOOSE_REPORT",
          width: 400,
          height: 300,
          onAfterClose: function (data) {
            if (Gikam.isEmpty(data)) {
              return;
            }
            var selectedRow = null;
            for (var i = 0; i < r.rows.length; i++) {
              if (r.rows[i].id == data) {
                selectedRow = r.rows[i];
              }
            }
            var fileName = encodeURIComponent(selectedRow.ext$.filename);
            var param = null;
            if (!Gikam.isEmpty(selectedRow.param)) {
              param = selectedRow.param.replace(/{[\w]+}/g, function (match) {
                return match.toLocaleLowerCase();
              });
            }

            var reportPath = null;
            if (
              selectedRow.reportCategory === "dataCenter" &&
              !Gikam.isEmpty(selectedRow.dataCenterReportUrl)
            ) {
              // 数据中心报表
              reportPath = selectedRow.dataCenterReportUrl;
            } else {
              // 帆软报表
              reportPath = selectedRow.reportConfigLineNo + "/" + fileName;
            }
            var reportViewUrl = Gikam.getViewReportUrl(
              selectedRow.reportCategory,
              reportPath,
              param,
              paramValueJson
            );
            Gikam.create("modal", {
              src: reportViewUrl,
              title: "CORE.MODULE.SYS.REPORTS.TITLE.REPORTVIEW",
              iframe: true,
              width: Gikam.width.L,
            });
          },
        });
      }
    });
  },

  getViewReportUrl: function (
    reportCategory,
    reportPath,
    param,
    paramValueJson
  ) {
    var reportViewUrl = null;
    if (reportCategory === "dataCenter") {
      // 数据中心报表：报表全地址当作reportPath传入
      reportViewUrl = reportPath;
    } else {
      // 帆软报表
      if (Gikam.report.deployMode === "integration") {
        // 集成部署
        if (Gikam.report.version === "9.0") {
          reportViewUrl =
            IFM_CONTEXT + "/ReportServer?" + reportCategory + "=" + reportPath;
        } else if (Gikam.report.version === "10.0") {
          reportViewUrl = IFM_CONTEXT + "/decision/view";
          if (reportCategory === "reportlet") {
            reportViewUrl += "/report?viewlet=" + reportPath;
          } else if (reportCategory === "formlet") {
            reportViewUrl += "/form?viewlet=" + reportPath;
          }
        } else if (Gikam.report.version === "11.0") {
          reportViewUrl = IFM_CONTEXT + "/decision/view";
          if (reportCategory === "reportlet") {
            reportViewUrl += "/report?viewlet=" + reportPath;
          } else if (reportCategory === "formlet") {
            reportViewUrl += "/form?viewlet=" + reportPath;
          }
        }
      } else if (Gikam.report.deployMode === "independent") {
        // 独立部署
        var serverUrl = Gikam.report.independentDeployServerUrl;
        if (Gikam.report.version === "9.0") {
          reportViewUrl =
            serverUrl + "/ReportServer?" + reportCategory + "=" + reportPath;
        } else if (Gikam.report.version === "10.0") {
          reportViewUrl = serverUrl + "/decision/view";
          if (reportCategory === "reportlet") {
            reportViewUrl += "/report?viewlet=" + reportPath;
          } else if (reportCategory === "formlet") {
            reportViewUrl += "/form?viewlet=" + reportPath;
          }
        } else if (Gikam.report.version === "11.0") {
          reportViewUrl = serverUrl + "/decision/view";
          if (reportCategory === "reportlet") {
            reportViewUrl += "/report?viewlet=" + reportPath;
          } else if (reportCategory === "formlet") {
            reportViewUrl += "/form?viewlet=" + reportPath;
          }
        }
      }
    }

    if (!Gikam.isEmpty(param)) {
      if (reportCategory === "dataCenter") {
        // 数据中心报表
        reportViewUrl += "?" + param;
      } else {
        // 帆软报表
        reportViewUrl += "&" + param;
      }

      // 添加用户、角色、部门参数
      if (param.indexOf("{loginuserid}") != -1) {
        // 参数中传了登录用户编码
        if (
          paramValueJson == null ||
          Gikam.isEmpty(paramValueJson.loginuserid)
        ) {
          if (paramValueJson == null) {
            paramValueJson = {};
          }
          paramValueJson.loginuserid = workspace.user.id;
        }
      }

      if (param.indexOf("{loginorgid}") != -1) {
        // 参数中传了当前登录部门编码
        if (
          paramValueJson == null ||
          Gikam.isEmpty(paramValueJson.loginorgid)
        ) {
          if (paramValueJson == null) {
            paramValueJson = {};
          }
          paramValueJson.loginorgid = workspace.user.ext$.loginorgid;
        }
      }

      if (param.indexOf("{loginroleid}") != -1) {
        // 参数中传了当前登录角色编码
        if (
          paramValueJson == null ||
          Gikam.isEmpty(paramValueJson.loginroleid)
        ) {
          if (paramValueJson == null) {
            paramValueJson = {};
          }
          paramValueJson.loginroleid = workspace.user.ext$.loginroleid;
        }
      }
    }
    return Gikam.printf(reportViewUrl, paramValueJson);
  },

  viewElnTmpl: function (elnTmplId) {
    var url = Gikam.printf(
      IFM_CONTEXT +
        "/secure/core/module/eln/eln-tmpls/{id}/top-versions/queries",
      {
        id: elnTmplId,
      }
    );
    var elnVersionId;
    Gikam.getJsonSync(url).done(function (version) {
      elnVersionId = version.id;
    });

    if (!Gikam.isEmpty(elnVersionId)) {
      var url =
        IFM_CONTEXT +
        "/static/plugin/spread-sheets-11.2.6/designer/index/index.html" +
        Gikam.param({
          operateType: 0,
          elnTmplId: elnTmplId,
          versionId: elnVersionId,
          IFM_CONTEXT: IFM_CONTEXT,
        });
      window.open(url, "eln");
    } else {
      Gikam.alert("CORE.MODULE.ITEM.ELN.TMPLS.TIP.NO_VERSION");
      return;
    }
  },

  getYesOrNoSelectItems: function () {
    return [
      {
        text: Gikam.propI18N("GIKAM.SELECT.YES"),
        value: 1,
      },
      {
        text: Gikam.propI18N("GIKAM.SELECT.NO"),
        value: 0,
      },
    ];
  },

  downloadFileById: function (fileId) {

    Gikam.getJson(Gikam.printf(IFM_CONTEXT + '/core/module/item/files/{id}', {
      id : fileId
    })).done(function(file) {
        Gikam.download(IFM_CONTEXT + file.downloadUrl);
    });

  },




  expdp: function (tableName, data) {
    if (Gikam.isEmpty(data)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }

    workspace.window.showMask(true);
    Gikam.postText(
      IFM_CONTEXT + "/secure/core/module/sys/dp-configs/action/export",
      Gikam.getJsonWrapper(
        {
          tableName: tableName,
        },
        ["", data]
      )
    )
      .done(function (url) {
        var downloadUrl = decodeURIComponent(url);
        if (downloadUrl.indexOf("http") == -1) {
          downloadUrl = Gikam.IFM_CONTEXT + downloadUrl;
        }
        Gikam.download(downloadUrl);
      })
      .always(function () {
        workspace.window.closeMask();
      });
  },

  importByTmplCode: function (tmplCode) {
    if (Gikam.isEmpty(tmplCode)) {
      Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CODE_REQUIRED');
      return;
    }
    var tmplId = Gikam.getTmplIdByCode(tmplCode);
    if(Gikam.isEmpty(tmplId)) {
      Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CODE_NOT_EXISTED');
      return;
    }


    var def = Gikam.getDeferred();

    Gikam.importExcel({
      url : IFM_CONTEXT + '/secure/core/module/item/import-tmpls/' + tmplId + '/files/{id}/action/import',
      dbTable : 'T_CORE_IMPORT_TMPL',
      onImportSuccess : function(files) {
        Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.IMPORT_SUCCSS');
        def.resolve();
      }
    });

    return def;
  },

  getTmplIdByCode: function (tmplCode) {
    if (Gikam.isEmpty(tmplCode)) {
      return;
    }
    var tmplId = null;
    Gikam.postSync(IFM_CONTEXT + '/secure/core/module/item/import-tmpls/queries', Gikam.getJsonWrapper({
      f : {
        tmplCode_SEQ : tmplCode
      },
      n : -1,
      s : 1
    })).done(function(r) {
      var data = r.rows;
      if (data.length == 0) {
        return;
      } else {
        tmplId = data[0].id;
      }
    });
    return tmplId;
  },

  exportByTmplCode: function (tmplCode) {
    if (Gikam.isEmpty(tmplCode)) {
      Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CODE_REQUIRED');
      return;
    }
    var tmplId = Gikam.getTmplIdByCode(tmplCode);
    if(Gikam.isEmpty(tmplId)) {
      Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.CODE_NOT_EXISTED');
      return;
    }
    Gikam.post(IFM_CONTEXT + '/core/module/item/files/queries', Gikam.getJsonWrapper({
      f : {
        targetId_SEQ : 'T_CORE_IMPORT_TMPL$' + tmplId
      },
      n : -1,
      s : 1
    })).done(function(r) {
      var data = r.rows;
      if (data.length == 0) {
        Gikam.alert('CORE.MODULE.ITEM.IMPORTTMPLS.TIP.NO_FILE');
        return;
      } else {
        Gikam.download(Gikam.getFileContextUrl(data[0].downloadUrl));
      }
    });
  },

});

!(function () {
  var fileUploadParam = {
    server: IFM_CONTEXT + "/core/module/item/files",
    searchUrl: IFM_CONTEXT + "/core/module/item/files/queries",
    deleteUrl: IFM_CONTEXT + "/core/module/item/files",
  };

  Gikam.override(Gikam.component.Uploader, {
    defaultOptions: fileUploadParam,
  });

  Gikam.override(Gikam.component.SimpleUploader, {
    defaultOptions: fileUploadParam,
  });
})();

Gikam.bpmn = {
  openProcessStatus: function (dbTable, id) {
    Gikam.create("modal", {
      title: "CORE.MODULE.SYS.T_CORE_BPMN_PROC.BUTTON.VIEW",
      url: Gikam.printf(
        IFM_CONTEXT +
          "/secure/core/module/sys/bpmn-exts/page/process-status-detail?id={id}&table={table}",
        {
          id: id,
          table: dbTable,
        }
      ),
    });
  },
};

Gikam.bp = {
  approve: function (gridId, bpCode, bpNodeCode) {
    var selectedRows = Gikam.getComp(gridId).getSelections();
    if (Gikam.isEmpty(selectedRows)) {
      Gikam.alert("GIKAM.TIP.CHOOSE_AT_LEAST_ONE_ITEM");
      return;
    }

    if (Gikam.isEmpty(bpCode) || Gikam.isEmpty(bpNodeCode)) {
      Gikam.alert("GIKAM.TIP.NEED_BPCODE");
      return;
    }

    // 请求节点动作
    Gikam.post(
      IFM_CONTEXT +
        "/secure/core/module/lcdp/bp-managers/bp-node-actions/queries",
      Gikam.getJsonWrapper(
        {
          bpCode: bpCode,
          bpNodeCode: bpNodeCode,
        },
        ["", selectedRows]
      )
    ).done(function (bpNodeActions) {
      var bpNodeActionSelectOptions = bpNodeActions.map(function (
        bpNodeAction
      ) {
        return {
          text: bpNodeAction.bpActionName,
          value: bpNodeAction.id,
        };
      });

      // 创建审核意见录入弹框

      var formFields = [
        {
          field: "actionId",
          title: "T_LCDP_BIZ_PROCESS_NODE_ACTION.BPACTIONNAME",
          type: "select",
          validators: ["notEmpty"],
          items: bpNodeActionSelectOptions,
          onChange: function (field, value, rowIndex, oldValue, text) {
            this.setData({ comment: text });
            loadFormFields(value);
          },
        },
        {
          field: "comment",
          title: "T_LCDP_BIZ_PROCESS_COMMENT.COMMENTS",
          type: "textarea",
          validators: ["strLength[0,100]"],
          height: 70,
        },
      ];

      var loadFormFields = function (actionId) {
        var nodeAction = bpNodeActions.find(function (item) {
          return item.id == actionId;
        });
        var modalHeight = 230;
        if (nodeAction.elecSignFlag === "1") {
          if (formFields.length == 2) {
            formFields = formFields.concat([
              {
                field: "userId",
                title: "T_LCDP_BIZ_PROCESS_COMMENT.USERID",
                value: workspace.user.id,
                readonly: true,
              },
              {
                field: "password",
                title: "T_LCDP_BIZ_PROCESS_COMMENT.PASSWORD",
                type: "password",
              },
            ]);
          }
          modalHeight = 320;
        } else {
          formFields = formFields.filter(function (item) {
            return item.field != "userId" && item.field != "password";
          });
          modalHeight = 230;
        }

        Gikam.getComp("lcdp-biz-process-common-modal").setSize(
          500,
          modalHeight
        );
        Gikam.getComp("lcdp-biz-process-common-form").refreshFields(formFields);
      };

      var processData = function () {
        var selectActionId = Gikam.getComp(
          "lcdp-biz-process-common-form"
        ).getData().actionId;
        var selectBpAction = bpNodeActions.find(function (e) {
          return selectActionId == e.id;
        });

        var checkScriptJs = selectBpAction.checkScriptJs;
        if (Gikam.isNotEmpty(checkScriptJs)) {
          var bizData = selectedRows; // 提交的业务数据(前端校验脚本要用到的参数)
          var bpActionJsCheckFunc = null;
          eval("bpActionJsCheckFunc = function() {" + checkScriptJs + "}");
          if (bpActionJsCheckFunc() === false) {
            return;
          }
        }

        Gikam.put(
          IFM_CONTEXT + "/secure/core/module/lcdp/bp-managers/action/approval",
          Gikam.getJsonWrapper(
            Gikam.getComp("lcdp-biz-process-common-form").getData(),
            ["", selectedRows]
          )
        ).done(function () {
          Gikam.getLastModal().close();
          Gikam.getComp(gridId).refresh();
        });
      };

      var modal = Gikam.create("modal", {
        id: "lcdp-biz-process-common-modal",
        title: "LCDP.MODULE.BPS.MODAL.BPAPPROVE",
        width: 500,
        height: 230,
        showCloseBtn: true,
      });

      var buttons = [
        {
          type: "button",
          text: "GIKAM.BUTTON.CONFIRM",
          class: "primary",
          onClick: function () {
            var form = Gikam.getComp("lcdp-biz-process-common-form");

            if (!form.validate()) {
              return;
            }

            var actionId = form.getData().actionId;
            var nodeAction = bpNodeActions.find(function (item) {
              return item.id == actionId;
            });
            if (nodeAction.elecSignFlag === "1") {
              var password = form.getData().password;
              if (Gikam.isEmpty(password)) {
                Gikam.alert("GIKAM.USER.PASSWORD.REQUIRE_NOT_EMPTY");
                return false;
              }

              Gikam.post(
                Gikam.printf(
                  IFM_CONTEXT +
                    "/open/core/module/mdm/user-passwords/userId/{userId}/action/validate-password",
                  {
                    userId: workspace.user.id,
                  }
                ),
                Gikam.getJsonWrapper({
                  password: password,
                })
              ).done(function (res) {
                if (res === 0) {
                  Gikam.alert(
                    "GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD"
                  );
                } else {
                  processData();
                }
              });
            } else {
              processData();
            }
          },
        },
        {
          type: "button",
          text: "GIKAM.BUTTON.CANCEL",
          onClick: function () {
            Gikam.getLastModal().close();
          },
        },
      ];

      Gikam.create("layout", {
        renderTo: modal.window.$dom,
        center: {
          items: [
            {
              type: "btnToolbar",
              items: buttons,
            },
            {
              type: "form",
              id: "lcdp-biz-process-common-form",
              columns: 1,
              fields: formFields,
              onRendered: function () {
                var _form = Gikam.getComp("lcdp-biz-process-common-form");

                // 筛选审核框默认显示的流程动作
                var nodeAction = bpNodeActions.find(function (item) {
                  return item.defaultFlag === "1";
                });
                if (Gikam.isEmpty(nodeAction)) {
                  // 若未设置默认,则显示第一个动作
                  _form.setData({
                    actionId: bpNodeActions[0].id,
                  });
                } else {
                  _form.setData({
                    actionId: nodeAction.id,
                  });
                }
              },
            },
          ],
        },
      });
    });
  },

  openProcessStatus: function (bizId, bpCode, bpId) {
    // 确定某一版本流程需要bpId 或者 bpCode与bizId组合
    if (
      Gikam.isEmpty(bpId) &&
      (Gikam.isEmpty(bizId) || Gikam.isEmpty(bpCode))
    ) {
      Gikam.alert("GIKAM.TIP.NEED_BPCODE");
      return;
    }

    Gikam.create("modal", {
      title: "LCDP.MODULE.BPS.MODAL.PROCESSVIEW",
      url: Gikam.printf(
        IFM_CONTEXT +
          "/secure/core/module/lcdp/bps/page/process-status-detail?bizId={bizId}&bpCode={bpCode}&bpId={bpId}",
        {
          bizId: bizId,
          bpId: bpId,
          bpCode: bpCode,
        }
      ),
    });
  },
};

Gikam.status = {
  getQuickSearchItems: function (pageType) {
    if (pageType === "edit") {
      return [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.RAW"),
          value: "draft",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT"),
          value: "draft_rejected",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.UNDO"),
          value: "draft_undo",
        },
      ];
    } else if (pageType === "audit") {
      return [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.APPROVE"),
          value: "approve",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT"),
          value: "approve_rejected",
        },
      ];
    } else if (pageType === "search") {
      return [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.APPROVE"),
          value: "approve",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT"),
          value: "draft_rejected",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.DONE"),
          value: "done",
        },
      ];
    } else if (pageType === "all") {
      return [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.RAW"),
          value: "draft",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.APPROVE"),
          value: "approve",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT"),
          value: "draft_rejected",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.DONE"),
          value: "done",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.UNDO"),
          value: "draft_undo",
        },
      ];
    } else {
      return [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.RAW"),
          value: "draft",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.APPROVE"),
          value: "approve",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT"),
          value: "draft_rejected",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.DONE"),
          value: "done",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.UNDO"),
          value: "draft_undo",
        },
      ];
    }
  },

  getRowProcessStatus: function (row) {
    if (Gikam.isNotEmpty(row.processStatus)) {
      return row.processStatus;
    }
    if (Gikam.isNotEmpty(row.processstatus)) {
      return row.processstatus;
    }
  },

  getBpmnColumn: function (dbTable, pageType) {
    return {
      field: "processStatus",
      title: "GIKAM.BPMN.FIELD.TITLE.STATUS",
      sort: false,
      type: "select",
      items: this.getQuickSearchItems(pageType),
      match: Gikam.isEmpty(pageType) ? "SB" : "SEQ",
      width: 100,
      formatter: function (index, value, row, text) {
        var taskName = Gikam.getFieldValue(row, "ext$.taskname_");

        if(!taskName){
          taskName = Gikam.getFieldValue(row, "taskname_");
        }

        var psStatus = Gikam.status.getRowProcessStatus(row);
        if (typeof psStatus === "undefined") {
          return "";
        }
        if (taskName) {
          var color = "";
          if (psStatus.indexOf("_rejected") > -1) {
            color = "red";
          } else if (psStatus.indexOf("_undo") > -1) {
            color = "#ffba00";
          }
          return (
            '<a href="javascript:;" style="color:' +
            color +
            ';" onclick="Gikam.bpmn.openProcessStatus(\'' +
            dbTable +
            "','" +
            row.id +
            "')\">" +
            taskName +
            "</a>"
          );
        }
        if (Gikam.isEmpty(pageType)) {
          if (psStatus.indexOf("draft") === 0) {
            if (psStatus === "draft_rejected") {
              return (
                '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
                "</a>"
              );
            } else if (psStatus === "draft_undo") {
              return (
                '<a href="javascript:;" style="color:#ffba00;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
                "</a>"
              );
            } else {
              return (
                '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
                "</a>"
              );
            }
          } else if (psStatus.indexOf("approve") === 0) {
            if (psStatus === "approve_rejected") {
              return (
                '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.PASS") +
                "</a>"
              );
            } else {
              return (
                '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.PASS") +
                "</a>"
              );
            }
          } else if (psStatus.indexOf("done") === 0) {
            return (
              '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              dbTable +
              "','" +
              row.id +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.DONE") +
              "</a>"
            );
          }
        } else {
          if (psStatus.indexOf("draft") === 0) {
            if (psStatus === "draft_rejected") {
              return (
                '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT") +
                "</a>"
              );
            } else if (psStatus === "draft_undo") {
              return (
                '<a href="javascript:;" style="color:#ffba00;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.UNDO") +
                "</a>"
              );
            } else {
              return (
                '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
                "</a>"
              );
            }
          } else if (psStatus.indexOf("approve") === 0) {
            if (psStatus === "approve_rejected") {
              return (
                '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.REJECT") +
                "</a>"
              );
            } else {
              return (
                '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
                dbTable +
                "','" +
                row.id +
                "')\">" +
                Gikam.propI18N("GIKAM.BPMN.STATUS.APPROVE") +
                "</a>"
              );
            }
          } else if (psStatus.indexOf("done") === 0) {
            return (
              '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              dbTable +
              "','" +
              row.id +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.DONE") +
              "</a>"
            );
          }
        }

        return "";
      },
    };
  },

  getBpmnDynamicColumn: function (tableColumn, idColumn) {
    return {
      field: "processStatus",
      title: "GIKAM.BPMN.FIELD.TITLE.STATUS",
      type: "select",
      items: [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.RAW"),
          value: "DRAFT",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.PASS"),
          value: "APPROVE",
        },
        {
          text: Gikam.propI18N("GIKAM.BPMN.STATUS.DONE"),
          value: "DONE",
        },
      ],
      width: 100,
      formatter: function (index, value, row, text) {
        var psStatus = Gikam.status.getRowProcessStatus(row);
        if (typeof psStatus === "undefined") {
          return "";
        }
        if (psStatus.toLowerCase().indexOf("draft") === 0) {
          if (psStatus.toLowerCase() === "draft_rejected") {
            return (
              '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              row[tableColumn] +
              "','" +
              row[idColumn] +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
              "</a>"
            );
          } else if (psStatus.toLowerCase() === "draft_undo") {
            return (
              '<a href="javascript:;" style="color:#ffba00;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              row[tableColumn] +
              "','" +
              row[idColumn] +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
              "</a>"
            );
          } else {
            return (
              '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              row[tableColumn] +
              "','" +
              row[idColumn] +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.RAW") +
              "</a>"
            );
          }
        } else if (psStatus.toLowerCase().indexOf("approve") === 0) {
          if (psStatus.toLowerCase() === "approve_rejected") {
            return (
              '<a href="javascript:;" style="color:red;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              row[tableColumn] +
              "','" +
              row[idColumn] +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.PASS") +
              "</a>"
            );
          } else {
            return (
              '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
              row[tableColumn] +
              "','" +
              row[idColumn] +
              "')\">" +
              Gikam.propI18N("GIKAM.BPMN.STATUS.PASS") +
              "</a>"
            );
          }
        } else if (psStatus.toLowerCase().indexOf("done") === 0) {
          return (
            '<a href="javascript:;" onclick="Gikam.bpmn.openProcessStatus(\'' +
            row[tableColumn] +
            "','" +
            row[idColumn] +
            "')\">" +
            Gikam.propI18N("GIKAM.BPMN.STATUS.DONE") +
            "</a>"
          );
        }

        return "";
      },
    };
  },

  getActiveColumn: function () {
    return {
      field: "activatedFlag",
      title: "GIKAM.ACTIVE.TITLE.STATUS",
      type: "select",
      items: [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.ACTIVE.STATUS.NO"),
          value: "0",
        },
        {
          text: Gikam.propI18N("GIKAM.ACTIVE.STATUS.YES"),
          value: "1",
        },
      ],
      width: 100,
    };
  },

  getSuspendColumn: function () {
    return {
      field: "lastSuspendedFlag",
      title: "GIKAM.SUSPEND.TITLE.STATUS",
      type: "select",
      items: [
        {
          text: "",
          value: "",
        },
        {
          text: Gikam.propI18N("GIKAM.SUSPEND.STATUS.NO"),
          value: "0",
        },
        {
          text: Gikam.propI18N("GIKAM.SUSPEND.STATUS.YES"),
          value: "1",
        },
      ],
      width: 100,
    };
  },
};

(function () {
  var parseCompType = [
    "form",
    "simpleGrid",
    "button",
    "uploader",
    "tab",
    "simpleTreeGrid",
    "grid",
    "dropDownMenu",
    "treeGrid",
  ];
  var displayAlways = ["back", "view-audit"];
  var currentWindow = null;

  var parseConfigParam = function (param, config) {
    var type = config[param.type],
      compConfig = type ? type[param.id] : void 0;
    if (param.type === "form") {
      var parseFields = function (fieldOptions, array, index) {
        if (Gikam.isEmptyObject(fieldOptions)) {
          return;
        }

        if (compConfig[fieldOptions.field]) {
          if (compConfig[fieldOptions.field].hidden) {
            array.splice(index, 1);
          } else {
            Gikam.extend(
              fieldOptions,
              {
                readonly: Gikam.isEmpty(compConfig[fieldOptions.field].edit)
                  ? config.readonly
                  : !compConfig[fieldOptions.field].edit,
              },
              compConfig[fieldOptions.field]
            );
          }
        } else {
          // 设置了form的readonly属性
          if (Gikam.isNotEmpty(compConfig.readonly)) {
            fieldOptions.readonly = compConfig.readonly;
          } else {
            fieldOptions.readonly = config.readonly;
          }
        }
      };
      if (compConfig) {
        if (Gikam.isNotEmpty(param.fields)) {
          for (let i = param.fields.length - 1; i >= 0; i--) {
            parseFields(param.fields[i], param.fields, i);
          }
        } else {
          param.panels.forEach(function (panel) {
            for (let i = panel.fields.length - 1; i >= 0; i--) {
              parseFields(panel.fields[i], panel.fields, i);
            }
          });
        }
      } else {
        param.readonly = config.readonly;
      }
    } else if (
      /simpleGrid|simpleTreeGrid|grid|treeGrid/.test(param.type) === true
    ) {
      if (compConfig) {
        param.columns = Gikam.extend(true, [], param.columns);
        for (var i = param.columns.length - 1; i > -1; i--) {
          var column = param.columns[i],
            columnConfig = compConfig[column.field];
          if (columnConfig && columnConfig.hidden === true) {
            param.columns.splice(i, 1);
            continue;
          }
          if (!Gikam.isEmpty(column.editor)) {
            column.readonly = columnConfig
              ? columnConfig.readonly
              : config.readonly;
          }
        }
      } else {
        if (config.readonly) {
          param.columns
            .filter(function (column) {
              return Gikam.isNotEmpty(column.editor);
            })
            .forEach(function (item) {
              item.editor = false;
            });
        }
      }
      param.toolbar &&
        Gikam.each(param.toolbar, function () {
          parseConfigParam(this, config);
        });
    } else if (param.type === "button") {
      if (compConfig) {
        Gikam.extend(param, compConfig);
      } else {
        if (config.readonly === true) {
          param.hidden = true;
        }
      }
      if (displayAlways.indexOf(param.icon) >= 0) {
        param.hidden = false;
      }
      if (
        currentWindow.$alwaysButton &&
        currentWindow.$alwaysButton.indexOf(param.id) >= 0
      ) {
        param.hidden = false;
      }
    } else if (param.type === "dropDownMenu") {
      if (compConfig) {
        Gikam.extend(param, compConfig);
      } else {
        if (config.readonly === true) {
          param.hidden = true;
        }
      }
    } else if (param.type === "uploader") {
      param.readonly = compConfig ? compConfig.readonly : config.readonly;
      if (compConfig && compConfig.gridToolbar) {
        param.gridToolbar = compConfig.gridToolbar;
      }
    } else if (param.type === "tab") {
      if (compConfig) {
        if (compConfig.hidden) {
          param.panels = [];
        } else {
          for (var i = param.panels.length - 1; i > -1; i--) {
            var panelConfig = compConfig[param.panels[i].id];
            panelConfig && panelConfig.hidden && param.panels.splice(i, 1);
          }
        }
      }
      Gikam.each(param.panels, function () {
        parsePageConfigParam(this, config);
      });
    }
  };

  var parsePageConfigParam = function (param, configParam) {
    if (Gikam.isPlainObject(param)) {
      if (
        parseCompType.indexOf(param.type ? param.type.toLowerCase() : "") >= 0
      ) {
        parseConfigParam(param, configParam);
      } else {
        Gikam.each(param, function (p, v) {
          parsePageConfigParam(v, configParam);
        });
      }
    } else if (Array.isArray(param)) {
      Gikam.each(param, function () {
        parsePageConfigParam(this, configParam);
      });
    }
  };

  var getPageConfig = function (param) {
    var url =
        param.pageConfig.pageObject.baseUrl +
        (param.pageConfig.bizId ? "/{id}/info/status" : "/info/status"),
      pageParam = Gikam.getRequestParam(workspace.activeMenuNode.href);
    if (pageParam.currentStatusCode) {
      url += Gikam.param({
        currentStatusCode: pageParam.currentStatusCode,
      });
    }
    return Gikam.getJson(
      Gikam.printf(url, {
        id: param.pageConfig.bizId,
      })
    );
  };

  var _init = Gikam.component.Layout.prototype.init;
  Gikam.component.Layout.prototype.init = function (param) {
    var _this = this,
      config;
    currentWindow = this.$window;
    if (param.config) {
      parsePageConfigParam(param, param.config);
      _init.call(_this, param);
      return;
    }
    if (param.pageConfig) {
      if (param.pageConfig.prior) {
        config = param.pageConfig.config[param.pageConfig.prior];
        parsePageConfigParam(param, config), _init.call(_this, param);
      } else {
        getPageConfig(param).done(function (data) {
          data.viewConfig &&
            parsePageConfigParam(param, JSON.parse(data.viewConfig)),
            _init.call(_this, param);
        });
      }
    } else {
      param.readonly &&
        parsePageConfigParam(param, {
          readonly: true,
        });
      _init.call(this, param);
    }
  };
})();
