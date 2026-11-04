/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.validator;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeMenuBean;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeMenuService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LcdpBizProcessActivateValidator
implements DataValidator {
    @Autowired
    private GlobalMapper globalMapper;
    @Autowired
    private LcdpBizProcessNodeMenuService lcdpBizProcessNodeMenuService;
    private static final Logger logger = LogManager.getLogger(LcdpBizProcessActivateValidator.class);

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        LcdpBizProcessBean bp = wrapper.parseUnique(LcdpBizProcessBean.class);
        List<LcdpBizProcessNodeMenuBean> bpNodeMenuList = this.lcdpBizProcessNodeMenuService.selectListByFilter(SearchFilter.instance().match("BPID", bp.getId()).filter(MatchPattern.EQ), new Order[0]);
        try {
            bpNodeMenuList.forEach(e -> {
                if (!StringUtils.isEmpty(e.getTodoScript())) {
                    NumberUtils.parseInt(this.globalMapper.selectOneString(e.getTodoScript()));
                }
            });
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), (Throwable)ex);
            this.addConstraintViolation("LCDP.MODULE.BPS.TIP.TODO_SCRIPT_FAILURE");
            return false;
        }
        return true;
    }
}
