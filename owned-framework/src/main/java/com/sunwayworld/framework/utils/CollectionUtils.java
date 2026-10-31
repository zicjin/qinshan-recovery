package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.UnexpectedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CollectionUtils {
    public static <T> boolean isEmpty(Collection<T> value) { return value == null || value.isEmpty(); }
    public static boolean isEmpty(Map<?,?> value) { return value == null || value.isEmpty(); }
    public static final <T> List<T> union(Collection<T> left, Collection<T> right) { List<T> out=new ArrayList<T>(left);out.addAll(right);return out.stream().distinct().collect(Collectors.toList()); }
    public static final <T> List<T> intersection(Collection<T> left, Collection<T> right) { return left.stream().filter(right::contains).distinct().collect(Collectors.toList()); }
    public static final <T> List<T> disjunction(Collection<T> left, Collection<T> right) { List<T> out=subtract(left,right);out.addAll(subtract(right,left));return out; }
    public static final <T> void merge(Collection<T> target, Collection<T> source) { if(isEmpty(source))return; source.stream().filter(v->!target.contains(v)).forEach(target::add); }
    public static final <T> List<T> subtract(Collection<T> left, Collection<T> right) { return left.stream().filter(v->!right.contains(v)).distinct().collect(Collectors.toList()); }
    public static final boolean containsIgnoreCase(Collection<String> values,String value){if(values.isEmpty())return false;for(String v:values)if(v==null?value==null:v.equalsIgnoreCase(value))return true;return false;}
    public static final Object getFirstValue(Map<?,?> values){if(isEmpty(values))return null;return values.values().toArray()[0];}
    public static final Object getFirstValue(Collection<?> values){if(isEmpty(values))return null;return values.iterator().next();}
    public static final boolean containsIgnoreCase(Map<String,Object> values,String key){return containsKeyIgnorecase(values,key);}
    public static final boolean containsKeyIgnorecase(Map<String,?> values,String key){if(isEmpty(values))return false;for(String k:values.keySet())if(key.equalsIgnoreCase(k))return true;return false;}
    public static final Object getValueIgnorecase(Map<String,?> values,String key){if(isEmpty(values))return null;String found=null;for(String k:values.keySet())if(key.equalsIgnoreCase(k)){found=k;break;}return found==null||found.trim().isEmpty()?null:values.get(found);}
    public static final <S,T> List<T> convert(List<S> values,Class<T> target){if(values.isEmpty())return emptyList();Class<?> source=values.get(0).getClass();if(target.isAssignableFrom(source))return values.stream().map(v->(T)v).collect(Collectors.toList());return values.stream().map(v->ConvertUtils.convert(v,target)).collect(Collectors.toList());}
    public static final <T> List<T> distinct(List<T> values){return values.stream().distinct().collect(Collectors.toList());}
    public static final void lowerCaseKey(Map<String,Object> values){new HashSet<String>(values.keySet()).forEach(k->{String n=k.toLowerCase();if(!k.equals(n)){Object v=values.get(k);values.remove(k);values.put(n,v);}});}
    public static final void upperCaseKey(Map<String,Object> values){new HashSet<String>(values.keySet()).forEach(k->{String n=k.toUpperCase();if(!k.equals(n)){Object v=values.get(k);values.remove(k);values.put(n,v);}});}
    public static final <K,V> void putIfNotBlank(Map<K,V> values,K key,V value){if(value!=null&&!isBlank(value.toString()))values.put(key,value);}
    private static boolean isBlank(String value){if(value==null||value.length()==0)return true;for(int i=0;i<value.length();i++)if(!Character.isWhitespace(value.charAt(i)))return false;return true;}
    public static final <T> void sort(List<T> values,Comparator<T> comparator){int size=values.size();if(size<=1)return;List<T> sorted=new ArrayList<T>();while(!values.isEmpty()){int currentSize=values.size();boolean found=true;for(int i=0;i<currentSize;i++){T candidate=values.get(i);found=true;for(int j=0;j<currentSize;j++)if(i!=j&&comparator.compare(candidate,values.get(j))>0){found=false;break;}if(found){sorted.add(candidate);values.remove(i);break;}}if(!found)throw new UnexpectedException("GIKAM.EXCEPTION.ORDER_INFINITE_LOOP");}values.addAll(sorted);}
    public static final <T> List<T> emptyList(){return new ArrayList<T>();}
    public static final <K,V> Map<K,V> emptyMap(){return new HashMap<K,V>();}
}
