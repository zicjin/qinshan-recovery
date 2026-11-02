package com.sunwayworld.framework;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Static guard for the source-owned material-use-record read family. */
public final class OwnedMtlUseRecordQueryControllerContractTest {
    public static void main(String[] args) throws Exception {
        java.nio.file.Path path = Files.exists(Paths.get("src/main/java/com/sunwayworld/owned/consumables/OwnedMtlUseRecordReadController.java"))
                ? Paths.get("src/main/java/com/sunwayworld/owned/consumables/OwnedMtlUseRecordReadController.java")
                : Paths.get("owned-consumables/src/main/java/com/sunwayworld/owned/consumables/OwnedMtlUseRecordReadController.java");
        String source = new String(Files.readAllBytes(path),
                StandardCharsets.UTF_8);
        check(source.contains("/secure/emc/module/mdm/basemdm/mtl-use-records/queries"), "query route");
        check(source.contains("/queries/raw") && source.contains("/queries/searchable"), "query aliases");
        check(source.contains("/{id}/use-record/queries"), "apply relation route");
        check(source.contains("/info/status") && source.contains("SELECT T.* FROM T_EMC_MTL_USE_RECORD"), "status and real table");
        check(source.contains("T.CREATEDBYORGID") && source.contains("OwnedIdentityProvider.CONSUMABLE_READ"), "organization and permission scope");
        check(source.contains("setColumnTypeMap") && source.contains("setTotalMap"), "pagination metadata");
        System.out.println("owned material-use-record query controller contract checks passed");
    }
    private static void check(boolean value, String label) { if (!value) throw new AssertionError(label); }
}
