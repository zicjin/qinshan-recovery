package com.sunwayworld.emc.framework.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.gexin.fastjson.JSON;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.OkHttpClientUtils;

/**
 * 苏交科集团股份有限公司电子签章平台接口辅助类
 * @author gerl@sunwayworld.com
 * 2021年8月11日
 */
public class JSTIeSealHelper {

	private static Logger logger = LogManager.getLogger(JSTIeSealHelper.class);

	private static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

	private static final String CODE = "code";
	private static final String MESSAGE = "message";

	public static File addWatermark(File file, Long projectId, String projectNo, String fileName, String orgName) {
		// 创建合同文档
		String documentId = createDocumentByFile(file, projectNo);
		if(StringUtils.isEmpty(documentId)) {
			throw new UnexpectedException("调用签章接口创建合同文档失败");
		}

		// 添加水印
		addWatermark(documentId, orgName);

		// 下载合同文档
		File documentFile = downloadDocumentFile(documentId, fileName);
		if(documentFile == null) {
			throw new UnexpectedException("下载水印文档失败");
		}
		logger.info("添加水印成功：" + documentFile.getAbsolutePath());
		// 返回
		return documentFile;
	}

	public static void addWatermark(String documentId, String orgName) {
		// 请求地址
		String url = ApplicationContextHelper.getConstantValue("IFDC_JSTI_ADD_WATERMARK_URL");
		if(StringUtils.isEmpty(url)) {
			throw new CheckedException("系统参数未配置！<br>添加水印地址[IFDC_JSTI_ADD_WATERMARK_URL]");
		}

		logger.info("添加水印请求地址："+url);
		// 请求头
		Map<String,String> headerMap = getHeaderMap();

		Map<String, String> params = new HashMap<>(5);
		params.put("documentId", documentId);
		params.put("type", "TEXT");
		params.put("content", orgName);
		params.put("location", "TILE");
		params.put("rotateAngle", "45");

		try {
			logger.info("传参：{}", JSON.toJSONString(params));
			String result = post(url, JSON.toJSONString(params), headerMap);
			logger.info("返回：{}", result);
			JSONObject resultJson = JSONObject.parseObject(result);
			if (resultJson.getInteger(CODE) != 0) {
				String message = resultJson.getString(MESSAGE);
				String cnMessage = unicodeToCN(message);
				logger.error("message：{}", cnMessage);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new UnexpectedException("调用添加水印接口失败");
		}
	}

	/**
	 * 给一个文件进行电子签章，返回已签章的文件
	 * @param file 检品报告文件
	 * @param folderId 检品报告书ID
	 * @param folderNo 检品报告书编码
	 * @return
	 */
	public static File sealFile(File file, String folderId, String folderNo, String fileName) {
		// 创建合同文档
		String documentId = createDocumentByFile(file, folderNo);
		if(StringUtils.isEmpty(documentId)) {
			throw new UnexpectedException("调用签章接口创建合同文档失败");
		}
		// 创建合同
		String contractId = createContract(documentId, folderId);
		if(StringUtils.isEmpty(contractId)) {
			throw new UnexpectedException("调用签章接口创建合同失败");
		}
		// 静默签署
		boolean signed = signContractByCompany(folderId, contractId, documentId);
		if(!signed) {
			throw new UnexpectedException("调用静默签章接口失败");
		}
		// 下载合同文档
		File documentFile = downloadDocumentFile(documentId, fileName);
		if(documentFile == null) {
			throw new UnexpectedException("下载签章文档失败");
		}
		logger.info("签章成功：" + documentFile.getAbsolutePath());
		// 返回
		return documentFile;
	}

	/**
	 * 签章平台请求header参数设置
	 * @return
	 */
	private static Map<String,String> getHeaderMap() {
		// 签章平台配置的应用令牌
		String appToken = ApplicationContextHelper.getConstantValue("IFDC_JSTI_SEAL_TOKEN");
		if(StringUtils.isEmpty(appToken)) {
			throw new CheckedException("系统参数未配置！签章应用令牌[IFDC_JSTI_SEAL_TOKEN]");
		}
		// 签章平台配置的应用密钥
		String appSecret = ApplicationContextHelper.getConstantValue("IFDC_JSTI_SEAL_SECRET");
		if(StringUtils.isEmpty(appSecret)) {
			throw new CheckedException("系统参数未配置！签章应用密钥[IFDC_JSTI_SEAL_SECRET]");
		}
		// 发起时间
		String timestamp = String.valueOf(System.currentTimeMillis());
		// 随机数
		String nonce = UUID.randomUUID().toString().replaceAll("-", "");
		// 签名
		String source = appToken + appSecret + timestamp + nonce;
		String signature = DigestUtils.md5DigestAsHex(source.getBytes());

		// 请求头
		Map<String,String> headerMap = new HashMap<>(5);
		// 应用令牌
		headerMap.put("x-qys-accesstoken", appToken);
		// 发起时间
		headerMap.put("x-qys-timestamp", timestamp);
		// 随机数
		headerMap.put("x-qys-nonce", nonce);
		// 签名
		headerMap.put("x-qys-signature", signature);
		// 内容类型
		headerMap.put("Content-Type","application/json");

		return headerMap;
	}

	/**
	 * 根据文件类型创建合同文档
	 * @param file 文件
	 * @param fileName 文档名称
	 * @return 合同文档ID
	 */
	private static String createDocumentByFile(File file, String fileName) {
		if(file == null || !file.exists()) {
			throw new CheckedException("文件不存在");
		}
		// 请求地址
		String url = ApplicationContextHelper.getConstantValue("IFDC_JSTI_CREATE_DOCUMENT_URL");
		if(StringUtils.isEmpty(url)) {
			throw new CheckedException("系统参数未配置！<br>创建合同文档地址[IFDC_JSTI_CREATE_DOCUMENT_URL]");
		}
		logger.info("创建合同文档请求地址："+url);
		// 请求头
		Map<String,String> headerMap = getHeaderMap();
		// 内容类型
		headerMap.put("Content-Type","multipart/form-data");
		try {
			// 文件参数
			Map<String,File> fileParamMap = new HashMap<>();
			fileParamMap.put("file", file);
			// 其他参数
			Map<String,String> paramMap = getCreateDocumentByFileParam(file, fileName);
			// 发送请求
			logger.info("传参：" + paramMap.toString());
			String result = post(url, fileParamMap, paramMap, headerMap);
			logger.info("返回：" + result);
			// 返回：{"result":{"documentId":"2861095219929056195"},"code":0,"message":"SUCCESS"}
			// 解析结果
			JSONObject obj = JSONObject.parseObject(result);
			if("0".equals(obj.getString("code"))){
				JSONObject resultObj = obj.getJSONObject("result");
				if(resultObj != null) {
					return resultObj.getString("documentId");
				}
			}else {
				String message = obj.getString("message");
				String cnmsg = unicodeToCN(message);
				logger.error("message："+cnmsg);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/**
	 * 获取创建合同文件接口参数
	 * 文件尺寸
	 * 		A0(841,1189)，A1(594,841)，A2(420,594)，A3(297,420)，A4(210,297)，A5(148,210)，A6(105,148)，A7(74,105)，A8(52,74)
	 * 		B0(1000,1414)，B1(707,1000)，B2(500,707)，B3(353,500)，B4(250,353)，B5(176,250)，B6(125,176)，B7(88,125)，B8(62,88)
	 * @param file
	 * @param fileName
	 * @return
	 */
	private static Map<String,String> getCreateDocumentByFileParam(File file, String fileName) {
		Map<String,String> paramMap = new HashMap<>();
		// 文档名称
		if(StringUtils.isEmpty(fileName)) {
			fileName = file.getName();
		}
		paramMap.put("title", fileName);
		// 文件类型：doc, docx, txt, pdf, png, gif, jpg, jpeg, tiff, html, rtf, xls, txt
		String fileType = FileUtils.getFileExtension(file);
		paramMap.put("fileType", fileType);

		// 宽（文件尺寸，单位：mm）
		paramMap.put("width", "210");
		// 宽（文件尺寸，单位：mm）
		paramMap.put("height", "297");
		return paramMap;
	}

	/**
	 * 创建合同
	 * @param documentId 合同文档ID
	 * @param folderId 报告书ID
	 * @return 合同ID
	 */
	private static String createContract(String documentId, String folderId) {
		// 请求地址
		String url = ApplicationContextHelper.getConstantValue("IFDC_JSTI_CREATE_CONTRACT_URL");
		if(StringUtils.isEmpty(url)) {
			throw new CheckedException("系统参数未配置！<br>创建合同地址[IFDC_JSTI_CREATE_CONTRACT_URL]");
		}
		logger.info("创建合同请求地址："+url);
		// 请求头
		Map<String,String> headerMap = getHeaderMap();
		try {
			String param = getCreateContractParam(documentId, folderId);
			logger.info("传参：" + param);
			String result = post(url, param, headerMap);
			logger.info("返回："+result);
			//返回：{"message":"\u9ED8\u8BA4\u7528\u5370\u6D41\u7A0B\u4E0D\u5B58\u5728","code":1000001} 默认用印流程不存在
			//返回： {"code":0,"contractId":"2861108057464669126","message":"SUCCESS"}
			JSONObject obj = JSONObject.parseObject(result);
			if("0".equals(obj.getString("code"))){
				String contractId = obj.getString("contractId");
				return contractId;
			}else {
				String message = obj.getString("message");
				String cnmsg = unicodeToCN(message);
				logger.error("message："+cnmsg);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	/**
	 * 获取创建合同的参数
	 * @param documentId 合同文档ID
	 * @param folderId 报告书id
	 * @return
	 */
	private static String getCreateContractParam(String documentId, String folderId) {
		// 调用函数获取参数
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT F_SYS_JSTI_CONTRACT_PARAM('");
		buf.append(folderId);
		buf.append("','");
		buf.append(documentId);
		buf.append("') VAL FROM DUAL");

		GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
		String param = globalMapper.selectOneString(buf.toString());
		if(StringUtils.isEmpty(param)) {
			throw new CheckedException("创建合同参数返回空！<br>函数[F_SYS_JSTI_CONTRACT_PARAM]");
		}
		return param;
	}

	/**
	 * unicode转中文
	 * @param unicodeStr
	 * @return
	 */
	private static String unicodeToCN(String unicodeStr) {
		if(unicodeStr == null) {
			return null;
		}
		Matcher matcher = pattern.matcher(unicodeStr);
		char ch;
		while (matcher.find()) {
			String group2 = matcher.group(2);
			ch = (char) Integer.parseInt(group2, 16);
			String group1 = matcher.group(1);
			unicodeStr = unicodeStr.replace(group1, ch + "");
		}
		return unicodeStr.replace("\\", "").trim();
	}
	/**
	 * 静默签署合同公司公章
	 */
	private static boolean signContractByCompany(String folderId, String contractId, String documentId) {
		// 请求地址
		String url = ApplicationContextHelper.getConstantValue("IFDC_JSTI_SIGN_BY_COMPANY_URL");
		if(StringUtils.isEmpty(url)) {
			throw new CheckedException("系统参数未配置！<br>静默签署公司公章接口地址[IFDC_JSTI_SIGN_BY_COMPANY_URL]");
		}
		logger.info("静默签章请求地址：" + url);
		try {
			// 请求头
			Map<String,String> headerMap = getHeaderMap();
			// 请求参数
			String param = getSignContractByCompanyParam(folderId, contractId, documentId);
			logger.info("传参：" + param);
			String result = post(url, param, headerMap);
			logger.info("返回："+result);
			//返回：
			//{"message":"\u516C\u53F8\u4E0D\u5B58\u5728","code":2002002} // 公司不存在
			//{"message":"\u4E0D\u80FD\u7B7E\u7F72\u5916\u90E8\u4F01\u4E1A","code":1000001} // 不能签署外部企业
			//{"message":"\u7B7E\u7F72\u5931\u8D25\uFF1A2861108057464669126;\u6CA1\u6709\u8BBE\u7F6E\u53C2\u6570\uFF1Astampers","code":1000000}
			// 签署失败：2861108057464669126;没有设置参数：stampers
			//返回：{"code":0,"message":"SUCCESS"}
			JSONObject obj = JSONObject.parseObject(result);
			if("0".equals(obj.getString("code"))){
				return true;
			}else {
				String message = obj.getString("message");
				String cnmsg = unicodeToCN(message);
				logger.error("message：" + cnmsg);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	/**
	 * 获取静默签署公司公章接口的参数
	 * @param contractId 合同ID
	 * @param documentId 合同文档ID
	 * @return 参数JSON字符串
	 */
	private static String getSignContractByCompanyParam(String folderId, String contractId, String documentId) {

		StringBuffer buf = new StringBuffer();
		buf.append("SELECT F_SYS_JSTI_SIGNBYCOMPANY_PARAM('");
		buf.append(folderId);
		buf.append("','");
		buf.append(documentId);
		buf.append("','");
		buf.append(contractId);
		buf.append("') VAL FROM DUAL");

		GlobalMapper globalMapper = ApplicationContextHelper.getBean(GlobalMapper.class);
		String param = globalMapper.selectOneString(buf.toString());
		if(StringUtils.isEmpty(param)) {
			throw new CheckedException("获取静默签署参数返回空！<br>函数[F_SYS_JSTI_SIGNBYCOMPANY_PARAM]");
		}
		return param;
	}

	/**
	 * 下载合同文档
	 */
	private static File downloadDocumentFile(String documentId, String fileName) {
		File file = null;
		// 请求地址
		String url = ApplicationContextHelper.getConstantValue("IFDC_JSTI_DOWNLOAD_DOCUMENT_URL");
		if(StringUtils.isEmpty(url)) {
			throw new CheckedException("系统参数未配置！<br>下载合同文档接口地址[IFDC_JSTI_DOWNLOAD_DOCUMENT_URL]");
		}
		StringBuffer buf = new StringBuffer(url);
		buf.append(url.contains("?") ? "&" : "&");
		buf.append("documentId=");
		buf.append(documentId);
		url = buf.toString();
		logger.info("下载合同文档请求地址：" + url);

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		InputStream ins = null;
		FileOutputStream out = null;
		try {
			// get请求也需要设置header
			httpClient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			Map<String,String> headerMap = getHeaderMap();
			for(String key : headerMap.keySet()) {
				if(key != "Content-Type") {
					httpget.addHeader(key,headerMap.get(key));
				}
			}
			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			ins = entity.getContent();
			// 临时存储文件
			file = FileHelper.createTempFile(fileName, 5000L);
			out = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int len = ins.read(b);
			while(len!=-1) {
				out.write(b,0,len);
				len = ins.read(b);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if(ins!=null) {
				try {
					ins.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if(response!=null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if(httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return file;
	}

	private static String post(String url, Map<String, File> fileParamMap, Map<String, String> paramMap, Map<String, String> headers) throws IOException {
		MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if (MapUtils.isNotEmpty(fileParamMap)) {
			fileParamMap.forEach((key, file) -> requestBody.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file)));
		}

		if (MapUtils.isNotEmpty(paramMap)) {
			paramMap.forEach(requestBody::addFormDataPart);
		}

		Request.Builder requestBuilder = new Request.Builder();
		if (MapUtils.isNotEmpty(headers)) {
			headers.forEach(requestBuilder::header);
		}

		RequestBody body = requestBody.build();
		Request request = requestBuilder.url(url).method("POST", body).build();
		Response response = OkHttpClientUtils.execute(request);

		if (response.body() != null) {
			return response.body().string();
		}

		return null;
	}

	private static String post(String url, String data, Map<String, String> headers) throws IOException {
		RequestBody body = RequestBody.create(MediaType.parse("application/json"), StringUtils.isEmpty(data) ? "{}" : data);
		Request.Builder requestBuilder = new Request.Builder();
		if (MapUtils.isNotEmpty(headers)) {
			headers.forEach(requestBuilder::header);
		}

		Request request = requestBuilder.url(url).method("POST", body).build();
		Response response = OkHttpClientUtils.execute(request);

		if (response.body() != null) {
			return response.body().string();
		}

		return null;
	}
}
