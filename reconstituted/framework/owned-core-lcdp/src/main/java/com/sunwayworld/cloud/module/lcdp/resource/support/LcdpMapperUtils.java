package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

public class LcdpMapperUtils {
   private static final Logger logger = LoggerFactory.getLogger(LcdpMapperUtils.class);
   public static final String NAMESPACE_DEV_SUFFIX = "$DEV";
   public static String LCDP_SYNC_MAPPER_TOPIC = "_event@" + ApplicationContextHelper.getEnvironment().getProperty("spring.redis.database") + "_:lcdp-mapper";
   public static String LCDP_SYNC_SCRIPT_TOPIC = "_event@" + ApplicationContextHelper.getEnvironment().getProperty("spring.redis.database") + "_:lcdp-script";
   private static final String RESOURCE_PREFIX = "LCDP_MAPPER_HOT_RELOAD:";
   public static final String DIALECT_MAPPER_SUFFIX = DialectRepository.getDialect().getDatabase().substring(0, 1).toUpperCase()
      + DialectRepository.getDialect().getDatabase().substring(1)
      + "Mapper";

   public static void syncMapper(String lcdpMapperPath, boolean isPro, String content) {
      if (StringUtils.endsWith(lcdpMapperPath, DIALECT_MAPPER_SUFFIX)) {
         LcdpMapperUtils.MapperInfo info = new LcdpMapperUtils.MapperInfo();
         info.setContent(content);
         info.setPath(lcdpMapperPath);
         info.setProFlag(isPro);
         TransactionUtils.runAfterCommit(
            () -> ApplicationContextHelper.getBean(StringRedisTemplate.class).convertAndSend(LCDP_SYNC_MAPPER_TOPIC, JSON.toJSONString(info))
         );
      }
   }

   public static void loadMapper(String lcdpMapperPath, boolean isPro, String mapperXmlContent) {
      Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
      String resource = getResource(lcdpMapperPath, isPro);
      String namespace = getNamespace(lcdpMapperPath, isPro);
      String content = changeMapperNamespace(mapperXmlContent, namespace);
      Set<String> loadedResourcesSet = (Set<String>)ReflectionUtils.getFieldValue(configuration, "loadedResources");
      boolean contains = loadedResourcesSet.contains(resource);

      try {
         loadedResourcesSet.remove(resource);
         List<String> mapperIdList = parseMapperIdListByXml(content, configuration, namespace);
         if (StringUtils.endsWith(lcdpMapperPath, DIALECT_MAPPER_SUFFIX)) {
            synchronized (configuration) {
               XMLMapperBuilder builder = new XMLMapperBuilder(
                  new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), configuration, resource, configuration.getSqlFragments()
               );
               builder.parse();
               clearStrictMap(namespace, mapperId -> !mapperIdList.contains(mapperId));
            }
         }
      } catch (Exception var14) {
         logger.error("============>mapper加载异常：path为" + lcdpMapperPath + "，异常日志如下：" + var14.getMessage());
         if (contains) {
            loadedResourcesSet.add(resource);
         }

         throw new CheckedException(var14);
      }
   }

   public static void unloadMapper(String lcdpMapperPath, boolean isPro) {
      if (StringUtils.endsWith(lcdpMapperPath, DIALECT_MAPPER_SUFFIX)) {
         Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
         String resource = getResource(lcdpMapperPath, isPro);
         String namespace = getNamespace(lcdpMapperPath, isPro);
         Set<String> loadedResourcesSet = (Set<String>)ReflectionUtils.getFieldValue(configuration, "loadedResources");
         loadedResourcesSet.remove(resource);
         clearStrictMap(namespace, mapperId -> true);
      }
   }

   public static void validateMapper(String lcdpMapperPath, boolean isPro, String mapperXmlContent) {
      Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();
      String namespace = getNamespace(lcdpMapperPath, isPro);
      String content = changeMapperNamespace(mapperXmlContent, namespace);

      try {
         XPathParser xPathParser = new XPathParser(
            new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), true, configuration.getVariables(), new XMLMapperEntityResolver()
         );
         XNode mapperNode = xPathParser.evalNode("/mapper");
         String mapperNamespace = mapperNode.getStringAttribute("namespace");
         if (!StringUtils.equals(mapperNamespace, namespace)) {
            throw new ApplicationWarningException("mapper namespace error");
         }
      } catch (Exception var9) {
         throw new ApplicationWarningException(var9);
      }
   }

   private static void clearStrictMap(String namespace, Predicate<String> mapperIdFilter) {
      Configuration configuration = MybatisHelper.getSqlSessionFactory().getConfiguration();

      for (String field : new String[]{"mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments"}) {
         Object fieldValue = ReflectionUtils.getFieldValue(configuration, field);
         if (fieldValue instanceof Map) {
            Map<String, ?> propertyMap = (Map<String, ?>)fieldValue;
            List<String> list = propertyMap.keySet()
               .stream()
               .filter(o -> o.startsWith(namespace + ".") && !o.replace(namespace + ".", "").contains(".") && mapperIdFilter.test(o))
               .collect(Collectors.toList());
            list.forEach(propertyMap::remove);
         }
      }
   }

   private static String changeMapperNamespace(String mapperXmlContent, String namespace) {
      Pattern pattern = Pattern.compile("namespace\\s*=\\s*\"(\\S+)\"");
      Matcher matcher = pattern.matcher(mapperXmlContent);
      if (matcher.find()) {
         String originNamespace = matcher.group(1);
         return mapperXmlContent.replace(originNamespace, namespace);
      } else {
         throw new ApplicationWarningException("namespace parse error");
      }
   }

   private static String getResource(String lcdpMapperPath, boolean isPro) {
      return "LCDP_MAPPER_HOT_RELOAD:" + lcdpMapperPath + (isPro ? "" : "$DEV");
   }

   private static String getNamespace(String lcdpMapperPath, boolean isPro) {
      return lcdpMapperPath.replace(DIALECT_MAPPER_SUFFIX, "Mapper") + (isPro ? "" : "$DEV");
   }

   private static List<String> parseMapperIdListByXml(String mapperXmlContent, Configuration configuration, String namespace) {
      XPathParser xPathParser = new XPathParser(
         new ByteArrayInputStream(mapperXmlContent.getBytes(StandardCharsets.UTF_8)), true, configuration.getVariables(), new XMLMapperEntityResolver()
      );
      XNode mapperNode = xPathParser.evalNode("/mapper");
      String mapperNamespace = mapperNode.getStringAttribute("namespace");
      if (!StringUtils.equals(mapperNamespace, namespace)) {
         throw new CheckedException("mapper namespace error");
      } else {
         List<XNode> statementNodeList = mapperNode.evalNodes("/mapper/select|update|insert|sql|parameterMap|resultMap");
         List<String> sqlList = statementNodeList.stream()
            .filter(node -> StringUtils.equalsIgnoreCase(node.getName(), "sql"))
            .map(node -> node.getStringAttribute("id"))
            .collect(Collectors.toList());
         statementNodeList.stream().forEach(node -> validateInclude(node, configuration, sqlList));
         return statementNodeList.stream().map(node -> namespace + "." + node.getStringAttribute("id")).collect(Collectors.toList());
      }
   }

   private static void validateInclude(XNode node, Configuration configuration, List<String> sqlList) {
      if (!ObjectUtils.isEmpty(node)) {
         for (XNode childen : node.getChildren()) {
            if (StringUtils.equals("include", childen.getName())) {
               Map<String, XNode> sqlFragments = configuration.getSqlFragments();
               String refId = childen.getStringAttribute("refid");
               if (!sqlFragments.containsKey(refId) && !sqlList.contains(refId)) {
                  throw new CheckedException(childen.getStringAttribute("refid") + " sql fragment is not exist");
               }
            } else {
               validateInclude(childen, configuration, sqlList);
            }
         }
      }
   }

   public static class MapperInfo implements Serializable {
      private static final long serialVersionUID = -6921124417297589602L;
      private String path;
      private String content;
      private boolean proFlag;

      public String getPath() {
         return this.path;
      }

      public void setPath(String path) {
         this.path = path;
      }

      public String getContent() {
         return this.content;
      }

      public void setContent(String content) {
         this.content = content;
      }

      public boolean getProFlag() {
         return this.proFlag;
      }

      public void setProFlag(boolean proFlag) {
         this.proFlag = proFlag;
      }
   }
}
