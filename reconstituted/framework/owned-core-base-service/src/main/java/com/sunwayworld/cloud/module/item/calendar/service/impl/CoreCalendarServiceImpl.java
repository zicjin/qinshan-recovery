package com.sunwayworld.cloud.module.item.calendar.service.impl;

import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarBean;
import com.sunwayworld.cloud.module.item.calendar.bean.CoreCalendarRepresentation;
import com.sunwayworld.cloud.module.item.calendar.persistent.dao.CoreCalendarDao;
import com.sunwayworld.cloud.module.item.calendar.service.CoreCalendarService;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreCalendarServiceImpl implements CoreCalendarService {
   @Autowired
   private CoreCalendarDao calendarDao;

   public CoreCalendarDao getDao() {
      return this.calendarDao;
   }

   @Override
   public List<CoreCalendarRepresentation> selectCalendarList(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      List<CoreCalendarBean> calendarList = this.getDao()
         .selectByCondition(parameter)
         .stream()
         .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreCalendarBean.class))
         .collect(Collectors.toList());
      return calendarList.stream().map(c -> {
         CoreCalendarRepresentation cr = new CoreCalendarRepresentation();
         cr.setYmd(c.getId());
         cr.setIsWorkDay(c.getWorkday());
         return cr;
      }).collect(Collectors.toList());
   }

   @Transactional
   @Override
   public void save(int year, int month, int dayOfMonth, String dayRepresentation) {
      LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
      String id = DateTimeUtils.getDateSerialNumber(localDate);
      CoreCalendarBean calendar = this.getDao().selectByIdIfPresent(id);
      if (calendar == null) {
         calendar = new CoreCalendarBean();
         calendar.setId(id);
         calendar.setWorkday(dayRepresentation);
         this.getDao().insert(calendar);
      } else if ("2".equals(dayRepresentation)) {
         this.getDao().delete(id);
      } else {
         CoreCalendarBean updateCalendar = new CoreCalendarBean();
         updateCalendar.setId(id);
         updateCalendar.setWorkday(dayRepresentation);
         this.getDao().update(updateCalendar, new String[0]);
      }
   }

   @Override
   public LocalDateTime plusDays(LocalDateTime dateTime, int workingDays) {
      return dateTime == null ? null : dateTime.plusDays(workingDays);
   }

   @Override
   public LocalDateTime plusWorkingDays(LocalDateTime dateTime, int workingDays) {
      if (dateTime == null) {
         return null;
      } else {
         for (int i = 0; i < workingDays; i++) {
            dateTime = dateTime.plusDays(1L);

            while (this.isHoliday(dateTime)) {
               dateTime = dateTime.plusDays(1L);
            }
         }

         return dateTime;
      }
   }

   @Override
   public LocalDate plusDays(LocalDate date, int workingDays) {
      return date == null ? null : date.plusDays(workingDays);
   }

   @Override
   public LocalDate plusWorkingDays(LocalDate date, int workingDays) {
      if (date == null) {
         return null;
      } else {
         for (int i = 0; i < workingDays; i++) {
            date = date.plusDays(1L);

            while (this.isHoliday(date)) {
               date = date.plusDays(1L);
            }
         }

         return date;
      }
   }

   @Override
   public boolean isHoliday(LocalDateTime dateTime) {
      return this.isHoliday(dateTime.toLocalDate());
   }

   @Override
   public boolean isHoliday(LocalDate date) {
      String id = DateTimeUtils.getDateSerialNumber(date);
      CoreCalendarBean calendar = this.getDao().selectByIdIfPresent(id);
      if (calendar != null) {
         return "0".equals(calendar.getWorkday());
      } else {
         DayOfWeek dow = date.getDayOfWeek();
         return DayOfWeek.SUNDAY.equals(dow) || DayOfWeek.SATURDAY.equals(dow);
      }
   }

   @Override
   public Long getWorkDays(RestJsonWrapperBean wrapper) {
      String startDateStr = wrapper.getParamValue("startDate");
      String endDateStr = wrapper.getParamValue("endDate");
      if (!StringUtils.isEmpty(startDateStr) && !StringUtils.isEmpty(endDateStr)) {
         LocalDate startDate = DateTimeUtils.parseLocalDate(startDateStr);
         LocalDate endDate = DateTimeUtils.parseLocalDate(endDateStr);

         long workDays;
         for (workDays = 0L; !startDate.isAfter(endDate); startDate = startDate.plusDays(1L)) {
            if (!this.isHoliday(startDate)) {
               workDays++;
            }
         }

         return workDays;
      } else {
         return 0L;
      }
   }
}
