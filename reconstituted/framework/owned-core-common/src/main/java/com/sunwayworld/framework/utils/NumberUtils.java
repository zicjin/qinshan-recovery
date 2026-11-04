package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.OutOfRangeException;
import com.sunwayworld.framework.exception.RoundingRuleException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleBean;
import com.sunwayworld.module.sys.rounding.service.CoreRoundingRuleService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class NumberUtils {
   private static final int NUMBER_SCALE = 8;
   private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]*(\\.\\d+|\\d+\\.{0,1}\\d*)([Ee][+-]?[\\d]+)?$");
   private static final Pattern SCIENTIFIC_NUMBER_PATTERN = Pattern.compile("^[-+]*(\\.\\d+|\\d+\\.{0,1}\\d*)[Ee]{1}[+-]?[\\d]+$");
   private static final String[] CHINESE_FRACTIONS = new String[]{"角", "分"};
   private static final String[] CHINESE_DIGITS = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
   private static final String[] CHINESE_UNITS = new String[]{"", "拾", "佰", "仟", "元", "万", "亿"};
   private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.##########");
   private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#");
   private static final Pattern DOUBLE_INCORRECT_PRECISION = Pattern.compile("\\.[0-9]+9{7}[0-9]?$");

   public static final boolean isNumber(String text) {
      return StringUtils.isEmpty(text) ? false : NUMBER_PATTERN.matcher(text).matches();
   }

   public static final boolean isScientificNumber(String text) {
      return StringUtils.isEmpty(text) ? false : SCIENTIFIC_NUMBER_PATTERN.matcher(text).matches();
   }

   public static final <T extends Number> String formatToChinese(T number) {
      String strNumber = formatNumber(number, 2, true);
      StringBuilder chineseBuilder = new StringBuilder();
      boolean hasFraction = strNumber.contains(".");
      if (StringUtils.startsWith(strNumber, "-")) {
         chineseBuilder.append("负");
         strNumber = StringUtils.removeStart(strNumber, "-");
      }

      String intStrNumber = hasFraction ? strNumber.substring(0, strNumber.indexOf(".")) : strNumber;
      char[] chars = intStrNumber.toCharArray();
      if (chars.length > 16) {
         throw new OutOfRangeException("The number to format chinese must be less than ten million billion.");
      } else {
         int i = 0;

         for (int j = chars.length; i < j; i++) {
            char c = chars[i];
            chineseBuilder.append(CHINESE_DIGITS[Character.getNumericValue(c)]);
            int index = (j - i) % 4;
            if (index == 0) {
               index = 4;
            }

            chineseBuilder.append(CHINESE_UNITS[--index]);
            if (j - i == 9) {
               chineseBuilder.append("亿");
            } else if ((j - i) % 4 == 1 && (j - i - 1) / 4 % 2 == 1) {
               chineseBuilder.append("万");
            }
         }

         chineseBuilder.append("元");
         String chinese = chineseBuilder.toString();
         String var12 = chinese.replaceAll("零[佰|仟|拾]", "零");
         String var13 = var12.replaceAll("零+", "零");
         String var14 = var13.replaceAll("零亿", "亿");
         String var15 = var14.replaceAll("零万", "万");
         String var16 = var15.replaceAll("亿万", "亿");
         if (!var16.equals("负零元") && !var16.equals("零元")) {
            var16 = var16.replaceAll("零元", "元");
         }

         if (hasFraction) {
            String fractionStrNumber = strNumber.substring(strNumber.indexOf(".") + 1);
            char[] fractionChars = fractionStrNumber.toCharArray();

            for (int ix = 0; ix < fractionChars.length; ix++) {
               if (ix == 0) {
                  int numericValue = Character.getNumericValue(fractionChars[0]);
                  var16 = var16 + CHINESE_DIGITS[numericValue];
                  if (numericValue > 0) {
                     var16 = var16 + CHINESE_FRACTIONS[ix];
                  }
               } else {
                  if (ix != 1) {
                     throw new UnexpectedException("Oops, are you kidding me?");
                  }

                  var16 = var16 + CHINESE_DIGITS[Character.getNumericValue(fractionChars[1])] + CHINESE_FRACTIONS[ix];
               }
            }

            return var16;
         } else {
            return var16 + "整";
         }
      }
   }

   public static final <T extends Number> String formatNumber(T number, int precision, boolean significantDigit) {
      number = getCorrectedNumber(number);
      String numberStr = String.format("%1$." + precision + "f", number);
      if (!significantDigit) {
         return numberStr;
      } else {
         while (numberStr.contains(".") && StringUtils.endsWith(numberStr, "0")) {
            numberStr = StringUtils.removeEnd(numberStr, "0");
         }

         return StringUtils.endsWith(numberStr, ".") ? StringUtils.removeEnd(numberStr, ".") : numberStr;
      }
   }

   public static final <T extends Number> String formatNumber(T number) {
      if (number == null) {
         return "";
      } else {
         Class<T> numberClass = (Class<T>)number.getClass();
         if (numberClass.isPrimitive()) {
            numberClass = (Class<T>)ClassUtils.getWrapperType(numberClass);
         }

         number = getCorrectedNumber(number);
         if (Float.class == numberClass) {
            return Float.isNaN((Float)number) ? "" : DOUBLE_FORMAT.format(Double.valueOf(Float.toString((Float)number)));
         } else if (Double.class == numberClass) {
            return Double.isNaN((Double)number) ? "" : DOUBLE_FORMAT.format(number);
         } else if (BigDecimal.class == numberClass) {
            return DOUBLE_FORMAT.format(((BigDecimal)number).doubleValue());
         } else if (Byte.class != numberClass
            && Short.class != numberClass
            && Integer.class != numberClass
            && Long.class != numberClass
            && BigInteger.class != numberClass) {
            throw new ApplicationRuntimeException("Unable format number (" + number + ").");
         } else {
            return INTEGER_FORMAT.format(number);
         }
      }
   }

   public static final <T extends Number> String scientificNotationFormat(T number, int precision) {
      if (number == null) {
         return "";
      } else {
         double d = number.doubleValue();
         if (d == 0.0) {
            return formatNumber(d, precision, false) + "E+0";
         } else {
            StringBuilder sb = new StringBuilder();
            if (d < 0.0) {
               sb.append("-");
               d = -d;
            }

            if (d >= 1.0) {
               int qty;
               for (qty = 0; d >= 10.0; d = div(d, 10.0)) {
                  qty++;
               }

               sb.append(formatNumber(d, precision, false)).append("E+").append(qty);
            } else {
               int qty;
               for (qty = 0; d < 1.0; d = mul(d, 10.0)) {
                  qty++;
               }

               sb.append(formatNumber(d, precision, false)).append("E-").append(qty);
            }

            return sb.toString();
         }
      }
   }

   public static final <T extends Number> T parseNumber(String text, Class<T> targetClass) {
      if (StringUtils.isEmpty(text)) {
         return (T)(targetClass.isPrimitive() ? ClassUtils.getPrimitiveDefaultValue(targetClass) : null);
      } else {
         if (ClassUtils.isPrimitiveType(targetClass)) {
            targetClass = (Class<T>)ClassUtils.getWrapperType(targetClass);
         }

         String newText = getAccurateNumber(text);
         if (Byte.class == targetClass) {
            return (T)(isHexNumber(newText) ? Byte.decode(newText) : Byte.valueOf(newText));
         } else if (Short.class == targetClass) {
            return (T)(isHexNumber(newText) ? Short.decode(newText) : Short.valueOf(newText));
         } else if (Integer.class == targetClass) {
            return (T)(isHexNumber(newText) ? Integer.decode(newText) : Integer.valueOf(newText));
         } else if (Long.class == targetClass) {
            return (T)(isHexNumber(newText) ? Long.decode(newText) : Long.valueOf(newText));
         } else if (BigInteger.class == targetClass) {
            return (T)(isHexNumber(newText) ? decodeBigInteger(newText) : new BigInteger(newText));
         } else if (Float.class == targetClass) {
            return (T)Float.valueOf(newText);
         } else if (Double.class == targetClass) {
            return (T)Double.valueOf(newText);
         } else if (BigDecimal.class != targetClass && Number.class != targetClass) {
            throw new ConvertFailedException(String.class, targetClass, text);
         } else {
            return (T)(new BigDecimal(newText));
         }
      }
   }

   public static <T extends Number> T max(T t1, T t2) {
      if (t1 == null) {
         return t2;
      } else if (t2 == null) {
         return t1;
      } else {
         return t1.doubleValue() > t2.doubleValue() ? t1 : t2;
      }
   }

   public static <T extends Number> T min(T t1, T t2) {
      if (t1 == null) {
         return t2;
      } else if (t2 == null) {
         return t1;
      } else {
         return t1.doubleValue() > t2.doubleValue() ? t2 : t1;
      }
   }

   public static String rounding(String data, String roundingScheme) {
      if (StringUtils.isEmpty(data)) {
         return null;
      } else if (!isNumber(data)) {
         throw new InvalidDataException("GIKAM.EXCEPTION.ROUNDING_NAN");
      } else if (!StringUtils.isEmpty(roundingScheme) && !"NONE".equals(roundingScheme) && !"无修约规则".equals(roundingScheme)) {
         String roundingRuleId = StringUtils.split(roundingScheme, ",")[0];
         CoreRoundingRuleBean roundingRule = ApplicationContextHelper.getBean(CoreRoundingRuleService.class).selectByIdIfPresent(roundingRuleId);
         if (roundingRule == null || "1".equals(roundingRule.getLastSuspendedFlag())) {
            throw new RoundingRuleException("GIKAM.EXCEPTION.ROUNDINGRULE_EXISTS");
         } else if (ObjectUtils.isEmpty(roundingRule.getScript())) {
            throw new RoundingRuleException(roundingRule.getRuleName() + I18nHelper.getMessage("GIKAM.EXCEPTION.ROUNDINGRULE_SCRIPT_NOT_EXISTS"));
         } else {
            int numberCnt = parseInt(StringUtils.split(roundingScheme, ",")[1]);
            BshExecutor bsh = BshExecutor.instance();
            bsh.eval("import com.sunwayworld.framework.utils.StringUtils;");
            bsh.eval("import com.sunwayworld.framework.utils.NumberUtils;");
            bsh.eval("import java.math.BigDecimal;");
            bsh.eval("import java.math.RoundingMode;");
            bsh.eval("import java.util.regex.Pattern;");
            bsh.set("data", data);
            bsh.set("numberCnt", numberCnt);
            return bsh.eval(roundingRule.getScript());
         }
      } else {
         return data;
      }
   }

   public static <T> long parseLong(T text) {
      return ConvertUtils.convert(text, long.class);
   }

   public static <T> long parseLong(T text, long defaultValue) {
      try {
         return ConvertUtils.convert(text, long.class);
      } catch (Exception var4) {
         return defaultValue;
      }
   }

   public static <T> double parseDouble(T text) {
      return ConvertUtils.convert(text, double.class);
   }

   public static <T> double parseDouble(T text, double defaultValue) {
      try {
         return ConvertUtils.convert(text, double.class);
      } catch (Exception var4) {
         return defaultValue;
      }
   }

   public static <T> float parseFloat(T text) {
      return ConvertUtils.convert(text, float.class);
   }

   public static <T> float parseFloat(T text, float defaultValue) {
      try {
         return ConvertUtils.convert(text, float.class);
      } catch (Exception var3) {
         return defaultValue;
      }
   }

   public static <T> int parseInt(T text) {
      return ConvertUtils.convert(text, int.class);
   }

   public static <T> int parseInt(T text, int defaultValue) {
      try {
         return ConvertUtils.convert(text, int.class);
      } catch (Exception var3) {
         return defaultValue;
      }
   }

   public static <T> short parseShort(T text) {
      return ConvertUtils.convert(text, short.class);
   }

   public static <T> short parseShort(T text, short defaultValue) {
      try {
         return ConvertUtils.convert(text, short.class);
      } catch (Exception var3) {
         return defaultValue;
      }
   }

   public static <T> BigInteger parseBigInteger(T text) {
      return ConvertUtils.convert(text, BigInteger.class);
   }

   public static <T> BigInteger parseBigInteger(T text, BigInteger defaultValue) {
      try {
         return ConvertUtils.convert(text, BigInteger.class, defaultValue);
      } catch (Exception var3) {
         return defaultValue;
      }
   }

   public static <T> BigDecimal parseBigDecimal(T text) {
      return ConvertUtils.convert(text, BigDecimal.class);
   }

   public static <T> BigDecimal parseBigDecimal(T text, BigDecimal defaultValue) {
      try {
         return ConvertUtils.convert(text, BigDecimal.class, defaultValue);
      } catch (Exception var3) {
         return defaultValue;
      }
   }

   public static final <T> double toDouble(final T number) {
      return number == null ? 0.0 : ConvertUtils.convert(number.toString(), double.class, 0.0);
   }

   public static final <T> double toCurrencyDouble(final T number) {
      return toDouble(number, 2);
   }

   public static final <T> double toUnitPriceDouble(final T number) {
      return toDouble(number, 6);
   }

   public static final <T> double toQuantityDouble(final T number) {
      return toDouble(number, 4);
   }

   public static final <T> double toDouble(final T number, int precision) {
      if (number == null) {
         return 0.0;
      } else {
         double d = toDouble(number);
         return precision == 0 ? d : new BigDecimal(Double.toString(d)).setScale(precision, 4).doubleValue();
      }
   }

   public static final int toBinaryNumber(final String number) {
      if (number == null) {
         return 0;
      } else {
         try {
            return Integer.valueOf(number, 2);
         } catch (NumberFormatException var2) {
            return 0;
         }
      }
   }

   public static final boolean isGreater(Number n1, Number n2) {
      if (n1 == null) {
         return false;
      } else {
         return n2 == null ? true : n1.doubleValue() > n2.doubleValue();
      }
   }

   public static final boolean isLess(Number n1, Number n2) {
      if (n1 == null) {
         return n2 != null;
      } else {
         return n2 == null ? false : n1.doubleValue() < n2.doubleValue();
      }
   }

   @Deprecated
   public static double add(double v1, double v2) {
      BigDecimal b1 = new BigDecimal(Double.toString(v1));
      BigDecimal b2 = new BigDecimal(Double.toString(v2));
      return b1.add(b2).doubleValue();
   }

   @Deprecated
   public static double sub(double v1, double v2) {
      BigDecimal b1 = new BigDecimal(Double.toString(v1));
      BigDecimal b2 = new BigDecimal(Double.toString(v2));
      return b1.subtract(b2).doubleValue();
   }

   @Deprecated
   public static double mul(double v1, double v2) {
      BigDecimal b1 = new BigDecimal(Double.toString(v1));
      BigDecimal b2 = new BigDecimal(Double.toString(v2));
      return b1.multiply(b2).doubleValue();
   }

   @Deprecated
   public static double div(double v1, double v2) {
      BigDecimal b1 = new BigDecimal(Double.toString(v1));
      BigDecimal b2 = new BigDecimal(Double.toString(v2));
      return b1.divide(b2, 8, 4).doubleValue();
   }

   @Deprecated
   public static double round(double v1, int scale) {
      BigDecimal b1 = new BigDecimal(Double.toString(v1));
      BigDecimal b2 = new BigDecimal(1.0);
      return b1.divide(b2, scale, 4).doubleValue();
   }

   private static boolean isHexNumber(String value) {
      int index = value.startsWith("-") ? 1 : 0;
      return value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index);
   }

   private static BigInteger decodeBigInteger(String value) {
      int radix = 10;
      int index = 0;
      boolean negative = false;
      if (value.startsWith("-")) {
         negative = true;
         index++;
      }

      if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
         index += 2;
         radix = 16;
      } else if (value.startsWith("#", index)) {
         index++;
         radix = 16;
      } else if (value.startsWith("0", index) && value.length() > 1 + index) {
         index++;
         radix = 8;
      }

      BigInteger result = new BigInteger(value.substring(index), radix);
      return negative ? result.negate() : result;
   }

   private static final <T extends Number> T getCorrectedNumber(T number) {
      Class<T> numberClass = (Class<T>)number.getClass();
      return !numberClass.equals(Double.class) && !numberClass.equals(double.class) && !numberClass.equals(Float.class) && !numberClass.equals(float.class)
         ? number
         : parseNumber(getAccurateNumber("" + number), numberClass);
   }

   private static String getAccurateNumber(final String strNumber) {
      String noCommaStrNumber = StringUtils.trim(StringUtils.replace(strNumber, ",", ""));
      if (!DOUBLE_INCORRECT_PRECISION.matcher(noCommaStrNumber).find()) {
         return noCommaStrNumber;
      } else {
         int length = noCommaStrNumber.substring(noCommaStrNumber.indexOf(".")).length() - 3;
         StringBuilder offset = new StringBuilder("0.");

         for (int i = 0; i < length; i++) {
            offset.append("0");
         }

         offset.append("1");
         Double d = new BigDecimal(noCommaStrNumber).doubleValue() + new Double(offset.toString());
         DecimalFormat df = new DecimalFormat();
         df.setMaximumFractionDigits(6);
         df.setGroupingUsed(false);
         return df.format(d);
      }
   }
}
