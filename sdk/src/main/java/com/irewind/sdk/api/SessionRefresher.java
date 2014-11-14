package com.irewind.sdk.api;

import com.irewind.sdk.model.Session;

public interface SessionRefresher {
    void refreshSession(Session session);
}
