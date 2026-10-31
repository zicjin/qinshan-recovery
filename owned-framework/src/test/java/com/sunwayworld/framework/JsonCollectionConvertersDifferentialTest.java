package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.time.LocalDate;
import java.util.*;

/** Observes converter public methods through independent supplier/owned loaders. */
public final class JsonCollectionConvertersDifferentialTest {
    private static int checks;
    private static final String[] NAMES = {
        "com.sunwayworld.framework.converter.collection.CollectionToStringConverter",
        "com.sunwayworld.framework.converter.collection.MapToStringConverter",
        "com.sunwayworld.framework.converter.collection.MapToJSONObjectConverter",
        "com.sunwayworld.framework.converter.json.JSONObjectToMapConverter"};
    public static final class IterableValue implements Iterable<String> {
        public Iterator<String> iterator() { return Arrays.asList("one", "two").iterator(); }
    }
    private static URLClassLoader loader(File deps, File framework) throws Exception {
        List<URL> urls=new ArrayList<URL>(); urls.add(framework.toURI().toURL());
        for(File file:deps.listFiles()) if(file.getName().endsWith(".jar")) urls.add(file.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), JsonCollectionConvertersDifferentialTest.class.getClassLoader());
    }
    private static String call(Object converter, Method method, Object... args) throws Exception {
        try { Object result=method.invoke(converter,args); return describe(result); }
        catch(InvocationTargetException failure) { return "ERROR:"+failure.getCause().getClass().getName(); }
    }
    private static String describe(Object result) {
        return result==null?"null":result.getClass().getName()+":"+String.valueOf(result);
    }
    private static void check(Object reference,Object owned,String context) {
        checks++; if(!Objects.equals(reference,owned)) throw new AssertionError(context+" reference="+reference+" owned="+owned);
    }
    private static List<String> observe(ClassLoader loader) throws Exception {
        List<String> observations=new ArrayList<String>();
        Class<?> jsonType=loader.loadClass("com.alibaba.fastjson.JSONObject");
        Method parse=jsonType.getMethod("parseObject",String.class);
        Class<?> arrayType=loader.loadClass("com.alibaba.fastjson.JSONArray");
        Map<String,Object> ordered=new LinkedHashMap<String,Object>();
        ordered.put("z",2); ordered.put("A",Arrays.asList("x","y")); ordered.put("a",null);
        Map<String,Object> nested=new LinkedHashMap<String,Object>(); nested.put("k",true);ordered.put("nested",nested);
        Map<Object,Object> oddKeys=new LinkedHashMap<Object,Object>();oddKeys.put(5,"five");oddKeys.put(null,"null-key");
        Map<String,Object> cycle=new LinkedHashMap<String,Object>();cycle.put("self",cycle);
        List<Object> collection=new ArrayList<Object>(Arrays.<Object>asList("text",3,true,null,ordered));
        Object json=parse.invoke(null,"{\"A\":1,\"a\":2,\"nested\":{\"ok\":true},\"array\":[1,2]}");
        Object orderedJson=jsonType.getConstructor(boolean.class).newInstance(true);
        ((Map<String,Object>)orderedJson).putAll(ordered);
        Object[] values={null,Collections.emptyList(),collection,new LinkedHashSet<String>(Arrays.asList("b","a")),new IterableValue(),Collections.emptyMap(),ordered,new TreeMap<String,Object>(ordered),oddKeys,cycle,json,orderedJson,new String[]{"x","y"},"{bad-json",LocalDate.of(2026,10,19)};
        for(String name:NAMES) {
            Class<?> type=loader.loadClass(name);Object converter=type.getField("INSTANCE").get(null);
            Class<?> source=(Class<?>)type.getMethod("getSourceType").invoke(converter);
            Class<?> target=(Class<?>)type.getMethod("getTargetType").invoke(converter);
            observations.add(name+" source="+source.getName()+" target="+target.getName());
            observations.add(name+" enum="+Arrays.toString(type.getEnumConstants()));
            Method bridge=type.getMethod("convert",Object.class);
            for(int i=0;i<values.length;i++) observations.add(name+" convert#"+i+"="+call(converter,bridge,values[i]));
            Method canConvert=type.getMethod("canConvert",Class.class,Class.class);
            for(Class<?> from:new Class<?>[]{null,Object.class,Map.class,LinkedHashMap.class,Iterable.class,ArrayList.class,source,jsonType,arrayType})
                for(Class<?> to:new Class<?>[]{null,Object.class,String.class,Map.class,HashMap.class,jsonType,arrayType,target})
                    observations.add(name+" can="+String.valueOf(from)+":"+String.valueOf(to)+"="+call(converter,canConvert,from,to));
            if(name.endsWith("MapToJSONObjectConverter")) {
                Object result=bridge.invoke(converter,ordered);
                observations.add("map-json alias="+(ordered==result));
                observations.add("map-json nested alias="+(nested==((Map<?,?>)result).get("nested")));
                ordered.put("mutation","after"); observations.add("map-json source mutation visible="+((Map<?,?>)result).containsKey("mutation")); ordered.remove("mutation");
            }
            if(name.endsWith("JSONObjectToMapConverter")) {
                Object result=bridge.invoke(converter,json);
                observations.add("json-map alias="+(json==result));
                observations.add("json-map nested alias="+(((Map<?,?>)json).get("nested")==((Map<?,?>)result).get("nested")));
                ((Map<String,Object>)result).put("mutation","after");observations.add("json-map result mutation visible="+((Map<?,?>)json).containsKey("mutation"));((Map<?,?>)json).remove("mutation");
            }
        }
        return observations;
    }
    public static void main(String[] args) throws Exception {
        try(URLClassLoader reference=loader(new File(args[0]),new File(args[1]));URLClassLoader owned=loader(new File(args[2]),new File(args[3]))) {
            List<String> expected=observe(reference),actual=observe(owned);
            check(expected.size(),actual.size(),"observation count");
            for(int i=0;i<expected.size();i++)check(expected.get(i),actual.get(i),"observation "+i);
            System.out.println("JSON/collection converter differential: PASS ("+checks+" observations)");
        }
    }
}
