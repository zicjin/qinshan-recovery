package com.sunwayworld.emc.module.mdm.bizmdm.client.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.emc.module.mdm.basemdm.area.service.EmcAreaService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientDTO;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

@Component(value = "emcClientImportFileFunction")
public class EmcClientImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcClientService emcClientService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Autowired
    private EmcAreaService emcAreaService;

    @Autowired
    private EmcMonitorPointCategoryService emcMonitorPointCategoryService;

    @Autowired
    private EmcMonitorPointService emcMonitorPointService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();

        FilePathDTO fr = CoreFileUtils.toFilePath(file);

        List<EmcClientDTO> clientDTOList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 1, 2, EmcClientDTO.class);

        if (clientDTOList.size() > 0) {

            // 下拉框，choose框数据
            List<CoreCodeBean> customerCategoryList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "customerCategory").filter(MatchPattern.SEQ));
            List<CoreCodeBean> customerSubclassList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "customerSubclass").filter(MatchPattern.SEQ));
            List<CoreCodeBean> monitoringPointCategoryList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "monitoringPointCategory").filter(MatchPattern.SEQ));
            List<CoreCodeBean> yesOrNoList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "yesorno").filter(MatchPattern.SEQ));
            List<CoreCodeBean> monitoringPointLevelList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "monitoringPointLevel").filter(MatchPattern.SEQ));
            List<CoreCodeBean> controllerLeverList = coreCodeService.selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "controllerLever").filter(MatchPattern.SEQ));
            List<EmcAreaBean> areaList = emcAreaService.selectAll();
            List<EmcMonitorPointCategoryBean> monitorPointCategoryList = emcMonitorPointCategoryService.selectSelectableList();

            List<String> clientNameList = clientDTOList.stream().map(e -> e.getClientName()).collect(Collectors.toList());
            List<EmcClientBean> existClientList = emcClientService.getDao().selectListByOneColumnValues(clientNameList, "CLIENTNAME");

            List<Long> clientNoList = existClientList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcMonitorPointBean> existMonitorPointList = emcMonitorPointService.getDao().selectListByOneColumnValues(clientNoList, "CLIENTID");

            existClientList.forEach(e -> {
                clientDTOList.forEach(m -> {
                    if (e.getClientName().equals(m.getClientName())) {
                        m.setClientId(e.getId());
                    }
                });
            });

            clientDTOList.forEach(e -> {
                customerCategoryList.forEach(m -> {
                    if (m.getCodeName().equals(e.getClientCategoryName())) {
                        e.setClientCategory(m.getCode());
                    }
                });

                customerSubclassList.forEach(m -> {
                    if (m.getCodeName().equals(e.getClientSCategoryName())) {
                        e.setClientSCategory(m.getCode());
                    }
                });

                areaList.forEach(m -> {
                    if (m.getAreaName().equals(e.getAreaName())) {
                        e.setAreaId(m.getId());
                    }
                });

                monitorPointCategoryList.forEach(m -> {
                    if (m.getExt$Item("templatename").equals(e.getMonitorPointCategoryName())) {
                        e.setMonitorPointCategoryId(m.getId());
                    }
                });

                monitoringPointCategoryList.forEach(m -> {
                    if (m.getCodeName().equals(e.getMonitorPointTypeName())) {
                        e.setMonitorPointType(m.getCode());
                    }
                });

                yesOrNoList.forEach(m -> {
                    if (m.getCodeName().equals(e.getExportName())) {
                        e.setExport(m.getCode());
                    }
                });

                monitoringPointLevelList.forEach(m -> {
                    if (m.getCodeName().equals(e.getMonitorPointLevelName())) {
                        e.setMonitorPointLevel(m.getCode());
                    }
                });

                controllerLeverList.forEach(m -> {
                    if (m.getCodeName().equals(e.getControllerLeverName())) {
                        e.setControllerLever(m.getCode());
                    }
                });
            });

            List<EmcClientBean> clientList = new ArrayList<>();
            List<EmcMonitorPointBean> monitorPointList = new ArrayList<>();
            clientDTOList.forEach(e -> {
                EmcClientBean client = new EmcClientBean();
                EmcMonitorPointBean monitorPoint = new EmcMonitorPointBean();

                clientList.forEach(f -> {
                    if (f.getClientName().equals(e.getClientName())) {
                        BeanUtils.copyProperties(f, client);
                        client.setId(f.getId());
                    }
                });
                if (ObjectUtils.isEmpty(client.getClientName())) {
                    BeanUtils.copyProperties(e, client);
                }
                if (!ObjectUtils.isEmpty(e.getClientLongitude())) {
                    client.setLongitude(e.getClientLongitude());
                }
                if (!ObjectUtils.isEmpty(e.getClientLatitude())) {
                    client.setLatitude(e.getClientLatitude());
                }

                BeanUtils.copyProperties(e, monitorPoint);
                if (!ObjectUtils.isEmpty(e.getMonitorPointLongitude())) {
                    monitorPoint.setLongitude(e.getMonitorPointLongitude());
                }
                if (!ObjectUtils.isEmpty(e.getMonitorPointLatitude())) {
                    monitorPoint.setLatitude(e.getMonitorPointLatitude());
                }

                if (ObjectUtils.isEmpty(e.getClientId())) {
                    if (ObjectUtils.isEmpty(client.getId())) {
                        client.setId(ApplicationContextHelper.getNextIdentity());
                        client.setClientNo(ApplicationContextHelper.getNextSequence(emcClientService.getDao().getTable()));
                        clientList.add(client);
                    }
                    monitorPoint.setClientId(NumberUtils.parseLong(client.getId()));
                }
                monitorPoint.setId(ApplicationContextHelper.getNextIdentity());
                monitorPointList.add(monitorPoint);
            });

            clientList.removeIf(u -> existClientList.stream().anyMatch(m -> m.getClientName().equals(u.getClientName())));

            monitorPointList
                    .removeIf(u -> existMonitorPointList.stream().anyMatch(m -> m.getMonitorPointNo().equals(u.getMonitorPointNo()) && m.getMonitorPointName().equals(u.getMonitorPointName())));

            emcClientService.getDao().insert(clientList);
            emcMonitorPointService.getDao().insert(monitorPointList);
        }
        return CoreFileImportResultDTO;
    }
}
