package com.samuel.zuo.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * description: MyNotificationAction
 * date: 2025/4/7 14:24
 * author: samuel_zuo
 * version: 1.0
 */
public class MyNotificationAction extends NotificationAction {

    public MyNotificationAction(@Nullable @NlsContexts.NotificationContent String text) {
        super(text);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
        com.intellij.openapi.options.ShowSettingsUtil.getInstance().showSettingsDialog(anActionEvent.getProject(), "Summarize Code Changes");
    }
}
