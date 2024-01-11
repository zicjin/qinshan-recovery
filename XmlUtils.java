package com.sunwayworld.emc.module.reptile.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author magh@sunwayworld.com
 * @date 2024-01-06
 */
public class XmlUtils {

    public static void main(String[] args) {

        String dirString = "D:\\IdeaWorkSpace\\gitWorkSpace\\wpjc-copy2\\src\\main\\java";
        Path dirPath = Paths.get(dirString);

        deleteFile(dirPath);
        changeFile(dirPath);


    }

    private static void deleteFile(Path dirPath) {
        List<File> fileList = FileUtils.getFileList(dirPath);

        for (File file : fileList) {
            String name = file.getName();
            if(StringUtils.endsWith(name,"oracle-mapper.xml")){
//                System.out.println(name);
                FileUtils.deleteQuietly(file);
            }
        }
    }

    private static void changeFile(Path dirPath) {
        List<File> fileList = FileUtils.getFileList(dirPath);

        for (File file : fileList) {
            String name = file.getName();
            if(StringUtils.endsWith(name,"mysql-mapper.xml")){
                File parentFile = file.getParentFile();
                String newFileName = StringUtils.replace(name, "mysql-mapper.xml", "oracle-mapper.xml");

                String newFilePathStr = parentFile.getAbsolutePath() + File.separator + newFileName;
                File newFile = new File(newFilePathStr);
//                System.out.println(newFilePathStr);
                file.renameTo(newFile);
            }
        }
    }
}
