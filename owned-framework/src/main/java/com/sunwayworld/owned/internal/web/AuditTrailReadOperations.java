package com.sunwayworld.owned.internal.web;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** In-process original audit query integration. Organization is supplied by the authenticated boundary. */
public interface AuditTrailReadOperations {
    Page<?> records(RestJsonWrapperBean wrapper, String trustedOrganization);
    Page<?> lines(RestJsonWrapperBean wrapper, String trustedOrganization);
}
