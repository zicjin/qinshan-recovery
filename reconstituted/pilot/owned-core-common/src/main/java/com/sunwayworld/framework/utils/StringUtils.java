/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.EncodingUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;

public abstract class StringUtils {
    public static final boolean isBlank(String target) {
        return target == null || target.length() == 0 || StringUtils.trim(target).length() == 0;
    }

    public static final boolean isEmpty(String target) {
        return target == null || target.length() == 0;
    }

    public static final boolean contains(String str, String searchPattern) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        if (StringUtils.isEmpty(searchPattern)) {
            return true;
        }
        return str.contains(searchPattern);
    }

    public static final boolean containsIgnoreCase(String str, String searchPattern) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        if (StringUtils.isEmpty(searchPattern)) {
            return true;
        }
        return str.toLowerCase().contains(searchPattern.toLowerCase());
    }

    public static final String capitalize(String str) {
        return StringUtils.changeFirstCharacterCase(str, true);
    }

    public static final String uncapitalize(String str) {
        return StringUtils.changeFirstCharacterCase(str, false);
    }

    public static final boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.startsWith(prefix);
    }

    public static final boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.substring(0, prefix.length()).toLowerCase().equals(prefix.toLowerCase());
    }

    public static final boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        return str.endsWith(suffix);
    }

    public static final boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        return str.substring(str.length() - suffix.length()).toLowerCase().equals(suffix.toLowerCase());
    }

    public static final String trim(String target) {
        if (target == null) {
            return null;
        }
        char[] chars = target.toCharArray();
        int start = 0;
        int end = chars.length;
        for (char c : chars) {
            if (!Character.isWhitespace(c)) break;
            ++start;
        }
        for (int i = chars.length - 1; i > 0 && Character.isWhitespace(chars[i]); --i) {
            --end;
        }
        if (end > start) {
            return String.copyValueOf(chars, start, end - start);
        }
        return "";
    }

    public static final <V> String replace(String target, String searchPattern, V replacement) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        int start = 0;
        int end = target.indexOf(searchPattern, start);
        if (end == -1) {
            return target;
        }
        StringBuilder sb = new StringBuilder(target.substring(0, start));
        int searchPatternLen = searchPattern.length();
        while (end != -1) {
            sb.append(target.substring(start, end)).append(replacement);
            start = end + searchPatternLen;
            end = target.indexOf(searchPattern, start);
        }
        sb.append(target.substring(start));
        return sb.toString();
    }

    public static final <V> String replaceFirst(String target, String searchPattern, V replacement) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        int pos = target.indexOf(searchPattern);
        if (pos == -1) {
            return target;
        }
        return target.substring(0, pos) + replacement + target.substring(pos + searchPattern.length());
    }

    public static final <V> String replaceLast(String target, String searchPattern, V replacement) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        int pos = target.lastIndexOf(searchPattern);
        if (pos == -1) {
            return target;
        }
        return target.substring(0, pos) + replacement + target.substring(pos + searchPattern.length());
    }

    public static final <V> String replaceIgnoreCase(String target, String searchPattern, V replacement) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        int targetLength = target.length();
        int searchPatternLength = searchPattern.length();
        StringBuilder sb = new StringBuilder();
        int start = 0;
        int lastEnd = 0;
        while (start + searchPatternLength <= targetLength) {
            String next = target.substring(start, start + searchPatternLength);
            if (next.equalsIgnoreCase(searchPattern)) {
                sb.append(target.substring(lastEnd, start)).append(replacement);
                start = lastEnd = start + searchPatternLength;
                continue;
            }
            ++start;
        }
        sb.append(target.substring(lastEnd));
        return sb.toString();
    }

    public static final String removeStart(String target, String searchPattern) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        if (target.startsWith(searchPattern)) {
            return target.substring(searchPattern.length());
        }
        return target;
    }

    public static final String removeStartIgnoreCase(String target, String searchPattern) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        if (target.toLowerCase().startsWith(searchPattern.toLowerCase())) {
            return target.substring(searchPattern.length());
        }
        return target;
    }

    public static final String removeEnd(String target, String searchPattern) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        if (target.endsWith(searchPattern)) {
            return target.substring(0, target.length() - searchPattern.length());
        }
        return target;
    }

    public static final String removeEndIgnoreCase(String target, String searchPattern) {
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(searchPattern)) {
            return target;
        }
        if (target.toLowerCase().endsWith(searchPattern.toLowerCase())) {
            return target.substring(0, target.length() - searchPattern.length());
        }
        return target;
    }

    public static final String trimAllWhitespace(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (Character.isSpaceChar(c) || Character.isWhitespace(c)) continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static final String replaceBetween(String target, String openTag, String closeTag, Map<String, ? extends Object> map) {
        if (map == null || map.isEmpty()) {
            return target;
        }
        List<String> substrings = StringUtils.substringsBetween(target, openTag, closeTag);
        if (substrings.isEmpty()) {
            return target;
        }
        String replaceTarget = target;
        for (String substring : substrings) {
            String[] values = StringUtils.split(substring, ":");
            if (values.length > 2) {
                throw new ApplicationRuntimeException("Invalid string:" + target + ", substring:" + substring + ", contains more than one \":\".", new String[0]);
            }
            String key = values[0];
            Object replacementValue = map.get(key);
            if (replacementValue == null) {
                if (values.length <= 1) continue;
                replaceTarget = StringUtils.replace(replaceTarget, openTag + substring + closeTag, values[1]);
                continue;
            }
            String replacement = ConvertUtils.convert(replacementValue, String.class);
            replaceTarget = StringUtils.replace(replaceTarget, openTag + substring + closeTag, replacement);
        }
        return replaceTarget;
    }

    public static final String replaceBetweenSpan(String target, Map<String, Object> map) {
        if (map.isEmpty()) {
            return target;
        }
        String replaceTarget = target;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String regex = "<span\\s+id\\s*=\\s*('|\")" + entry.getKey() + "('|\")\\s*>(.|\\s)*?</span>";
            Pattern p = Pattern.compile(regex, 2);
            Matcher m = p.matcher(replaceTarget);
            replaceTarget = m.replaceAll("<span id='" + entry.getKey() + "'>" + entry.getValue() + "</span>");
        }
        return replaceTarget;
    }

    public static final String repeat(String source, int repeat) {
        if (repeat <= 0 || StringUtils.isEmpty(source)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(source.length() * repeat);
        for (int i = 0; i < repeat; ++i) {
            sb.append(source);
        }
        return sb.toString();
    }

    public static final boolean repeatOf(String source, String repeat) {
        char[] repeatChars;
        if (source == null) {
            return repeat == null;
        }
        if (repeat == null) {
            return false;
        }
        char[] sourceChars = source.toCharArray();
        if (sourceChars.length < (repeatChars = repeat.toCharArray()).length) {
            return false;
        }
        if (repeatChars.length == 0) {
            return sourceChars.length == 0;
        }
        if (sourceChars.length % repeatChars.length > 0) {
            return false;
        }
        int j = sourceChars.length;
        for (int i = 0; i < j; ++i) {
            if (sourceChars[i] == repeatChars[i % repeatChars.length]) continue;
            return false;
        }
        return true;
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int subLength = sub.length();
        int i = 0;
        while (i <= str.length() - subLength) {
            if (sub.equals(str.substring(i, i + subLength))) {
                ++count;
                i += subLength;
                continue;
            }
            ++i;
        }
        return count;
    }

    public static String[] split(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (StringUtils.isEmpty(delimiter)) {
            return new String[]{str};
        }
        ArrayList<String> container = new ArrayList<String>();
        int currentIndex = 0;
        int lastIndex = 0;
        int delimiterLength = delimiter.length();
        while ((currentIndex = str.indexOf(delimiter, lastIndex)) > -1) {
            container.add(str.substring(lastIndex, currentIndex));
            lastIndex = currentIndex + delimiterLength;
        }
        if (lastIndex <= str.length()) {
            container.add(str.substring(lastIndex));
        }
        return container.toArray(new String[0]);
    }

    public static String[] regexSplit(String str, String regex) {
        if (str == null) {
            return new String[0];
        }
        if (StringUtils.isEmpty(regex)) {
            return new String[]{str};
        }
        return str.split(regex);
    }

    public static String decode(String str, String fromCharsetName, String toCharsetName) {
        try {
            return new String(str.getBytes(fromCharsetName), toCharsetName);
        }
        catch (UnsupportedEncodingException uee) {
            throw new UnsupportedException(uee);
        }
    }

    public static final String unionArithmetic(String target1, String target2, int length) {
        if (StringUtils.isEmpty(target1) || StringUtils.isEmpty(target2)) {
            return "";
        }
        String temp = Integer.toBinaryString(NumberUtils.toBinaryNumber(target1) | NumberUtils.toBinaryNumber(target2));
        return StringUtils.leftPad(temp, length);
    }

    public static final String leftPad(String source, int length) {
        return StringUtils.pad(source, length, true);
    }

    public static final String rightPad(String source, int length) {
        return StringUtils.pad(source, length, false);
    }

    public static List<String> substringsBetween(String target, String openTag, String closeTag) {
        int end;
        int start;
        ArrayList<String> subStrings = new ArrayList<String>();
        if (StringUtils.isEmpty(target) || StringUtils.isEmpty(openTag) || StringUtils.isEmpty(closeTag)) {
            return subStrings;
        }
        int strLen = target.length();
        if (strLen == 0) {
            return subStrings;
        }
        int closeLen = closeTag.length();
        int openLen = openTag.length();
        int pos = 0;
        while (pos < strLen - closeLen && (start = target.indexOf(openTag, pos)) >= 0 && (end = target.indexOf(closeTag, start += openLen)) >= 0) {
            subStrings.add(target.substring(start, end));
            pos = end + closeLen;
        }
        return subStrings;
    }

    public static String randomUUID(int size) {
        return StringUtils.randomUUID().substring(0, size);
    }

    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        return StringUtils.replace(uuid.toString(), "-", "");
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        sb.append(str.substring(1));
        return sb.toString();
    }

    private static final String pad(String source, int length, boolean leftPosition) {
        if (StringUtils.isEmpty(source)) {
            return StringUtils.repeat("0", length);
        }
        if (length <= 0 || source.length() >= length) {
            return source;
        }
        if (leftPosition) {
            return StringUtils.repeat("0", length - source.length()) + source;
        }
        return source + StringUtils.repeat("0", length - source.length());
    }

    public static final String encodeChineseFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String encodedFileName = fileName;
        String agent = request.getHeader("USER-AGENT");
        if (agent != null) {
            try {
                if (-1 != agent.indexOf("Firefox")) {
                    encodedFileName = "=?UTF-8?B?" + new String(Base64.encodeBase64(fileName.getBytes("UTF-8"))) + "?=";
                } else if (-1 != agent.indexOf("Chrome")) {
                    encodedFileName = new String(fileName.getBytes(), "ISO8859-1");
                } else {
                    encodedFileName = URLEncoder.encode(fileName, "UTF-8");
                    encodedFileName = StringUtils.replace(encodedFileName, "+", "%20");
                }
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return encodedFileName;
    }

    public static final boolean equalsIgnoreCase(String v1, String v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v2 == null) {
            return v1 == null;
        }
        return v1.equalsIgnoreCase(v2);
    }

    public static final boolean equals(String v1, String v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v2 == null) {
            return v1 == null;
        }
        return v1.equals(v2);
    }

    public static final String encodeURL(String url) {
        if (url.contains(" ")) {
            String[] strs = StringUtils.split(url, " ");
            StringBuilder sb = new StringBuilder();
            for (String s : strs) {
                if (sb.length() > 0) {
                    sb.append("%20");
                }
                if (StringUtils.isEmpty(s)) continue;
                try {
                    sb.append(URLEncoder.encode(s, "UTF-8"));
                }
                catch (UnsupportedEncodingException uee) {
                    throw new UnexpectedException(uee);
                }
            }
            return sb.toString();
        }
        try {
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new UnexpectedException(uee);
        }
    }

    public static final String decodeURL(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new UnexpectedException(uee);
        }
    }

    public static final String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static final String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static final Object lowerCaseIfPossible(Object value) {
        if (value == null || !(value instanceof String)) {
            return value;
        }
        return ((String)value).toLowerCase();
    }

    public static final String upperCase(String value) {
        if (value == null || !(value instanceof String)) {
            return value;
        }
        return value.toUpperCase();
    }

    public static final String join(Collection<?> itemList, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object item : itemList) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(ConvertUtils.convert(item, String.class, ""));
        }
        return sb.toString();
    }

    public static final <T> String join(T[] itemList, String separator) {
        StringBuilder sb = new StringBuilder();
        for (T item : itemList) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(ConvertUtils.convert(item, String.class, ""));
        }
        return sb.toString();
    }

    public static final String read(InputStream is) {
        try {
            int i;
            byte[] bytes = null;
            byte[] buf = new byte[2048];
            while ((i = is.read(buf)) != -1) {
                if (bytes == null) {
                    bytes = new byte[i];
                    System.arraycopy(buf, 0, bytes, 0, i);
                    continue;
                }
                byte[] temp = new byte[bytes.length + i];
                System.arraycopy(bytes, 0, temp, 0, bytes.length);
                System.arraycopy(buf, 0, temp, bytes.length, i);
                bytes = temp;
            }
            if (bytes == null) {
                return "";
            }
            return new String(bytes, EncodingUtils.detect(bytes));
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    public static final String abbreviate(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        int length = text.length();
        if (length > 240) {
            return text.substring(0, 240) + "...";
        }
        return text;
    }

    public static final boolean containsNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.matches(".*\\d+.*");
    }

    public static final boolean containsLowercaseLetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.matches(".*[a-z]+.*");
    }

    public static final boolean containsCapitalLetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.matches(".*[A-Z]+.*");
    }

    public static final boolean containsSpecialCharacter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return text.matches(".*[^a-zA-Z0-9]+.*");
    }

    public static final String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 0xFF) < 16) {
                sb.append("0");
            }
            sb.append(Long.toString(bytes[i] & 0xFF, 16));
        }
        return sb.toString();
    }
}
