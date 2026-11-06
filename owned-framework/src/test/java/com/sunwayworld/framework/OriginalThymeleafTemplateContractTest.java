package com.sunwayworld.framework;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public final class OriginalThymeleafTemplateContractTest {
 public static void main(String[] args) throws Exception {
  Path root=Paths.get(args.length==0?".":args[0]);
  String factories=new String(Files.readAllBytes(root.resolve("owned-framework/src/main/resources/META-INF/spring.factories")),StandardCharsets.UTF_8);
  if(factories.contains("OwnedTemplateViewConfiguration")) throw new AssertionError("regex template facade remains registered");
  String home=new String(Files.readAllBytes(root.resolve("reconstituted/framework/owned-core-web/src/main/resources/templates/module/gikam/home.html")),StandardCharsets.UTF_8);
  if(!home.contains("<!DOCTYPE html>")||!home.contains("chunk-vendors.js")||!home.contains("chunk-common.js")||!home.contains("home.js")) throw new AssertionError("original Gikam shell incomplete");
  String index=new String(Files.readAllBytes(root.resolve("project/src/main/resources/templates/emc/module/index/homepage/index.html")),StandardCharsets.UTF_8);
  if(!index.contains("th:replace=\"module/gikam/home\"")) throw new AssertionError("homepage fragment missing");
  System.out.println("original Thymeleaf template contract checks passed");
 }
}
