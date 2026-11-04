/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceParamDTO;
import com.sunwayworld.module.sys.sequence.dao.CoreSequenceDao;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceLineService;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class CoreSequenceAbstractService
implements CoreSequenceService {
    public static final String DEFAULT_SEQUENCE_PATTERN = "[yyyy][mm][nnnnn]";
    public static final String DEFAULT_SEQUENCE_KEY_PATTERN = "[key][yyyy][mm][nnnnn]";
    public static final String DEFAULT_SURROGATE_SEQUENCE_PATTERN = "[nnnnnnnnnnnn]";
    public static final String DEFAULT_SURROGATE_SEQUENCE_LINE_CATEGORY = "nnn";
    @Autowired
    private CoreSequenceDao sequenceDao;
    @Autowired
    private CoreSequenceLineService sequenceLineService;

    @Override
    public CoreSequenceDao getDao() {
        return this.sequenceDao;
    }

    @Override
    public List<String> getNextSequenceList(String id, String key, int qty) {
        String jsonParam;
        GikamConcurrentLocker.block(id);
        CoreSequenceBean sequence = (CoreSequenceBean)this.getDao().selectByIdIfPresent(id);
        if (sequence == null) {
            sequence = new CoreSequenceBean();
            sequence.setId(id);
            if (!StringUtils.isEmpty(key)) {
                sequence.setPattern(DEFAULT_SEQUENCE_KEY_PATTERN);
            } else {
                sequence.setPattern(DEFAULT_SEQUENCE_PATTERN);
            }
            this.getDao().insert(sequence);
        }
        if (StringUtils.isEmpty(jsonParam = sequence.getJsonParam())) {
            jsonParam = "[]";
        }
        ArrayList<String> sequenceList = new ArrayList<String>();
        List<CoreSequenceParamDTO> paramList = JSON.parseArray(jsonParam, CoreSequenceParamDTO.class);
        if (paramList.isEmpty() || paramList.stream().noneMatch(p -> ObjectUtils.equals(p.getKey(), key))) {
            CoreSequenceParamDTO param = this.getNewSequenceParam(sequence.getPattern(), key, qty);
            paramList.add(param);
            sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
        } else {
            CoreSequenceParamDTO param = paramList.stream().filter(p -> ObjectUtils.equals(p.getKey(), key)).findFirst().get();
            if (StringUtils.contains(sequence.getPattern(), "[yyyy]") && !String.format("%04d", LocalDate.now().getYear()).equals(param.getYyyy()) || StringUtils.contains(sequence.getPattern(), "[yy]") && !String.format("%02d", LocalDate.now().getYear() % 100).equals(param.getYy()) || StringUtils.contains(sequence.getPattern(), "[mm]") && !String.format("%02d", LocalDate.now().getMonthValue()).equals(param.getMm()) || StringUtils.contains(sequence.getPattern(), "[dd]") && !String.format("%02d", LocalDate.now().getDayOfMonth()).equals(param.getDd())) {
                paramList.removeIf(p -> ObjectUtils.equals(p.getKey(), key));
                param = this.getNewSequenceParam(sequence.getPattern(), key, qty);
                paramList.add(param);
                sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
            } else {
                param.setN(NumberUtils.parseInt(param.getN()) + qty);
                sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
            }
        }
        sequence.setJsonParam(JSON.toJSONString(paramList));
        this.getDao().update(sequence, new String[]{"JSONPARAM"});
        return sequenceList;
    }

    @Override
    public List<Long> getNextIdentityList(String id, int qty) {
        String jsonParam;
        GikamConcurrentLocker.block(id);
        CoreSequenceBean sequence = (CoreSequenceBean)this.getDao().selectByIdIfPresent(id);
        if (sequence == null) {
            sequence = new CoreSequenceBean();
            sequence.setId(id);
            sequence.setPattern(DEFAULT_SURROGATE_SEQUENCE_PATTERN);
            CoreSequenceLineBean coreSequenceLine = new CoreSequenceLineBean();
            coreSequenceLine.setId(ApplicationContextHelper.getNextIdentity());
            coreSequenceLine.setSequenceId(id);
            coreSequenceLine.setSequenceCodeCategory(DEFAULT_SURROGATE_SEQUENCE_LINE_CATEGORY);
            coreSequenceLine.setSequenceCode(StringUtils.substringsBetween(DEFAULT_SURROGATE_SEQUENCE_PATTERN, "[", "]").get(0));
            this.getDao().insert(sequence);
            this.sequenceLineService.getDao().insert((CoreSequenceLineBean)coreSequenceLine);
        }
        if (StringUtils.isEmpty(jsonParam = sequence.getJsonParam())) {
            jsonParam = "[]";
        }
        ArrayList<String> sequenceList = new ArrayList<String>();
        List<CoreSequenceParamDTO> paramList = JSON.parseArray(jsonParam, CoreSequenceParamDTO.class);
        if (paramList.isEmpty()) {
            CoreSequenceParamDTO param = this.getNewSequenceParam(sequence.getPattern(), "", qty);
            paramList.add(param);
            sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
        } else {
            CoreSequenceParamDTO param = paramList.get(0);
            if (StringUtils.contains(sequence.getPattern(), "[yyyy]") && !String.format("%04d", LocalDate.now().getYear()).equals(param.getYyyy()) || StringUtils.contains(sequence.getPattern(), "[yy]") && !String.format("%02d", LocalDate.now().getYear() % 100).equals(param.getYy()) || StringUtils.contains(sequence.getPattern(), "[mm]") && !String.format("%02d", LocalDate.now().getMonthValue()).equals(param.getMm()) || StringUtils.contains(sequence.getPattern(), "[dd]") && !String.format("%02d", LocalDate.now().getDayOfMonth()).equals(param.getDd())) {
                paramList.remove(0);
                param = this.getNewSequenceParam(sequence.getPattern(), "", qty);
                paramList.add(param);
                sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
            } else {
                param.setN(NumberUtils.parseInt(param.getN()) + qty);
                sequenceList.addAll(this.parseSequenceList(sequence.getPattern(), param, qty));
            }
        }
        sequence.setJsonParam(JSON.toJSONString(paramList));
        this.getDao().update(sequence, new String[]{"JSONPARAM"});
        return sequenceList.stream().map(NumberUtils::parseLong).collect(Collectors.toList());
    }

    @Override
    public List<String> getNextSequenceList(String prefix, Supplier<String> getLastSequenceSupplier, int numLength, int qty) {
        String lastSequence = getLastSequenceSupplier.get();
        int seq = 0;
        if (!StringUtils.isEmpty(lastSequence)) {
            seq = NumberUtils.parseInt(StringUtils.removeStart(lastSequence, prefix));
        }
        ArrayList<String> sequenceList = new ArrayList<String>();
        for (int i = 0; i < qty; ++i) {
            sequenceList.add(prefix + StringUtils.leftPad("" + (seq + i + 1), numLength));
        }
        return sequenceList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreSequenceBean sequence = wrapper.parseUnique(CoreSequenceBean.class);
        this.getDao().insert(sequence);
        return sequence.getId();
    }

    @Override
    public Page<CoreSequenceLineBean> selectLinePaginationBySequenceId(String id, RestJsonWrapperBean wrapper) {
        return this.sequenceLineService.selectPaginationByFilter(SearchFilter.instance().match("SEQUENCEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertLine(String id, RestJsonWrapperBean wrapper) {
        List<CoreSequenceLineBean> CoreSequenceLineList = wrapper.parse(CoreSequenceLineBean.class);
        CoreSequenceLineList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setSequenceId(id);
        });
        this.sequenceLineService.getDao().insert(CoreSequenceLineList);
        return CoreSequenceLineList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteLine(String id, RestJsonWrapperBean wrapper) {
        this.sequenceLineService.delete(wrapper);
        this.buildSequencePattern(id);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void swapLine(String column, Long firstId, Long secondId) {
        this.sequenceLineService.swap(column, firstId, secondId);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void buildSequencePattern(String id) {
        List<CoreSequenceLineBean> sequenceLineList = this.sequenceLineService.selectListByFilter(SearchFilter.instance().match("SEQUENCEID", id).filter(MatchPattern.EQ), Order.asc("ORDERNO"));
        CoreSequenceBean sequence = new CoreSequenceBean();
        sequence.setId(id);
        if (!sequenceLineList.isEmpty()) {
            StringBuilder patternBuilder = new StringBuilder();
            sequenceLineList.forEach(e -> {
                if (!StringUtils.isEmpty(e.getSequenceCode())) {
                    if (StringUtils.equals(e.getSequenceCodeCategory(), "text")) {
                        patternBuilder.append(e.getSequenceCode());
                    } else {
                        patternBuilder.append("[").append(e.getSequenceCode()).append("]");
                    }
                }
            });
            sequence.setPattern(StringUtils.isEmpty(patternBuilder.toString()) ? null : patternBuilder.toString());
        } else {
            sequence.setPattern(null);
        }
        this.getDao().update(sequence, new String[]{"PATTERN"});
    }

    private CoreSequenceParamDTO getNewSequenceParam(String pattern, String key, int qty) {
        CoreSequenceParamDTO param = new CoreSequenceParamDTO();
        List<String> substringList = StringUtils.substringsBetween(pattern, "[", "]");
        substringList.forEach(s -> {
            if ("key".equals(s)) {
                param.setKey(key);
            } else if ("yyyy".equals(s)) {
                param.setYyyy(String.format("%04d", LocalDate.now().getYear()));
            } else if ("yy".equals(s)) {
                param.setYy(String.format("%02d", LocalDate.now().getYear() % 100));
            } else if ("mm".equals(s)) {
                param.setMm(String.format("%02d", LocalDate.now().getMonthValue()));
            } else if ("dd".equals(s)) {
                param.setDd(String.format("%02d", LocalDate.now().getDayOfMonth()));
            } else if (StringUtils.repeatOf(s, "n")) {
                param.setN(qty);
            }
        });
        return param;
    }

    private String parseSequence(String pattern, CoreSequenceParamDTO param) {
        String sequence = pattern;
        List<String> substringList = StringUtils.substringsBetween(pattern, "[", "]");
        for (String substring : substringList) {
            if ("key".equals(substring)) {
                if (!StringUtils.isEmpty(param.getKey())) {
                    sequence = StringUtils.replace(sequence, "[key]", param.getKey());
                    continue;
                }
                throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.SYS.SEQUENCES.TIP.SEQUENCE_CONFIG_ERROR_KEY_NOT_EXIST", pattern, substring));
            }
            if ("yyyy".equals(substring)) {
                if (!StringUtils.isEmpty(param.getYyyy())) {
                    sequence = StringUtils.replace(sequence, "[yyyy]", param.getYyyy());
                    continue;
                }
                throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.SYS.SEQUENCES.TIP.SEQUENCE_CONFIG_ERROR_KEY_NOT_EXIST", pattern, substring));
            }
            if ("yy".equals(substring)) {
                if (!StringUtils.isEmpty(param.getYy())) {
                    sequence = StringUtils.replace(sequence, "[yy]", param.getYy());
                    continue;
                }
                throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.SYS.SEQUENCES.TIP.SEQUENCE_CONFIG_ERROR_KEY_NOT_EXIST", pattern, substring));
            }
            if ("mm".equals(substring)) {
                if (!StringUtils.isEmpty(param.getMm())) {
                    sequence = StringUtils.replace(sequence, "[mm]", param.getMm());
                    continue;
                }
                throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.SYS.SEQUENCES.TIP.SEQUENCE_CONFIG_ERROR_KEY_NOT_EXIST", pattern, substring));
            }
            if ("dd".equals(substring)) {
                if (!StringUtils.isEmpty(param.getDd())) {
                    sequence = StringUtils.replace(sequence, "[dd]", param.getDd());
                    continue;
                }
                throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.SYS.SEQUENCES.TIP.SEQUENCE_CONFIG_ERROR_KEY_NOT_EXIST", pattern, substring));
            }
            if (!substring.matches("^[n]+$")) continue;
            int defaultLength = CoreSequenceAbstractService.getNumberSequenceLength(pattern);
            int length = Math.max(defaultLength, param.getN().toString().length());
            sequence = StringUtils.replace(sequence, "[" + StringUtils.repeat("n", substring.length()) + "]", String.format("%0" + length + "d", param.getN()));
        }
        return sequence;
    }

    private List<String> parseSequenceList(String pattern, CoreSequenceParamDTO additiveParam, int qty) {
        ArrayList<String> sequenceList = new ArrayList<String>();
        Integer n = additiveParam.getN();
        for (int i = 0; i < qty; ++i) {
            additiveParam.setN(n - qty + i + 1);
            sequenceList.add(this.parseSequence(pattern, additiveParam));
        }
        additiveParam.setN(n);
        return sequenceList;
    }

    private static Integer getNumberSequenceLength(String pattern) {
        int pos = pattern.indexOf("[n");
        if (pos < 0) {
            return 0;
        }
        int length = 0;
        char[] chars = pattern.toCharArray();
        int j = chars.length;
        for (int i = pos + 1; i < j && 'n' == chars[i]; ++i) {
            ++length;
        }
        return length;
    }
}
