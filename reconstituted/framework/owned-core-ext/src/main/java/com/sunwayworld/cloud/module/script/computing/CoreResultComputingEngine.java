package com.sunwayworld.cloud.module.script.computing;

import com.sunwayworld.cloud.module.script.computing.formula.CoreFormulaRepository;
import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.cloud.module.script.result.service.CoreResultService;
import com.sunwayworld.framework.beans.BeanPropertyEvent;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.formula.GikamComputingDataHolder;
import com.sunwayworld.framework.formula.GikamComputingEngine;
import com.sunwayworld.framework.formula.GikamFormulaRegistry;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoreResultComputingEngine implements GikamComputingEngine {
   private static final String DEPENDENCY_RESULT_LIST = "DEPENDENCY_RESULT_LIST";
   private static final String COMPUTE_RESULT_LIST = "COMPUTE_RESULT_LIST";
   private static final Logger logger = LogManager.getLogger("");
   private List<Long> resultIdList;

   private CoreResultComputingEngine() {
   }

   public static CoreResultComputingEngine getEngine(List<Long> resultIdList) {
      CoreResultComputingEngine engine = new CoreResultComputingEngine();
      engine.resultIdList = resultIdList;
      return engine;
   }

   @Override
   public BshExecutor getBshExecutor() {
      BshExecutor bsh = BshExecutor.instance();
      bsh.eval("import com.sunwayworld.framework.utils.StringUtils;");
      bsh.eval("import com.sunwayworld.framework.utils.ArithUtils;");
      bsh.eval("import com.sunwayworld.framework.utils.NumberUtils;");
      bsh.eval("import com.sunwayworld.cloud.gikam.script.bean.result.*;");
      bsh.eval("import java.text.*;");
      GikamFormulaRegistry.INSTANCE.getRepository().forEach(c -> bsh.eval("import static " + c.getName() + ".*;"));
      return bsh;
   }

   @Override
   public void sortData() {
      List<CoreResultBean> resultList = getComputeResultList();
      CollectionUtils.sort(resultList, this.getComparator());
   }

   @Override
   public void processData() {
      CoreResultService resultService = ApplicationContextHelper.getBean(CoreResultService.class);
      List<CoreResultBean> updateTaskResultList = new ArrayList<>();
      Set<String> updatedColumnList = new HashSet<>();

      for (CoreResultBean result : getComputeResultList()) {
         BeanPropertyListener proxy = (BeanPropertyListener)result;
         List<BeanPropertyEvent> propertyEventList = proxy.getChangedPropertyEventList().stream().collect(Collectors.toList());
         if (!propertyEventList.isEmpty()) {
            updateTaskResultList.add(result);
            propertyEventList.forEach(p -> updatedColumnList.add(p.getPropertyDescriptor().getName().toUpperCase()));
         }
      }

      if (!updateTaskResultList.isEmpty() && !updatedColumnList.isEmpty()) {
         resultService.getDao().update(updateTaskResultList, updatedColumnList.toArray(new String[0]));
      }
   }

   @Override
   public void run(BshExecutor bsh) {
      boolean dependencyCheck = this.isDependencyCheck();

      for (CoreResultBean result : getComputeResultList()) {
         try {
            GikamComputingDataHolder.setCurrentData(result);
            String script = this.replaceFuncBracket(result.getExt$Item("script"));
            if (!StringUtils.isEmpty(script)) {
               Object resultValue = bsh.eval(
                  script, I18nHelper.getMessage("CORE.MODULE.SCRIPT.RESULTS.TIP.BAD_TEST_SCRIPT", result.getTestName(), result.getItemName())
               );
               String finalResultValue = ObjectUtils.toString(resultValue);
               if (!dependencyCheck) {
                  result.setOriginalResult(finalResultValue);
               }
            }
         } catch (Exception var12) {
            if (!dependencyCheck) {
               result.setOriginalResult(null);
            }

            if (RuntimeException.class.isAssignableFrom(var12.getClass())) {
               throw var12;
            }

            logger.error(var12.getMessage(), (Throwable)var12);
         } finally {
            GikamComputingDataHolder.removeCurrentData();
         }
      }
   }

   @Override
   public void initData() {
      if (!this.resultIdList.isEmpty()) {
         CoreResultService resultService = ApplicationContextHelper.getBean(CoreResultService.class);
         List<CoreResultBean> resultList = resultService.selectListByFilter(
            SearchFilter.instance().match("ID", this.resultIdList).filter(MatchPattern.OR), new Order[0]
         );
         List<CoreResultBean> computeResultList = new ArrayList<>();
         computeResultList.addAll(resultList);
         List<String> itemIdList = resultList.stream().map(r -> "" + r.getItemId()).collect(Collectors.toList());
         CoreFormulaRequiredService requiredService = ApplicationContextHelper.getBean(CoreFormulaRequiredService.class);
         List<CoreFormulaRequiredBean> formulaRequiredList = new ArrayList<>();
         this.setupFormulaRequiredList(requiredService, formulaRequiredList, itemIdList);
         if (!formulaRequiredList.isEmpty()) {
            List<Long> relatedItemIdList = formulaRequiredList.stream().map(r -> NumberUtils.parseLong(r.getItemId())).collect(Collectors.toList());
            List<Long> allRelatedItemIdList = requiredService.selectListByFilter(
                  SearchFilter.instance().match("ITEMID", relatedItemIdList).filter(MatchPattern.OR), new Order[0]
               )
               .stream()
               .map(e -> Long.valueOf(e.getRequiredItemId()))
               .collect(Collectors.toList());
            allRelatedItemIdList.addAll(relatedItemIdList);
            List<CoreResultBean> allRelatedResultList = this.getAllResultList(resultList, allRelatedItemIdList);
            List<CoreResultBean> relatedTaskResultList = allRelatedResultList.stream()
               .filter(e -> relatedItemIdList.contains(e.getItemId()))
               .collect(Collectors.toList());

            for (CoreResultBean result : allRelatedResultList) {
               if (resultList.stream().noneMatch(r -> r.getId().equals(result.getId()))) {
                  resultList.add(result);
               }
            }

            for (CoreResultBean resultx : relatedTaskResultList) {
               if (computeResultList.stream().noneMatch(r -> r.getId().equals(resultx.getId()))) {
                  computeResultList.add(resultx);
               }
            }
         }

         List<CoreResultBean> computeResultProxyList = ObjectUtils.clone(computeResultList)
            .stream()
            .map(r -> BeanUtils.getPropertyListenerProxy((CoreResultBean)r))
            .collect(Collectors.toList());
         GikamComputingDataHolder.put("TESTABLE_COMPUTE_RESULT_LIST", computeResultList);
         GikamComputingDataHolder.put("COMPUTE_RESULT_LIST", computeResultProxyList);
         List<CoreResultBean> copyList = new ArrayList<>(computeResultList);
         List<CoreResultBean> copyProxyList = new ArrayList<>(computeResultProxyList);

         for (CoreResultBean resultxx : resultList) {
            if (copyList.stream().noneMatch(r -> r.getId().equals(resultxx.getId()))) {
               copyList.add(resultxx);
               copyProxyList.add(BeanUtils.getPropertyListenerProxy(ObjectUtils.clone(resultxx)));
            }
         }

         GikamComputingDataHolder.put("TESTABLE_DEPENDENCY_RESULT_LIST", copyList);
         GikamComputingDataHolder.put("DEPENDENCY_RESULT_LIST", copyProxyList);
      }
   }

   private List<CoreResultBean> getAllResultList(List<CoreResultBean> resultList, List<Long> allRelatedItemIdList) {
      CoreResultService resultService = ApplicationContextHelper.getBean(CoreResultService.class);
      return resultService.selectListByFilter(SearchFilter.instance().match("ITEMID", allRelatedItemIdList).filter(MatchPattern.OR), new Order[0]);
   }

   @Override
   public void testDeadlock() {
      Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();
      if (map != null) {
         for (Long resultId : map.keySet()) {
            if (this.containsLeaf(resultId, resultId)) {
               List<CoreResultBean> resultList = getComputeResultList();
               CoreResultBean deadLockResult = resultList.stream().filter(e -> ObjectUtils.equals(resultId, e.getId())).findFirst().get();
               throw new FormulaException(
                  I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK")
                     + ":"
                     + I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK.TESTNAME")
                     + deadLockResult.getTestName()
                     + ","
                     + I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK.ITEMNAME")
                     + deadLockResult.getItemName()
               );
            }
         }
      }
   }

   public static List<CoreResultBean> getComputeResultList() {
      return GikamComputingDataHolder.get("COMPUTE_RESULT_LIST");
   }

   public static List<CoreResultBean> getDependencyResultList() {
      return GikamComputingDataHolder.get("DEPENDENCY_RESULT_LIST");
   }

   private Comparator<CoreResultBean> getComparator() {
      Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();
      return (v1, v2) -> {
         Set<Long> set1 = map.get(v1.getId());
         if (set1 != null && set1.contains(v2.getId())) {
            return 1;
         } else {
            Set<Long> set2 = map.get(v2.getId());
            return set2 != null && set2.contains(v1.getId()) ? -1 : 0;
         }
      };
   }

   private String replaceFuncBracket(String script) {
      if (StringUtils.isEmpty(script)) {
         return script;
      } else {
         for (Method method : ReflectionUtils.getMethodList(CoreFormulaRepository.class, 1, 8)) {
            Pattern pattern = Pattern.compile(method.getName() + "\\(\\S+\\s*\\S+\\)");
            String oldScript = script;
            Matcher matcher = pattern.matcher(script);

            while (matcher.find()) {
               int end = matcher.end();
               String matchValue = oldScript.substring(matcher.start(), end);
               String replacedValue = matchValue.replaceAll("\\[", "\"").replaceAll("\\]", "\"");
               replacedValue = replacedValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(new String\\[\\]{");
               replacedValue = StringUtils.replaceLast(replacedValue, ")", "})");
               script = StringUtils.replace(script, matchValue, replacedValue);
            }
         }

         Pattern pattern = Pattern.compile("\\[[^\\[]*\\]");
         String oldScript = script;
         Matcher matcher = pattern.matcher(script);

         while (matcher.find()) {
            int end = matcher.end();
            String matchValue = oldScript.substring(matcher.start(), end);
            if (matchValue.length() != 2) {
               String methodName = matchValue.startsWith("[Object:") ? "getObject" : "get";
               script = StringUtils.replace(script, matchValue, methodName + "(" + matchValue.replaceAll("\\[", "\"").replaceAll("\\]", "\"") + ")");
            }
         }

         return script;
      }
   }

   private void setupFormulaRequiredList(CoreFormulaRequiredService requiredService, List<CoreFormulaRequiredBean> formulaRequiredList, List<String> itemIdList) {
      if (!itemIdList.isEmpty()) {
         List<CoreFormulaRequiredBean> requiredList = requiredService.selectListByFilter(
            SearchFilter.instance().match("REQUIREDITEMID", itemIdList).filter(MatchPattern.OR), new Order[]{Order.asc("ID")}
         );
         requiredList.removeIf(r -> formulaRequiredList.stream().anyMatch(r1 -> r1.getId().equals(r.getId())));
         if (!requiredList.isEmpty()) {
            formulaRequiredList.addAll(requiredList);
            List<String> nextItemIdList = requiredList.stream().map(r -> r.getItemId()).collect(Collectors.toList());
            this.setupFormulaRequiredList(requiredService, formulaRequiredList, nextItemIdList);
         }
      }
   }
}
