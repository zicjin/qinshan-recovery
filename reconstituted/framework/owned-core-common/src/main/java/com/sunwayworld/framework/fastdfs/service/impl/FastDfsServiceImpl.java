/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.fastdfs.bean.FastDfsDeleteResponseDTO;
import com.sunwayworld.framework.fastdfs.bean.FastDfsInfoResponseDTO;
import com.sunwayworld.framework.fastdfs.bean.FastDfsUploadResponseDTO;
import com.sunwayworld.framework.fastdfs.core.FastDfsUploadConfig;
import com.sunwayworld.framework.fastdfs.loadbalancer.FastDfsRule;
import com.sunwayworld.framework.fastdfs.service.FastDfsService;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.OkHttpClientUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
@ConditionalOnProperty(prefix="sunway.fastdfs", name={"enabled"}, havingValue="true")
public class FastDfsServiceImpl
implements FastDfsService {
    @Autowired
    private FastDfsRule fastDfsRule;

    @Override
    public FastDfsUploadResponseDTO upload(FastDfsUploadConfig config, MultipartFile multipartFile) {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MultipartBody.FORM, multipartFile.getBytes());
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
        return this.upload(Optional.ofNullable(config.getGroup()).orElse("group"), config.getTimestamp(), Objects.requireNonNull(config.getPath()), Optional.ofNullable(config.getFileName()).orElse(multipartFile.getOriginalFilename()), requestBody);
    }

    @Override
    public FastDfsUploadResponseDTO upload(FastDfsUploadConfig config, File file) {
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        return this.upload(Optional.ofNullable(config.getGroup()).orElse("group"), config.getTimestamp(), Objects.requireNonNull(config.getPath()), Optional.ofNullable(config.getFileName()).orElse(file.getName()), requestBody);
    }

    @Override
    public FastDfsUploadResponseDTO upload(File file) {
        return this.upload(FastDfsUploadConfig.defaultConfig(file.getName(), StringUtils.randomUUID()), file);
    }

    @Override
    public FastDfsInfoResponseDTO getInfo(String group, String md5) {
        String url = this.getInfoUrl(this.fastDfsRule.getNextFastDfsUrl(group)) + "?md5=" + StringUtils.encodeURL(md5);
        Request request = new Request.Builder().url(url).get().build();
        Response response = OkHttpClientUtils.execute(request);
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            try {
                FastDfsInfoResponseDTO infoResponse = JSON.parseObject(body.string(), FastDfsInfoResponseDTO.class);
                if (infoResponse.success()) {
                    infoResponse.setPublicUrl(this.getPublicUrl(group, infoResponse));
                }
                return infoResponse;
            }
            catch (IOException ex) {
                throw new NetworkException(ex);
            }
        }
        FastDfsInfoResponseDTO infoResponse = new FastDfsInfoResponseDTO();
        infoResponse.setStatus(HttpStatus.valueOf(response.code()).name());
        infoResponse.setMessage(response.message());
        return infoResponse;
    }

    @Override
    public FastDfsDeleteResponseDTO delete(String group, String md5) {
        String url = this.getDeleteUrl(this.fastDfsRule.getNextFastDfsUrl(group)) + "?md5=" + StringUtils.encodeURL(md5);
        Request request = new Request.Builder().url(url).delete().build();
        Response response = OkHttpClientUtils.execute(request);
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            try {
                return JSON.parseObject(body.string(), FastDfsDeleteResponseDTO.class);
            }
            catch (IOException ex) {
                throw new NetworkException(ex);
            }
        }
        FastDfsDeleteResponseDTO deleteResponse = new FastDfsDeleteResponseDTO();
        deleteResponse.setStatus(HttpStatus.valueOf(response.code()).name());
        deleteResponse.setMessage(response.message());
        return deleteResponse;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FastDfsUploadResponseDTO upload(@Nullable String group, FilePathDTO filePath, InputStream is) {
        File temp = null;
        try {
            temp = FileUtils.createTempFile();
            FileUtils.write(temp, is);
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, temp);
            FastDfsUploadResponseDTO fastDfsUploadResponseDTO = this.upload(Optional.ofNullable(group).orElse("group"), FileScope.finereport.equals((Object)filePath.getScope()) ? "" : filePath.getTimestamp(), Objects.requireNonNull(filePath.getIrregularName()), Objects.requireNonNull(filePath.getFileName()), requestBody);
            return fastDfsUploadResponseDTO;
        }
        finally {
            FileUtils.deleteQuietly(temp);
        }
    }

    @Override
    public FastDfsUploadResponseDTO upload(@Nullable String group, FilePathDTO filePath, MultipartFile multipartFile) {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MultipartBody.FORM, multipartFile.getBytes());
        }
        catch (IOException ioe) {
            throw new NetworkException(ioe);
        }
        return this.upload(Optional.ofNullable(group).orElse("group"), FileScope.finereport.equals((Object)filePath.getScope()) ? "" : filePath.getTimestamp(), Objects.requireNonNull(filePath.getIrregularName()), Optional.ofNullable(filePath.getFileName()).orElse(multipartFile.getOriginalFilename()), requestBody);
    }

    @Override
    public FastDfsUploadResponseDTO upload(@Nullable String group, FilePathDTO filePath, File file) {
        return this.upload(FastDfsUploadConfig.builder().group(group).timestamp(FileScope.finereport.equals((Object)filePath.getScope()) ? "" : filePath.getTimestamp()).path(filePath.getIrregularName()).fileName(filePath.getFileName()).build(), file);
    }

    @Override
    public void download(@Nullable String group, FilePathDTO filePath, Path toFile) {
        StringBuilder sb = new StringBuilder(StringUtils.removeEnd(this.fastDfsRule.getNextFastDfsUrl(group), "/")).append("/");
        if (!StringUtils.isBlank(filePath.getTimestamp())) {
            sb.append(filePath.getTimestamp()).append("/");
        }
        sb.append(filePath.getIrregularName()).append("/").append(StringUtils.encodeURL(filePath.getFileName()));
        HttpClientManager.getInstance().download(sb.toString(), toFile.toFile());
    }

    private FastDfsUploadResponseDTO upload(String group, String timestamp, String path, String fileName, RequestBody requestBody) {
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", fileName, requestBody).addFormDataPart("output", "json2").addFormDataPart("path", StringUtils.isBlank(timestamp) ? path : timestamp + "/" + path).build();
        Request request = new Request.Builder().url(this.getUploadUrl(this.fastDfsRule.getNextFastDfsUrl(group))).post(multipartBody).build();
        Response response = OkHttpClientUtils.execute(request);
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            try {
                FastDfsUploadResponseDTO uploadResponse = JSON.parseObject(body.string(), FastDfsUploadResponseDTO.class);
                if (uploadResponse.success()) {
                    uploadResponse.setPublicUrl(this.getPublicUrl(group, uploadResponse));
                }
                return uploadResponse;
            }
            catch (IOException ex) {
                throw new NetworkException(ex);
            }
        }
        FastDfsUploadResponseDTO uploadResponse = new FastDfsUploadResponseDTO();
        uploadResponse.setStatus(HttpStatus.valueOf(response.code()).name());
        uploadResponse.setMessage(response.message());
        return uploadResponse;
    }

    private String getUploadUrl(String fastDfsUrl) {
        return StringUtils.removeEnd(fastDfsUrl, "/") + "/upload";
    }

    private String getDeleteUrl(String fastDfsUrl) {
        return StringUtils.removeEnd(fastDfsUrl, "/") + "/delete";
    }

    private String getInfoUrl(String fastDfsUrl) {
        return StringUtils.removeEnd(fastDfsUrl, "/") + "/get_file_info";
    }

    private String getPublicUrl(String group, FastDfsInfoResponseDTO infoResponse) {
        group = Optional.ofNullable(group).orElse("group");
        return StringUtils.removeEnd(this.fastDfsRule.getNextFastDfsUrl(group), "/") + StringUtils.removeStart(infoResponse.getData().getPath(), "files") + "/" + StringUtils.encodeURL(infoResponse.getData().getName());
    }

    private String getPublicUrl(String group, FastDfsUploadResponseDTO uploadResponse) {
        group = Optional.ofNullable(group).orElse("group");
        StringBuilder sb = new StringBuilder(StringUtils.removeEnd(this.fastDfsRule.getNextFastDfsUrl(group), "/"));
        int index = uploadResponse.getData().getPath().indexOf("/", 1);
        return sb.append(uploadResponse.getData().getPath().substring(index)).toString();
    }
}
