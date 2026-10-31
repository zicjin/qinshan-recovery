package com.sunwayworld.framework;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Parentless differential observations for ListChunkIterator. */
public final class ListChunkIteratorDifferentialTest {
    private static final class FailingList extends ArrayList<Integer> {
        private boolean fail = true;
        FailingList() { super(Arrays.asList(31,32,33)); }
        @Override public Integer get(int index) {
            if (index == 1 && fail) { fail = false; throw new IllegalStateException("synthetic list read failure"); }
            return super.get(index);
        }
    }
    private static URLClassLoader loader(File artifact, File owned, File deps) throws Exception {
        List<URL> urls = new ArrayList<URL>();
        if (artifact != null) urls.add(artifact.toURI().toURL());
        if (owned != null) urls.add(owned.toURI().toURL());
        if (deps != null) for (File f : deps.listFiles()) if (f.getName().endsWith(".jar")) urls.add(f.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), null);
    }
    private static String describe(Throwable t) {
        String s = t.getClass().getName() + ":" + String.valueOf(t.getMessage());
        return s + (t.getCause() == null ? "" : ":cause=" + describe(t.getCause()));
    }
    private static String invoke(Method m, Object receiver, Object... args) throws Exception {
        try { Object v=m.invoke(receiver,args); return v == null ? "null" : v.getClass().getName()+":"+v; }
        catch (InvocationTargetException e) { return "!"+describe(e.getCause()); }
    }
    private static List<String> observe(ClassLoader cl) throws Exception {
        Class<?> type=cl.loadClass("com.sunwayworld.framework.data.ListChunkIterator");
        Method of=type.getMethod("of",List.class,int.class), has=type.getMethod("hasNext"), next=type.getMethod("nextChunk"), size=type.getMethod("getChunkSize");
        List<String> out=new ArrayList<String>();
        for (List<Integer> input : Arrays.asList(null, new ArrayList<Integer>(), Arrays.asList(1), Arrays.asList(1,2,3,4,5))) {
            for (int chunk : new int[]{Integer.MIN_VALUE,-1,0,1,2,3,10,Integer.MAX_VALUE}) {
                Object it;
                try { it=of.invoke(null,input,chunk); out.add("create="+input+":"+chunk); }
                catch (InvocationTargetException e) { out.add("create=!"+describe(e.getCause())); continue; }
                out.add("size="+invoke(size,it));
                out.add("has="+invoke(has,it));
                for (int pass=0; pass<8; pass++) { out.add("next="+invoke(next,it)); out.add("has="+invoke(has,it)); }
            }
        }
        List<Integer> mutable=new ArrayList<Integer>(Arrays.asList(7,8,9));
        Object it=of.invoke(null,mutable,2); mutable.set(0,70); mutable.add(10);
        out.add("mutation="+invoke(next,it)); out.add("mutation="+invoke(next,it)); out.add("mutation-has="+invoke(has,it));
        List<Integer> independent=new ArrayList<Integer>(Arrays.asList(11,12,13));
        Object independentIterator=of.invoke(null,independent,2);
        Object firstChunk=next.invoke(independentIterator);
        ((List<Integer>) firstChunk).set(0,99);
        out.add("returned-independent="+independent.get(0)+":"+((List<Integer>) firstChunk).get(0));
        List<Integer> shrinking=new ArrayList<Integer>(Arrays.asList(21,22,23));
        Object shrinkingIterator=of.invoke(null,shrinking,2);
        shrinking.clear();
        out.add("shrinking-next="+invoke(next,shrinkingIterator));
        shrinking.addAll(Arrays.asList(24,25,26));
        out.add("restored-retry="+invoke(next,shrinkingIterator));
        out.add("restored-tail="+invoke(next,shrinkingIterator));
        Object failingIterator=of.invoke(null,new FailingList(),2);
        out.add("failing-next="+invoke(next,failingIterator));
        out.add("failing-has="+invoke(has,failingIterator));
        out.add("failing-retry="+invoke(next,failingIterator));
        out.add("failing-tail="+invoke(next,failingIterator));
        return out;
    }
    public static void main(String[] args) throws Exception {
        try (URLClassLoader supplier=loader(new File(args[0]),null,new File(args[2])); URLClassLoader owned=loader(null,new File(args[1]),new File(args[2]))) {
            for (String name : new String[]{"data.ChunkIterator","data.ListChunkIterator","exception.InvalidDataException"}) {
                Class<?> type=owned.loadClass("com.sunwayworld.framework."+name);
                if (!type.getProtectionDomain().getCodeSource().getLocation().equals(new File(args[1]).toURI().toURL()))
                    throw new AssertionError("owned provenance "+name);
            }
            List<String> expected=observe(supplier), actual=observe(owned);
            if (!expected.equals(actual)) { for(int i=0;i<Math.min(expected.size(),actual.size());i++) if(!expected.get(i).equals(actual.get(i))) throw new AssertionError("observation "+i+" supplier="+expected.get(i)+" owned="+actual.get(i)); throw new AssertionError("observation size supplier="+expected.size()+" owned="+actual.size()); }
            System.out.println("ListChunkIterator differential: PASS ("+expected.size()+" observations; mutation and exhaustion covered)");
        }
    }
}
