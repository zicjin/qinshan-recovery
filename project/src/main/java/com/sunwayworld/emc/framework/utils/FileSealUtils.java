/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.framework.utils;

import com.sunwayworld.framework.exception.InvalidDataException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.qiyuesuo.sdk.SDKClient;
import net.qiyuesuo.sdk.api.ContractService;
import net.qiyuesuo.sdk.bean.company.TenantType;
import net.qiyuesuo.sdk.bean.contract.Action;
import net.qiyuesuo.sdk.bean.contract.ActionType;
import net.qiyuesuo.sdk.bean.contract.ContractQueryRequest;
import net.qiyuesuo.sdk.bean.contract.CreateContractRequest;
import net.qiyuesuo.sdk.bean.contract.StamperType;
import net.qiyuesuo.sdk.bean.document.CreateDocumentRequest;
import net.qiyuesuo.sdk.bean.document.CreateDocumentResult;
import net.qiyuesuo.sdk.bean.document.DownloadDocRequest;
import net.qiyuesuo.sdk.bean.sign.Signatory;
import net.qiyuesuo.sdk.bean.sign.SignatoryRect;
import net.qiyuesuo.sdk.common.http.StreamFile;
import net.qiyuesuo.sdk.impl.ContractServiceImpl;
import net.qiyuesuo.v2sdk.SdkV2Client;
import net.qiyuesuo.v2sdk.bean.CompanyRequest;
import net.qiyuesuo.v2sdk.bean.ContractRequest;
import net.qiyuesuo.v2sdk.bean.StamperBean;
import net.qiyuesuo.v2sdk.bean.UserRequest;
import net.qiyuesuo.v2sdk.request.ContractSignCompanyRequest;
import net.qiyuesuo.v2sdk.request.SilentCompanySignAuthRequest;
import net.qiyuesuo.v2sdk.request.SilentPersonSignAuthRequest;
import net.qiyuesuo.v2sdk.response.SdkResponse;
import net.qiyuesuo.v2sdk.response.SilentSignAuthPageResponse;
import net.qiyuesuo.v2sdk.utils.JSONUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileSealUtils {
    private static final Logger logger = LogManager.getLogger(FileSealUtils.class);
    private static final String URL = "http://10.100.2.33:9182";
    private static final String ACCESSKEY = "UYwa6ZKgWB";
    private static final String ACCESSSECRET = "z5ewQkHfxhGEE4DoDIYQ0DvFZONs6k";
    private static final Long SEALID = 3266954143383818788L;
    private static final double REPORT_ISSUE_STAMP_WIDTH = 14.0;
    private static final double REPORT_ISSUE_STAMP_HEIGHT = 7.0;
    private static final double REPORT_ISSUE_STAMP_OFFSET_X = 0.03;
    private static final double REPORT_ISSUE_STAMP_OFFSET_Y = 0.05;
    private static final String REPORT_ISSUE_KEYWORD = "\u62a5\u544a\u7b7e\u53d1\uff1a";
    private static final String REPORT_ISSUE_SEAL_CATEGORY_NAME = "\u5176\u4ed6";
    private static final String SEAL_COMPANYNAME = "\u9752\u5c71\u7eff\u6c34\uff08\u6c5f\u82cf\uff09\u68c0\u9a8c\u68c0\u6d4b\u6709\u9650\u516c\u53f8";

    public static void seal(String file, String downloadUrl, boolean isAcross, String fileName) {
        SDKClient client = new SDKClient(URL, ACCESSKEY, ACCESSSECRET);
        Long docId = FileSealUtils.createDoc(client, file);
        Long contractId = FileSealUtils.createContractByCategory(client, docId, isAcross);
        Integer keywordIndex = 2;
        if (fileName.contains("\u4e2a\u4eba\u5242\u91cf")) {
            keywordIndex = 3;
        }
        FileSealUtils.gaizhang(docId, contractId, keywordIndex);
        FileSealUtils.downloadDoc(client, docId, downloadUrl);
    }

    public static void signReportIssue(String file, String downloadUrl, Long sealId, String signerName, String signerMobile, String signerNo) {
        FileSealUtils.signReportIssue(file, downloadUrl, sealId);
    }

    public static void signReportIssue(String file, String downloadUrl, Long sealId) {
        if (sealId == null) {
            throw new InvalidDataException("\u5951\u7ea6\u9501\u62a5\u544a\u7b7e\u53d1\u7ae0ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        SDKClient client = new SDKClient(URL, ACCESSKEY, ACCESSSECRET);
        Long docId = FileSealUtils.createDoc(client, file);
        Long contractId = FileSealUtils.createIssueSignContract(client, docId, sealId);
        FileSealUtils.signIssue(docId, contractId, sealId);
        FileSealUtils.downloadDoc(client, docId, downloadUrl);
    }

    private static void gaizhang(Long docId, Long contractId, Integer keywordIndex) {
        try {
            SdkV2Client sdkClient = new SdkV2Client(URL, ACCESSKEY, ACCESSSECRET);
            ContractSignCompanyRequest request = new ContractSignCompanyRequest();
            ContractRequest contractRequest = new ContractRequest(contractId, null);
            request.setContract(contractRequest);
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setName(SEAL_COMPANYNAME);
            request.setCompany(companyRequest);
            StamperBean stamperBean = new StamperBean();
            stamperBean.setDocumentId(docId);
            stamperBean.setType("SEAL_CORPORATE");
            stamperBean.setSealId(SEALID);
            stamperBean.setKeyword("\u68c0\u9a8c\u68c0\u6d4b\u4e13\u7528\u7ae0");
            stamperBean.setKeywordIndex(keywordIndex);
            stamperBean.setOffsetX(-0.1);
            request.addStamper(stamperBean);
            StamperBean stamperBean2 = new StamperBean();
            stamperBean2.setDocumentId(docId);
            stamperBean2.setType("SEAL_CORPORATE");
            stamperBean2.setSealId(SEALID);
            stamperBean2.setKeyword(SEAL_COMPANYNAME);
            stamperBean2.setKeywordIndex(1);
            stamperBean2.setOffsetX(-0.35);
            request.addStamper(stamperBean2);
            String response = sdkClient.service(request);
            SdkResponse responseObj = JSONUtils.toQysResponse(response);
            if (responseObj.getCode() == 0) {
                logger.info("\u8bf7\u6c42\u6210\u529f");
            } else {
                logger.info("\u8bf7\u6c42\u5931\u8d25\uff0c\u9519\u8bef\u7801:{}\uff0c\u9519\u8bef\u4fe1\u606f:{}", (Object)responseObj.getCode(), (Object)responseObj.getMessage());
            }
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static void signIssue(Long docId, Long contractId, Long sealId) {
        try {
            SdkV2Client sdkClient = new SdkV2Client(URL, ACCESSKEY, ACCESSSECRET);
            ContractSignCompanyRequest request = new ContractSignCompanyRequest();
            request.setContract(new ContractRequest(contractId, null));
            CompanyRequest companyRequest = new CompanyRequest();
            companyRequest.setName(SEAL_COMPANYNAME);
            request.setCompany(companyRequest);
            request.setSealId(sealId);
            StamperBean stamperBean = new StamperBean();
            stamperBean.setDocumentId(docId);
            stamperBean.setType("SEAL_CORPORATE");
            stamperBean.setSealId(sealId);
            stamperBean.setKeyword(REPORT_ISSUE_KEYWORD);
            stamperBean.setKeywordIndex(1);
            stamperBean.setWidth(14.0);
            stamperBean.setHeight(7.0);
            stamperBean.setOffsetX(0.03);
            stamperBean.setOffsetY(0.05);
            request.addStamper(stamperBean);
            String response = sdkClient.service(request);
            SdkResponse responseObj = JSONUtils.toQysResponse(response);
            if (responseObj.getCode() == 0) {
                logger.info("\u62a5\u544a\u7b7e\u53d1\u7ec4\u7ec7\u7ae0\u8bf7\u6c42\u6210\u529f");
            } else {
                throw new InvalidDataException(responseObj.getMessage());
            }
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static void shouquan(SDKClient client) {
        try {
            SdkV2Client sdkClient = new SdkV2Client(URL, ACCESSKEY, ACCESSSECRET);
            SilentCompanySignAuthRequest request = new SilentCompanySignAuthRequest();
            CompanyRequest applyCompany = new CompanyRequest();
            applyCompany.setName(SEAL_COMPANYNAME);
            request.setApplyCompany(applyCompany);
            CompanyRequest authCompany = new CompanyRequest();
            authCompany.setName(SEAL_COMPANYNAME);
            request.setAuthCompany(authCompany);
            UserRequest userRequest = new UserRequest();
            userRequest.setMobile("18362232106");
            request.setAuthUser(userRequest);
            request.setAuthEndDate("2055-12-29");
            request.setAuthScope("\u9752\u5c71\u7eff\u6c34\uff08\u6c5f\u82cf\uff09\u68c0\u9a8c\u68c0\u6d4b\u7ae0");
            String response = sdkClient.service(request);
            SdkResponse<SilentSignAuthPageResponse> responseObj = JSONUtils.toQysResponse(response, SilentSignAuthPageResponse.class);
            if (responseObj.getCode() == 0) {
                SilentSignAuthPageResponse result = responseObj.getResult();
                logger.info("\u6388\u6743\u9875\u9762\u94fe\u63a5\uff1a{}", (Object)result.getUrl());
            } else {
                logger.info("\u8bf7\u6c42\u5931\u8d25\uff0c\u9519\u8bef\u7801:{}\uff0c\u9519\u8bef\u4fe1\u606f:{}", (Object)responseObj.getCode(), (Object)responseObj.getMessage());
            }
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    public static String applyReportIssueCompanySilentAuth(String authUserMobile, String authEndDate) {
        return FileSealUtils.applyReportIssueCompanySilentAuth(null, authUserMobile, null, authEndDate, REPORT_ISSUE_SEAL_CATEGORY_NAME);
    }

    public static String applyReportIssueCompanySilentAuth(String authUserName, String authUserMobile, String authUserNo, String authEndDate, String authScope) {
        try {
            SdkV2Client sdkClient = new SdkV2Client(URL, ACCESSKEY, ACCESSSECRET);
            SilentCompanySignAuthRequest request = new SilentCompanySignAuthRequest();
            CompanyRequest applyCompany = new CompanyRequest();
            applyCompany.setName(SEAL_COMPANYNAME);
            request.setApplyCompany(applyCompany);
            CompanyRequest authCompany = new CompanyRequest();
            authCompany.setName(SEAL_COMPANYNAME);
            request.setAuthCompany(authCompany);
            UserRequest authUser = new UserRequest();
            authUser.setName(authUserName);
            authUser.setMobile(authUserMobile);
            authUser.setNumber(authUserNo);
            request.setAuthUser(authUser);
            request.setAuthEndDate(authEndDate);
            request.setAuthScope(authScope);
            String response = sdkClient.service(request);
            SdkResponse<SilentSignAuthPageResponse> responseObj = JSONUtils.toQysResponse(response, SilentSignAuthPageResponse.class);
            if (responseObj.getCode() == 0) {
                SilentSignAuthPageResponse result = responseObj.getResult();
                logger.info("\u62a5\u544a\u7b7e\u53d1\u7ec4\u7ec7\u7ae0\u9759\u9ed8\u7b7e\u7f72\u6388\u6743\u9875\u9762\u94fe\u63a5\uff1a{}", (Object)result.getUrl());
                return result.getUrl();
            }
            throw new InvalidDataException(responseObj.getMessage());
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    public static String applyReportIssueSilentAuth(String signerName, String signerMobile, String signerNo, String authEndDate, String authScope) {
        try {
            SdkV2Client sdkClient = new SdkV2Client(URL, ACCESSKEY, ACCESSSECRET);
            SilentPersonSignAuthRequest request = new SilentPersonSignAuthRequest();
            CompanyRequest applyCompany = new CompanyRequest();
            applyCompany.setName(SEAL_COMPANYNAME);
            request.setApplyCompany(applyCompany);
            UserRequest authUser = new UserRequest();
            authUser.setName(signerName);
            authUser.setMobile(signerMobile);
            authUser.setNumber(signerNo);
            request.setAuthUser(authUser);
            request.setAuthEndDate(authEndDate);
            request.setAuthScope(authScope);
            String response = sdkClient.service(request);
            SdkResponse<SilentSignAuthPageResponse> responseObj = JSONUtils.toQysResponse(response, SilentSignAuthPageResponse.class);
            if (responseObj.getCode() == 0) {
                SilentSignAuthPageResponse result = responseObj.getResult();
                logger.info("\u4e2a\u4eba\u9759\u9ed8\u7b7e\u7f72\u6388\u6743\u9875\u9762\u94fe\u63a5\uff1a{}", (Object)result.getUrl());
                return result.getUrl();
            }
            throw new InvalidDataException(responseObj.getMessage());
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static Long createDoc(SDKClient client, String file) {
        try {
            File originalFile = new File(file);
            logger.info("\u521b\u5efa\u5408\u540c\u6587\u4ef6\u5730\u5740\uff1a" + file);
            if (!originalFile.exists() || originalFile.length() == 0L) {
                logger.info("\u521b\u5efa\u5408\u540c\u6587\u4ef6\u5927\u5c0f\u5f02\u5e38\uff1a" + file);
            }
            ContractService contractService = new ContractServiceImpl(client);
            CreateDocumentRequest request = new CreateDocumentRequest();
            request.setFile(new StreamFile(new FileInputStream(originalFile)));
            request.setFileType("pdf");
            request.setTitle("\u6587\u68631");
            CreateDocumentResult result = contractService.createByFile(request);
            Long documentId = result.getDocumentId();
            logger.info("\u521b\u5efa\u5408\u540c\u6587\u4ef6\u6210\u529f,documentId:{}", (Object)documentId);
            return documentId;
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static Long createContractByCategory(SDKClient client, Long docId, Boolean isAcross) {
        try {
            ContractService contractService = new ContractServiceImpl(client);
            CreateContractRequest createContractRequest = new CreateContractRequest();
            createContractRequest.setSend(true);
            createContractRequest.setSubject("\u9752\u5c71\u7eff\u6c34\u62a5\u544a");
            List<Long> docIds = new ArrayList<Long>();
            docIds.add(docId);
            createContractRequest.setDocuments(docIds);
            createContractRequest.setCategoryName("\u9ed8\u8ba4");
            List<Signatory> signatories = new ArrayList<Signatory>();
            Signatory signatory1 = new Signatory();
            signatory1.setTenantType(TenantType.INNER_COMPANY);
            signatory1.setTenantName(SEAL_COMPANYNAME);
            signatory1.setSerialNo(1);
            List<Action> actions = new ArrayList<Action>();
            Action action = new Action();
            action.setType(ActionType.CORPORATE);
            action.setName("\u7ec4\u7ec7\u7b7e\u7ae0");
            action.setSerialNo(1);
            action.setSealId(SEALID);
            if (isAcross.booleanValue()) {
                List<SignatoryRect> rectList = new ArrayList<SignatoryRect>();
                SignatoryRect rect = new SignatoryRect();
                rect.setDocumentId(docId);
                rect.setRectType(StamperType.ACROSS_PAGE);
                rect.setPage(0);
                rect.setOffsetX(0.01);
                rect.setOffsetY(0.5);
                rectList.add(rect);
                action.setLocations(rectList);
            }
            actions.add(action);
            signatory1.setActions(actions);
            signatories.add(signatory1);
            createContractRequest.setSignatories(signatories);
            ContractQueryRequest contractQueryRequest = new ContractQueryRequest();
            contractQueryRequest.setContact("18015881125");
            contractQueryRequest.setContractStatus("SIGNING");
            Long contractId = contractService.createContractByCategory(createContractRequest);
            logger.info("\u521b\u5efa\u5408\u540c\u6210\u529f,contractId:{}", (Object)contractId);
            return contractId;
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static Long createIssueSignContract(SDKClient client, Long docId, Long sealId) {
        try {
            ContractService contractService = new ContractServiceImpl(client);
            CreateContractRequest createContractRequest = new CreateContractRequest();
            createContractRequest.setSend(true);
            createContractRequest.setSubject("\u9752\u5c71\u7eff\u6c34\u62a5\u544a");
            List<Long> docIds = new ArrayList<Long>();
            docIds.add(docId);
            createContractRequest.setDocuments(docIds);
            createContractRequest.setCategoryName("\u9ed8\u8ba4");
            Signatory signatory = new Signatory();
            signatory.setTenantType(TenantType.INNER_COMPANY);
            signatory.setTenantName(SEAL_COMPANYNAME);
            signatory.setSerialNo(1);
            Action action = new Action();
            action.setType(ActionType.CORPORATE);
            action.setName("\u62a5\u544a\u7b7e\u53d1\u7ae0");
            action.setSerialNo(1);
            action.setSealId(sealId);
            action.setSealCategoryName(REPORT_ISSUE_SEAL_CATEGORY_NAME);
            List<Action> actions = new ArrayList<Action>();
            actions.add(action);
            signatory.setActions(actions);
            List<Signatory> signatories = new ArrayList<Signatory>();
            signatories.add(signatory);
            createContractRequest.setSignatories(signatories);
            Long contractId = contractService.createContractByCategory(createContractRequest);
            logger.info("\u521b\u5efa\u62a5\u544a\u7b7e\u53d1\u7ec4\u7ec7\u7ae0\u5408\u540c\u6210\u529f,contractId:{}", (Object)contractId);
            return contractId;
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private static void downloadDoc(SDKClient client, Long docId, String downloadUrl) {
        try {
            ContractService contractService = new ContractServiceImpl(client);
            DownloadDocRequest request = new DownloadDocRequest();
            FileOutputStream fos = new FileOutputStream(downloadUrl);
            request.setDocumentId(docId);
            request.setContact("10000000001");
            request.setOutputStream(fos);
            contractService.downloadDoc(request);
            logger.info("\u4e0b\u8f7d\u6587\u4ef6\u6210\u529f");
        }
        catch (Exception e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    public static void testReportIssueSign(String file, String downloadUrl, Long sealId, String signerName, String signerMobile, String signerNo) {
        FileSealUtils.signReportIssue(file, downloadUrl, sealId, signerName, signerMobile, signerNo);
    }

    public static void main(String[] args) {
        String file = "C:\\Users\\1\\Desktop\\\u7efc\u5408\u62a5\u544a-CQTW251074.pdf";
        String downloadUrl = "C:\\Users\\1\\Desktop\\\u4e2a\u4eba\u7ae0\u6d4b\u8bd5.pdf";
        Long sealId = 3497061806970045067L;
        String signerName = "\u6bdb\u4e9a\u4e91";
        String signerMobile = "13584510869";
        String signerNo = "00544";
        FileSealUtils.testReportIssueSign(file, downloadUrl, sealId, signerName, signerMobile, signerNo);
    }
}
