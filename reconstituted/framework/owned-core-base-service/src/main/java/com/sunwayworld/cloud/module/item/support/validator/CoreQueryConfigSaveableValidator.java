/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.support.validator;

import com.sunwayworld.cloud.module.item.query.bean.CoreQueryConfigBean;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreQueryConfigSaveableValidator
implements DataValidator {
    @Autowired
    protected GlobalMapper globalMapper;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[1];
        CoreQueryConfigBean config = wrapper.parseUnique(CoreQueryConfigBean.class);
        if (!StringUtils.isEmpty(config.getSql())) {
            try {
                this.globalMapper.selectOne(DialectRepository.getDialect().getLimitSql(config.getSql(), 0, 0));
            }
            catch (Exception ex) {
                this.addConstraintViolation("CORE.MODULE.ITEM.QUERY-CONFIGS.TIP.INVALID_SQL");
                return false;
            }
        }
        return true;
    }
}
