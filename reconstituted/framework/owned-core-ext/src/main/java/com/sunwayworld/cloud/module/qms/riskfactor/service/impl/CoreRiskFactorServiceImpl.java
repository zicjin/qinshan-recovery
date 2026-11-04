/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.service.impl;

import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorBean;
import com.sunwayworld.cloud.module.qms.riskfactor.bean.CoreRiskFactorDTO;
import com.sunwayworld.cloud.module.qms.riskfactor.persistent.dao.CoreRiskFactorDao;
import com.sunwayworld.cloud.module.qms.riskfactor.service.CoreRiskFactorService;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaFactorService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.id.GikamIdHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRiskFactorServiceImpl
implements CoreRiskFactorService {
    @Autowired
    private CoreRiskFactorDao coreRiskFactorDao;
    @Autowired
    @Lazy
    private CoreRiskIdentificaFactorService riskIdentificaFactorService;

    @Override
    public CoreRiskFactorDao getDao() {
        return this.coreRiskFactorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreRiskFactorBean riskFactor = wrapper.parseUnique(CoreRiskFactorBean.class);
        if (riskFactor.getParentId() == null) {
            riskFactor.setDataLevel(1L);
        } else {
            CoreRiskFactorBean parent = (CoreRiskFactorBean)this.selectById(riskFactor.getParentId());
            riskFactor.setDataLevel(parent.getDataLevel() + 1L);
        }
        riskFactor.setId(GikamIdHelper.nextId());
        this.getDao().insert(riskFactor);
        return riskFactor.getId();
    }

    @Override
    public Page<CoreRiskFactorDTO> selectTreeList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List riskFactorList = this.selectList(parameter);
        TreeDescriptor descriptor = new TreeDescriptor("id", "parentId", "riskFactor");
        List<CoreRiskFactorDTO> treeNodeList = TreeHelper.parseTreeNode(riskFactorList, descriptor, CoreRiskFactorDTO.class);
        return new Pagination<CoreRiskFactorDTO>(treeNodeList);
    }

    @Override
    public List<CoreRiskFactorBean> selectSelectableList() {
        MapperParameter parameter = new MapperParameter();
        parameter.setOrderParam("DATALEVEL", "asc");
        parameter.setOrderParam("ID", "asc");
        Map<String, String> requestParamMap = ServletUtils.getCurrentRequestParamMap();
        if (!requestParamMap.isEmpty()) {
            SearchFilter filter = SearchFilter.instance();
            requestParamMap.forEach((k, v) -> {
                if (StringUtils.startsWith(k, "filter_")) {
                    parameter.put(k, v);
                } else {
                    MatchPattern match = Arrays.stream(MatchPattern.values()).filter(m -> StringUtils.endsWith(k, "_" + m.name())).findAny().orElse(null);
                    if (match != null) {
                        filter.match(StringUtils.removeEnd(k, "_" + match.name()), v).filter(match);
                    } else {
                        filter.match((String)k, v).filter(MatchPattern.EQ);
                    }
                }
            });
            parameter.setFilter(filter);
        }
        return this.getDao().selectByCondition(parameter).stream().map(m -> (CoreRiskFactorBean)PersistableHelper.mapToPersistable(m, this.getDao().getType())).collect(Collectors.toList());
    }
}
