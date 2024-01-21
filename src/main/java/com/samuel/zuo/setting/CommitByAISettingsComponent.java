package com.samuel.zuo.setting;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;

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
        modelComboBox.addItem("mistral-7B");
        JBTextArea tipsTextArea = new JBTextArea("1. Please input the prompt in English.\n" +
                "2. ${UnifiedDiff} can't be removed from prompt, it's a placeholder that represents\n changed files.");
        tipsTextArea.setEditable(false);
        tipsTextArea.setOpaque(false);
        // Set the font to bold
        Font boldFont = tipsTextArea.getFont().deriveFont(Font.BOLD);
        tipsTextArea.setFont(boldFont);
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Model: "), modelComboBox, 1, false)
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Prompt: "), promptTextArea, 1, false)
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Tips: "), tipsTextArea, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getPrompt() {
        return promptTextArea.getText();
    }

    public void setPrompt(String prompt) {
        promptTextArea.setText(prompt);
    }

    public String getAiServerAddress() {
        return aiServerAddressTextField.getText();
    }

    public void setAiServerAddress(String aiServerAddress) {
        aiServerAddressTextField.setText(aiServerAddress);
    }
}
