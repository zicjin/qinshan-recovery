package com.sunwayworld.framework.support.base.service;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericExcelBuilderService {
    public abstract <T> GenericExcelBuilderService.DownloadExcelDTO buildExcel(com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor p0, java.lang.Class<? extends java.lang.Object> p1, java.sql.ResultSet p2, java.lang.String p3, java.lang.String... p4);
    public abstract <T> GenericExcelBuilderService.DownloadExcelDTO buildExcel(com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor p0, com.sunwayworld.framework.data.page.Page<T> p1, java.lang.String p2);
    public static class DownloadExcelDTO {
        public DownloadExcelDTO() {  }
        public java.lang.String getFileName() { throw new UnsupportedOperationException("Owned read-only boundary: DownloadExcelDTO.getFileName is not implemented"); }
        public void setFileName(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: DownloadExcelDTO.setFileName is not implemented"); }
        public java.lang.String getUrl() { throw new UnsupportedOperationException("Owned read-only boundary: DownloadExcelDTO.getUrl is not implemented"); }
        public void setUrl(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: DownloadExcelDTO.setUrl is not implemented"); }
    }
}
