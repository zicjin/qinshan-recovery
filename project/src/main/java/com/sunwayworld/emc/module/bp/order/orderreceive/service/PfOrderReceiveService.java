package com.sunwayworld.emc.module.bp.order.orderreceive.service;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.emc.module.bp.mobile.service.EmcMobileProjectService;
import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PfOrderReceiveService {


    Long receive(RestJsonWrapperBean wrapper);

    Long match(RestJsonWrapperBean wrapper);
}
