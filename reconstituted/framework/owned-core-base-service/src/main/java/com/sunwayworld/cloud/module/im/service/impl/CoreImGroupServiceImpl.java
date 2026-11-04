package com.sunwayworld.cloud.module.im.service.impl;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsBean;
import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import com.sunwayworld.cloud.module.im.bean.CoreImGroupBean;
import com.sunwayworld.cloud.module.im.bean.CoreImGroupContactsDTO;
import com.sunwayworld.cloud.module.im.persistent.dao.CoreImGroupDao;
import com.sunwayworld.cloud.module.im.service.CoreImContactsService;
import com.sunwayworld.cloud.module.im.service.CoreImGroupService;
import com.sunwayworld.cloud.module.im.support.ImHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreImGroupServiceImpl implements CoreImGroupService {
   @Autowired
   private CoreImGroupDao coreImGroupDao;
   @Autowired
   private CoreUserService coreUserService;
   @Autowired
   private CoreImContactsService coreImContactsService;

   public CoreImGroupDao getDao() {
      return this.coreImGroupDao;
   }

   @Transactional
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreImGroupBean coreImGroup = jsonWrapper.parseUnique(CoreImGroupBean.class);
      coreImGroup.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreImGroup);
      return coreImGroup.getId();
   }

   @Override
   public List<CoreImGroupContactsDTO> selectContactsList() {
      List<CoreImGroupContactsDTO> imGroupContactsDTOList = new ArrayList<>();
      List<CoreUserBean> allUserList = this.coreUserService.getDao().selectAll("ID", "USERNAME", "EMAIL", "ORGNAME");
      List<CoreUserBean> userList = allUserList.stream().filter(e -> !e.getId().equals(LocalContextHelper.getLoginUserId())).collect(Collectors.toList());
      CoreImGroupContactsDTO imGroupContactsFriendDTO = new CoreImGroupContactsDTO();
      imGroupContactsFriendDTO.setId(ApplicationContextHelper.getNextIdentity());
      imGroupContactsFriendDTO.setGroupName("我的好友");
      imGroupContactsFriendDTO.setChildren(this.buildContactsDTOByUser(userList));
      imGroupContactsFriendDTO.setUserQty(imGroupContactsFriendDTO.getChildren().size());
      imGroupContactsDTOList.add(imGroupContactsFriendDTO);
      return imGroupContactsDTOList;
   }

   private List<CoreImContactsDTO> buildContactsDTOByUser(List<CoreUserBean> userList) {
      List<CoreImContactsDTO> imContactsDTOList = new ArrayList<>();
      List<String> userIdList = userList.stream().map(e -> e.getId()).collect(Collectors.toList());
      Map<String, String> avatarMap = this.coreUserService.selectAbsoluteAvatarUrl(userIdList);
      Set<String> onlineUserIdSet = ImHelper.getOnlineUser();
      String loginUserId = LocalContextHelper.getLoginUserId();
      List<CoreImContactsBean> imContactsBean = this.coreImContactsService
         .selectListByFilter(SearchFilter.instance().match("USERID", loginUserId).filter(MatchPattern.EQ), new Order[0]);
      Map<String, CoreImContactsBean> imContactsBeanMap = imContactsBean.stream()
         .collect(Collectors.toMap(CoreImContactsBean::getContactsId, CoreImContactsBean -> (CoreImContactsBean)CoreImContactsBean));

      for (CoreUserBean user : userList) {
         CoreImContactsDTO imContactsDTO = new CoreImContactsDTO();
         imContactsDTO.setId(user.getId());
         imContactsDTO.setUserId(user.getId());
         imContactsDTO.setUserName(user.getUserName());
         imContactsDTO.setEmail(user.getEmail());
         imContactsDTO.setOrgName(user.getOrgName());
         imContactsDTO.setStatus(onlineUserIdSet.contains(user.getId()) ? "online" : "offline");
         imContactsDTO.setAvatarUrl(avatarMap.get(user.getId()));
         imContactsDTO.setSignature(avatarMap.get(user.getId()));
         CoreImContactsBean coreImContactsBean = imContactsBeanMap.get(user.getId());
         if (coreImContactsBean != null) {
            imContactsDTO.setTop(coreImContactsBean.getTop());
         } else {
            imContactsDTO.setTop("0");
         }

         imContactsDTOList.add(imContactsDTO);
      }

      return imContactsDTOList;
   }
}
