/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.acqconfig.bean.CoreEquiptAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.acqconfig.persistent.dao.CoreEquiptAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.acqconfig.service.CoreEquiptAcqConfigService;
import com.sunwayworld.cloud.module.equipt.acqconfig.service.impl.SocketRun;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import java.io.IOException;
import java.net.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEquiptAcqConfigServiceImpl
implements CoreEquiptAcqConfigService {
    @Autowired
    private CoreEquiptAcqConfigDao coreEquiptAcqConfigDao;
    @Autowired
    private final SimpMessagingTemplate template;

    public CoreEquiptAcqConfigServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public CoreEquiptAcqConfigDao getDao() {
        return this.coreEquiptAcqConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertByEquiptId(String equiptId) {
        CoreEquiptAcqConfigBean equiptAcqConfig = new CoreEquiptAcqConfigBean();
        equiptAcqConfig.setId(equiptId);
        this.getDao().insert(equiptAcqConfig);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String connect(String id, RestJsonWrapperBean wrapper) {
        CoreEquiptAcqConfigBean acqConfig = (CoreEquiptAcqConfigBean)this.selectById(id);
        String ipPort = acqConfig.getIp() + ":" + acqConfig.getPort();
        CoreEquiptAcqConfigServiceImpl coreEquiptAcqConfigServiceImpl = this;
        synchronized (coreEquiptAcqConfigServiceImpl) {
            try {
                if (SocketRun.IPPORTLIST.contains(ipPort)) {
                    SocketRun.SOCKETMAP.remove(ipPort).getInputStream().close();
                    SocketRun.IPPORTLIST.remove(ipPort);
                }
                Socket sockect = new Socket(acqConfig.getIp(), NumberUtils.parseInt(acqConfig.getPort()));
                Thread thread = new Thread(new SocketRun(ipPort, sockect, this));
                thread.start();
                SocketRun.IPPORTLIST.add(ipPort);
                SocketRun.SOCKETMAP.put(ipPort, sockect);
            }
            catch (IOException e) {
                e.printStackTrace();
                return "fail";
            }
        }
        return "success";
    }

    @Override
    public void pushPortMessage(String ipPort, String listenString) {
        this.template.convertAndSend("/topic/tcp-ports/" + ipPort, listenString);
    }

    @Override
    public void setProperty(CoreEquiptAcqConfigBean equiptAcqConfig) {
    }
}
