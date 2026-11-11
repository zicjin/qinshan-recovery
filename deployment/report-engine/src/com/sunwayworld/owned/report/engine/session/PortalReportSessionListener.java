package com.sunwayworld.owned.report.engine.session;

import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.stable.web.SessionEvent;
import java.util.function.Consumer;

/** Public lifecycle integration, compiled against the operator's authorized Fine11 input. */
public final class PortalReportSessionListener {
    private PortalReportSessionListener() { }
    public static AutoCloseable register(final Consumer<String> created) {
        final Listener<String> listener=new Listener<String>() {
            @Override public void on(Event event,String sid) { created.accept(sid); }
        };
        EventDispatcher.listen(SessionEvent.CREATE,listener);
        return () -> EventDispatcher.stopListen(listener);
    }
}
