/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import com.sunwayworld.module.sys.sequence.resource.CoreSequenceResource;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u7f16\u53f7\u89c4\u5219")
@GikamBean
public class CoreSequenceResourceImpl
implements CoreSequenceResource {
    @Autowired
    private CoreSequenceService sequenceService;

    @Override
    public CoreSequenceService getService() {
        return this.sequenceService;
    }

    @Override
    @Log(value="\u6839\u636e\u7f16\u53f7\u89c4\u5219ID\u67e5\u8be2\u7f16\u53f7\u89c4\u5219\u660e\u7ec6", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/lines/queries"}, method={RequestMethod.POST})
    public Page<CoreSequenceLineBean> selectLinePaginationBySequenceId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectLinePaginationBySequenceId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u7f16\u53f7\u89c4\u5219\u660e\u7ec6", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.POST})
    public Long insertLine(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertLine(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u7f16\u53f7\u89c4\u5219\u660e\u7ec6", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/lines"}, method={RequestMethod.DELETE})
    public void deleteLine(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteLine(id, wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    @RequestMapping(value={"/lines/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapLine(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapLine("ORDERNO", firstId, secondId);
    }
}
