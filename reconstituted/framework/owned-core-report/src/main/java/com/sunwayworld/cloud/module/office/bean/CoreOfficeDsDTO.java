/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.framework.data.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreOfficeDsDTO
implements Serializable {
    private static final long serialVersionUID = 3121851571035138352L;
    private List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> tableDsList = new ArrayList<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>>();
    private List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> detailDsList = new ArrayList<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>>();

    public List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> getTableDsList() {
        return this.tableDsList;
    }

    public List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> getDetailDsList() {
        return this.detailDsList;
    }

    public void addDs(CoreOfficeDsBean ds, List<CoreOfficeDsFieldBean> dsFieldList) {
        if ("list".equals(ds.getDisplayCategory())) {
            this.tableDsList.add(Pair.of(ds, dsFieldList));
        } else if ("detail".equals(ds.getDisplayCategory())) {
            this.detailDsList.add(Pair.of(ds, dsFieldList));
        }
    }
}
