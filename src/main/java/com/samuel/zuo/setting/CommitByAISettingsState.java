package com.samuel.zuo.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * description: MyPluginSettingsState
 * date: 2024/1/14 13:47
 * author: samuel_zuo
 * version: 1.0
 */
@State(
        name = "com.samuel.zuo.setting.CommitByAISettingsState",
        storages = @Storage("CommitByAISettingsState.xml")
)
public class CommitByAISettingsState implements PersistentStateComponent<CommitByAISettingsState> {
    public String prompt = """
            Summarize the changes simply and clearly in Chinese. The following is changes in unified view:
            ${UnifiedDiff}
            the summary is formatted as list, like:\s
            1..;
            2..;
            """;
    private String aiServerAddress = "";

    @Override
    public CommitByAISettingsState getState() {
        return this;
    }

    @Override
    public void loadState(CommitByAISettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static CommitByAISettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CommitByAISettingsState.class);
    }
}

