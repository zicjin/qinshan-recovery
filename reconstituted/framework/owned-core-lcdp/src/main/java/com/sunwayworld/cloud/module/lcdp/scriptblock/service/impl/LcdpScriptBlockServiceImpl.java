package com.sunwayworld.cloud.module.lcdp.scriptblock.service.impl;

import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockBean;
import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockTreeNodeDTO;
import com.sunwayworld.cloud.module.lcdp.scriptblock.persistent.dao.LcdpScriptBlockDao;
import com.sunwayworld.cloud.module.lcdp.scriptblock.service.LcdpScriptBlockService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpScriptBlockServiceImpl implements LcdpScriptBlockService {
   @Autowired
   private LcdpScriptBlockDao lcdpScriptBlockDao;

   public LcdpScriptBlockDao getDao() {
      return this.lcdpScriptBlockDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpScriptBlockBean lcdpScriptBlock = jsonWrapper.parseUnique(LcdpScriptBlockBean.class);
      lcdpScriptBlock.setId(ApplicationContextHelper.getNextIdentity());
      lcdpScriptBlock.setClassId("biz");
      this.getDao().insert(lcdpScriptBlock);
      return lcdpScriptBlock.getId();
   }

   @Override
   public List<LcdpScriptBlockTreeNodeDTO> selectTree(RestJsonWrapperBean wrapper) {
      String category = wrapper.getParamValue("category");
      String name = wrapper.getParamValue("name");
      List<LcdpScriptBlockBean> scriptBlockList;
      if (StringUtils.isEmpty(category)) {
         scriptBlockList = this.selectAll();
      } else if (!StringUtils.isEmpty(name)) {
         scriptBlockList = this.selectListBySearchParam(category, name);
      } else {
         scriptBlockList = this.selectListByFilter(SearchFilter.instance().match("CATEGORY", category).filter(MatchPattern.EQ), new Order[0]);
      }

      TreeDescriptor<LcdpScriptBlockBean> descriptor = new TreeDescriptor<>("id", "parentId", "name", "orderNo");
      descriptor.setParseTreeNodeFunction(t -> LcdpScriptBlockTreeNodeDTO.of(t));
      List<LcdpScriptBlockTreeNodeDTO> scriptBlockTreeNodeDTOList = TreeHelper.parseTreeNode(scriptBlockList, descriptor, LcdpScriptBlockTreeNodeDTO.class);
      TreeHelper.updateChildQty(scriptBlockTreeNodeDTOList);
      return scriptBlockTreeNodeDTOList;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void drag(RestJsonWrapperBean wrapper) {
      String currentBlockId = wrapper.getParamValue("currentBlockId");
      String dropParentBlockId = wrapper.getParamValue("dropParentBlockId");
      String dropPreviousBlockId = wrapper.getParamValue("dropPreviousBlockId");
      LcdpScriptBlockBean scriptBlock = this.getDao().selectById(Long.valueOf(currentBlockId));
      scriptBlock.setParentId(StringUtils.isEmpty(dropParentBlockId) ? null : Long.valueOf(dropParentBlockId));
      List<LcdpScriptBlockBean> updateBlockOrderNoList = new ArrayList<>();
      List<LcdpScriptBlockBean> scriptBlockList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", dropParentBlockId).filter(MatchPattern.EQ), new Order[0]
      );
      if (ObjectUtils.isEmpty(dropPreviousBlockId)) {
         scriptBlock.setOrderNo(1L);
      } else {
         LcdpScriptBlockBean previousBlock = this.getDao().selectById(Long.valueOf(dropPreviousBlockId));
         scriptBlock.setOrderNo(previousBlock.getOrderNo() + 1L);
      }

      scriptBlockList.stream().forEach(e -> {
         Long compareOrderNo = e.getOrderNo();
         if (e.getOrderNo() >= scriptBlock.getOrderNo()) {
            e.setOrderNo(compareOrderNo + 1L);
            updateBlockOrderNoList.add(e);
         }
      });
      this.getDao().update(updateBlockOrderNoList, new String[]{"ORDERNO"});
      this.getDao().update(scriptBlock, new String[]{"PARENTID", "ORDERNO"});
   }

   private List<LcdpScriptBlockBean> selectListBySearchParam(String category, String name) {
      List<LcdpScriptBlockBean> scriptBlockList = this.selectListByFilter(
         SearchFilter.instance().match("CATEGORY", category).filter(MatchPattern.EQ).match("NAME", name).filter(MatchPattern.SC), new Order[0]
      );
      if (scriptBlockList.isEmpty()) {
         return new ArrayList<>();
      } else {
         List<Long> idList = scriptBlockList.stream().map(LcdpScriptBlockBean::getId).collect(Collectors.toList());
         this.addChildrenList(scriptBlockList, idList);
         return scriptBlockList;
      }
   }

   private void addChildrenList(List<LcdpScriptBlockBean> scriptBlockList, List<Long> idList) {
      List<LcdpScriptBlockBean> childScriptBlockList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", idList).filter(MatchPattern.OR), new Order[0]
      );
      if (!childScriptBlockList.isEmpty()) {
         scriptBlockList.addAll(childScriptBlockList);
         List<Long> parentIdList = childScriptBlockList.stream().map(LcdpScriptBlockBean::getId).collect(Collectors.toList());
         this.addChildrenList(scriptBlockList, parentIdList);
      }
   }
}
