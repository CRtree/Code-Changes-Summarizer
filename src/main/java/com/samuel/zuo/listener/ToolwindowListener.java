package com.samuel.zuo.listener;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * description: ToolwindowListener
 * date: 2023/12/22 15:16
 * author: samuel_zuo
 * version: 1.0
 */
public class ToolwindowListener implements ToolWindowManagerListener {
    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
        System.out.println("toolWindowShown: "+ toolWindow.getId());
    }
}
