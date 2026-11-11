package com.fr.web.core;

import com.fr.stable.web.SessionProvider;
import java.util.HashMap;
import java.util.Map;

/** In-memory fixture for the engine's documented session lookup boundary. */
public final class SessionPoolManager {
    private static final Map<String, SessionProvider> SESSIONS = new HashMap<>();

    private SessionPoolManager() { }

    public static <T extends SessionProvider> T getSessionIDInfor(String id, Class<T> type) {
        return type.cast(SESSIONS.get(id));
    }

    public static void put(String id, SessionProvider session) { SESSIONS.put(id, session); }
    public static void clear() {
        SESSIONS.clear();
    }
}
