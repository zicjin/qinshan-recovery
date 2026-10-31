package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.*;
import java.time.*;
import java.util.*;

/** Public mapper/filter behavior, never supplier classes on owned compile path. */
public final class SearchFilterDifferentialTest {
    private static int checks;
    private static URLClassLoader loader(File directory, File owned) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (owned != null) urls.add(owned.toURI().toURL());
        for (File jar : directory.listFiles()) if (jar.getName().endsWith(".jar")) urls.add(jar.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), SearchFilterDifferentialTest.class.getClassLoader().getParent());
    }
    private static Object invoke(Method method, Object receiver, Object... args) throws Exception {
        try { Object value=method.invoke(receiver,args); return value == null ? "null" : value.getClass().getName()+":"+value; }
        catch (InvocationTargetException failure) { return "error:"+failure.getCause().getClass().getName(); }
    }
    private static Object match(ClassLoader loader, String pattern, Object actual, Object expected) throws Exception {
        Class<?> type=loader.loadClass("com.sunwayworld.framework.mybatis.mapper.FilterParamPattern");
        Object filter=type.getConstructor(String.class,String.class,Object.class).newInstance(pattern,"X",expected);
        Map<String,Object> row=new HashMap<String,Object>(); row.put("x",actual);
        return invoke(type.getMethod("match",Map.class),filter,row);
    }
    private static void same(Object expected,Object actual,String description) {
        checks++; if(!Objects.equals(expected,actual))throw new AssertionError(description+": reference="+expected+", owned="+actual);
    }
    @SuppressWarnings({"unchecked","rawtypes"})
    public static void main(String[] args) throws Exception {
        try(URLClassLoader reference=loader(new File(args[0]),null); URLClassLoader owned=loader(new File(args[1]),new File(args[2]))) {
            Class<?> rp=reference.loadClass("com.sunwayworld.framework.mybatis.mapper.MatchPattern"), op=owned.loadClass(rp.getName());
            same(Arrays.toString(rp.getEnumConstants()),Arrays.toString(op.getEnumConstants()),"enum order");
            Object[] vals={null,""," ","abc","2,345.67","12.5",true,5L,LocalDate.of(2026,10,19),LocalDateTime.of(2026,10,19,12,34,56),"2026-10-19","2026-10-19 12:34:56","20261019",Arrays.asList(1,2),Arrays.asList("a","b"),Collections.singletonMap("a",1), LocalTime.of(3,4,5), new java.util.Date(0), new java.sql.Date(0), new java.sql.Timestamp(0), 1,"2026/1/2","2026-02-30","2026-01-02T03:04:05Z","2026-01-02 03:04:05+08:00","2026-01-02 03:04","2026-1-2","2026-01-02 03:04:05.123"};
            for(Object pattern:rp.getEnumConstants()) {
                String p=pattern.toString(); Object ours=Enum.valueOf((Class)op,p);
                same(rp.getMethod("getType").invoke(pattern).toString(),op.getMethod("getType").invoke(ours).toString(),p+" type");
                for(Object v:vals) same(invoke(rp.getMethod("parse",Object.class),pattern,v),invoke(op.getMethod("parse",Object.class),ours,v),p+" parse "+v);
                Class<?> target=(Class<?>)rp.getMethod("getType").invoke(pattern);
                Object[] targets=target==LocalDate.class?new Object[]{null,"",LocalDate.of(2026,1,1),LocalDate.of(2026,1,2),"2026-01-01"}:target==LocalDateTime.class?new Object[]{null,"",LocalDateTime.of(2026,1,1,0,0),LocalDateTime.of(2026,1,2,0,0),"2026-01-01 00:00:00"}:target==Double.class?new Object[]{null,"",1,2,3,"2"}:new Object[]{null,"","abc","ABC","zabc","abcx",1,Arrays.asList("a","b")};
                for(Object a:targets)for(Object b:targets) same(match(reference,p,a,b),match(owned,p,a,b),p+" match "+a+" / "+b);
            }
            for(String name:new String[]{null,"","sb","SB","not-a-pattern"})same(invoke(rp.getMethod("getPatternIfPossible",String.class),null,name),invoke(op.getMethod("getPatternIfPossible",String.class),null,name),"lookup "+name);
            same(observeSearch(reference),observeSearch(owned),"fluent search state");
            for(String p:new String[]{"DR","TR","NR","ISNULL","ISNOTNULL","NSC","MULTIPLE","OSB","COBM"}) {
                same(match(reference,p,"2026-01-02",Arrays.asList("2026-01-01","2026-01-03")),match(owned,p,"2026-01-02",Arrays.asList("2026-01-01","2026-01-03")),p+" list/range unsupported match");
            }
            same(observeMapFilter(reference),observeMapFilter(owned),"multi-field and empty-map operators");
            System.out.println("search filter differential: PASS ("+checks+" observations)");
        }
    }
    @SuppressWarnings("unchecked")
    private static List<String> observeMapFilter(ClassLoader loader) throws Exception {
        Class<?> type=loader.loadClass("com.sunwayworld.framework.mybatis.mapper.FilterParamPattern");
        List<String> result=new ArrayList<String>();
        for(String p:new String[]{"EQ","OR","IN","SB","NSC","DG","DOR"}) {
            Map<String,Object> fields=new LinkedHashMap<String,Object>();
            Object empty=type.getConstructor(String.class,Map.class).newInstance(p,fields);
            result.add(p+" empty="+invoke(type.getMethod("match",Map.class),empty,Collections.emptyMap()));
            fields.put("first", "2026-01-01"); fields.put("second", "2026-01-02");
            Object expression=type.getConstructor(String.class,Map.class).newInstance(p,fields);
            Map<String,Object> row=new HashMap<String,Object>(); row.put("FIRST", "2026-01-01"); row.put("second", "2026-01-03");
            result.add(p+" multi="+invoke(type.getMethod("match",Map.class),expression,row));
            result.add(p+" field="+type.getMethod("getMatchField").invoke(expression));
            type.getMethod("setMatchField",String.class).invoke(expression,"changed");
            result.add(p+" changed="+type.getMethod("getMatchField").invoke(expression));
            type.getMethod("setMatchPattern",String.class).invoke(expression,"OR");
            result.add(p+" pattern="+type.getMethod("getMatchPattern").invoke(expression));
        }
        return result;
    }
    @SuppressWarnings("unchecked")
    private static List<String> observeSearch(ClassLoader loader) throws Exception {
        Class<?> s=loader.loadClass("com.sunwayworld.framework.mybatis.mapper.SearchFilter"), p=loader.loadClass("com.sunwayworld.framework.mybatis.mapper.MatchPattern"), f=loader.loadClass("com.sunwayworld.framework.mybatis.mapper.FilterParamPattern");
        Object filter=s.getMethod("instance").invoke(null); List<String> out=new ArrayList<String>(); out.add("empty="+s.getMethod("isEmpty").invoke(filter));
        Map<String,Object> live=(Map<String,Object>)s.getMethod("getFilterMap").invoke(filter);
        Object matcher=s.getMethod("match",String.class,Object.class).invoke(filter,"org_filter","department-a");matcher.getClass().getMethod("special").invoke(matcher);
        out.add("special="+live); out.add("empty="+s.getMethod("isEmpty").invoke(filter));
        try{live.put("x",1);out.add("mutable");}catch(UnsupportedOperationException expected){out.add("unmodifiable");}
        Object m=s.getMethod("match",String[].class,Object.class).invoke(filter,new Object[]{new String[]{"EMPID","OTHER"},Arrays.asList("u1","u2")});
        Object pattern=p.getMethod("valueOf",String.class).invoke(null,"OR");m.getClass().getMethod("filter",p).invoke(m,pattern);
        List<Object> standards=(List<Object>)s.getMethod("getStdFilterList").invoke(filter);Object standard=standards.get(0); Map<String,Object> entries=(Map<String,Object>)f.getMethod("getMap").invoke(standard);
        List<String> pairs=new ArrayList<String>(); for(Map.Entry<String,Object> e:entries.entrySet())pairs.add(e.getKey()+"="+e.getValue()); Collections.sort(pairs);out.add("standard="+pairs);out.add("pattern="+f.getMethod("getMatchPattern").invoke(standard));
        return out;
    }
}
