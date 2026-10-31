package com.sunwayworld.framework.database.context;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import java.io.Serializable;
public class EntityColumnContext implements Serializable {
 private static final long serialVersionUID=2514050087058095578L;
 private Class<?> entityClass; private String columnName; private Class<?> type; private boolean clob; private boolean blob; private boolean timestamp;
 public EntityColumnContext(){}
 public String getColumnName(){return columnName;} public void setColumnName(String v){columnName=v;}
 public BeanPropertyDescriptor getPropertyDescriptor(){BeanPropertyDescriptor d=BeanPropertyHelper.getBeanPropertyDescriptor(entityClass,columnName); return d!=null?d:BeanPropertyHelper.getBeanPropertyDescriptor(entityClass,"ext$");}
 public Class<?> getEntityClass(){return entityClass;} public void setEntityClass(Class<?> v){entityClass=v;}
 public Class<?> getType(){return type;} public void setType(Class<?> v){type=v;}
 public boolean isClob(){return clob;} public void setClob(boolean v){clob=v;}
 public boolean isBlob(){return blob;} public void setBlob(boolean v){blob=v;}
 public boolean isTimestamp(){return timestamp;} public void setTimestamp(boolean v){timestamp=v;}
 public <T> Object getValue(T value){BeanPropertyDescriptor d=getPropertyDescriptor(); if(d.isExt$BeanProperty()) return ((BaseData)value).getExt$().get(columnName); return d.getPropertyValue(value);}
}
