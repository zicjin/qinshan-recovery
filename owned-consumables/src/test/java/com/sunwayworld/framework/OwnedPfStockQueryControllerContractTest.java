package com.sunwayworld.framework;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Static guard for the PF stock aliases backed by the MDM stock reader. */
public final class OwnedPfStockQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        Path path = Files.exists(Paths.get("src/main/java/com/sunwayworld/owned/consumables/OwnedPfStockReadController.java"))
                ? Paths.get("src/main/java/com/sunwayworld/owned/consumables/OwnedPfStockReadController.java")
                : Paths.get("owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedPfStockReadController.java");
        String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        check(source.contains("/secure/emc/module/mdm/basemdm/stock/queries"), "stock query alias");
        check(source.contains("/queries/raw") && source.contains("/queries/searchable"), "stock query variants");
        check(source.contains("/stock/info/status"), "stock status alias");
        check(source.contains("delegate.stocks(\"0\""), "shared MDM stock behavior");
        System.out.println("owned PF stock query controller contract checks passed");
    }
    private static void check(boolean value, String label) { if (!value) throw new AssertionError(label); }
}
