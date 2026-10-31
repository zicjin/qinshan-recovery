package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * 样品或采样照片
 * @author zhengxy@sunwayworld.com 2023年09月15日
 */
public class OrderScenePhotoTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<CoreFileBean> fileList;
    private boolean sampling;
    private int tableIndex;

    public OrderScenePhotoTable(XWPFDocument document, EmcProjectBean project, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<CoreFileBean> fileList, int tableIndex) {
        this.document = document;
        this.folderList = folderList;
        this.orderList = orderList;
        this.fileList = fileList;
        this.sampling = Constant.YES.equals(project.getSample());
        this.tableIndex = tableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return sampling ? String.format(EmcReportConstant.NORMAL_SCENE_PHOTO_TABLE_TITLE, tableIndex) : String.format(EmcReportConstant.NORMAL_SCENE_PHOTO_TABLE_TITLE_2, tableIndex);
    }

    @Override
    public int getTableIndex() {
        return 3;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        dynamicTableDTO.setShowBorder(false);
        dynamicTableDTO.setShowCellBorder(false);
        dynamicTableDTO.setRepeatHeader(false);
        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        List<CoreFileBean> photoList = fileList.stream().filter(file -> StringUtils.equals(file.getBizCategory(), "photo") && (StringUtils.startsWith(file.getTargetId(), "T_EMC_SAMPLE") || StringUtils.startsWith(file.getTargetId(), "T_EMC_PROJECT"))).collect(Collectors.toList());
        List<CoreFileBean> validFileList = new ArrayList<>();
        List<String> folderNameList = new ArrayList<>();
        photoList.forEach(file -> {
            String[] pairs = StringUtils.split(file.getTargetId(), "$");
            if (pairs.length <= 1) {
                return;
            }

            long sampleId = NumberUtils.parseLong(pairs[1]);
            EmcOrderBean curOrder = orderList.stream().filter(order -> ObjectUtils.equals(order.getSampleId(), sampleId)).findFirst().orElseGet(EmcOrderBean::new);
            EmcFolderBean curFolder = folderList.stream().filter(folder -> ObjectUtils.equals(curOrder.getFolderId(), folder.getId())).findFirst().orElseGet(EmcFolderBean::new);
            validFileList.add(file);
            folderNameList.add(sampling ? "◎" + Optional.ofNullable(curFolder.getMonitorPointName()).orElse("-") : "");
        });

        List<List<String>> subNameList = ListUtils.partition(folderNameList, EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY);
        List<List<CoreFileBean>> subFileList = ListUtils.partition(validFileList, EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY);
        List<WordTemplateDynamicTableDTO.TableImage> tableImageList = new ArrayList<>();

        IntStream.range(0, subNameList.size()).forEach(index -> {
            List<String> curNameList = subNameList.get(index);
            List<CoreFileBean> curFileList = subFileList.get(index);

            // 首先构建两行数据
            if (index != 0) {
                List<Object> photoRowDataList = new ArrayList<>();
                IntStream.range(0, EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY).forEach(nameIndex -> photoRowDataList.add(""));
                tableBodyDataList.add(photoRowDataList);
            }

            // 只有采样的，才需要写点位名称
            if (sampling) {
                List<Object> dataRowDataList = new ArrayList<>(curNameList);
                int paddingQty = EmcReportConstant.DIOXIN_SCENE_PHOTO_TABLE_HEADER_COLUMN_QTY - curNameList.size();
                IntStream.range(0, paddingQty).forEach(i -> dataRowDataList.add(""));
                tableBodyDataList.add(dataRowDataList);
            }

            // 照片所在的行，采样的需要展示点位名称
            int curImageRowIndex = sampling ? index * 2 : index;
            IntStream.range(0, curFileList.size()).forEach(fileIndex -> {
                String imageUrl = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(curFileList.get(fileIndex))).toString();
                WordTemplateDynamicTableDTO.TableImage tableImage = new WordTemplateDynamicTableDTO.TableImage(curImageRowIndex, fileIndex, imageUrl);
                tableImageList.add(tableImage);
            });
        });

        dynamicTableDTO.setTableImageList(tableImageList);

        return tableBodyDataList;
    }
}
