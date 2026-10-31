package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.owned.internal.context.OwnedRequestContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public abstract class StringUtils {
    public StringUtils() { }

    public static final boolean isBlank(String value) { return value == null || value.length() == 0 || trim(value).length() == 0; }
    public static final boolean isEmpty(String value) { return value == null || value.length() == 0; }
    public static final boolean contains(String value, String search) { return isEmpty(value) ? false : isEmpty(search) || value.contains(search); }
    public static final boolean containsIgnoreCase(String value, String search) { return isEmpty(value) ? false : isEmpty(search) || value.toLowerCase().contains(search.toLowerCase()); }
    public static final String capitalize(String value) { return changeFirstCharacterCase(value, true); }
    public static final String uncapitalize(String value) { return changeFirstCharacterCase(value, false); }
    public static final boolean startsWith(String value, String prefix) { return value != null && prefix != null && prefix.length() <= value.length() && value.startsWith(prefix); }
    public static final boolean startsWithIgnoreCase(String value, String prefix) { return value != null && prefix != null && prefix.length() <= value.length() && value.substring(0, prefix.length()).toLowerCase().equals(prefix.toLowerCase()); }
    public static final boolean endsWith(String value, String suffix) { return value != null && suffix != null && suffix.length() <= value.length() && value.endsWith(suffix); }
    public static final boolean endsWithIgnoreCase(String value, String suffix) { return value != null && suffix != null && suffix.length() <= value.length() && value.substring(value.length() - suffix.length()).toLowerCase().equals(suffix.toLowerCase()); }
    public static final String trim(String value) {
        if (value == null) return null;
        char[] chars = value.toCharArray(); int start = 0, end = chars.length;
        while (start < end && Character.isWhitespace(chars[start])) start++;
        while (end > 1 && Character.isWhitespace(chars[end - 1])) end--;
        return end > start ? String.copyValueOf(chars, start, end - start) : "";
    }
    public static final <V> String replace(String value, String search, V replacement) {
        if (isEmpty(value) || isEmpty(search)) return value;
        int start = 0, index = value.indexOf(search); if (index < 0) return value;
        StringBuilder out = new StringBuilder(value.substring(0, start)); int length = search.length();
        while (index >= 0) { out.append(value.substring(start, index)).append(replacement); start = index + length; index = value.indexOf(search, start); }
        return out.append(value.substring(start)).toString();
    }
    public static final <V> String replaceFirst(String value, String search, V replacement) { return replaceOne(value, search, replacement, false); }
    public static final <V> String replaceLast(String value, String search, V replacement) { return replaceOne(value, search, replacement, true); }
    public static final <V> String replaceIgnoreCase(String value, String search, V replacement) {
        if (isEmpty(value) || isEmpty(search)) return value;
        StringBuilder out = new StringBuilder(); int last = 0, index = 0, n = search.length();
        while (index + n <= value.length()) { if (value.substring(index, index + n).equalsIgnoreCase(search)) { out.append(value, last, index).append(replacement); last = index + n; index = last; } else index++; }
        return out.append(value.substring(last)).toString();
    }
    public static final String removeStart(String value, String prefix) { return isEmpty(value) || isEmpty(prefix) ? value : value.startsWith(prefix) ? value.substring(prefix.length()) : value; }
    public static final String removeStartIgnoreCase(String value, String prefix) { return isEmpty(value) || isEmpty(prefix) ? value : value.toLowerCase().startsWith(prefix.toLowerCase()) ? value.substring(prefix.length()) : value; }
    public static final String removeEnd(String value, String suffix) { return isEmpty(value) || isEmpty(suffix) ? value : value.endsWith(suffix) ? value.substring(0, value.length() - suffix.length()) : value; }
    public static final String removeEndIgnoreCase(String value, String suffix) { return isEmpty(value) || isEmpty(suffix) ? value : value.toLowerCase().endsWith(suffix.toLowerCase()) ? value.substring(0, value.length() - suffix.length()) : value; }
    public static final String trimAllWhitespace(String value) { if (isEmpty(value)) return value; StringBuilder out = new StringBuilder(value.length()); for (int i=0;i<value.length();i++) { char c=value.charAt(i); if (!Character.isSpaceChar(c) && !Character.isWhitespace(c)) out.append(c); } return out.toString(); }
    public static final String replaceBetween(String value, String open, String close, Map<String, ? extends Object> replacements) {
        if (replacements == null || replacements.isEmpty()) return value;
        List<String> parts = substringsBetween(value, open, close); if (parts.isEmpty()) return value; String result=value;
        for (String part : parts) { String[] split=split(part, ":"); if (split.length > 2) throw new ApplicationRuntimeException("Invalid string:"+value+", substring:"+part+", contains more than one \":\"."); String key=split[0]; Object replacement=replacements.get(key); if (replacement == null) { if (split.length > 1) result=replace(result, open+part+close, split[1]); } else result=replace(result, open+part+close, ConvertUtils.convert(replacement,String.class)); }
        return result;
    }
    public static final String replaceBetweenSpan(String value, Map<String,Object> replacements) {
        if (replacements.isEmpty()) return value; String result=value;
        for (Map.Entry<String,Object> entry:replacements.entrySet()) { Pattern p=Pattern.compile("<span\\s+id\\s*=\\s*('|\")"+entry.getKey()+"('|\")\\s*>(.|\\s)*?</span>", Pattern.CASE_INSENSITIVE); Matcher m=p.matcher(result); result=m.replaceAll("<span id='"+entry.getKey()+"'>"+entry.getValue()+"</span>"); }
        return result;
    }
    public static final String repeat(String value, int count) { if (count <= 0 || isEmpty(value)) return ""; StringBuilder out=new StringBuilder(value.length()*count); for(int i=0;i<count;i++)out.append(value); return out.toString(); }
    public static final boolean repeatOf(String value, String pattern) { if(value==null)return pattern==null; if(pattern==null)return false; if(value.length()<pattern.length())return false; if(pattern.length()==0)return value.length()==0; if(value.length()%pattern.length()!=0)return false; for(int i=0;i<value.length();i++)if(value.charAt(i)!=pattern.charAt(i%pattern.length()))return false; return true; }
    public static int countOccurrencesOf(String value, String search) { if(value==null||search==null||value.length()==0||search.length()==0)return 0; int count=0; for(int i=0;i<=value.length()-search.length();) { if(value.substring(i,i+search.length()).equals(search)){count++;i+=search.length();}else i++; } return count; }
    public static String[] split(String value, String delimiter) { if(value==null)return new String[0]; if(isEmpty(delimiter))return new String[]{value}; List<String> parts=new ArrayList<String>(); int start=0,index; while((index=value.indexOf(delimiter,start))>=0){parts.add(value.substring(start,index));start=index+delimiter.length();} if(start<=value.length())parts.add(value.substring(start)); return parts.toArray(new String[0]); }
    public static String[] regexSplit(String value, String regex) { return value==null?new String[0]:isEmpty(regex)?new String[]{value}:value.split(regex); }
    public static String decode(String value, String sourceEncoding, String targetEncoding) { try{return new String(value.getBytes(sourceEncoding),targetEncoding);}catch(UnsupportedEncodingException e){throw new UnsupportedException(e);} }
    public static final String unionArithmetic(String first, String second, int length) { if(isEmpty(first)||isEmpty(second))return ""; int value=toBinaryNumber(first)|toBinaryNumber(second); return leftPad(Integer.toBinaryString(value),length); }
    public static final String leftPad(String value, int length) { return pad(value,length,true); }
    public static final String rightPad(String value, int length) { return pad(value,length,false); }
    public static List<String> substringsBetween(String value, String open, String close) { List<String> out=new ArrayList<String>(); if(isEmpty(value)||isEmpty(open)||isEmpty(close))return out; int pos=0; while(pos<value.length()-close.length()){int start=value.indexOf(open,pos);if(start<0)break;start+=open.length();int end=value.indexOf(close,start);if(end<0)break;out.add(value.substring(start,end));pos=end+close.length();} return out; }
    public static String randomUUID(int length) { return randomUUID().substring(0,length); }
    public static String randomUUID() { return replace(UUID.randomUUID().toString(),"-",""); }
    public static final String encodeChineseFileName(String value) {
        if (isEmpty(value)) return "";
        String userAgent = null;
        HttpServletRequest request = OwnedRequestContext.getHttpServletRequest();
        userAgent = request.getHeader("USER-AGENT");
        if (userAgent == null) return value;
        try {
            if (userAgent.indexOf("Firefox") != -1) return "=?UTF-8?B?" + java.util.Base64.getEncoder().encodeToString(value.getBytes("UTF-8")) + "?=";
            if (userAgent.indexOf("Chrome") != -1) return new String(value.getBytes(), "ISO8859-1");
            return replace(URLEncoder.encode(value, "UTF-8"), "+", "%20");
        } catch (UnsupportedEncodingException e) { return value; }
    }
    public static final boolean equalsIgnoreCase(String a,String b){return a==null?b==null:b!=null&&a.equalsIgnoreCase(b);}
    public static final boolean equals(String a,String b){return a==null?b==null:b!=null&&a.equals(b);}
    public static final String encodeURL(String value) { try { if(value.contains(" ")) { StringBuilder out=new StringBuilder(); for(String part:split(value," ")) { if(out.length()>0)out.append("%20"); if(!isEmpty(part))out.append(URLEncoder.encode(part,"UTF-8")); } return out.toString(); } return URLEncoder.encode(value,"UTF-8"); } catch(UnsupportedEncodingException e){throw new UnexpectedException(e);} }
    public static final String decodeURL(String value) { try{return URLDecoder.decode(value,"UTF-8");}catch(UnsupportedEncodingException e){throw new UnexpectedException(e);} }
    public static final String reverse(String value){return value==null?null:new StringBuilder(value).reverse().toString();}
    public static final String lowerCase(String value){return value==null?null:value.toLowerCase();}
    public static final Object lowerCaseIfPossible(Object value){return value instanceof String?((String)value).toLowerCase():value;}
    public static final String upperCase(String value){return value instanceof String?value.toUpperCase():value;}
    public static final String join(Collection<?> values,String delimiter){StringBuilder out=new StringBuilder();for(Object value:values){if(out.length()>0)out.append(delimiter);out.append(ConvertUtils.convert(value,String.class,""));}return out.toString();}
    public static final <T> String join(T[] values,String delimiter){StringBuilder out=new StringBuilder();for(T value:values){if(out.length()>0)out.append(delimiter);out.append(ConvertUtils.convert(value,String.class,""));}return out.toString();}
    public static final String read(InputStream input){try{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] buf=new byte[2048];int n;while((n=input.read(buf))!=-1)out.write(buf,0,n);if(out.size()==0)return "";byte[] bytes=out.toByteArray();return new String(bytes,EncodingUtils.detect(bytes));}catch(IOException e){throw new FileException(e);}}
    public static final String abbreviate(String value){return isBlank(value)||value.length()<=240?value:value.substring(0,240)+"...";}
    public static final boolean containsNumber(String value){return !isBlank(value)&&value.matches(".*\\d+.*");}
    public static final boolean containsLowercaseLetter(String value){return !isBlank(value)&&value.matches(".*[a-z]+.*");}
    public static final boolean containsCapitalLetter(String value){return !isBlank(value)&&value.matches(".*[A-Z]+.*");}
    public static final boolean containsSpecialCharacter(String value){return !isBlank(value)&&value.matches(".*[^a-zA-Z0-9]+.*");}
    public static final String toHexString(byte[] bytes){StringBuilder out=new StringBuilder(bytes.length*2);for(byte b:bytes){int n=b&255;if(n<16)out.append('0');out.append(Long.toString(n,16));}return out.toString();}

    private static <V> String replaceOne(String value,String search,V replacement,boolean last){if(isEmpty(value)||isEmpty(search))return value;int index=last?value.lastIndexOf(search):value.indexOf(search);return index<0?value:value.substring(0,index)+replacement+value.substring(index+search.length());}
    private static String changeFirstCharacterCase(String value,boolean upper){if(isEmpty(value))return value;return (upper?Character.toUpperCase(value.charAt(0)):Character.toLowerCase(value.charAt(0)))+value.substring(1);}
    private static final String pad(String value,int length,boolean left){if(isEmpty(value))return repeat("0",length);if(length<=0||value.length()>=length)return value;String zeros=repeat("0",length-value.length());return left?zeros+value:value+zeros;}
    private static int toBinaryNumber(String value){if(value==null)return 0;try{return Integer.valueOf(value,2);}catch(NumberFormatException ignored){return 0;}}
}
