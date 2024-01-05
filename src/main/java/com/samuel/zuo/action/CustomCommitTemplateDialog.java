package com.samuel.zuo.action;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.samuel.zuo.markdown.MarkdownUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * description: CustomCommitTemplateDialog
 * date: 2023/12/28 14:35
 * author: samuel_zuo
 * version: 1.0
 */
public class CustomCommitTemplateDialog extends DialogWrapper {
    private JTextPane inputPane;
    private JEditorPane previewPane;

    public CustomCommitTemplateDialog(String commitMessage) {
        super(true);
        init();
        setTitle("Commit Template Helper");
        if (commitMessage != null && inputPane != null) {
            inputPane.setText(commitMessage);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));

        // 创建一个EmptyBorder对象，指定上下填充为10像素，左右填充为0像素
        EmptyBorder padding = JBUI.Borders.empty(10, 0);
        JLabel inputLabel = new JLabel("Template:");
        // 将EmptyBorder对象应用于inputLabel
        inputLabel.setBorder(padding);
        inputPane = new JTextPane();
        inputPane.setPreferredSize(new Dimension(600, 300)); // 设置多行输入框的行数和列数
        inputPane.setEditorKit(new StyledEditorKit()); // 设置编辑器工具包
        inputPane.setDocument(new DefaultStyledDocument()); // 设置文档

        SimpleAttributeSet attributes = new SimpleAttributeSet();
        // 设置字体样式
        StyleConstants.setFontFamily(attributes, "Arial");
        StyleConstants.setLineSpacing(attributes, 1.5f); // 设置行间距
        inputPane.setCharacterAttributes(attributes, false);
        inputPane.setText("请在此处输入模板");
        JBScrollPane inputScrollPane = new JBScrollPane(inputPane);
        inputScrollPane.setAlignmentX(0.0f); // 设置滚动面板在X轴上的对齐方式

        inputPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updatePreview();
            }
        });

        inputPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputPane.getText().equals("请在此处输入模板")) {
                    inputPane.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputPane.getText().isEmpty()) {
                    inputPane.setText("请在此处输入模板");
                }
            }
        });
        JLabel previewLabel = new JLabel("Preview:");
        previewLabel.setBorder(padding);
        previewPane = new JEditorPane();
        previewPane.setEditable(false);
        previewPane.setPreferredSize(new Dimension(100, 300)); // 设置预览面板的高度为10行
        JBScrollPane previewScrollPane = new JBScrollPane(previewPane);
        previewScrollPane.setAlignmentX(0.0f); // 设置滚动面板在X轴上的对齐方式
        dialogPanel.add(inputLabel);
        dialogPanel.add(inputScrollPane);
        dialogPanel.add(Box.createVerticalStrut(10)); // 添加垂直间距
        dialogPanel.add(previewLabel);
        dialogPanel.add(previewScrollPane);
        return dialogPanel;
    }

    private void updatePreview() {
        String inputText = inputPane.getText();
        if (inputText.equals("请在此处输入模板")) {
           return;
        }
        String html = MarkdownUtil.markdownToHtml(inputText);
        previewPane.setContentType("text/html");
        String css = "<style>body { font-family: Arial, sans-serif; font-size: 14px; padding: 10px; }</style>";
        previewPane.setText(css+html);
    }

    public String getCommitMessage() {
        return inputPane.getText();
    }
}
