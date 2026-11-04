/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.builder;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.SoapHelper;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.base.SoapDynamicDTO;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.DynamicCreateObjectUtil;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.EsbXmlUtils;
import com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.util.JsonToSoapEntityUtil;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.util.proxy.DefinePackageHelper;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

public abstract class AbstractSoapDynamicBuilder {
    private ClassPool classPool = DynamicCreateObjectUtil.pair.getFirst();
    private CtClass ctClass;
    private ClassFile classFile;
    private ConstPool constPool;
    private String packageName;
    private Long soapId;
    private String apiCode;
    private SoapDynamicDTO soapDynamicDTO;

    public AbstractSoapDynamicBuilder(Class<?> clazz, SoapDynamicDTO soapDynamicDTO, String apiCode) throws CannotCompileException {
        this.packageName = clazz.getPackage().getName();
        this.apiCode = apiCode;
        this.soapId = soapDynamicDTO.getServiceId();
        this.soapDynamicDTO = soapDynamicDTO;
        try {
            this.classPool.importPackage(DynamicCreateObjectUtil.DYNAMIC_SOAP_ENTITY_PREFIX + this.soapId);
            this.ctClass = this.classPool.getCtClass(DynamicCreateObjectUtil.DYNAMIC_SOAP_ENTITY_PREFIX + this.soapId + ".SoapClass" + this.soapId);
            this.ctClass.stopPruning(Boolean.TRUE);
            if (this.ctClass.isFrozen()) {
                this.ctClass.defrost();
            }
        }
        catch (NotFoundException e) {
            this.ctClass = this.classPool.makeClass(this.packageName + this.soapId + ".SoapClass" + this.soapId);
            DefinePackageHelper.definePackage(this.packageName + this.soapId, DynamicCreateObjectUtil.classLoader);
        }
        this.classFile = this.ctClass.getClassFile();
        this.constPool = this.classFile.getConstPool();
    }

    protected AnnotationsAttribute createAnnotationAttribute() {
        return new AnnotationsAttribute(this.getConstPool(), "RuntimeVisibleAnnotations");
    }

    public AbstractSoapDynamicBuilder createClassAnnotation() {
        HashMap<String, Object> webServiceAnnotationMap = new HashMap<String, Object>();
        webServiceAnnotationMap.put("name", "lcdpWebServiceName" + this.apiCode);
        webServiceAnnotationMap.put("portName", "lcdpWebServicePortName" + this.apiCode);
        webServiceAnnotationMap.put("serviceName", "lcdpWebServiceServiceName" + this.apiCode);
        webServiceAnnotationMap.put("targetNamespace", "lcdpwebservice" + this.apiCode + ".ws.lcdp.sunwayworld.com");
        AnnotationsAttribute annotationsAttribute = this.createAnnotationAttribute();
        Annotation classAnnotation = DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), WebService.class.getName(), webServiceAnnotationMap);
        Annotation bindTypeAnnotation = this.createSoapBindingTypeAnnotation();
        if (null == bindTypeAnnotation) {
            annotationsAttribute.setAnnotation(classAnnotation);
        } else {
            annotationsAttribute.setAnnotations(new Annotation[]{classAnnotation, bindTypeAnnotation});
        }
        this.getClassFile().addAttribute(annotationsAttribute);
        return this;
    }

    public Annotation createMethodAnnotation(SoapDynamicDTO.WebMethod webMethod, Class<?> annotationClass) {
        HashMap<String, Object> annotationMap = new HashMap<String, Object>();
        Annotation annotation = null;
        if (WebMethod.class.isAssignableFrom(annotationClass)) {
            annotationMap.put("operationName", webMethod.getOperationName());
            annotation = DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), WebMethod.class.getName(), annotationMap);
        } else if (WebResult.class.isAssignableFrom(annotationClass)) {
            annotationMap.put("name", webMethod.getWebResult().getName());
            annotationMap.put("partName", webMethod.getWebResult().getPartName());
            annotation = DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), WebResult.class.getName(), annotationMap);
        } else if (RequestWrapper.class.isAssignableFrom(annotationClass)) {
            String name = "Request" + new Random().nextInt();
            annotationMap.put("className", "com.sunwayworld.cloud.common.webservice.support." + name);
            annotationMap.put("localName", name);
            annotation = DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), RequestWrapper.class.getName(), annotationMap);
        } else if (ResponseWrapper.class.isAssignableFrom(annotationClass)) {
            String name = "Request" + new Random().nextInt();
            annotationMap.put("className", "com.sunwayworld.cloud.common.webservice.support.Response" + name);
            annotationMap.put("localName", name);
            annotation = DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), ResponseWrapper.class.getName(), annotationMap);
        }
        return annotation;
    }

    protected abstract Annotation createSoapBindingTypeAnnotation();

    protected CtMethod createCtMethod(SoapDynamicDTO.WebMethod webMethod, CtClass returnType) throws CannotCompileException, NotFoundException {
        List<SoapDynamicDTO.WebParam> webParamList = webMethod.getWebParam();
        CtMethod ctMethod = null;
        try {
            ctMethod = this.ctClass.getDeclaredMethod(webMethod.getOperationName());
            this.ctClass.removeMethod(ctMethod);
        }
        catch (NotFoundException notFoundException) {
            // empty catch block
        }
        StringBuilder stringBuilder = new StringBuilder("{com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService service = com.sunwayworld.framework.context.ApplicationContextHelper.getBean(com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiIntegrationService.class);\n");
        if (CollectionUtils.isEmpty(webParamList)) {
            ctMethod = new CtMethod(returnType, webMethod.getOperationName(), null, this.getCtClass());
            stringBuilder.append("Object obj = service.callApiScript(\"" + this.apiCode + "\");");
        } else {
            ctMethod = SoapHelper.createCtMethod(returnType, webMethod.getOperationName(), webParamList, this);
            stringBuilder.append("java.util.Map map = new java.util.HashMap();");
            for (int i = 0; i < webParamList.size(); ++i) {
                if (StringUtils.equalsIgnoreCase(webParamList.get(i).getParamType(), "simple")) {
                    stringBuilder.append("map.put(\"").append(webParamList.get(i).getName()).append("\", $").append(i + 1).append(");");
                    continue;
                }
                stringBuilder.append("map.put(\"").append(webParamList.get(i).getName()).append("\", com.sunwayworld.cloud.module.lcdp.apiintegration.webservice.SoapHelper.fastJsonBean2Map($").append(i + 1).append("));");
            }
            stringBuilder.append("Object obj = service.callApiScript(\"" + this.apiCode + "\",new Object[]{map});");
        }
        stringBuilder.append("String resultStr = String.valueOf(obj);");
        SoapDynamicDTO.WebParam webResult = webMethod.getWebResult();
        Annotation webResultAnno = null;
        if (null != webResult) {
            if ("simple".equalsIgnoreCase(webResult.getParamType())) {
                stringBuilder.append("return resultStr;");
            } else {
                stringBuilder.append("return ($r)com.alibaba.fastjson.JSON.parseObject(resultStr,$type);");
            }
            webResultAnno = this.createMethodAnnotation(webMethod, WebResult.class);
        }
        stringBuilder.append("}");
        AnnotationsAttribute methodAnno = this.createAnnotationAttribute();
        Annotation webMethodAnno = this.createMethodAnnotation(webMethod, WebMethod.class);
        methodAnno.setAnnotations(new Annotation[]{webMethodAnno, webResultAnno});
        ctMethod.getMethodInfo().addAttribute(methodAnno);
        ctMethod.setBody(stringBuilder.toString());
        ctMethod.addCatch("{System.out.println($e);throw $e;}", ClassPool.getDefault().get(ApplicationRuntimeException.class.getName()));
        return ctMethod;
    }

    public AbstractSoapDynamicBuilder createClassMethod() {
        ClassPool classPool = DynamicCreateObjectUtil.pair.getFirst();
        SoapDynamicDTO.WebMethod webMethod = this.getSoapDynamicDTO().getWebService().getWebMethod();
        if (ObjectUtils.isEmpty(webMethod)) {
            throw new ApplicationRuntimeException("LCDP.MODULE.API_INTEGRATION.TIP.SOAP_METHOD_IS_NOT_NULL", new String[0]);
        }
        try {
            String paramType;
            SoapDynamicDTO.WebParam webResult = webMethod.getWebResult();
            CtClass returnType = null == webResult || "void".equals(webResult.getParamType()) ? CtClass.voidType : ("simple".equalsIgnoreCase(paramType = webResult.getParamType()) ? classPool.get(webMethod.getWebResult().getParamDefinition()) : classPool.getCtClass(DynamicCreateObjectUtil.DYNAMIC_SOAP_ENTITY_PREFIX + this.soapId + ".SoapClass" + this.soapId + webMethod.getWebResult().getParamDefinition()));
            CtMethod ctMethod = this.createCtMethod(webMethod, returnType);
            this.getCtClass().addMethod(ctMethod);
        }
        catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
        return this;
    }

    public AbstractSoapDynamicBuilder createEntityClass() {
        Map<String, String> paramConfig = this.getSoapDynamicDTO().getParamConfig();
        if (!CollectionUtils.isEmpty(paramConfig)) {
            for (Map.Entry<String, String> entry : paramConfig.entrySet()) {
                try {
                    if (JsonToSoapEntityUtil.isJson(entry.getValue())) {
                        JsonToSoapEntityUtil.fromJson(entry.getValue(), DynamicCreateObjectUtil.DYNAMIC_SOAP_ENTITY_PREFIX + this.getSoapId() + ".SoapClass" + this.getSoapId() + entry.getKey());
                        continue;
                    }
                    if (EsbXmlUtils.isXml(entry.getValue())) {
                        Map<String, Object> map = EsbXmlUtils.xmlToMap(entry.getValue());
                        JsonToSoapEntityUtil.fromJson(JSON.toJSONString(map), DynamicCreateObjectUtil.DYNAMIC_SOAP_ENTITY_PREFIX + this.getSoapId() + ".SoapClass" + this.getSoapId() + entry.getKey());
                        continue;
                    }
                    throw new RuntimeException("LCDP.MODULE.API_INTEGRATION.TIP.SOAP_DYNAMIC_ENTITY_DEFINE_NOT_ALLOWED");
                }
                catch (Exception e) {
                    throw new ApplicationRuntimeException(e);
                }
            }
        }
        return this;
    }

    public Class<?> createSoapClass() {
        this.createEntityClass().createClassAnnotation().createClassMethod().writeClassFile();
        try {
            return this.ctClass.toClass(DynamicCreateObjectUtil.classLoader, this.getClass().getProtectionDomain());
        }
        catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    public void writeClassFile() {
    }

    public ClassPool getClassPool() {
        return this.classPool;
    }

    public void setClassPool(ClassPool classPool) {
        this.classPool = classPool;
    }

    public CtClass getCtClass() {
        return this.ctClass;
    }

    public void setCtClass(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    public ClassFile getClassFile() {
        return this.classFile;
    }

    public void setClassFile(ClassFile classFile) {
        this.classFile = classFile;
    }

    public ConstPool getConstPool() {
        return this.constPool;
    }

    public void setConstPool(ConstPool constPool) {
        this.constPool = constPool;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getSoapId() {
        return this.soapId;
    }

    public void setSoapId(Long soapId) {
        this.soapId = soapId;
    }

    public String getApiCode() {
        return this.apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public SoapDynamicDTO getSoapDynamicDTO() {
        return this.soapDynamicDTO;
    }

    public void setSoapDynamicDTO(SoapDynamicDTO soapDynamicDTO) {
        this.soapDynamicDTO = soapDynamicDTO;
    }

    public static class Soap12DynamicBuilder
    extends AbstractSoapDynamicBuilder {
        public Soap12DynamicBuilder(Class<?> clazz, SoapDynamicDTO soapDynamicDTO, String apiCode) throws CannotCompileException {
            super(clazz, soapDynamicDTO, apiCode);
        }

        @Override
        protected Annotation createSoapBindingTypeAnnotation() {
            HashMap<String, Object> bindTypeMap = new HashMap<String, Object>();
            bindTypeMap.put("value", "http://www.w3.org/2003/05/soap/bindings/HTTP/");
            return DynamicCreateObjectUtil.addSingleAnnotation(this.getConstPool(), BindingType.class.getName(), bindTypeMap);
        }
    }

    public static class Soap11DynamicBuilder
    extends AbstractSoapDynamicBuilder {
        public Soap11DynamicBuilder(Class<?> clazz, SoapDynamicDTO soapDynamicDTO, String apiCode) throws CannotCompileException {
            super(clazz, soapDynamicDTO, apiCode);
        }

        @Override
        protected Annotation createSoapBindingTypeAnnotation() {
            return null;
        }
    }
}
