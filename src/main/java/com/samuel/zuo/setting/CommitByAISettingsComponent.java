package com.samuel.zuo.setting;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;

import javax.swing.*;

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
    private JTextField aiServerAddressTextField = new JTextField();

    public CommitByAISettingsComponent() {
        modelComboBox.addItem("mistral");
        modelComboBox.addItem("codellama:7b");
        modelComboBox.addItem("codellama");
        modelComboBox.addItem("llama2:7b");
        modelComboBox.addItem("llama3");
        modelComboBox.addItem("llama3.1:8b");
        promptTextArea.setMargin(JBUI.insets(5));
        JPanel panel2 = UI.PanelFactory.panel(promptTextArea).
                withComment("<p>Plugin is based on Ollama API running on local machine. Please download" +
                        " <a href=\"https://ollama.ai/\">Ollama</a> firstly.</p>"+
                        "<br/>" +
                        "<p> Parameters can be used in prompt: </p> "+
                        "<p> ${TotalFileCount}: number of changed files</p>"+
                        "<p> ${UnifiedDiff}: changed code by git unified view</p>")
                .createPanel();
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Model: "), modelComboBox, 1, false)
                .addVerticalGap(5)
                .addComponent(new JBLabel("Prompt text:"))
                .addVerticalGap(5)
                .addComponent(panel2)
                .addVerticalGap(10)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
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
}
