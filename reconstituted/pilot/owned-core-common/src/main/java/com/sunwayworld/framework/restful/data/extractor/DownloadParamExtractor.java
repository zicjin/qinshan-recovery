package com.sunwayworld.framework.restful.data.extractor;

import com.sunwayworld.framework.beans.BeanPropertyType;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.TriConsumer;
import com.sunwayworld.framework.io.excel.CellStyleData;
import com.sunwayworld.framework.io.excel.support.PropertyContext;
import com.sunwayworld.framework.io.excel.support.SheetContext;
import com.sunwayworld.framework.io.excel.support.impl.DefaultPropertyContextImpl;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;

public class DownloadParamExtractor implements ParamExtractor<SheetContext> {
   private static final Logger logger = LogManager.getLogger(DownloadParamExtractor.class);
   private RestJsonWrapperBean jsonWrapper;
   private static ScriptEngineManager manager = new ScriptEngineManager();

   public DownloadParamExtractor(RestJsonWrapperBean jsonWrapper) {
      this.jsonWrapper = jsonWrapper;
   }

   @Override
   public RestJsonWrapperBean getJsonWrapper() {
      return this.jsonWrapper;
   }

   public SheetContext extract() {
      String topic = this.jsonWrapper.getParamValue("dt");
      List<DownloadParamExtractor.TitleDef> titleDefList = this.jsonWrapper.parse(DownloadParamExtractor.TitleDef.class);
      List<PropertyContext> propertyContextList = new ArrayList<>();
      titleDefList.forEach(
         t -> {
            String title = t.getName();
            String name = StringUtils.removeStart(t.getId(), "ext$.");
            MatchPattern matchPattern = MatchPattern.EQ;
            int pos = name.lastIndexOf("_");
            if (pos > 0) {
               MatchPattern pattern = MatchPattern.getPatternIfPossible(name.substring(pos + 1));
               if (pattern != null) {
                  matchPattern = pattern;
                  name = name.substring(0, pos);
               }
            }

            Function<Object, Object> function = null;
            if (!CollectionUtils.isEmpty(t.getOptions())) {
               if (MatchPattern.SB.equals(matchPattern)) {
                  function = v -> ConvertUtils.convert(
                     t.getOptions()
                        .entrySet()
                        .stream()
                        .filter(e -> !StringUtils.isBlank(e.getKey()))
                        .filter(e -> StringUtils.startsWith(ConvertUtils.convert(v, String.class), e.getKey()))
                        .map(e -> e.getValue())
                        .findFirst()
                        .orElse(null),
                     String.class
                  );
               } else if (MatchPattern.SC.equals(matchPattern)) {
                  function = v -> ConvertUtils.convert(
                     t.getOptions()
                        .entrySet()
                        .stream()
                        .filter(e -> !StringUtils.isBlank(e.getKey()))
                        .filter(e -> StringUtils.contains(e.getKey(), ConvertUtils.convert(v, String.class)))
                        .map(e -> e.getValue())
                        .findFirst()
                        .orElse(null),
                     String.class
                  );
               } else if (MatchPattern.SE.equals(matchPattern)) {
                  function = v -> ConvertUtils.convert(
                     t.getOptions()
                        .entrySet()
                        .stream()
                        .filter(e -> !StringUtils.isBlank(e.getKey()))
                        .filter(e -> StringUtils.endsWith(e.getKey(), ConvertUtils.convert(v, String.class)))
                        .map(e -> e.getValue())
                        .findFirst()
                        .orElse(null),
                     String.class
                  );
               } else {
                  function = v -> {
                     String value = ConvertUtils.convert(v, String.class);
                     return !StringUtils.isBlank(value) && value.contains(",")
                        ? StringUtils.join(Arrays.asList(value.split(",")).stream().map(e -> this.getOptionValue(t, e)).collect(Collectors.toList()), " ")
                        : this.getOptionValue(t, v);
                  };
               }
            }

            HorizontalAlignment alignment = this.getAlignment(t.getAlignment());
            PropertyContext propertyContext = DefaultPropertyContextImpl.of(title, name, function, alignment);
            if ("number".equals(t.getType())) {
               propertyContext.setPropertyType(BeanPropertyType.DOUBLE);
            } else if ("date".equals(t.getType())) {
               propertyContext.setPropertyType(BeanPropertyType.DATE);
            } else if ("dateTime".equals(t.getType())) {
               propertyContext.setPropertyType(BeanPropertyType.DATETIME);
            }

            if (!StringUtils.isBlank(t.getStyleFormatter())) {
               ScriptEngine engine = manager.getEngineByName("JavaScript");
               String field = name;
               TriConsumer<Sheet, CellStyleData, Object> styleConsumer = (sheet, csd, item) -> {
                  String styleFormatter = StringUtils.replace(t.getStyleFormatter().replaceAll("(\\\\r|\\\\n)+", "").trim(), "function", "function get");

                  try {
                     engine.eval("var field = '" + field + "';");
                     engine.eval(styleFormatter);
                     Invocable invocable = (Invocable)engine;
                     Object result = invocable.invokeFunction("get", item);
                     if (result instanceof ScriptObjectMirror) {
                        for (Entry<String, Object> entry : ((ScriptObjectMirror)result).entrySet()) {
                           String key = entry.getKey();
                           Object value = entry.getValue();
                           if ("backgroundColor".equals(key)) {
                              IndexedColors indexedColors = this.getIndexedColorsIfPresent(value.toString());
                              if (indexedColors != null) {
                                 csd.setFillForegroundColor(indexedColors);
                                 csd.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                              }
                           } else if ("color".equals(key)) {
                              IndexedColors indexedColors = this.getIndexedColorsIfPresent(value.toString());
                              if (indexedColors != null) {
                                 csd.setFontColor(indexedColors);
                              }
                           }
                        }
                     }
                  } catch (Exception var16) {
                     logger.warn(var16.getMessage());
                  }
               };
               propertyContext.setStyleConsumer(styleConsumer);
            }

            propertyContextList.add(propertyContext);
         }
      );
      List<Pair<String, Pair<Integer, Integer>>> subTopicList = new ArrayList<>();

      for (int i = 0; i < titleDefList.size(); i++) {
         DownloadParamExtractor.TitleDef def = titleDefList.get(i);
         if (!StringUtils.isEmpty(def.getParentTitle()) && !subTopicList.stream().anyMatch(t -> t.getFirst().equals(def.getParentTitle()))) {
            for (int j = i; j < titleDefList.size(); j++) {
               DownloadParamExtractor.TitleDef subDef = titleDefList.get(j);
               if (j == titleDefList.size() - 1) {
                  if (def.getParentTitle().equals(subDef.getParentTitle())) {
                     subTopicList.add(Pair.of(def.getParentTitle(), Pair.of(i + 1, j + 1)));
                     break;
                  }
               } else if (!def.getParentTitle().equals(subDef.getParentTitle())) {
                  subTopicList.add(Pair.of(def.getParentTitle(), Pair.of(i + 1, j)));
                  break;
               }
            }
         }
      }

      return subTopicList.isEmpty() ? new SheetContext(topic, topic, propertyContextList) : new SheetContext(topic, topic, propertyContextList, subTopicList);
   }

   private HorizontalAlignment getAlignment(String alignment) {
      switch (alignment) {
         case "left":
            return HorizontalAlignment.LEFT;
         case "right":
            return HorizontalAlignment.RIGHT;
         case "center":
            return HorizontalAlignment.CENTER;
         default:
            return HorizontalAlignment.GENERAL;
      }
   }

   private IndexedColors getIndexedColorsIfPresent(String color) {
      for (IndexedColors indexedColors : IndexedColors.values()) {
         if (indexedColors.name().equalsIgnoreCase(color)) {
            return indexedColors;
         }
      }

      return null;
   }

   private String getOptionValue(DownloadParamExtractor.TitleDef t, Object value) {
      String optionKey = ConvertUtils.convert(value, String.class);
      if (StringUtils.isBlank(optionKey)) {
         return "";
      } else {
         String optionValue = t.getOptions().get(optionKey);
         return optionValue == null ? optionKey : optionValue;
      }
   }

   public static class TitleDef {
      private String id;
      private String name;
      private String alignment;
      private String type;
      private String parentTitle;
      private String styleFormatter;
      private Map<String, String> options;

      public String getId() {
         return this.id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getAlignment() {
         return this.alignment;
      }

      public void setAlignment(String alignment) {
         this.alignment = alignment;
      }

      public String getParentTitle() {
         return this.parentTitle;
      }

      public void setParentTitle(String parentTitle) {
         this.parentTitle = parentTitle;
      }

      public Map<String, String> getOptions() {
         return this.options;
      }

      public void setOptions(Map<String, String> options) {
         this.options = options;
      }

      public String getStyleFormatter() {
         return this.styleFormatter;
      }

      public void setStyleFormatter(String styleFormatter) {
         this.styleFormatter = styleFormatter;
      }

      public String getType() {
         return this.type;
      }

      public void setType(String type) {
         this.type = type;
      }
   }
}
