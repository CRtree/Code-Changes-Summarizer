package com.samuel.zuo.setting;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import com.samuel.zuo.service.OllamaService;

import javax.swing.*;
import java.util.List;

/**
 * description: MyPluginSettingsForm
 * date: 2024/1/14 13:51
 * author: samuel_zuo
 * version: 1.0
 */
public class CommitByAISettingsComponent {
    private JPanel panel;
    private JBTextArea promptTextArea = new JBTextArea();
    private ComboBox<String> modelComboBox = new ComboBox<>();
    private PlaceholderTextField aiServerAddressTextField = new PlaceholderTextField("API URL, not required for local Ollama API");
    private OllamaService ollamaService = new OllamaService();
    private ComboBox<String> typeComboBox = new ComboBox<>();
    private final PlaceholderTextField apiTokenTextField = new PlaceholderTextField("API token, not required for local Ollama API");
    private final List<String> remoteAIModelList = List.of("deepseek-chat");

    public CommitByAISettingsComponent() {
        List<String> modelList = ollamaService.listLocalModels();
        typeComboBox.addItem("local");
        typeComboBox.addItem("remote");
        typeComboBox.addActionListener(e -> {
            String selectedType = (String) typeComboBox.getSelectedItem();
            if ("local".equals(selectedType)) {
                aiServerAddressTextField.setEnabled(false);
                apiTokenTextField.setEnabled(false);
                modelComboBox.removeAllItems();
                for (String model : modelList) {
                    modelComboBox.addItem(model);
                }
            } else {
                aiServerAddressTextField.setEnabled(true);
                apiTokenTextField.setEnabled(true);
                modelComboBox.removeAllItems();
                for (String model : remoteAIModelList) {
                    modelComboBox.addItem(model);
                }
            }
            modelComboBox.setSelectedItem(CommitByAISettingsState.getInstance().model);
        });
        promptTextArea.setMargin(JBUI.insets(5));
        JPanel panel2 = UI.PanelFactory.panel(promptTextArea).
                withComment("<p>Plugin is based on Ollama API running on local machine. Please download" +
                        " <a href=\"https://ollama.ai/\">Ollama</a> firstly.</p>" +
                        "<br/>" +
                        "<p> Parameters can be used in prompt: </p> " +
                        "<p> ${TotalFileCount}: number of changed files</p>" +
                        "<p> ${UnifiedDiff}: changed code by git unified view</p>" +
                        "<p> ${MethodStackSummary}: method structure summary of changed files, only support Java language</p>")
                .createPanel();
        FormBuilder formBuilder = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Local/Remote: "), typeComboBox, 1, false)
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Model: "), modelComboBox, 1, false)
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Remote API: "), aiServerAddressTextField, 1, false)
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Token: "), apiTokenTextField, 1, false)
                .addVerticalGap(5)
                .addComponent(new JBLabel("Prompt text:"))
                .addVerticalGap(5)
                .addComponent(panel2)
                .addVerticalGap(10)
                .addComponentFillVertically(new JPanel(), 0);
        panel = formBuilder.getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getPrompt() {
        return promptTextArea.getText();
    }

    public String getModel() {
        return modelComboBox.getSelectedItem().toString();
    }

    public void setPrompt(String prompt) {
        promptTextArea.setText(prompt);
    }

    public void setModel(String model) {
        modelComboBox.setSelectedItem(model);
    }

    public String getAiServerAddress() {
        return aiServerAddressTextField.getText();
    }

    public void setAiServerAddress(String aiServerAddress) {
        aiServerAddressTextField.setText(aiServerAddress);
    }

    public String getToken() {
        return apiTokenTextField.getText();
    }

    public void setToken(String token) {
        apiTokenTextField.setText(token);
    }

    public String getType() {
        return typeComboBox.getSelectedItem().toString();
    }

    public void setType(String type) {
        typeComboBox.setSelectedItem(type);
    }
}
