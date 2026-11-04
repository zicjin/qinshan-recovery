coreCustomChart.detailPage = {
  //获取基础信息
  getBaseInfoTab: function () {
    return {
      type: "chartEdit",
      requestId: this.param.id,
    };
  },
  createPage: function () {
    Gikam.create("layout", {
      center: {
        items: [this.getBaseInfoTab()],
      },
      renderTo: workspace.window.$dom,
    });

    return this;
  },
  init: function (param) {
    this.param = param;
    this.createPage();
  },
};
