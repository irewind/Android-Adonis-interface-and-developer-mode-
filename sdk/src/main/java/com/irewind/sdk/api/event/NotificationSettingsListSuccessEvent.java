package com.irewind.sdk.api.event;

import com.irewind.sdk.model.NotificationSettings;

public class NotificationSettingsListSuccessEvent {
    public NotificationSettings notificationSettings;

    public NotificationSettingsListSuccessEvent(NotificationSettings settings) {
        notificationSettings = settings;
    }
}
