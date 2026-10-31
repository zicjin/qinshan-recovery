package com.sunwayworld.emc.module.bp.project.project.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectOtherFeeBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcProjectOtherFeeDao;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectOtherFeeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcProjectOtherFeeServiceImpl implements EmcProjectOtherFeeService {

    /** 默认费用，采样费、车辆费、燃油费、过路费 */
    private static final Map<String, Pair<String, Integer>> DEFAULT_FEE_MAP = new HashMap<>(4);
    static {
        DEFAULT_FEE_MAP.put("1", new Pair<>("500", 2));
        DEFAULT_FEE_MAP.put("10", new Pair<>("350", 1));
        DEFAULT_FEE_MAP.put("11", new Pair<>("1.5", 1));
        DEFAULT_FEE_MAP.put("12", new Pair<>("0.5", 1));
    }

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private EmcProjectOtherFeeDao projectOtherFeeDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectOtherFeeDao getDao() {
        return projectOtherFeeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcProjectOtherFeeBean otherFee = wrapperBean.parseUnique(EmcProjectOtherFeeBean.class);
        otherFee.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(otherFee);

        return otherFee.getId();
    }

    @Override
    public void createOtherFee(EmcProjectBean project) {
        boolean sampling = Constant.YES.equals(project.getSample());
        if (!sampling) {
            return;
        }

        List<CoreCodeBean> otherFeeCodeList = coreCodeService.getDao().selectListByOneColumnValue("otherFeeType", "codeCategoryId");
        // 对于采样类型，采样人工费、车辆费、燃油费和过路费比选，分别对应1、10、11、12
        List<String> defaultTypeList = Arrays.asList("1", "10", "11", "12");
        List<EmcProjectOtherFeeBean> otherFeeList = new ArrayList<>();

        otherFeeCodeList.stream().filter(code -> defaultTypeList.contains(code.getCode())).forEach(code -> {
            EmcProjectOtherFeeBean otherFee = new EmcProjectOtherFeeBean();
            otherFee.setId(ApplicationContextHelper.getNextIdentity());
            otherFee.setProjId(project.getId());
            otherFee.setFeeType(Integer.valueOf(code.getCode()));
            otherFee.setFeeName(code.getCodeName());

            Pair<String, Integer> curPair = DEFAULT_FEE_MAP.get(code.getCode());
            otherFee.setFeeSinglePrice(curPair.getKey());
            otherFee.setFeeCount(curPair.getValue());

            double amount = Double.valueOf(otherFee.getFeeSinglePrice()) * otherFee.getFeeCount();
            String amountStr = BigDecimal.valueOf(amount).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
            otherFee.setAmount(amountStr);

            otherFee.setStatus(Constant.YES);
            otherFeeList.add(otherFee);
        });

        getDao().insert(otherFeeList);
    }
}
