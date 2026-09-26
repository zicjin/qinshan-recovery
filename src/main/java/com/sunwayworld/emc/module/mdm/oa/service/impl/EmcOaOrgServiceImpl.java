/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.mdm.oa.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.emc.module.mdm.basemdm.area.service.EmcAreaService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaAreaBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaBankBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaClientBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaEquiptBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaNewClientBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaOrgBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaOrgRelationBean;
import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaUserBean;
import com.sunwayworld.emc.module.mdm.oa.bean.TokenResponseDTO;
import com.sunwayworld.emc.module.mdm.oa.dao.EmcOaOrgDao;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaAreaService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaBankService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaEquiptService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaNewClientService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaOrgRelationService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaOrgService;
import com.sunwayworld.emc.module.mdm.oa.service.EmcOaUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class EmcOaOrgServiceImpl
implements EmcOaOrgService {
    private static final Logger logger = LogManager.getLogger(EmcOaOrgServiceImpl.class);
    private static String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    private static int USER_PAGE_SIZE = 1000;
    private static int CLIENT_PAGE_SIZE = 1000;
    private static int NEW_CLIENT_PAGE_SIZE = 10;
    private static int AREA_PAGE_SIZE = 1000;
    private static int EQUIPT_PAGE_SIZE = 1000;
    @Autowired
    private EmcOaOrgDao emcOaOrgDao;
    @Autowired
    private EmcOaUserService emcOaUserService;
    @Autowired
    private CoreOrgService coreOrgService;
    @Autowired
    private CoreUserService coreUserService;
    @Autowired
    private CoreUserPermissionService coreUserPermissionService;
    @Autowired
    private EmcClientService emcClientService;
    @Autowired
    private EmcOaNewClientService emcOaNewClientService;
    @Autowired
    private EmcOaBankService emcOaBankService;
    @Autowired
    private EmcAreaService emcAreaService;
    @Autowired
    private EmcOaAreaService emcOaAreaService;
    @Autowired
    private EmcEquiptService emcEquiptService;
    @Autowired
    private EmcOaEquiptService emcOaEquiptService;
    @Autowired
    private EmcOaOrgRelationService emcOaOrgRelationService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public EmcOaOrgDao getDao() {
        return this.emcOaOrgDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOaOrgBean emcOaOrg = jsonWrapper.parseUnique(EmcOaOrgBean.class);
        emcOaOrg.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(emcOaOrg);
        return emcOaOrg.getId();
    }

    @Override
    @Transactional
    public void syncOaUserOrg() {
        String token = this.getToken();
        logger.info("--------------token-------------" + token);
        logger.info("--------------syncOaOrg-------------");
        this.syncOaOrg(token);
        logger.info("--------------syncOaUser-------------");
        this.syncOaUser(token);
        logger.info("--------------syncCoreOrg-------------");
        this.syncCoreOrg();
        logger.info("--------------syncCoreUser-------------");
        this.syncCoreUser();
        logger.info("--------------validateData-------------");
        this.validateData();
        logger.info("--------------syncCoreUserPermission-------------");
        this.syncCoreUserPermission();
    }

    @Override
    @Transactional
    public void syncOaClientArea() {
        logger.info("--------------syncOaClient-------------");
        this.syncOaNewClient("");
    }

    @Override
    @Transactional
    public void syncOaClientAreaWithParam(RestJsonWrapperBean wrapperBean) {
        String contractName = wrapperBean.getParamValue("contractName");
        logger.info("--------------syncOaClient-------------");
        this.syncOaNewClient(contractName);
        logger.info("--------------syncEmcClient-------------");
        this.syncEmcClient(contractName);
    }

    @Override
    @Transactional
    public void syncEmcClientSchedule() {
        logger.info("--------------syncEmcClient-------------");
        this.syncEmcClient("");
    }

    @Override
    @Transactional
    public void syncOaEquipt() {
        String token = this.getToken();
        logger.info("--------------token-------------" + token);
        logger.info("--------------syncOaEquipt-------------");
        this.syncOaEquipt(token);
        logger.info("--------------syncEmcEquipt-------------");
        this.syncEmcEquipt();
    }

    private void validateData() {
        List<CoreOrgBean> orgBeanList = this.coreOrgService.selectAll();
        ArrayList<CoreOrgBean> updateBeanList = new ArrayList<CoreOrgBean>();
        for (CoreOrgBean o : orgBeanList) {
            CoreOrgBean parentOrgBean = orgBeanList.stream().filter(e -> StringUtils.equals(e.getId(), o.getParentId())).findFirst().orElse(null);
            if (parentOrgBean != null) continue;
            CoreOrgBean coreOrgBean = new CoreOrgBean();
            coreOrgBean.setId(o.getId());
            coreOrgBean.setParentId(null);
            updateBeanList.add(coreOrgBean);
        }
        this.coreOrgService.getDao().update(updateBeanList, (String[])new String[]{"PARENTID"});
    }

    private void syncCoreUser() {
        List<CoreUserBean> allOldCoreUserBeanList = this.coreUserService.selectAll();
        List<EmcOaUserBean> oaUserBeanList = this.emcOaUserService.selectAll();
        List<CoreOrgBean> coreOrgBeanList = this.coreOrgService.selectAll();
        List<EmcOaOrgBean> oaOrgBeanList = this.selectListByFilter(SearchFilter.instance().match("SFFC", "S").filter(MatchPattern.DIFFER), new Order[0]);
        List<EmcOaOrgRelationBean> emcOaOrgRelationBeanList = this.emcOaOrgRelationService.selectAll();
        ArrayList<CoreUserBean> insertBeanList = new ArrayList<CoreUserBean>();
        ArrayList<CoreUserBean> updateBeanList = new ArrayList<CoreUserBean>();
        for (EmcOaUserBean oo : oaUserBeanList) {
            CoreOrgBean compOrg;
            CoreUserBean user = new CoreUserBean();
            CoreUserBean oldBean = allOldCoreUserBeanList.stream().filter(e -> StringUtils.equals(oo.getOaid(), e.getExt$Item("oaid"))).findFirst().orElse(null);
            if (oldBean == null) {
                user.setId(oo.getYggh());
                user.setPassword(this.getDefaultPassword(user.getId()));
                LocalDateTime now = LocalDateTime.now();
                user.setPasswordUpdatedTime(now);
                long expiryInterval = NumberUtils.parseLong(ApplicationContextHelper.getConstantValue("ACCOUNT_EXPIRY_INTERVAL"));
                if (expiryInterval > 0L) {
                    user.setExpiryDate(now.plusDays(expiryInterval));
                }
                insertBeanList.add(user);
            } else {
                user.setId(oldBean.getId());
                updateBeanList.add(user);
            }
            user.setUserNo(oo.getYggh());
            user.setUserName(oo.getXm());
            user.setMobile(oo.getYddh());
            user.setEmail(oo.getDzyx());
            user.setExt$Item("oaid", oo.getOaid());
            if (Arrays.asList("0", "1", "2", "3").contains(oo.getYgzt())) {
                user.setStatus("activated");
            } else {
                user.setStatus("locked");
            }
            CoreOrgBean dep = coreOrgBeanList.stream().filter(e -> StringUtils.equals(oo.getSsbm(), e.getId())).findFirst().orElse(null);
            if (dep != null) {
                user.setOrgId(dep.getId());
                user.setOrgName(dep.getOrgName());
            }
            compOrg = this.getCompOrg(oo.getSsbm(), oaOrgBeanList, emcOaOrgRelationBeanList, coreOrgBeanList);
            if (compOrg == null) continue;
            user.setExt$Item("parentorgid", compOrg.getId());
        }
        this.coreUserService.getDao().insert(insertBeanList);
        this.coreUserService.getDao().updateIfChanged(updateBeanList);
    }

    private CoreOrgBean getCompOrg(String bm, List<EmcOaOrgBean> oaOrgBeanList, List<EmcOaOrgRelationBean> emcOaOrgRelationBeanList, List<CoreOrgBean> coreOrgBeanList) {
        EmcOaOrgBean emcOaOrgBean = oaOrgBeanList.stream().filter(e -> StringUtils.equals(bm, e.getZzbh())).findFirst().orElse(null);
        if (emcOaOrgBean == null) {
            return null;
        }
        EmcOaOrgRelationBean emcOaOrgRelationBean = emcOaOrgRelationBeanList.stream().filter(e -> StringUtils.equals(emcOaOrgBean.getSsgfgsbm(), e.getOaGqOrgCode())).findFirst().orElse(null);
        if (emcOaOrgRelationBean == null) {
            return null;
        }
        CoreOrgBean coreOrgBean = coreOrgBeanList.stream().filter(e -> StringUtils.equals(emcOaOrgRelationBean.getLimsOrgCode(), e.getId())).findFirst().orElse(null);
        return coreOrgBean;
    }

    private void syncEmcEquipt() {
        List<EmcEquiptBean> allOldEmcBeanList = this.emcEquiptService.selectAll();
        List<EmcOaEquiptBean> oaBeanList = this.emcOaEquiptService.selectListByFilter(SearchFilter.instance().match("sbbh", null).filter(MatchPattern.DIFFER), new Order[0]);
        oaBeanList = oaBeanList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<EmcOaEquiptBean>(Comparator.comparing(e -> e.getSbbh()))), ArrayList::new));
        ArrayList<EmcEquiptBean> updateBeanList = new ArrayList<EmcEquiptBean>();
        for (EmcOaEquiptBean oo : oaBeanList) {
            EmcEquiptBean newBean = new EmcEquiptBean();
            EmcEquiptBean oldBean = allOldEmcBeanList.stream().filter(e -> StringUtils.equals(oo.getSbbh(), e.getEquiptId())).findFirst().orElse(null);
            if (oldBean != null) {
                newBean.setId(oldBean.getId());
                updateBeanList.add(newBean);
            }
            newBean.setExt$Item("oaid", oo.getSbbh());
            newBean.setExt$Item("assetid", oo.getEquiptid() + "");
            newBean.setExt$Item("assetno", oo.getMark());
        }
        this.emcEquiptService.getDao().updateIfChanged(updateBeanList);
    }

    private void syncEmcArea() {
        List<EmcAreaBean> allOldEmcAreaBeanList = this.emcAreaService.selectAll();
        List<EmcOaAreaBean> oaAreaBeanList = this.emcOaAreaService.selectListByFilter(SearchFilter.instance().match("Zddm", null).filter(MatchPattern.DIFFER), new Order[0]);
        ArrayList<EmcAreaBean> insertBeanList = new ArrayList<EmcAreaBean>();
        ArrayList<EmcAreaBean> updateBeanList = new ArrayList<EmcAreaBean>();
        for (EmcOaAreaBean oo : oaAreaBeanList) {
            EmcAreaBean newBean = new EmcAreaBean();
            EmcAreaBean oldBean = allOldEmcAreaBeanList.stream().filter(e -> StringUtils.equals(oo.getZddm(), e.getExt$Item("oaid"))).findFirst().orElse(null);
            if (oldBean == null) {
                newBean.setId(NumberUtils.parseLong(oo.getZddm()));
                insertBeanList.add(newBean);
            } else {
                newBean.setId(oldBean.getId());
                updateBeanList.add(newBean);
            }
            newBean.setAreaId(oo.getBm());
            newBean.setAreaName(oo.getZdmc());
            newBean.setExt$Item("oaid", oo.getZddm());
        }
        this.emcAreaService.getDao().insert(insertBeanList);
        this.emcAreaService.getDao().updateIfChanged(updateBeanList);
    }

    @Override
    public void syncEmcClient(String contractName) {
        List<EmcClientBean> allOldEmcClientBeanList = this.emcClientService.selectAll();
        SearchFilter searchFilter = SearchFilter.instance().match("status", "normal").filter(MatchPattern.SEQ);
        if (!StringUtils.isBlank(contractName)) {
            searchFilter.match("name", contractName).filter(MatchPattern.SEQ);
        }
        List<EmcOaNewClientBean> oaClientBeanList = this.emcOaNewClientService.selectListByFilter(searchFilter, new Order[0]);
        ArrayList<EmcClientBean> insertBeanList = new ArrayList<EmcClientBean>();
        ArrayList<EmcClientBean> updateBeanList = new ArrayList<EmcClientBean>();
        oaClientBeanList = oaClientBeanList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<EmcOaNewClientBean>(Comparator.comparing(e -> e.getCode()))), ArrayList::new));
        for (EmcOaNewClientBean oo : oaClientBeanList) {
            EmcClientBean newBean = new EmcClientBean();
            EmcClientBean oldBean = allOldEmcClientBeanList.stream().filter(e -> StringUtils.equals(oo.getCode(), e.getClientNo())).findFirst().orElse(null);
            if (oldBean == null) {
                newBean.setId(ApplicationContextHelper.getNextIdentity());
                newBean.setClientNo(oo.getCode());
                insertBeanList.add(newBean);
            } else {
                newBean.setId(oldBean.getId());
                updateBeanList.add(newBean);
            }
            newBean.setClientName(oo.getName());
            newBean.setMail(oo.getCode());
            newBean.setCreditCode(oo.getCreditCode());
            newBean.setAddress(oo.getAddress());
            newBean.setPhone(oo.getPhone());
            newBean.setExt$Item("oaid", oo.getCode());
        }
        this.emcClientService.getDao().insert(insertBeanList);
        this.emcClientService.getDao().updateIfChanged(updateBeanList);
        logger.info("--------------syncEmcClient----------end---");
    }

    private void syncCoreOrg() {
        List<CoreOrgBean> allOldCoreOrgBeanList = this.coreOrgService.selectAll();
        List<EmcOaOrgBean> oaOrgBeanList = this.selectListByFilter(SearchFilter.instance().match("SFFC", "S").filter(MatchPattern.DIFFER), new Order[0]);
        ArrayList<CoreOrgBean> insertBeanList = new ArrayList<CoreOrgBean>();
        ArrayList<CoreOrgBean> updateBeanList = new ArrayList<CoreOrgBean>();
        for (EmcOaOrgBean oo : oaOrgBeanList) {
            CoreOrgBean coreOrgBean = new CoreOrgBean();
            coreOrgBean.setOrgName(oo.getZzmc());
            coreOrgBean.setParentId(oo.getSjzzbh());
            coreOrgBean.setExt$Item("oaid", oo.getBm());
            coreOrgBean.setExt$Item("sffc", oo.getSffc());
            CoreOrgBean oldBean = allOldCoreOrgBeanList.stream().filter(e -> StringUtils.equals(oo.getBm(), e.getExt$Item("oaid"))).findFirst().orElse(null);
            if (oldBean == null) {
                coreOrgBean.setId(oo.getZzbh());
                coreOrgBean.setOrderNo(1L);
                insertBeanList.add(coreOrgBean);
                continue;
            }
            coreOrgBean.setId(oldBean.getId());
            updateBeanList.add(coreOrgBean);
        }
        this.coreOrgService.getDao().insert(insertBeanList);
        this.coreOrgService.getDao().update(updateBeanList, (String[])new String[]{"ORGNAME", "PARENTID", "OAID", "SFFC"});
    }

    private void syncOaUser(String token) {
        List<EmcOaUserBean> allOldOaUserBeanList = this.emcOaUserService.selectAll();
        JSONObject dataJSONObject = this.syncOaUserOfPage(token, "1", allOldOaUserBeanList);
        int total = dataJSONObject.getIntValue("total");
        if (total <= USER_PAGE_SIZE) {
            return;
        }
        int queryCount = total % USER_PAGE_SIZE == 0 ? total / USER_PAGE_SIZE : total / USER_PAGE_SIZE + 1;
        for (int i = 1; i < queryCount; ++i) {
            this.syncOaUserOfPage(token, i + 1 + "", allOldOaUserBeanList);
        }
    }

    private JSONObject syncOaUserOfPage(String token, String pageNum, List<EmcOaUserBean> allOldOaUserBeanList) {
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.user.url");
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("updatatime_st", "2021-05-17 15:10:10");
        paramMap.put("updatatime_ed", DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        paramMap.put("pageSize", USER_PAGE_SIZE + "");
        paramMap.put("pageNum", pageNum);
        String joinedParams = this.joinGetParams(paramMap);
        url = url + "?" + joinedParams;
        String resultJson = HttpClientManager.getInstance().sendHttpGet(url, e -> {
            e.addHeader("authorization", token);
            e.addHeader("Content-Type", CONTENT_TYPE_APPLICATION_JSON);
        });
        JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
        logger.info("--------------user--------errmsg-------------" + resultJsonObject.getString("errmsg"));
        String errcode = resultJsonObject.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new InvalidDataException("\u83b7\u53d6user\u5931\u8d25");
        }
        String data = resultJsonObject.getString("data");
        JSONObject dataJSONObject = JSONObject.parseObject(data);
        String rows = dataJSONObject.getString("rows");
        JSONArray jsonArray = JSONArray.parseArray(rows);
        List<EmcOaUserBean> oaUserBeanList = new ArrayList<EmcOaUserBean>();
        for (Object o : jsonArray) {
            JSONObject jo = JSONObject.parseObject(o.toString());
            EmcOaUserBean emcOaUserBean = new EmcOaUserBean();
            this.parseOaUser(jo, emcOaUserBean);
            emcOaUserBean.setId(ApplicationContextHelper.getNextIdentity());
            oaUserBeanList.add(emcOaUserBean);
        }
        oaUserBeanList = oaUserBeanList.stream().filter(e -> !StringUtils.isEmpty(e.getOaid())).collect(Collectors.toList());
        ArrayList<EmcOaUserBean> insertOaUserBeanList = new ArrayList<EmcOaUserBean>();
        ArrayList<EmcOaUserBean> updateOaUserBeanList = new ArrayList<EmcOaUserBean>();
        for (EmcOaUserBean emcOaUserBean : oaUserBeanList) {
            EmcOaUserBean oldBean = allOldOaUserBeanList.stream().filter(e -> StringUtils.equals(emcOaUserBean.getOaid(), e.getOaid())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaUserBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaUserBeanList.add(emcOaUserBean);
                continue;
            }
            emcOaUserBean.setId(oldBean.getId());
            updateOaUserBeanList.add(emcOaUserBean);
        }
        this.emcOaUserService.getDao().insert(insertOaUserBeanList);
        this.emcOaUserService.getDao().updateIfChanged(updateOaUserBeanList);
        return dataJSONObject;
    }

    private void syncOaOrg(String token) {
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.org.url");
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("TS_st", "2021-05-17 15:10:10");
        paramMap.put("TS_ed", DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        String joinedParams = this.joinGetParams(paramMap);
        url = url + "?" + joinedParams;
        String resultJson = HttpClientManager.getInstance().sendHttpGet(url, e -> {
            e.addHeader("authorization", token);
            e.addHeader("Content-Type", CONTENT_TYPE_APPLICATION_JSON);
        });
        JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
        logger.info("--------------org--------errmsg-------------" + resultJsonObject.getString("errmsg"));
        String errcode = resultJsonObject.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new InvalidDataException("\u83b7\u53d6org\u5931\u8d25");
        }
        List<EmcOaOrgBean> oaOrgBeanList = new ArrayList<EmcOaOrgBean>();
        String data = resultJsonObject.getString("data");
        JSONArray jsonArray = JSONArray.parseArray(data);
        for (Object o : jsonArray) {
            JSONObject jo = JSONObject.parseObject(o.toString());
            EmcOaOrgBean oaOrgBean = new EmcOaOrgBean();
            this.parseOaOrg(jo, oaOrgBean);
            oaOrgBeanList.add(oaOrgBean);
        }
        oaOrgBeanList = oaOrgBeanList.stream().filter(e -> !StringUtils.isEmpty(e.getBm())).collect(Collectors.toList());
        List<EmcOaOrgBean> allOldOaOrgBeanList = this.selectAll();
        ArrayList<EmcOaOrgBean> insertOaOrgBeanList = new ArrayList<EmcOaOrgBean>();
        ArrayList<EmcOaOrgBean> updateOaOrgBeanList = new ArrayList<EmcOaOrgBean>();
        for (EmcOaOrgBean emcOaOrgBean : oaOrgBeanList) {
            EmcOaOrgBean oldBean = allOldOaOrgBeanList.stream().filter(e -> StringUtils.equals(emcOaOrgBean.getBm(), e.getBm())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaOrgBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaOrgBeanList.add(emcOaOrgBean);
                continue;
            }
            emcOaOrgBean.setId(oldBean.getId());
            updateOaOrgBeanList.add(emcOaOrgBean);
        }
        this.getDao().insert(insertOaOrgBeanList);
        this.getDao().updateIfChanged(updateOaOrgBeanList);
    }

    @Override
    public void syncOaNewClient(String contractName) {
        List<EmcOaBankBean> allOldBankBeanList;
        List<EmcOaNewClientBean> allOldOaClientBeanList = this.emcOaNewClientService.selectAll();
        allOldBankBeanList = this.emcOaBankService.selectAll();
        JSONObject dataJSONObject = this.syncOaNewClientOfPage("1", allOldOaClientBeanList, allOldBankBeanList, contractName);
        if (dataJSONObject == null) {
            return;
        }
        int total = dataJSONObject.getIntValue("total");
        if (total <= NEW_CLIENT_PAGE_SIZE) {
            return;
        }
        int queryCount = total % NEW_CLIENT_PAGE_SIZE == 0 ? total / NEW_CLIENT_PAGE_SIZE : total / NEW_CLIENT_PAGE_SIZE + 1;
        for (int i = 1; i < queryCount; ++i) {
            this.syncOaNewClientOfPage(i + 1 + "", allOldOaClientBeanList, allOldBankBeanList, contractName);
        }
    }

    private JSONObject syncOaNewClientOfPage(String pageNum, List<EmcOaNewClientBean> allOldOaClientBeanList, List<EmcOaBankBean> allOldBankBeanList, String contractName) {
        AbstractInsertable oldBean;
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.new.client.url");
        String byNameUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.new.client.by-name.url");
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        if (StringUtils.isBlank(contractName)) {
            paramMap.put("start", DateTimeUtils.formatLocalDateTime(LocalDateTime.now().plusDays(-1L), "yyyy-MM-dd HH:mm:ss"));
            paramMap.put("end", DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        } else {
            url = byNameUrl;
            paramMap.put("name", contractName);
        }
        paramMap.put("pageSize", NEW_CLIENT_PAGE_SIZE + "");
        paramMap.put("page", pageNum);
        String joinedParams = this.joinGetParams(paramMap);
        url = url + "?" + joinedParams;
        logger.info("--------------pageNum-------------" + pageNum);
        String resultJson = HttpClientManager.getInstance().sendHttpGet(url, e -> e.addHeader("Content-Type", CONTENT_TYPE_APPLICATION_JSON));
        JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
        logger.info("--------------client--------errmsg-------------" + resultJsonObject.getString("errmsg"));
        String errcode = resultJsonObject.getString("code");
        if (!"01".equals(errcode)) {
            throw new InvalidDataException("\u83b7\u53d6client\u5931\u8d25");
        }
        String data = resultJsonObject.getString("data");
        JSONObject dataJSONObject = JSONObject.parseObject(data);
        String rows = dataJSONObject.getString("list");
        if (StringUtils.isBlank(rows)) {
            return null;
        }
        JSONArray jsonArray = JSONArray.parseArray(rows);
        List<EmcOaNewClientBean> oaClientBeanList = new ArrayList<EmcOaNewClientBean>();
        ArrayList<EmcOaBankBean> oaBankBeanList = new ArrayList<EmcOaBankBean>();
        for (Object o : jsonArray) {
            JSONObject jo = JSONObject.parseObject(o.toString());
            EmcOaNewClientBean oaClientBean = new EmcOaNewClientBean();
            this.parseOaNewClient(jo, oaClientBean);
            oaClientBean.setId(ApplicationContextHelper.getNextIdentity());
            oaClientBeanList.add(oaClientBean);
            String bank = jo.getString("bank");
            JSONArray jSONArray = JSONObject.parseArray(bank);
            for (Object bankObject : jSONArray) {
                JSONObject bankJsonObject = JSONObject.parseObject(bankObject.toString());
                EmcOaBankBean oaBankBean = new EmcOaBankBean();
                this.parseOaBank(bankJsonObject, oaBankBean, oaClientBean.getCode());
                oaBankBean.setId(ApplicationContextHelper.getNextIdentity());
                oaBankBeanList.add(oaBankBean);
            }
        }
        oaClientBeanList = oaClientBeanList.stream().filter(e -> !StringUtils.isEmpty(e.getCode())).collect(Collectors.toList());
        ArrayList<EmcOaNewClientBean> insertOaClientBeanList = new ArrayList<EmcOaNewClientBean>();
        ArrayList<EmcOaNewClientBean> updateOaClientBeanList = new ArrayList<EmcOaNewClientBean>();
        ArrayList<EmcOaBankBean> insertOaBankBeanList = new ArrayList<EmcOaBankBean>();
        ArrayList<EmcOaBankBean> updateOaBankBeanList = new ArrayList<EmcOaBankBean>();
        for (EmcOaNewClientBean emcOaNewClientBean : oaClientBeanList) {
            oldBean = allOldOaClientBeanList.stream().filter(e -> StringUtils.equals(emcOaNewClientBean.getCode(), e.getCode())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaNewClientBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaClientBeanList.add(emcOaNewClientBean);
                continue;
            }
            emcOaNewClientBean.setId(((EmcOaNewClientBean)oldBean).getId());
            updateOaClientBeanList.add(emcOaNewClientBean);
        }
        this.emcOaNewClientService.getDao().insert(insertOaClientBeanList);
        this.emcOaNewClientService.getDao().updateIfChanged(updateOaClientBeanList);
        for (EmcOaBankBean emcOaBankBean : oaBankBeanList) {
            oldBean = allOldBankBeanList.stream().filter(e -> StringUtils.equals(emcOaBankBean.getAccountbankno(), e.getAccountbankno())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaBankBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaBankBeanList.add(emcOaBankBean);
                continue;
            }
            emcOaBankBean.setId(((EmcOaBankBean)oldBean).getId());
            updateOaBankBeanList.add(emcOaBankBean);
        }
        this.emcOaBankService.getDao().insert(insertOaBankBeanList);
        this.emcOaBankService.getDao().updateIfChanged(updateOaBankBeanList);
        allOldOaClientBeanList.addAll(insertOaClientBeanList);
        allOldBankBeanList.addAll(insertOaBankBeanList);
        return dataJSONObject;
    }

    private void syncOaArea(String token) {
        List<EmcOaAreaBean> allOldOaAreaBeanList = this.emcOaAreaService.selectAll();
        JSONObject dataJSONObject = this.syncOaAreaOfPage(token, "1", allOldOaAreaBeanList);
        int total = dataJSONObject.getIntValue("total");
        if (total <= AREA_PAGE_SIZE) {
            return;
        }
        int queryCount = total % AREA_PAGE_SIZE == 0 ? total / AREA_PAGE_SIZE : total / AREA_PAGE_SIZE + 1;
        for (int i = 1; i < queryCount; ++i) {
            this.syncOaAreaOfPage(token, i + 1 + "", allOldOaAreaBeanList);
        }
    }

    private JSONObject syncOaAreaOfPage(String token, String pageNum, List<EmcOaAreaBean> allOldOaAreaBeanList) {
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.area.url");
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("updatatime_st", "2024-01-10 08:30:00");
        paramMap.put("updatatime_ed", DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        paramMap.put("pageSize", AREA_PAGE_SIZE + "");
        paramMap.put("pageNum", pageNum);
        String joinedParams = this.joinGetParams(paramMap);
        url = url + "?" + joinedParams;
        String resultJson = HttpClientManager.getInstance().sendHttpGet(url, e -> {
            e.addHeader("authorization", token);
            e.addHeader("Content-Type", CONTENT_TYPE_APPLICATION_JSON);
        });
        JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
        logger.info("--------------area--------errmsg-------------" + resultJsonObject.getString("errmsg"));
        String errcode = resultJsonObject.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new InvalidDataException("\u83b7\u53d6area\u5931\u8d25");
        }
        String data = resultJsonObject.getString("data");
        JSONObject dataJSONObject = JSONObject.parseObject(data);
        String rows = dataJSONObject.getString("rows");
        JSONArray jsonArray = JSONArray.parseArray(rows);
        List<EmcOaAreaBean> oaAreaBeanList = new ArrayList<EmcOaAreaBean>();
        for (Object o : jsonArray) {
            JSONObject jo = JSONObject.parseObject(o.toString());
            EmcOaAreaBean emcOaAreaBean = new EmcOaAreaBean();
            this.parseOaArea(jo, emcOaAreaBean);
            emcOaAreaBean.setId(ApplicationContextHelper.getNextIdentity());
            oaAreaBeanList.add(emcOaAreaBean);
        }
        oaAreaBeanList = oaAreaBeanList.stream().filter(e -> !StringUtils.isEmpty(e.getZddm())).collect(Collectors.toList());
        ArrayList<EmcOaAreaBean> insertOaAreaBeanList = new ArrayList<EmcOaAreaBean>();
        ArrayList<EmcOaAreaBean> updateOaAreaBeanList = new ArrayList<EmcOaAreaBean>();
        for (EmcOaAreaBean emcOaAreaBean : oaAreaBeanList) {
            EmcOaAreaBean oldBean = allOldOaAreaBeanList.stream().filter(e -> StringUtils.equals(emcOaAreaBean.getZddm(), e.getZddm())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaAreaBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaAreaBeanList.add(emcOaAreaBean);
                continue;
            }
            emcOaAreaBean.setId(oldBean.getId());
            updateOaAreaBeanList.add(emcOaAreaBean);
        }
        this.emcOaAreaService.getDao().insert(insertOaAreaBeanList);
        this.emcOaAreaService.getDao().updateIfChanged(updateOaAreaBeanList);
        return dataJSONObject;
    }

    private void syncOaEquipt(String token) {
        List<EmcOaEquiptBean> allOldOaEquiptBeanList = this.emcOaEquiptService.selectAll();
        JSONObject dataJSONObject = this.syncOaEquiptOfPage(token, "1", allOldOaEquiptBeanList);
        int total = dataJSONObject.getIntValue("total");
        if (total <= EQUIPT_PAGE_SIZE) {
            return;
        }
        int queryCount = total % EQUIPT_PAGE_SIZE == 0 ? total / EQUIPT_PAGE_SIZE : total / EQUIPT_PAGE_SIZE + 1;
        logger.info("--------------equipt--------queryCount-------------" + queryCount);
        for (int i = 1; i < queryCount; ++i) {
            logger.info("--------------equipt--------queryCount-i-------------" + i);
            this.syncOaEquiptOfPage(token, i + 1 + "", allOldOaEquiptBeanList);
        }
    }

    private JSONObject syncOaEquiptOfPage(String token, String pageNum, List<EmcOaEquiptBean> allOldOaBeanList) {
        String url = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.equipt.url");
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("pageSize", EQUIPT_PAGE_SIZE + "");
        paramMap.put("pageNum", pageNum);
        String joinedParams = this.joinGetParams(paramMap);
        url = url + "?" + joinedParams;
        String resultJson = HttpClientManager.getInstance().sendHttpGet(url, e -> {
            e.addHeader("authorization", token);
            e.addHeader("Content-Type", CONTENT_TYPE_APPLICATION_JSON);
        });
        JSONObject resultJsonObject = JSONObject.parseObject(resultJson);
        logger.info("--------------equipt--------errmsg-------------" + resultJsonObject.getString("errmsg"));
        String errcode = resultJsonObject.getString("errcode");
        if (!"0".equals(errcode)) {
            throw new InvalidDataException("\u83b7\u53d6equipt\u5931\u8d25");
        }
        String data = resultJsonObject.getString("data");
        JSONObject dataJSONObject = JSONObject.parseObject(data);
        String rows = dataJSONObject.getString("rows");
        JSONArray jsonArray = JSONArray.parseArray(rows);
        List<EmcOaEquiptBean> oaBeanList = new ArrayList<EmcOaEquiptBean>();
        for (Object o : jsonArray) {
            JSONObject jo = JSONObject.parseObject(o.toString());
            EmcOaEquiptBean emcOaEquiptBean = new EmcOaEquiptBean();
            this.parseOaEquipt(jo, emcOaEquiptBean);
            emcOaEquiptBean.setId(ApplicationContextHelper.getNextIdentity());
            oaBeanList.add(emcOaEquiptBean);
        }
        oaBeanList = oaBeanList.stream().filter(e -> !StringUtils.isBlank(e.getSbbh())).collect(Collectors.toList());
        ArrayList<EmcOaEquiptBean> insertOaBeanList = new ArrayList<EmcOaEquiptBean>();
        ArrayList<EmcOaEquiptBean> updateOaBeanList = new ArrayList<EmcOaEquiptBean>();
        for (EmcOaEquiptBean emcOaEquiptBean : oaBeanList) {
            EmcOaEquiptBean oldBean = allOldOaBeanList.stream().filter(e -> ObjectUtils.equals(emcOaEquiptBean.getSbbh(), e.getSbbh())).findFirst().orElse(null);
            if (oldBean == null) {
                emcOaEquiptBean.setId(ApplicationContextHelper.getNextIdentity());
                insertOaBeanList.add(emcOaEquiptBean);
                continue;
            }
            emcOaEquiptBean.setId(oldBean.getId());
            updateOaBeanList.add(emcOaEquiptBean);
        }
        this.emcOaEquiptService.getDao().insert(insertOaBeanList);
        this.emcOaEquiptService.getDao().updateIfChanged(updateOaBeanList);
        allOldOaBeanList.addAll(insertOaBeanList);
        return dataJSONObject;
    }

    private String getToken() {
        String tokenUrl = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.url");
        String tokenHeaderAuthorization = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.header.Authorization");
        String tokenHeaderContentType = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.header.Content-Type");
        String tokenParamGrantType = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.param.grant-type");
        String tokenParamUsername = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.param.username");
        String tokenParamPassword = ApplicationContextHelper.getEnvironment().getProperty("sunway.emc.oa.token.param.password");
        String resultJson = HttpClientManager.getInstance().sendHttpPost(tokenUrl, null, e -> {
            e.addHeader("Authorization", tokenHeaderAuthorization);
            e.addHeader("Content-Type", tokenHeaderContentType);
            ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("grant_type", tokenParamGrantType));
            nameValuePairs.add(new BasicNameValuePair("username", tokenParamUsername));
            nameValuePairs.add(new BasicNameValuePair("password", tokenParamPassword));
            try {
                e.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            }
            catch (UnsupportedEncodingException ex) {
                throw new InvalidDataException("token-post-\u53c2\u6570\u5c01\u88c5\u5931\u8d25");
            }
        });
        logger.info("--------------token--------resultJson-------------" + resultJson);
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        JsonUtils.parse(resultJson, tokenResponseDTO);
        if (!"0".equals(tokenResponseDTO.getErrcode())) {
            throw new InvalidDataException("\u83b7\u53d6token\u5931\u8d25");
        }
        return tokenResponseDTO.getAccess_token();
    }

    private String joinGetParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                String name = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue(), "UTF-8");
                sb.append(name).append("=").append(value);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }

    private void parseOaOrg(JSONObject jo, EmcOaOrgBean oaOrgBean) {
        oaOrgBean.setBm(jo.getString("\u7f16\u7801"));
        oaOrgBean.setCjr(jo.getString("\u521b\u5efa\u4eba"));
        oaOrgBean.setCjsj(this.parseTimeStamp(jo.getString("\u521b\u5efa\u65f6\u95f4")));
        oaOrgBean.setCzr(jo.getString("\u64cd\u4f5c\u4eba"));
        oaOrgBean.setGxss(this.parseTimeStamp(jo.getString("\u66f4\u65b0\u65f6\u95f4")));
        oaOrgBean.setSfdj(jo.getString("\u662f\u5426\u51bb\u7ed3"));
        oaOrgBean.setSfyx(jo.getString("\u662f\u5426\u6709\u6548"));
        oaOrgBean.setZzbh(jo.getString("\u7ec4\u7ec7\u7f16\u53f7"));
        oaOrgBean.setZzmc(jo.getString("\u7ec4\u7ec7\u540d\u79f0"));
        oaOrgBean.setSjzzbh(jo.getString("\u4e0a\u7ea7\u7ec4\u7ec7\u7f16\u53f7"));
        oaOrgBean.setZzfzr(jo.getString("\u7ec4\u7ec7\u8d1f\u8d23\u4eba"));
        oaOrgBean.setZzlx(jo.getString("\u7ec4\u7ec7\u7c7b\u578b"));
        oaOrgBean.setSsbk(jo.getString("\u6240\u5c5e\u677f\u5757"));
        oaOrgBean.setSffc(jo.getString("\u662f\u5426\u5c01\u5b58"));
        oaOrgBean.setSjzzmc(jo.getString("\u4e0a\u7ea7\u7ec4\u7ec7\u540d\u79f0"));
        oaOrgBean.setSssyb(jo.getString("\u6240\u5c5e\u4e8b\u4e1a\u90e8"));
        oaOrgBean.setSsgfgsmc(jo.getString("\u6240\u5c5e\u80a1\u6743\u516c\u53f8\u540d\u79f0"));
        oaOrgBean.setSsgfgsbm(jo.getString("\u6240\u5c5e\u80a1\u6743\u516c\u53f8\u7f16\u7801"));
    }

    private void parseOaUser(JSONObject jo, EmcOaUserBean oaUserBean) {
        oaUserBean.setOaid(jo.getString("oaid"));
        oaUserBean.setYggh(jo.getString("\u5458\u5de5\u5de5\u53f7"));
        oaUserBean.setXm(jo.getString("\u59d3\u540d"));
        oaUserBean.setXb(jo.getString("\u6027\u522b"));
        oaUserBean.setCsrq(DateTimeUtils.parseLocalDateTime(jo.getString("\u51fa\u751f\u65e5\u671f")));
        oaUserBean.setYgzt(jo.getString("\u5458\u5de5\u72b6\u6001"));
        oaUserBean.setYddh(jo.getString("\u79fb\u52a8\u7535\u8bdd"));
        oaUserBean.setDzyx(jo.getString("\u7535\u5b50\u90ae\u7bb1"));
        oaUserBean.setSsgs(jo.getString("\u6240\u5c5e\u516c\u53f8"));
        oaUserBean.setSsbm(jo.getString("\u6240\u5c5e\u90e8\u95e8"));
        oaUserBean.setZwoabh(jo.getString("\u804c\u52a1oa\u7f16\u53f7"));
        oaUserBean.setZwmc(jo.getString("\u804c\u52a1\u540d\u79f0"));
        oaUserBean.setGwoabh(jo.getString("\u5c97\u4f4doa\u7f16\u53f7"));
        oaUserBean.setGwmc(jo.getString("\u5c97\u4f4d\u540d\u79f0"));
        oaUserBean.setSjzg(jo.getString("\u4e0a\u7ea7\u4e3b\u7ba1"));
        oaUserBean.setUpdatetime(this.parseTimeStamp(jo.getString("updatetime")));
    }

    private void parseOaNewClient(JSONObject jo, EmcOaNewClientBean oaClientBean) {
        oaClientBean.setCode(jo.getString("code"));
        oaClientBean.setName(jo.getString("name"));
        oaClientBean.setPhone(jo.getString("tel"));
        oaClientBean.setAddress(jo.getString("address"));
        oaClientBean.setMail(jo.getString("email"));
        oaClientBean.setCreditCode(jo.getString("uniform_social_credit_code"));
        oaClientBean.setStatus(jo.getString("life_status"));
        oaClientBean.setOaCreatedTime(this.parseTimeStamp(jo.getString("create_time")));
        oaClientBean.setOaUpdateTime(this.parseTimeStamp(jo.getString("update_time")));
    }

    private void parseOaBank(JSONObject jo, EmcOaBankBean oaBean, String clientCode) {
        oaBean.setInterbanknumber(jo.getString("interbank_number"));
        oaBean.setAccountbankno(jo.getString("account_bank_no"));
        oaBean.setBankdeposit(jo.getString("bank_deposit"));
        oaBean.setAccountbank(jo.getString("account_bank"));
        oaBean.setBankname(jo.getString("bank_name"));
        oaBean.setTitletype(jo.getString("title_type"));
        oaBean.setClientCode(clientCode);
    }

    private void parseOaClient(JSONObject jo, EmcOaClientBean oaClientBean) {
        oaClientBean.setBm(jo.getString("code"));
        oaClientBean.setCjr(jo.getString("\u521b\u5efa\u4eba"));
        oaClientBean.setCjsj(this.parseTimeStamp(jo.getString("\u521b\u5efa\u65f6\u95f4")));
        oaClientBean.setGxczr(jo.getString("\u64cd\u4f5c\u4eba"));
        oaClientBean.setGxsj(this.parseTimeStamp(jo.getString("\u66f4\u65b0\u65f6\u95f4")));
        oaClientBean.setSfdj(jo.getString("\u662f\u5426\u51bb\u7ed3"));
        oaClientBean.setSfyx(jo.getString("\u662f\u5426\u6709\u6548"));
        oaClientBean.setKhybm(jo.getString("\u5ba2\u6237\u539f\u7f16\u7801"));
        oaClientBean.setKhmc(jo.getString("\u5ba2\u6237\u540d\u79f0"));
        oaClientBean.setKhlx(jo.getString("\u5ba2\u6237\u7c7b\u578b"));
        oaClientBean.setSf(jo.getString("\u7701\u4efd"));
        oaClientBean.setXypjdj(jo.getString("\u4fe1\u7528\u8bc4\u4ef7\u7b49\u7ea7"));
        oaClientBean.setSfgys(jo.getString("\u662f\u5426\u4f9b\u5e94\u5546"));
        oaClientBean.setKhcwfl(jo.getString("\u5ba2\u6237\u8d22\u52a1\u5206\u7c7b"));
        oaClientBean.setKhcwlx(jo.getString("\u5ba2\u6237\u8d22\u52a1\u7c7b\u578b"));
        oaClientBean.setSffc(jo.getString("\u662f\u5426\u5c01\u5b58"));
        oaClientBean.setTyshxydm(jo.getString("\u7edf\u4e00\u793e\u4f1a\u4fe1\u7528\u4ee3\u7801"));
        oaClientBean.setQyzt(jo.getString("\u4f01\u4e1a\u72b6\u6001"));
        oaClientBean.setFddbr(jo.getString("\u6cd5\u5b9a\u4ee3\u8868\u4eba"));
        oaClientBean.setFddbrlxfs(jo.getString("\u6cd5\u5b9a\u4ee3\u8868\u4eba\u8054\u7cfb\u65b9\u5f0f"));
        oaClientBean.setClrq(DateTimeUtils.parseLocalDateTime(jo.getString("\u6210\u7acb\u65e5\u671f")));
        oaClientBean.setDxrq(DateTimeUtils.parseLocalDateTime(jo.getString("\u540a\u9500\u65e5\u671f")));
        oaClientBean.setDjjg(jo.getString("\u767b\u8bb0\u673a\u5173"));
        oaClientBean.setZzjgdm(jo.getString("\u7ec4\u7ec7\u673a\u6784\u4ee3\u7801"));
        oaClientBean.setZczb(jo.getString("\u6ce8\u518c\u8d44\u672c"));
        oaClientBean.setSjzb(jo.getString("\u5b9e\u7f34\u8d44\u672c"));
        oaClientBean.setQyfl(jo.getString("\u4f01\u4e1a\u5206\u7c7b"));
        oaClientBean.setDz(jo.getString("\u5730\u5740"));
        oaClientBean.setYyksrq(this.parseTimeStamp(jo.getString("\u8425\u4e1a\u5f00\u59cb\u65e5\u671f")));
        oaClientBean.setYyjsrq(this.parseTimeStamp(jo.getString("\u8425\u4e1a\u7ed3\u675f\u65e5\u671f")));
        oaClientBean.setFzrq(this.parseTimeStamp(jo.getString("\u53d1\u7167\u65e5\u671f")));
        oaClientBean.setSfss(jo.getString("\u662f\u5426\u4e0a\u5e02"));
        oaClientBean.setSslx(jo.getString("\u4e0a\u5e02\u7c7b\u578b"));
        oaClientBean.setGpdm(jo.getString("\u80a1\u7968\u4ee3\u7801"));
        oaClientBean.setKhsfzh(jo.getString("\u5ba2\u6237\u8eab\u4efd\u8bc1\u53f7"));
        oaClientBean.setKhzbm(jo.getString("\u5ba2\u6237\u5b50\u7f16\u7801"));
        oaClientBean.setYwjl(jo.getString("\u4e1a\u52a1\u7ecf\u7406"));
        oaClientBean.setJtmc(jo.getString("\u96c6\u56e2\u540d\u79f0"));
        oaClientBean.setJtlb(jo.getString("\u96c6\u56e2\u7c7b\u522b"));
    }

    private void parseOaArea(JSONObject jo, EmcOaAreaBean oaBean) {
        oaBean.setBm(jo.getString("\u7f16\u7801"));
        oaBean.setCjr(jo.getString("\u521b\u5efa\u4eba"));
        oaBean.setCjsj(this.parseTimeStamp(jo.getString("\u521b\u5efa\u65f6\u95f4")));
        oaBean.setCzr(jo.getString("\u64cd\u4f5c\u4eba"));
        oaBean.setGxsj(this.parseTimeStamp(jo.getString("\u66f4\u65b0\u65f6\u95f4")));
        oaBean.setSfdj(jo.getString("\u662f\u5426\u51bb\u7ed3"));
        oaBean.setSfyx(jo.getString("\u662f\u5426\u6709\u6548"));
        oaBean.setZddm(jo.getString("\u5b57\u5178\u4ee3\u7801"));
        oaBean.setZdmc(jo.getString("\u5b57\u5178\u540d\u79f0"));
        oaBean.setXzqhdm(jo.getString("\u884c\u653f\u533a\u5212\u4ee3\u7801"));
        oaBean.setXzqhmc(jo.getString("\u884c\u653f\u533a\u5212\u540d\u79f0"));
        oaBean.setDzyx(jo.getString("\u884c\u653f\u533a\u5212\u540d\u79f0"));
    }

    private void parseOaEquipt(JSONObject jo, EmcOaEquiptBean oaBean) {
        oaBean.setFnamark(jo.getString("fnamark"));
        oaBean.setSelectdate(DateTimeUtils.parseLocalDateTime(jo.getString("selectdate")));
        oaBean.setSbbh(jo.getString("sbbh"));
        oaBean.setBlongdepartment(jo.getLong("blongdepartment"));
        oaBean.setDepartmentname(jo.getString("departmentname"));
        oaBean.setCreatedatetime(this.parseTimeStamp(jo.getString("createdatetime")));
        oaBean.setLastmoddatetime(this.parseTimeStamp(jo.getString("lastmoddatetime")));
        oaBean.setEquiptid(jo.getLong("id"));
        oaBean.setMark(jo.getString("mark"));
        oaBean.setName(jo.getString("name"));
        oaBean.setBarcode(jo.getString("barcode"));
        oaBean.setCapitalspec(jo.getString("capitalspec"));
    }

    private void syncCoreUserPermission() {
        List<CoreUserBean> userBeanList = this.coreUserService.selectListByFilter(SearchFilter.instance().match("parentorgid", null).filter(MatchPattern.DIFFER), new Order[0]);
        List<CoreUserPermissionBean> userPermissionBeanList = this.coreUserPermissionService.selectAll();
        ArrayList<CoreUserPermissionBean> insertBeanList = new ArrayList<CoreUserPermissionBean>();
        for (CoreUserBean u : userBeanList) {
            CoreUserPermissionBean newBean;
            if (userPermissionBeanList.stream().noneMatch(e -> StringUtils.equals(e.getTargetId(), u.getExt$Item("parentorgid")) && StringUtils.equals(e.getUserId(), u.getId()))) {
                newBean = new CoreUserPermissionBean();
                newBean.setId(ApplicationContextHelper.getNextIdentity());
                newBean.setUserId(u.getId());
                newBean.setTargetId(u.getExt$Item("parentorgid"));
                newBean.setPermissionTypeId("T_CORE_ORG");
                insertBeanList.add(newBean);
            }
            if (!userPermissionBeanList.stream().noneMatch(e -> StringUtils.equals(e.getTargetId(), u.getOrgId()) && StringUtils.equals(e.getUserId(), u.getId()))) continue;
            newBean = new CoreUserPermissionBean();
            newBean.setId(ApplicationContextHelper.getNextIdentity());
            newBean.setUserId(u.getId());
            newBean.setTargetId(u.getOrgId());
            newBean.setPermissionTypeId("T_CORE_ORG");
            insertBeanList.add(newBean);
        }
        this.coreUserPermissionService.getDao().insert(insertBeanList);
    }

    private LocalDateTime parseTimeStamp(String timeStampStr) {
        if (timeStampStr == null) {
            return null;
        }
        long timestamp = NumberUtils.parseLong(timeStampStr, 0L);
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime;
    }

    private String getDefaultPassword(String userId) {
        return this.getEncodedPassword(userId);
    }

    private String getEncodedPassword(String rawPassword) {
        return this.passwordEncoder.encode(rawPassword);
    }
}
