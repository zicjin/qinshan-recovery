/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.service.impl;

import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.metadata.bean.CoreValidatorBean;
import com.sunwayworld.module.sys.metadata.dao.CoreValidatorDao;
import com.sunwayworld.module.sys.metadata.service.CoreValidatorService;
import com.sunwayworld.module.sys.metadata.support.ValidatorType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreValidatorServiceImpl
implements CoreValidatorService {
    @Autowired
    private CoreValidatorDao validatorDao;

    @Override
    public List<CoreValidatorBean> selectEnabledValidatorByTable(String table) {
        return this.validatorDao.selectEnabledValidatorByTable(table);
    }

    @Override
    public <T extends Persistable<?>> List<String> validate(T item) {
        if (item == null) {
            return CollectionUtils.emptyList();
        }
        String table = PersistableMetadataHelper.getTableName(item.getClass());
        List<CoreValidatorBean> validatorList = this.selectEnabledValidatorByTable(table);
        if (validatorList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return validatorList.stream().map(v -> this.validate((CoreValidatorBean)v, item)).filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList());
    }

    private <T extends Persistable<?>> String validate(CoreValidatorBean validator, T item) {
        if (validator.getTypeName().equalsIgnoreCase(ValidatorType.STRING.name())) {
            String value = ConvertUtils.convert(PersistableHelper.getPropertyValue(item, validator.getColumnName()), String.class);
            if (!(value != null || Integer.valueOf(1).equals(validator.getNullable()) && validator.getMinSize() <= 0)) {
                return validator.getMessage();
            }
            if (value != null && (value.length() < validator.getMinSize() || value.length() > validator.getMaxSize())) {
                return validator.getMessage();
            }
        } else if (validator.getTypeName().equalsIgnoreCase(ValidatorType.NUMBER.name())) {
            Double value = ConvertUtils.convert(PersistableHelper.getPropertyValue(item, validator.getColumnName()), Double.class);
            if (value == null && !Integer.valueOf(1).equals(validator.getNullable())) {
                return validator.getMessage();
            }
            if (value != null && (value < (double)validator.getMin().intValue() || value > (double)validator.getMax().intValue())) {
                return validator.getMessage();
            }
        } else if (validator.getTypeName().equalsIgnoreCase(ValidatorType.DATE.name())) {
            LocalDateTime value = ConvertUtils.convert(PersistableHelper.getPropertyValue(item, validator.getColumnName()), LocalDateTime.class);
            if (value == null && Objects.equals(1, validator.getNullable())) {
                return validator.getMessage();
            }
            if (value != null) {
                String past;
                String future = validator.getFuture();
                if (!StringUtils.isEmpty(future)) {
                    LocalDateTime futureValue;
                    if ("now".equals(future) && value.isAfter(LocalDateTime.now())) {
                        return validator.getMessage();
                    }
                    if (!"now".equals(future) && (futureValue = ConvertUtils.convert(PersistableHelper.getPropertyValue(item, future), LocalDateTime.class)) != null && value.isAfter(futureValue)) {
                        return validator.getMessage();
                    }
                }
                if (!StringUtils.isEmpty(past = validator.getPast())) {
                    LocalDateTime pastValue;
                    if ("now".equals(past) && value.isBefore(LocalDateTime.now())) {
                        return validator.getMessage();
                    }
                    if (!"now".equals(past) && (pastValue = ConvertUtils.convert(PersistableHelper.getPropertyValue(item, past), LocalDateTime.class)) != null && value.isBefore(pastValue)) {
                        return validator.getMessage();
                    }
                }
            }
        } else {
            throw new UnsupportedException("Type name [" + validator.getTypeName() + "] is not supported!");
        }
        return null;
    }
}
