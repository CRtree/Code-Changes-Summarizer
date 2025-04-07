package com.samuel.zuo.setting;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * description: CommitByAIConfigurable
 * date: 2024/1/12 21:38
 * author: samuel_zuo
 * version: 1.0
 */
public class CommitByAIConfigurable implements Configurable {
    private CommitByAISettingsComponent commitByAISettingsComponent;

    @Nls
    @Override
    public String getDisplayName() {
        return "Commit By AI";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        commitByAISettingsComponent = new CommitByAISettingsComponent();
        return commitByAISettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        CommitByAISettingsState settingsState = CommitByAISettingsState.getInstance();
        return !commitByAISettingsComponent.getPrompt().equals(settingsState.prompt) ||
                !commitByAISettingsComponent.getModel().equals(settingsState.model) ||
                !commitByAISettingsComponent.getAiServerAddress().equals(settingsState.aiServerAddress) ||
                !commitByAISettingsComponent.getToken().equals(settingsState.token) ||
                !commitByAISettingsComponent.getType().equals(settingsState.type);
    }

    @Override
    public void apply() {
        CommitByAISettingsState settingsState = CommitByAISettingsState.getInstance();
        settingsState.prompt = commitByAISettingsComponent.getPrompt();
        settingsState.model = commitByAISettingsComponent.getModel();
        settingsState.type = commitByAISettingsComponent.getType();
        settingsState.aiServerAddress = commitByAISettingsComponent.getAiServerAddress();
        settingsState.token = commitByAISettingsComponent.getToken();
    }

    @Override
    public void reset() {
        commitByAISettingsComponent.setPrompt(CommitByAISettingsState.getInstance().prompt);
        commitByAISettingsComponent.setModel(CommitByAISettingsState.getInstance().model);
        commitByAISettingsComponent.setAiServerAddress(CommitByAISettingsState.getInstance().aiServerAddress);
        commitByAISettingsComponent.setToken(CommitByAISettingsState.getInstance().token);
        commitByAISettingsComponent.setType(CommitByAISettingsState.getInstance().type);
    }

    @Override
    public void disposeUIResources() {
        commitByAISettingsComponent = null;
    }
}
