package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned base personnel chooser; target-specific historical filters stay explicit. */
@RestController
@RequestMapping("/secure/emc/module/mdm/emp/emps")
public final class OwnedPersonnelChoosableController {
    private final OwnedPersonnelReadDao dao;
    public OwnedPersonnelChoosableController(OwnedPersonnelReadDao dao) { this.dao = dao; }

    @RequestMapping(value = "/queries/choosable", method = RequestMethod.POST)
    public Page<EmcEmpBean> read(RestJsonWrapperBean wrapper) {
        return dao.readPage(wrapper);
    }

    @RequestMapping(value = "/queries/searchable", method = RequestMethod.POST)
    public Page<EmcEmpBean> searchable(RestJsonWrapperBean wrapper) {
        return dao.readPage(wrapper);
    }
}
