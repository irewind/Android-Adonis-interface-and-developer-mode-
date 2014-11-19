package com.irewind.sdk.api.event;

import com.irewind.sdk.model.NotificationSettings;

public class UserNotificationSettingsLoadedEvent {
    public NotificationSettings notificationSettings;

    public UserNotificationSettingsLoadedEvent(NotificationSettings settings) {
        notificationSettings = settings;
    }
}
