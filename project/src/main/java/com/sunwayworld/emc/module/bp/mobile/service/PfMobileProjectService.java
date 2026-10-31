package com.sunwayworld.emc.module.bp.mobile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobileDTO;
import com.sunwayworld.emc.module.bp.mobile.bean.EmcMobilePaginationDTO;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.database.context.EntityContext;
import com.sunwayworld.framework.database.context.instance.EntityContextInstance;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.JsonUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年11月02日
 */
public interface PfMobileProjectService extends EmcMobileProjectService {

    /**
     * 解析接口参数为 EmcMobileDTO
     * @param wrapper 参数
     * @return EmcMobileDTO
     */
    default EmcMobileDTO parse(RestJsonWrapperBean wrapper) {
        return parse(wrapper, EmcMobileDTO.class);
    }

    /**
     * 通用解析方法
     * @param wrapper 参数
     * @param clazz 目标类
     * @param <T> 目标泛型
     * @return 目标对象
     */
    default<T> T parse(RestJsonWrapperBean wrapper, Class<T> clazz) {
        List<List<String>> bodyList = wrapper.getBodyList();
        if (bodyList.isEmpty() || bodyList.get(0).isEmpty()) {
            return null;
        }

        return JsonUtils.parse(bodyList.get(0).get(0), clazz);
    }

    /**
     * 获取待更新的字段列表
     * @param item 数据对象
     * @return
     */
    default List<String> getUpdateColNames(Persistable<?> item) {
        List<String> updateColNameList = new ArrayList<>();
        EntityContextInstance contextInstance = EntityContextInstance.updatable(new EntityContext(), item);

        contextInstance.getColumnContextInstanceList().stream().filter(i -> i.isActive() && !EntityHelper.isIdInstance(i))
                .forEach(i -> updateColNameList.add(i.getColumnContext().getColumnName()));

        return updateColNameList;
    }

    /**
     * 移动端创建/复制点位
     * @param wrapper 被复制的点位数据
     * @return EmcMobileDTO
     */
    EmcMobileDTO createFolder(RestJsonWrapperBean wrapper);

    /**
     * 移动端复制样品
     * @param wrapper 被复制的样品数据
     * @return EmcMobileDTO
     */
    EmcMobileDTO createOrder(RestJsonWrapperBean wrapper);

    /**
     * 移动端删除点位
     * @param wrapper 被删除的点位数据
     * @return EmcMobileDTO
     */
    EmcMobileDTO deleteFolder(RestJsonWrapperBean wrapper);

    /**
     * 移动端删除样品
     * @param wrapper 被删除的样品数据
     * @return EmcMobileDTO
     */
    EmcMobileDTO deleteOrder(RestJsonWrapperBean wrapper);

    /**
     * 分页下载静态数据
     * @param wrapper 分页参数
     * @return EmcMobilePaginationDTO
     */
    EmcMobilePaginationDTO selectStaticDataByPagination(RestJsonWrapperBean wrapper);

    /**
     * 分页下载项目业务数据
     * @param id 项目ID
     * @param wrapper 分页参数
     * @return EmcMobilePaginationDTO
     */
    EmcMobilePaginationDTO selectProjectDataByPagination(Long id, RestJsonWrapperBean wrapper);

    /**
     * 新增或更新仪器领用出入库记录
     * @param mobileDTO
     * @param calibrationIdMap
     * @param sampleIdMap
     */
    void updateEquiptRequisition(EmcMobileDTO mobileDTO, Map<Long, Long> calibrationIdMap, Map<Long, Long> sampleIdMap);

    EmcMobileDTO createDelivery(RestJsonWrapperBean wrapper);
}
