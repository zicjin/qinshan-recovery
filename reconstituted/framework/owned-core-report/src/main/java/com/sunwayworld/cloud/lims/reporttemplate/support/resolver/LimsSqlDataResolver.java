package com.sunwayworld.cloud.lims.reporttemplate.support.resolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceTreeDTO;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.cloud.lims.reporttemplate.support.constant.LimsSourceDataTypeEnum;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsCoreFileData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.helper.LimsSqlHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class LimsSqlDataResolver {
   private static final Logger LOGGER = LoggerFactory.getLogger(LimsSqlDataResolver.class);
   private LimsReportTemplateSourceService reportTemplateSourceService;
   private MapperParameter rootParameter;
   private List<LimsReportTemplateSourceBean> sourceList;
   private ThreadPoolTaskExecutor poolTaskExecutor;
   private Environment env;
   private static final String INDEX_FIELD = "IDX";
   private static final String GROUP_INDEX_FIELD = "GIDX";

   private LimsSqlDataResolver(MapperParameter rootParameter, List<LimsReportTemplateSourceBean> sourceList) {
      this.rootParameter = rootParameter;
      this.sourceList = sourceList;
   }

   public static LimsSqlDataResolver getResolver(MapperParameter parameter, List<LimsReportTemplateSourceBean> sourceList) {
      LOGGER.warn("**WILL RESOLVE THE SQL SOURCE [{}]**", sourceList.size());
      LimsSqlDataResolver resolver = new LimsSqlDataResolver(parameter, sourceList);
      resolver.reportTemplateSourceService = ApplicationContextHelper.getBean(LimsReportTemplateSourceService.class);
      resolver.poolTaskExecutor = (ThreadPoolTaskExecutor)ApplicationContextHelper.getApplicationContext().getBean("limsSqlDataResolverExecutor");
      resolver.env = ApplicationContextHelper.getBean(Environment.class);
      return resolver;
   }

   public LimsTemplateDataMap<Object> resolve() {
      LOGGER.warn("**BEGIN TO EXECUTE DATA**");
      long startExecuteTime = System.currentTimeMillis();
      LimsTemplateDataMap<Object> paramMap = new LimsTemplateDataMap<>();
      if (!CollectionUtils.isEmpty(this.sourceList)) {
         TreeDescriptor<LimsReportTemplateSourceBean> treeDescriptor = new TreeDescriptor<>("sourceCode", "parentSourceCode", "sourceName");
         List<LimsReportTemplateSourceTreeDTO> sourceTreeDTOList = TreeHelper.parseTreeNode(
            this.sourceList, treeDescriptor, LimsReportTemplateSourceTreeDTO.class
         );
         CountDownLatch latch = new CountDownLatch(sourceTreeDTOList.size());
         sourceTreeDTOList.forEach(sourceTreeDTO -> this.poolTaskExecutor.submit(() -> {
            try {
               List<LimsTemplateDataMap<Object>> dataParam = this.execute(sourceTreeDTO, this.rootParameter);
               putParam(paramMap, sourceTreeDTO, dataParam);
            } catch (Exception var8x) {
               LOGGER.error("********** EXECUTE SOURCE [{}] ERROR ", sourceTreeDTO.getText(), var8x);
            } finally {
               latch.countDown();
            }
         }));

         try {
            String resolveSQLTimeOutSecond = this.env.getProperty("limsSqlDataResolverExecutor.resolveSQLTimeOutSecond", "600");
            latch.await(Long.parseLong(resolveSQLTimeOutSecond), TimeUnit.SECONDS);
         } catch (InterruptedException var8) {
            LOGGER.error("********** TEMPLATE EXECUTE SOURCE TIME OUT ", (Throwable)var8);
         }
      }

      LOGGER.warn("**FIND DATA SIZE  [{}]**", paramMap.size());
      LOGGER.warn("**EXECUTE DATA END USED [{}s]**", ArithUtils.div(System.currentTimeMillis() - startExecuteTime, 1000.0));
      return paramMap;
   }

   private List<LimsTemplateDataMap<Object>> execute(LimsReportTemplateSourceTreeDTO sourceTreeDTO, MapperParameter param) {
      LOGGER.warn("**GET DATA BY SOURCE  [{}]**", sourceTreeDTO.getText());
      long startTime = System.currentTimeMillis();
      String sql = sourceTreeDTO.getSql();
      LimsTemplateDataList<LimsTemplateDataMap<Object>> dataMapList = new LimsTemplateDataList<>();
      if (!StringUtils.isEmpty(sql)) {
         RestJsonWrapperBean wrapperBean = new RestJsonWrapperBean();
         sql = LimsSqlHelper.replaceParamString(sql, param, this.rootParameter);
         if (!StringUtils.isEmpty(sourceTreeDTO.getParam())) {
            String paramString = LimsSqlHelper.replaceParamString(sourceTreeDTO.getParam(), param, this.rootParameter);
            JSON.parseObject(paramString).forEach(wrapperBean::setFilterValue);
         }

         long sqlStartTime = System.currentTimeMillis();
         List<Map<String, Object>> mapList = this.reportTemplateSourceService.selectListBySql(sql, wrapperBean.extractMapFilter());
         LOGGER.warn("**[{}] EXECUTE SQL END - USED [{}s]**", sourceTreeDTO.getText(), ArithUtils.div(System.currentTimeMillis() - sqlStartTime, 1000.0));
         if (!CollectionUtils.isEmpty(mapList)) {
            Map<LimsReportTemplateSourceTreeDTO, List<LimsTemplateDataMap<Object>>> childrenDataMap = new HashMap<>();
            List<AbstractTreeNode> children = sourceTreeDTO.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
               children.forEach(
                  childNode -> {
                     LOGGER.warn("**DEAL THE CHILD SOURCE [{}] FOR [{}]**", childNode.getText(), sourceTreeDTO.getText());
                     LimsReportTemplateSourceTreeDTO childSourceDTO = (LimsReportTemplateSourceTreeDTO)childNode;
                     MapperParameter childParameter = this.getParamFromParent(
                        LimsSourceDataTypeEnum.getTypeEnum(sourceTreeDTO.getSourceType()), mapList, childSourceDTO.getParam()
                     );
                     List<LimsTemplateDataMap<Object>> childDataList = this.execute(childSourceDTO, childParameter);
                     LOGGER.warn("**CHILD SOURCE [{}] FIND DATA SIZE [{}]**", childNode.getText(), childDataList.size());
                     if (!CollectionUtils.isEmpty(childDataList)) {
                        childrenDataMap.put(childSourceDTO, childDataList);
                     }
                  }
               );
            }

            AtomicInteger index = new AtomicInteger(1);
            dataMapList = mapList.stream().map(map -> {
               LimsTemplateDataMap<Object> limsDataMap = (LimsTemplateDataMap<Object>)map;
               this.putRelativeChildDataMap(limsDataMap, childrenDataMap);
               limsDataMap.put("IDX", index.getAndIncrement());
               return limsDataMap;
            }).collect(Collectors.toCollection(LimsTemplateDataList::new));
            if (!StringUtils.isEmpty(sourceTreeDTO.getResolveFunction())) {
               dataMapList = ApplicationContextHelper.<LimsCustomerDataResolveFunction>getBean(sourceTreeDTO.getResolveFunction()).apply(dataMapList);
            }
         }
      }

      LOGGER.warn(
         "**GET DATA BY THE SOURCE [{}] FINISHED - USED [{}s] - find data [{}]**",
         sourceTreeDTO.getText(),
         ArithUtils.div(System.currentTimeMillis() - startTime, 1000.0),
         dataMapList.size()
      );
      return dataMapList;
   }

   private static void putParam(LimsTemplateDataMap<Object> dataMap, LimsReportTemplateSourceTreeDTO source, List<LimsTemplateDataMap<Object>> dataMapList) {
      if (!CollectionUtils.isEmpty(dataMapList)) {
         switch (LimsSourceDataTypeEnum.getTypeEnum(source.getSourceType())) {
            case DETAIL:
               dataMap.put(source.getSourceCode(), dataMapList.get(0));
               break;
            case LIST:
               dataMap.put(source.getSourceCode(), dataMapList);
               break;
            case FILE:
            case SUB_TEMPLATE:
               dataMap.put(source.getSourceCode(), PersistableHelper.mapToPersistable((Map<String, Object>)dataMapList.get(0), LimsCoreFileData.class));
               break;
            case FILE_LIST:
            case SUB_TEMPLATE_LIST:
               LimsTemplateDataList<CoreFileBean> fileList = dataMapList.stream()
                  .map(map -> PersistableHelper.mapToPersistable(map, LimsCoreFileData.class))
                  .collect(Collectors.toCollection(LimsTemplateDataList::new));
               dataMap.put(source.getSourceCode(), fileList);
               break;
            default:
               dataMap.put(source.getSourceCode(), dataMapList);
         }
      }
   }

   private MapperParameter getParamFromParent(LimsSourceDataTypeEnum sourceType, List<Map<String, Object>> parentDataList, String param) {
      List<String> paramTagList = LimsSqlHelper.getParamTagList(param);
      MapperParameter parameter = new MapperParameter();
      parameter.putAll(this.rootParameter);
      paramTagList.forEach(
         paramTag -> {
            List<Object> paramDataList = parentDataList.stream()
               .filter(parentData -> parentData.get(paramTag) != null)
               .map(parentData -> parentData.get(paramTag))
               .distinct()
               .collect(Collectors.toList());
            parameter.put(paramTag, StringUtils.join(paramDataList, ","));
         }
      );
      return parameter;
   }

   private void putRelativeChildDataMap(
      LimsTemplateDataMap<Object> parentDataMap, Map<LimsReportTemplateSourceTreeDTO, List<LimsTemplateDataMap<Object>>> childrenDataMap
   ) {
      childrenDataMap.forEach((childNode, dataList) -> {
         if (!CollectionUtils.isEmpty(dataList)) {
            String childParamString = childNode.getParam();
            List<String> paramTagList = LimsSqlHelper.getParamTagList(childParamString);
            if (!StringUtils.isEmpty(childParamString) && !CollectionUtils.isEmpty(paramTagList)) {
               JSONObject paramObject = JSON.parseObject(childParamString);
               List<BiFunction<LimsTemplateDataMap<Object>, LimsTemplateDataMap<Object>, Boolean>> checkList = paramObject.entrySet()
                  .stream()
                  .filter(entry -> {
                     String valueTag = ObjectUtils.toString(entry.getValue());
                     String parentKey = StringUtils.replace(valueTag, "${", "").replace("}", "");
                     return parentDataMap.containsKey(parentKey);
                  })
                  .map(entry -> {
                     String valueTag = ObjectUtils.toString(entry.getValue());
                     String parentKey = StringUtils.replace(valueTag, "${", "").replace("}", "");
                     return (BiFunction<LimsTemplateDataMap<Object>, LimsTemplateDataMap<Object>, Boolean>)(parent, childItem) -> ObjectUtils.equals(childItem.get(StringUtils.split(entry.getKey(), "_")[0]), parent.get(parentKey));
                  })
                  .collect(Collectors.toList());
               AtomicInteger groupIndex = new AtomicInteger(1);
               List<LimsTemplateDataMap<Object>> filterList = dataList.stream().filter(childData -> {
                  boolean check = checkList.stream().allMatch(func -> func.apply(parentDataMap, childData));
                  if (check) {
                     childData.put("GIDX", groupIndex.getAndIncrement());
                  }

                  return check;
               }).collect(Collectors.toCollection(LimsTemplateDataList::new));
               if (!CollectionUtils.isEmpty(filterList)) {
                  putParam(parentDataMap, childNode, filterList);
               }
            } else {
               parentDataMap.put(childNode.getSourceCode(), dataList);
            }
         }
      });
   }
}
