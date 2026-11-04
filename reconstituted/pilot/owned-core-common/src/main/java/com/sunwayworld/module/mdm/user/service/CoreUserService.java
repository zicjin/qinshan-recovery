package com.sunwayworld.module.mdm.user.service;

import com.sunwayworld.framework.restful.data.RestHttpTextEntity;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserOrgDTO;
import com.sunwayworld.module.mdm.user.bean.CoreUserSignatureDTO;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CoreUserService
   extends GenericService<CoreUserBean, String>,
   GenericSuspendableService<CoreUserBean, String>,
   GenericChoosableService<CoreUserBean, String>,
   GenericSelectableService<CoreUserBean, String> {
   String insert(RestJsonWrapperBean wrapper);

   void delete(String id);

   CoreUserBean selectByLoginId(String loginId);

   void resetPassword(RestJsonWrapperBean wrapper);

   void resetPassword(String id);

   RestHttpTextEntity changePassword(RestJsonWrapperBean wrapper);

   RestHttpTextEntity changePassword(String id, RestJsonWrapperBean wrapper);

   RestHttpTextEntity changeNewPassword(RestJsonWrapperBean wrapper);

   RestHttpTextEntity changePassword(String userId, String newRawPassword);

   void deprecate(RestJsonWrapperBean wrapper);

   void insertUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper);

   void deleteUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper);

   void setDefaultUserPermission(String id, String permissionTypeId, RestJsonWrapperBean wrapper);

   CoreUserSignatureDTO sign(RestJsonWrapperBean wrapper);

   String selectSignatureUrl(String userId);

   boolean defaultPassword();

   String selectAvatarUrl(String userId);

   Map<String, String> selectAbsoluteAvatarUrl(List<String> userIdList);

   Long selectLoginUserIncorrectPasswordAttempts(String userId, List<CorePasswordPolicyBean> passwordPolicyList);

   void setLoginUserIncorrectPasswordAttempts(String userId, Long remainAttempts);

   default List<CoreUserOrgDTO> selectUserOrgList(List<String> userIdList) {
      List<CoreUserBean> userList = this.getDao().selectListByOneColumnValues(userIdList, "ID", Arrays.asList("ID", "ORGID"));
      return userList == null ? CollectionUtils.emptyList() : userList.stream().map(u -> {
         CoreUserOrgDTO userOrg = new CoreUserOrgDTO();
         userOrg.setId(u.getId());
         userOrg.setOrgList(Arrays.asList(u.getOrgId()));
         return userOrg;
      }).collect(Collectors.toList());
   }

   void tenantTriggerInsertOrActivateUser(List<CoreUserBean> userList);

   void tenantTriggerDeactivateUser(List<String> idList);
}
