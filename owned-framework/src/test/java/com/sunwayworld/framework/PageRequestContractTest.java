package com.sunwayworld.framework;

import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.data.page.Pageable;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.data.domain.Sort;

/** Public PageRequest value/factory/navigation contract. */
public final class PageRequestContractTest {
    public static final class Filter {
        public String status;
        public int limit;
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void defaultsAndNavigation() {
        PageRequest request = new PageRequest();
        checkEquals(Integer.valueOf(1), Integer.valueOf(request.getPageNumber()), "default page number");
        checkEquals(Integer.valueOf(50), Integer.valueOf(request.getPageSize()), "default page size");
        checkEquals(Long.valueOf(0), Long.valueOf(request.getOffset()), "default offset");
        check(!request.hasPrevious(), "first page has no previous");

        request.setPageNumber(3);
        request.setPageSize(25);
        request.setFilterJson("{\"status\":\"OPEN\",\"limit\":7}");
        checkEquals(Long.valueOf(50), Long.valueOf(request.getOffset()), "offset");
        check(request.hasPrevious(), "page three has previous");
        checkEquals(Integer.valueOf(4), Integer.valueOf(request.next().getPageNumber()), "next page");
        checkEquals(Integer.valueOf(2), Integer.valueOf(request.previousOrFirst().getPageNumber()), "previous page");
        checkEquals(Integer.valueOf(1), Integer.valueOf(request.first().getPageNumber()), "first page");
        checkEquals(Integer.valueOf(25), Integer.valueOf(request.next().getPageSize()), "navigation keeps size");
        checkEquals(request.getFilterJson(), request.next().getFilterJson(), "navigation keeps filter");
        Filter filter = request.map(Filter.class);
        checkEquals("OPEN", filter.status, "mapped filter text");
        checkEquals(Integer.valueOf(7), Integer.valueOf(filter.limit), "mapped filter number");
    }

    private static void metadataAndFactories() {
        PageRequest request = PageRequest.of(2, 10);
        checkEquals(Integer.valueOf(2), Integer.valueOf(request.getPageNumber()), "factory page number");
        checkEquals(Integer.valueOf(10), Integer.valueOf(request.getPageSize()), "factory page size");
        Sort sort = Sort.by("name");
        request.setSort(sort);
        request.setSumProperties("amount");
        request.setCountProperties("id");
        request.setOptionsProperty("status");
        checkEquals(sort, request.getSort(), "sort");
        checkEquals("amount", request.getSumProperties()[0], "sum property");
        checkEquals("id", request.getCountProperties()[0], "count property");
        checkEquals("status", request.getOptionsProperty(), "options property");
    }

    private static void wrapperFactory() {
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
        wrapper.setParamValue("n", "4");
        wrapper.setParamValue("s", "12");
        wrapper.setParamValue("f", "{\"status\":\"READY\"}");
        wrapper.setParamValue("tf", "amount,total");
        wrapper.setParamValue("cp", "id");
        wrapper.setParamValue("op", "status");
        PageRequest request = PageRequest.of(wrapper);
        checkEquals(Integer.valueOf(4), Integer.valueOf(request.getPageNumber()), "wrapper page number");
        checkEquals(Integer.valueOf(12), Integer.valueOf(request.getPageSize()), "wrapper page size");
        checkEquals("{\"status\":\"READY\"}", request.getFilterJson(), "wrapper filter");
        checkEquals("amount", request.getSumProperties()[0], "wrapper sum properties");
        checkEquals("id", request.getCountProperties()[0], "wrapper count properties");
        checkEquals("status", request.getOptionsProperty(), "wrapper options property");
    }

    public static void main(String[] args) {
        defaultsAndNavigation();
        metadataAndFactories();
        wrapperFactory();
        System.out.println("PageRequest contract: PASS");
    }
}
