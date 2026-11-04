package com.sunwayworld.cloud.module.les.service;

import com.sunwayworld.cloud.module.les.bean.CoreLesUnitCommentBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreLesUnitCommentService extends GenericService<CoreLesUnitCommentBean, Long> {
   void update(RestJsonWrapperBean jsonWrapper);

   void reply(RestJsonWrapperBean jsonWrapper);
}
