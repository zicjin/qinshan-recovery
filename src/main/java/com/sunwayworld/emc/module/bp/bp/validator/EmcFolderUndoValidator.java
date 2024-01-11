package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年11月06日
 */
@Repository
public class EmcFolderUndoValidator implements DataValidator {

    @Autowired
    private EmcOrderService orderService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "SAMPLEID"));

        // 如果点位下的样品关联了采样单，不允许退回点位
        List<EmcOrderBean> relatedSampleOrderList = orderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleId())).collect(Collectors.toList());
        if (!relatedSampleOrderList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.FOLDER.FOLDERS.TIP.CAN_NOT_UNDO_FOLDER");
            return false;
        }

        return true;
    }
}
