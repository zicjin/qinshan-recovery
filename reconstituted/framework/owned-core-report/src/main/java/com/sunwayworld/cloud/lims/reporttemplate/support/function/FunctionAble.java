package com.sunwayworld.cloud.lims.reporttemplate.support.function;

import com.deepoove.poi.data.HyperlinkTextRenderData;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsCoreFileData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsCustomizeRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTextRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FunctionAble extends ListFunction, FileFunction, TableFunction {
   Logger logger = LoggerFactory.getLogger("FunctionAble");

   default LimsTextRenderData checkBox(String text, boolean checked) {
      return new LimsTextRenderData(" " + text, true, checked);
   }

   default LimsTemplateDataList<LimsTextRenderData> checkBox(
      LimsTemplateDataList<LimsTemplateDataMap<Object>> checkList, String textField, String checkField, Object checkValue, String... separator
   ) {
      if (CollectionUtils.isEmpty(checkList)) {
         return new LimsTemplateDataList<>();
      } else {
         AtomicBoolean isFirst = new AtomicBoolean(true);
         String sep = separator != null ? StringUtils.replace(StringUtils.replace(separator[0], "\\s", " "), "\\t", "\t") : "  ";
         return checkList.stream().map(item -> {
            LimsTextRenderData limsTextRenderData = this.checkBox(item.get(textField), ObjectUtils.equals(item.get(checkField), checkValue));
            if (!isFirst.getAndSet(false)) {
               limsTextRenderData.setSeparator(sep);
            }

            return limsTextRenderData;
         }).collect(Collectors.toCollection(LimsTemplateDataList::new));
      }
   }

   default <T> LimsCustomizeRenderData<T> render(T data, String customerRender) {
      LimsCustomizeRenderData<T> customRenderData = new LimsCustomizeRenderData<>();
      customRenderData.setData(data);
      customRenderData.setCustomerRender(customerRender);
      return customRenderData;
   }

   default <T> LimsCustomizeRenderData<T> render(T data, double accuracy, String customerRender) {
      LimsCustomizeRenderData<T> customRenderData = new LimsCustomizeRenderData<>();
      customRenderData.setData(data);
      customRenderData.setCustomerRender(customerRender);
      if (data instanceof List && ((List)data).size() > 0 && ((List)data).get(0) instanceof CoreFileBean) {
         for (CoreFileBean fileBean : (List<CoreFileBean>)data) {
            fileBean.setExt$Item("accuracy", accuracy + "");
         }
      }

      return customRenderData;
   }

   default <T> LimsCustomizeRenderData<T> render(T data, double accuracy, String notcontraction, String customerRender) {
      LimsCustomizeRenderData<T> customRenderData = new LimsCustomizeRenderData<>();
      customRenderData.setData(data);
      customRenderData.setCustomerRender(customerRender);
      if (data instanceof List && ((List)data).size() > 0 && ((List)data).get(0) instanceof CoreFileBean) {
         for (CoreFileBean fileBean : (List<CoreFileBean>)data) {
            fileBean.setExt$Item("accuracy", accuracy + "");
            fileBean.setExt$Item("notcontraction", notcontraction + "");
         }
      }

      return customRenderData;
   }

   default <T> LimsCustomizeRenderData<T> render(T data, double accuracy, String notcontraction, String scaleType, String customerRender) {
      LimsCustomizeRenderData<T> customRenderData = new LimsCustomizeRenderData<>();
      customRenderData.setData(data);
      customRenderData.setCustomerRender(customerRender);
      if (StringUtils.isBlank(scaleType)) {
         scaleType = "1";
      }

      if (data instanceof List && ((List)data).size() > 0 && ((List)data).get(0) instanceof CoreFileBean) {
         for (CoreFileBean fileBean : (List<CoreFileBean>)data) {
            fileBean.setExt$Item("accuracy", accuracy + "");
            fileBean.setExt$Item("notcontraction", notcontraction + "");
            fileBean.setExt$Item("scaleType", scaleType);
         }
      }

      return customRenderData;
   }

   default HyperlinkTextRenderData link(String text, CoreFileBean coreFile) {
      if (coreFile == null) {
         return null;
      } else {
         String url = LimsFileUtils.getFileDownLoadUrl(coreFile);
         return new HyperlinkTextRenderData(Optional.ofNullable(text).orElse(coreFile.getName()), url);
      }
   }

   default HyperlinkTextRenderData link(String text, String url) {
      return new HyperlinkTextRenderData(Optional.ofNullable(text).orElse("超链接"), Optional.ofNullable(url).orElse(""));
   }

   default String pinyin(String text, int type, boolean userNameFlag) {
      try {
         if (StringUtils.isEmpty(text)) {
            return text;
         } else {
            PinyinFormat format = type == 0 ? PinyinFormat.WITHOUT_TONE : (type == 1 ? PinyinFormat.WITH_TONE_MARK : PinyinFormat.WITH_TONE_NUMBER);
            String pinyinString = PinyinHelper.convertToPinyinString(text, " ", format);
            if (userNameFlag) {
               String[] nameSplit = StringUtils.split(pinyinString, " ");
               StringBuilder sb = new StringBuilder();

               for (int i = 0; i < nameSplit.length; i++) {
                  String name = nameSplit[i];
                  if (i < 2) {
                     sb.append(StringUtils.upperCase(name.substring(0, 1))).append(name.substring(1));
                     if (i == 0) {
                        sb.append(" ");
                     }
                  } else {
                     sb.append(name);
                  }
               }

               return sb.toString();
            } else {
               return pinyinString;
            }
         }
      } catch (PinyinException var10) {
         logger.error("TEXT {} TO PINYIN ERROR: ", text, var10);
         return text;
      }
   }

   default LocalDateTime now() {
      return LocalDateTime.now();
   }

   default String get(String text, String... args) {
      if (!StringUtils.isEmpty(StringUtils.trim(text))) {
         return text;
      } else {
         return args.length > 0 ? args[0] : "/";
      }
   }

   default String split(String text, String tag, int getIndex) {
      if (!StringUtils.isEmpty(StringUtils.trim(text)) && StringUtils.containsIgnoreCase(text, tag)) {
         String[] split = StringUtils.split(text, tag);
         if (split.length <= Math.abs(getIndex)) {
            return split[split.length - 1];
         } else {
            int index = getIndex < 0 ? split.length + getIndex : getIndex;
            return split[index];
         }
      } else {
         return text;
      }
   }

   default String sub(String text, int subPos, String etc) {
      if (!StringUtils.isEmpty(text) && text.length() > subPos) {
         etc = etc == null ? "" : etc;
         return text.substring(0, subPos) + etc;
      } else {
         return text;
      }
   }

   default String splus(String... arr) {
      List<String> list = Arrays.asList(arr);
      list.forEach(e -> {
         if (e == null) {
            e = "";
         }
      });
      return StringUtils.join(list, "");
   }

   default CoreFileBean fileFromObj(LimsTemplateDataMap<Object> obj) {
      return obj != null ? PersistableHelper.mapToPersistable(obj, LimsCoreFileData.class) : null;
   }

   default String fd(LocalDateTime date) {
      return this.fd(date, null);
   }

   default String fd(LocalDateTime date, String formatter) {
      if (ObjectUtils.isEmpty(date)) {
         return "";
      } else {
         String fd = !StringUtils.isEmpty(formatter) ? formatter : "yyyy-MM-dd";
         return DateTimeUtils.formatLocalDateTime(date, fd);
      }
   }

   default String fd(LocalDateTime date, String formatter, String lang) {
      if (ObjectUtils.isEmpty(date)) {
         return "";
      } else {
         DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
         if (!StringUtils.isEmpty(lang)) {
            dateTimeFormatter = dateTimeFormatter.withLocale(Locale.forLanguageTag(lang));
         }

         return date.format(dateTimeFormatter);
      }
   }

   default String fd(LocalDate date) {
      return this.fd(date, null);
   }

   default String fd(LocalDate date, String formatter) {
      if (ObjectUtils.isEmpty(date)) {
         return "";
      } else {
         String fd = !StringUtils.isEmpty(formatter) ? formatter : "yyyy-MM-dd";
         return DateTimeUtils.formatLocalDate(date, fd);
      }
   }
}
