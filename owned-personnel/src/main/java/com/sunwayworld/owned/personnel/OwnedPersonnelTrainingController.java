package com.sunwayworld.owned.personnel;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned read seam for the original personnel training tab. */
@RestController
@RequestMapping("/secure/emc/module/mdm/emp/emps")
public final class OwnedPersonnelTrainingController {
    private final OwnedPersonnelReadDao dao;

    public OwnedPersonnelTrainingController(OwnedPersonnelReadDao dao) { this.dao = dao; }

    @RequestMapping(value = "/{id}/trains/queries", method = RequestMethod.POST)
    public Page<EmcEmpTrainBean> read(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return dao.readTraining(id, wrapper);
    }

    @RequestMapping(value = "/{id}/trains", method = RequestMethod.POST)
    public void insert(@PathVariable String id, RestJsonWrapperBean wrapper) {
        dao.insertTraining(id, wrapper);
    }

    @RequestMapping(value = "/{id}/trains", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id, RestJsonWrapperBean wrapper) {
        dao.deleteTraining(id, wrapper);
    }
}
