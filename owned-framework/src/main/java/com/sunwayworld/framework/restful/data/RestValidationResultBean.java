package com.sunwayworld.framework.restful.data;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class RestValidationResultBean implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private java.lang.Boolean valid;
    private java.lang.String message;
    public RestValidationResultBean(java.lang.Boolean p0) { this.valid = p0; }
    public RestValidationResultBean(java.lang.Boolean p0, java.lang.String p1) { this.valid = p0; this.message = p1; }
    public java.lang.Boolean getValid() { return valid; }
    public void setValid(java.lang.Boolean p0) { this.valid = p0; }
    public java.lang.String getMessage() { return message; }
    public void setMessage(java.lang.String p0) { this.message = p0; }
}
