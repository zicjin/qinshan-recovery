package com.sunwayworld.cloud.module.script.test.service.impl;

import com.sunwayworld.cloud.module.script.computing.formula.CoreFormulaRepository;
import com.sunwayworld.cloud.module.script.result.bean.CoreResultBean;
import com.sunwayworld.cloud.module.script.result.service.CoreResultService;
import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.bean.CoreScriptSelectorDTO;
import com.sunwayworld.cloud.module.script.test.bean.CoreTestBean;
import com.sunwayworld.cloud.module.script.test.persistent.dao.CoreTestDao;
import com.sunwayworld.cloud.module.script.test.service.CoreItemService;
import com.sunwayworld.cloud.module.script.test.service.CoreTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.script.BshExecutor;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreTestServiceImpl implements CoreTestService {
   @Autowired
   private CoreTestDao coreTestDao;
   @Autowired
   private CoreItemService coreItemService;
   @Autowired
   private CoreResultService coreResultService;

   public CoreTestDao getDao() {
      return this.coreTestDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreTestBean coreTest = jsonWrapper.parseUnique(CoreTestBean.class);
      coreTest.setId(ApplicationContextHelper.getNextSequenceNumber(this.getDao().getTable()));
      this.getDao().insert(coreTest);
      return coreTest.getId();
   }

   @Override
   public Page<CoreItemBean> selectItemPaginationByTestId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreItemService.selectPaginationByFilter(SearchFilter.instance().match("TESTID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public Page<CoreItemBean> selectItemPagination(RestJsonWrapperBean wrapper) {
      return this.coreItemService.selectPagination(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertItem(Long id, RestJsonWrapperBean wrapper) {
      List<CoreItemBean> coreItemList = wrapper.parse(CoreItemBean.class);
      coreItemList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTestId(id);
      });
      this.coreItemService.getDao().insert(coreItemList);
      return coreItemList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteItem(Long id, RestJsonWrapperBean wrapper) {
      this.coreItemService.delete(wrapper);
   }

   @Override
   public CoreItemBean selectItemDetail(Long itemId) {
      return this.coreItemService.selectDetail(itemId);
   }

   @Override
   public String testItemScript(Long itemId, RestJsonWrapperBean wrapper) {
      List<CoreScriptSelectorDTO> selectorDTOList = wrapper.parse(CoreScriptSelectorDTO.class);
      String script = wrapper.getParamValue("script");

      for (CoreScriptSelectorDTO selectorDTO : selectorDTOList) {
         script = script.replace(selectorDTO.getSelector(), selectorDTO.getValue() + "");
      }

      for (Method method : ReflectionUtils.getMethodList(CoreFormulaRepository.class, 1, 8)) {
         Pattern pattern = Pattern.compile(method.getName() + "\\(\\S+\\)");
         String oldScript = script;
         Matcher matcher = pattern.matcher(script);

         while (matcher.find()) {
            int end = matcher.end();
            String matchValue = oldScript.substring(matcher.start(), end);
            String replacedValue = matchValue.replaceFirst(method.getName() + "\\s*\\(", method.getName() + "(new Double\\[\\]{");
            replacedValue = StringUtils.replaceLast(replacedValue, ")", "})");
            script = StringUtils.replace(script, matchValue, replacedValue);
         }
      }

      BshExecutor bsh = BshExecutor.instance();
      bsh.eval("import static com.sunwayworld.cloud.gikam.script.service.test.impl.CoreScriptTestRepository.*;");

      try {
         Object resultValue = bsh.eval(script);
         return !ObjectUtils.isEmpty(resultValue) ? ObjectUtils.toString(resultValue) : null;
      } catch (Exception var14) {
         throw new CheckedException("CORE.MODULE.SCRIPT.TESTS.TIP.SCRIPT_ERROR");
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public void generateResult(RestJsonWrapperBean wrapper) {
      List<Long> resultIdList = this.coreResultService.selectAll().stream().map(e -> e.getId()).collect(Collectors.toList());
      this.coreResultService.getDao().deleteByIdList(resultIdList);
      List<CoreTestBean> testList = this.selectAll();
      List<CoreItemBean> itemList = this.coreItemService.selectListByFilter(SearchFilter.instance(), new Order[0]);
      List<CoreResultBean> insertResultList = new ArrayList<>();

      for (CoreTestBean test : testList) {
         for (CoreItemBean testItem : itemList.stream().filter(e -> e.getTestId() == test.getId()).collect(Collectors.toList())) {
            for (Long index = 1L; index <= testItem.getRepeatQty(); index = index + 1L) {
               CoreResultBean result = new CoreResultBean();
               result.setId(ApplicationContextHelper.getNextIdentity());
               result.setTestId(test.getId());
               result.setTestName(test.getTestName());
               result.setItemId(testItem.getId());
               result.setItemName(testItem.getItemName());
               result.setRepeatNo(index);
               insertResultList.add(result);
            }
         }
      }

      this.coreResultService.getDao().insert(insertResultList);
   }
}
