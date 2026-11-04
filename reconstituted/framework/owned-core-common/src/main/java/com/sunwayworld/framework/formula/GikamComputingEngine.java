package com.sunwayworld.framework.formula;

import com.sunwayworld.framework.exception.FormulaException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.script.BshExecutor;
import java.util.Map;
import java.util.Set;

public interface GikamComputingEngine {
   default void compute() {
      try {
         this.setDependencyCheck(false);
         this.initData();
         BshExecutor bsh = this.getBshExecutor();
         this.setDependencyCheck(true);
         this.run(bsh);
         this.setDependencyCheck(false);
         this.testDeadlock();
         this.sortData();
         this.run(bsh);
         this.processData();
      } finally {
         this.cleanData();
      }
   }

   default void setDependencyCheck(boolean dependencyCheck) {
      GikamComputingDataHolder.put("DEPENDENCY_CHECK", dependencyCheck);
   }

   default boolean isDependencyCheck() {
      Boolean dependencyCheck = GikamComputingDataHolder.get("DEPENDENCY_CHECK");
      return dependencyCheck == null ? false : dependencyCheck;
   }

   default void testDeadlock() {
      Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();
      if (map != null) {
         for (Long resultId : map.keySet()) {
            if (this.containsLeaf(resultId, resultId)) {
               throw new FormulaException(I18nHelper.getMessage("GIKAM.EXCEPTION.DEADLOCK") + ":" + resultId);
            }
         }
      }
   }

   default void cleanData() {
      GikamComputingDataHolder.clean();
   }

   default boolean containsLeaf(Long id, Long testId) {
      Map<Long, Set<Long>> map = GikamComputingDataHolder.getRelevantData();
      if (!map.containsKey(id)) {
         return false;
      } else {
         for (Long relevantId : map.get(id)) {
            if (testId.equals(relevantId)) {
               return true;
            }

            if (this.containsLeaf(relevantId, testId)) {
               return true;
            }
         }

         return false;
      }
   }

   BshExecutor getBshExecutor();

   void processData();

   void run(BshExecutor bsh);

   void sortData();

   void initData();
}
