package com.sunwayworld.module.item.datacenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.tree.GroupingTreeHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterDTO;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterDao;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterParamService;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterRoleService;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterService;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterUserService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDataCenterServiceImpl implements CoreDataCenterService {
   private static final String TOKEN_URL = "/gateway/auth/oauth/token";
   private static final String SCREEN_REPORT_URL = "/gateway/bi-dv/api/v1/rc/report-folders/action/fetch-tree-with-authcode";
   private static final String FORM_REPORT_URL = "/gateway/bi-df/api/v1/rc/form-folders/action/fetch-tree";
   private static final String SHARE_URL = "/share/page/share-report?id=REPORTID&authCode=AUTHCODE&index=0&footer=0";
   private static final String EXPORT_PARAMETER = "&_export_=1";
   private static final Logger logger = LogManager.getLogger(CoreDataCenterService.class);
   @Value("${sunway.datacenter.url:}")
   private String dataCenterUrl;
   @Value("${sunway.datacenter.username:}")
   private String dataCenterUserName;
   @Value("${sunway.datacenter.password:}")
   private String dataCenterPassword;
   @Value("${sunway.datacenter.export:false}")
   private boolean dataCenterExport;
   @Autowired
   private CoreDataCenterDao dataCenterDao;
   @Autowired
   private CoreDataCenterRoleService dataCenterRoleService;
   @Autowired
   private CoreRemoteCallRequestService remoteCallRequestService;
   @Autowired
   private CoreRoleUserService roleUserService;
   @Autowired
   private CoreDataCenterParamService dataCenterParamService;
   @Autowired
   private CoreDataCenterUserService dataCenterUserService;

   public CoreDataCenterDao getDao() {
      return this.dataCenterDao;
   }

   @Override
   public Page<CoreDataCenterRoleBean> selectRolePaginationByDataCenterId(Long id, RestJsonWrapperBean wrapper) {
      return this.dataCenterRoleService.selectPaginationByFilter(SearchFilter.instance().match("DATACENTERID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertRole(Long id, RestJsonWrapperBean wrapper) {
      List<CoreDataCenterRoleBean> dataCenterRoleList = wrapper.parse(CoreDataCenterRoleBean.class);
      dataCenterRoleList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setDataCenterId(id);
      });
      this.dataCenterRoleService.getDao().insert(dataCenterRoleList);
      this.dataCenterUserService.syncAfterDataCenterRoleModify(dataCenterRoleList, "dataCenterAddRole");
      return dataCenterRoleList.get(0).getId();
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteRole(Long id, RestJsonWrapperBean wrapper) {
      List<Long> idList = wrapper.parseId(Long.class);
      List<CoreDataCenterRoleBean> dataCenterRoleList = this.dataCenterRoleService.selectListByIds(idList);
      this.dataCenterRoleService.delete(wrapper);
      this.dataCenterUserService.syncAfterDataCenterRoleModify(dataCenterRoleList, "dataCenterDeleteRole");
   }

   @Override
   public List<CoreDataCenterDTO> selectTree(MapperParameter parameter) {
      TreeDescriptor<CoreDataCenterBean> descriptor = new TreeDescriptor<>("id", "parentId", "nodeName", "orderNo");
      List<CoreDataCenterBean> dataCenterList = this.addExportParam(this.selectList(parameter));
      List<CoreDataCenterDTO> treeNodeList = TreeHelper.parseTreeNode(dataCenterList, descriptor, CoreDataCenterDTO.class);
      GroupingTreeHelper.updateChildQty(treeNodeList);
      return treeNodeList;
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void sync(RestJsonWrapperBean wrapper) {
      this.remoteCallRequestService.processSyncRequest("DATA_CENTER_REPORT", null);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void syncDataCenterData() {
      List<CoreDataCenterBean> oldDataCenterList = this.selectAll();
      this.getDao().deleteByIdList(oldDataCenterList.stream().map(e -> e.getId()).collect(Collectors.toList()));
      String reportResult = this.sendHttpRequest("/gateway/bi-dv/api/v1/rc/report-folders/action/fetch-tree-with-authcode");
      List<CoreDataCenterBean> dataCenterList = JsonUtils.parseList(reportResult, CoreDataCenterBean.class);
      if (dataCenterList != null && !dataCenterList.isEmpty()) {
         List<CoreDataCenterBean> insertDataCenterList = this.parse(null, dataCenterList);
         this.getDao().insert(insertDataCenterList);
      }
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void drag(RestJsonWrapperBean wrapper) {
      String currentId = wrapper.getParamValue("currentId");
      String dropParentId = wrapper.getParamValue("dropParentId");
      String dropPreviousId = wrapper.getParamValue("dropPreviousId");
      CoreDataCenterBean currentData = this.getDao().selectById(Long.parseLong(currentId));
      if (ObjectUtils.isEmpty(dropParentId)) {
         currentData.setParentId(null);
      } else {
         currentData.setParentId(Long.parseLong(dropParentId));
      }

      List<CoreDataCenterBean> updateDataOrderNoList = new ArrayList<>();
      List<CoreDataCenterBean> coreDataList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", dropParentId).filter(MatchPattern.EQ), new Order[0]
      );
      if (ObjectUtils.isEmpty(dropPreviousId)) {
         currentData.setOrderNo(1L);
      } else {
         CoreDataCenterBean previousData = this.getDao().selectById(Long.parseLong(dropPreviousId));
         currentData.setOrderNo(previousData.getOrderNo() + 1L);
      }

      coreDataList.stream().forEach(e -> {
         Long compareOrderNo = e.getOrderNo();
         if (e.getOrderNo() >= currentData.getOrderNo()) {
            e.setOrderNo(compareOrderNo + 1L);
            updateDataOrderNoList.add(e);
         }
      });
      this.getDao().update(updateDataOrderNoList, new String[]{"ORDERNO"});
      this.getDao().update(currentData, new String[]{"PARENTID", "ORDERNO"});
   }

   @Override
   public List<CoreDataCenterBean> selectCurrentUserDataCenter() {
      List<CoreDataCenterUserBean> dataCenterUserList = this.dataCenterUserService
         .selectListByFilter(
            SearchFilter.instance()
               .match("ACTIVATEDFLAG", "1")
               .filter(MatchPattern.EQ)
               .match("USERID", LocalContextHelper.getLoginUserId())
               .filter(MatchPattern.EQ),
            new Order[]{Order.asc("ORDERNO")}
         );
      if (dataCenterUserList.isEmpty()) {
         return CollectionUtils.emptyList();
      } else {
         List<String> dataCenterUserIdList = dataCenterUserList.stream().map(e -> e.getDataCenterId().toString()).collect(Collectors.toList());
         List<CoreDataCenterBean> dataCenterList = this.selectListByIds(dataCenterUserList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList()));
         dataCenterList.sort(Comparator.comparingInt(o -> dataCenterUserIdList.indexOf(o.getId().toString())));
         return this.addParam(dataCenterList);
      }
   }

   @Override
   public Page<CoreDataCenterDTO> selectReportTree(RestJsonWrapperBean wrapper) {
      String reportResult = this.sendHttpRequest("/gateway/bi-df/api/v1/rc/form-folders/action/fetch-tree");
      List<CoreDataCenterBean> dataCenterList = JsonUtils.parseList(reportResult, CoreDataCenterBean.class);
      if (dataCenterList != null && !dataCenterList.isEmpty()) {
         List<CoreDataCenterBean> parseDataCenterList = this.parse(null, dataCenterList);
         String filterJson = wrapper.getParamValue("qf");
         if (!StringUtils.isEmpty(filterJson)) {
            Map<String, String> optionsMap = JSONObject.parseObject(filterJson, new TypeReference<Map<String, String>>() {}, new Feature[0]);
            String nodeNameFilter = optionsMap.get("nodeName_CISC");
            parseDataCenterList = parseDataCenterList.stream().filter(e -> StringUtils.contains(e.getNodeName(), nodeNameFilter)).collect(Collectors.toList());
         }

         TreeDescriptor<CoreDataCenterBean> descriptor = new TreeDescriptor<>("id", "parentId", "nodeName", "orderNo");
         List<CoreDataCenterDTO> treeNodeList = TreeHelper.parseTreeNode(parseDataCenterList, descriptor, CoreDataCenterDTO.class);
         return new Pagination<>(treeNodeList);
      } else {
         return new Pagination<>(CollectionUtils.emptyList());
      }
   }

   @Override
   public List<CoreDataCenterBean> selectSelectableList() {
      List<CoreRoleUserBean> coreRoleUserList = this.roleUserService.selectByUserId(LocalContextHelper.getLoginUserId());
      List<Long> roleIdList = coreRoleUserList.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
      List<CoreDataCenterRoleBean> dataCenterRoleList = this.dataCenterRoleService
         .getDao()
         .selectListByOneColumnValues(roleIdList, "ROLEID", Arrays.asList("DATACENTERID"));
      List<Long> dataCenterIdList = dataCenterRoleList.stream().map(e -> e.getDataCenterId()).collect(Collectors.toList());
      return dataCenterIdList.isEmpty() ? CollectionUtils.emptyList() : this.addParam(this.addExportParam(this.selectListByIds(dataCenterIdList)));
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertParam(Long id, RestJsonWrapperBean wrapper) {
      List<CoreDataCenterParamBean> dataCenterParamsList = wrapper.parse(CoreDataCenterParamBean.class);
      dataCenterParamsList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setDataCenterId(id);
      });
      this.dataCenterParamService.getDao().insert(dataCenterParamsList);
      return dataCenterParamsList.get(0).getId();
   }

   @Transactional
   @Audit("AUDIT.DELETE")
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteParam(Long id, RestJsonWrapperBean wrapper) {
      this.dataCenterParamService.delete(wrapper);
   }

   @Override
   public Page<CoreDataCenterParamBean> selectParamPaginationByDataCenterId(Long id, RestJsonWrapperBean wrapper) {
      return this.dataCenterParamService.selectPaginationByFilter(SearchFilter.instance().match("DATACENTERID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void syncUserDataCenterData(RestJsonWrapperBean wrapper) {
      this.dataCenterUserService.sync(wrapper);
   }

   @Override
   public Page<CoreDataCenterUserBean> selectCurrentUserPagination(RestJsonWrapperBean wrapper) {
      return this.dataCenterUserService
         .selectPaginationByFilter(SearchFilter.instance().match("USERID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void swapUser(String column, Long firstId, Long secondId) {
      this.dataCenterUserService.swap(column, firstId, secondId);
   }

   public CoreDataCenterBean selectDetail(Long id) {
      List<CoreDataCenterBean> dataCenterList = new ArrayList<>();
      dataCenterList.add(this.getDao().selectById(id));
      return this.addParam(dataCenterList).get(0);
   }

   public String getReplacedParam(String param) {
      if (StringUtils.contains(param, "${loginUserId}")) {
         param = StringUtils.replace(param, "${loginUserId}", LocalContextHelper.getLoginUserId());
      }

      if (StringUtils.contains(param, "${loginOrgId}")) {
         param = StringUtils.replace(param, "${loginOrgId}", LocalContextHelper.getLoginOrgId());
      }

      if (StringUtils.contains(param, "${loginRoleId}")) {
         param = StringUtils.replace(param, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
      }

      return param;
   }

   private List<CoreDataCenterBean> parse(Long parentId, List<CoreDataCenterBean> dataCenterList) {
      List<CoreDataCenterBean> insertDataCenterList = new ArrayList<>();
      dataCenterList.forEach(e -> {
         e.setParentId(parentId);
         List<CoreDataCenterBean> childrenList = e.getChildren();
         if (childrenList != null && !childrenList.isEmpty()) {
            insertDataCenterList.addAll(this.parse(e.getId(), childrenList));
         }

         if ("data".equals(e.getNodeType())) {
            e.setUrl(this.getUrl(e.getId(), e.getAuthcode()));
         }

         e.setChildren(null);
         e.setOrderNo(ApplicationContextHelper.getNextOrderNo());
         insertDataCenterList.add(e);
      });
      return insertDataCenterList;
   }

   private String sendHttpRequest(String requestUrl) {
      String token = this.getDataCenterToken();
      HttpURLConnection httpConnection = null;
      InputStream is = null;
      OutputStream out = null;
      ByteArrayOutputStream baos = null;
      String message = "";

      try {
         baos = new ByteArrayOutputStream();
         URL url = new URL(this.dataCenterUrl + requestUrl);
         httpConnection = (HttpURLConnection)url.openConnection();
         httpConnection.setRequestMethod("POST");
         httpConnection.setRequestProperty("Content-Type", "application/json");
         httpConnection.addRequestProperty("Authorization", "Bearer " + token);
         httpConnection.setDoOutput(true);
         httpConnection.setDoInput(true);
         httpConnection.connect();
         out = httpConnection.getOutputStream();
         out.write("{}".getBytes());
         is = httpConnection.getInputStream();
         byte[] jsonBytes = new byte[1024];
         int len = -1;

         while ((len = is.read(jsonBytes)) != -1) {
            baos.write(jsonBytes, 0, len);
         }

         baos.flush();
         message = new String(baos.toByteArray(), "UTF-8");
      } catch (Exception var23) {
         logger.error(var23);
      } finally {
         if (is != null) {
            try {
               is.close();
            } catch (Exception var22) {
            }
         }

         if (out != null) {
            try {
               out.close();
            } catch (Exception var21) {
            }
         }

         if (httpConnection != null) {
            httpConnection.disconnect();
         }
      }

      return message;
   }

   private String getDataCenterToken() {
      String url = this.dataCenterUrl + "/gateway/auth/oauth/token" + "?username=" + this.dataCenterUserName + "&password=" + this.dataCenterPassword;
      String result = HttpClientManager.getInstance().sendHttpPost(url, "");
      JSONObject jsonObject = JSONObject.parseObject(result);
      return jsonObject.get("access_token").toString();
   }

   private String getUrl(Long reportId, String authCode) {
      return this.dataCenterUrl
         + "/share/page/share-report?id=REPORTID&authCode=AUTHCODE&index=0&footer=0"
            .replace("REPORTID", reportId.toString())
            .replace("AUTHCODE", authCode == null ? "" : authCode);
   }

   private List<CoreDataCenterBean> addExportParam(List<CoreDataCenterBean> dataCenterList) {
      if (this.dataCenterExport) {
         dataCenterList.stream().forEach(e -> {
            String url = e.getUrl();
            if (!StringUtils.isEmpty(url)) {
               e.setUrl(url + "&_export_=1");
            }
         });
      }

      return dataCenterList;
   }

   private List<CoreDataCenterBean> addParam(List<CoreDataCenterBean> dataCenterList) {
      List<Long> dataCenterIdList = dataCenterList.stream().map(e -> e.getId()).collect(Collectors.toList());
      List<CoreDataCenterParamBean> dataCenterParamList = this.dataCenterParamService
         .selectListByFilter(SearchFilter.instance().match("DATACENTERID", dataCenterIdList).filter(MatchPattern.OR), new Order[0]);
      dataCenterList.forEach(
         e -> {
            List<CoreDataCenterParamBean> dataCenterParamByDataCenterIdList = dataCenterParamList.stream()
               .filter(info -> info.getDataCenterId().equals(e.getId()))
               .collect(Collectors.toList());
            StringBuilder urlParam = new StringBuilder();
            dataCenterParamByDataCenterIdList.forEach(p -> {
               String parameterName = p.getParameterName();
               if (!StringUtils.isEmpty(parameterName)) {
                  String parameterValue = p.getParameterValue();
                  String defaultValue = p.getDefaultValue();
                  if (!StringUtils.isEmpty(parameterValue) || !StringUtils.isEmpty(defaultValue)) {
                     urlParam.append("&");
                     urlParam.append(parameterName);
                     urlParam.append("=");
                     urlParam.append(this.getReplacedParam(StringUtils.isEmpty(parameterValue) ? defaultValue : parameterValue));
                  }
               }
            });
            e.setUrl(e.getUrl() + urlParam.toString());
         }
      );
      return dataCenterList;
   }
}
