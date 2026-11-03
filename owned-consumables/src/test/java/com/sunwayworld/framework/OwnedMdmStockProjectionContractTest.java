package com.sunwayworld.framework;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Guards the material/stock projection metadata used by all read aliases. */
public final class OwnedMdmStockProjectionContractTest {
    public static void main(String[] args) throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedMdmStockReadController.java")),
                StandardCharsets.UTF_8);
        require(source, "setMtlLabNo", "material laboratory number is preserved");
        require(source, "setMtlNo", "material number is preserved");
        require(source, "setCreatedByOrgId", "organization id is preserved");
        require(source, "setCreatedByOrgName", "organization name is preserved");
        require(source, "setFineness", "stock fineness is preserved");
        require(source, "setRemindDays", "stock reminder days are preserved");
        require(source, "materialColumnTypes", "material column metadata is declared");
        require(source, "stockColumnTypes", "stock column metadata is declared");
        require(source, "setTotalMap", "pagination totals are declared");
        System.out.println("owned MDM stock/material projection contract checks passed");
    }

    private static void require(String source, String token, String message) {
        if (!source.contains(token)) throw new AssertionError(message);
    }
}
