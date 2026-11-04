/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree;

import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TreeDescriptor<T> {
    private Function<T, String> parseTreeNodeIdFunc;
    private Function<T, String> parseTreeNodeParentIdFunc;
    private Function<T, String> parseTreeNodeTextFunction;
    private Comparator<T> orderComparator;
    private Function<T, ? extends AbstractTreeNode> parseTreeNodeFunction;
    private List<BiConsumer<T, AbstractTreeNode>> consumerList = new ArrayList<BiConsumer<T, AbstractTreeNode>>();

    public TreeDescriptor(String idName) {
        this(idName, idName);
    }

    public TreeDescriptor(String idName, String textName) {
        this.parseTreeNodeIdFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, idName), String.class);
        this.orderComparator = (v1, v2) -> ((Comparable)ReflectionUtils.invokeReadMethod(v1, idName)).compareTo((Comparable)ReflectionUtils.invokeReadMethod(v2, idName));
        this.parseTreeNodeTextFunction = StringUtils.contains(textName, "${") ? t -> StringUtils.replaceBetween(textName, "${", "}", BeanUtils.deeplyToMap(t)) : t -> {
            if (t instanceof Persistable) {
                return (String)PersistableHelper.getPropertyValue((Persistable)t, textName);
            }
            return ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, textName), String.class);
        };
    }

    public TreeDescriptor(String idName, String parentIdName, String textName) {
        this(idName, parentIdName, textName, idName);
    }

    public TreeDescriptor(String idName, String parentIdName, String textName, String orderName) {
        this.parseTreeNodeIdFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, idName), String.class);
        this.parseTreeNodeParentIdFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, parentIdName), String.class);
        this.orderComparator = (v1, v2) -> {
            if (ReflectionUtils.findReadMethod(v1.getClass(), orderName) == null) {
                Map v1Ext$Map = (Map)ReflectionUtils.invokeReadMethod(v1, "ext$");
                Map v2Ext$Map = (Map)ReflectionUtils.invokeReadMethod(v2, "ext$");
                if (NumberUtils.isNumber((String)v1Ext$Map.get(orderName)) && NumberUtils.isNumber((String)v2Ext$Map.get(orderName))) {
                    if (ConvertUtils.canConvert(v1Ext$Map.get(orderName), Long.class) && ConvertUtils.canConvert(v2Ext$Map.get(orderName), Long.class)) {
                        return ConvertUtils.convert(v1Ext$Map.get(orderName), Long.class).compareTo(ConvertUtils.convert(v2Ext$Map.get(orderName), Long.class));
                    }
                    if (ConvertUtils.canConvert(v1Ext$Map.get(orderName), Integer.class) && ConvertUtils.canConvert(v2Ext$Map.get(orderName), Integer.class)) {
                        return ConvertUtils.convert(v1Ext$Map.get(orderName), Integer.class).compareTo(ConvertUtils.convert(v2Ext$Map.get(orderName), Integer.class));
                    }
                    if (ConvertUtils.canConvert(v1Ext$Map.get(orderName), Double.class) && ConvertUtils.canConvert(v2Ext$Map.get(orderName), Double.class)) {
                        return ConvertUtils.convert(v1Ext$Map.get(orderName), Double.class).compareTo(ConvertUtils.convert(v2Ext$Map.get(orderName), Double.class));
                    }
                    if (ConvertUtils.canConvert(v1Ext$Map.get(orderName), Float.class) && ConvertUtils.canConvert(v2Ext$Map.get(orderName), Float.class)) {
                        return ConvertUtils.convert(v1Ext$Map.get(orderName), Float.class).compareTo(ConvertUtils.convert(v2Ext$Map.get(orderName), Float.class));
                    }
                }
                return ((String)v1Ext$Map.get(orderName)).compareTo((String)v2Ext$Map.get(orderName));
            }
            return ((Comparable)ReflectionUtils.invokeReadMethod(v1, orderName)).compareTo((Comparable)ReflectionUtils.invokeReadMethod(v2, orderName));
        };
        this.parseTreeNodeTextFunction = StringUtils.contains(textName, "${") ? t -> StringUtils.replaceBetween(textName, "${", "}", BeanUtils.deeplyToMap(t)) : t -> {
            if (t instanceof Persistable) {
                return (String)PersistableHelper.getPropertyValue((Persistable)t, textName);
            }
            return ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, textName), String.class);
        };
    }

    public Function<T, String> getParseTreeNodeIdFunc() {
        return this.parseTreeNodeIdFunc;
    }

    public Function<T, String> getParseTreeNodeParentIdFunc() {
        return this.parseTreeNodeParentIdFunc;
    }

    public void setParseTreeNodeParentIdFunc(Function<T, String> parseTreeNodeParentIdFunc) {
        this.parseTreeNodeParentIdFunc = parseTreeNodeParentIdFunc;
    }

    public Function<T, String> getParseTreeNodeTextFunction() {
        return this.parseTreeNodeTextFunction;
    }

    public void setParseTreeNodeTextFunction(Function<T, String> parseTreeNodeTextFunction) {
        this.parseTreeNodeTextFunction = parseTreeNodeTextFunction;
    }

    public Comparator<T> getOrderComparator() {
        return this.orderComparator;
    }

    public void setOrderComparator(Comparator<T> orderComparator) {
        this.orderComparator = orderComparator;
    }

    public List<BiConsumer<T, AbstractTreeNode>> getConsumerList() {
        return this.consumerList;
    }

    public void addConsumer(BiConsumer<T, AbstractTreeNode> consumer) {
        this.consumerList.add(consumer);
    }

    public Function<T, ? extends AbstractTreeNode> getParseTreeNodeFunction() {
        return this.parseTreeNodeFunction;
    }

    public void setParseTreeNodeFunction(Function<T, ? extends AbstractTreeNode> parseTreeNodeFunction) {
        this.parseTreeNodeFunction = parseTreeNodeFunction;
    }
}
