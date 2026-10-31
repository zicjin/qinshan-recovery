package com.sunwayworld.framework.database.context;
import java.io.Serializable;
import java.util.List;
public class EntityContext implements Serializable {
 private static final long serialVersionUID=7530861832329381453L;
 private String tableName; private EntityColumnContext idContext; private EntityColumnContext versionContext; private List<EntityColumnContext> columnContextList;
 public EntityContext(){}
 public String getTableName(){return tableName;} public void setTableName(String v){tableName=v;}
 public EntityColumnContext getIdContext(){return idContext;} public void setIdContext(EntityColumnContext v){idContext=v;}
 public EntityColumnContext getVersionContext(){return versionContext;} public void setVersionContext(EntityColumnContext v){versionContext=v;}
 public List<EntityColumnContext> getColumnContextList(){return columnContextList;} public void setColumnContextList(List<EntityColumnContext> v){columnContextList=v;}
 public boolean isNumberId(){return Number.class.isAssignableFrom(idContext.getType());}
}
