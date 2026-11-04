package com.sunwayworld.framework.restful.data;

import com.sunwayworld.framework.utils.ServletUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class RestFieldValueTextContainer {
   public static final String HTTP_SERVLET_REQUEST_ATTRIBUTE_KEY = "GIKAM_RFVTC";
   public List<RestFieldValueTextContainer.FieldData> fieldDataList = new ArrayList<>();

   private RestFieldValueTextContainer() {
   }

   public static RestFieldValueTextContainer getInstance() {
      return new RestFieldValueTextContainer();
   }

   public static String getText(String field, String value) {
      HttpServletRequest request = ServletUtils.getCurrentRequest();
      if (request == null) {
         return value;
      } else {
         RestFieldValueTextContainer container = (RestFieldValueTextContainer)request.getAttribute("GIKAM_RFVTC");
         if (container == null) {
            return value;
         } else {
            RestFieldValueTextContainer.FieldData fieldData = container.fieldDataList
               .stream()
               .filter(f -> f.getField().equalsIgnoreCase(field))
               .findFirst()
               .orElse(null);
            return fieldData == null ? value : fieldData.getText(value);
         }
      }
   }

   public void addFieldData(String field, String type) {
      this.fieldDataList.add(new RestFieldValueTextContainer.FieldData(field, type));
   }

   public void addFieldData(String field, String type, Map<String, String> valueTextMap) {
      this.fieldDataList.add(new RestFieldValueTextContainer.FieldData(field, type, valueTextMap));
   }

   private static class FieldData {
      private final String field;
      private final RestFieldValueTextContainer.FieldType type;
      private final Map<String, String> valueTextMap;

      public FieldData(String field, String type) {
         this(field, type, null);
      }

      public FieldData(String field, String type, Map<String, String> valueTextMap) {
         this.type = Arrays.stream(RestFieldValueTextContainer.FieldType.values()).filter(t -> t.name().equalsIgnoreCase(type)).findAny().get();
         this.field = field;
         if (valueTextMap == null) {
            this.valueTextMap = new HashMap<>();
            if (RestFieldValueTextContainer.FieldType.CHECKBOX.equals(this.type)) {
               this.valueTextMap.put("0", "GIKAM.CHECKBOX.UNCHECKED");
               this.valueTextMap.put("1", "GIKAM.CHECKBOX.CHECKED");
            }
         } else {
            this.valueTextMap = valueTextMap;
         }
      }

      public String getField() {
         return this.field;
      }

      public String getText(String value) {
         return this.valueTextMap.get(value);
      }
   }

   private static enum FieldType {
      SELECT,
      CHECKBOX;
   }
}
