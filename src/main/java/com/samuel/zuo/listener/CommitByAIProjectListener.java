package com.samuel.zuo.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangesViewManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.vcs.commit.ChangesViewCommitPanel;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * description: CommitByAIProjectListener
 * date: 2024/3/16 21:05
 * author: samuel_zuo
 * version: 1.0
 */
public class CommitByAIProjectListener implements ToolWindowManagerListener {
    private final Project project;
    public CommitByAIProjectListener(Project project) {
        this.project = project;
    }

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        ToolWindowManagerListener.super.toolWindowShown(toolWindow);
        if (toolWindow.getId().equals(ToolWindowId.COMMIT)) {
            ContentManager contentManager = toolWindow.getContentManager();
            Content[] contents = contentManager.getContents();
            for (Content content : contents) {
                ChangesViewManager.ChangesViewToolWindowPanel component = (ChangesViewManager.ChangesViewToolWindowPanel) content.getComponent();
                Class<ChangesViewManager.ChangesViewToolWindowPanel> clazz = ChangesViewManager.ChangesViewToolWindowPanel.class;
                try {
                    Field field = clazz.getDeclaredField("myCommitPanel");
                    field.setAccessible(true);
                    ChangesViewCommitPanel commitPanel = (ChangesViewCommitPanel) field.get(component);
                    if (commitPanel != null) {
                        EditorTextField editorField = (EditorTextField) commitPanel.getPreferredFocusableComponent();
                        editorField.addDocumentListener(new CommitMessageDocListener());
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
