package com.sunwayworld.framework;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import java.util.Arrays;

public final class OwnedFrameworkContractTest {
    public static void main(String[] args) {
        Page<String> page = new Pagination<String>(Arrays.asList("row"));
        if (page.getTotal() != 1 || page.getRows().size() != 1 || !page.isFirst() || !page.isLast()) {
            throw new AssertionError("page contract");
        }
        page.setPageSize(1);
        page.setTotal(2);
        if (!page.hasNext()) throw new AssertionError("page navigation contract");
    }
}
