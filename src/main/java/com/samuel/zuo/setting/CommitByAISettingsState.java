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
            It is the code changes gives by unified view, changed file num is ${TotalFileCount}:
            ${UnifiedDiff}
            Please generate commit message with template:
                        
            [Feature/Bugfix]: A brief summary of the changes in this commit (max 50 characters)
                        
            Detailed description of the changes:
            - Description of change #1 (max. 72 characters per line, no period at the end)
            - Description of change #2 (max. 72 characters per line, no period at the end)
            - ... and so on for as many changes as necessary
            """;
    private String aiServerAddress = "";

    public String model = "mistral";

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

