package com.sunwayworld.cloud.module.eln.resource.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionDTO;
import com.sunwayworld.cloud.module.eln.resource.CoreElnTmplResource;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplDsService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplVersionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule("eln模板模块")
@GikamBean
@RestController
@RequestMapping({"/secure/core/module/eln/eln-tmpls"})
public class CoreElnTmplResourceImpl
   implements CoreElnTmplResource,
   AbstractGenericResource<CoreElnTmplService, CoreElnTmplBean, Long>,
   AbstractGenericActivatableResource<CoreElnTmplService, CoreElnTmplBean, Long>,
   AbstractGenericChoosableResource<CoreElnTmplService, CoreElnTmplBean, Long>,
   AbstractGenericSelectableResource<CoreElnTmplService, CoreElnTmplBean, Long> {
   @Autowired
   private CoreElnTmplService coreElnTmplService;
   @Autowired
   private CoreElnTmplDsService coreElnTmplDsService;
   @Autowired
   private CoreElnTmplVersionService coreElnTmplVersionService;

   public CoreElnTmplService getService() {
      return this.coreElnTmplService;
   }

   @Log(
      value = "新增eln模板数据源",
      type = LogType.INSERT
   )
   @Override
   public Long insertDs(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      return this.coreElnTmplDsService.insert(wrapper);
   }

   @Log(
      value = "删除eln模板数据源",
      type = LogType.DELETE
   )
   @Override
   public void deleteDs(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      this.coreElnTmplDsService.delete(wrapper);
   }

   @Log(
      value = "根据模板查询模板数据源",
      type = LogType.SELECT
   )
   @Override
   public Pagination<CoreElnTmplDsBean> selectDsByTmpl(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      return (Pagination<CoreElnTmplDsBean>)this.coreElnTmplDsService
         .selectPaginationByFilter(SearchFilter.instance().match("ELNTMPLID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Log(
      value = "打开eln模板",
      type = LogType.SELECT
   )
   @Override
   public CoreElnTmplVersionDTO selectElnDsDTO(@PathVariable Long id, @PathVariable Long versionId, RestJsonWrapperBean wrapper) {
      return this.getService().selectElnDsDTO(versionId, wrapper);
   }

   @Log(
      value = "新增eln模板版本",
      type = LogType.INSERT
   )
   @Override
   public Long insertVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      return this.coreElnTmplVersionService.insert(wrapper);
   }

   @Log(
      value = "根据模板查询版本",
      type = LogType.SELECT
   )
   @Override
   public Pagination<CoreElnTmplVersionBean> selectVersionByTmpl(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      Page<CoreElnTmplVersionBean> versionPage = this.coreElnTmplVersionService
         .selectPaginationByFilter(SearchFilter.instance().match("ELNTMPLID", id).filter(MatchPattern.EQ), wrapper);
      versionPage.getRows().forEach(v -> v.setElnTmplContent(null));
      return (Pagination<CoreElnTmplVersionBean>)versionPage;
   }

   @Log(
      value = "保存eln模板，修改eln模板版本内容",
      type = LogType.UPDATE
   )
   @Override
   public void updateVersion(@PathVariable Long id, RestJsonWrapperBean wrapper) {
      this.coreElnTmplVersionService.updateVersion(wrapper);
   }

   @Log(
      value = "查询eln测试",
      type = LogType.SELECT
   )
   @Override
   public CoreElnDTO selectElnTestDTO(@PathVariable(required = false) Long id, @PathVariable Long versionId, RestJsonWrapperBean wrapper) {
      return this.coreElnTmplService.selectElnTestDTO(id, versionId, wrapper);
   }

   @Log(
      value = "查询eln模板版本详情",
      type = LogType.SELECT
   )
   @Override
   public CoreElnTmplVersionBean selectVsersionDetail(@PathVariable Long id, @PathVariable Long versionId) {
      return this.coreElnTmplVersionService.selectDetail(versionId);
   }

   @Log(
      value = "根据eln模板ID查询最高版本",
      type = LogType.SELECT
   )
   @Override
   public CoreElnTmplVersionBean selectTopVersionByTmplId(@PathVariable Long id) {
      return this.coreElnTmplService.selectTopVersionByTmplId(id);
   }

   @Log(
      value = "模板复制",
      type = LogType.INSERT
   )
   @Override
   public void copyTmpl(Long id, Long versionId, RestJsonWrapperBean wrapper) {
      this.coreElnTmplService.copyTmpl(id, versionId, wrapper);
   }
}
