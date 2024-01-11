package com.sunwayworld.emc.module.item.patrol.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.emc.module.item.patrol.dao.EmcPatrolDao;
import com.sunwayworld.emc.module.item.patrol.service.EmcPatrolService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.FilterParamPattern;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */

@Repository
public class EmcPatrolServiceImpl implements EmcPatrolService {
    @Autowired
    private EmcPatrolDao emcPatrolDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcPatrolDao getDao() {
        return emcPatrolDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void monthInsert(RestJsonWrapperBean jsonWrapper) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date d = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-d");
        String dateNowStr = sdf.format(d);
        String dateNowStr2 = sdf2.format(d);

        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

        SearchFilter filter = SearchFilter.instance().match("PATROLDATE", dateNowStr).filter(MatchPattern.EQ);
        List<EmcPatrolBean> selectListByFilter = selectListByFilter(filter);
        if (selectListByFilter.isEmpty()) {
            ArrayList<EmcPatrolBean> emcPatrolBeanList = new ArrayList<EmcPatrolBean>();
            for (int i = 1; i <= cal.getMaximum(Calendar.DATE) - 1; i++) {
                EmcPatrolBean emcPatrolBean = new EmcPatrolBean();
                emcPatrolBean.setId(ApplicationContextHelper.getNextIdentity());
                String dateStr = dateNowStr2 + "-" + i;
                Date date = sdf3.parse(dateStr);
                emcPatrolBean.setPatrolDate(sdf.format(date));
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(date);
                int w = cal2.get(Calendar.DAY_OF_WEEK) - 1;
                emcPatrolBean.setPatrolWeekday(weekDays[w]);
                emcPatrolBeanList.add(emcPatrolBean);
            }
            emcPatrolDao.insert(emcPatrolBeanList);
        }

    }

    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<EmcPatrolBean> selectMonthRawPagination(RestJsonWrapperBean wrapper) {
        // TODO Auto-generated method stub
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dateNowStr = sdf.format(d);
        return selectPaginationByFilter(SearchFilter.instance().match("patrolDate", dateNowStr + "%").filter(MatchPattern.SC), wrapper);
    }

    @Override
    public Page<EmcPatrolBean> selectMonthRawPaginationByPatrolUserId(RestJsonWrapperBean wrapper) {
        // TODO Auto-generated method stub
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dateNowStr = sdf.format(d);
        SearchFilter searchFilter = SearchFilter.instance();
        searchFilter.stdFilterList.add(new FilterParamPattern(MatchPattern.SC.name(), "patrolDate", dateNowStr + "%"));
        searchFilter.stdFilterList.add(new FilterParamPattern(MatchPattern.EQ.name(), "patrolUserId", LocalContextHelper.getLoginUserId()));
        return selectPaginationByFilter(searchFilter, wrapper);
    }

    @Override
    public Page<EmcPatrolBean> selectMonthRawPaginationByHandleUserId(RestJsonWrapperBean wrapper) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dateNowStr = sdf.format(d);
        SearchFilter searchFilter = SearchFilter.instance();
        searchFilter.stdFilterList.add(new FilterParamPattern(MatchPattern.SC.name(), "patrolDate", dateNowStr + "%"));
        searchFilter.stdFilterList.add(new FilterParamPattern(MatchPattern.SC.name(), "handleUserIds", "%" + LocalContextHelper.getLoginUserId() + "%"));

        Page<EmcPatrolBean> page = selectPaginationByFilter(searchFilter, wrapper);
        List<EmcPatrolBean> list = new ArrayList<>();
        for (EmcPatrolBean emcPatrolBean : page.getRows()) {
            String[] handleUserIds = emcPatrolBean.getHandleUserIds().split(",");
            for (String handleUserId : handleUserIds) {
                if (handleUserId.equals(LocalContextHelper.getLoginUserId())) {
                    list.add(emcPatrolBean);
                    break;
                }
            }
        }

        return new Pagination<>(page, list);
    }

}
