package com.sunwayworld.contractscan;
public class ScanFixture {
    public interface Service { }
    public abstract static class AbstractService implements Service { }
    public static class Concrete extends AbstractService { }
    public static class Lazy { static { System.setProperty("classutils.fixture.initialized","yes"); } }
}
